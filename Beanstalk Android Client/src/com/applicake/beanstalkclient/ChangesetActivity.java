package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

import com.applicake.beanstalkclient.adapters.ChangesAdapter;
import com.applicake.beanstalkclient.adapters.CommentAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
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
	private ArrayList<Comment> commentsArray;
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
	private int lastLoadedPage = 0;
	private View footerRefreshButtonView;
	private ProgressBar footerRefreshProgressBar;
	private TextView footerRefreshBodyText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// loadinig initial data and layout
		setContentView(R.layout.changeset_layout);
		mContext = getApplicationContext();
		Intent acitivyIntent = getIntent();
		commentListParsed = false;
		repoId = acitivyIntent.getIntExtra(Constants.COMMIT_REPOSITORY_ID, 0);
		revisionId = acitivyIntent.getStringExtra(Constants.COMMIT_REVISION_ID);

		findViewById(R.id.colorLabel).getBackground().setLevel(
				acitivyIntent.getIntExtra(Constants.COMMIT_REPOSIOTRY_LABEL, 0));
		((TextView) findViewById(R.id.repoName)).setText(acitivyIntent
				.getStringExtra(Constants.COMMIT_REPOSIOTRY_NAME));
		((TextView) findViewById(R.id.userName)).setText(acitivyIntent
				.getStringExtra(Constants.COMMIT_USERNAME));
		((TextView) findViewById(R.id.commitMessage)).setText(acitivyIntent
				.getStringExtra(Constants.COMMIT_MESSAGE));

		changesetButton = (Button) findViewById(R.id.changesetButton);
		commentsButton = (Button) findViewById(R.id.commentsButton);
		changesetButton.setOnClickListener(this);
		commentsButton.setOnClickListener(this);

		changesetButton.setSelected(true);
		commentsButton.setSelected(false);

		// getting changed files list and changed dirs list and merging them
		// into one arraylist
		ArrayList<YamlEntry> changedFilesArray = acitivyIntent
				.getParcelableArrayListExtra(Constants.CHANGEDFILES_ARRAYLIST);
		ArrayList<YamlEntry> changedDirsArray = acitivyIntent
				.getParcelableArrayListExtra(Constants.CHANGEDDIRS_ARRAYLIST);
		ArrayList<YamlEntry> changesArray = new ArrayList<YamlEntry>(changedFilesArray);
		changesArray.addAll(changedDirsArray);

		// handle changes list and adapter
		changesList = (ListView) findViewById(R.id.changesList);

		ChangesAdapter changesAdapter = new ChangesAdapter(this,
				R.layout.changeset_list_entry, changesArray);

		changesList.setAdapter(changesAdapter);
		changesList.setEnabled(false);

		// handle comments list and adapter
		commentList = (ListView) findViewById(R.id.commentsList);
		commentsArray = new ArrayList<Comment>();
		commentAdapter = new CommentAdapter(mContext, R.layout.comment_list_entry,
				commentsArray);

		// adding refresh footer view and assigning its elements
		footerRefreshButtonView = ((LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.comment_list_footer_refresh, commentList, false);

		footerRefreshProgressBar = (ProgressBar) footerRefreshButtonView
				.findViewById(R.id.commentsDownloadProgressBar);
		footerRefreshBodyText = (TextView) footerRefreshButtonView
				.findViewById(R.id.refreshWidgetBody);
		footerRefreshButtonView.setOnClickListener(this);

		// adding input footer view with comment input box and assigning its
		// elements
		View footerCommentInputView = ((LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.comment_list_footer, commentList, false);

		newCommentBody = (EditText) footerCommentInputView
				.findViewById(R.id.newCommentBody);
		submitButton = (Button) footerCommentInputView.findViewById(R.id.submitButton);
		submitButton.setOnClickListener(this);

		commentList.addFooterView(footerRefreshButtonView);
		commentList.addFooterView(footerCommentInputView);
		commentList.setAdapter(commentAdapter);

		footerRefreshButtonView.setEnabled(false);
		footerRefreshButtonView.setClickable(false);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == changesetButton.getId()) {
			changesList.setVisibility(View.VISIBLE);
			commentList.setVisibility(View.GONE);
			changesetButton.setSelected(true);
			commentsButton.setSelected(false);

		} else if (v.getId() == footerRefreshButtonView.getId()) {
			new ParseMoreCommentListTask().execute(String.valueOf(repoId), revisionId);

		} else if (v.getId() == commentsButton.getId()) {
			changesList.setVisibility(View.GONE);
			commentList.setVisibility(View.VISIBLE);
			changesetButton.setSelected(false);
			commentsButton.setSelected(true);
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

		private boolean failed = false;
		private String failMessage;

		private String repoId;
		private String revision;

		@Override
		protected ArrayList<Comment> doInBackground(String... params) {

			repoId = params[0];
			revision = params[1];

			try {
				// getting first page of comments
				String commentsXml = HttpRetriever.getCommentsListForRevisionXML(prefs,
						repoId, revision, 1);
				return XmlParser.parseCommentList(commentsXml);

			} catch (UnsuccessfulServerResponseException e) {
				failMessage = "The target server failed to respond.";
			} catch (HttpConnectionErrorException e) {
				failMessage = "An error occured while trying to connect to Beanstalk server.";
			} catch (XMLParserException e) {
				failMessage = "An error occured while analysing server response";
				e.printStackTrace();
			}
			failed = true;
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Comment> result) {
			if (failed) {
				SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
						failMessage) {

					@Override
					public void retryAction() {
						new ParseCommentListTask().execute(repoId, revision);
					}

					@Override
					public void noRetryAction(DialogInterface dialog) {
						super.noRetryAction(dialog);

					}
				};

				builder.displayDialog();

			} else {

				commentsArray.addAll(result);
				footerRefreshProgressBar.setVisibility(View.GONE);
				footerRefreshButtonView.setEnabled(true);
				footerRefreshButtonView.setClickable(true);
				footerRefreshBodyText.setText("Click here do download more comments");

				// determine whether there are more comments to be downloaded;
				// if so
				// - display header button
				if (result.size() < NUMBER_OF_ENTRIES_PER_PAGE)
					commentList.removeFooterView(footerRefreshButtonView);
				lastLoadedPage = 1;
				commentAdapter.notifyDataSetChanged();
			}

		}

	}

	public class ParseMoreCommentListTask extends
			AsyncTask<String, Void, ArrayList<Comment>> {

		// error handling
		private boolean failed = false;
		private String failMessage;

		// task parameters
		private String repoId;
		private String revision;

		@Override
		protected void onPreExecute() {
			footerRefreshProgressBar.setVisibility(View.VISIBLE);
			footerRefreshBodyText.setText("Downloading more comments...");
			footerRefreshButtonView.setEnabled(false);
			footerRefreshButtonView.setClickable(false);
		}

		@Override
		protected ArrayList<Comment> doInBackground(String... params) {

			repoId = params[0];
			revision = params[1];

			try {

				// getting first page of comments
				String commentsXml = HttpRetriever.getCommentsListForRevisionXML(prefs,
						repoId, revision, lastLoadedPage + 1);
				return XmlParser.parseCommentList(commentsXml);

			} catch (UnsuccessfulServerResponseException e) {
				failMessage = "The target server failed to respond.";
			} catch (HttpConnectionErrorException e) {
				failMessage = "An error occured while trying to connect to Beanstalk server.";
			} catch (XMLParserException e) {
				failMessage = "An error occured while analysing server response";
				e.printStackTrace();
			}
			failed = true;
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Comment> result) {
			if (failed) {
				SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
						failMessage) {

					@Override
					public void retryAction() {
						new ParseMoreCommentListTask().execute(repoId, revision);
					}

				};

				builder.displayDialog();

			} else {

				lastLoadedPage++;

				if (result.size() < NUMBER_OF_ENTRIES_PER_PAGE) {
					commentList.removeFooterView(footerRefreshButtonView);
				} else {
					footerRefreshProgressBar.setVisibility(View.GONE);
					footerRefreshBodyText.setText("Press here to download more comments");
					footerRefreshButtonView.setEnabled(true);
					footerRefreshButtonView.setClickable(true);
				}
				commentsArray.addAll(result);
				commentAdapter.notifyDataSetChanged();
			}

		}

	}

	public class SendCommentTask extends AsyncTask<String, Void, String> {

		private boolean failed = false;
		private String failMessage;

		ProgressDialog progressDialog;
		String errorMessage = null;

		@SuppressWarnings("rawtypes")
		private AsyncTask thisTask = this;
		private String commentBody;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"sending comment");
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					thisTask.cancel(true);
					GUI.displayMonit(mContext, "Sending comment task was cancelled");
				}
			});
		}

		protected String doInBackground(String... params) {

			commentBody = params[0];
			XmlCreator xmlCreator = new XmlCreator();
			try {
				String commentXml = xmlCreator.createCommentXML(revisionId, commentBody);
				return HttpSender.sendCommentXML(prefs, commentXml,
						String.valueOf(repoId));

			} catch (XMLParserException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (IllegalArgumentException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (IllegalStateException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (IOException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (HttpSenderException e) {
				failMessage = Strings.networkConnectionErrorMessage;
			} catch (HttpSenderServerErrorException e) {
				errorMessage = e.getMessage();
				return null;
			}
			failed = true;
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			if (failed) {
				SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
						failMessage) {

					@Override
					public void retryAction() {
						new SendCommentTask().execute(commentBody);
					}

				};

				builder.displayDialog();
			} else {

				if (result != null) {

					try {
						Comment comment = XmlParser.parseComment(result);
						commentsArray.add(comment);
						commentAdapter.notifyDataSetChanged();

					} catch (XMLParserException e) {
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

}
