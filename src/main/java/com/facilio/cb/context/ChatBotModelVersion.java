package com.facilio.cb.context;

public class ChatBotModelVersion {

	long id = -1;
	long orgId = -1;
	long modelId = -1;
	int versionNo = -1;
	String mlModel;
	boolean latestVersion;
	double accuracyRate = -1;
	public double getAccuracyRate() {
		return accuracyRate;
	}
	public void setAccuracyRate(double accuracyRate) {
		this.accuracyRate = accuracyRate;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getModelId() {
		return modelId;
	}
	public void setModelId(long modelId) {
		this.modelId = modelId;
	}
	public int getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}
	public String getMlModel() {
		return mlModel;
	}
	public void setMlModel(String mlModel) {
		this.mlModel = mlModel;
	}
	public boolean isLatestVersion() {
		return latestVersion;
	}
	public boolean getLatestVersion() {
		return latestVersion;
	}
	public void setLatestVersion(boolean latestVersion) {
		this.latestVersion = latestVersion;
	}
	
	
}
