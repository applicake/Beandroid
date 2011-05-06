package com.applicake.beanstalkclient;

import java.util.ArrayList;
import java.util.Date;

public class Changeset {
	
	private int accountId;
	private String author;
	private ArrayList<YamlEntry> changedDirs;
	private ArrayList<YamlEntry> changedFiles;
	private ArrayList<YamlEntry> changedProperties;
	private String email;
	private String hashId;
	private String message;
	
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
	public ArrayList<YamlEntry> getChangedDirs() {
		return changedDirs;
	}
	public void setChangedDirs(ArrayList<YamlEntry> changedDirs) {
		this.changedDirs = changedDirs;
	}
	public ArrayList<YamlEntry> getChangedFiles() {
		return changedFiles;
	}
	public void setChangedFiles(ArrayList<YamlEntry> changedFiles) {
		this.changedFiles = changedFiles;
	}
	public ArrayList<YamlEntry> getChangedProperties() {
		return changedProperties;
	}
	public void setChangedProperties(ArrayList<YamlEntry> changedProperties) {
		this.changedProperties = changedProperties;
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
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
	private int repositoryId;
	private String revision;
	private String time;
	private boolean tooLarge;
	private int userId;
	
	

}
