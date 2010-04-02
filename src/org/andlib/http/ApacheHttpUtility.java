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

package org.andlib.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;


import org.andlib.helper.LogHelper;
import org.andlib.helper.StringCodec;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;
import android.webkit.MimeTypeMap;

/*
 * 
 * currently using apache-mime4j-0.6.jar, httpmime-4.0.1.jar
 * (version: 4.0.1)
 * url: http://hc.apache.org/downloads.cgi
 * 
 */
/**
 * 
 * @author meinside@gmail.com
 * @since 10.01.13.
 * 
 * last update 10.01.14.
 *
 */
final public class ApacheHttpUtility
{
	public static final int READ_BUFFER_SIZE = 8 * 1024;

	private static ApacheHttpUtility httpUtility = null;

	//default timeout values
	private static int connectionTimeoutMillis = 3000;
	private static int socketTimeoutMillis = 3000;
	
	/**
	 * 
	 */
	private ApacheHttpUtility(){}
	
	/**
	 * 
	 * @return
	 */
	public static ApacheHttpUtility getInstance()
	{
		if(httpUtility == null)
			httpUtility = new ApacheHttpUtility();
		
		return httpUtility;
	}

	/**
	 * @return the connectionTimeoutMillis
	 */
	public static int getConnectionTimeoutMillis()
	{
		return connectionTimeoutMillis;
	}

	/**
	 * @param connectionTimeoutMillis the connectionTimeoutMillis to set
	 */
	public static void setConnectionTimeoutMillis(int connectionTimeoutMillis)
	{
		ApacheHttpUtility.connectionTimeoutMillis = connectionTimeoutMillis;
	}

	/**
	 * @return the socketTimeoutMillis
	 */
	public static int getSocketTimeoutMillis()
	{
		return socketTimeoutMillis;
	}

	/**
	 * @param socketTimeoutMillis the socketTimeoutMillis to set
	 */
	public static void setSocketTimeoutMillis(int socketTimeoutMillis)
	{
		ApacheHttpUtility.socketTimeoutMillis = socketTimeoutMillis;
	}
	
	/**
	 * override this to set some more parameters to DefaultHttpClient
	 * 
	 * @return default http client to use
	 */
	protected DefaultHttpClient getHttpClient()
	{
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeoutMillis);
		HttpConnectionParams.setSoTimeout(params, socketTimeoutMillis);

		return new DefaultHttpClient(params);
	}

	/**
	 * 
	 * @param url
	 * @param headerValues
	 * @param getParameters
	 * @return
	 */
	final public SimpleHttpResponse get(String url, Map<String, String> headerValues, Map<String, String> getParameters)
	{
		return get(url, headerValues, getParameters, false);
	}

	/**
	 * 
	 * @param url
	 * @param headerValues
	 * @param getParameters
	 * @param closeConnectionAfterUse whether to shut down connection manager or not (releases allocated resources, this includes closing all connections, whether they are currently used or not) 
	 * @return
	 */
	final public SimpleHttpResponse get(String url, Map<String, String> headerValues, Map<String, String> getParameters, boolean closeConnectionAfterUse)
	{
		DefaultHttpClient httpClient = getHttpClient();

		try
		{
			//add get parameters to url
			StringBuffer urlWithQuery = new StringBuffer(url);
			if(getParameters != null)
			{
				boolean endsWithQuestion = false;
				if(url.indexOf("?") == -1)
				{
					urlWithQuery.append("?");
					endsWithQuestion = true;
				}
				for(String key: getParameters.keySet())
				{
					if(!endsWithQuestion)
						urlWithQuery.append("&");
					endsWithQuestion = false;

					urlWithQuery.append(StringCodec.urlencode(key));
					urlWithQuery.append("=");
					urlWithQuery.append(StringCodec.urlencode(getParameters.get(key)));
				}
			}

			HttpGet get = new HttpGet(urlWithQuery.toString());

			//set header
			if(headerValues != null)
				for(String key: headerValues.keySet())
					get.setHeader(key, headerValues.get(key));

			//send get
			HttpResponse response = httpClient.execute(get);

			//retrieve and parse result
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = null;
			HttpEntity responseEntity = response.getEntity();
			if(responseEntity != null)
				responseBody = readStringFromInputStream(responseEntity.getContent());

			//return
			return new SimpleHttpResponse(statusCode, responseBody);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		finally
		{
			if(closeConnectionAfterUse)
				httpClient.getConnectionManager().shutdown();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param url
	 * @param headerValues
	 * @param postParameters
	 * @return
	 */
	final public SimpleHttpResponse post(String url, Map<String, String> headerValues, Map<String, Object> postParameters)
	{
		return post(url, headerValues, postParameters, false);
	}

	/**
	 * 
	 * @param url
	 * @param headerValues
	 * @param postParameters
	 * @param closeConnectionAfterUse whether to shut down connection manager or not (releases allocated resources, this includes closing all connections, whether they are currently used or not)
	 * @return
	 */
	final public SimpleHttpResponse post(String url, Map<String, String> headerValues, Map<String, Object> postParameters, boolean closeConnectionAfterUse)
	{
		DefaultHttpClient httpClient = getHttpClient();

		try
		{
			HttpPost post = new HttpPost(url);

			//set header
			if(headerValues != null)
				for(String key: headerValues.keySet())
					post.setHeader(key, headerValues.get(key));

			if(postParameters != null)
			{
				//check file existence
				boolean fileExists = false;
				for(String key: postParameters.keySet())
				{
					if(postParameters.get(key).getClass() == File.class)
					{
						fileExists = true;
						break;
					}
				}

				//set post body
				if(postParameters != null)
				{
					if(fileExists)
					{
						MultipartEntity multipartEntity = new MultipartEntity();
						for(String key: postParameters.keySet())
						{
							Object value = postParameters.get(key);
							if(value.getClass() == File.class)
								multipartEntity.addPart(key, new FileBody((File)value));
							else
								multipartEntity.addPart(key, new StringBody(value.toString()));
						}
						post.setEntity(multipartEntity);	
					}
					else
					{
						ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						for(String key: postParameters.keySet())
							params.add(new BasicNameValuePair(key, postParameters.get(key).toString()));
						UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params);
						post.setEntity(urlEncodedFormEntity);
					}
				}
			}

			//send post
			HttpResponse response = httpClient.execute(post);

			//retrieve and parse result
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = null;
			HttpEntity responseEntity = response.getEntity();
			if(responseEntity != null)
				responseBody = readStringFromInputStream(responseEntity.getContent());

			//return
			return new SimpleHttpResponse(statusCode, responseBody);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		finally
		{
			if(closeConnectionAfterUse)
				httpClient.getConnectionManager().shutdown();
		}

		return null;
	}

	/**
	 * 
	 * @param url
	 * @param headerValues
	 * @param bytes
	 * @param contentType
	 * @return
	 */
	final public SimpleHttpResponse postBytes(String url, Map<String, String> headerValues, byte[] bytes, String contentType)
	{
		return postBytes(url, headerValues, bytes, contentType, false);
	}

	/**
	 * 
	 * @param url
	 * @param headerValues
	 * @param bytes
	 * @param contentType
	 * @param closeConnectionAfterUse
	 * @return
	 */
	final public SimpleHttpResponse postBytes(String url, Map<String, String> headerValues, byte[] bytes, String contentType, boolean closeConnectionAfterUse)
	{
		DefaultHttpClient httpClient = getHttpClient();

		try
		{
			HttpPost post = new HttpPost(url);

			//set header
			if(headerValues != null)
				for(String key: headerValues.keySet())
					post.setHeader(key, headerValues.get(key));

			//set post body
			if(bytes != null)
			{
				ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
				byteArrayEntity.setContentType(contentType);
				post.setEntity(byteArrayEntity);
			}

			//send post
			HttpResponse response = httpClient.execute(post);

			//retrieve and parse result
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = null;
			HttpEntity responseEntity = response.getEntity();
			if(responseEntity != null)
				responseBody = readStringFromInputStream(responseEntity.getContent());

			//return
			return new SimpleHttpResponse(statusCode, responseBody);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		finally
		{
			if(closeConnectionAfterUse)
				httpClient.getConnectionManager().shutdown();
		}

		return null;
	}

	/**
	 * Read up bytes from given InputStream instance and return them as a String
	 * 
	 * @param is (given InputStream instance is not closed by this function)
	 * @return
	 */
	private String readStringFromInputStream(InputStream is)
	{
		try
		{
			StringBuffer buffer = new StringBuffer();
			byte[] bytes = new byte[READ_BUFFER_SIZE];
			int bytesRead, startPos, length;
			boolean firstRead = true;
			while((bytesRead = is.read(bytes, 0, READ_BUFFER_SIZE)) > 0)
			{
				startPos = 0;
				length = bytesRead;
				if(firstRead)
				{
					//remove first occurrence of '0xEF 0xBB 0xBF' (UTF-8 BOM)
					if(bytesRead >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF)
					{
						startPos += 3;
						length -= 3;
					}
					firstRead = false;
				}
				buffer.append(new String(bytes, startPos, length));
			}
			return buffer.toString();
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		
		return null;
	}

	/**
	 * get mime type of given file
	 * @param file
	 * @return
	 */
	public static String getMimeType(File file)
	{
		String mimeType = null;
		try
		{
			mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getCanonicalPath()));
		}
		catch(IOException e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		if(mimeType == null)
			mimeType = "application/octet-stream";
		
		return mimeType;
	}

	/**
	 * 
	 * @author meinside@gmail.com
	 * @since 09.10.08.
	 * 
	 * last update 09.11.26.
	 *
	 */
	final public class SimpleHttpResponse
	{
		private int httpStatusCode;
		private String httpResponseBody;
		
		/**
		 * 
		 * @param httpStatusCode
		 * @param httpResponseBody
		 */
		public SimpleHttpResponse(int httpStatusCode, String httpResponseBody)
		{
			this.httpStatusCode = httpStatusCode;
			this.httpResponseBody = httpResponseBody;
		}

		/**
		 * 
		 * @return
		 */
		public int getHttpStatusCode()
	    {
	    	return httpStatusCode;
	    }

		/**
		 * 
		 * @return
		 */
		public String getHttpResponseBody()
	    {
	    	return httpResponseBody;
	    }
	}
}
