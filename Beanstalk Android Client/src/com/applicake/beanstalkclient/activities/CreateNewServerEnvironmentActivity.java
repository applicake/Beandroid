package com.applicake.beanstalkclient.activities;

import com.applicake.beanstalkclient.R;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

public class CreateNewServerEnvironmentActivity extends BeanstalkActivity {
  
  private EditText nameEditText;
  private CheckBox automaticCheckBox;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.create_new_server_environment_layout);
    
    nameEditText = (EditText) findViewById(R.id.nameLabel);
    automaticCheckBox = (CheckBox) findViewById(R.id.is_automatic_checkbox);
    
     
  }

}
