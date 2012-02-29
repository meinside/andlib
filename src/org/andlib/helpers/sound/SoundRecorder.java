package org.andlib.helpers.sound;

import org.andlib.helpers.MediaRecorderBase;


/**
 * helper class for recording sounds
 * <br>
 * <br>
 * (needs "android.permission.RECORD_AUDIO" permission in the manifest file)
 * 
 * @author meinside@gmail.com
 * @since 10.11.04.
 * 
 * last update 10.11.04.
 *
 */
public class SoundRecorder extends MediaRecorderBase
{
	private int audioSource;
	private int outputFormat;
	private int audioEncoder;
	private int samplingRate;
	private int encodingBitRate;
	private int numChannels;
	private int maxDurationMillis;
	private long maxFileSize;

	/**
	 * default constructor
	 * 
	 * @param outputFormat MediaRecorder.OutputFormat.*
	 * @param audioSource MediaRecorder.AudioSource.*
	 * @param audioEncoder MediaRecorder.AudioEncoder.*
	 */
	public SoundRecorder(int outputFormat, int audioSource, int audioEncoder)
	{
		this(outputFormat, audioSource, audioEncoder, DEFAULT_SAMPLING_RATE, DEFAULT_ENCODING_BITRATE, DEFAULT_NUM_CHANNELS, DEFAULT_MAX_DURATION, DEFAULT_MAX_FILESIZE);
	}

	/**
	 * default constructor for some more parameters
	 * 
	 * @param outputFormat MediaRecorder.OutputFormat.*
	 * @param audioSource MediaRecorder.AudioSource.*
	 * @param audioEncoder MediaRecorder.AudioEncoder.*
	 * @param samplingRate
	 * @param encodingBitRate
	 * @param numChannels 1 for mono, 2 for stereo
	 * @param maxDurationMillis 0 for no duration limit
	 * @param maxFileSize 0 for infinite file size
	 */
	public SoundRecorder(int outputFormat, int audioSource, int audioEncoder, int samplingRate, int encodingBitRate, int numChannels, int maxDurationMillis, long maxFileSize)
	{
		super();

		this.outputFormat = outputFormat;
		this.audioSource = audioSource;
		this.audioEncoder = audioEncoder;
		this.samplingRate = samplingRate;
		this.encodingBitRate = encodingBitRate;
		this.numChannels = numChannels;
		this.maxDurationMillis = maxDurationMillis;
		this.maxFileSize = maxFileSize;
	}

	@Override
	protected void initRecorderMore()
	{
		recorder.setAudioSource(audioSource);

		recorder.setOutputFormat(outputFormat);

		recorder.setAudioEncoder(audioEncoder);
		recorder.setAudioSamplingRate(samplingRate);
		recorder.setAudioEncodingBitRate(encodingBitRate);
		recorder.setAudioChannels(numChannels);
		recorder.setMaxDuration(maxDurationMillis);
		recorder.setMaxFileSize(maxFileSize);
	}
}
