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

import org.andlib.helpers.MediaRecorderBase;


/**
 * helper class for recording sounds
 * 
 * @author meinside@gmail.com
 * @since 10.11.04.
 * 
 * last update 10.11.04.
 *
 */
public class SoundRecorder extends MediaRecorderBase
{
	private String filepath;
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
	 * @param filepath
	 * @param audioSource MediaRecorder.AudioSource.*
	 * @param outputFormat MediaRecorder.OutputFormat.*
	 * @param audioEncoder MediaRecorder.AudioEncoder.*
	 */
	public SoundRecorder(String filepath, int audioSource, int outputFormat, int audioEncoder)
	{
		this(filepath, audioSource, outputFormat, audioEncoder, DEFAULT_SAMPLING_RATE, DEFAULT_ENCODING_BITRATE, DEFAULT_NUM_CHANNELS, DEFAULT_MAX_DURATION, DEFAULT_MAX_FILESIZE);
	}

	/**
	 * default constructor for some more parameters
	 * 
	 * @param filepath
	 * @param audioSource MediaRecorder.AudioSource.*
	 * @param outputFormat MediaRecorder.OutputFormat.*
	 * @param audioEncoder MediaRecorder.AudioEncoder.*
	 * @param samplingRate
	 * @param encodingBitRate
	 * @param numChannels 1 for mono, 2 for stereo
	 * @param maxDurationMillis 0 for no duration limit
	 * @param maxFileSize 0 for infinite file size
	 */
	public SoundRecorder(String filepath, int audioSource, int outputFormat, int audioEncoder, int samplingRate, int encodingBitRate, int numChannels, int maxDurationMillis, long maxFileSize)
	{
		super();

		this.filepath = filepath;
		this.audioSource = audioSource;
		this.outputFormat = outputFormat;
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
		recorder.setOutputFile(filepath);
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
