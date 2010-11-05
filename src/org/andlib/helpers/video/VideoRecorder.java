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
