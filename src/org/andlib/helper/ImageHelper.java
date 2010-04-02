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

package org.andlib.helper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import outsourced.image.filter.base.FilterBase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

/**
 * 
 * @author meinside@gmail.com
 * @since 09.10.12.
 * 
 * last update 10.03.08.
 *
 */
final public class ImageHelper
{
	public static final int READ_BUFFER_SIZE = 8 * 1024;
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapFromUrl(String url)
	{
		try
		{
			URLConnection connection = new URL(url).openConnection();
			connection.connect();
			InputStream is = connection.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is, READ_BUFFER_SIZE);
			Bitmap bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			
			return bitmap;
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param path
	 * @param sampleSize 1 = 100%, 2 = 50%(1/2), 4 = 25%(1/4), ...
	 * @return
	 */
	public static Bitmap getBitmapFromLocalPath(String path, int sampleSize)
	{
		try
		{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = sampleSize;
			return BitmapFactory.decodeFile(path, options);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static Bitmap getBitmapFromBytes(byte[] bytes)
	{
		try
		{
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param bitmap
	 * @param quality 1 ~ 100
	 * @return
	 */
	public static byte[] compressBitmap(Bitmap bitmap, int quality)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
			
			return baos.toByteArray();
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param srcBitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap getResizedBitmap(Bitmap srcBitmap, int newWidth, int newHeight)
	{
		try
		{
			return Bitmap.createScaledBitmap(srcBitmap, newWidth, newHeight, true);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		
		return null;
	}
	
	/**
	 * captures given view and converts it to a bitmap
	 * @param view
	 * @return
	 */
	public static Bitmap captureViewToBitmap(View view)
	{
		Bitmap result = null;

		try
		{
			result = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
			view.draw(new Canvas(result));
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param original
	 * @param format
	 * @param quality
	 * @param outputLocation
	 * @return
	 */
	public static boolean saveBitmap(Bitmap original, Bitmap.CompressFormat format, int quality, String outputLocation)
	{
		if(original == null)
			return false;

		try
		{
			return original.compress(format, quality, new FileOutputStream(outputLocation));
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		
		return false;
	}

	/**
	 * 
	 * @param filepath
	 * @param widthLimit
	 * @param heightLimit
	 * @param totalSize
	 * @return
	 */
	public static Bitmap getResizedBitmap(String filepath, int widthLimit, int heightLimit, int totalSize)
	{
		int outWidth = 0;
		int outHeight = 0;
		int resize = 1;
		InputStream input = null;

		try
		{
			input = new FileInputStream(new File(filepath));

			BitmapFactory.Options getSizeOpt = new BitmapFactory.Options();
			getSizeOpt.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, getSizeOpt);
			outWidth = getSizeOpt.outWidth;
			outHeight = getSizeOpt.outHeight;

			while((outWidth / resize) > widthLimit || (outHeight / resize) > heightLimit)
			{
				resize *= 2;
			}
			resize = resize * (totalSize + 15) / 15;

			BitmapFactory.Options resizeOpt = new BitmapFactory.Options();
			resizeOpt.inSampleSize = resize;

			input.close();
			input = null;

			input = new FileInputStream(new File(filepath));
			Bitmap bitmapImage = BitmapFactory.decodeStream(input, null, resizeOpt);
			return bitmapImage;
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		finally
		{
			if(input != null)
			{
				try
				{
					input.close();
				}
				catch(IOException e)
				{
					Log.e(LogHelper.where(), e.getMessage(), e);
				}
			}
		}
		return null;
	}
	
	/**
	 * apply filter to a bitmap
	 * 
	 * @param original
	 * @param filter
	 * @return filtered bitmap
	 */
	public static Bitmap applyFilter(Bitmap original, FilterBase filter)
	{
		return filter.filter(original);
	}
	
	/**
	 * generate a blurred bitmap from given one
	 * 
	 * referenced: http://incubator.quasimondo.com/processing/superfastblur.pde
	 * 
	 * @param original
	 * @param radius
	 * @return
	 */
	public Bitmap getBlurredBitmap(Bitmap original, int radius)
	{
		if (radius < 1)
			return null;

		int width = original.getWidth();
		int height = original.getHeight();
		int wm = width - 1;
		int hm = height - 1;
		int wh = width * height;
		int div = radius + radius + 1;
		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, p1, p2, yp, yi, yw;
		int vmin[] = new int[Math.max(width, height)];
		int vmax[] = new int[Math.max(width,height)];
		int dv[] = new int[256 * div];
		for (i=0; i<256*div; i++)
			dv[i] = i / div;

		int[] blurredBitmap = new int[wh];
		original.getPixels(blurredBitmap, 0, width, 0, 0, width, height);

		yw = 0;
		yi = 0;

		for (y=0; y<height; y++)
		{
			rsum = 0;
			gsum = 0;
			bsum = 0;
			for(i=-radius; i<=radius; i++)
			{
				p = blurredBitmap[yi + Math.min(wm, Math.max(i,0))];
				rsum += (p & 0xff0000) >> 16;
				gsum += (p & 0x00ff00) >> 8;
				bsum += p & 0x0000ff;
			}
			for (x=0; x<width; x++)
			{
				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				if(y==0)
				{
					vmin[x] = Math.min(x + radius+1,wm);
					vmax[x] = Math.max(x - radius,0);
				} 
				p1 = blurredBitmap[yw + vmin[x]];
				p2 = blurredBitmap[yw + vmax[x]];

				rsum += ((p1 & 0xff0000)-(p2 & 0xff0000))>>16;
				gsum += ((p1 & 0x00ff00)-(p2 & 0x00ff00))>>8;
				bsum += (p1 & 0x0000ff)-(p2 & 0x0000ff);
				yi ++;
			}
			yw += width;
		}

		for (x=0; x<width; x++)
		{
			rsum=gsum=bsum=0;
			yp =- radius * width;
			for(i=-radius; i<=radius; i++)
			{
				yi = Math.max(0,yp) + x;
				rsum += r[yi];
				gsum += g[yi];
				bsum += b[yi];
				yp += width;
			}
			yi = x;
			for (y=0; y<height; y++)
			{
				blurredBitmap[yi] = 0xff000000 | (dv[rsum]<<16) | (dv[gsum]<<8) | dv[bsum];
				if(x == 0)
				{
					vmin[y] = Math.min(y + radius + 1, hm) * width;
					vmax[y] = Math.max(y - radius, 0) * width;
				} 
				p1 = x + vmin[y];
				p2 = x + vmax[y];

				rsum += r[p1] - r[p2];
				gsum += g[p1] - g[p2];
				bsum += b[p1] - b[p2];

				yi += width;
			}
		}

		return Bitmap.createBitmap(blurredBitmap, width, height, Bitmap.Config.RGB_565);
	}
}
