package com.facilio.leed.context;

import com.facilio.bmsconsole.context.BuildingContext;

public class LeedConfigurationContext extends BuildingContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long leedId;
	private String buildingStatus;
	private long leedScore;
	private long energyScore;
	private long waterScore;
	private long wasteScore;
	private long humanExperienceScore;
	private long transportScore;	
	
	
	public long getLeedId() 
	{
		return this.leedId;
	}
	public void setLeedId(long leedId) 
	{
		this.leedId = leedId;
	}
	
	public String getBuildingStatus() {
		return this.buildingStatus;
	}
	public void setBuildingStatus(String buildingStatus) {
		this.buildingStatus = buildingStatus;
	}
	
	public long getLeedScore() {
		return leedScore;
	}
	public void setLeedScore(long leedScore) {
		this.leedScore = leedScore;
	}
	
	public long getEnergyScore() {
		return this.energyScore;
	}
	public void setEnergyScore(long energyScore) {
		this.energyScore = energyScore;
	}
	
	public long getWaterScore() {
		return this.waterScore;
	}
	public void setWaterScore(long waterScore) {
		this.waterScore = waterScore;
	}
	
	public long getWasteScore() {
		return this.wasteScore;
	}
	public void setWasteScore(long wasteScore) {
		this.wasteScore = wasteScore;
	}
	
	public long getHumanExperienceScore() {
		return this.humanExperienceScore;
	}
	public void setHumanExperienceScore(long humanExperienceScore) {
		this.humanExperienceScore = humanExperienceScore;
	}
	
	public long getTransportScore() {
		return this.transportScore;
	}
	public void setTransportScore(long transportScore) {
		this.transportScore = transportScore;
	}
	
}
