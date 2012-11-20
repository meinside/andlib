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
 * last update 12.11.20.
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

					//edited by jipark - 2011.05.25.
					if(!isEnabled())
						changeToDisabledState();
					else
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
