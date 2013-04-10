package br.com.ettore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import android.net.Uri;
import android.util.Log;

public class Twitter {
	private static final String TAG = "Twitter";

	// Application keys
	private static final String CONSUMER_KEY = "4ZQ9WODvxYUgYISDb8F0A";
	private static final String CONSUMER_SECRET = "rtsCD4KhhNbnI7sgrXRzoPn648DOmOQx3o9QaFr93M";

	// Twitter URLs
	public static final String TWITTER_REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

	public static final String USER_TOKEN = "user_token";
	public static final String USER_SECRET = "user_secret";
	public static final String REQUEST_TOKEN = "request_token";
	public static final String REQUEST_SECRET = "request_secret";

	public static final Uri CALLBACK_URI = Uri.parse("Pardal://connect");

	private static OAuthConsumer consumer;
	private static OAuthProvider provider;

	private Uri authUrl;
 
	public Twitter() {
		consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		provider = new DefaultOAuthProvider(TWITTER_REQUEST_TOKEN_URL,
				TWITTER_ACCESS_TOKEN_URL, TWITTER_AUTHORIZE_URL);

		provider.setOAuth10a(true);

		try {
			authUrl = Uri.parse(provider.retrieveRequestToken(consumer,	CALLBACK_URI.toString()));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		
		Log.d("TwitterClient", "authUrl: " + authUrl);
		Log.d("TwitterClient", "CALLBACK_URI: " + CALLBACK_URI.toString());
	}

	public boolean signIn(String token, String secret, String otoken, String verifier) {
		try {
			if (!(token == null || secret == null))
				consumer.setTokenWithSecret(token, secret);
			
			if (!otoken.equals(consumer.getToken()))
				return false;
			
			provider.retrieveAccessToken(consumer, verifier);
			
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		return false;
	}
	
	public void signOut() {
		consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		provider = new DefaultOAuthProvider(TWITTER_REQUEST_TOKEN_URL,
				TWITTER_ACCESS_TOKEN_URL, TWITTER_AUTHORIZE_URL);
	}

	public Uri getAuthUrl() {
		return authUrl;
	}
	
	public String getConsumerToken() {
		return consumer.getToken();
	}
	
	public String getConsumerTokenSecret() {
		return consumer.getTokenSecret();
	}

	public boolean isAuthorized() {
		URL url;
		try {
			url = new URL(
					"https://api.twitter.com/1.1/account/settings.json");
			HttpURLConnection request = (HttpURLConnection) url
					.openConnection();
			consumer.sign(request);
			request.connect();

			if (request.getResponseCode() == 200)
				return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return false;
	}
	
	
	//TODO Alterar nome dessa função
	private String outputBuilder(InputStream stream) {
		try {
			StringBuilder out = new StringBuilder();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(stream));
			for (String line = br.readLine(); line != null; line = br
					.readLine())
				out.append(line);
			br.close();
			return out.toString();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}
	
	public String getAccountSettings() {
		try {
			URL url = new URL(
					"https://api.twitter.com/1.1/account/settings.json");
			HttpURLConnection request = (HttpURLConnection) url
					.openConnection();

			consumer.sign(request);
			request.connect();

			return outputBuilder(request.getInputStream());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public String getHomeTimeline() {
		try {
			URL url = new URL(
					"https://api.twitter.com/1.1/statuses/home_timeline.json");
			HttpURLConnection request = (HttpURLConnection) url
					.openConnection();

			consumer.sign(request);
			request.connect();

			return outputBuilder(request.getInputStream());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public int retweet(long id){
		try {
		URL url = new URL(
				"https://api.twitter.com/1.1/statuses/retweet/"+ id +".json");
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.setRequestMethod("POST");

		consumer.sign(request);
		request.connect();

		return request.getResponseCode();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return 0;
	}

	public int update(String status) {
		try {
			status = oauth.signpost.OAuth.percentEncode(status);
			URL url = new URL(
					"https://api.twitter.com/1.1/statuses/update.json?status="
							+ status + "&display_coordinates=false");
			HttpURLConnection request = (HttpURLConnection) url
					.openConnection();
			request.setRequestMethod("POST");

			consumer.sign(request);
			request.connect();

			return request.getResponseCode();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return 0;
	}

	public String getUserTimeline(String userScreenName) {
		try {
			URL url = new URL(
					"https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + userScreenName);
			HttpURLConnection request = (HttpURLConnection) url
					.openConnection();

			consumer.sign(request);
			request.connect();

			return outputBuilder(request.getInputStream());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public String getSearchResults(String query) {
		try {
			URL url = new URL(
					"https://api.twitter.com/1.1/search/tweets.json?q=" + query);
			HttpURLConnection request = (HttpURLConnection) url
					.openConnection();

			consumer.sign(request);
			request.connect();

			return outputBuilder(request.getInputStream());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}
}
