package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;

import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.time.SecondsChronoUnit;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ControllerContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String macAddr;
	public String getMacAddr() {
		return macAddr;
	}
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}
	
	
	private long siteId = -1;
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	public long getSiteId() {
		return this.siteId;
	}
	
	private List<Long> buildingIds;
	public List<Long> getBuildingIds() {
		return this.buildingIds;
	}
	
	public void setBuildingIds(List<Long> buildingIds) {
		this.buildingIds = buildingIds;
	}
	
	private int dataInterval = -1; //In minutes
	public int getDataInterval() {
		return dataInterval;
	}
	public void setDataInterval(int dataInterval) {
		this.dataInterval = dataInterval;
		this.dateIntervalUnit = new SecondsChronoUnit(dataInterval * 60);
	}
	
	private SecondsChronoUnit dateIntervalUnit;
	
	@JsonIgnore
	@JSON(serialize=false)
	public SecondsChronoUnit getDateIntervalUnit() {
		return dateIntervalUnit;
	}
	
	private int batchesPerCycle = -1;
	public int getBatchesPerCycle() {
		return batchesPerCycle;
	}
	public void setBatchesPerCycle(int batchesPerCycle) {
		this.batchesPerCycle = batchesPerCycle;
	}

	private Boolean active;
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public boolean isActive() {
		if (active != null) {
			return active.booleanValue();
		}
		return false;
	}
	
	
	private String controllerType;
	public String getControllerType() {
		return controllerType;
	}
	public void setControllerType(String controllerType) {
		this.controllerType = controllerType;
	}
	
	private String destinationId;
	public String getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}
	
	private String broadcastIp;
	public String getBroadcastIp() {
		return broadcastIp;
	}
	public void setBroadcastIp(String broadcastIp) {
		this.broadcastIp = broadcastIp;
	}
	
	private long instanceNumber = -1;
	public void setInstanceNumber(long instanceNumber) {
		this.instanceNumber = instanceNumber;
	}
	public long getInstanceNumber() {
		return this.instanceNumber;
	}
	
	
	private JSONObject controllerProps;
	
	public JSONObject getAdditionInfo() {
		return controllerProps;
	}
	public void setAdditionInfo(JSONObject additionInfo) {
		this.controllerProps = additionInfo;
	}
	public void addAdditionInfo(String key, Object value) {
		if(this.controllerProps == null) {
			this.controllerProps =  new JSONObject();
		}
		this.controllerProps.put(key,value);
	}
	
	
	public String getControllerPropsJsonStr() {
		
		if(controllerProps != null) {
			return controllerProps.toJSONString();
		}
		return null;
	}
	public void setControllerPropsJsonStr(String jsonStr) throws ParseException {
		JSONParser parser = new JSONParser();
		controllerProps = (JSONObject) parser.parse(jsonStr);
	}
	
	@Override
	 public String toString() {
		return "Controllers [id=" + id + ", orgId=" + orgId + ", name=" + name + ","
				+ " ,macAddr=" + macAddr +"]";
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
