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

package org.andlib.helper;

import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

/**
 * 
 * @author meinside@gmail.com
 * @since 10.02.25.
 * 
 * last update 10.04.13.
 *
 */
public class ResourceHelper
{
	/**
	 * get resource id from given name and type
	 * 
	 * @param context
	 * @param name resource's name (without extension)
	 * @param type type of resource (ex: "drawable", "raw", ...)
	 * @return 0 if fails
	 */
	public static int getResourceId(Context context, String name, String type)
	{
		return context.getResources().getIdentifier(name, type, context.getPackageName());
	}
	
	/**
	 * 
	 * @param context
	 * @param resourceId
	 * @return null if fails
	 */
	public static InputStream getResourceAsInputStream(Context context, int resourceId)
	{
		try
		{
			return context.getResources().openRawResource(resourceId);
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
		}
		return null;
	}
	
	/**
	 * 
	 * @param context
	 * @param name of resource (without extension)
	 * @param type of resource (ex: "drawable", "raw", ...)
	 * @return null if fails
	 */
	public static InputStream getResourceAsInputStream(Context context, String name, String type)
	{
		try
		{
			return context.getResources().openRawResource(ResourceHelper.getResourceId(context, name, type));
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
		}
		return null;
	}
	
	/**
	 * 
	 * @param context
	 * @param resourceId
	 * @return null if fails
	 */
	public static AssetFileDescriptor getResourceAsFd(Context context, int resourceId)
	{
		try
		{
			return context.getResources().openRawResourceFd(resourceId);
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
		}
		return null;
	}
	
	/**
	 * 
	 * @param context
	 * @param name of resource (without extension)
	 * @param type of resource (ex: "drawable", "raw", ...)
	 * @return null if fails
	 */
	public static AssetFileDescriptor getResourceAsFd(Context context, String name, String type)
	{
		try
		{
			return context.getResources().openRawResourceFd(ResourceHelper.getResourceId(context, name, type));
		}
		catch(Exception e)
		{
			LogHelper.e(e.toString());
		}
		return null;
	}
}
