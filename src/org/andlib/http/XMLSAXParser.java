package org.andlib.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andlib.helpers.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author meinside@gmail.com
 * @since 09.10.07.
 * 
 * last update 10.04.13.
 *
 */
public class XMLSAXParser
{
	private static XMLSAXParser parser = null;
	
	private SAXParser saxParser = null;
	private XMLReader xmlReader = null;
	
	/**
	 * 
	 */
	private XMLSAXParser()
	{
		try
		{
			saxParser = SAXParserFactory.newInstance().newSAXParser();
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static XMLSAXParser getInstance()
	{
		if(parser == null)
		{
			parser = new XMLSAXParser();
		}
		return parser;
	}
	
	/**
	 * 
	 * @param is
	 * @param handler
	 * @return
	 */
	public synchronized boolean parse(InputStream is, DefaultHandler handler)
	{
		saxParser.reset();
		try
		{
			xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(is));

			return true;
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
			return false;
		}
	}
	
	/**
	 * 
	 * @param url
	 * @param handler
	 * @return
	 */
	public synchronized boolean parse(String url, DefaultHandler handler)
	{
		try
		{
			return this.parse((new URL(url)).openStream(), handler);
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
			return false;
		}
	}
	
	/**
	 * 
	 * @param file
	 * @param handler
	 * @return
	 */
	public synchronized boolean parse(File file, DefaultHandler handler)
	{
		try
		{
			return this.parse(new FileInputStream(file), handler);
		}
		catch(Exception e)
		{
			Logger.e(e.toString());
			return false;
		}
	}
}
