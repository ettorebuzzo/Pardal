package br.com.ettore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	public static final String TAG = "LoginActivity";

	private Button buttonLogin;
	private TwitterClient twitterClient;

	SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);

		twitterClient = ((TwitterClient) getApplication());
		settings = getSharedPreferences(TwitterClient.PREFS,
				Context.MODE_PRIVATE);

		buttonLogin = (Button) findViewById(R.id.buttonLogin);

		buttonLogin.setOnClickListener(this);

		Log.d(TAG, "onCreate");
	}

	@Override
	public void onResume() {
		super.onResume();

		if (twitterClient.isAuthorized()) {
			Log.d(TAG, "Is authorized");

			buttonLogin.setText("Continue, " + settings.getString("screenName", null));
		} else {
			Log.d(TAG, "Isn't authorized");
			buttonLogin.setText("Sign in");
		}
		Log.d(TAG, "onResume");
	}

	@Override
	public void onClick(View v) {
		if (v == buttonLogin) {
			if (twitterClient.isAuthorized()) {
				if (twitterClient.isNetworkConnected()) {
					Log.d(TAG, "Starting TimelineActivity...");
					Intent i = new Intent(this, TimelineActivity.class);
					startActivity(i);
				} else {
					String text = "Connection failed. Try again later...";
					Toast.makeText(getApplicationContext(), text,
							Toast.LENGTH_LONG).show();
				}
			} else {
				Intent i = new Intent(this, AuthActivity.class);
				startActivity(i);
			}
		}
	}

}
