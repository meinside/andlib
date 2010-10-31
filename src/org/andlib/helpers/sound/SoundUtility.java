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

import org.andlib.helpers.Logger;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

/**
 * various utility functions for audio play
 * 
 * @author meinside@gmail.com
 * @since 10.10.29.
 * 
 * last update 10.10.31.
 *
 */
public class SoundUtility
{
	/**
	 * 
	 * @param context
	 * @param soundFile sound file object (can be one of: Integer(resource id), String(file path), or File)
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
