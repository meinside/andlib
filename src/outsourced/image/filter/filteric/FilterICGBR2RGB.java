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
public class FilterICGBR2RGB extends FilterIC
{
	public FilterICGBR2RGB()
	{
		super("IC BGR 2 RGB", new int[][]{{0,1,0,0},{0,0,1,0},{1,0,0,0},{0,0,0,1}}, new int[]{1,1,1,1}, new int[]{0,0,0,0});
	}
}
