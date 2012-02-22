package com.applicake.beanstalkclient.widgets;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.activities.BeanstalkActivity;
import com.applicake.beanstalkclient.activities.PlansActivity;

public class AddNewObjectView extends RelativeLayout {

  private AddNewObjectViewController viewController;
  private ImageView imageView;
  private TextView availbilityInfoTextView;
  private TextView mainTextView;
  private int available;
  private int max;
  private boolean currentState = true;
  private OnClickListener onClickListener;
  private OnClickListener disabledClickListener;
  private BeanstalkActivity contextActivity;
  
  public AddNewObjectView(BeanstalkActivity context, AddNewObjectViewController viewData) {
    this(context, null, viewData);
    this.contextActivity = context;
  }
  
  public AddNewObjectView(Context context, AttributeSet attrs, AddNewObjectViewController viewData) {
    super(context, attrs);
    this.viewController = viewData;

    LayoutInflater.from(getContext()).inflate(R.layout.add_new_object_layout, this);
    
    imageView = (ImageView)findViewById(R.id.plusSign);
    mainTextView = (TextView)findViewById(R.id.add_object_label);
    availbilityInfoTextView = (TextView)findViewById(R.id.objectCounter);
    
    refreshViewAccordingToViewData(viewController.getEnabledData());
  }

  public int getAvailable() {
    return available;
  }

  public int getMax() {
    return max;
  }

  public void setAvailable(int available, int max) {
    this.available = available;
    this.max = max;

    if(availbilityInfoTextView.getVisibility() != View.VISIBLE) {
      availbilityInfoTextView.setVisibility(View.VISIBLE);
    }

    boolean newState = (available != 0);
    
    AddNewObjectViewData viewData = viewController.getViewData(newState);
    availbilityInfoTextView.setText(getResources().getString(viewData.getAvailbilityStringId()) + " " + available + "/" + max);
    if(currentState != newState) {
      refreshViewAccordingToViewData(viewData);
      super.setOnClickListener(newState ? onClickListener : generateDisabledViewListener());
    }
    this.currentState = newState;
  }
  
  @Override
  public void setOnClickListener(OnClickListener l) {
    this.onClickListener = l;
    if(currentState) {
      super.setOnClickListener(l);
    }
  }
  
  private void refreshViewAccordingToViewData(AddNewObjectViewData viewData) {
    imageView.setImageDrawable(getResources().getDrawable(viewData.getImageResId()));
    mainTextView.setText(viewData.getMainTextStringId());
    setBackgroundResource(viewData.getBackgroundResId());
  }
  
  private OnClickListener generateDisabledViewListener() {
    if(disabledClickListener == null) {
      disabledClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(getContext(), PlansActivity.class);
          contextActivity.startActivityForResult(intent, 0);
        }
      };
    }
    return disabledClickListener;
  }
  
  
}
