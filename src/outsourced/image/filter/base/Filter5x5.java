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
public class Filter5x5 extends FilterBase
{
	private String name;

	private int[][] multiplier;	//[5][5]
	private int divider;
	private int bias;
	
	/**
	 * 
	 * @param name
	 * @param multiplier
	 * @param divider
	 * @param bias
	 */
	public Filter5x5(String name, int[][] multiplier, int divider, int bias)
	{
		this.name = name;
		this.multiplier = multiplier;
		this.divider = divider;
		this.bias = bias;
	}

	@Override
	public Bitmap filter(Bitmap bitmap)
	{
		Logger.v(name + ", multiplier: " + multiplier + ", divider: " + divider + ", bias: " + bias);

		if(bitmap == null || divider == 0)
		{
			Logger.e("parameter error (bitmap null or divider is zero)");
			return null;
		}

		int bytesPerPixel = getBitmapPixelDepth(bitmap) / 8;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int[] bitmapBytes = new int[width * height];
		bitmap.getPixels(bitmapBytes, 0, width, 0, 0, width, height);

		/*
		 * 00 10 20 30 40
		 * 01 11 21 31 41
		 * 02 12 22 32 42
		 * 03 13 23 33 43
		 * 04 14 24 34 44
		 */
		int p00, p10, p20, p30, p40,
			p01, p11, p21, p31, p41,
			p02, p12, p22, p32, p42,
			p03, p13, p23, p33, p43,
			p04, p14, p24, p34, p44;
		int m00 = multiplier[0][0],m10 = multiplier[0][1],m20 = multiplier[0][2],m30 = multiplier[0][3],m40 = multiplier[0][4],
			m01 = multiplier[1][0],m11 = multiplier[1][1],m21 = multiplier[1][2],m31 = multiplier[1][3],m41 = multiplier[1][4],
			m02 = multiplier[2][0],m12 = multiplier[2][1],m22 = multiplier[2][2],m32 = multiplier[2][3],m42 = multiplier[2][4],
			m03 = multiplier[3][0],m13 = multiplier[3][1],m23 = multiplier[3][2],m33 = multiplier[3][3],m43 = multiplier[3][4],
			m04 = multiplier[4][0],m14 = multiplier[4][1],m24 = multiplier[4][2],m34 = multiplier[4][3],m44 = multiplier[4][4];

		//speed up
		if(m00 == 0 && m10 == 0 && m20 == 0 && m30 == 0 && m40 == 0 &&
			m01 == 0 &&                                    m41 == 0 &&
			m02 == 0 &&                                    m42 == 0 &&
			m03 == 0 &&                                    m43 == 0 &&
			m04 == 0 && m14 == 0 && m24 == 0 && m34 == 0 && m44 == 0)
		{
			return new Filter3x3("3x3 Speed Up", new int[][]{
					{m11, m21, m31}, 
					{m12, m22, m32}, 
					{m13, m23, m33}
					}, divider, bias).filter(bitmap);
		}

		int destPos = 0;
		int x, y;
		int a, r, g, b;

		int prevPrevLinePos = 0;
		int prevLinePos = 0;
		int currentLinePos = width;
		int nextLinePos = width * 2;
		int nextNextLinePos = width * 3;

		for(y=2; y<height-2; y++)
		{
			prevPrevLinePos = prevLinePos;
			prevLinePos = currentLinePos;
			currentLinePos = nextLinePos;
			nextLinePos = nextNextLinePos;
			nextNextLinePos = width * (y + 1);
			
			p00 = prevPrevLinePos;
			p10 = prevPrevLinePos + 1;
			p20 = prevPrevLinePos + 2;
			p30 = prevPrevLinePos + 3;
			p40 = prevPrevLinePos + 4;
			p01 = prevLinePos;
			p11 = prevLinePos + 1;
			p21 = prevLinePos + 2;
			p31 = prevLinePos + 3;
			p41 = prevLinePos + 4;
			p02 = currentLinePos;
			p12 = currentLinePos + 1;
			p22 = currentLinePos + 2;
			p32 = currentLinePos + 3;
			p42 = currentLinePos + 4;
			p03 = nextLinePos;
			p13 = nextLinePos + 1;
			p23 = nextLinePos + 2;
			p33 = nextLinePos + 3;
			p43 = nextLinePos + 4;
			p04 = nextNextLinePos;
			p14 = nextNextLinePos + 1;
			p24 = nextNextLinePos + 2;
			p34 = nextNextLinePos + 3;
			p44 = nextNextLinePos + 4;

			destPos = 2 + y * width;
			for(x=2; x<width-2; x++)
			{
				//Red
				r = forcepin(0,
						(m00 * r(bitmapBytes[p00]) + 
						m10 * r(bitmapBytes[p10]) + 
						m20 * r(bitmapBytes[p20]) + 
						m30 * r(bitmapBytes[p30]) + 
						m40 * r(bitmapBytes[p40]) +
						m01 * r(bitmapBytes[p01]) + 
						m11 * r(bitmapBytes[p11]) + 
						m21 * r(bitmapBytes[p21]) + 
						m31 * r(bitmapBytes[p31]) + 
						m41 * r(bitmapBytes[p41]) +
						m02 * r(bitmapBytes[p02]) + 
						m12 * r(bitmapBytes[p12]) + 
						m22 * r(bitmapBytes[p22]) + 
						m32 * r(bitmapBytes[p32]) + 
						m42 * r(bitmapBytes[p42]) +
						m03 * r(bitmapBytes[p03]) + 
						m13 * r(bitmapBytes[p13]) + 
						m23 * r(bitmapBytes[p23]) + 
						m33 * r(bitmapBytes[p33]) + 
						m43 * r(bitmapBytes[p43]) +
						m04 * r(bitmapBytes[p04]) + 
						m14 * r(bitmapBytes[p14]) + 
						m24 * r(bitmapBytes[p24]) + 
						m34 * r(bitmapBytes[p34]) + 
						m44 * r(bitmapBytes[p44])) / divider + bias,
					 255);

				//Green
				g = forcepin(0,
						(m00 * g(bitmapBytes[p00]) + 
						m10 * g(bitmapBytes[p10]) + 
						m20 * g(bitmapBytes[p20]) + 
						m30 * g(bitmapBytes[p30]) + 
						m40 * g(bitmapBytes[p40]) +
						m01 * g(bitmapBytes[p01]) + 
						m11 * g(bitmapBytes[p11]) + 
						m21 * g(bitmapBytes[p21]) + 
						m31 * g(bitmapBytes[p31]) + 
						m41 * g(bitmapBytes[p41]) +
						m02 * g(bitmapBytes[p02]) + 
						m12 * g(bitmapBytes[p12]) + 
						m22 * g(bitmapBytes[p22]) + 
						m32 * g(bitmapBytes[p32]) + 
						m42 * g(bitmapBytes[p42]) +
						m03 * g(bitmapBytes[p03]) + 
						m13 * g(bitmapBytes[p13]) + 
						m23 * g(bitmapBytes[p23]) + 
						m33 * g(bitmapBytes[p33]) + 
						m43 * g(bitmapBytes[p43]) +
						m04 * g(bitmapBytes[p04]) + 
						m14 * g(bitmapBytes[p14]) + 
						m24 * g(bitmapBytes[p24]) + 
						m34 * g(bitmapBytes[p34]) + 
						m44 * g(bitmapBytes[p44])) / divider + bias,
					 255);

				//Blue
				b = forcepin(0,
						(m00 * b(bitmapBytes[p00]) + 
						m10 * b(bitmapBytes[p10]) + 
						m20 * b(bitmapBytes[p20]) + 
						m30 * b(bitmapBytes[p30]) + 
						m40 * b(bitmapBytes[p40]) +
						m01 * b(bitmapBytes[p01]) + 
						m11 * b(bitmapBytes[p11]) + 
						m21 * b(bitmapBytes[p21]) + 
						m31 * b(bitmapBytes[p31]) + 
						m41 * b(bitmapBytes[p41]) +
						m02 * b(bitmapBytes[p02]) + 
						m12 * b(bitmapBytes[p12]) + 
						m22 * b(bitmapBytes[p22]) + 
						m32 * b(bitmapBytes[p32]) + 
						m42 * b(bitmapBytes[p42]) +
						m03 * b(bitmapBytes[p03]) + 
						m13 * b(bitmapBytes[p13]) + 
						m23 * b(bitmapBytes[p23]) + 
						m33 * b(bitmapBytes[p33]) + 
						m43 * b(bitmapBytes[p43]) +
						m04 * b(bitmapBytes[p04]) + 
						m14 * b(bitmapBytes[p14]) + 
						m24 * b(bitmapBytes[p24]) + 
						m34 * b(bitmapBytes[p34]) + 
						m44 * b(bitmapBytes[p44])) / divider + bias,
					 255);
				
				if (bytesPerPixel == 3)
				{
					bitmapBytes[destPos++] = Color.rgb(r, g, b);
				}
				else
				{
					//Alpha
					a = forcepin(0,
							(m00 * a(bitmapBytes[p00]) + 
							m10 * a(bitmapBytes[p10]) + 
							m20 * a(bitmapBytes[p20]) + 
							m30 * a(bitmapBytes[p30]) + 
							m40 * a(bitmapBytes[p40]) +
							m01 * a(bitmapBytes[p01]) + 
							m11 * a(bitmapBytes[p11]) + 
							m21 * a(bitmapBytes[p21]) + 
							m31 * a(bitmapBytes[p31]) + 
							m41 * a(bitmapBytes[p41]) +
							m02 * a(bitmapBytes[p02]) + 
							m12 * a(bitmapBytes[p12]) + 
							m22 * a(bitmapBytes[p22]) + 
							m32 * a(bitmapBytes[p32]) + 
							m42 * a(bitmapBytes[p42]) +
							m03 * a(bitmapBytes[p03]) + 
							m13 * a(bitmapBytes[p13]) + 
							m23 * a(bitmapBytes[p23]) + 
							m33 * a(bitmapBytes[p33]) + 
							m43 * a(bitmapBytes[p43]) +
							m04 * a(bitmapBytes[p04]) + 
							m14 * a(bitmapBytes[p14]) + 
							m24 * a(bitmapBytes[p24]) + 
							m34 * a(bitmapBytes[p34]) + 
							m44 * a(bitmapBytes[p44])) / divider + bias,
						 255);

					bitmapBytes[destPos++] = Color.argb(a, r, g, b);
				}
				
				p00 ++; p10 ++; p20 ++; p30 ++; p40 ++;
				p01 ++; p11 ++; p21 ++; p31 ++; p41 ++;
				p02 ++; p12 ++; p22 ++; p32 ++; p42 ++;
				p03 ++; p13 ++; p23 ++; p33 ++; p43 ++;
				p04 ++; p14 ++; p24 ++; p34 ++; p44 ++;
			}
		}

		return Bitmap.createBitmap(bitmapBytes, width, height, bitmap.getConfig());
	}
}
