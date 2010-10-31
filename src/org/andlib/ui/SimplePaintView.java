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

package org.andlib.ui;

import java.io.FileOutputStream;

import org.andlib.helpers.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * referenced: com.example.android.apis.graphics.FingerPaint.java
 * 
 * @author meinside@gmail.com
 * @since 10.01.26.
 * 
 * last update 10.04.13.
 *
 */
public class SimplePaintView extends View
{
	public static final int DEFAULT_BGCOLOR = 0xFFFFFFFF;	//default background color: white
	public static final int DEFAULT_FGCOLOR = 0xFF000000;	//default foreground color: black
	public static final int DEFAULT_STROKEWIDTH = 2;
	public static final float TOUCH_THRESHOLD = 2.0f;

	protected Paint paint;
	protected Canvas canvas;

	protected int currentBgColor = DEFAULT_BGCOLOR;
	protected int currentFgColor = DEFAULT_FGCOLOR;

	private Bitmap bitmap;
	private Path path;
	private Paint bitmapPaint;

	private float currentX, currentY;

	/**
	 * 
	 * @param context
	 */
	public SimplePaintView(Context context)
	{
		super(context);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public SimplePaintView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SimplePaintView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		//get width/height of the view
		int width = View.MeasureSpec.getSize(widthMeasureSpec);
		int height = View.MeasureSpec.getSize(heightMeasureSpec);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(currentFgColor);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(DEFAULT_STROKEWIDTH);

		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		path = new Path();
		bitmapPaint = new Paint(Paint.DITHER_FLAG);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(currentBgColor);
		canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
		paint.setColor(currentFgColor);
		canvas.drawPath(path, paint);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	private void touchStart(float x, float y)
	{
		path.reset();
		path.moveTo(x, y);
		currentX = x;
		currentY = y;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	private void touchMove(float x, float y)
	{
		float dx = Math.abs(x - currentX);
		float dy = Math.abs(y - currentY);
		if(dx >= TOUCH_THRESHOLD || dy >= TOUCH_THRESHOLD)
		{
			path.quadTo(currentX, currentY, (x + currentX) / 2, (y + currentY) / 2);
			currentX = x;
			currentY = y;
		}
	}

	/**
	 * 
	 */
	private void touchUp()
	{
		path.lineTo(currentX, currentY);

		//commit the path to our offscreen
		paint.setColor(currentFgColor);
		canvas.drawPath(path, paint);

		//kill this so we don't double draw
		path.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float x = event.getX();
		float y = event.getY();

		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			touchStart(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touchMove(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touchUp();
			invalidate();
			break;
		}
		return true;
	}

	/**
	 *  
	 * @return reference to the bitmap
	 */
	public Bitmap getBitmap()
	{
		return bitmap;
	}
	
	/**
	 * 
	 * @return immutable copy of the bitmap
	 */
	public Bitmap copyBitmap()
	{
		return bitmap.copy(Bitmap.Config.ARGB_8888, false);
	}
	
	/**
	 * 
	 * @param format
	 * @param quality
	 * @param outputLocation
	 * @return
	 */
	public boolean saveToBitmap(Bitmap.CompressFormat format, int quality, String outputLocation)
	{
		try
		{
			return bitmap.compress(format, quality, new FileOutputStream(outputLocation));
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
		}
		
		return false;
	}

	/**
	 * clears canvas
	 */
	public void clear()
	{
        canvas.drawColor(currentBgColor);
        invalidate();
	}

	/**
	 * ex: setFgColor(0xFF000000); //color: black
	 * 
	 * @param color
	 */
	public void setFgColor(int color)
	{
		currentFgColor = color;
	}
	
	/**
	 * ex: setBgColor(0xFFFFFFFF); //color: white
	 * 
	 * @param color
	 */
	public void setBgColor(int color)
	{
		currentBgColor = color;
	}
	
	/**
	 * 
	 * @param width
	 */
	public void setStrokeWidth(int width)
	{
		paint.setStrokeWidth(width);
	}

	/**
	 * ex: setMaskFilter(new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f)); //emboss filter
	 *   : setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL)); //blur filter
	 * 
	 * @param filter
	 */
	public void setMaskFilter(MaskFilter filter)
	{
		paint.setMaskFilter(filter);
	}
}
