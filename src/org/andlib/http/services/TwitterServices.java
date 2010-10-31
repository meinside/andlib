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

package org.andlib.http.services;

import java.util.HashMap;

import org.andlib.helpers.Logger;
import org.andlib.http.OAuthBase;
import org.andlib.http.SimpleHttpResponse;

/**
 * 
 * @author meinside@gmail.com
 * @since 10.01.14.
 * 
 * last update 10.10.28.
 *
 */
public class TwitterServices extends OAuthBase
{
	public static final String TWITTER_REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

	public static final int TWITTER_MESSAGE_MAX_LENGTH = 140;

	public static final String TWITTER_VERIFY_CREDENTIALS_URL = "http://api.twitter.com/1/account/verify_credentials.json";
	public static final String TWITTER_STATUSES_UPDATE_URL = "http://api.twitter.com/1/statuses/update.json";
	public static final String TWITTER_STATUSES_RETWEET_URL = "http://api.twitter.com/1/statuses/retweet/%s.json";
	public static final String TWITTER_FRIENDSHIP_CHECK_URL = "http://api.twitter.com/1/friendships/exists.json";
	public static final String TWITTER_FOLLOW_URL = "http://api.twitter.com/1/friendships/create.json";
	public static final String TWITTER_UNFOLLOW_URL = "http://api.twitter.com/1/friendships/destroy.json";
	public static final String TWITTER_DIRECT_MESSAGE_WRITE_URL = "http://api.twitter.com/1/direct_messages/new.json";
	
	private String screenName = null;

	/**
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 */
	public TwitterServices(String consumerKey, String consumerSecret)
	{
		super(consumerKey, consumerSecret, TWITTER_REQUEST_TOKEN_URL, TWITTER_ACCESS_TOKEN_URL, TWITTER_AUTHORIZE_URL);
	}

	/**
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param accessToken
	 * @param accessTokenSecret
	 * @param screenName
	 */
	public TwitterServices(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret, String screenName)
	{
		super(consumerKey, consumerSecret, TWITTER_REQUEST_TOKEN_URL, TWITTER_ACCESS_TOKEN_URL, TWITTER_AUTHORIZE_URL, accessToken, accessTokenSecret);
		this.screenName = screenName;
	}

	/* (non-Javadoc)
	 * @see org.andlib.http.OAuthBase#retrieveValuesAfterAuthorization(java.util.HashMap)
	 */
	@Override
	protected void retrieveValuesAfterAuthorization(HashMap<String, String> values)
	{
		super.retrieveValuesAfterAuthorization(values);
		this.screenName = values.get("screen_name");
	}

	/**
	 * 
	 * @return screen name of user
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

	/**
	 * http://dev.twitter.com/doc/get/account/verify_credentials
	 * 
	 * @return json result of verify_credentials
	 */
	public SimpleHttpResponse verifyCredentials()
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		return this.get(TWITTER_VERIFY_CREDENTIALS_URL, null);
	}

	/**
	 * http://dev.twitter.com/doc/post/statuses/update
	 * 
	 * @param status status text
	 * @param inReplyToStatusId existing status' id that this update replies to (null if none)
	 * @param latitude -90.0 ~ +90.0 (null if none)
	 * @param longitude -180.0 ~ +180.0 (null if none)
	 * @param placeId place id that this update will be attached to (nil if none)
	 * @param displayCoordinate display coordinate or not
	 * @return json result of status update
	 */
	public SimpleHttpResponse updateStatus(String status, String inReplyToStatusId, String latitude, String longitude, String placeId, boolean displayCoordinate)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		if(status.length() > TWITTER_MESSAGE_MAX_LENGTH)
		{
			Logger.i("status is too long (" + status.length() + ")");
			return null;
		}
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		if(status != null)
			params.put("status", status);
		if(inReplyToStatusId != null)
			params.put("in_reply_to_status_id", inReplyToStatusId);
		if(latitude != null)
			params.put("lat", latitude);
		if(longitude != null)
			params.put("long", longitude);
		if(placeId != null)
			params.put("place_id", placeId);
		if(!displayCoordinate)
			params.put("display_coordinates", "false");

		return this.post(TWITTER_STATUSES_UPDATE_URL, params);
	}

	/**
	 * http://dev.twitter.com/doc/post/statuses/retweet/:id
	 * 
	 * @param statusId
	 * @return json result of retweet
	 */
	public SimpleHttpResponse retweet(String statusId)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		//TODO
		return null;
	}

	/**
	 * http://dev.twitter.com/doc/get/friendships/exists
	 * 
	 * @param user
	 * @return whether following given user or not
	 */
	public boolean isFollowingUser(String user)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return false;
		}

		//TODO
		return false;
	}
	/**
	 * http://dev.twitter.com/doc/get/friendships/exists
	 * 
	 * @param user
	 * @return whether being followed or not by given user
	 */
	public boolean isFollowedByUser(String user)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return false;
		}

		//TODO
		return false;
	}

	/**
	 * http://dev.twitter.com/doc/post/friendships/create
	 * 
	 * @param userId
	 * @return json result of following (if already following, HTTP 403 will be returned)
	 */
	public SimpleHttpResponse followUserId(String userId)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		//TODO
		return null;
	}
	/**
	 * http://dev.twitter.com/doc/post/friendships/create
	 * 
	 * @param screenName
	 * @return json result of following (if already following, HTTP 403 will be returned)
	 */
	public SimpleHttpResponse followUser(String screenName)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		//TODO
		return null;
	}

	/**
	 * http://dev.twitter.com/doc/post/friendships/destroy
	 * 
	 * @param userId
	 * @return json result of unfollowing
	 */
	public SimpleHttpResponse unfollowUserId(String userId)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		//TODO
		return null;
	}
	/**
	 * http://dev.twitter.com/doc/post/friendships/destroy
	 * 
	 * @param screenName
	 * @return json result of unfollowing
	 */
	public SimpleHttpResponse unfollowUser(String screenName)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		//TODO
		return null;
	}

	/**
	 * http://dev.twitter.com/doc/post/direct_messages/new
	 * 
	 * @param message
	 * @param userId
	 * @return json result of sending DM
	 */
	public SimpleHttpResponse sendDirectMessageToUserId(String message, String userId)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		//TODO
		return null;
	}
	/**
	 * http://dev.twitter.com/doc/post/direct_messages/new
	 * 
	 * @param message
	 * @param screenName
	 * @return json result of sending DM
	 */
	public SimpleHttpResponse sendDirectMessageToUser(String message, String screenName)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		//TODO
		return null;
	}

	//TODO - implement more twitter apis
}
