package outsourced.image.filter.filter1x1;

import outsourced.image.filter.base.Filter1x1;

/**
 * 
 * ported from: http://www.gdargaud.net/Hack/SourceCode.html#GraphicFilter
 * 
 * @author meinside@gmail.com
 * @since 10.03.05.
 * 
 * last update 10.03.08.
 */
public class Filter1x1Darker extends Filter1x1
{
	public Filter1x1Darker()
	{
		super("1x1 Darker", 1, 1, -32);
	}
	
	public Filter1x1Darker(int amount)
	{
		super("1x1 Darker", 1, 1, -1 * amount);
	}
}
