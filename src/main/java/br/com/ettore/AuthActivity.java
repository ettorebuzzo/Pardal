package br.com.ettore;

import oauth.signpost.OAuth;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class AuthActivity extends Activity {
	
	TwitterClient twitterClient;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		twitterClient = ((TwitterClient) getApplication());
		
		Intent i = this.getIntent();
		if (i.getData() == null) {	
			this.startActivity(new Intent(Intent.ACTION_VIEW, twitterClient.getAuthUrl())); 
		}
		
		Log.d("AuthActivity", "onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();

		Uri uri = getIntent().getData();
		if (uri != null && twitterClient.getCallbackUri().getScheme().equals(uri.getScheme())) {
			
			String otoken = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
			String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
			twitterClient.signIn(otoken, verifier);

			Intent i = new Intent(this, TimelineActivity.class);
			startActivity(i);
			finish();
		}
		
		Log.d("AuthActivity", "onResume");
	}	
}
