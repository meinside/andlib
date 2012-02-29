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
