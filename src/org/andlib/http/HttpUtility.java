package org.andlib.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.andlib.helpers.Logger;
import org.andlib.helpers.StringCodec;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author meinside@gmail.com
 * @since 09.10.05.
 * 
 * last update 11.03.10.
 *
 */
final public class HttpUtility
{
	private static HttpUtility httpUtility = null;
	private static HashMap<String, AsyncHttpTask> asyncTaskPool = null;

	//default connection values
	private static int connectionTimeoutMillis = 3000;
	private static int readTimeoutMillis = 3000;
	private static boolean useCaches = false;

	public static final int ASYNC_METHOD_GET = 1;
	public static final int ASYNC_METHOD_POST = 1 << 1;
	public static final int ASYNC_METHOD_POSTBYTES = 1 << 2;

	public static final String ASYNC_HTTP_TASK_ID = "httputil.async.task.id";
	public static final String ASYNC_HTTP_RESULT = "httputil.async.result";
	
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
	 * 
	 * @param newReadTimeoutMillis
	 */
	public static void setReadTimeout(int newReadTimeoutMillis)
	{
		readTimeoutMillis = newReadTimeoutMillis;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getReadTimeout()
	{
		return readTimeoutMillis;
	}
	
	/**
	 * 
	 * @param useCashesOrNot
	 */
	public static void setUseCaches(boolean useCashesOrNot)
	{
		useCaches = useCashesOrNot;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean getUseCaches()
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
			Logger.e(e.toString());
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
			byte[] responseBody = SimpleHttpResponse.readBytesFromInputStream(is);
			is.close();
			return new SimpleHttpResponse(connection.getResponseCode(), responseBody, connection.getHeaderFields());
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
			try
			{
				int responseCode = connection.getResponseCode();
				if(responseCode != -1)
					return new SimpleHttpResponse(responseCode, connection.getResponseMessage().getBytes(), null);
			}
			catch(IOException ioe)
			{
				Logger.e(ioe.toString());
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
			Logger.e(e.toString());
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
							dos.writeBytes("Content-Type: " + SimpleHttpResponse.getMimeType(file) + "\r\n\r\n");
							
							FileInputStream fis = new FileInputStream((File)value);
							byte[] buffer = new byte[SimpleHttpResponse.FILE_BUFFER_SIZE];
							
							int bytesRead = 0;
							while((bytesRead = fis.read(buffer, 0, SimpleHttpResponse.FILE_BUFFER_SIZE)) > 0)
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
				Logger.e(e.toString());
				connection.disconnect();
				return null;
			}
		}

		//get response
		try
		{
			InputStream is = connection.getInputStream();
			byte[] responseBody = SimpleHttpResponse.readBytesFromInputStream(is);
			is.close();
			return new SimpleHttpResponse(connection.getResponseCode(), responseBody, connection.getHeaderFields());
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
			try
			{
				int responseCode = connection.getResponseCode();
				if(responseCode != -1)
					return new SimpleHttpResponse(responseCode, connection.getResponseMessage().getBytes(), null);
			}
			catch(IOException ioe)
			{
				Logger.e(ioe.toString());
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
			Logger.e(e.toString());
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
			Logger.e(e.toString());
			connection.disconnect();
			return null;
		}

		//get response
		try
		{
			InputStream is = connection.getInputStream();
			byte[] responseBody = SimpleHttpResponse.readBytesFromInputStream(is);
			is.close();
			return new SimpleHttpResponse(connection.getResponseCode(), responseBody, connection.getHeaderFields());
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
			try
			{
				int responseCode = connection.getResponseCode();
				if(responseCode != -1)
					return new SimpleHttpResponse(responseCode, connection.getResponseMessage().getBytes(), null);
			}
			catch(IOException ioe)
			{
				Logger.e(ioe.toString());
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
	 * 
	 * @param id
	 * @return
	 */
	public AsyncHttpTask getAsyncHttpTask(String id)
	{
		return asyncTaskPool.get(id);
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
			synchronized(HttpUtility.this)
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

			synchronized(HttpUtility.this)
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
			synchronized(HttpUtility.this)
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


	/**
	 * get file's size at given url (using http header)
	 * 
	 * @param url
	 * @return -1 when failed
	 */
	public static int getFileSizeAtURL(URL url)
	{
		int filesize = -1;
		try
		{
	    	HttpURLConnection http = (HttpURLConnection)url.openConnection();
	    	filesize = http.getContentLength();
	    	http.disconnect();
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
		}
    	return filesize;
	}
}
