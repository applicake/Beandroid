package com.applicake.beanstalkclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.applicake.beanstalkclient.enums.UserType;

public class User implements Parcelable{
	static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	//parcelable implementation
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(accountId);
		dest.writeInt(admin.ordinal());
		dest.writeLong(createdAt);
		dest.writeString(email);
		dest.writeString(firstName);
		dest.writeInt(id);
		dest.writeString(lastName);
		dest.writeString(login);
		dest.writeString(timezone);
		dest.writeLong(updatedAt);
	}
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public User(Parcel in) {
		this.accountId = in.readInt();
		this.admin = UserType.values()[in.readInt()];
		this.createdAt = in.readLong();
		this.email = in.readString();
		this.firstName = in.readString();
		this.id = in.readInt();
		this.lastName = in.readString();
		this.login = in.readString();
		this.timezone = in.readString();
		this.updatedAt = in.readLong();
		
	}
	
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};

	private int accountId;
	private UserType admin;
	private long createdAt;
	private String email;
	private String firstName;
	private int id;
	private String lastName;
	private String login;
	private String timezone;
	private long updatedAt;

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public UserType getAdmin() {
		return admin;
	}

	public void setAdmin(UserType type) {
		this.admin = type;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	// throws parse exception
	public void setCreatedAt(String date) throws ParseException {

		this.createdAt = FORMATTER.parse(date.trim()).getTime();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String date) throws ParseException {
		this.updatedAt = FORMATTER.parse(date.trim()).getTime();;
	}


}
