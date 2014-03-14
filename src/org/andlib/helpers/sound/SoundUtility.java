package org.andlib.helpers.sound;

import java.io.File;

import org.andlib.helpers.Logger;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

/**
 * various utility functions for audio play
 * 
 * @author meinside@gmail.com
 * @since 2010.10.29.
 * 
 * last update 2014.03.14.
 *
 */
public class SoundUtility
{
	/**
	 * 
	 * @param context
	 * @param soundFile sound file object (can be one of: Integer(resource id), String(file path), File, or AssetFileDescriptor)
	 * @return duration of given sound file in millis (0 if failed)
	 */
	public static long getDurationOfSound(Context context, Object soundFile)
	{
		int millis = 0;
		MediaPlayer mp = new MediaPlayer();
		try
		{
			Class<? extends Object> currentArgClass = soundFile.getClass();
			if(currentArgClass == Integer.class)
			{
				AssetFileDescriptor afd = context.getResources().openRawResourceFd((Integer)soundFile);
		        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				afd.close();
			}
			else if(currentArgClass == String.class)
			{
				mp.setDataSource((String)soundFile);
			}
			else if(currentArgClass == File.class)
			{
				mp.setDataSource(((File)soundFile).getAbsolutePath());
			}
			else if(currentArgClass == AssetFileDescriptor.class)
			{
				AssetFileDescriptor afd = (AssetFileDescriptor)soundFile;
				mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			}
			mp.prepare();
			millis = mp.getDuration();
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
		}
		finally
		{
			mp.release();
			mp = null;
		}
		return millis;
	}
}
