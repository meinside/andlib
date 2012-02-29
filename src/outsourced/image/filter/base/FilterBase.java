package outsourced.image.filter.base;

import org.andlib.helpers.Logger;

import android.graphics.Bitmap;

/**
 * 
 * ported from: http://www.gdargaud.net/Hack/SourceCode.html#GraphicFilter
 * 
 * XXX: needs optimization
 * 
 * @author meinside@gmail.com
 * @since 10.03.05.
 * 
 * last update 10.04.13.
 */
public abstract class FilterBase
{
	abstract public Bitmap filter(Bitmap bitmap);
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	final public int forcepin(int a, int b, int c)
	{
		return (a > b) ? a : (b > c ? c : b); 
	}

	/**
	 * 
	 * @param color
	 * @return
	 */
	final public int a(int color)
	{
		return (color >> 24) & 0xFF;
	}

	/**
	 * 
	 * @param color
	 * @return
	 */
	final public int r(int color)
	{
		return (color >> 16) & 0xFF;
	}
	
	/**
	 * 
	 * @param color
	 * @return
	 */
	final public int g(int color)
	{
		return (color >> 8) & 0xFF;
	}
	
	/**
	 * 
	 * @param color
	 * @return
	 */
	final public int b(int color)
	{
		return color & 0xFF;
	}

	/**
	 * 
	 * @param bitmap
	 * @return return pixel depth of given bitmap (in bits)
	 */
	final public int getBitmapPixelDepth(Bitmap bitmap)
	{
		int pixDepth = -1;
		Bitmap.Config config = bitmap.getConfig();
		if(config.equals(Bitmap.Config.ALPHA_8))
			pixDepth = 8;
		else if(config.equals(Bitmap.Config.ARGB_4444))
			pixDepth = 16;
		else if(config.equals(Bitmap.Config.ARGB_8888))
			pixDepth = 32;
		else if(config.equals(Bitmap.Config.RGB_565))
			pixDepth = 16;
		else
			Logger.e("no matching Bitmap.Config");
		return pixDepth;
	}
}
