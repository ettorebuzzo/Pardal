package br.com.ettore;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends Activity implements OnItemClickListener,
		OnClickListener {

	public static final String TAG = "TimelineActivity";

	private TextView textScreenName;
	private ImageButton imageButtonHome, imageButtonCompose, imageButtonSearch,
			imageButtonProfile;
	private ListView listTimeline;
	private TimelineAdapter adapter;

	ArrayList<Tweet> tweets;

	private TwitterClient twitterClient;

	SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		twitterClient = ((TwitterClient) getApplication());
		settings = getSharedPreferences(TwitterClient.PREFS,
				Context.MODE_PRIVATE);

		textScreenName = (TextView) findViewById(R.id.textScreenName);
		listTimeline = (ListView) findViewById(R.id.listTimeline);
		imageButtonHome = (ImageButton) findViewById(R.id.imageButtonHome);
		imageButtonCompose = (ImageButton) findViewById(R.id.imageButtonCompose);
		imageButtonSearch = (ImageButton) findViewById(R.id.imageButtonSearch);
		imageButtonProfile = (ImageButton) findViewById(R.id.imageButtonProfile);

		textScreenName.setText("Start");

		imageButtonHome.setOnClickListener(this);
		imageButtonCompose.setOnClickListener(this);
		imageButtonSearch.setOnClickListener(this);
		imageButtonProfile.setOnClickListener(this);
	}

	public void onResume() {
		super.onResume();

		Log.d(TAG, "UpdateTimeline()");
		new UpdateTimeline().execute("getHomeTimeline");

		Log.d(TAG, "onResume");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_timeline, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuSignOut:
			twitterClient.signOut();
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			return true;
		case R.id.menuRefresh:
			listTimeline.setAdapter(null);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {

		final TimelineAdapter adapter = (TimelineAdapter) parent.getAdapter();

		String tweetUserScreenName = ((Tweet) adapter.getItem(position))
				.getUserScreenName();
		String[] options = { "Retweet", "@" + tweetUserScreenName };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(options, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					long id = ((Tweet) adapter.getItem(position)).getId();
					twitterClient.retweet(id);
					break;
				case 1:
					String tweetUserScreenName = ((Tweet) adapter
							.getItem(position)).getUserScreenName();
					new UpdateTimeline().execute("getUserTimeline",
							tweetUserScreenName);
					textScreenName.setText("@" + tweetUserScreenName);
				}
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onClick(View v) {
		if (v == imageButtonHome) {
			new UpdateTimeline().execute("getHomeTimeline");
			textScreenName.setText("Start");
		}
		if (v == imageButtonCompose) {
			Intent i = new Intent(this, ComposeActivity.class);
			startActivity(i);
		}
		if (v == imageButtonSearch) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Search");

			final EditText query = new EditText(this);
			query.setHint("search terms");
			alert.setView(query);

			alert.setPositiveButton("Search",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String search = query.getText().toString();
							if (search.length() > 0) {
								textScreenName.setText(search);
								new UpdateTimeline().execute(
										"getSearchResults", search);
							} else {
								String text = "Search can't be empty.";
								Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG)
										.show();
							}
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
						}
					});

			alert.show();
		}
		if (v == imageButtonProfile) {
			textScreenName
					.setText("@" + settings.getString("screenName", null));
			new UpdateTimeline().execute("getUserTimeline",
					settings.getString("screenName", null));
		}
	}

	private class UpdateTimeline extends AsyncTask<String, Integer, Boolean> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(TimelineActivity.this, "",
					"Loading...");
		}

		@Override
		protected Boolean doInBackground(String... params) {
			if (params == null) {
				return false;
			}
			try {
				if (params[0].equals("getHomeTimeline")) {
					tweets = twitterClient.getHomeTimeline();
				}
				if (params[0].equals("getUserTimeline")) {
					tweets = twitterClient.getUserTimeline(params[1]);
				}
				if (params[0].equals("getSearchResults")) {
					tweets = twitterClient.getSearchResults(params[1]);
				}
			} catch (Exception e) {
				Log.e("tag", e.getMessage());
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (tweets != null) {
				adapter = new TimelineAdapter(TimelineActivity.this, tweets);
				adapter.setNotifyOnChange(false);

				listTimeline.setAdapter(adapter);
				listTimeline.setOnItemClickListener(TimelineActivity.this);
				progressDialog.dismiss();
			} else {
				String text = "Connection failed. Try again later...";
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG)
						.show();
				progressDialog.dismiss();
			}
		}
	}
}
