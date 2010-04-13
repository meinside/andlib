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

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andlib.helper.LogHelper;
import org.andlib.helper.StringCodec;
import org.apache.http.util.ByteArrayBuffer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.MimeTypeMap;

/**
 * 
 * @author meinside@gmail.com
 * @since 09.10.05.
 * 
 * last update 10.04.13.
 *
 */
final public class HttpUtility
{
	public static final int MAX_BUFFER_SIZE = 4 * 1024 * 1024;	//4MB
	public static final int FILE_BUFFER_SIZE = 8 * 1024;	//8KB
	public static final int READ_BUFFER_SIZE = 8 * 1024;	//8KB

	private static HttpUtility httpUtility = null;
	private static HashMap<String, AsyncHttpTask> asyncTaskPool = null;

	//default connection values
	private static int connectionTimeoutMillis = 3000;
	private static int readTimeoutMillis = 3000;
	private static boolean useCaches = false;

	public static final int ASYNC_METHOD_GET = 1;
	public static final int ASYNC_METHOD_POST = 1 << 1;
	public static final int ASYNC_METHOD_POSTBYTES = 1 << 2;
	
	public static final String ASYNC_RESULT_BUNDLE_KEY_HTTP_STATUS = "status";
	public static final String ASYNC_RESULT_BUNDLE_KEY_HTTP_CONTENTTYPE = "type";
	public static final String ASYNC_RESULT_BUNDLE_KEY_HTTP_BODY = "body";
	
	/**
	 * 
	 */
	private HttpUtility(){}
	
	/**
	 * 
	 * @return
	 */
	public static HttpUtility getInstance()
	{
		if(httpUtility == null)
		{
			httpUtility = new HttpUtility();
			asyncTaskPool = new HashMap<String, AsyncHttpTask>();
		}
		
		return httpUtility;
	}
	
	/**
	 * 
	 * @param newConnectionTimeoutMillis
	 */
	final public static void setConnectionTimeout(int newConnectionTimeoutMillis)
	{
		connectionTimeoutMillis = newConnectionTimeoutMillis;
	}
	
	/**
	 * 
	 * @return
	 */
	final public static int getConnectionTimeout()
	{
		return connectionTimeoutMillis;
	}
	
	/**
	 * 
	 * @param newReadTimeoutMillis
	 */
	final public static void setReadTimeout(int newReadTimeoutMillis)
	{
		readTimeoutMillis = newReadTimeoutMillis;
	}
	
	/**
	 * 
	 * @return
	 */
	final public static int getReadTimeout()
	{
		return readTimeoutMillis;
	}
	
	/**
	 * 
	 * @param useCashesOrNot
	 */
	final public static void setUseCaches(boolean useCashesOrNot)
	{
		useCaches = useCashesOrNot;
	}
	
	/**
	 * 
	 * @return
	 */
	final public static boolean getUseCaches()
	{
		return useCaches;
	}
	
	/**
	 * 
	 * @param url
	 * @param headerValues
	 * @param getParameters
	 * @return
	 */
	public SimpleHttpResponse get(String url, Map<String, String> headerValues, Map<String, String> getParameters)
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

		//open connection
		HttpURLConnection connection = null;
		try
		{
			connection = (HttpURLConnection)new URL(urlWithQuery.toString()).openConnection();

			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setDoOutput(false);

			connection.setUseCaches(getUseCaches());
			connection.setConnectTimeout(getConnectionTimeout());
			connection.setReadTimeout(getReadTimeout());
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
			if(connection != null)
				connection.disconnect();
			return null;
		}

		//set header
		if(headerValues != null)
			for(String key: headerValues.keySet())
				connection.setRequestProperty(key, headerValues.get(key));

		//get response
		try
		{
			InputStream is = connection.getInputStream();
			byte[] responseBody = readBytesFromInputStream(is);
			is.close();
			return new SimpleHttpResponse(connection.getResponseCode(), responseBody, connection.getHeaderFields());
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
			try
			{
				int responseCode = connection.getResponseCode();
				if(responseCode != -1)
					return new SimpleHttpResponse(responseCode, connection.getResponseMessage());
			}
			catch(IOException ioe)
			{
				LogHelper.e(ioe.toString());
			}
		}
		finally
		{
			connection.disconnect();
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
	public SimpleHttpResponse post(String url, Map<String, String> headerValues, Map<String, Object> postParameters)
	{
		//open connection
		HttpURLConnection connection = null;
		try
		{
			connection = (HttpURLConnection)new URL(url).openConnection();
			
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);

			connection.setUseCaches(getUseCaches());
			connection.setConnectTimeout(getConnectionTimeout());
			connection.setReadTimeout(getReadTimeout());
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
			if(connection != null)
				connection.disconnect();
			return null;
		}

		//set header
		if(headerValues != null)
			for(String key: headerValues.keySet())
				connection.setRequestProperty(key, headerValues.get(key));

		//set post parameters
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

			try
			{
				DataOutputStream dos = null;
				if(fileExists)
				{
					String boundary = "__boundary_" + Calendar.getInstance().getTimeInMillis() + "__";
					connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
					
					dos = new DataOutputStream(connection.getOutputStream());
	
					for(String key: postParameters.keySet())
					{
						dos.writeBytes("--" + boundary + "\r\n");
						
						Object value = postParameters.get(key);
						if(value.getClass() == File.class)
						{
							File file = (File)value;
							dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + file.getName() + "\"\r\n");
							dos.writeBytes("Content-Type: " + getMimeType(file) + "\r\n\r\n");
							
							FileInputStream fis = new FileInputStream((File)value);
							byte[] buffer = new byte[FILE_BUFFER_SIZE];
							
							int bytesRead = 0;
							while((bytesRead = fis.read(buffer, 0, FILE_BUFFER_SIZE)) > 0)
							{
								dos.write(buffer, 0, bytesRead);
							}
							fis.close();
						}
						else
						{
							dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n");
							dos.writeBytes("Content-Type: text/plain\r\n\r\n");
							dos.writeBytes(value.toString());
						}
						
						dos.writeBytes("\r\n");
					}
					
					dos.writeBytes("\r\n--" + boundary + "--\r\n");
				}
				else
				{
					connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					
					dos = new DataOutputStream(connection.getOutputStream());
					
					StringBuffer buffer = new StringBuffer();
					for(String key: postParameters.keySet())
					{
						if(buffer.length() > 0)
							buffer.append("&");
						buffer.append(StringCodec.urlencode(key));
						buffer.append("=");
						buffer.append(StringCodec.urlencode(postParameters.get(key).toString()));
					}
					
					dos.writeBytes(buffer.toString());
				}
				
				dos.flush();
				dos.close();
			}
			catch(Exception e)
			{
				LogHelper.e(e.toString());
				connection.disconnect();
				return null;
			}
		}

		//get response
		try
		{
			InputStream is = connection.getInputStream();
			byte[] responseBody = readBytesFromInputStream(is);
			is.close();
			return new SimpleHttpResponse(connection.getResponseCode(), responseBody, connection.getHeaderFields());
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
			try
			{
				int responseCode = connection.getResponseCode();
				if(responseCode != -1)
					return new SimpleHttpResponse(responseCode, connection.getResponseMessage());
			}
			catch(IOException ioe)
			{
				LogHelper.e(ioe.toString());
			}
		}
		finally
		{
			connection.disconnect();
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
	public SimpleHttpResponse postBytes(String url, Map<String, String> headerValues, byte[] bytes, String contentType)
	{
		//open connection
		HttpURLConnection connection = null;
		try
		{
			connection = (HttpURLConnection)new URL(url).openConnection();
			
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			
			connection.setUseCaches(getUseCaches());
			connection.setConnectTimeout(getConnectionTimeout());
			connection.setReadTimeout(getReadTimeout());
			
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
			if(connection != null)
				connection.disconnect();
			return null;
		}

		//set header
		if(headerValues != null)
			for(String key: headerValues.keySet())
				connection.setRequestProperty(key, headerValues.get(key));

		//write post data
		try
		{
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			dos.write(bytes);
			dos.flush();
			dos.close();
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
			connection.disconnect();
			return null;
		}

		//get response
		try
		{
			InputStream is = connection.getInputStream();
			byte[] responseBody = readBytesFromInputStream(is);
			is.close();
			return new SimpleHttpResponse(connection.getResponseCode(), responseBody, connection.getHeaderFields());
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
			try
			{
				int responseCode = connection.getResponseCode();
				if(responseCode != -1)
					return new SimpleHttpResponse(responseCode, connection.getResponseMessage());
			}
			catch(IOException ioe)
			{
				LogHelper.e(ioe.toString());
			}
		}
		finally
		{
			connection.disconnect();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param resultHandler that will handle result message
	 * @param url
	 * @param headerValues
	 * @param getParameters
	 * @return id of AsyncHttpTask that is assigned to this GET job
	 */
	public String getAsync(Handler resultHandler, String url, Map<String, String> headerValues, Map<String, String> getParameters)
	{
		AsyncHttpTask task = new AsyncHttpTask();
		task.execute(ASYNC_METHOD_GET, resultHandler, url, headerValues, getParameters);
		return task.getId();
	}
	
	/**
	 * 
	 * @param resultHandler that will handle result message
	 * @param url
	 * @param headerValues
	 * @param postParameters
	 * @return id of AsyncHttpTask that is assigned to this POST job
	 */
	public String postAsync(Handler resultHandler, String url, Map<String, String> headerValues, Map<String, Object> postParameters)
	{
		AsyncHttpTask task = new AsyncHttpTask();
		task.execute(ASYNC_METHOD_POST, resultHandler, url, headerValues, postParameters);
		return task.getId();
	}

	/**
	 * 
	 * @param resultHandler that will handle result message
	 * @param url
	 * @param headerValues
	 * @param bytes
	 * @param contentType
	 * @return id of AsyncHttpTask that is assigned to this POST job
	 */
	public String postBytesAsync(Handler resultHandler, String url, Map<String, String> headerValues, byte[] bytes, String contentType)
	{
		AsyncHttpTask task = new AsyncHttpTask();
		task.execute(ASYNC_METHOD_POSTBYTES, resultHandler, url, headerValues, bytes, contentType);
		return task.getId();
	}
	
	/**
	 * Read up bytes from given InputStream instance and return
	 * 
	 * @param is (given InputStream instance is not closed by this function)
	 * @return
	 */
	private byte[] readBytesFromInputStream(InputStream is)
	{
		try
		{
			ByteArrayBuffer buffer = new ByteArrayBuffer(MAX_BUFFER_SIZE);
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
				buffer.append(bytes, startPos, length);
			}
			return buffer.toByteArray();
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
		}
		
		return null;
	}
	
	/**
	 * Read up bytes from given InputStream instance and return them as a String
	 * 
	 * @param is (given InputStream instance is not closed by this function)
	 * @return
	 */
	@SuppressWarnings("unused")
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
			LogHelper.e(e.toString());
		}
		
		return null;
	}
	
	/**
	 * return mime type of given file
	 * 
	 * @param file
	 * @return when mime type is unknown, it simply returns "application/octet-stream"
	 */
	final public static String getMimeType(File file)
	{
		String mimeType = null;
		try
		{
			mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getCanonicalPath()));
		}
		catch(IOException e)
		{
			LogHelper.e(e.toString());
		}
		if(mimeType == null)
			mimeType = "application/octet-stream";
		
		return mimeType;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	final public AsyncHttpTask getAsyncHttpTask(String id)
	{
		return asyncTaskPool.get(id);
	}

	/**
	 * 
	 * @author meinside@gmail.com
	 * @since 09.10.08.
	 * 
	 * last update 10.01.22.
	 *
	 */
	final public class SimpleHttpResponse
	{
		private int httpStatusCode;
		private byte[] httpResponseBody;
		private Map<String, List<String>> httpHeaders = null;
		
		/**
		 * 
		 * @param httpStatusCode
		 * @param httpResponseBody
		 * @param httpHeaders
		 */
		public SimpleHttpResponse(int httpStatusCode, byte[] httpResponseBody, Map<String, List<String>> httpHeaders)
		{
			this(httpStatusCode, httpResponseBody);
			
			if(httpHeaders != null)
				this.httpHeaders = httpHeaders;
		}
		
		/**
		 * 
		 * @param httpStatusCode
		 * @param httpResponseBody
		 */
		public SimpleHttpResponse(int httpStatusCode, byte[] httpResponseBody)
		{
			this.httpStatusCode = httpStatusCode;
			this.httpResponseBody = httpResponseBody;
		}
		
		/**
		 * 
		 * @param httpStatusCode
		 * @param httpResponseBody
		 * @param contentTypes
		 */
		public SimpleHttpResponse(int httpStatusCode, String httpResponseBody, Map<String, List<String>> httpHeaders)
		{
			this(httpStatusCode, httpResponseBody);
			
			if(httpHeaders != null)
				this.httpHeaders = httpHeaders;
		}
		
		/**
		 * 
		 * @param httpStatusCode
		 * @param httpResponseBody
		 */
		public SimpleHttpResponse(int httpStatusCode, String httpResponseBody)
		{
			this.httpStatusCode = httpStatusCode;
			this.httpResponseBody = httpResponseBody.getBytes();
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
		public byte[] getHttpResponseBody()
		{
			return httpResponseBody;
		}

		/**
		 * 
		 * @return
		 */
		public String getHttpResponseBodyAsString()
	    {
	    	return new String(httpResponseBody);
	    }

		/**
		 * 
		 * @return
		 */
		public ByteArrayInputStream getHttpResponseBodyAsInputStream()
		{
			return new ByteArrayInputStream(httpResponseBody);
		}
		
		/**
		 * 
		 * @return
		 */
		public Map<String, List<String>> getHeaders()
		{
			return httpHeaders;
		}
		
		/**
		 * 
		 * @param key
		 * @return
		 */
		public String getHeaderForKey(String key)
		{
			if(httpHeaders == null)
				return null;
			
			List<String> values = httpHeaders.get(key);
			if(values == null)
				values = httpHeaders.get(key.toLowerCase());	//check once more with lower case key
			if(values == null)
				return null;
			
			StringBuffer buffer = new StringBuffer();
			for(String value: values)
			{
				if(buffer.length() > 0)
					buffer.append(";");
				buffer.append(value);
			}
			
			return buffer.toString();
		}
	}

	/**
	 * 
	 * AsyncTask for executing HTTP methods
	 * (result Bundle object will carry http status code, and body content)
	 * 
	 * @author meinside@gmail.com
	 * @since 10.01.15.
	 * 
	 * last update 10.01.21.
	 *
	 */
	public class AsyncHttpTask extends AsyncTask<Object, Void, org.andlib.http.HttpUtility.SimpleHttpResponse>
	{
		private String _id;
		private Handler resultHandler;
		
		public AsyncHttpTask()
		{
			_id = new StringBuffer().append(System.currentTimeMillis()).append("_").append(this.hashCode()).toString();
		}
		
		/**
		 * 
		 * @return
		 */
		public String getId()
		{
			return _id;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			//add to pool
			asyncTaskPool.put(_id, this);
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(SimpleHttpResponse result)
		{
			super.onPostExecute(result);

			Message msg = Message.obtain();
			if(result != null)
			{
				msg.arg1 = result.getHttpStatusCode();
				msg.arg2 = result.getHttpResponseBody().length;

				Bundle data = new Bundle();
				data.putInt(ASYNC_RESULT_BUNDLE_KEY_HTTP_STATUS, msg.arg1);
				data.putString(ASYNC_RESULT_BUNDLE_KEY_HTTP_CONTENTTYPE, result.getHeaderForKey("Content-Type"));
				data.putByteArray(ASYNC_RESULT_BUNDLE_KEY_HTTP_BODY, result.getHttpResponseBody());
				msg.setData(data);
			}
			else
			{
				msg.arg1 = -1;
				msg.arg2 = -1;
			}
			resultHandler.dispatchMessage(msg);

			//remove from pool
			asyncTaskPool.remove(_id);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected SimpleHttpResponse doInBackground(Object... params)
		{
			resultHandler = (Handler)params[1];
			SimpleHttpResponse response = null;
			switch(((Integer)params[0]).intValue())
			{
			case ASYNC_METHOD_GET:
				response = HttpUtility.this.get((String)params[2], (Map<String, String>)params[3], (Map<String, String>)params[4]);
				break;
			case ASYNC_METHOD_POST:
				response = HttpUtility.this.post((String)params[2], (Map<String, String>)params[3], (Map<String, Object>)params[4]);
				break;
			case ASYNC_METHOD_POSTBYTES:
				response = HttpUtility.this.postBytes((String)params[2], (Map<String, String>)params[3], (byte[])params[4], (String)params[5]);
				break;
			}
			return response;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
		protected void onCancelled()
		{
			//remove from pool
			asyncTaskPool.remove(_id);

			super.onCancelled();
		}
	}
}
