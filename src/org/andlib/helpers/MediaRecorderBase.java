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

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Handler;

/**
 * base class for recording various media
 * 
 * @author meinside@gmail.com
 * @since 10.11.04.
 * 
 * last update 10.11.04.
 *
 */
public abstract class MediaRecorderBase implements OnErrorListener, OnInfoListener
{
	public static final int DEFAULT_SAMPLING_RATE = 44100;	//44.1khz
	public static final int DEFAULT_ENCODING_BITRATE = 128 * 1024;	//128kbps
	public static final int DEFAULT_NUM_CHANNELS = 2;	//stereo
	public static final int DEFAULT_MAX_DURATION = 0;	//infinite
	public static final int DEFAULT_MAX_FILESIZE = 0;	//infinite

	protected MediaRecorder recorder = null;

	private boolean isRecording = false;
	private int recorderState = -1;

	private Handler handler = null;
	private MediaRecorderListener listener = null;

	/**
	 * default constructor that does nothing
	 * 
	 */
	public MediaRecorderBase()
	{
		//do nothing
	}

	/**
	 * initialize recorder object
	 */
	private void initRecorder()
	{
		if(recorder != null)
		{
			recorder.release();
			recorder = null;

			Logger.v("recorder released");
		}

		isRecording = false;

		recorder = new MediaRecorder();
		recorder.setOnErrorListener(this);
		recorder.setOnInfoListener(this);
		
		initRecorderMore();
	}

	/**
	 * <p>
	 * initialize more (this is the point for setting more parameters)
	 * <br>
	 * <br>
	 * <b>caution</b>: it is called after MediaRecorder object's creation, and called before MediaRecorder.prepare().
	 * </p>
	 */
	protected abstract void initRecorderMore();

	/**
	 * 
	 * @return
	 */
	synchronized public boolean isRecording()
	{
		return isRecording;
	}

	/**
	 * start recording
	 */
	synchronized public void startRecording()
	{
		initRecorder();
		try
		{
			recorder.prepare();
			recorder.start();

			isRecording = true;
			
			Logger.v("recording started");
			
			recorderState = MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN;
			if(listener != null && handler != null)
			{
				handler.post(new Runnable(){
					@Override
					public void run()
					{
						listener.onStartRecording();
					}});
			}
		}
		catch(Exception e)
		{
			recorderState = MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN;
			if(listener != null && handler != null)
			{
				handler.post(new Runnable(){
					@Override
					public void run()
					{
						listener.onErrorRecording(recorderState);
					}});
			}

			Logger.e(e.toString());

			isRecording = false;
		}
	}

	/**
	 * stop recording
	 */
	synchronized public void stopRecording()
	{
		if(isRecording)
		{
			Logger.v("stop recording");

			recorder.stop();
			isRecording = false;
		}

		if(recorder != null)
		{
			recorder.release();
			recorder = null;
			
			Logger.v("recorder released");
		}

		recorderState = MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN;
		if(listener != null && handler != null)
		{
			handler.post(new Runnable(){
				@Override
				public void run()
				{
					listener.onStopRecording(recorderState);
				}});
		}
	}

	/**
	 * set listener for UI manipulation
	 * 
	 * @param handler
	 * @param listener
	 */
	public void setListener(Handler handler, MediaRecorderListener listener)
	{
		this.handler = handler;
		this.listener = listener;
	}

	/**
	 * listener class for MediaRecorderBase
	 * 
	 * @author meinside@gmail.com
	 *
	 */
	public interface MediaRecorderListener
	{
		/**
		 * called when recording started
		 */
		public void onStartRecording();

		/**
		 * called when recording stopped
		 * 
		 * @param what
		 * <p> 
		 * 	<b>MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN</b>: when finished with no error
		 * <br>
		 * 	<b>MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED</b>: when reached time limit
		 * <br>
		 * 	<b>MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED</b>: when reached file size limit
		 * </p>
		 */
		public void onStopRecording(int what);

		/**
		 * called on error
		 * 
		 * @param what
		 * <p>
		 * 	<b>MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN</b>: when finished with unknown error
		 * </p>
		 */
		public void onErrorRecording(int what);
	}

	/* (non-Javadoc)
	 * @see android.media.MediaRecorder.OnInfoListener#onInfo(android.media.MediaRecorder, int, int)
	 */
	@Override
	public void onInfo(MediaRecorder mr, int what, int extra)
	{
		recorderState = what;
		switch(what)
		{
		case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
			if(listener != null && handler != null)
			{
				handler.post(new Runnable(){
					@Override
					public void run()
					{
						listener.onErrorRecording(recorderState);
					}});
			}
			break;
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
			if(listener != null && handler != null)
			{
				handler.post(new Runnable(){
					@Override
					public void run()
					{
						listener.onStopRecording(recorderState);
					}});
			}
			break;
		}

		//release
		isRecording = false;
		mr.release();
		mr = null;
	}

	/* (non-Javadoc)
	 * @see android.media.MediaRecorder.OnErrorListener#onError(android.media.MediaRecorder, int, int)
	 */
	@Override
	public void onError(MediaRecorder mr, int what, int extra)
	{
		recorderState = what;
		switch(what)
		{
		case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
			if(listener != null && handler != null)
			{
				handler.post(new Runnable(){
					@Override
					public void run()
					{
						listener.onErrorRecording(recorderState);
					}});
			}
			break;
		}

		//release
		isRecording = false;
		mr.release();
		mr = null;
	}
}
