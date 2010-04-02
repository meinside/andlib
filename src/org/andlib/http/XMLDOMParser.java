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
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.andlib.helper.LogHelper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.util.Log;


/**
 * 
 * @author meinside@gmail.com
 * @since 09.10.07.
 * 
 * last update 09.11.22.
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
			Log.e(LogHelper.where(), e.toString());
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
			Log.e(LogHelper.where(), e.toString());
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
			Log.e(LogHelper.where(), e.toString());
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
			Log.e(LogHelper.where(), e.toString());
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
			Log.e(LogHelper.where(), e.toString());
			return null;
        }
	}
}
