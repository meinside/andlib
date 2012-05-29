package org.andlib.http;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import org.andlib.helpers.Logger;
import org.andlib.helpers.StringCodec;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author meinside@gmail.com
 * @since 2009.10.07.
 *
 * OAuth base class
 * <br>
 * <br>
 * currently using Apache Http library due to <a href="http://code.google.com/p/android/issues/detail?id=6684">this issue</a>
 * <br>
 * <br>
 * last update: 2012.05.29.
 * 
 */
public class OAuthBase
{
	//long timeout seconds for slow mobile devices
	public static final int OAUTH_DEFAULT_CONNECTION_TIMEOUT = 100000;
	public static final int OAUTH_DEFAULT_SOCKET_TIMEOUT = 100000;
	
	private String consumerKey = null;
	private String consumerSecret = null;
	
	private String requestTokenUrl = null;
	private String accessTokenUrl = null;
	private String authorizeUrl = null;
	
	private String accessToken = null;
	private String accessTokenSecret = null;
	
	private String oauthToken = null;
	private String oauthTokenSecret = null;
	
	protected boolean isAuthorized = false;
	
	/**
	 * default constructor (when not authorized yet)
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param requestTokenUrl
	 * @param accessTokenUrl
	 * @param authorizeUrl
	 */
	public OAuthBase(String consumerKey, String consumerSecret, String requestTokenUrl, String accessTokenUrl, String authorizeUrl)
	{
		this(consumerKey, consumerSecret, requestTokenUrl, accessTokenUrl, authorizeUrl, null, null);
	}
	
	/**
	 * default constructor (when already authorized)
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param requestTokenUrl
	 * @param accessTokenUrl
	 * @param authorizeUrl
	 * @param accessToken null if none (if not authorized yet)
	 * @param accessTokenSecret null if none (if not authorized yet)
	 */
	public OAuthBase(String consumerKey, String consumerSecret, String requestTokenUrl, String accessTokenUrl, String authorizeUrl, String accessToken, String accessTokenSecret)
	{
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.accessTokenUrl = accessTokenUrl;
		this.authorizeUrl = authorizeUrl;
		this.oauthToken = "";
		this.oauthTokenSecret = "";
		
		if(accessToken != null && accessTokenSecret != null)
		{
			this.accessToken = accessToken;
			this.accessTokenSecret = accessTokenSecret;
			
			this.isAuthorized = true;
		}

		ApacheHttpUtility.setConnectionTimeout(OAUTH_DEFAULT_CONNECTION_TIMEOUT);
		ApacheHttpUtility.setSocketTimeoutMillis(OAUTH_DEFAULT_SOCKET_TIMEOUT);
	}
	
	/**
	 * converts string parameter to HashMap (ex: 'name1=value1&name2=value2&...' => ...)
	 * 
	 * @param parameter
	 * @return
	 */
	private HashMap<String, String> convertStringParamToMap(String parameter)
	{
		if(parameter != null)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			String[] keyValue;
			for(String param: parameter.split("&"))
			{
				keyValue = param.split("=");
				if(keyValue.length > 1)
					map.put(keyValue[0], keyValue[1]);
			}
			return map;
		}
		return null;
	}

	/**
	 * normalizes given url
	 * 
	 * @param url
	 * @return null if fails
	 */
	private String getNormalizedURLString(String url)
	{
		if(url != null)
		{
			try
			{
				StringBuffer buffer = new StringBuffer();
				URI uri = new URI(url);
				String scheme = uri.getScheme().toLowerCase();
				buffer.append(scheme);	//scheme
				buffer.append("://");
				buffer.append(uri.getHost().toLowerCase());	//host
				int port = uri.getPort();
				if(port != -1 && ((scheme.compareTo("http") == 0 && port != 80) || (scheme.compareTo("https") == 0 && port != 433)))
				{
					buffer.append(":");
					buffer.append(port);	//port
				}
				buffer.append(uri.getPath());	//path
				
				String result = buffer.toString();

				Logger.v("normalized url = " + result);
				
				return result;
			}
			catch(Exception e)
			{
				Logger.e(e.toString());
			}
		}
		return null;
	}
	
	/**
	 * get normalized request parameter from given parameters
	 * 
	 * @param params
	 * @param getOrPostParams
	 * @return
	 */
	private String getNormalizedRequestParameter(HashMap<String, String> params, HashMap<String, String> getOrPostParams)
	{
		StringBuffer buffer = new StringBuffer();
		HashMap<String, String> dict = new HashMap<String, String>();
		if(params != null)
			for(String key: params.keySet())
				dict.put(key, params.get(key));
		if(getOrPostParams != null)
			for(String key: getOrPostParams.keySet())
				dict.put(key, getOrPostParams.get(key));
		if(dict.size() > 0)
		{
			TreeSet<String> sortedKeys = new TreeSet<String>(dict.keySet());	//sort keys
			for(String key: sortedKeys)
			{
				if(key.compareTo("realm") == 0)	//skip realm
					continue;
				if(buffer.length() > 0)
					buffer.append("&");
				buffer.append(StringCodec.urlencode(key));
				buffer.append("=");
				buffer.append(StringCodec.urlencode(dict.get(key)));
			}
		}
		String result = buffer.toString();

		Logger.v("normalized param = " + result);
		
		return result;
	}
	
	/**
	 * generate base string for signature
	 * 
	 * @param method
	 * @param url
	 * @param params
	 * @param getOrPostParams
	 * @return
	 */
	protected String generateSignatureBaseString(String method, String url, HashMap<String, String> params, HashMap<String, String> getOrPostParams)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(method);
		buffer.append("&");
		buffer.append(StringCodec.urlencode(getNormalizedURLString(url)));
		buffer.append("&");
		buffer.append(StringCodec.urlencode(getNormalizedRequestParameter(params, getOrPostParams)));
		
		String result = buffer.toString();
		
		Logger.v("signature base string = " + result);
		
		return result;
	}
	
	/**
	 * generate OAuth signature from signature base string
	 * 
	 * @param signatureBaseString
	 * @return
	 */
	private String generateOAuthSignature(String signatureBaseString)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(StringCodec.urlencode(this.consumerSecret));
		buffer.append("&");
		buffer.append(StringCodec.urlencode(this.oauthTokenSecret));
		
		String key = buffer.toString();

		Logger.v("oauth signature key = " + key);

		return StringCodec.hmacSha1Digest(signatureBaseString, key);
	}
	
	/**
	 * generate access signature from signature base string
	 * 
	 * @param signatureBaseString
	 * @return
	 */
	protected String generateAccessSignature(String signatureBaseString)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(StringCodec.urlencode(this.consumerSecret));
		buffer.append("&");
		buffer.append(StringCodec.urlencode(this.accessTokenSecret));

		String key = buffer.toString();

		Logger.v("access signature key = " + key);

		return StringCodec.hmacSha1Digest(signatureBaseString, key);
	}
	
	/**
	 * generate auth header from given parameters
	 * 
	 * @param params
	 * @return
	 */
	protected String generateAuthHeader(HashMap<String, String> params)
	{
		StringBuffer buffer = new StringBuffer();
		for(String key: params.keySet())
		{
			if(buffer.length() > 0)
				buffer.append(",");
			else
				buffer.append("OAuth ");
			buffer.append(StringCodec.urlencode(key));
			buffer.append("=\"");
			buffer.append(StringCodec.urlencode(params.get(key)));
			buffer.append("\"");
		}
		
		String result = buffer.toString();

		Logger.v("auth header = " + result);
		
		return result;
	}

	/**
	 * request auth url for user
	 * 
	 * @return id of AsyncHttpTask
	 */
	final public String requestUserAuthUrl(final AuthUrlListener listener)
	{
		//request and update oauth token/secret value
		this.oauthToken = "";
		this.oauthTokenSecret = "";

		HashMap<String, String> requestTokenHash = new HashMap<String, String>();
		requestTokenHash.put("oauth_consumer_key", this.consumerKey);
		requestTokenHash.put("oauth_signature_method", "HMAC-SHA1");
		requestTokenHash.put("oauth_timestamp", getTimestamp());
		requestTokenHash.put("oauth_nonce", getNonce());
		requestTokenHash.put("oauth_version", "1.0");
		requestTokenHash.put("oauth_callback", "oob");
		
		//set signature
		requestTokenHash.put("oauth_signature", generateOAuthSignature(generateSignatureBaseString("POST", this.requestTokenUrl, requestTokenHash, null)));
		
		//post with auth header
		HashMap<String, String> requestHeader = new HashMap<String, String>();
		requestHeader.put("Authorization", generateAuthHeader(requestTokenHash));
		
		return ApacheHttpUtility.getInstance().postAsync(new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				Log.e("test", "msg = " + msg + ", data = " + msg.getData());	//test

				SimpleHttpResponse response = (SimpleHttpResponse)msg.getData().getParcelable(ApacheHttpUtility.ASYNC_HTTP_RESULT);
				if(response.getHttpStatusCode() == 200)
				{
					HashMap<String, String> oauthTokenHash = convertStringParamToMap(response.getHttpResponseBodyAsString());
					if(oauthTokenHash != null)
					{
						oauthToken = oauthTokenHash.get("oauth_token");
						oauthTokenSecret = oauthTokenHash.get("oauth_token_secret");
						
						String userAuthUrl = authorizeUrl + "?oauth_token=" + oauthToken;
						
						Logger.v("user auth url = " + userAuthUrl);

						listener.authUrlReceived(userAuthUrl);
					}
					else
						listener.authUrlReceiveFailed(200, "returned params are malformed: " + response.getHttpResponseBodyAsString());
				}
				else
				{
					listener.authUrlReceiveFailed(response.getHttpStatusCode(), response.getHttpResponseBodyAsString());
				}
			}}, this.requestTokenUrl, requestHeader, null);
	}

	/**
	 * override this function to retrieve some more values from service provider's post-authorization response
	 * 
	 * @param values
	 */
	protected void retrieveValuesAfterAuthorization(HashMap<String, String> values)
	{
		this.accessToken = values.get("oauth_token");
		this.accessTokenSecret = values.get("oauth_token_secret");
		
		Logger.v("retrieved: oauth_token = " + this.accessToken + ", oauth_token_secret = " + this.accessTokenSecret);
	}

	/**
	 * authorize user with given verifier
	 * 
	 * @param verifier PIN retrieved from service provider
	 * @return id of AsyncHttpTask
	 */
	final public String authorizeWithOAuthVerifier(final String verifier, final AuthorizationListener authListener)
	{
		HashMap<String, String> requestTokenHash = new HashMap<String, String>();
		requestTokenHash.put("oauth_consumer_key", this.consumerKey);
		requestTokenHash.put("oauth_token", this.oauthToken);
		requestTokenHash.put("oauth_signature_method", "HMAC-SHA1");
		requestTokenHash.put("oauth_timestamp", getTimestamp());
		requestTokenHash.put("oauth_nonce", getNonce());
		requestTokenHash.put("oauth_version", "1.0");
		requestTokenHash.put("oauth_verifier", verifier);
		
		//set signature
		requestTokenHash.put("oauth_signature", generateOAuthSignature(generateSignatureBaseString("POST", this.accessTokenUrl, requestTokenHash, null)));

		//post with auth header
		HashMap<String, String> requestHeader = new HashMap<String, String>();
		requestHeader.put("Authorization", generateAuthHeader(requestTokenHash));
		return ApacheHttpUtility.getInstance().postAsync(new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				SimpleHttpResponse response = (SimpleHttpResponse)msg.getData().getParcelable(ApacheHttpUtility.ASYNC_HTTP_RESULT);
				if(response.getHttpStatusCode() == 200)
				{
					HashMap<String, String> accessTokenHash = convertStringParamToMap(response.getHttpResponseBodyAsString());
					if(accessTokenHash != null)
					{
						retrieveValuesAfterAuthorization(accessTokenHash);
						isAuthorized = true;

						authListener.authorizationSucceeded();
					}
					else
						authListener.authorizationFailed(200, "returned params are malformed: " + response.getHttpResponseBodyAsString());
				}
				else
					authListener.authorizationFailed(response.getHttpStatusCode(), response.getHttpResponseBodyAsString());
			}}, this.accessTokenUrl, requestHeader, null);
	}
	
	/**
	 * generate current timestamp
	 * 
	 * @return
	 */
	public String getTimestamp()
	{
		return "" + (System.currentTimeMillis() / 1000);
	}
	
	/**
	 * generate nonce value
	 * 
	 * @return
	 */
	public String getNonce()
	{
		Random rand = new Random(System.currentTimeMillis());
		return StringCodec.md5sum("" + rand.nextLong());
	}

	/**
	 * send asynchronous GET request to the service provider with essential header values
	 * 
	 * @param resultHandler
	 * @param url
	 * @param params
	 * @return id string of async task object (can be used with cancelAsyncTask() to cancel task)
	 */
	final public String getAsync(Handler resultHandler, String url, HashMap<String, String> params)
	{
		if(!this.isAuthorized)
			return null;

		HashMap<String, String> requestTokenHash = new HashMap<String, String>();
		requestTokenHash.put("oauth_consumer_key", this.consumerKey);
		requestTokenHash.put("oauth_token", this.accessToken);
		requestTokenHash.put("oauth_signature_method", "HMAC-SHA1");
		requestTokenHash.put("oauth_timestamp", getTimestamp());
		requestTokenHash.put("oauth_nonce", getNonce());
		requestTokenHash.put("oauth_version", "1.0");
		
		//set signature
		requestTokenHash.put("oauth_signature", generateAccessSignature(generateSignatureBaseString("GET", url, requestTokenHash, params)));
		
		//get with auth header
		HashMap<String, String> requestHeader = new HashMap<String, String>();
		requestHeader.put("Authorization", generateAuthHeader(requestTokenHash));
		
		return ApacheHttpUtility.getInstance().getAsync(resultHandler, url, requestHeader, params);
	}

	/**
	 * send asynchronous POST request to the service provider with essential header values
	 * 
	 * @param resultHandler
	 * @param url
	 * @param params
	 * @return id string of async task object (can be used with cancelAsyncTask() to cancel task)
	 */
	final public String postAsync(Handler resultHandler, String url, HashMap<String, Object> params)
	{
		if(!this.isAuthorized)
			return null;

		//check file existence
		boolean fileExists = false;
		if(params != null)
		{
			for(String key: params.keySet())
			{
				if(params.get(key).getClass() == File.class)
				{
					fileExists = true;
					break;
				}
			}
		}

		HashMap<String, String> requestTokenHash = new HashMap<String, String>();
		requestTokenHash.put("oauth_consumer_key", this.consumerKey);
		requestTokenHash.put("oauth_token", this.accessToken);
		requestTokenHash.put("oauth_signature_method", "HMAC-SHA1");
		requestTokenHash.put("oauth_timestamp", getTimestamp());
		requestTokenHash.put("oauth_nonce", getNonce());
		requestTokenHash.put("oauth_version", "1.0");
		
		//set signature
		if(fileExists)
		{
			requestTokenHash.put("oauth_signature", generateAccessSignature(generateSignatureBaseString("POST", url, requestTokenHash, null)));
		}
		else
		{
			HashMap<String, String> paramHash = null;
			if(params != null)
			{
				paramHash = new HashMap<String, String>();
				for(String key: params.keySet())
					paramHash.put(key, params.get(key).toString());
			}
			requestTokenHash.put("oauth_signature", generateAccessSignature(generateSignatureBaseString("POST", url, requestTokenHash, paramHash)));
		}
		
		//post with auth header
		HashMap<String, String> requestHeader = new HashMap<String, String>();
		requestHeader.put("Authorization", generateAuthHeader(requestTokenHash));

		return ApacheHttpUtility.getInstance().postAsync(resultHandler, url, requestHeader, params);
	}

	/**
	 * getter for consumer key
	 * 
	 * @return
	 */
	final public String getConsumerKey()
	{
		return this.consumerKey;
	}
	
	/**
	 * getter for access token
	 * 
	 * @return
	 */
	final public String getAccessToken()
	{
		return this.accessToken;
	}
	
	/**
	 * getter for access token secret
	 * 
	 * @return
	 */
	final public String getAccessTokenSecret()
	{
		return this.accessTokenSecret;
	}

	/**
	 * 
	 * @return is authorized or not 
	 */
	final public boolean isAuthorized()
	{
		return this.isAuthorized;
	}

	/**
	 * cancel async task with given task id
	 * 
	 * @param taskId
	 */
	final public void cancelAsyncTask(String taskId)
	{
		ApacheHttpUtility.getInstance().cancelAsyncHttpTask(taskId);
	}

	/**
	 * listener for receiving auth url
	 * @author meinside
	 *
	 */
	public interface AuthUrlListener
	{
		void authUrlReceiveFailed(int errorCode, String errorMessage);
		void authUrlReceived(String url);
	}

	/**
	 * listener for authorization with pin
	 * @author meinside
	 *
	 */
	public interface AuthorizationListener
	{
		void authorizationFailed(int errorCode, String errorMessage);
		void authorizationSucceeded();
	}
}
