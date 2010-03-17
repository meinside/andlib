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

package kr.pe.meinside.android.http.services;

import java.io.File;
import java.util.HashMap;

import kr.pe.meinside.android.http.ApacheHttpUtility;
import kr.pe.meinside.android.http.HttpUtility;
import kr.pe.meinside.android.http.OAuthHelper;
import kr.pe.meinside.android.http.XMLDOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author meinside@gmail.com
 * @since 10.01.14.
 * 
 * last update 10.01.14.
 *
 */
public class TwitterHelper extends OAuthHelper
{
	public static final String YFROG_TWITTER_VERIFY_CREDENTIALS_URL = "https://twitter.com/account/verify_credentials.xml";
	
	private String screenName = null;

	/**
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param requestTokenUrl
	 * @param accessTokenUrl
	 * @param authorizeUrl
	 */
	public TwitterHelper(String consumerKey, String consumerSecret, String requestTokenUrl, String accessTokenUrl, String authorizeUrl)
	{
		super(consumerKey, consumerSecret, requestTokenUrl, accessTokenUrl, authorizeUrl, null, null);
	}

	/**
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param requestTokenUrl
	 * @param accessTokenUrl
	 * @param authorizeUrl
	 * @param accessToken
	 * @param accessTokenSecret
	 * @param screenName
	 */
	public TwitterHelper(String consumerKey, String consumerSecret, String requestTokenUrl, String accessTokenUrl, String authorizeUrl, String accessToken, String accessTokenSecret, String screenName)
	{
		super(consumerKey, consumerSecret, requestTokenUrl, accessTokenUrl, authorizeUrl, accessToken, accessTokenSecret);
		this.screenName = screenName;
	}

	/* (non-Javadoc)
	 * @see kr.pe.meinside.android.http.OAuthHelper#retrieveValuesAfterAuthorization(java.util.HashMap)
	 */
	@Override
	protected void retrieveValuesAfterAuthorization(HashMap<String, String> values)
	{
		super.retrieveValuesAfterAuthorization(values);
		this.screenName = values.get("screen_name");
	}

	/**
	 * 
	 * @return
	 */
	public String getScreenName()
	{
		return screenName;
	}




	/* ********************************************************************************
	 * functions for twitter service
	 * 
	 * - http://apiwiki.twitter.com/Twitter-API-Documentation
	 */
	//TODO - ....





	/* ********************************************************************************
	 * functions for yfrog service
	 * 
	 * - http://code.google.com/p/imageshackapi/wiki/TwitterAuthentication
	 * - http://code.google.com/p/imageshackapi/wiki/YFROGupload
	 */

	/**
	 * 
	 * @return
	 */
	private String getVerifyURLForYfrog()
	{
		if(!this.isAuthorized)
			return null;

		String timestamp = getTimestamp();
		String nonce = getNonce();

		HashMap<String, String> oauthHash = new HashMap<String, String>();
		oauthHash.put("oauth_consumer_key", this.consumerKey);
		oauthHash.put("oauth_token", this.accessToken);
		oauthHash.put("oauth_signature_method", "HMAC-SHA1");
		oauthHash.put("oauth_timestamp", timestamp);
		oauthHash.put("oauth_nonce", nonce);
		oauthHash.put("oauth_version", "1.0");

		//FIXXX: sometimes the result url doesn't work
		return String.format("%s?oauth_version=1.0&oauth_nonce=%s&oauth_timestamp=%s&oauth_consumer_key=%s&oauth_token=%s&oauth_signature_method=HMAC-SHA1&oauth_signature=%s", 
				YFROG_TWITTER_VERIFY_CREDENTIALS_URL, 
				nonce, 
				timestamp, 
				this.consumerKey, 
				this.accessToken, 
				generateSignature("GET", YFROG_TWITTER_VERIFY_CREDENTIALS_URL, oauthHash));
	}
	
	/**
	 * upload media(image/video) to yfrog
	 * @param developerKey
	 * @param username
	 * @param media
	 * @param filename
	 * @param geoTagOrNot
	 * @param publicOrNot
	 * @return
	 */
	public String getYfrogMediaUrl(String developerKey, String username, File media, String filename, boolean geoTagOrNot, boolean publicOrNot)
	{
		String verifyUrl = getVerifyURLForYfrog();
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("auth", "oauth");
		params.put("username", username);
		params.put("public", publicOrNot ? "yes" : "no");
		if(geoTagOrNot)
			params.put("tags", "");	//TODO - add some geo tag information here
		params.put("key", developerKey);
		params.put("media", media);
		params.put("verify_url", verifyUrl);

		//FIXXX: i don't get why multi-part file uploading does not work with HttpUtility but with ApacheHttpUtility
		kr.pe.meinside.android.http.ApacheHttpUtility.SimpleHttpResponse response = ApacheHttpUtility.getInstance().post("http://yfrog.com/api/upload", null, params);
		if(response != null && response.getHttpStatusCode() == 200)
		{
			return extractMediaUrlFromYfrogResponse(response.getHttpResponseBody());
		}
		return null;
	}

	/**
	 * upload media url to yfrog
	 * @param developerKey
	 * @param username
	 * @param url
	 * @param geoTagOrNot
	 * @param publicOrNot
	 * @return
	 */
	public String getYfrogMediaUrl(String developerKey, String username, String url, boolean geoTagOrNot, boolean publicOrNot)
	{
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("auth", "oauth");
		params.put("username", username);
		params.put("public", publicOrNot ? "yes" : "no");
		if(geoTagOrNot)
			params.put("tags", "");	//TODO - add some geo tag information here
		params.put("key", developerKey);
		params.put("url", url);
		params.put("verify_url", getVerifyURLForYfrog());

		kr.pe.meinside.android.http.HttpUtility.SimpleHttpResponse response = HttpUtility.getInstance().post("http://yfrog.com/api/upload", null, params);
		if(response != null && response.getHttpStatusCode() == 200)
		{
			return extractMediaUrlFromYfrogResponse(response.getHttpResponseBodyAsString());
		}
		return null;
	}
	
	private String extractMediaUrlFromYfrogResponse(String xmlResponse)
	{
		/*
		 * <?xml version="1.0" encoding="UTF-8"?>
		 * <rsp stat="ok">
		 * 	<mediaid>xyz0123</mediaid>
		 * 	<mediaurl>http://yfrog.com/xyz0123</mediaurl>
		 * </rsp>
		 */
		String mediaUrl = null;
		
		Document doc = XMLDOMParser.getInstance().parseString(xmlResponse);
		NodeList nodes = doc.getElementsByTagName("rsp");
		if(nodes != null)
		{
			Node stat = nodes.item(0).getAttributes().getNamedItem("stat");

			if(stat.getNodeValue().compareTo("ok") == 0)
			{
				nodes = doc.getElementsByTagName("mediaurl");
				if(nodes != null)
					mediaUrl = nodes.item(0).getFirstChild().getNodeValue();
			}
		}
		
		return mediaUrl;
	}
}
