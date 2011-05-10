package com.applicake.beanstalkclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Repository {
	static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	static SimpleDateFormat FORMATTER_NOTIMEZONE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private int accountId;
	private boolean anonymous;
	private String colorLabel;
	private Date createdAt;
	private int id;
	private Date lastCommitAt;
	private String name;
	private int revision;
	private int storageUsedBytes;
	private String title;
	private String type; 
	private Date updatedAt;
	private String vcs;
	private String defaultBranch;
	
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public boolean isAnonymous() {
		return anonymous;
	}
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	public String getColorLabel() {
		return colorLabel;
	}
	public void setColorLabel(String colorLabel) {
		this.colorLabel = colorLabel;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String date) throws ParseException {
		this.createdAt = FORMATTER.parse(date.trim());
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getLastCommitAt() {
		return lastCommitAt;
	}
	public void setLastCommitAt(String date) throws ParseException {
		this.lastCommitAt = FORMATTER_NOTIMEZONE.parse(date.trim());
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRevision() {
		return revision;
	}
	public void setRevision(int revision) {
		this.revision = revision;
	}
	public int getStorageUsedBytes() {
		return storageUsedBytes;
	}
	public void setStorageUsedBytes(int storageUsedBytes) {
		this.storageUsedBytes = storageUsedBytes;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String date) throws ParseException {
		this.updatedAt = FORMATTER.parse(date.trim());
	}
	public String getVcs() {
		return vcs;
	}
	public void setVcs(String vcs) {
		this.vcs = vcs;
	}
	public String getDefaultBranch() {
		return defaultBranch;
	}
	public void setDefaultBranch(String defaultBranch) {
		this.defaultBranch = defaultBranch;
	}

}
