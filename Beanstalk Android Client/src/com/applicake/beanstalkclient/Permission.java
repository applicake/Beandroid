package com.applicake.beanstalkclient;

public class Permission {
	private boolean fullDeploymentAccess;
	private int id;
	private int repositoryId;
	private int serverEnvironmentId;
	private int userId;
	private boolean readAccess;
	private boolean writeAccess;
	
	
	public boolean isFullDeploymentAccess() {
		return fullDeploymentAccess;
	}
	public void setFullDeploymentAccess(boolean fullDeploymentAccess) {
		this.fullDeploymentAccess = fullDeploymentAccess;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRepositoryId() {
		return repositoryId;
	}
	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	public int getServerEnvironmentId() {
		return serverEnvironmentId;
	}
	public void setServerEnvironmentId(int serverEnvironmentId) {
		this.serverEnvironmentId = serverEnvironmentId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public boolean isReadAccess() {
		return readAccess;
	}
	public void setReadAccess(boolean readAccess) {
		this.readAccess = readAccess;
	}
	public boolean isWriteAccess() {
		return writeAccess;
	}
	public void setWriteAccess(boolean writeAccess) {
		this.writeAccess = writeAccess;
	}
	

}
