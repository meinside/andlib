/*
 Copyright (c) 2010, Sungjin Han <meinside@gmail.com>
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of meinside nor the names of its contributors may be
    used to endorse or promote products derived from this software without
    specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */

package kr.pe.meinside.android.http;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import kr.pe.meinside.android.helper.LogHelper;
import kr.pe.meinside.android.helper.StringCodec;
import kr.pe.meinside.android.http.HttpUtility.SimpleHttpResponse;
import android.util.Log;

/**
 * 
 * @author meinside@gmail.com
 * @since 09.10.07.
 * 
 * last update 10.01.14.
 *
 */
public class OAuthHelper
{
	protected String consumerKey = null;
	private String consumerSecret = null;
	
	private String requestTokenUrl = null;
	private String accessTokenUrl = null;
	private String authorizeUrl = null;
	
	protected String accessToken = null;
	private String accessTokenSecret = null;
	
	private String oauthToken = null;
	private String oauthTokenSecret = null;
	
	protected boolean isAuthorized = false;
	
	/**
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param requestTokenUrl
	 * @param accessTokenUrl
	 * @param authorizeUrl
	 * @param accessToken null if none (if not authorized yet)
	 * @param accessTokenSecret null if none (if not authorized yet)
	 */
	public OAuthHelper(String consumerKey, String consumerSecret, String requestTokenUrl, String accessTokenUrl, String authorizeUrl, String accessToken, String accessTokenSecret)
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
	}
	
	/**
	 * converts string parameter to HashMap (ex: 'name1=value1&name2=value2&...' => ...)
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
//				Log.d(LogHelper.where(), "normalized url = " + result);
				
				return result;
			}
			catch(Exception e)
			{
				Log.e(LogHelper.where(), e.toString());
			}
		}
		return null;
	}
	
	/**
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
//		Log.d(LogHelper.where(), "normalized param = " + result);
		
		return result;
	}
	
	/**
	 * 
	 * @param method
	 * @param url
	 * @param params
	 * @param getOrPostParams
	 * @return
	 */
	private String generateSignatureBaseString(String method, String url, HashMap<String, String> params, HashMap<String, String> getOrPostParams)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(method);
		buffer.append("&");
		buffer.append(StringCodec.urlencode(getNormalizedURLString(url)));
		buffer.append("&");
		buffer.append(StringCodec.urlencode(getNormalizedRequestParameter(params, getOrPostParams)));
		
		String result = buffer.toString();
//		Log.d(LogHelper.where(), "signature base string = " + result);
		
		return result;
	}
	
	/**
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
//		Log.d(LogHelper.where(), "oauth signature key = " + key);

		return StringCodec.hmacSha1Digest(signatureBaseString, key);
	}
	
	/**
	 * 
	 * @param signatureBaseString
	 * @return
	 */
	private String generateAccessSignature(String signatureBaseString)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(StringCodec.urlencode(this.consumerSecret));
		buffer.append("&");
		buffer.append(StringCodec.urlencode(this.accessTokenSecret));

		String key = buffer.toString();
//		Log.d(LogHelper.where(), "access signature key = " + key);

		return StringCodec.hmacSha1Digest(signatureBaseString, key);
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	private String generateAuthHeader(HashMap<String, String> params)
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
//		Log.d(LogHelper.where(), "auth header = " + result);
		
		return result;
	}

	/**
	 * 
	 * @return
	 */
	private HashMap<String, String> requestOAuthToken()
	{
		//reset oauth token
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
		SimpleHttpResponse response = HttpUtility.getInstance().post(this.requestTokenUrl, requestHeader, null);
		if(response != null)
		{
			if(response.getHttpStatusCode() == 200)
			{
//				Log.d(LogHelper.where(), "received oauth token: " + response.getHttpResponseBody());
				return convertStringParamToMap(response.getHttpResponseBodyAsString());
			}
			else
				Log.e(LogHelper.where(), "auth token request error: " + response.getHttpStatusCode() + " (" + response.getHttpResponseBodyAsString() + ")");
		} 
		return null;
	}
	
	/**
	 * 
	 * @param verifier
	 * @return
	 */
	private HashMap<String, String> requestAccessToken(String verifier)
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
		SimpleHttpResponse response = HttpUtility.getInstance().post(this.accessTokenUrl, requestHeader, null);
		if(response != null)
		{
			if(response.getHttpStatusCode() == 200)
			{
//				Log.d(LogHelper.where(), "received access token: " + response.getHttpResponseBody());
				return convertStringParamToMap(response.getHttpResponseBodyAsString());
			}
			else
				Log.e(LogHelper.where(), "access token request error: " + response.getHttpStatusCode() + " (" + response.getHttpResponseBodyAsString() + ")");
		} 
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	final public String getUserAuthUrl()
	{
		//request and update oauth token/secret value
		HashMap<String, String> oauthTokenHash = requestOAuthToken();
		this.oauthToken = oauthTokenHash.get("oauth_token");
		this.oauthTokenSecret = oauthTokenHash.get("oauth_token_secret");
		return this.authorizeUrl + "?oauth_token=" + this.oauthToken;
	}
	
	/**
	 * override this to retrieve some more values from service provider's response
	 * @param values
	 */
	protected void retrieveValuesAfterAuthorization(HashMap<String, String> values)
	{
		this.accessToken = values.get("oauth_token");
		this.accessTokenSecret = values.get("oauth_token_secret");
	}
	
	/**
	 * 
	 * @param verifier
	 * @return
	 */
	final public boolean authorizeWithOAuthVerifier(String verifier)
	{
		HashMap<String, String> returnedAccessToken = requestAccessToken(verifier);
		if(returnedAccessToken != null)
		{
			retrieveValuesAfterAuthorization(returnedAccessToken);
			this.isAuthorized = true;
			return true;
		}
		else
			Log.e(LogHelper.where(), "authorize with oauth verifier failed");
		return false;
	}
	
	/**
	 * 
	 * @param method
	 * @param url
	 * @param oauthHash
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	protected String generateSignature(String method, String url, HashMap<String, String> oauthHash)
	{
		return generateAccessSignature(generateSignatureBaseString(method, url, oauthHash, null));
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTimestamp()
	{
		return "" + System.currentTimeMillis();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getNonce()
	{
		Random rand = new Random(System.currentTimeMillis());
		return StringCodec.md5sum("" + rand.nextLong());
	}
	
	/**
	 * 
	 * @param url
	 * @param params
	 * @return null if not authorized or fails
	 */
	final public SimpleHttpResponse get(String url, HashMap<String, String> params)
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
		SimpleHttpResponse response = HttpUtility.getInstance().get(url, requestHeader, params);
		if(response != null)
		{
			if(response.getHttpStatusCode() == 200)
				return response;
			else
				Log.e(LogHelper.where(), "get error: " + response.getHttpStatusCode() + " (" + response.getHttpResponseBodyAsString() + ")");
		}
		return null;
	}
	
	/**
	 * 
	 * @param url
	 * @param params
	 * @return null if not authorized or fails
	 */
	final public SimpleHttpResponse post(String url, HashMap<String, Object> params)
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
		SimpleHttpResponse response = HttpUtility.getInstance().post(url, requestHeader, params);
		if(response != null)
		{
			if(response.getHttpStatusCode() == 200)
				return response;
			else
				Log.e(LogHelper.where(), "post error: " + response.getHttpStatusCode() + " (" + response.getHttpResponseBodyAsString() + ")");
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	final public String getAccessToken()
	{
		return this.accessToken;
	}
	
	/**
	 * 
	 * @return
	 */
	final public String getAccessTokenSecret()
	{
		return this.accessTokenSecret;
	}

	/**
	 * 
	 * @return
	 */
	final public boolean isAuthorized()
	{
		return this.isAuthorized;
	}
	
}
