package br.com.ettore;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
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

public class TimelineActivity extends Activity implements OnItemClickListener, OnClickListener {

	private TextView textScreenName;
	private ImageButton imageButtonHome, imageButtonCompose, imageButtonSearch, imageButtonTrending;
	private ListView listTimeline;
	private TimelineAdapter adapter;

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
		imageButtonTrending = (ImageButton) findViewById(R.id.imageButtonTrending);

		textScreenName.setText("Start");
		
		imageButtonHome.setOnClickListener(this);		
		imageButtonCompose.setOnClickListener(this);		
		imageButtonSearch.setOnClickListener(this);		
		imageButtonTrending.setOnClickListener(this);				

		ArrayList<Tweet> tweets = twitterClient.getHomeTimeline();

		adapter = new TimelineAdapter(getApplicationContext(), tweets);
		listTimeline.setAdapter(adapter);
		listTimeline.setOnItemClickListener(this);
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

	public void onItemClick(AdapterView<?> parent, View view, final int position,
			long id) {
		
		final TimelineAdapter adapter = (TimelineAdapter) parent.getAdapter();
		
		String tweetUserScreenName = ((Tweet)adapter.getItem(position)).getUserScreenName();
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
					String tweetUserScreenName = ((Tweet)adapter.getItem(position)).getUserScreenName();
					ArrayList<Tweet> tweets = twitterClient.getUserTimeline(tweetUserScreenName);
					TimelineAdapter tempAdapter = new TimelineAdapter(getApplicationContext(), tweets);
					listTimeline.setAdapter(tempAdapter);
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
			ArrayList<Tweet> tweets = twitterClient.getHomeTimeline();
			adapter = new TimelineAdapter(getApplicationContext(), tweets);
			listTimeline.setAdapter(adapter);
			textScreenName.setText("Start");
		}		
		if (v == imageButtonCompose) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("What's happening?");

			final EditText status = new EditText(this);
			status.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

			InputFilter[] FilterArray = new InputFilter[1];
			FilterArray[0] = new InputFilter.LengthFilter(140);
			status.setFilters(FilterArray);

			alert.setView(status);

			alert.setPositiveButton("Tweet",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							twitterClient.update(status.getText().toString());
							
							ArrayList<Tweet> tweets = twitterClient
									.getHomeTimeline();
							adapter = new TimelineAdapter(
									getApplicationContext(), tweets);
							listTimeline.setAdapter(adapter);
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
							ArrayList<Tweet> tweets = twitterClient
									.getSearchResults(query.getText().toString());
							adapter = new TimelineAdapter(
									getApplicationContext(), tweets);
							listTimeline.setAdapter(adapter);
							textScreenName.setText(query.getText().toString());
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
		if (v == imageButtonTrending) {
		}				
	}

}
