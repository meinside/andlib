package outsourced.image.filter.base;

import org.andlib.helpers.Logger;

import android.graphics.Bitmap;
import android.graphics.Color;

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
public class Filter1x1 extends FilterBase
{
	private String name;

	private int multiplier;
	private int divider;
	private int bias;
	
	/**
	 * 
	 * @param name
	 * @param multiplier
	 * @param divider
	 * @param bias
	 */
	public Filter1x1(String name, int multiplier, int divider, int bias)
	{
		this.name = name;
		this.multiplier = multiplier;
		this.divider = divider;
		this.bias = bias;
	}

	@Override
	public Bitmap filter(Bitmap bitmap)
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		Logger.v(name + ", multiplier: " + multiplier + ", divider: " + divider + ", bias: " + bias);
		
		if(bitmap == null || divider == 0 || (multiplier == 1 && divider == 1 && bias == 0))
		{
			Logger.e("parameter error (bitmap null, divider is zero, or given filter has no effect)");
			return null;
		}

		int[] bitmapBytes = new int[width * height];
		bitmap.getPixels(bitmapBytes, 0, width, 0, 0, width, height);

		int color;
		for(int i=0; i<bitmapBytes.length; i++)
		{
			color = bitmapBytes[i];
			
			bitmapBytes[i] = Color.rgb(
					forcepin(0, r(color) * multiplier / divider + bias, 255), 
					forcepin(0, g(color) * multiplier / divider + bias, 255), 
					forcepin(0, b(color) * multiplier / divider + bias, 255));
		}

		return Bitmap.createBitmap(bitmapBytes, width, height, bitmap.getConfig());
	}
}
