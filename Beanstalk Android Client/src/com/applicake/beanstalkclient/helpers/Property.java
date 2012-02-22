package com.applicake.beanstalkclient.helpers;

public class Property {

  private int nameResId;
  private CharSequence value;

  public Property() {
    
  }
  
  public Property(int nameResId, CharSequence value) {
    this.nameResId = nameResId;
    this.value = value;
  }
  
  public int getName() {
    return nameResId;
  }

  public void setName(int nameResId) {
    this.nameResId = nameResId;
  }

  public CharSequence getValue() {
    return value;
  }

  public void setValue(CharSequence value) {
    this.value = value;
  }

}
