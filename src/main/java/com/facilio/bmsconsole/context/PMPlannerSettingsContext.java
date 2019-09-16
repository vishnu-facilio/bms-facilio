package com.facilio.bmsconsole.context;

import java.io.Serializable;

import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.util.FacilioUtil;

public class PMPlannerSettingsContext implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id = -1;
	private JSONObject viewSettings;
	private JSONArray columnSettings; 
	private JSONArray timeMetricSettings;
	private String moveType;
	private JSONArray legendSettings;
	private PlannerType plannerType;

	public PlannerType getPlannerTypeEnum() {
		return plannerType;
	}
	public void setPlannerType(PlannerType type) {
		this.plannerType = type;
	}
	public int getPlannerType() {
		if (plannerType != null) {
			return plannerType.getValue();
		}
		return -1;
	}
	public void setPlannerType(int type) {
		this.plannerType = PlannerType.valueOf(type);
	}
	
	
	public JSONArray getLegendSettings() {
		return legendSettings;
	}
	public void setLegendSettings(JSONArray legendSettings) {
		this.legendSettings = legendSettings;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public JSONObject getViewSettings() {
		return viewSettings;
	}
	public void setViewSettings(JSONObject viewSettings) {
		this.viewSettings = viewSettings;
	}
	public JSONArray getColumnSettings() {
		return columnSettings;
	}
	public void setColumnSettings(JSONArray columnSettings) {
		this.columnSettings = columnSettings;
	}
	public JSONArray getTimeMetricSettings() {
		return timeMetricSettings;
	}
	public void setTimeMetricSettings(JSONArray timeMetricSettings) {
		this.timeMetricSettings = timeMetricSettings;
	}
	public String getMoveType() {
		return moveType;
	}
	public void setMoveType(String moveType) {
		this.moveType = moveType;
	}
	
	@JSON(serialize = false)
	public String getColumnSettingsJson() {
		if (this.columnSettings!=null)
		return columnSettings.toJSONString();
		
		else {
			return null;
		}
	}
	public void setColumnSettingsJson(String columnSettingsJson) {
		if(columnSettingsJson!=null)
		{
		try {
			this.columnSettings = FacilioUtil.parseJsonArray(columnSettingsJson);
		} catch (ParseException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
		}
		}
	}


@JSON(serialize = false)
public String getViewSettingsJson() {
	if (this.viewSettings!=null)
	return viewSettings.toJSONString();
	else {
		return null;
	}
}
public void setViewSettingsJson(String viewSettingsJson) {
	if(viewSettingsJson!=null)
	{
	try {
		this.viewSettings = FacilioUtil.parseJson(viewSettingsJson);
	} catch (ParseException e) {
		// TODO Auto-generated catch block			
		e.printStackTrace();
	}
	}
}



@JSON(serialize = false)
public String getTimeMetricSettingsJson() {
	if (this.timeMetricSettings!=null)
	return timeMetricSettings.toJSONString();
	else {
		return null;
	}
}
public void setTimeMetricSettingsJson(String timeMetricSettingsJson) {
	if(timeMetricSettingsJson!=null)
	{
	try {
		this.timeMetricSettings = FacilioUtil.parseJsonArray(timeMetricSettingsJson);
	} catch (ParseException e) {
		// TODO Auto-generated catch block			
		e.printStackTrace();
	}
	}
}
@JSON(serialize = false)
public String getLegendSettingsJson() {
	if (this.legendSettings!=null)
	return legendSettings.toJSONString();
	else {
		return null;
	}
}
public void setLegendSettingsJson(String legendSettingsJson) {
	if(legendSettingsJson!=null)
	{
	try {
		this.legendSettings = FacilioUtil.parseJsonArray(legendSettingsJson);
	} catch (ParseException e) {
		// TODO Auto-generated catch block			
		e.printStackTrace();
	}
	}
}


public enum PlannerType {
	ASSET_PLANNER,
	STAFF_PLANNER	
	;
	
	public int getValue() {
		return ordinal() + 1;
	}
	public static PlannerType valueOf (int value) {
		if (value > 0 && value <= values().length) {
			return values() [value - 1];
		}
		return null;
	}
}

}


	
	