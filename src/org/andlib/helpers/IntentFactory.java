package org.andlib.helpers;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * factory class for generating various intents
 * <br>
 * <br>
 * last update: 2011.01.24.
 * 
 * @author meinside@gmail.com
 * @since 2010.11.03.
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
	 * <br>
	 * <br>
	 * (when type == <b>MediaType.VIDEO</b>, picker doesn't work.
	 * <br>
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
	 * <br>
	 * <br>
	 * (when type == <b>MediaType.AUDIO</b>, picker doesn't work.
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

	/**
	 * generates an intent for taking photos/videos that saves result to given uri
	 * 
	 * @param type either one of <b>MediaType.IMAGE</b> or <b>MediaType.VIDEO</b>
	 * @param outputUri
	 * @return null if error
	 */
	public static Intent getCameraIntent(MediaType type, Uri outputUri)
	{
		Intent intent = null;
		if(type == MediaType.IMAGE)
		{
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
		}
		else if(type == MediaType.VIDEO)
		{
			intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
		}
		return intent;
	}
}
