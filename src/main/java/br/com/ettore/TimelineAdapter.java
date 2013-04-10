package br.com.ettore;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TimelineAdapter extends ArrayAdapter<Tweet> {
	private final Activity context;
	private final ArrayList<Tweet> tweets;
	private final ImageDownloader imageDownloader = new ImageDownloader();

	static class ViewHolder {
		public TextView textText, textUserName;
		public ImageView imageProfile;
	}

	public TimelineAdapter(Activity context, ArrayList<Tweet> tweets) {
		super(context, R.layout.line_tweet, tweets);
		this.context = context;
		this.tweets = tweets;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.line_tweet, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.textText = (TextView) rowView
					.findViewById(R.id.textText);
			viewHolder.textUserName = (TextView) rowView
					.findViewById(R.id.textUserName);
			viewHolder.imageProfile = (ImageView) rowView
					.findViewById(R.id.imageProfile);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		Tweet t = tweets.get(position);
		holder.textText.setText(t.getText());
		holder.textUserName.setText(t.getUserName());
		//if (t.getProfileImage() != null)
		//	holder.imageProfile.setImageBitmap(t.getProfileImage());
		
		imageDownloader.download(t.getProfileImageUrl(), (ImageView) holder.imageProfile);

		return rowView;
	}
}