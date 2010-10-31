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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.andlib.helpers.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simple HTTP response class for ApacheHttpUtility
 * 
 * @author meinside@gmail.com
 * @since 09.10.25.
 * 
 * last update 10.10.27.
 *
 */
public class SimpleHttpResponse implements Parcelable
{
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
					httpResponseBody = ApacheHttpUtility.readBytesFromInputStream(entity.getContent());
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
}
