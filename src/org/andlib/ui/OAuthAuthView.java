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

import org.andlib.helpers.Logger;
import org.andlib.http.OAuthBase;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * web view class for authenticating user using OAuth protocol
 * 
 * @author meinside@gmail.com
 * @since 10.11.07.
 * 
 * last update 10.11.08.
 *
 */
public abstract class OAuthAuthView extends WebView
{
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public OAuthAuthView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		loadAuthPage();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public OAuthAuthView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		loadAuthPage();
	}

	/**
	 * @param context
	 */
	public OAuthAuthView(Context context)
	{
		super(context);
		loadAuthPage();
	}

	/**
	 * 
	 */
	private void loadAuthPage()
	{
		getSettings().setJavaScriptEnabled(true);
		setWebViewClient(new OAuthWebViewClient());
		clearCache(true);

		OAuthBase oauth = getOAuthBaseForLoadingAuthPage();
		if(oauth != null)
		{
			String url = oauth.getUserAuthUrl();
			if(url != null)
				loadUrl(url);
			else
				Logger.e("returned url of auth page is null");
		}
		else
			Logger.e("OAuthBase object is null");
	}

	/**
	 * implement this function to get an initialized OAuthBase object for retrieving auth page 
	 * 
	 * @return
	 */
	protected abstract OAuthBase getOAuthBaseForLoadingAuthPage();

	/**
	 * 
	 * @author meinside@gmail.com
	 *
	 */
	private class OAuthWebViewClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			Logger.v("page finished: " + url);
		}
	}
}
