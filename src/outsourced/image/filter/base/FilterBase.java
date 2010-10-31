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

import org.andlib.helpers.Logger;

import android.graphics.Bitmap;

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
public abstract class FilterBase
{
	abstract public Bitmap filter(Bitmap bitmap);
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	final public int forcepin(int a, int b, int c)
	{
		return (a > b) ? a : (b > c ? c : b); 
	}

	/**
	 * 
	 * @param color
	 * @return
	 */
	final public int a(int color)
	{
		return (color >> 24) & 0xFF;
	}

	/**
	 * 
	 * @param color
	 * @return
	 */
	final public int r(int color)
	{
		return (color >> 16) & 0xFF;
	}
	
	/**
	 * 
	 * @param color
	 * @return
	 */
	final public int g(int color)
	{
		return (color >> 8) & 0xFF;
	}
	
	/**
	 * 
	 * @param color
	 * @return
	 */
	final public int b(int color)
	{
		return color & 0xFF;
	}

	/**
	 * 
	 * @param bitmap
	 * @return return pixel depth of given bitmap (in bits)
	 */
	final public int getBitmapPixelDepth(Bitmap bitmap)
	{
		int pixDepth = -1;
		Bitmap.Config config = bitmap.getConfig();
		if(config.equals(Bitmap.Config.ALPHA_8))
			pixDepth = 8;
		else if(config.equals(Bitmap.Config.ARGB_4444))
			pixDepth = 16;
		else if(config.equals(Bitmap.Config.ARGB_8888))
			pixDepth = 32;
		else if(config.equals(Bitmap.Config.RGB_565))
			pixDepth = 16;
		else
			Logger.e("no matching Bitmap.Config");
		return pixDepth;
	}
}
