package org.andlib.ui;

import org.andlib.helpers.Logger;
import org.andlib.http.OAuthBase;
import org.andlib.http.OAuthBase.AuthUrlListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * web view class for authenticating user using OAuth protocol
 * 
 * @author meinside@gmail.com
 * @since 10.11.07.
 * 
 * last update 12.11.20.
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
		loadAuthPage(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public OAuthAuthView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		loadAuthPage(context);
	}

	/**
	 * @param context
	 */
	public OAuthAuthView(Context context)
	{
		super(context);
		loadAuthPage(context);
	}

	/**
	 * 
	 * @param context
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void loadAuthPage(Context context)
	{
		if(isInEditMode())
			return;

		//do not use cache
		getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		clearCache(true);

		//enable javascript
		getSettings().setJavaScriptEnabled(true);

		//set webview client
		setWebViewClient(new OAuthWebViewClient());

		OAuthBase oauth = getOAuthBaseForLoadingAuthPage();
		if(oauth != null)
		{
			oauth.requestUserAuthUrl(new AuthUrlListener(){
				public void authUrlReceiveFailed(int errorCode, String errorMessage)
				{
					Logger.e("receiving auth url failed - " + errorCode + ", " + errorMessage);
					
					onAuthUrlError(errorCode, errorMessage);
				}

				public void authUrlReceived(String url)
				{
					loadUrl(url);
				}});
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
	 * implement this function to show any alert or error message for not receiving auth url successfully
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	protected abstract void onAuthUrlError(int errorCode, String errorMessage);

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
