package org.andlib.helpers.sound;

import java.io.File;
import java.util.ArrayList;

import org.andlib.helpers.Logger;

import android.content.Context;
import android.os.AsyncTask;


/**
 * for playing multiple sound files in a row
 * 
 * @author meinside@gmail.com
 * @since 10.10.28.
 * 
 * last update 13.06.12.
 *
 */
public class MultipleSoundPlayer<F> extends SoundPlayer
{
	private Context context;
	private MultipleSoundPlayerListener<F> listener;

	private long gapTimeMillis;
	private boolean isFixedRate;
	private AsyncSoundTask asyncSoundTask;

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
				
		stop();
	}

	/**
	 * 
	 * @param soundFiles an ArrayList of type: Integer(resource id), String(file path), or File
	 * @param delayTimeMillis
	 * @param gapTimeMillis
	 * @param isFixedRate
	 */
	synchronized public void playSounds(ArrayList<F> soundFiles, long delayTimeMillis, long gapTimeMillis, boolean isFixedRate)
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
			
		stop();

		asyncSoundTask = new AsyncSoundTask();
		asyncSoundTask.execute(soundFiles);
	}

	/**
	 * 
	 * @author meinside@gmail.com
	 *
	 */
	protected class AsyncSoundTask extends AsyncTask<Object, Integer, ArrayList<F>>
	{
		private F currentSound;

		@Override
		protected void onCancelled() {
			if(listener != null)
				listener.allSoundPlaysFinished(false);

			super.onCancelled();
		}

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
			ArrayList<F> files = (ArrayList<F>)args[0];

			while(files.size() > 0)
			{
				if(isCancelled())
					return null;

				//current sound's play will start
				currentSound = files.remove(0);

				if(listener != null)
					listener.soundWillBePlayed(currentSound);

				if(isCancelled())
					return null;

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
					if(listener != null)
						listener.soundPlayFailed(currentSound);
				}

				if(isCancelled())
					return null;

				//sleep
				long timeToSleep = gapTimeMillis + (isFixedRate ? 0 : SoundUtility.getDurationOfSound(context, currentSound));
				try
				{
					Thread.sleep(timeToSleep);
				}
				catch(InterruptedException e)	//it doesn't do harm
				{
					Logger.v(e.toString());
				}

				if(isCancelled())
					return null;

				//current sound's play finished
				if(listener != null)
					listener.soundPlayFinished(currentSound);
			}

			//all sound plays were finished
			if(listener != null)
				listener.allSoundPlaysFinished(true);

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<F> played)
		{
			super.onPostExecute(played);

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
		public void allSoundPlaysFinished(boolean finishedSuccessfully);

		/**
		 * 
		 * @param file
		 */
		public void soundPlayFailed(F file);
	}
}

