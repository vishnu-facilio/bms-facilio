package com.facilio.bmsconsole.context;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MultiVariateAnomalyAlarm extends BaseAlarmContext{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(MultiVariateAnomalyAlarm.class.getName());

	long multivariateAnomalyId = -1;
	long causingVariableId = -1;
	long startDate = -1;
	long endDate = -1;	
	Boolean outlier;
	private JSONObject listOfVarFields;
	private String listOfVarFieldsStr;
	private JSONObject ratio;
	private String ratioStr;
	private JSONObject neighbourCount;
	private String neighbourCountStr;
	
	public void addListOfVarFields(String key, Object value) {
		if(this.listOfVarFields == null) {
			this.listOfVarFields =  new JSONObject();
		}
		this.listOfVarFields.put(key,value);
	}
	
	public String getListOfVarFieldsStr() {
		if(listOfVarFields != null) {
			return listOfVarFields.toJSONString();
		}
		return listOfVarFieldsStr;
	}
	public void setListOfVarFieldsStr(String listOfVarFieldsStr) throws ParseException {
		this.listOfVarFieldsStr = listOfVarFieldsStr;
		JSONParser parser = new JSONParser();
		this.listOfVarFields = (JSONObject) parser.parse(listOfVarFieldsStr);
	}
	
	public void addRatio(String key, Object value) {
		if(this.ratio == null) {
			this.ratio =  new JSONObject();
		}
		this.ratio.put(key,value);
	}
	
	public String getRatioStr() {
		if(ratio != null) {
			return ratio.toJSONString();
		}
		return ratioStr;
	}
	public void setRatioStr(String ratioStr) throws ParseException {
		this.ratioStr = ratioStr;
		JSONParser parser = new JSONParser();
		this.ratio = (JSONObject) parser.parse(ratioStr);
	}
	
	public void addNeighbourCount(String key, Object value) {
		if(this.neighbourCount == null) {
			this.neighbourCount =  new JSONObject();
		}
		this.neighbourCount.put(key,value);
	}
	
	public String getNeighbourCountStr() {
		if(neighbourCount != null) {
			return neighbourCount.toJSONString();
		}
		return neighbourCountStr;
	}
	public void setNeighbourCountStr(String neighbourCountStr) throws ParseException {
		this.neighbourCountStr = neighbourCountStr;
		JSONParser parser = new JSONParser();
		this.neighbourCount = (JSONObject) parser.parse(neighbourCountStr);
	}
	
	public long getMultivariateAnomalyId() {
		return multivariateAnomalyId;
	}

	public void setMultivariateAnomalyId(long multivariateAnomalyId) {
		this.multivariateAnomalyId = multivariateAnomalyId;
	}

	public long getCausingVariableId() {
		return causingVariableId;
	}

	public void setCausingVariableId(long causingVariableId) {
		this.causingVariableId = causingVariableId;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public Boolean getOutlier() {
		return outlier.booleanValue();
	}

	public void setOutlier(Boolean outlier) {
		this.outlier = outlier;
	}

	public JSONObject getListOfVarFields() {
		return listOfVarFields;
	}

	public void setListOfVarFields(JSONObject listOfVarFields) {
		this.listOfVarFields = listOfVarFields;
	}

	public JSONObject getRatio() {
		return ratio;
	}

	public void setRatio(JSONObject ratio) {
		this.ratio = ratio;
	}

	public JSONObject getNeighbourCount() {
		return neighbourCount;
	}

	public void setNeighbourCount(JSONObject neighbourCount) {
		this.neighbourCount = neighbourCount;
	}
}
