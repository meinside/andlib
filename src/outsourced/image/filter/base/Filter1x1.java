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

package outsourced.image.filter.base;

import kr.pe.meinside.android.helper.LogHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * 
 * ported from: http://www.gdargaud.net/Hack/SourceCode.html#GraphicFilter
 * 
 * @author meinside@gmail.com
 * @since 10.03.05.
 * 
 * last update 10.03.08.
 */
public class Filter1x1 extends FilterBase
{
	private String name;

	private int multiplier;
	private int divider;
	private int bias;
	
	/**
	 * 
	 * @param name
	 * @param multiplier
	 * @param divider
	 * @param bias
	 */
	public Filter1x1(String name, int multiplier, int divider, int bias)
	{
		this.name = name;
		this.multiplier = multiplier;
		this.divider = divider;
		this.bias = bias;
	}

	@Override
	public Bitmap filter(Bitmap bitmap)
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		Log.v(LogHelper.where(), name + ", multiplier: " + multiplier + ", divider: " + divider + ", bias: " + bias);
		
		if(bitmap == null || divider == 0 || (multiplier == 1 && divider == 1 && bias == 0))
		{
			Log.e(LogHelper.where(), "parameter error (bitmap null, divider is zero, or given filter has no effect)");
			return null;
		}

		int[] bitmapBytes = new int[width * height];
		bitmap.getPixels(bitmapBytes, 0, width, 0, 0, width, height);

		int color;
		for(int i=0; i<bitmapBytes.length; i++)
		{
			color = bitmapBytes[i];
			
			bitmapBytes[i] = Color.rgb(
					forcepin(0, r(color) * multiplier / divider + bias, 255), 
					forcepin(0, g(color) * multiplier / divider + bias, 255), 
					forcepin(0, b(color) * multiplier / divider + bias, 255));
		}

		return Bitmap.createBitmap(bitmapBytes, width, height, bitmap.getConfig());
	}
}
