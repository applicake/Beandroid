package com.applicake.beanstalkclient.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.Plan;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.adapters.PlansAdapter;
import com.applicake.beanstalkclient.enums.PlanColors;
import com.applicake.beanstalkclient.helpers.MegabytesConverter;
import com.applicake.beanstalkclient.helpers.PlanPropertiesHolder;
import com.applicake.beanstalkclient.helpers.PlansCostsComparator;
import com.applicake.beanstalkclient.tasks.BeanstalkAsyncTask;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.widgets.DetailsView;

public class PlansActivity extends BeanstalkActivity implements OnItemClickListener {

  private ListView planListView;
  private PlansAdapter plansAdapter;
  private List<Plan> plansList = new ArrayList<Plan>();
  private Plan currentPlan;
  private int currentPlanId;
  private TextView currentPlanName;
  private DetailsView currentPlanDetails;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.plans_list_layout);
    this.planListView = (ListView)findViewById(R.id.plans_list);
    plansAdapter = new PlansAdapter(this, R.layout.collapsed_plan_item, plansList);
    
    currentPlanId = prefs.getInt(Constants.PLAN_ID, Plan.NO_PLAN_ID_SET);
    plansAdapter.setCurrentPlanId(currentPlanId);
    
    planListView.setAdapter(plansAdapter);
    planListView.setOnItemClickListener(this);
    
    currentPlanName = (TextView)findViewById(R.id.current_plan_name);
    currentPlanDetails = (DetailsView)findViewById(R.id.details_view);
    
    setVisible(false);
    
    new DownloadPlansAsyncTask(this).execute();
  }
  
  @Override
  public void onItemClick(AdapterView<?> adapterView, View itemView, int position, long id) {
    Plan plan = plansList.get(position);
    Intent intent = new Intent(this, PlanDetailsActivity.class);
    intent.putExtra(Constants.PLAN, plan);
    startActivityForResult(intent, 0);
  }
  
  private class AvailablePlans {
    private Plan currentPlan;
    private List<Plan> availablePlans;
    
    public Plan getCurrentPlan() {
      return currentPlan;
    }
    
    public void setCurrentPlan(Plan currentPlan) {
      this.currentPlan = currentPlan;
    }
    
    public List<Plan> getAvailablePlans() {
      return availablePlans;
    }
    
    public void setAvailablePlans(List<Plan> availablePlans) {
      this.availablePlans = availablePlans;
    }
    
    
  }
  
  public class DownloadPlansAsyncTask extends BeanstalkAsyncTask<Void, Void, AvailablePlans> {

    public DownloadPlansAsyncTask(Activity context) {
      super(context);
    }

    @Override
    protected String getProgressDialogTip() {
      return getString(R.string.downloading_available_plans_label);
    }
    
    @Override
    protected AvailablePlans trueDoInBackground(Void... params) throws Throwable {
      
      String plansXml = HttpRetriever.getAvailablePlans(prefs);
      
      plansList.clear();
      Map<Integer, Plan> plansMap = XmlParser.parsePlan(plansXml);
      Plan currentPlan = plansMap.remove(currentPlanId);
      plansList.addAll(plansMap.values());
      Collections.sort(plansList, new PlansCostsComparator());
      
      AvailablePlans availablePlans = new AvailablePlans();
      availablePlans.setAvailablePlans(plansList);
      availablePlans.setCurrentPlan(currentPlan);
      
      return availablePlans;
    }
    
    @Override
    protected void trueOnPostExceute(AvailablePlans result) {
      setVisible(true);
      plansAdapter.notifyDataSetChanged();
      currentPlan = result.getCurrentPlan();
      
      findViewById(R.id.colorLabel).setBackgroundColor(PlanColors.getColorResIdForPlan(currentPlan));
      currentPlanName.setText(currentPlan.getName());
      currentPlanDetails.setPropertiesHolder(new PlanPropertiesHolder(currentPlan));
    }

    @Override
    protected void performTaskAgain() {
      new DownloadPlansAsyncTask(getContext()).execute();
    }
    
  }
}
