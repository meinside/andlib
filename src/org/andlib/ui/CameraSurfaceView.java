package org.andlib.ui;

import org.andlib.helpers.Logger;
import org.andlib.helpers.image.ImageUtility;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * surface view for capturing photos through camera
 * 
 * @author meinside@gmail.com
 * @since 10.03.17.
 * 
 * last update 12.05.29.
 *
 */
public abstract class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback, Camera.ShutterCallback, Camera.PreviewCallback
{
	private static SurfaceHolder holder = null;
	protected static Camera camera = null;

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public CameraSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	/**
	 * 
	 * @param context
	 */
	public CameraSurfaceView(Context context)
	{
		super(context);
		initialize(context);
	}

	/**
	 * 
	 * @param context
	 */
	private void initialize(Context context)
	{
		if(isInEditMode())
			return;

		Logger.v("initialize");
		
		holder = getHolder();
		holder.addCallback(this);
		//FIXXX - deprecated
//		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**
	 * override this function to alter camera parameters (preview size, picture size, and so on)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		Logger.v("surfaceChanged");
		
		Camera.Parameters params = camera.getParameters();

		Size optimalSize = ImageUtility.getOptimalPreviewSize(params.getSupportedPreviewSizes(), width, height);
		params.setPreviewSize(optimalSize.width, optimalSize.height);
		params.setPictureSize(optimalSize.width, optimalSize.height);

		params.setPictureFormat(ImageFormat.JPEG);

		camera.setParameters(params);

		camera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		Logger.v("surfaceCreated");

		try
		{
			camera = Camera.open();
			camera.setPreviewDisplay(holder);
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Logger.v("surfaceDestroyed");
		
		if(camera != null)
		{
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean capture()
	{
		Logger.v("shutter clicked");

		if(camera != null)
		{
			camera.takePicture(this, this, this);
			return true;
		}
		return false;
	}

	/**
	 * implement this to do something with picture
	 */
	@Override
	abstract public void onPictureTaken(byte[] data, Camera camera);

	/**
	 * implement this to do something with preview frame
	 */
	@Override
	abstract public void onPreviewFrame(byte[] data, Camera camera);

	/**
	 * override this function to do something more on shutter
	 */
	@Override
	public void onShutter()
	{
		Logger.v("onShutter");
	}
}
