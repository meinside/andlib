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

package org.andlib.helpers.sound;

import org.andlib.helpers.Logger;
import org.andlib.helpers.ResourceHelper;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;

/**
 * for playing sound files using MediaPlayer
 * (SoundPool can be an alternate way, but it limits file formats and play time) 
 * 
 * FIXXX: creating MediaPlayer each time due to this problem: 
 *  - http://www.mail-archive.com/android-developers@googlegroups.com/msg32581.html
 * 
 * @author meinside@gmail.com
 * @since 10.02.28.
 * 
 * last update 10.10.29.
 *
 */
public class SoundPlayer
{
	private static SoundPlayer sharedInstance = null;
	
	protected MediaPlayer player = null;
	
	protected OnCompletionListener onCompletionListener = null;
	protected OnErrorListener onErrorListener = null;

	
	/**
	 * 
	 */
	public SoundPlayer()
	{
		this(null, null);
	}

	/**
	 * 
	 * @param completionListener
	 * @param errorListener
	 */
	public SoundPlayer(OnCompletionListener completionListener, OnErrorListener errorListener)
	{
		onCompletionListener = completionListener;
		onErrorListener = errorListener;
	}

	/**
	 * 
	 * @return shared instance
	 */
	public synchronized static SoundPlayer getSharedInstance()
	{
		if(sharedInstance == null)
		{
			sharedInstance = new SoundPlayer();
		}
		return sharedInstance;
	}

	/**
	 * 
	 */
	public synchronized static void disposeSharedInstance()
	{
		if(sharedInstance != null)
		{
			sharedInstance.stop();
			sharedInstance = null;
		}
	}
	
	/**
	 * set OnCompletionListener for MediaPlayer
	 * 
	 * @param listener
	 */
	public void setOnCompletionListener(OnCompletionListener listener)
	{
		onCompletionListener = listener;
	}
	
	/**
	 * set OnErrorListener for MediaPlayer
	 * 
	 * @param listener
	 */
	public void setOnErrorListener(OnErrorListener listener)
	{
		onErrorListener = listener;
	}

	/**
	 * 
	 */
	public void clearListeners()
	{
		onCompletionListener = null;
		onErrorListener = null;
	}
	
	/**
	 * 
	 */
	protected void setListeners()
	{
		if(player != null)
		{
			if(onCompletionListener != null)
				player.setOnCompletionListener(onCompletionListener);
			if(onErrorListener != null)
				player.setOnErrorListener(onErrorListener);
		}
	}
	
	/**
	 * play a file with given resource id
	 * 
	 * @param context
	 * @param resid
	 */
	synchronized public void play(Context context, int resid)
	{
		Logger.v("trying to play: " + resid);

		stop();

		try
        {
			player = MediaPlayer.create(context, resid);
			setListeners();
			player.start();
        }
        catch(Exception e)
        {
        	Logger.e(e.toString());
        }
	}
	
	/**
	 * 
	 * @param context
	 * @param type (ex: raw/sound.mp3 => "raw")
	 * @param filename without extension (ex: raw/sound.mp3 => "sound")
	 */
	synchronized public void play(Context context, String type, String filename)
	{
		Logger.v("trying to play: " + filename);

		stop();
		
		try
        {
			player = new MediaPlayer();
			setListeners();
			AssetFileDescriptor afd = ResourceHelper.getResourceAsFd(context, filename, type);
			//???: MediaPlayer.setDataSource(FileDescriptor) function always fails here
	        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
	        afd.close();
	        player.prepare();
			player.start();
        }
        catch(Exception e)
        {
        	Logger.e(e.toString());
        }
	}
	
	/**
	 * play a file with given path string
	 * 
	 * @param filepath
	 */
	synchronized public void play(String filepath)
	{
		Logger.v("trying to play: " + filepath);
		
		stop();
		
		try
        {
			player = new MediaPlayer();
			setListeners();
			player.setDataSource(filepath);
			player.prepare();
			player.start();
        }
        catch(Exception e)
        {
        	Logger.e(e.toString());
        }
	}
	
	/**
	 * play a file with given uri
	 * 
	 * @param context
	 * @param fileuri
	 */
	synchronized public void play(Context context, Uri fileuri)
	{
		Logger.v("trying to play: " + fileuri.toString());

		stop();
		
		try
        {
			player = MediaPlayer.create(context, fileuri);
			setListeners();
			player.start();
        }
        catch(Exception e)
        {
        	Logger.e(e.toString());
        }
	}
	
	/**
	 * 
	 */
	synchronized public void stop()
	{
		try
		{
			if(player != null)
			{
				if(player.isPlaying())
				{
					player.stop();
					
					Logger.v("stopped previously played sound");
				}
			}
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
		}
		finally
		{
			if(player != null)
			{
				player.release();
				player = null;
			}
		}
	}
}
