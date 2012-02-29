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
 * last update 11.04.20.
 *
 */
public class SimplePaintView extends View
{
	public static final int DEFAULT_BGCOLOR = 0xFFFFFFFF;	//default background color: white
	public static final int DEFAULT_FGCOLOR = 0xFF000000;	//default foreground color: black
	public static final int DEFAULT_STROKEWIDTH = 2;
	public static final float TOUCH_THRESHOLD = 2.0f;

	protected Paint drawPaint;
	protected Canvas backCanvas;

	protected int currentBgColor = DEFAULT_BGCOLOR;
	protected int currentFgColor = DEFAULT_FGCOLOR;

	private Bitmap backBitmap;
	private Path drawPath;
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

		drawPaint = new Paint();
		drawPaint.setAntiAlias(true);
		drawPaint.setDither(true);
		drawPaint.setColor(currentFgColor);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		drawPaint.setStrokeWidth(DEFAULT_STROKEWIDTH);

		backBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		backCanvas = new Canvas(backBitmap);
		drawPath = new Path();
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
		canvas.drawBitmap(backBitmap, 0, 0, bitmapPaint);
		drawPaint.setColor(currentFgColor);
		canvas.drawPath(drawPath, drawPaint);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	private void touchStart(float x, float y)
	{
		drawPath.reset();
		drawPath.moveTo(x, y);
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
			drawPath.quadTo(currentX, currentY, (x + currentX) / 2, (y + currentY) / 2);
			currentX = x;
			currentY = y;
		}
	}

	/**
	 * 
	 */
	private void touchUp()
	{
		drawPath.lineTo(currentX, currentY);

		//commit the drawPath to our offscreen
		drawPaint.setColor(currentFgColor);
		backCanvas.drawPath(drawPath, drawPaint);

		//kill this so we don't double draw
		drawPath.reset();
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
	 * @return reference to the backBitmap
	 */
	public Bitmap getBitmap()
	{
		return backBitmap;
	}
	
	/**
	 * 
	 * @return immutable copy of the backBitmap
	 */
	public Bitmap copyBitmap()
	{
		return backBitmap.copy(Bitmap.Config.ARGB_8888, false);
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
			return backBitmap.compress(format, quality, new FileOutputStream(outputLocation));
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
		}
		
		return false;
	}

	/**
	 * clears backCanvas
	 */
	public void clear()
	{
        backCanvas.drawColor(currentBgColor);
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
		drawPaint.setStrokeWidth(width);
	}

	/**
	 * ex: setMaskFilter(new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f)); //emboss filter
	 *   : setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL)); //blur filter
	 * 
	 * @param filter
	 */
	public void setMaskFilter(MaskFilter filter)
	{
		drawPaint.setMaskFilter(filter);
	}
}
