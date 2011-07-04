package com.applicake.beanstalkclient;

import java.io.IOException;

import com.applicake.beanstalkclient.enums.ColorLabels;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
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
        colorLabelButton.setText(ColorLabels.getLabelFromNumberForButton(colorLabelNo));
      }

      if (v.getId() == R.id.buttonGreen) {
        colorLabelButton.getBackground().setLevel(
            colorLabelNo = ColorLabels.GREEN.getNumber());
        colorLabelButton.setText(ColorLabels.getLabelFromNumberForButton(colorLabelNo));
      }

      if (v.getId() == R.id.buttonRed) {
        colorLabelButton.getBackground().setLevel(
            colorLabelNo = ColorLabels.RED.getNumber());
        colorLabelButton.setText(ColorLabels.getLabelFromNumberForButton(colorLabelNo));
      }

      if (v.getId() == R.id.buttonOrange) {
        colorLabelButton.getBackground().setLevel(
            colorLabelNo = ColorLabels.ORANGE.getNumber());
        colorLabelButton.setText(ColorLabels.getLabelFromNumberForButton(colorLabelNo));
      }

      if (v.getId() == R.id.buttonYellow) {
        colorLabelButton.getBackground().setLevel(
            colorLabelNo = ColorLabels.YELLOW.getNumber());
        colorLabelButton.setText(ColorLabels.getLabelFromNumberForButton(colorLabelNo));
      }

      if (v.getId() == R.id.buttonBlue) {
        colorLabelButton.getBackground().setLevel(
            colorLabelNo = ColorLabels.BLUE.getNumber());
        colorLabelButton.setText(ColorLabels.getLabelFromNumberForButton(colorLabelNo));
      }

      if (v.getId() == R.id.buttonPink) {
        colorLabelButton.getBackground().setLevel(
            colorLabelNo = ColorLabels.PINK.getNumber());
        colorLabelButton.setText(ColorLabels.getLabelFromNumberForButton(colorLabelNo));
      }

      if (v.getId() == R.id.buttonGrey) {
        colorLabelButton.getBackground().setLevel(
            colorLabelNo = ColorLabels.GREY.getNumber());
        colorLabelButton.setText(ColorLabels.getLabelFromNumberForButton(colorLabelNo));
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
    colorLabelButton.setText(ColorLabels.getLabelFromNumberForButton(colorLabelNo));
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
    String errorMessage;

    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask = this;
    private String failMessage;
    private boolean failed;

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...",
          "modifying repository properties");

      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Download task was cancelled");
        }
      });
      super.onPreExecute();
    }

    protected Integer doInBackground(Void... params) {

      XmlCreator xmlCreator = new XmlCreator();
      try {
        String repostitoryModificationXml = xmlCreator.createRepositoryModifyXML(
            repoTitleEditText.getText().toString().trim(),
            ColorLabels.getLabelFromNumber(colorLabelNo));
        return HttpSender.sendUpdateRepositoryXML(prefs, repostitoryModificationXml,
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
        return 0;
      }
      failed = true;
      return 0;

    }

    @Override
    protected void onPostExecute(Integer result) {
      progressDialog.dismiss();
      if (failed) {
        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
            failMessage) {
          @Override
          public void retryAction() {
            new SendRepositoryPropertiesTask().execute();
          }
        };

        builder.displayDialog();
      }

      if (result == 200) {
        GUI.displayMonit(mContext, "reposiotry properties were modified!");
        setResult(Constants.REFRESH_ACTIVITY);
        finish();
      } else if ((result == 0) && (errorMessage != null)) {
        GUI.displayMonit(mContext, "Server error: " + errorMessage);
      } else {
        GUI.displayMonit(mContext, "Unexpected error, please try again later");
      }

    }

  }

}
