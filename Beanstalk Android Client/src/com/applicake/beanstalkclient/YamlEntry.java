package com.applicake.beanstalkclient;

import android.os.Parcel;
import android.os.Parcelable;

public class YamlEntry implements Parcelable {

	String value;
	String property;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(value);
		dest.writeString(property);

	}
	
	public static final Parcelable.Creator<YamlEntry> CREATOR = new Parcelable.Creator<YamlEntry>() {
		public YamlEntry createFromParcel(Parcel in) {
			return new YamlEntry(in.readString(), in.readString());
		}

		public YamlEntry[] newArray(int size) {
			return new YamlEntry[size];
		}
	};
	
	
	public YamlEntry(String value, String key) {
		this.value = value;
		this.property = key;
	}

	public String getValue() {
		return value;
	}

	public String getProperty() {
		return property;
	}

}
