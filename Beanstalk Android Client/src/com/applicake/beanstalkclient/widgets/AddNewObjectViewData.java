package com.applicake.beanstalkclient.widgets;


public class AddNewObjectViewData {
  
  public static final int NO_COLOR_SET = -1;
  
  private int imageResId;
  private int mainTextStringId;
  private int availbilityStringId;
  private int backgroundResId = NO_COLOR_SET;
  
  public AddNewObjectViewData() {

  }

  public int getImageResId() {
    return imageResId;
  }

  public void setImageResId(int imageResId) {
    this.imageResId = imageResId;
  }

  public int getMainTextStringId() {
    return mainTextStringId;
  }

  public void setMainTextStringId(int mainTextStringId) {
    this.mainTextStringId = mainTextStringId;
  }

  public int getAvailbilityStringId() {
    return availbilityStringId;
  }

  public void setAvailbilityStringId(int availbilityStringId) {
    this.availbilityStringId = availbilityStringId;
  }

  public int getBackgroundResId() {
    return backgroundResId;
  }

  public void setBackgroundResId(int backgroundResId) {
    this.backgroundResId = backgroundResId;
  }

}
