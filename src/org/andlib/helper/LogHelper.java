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

import android.util.Log;

/**
 * ugly log helper class
 * 
 * @author meinside@gmail.com
 * @since 09.10.12.
 * 
 * last update 10.04.13.
 *
 */
final public class LogHelper
{
	/**
	 * set this to true when in production building
	 */
	public static boolean WILL_BE_RELEASED = false;
	
	public static final int SHOW_MINIMAL = 1;
	public static final int SHOW_CLASS = SHOW_MINIMAL >> 1;
	public static final int SHOW_LINENUMBER = SHOW_MINIMAL >> 2;
	
	public static final int SHOW_DEFAULT = SHOW_CLASS | SHOW_LINENUMBER;

	/**
	 * 
	 * @param message
	 */
	public static void v(String message)
	{
		if(WILL_BE_RELEASED)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();

		//class name
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
		
		Log.v(buffer.toString(), message);
	}

	/**
	 * 
	 * @param message
	 * @param option
	 */
	public static void v(String message, int option)
	{
		if(WILL_BE_RELEASED)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();
		
		//class
		if((option & SHOW_CLASS) == SHOW_CLASS)
		{
			String className = stack.getClassName();
			int indexOfPoint;
			if((indexOfPoint = className.lastIndexOf(".")) != -1)
			{
				className = className.substring(indexOfPoint + 1);
			}
			buffer.append(className);
			buffer.append(".");
		}
		
		//method name
		buffer.append(stack.getMethodName());
		buffer.append("()");
		
		//line number
		if((option & SHOW_LINENUMBER) == SHOW_LINENUMBER)
		{
			buffer.append(": Line ");
			buffer.append(stack.getLineNumber());
		}

		Log.v(buffer.toString(), message);
	}

	/**
	 * 
	 * @param message
	 */
	public static void d(String message)
	{
		if(WILL_BE_RELEASED)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();

		//class name
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
		
		Log.d(buffer.toString(), message);
	}

	/**
	 * 
	 * @param message
	 * @param option
	 */
	public static void d(String message, int option)
	{
		if(WILL_BE_RELEASED)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();
		
		//class
		if((option & SHOW_CLASS) == SHOW_CLASS)
		{
			String className = stack.getClassName();
			int indexOfPoint;
			if((indexOfPoint = className.lastIndexOf(".")) != -1)
			{
				className = className.substring(indexOfPoint + 1);
			}
			buffer.append(className);
			buffer.append(".");
		}
		
		//method name
		buffer.append(stack.getMethodName());
		buffer.append("()");
		
		//line number
		if((option & SHOW_LINENUMBER) == SHOW_LINENUMBER)
		{
			buffer.append(": Line ");
			buffer.append(stack.getLineNumber());
		}

		Log.d(buffer.toString(), message);
	}

	/**
	 * 
	 * @param message
	 */
	public static void i(String message)
	{
		if(WILL_BE_RELEASED)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();

		//class name
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
		
		Log.i(buffer.toString(), message);
	}

	/**
	 * 
	 * @param message
	 * @param option
	 */
	public static void i(String message, int option)
	{
		if(WILL_BE_RELEASED)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();
		
		//class
		if((option & SHOW_CLASS) == SHOW_CLASS)
		{
			String className = stack.getClassName();
			int indexOfPoint;
			if((indexOfPoint = className.lastIndexOf(".")) != -1)
			{
				className = className.substring(indexOfPoint + 1);
			}
			buffer.append(className);
			buffer.append(".");
		}
		
		//method name
		buffer.append(stack.getMethodName());
		buffer.append("()");
		
		//line number
		if((option & SHOW_LINENUMBER) == SHOW_LINENUMBER)
		{
			buffer.append(": Line ");
			buffer.append(stack.getLineNumber());
		}

		Log.i(buffer.toString(), message);
	}

	/**
	 * 
	 * @param message
	 */
	public static void w(String message)
	{
		if(WILL_BE_RELEASED)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();

		//class name
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
		
		Log.w(buffer.toString(), message);
	}

	/**
	 * 
	 * @param message
	 * @param option
	 */
	public static void w(String message, int option)
	{
		if(WILL_BE_RELEASED)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();
		
		//class
		if((option & SHOW_CLASS) == SHOW_CLASS)
		{
			String className = stack.getClassName();
			int indexOfPoint;
			if((indexOfPoint = className.lastIndexOf(".")) != -1)
			{
				className = className.substring(indexOfPoint + 1);
			}
			buffer.append(className);
			buffer.append(".");
		}
		
		//method name
		buffer.append(stack.getMethodName());
		buffer.append("()");
		
		//line number
		if((option & SHOW_LINENUMBER) == SHOW_LINENUMBER)
		{
			buffer.append(": Line ");
			buffer.append(stack.getLineNumber());
		}

		Log.w(buffer.toString(), message);
	}

	/**
	 * 
	 * @param message
	 */
	public static void e(String message)
	{
		if(WILL_BE_RELEASED)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();

		//class name
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
		
		Log.e(buffer.toString(), message);
	}

	/**
	 * 
	 * @param message
	 * @param option
	 */
	public static void e(String message, int option)
	{
		if(WILL_BE_RELEASED)
			return;

		StackTraceElement stack = new Throwable().getStackTrace()[1];
		StringBuffer buffer = new StringBuffer();
		
		//class
		if((option & SHOW_CLASS) == SHOW_CLASS)
		{
			String className = stack.getClassName();
			int indexOfPoint;
			if((indexOfPoint = className.lastIndexOf(".")) != -1)
			{
				className = className.substring(indexOfPoint + 1);
			}
			buffer.append(className);
			buffer.append(".");
		}
		
		//method name
		buffer.append(stack.getMethodName());
		buffer.append("()");
		
		//line number
		if((option & SHOW_LINENUMBER) == SHOW_LINENUMBER)
		{
			buffer.append(": Line ");
			buffer.append(stack.getLineNumber());
		}

		Log.e(buffer.toString(), message);
	}
}
