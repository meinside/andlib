package org.andlib.http;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.andlib.helpers.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


/**
 * 
 * @author meinside@gmail.com
 * @since 09.10.07.
 * 
 * last update 10.04.13.
 *
 */
public class XMLDOMParser
{
	private static XMLDOMParser parser = null;
	
	private DocumentBuilder docBuilder = null;
	
	/**
	 * 
	 */
	private XMLDOMParser()
	{
		try
        {
	        docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
	public static XMLDOMParser getInstance()
	{
		if(parser == null)
			parser = new XMLDOMParser();
		
		return parser;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public synchronized Document parse(String url)
	{
		docBuilder.reset();
		
		try
        {
	        return docBuilder.parse(url);
        }
        catch(Exception e)
        {
        	Logger.e(e.toString());
			return null;
        }
	}
	
	/**
	 * 
	 * @param is
	 * @return
	 */
	public synchronized Document parse(InputStream is)
	{
		docBuilder.reset();
		
		try
        {
	        return docBuilder.parse(is);
        }
        catch(Exception e)
        {
        	Logger.e(e.toString());
			return null;
        }
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public synchronized Document parse(File file)
	{
		docBuilder.reset();
		
		try
        {
	        return docBuilder.parse(file);
        }
        catch(Exception e)
        {
        	Logger.e(e.toString());
			return null;
        }
	}
	
	/**
	 * 
	 * @param xmlString
	 * @return
	 */
	public synchronized Document parseString(String xmlString)
	{
		docBuilder.reset();
		
		try
        {
	        return docBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch(Exception e)
        {
        	Logger.e(e.toString());
			return null;
        }
	}
}
