package br.com.ettore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeActivity extends Activity implements OnClickListener, TextWatcher {
	
	private static final String TAG = "ComposeActivity"; 
			
	private TextView textScreenName, textLimit;
	private Button buttonTweet;
	private EditText editTweet;

	private TwitterClient twitterClient;

	SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);

		twitterClient = ((TwitterClient) getApplication());
		settings = getSharedPreferences(TwitterClient.PREFS,
				Context.MODE_PRIVATE);

		textScreenName = (TextView) findViewById(R.id.textScreenName);
		textLimit = (TextView) findViewById(R.id.textLimit);
		buttonTweet = (Button) findViewById(R.id.buttonTweet);
		editTweet = (EditText) findViewById(R.id.editTweet);

		textScreenName.setText("What's happening?");
		
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(140);
		editTweet.setFilters(FilterArray);
		editTweet.addTextChangedListener(this);
	
		buttonTweet.setOnClickListener(this);
		
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onClick(View v) {
		if (v == buttonTweet) {
			String tweet = editTweet.getText().toString();
			if (twitterClient.update(tweet)) {
				finish();
			}
			else {
				String text = "Connection failed. Try again later...";
				Toast.makeText(getApplicationContext(), text,
						Toast.LENGTH_LONG).show();				
			}
		}
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		int limit = 140 - editTweet.getText().length();
		textLimit.setText(Integer.toString(limit));
	}

	@Override
	public void afterTextChanged(Editable arg0) {		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {		
	}

}
