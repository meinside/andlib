package org.andlib.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.andlib.helpers.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.ByteArrayBuffer;

import android.os.Parcel;
import android.os.Parcelable;
import android.webkit.MimeTypeMap;

/**
 * Simple HTTP response class for ApacheHttpUtility
 * 
 * @author meinside@gmail.com
 * @since 09.10.25.
 * 
 * last update 11.03.10.
 *
 */
public class SimpleHttpResponse implements Parcelable
{
	public static final int BYTES_BUFFER_INITIAL_SIZE = 32 * 1024;	//32KB
	public static final int FILE_BUFFER_SIZE = 8 * 1024;	//8KB
	public static final int READ_BUFFER_SIZE = 8 * 1024;	//8KB

	private int httpStatusCode = -1;
	private byte[] httpResponseBody = null;
	private Header[] httpResponseHeaders = null;
	private String contentType = null;
	private String contentEncoding = null;

	/**
	 * 
	 * @param httpStatusCode
	 * @param httpResponseBody
	 * @param httpHeaders
	 */
	public SimpleHttpResponse(HttpResponse response)
	{
		if(response != null)
		{
			httpStatusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();  
			if(entity != null)
			{
				try
				{
					httpResponseBody = readBytesFromInputStream(entity.getContent());
				}
				catch(IllegalStateException e)
				{
					Logger.e(e.toString());
				}
				catch(IOException e)
				{
					Logger.e(e.toString());
				}
				httpResponseHeaders = response.getAllHeaders();
			}

			Header contentTypeHeader = response.getEntity().getContentType();
			if(contentTypeHeader != null)
				contentType = contentTypeHeader.getValue();
			Header contentEncodingHeader = response.getEntity().getContentEncoding();
			if(contentEncodingHeader != null)
				contentEncoding = contentEncodingHeader.getValue();
		}
		else
		{
			Logger.i("response is null");
		}
	}

	/**
	 * 
	 * @param httpStatusCode
	 * @param httpResponseBody
	 * @param httpHeaders
	 */
	public SimpleHttpResponse(int httpStatusCode, byte[] httpResponseBody, Map<String, List<String>> httpHeaders)
	{
		this.httpStatusCode = httpStatusCode;
		this.httpResponseBody = httpResponseBody;
		
		if(httpHeaders != null)
		{
			httpResponseHeaders = new Header[httpHeaders.size()];
			int index = 0;
			for(String key: httpHeaders.keySet())
			{
				if(key == null)	//skip header with null name (fix for honeycomb?)
					continue;

				List<String> values = httpHeaders.get(key);
				StringBuffer valuesBuffer = new StringBuffer();
				for(String value: values)
				{
					if(valuesBuffer.length() > 0)
						valuesBuffer.append(", ");
					valuesBuffer.append(value);
				}
				httpResponseHeaders[index ++] = new BasicHeader(key, valuesBuffer.toString());
			}
		}
	}

	/**
	 * 
	 * @param in
	 */
	public SimpleHttpResponse(Parcel in)
	{
		readFromParcel(in);
	}
	
	/**
	 * 
	 * @return -1 if none
	 */
	public int getHttpStatusCode()
	{
		return httpStatusCode;
	}
	
	/**
	 * 
	 * @return empty array if none
	 */
	public byte[] getHttpResponseBody()
	{
		if(httpResponseBody == null)
			return new byte[0];
		return httpResponseBody;
	}

	/**
	 * 
	 * @return empty string if none
	 */
	public String getHttpResponseBodyAsString()
	{
		if(httpResponseBody == null)
			return "";
		return new String(httpResponseBody);
	}

	/**
	 * 
	 * @return empty array if not known
	 */
	public Header[] httpResponseHeaders()
	{
		if(httpResponseHeaders == null)
			return new Header[0];
		return httpResponseHeaders;
	}

	/**
	 * 
	 * @return empty string if not known
	 */
	public String getContentType()
	{
		if(contentType == null)
			return "";
		return contentType;
	}

	/**
	 * 
	 * @return null if not known
	 */
	public String getContentEncoding()
	{
		return contentEncoding;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SimpleHttpResponse createFromParcel(Parcel in) {
            return new SimpleHttpResponse(in);
        }
 
        public SimpleHttpResponse[] newArray(int size) {
            return new SimpleHttpResponse[size];
        }
    };

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents()
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		//http status code
		dest.writeInt(httpStatusCode);
		
		//http response body
		if(httpResponseBody != null)
		{
			//bytes' length
			dest.writeInt(httpResponseBody.length);

			//bytes
			dest.writeByteArray(httpResponseBody);
		}
		else
		{
			dest.writeInt(0);
		}
		
		//http headers
		if(httpResponseHeaders != null)
		{
			//http headers' count
			int count = httpResponseHeaders.length;
			dest.writeInt(count);

			//http header keys and values
			for(Header header: httpResponseHeaders)
			{
				dest.writeString(header.getName());
				dest.writeString(header.getValue());
			}
		}
		else
		{
			dest.writeInt(0);
		}
		
		//content type and encoding
		dest.writeString(getContentType());
		dest.writeString(getContentEncoding());
	}

	/**
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in)
	{
		//http status code
		httpStatusCode = in.readInt();
		
		//http response body
		int bytesLength = in.readInt();
		if(bytesLength > 0)
		{
			httpResponseBody = new byte[bytesLength];
			in.readByteArray(httpResponseBody);
		}
		else
			httpResponseBody = null;
		
		//http headers
		int headerCount = in.readInt();
		if(headerCount > 0)
		{
			httpResponseHeaders = new Header[headerCount];

			String key, value;
			for(int i=0; i<headerCount; i++)
			{
				key = in.readString();
				value = in.readString();
				
				httpResponseHeaders[i] = new BasicHeader(key, value);
			}
		}
		else
			httpResponseHeaders = null;
		
		//content type and encoding
		contentType = in.readString();
		contentEncoding = in.readString();
	}

	/**
	 * Read up bytes from given InputStream instance and return
	 * 
	 * @param is (given InputStream instance is not closed by this function)
	 * @return
	 */
	public static byte[] readBytesFromInputStream(InputStream is)
	{
		try
		{
			ByteArrayBuffer buffer = new ByteArrayBuffer(BYTES_BUFFER_INITIAL_SIZE);
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
			Logger.e(e.toString());
		}

		return null;
	}

	/**
	 * return mime type of given file
	 * 
	 * @param file
	 * @return when mime type is unknown, it simply returns "application/octet-stream"
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
			Logger.e(e.toString());
		}
		if(mimeType == null)
			mimeType = "application/octet-stream";

		return mimeType;
	}
}
