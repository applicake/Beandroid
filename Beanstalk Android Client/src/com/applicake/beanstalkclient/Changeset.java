package com.applicake.beanstalkclient;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

public class Changeset implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2632263283471491397L;

	static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private int accountId;
	private String author;
	private String changedDirs;
	private String changedFiles;
	private String changedProperties;
	private String email;
	private String hashId;
	private String message;
	private int repositoryId;
	private String revision;
	private Date time;
	private boolean tooLarge;
	private int userId;

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getChangedDirs() {
		return changedDirs;
	}

	public void setChangedDirs(String yaml) {
		this.changedDirs = yaml;
	}

	public String getChangedFiles() {
		return changedFiles;
	}

	public void setChangedFiles(String yaml) {
		this.changedFiles = yaml;
	}

	public String getChangedProperties() {
		return changedProperties;
	}

	public void setChangedProperties(String yaml) {
		changedProperties = yaml;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHashId() {
		return hashId;
	}

	public void setHashId(String hashId) {
		this.hashId = hashId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(String date) {
		try {
			this.time = FORMATTER.parse(date.trim());
		} catch (ParseException e) {
			this.time = new Date(0);
		}
	}

	public boolean isTooLarge() {
		return tooLarge;
	}

	public void setTooLarge(boolean tooLarge) {
		this.tooLarge = tooLarge;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
