package org.andlib.helpers;

import android.hardware.Camera;
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
 * last update 10.11.05.
 *
 */
public abstract class MediaRecorderBase implements OnErrorListener, OnInfoListener
{
	public static final int DEFAULT_SAMPLING_RATE = 44100;	//44.1khz
	public static final int DEFAULT_ENCODING_BITRATE = 128 * 1024;	//128kbps
	public static final int DEFAULT_NUM_CHANNELS = 2;	//stereo
	public static final int DEFAULT_FRAME_RATE = 24;	//24 fps
	public static final int DEFAULT_MAX_DURATION = 0;	//infinite
	public static final int DEFAULT_MAX_FILESIZE = 0;	//infinite

	protected Camera camera = null;
	protected MediaRecorder recorder = null;
	
	protected String filepath = null;

	protected boolean isRecording = false;
	protected int recorderState = -1;

	protected Handler handler = null;
	protected MediaRecorderListener listener = null;

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
	protected void initRecorder()
	{
		if(recorder != null)
		{
			recorder.release();
			recorder = null;

			Logger.v("recorder released");
		}

		isRecording = false;

		recorder = new MediaRecorder();
		if(camera != null)
			recorder.setCamera(camera);
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
	 * 
	 * @param filepath
	 * @param camera null if none (should be unlocked before handing)
	 */
	synchronized public void startRecording(String filepath, Camera camera)
	{
		this.camera = camera;

		initRecorder();
		try
		{
			this.filepath = filepath;
			recorder.setOutputFile(filepath);

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
			if(camera != null)
			{
				try
				{
					camera.reconnect();
				}
				catch(Exception ce)
				{
					Logger.e(ce.toString());
				}
			}

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

		if(camera != null)
		{
			try
			{
				camera.reconnect();
			}
			catch(Exception e)
			{
				Logger.e(e.toString());
			}
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
