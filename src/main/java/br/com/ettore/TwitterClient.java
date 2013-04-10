package br.com.ettore;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

public class TwitterClient extends Application {
	
	public static final String TAG = "TwitterClient";

	public static final String USER_TOKEN = "user_token";
	public static final String USER_SECRET = "user_secret";
	public static final String REQUEST_TOKEN = "request_token";
	public static final String REQUEST_SECRET = "request_secret";

	public static final String PREFS = "MyPrefsFile";
	
	Twitter twitter;

	SharedPreferences settings;
	SharedPreferences.Editor editor;
	
	public void onCreate() {
		super.onCreate();
		
		twitter = new Twitter();		
		settings = getSharedPreferences(TwitterClient.PREFS, Context.MODE_PRIVATE);
		editor = settings.edit();
		
		saveRequestInformation(settings, twitter.getConsumerToken(), twitter.getConsumerTokenSecret());
		
		Log.d(TAG, "onCreate");
	}

	private void saveRequestInformation(SharedPreferences settings,
			String token, String secret) {
		if (token == null) {
			editor.remove(REQUEST_TOKEN);
		} else {
			editor.putString(REQUEST_TOKEN, token);
		}
		if (secret == null) {
			editor.remove(REQUEST_SECRET);
		} else {
			editor.putString(REQUEST_SECRET, secret);
		}
		editor.commit();
	}

	private void saveAuthInformation(SharedPreferences settings,
			String token, String secret) {
		if (token == null) {
			editor.remove(USER_TOKEN);
		} else {
			editor.putString(USER_TOKEN, token);
		}
		if (secret == null) {
			editor.remove(USER_SECRET);
		} else {
			editor.putString(USER_SECRET, secret);
		}
		editor.commit();
	}
	
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null)
			return false;
		return true;
	}

	public boolean signIn(String otoken, String verifier) {
		String token = settings.getString(REQUEST_TOKEN, null);
		String secret = settings.getString(REQUEST_SECRET, null);	
		
		Log.d(TAG, "token: " + token);
		Log.d(TAG, "secret: " + secret);
		Log.d(TAG, "otoken: " + otoken);
		Log.d(TAG, "verifier: " + verifier);
		
		if (!twitter.signIn(token, secret, otoken, verifier))
			return false;
		
		saveAuthInformation(settings, twitter.getConsumerToken(), twitter.getConsumerTokenSecret());
		saveRequestInformation(settings, null, null);
		
		editor.putString("screenName", this.getScreenName());		
		editor.commit();
		
		return true;
	}
	
	public void signOut() {
		saveAuthInformation(settings, null, null);		
		editor.remove("userScreenName");	
		twitter.signOut();
	}

	public boolean isAuthorized() {
		return twitter.isAuthorized();
	}
	
	public Uri getAuthUrl() {
		return twitter.getAuthUrl();
	}

	public Uri getCallbackUri() {
		return Twitter.CALLBACK_URI;
	}
	
	public String getScreenName() {
		String accountSettings = twitter.getAccountSettings();

		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(accountSettings);
			return obj.get("screen_name").toString();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		return null;
	}

	public ArrayList<Tweet> getHomeTimeline() {

		String homeTimeline = twitter.getHomeTimeline();

		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(homeTimeline);
			JSONArray jarray = (JSONArray) obj;
			
			ArrayList<Tweet> tweets = new ArrayList<Tweet>();
			Tweet tempTweet;
			Iterator<JSONObject> iterator = jarray.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonTweet = iterator.next();
				JSONObject jsonUser = (JSONObject) jsonTweet.get("user");		
				
				tempTweet = new Tweet();
				tempTweet.setId(Long.parseLong(jsonTweet.get("id_str").toString()));
				tempTweet.setCreatedAt(jsonTweet.get("created_at").toString());
				tempTweet.setText(jsonTweet.get("text").toString());		
				tempTweet.setUserId(Long.parseLong(jsonUser.get("id_str").toString()));		
				tempTweet.setUserName(jsonUser.get("name").toString());
				tempTweet.setUserScreenName(jsonUser.get("screen_name").toString());
				tempTweet.setProfileImageUrl(jsonUser.get("profile_image_url").toString());
				
				tweets.add(tempTweet);
			}
			return tweets;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public ArrayList<Tweet> getUserTimeline(String userScreenName) {

		String homeTimeline = twitter.getUserTimeline(userScreenName);

		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(homeTimeline);
			JSONArray jarray = (JSONArray) obj;
			
			ArrayList<Tweet> tweets = new ArrayList<Tweet>();
			Tweet tempTweet;
			Iterator<JSONObject> iterator = jarray.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonTweet = iterator.next();
				JSONObject jsonUser = (JSONObject) jsonTweet.get("user");		
				
				tempTweet = new Tweet();
				tempTweet.setId(Long.parseLong(jsonTweet.get("id_str").toString()));
				tempTweet.setCreatedAt(jsonTweet.get("created_at").toString());
				tempTweet.setText(jsonTweet.get("text").toString());		
				tempTweet.setUserId(Long.parseLong(jsonUser.get("id_str").toString()));		
				tempTweet.setUserName(jsonUser.get("name").toString());
				tempTweet.setUserScreenName(jsonUser.get("screen_name").toString());
				tempTweet.setProfileImageUrl(jsonUser.get("profile_image_url").toString());
				
				tweets.add(tempTweet);
			}
			return tweets;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public ArrayList<Tweet> getSearchResults(String query) {

		String searchResults = twitter.getSearchResults(query);

		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(searchResults);
			JSONObject jobject = (JSONObject) obj;
			
			JSONArray jarray = (JSONArray) jobject.get("statuses");	
			
			ArrayList<Tweet> tweets = new ArrayList<Tweet>();
			Tweet tempTweet;
			Iterator<JSONObject> iterator = jarray.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonTweet = iterator.next();
				JSONObject jsonUser = (JSONObject) jsonTweet.get("user");		
				
				tempTweet = new Tweet();
				tempTweet.setId(Long.parseLong(jsonTweet.get("id_str").toString()));
				tempTweet.setCreatedAt(jsonTweet.get("created_at").toString());
				tempTweet.setText(jsonTweet.get("text").toString());		
				tempTweet.setUserId(Long.parseLong(jsonUser.get("id_str").toString()));		
				tempTweet.setUserName(jsonUser.get("name").toString());
				tempTweet.setUserScreenName(jsonUser.get("screen_name").toString());
				tempTweet.setProfileImageUrl(jsonUser.get("profile_image_url").toString());

				tweets.add(tempTweet);
			}
			return tweets;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}
	
	public boolean retweet(long id) {
		if (twitter.retweet(id) == 200)
			return true;
		return false;
	}

	public boolean update(String status) {
		if (twitter.update(status) == 200)
			return true;
		return false;
	}
}
