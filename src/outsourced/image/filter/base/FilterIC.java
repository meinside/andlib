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

import org.andlib.helper.LogHelper;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * 
 * ported from: http://www.gdargaud.net/Hack/SourceCode.html#GraphicFilter
 * 
 * XXX: needs optimization
 * 
 * @author meinside@gmail.com
 * @since 10.03.05.
 * 
 * last update 10.04.13.
 */
public class FilterIC extends FilterBase
{
	private String name;

	private int[][] multiplier;	//[4][4]
	private int[] divider;	//[4]
	private int[] bias;	//[4]
	
	/**
	 * 
	 * @param name
	 * @param multiplier
	 * @param divider
	 * @param bias
	 */
	public FilterIC(String name, int[][] multiplier, int[] divider, int[] bias)
	{
		this.name = name;
		this.multiplier = multiplier;
		this.divider = divider;
		this.bias = bias;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the multiplier
	 */
	public int[][] getMultiplier()
	{
		return multiplier;
	}

	/**
	 * @return the divider
	 */
	public int[] getDivider()
	{
		return divider;
	}

	/**
	 * @return the bias
	 */
	public int[] getBias()
	{
		return bias;
	}

	@Override
	public Bitmap filter(Bitmap bitmap)
	{
		LogHelper.v(name + ", multiplier: " + multiplier + ", divider: " + divider + ", bias: " + bias);

		int bytesPerPixel = getBitmapPixelDepth(bitmap) / 8;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(bitmap == null || bytesPerPixel < 3)
		{
			LogHelper.e("parameter error (bitmap null or bit depth too small)");
			return null;
		}

		for(int div: divider)
		{
			if(div == 0)
			{
				LogHelper.e("parameter error (dividers should not be zero)");
				return null;
			}
		}

		int[] bitmapBytes = new int[width * height];
		bitmap.getPixels(bitmapBytes, 0, width, 0, 0, width, height);

		//speed up
		if(multiplier[1][0] == 0 && multiplier[2][0] == 0 &&  multiplier[3][0] == 0 &&
			multiplier[0][1] == 0 && multiplier[2][1] == 0 && multiplier[3][1] == 0 && 
			multiplier[0][2] == 0 && multiplier[1][2] == 0 && multiplier[3][2]==0 && 
			multiplier[0][3] == 0 && multiplier[1][3] == 0 && multiplier[2][3] == 0 && 
			multiplier[0][0] * divider[1] == multiplier[1][1] * divider[0] &&
			multiplier[1][1] * divider[2] == multiplier[2][2] * divider[1] &&
			multiplier[2][2] * divider[3] == multiplier[3][3] * divider[2] &&
			multiplier[3][3] * divider[0] == multiplier[0][0] * divider[3] &&
			multiplier[0][0] == bias[1] &&
			multiplier[1][0] == bias[2] &&
			multiplier[2][0] == bias[3] &&
			multiplier[3][0] == bias[0])
		{
			return new Filter1x1("IC Speed Up", multiplier[0][0], divider[0], bias[0]).filter(bitmap);
		}
		
		int r, g, b, a;
		int p, i;

		if(bytesPerPixel == 4)
		{
			for(i=0; i<bitmapBytes.length; i++)
			{
				p = bitmapBytes[i];
				r = r(p);
				g = g(p);
				b = b(p);
				a = a(p);

				bitmapBytes[i] = Color.argb(
						forcepin(0, (r * multiplier[3][0] + g * multiplier[3][1] + b * multiplier[3][2] + a * multiplier[3][3]) / divider[3] + bias[3], 255), 
						forcepin(0, (r * multiplier[0][0] + g * multiplier[0][1] + b * multiplier[0][2] + a * multiplier[0][3]) / divider[0] + bias[0], 255), 
						forcepin(0, (r * multiplier[1][0] + g * multiplier[1][1] + b * multiplier[1][2] + a * multiplier[1][3]) / divider[1] + bias[1], 255), 
						forcepin(0, (r * multiplier[2][0] + g * multiplier[2][1] + b * multiplier[2][2] + a * multiplier[2][3]) / divider[2] + bias[2], 255));
			}
		}
		else 
		{
			for(i=0; i<bitmapBytes.length; i++)
			{
				p = bitmapBytes[i];
				r = r(p);
				g = g(p);
				b = b(p);

				bitmapBytes[i] = Color.rgb( 
						forcepin(0, (r * multiplier[0][0] + g * multiplier[0][1] + b * multiplier[0][2]) / divider[0] + bias[0], 255), 
						forcepin(0, (r * multiplier[1][0] + g * multiplier[1][1] + b * multiplier[1][2]) / divider[1] + bias[1], 255), 
						forcepin(0, (r * multiplier[2][0] + g * multiplier[2][1] + b * multiplier[2][2]) / divider[2] + bias[2], 255));
				
			}
		}

		return Bitmap.createBitmap(bitmapBytes, width, height, bitmap.getConfig());
	}
}
