package outsourced.image.filter.filter3x3;

import outsourced.image.filter.base.Filter3x3;

/**
 * 
 * ported from: http://www.gdargaud.net/Hack/SourceCode.html#GraphicFilter
 * 
 * @author meinside@gmail.com
 * @since 10.03.05.
 * 
 * last update 10.03.08.
 */
public class Filter3x3SharpenStrong extends Filter3x3
{
	public Filter3x3SharpenStrong()
	{
		super("3x3 Sharpen Strong", new int[][]{{-1,-1,-1},{-1,9,-1},{-1,-1,-1}}, 1, 0);
	}
}
