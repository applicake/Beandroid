package com.applicake.beanstalkclient.widgets;

import com.applicake.beanstalkclient.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddNewObjectView extends RelativeLayout {

  private AddNewObjectViewController viewController;
  private ImageView imageView;
  private TextView availbilityInfoTextView;
  private TextView mainTextView;
  private int available;
  private int max;
  private boolean currentState = true;
  private View footerBtnLayout;
    
  public AddNewObjectView(Context context, AddNewObjectViewController viewData) {
    this(context, null, viewData);
  }
  
  public AddNewObjectView(Context context, AttributeSet attrs, AddNewObjectViewController viewData) {
    super(context, attrs);
    this.viewController = viewData;

    LayoutInflater.from(getContext()).inflate(R.layout.add_new_object_layout, this);
    
    footerBtnLayout = findViewById(R.id.footer_btn_layout);
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
      if(!newState) {
        setClickable(false);
      }
    }
    this.currentState = newState;
  }
  
  private void refreshViewAccordingToViewData(AddNewObjectViewData viewData) {
    imageView.setImageDrawable(getResources().getDrawable(viewData.getImageResId()));
    mainTextView.setText(viewData.getMainTextStringId());
    footerBtnLayout.setBackgroundResource(viewData.getBackgroundResId());
  }
  
}
