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

import kr.pe.meinside.android.ui.controller.handler.ContentsHandlerWorker;

import android.view.View;

/**
 * 
 * @author meinside@gmail.com
 * @since 09.11.19.
 * 
 * last update 10.02.22.
 * 
 */
public abstract class SimpleNavigationController 
{
	protected SimpleNavigationControllerActivity parent = null;
	protected ContentsHandlerWorker worker = null;
	
	private View rootChildView = null;
	private String title = null;
	private boolean removeWhenUnused = true;

	/**
	 * 
	 * @param parent
	 * @param rootChildView
	 */
	public SimpleNavigationController(final SimpleNavigationControllerActivity parent, int viewResId)
	{
		this.parent = parent;
		this.worker = generateWorker();

		this.rootChildView = parent.inflateView(viewResId);
	}
	
	/**
	 * 
	 * @param parent
	 * @param rootChildView
	 * @param title null if none
	 */
	public SimpleNavigationController(final SimpleNavigationControllerActivity parent, int viewResId, String title)
	{
		this(parent, viewResId);
		this.title = title;
	}
	
	/**
	 * 
	 * @return the title of this controller
	 */
	public String getTitle()
	{
		return this.title;
	}
	
	/**
	 * 
	 * @param title new title of this controller
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean checkRemoveWhenUnused()
	{
		return this.removeWhenUnused;
	}
	
	/**
	 * 
	 * @param removeWhenUnused
	 */
	public void setRemoveWhenUnused(boolean removeWhenUnused)
	{
		this.removeWhenUnused = removeWhenUnused;
	}
	
	/**
	 * 
	 * @return root child view of this controller
	 */
	public View getView()
	{
		return this.rootChildView;
	}
	
	/**
	 * find and return a view inside the root child view of this controller
	 * @param resid
	 * @return
	 */
	public View findViewById(int resid)
	{
		return this.getView().findViewById(resid);
	}
	
	/**
	 * 
	 * @return contents handler worker of this controller
	 */
	public ContentsHandlerWorker getWorker()
	{
		return this.worker;
	}
	
	/**
	 * implement this to generate a worker for this controller
	 * @return null if none
	 */
	public abstract ContentsHandlerWorker generateWorker();
	
	/**
	 * implement this to reload child view
	 */
	public abstract void reload();

	/**
	 * implement this to set previous button text manually (make it return null if not needed)
	 * @return
	 */
	public abstract String getPrevButtonText();
	
	/**
	 * implement this to set next button text manually (make it return null if not needed)
	 * @return
	 */
	public abstract String getNextButtonText();
}
