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

package org.andlib.helpers;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

/**
 * logger helper class
 * 
 * @author meinside@gmail.com
 * @since 09.10.12.
 * 
 * last update 10.10.25.
 *
 */
final public class Logger
{
	private static boolean initialized;
	private static boolean debuggable;
	
	//default action: log unconditionally
	static{
		doLogUnconditionally();
	}

	/**
	 * always log messages 
	 */
	public static void doLogUnconditionally()
	{
		debuggable = true;
		initialized = false;
	}

	/**
	 * do not log messages unless this application is debuggable (declared in Manifest file)
	 * @param appContext
	 */
	public static void doNotLogUnlessDebuggable(Context appContext)
	{
		if(!initialized || !debuggable)
		{
			ApplicationInfo info = appContext.getApplicationInfo();

			debuggable = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) > 0;
			initialized = true;
		}
	}
	
	/**
	 * 
	 * @param tag
	 * @param message
	 */
	public static void v(String tag, String message)
	{
		if(initialized && !debuggable)
			return;

		Log.v(tag, message);
	}

	/**
	 * 
	 * @param message
	 * @param verbose
	 */
	public static void v(String message)
	{
		if(initialized && !debuggable)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();
		
		//class
		String className = stack.getClassName();
		int indexOfPoint;
		if((indexOfPoint = className.lastIndexOf(".")) != -1)
		{
			className = className.substring(indexOfPoint + 1);
		}
		buffer.append(className);
		buffer.append(".");

		//method name
		buffer.append(stack.getMethodName());
		buffer.append("()");
		
		//line number
		buffer.append(":");
		buffer.append(stack.getLineNumber());

		Log.v(buffer.toString(), message);
	}
	
	/**
	 * 
	 * @param tag
	 * @param message
	 */
	public static void d(String tag, String message)
	{
		if(initialized && !debuggable)
			return;

		Log.d(tag, message);
	}

	/**
	 * 
	 * @param message
	 * @param verbose
	 */
	public static void d(String message)
	{
		if(initialized && !debuggable)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();
		
		//class
		String className = stack.getClassName();
		int indexOfPoint;
		if((indexOfPoint = className.lastIndexOf(".")) != -1)
		{
			className = className.substring(indexOfPoint + 1);
		}
		buffer.append(className);
		buffer.append(".");
		
		//method name
		buffer.append(stack.getMethodName());
		buffer.append("()");
		
		//line number
		buffer.append(":");
		buffer.append(stack.getLineNumber());

		Log.d(buffer.toString(), message);
	}
	
	/**
	 * 
	 * @param tag
	 * @param message
	 */
	public static void i(String tag, String message)
	{
		if(initialized && !debuggable)
			return;

		Log.i(tag, message);
	}

	/**
	 * 
	 * @param message
	 * @param verbose
	 */
	public static void i(String message)
	{
		if(initialized && !debuggable)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();
		
		//class
		String className = stack.getClassName();
		int indexOfPoint;
		if((indexOfPoint = className.lastIndexOf(".")) != -1)
		{
			className = className.substring(indexOfPoint + 1);
		}
		buffer.append(className);
		buffer.append(".");
		
		//method name
		buffer.append(stack.getMethodName());
		buffer.append("()");
		
		//line number
		buffer.append(":");
		buffer.append(stack.getLineNumber());

		Log.i(buffer.toString(), message);
	}

	/**
	 * 
	 * @param tag
	 * @param message
	 */
	public static void w(String tag, String message)
	{
		if(initialized && !debuggable)
			return;

		Log.w(tag, message);
	}

	/**
	 * 
	 * @param message
	 * @param verbose
	 */
	public static void w(String message)
	{
		if(initialized && !debuggable)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();
		
		//class
		String className = stack.getClassName();
		int indexOfPoint;
		if((indexOfPoint = className.lastIndexOf(".")) != -1)
		{
			className = className.substring(indexOfPoint + 1);
		}
		buffer.append(className);
		buffer.append(".");
		
		//method name
		buffer.append(stack.getMethodName());
		buffer.append("()");
		
		//line number
		buffer.append(":");
		buffer.append(stack.getLineNumber());

		Log.w(buffer.toString(), message);
	}

	/**
	 * 
	 * @param tag
	 * @param message
	 */
	public static void e(String tag, String message)
	{
		if(initialized && !debuggable)
			return;

		Log.e(tag, message);
	}

	/**
	 * 
	 * @param message
	 * @param verbose
	 */
	public static void e(String message)
	{
		if(initialized && !debuggable)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();
		
		//class
		String className = stack.getClassName();
		int indexOfPoint;
		if((indexOfPoint = className.lastIndexOf(".")) != -1)
		{
			className = className.substring(indexOfPoint + 1);
		}
		buffer.append(className);
		buffer.append(".");
		
		//method name
		buffer.append(stack.getMethodName());
		buffer.append("()");
		
		//line number
		buffer.append(":");
		buffer.append(stack.getLineNumber());

		Log.e(buffer.toString(), message);
	}
}
