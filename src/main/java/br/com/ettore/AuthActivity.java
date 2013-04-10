package br.com.ettore;

import oauth.signpost.OAuth;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AuthActivity extends Activity {

	TwitterClient twitterClient;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		twitterClient = ((TwitterClient) getApplication());

		Uri authUrl = twitterClient.getAuthUrl();

		Intent i = this.getIntent();
		if (i.getData() == null) {
			if (authUrl != null) {
				Log.d("getAuthUrl", authUrl.toString());
				this.startActivity(new Intent(Intent.ACTION_VIEW, authUrl));
			} else {
				String text = "Connection failed. Try again later...";
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG)
						.show();

				i = new Intent(this, LoginActivity.class);
				startActivity(i);
				finish();
			}
		}

		Log.d("AuthActivity", "onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();

		Uri uri = getIntent().getData();
		if (uri != null
				&& twitterClient.getCallbackUri().getScheme()
						.equals(uri.getScheme())) {

			String otoken = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
			String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

			if (twitterClient.signIn(otoken, verifier)) {
				Intent i = new Intent(this, TimelineActivity.class);
				startActivity(i);
				finish();
			} else {
				String text = "Sign in failed. Try again later...";
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG)
						.show();

				Intent i = new Intent(this, LoginActivity.class);
				startActivity(i);
				finish();
			}

		}

		Log.d("AuthActivity", "onResume");
	}
}
