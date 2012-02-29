package outsourced.image.filter.filteric;

import outsourced.image.filter.base.FilterIC;

/**
 * 
 * ported from: http://www.gdargaud.net/Hack/SourceCode.html#GraphicFilter
 * 
 * @author meinside@gmail.com
 * @since 10.03.05.
 * 
 * last update 10.03.08.
 */
public class FilterICWhiteAlpha extends FilterIC
{
	public FilterICWhiteAlpha()
	{
		super("IC White Alpha", new int[][]{{1,0,0,256},{0,1,0,256},{0,0,1,256},{0,0,0,1}}, new int[]{1,1,1,1}, new int[]{0,0,0,0});
	}
}
