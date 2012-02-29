package org.andlib.helpers.video;

import org.andlib.helpers.MediaRecorderBase;

import android.view.Surface;


/**
 * helper class for recording video
 * <br>
 * <br>
 * (needs "android.permission.CAMERA" and "android.permission.RECORD_AUDIO" permissions in the manifest file)
 * 
 * @author meinside@gmail.com
 * @since 10.11.04.
 * 
 * last update 10.11.05.
 *
 */
public class VideoRecorder extends MediaRecorderBase
{
	private int audioSource;
	private int outputFormat;
	private int audioEncoder;
	private int samplingRate;
	private int encodingBitRate;
	private int numChannels;
	private int videoSource;
	private int videoEncoder;
	private int videoFrameRate;
	private int videoWidth;
	private int videoHeight;
	private Surface previewDisplay;
	private int maxDurationMillis;
	private long maxFileSize;

	/**
	 * 
	 * @param outputFormat MediaRecorder.OutputFormat.*
	 * @param audioSource MediaRecorder.AudioSource.*
	 * @param audioEncoder MediaRecorder.AudioEncoder.*
	 * @param videoSource MediaRecorder.VideoSource.*
	 * @param videoEncoder MediaRecorder.AudioEncoder.*
	 * @param previewDisplay
	 */
	public VideoRecorder(int outputFormat, int audioSource, int audioEncoder, int videoSource, int videoEncoder, int videoWidth, int videoHeight, Surface previewDisplay)
	{
		this(outputFormat, audioSource, audioEncoder, DEFAULT_SAMPLING_RATE, DEFAULT_ENCODING_BITRATE, DEFAULT_NUM_CHANNELS, videoSource, videoEncoder, DEFAULT_FRAME_RATE, previewDisplay, DEFAULT_MAX_DURATION, DEFAULT_MAX_FILESIZE);
	}

	/**
	 * 
	 * @param outputFormat MediaRecorder.OutputFormat.*
	 * @param audioSource MediaRecorder.AudioSource.*
	 * @param audioEncoder MediaRecorder.AudioEncoder.*
	 * @param audioSamplingRate
	 * @param audioEncodingBitRate
	 * @param audioNumChannels 1 for mono, 2 for stereo
	 * @param videoSource MediaRecorder.VideoSource.*
	 * @param videoEncoder MediaRecorder.AudioEncoder.*
	 * @param videoFrameRate
	 * @param previewDisplay
	 * @param maxDurationMillis 0 for no duration limit
	 * @param maxFileSize 0 for infinite file size
	 */
	public VideoRecorder(int outputFormat, int audioSource, int audioEncoder, int audioSamplingRate, int audioEncodingBitRate, int audioNumChannels, int videoSource, int videoEncoder, int videoFrameRate, Surface previewDisplay, int maxDurationMillis, long maxFileSize)
	{
		super();

		this.outputFormat = outputFormat;
		this.audioSource = audioSource;
		this.audioEncoder = audioEncoder;
		this.samplingRate = audioSamplingRate;
		this.encodingBitRate = audioEncodingBitRate;
		this.numChannels = audioNumChannels;
		this.videoEncoder = videoEncoder;
		this.videoSource = videoSource;
		this.videoFrameRate = videoFrameRate;
		this.previewDisplay = previewDisplay;
		this.maxDurationMillis = maxDurationMillis;
		this.maxFileSize = maxFileSize;
	}

	@Override
	protected void initRecorderMore()
	{
		recorder.setAudioSource(audioSource);
		recorder.setVideoSource(videoSource);

		recorder.setOutputFormat(outputFormat);

		recorder.setAudioEncoder(audioEncoder);
		recorder.setAudioSamplingRate(samplingRate);
		recorder.setAudioEncodingBitRate(encodingBitRate);
		recorder.setAudioChannels(numChannels);
		recorder.setVideoEncoder(videoEncoder);
		recorder.setVideoFrameRate(videoFrameRate);
		recorder.setPreviewDisplay(previewDisplay);
		recorder.setMaxDuration(maxDurationMillis);
		recorder.setMaxFileSize(maxFileSize);
	}

	/**
	 * set video size
	 * 
	 * @param width
	 * @param height
	 */
	public void setVideoSize(int width, int height)
	{
		videoWidth = width;
		videoHeight = height;
		
		if(recorder != null)
			recorder.setVideoSize(videoWidth, videoHeight);
	}
}
