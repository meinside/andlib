/*
 Copyright (c) 2010, Sungjin Han <meinside@gmail.com>
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of meinside nor the names of its contributors may be
    used to endorse or promote products derived from this software without
    specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */

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
