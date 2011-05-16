package com.applicake.beanstalkclient;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CreateNewRepositoryActivity extends BeanstalkActivity implements OnClickListener {
	
	private Button createButton;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_new_repository_layout);
		
		createButton = (Button) findViewById(R.id.createButton);
		createButton.setOnClickListener(this);
		mContext = this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.createButton){
			GUI.displayMonit(mContext, "to be implemented");
		}
		
	}

}
