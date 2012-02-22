package com.applicake.beanstalkclient.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.Plan;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.helpers.PlanPropertiesHolder;
import com.applicake.beanstalkclient.widgets.DetailsView;

public class PlanDetailsActivity extends BeanstalkActivity implements OnClickListener {
  
  private Plan plan;
  private Button upgradeButton;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.expanded_plan_item);
    
    plan = (Plan)getIntent().getSerializableExtra(Constants.PLAN);
    if(plan == null) {
      finish();
    }
    int currentPlanId = prefs.getInt(Constants.PLAN_ID, Plan.NO_PLAN_ID_SET);
    
    TextView planName = (TextView)findViewById(R.id.plan_name);
    TextView planPrice = (TextView)findViewById(R.id.plan_price);
    DetailsView detailsView = (DetailsView)findViewById(R.id.details_view);
    
    planName.setText(plan.getName());
    planPrice.setText(plan.getPrice() + " " + Constants.DEFAULT_CURRENCY_SYMBOL);
    
    if(currentPlanId == plan.getId()) {
      findViewById(R.id.current_plan_marker).setVisibility(View.VISIBLE);
    } else if(currentPlanId != Plan.NO_PLAN_ID_SET) {
      upgradeButton = (Button)findViewById(R.id.upgradeButton);
      upgradeButton.setVisibility(View.VISIBLE);
      upgradeButton.setOnClickListener(this);
    }
        
    detailsView.setPropertiesHolder(new PlanPropertiesHolder(plan));
        
  }
  
  @Override
  public void onClick(View v) {
    if(v == upgradeButton) {
      String userAccountDomain = prefs.getString(Constants.USER_ACCOUNT_DOMAIN, "");
      Intent intent = new Intent(Intent.ACTION_VIEW,
          Uri.parse("https://" + userAccountDomain + ".beanstalkapp.com/receipts/new/" + plan.getName()));
      startActivity(intent);
    }
  }

}
