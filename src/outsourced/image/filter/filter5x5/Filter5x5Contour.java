package outsourced.image.filter.filter5x5;

import outsourced.image.filter.base.Filter5x5;

/**
 * 
 * ported from: http://www.gdargaud.net/Hack/SourceCode.html#GraphicFilter
 * 
 * @author meinside@gmail.com
 * @since 10.03.05.
 * 
 * last update 10.03.08.
 */
public class Filter5x5Contour extends Filter5x5
{
	public Filter5x5Contour()
	{
		super("5x5 Contour", new int[][]{{0,0,0,0,0},{0,-1,-1,-1,0},{0,-1,8,-1,0},{0,-1,-1,-1,0},{0,0,0,0,0}}, 1, 255);
	}
}
