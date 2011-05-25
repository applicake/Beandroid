package com.applicake.beanstalkclient;

import java.io.IOException;

import com.applicake.beanstalkclient.enums.ColorLabels;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateNewRepositoryActivity extends BeanstalkActivity implements
		OnClickListener {
	private Context mContext;

	String repoId;
	String repoTitle;
	int colorLabelNo = 0;
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
	private EditText repoNameEditText;
	private Spinner repoTypeSpinner;
	private CheckBox repoCreateStructCheckbox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_new_repository_layout);
		mContext = this;

		repoNameEditText = (EditText) findViewById(R.id.nameEditText);
		repoTitleEditText = (EditText) findViewById(R.id.titleEditText);

		repoTypeSpinner = (Spinner) findViewById(R.id.typeSpinner);
		repoTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int selectedItem, long arg3) {
				if (selectedItem == 0){
					repoCreateStructCheckbox.setEnabled(true);
				} else if (selectedItem == 1) {
					repoCreateStructCheckbox.setEnabled(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		repoCreateStructCheckbox = (CheckBox) findViewById(R.id.structureCheckBox);

		colorLabelButton = (Button) findViewById(R.id.colorLabelButton);
		colorLabelButton.getBackground().setLevel(colorLabelNo);
		colorLabelButton.setText(ColorLabels.getLabelFromNumber(colorLabelNo));
		colorLabelButton.setOnClickListener(this);

		Button createButton = (Button) findViewById(R.id.createButton);
		createButton.setOnClickListener(this);

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
		if (v.getId() == R.id.createButton) {
			new SendRepositoryCreateTask().execute();
			
		}

		if (v.getId() == R.id.colorLabelButton) {
			showDialog(0);

		}

	}

	public class SendRepositoryCreateTask extends AsyncTask<Void, Void, Integer> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"creating repository");
			super.onPreExecute();
		}

		protected Integer doInBackground(Void... params) {

			XmlCreator xmlCreator = new XmlCreator();
			HttpSender httpSender = new HttpSender();

			try {
				if (repoTypeSpinner.getSelectedItemPosition() == 1) {
					String repostitoryCreateXml = xmlCreator
							.createGitRepositoryCreationXML(repoNameEditText.getText()
									.toString(), repoTitleEditText.getText().toString(),
									ColorLabels.getLabelFromNumber(colorLabelNo));
					return httpSender.sendCreateNewRepostiroyXML(prefs,
							repostitoryCreateXml);
				} else {
					String repostitoryCreateXml = xmlCreator
							.createSVNRepositoryCreationXML(repoNameEditText.getText()
									.toString(), repoTitleEditText.getText().toString(),
									ColorLabels.getLabelFromNumber(colorLabelNo),
									repoCreateStructCheckbox.isChecked());
					return httpSender.sendCreateNewRepostiroyXML(prefs,
							repostitoryCreateXml);
				}

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
			return 0;

		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.cancel();
			if (result == 201) {
				GUI.displayMonit(mContext, "reposiotry was created successfully!");
				setResult(Constants.REFRESH_ACTIVITY);
				finish();
			}

			super.onPostExecute(result);
		}

	}

}
