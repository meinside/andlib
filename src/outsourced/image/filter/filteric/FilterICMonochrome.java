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
public class FilterICMonochrome extends FilterIC
{
	public FilterICMonochrome()
	{
		super("IC Monochrome", new int[][]{{77,150,29,0},{77,150,29,0},{77,150,29,0},{0,0,0,1}}, new int[]{256,256,256,1}, new int[]{0,0,0,0});
	}
}
