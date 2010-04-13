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
public class Filter3x3 extends FilterBase
{
	private String name;

	private int[][] multiplier;	//[3][3]
	private int divider;
	private int bias;
	
	/**
	 * 
	 * @param name
	 * @param multiplier
	 * @param divider
	 * @param bias
	 */
	public Filter3x3(String name, int[][] multiplier, int divider, int bias)
	{
		this.name = name;
		this.multiplier = multiplier;
		this.divider = divider;
		this.bias = bias;
	}

	@Override
	public Bitmap filter(Bitmap bitmap)
	{
		LogHelper.v(name + ", multiplier: " + multiplier + ", divider: " + divider + ", bias: " + bias);

		if(bitmap == null || divider == 0)
		{
			LogHelper.e("parameter error (bitmap null, or divider is zero)");
			return null;
		}
		int bytesPerPixel = getBitmapPixelDepth(bitmap) / 8;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int[] bitmapBytes = new int[width * height];
		bitmap.getPixels(bitmapBytes, 0, width, 0, 0, width, height);

		/*
		 * NW  N  NE
		 *  W  C  E
		 * SW  S  SE
		 */
		int pNW, pN, pNE, pW, pC, pE, pSW, pS, pSE;
		int mNW = multiplier[0][0], 
			mN = multiplier[0][1], 
			mNE = multiplier[0][2], 
			mW = multiplier[1][0], 
			mC = multiplier[1][1], 
			mE = multiplier[1][2], 
			mSW = multiplier[2][0], 
			mS = multiplier[2][1], 
			mSE = multiplier[2][2];
		
		//speed up
		if(mNW == 0 && mN == 0 && mNE == 0 && 
			mW == 0            && mE == 0 &&
			mSW == 0 && mS == 0 && mSE == 0)
		{
			return new Filter1x1("1x1 Speed Up", mC, divider, bias).filter(bitmap);
		}

		int destPos = 0;
		int x, y;
		int a, r, g, b;

		int currentLinePos = 0;
		int nextLinePos = width;
		int prevLinePos = 0;
		for(y=1; y<height-1; y++)
		{
			prevLinePos = currentLinePos;
			currentLinePos = nextLinePos;
			nextLinePos = width * (y + 1);

			pNW = prevLinePos;
			pN = prevLinePos + 1;
			pNE = prevLinePos + 2;
			pW = currentLinePos;
			pC = currentLinePos + 1;
			pE = currentLinePos + 2;
			pSW = nextLinePos;
			pS = nextLinePos + 1;
			pSE = nextLinePos + 2;   

			destPos = 1 + y * width;
			for(x=1; x<width-1; x++)
			{
				//Red
				r = forcepin(0,
						(mNW * r(bitmapBytes[pNW]) + 
						 mN * r(bitmapBytes[pN]) + 
						 mNE * r(bitmapBytes[pNE]) + 
						 mW * r(bitmapBytes[pW]) + 
						 mC * r(bitmapBytes[pC]) + 
						 mE * r(bitmapBytes[pE]) + 
						 mSW * r(bitmapBytes[pSW]) + 
						 mS * r(bitmapBytes[pS]) + 
						 mSE * r(bitmapBytes[pSE])) / divider + bias, 
						 255);

				//Green
				g = forcepin(0, 
						(mNW * g(bitmapBytes[pNW]) + 
						 mN * g(bitmapBytes[pN]) + 
						 mNE * g(bitmapBytes[pNE]) +
						 mW * g(bitmapBytes[pW]) + 
						 mC * g(bitmapBytes[pC]) + 
						 mE * g(bitmapBytes[pE]) +
						 mSW * g(bitmapBytes[pSW]) + 
						 mS * g(bitmapBytes[pS]) + 
						 mSE * g(bitmapBytes[pSE])) / divider + bias,
						 255);

				//Blue
				b = forcepin(0, 
						(mNW * b(bitmapBytes[pNW]) + 
						 mN * b(bitmapBytes[pN]) + 
						 mNE * b(bitmapBytes[pNE]) +
						 mW * b(bitmapBytes[pW]) + 
						 mC * b(bitmapBytes[pC]) + 
						 mE * b(bitmapBytes[pE]) +
						 mSW * b(bitmapBytes[pSW]) + 
						 mS * b(bitmapBytes[pS]) + 
						 mSE * b(bitmapBytes[pSE])) / divider + bias, 
						 255);

				if(bytesPerPixel == 3)
				{
					bitmapBytes[destPos++] = Color.rgb(r, g, b);
				}
				else
				{
					//Alpha
					a = forcepin(0,
							(mNW * a(bitmapBytes[pNW]) + 
							 mN * a(bitmapBytes[pN]) + 
							 mNE * a(bitmapBytes[pNE]) +
							 mW * a(bitmapBytes[pW]) + 
							 mC * a(bitmapBytes[pC]) + 
							 mE * a(bitmapBytes[pE]) +
							 mSW * a(bitmapBytes[pSW]) + 
							 mS * a(bitmapBytes[pS]) + 
							 mSE * a(bitmapBytes[pSE])) / divider + bias, 
							 255);
					
					bitmapBytes[destPos++] = Color.argb(a, r, g, b);
				}

				pNW ++; pN ++; pNE ++;
				pW ++; pC ++; pE ++;
				pSW ++; pS ++; pSE ++;
			}
		}

		return Bitmap.createBitmap(bitmapBytes, width, height, bitmap.getConfig());
	}
}
