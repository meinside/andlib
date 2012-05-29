package org.andlib.http.services;

import java.io.File;
import java.util.HashMap;

import org.andlib.helpers.Logger;
import org.andlib.http.OAuthBase;

import android.os.Handler;

/**
 * 
 * @author meinside@gmail.com
 * @since 10.01.14.
 * 
 * last update 12.05.29.
 *
 */
public class TwitterServices extends OAuthBase
{
	public static final String TWITTER_REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

	public static final int TWITTER_MESSAGE_MAX_LENGTH = 140;

	public static final String TWITTER_VERIFY_CREDENTIALS_URL = "https://api.twitter.com/1/account/verify_credentials.json";
	public static final String TWITTER_STATUSES_UPDATE_URL = "https://api.twitter.com/1/statuses/update.json";
	public static final String TWITTER_STATUSES_UPDATE_WITH_MEDIA_URL = "https://upload.twitter.com/1/statuses/update_with_media.json";
	public static final String TWITTER_STATUSES_RETWEET_URL = "https://api.twitter.com/1/statuses/retweet/%s.json";
	public static final String TWITTER_FRIENDSHIP_CHECK_URL = "https://api.twitter.com/1/friendships/exists.json";
	public static final String TWITTER_FOLLOW_URL = "https://api.twitter.com/1/friendships/create.json";
	public static final String TWITTER_UNFOLLOW_URL = "https://api.twitter.com/1/friendships/destroy.json";
	public static final String TWITTER_DIRECT_MESSAGE_WRITE_URL = "https://api.twitter.com/1/direct_messages/new.json";
	
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
	 * @param resultHandler
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String verifyCredentials(Handler resultHandler)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		return this.getAsync(resultHandler, TWITTER_VERIFY_CREDENTIALS_URL, null);
	}

	/**
	 * http://dev.twitter.com/doc/post/statuses/update
	 * 
	 * @param resultHandler
	 * @param status status text
	 * @param inReplyToStatusId existing status' id that this update replies to (null if none)
	 * @param latitude -90.0 ~ +90.0 (null if none)
	 * @param longitude -180.0 ~ +180.0 (null if none)
	 * @param placeId place id that this update will be attached to (nil if none)
	 * @param displayCoordinate display coordinate or not
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String updateStatus(Handler resultHandler, String status, String inReplyToStatusId, String latitude, String longitude, String placeId, boolean displayCoordinate)
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

		return this.postAsync(resultHandler, TWITTER_STATUSES_UPDATE_URL, params);
	}

	/**
	 * https://dev.twitter.com/docs/api/1/post/statuses/update_with_media
	 * 
	 * @param resultHandler
	 * @param status
	 * @param media
	 * @param inReplyToStatusId
	 * @param latitude
	 * @param longitude
	 * @param placeId
	 * @param displayCoordinate
	 * @return
	 */
	public String updateStatusWithMedia(Handler resultHandler, String status, File media, String inReplyToStatusId, String latitude, String longitude, String placeId, boolean displayCoordinate)
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
		if(media != null)
			params.put("media", media);
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

		return this.postAsync(resultHandler, TWITTER_STATUSES_UPDATE_WITH_MEDIA_URL, params);
	}

	/**
	 * http://dev.twitter.com/doc/post/statuses/retweet/:id
	 * 
	 * @param resultHandler
	 * @param statusId
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String retweet(Handler resultHandler, String statusId)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		return this.postAsync(resultHandler, String.format(TWITTER_STATUSES_RETWEET_URL, statusId), null);
	}

	/**
	 * http://dev.twitter.com/doc/get/friendships/exists
	 * 
	 * @param resultHandler
	 * @param user
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String isFollowingUser(Handler resultHandler, String user)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_a", screenName);
		params.put("user_b", user);

		return this.getAsync(resultHandler, TWITTER_FRIENDSHIP_CHECK_URL, params);
	}

	/**
	 * http://dev.twitter.com/doc/get/friendships/exists
	 * 
	 * @param resultHandler
	 * @param user
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String isFollowedByUser(Handler resultHandler, String user)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_a", user);
		params.put("user_b", screenName);

		return this.getAsync(resultHandler, TWITTER_FRIENDSHIP_CHECK_URL, params);
	}

	/**
	 * http://dev.twitter.com/doc/post/friendships/create
	 * 
	 * @param resultHandler
	 * @param userId
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String followUserId(Handler resultHandler, String userId)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", userId);

		return this.postAsync(resultHandler, TWITTER_FOLLOW_URL, params);
	}

	/**
	 * http://dev.twitter.com/doc/post/friendships/create
	 * 
	 * @param resultHandler
	 * @param screenName
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String followUser(Handler resultHandler, String screenName)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("screen_name", screenName);

		return this.postAsync(resultHandler, TWITTER_FOLLOW_URL, params);
	}

	/**
	 * http://dev.twitter.com/doc/post/friendships/destroy
	 * 
	 * @param resultHandler
	 * @param userId
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String unfollowUserId(Handler resultHandler, String userId)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", userId);

		return this.postAsync(resultHandler, TWITTER_UNFOLLOW_URL, params);
	}

	/**
	 * http://dev.twitter.com/doc/post/friendships/destroy
	 * 
	 * @param resultHandler
	 * @param screenName
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String unfollowUser(Handler resultHandler, String screenName)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("screen_name", screenName);

		return this.postAsync(resultHandler, TWITTER_UNFOLLOW_URL, params);
	}

	/**
	 * http://dev.twitter.com/doc/post/direct_messages/new
	 * 
	 * @param resultHandler
	 * @param message
	 * @param userId
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String sendDirectMessageToUserId(Handler resultHandler, String message, String userId)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		if(message.length() > TWITTER_MESSAGE_MAX_LENGTH)
		{
			Logger.i("message is too long (" + message.length() + ")");
			return null;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", userId);
		params.put("text", message);

		return this.postAsync(resultHandler, TWITTER_DIRECT_MESSAGE_WRITE_URL, params);
	}

	/**
	 * http://dev.twitter.com/doc/post/direct_messages/new
	 * 
	 * @param resultHandler
	 * @param message
	 * @param screenName
	 * @return id of AsyncHttpTask that is assigned to this job
	 */
	public String sendDirectMessageToUser(Handler resultHandler, String message, String screenName)
	{
		if(!isAuthorized)
		{
			Logger.i("not authorized yet");
			return null;
		}

		if(message.length() > TWITTER_MESSAGE_MAX_LENGTH)
		{
			Logger.i("message is too long (" + message.length() + ")");
			return null;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("screen_name", screenName);
		params.put("text", message);

		return this.postAsync(resultHandler, TWITTER_DIRECT_MESSAGE_WRITE_URL, params);
	}

	//TODO - implement more twitter apis
}
