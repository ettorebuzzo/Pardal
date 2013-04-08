package br.com.ettore;

import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TimelineAdapter extends BaseAdapter {

	private List<Tweet> tweets;
	private LayoutInflater mInflater;

	public TimelineAdapter(Context context, List<Tweet> tweets) {
		mInflater = LayoutInflater.from(context);
		this.tweets = tweets;
	}

	@Override
	public int getCount() {
		return tweets.size();
	}

	@Override
	public Object getItem(int position) {
		return tweets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return tweets.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.line_tweet, null);

		TextView textUserName = (TextView) v.findViewById(R.id.textUserName);
		TextView textScreenName = (TextView) v.findViewById(R.id.textScreenName);
		TextView textText = (TextView) v.findViewById(R.id.textText);
		ImageView imageProfile = (ImageView) v.findViewById(R.id.imageProfile);

		Tweet t = tweets.get(position);
		textUserName.setText(t.getUserName());
		textScreenName.setText("@" + t.getUserScreenName());
		textText.setText(t.getText());

		try {
			URL url = new URL(t.getProfileImageUrl());
			Bitmap bmp = BitmapFactory.decodeStream(url.openConnection()
					.getInputStream());
			imageProfile.setImageBitmap(bmp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return v;
	}
}