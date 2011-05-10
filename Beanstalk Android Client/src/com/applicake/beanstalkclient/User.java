package com.applicake.beanstalkclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.applicake.beanstalkclient.enums.UserType;

public class User {
	static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private int accountId;
	private UserType admin;
	private Date createdAt;
	private String email;
	private String firstName;
	private int id;
	private String lastName;
	private String login;
	private String timezone;
	private Date updatedAt;

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

	public Date getCreatedAt() {
		return createdAt;
	}

	// throws parse exception
	public void setCreatedAt(String date) throws ParseException {

		this.createdAt = FORMATTER.parse(date.trim());
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

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String date) throws ParseException {
		this.updatedAt = FORMATTER.parse(date.trim());
	}

}
