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

import java.io.File;
import java.util.ArrayList;

import org.andlib.helpers.Logger;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;


/**
 * for playing multiple sound files in a row
 * 
 * @author meinside@gmail.com
 * @since 10.10.28.
 * 
 * last update 10.11.24.
 *
 */
public class MultipleSoundPlayer<F> extends SoundPlayer
{
	private Context context;
	private MultipleSoundPlayerListener<F> listener;

	private long gapTimeMillis;
	private boolean isFixedRate;
	private AsyncSoundTask asyncSoundTask;
	
	private F currentSound;
	private Integer currentAsyncTaskHash;


	/**
	 * 
	 */
	public MultipleSoundPlayer(Context context, MultipleSoundPlayerListener<F> listener)
	{
		super();
		
		this.context = context;

		if(listener == null)
		{
			Logger.e("listener is null");
		}
		this.listener = listener;
		
		currentAsyncTaskHash = -1;
	}

	/**
	 * 
	 */
	synchronized public void stopAllSounds()
	{
		Logger.v("trying to stop all remaining sounds");

		if(asyncSoundTask != null)
		{
			if(!asyncSoundTask.isCancelled())
			{
				asyncSoundTask.cancel(true);
				asyncSoundTask = null;

				Logger.v("cancelled previous async sound task");
			}
		}

		currentAsyncTaskHash = -1;
		currentSound = null;
				
		stop();
	}

	/**
	 * 
	 * @param handler
	 * @param soundFiles an ArrayList of type: Integer(resource id), String(file path), or File
	 * @param delayTimeMillis
	 * @param gapTimeMillis
	 * @param isFixedRate
	 */
	synchronized public void playSounds(Handler handler, ArrayList<F> soundFiles, long delayTimeMillis, long gapTimeMillis, boolean isFixedRate)
	{
		Logger.v("start playing multiple sound files: " + soundFiles);

		this.gapTimeMillis = gapTimeMillis;
		this.isFixedRate = isFixedRate;

		if(asyncSoundTask != null)
		{
			if(!asyncSoundTask.isCancelled())
			{
				asyncSoundTask.cancel(true);
				asyncSoundTask = null;

				Logger.v("cancelled previous async sound task");
			}
		}

		currentAsyncTaskHash = -1;
		currentSound = null;
			
		stop();

		asyncSoundTask = new AsyncSoundTask();
		currentAsyncTaskHash = asyncSoundTask.hashCode();
		asyncSoundTask.execute(handler, soundFiles);
	}

	/**
	 * 
	 * @author meinside@gmail.com
	 *
	 */
	protected class AsyncSoundTask extends AsyncTask<Object, Integer, ArrayList<F>>
	{
		private Handler handler;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			Logger.v("start playing multiple files");
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected ArrayList<F> doInBackground(Object... args)
		{
			handler = (Handler)args[0];
			ArrayList<F> files = (ArrayList<F>)args[1];

			while(files.size() > 0)
			{
				//current sound's play will start
				synchronized(currentAsyncTaskHash)
				{
					if(currentAsyncTaskHash == this.hashCode())
					{
						currentSound = files.remove(0);

						if(listener != null && handler != null)
						{
							if(currentAsyncTaskHash == this.hashCode())
								handler.post(new Runnable(){
									@Override
									public void run()
									{
										listener.soundWillBePlayed(currentSound);
									}});
							else
								break;
						}
					}
					else
						break;
				}

				synchronized(currentAsyncTaskHash)
				{
					if(currentAsyncTaskHash != this.hashCode())
						break;

					Class<? extends Object> currentArgClass = currentSound.getClass();
					if(currentArgClass == Integer.class)
					{
						play(context, ((Integer)currentSound).intValue());
					}
					else if(currentArgClass == String.class)
					{
						play((String)currentSound);
					}
					else if(currentArgClass == File.class)
					{
						play(((File)currentSound).getAbsolutePath());
					}
					else
					{
						Logger.e("not a proper type: " + currentArgClass.getName());
						
						//current sound's play failed
						if(currentAsyncTaskHash == this.hashCode())
						{
							if(listener != null && handler != null)
								handler.post(new Runnable(){
									@Override
									public void run()
									{
										listener.soundPlayFailed(currentSound);
									}});
						}
					}
				}

				//current sound's play finished
				synchronized(currentAsyncTaskHash)
				{
					if(currentAsyncTaskHash == this.hashCode())
					{
						if(listener != null && handler != null)
							handler.post(new Runnable(){
								@Override
								public void run()
								{
									listener.soundPlayFinished(currentSound);
								}});
					}
				}

				synchronized(currentAsyncTaskHash)
				{
					if(currentAsyncTaskHash == this.hashCode())
					{
						long timeToSleep = gapTimeMillis + (isFixedRate ? 0 : SoundUtility.getDurationOfSound(context, currentSound));
						try
						{
							Thread.sleep(timeToSleep);
						}
						catch(InterruptedException e)	//it doesn't do harm
						{
							Logger.v(e.toString());
						}
					}
				}
			}

			//all sound plays were finished
			synchronized(currentAsyncTaskHash)
			{
				if(currentAsyncTaskHash == this.hashCode())
				{
					if(listener != null && handler != null)
						handler.post(new Runnable(){
							@Override
							public void run()
							{
								listener.allSoundPlaysFinished();
							}});
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<F> played)
		{
			super.onPostExecute(played);
			
			synchronized(currentAsyncTaskHash)
			{
				if(currentAsyncTaskHash == this.hashCode())
					currentAsyncTaskHash = -1;
			}

			Logger.v("finished playing multiple files");
		}
	}

	/**
	 * 
	 * @author meinside@gmail.com
	 *
	 */
	public interface MultipleSoundPlayerListener<F>
	{
		/**
		 * 
		 * @param file
		 */
		public void soundWillBePlayed(F file);

		/**
		 * 
		 * @param file
		 */
		public void soundPlayFinished(F file);

		/**
		 * 
		 */
		public void allSoundPlaysFinished();

		/**
		 * 
		 * @param file
		 */
		public void soundPlayFailed(F file);
	}
}
