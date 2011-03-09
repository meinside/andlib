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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.andlib.helpers.Logger;
import org.andlib.helpers.StringCodec;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


/**
 * 
 * @author meinside@gmail.com
 * @since 10.01.13.
 * 
 * last update 11.03.10.
 * 
 * <p>
 * this class needs: apache-mime4j-0.6.jar & httpmime-4.0.1.jar<br>
 * (http://hc.apache.org/downloads.cgi)<br>
 * </p>
 * 
 * <p>
 * usage:<br><br>
 * 
 * ApacheHttpUtility instance = ApacheHttpUtility.getInstance();<br>
 * SomeHandler handler = new SomeHandler();<br>
 * HashMap<String, String> params = new HashMap<String, String>();<br>
 * params.put("query1", "1");<br>
 * params.put("query2", "test query");<br>
 * HashMap<String, String> headers = new HashMap<String, String>();<br>
 * headers.put("Authorization", "Some-Credential-Values");<br>
 * headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.1-update1; ko-kr; Nexus One Build/ERE27) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");<br>
 * SimpleHttResponse response = instance.get("http://some.test.url", headers, params);	//synchronous<br>
 * String asyncJobId = instance.getAsync(handler, "http://some.test.url", headers, params);	//asynchronous<br><br>
 * 
 *	//blah blah...<br><br>
 * 
 * class SomeHandler extends Handler{<br>
 * 	\@Override<br>
 * 	public void handleMessage(Message msg)<br>
 * 	{<br>
 * 		super.handleMessage(msg);<br>
 * 		if(msg.arg1 > 0)<br>
 * 		{<br>
 * 			Bundle resultData = msg.getData();<br>
 * 			String _id = resultData.getString(ApacheHttpUtility.ASYNC_HTTP_TASK_ID);<br>
 * 			if(asyncJobId.compareTo(_id) == 0)<br>
 * 			{<br>
 * 				SimpleHttpResponse response = (SimpleHttpResponse)resultData.getParcelable(ApacheHttpUtility.ASYNC_HTTP_RESULT);<br>
 * 				Logger.d("(" + _id + ") http status code = " + response.getHttpStatusCode() + ", content = " + response.getHttpResponseBodyAsString());<br>
 * 				((TextView)TestActivity.this.findViewById(R.id.txt)).setText("length: " + response.getHttpResponseBody().length);<br>
 * 			}<br>
 * 		}<br>
 * 		else<br>
 * 			Logger.e("http error");<br>
 * }
 * </p>
 */
final public class ApacheHttpUtility
{
	private static ApacheHttpUtility httpUtility = null;
	private static HashMap<String, AsyncHttpTask> asyncTaskPool = null;

	//default timeout values
	private static int connectionTimeoutMillis = 3000;
	private static int socketTimeoutMillis = 3000;

	public static final int ASYNC_METHOD_GET = 1;
	public static final int ASYNC_METHOD_POST = 1 << 1;
	public static final int ASYNC_METHOD_POSTBYTES = 1 << 2;

	public static final String ASYNC_HTTP_TASK_ID = "apache.httputil.async.task.id";
	public static final String ASYNC_HTTP_RESULT = "apache.httputil.async.result";

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
		{
			httpUtility = new ApacheHttpUtility();
			asyncTaskPool = new HashMap<String, AsyncHttpTask>();
		}

		return httpUtility;
	}

	/**
	 * 
	 * @param newConnectionTimeoutMillis
	 */
	public static void setConnectionTimeout(int newConnectionTimeoutMillis)
	{
		connectionTimeoutMillis = newConnectionTimeoutMillis;
	}

	/**
	 * 
	 * @return
	 */
	public static int getConnectionTimeout()
	{
		return connectionTimeoutMillis;
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
	 * 
	 * @return
	 */
	private DefaultHttpClient createHttpClient()
	{
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeoutMillis);
		HttpConnectionParams.setSoTimeout(params, socketTimeoutMillis);

		DefaultHttpClient client = new DefaultHttpClient(params);
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		
		return client;
	}

	/**
	 * 
	 * @param url
	 * @param headerValues
	 * @param getParameters
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public SimpleHttpResponse get(String url, Map<String, String> headerValues, Map<String, String> getParameters)
	{
		DefaultHttpClient client = createHttpClient();  

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

		//header
		if(headerValues != null)
		{
			for(String key: headerValues.keySet())
				get.addHeader(key, headerValues.get(key));
		}

		//response
		HttpResponse response;
		try
		{
			response = client.execute(get);
			return new SimpleHttpResponse(response);
		}
		catch(IOException e)
		{
			Logger.e(e.toString());
		}
		finally
		{
			client.getConnectionManager().shutdown();
		}

		return null;
	}

	/**
	 * 
	 * @param url
	 * @param headerValues
	 * @param postParameters
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public SimpleHttpResponse post(String url, Map<String, String> headerValues, Map<String, Object> postParameters)
	{
		DefaultHttpClient client = createHttpClient();
		HttpPost post = new HttpPost(url);

		//header
		if(headerValues != null)
		{
			for(String key: headerValues.keySet())
				post.addHeader(key, headerValues.get(key));
		}

		//params
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
					MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
					for(String key: postParameters.keySet())
					{
						Object value = postParameters.get(key);
						if(value.getClass() == File.class)
						{
							File file = (File)value;
							multipartEntity.addPart(key, new FileBody(file, SimpleHttpResponse.getMimeType(file)));
						}
						else
						{
							try
							{
								multipartEntity.addPart(key, new StringBody(value.toString()));
							}
							catch(UnsupportedEncodingException e)
							{
								Logger.e(e.toString());
							}
						}
					}
					post.setEntity(multipartEntity);
				}
				else
				{
					ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					for(String key: postParameters.keySet())
						params.add(new BasicNameValuePair(key, postParameters.get(key).toString()));
					try
					{
						UrlEncodedFormEntity urlEncodedFormEntity;
						urlEncodedFormEntity = new UrlEncodedFormEntity(params, "utf-8");
						post.setEntity(urlEncodedFormEntity);
					}
					catch(UnsupportedEncodingException e)
					{
						Logger.e(e.toString());
					}
				}
			}
		}

		//response
		HttpResponse response;
		try
		{
			response = client.execute(post);
			return new SimpleHttpResponse(response);
		}
		catch(IOException e)
		{
			Logger.e(e.toString());
		}
		finally
		{
			client.getConnectionManager().shutdown();
		}

		return null;
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
	public SimpleHttpResponse postBytes(String url, Map<String, String> headerValues, byte[] bytes, String contentType)
	{
		DefaultHttpClient client = createHttpClient();

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

		//response
		HttpResponse response;
		try
		{
			response = client.execute(post);
			return new SimpleHttpResponse(response);
		}
		catch(IOException e)
		{
			Logger.e(e.toString());
		}
		finally
		{
			client.getConnectionManager().shutdown();
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
	 * 
	 * @param id
	 * @return
	 */
	public AsyncHttpTask getAsyncHttpTask(String id)
	{
		synchronized(this)
		{
			return asyncTaskPool.get(id);
		}
	}

	/**
	 * 
	 * @param id
	 * @return false if failed, or no need to cancel (already finished)
	 */
	public boolean cancelAsyncHttpTask(String id)
	{
		synchronized(this)
		{
			AsyncHttpTask selected = asyncTaskPool.get(id);
			if(selected == null)
			{
				Logger.i("no such AsyncHttpTask id to cancel: " + id);
			}
			else
			{
				return selected.cancelConnection();
			}
		}
		
		return false;
	}

	/**
	 * 
	 */
	public void cancelAllAsyncHttpTasks()
	{
		Logger.v("canceling all async http tasks");

		synchronized(this)
		{
			for(AsyncHttpTask task: asyncTaskPool.values())
			{
				task.cancelConnection();
			}
		}
	}


	/**
	 * 
	 * AsyncTask for executing HTTP methods
	 * (result Bundle object will carry AsyncHttpTask's id and SimpleHttpResponse)
	 * 
	 * @author meinside@gmail.com
	 * @since 10.10.25.
	 * 
	 * last update 10.10.27.
	 *
	 */
	private class AsyncHttpTask extends AsyncTask<Object, Void, SimpleHttpResponse>
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
			synchronized(ApacheHttpUtility.this)
			{
				asyncTaskPool.put(_id, this);
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(SimpleHttpResponse result)
		{
			super.onPostExecute(result);

			synchronized(ApacheHttpUtility.this)
			{
				Message msg = Message.obtain();
				if(result != null)
				{
					msg.arg1 = result.getHttpStatusCode();
					msg.arg2 = result.getHttpResponseBody().length;

					Bundle data = new Bundle();
					data.putString(ASYNC_HTTP_TASK_ID, _id);
					data.putParcelable(ASYNC_HTTP_RESULT, result);
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
				response = ApacheHttpUtility.this.get((String)params[2], (Map<String, String>)params[3], (Map<String, String>)params[4]);
				break;
			case ASYNC_METHOD_POST:
				response = ApacheHttpUtility.this.post((String)params[2], (Map<String, String>)params[3], (Map<String, Object>)params[4]);
				break;
			case ASYNC_METHOD_POSTBYTES:
				response = ApacheHttpUtility.this.postBytes((String)params[2], (Map<String, String>)params[3], (byte[])params[4], (String)params[5]);
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
			synchronized(ApacheHttpUtility.this)
			{
				asyncTaskPool.remove(_id);
			}

			super.onCancelled();
		}

		/**
		 * 
		 * @return
		 */
		public boolean cancelConnection()
		{
			Logger.v("canceling async http tasks with id: " + _id);

			return this.cancel(true);
		}
	}
}
