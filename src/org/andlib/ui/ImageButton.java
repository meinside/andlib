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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * simple image button class that needs only one image file for its normal/pressed/disabled states
 * <br>
 * <br>
 * ex)
 * <pre>
 *	&lt;org.andlib.ui.ImageButton
 *		android:id="@+id/some_button_id"
 *		android:layout_width="wrap_content"
 *		android:layout_height="wrap_content"
 *		android:background="@drawable/some_image_file"
 *		android:text="TEST"
 *		android:textStyle="italic"
 *		android:layout_margin="10dip"
 *		/&gt;
 * </pre>
 * 
 * @author meinside@gmail.com
 * @since 10.11.22.
 * 
 * last update 11.02.08.
 *
 */
public class ImageButton extends Button
{
	private boolean isDown = false;	
	private BitmapDrawable original = null;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ImageButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ImageButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize();
	}

	/**
	 * @param context
	 */
	public ImageButton(Context context)
	{
		super(context);
		initialize();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		if(!enabled)
			changeToDisabledState();
		else
			changeToNormalState();

		super.setEnabled(enabled);
	}

	@Override
	public void setBackgroundDrawable(Drawable d)
	{
		//copy original drawable
		original = new BitmapDrawable(getResources(), ((BitmapDrawable)d).getBitmap());

		if(isEnabled())
			changeToNormalState();
		else
			changeToDisabledState();
	}

	@Override
	public void setBackgroundResource(int resid)
	{
		//copy original drawable
		Resources res = getResources();
		original = new BitmapDrawable(res, ((BitmapDrawable)res.getDrawable(resid)).getBitmap());

		if(isEnabled())
			changeToNormalState();
		else
			changeToDisabledState();
	}

	/**
	 * 
	 * @return
	 */
	private int getNormalColor()
	{
		return 0xFFFFFFFF;
	}
	
	/**
	 * override this function to change pressed button's color filter
	 * 
	 * @return
	 */
	protected int getPressedColor()
	{
		return 0x80A0A0A0;
	}

	/**
	 * override this function to change disabled button's color filter
	 * 
	 * @return
	 */
	protected int getDisabledColor()
	{
		return 0x80808080;
	}

	/**
	 * 
	 */
	private void changeToNormalState()
	{
		BitmapDrawable normal = (BitmapDrawable)original.mutate();
		normal.setColorFilter(getNormalColor(), PorterDuff.Mode.MULTIPLY);
		super.setBackgroundDrawable(normal);
	}

	/**
	 * 
	 */
	private void changeToPressedState()
	{
		BitmapDrawable pressed = (BitmapDrawable)original.mutate();
		pressed.setColorFilter(getPressedColor(), PorterDuff.Mode.MULTIPLY);
		super.setBackgroundDrawable(pressed);
	}

	/**
	 * 
	 */
	private void changeToDisabledState()
	{
		BitmapDrawable disabled = (BitmapDrawable)original.mutate();
		disabled.setColorFilter(getDisabledColor(), PorterDuff.Mode.MULTIPLY);
		super.setBackgroundDrawable(disabled);
	}

	/**
	 * initialize this button
	 */
	private void initialize()
	{
		if(isInEditMode())
			return;

		//copy original drawable
		original = new BitmapDrawable(getResources(), ((BitmapDrawable)getBackground()).getBitmap());
		
		if(!isEnabled())
			changeToDisabledState();
		
		setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View view, MotionEvent event)
			{
				if(!isEnabled())
					return false;

				int action = event.getAction();
				if(action == MotionEvent.ACTION_DOWN)
				{
					//Logger.v("button is down");
					changeToPressedState();

					isDown = true;
					return true;
				}
				else if(action == MotionEvent.ACTION_MOVE)
				{
					float x = event.getX(), y = event.getY();
					if(x >= 0 && x <= view.getWidth() && y >= 0 && y <= view.getHeight())
					{
						//Logger.v("in button rect");
						changeToPressedState();

						isDown = true;
					}
					else
					{
						//Logger.v("out of button rect");
						changeToNormalState();

						isDown = false;
					}

					return false;
				}
				else if(action == MotionEvent.ACTION_UP)
				{
					if(isDown)
					{
						//Logger.v("button is up");
						view.performClick();
					}
					else
					{
						//Logger.v("button is up (out of bounds)");
					}

					changeToNormalState();
					
					isDown = false;
					return true;
				}
				else if(action == MotionEvent.ACTION_CANCEL)
				{
					//Logger.v("button canceled");

					changeToNormalState();

					isDown = false;
					return false;
				}
				return false;
			}});
	}
}
