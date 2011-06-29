package com.applicake.beanstalkclient.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.applicake.beanstalkclient.Comment;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.utils.GravatarDowloader;

public class CommentAdapter extends ArrayAdapter<Comment> {

	private List<Comment> commentsArray;
	private Spanned spannedText;
	private LayoutInflater mInflater;
	static int rowColorSwapper;

	public CommentAdapter(Context context, int textViewResourceId,
			List<Comment> commentsArray) {
		super(context, textViewResourceId, commentsArray);
		this.commentsArray = commentsArray;
		rowColorSwapper = 0;
		mInflater = LayoutInflater.from(context);

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			view = mInflater.inflate(R.layout.comment_list_entry, null);
		}

		Comment comment = commentsArray.get(position);

		if (comment != null) {
			ImageView userGravatar = (ImageView) view.findViewById(R.id.userGravatar);
			GravatarDowloader.getInstance().download(comment.getAuthorEmail(),
					userGravatar);

			TextView userNameTextView = (TextView) view.findViewById(R.id.userName);
			userNameTextView.setText(comment.getAuthorName());

			TextView dateTextView = (TextView) view.findViewById(R.id.commentDate);
			dateTextView.setText(DateUtils.getRelativeTimeSpanString(comment
					.getUpdatedAt().getTime()));

			TextView commentBodyTextView = (TextView) view.findViewById(R.id.commentBody);
			spannedText = Html.fromHtml(comment.getRenderedBody());
			commentBodyTextView.setText(spannedText, BufferType.SPANNABLE);

		}

		if ((rowColorSwapper % 2) == 0) {
			view.getBackground().setLevel(0);
		} else {
			view.getBackground().setLevel(1);
		}
		rowColorSwapper++;

		return view;
	}

}
