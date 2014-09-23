package org.andlib.helpers.sound;

import org.andlib.helpers.Logger;
import org.andlib.helpers.ResourceHelper;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
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
 * @since 2010.02.28.
 * 
 * last update 2014.09.23.
 *
 */
public class SoundPlayer
{
	private static SoundPlayer sharedInstance = null;
	
	protected MediaPlayer player = null;
	
	protected OnCompletionListener onCompletionListener = null;
	protected OnErrorListener onErrorListener = null;
	
	protected float leftVolume, rightVolume;

	protected int audioStreamType = AudioManager.STREAM_MUSIC;
	
	protected boolean isPaused = false;
	
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
		
		leftVolume = 1.0f;
		rightVolume = 1.0f;
	}

	/**
	 * change audio stream type (takes effect when called before play())
	 * 
	 * @param newAudioStreamType default: AudioManager.STREAM_MUSIC
	 */
	public void setAudioStreamType(int newAudioStreamType)
	{
		audioStreamType = newAudioStreamType;
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
	 * 
	 * @param leftVolume
	 * @param rightVolume
	 */
	public void setVolumes(float leftVolume, float rightVolume)
	{
		this.leftVolume = leftVolume;
		this.rightVolume = rightVolume;
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
			player.setAudioStreamType(audioStreamType);
			setListeners();

			player.setVolume(leftVolume, rightVolume);
			player.start();
        }
        catch(Exception e)
        {
        	Logger.e(e.toString());
        }
	}
	
	/**
	 * play a sound file from the apk
	 * 
	 * @param context
	 * @param type (ex: raw/sound.mp3 => "raw")
	 * @param filename without extension (ex: raw/sound.mp3 => "sound")
	 */
	synchronized public void play(Context context, String type, String filename)
	{
		Logger.v("trying to play: " + type + "/" + filename);

		stop();
		
		try
        {
			player = new MediaPlayer();
			player.setAudioStreamType(audioStreamType);
			setListeners();

			AssetFileDescriptor afd = ResourceHelper.getResourceAsFd(context, filename, type);
	        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
	        afd.close();

	        player.prepare();
	        player.setVolume(leftVolume, rightVolume);
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
			player.setAudioStreamType(audioStreamType);
			setListeners();

			player.setDataSource(filepath);

			player.prepare();
			player.setVolume(leftVolume, rightVolume);
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
			player.setAudioStreamType(audioStreamType);
			setListeners();

			player.setVolume(leftVolume, rightVolume);
			player.start();
        }
        catch(Exception e)
        {
        	Logger.e(e.toString());
        }
	}

	/**
	 * play given asset file descriptor
	 * 
	 * @param afd caller of this function should close this descriptor
	 */
	synchronized public void play(AssetFileDescriptor afd)
	{
		Logger.v("trying to play: " + afd.toString());
		
		stop();
		
		try
		{
			player = new MediaPlayer();
			player.setAudioStreamType(audioStreamType);
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			setListeners();

			player.prepare();
			player.setVolume(leftVolume, rightVolume);
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

		isPaused = false;
	}

	/**
	 * 
	 */
	synchronized public void pause()
	{
		try
		{
			if(player != null)
			{
				if(player.isPlaying())
				{
					player.pause();
					isPaused = true;
					
					Logger.v("paused previously played sound");
				}
			}
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
		}
	}

	/**
	 * 
	 */
	synchronized public void resume()
	{
		try
		{
			if(player != null)
			{
				if(isPaused)
				{
					player.start();
					isPaused = false;
					
					Logger.v("resumed previously played sound");
				}
				else
				{
					Logger.v("cannot resume - not paused yet");
				}
			}
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
		}
	}

	/**
	 * 
	 * @return
	 */
	synchronized public boolean isPlaying()
	{
		if(player != null)
			return player.isPlaying();
		return false;
	}

	/**
	 * 
	 * @return
	 */
	synchronized public boolean isPaused()
	{
		return isPaused;
	}

	/**
	 * 
	 * @return in msec
	 */
	public int getCurrentPosition()
	{
		return player.getCurrentPosition();
	}

	/**
	 * 
	 * @param position in msec
	 */
	public void setCurrentPosition(int position)
	{
		player.seekTo(position);
	}

	/**
	 * 
	 * @return in msec
	 */
	public int getDuration()
	{
		return player.getDuration();
	}
}
