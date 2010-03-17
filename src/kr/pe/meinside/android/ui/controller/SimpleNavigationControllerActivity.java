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

package kr.pe.meinside.android.ui.controller;

import java.util.ArrayList;

import kr.pe.meinside.android.helper.LogHelper;
import kr.pe.meinside.android.ui.controller.handler.ContentsHandler;
import kr.pe.meinside.android.ui.controller.handler.ContentsHandlerWorker;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;

/**
 * 
 * @author meinside@gmail.com
 * @since 09.11.19.
 * 
 * last update 10.03.04.
 * 
 * 
 * 
 * FIXXX: this activity needs
 * 
 * 		android:configChanges="orientation"
 * 
 * in its manifest file to avoid crash when tilting.
 * (<= java.lang.IllegalArgumentException: Receiver not registered: android.widget.ViewFlipper ... blah blah)
 * 
 */
public abstract class SimpleNavigationControllerActivity extends Activity
{
	//ids
	public static final int ID_NOT_USED = -7777777;
	public static final int ID_PREVIOUS_BUTTON = ID_NOT_USED + 1;
	public static final int ID_NEXT_BUTTON = ID_NOT_USED + 2;
	public static final int ID_TITLE_TEXT = ID_NOT_USED + 3;
	public static final int ID_CHILD_VIEW = ID_NOT_USED + 4;
	
	//color constants
	public static final int DEFAULT_BGCOLOR_NAVIGATION_BAR = Color.LTGRAY;
	public static final int DEFAULT_BGCOLOR_CONTENT_VIEW = Color.WHITE;
	public static final int DEFAULT_BGCOLOR_TITLE_TEXT = Color.TRANSPARENT;
	public static final int DEFAULT_BGCOLOR_NAV_BUTTON = Color.WHITE;
	public static final int DEFAULT_FORECOLOR_TITLE_TEXT = Color.BLACK;
	public static final int DEFAULT_FORECOLOR_NAV_BUTTON = Color.BLACK;
	
	//other constants
	public static final int ANIMATION_NOT_SET = -1;
	public static final String ACTIVITY_FINISH_BUTTON_TEXT = "Finish";
	
	//margins
	public static final int VIEW_FLIPPER_INNER_MARGIN = 6;

	//variables
	private Intent caller = null;
	private ContentsHandler handler = null;
	private OnButtonClickListener onButtonClickListener = null;
	private Context context;
	
	//controls
	private Button prevButton = null;
	private Button nextButton = null;
	private TextView titleText = null;
	private LinearLayout verticalLayout = null;
	private LinearLayout horizontalLayout = null;
	private ViewFlipper viewFlipper = null;
	
	//data structures
	private int controllerIndex = -1;
	private ArrayList<SimpleNavigationController> controllers = null;
	
	//abstract functions
	protected abstract SimpleNavigationController getRootNavigationController();
	protected abstract boolean isAppTitleShown();
	protected abstract boolean isFinishButtonNeededOnRootNavigationController();
	
	/**
	 * Initializes with root view controller.
	 * 
	 * Override this to do something else (should also call super.initialize()).
	 * 
	 */
	protected void initialize()
	{
		this.addNavigationController(getRootNavigationController(), false);
	}

	/**
	 * 
	 * @param resid
	 * @return
	 */
	final protected View inflateView(int resid)
	{
		return getLayoutInflater().inflate(resid, null);
	}
	
	/**
	 * 
	 * @return
	 */
	final protected SimpleNavigationController getCurrentController()
	{
		return controllers.get(controllerIndex);
	}
	
	/**
	 * 
	 * @return
	 */
	final protected SimpleNavigationController getNextController()
	{
		if(controllerIndex < (controllers.size() - 1))
		{
			return controllers.get(controllerIndex + 1);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	final protected SimpleNavigationController getPrevController()
	{
		if(controllerIndex > 0)
		{
			return controllers.get(controllerIndex - 1);
		}
		else
		{
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	final protected View getCurrentControllerView()
	{
		return getCurrentController().getView();
	}
	
	/**
	 * override this to change navigation bar's background color
	 * 
	 * @return navigation bar's background color
	 */
	protected int getNavigationBarBgcolor()
	{
		return DEFAULT_BGCOLOR_NAVIGATION_BAR;
	}
	
	/**
	 * override this to change content view's background color
	 * 
	 * @return content view's background color
	 */
	protected int getContentViewBgcolor()
	{
		return DEFAULT_BGCOLOR_CONTENT_VIEW;
	}
	
	/**
	 * override this to change title text's background color
	 * 
	 * @return title text's background color
	 */
	protected int getTitleTextBgcolor()
	{
		return DEFAULT_BGCOLOR_TITLE_TEXT;
	}
	
	/**
	 * override this to change navigation button's background color
	 * 
	 * @return navigation button's background color
	 */
	protected int getNavigationButtonBgcolor()
	{
		return DEFAULT_BGCOLOR_NAV_BUTTON;
	}
	
	/**
	 * 
	 * @return
	 */
	protected int getTitleTextForecolor()
	{
		return DEFAULT_FORECOLOR_TITLE_TEXT;
	}
	
	/**
	 * 
	 * @return
	 */
	protected int getNavigationButtonForecolor()
	{
		return DEFAULT_FORECOLOR_NAV_BUTTON;
	}
	
	/**
	 * override this to change animation for 'previous' flip
	 * 
	 * @return resource id for previous flip animation
	 */
	protected int getPrevFlipAnimationId()
	{
		return ANIMATION_NOT_SET;
	}
	
	/**
	 * override this to change animation for 'next' flip
	 * 
	 * @return resource id for next flip animation
	 */
	protected int getNextFlipAnimationId()
	{
		return ANIMATION_NOT_SET;
	}
	
	/**
	 * reloads navigation bar (its title and previous/next button caption)
	 */
	final public void reloadNavigationBar()
	{
		Log.v(LogHelper.where(), "reloading nav bar: controllers.size = " + controllers.size() + ", index = " + controllerIndex + ", remove = " + controllers.get(controllerIndex).checkRemoveWhenUnused());
		
		//check current view controller and set/enable/disable buttons and title text
		if(controllers.size() > 0)
		{
			SimpleNavigationController current = getCurrentController();
			
			//set prev button
			SimpleNavigationController prevController = getPrevController();
			if(prevController != null)
			{
				String prevButtonTitle = current.getPrevButtonText();
				String text = (prevButtonTitle != null) ? prevButtonTitle : prevController.getTitle();
				if(text != null && text.length() > 0)
					prevButton.setText(text);
				else
					prevButton.setText("Previous");
				prevButton.setVisibility(View.VISIBLE);
			}
			else
			{
				if(getCurrentControllerIndex() == 0 && isFinishButtonNeededOnRootNavigationController())
				{
					//show 'finish' button
					prevButton.setText(getActivityFinishButtonText());
					prevButton.setVisibility(View.VISIBLE);
				}
				else
					prevButton.setVisibility(View.INVISIBLE);
			}
			
			//set next button
			SimpleNavigationController nextController = getNextController();
			if(nextController != null)
			{
				String nextButtonTitle = current.getNextButtonText();
				String text = (nextButtonTitle != null) ? nextButtonTitle : nextController.getTitle();
				if(text != null && text.length() > 0)
					nextButton.setText(text);
				else
					nextButton.setText("Next");
				nextButton.setVisibility(View.VISIBLE);
			}
			else
			{
				nextButton.setVisibility(View.INVISIBLE);
			}
			
			//set title
			if(current.getTitle() != null)
				titleText.setText(current.getTitle());
			else
				titleText.setText("");
		}
	}
	
	/**
	 * 
	 */
	final public void reloadContentView()
	{
		Log.v(LogHelper.where(), "reloading content view with index: " + getCurrentControllerIndex());

		getCurrentController().reload();
	}

	/**
	 * add a new controller
	 * @param controller newly created controller
	 * @param reload to reload or not
	 */
	final public synchronized void addNavigationController(SimpleNavigationController controller, boolean reload)
	{
		Log.v(LogHelper.where(), "adding a controller: " + controller.getTitle());
		
    	if(controllers.size() -1 > controllerIndex)	//if there are 'next' controllers left after this one,
    	{
    		Log.v(LogHelper.where(), "next controllers will be removed");

    		for(int i = controllers.size() - 1; i > controllerIndex; i--)
        		removeThingsAtIndex(i);
    	}
		
		controllers.add(controller);
		controllerIndex = controllers.size() - 1;

		ContentsHandlerWorker worker = controller.getWorker();
		if(worker != null)
		{
			handler.addWorker(worker);
			
			Log.v(LogHelper.where(), "added a worker: " + worker.getId() + " (total: " + handler.workerMap.size() + ")");
		}

		//add to view flipper
		viewFlipper.addView(controller.getView(), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		if(reload && getNextFlipAnimationId() != ANIMATION_NOT_SET)
			viewFlipper.setAnimation(AnimationUtils.loadAnimation(context, getNextFlipAnimationId()));
		viewFlipper.showNext();
		
		//reload other things
		if(reload)
		{
			reloadNavigationBar();
		}
		reloadContentView();
	}
	
	/**
	 * remove controller, worker, view at given index
	 * @param index
	 */
	private void removeThingsAtIndex(int index)
	{
		Log.v(LogHelper.where(), "removing controller/worker/view at index: " + index);
		
		//remove unused view from view flipper
		viewFlipper.removeViewAt(index);
		
		//remove unused worker
		ContentsHandlerWorker worker = controllers.get(index).getWorker();
		if(worker != null)
			handler.removeWorker(worker.getId());
		
		//remove unused controller
		controllers.remove(index);
	}
	
	/**
	 * 
	 * @param controller
	 */
	final public void addNavigationController(SimpleNavigationController controller)
	{
		this.addNavigationController(controller, true);
	}
	
	/**
	 * 
	 * @return
	 */
	final public ContentsHandler getHandler()
	{
		return this.handler;
	}
	
	/**
	 * 
	 * @param msg
	 * @param workerId
	 */
	final public void sendMessageToWorker(Message msg, int workerId)
	{
		getHandler().sendMessageToWorker(msg, workerId);
	}
	
	/**
	 * 
	 * @return
	 */
	final public Intent getCallerIntent()
	{
		return this.caller;
	}

	/* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    final protected void onCreate(Bundle savedInstanceState)
    {
		Log.v(LogHelper.where(), "onCreate called");

	    //- initialize parameters
    	context = getApplicationContext();
        caller = getIntent();
        handler = new ContentsHandler();
        onButtonClickListener = new OnButtonClickListener();
        controllers = new ArrayList<SimpleNavigationController>();

		//- organize views
		//layouts
		verticalLayout = new LinearLayout(this);
		verticalLayout.setOrientation(LinearLayout.VERTICAL);
		verticalLayout.setBackgroundColor(this.getContentViewBgcolor());
		horizontalLayout = new LinearLayout(this);
		horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
		horizontalLayout.setHorizontalGravity(Gravity.FILL_HORIZONTAL);
		horizontalLayout.setBackgroundColor(this.getNavigationBarBgcolor());
		//previous button
		prevButton = new Button(this);
		prevButton.setId(ID_PREVIOUS_BUTTON);
		prevButton.setOnClickListener(onButtonClickListener);
		prevButton.setVisibility(View.INVISIBLE);
//		prevButton.setBackgroundColor(this.getNavigationButtonBgcolor());
		prevButton.setTextColor(this.getNavigationButtonForecolor());
		prevButton.setMaxWidth(60);
		prevButton.setText("prev button");
		//next button
		nextButton = new Button(this);
		nextButton.setId(ID_NEXT_BUTTON);
		nextButton.setOnClickListener(onButtonClickListener);
		nextButton.setVisibility(View.INVISIBLE);
//		nextButton.setBackgroundColor(this.getNavigationButtonBgcolor());
		nextButton.setTextColor(this.getNavigationButtonForecolor());
		nextButton.setMaxWidth(60);
		nextButton.setText("next button");
		//title text
		titleText = new TextView(this);
		titleText.setId(ID_TITLE_TEXT);
		titleText.setVisibility(View.VISIBLE);
		titleText.setBackgroundColor(this.getTitleTextBgcolor());
		titleText.setTextColor(this.getTitleTextForecolor());
		titleText.setTextAppearance(context, android.R.style.TextAppearance_DialogWindowTitle);
		//view flipper
		viewFlipper = new ViewFlipper(this);
		viewFlipper.setId(ID_CHILD_VIEW);

		//- On/Off android app title
		if(!isAppTitleShown())
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//onCreate
        super.onCreate(savedInstanceState);

		//- place views
		//layouts
		verticalLayout.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		horizontalLayout.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		horizontalLayout.setGravity(Gravity.CENTER);
		//buttons
		prevButton.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT,
				1));
		prevButton.setGravity(Gravity.CENTER);
		prevButton.setSingleLine(true);
		prevButton.setEllipsize(TruncateAt.END);
		nextButton.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT,
				1));
		nextButton.setGravity(Gravity.CENTER);
		nextButton.setSingleLine(true);
		nextButton.setEllipsize(TruncateAt.END);
		//title text
		titleText.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT,
				6));
		titleText.setGravity(Gravity.CENTER);
		titleText.setSingleLine(true);
		titleText.setEllipsize(TruncateAt.END);
		//view flipper
		LayoutParams viewFlipperLayoutParams = new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		viewFlipperLayoutParams.setMargins(VIEW_FLIPPER_INNER_MARGIN, VIEW_FLIPPER_INNER_MARGIN, VIEW_FLIPPER_INNER_MARGIN, VIEW_FLIPPER_INNER_MARGIN);
		viewFlipper.setLayoutParams(viewFlipperLayoutParams);

		//- add views
		horizontalLayout.addView(prevButton, 0);
		horizontalLayout.addView(titleText, 1);
		horizontalLayout.addView(nextButton, 2);
		verticalLayout.addView(horizontalLayout, 0);
		verticalLayout.addView(viewFlipper, 1);
		setContentView(verticalLayout);
        
		//initialize
        initialize();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
		Log.v(LogHelper.where(), "onResume called");

	    super.onResume();
        reloadNavigationBar();
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		Log.v(LogHelper.where(), "onConfigurationChanged called");

		super.onConfigurationChanged(newConfig);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy()
	{
		Log.v(LogHelper.where(), "onDestroy called");

		super.onDestroy();
	}

	/**
     * 
     */
    final public synchronized void showPrevious()
    {
    	if(controllerIndex >= 1)
    	{
    		SimpleNavigationController controller = controllers.get(controllerIndex);
        	if(getPrevFlipAnimationId() != ANIMATION_NOT_SET)
        		viewFlipper.setAnimation(AnimationUtils.loadAnimation(context, getPrevFlipAnimationId()));
        	viewFlipper.showPrevious();

        	if(controller.checkRemoveWhenUnused())
        	{
        		Log.v(LogHelper.where(), "current(or more) controller will be removed:" + controller.getTitle());

        		for(int i = controllers.size() - 1; i >= controllerIndex; i--)
            		removeThingsAtIndex(i);
        	}
    		controllerIndex --;
    		
    		reloadNavigationBar();
    		reloadContentView();
    	}
    }

    /**
     * 
     */
    final public synchronized void showNext()
    {
    	if(controllerIndex < controllers.size() - 1)
    	{
    		if(getNextFlipAnimationId() != ANIMATION_NOT_SET)
    			viewFlipper.setAnimation(AnimationUtils.loadAnimation(context, getNextFlipAnimationId()));
    		viewFlipper.showNext();

    		controllerIndex ++;
    		
    		reloadNavigationBar();
    		reloadContentView();
    	}
    }

    /**
     * 
     * @author meinside@gmail.com
     *
     */
    final private class OnButtonClickListener implements OnClickListener
    {
        public void onClick(View view)
        {
			switch(view.getId())
			{
			case ID_PREVIOUS_BUTTON:
				if(getCurrentControllerIndex() == 0 && isFinishButtonNeededOnRootNavigationController())
					finishActivity();	//finish this activity
				else
					showPrevious();
				break;
			case ID_NEXT_BUTTON:
				showNext();
				break;
			default:
				break;
			}
        }
    }
    
    /**
     * 
     * @param id
     */
    final public void runWorker(int id)
    {
    	runWorker(id, null);
    }
    
    /**
     * 
     * @param id
     * @param msg
     */
    final public void runWorker(int id, Message msg)
    {
    	Message newMsg;
    	if(msg != null)
    		newMsg = Message.obtain(msg);
    	else
    		newMsg = Message.obtain();
    	newMsg.arg1 = id;
    	getHandler().sendMessage(newMsg);
    }
    
    /**
     * 
     * @return
     */
    final public int getCurrentControllerIndex()
    {
    	return this.controllerIndex;
    }
    
    /**
     * 
     * @return
     */
    final public Context getContext()
    {
    	return this.context;
    }
    
    /**
     * finish this activity 
     * (override this to set extra result values while finishing.
     *  should call super.finishActivity() at the end)
     */
    protected void finishActivity()
    {
		Log.v(LogHelper.where(), "finishing activity...");

    	finish();
    }
    
    /**
     * override this to change activity finish button's text
     * 
     * @return get default activity finish button's text
     */
    protected String getActivityFinishButtonText()
    {
    	return ACTIVITY_FINISH_BUTTON_TEXT;
    }
}
