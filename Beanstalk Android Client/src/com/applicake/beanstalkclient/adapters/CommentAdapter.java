package com.applicake.beanstalkclient.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.applicake.beanstalkclient.Comment;
import com.applicake.beanstalkclient.R;

public class CommentAdapter extends ArrayAdapter<Comment> {

	private Context context;
	private List<Comment> commentsArray;
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");
	private Spanned spannedText;

	public CommentAdapter(Context context, int textViewResourceId,
			List<Comment> commentsArray) {
		super(context, textViewResourceId);
		this.context = context;
		this.commentsArray = commentsArray;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.comment_list_entry, null);
		}

		Comment comment = commentsArray.get(position);

		if (comment != null) {
			TextView userNameTextView = (TextView) view.findViewById(R.id.userName);
			userNameTextView.setText(comment.getAuthorName());

			TextView dateTextView = (TextView) view.findViewById(R.id.commentDate);
			dateTextView.setText(DateUtils.getRelativeTimeSpanString(comment
					.getUpdatedAt().getTime()));

			TextView commentBodyTextView = (TextView) view.findViewById(R.id.commentBody);
			spannedText = Html.fromHtml(comment.getRenderedBody());
			commentBodyTextView.setText(spannedText, BufferType.SPANNABLE);

		}

		return view;
	}

}
