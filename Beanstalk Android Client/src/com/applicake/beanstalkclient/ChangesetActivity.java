package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.applicake.beanstalkclient.adapters.ChangesAdapter;
import com.applicake.beanstalkclient.adapters.CommentAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpRetreiverException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChangesetActivity extends BeanstalkActivity implements OnClickListener {

	private ListView changesList;
	private Button changesetButton;
	private Button commentsButton;
	private ArrayList<Comment> commentArray = new ArrayList<Comment>();
	private ListView commentList;
	private CommentAdapter commentAdapter;
	private boolean commentListParsed;
	private int repoId;
	private String revisionId;
	private EditText newCommentBody;
	private Button submitButton;
	private Context mContext;

	// loading more comments
	public static final int NUMBER_OF_ENTRIES_PER_PAGE = 15;
	private boolean listMightHaveMoreItems = false;
	private int lastLoadedPage = 0;
	private View headerView;
	private ProgressBar headerProgressBar;
	private TextView headerBodyText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changeset_layout);
		mContext = this;
		Intent acitivyIntent = getIntent();

		commentListParsed = false;
		repoId = acitivyIntent.getIntExtra(Constants.COMMIT_REPOSITORY_ID, 0);
		revisionId = acitivyIntent.getStringExtra(Constants.COMMIT_REVISION_ID);

		((TextView) findViewById(R.id.userName)).setText(acitivyIntent
				.getStringExtra(Constants.COMMIT_USERNAME));
		((TextView) findViewById(R.id.commitMessage)).setText(acitivyIntent
				.getStringExtra(Constants.COMMIT_MESSAGE));

		changesetButton = (Button) findViewById(R.id.changesetButton);
		commentsButton = (Button) findViewById(R.id.commentsButton);
		changesetButton.setOnClickListener(this);
		commentsButton.setOnClickListener(this);

		// getting changed files list and changed dirs list and merging them
		// into one arraylist
		ArrayList<YamlEntry> changedFilesArray = acitivyIntent
				.getParcelableArrayListExtra(Constants.CHANGEDFILES_ARRAYLIST);
		ArrayList<YamlEntry> changedDirsArray = acitivyIntent
				.getParcelableArrayListExtra(Constants.CHANGEDDIRS_ARRAYLIST);
		ArrayList<YamlEntry> changesArray = new ArrayList<YamlEntry>(changedFilesArray);
		changesArray.addAll(changedDirsArray);

		// handle comments list and adapter
		commentList = (ListView) findViewById(R.id.commentsList);

		View footerView = ((LayoutInflater) getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comment_list_footer,
				null, false);
		commentList.addFooterView(footerView);

		
		commentAdapter = new CommentAdapter(this,
				R.layout.comment_list_entry, commentArray);

		// adding header view and assigning its elements
		headerView = ((LayoutInflater) getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comment_list_header,
				null, false);

		headerProgressBar = (ProgressBar) headerView
				.findViewById(R.id.commentsDownloadProgressBar);
		headerBodyText = (TextView) headerView.findViewById(R.id.refreshWidgetBody);
		headerView.setOnClickListener(this);
		commentList.addHeaderView(headerView);


		newCommentBody = (EditText) footerView.findViewById(R.id.newCommentBody);
		submitButton = (Button) footerView.findViewById(R.id.submitButton);
		submitButton.setOnClickListener(this);
		
		commentList.setAdapter(commentAdapter);

		// handle changes list and adapter
		ChangesAdapter changesAdapter = new ChangesAdapter(this,
				R.layout.changeset_list_entry, changesArray);

		changesList = (ListView) findViewById(R.id.changesList);
		changesList.setAdapter(changesAdapter);
		changesList.setEnabled(false);


		if (changesArray != null && !changesArray.isEmpty()) {
			changesAdapter.notifyDataSetChanged();
			changesAdapter.clear();

			for (int i = 0; i < changesArray.size(); i++) {
				changesAdapter.add(changesArray.get(i));
			}
		}

		changesAdapter.notifyDataSetChanged();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == changesetButton.getId()) {
			changesList.setVisibility(View.VISIBLE);
			commentList.setVisibility(View.GONE);

		} else if (v.getId() == headerView.getId()) {
			new ParseMoreCommentListTask().execute(String.valueOf(repoId), revisionId);

		} else if (v.getId() == commentsButton.getId()) {
			changesList.setVisibility(View.GONE);
			commentList.setVisibility(View.VISIBLE);
			if (!commentListParsed) {
				new ParseCommentListTask().execute(String.valueOf(repoId), revisionId);
				commentListParsed = true;
			}
		} else if (v.getId() == submitButton.getId()) {

			String newCommentBodyString = newCommentBody.getText().toString();
			if (newCommentBodyString.trim().length() == 0) {
				GUI.displayMonit(getApplicationContext(), "You must enter comment body");
			} else {
				new SendCommentTask().execute(newCommentBodyString);
			}

		}

	}

	public class ParseCommentListTask extends AsyncTask<String, Void, ArrayList<Comment>> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"downloading comments");

		}

		@Override
		protected ArrayList<Comment> doInBackground(String... params) {

			String repoId = params[0];
			String revision = params[1];

			HttpRetriever httpRetriever = new HttpRetriever();

			try {
				// getting first page of comments
				String commentsXml = httpRetriever.getCommentsListForRevisionXML(prefs,
						repoId, revision, 1);
				return XmlParser.parseCommentList(commentsXml);
			} catch (HttpRetreiverException e) {
				// TODO generate http parsing exception handling
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Comment> result) {
			progressDialog.cancel();
			commentArray = result;

			if (result.size() < NUMBER_OF_ENTRIES_PER_PAGE) {
				headerProgressBar.setVisibility(View.GONE);
				listMightHaveMoreItems = false;
			} else {
				listMightHaveMoreItems = true;
			}
			lastLoadedPage = 1;

			// commentArray = result;
			if (commentArray != null && !commentArray.isEmpty()) {
				for (int i = 0; i < commentArray.size(); i++) {
					commentAdapter.add(commentArray.get(i));
				}
			}
			commentAdapter.notifyDataSetChanged();

		}

	}

	public class ParseMoreCommentListTask extends
			AsyncTask<String, Void, ArrayList<Comment>> {

		@Override
		protected void onPreExecute() {
			headerProgressBar.setVisibility(View.VISIBLE);
			headerBodyText.setText("Downloading more comments...");
			headerView.setEnabled(false);
			headerView.setClickable(false);
		}

		@Override
		protected ArrayList<Comment> doInBackground(String... params) {

			String repoId = params[0];
			String revision = params[1];

			HttpRetriever httpRetriever = new HttpRetriever();

			try {

				// getting first page of comments
				String commentsXml = httpRetriever.getCommentsListForRevisionXML(prefs,
						repoId, revision, lastLoadedPage + 1);
				return XmlParser.parseCommentList(commentsXml);
			} catch (HttpRetreiverException e) {
				// TODO generate http parsing exception handling
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Comment> result) {

			if (result.size() < NUMBER_OF_ENTRIES_PER_PAGE)
				headerView.setVisibility(View.GONE);
			// listMightHaveMoreItems = false;
			// } else {
			// listMightHaveMoreItems = true;
			// }
			lastLoadedPage++;

			for (Comment comment : commentArray) {
				result.add(comment);
			}
			commentArray = result;

			if (commentArray != null && !commentArray.isEmpty()) {
				commentAdapter.clear();
				for (int i = 0; i < commentArray.size(); i++) {
					commentAdapter.add(commentArray.get(i));
				}
			}
			commentAdapter.notifyDataSetChanged();

		}

	}

	public class SendCommentTask extends AsyncTask<String, Void, String> {

		ProgressDialog progressDialog;
		String errorMessage = null;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"sending comment");
			super.onPreExecute();
		}

		protected String doInBackground(String... params) {

			String commentBody = params[0];
			XmlCreator xmlCreator = new XmlCreator();
			HttpSender httpSender = new HttpSender();
			try {
				String commentXml = xmlCreator.createCommentXML(revisionId, commentBody);
				return httpSender.sendCommentXML(prefs, commentXml,
						String.valueOf(repoId));

			} catch (IllegalArgumentException e) {
				errorMessage = e.getMessage();
				e.printStackTrace();
			} catch (IllegalStateException e) {
				errorMessage = e.getMessage();
				e.printStackTrace();
			} catch (IOException e) {
				errorMessage = e.getMessage();
				e.printStackTrace();
			} catch (HttpSenderException e) {
				errorMessage = e.getMessage();
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.cancel();
			if (result != null) {

				try {
					Comment comment = XmlParser.parseComment(result);
					commentArray.add(comment);
					commentAdapter.add(comment);
					commentAdapter.notifyDataSetChanged();

				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (errorMessage != null) {
				GUI.displayMonit(mContext, errorMessage);

			} else {
				GUI.displayMonit(mContext, "unexpected error");
			}

		}

	}

}
