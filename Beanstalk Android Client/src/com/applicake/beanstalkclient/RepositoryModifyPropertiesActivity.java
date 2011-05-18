package com.applicake.beanstalkclient;

import java.io.IOException;

import com.applicake.beanstalkclient.enums.ColorLabels;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RepositoryModifyPropertiesActivity extends BeanstalkActivity implements
		OnClickListener {
	private Button saveChangesButton;
	private Context mContext;

	String repoId;
	String repoTitle;
	int colorLabelNo;
	private EditText repoTitleEditText;

	OnClickListener dialogButtonsListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.buttonWhite) {
				colorLabelButton.getBackground().setLevel(
						colorLabelNo = ColorLabels.WHITE.getNumber());
				colorLabelButton.setText(ColorLabels.getLabelFromNumber(colorLabelNo));
			}

			if (v.getId() == R.id.buttonGreen) {
				colorLabelButton.getBackground().setLevel(
						colorLabelNo = ColorLabels.GREEN.getNumber());
				colorLabelButton.setText(ColorLabels.getLabelFromNumber(colorLabelNo));
			}

			if (v.getId() == R.id.buttonRed) {
				colorLabelButton.getBackground().setLevel(
						colorLabelNo = ColorLabels.RED.getNumber());
				colorLabelButton.setText(ColorLabels.getLabelFromNumber(colorLabelNo));
			}

			if (v.getId() == R.id.buttonOrange) {
				colorLabelButton.getBackground().setLevel(
						colorLabelNo = ColorLabels.ORANGE.getNumber());
				colorLabelButton.setText(ColorLabels.getLabelFromNumber(colorLabelNo));
			}

			if (v.getId() == R.id.buttonYellow) {
				colorLabelButton.getBackground().setLevel(
						colorLabelNo = ColorLabels.YELLOW.getNumber());
				colorLabelButton.setText(ColorLabels.getLabelFromNumber(colorLabelNo));
			}

			if (v.getId() == R.id.buttonBlue) {
				colorLabelButton.getBackground().setLevel(
						colorLabelNo = ColorLabels.BLUE.getNumber());
				colorLabelButton.setText(ColorLabels.getLabelFromNumber(colorLabelNo));
			}

			if (v.getId() == R.id.buttonPink) {
				colorLabelButton.getBackground().setLevel(
						colorLabelNo = ColorLabels.PINK.getNumber());
				colorLabelButton.setText(ColorLabels.getLabelFromNumber(colorLabelNo));
			}

			if (v.getId() == R.id.buttonGrey) {
				colorLabelButton.getBackground().setLevel(
						colorLabelNo = ColorLabels.GREY.getNumber());
				colorLabelButton.setText(ColorLabels.getLabelFromNumber(colorLabelNo));
			}

			dialog.dismiss();
		}

	};
	private Button colorLabelButton;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_repository_layout);
		mContext = this;

		Intent currentIntent = getIntent();
		repoId = currentIntent.getStringExtra(Constants.REPOSITORY_ID);
		repoTitle = currentIntent.getStringExtra(Constants.REPOSITORY_TITLE);
		colorLabelNo = currentIntent.getIntExtra(Constants.REPOSITORY_COLOR_NO, 0);

		repoTitleEditText = (EditText) findViewById(R.id.titleEditText);
		repoTitleEditText.setText(repoTitle);

		colorLabelButton = (Button) findViewById(R.id.colorLabelButton);
		colorLabelButton.getBackground().setLevel(colorLabelNo);
		colorLabelButton.setText(ColorLabels.getLabelFromNumber(colorLabelNo));
		colorLabelButton.setOnClickListener(this);

		saveChangesButton = (Button) findViewById(R.id.saveChangesButton);
		saveChangesButton.setOnClickListener(this);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.color_label_dialog);
		dialog.setTitle("Pick a color");

		dialog.findViewById(R.id.buttonWhite).setOnClickListener(dialogButtonsListener);
		dialog.findViewById(R.id.buttonRed).setOnClickListener(dialogButtonsListener);
		dialog.findViewById(R.id.buttonOrange).setOnClickListener(dialogButtonsListener);
		dialog.findViewById(R.id.buttonYellow).setOnClickListener(dialogButtonsListener);
		dialog.findViewById(R.id.buttonGreen).setOnClickListener(dialogButtonsListener);
		dialog.findViewById(R.id.buttonBlue).setOnClickListener(dialogButtonsListener);
		dialog.findViewById(R.id.buttonPink).setOnClickListener(dialogButtonsListener);
		dialog.findViewById(R.id.buttonGrey).setOnClickListener(dialogButtonsListener);

		return dialog;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.saveChangesButton) {
			new SendRepositoryPropertiesTask().execute();
		}

		if (v.getId() == R.id.colorLabelButton) {
			showDialog(0);

		}

	}

	public class SendRepositoryPropertiesTask extends AsyncTask<Void, Void, Integer> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"modifying repository properties");
			super.onPreExecute();
		}

		protected Integer doInBackground(Void... params) {

			XmlCreator xmlCreator = new XmlCreator();
			HttpSender httpSender = new HttpSender();
			try {
				String repostitoryModificationXml = xmlCreator.createRepositoryModifyXML(
						repoTitleEditText.getText().toString().trim(),
						ColorLabels.getLabelFromNumber(colorLabelNo));
				return httpSender.sendUpdateRepositoryXML(prefs,
						repostitoryModificationXml, String.valueOf(repoId));

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HttpSenderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.cancel();
			if (result == 200) {
				GUI.displayMonit(mContext, "reposiotry properties were modified!");
				finish();
			}

			super.onPostExecute(result);
		}

	}

}
