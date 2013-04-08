package br.com.ettore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener {
	
	private Button buttonLogin;
	private TextView textHello, textSignout;
	private TwitterClient twitterClient;
	
	SharedPreferences settings;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		twitterClient = ((TwitterClient) getApplication());		
		settings = getSharedPreferences(TwitterClient.PREFS, Context.MODE_PRIVATE);	
		
		textHello = (TextView) findViewById(R.id.textHello);
		textSignout = (TextView) findViewById(R.id.textSignout);		
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		
		buttonLogin.setOnClickListener(this);
		textSignout.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();

		if (twitterClient.isAuthorized()) {
			Log.d("LoginActivity", "Is authorized");
			
			textHello.setText("Hello, " + settings.getString("screenName", null) + "!");
			textSignout.setText("I'm not " + settings.getString("screenName", null));
			buttonLogin.setText("Continue");
			
			textHello.setVisibility(View.VISIBLE);
			textSignout.setVisibility(View.VISIBLE);
		}
		else {
			Log.d("LoginActivity", "Isn't authorized");	
			buttonLogin.setText("Sign in");		
			textHello.setVisibility(View.INVISIBLE);	
			textSignout.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == buttonLogin) {
			if (twitterClient.isAuthorized()) {
				Intent i = new Intent(this, TimelineActivity.class);
				startActivity(i);			
			}
			else {
				Intent i = new Intent(this, AuthActivity.class);
				startActivity(i);				
			}
		}		
		if (v == textSignout) {
			twitterClient.signOut();
			buttonLogin.setText("Sign in");		
			textHello.setVisibility(View.INVISIBLE);	
			textSignout.setVisibility(View.INVISIBLE);
		}		
	}

}
