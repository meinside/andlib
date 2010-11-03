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

import android.content.Intent;
import android.net.Uri;

/**
 * factory class for generating various intents
 * 
 * @author meinside@gmail.com
 * @since 10.11.03.
 * 
 * last update 10.11.03.
 *
 */
public class IntentFactory
{
	public enum MediaType {
		IMAGE,
		AUDIO,
		VIDEO,
	};

	public enum MediaLocation {
		INTERNAL,
		EXTERNAL,
	};

	/**
	 * generates an intent for selecting media
	 * 
	 * (when type == VIDEO, picker doesn't work.
	 *  instead, it looks for a video player to play selected one.)
	 *  
	 * @param title
	 * @param type
	 * @param location
	 * @return
	 */
	public static Intent getMediaPickerIntent(String title, MediaType type, MediaLocation location)
	{
		Uri uri = null;
		if(type == MediaType.IMAGE)
		{
			if(location == MediaLocation.INTERNAL)
				uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
			else
				uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		}
		else if(type == MediaType.AUDIO)
		{
			if(location == MediaLocation.INTERNAL)
				uri = android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
			else
				uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		}
		else if(type == MediaType.VIDEO)
		{
			if(location == MediaLocation.INTERNAL)
				uri = android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI;
			else
				uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		}
		else
			return null;

		return Intent.createChooser(new Intent(Intent.ACTION_PICK, uri), title);
	}

	/**
	 * generates an intent for selecting media
	 * 
	 * (when type == AUDIO, picker doesn't work.
	 *  instead, it looks for an audio player to play selected one.)
	 * 
	 * @param title
	 * @param type
	 * @return
	 */
	public static Intent getMediaContentIntent(String title, MediaType type)
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		if(type == MediaType.IMAGE)
			intent.setType("image/*");
		else if(type == MediaType.AUDIO)
			intent.setType("audio/*");
		else if(type == MediaType.VIDEO)
			intent.setType("video/*");
		
		return Intent.createChooser(intent, title);
	}
}
