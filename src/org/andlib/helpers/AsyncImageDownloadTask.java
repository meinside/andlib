package org.andlib.helpers;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * helper task for downloading a bitmap image from http and setting it to given image view asynchronously  
 * <br>
 * <br>
 * <b>referenced</b>: http://android-developers.blogspot.com/2010/07/multithreading-for-performance.html
 * 
 * @author meinside@gmail.com
 * @since 10.11.12.
 * 
 * last update: 10.11.12.
 * 
 */
public class AsyncImageDownloadTask extends AsyncTask<String, Void, Bitmap>
{
	private String url;
	private final WeakReference<ImageView> imageViewReference;
	private ImageDownloadListener listener;

	/**
	 * 
	 * @param imageView
	 */
	public AsyncImageDownloadTask(ImageView imageView, ImageDownloadListener listener)
	{
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.listener = listener;
	}

	/**
	 * 
	 * @param url
	 * @param imageView
	 */
	public void download(String url, ImageView imageView)
	{
		if(cancelPotentialDownload(url, imageView))
		{
			AsyncImageDownloadTask task = new AsyncImageDownloadTask(imageView, listener);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url);
		}
	}

	@Override
	protected Bitmap doInBackground(String... params)
	{
		return downloadBitmap(params[0]);
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		if(isCancelled())
		{
			bitmap = null;
		}

		if(imageViewReference != null)
		{
			ImageView imageView = imageViewReference.get();
			AsyncImageDownloadTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

			// Change bitmap only if this process is still associated with it
			if(this == bitmapDownloaderTask)
			{
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	/**
	 * 
	 * @param url
	 * @param imageView
	 * @return false if the same url is already being downloaded
	 */
	private boolean cancelPotentialDownload(String url, ImageView imageView)
	{
		AsyncImageDownloadTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if(bitmapDownloaderTask != null)
		{
			String bitmapUrl = bitmapDownloaderTask.url;
			if(bitmapUrl == null || !bitmapUrl.equals(url))
			{
				bitmapDownloaderTask.cancel(true);
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap downloadBitmap(String url)
	{
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);
		try
		{
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK)
			{
				final HttpEntity entity = response.getEntity();
				if(entity != null)
				{
					InputStream inputStream = null;
					try
					{
						inputStream = entity.getContent();
						final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						
						if(listener != null)
							listener.imageDownloaded(url, bitmap);	//call back
						
						return bitmap;
					}
					finally
					{
						if(inputStream != null)
						{
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			}
		}
		catch(Exception e)
		{
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
		}
		finally
		{
			if(client != null)
			{
				client.close();
			}
		}
		
		if(listener != null)
			listener.imageDownloadFailed(url);	//call back

		return null;
	}

	/**
	 * 
	 * @param imageView
	 * @return
	 */
	private AsyncImageDownloadTask getBitmapDownloaderTask(ImageView imageView)
	{
		if(imageView != null)
		{
			Drawable drawable = imageView.getDrawable();
			if(drawable instanceof DownloadedDrawable)
			{
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	/**
	 * <b>referenced</b>: http://android-developers.blogspot.com/2010/07/multithreading-for-performance.html
	 * 
	 */
	private class DownloadedDrawable extends ColorDrawable
	{
		private final WeakReference<AsyncImageDownloadTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(AsyncImageDownloadTask bitmapDownloaderTask)
		{
			super(Color.BLACK);
			bitmapDownloaderTaskReference = new WeakReference<AsyncImageDownloadTask>(bitmapDownloaderTask);
		}

		public AsyncImageDownloadTask getBitmapDownloaderTask()
		{
			return bitmapDownloaderTaskReference.get();
		}
	}

	/**
	 * for calling back download results
	 * 
	 * @author meinside@gmail.com
	 *
	 */
	public interface ImageDownloadListener
	{
		/**
		 * called when the download failed
		 * 
		 * @param imageUrl
		 */
		public void imageDownloadFailed(String imageUrl);
		
		/**
		 * called when the download finished successfully
		 * 
		 * @param imageUrl
		 * @param downloadedImage
		 */
		public void imageDownloaded(String imageUrl, Bitmap downloadedImage);
	}
}
