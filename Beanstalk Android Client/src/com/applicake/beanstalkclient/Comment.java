package com.applicake.beanstalkclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
	static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	private int accountId;
	private String authorEmail;
	private int authorId;
	private String authorLogin;
	private String authorName;
	private String body;
	private Date createdAt;
	private String filePath;
	private int id;
	private String lineNumber;
	private String renderedBody;
	private int repositoryId;
	private String revision;
	private Date updatedAt;
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public String getAuthorEmail() {
		return authorEmail;
	}
	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public String getAuthorLogin() {
		return authorLogin;
	}
	public void setAuthorLogin(String authorLogin) {
		this.authorLogin = authorLogin;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String date) throws ParseException {
		this.createdAt = FORMATTER.parse(date.trim());
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String getRenderedBody() {
		return renderedBody;
	}
	public void setRenderedBody(String renderedBody) {
		this.renderedBody = renderedBody;
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
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String date) throws ParseException {
		this.updatedAt = FORMATTER.parse(date.trim());
	}
	

}
