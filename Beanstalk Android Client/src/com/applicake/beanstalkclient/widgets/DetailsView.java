package com.applicake.beanstalkclient.widgets;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.helpers.PropertiesHolder;
import com.applicake.beanstalkclient.helpers.Property;

public class DetailsView extends TableLayout {
  
  private SinglePropertyViewHandler rowHandler;
  
  public DetailsView(Context context) {
    this(context, null);
  }
  
  public DetailsView(Context context, AttributeSet attrs) {
    super(context, attrs);
    int rowResId = R.layout.default_property_row;
    if(attrs != null) {
      rowResId = attrs.getAttributeIntValue(null, "row_layout", R.layout.default_property_row);
    }
    rowHandler = new SinglePropertyViewHandler(rowResId);
  }
  
  public void addProperty(Property property) {
    addProperty(getContext().getString(property.getName()), property.getValue());
  }
  
  public void addProperty(CharSequence propertyName, CharSequence propertyValue) {
    addView(rowHandler.getView(propertyName, propertyValue));
  }
  
  public void addProperties(List<Property> properties) {
    for(Property property : properties) {
      addProperty(property);
    }
  }
  
  public void setPropertiesHolder(PropertiesHolder propertiesHolder) {
    removeAllViews();
    addProperties(propertiesHolder.getProperties());
  }
  
  private class SinglePropertyViewHandler {

    private int rowResId;
    
    public SinglePropertyViewHandler(int rowResId) {
      this.rowResId = rowResId;
    }
    
    public View getView(CharSequence propertyName, CharSequence propertyValue) {
      View tableRow = (View)LayoutInflater.from(getContext()).inflate(rowResId, null);
      
      TextView name = (TextView)tableRow.findViewById(R.id.property_name);
      TextView value = (TextView)tableRow.findViewById(R.id.property_value);
      
      if(name == null || value == null) {
        throw new IllegalArgumentException("Row layout has to have both property_name and property_value fields in it.");
      }
      
      name.setText(propertyName);
      value.setText(propertyValue);
      
      return tableRow;
    }
  }
}
