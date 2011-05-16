package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.params.HttpParams;
import org.xml.sax.SAXException;

import com.applicake.beanstalkclient.adapters.ChangesAdapter;
import com.applicake.beanstalkclient.adapters.CommentAdapter;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpRetreiverException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChangesetActivity extends BeanstalkActivity implements OnClickListener {

	private ListView changesList;
	private Button changesetButton;
	private Button commentsButton;
	private ArrayList<Comment> commentArray;
	private ListView commentList;
	private CommentAdapter commentAdapter;
	private boolean commentListParsed;
	private int repoId;
	private String revisionId;
	private EditText newCommentBody;
	private Button submitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changeset_layout);
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

		// handle changes list and adapter
		ChangesAdapter changesAdapter = new ChangesAdapter(this,
				R.layout.changeset_list_entry, changesArray);

		changesList = (ListView) findViewById(R.id.changesList);
		changesList.setAdapter(changesAdapter);
		
		View footerView = ((LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comment_list_footer, null, false);
		commentList.addFooterView(footerView);
		
		newCommentBody = (EditText) footerView.findViewById(R.id.newCommentBody);
		submitButton = (Button) footerView.findViewById(R.id.submitButton);
		submitButton.setOnClickListener(this);

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

		} else if (v.getId() == commentsButton.getId()) {
			changesList.setVisibility(View.GONE);
			commentList.setVisibility(View.VISIBLE);
			if (!commentListParsed) {
				new ParseCommentListTask().execute(String.valueOf(repoId), revisionId);
				commentListParsed = true;
			}
		} else if (v.getId() == submitButton.getId()){
			//TODO send comment via XML
			
			String newCommentBodyString = newCommentBody.getText().toString();
			if (newCommentBodyString.trim().length() == 0){
				GUI.displayMonit(getApplicationContext(), "You must enter comment body");
			} else {
				GUI.displayMonit(getApplicationContext(), newCommentBodyString.trim());
			}
			
			
		}

	}

	public class ParseCommentListTask extends AsyncTask<String, Void, ArrayList<Comment>> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected ArrayList<Comment> doInBackground(String... params) {

			HttpRetriever httpRetriever = new HttpRetriever();

			try {
				String commentsXml = httpRetriever.getCommentsListForRevisionXML(prefs,
						params[0], params[1]);
				XmlParser xmlParser = new XmlParser();
				return xmlParser.parseCommentList(commentsXml);
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
			commentArray = result;
			commentAdapter = new CommentAdapter(getApplicationContext(),
					R.layout.comment_list_entry, commentArray);
			commentList.setAdapter(commentAdapter);
			// commentArray = result;
			if (commentArray != null && !commentArray.isEmpty()) {
				commentAdapter.notifyDataSetChanged();
				commentAdapter.clear();

				for (int i = 0; i < commentArray.size(); i++) {
					commentAdapter.add(commentArray.get(i));
				}

			}
			
			commentAdapter.notifyDataSetChanged();

		}

	}

}
