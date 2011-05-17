package com.applicake.beanstalkclient;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RepositoryModifyPropertiesActivity extends BeanstalkActivity implements OnClickListener {
	private Button saveChangesButton;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_repository_layout);
		
		saveChangesButton = (Button) findViewById(R.id.createButton);
		saveChangesButton.setOnClickListener(this);
		mContext = this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.createButton){
			GUI.displayMonit(mContext, "to be implemented");
		}
		
	}

}
