package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PMPlannerSettingsContext;

public class PMPlannerAPI {
	
	private static final String DEFAULT_SETTING_JSON_STRING=initDefaultSettingsJSON();
	private static final String GRID_LINES="gridLines";
	private static final String GROUPING="grouping";
	private static final String DISPLAY_TYPE="displayType";
	
			
	public static PMPlannerSettingsContext getPMPlannerSettings() throws Exception
	{
		FacilioModule plannerModule= ModuleFactory.getPMPlannerSettingsModule();
		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
				.table(plannerModule.getTableName())
				.select(FieldFactory.getPMPlannerSettingsFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(plannerModule));
		List<Map<String, Object>> settingRows=builder.get();
		if(settingRows.isEmpty())
		{
			return getDefaultSettings();
		}
		else {
			return FieldUtil.getAsBeanFromMap(settingRows.get(0),PMPlannerSettingsContext.class);
		}
		
	}

	public static void updatePMPlannerSettings(String settingsJson) throws Exception{
		FacilioModule plannerModule=ModuleFactory.getPMPlannerSettingsModule();
		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
				.table(plannerModule.getTableName())
				.select(FieldFactory.getPMPlannerSettingsFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(plannerModule));
		List<Map<String, Object>> settingRows=builder.get();
		
		PMPlannerSettingsContext settingsContext=new PMPlannerSettingsContext();
		settingsContext.setSettingsJSON(settingsJson);
		Map<String,Object> props=FieldUtil.getAsProperties(settingsContext);

		
		if(settingRows.isEmpty())
		{
			GenericInsertRecordBuilder insertBuilder=new GenericInsertRecordBuilder()
					.table(plannerModule.getTableName())
					.fields(FieldFactory.getPMPlannerSettingsFields())
					.addRecord(props);
			insertBuilder.save();
					
			
		}
		else {
			
			long settingId=FieldUtil.getAsBeanFromMap(settingRows.get(0),PMPlannerSettingsContext.class).getId();
			GenericUpdateRecordBuilder updateBuilder=new GenericUpdateRecordBuilder()
					.table(plannerModule.getTableName())
					.fields(FieldFactory.getPMPlannerSettingsFields())
					.andCondition(CriteriaAPI.getIdCondition(settingId, plannerModule));
			updateBuilder.update(props);
		}
		
	}
	private static String initDefaultSettingsJSON()
	{
		JSONObject settingsJSON=new JSONObject();
		JSONObject viewJSON=new JSONObject();
		
		JSONObject daySettings=new JSONObject();
		daySettings.put(GRID_LINES,"1H");
		daySettings.put(GROUPING,"4H");
		daySettings.put(DISPLAY_TYPE,"dateTime");
		
		JSONObject weekSettings=new JSONObject();
		weekSettings.put(GRID_LINES,"DAY");
		weekSettings.put(GROUPING,"NONE");
		weekSettings.put(DISPLAY_TYPE,"dateTime");
		
		JSONObject monthSettings=new JSONObject();
		monthSettings.put(GRID_LINES,"DAY");
		monthSettings.put(GROUPING,"NONE");
		monthSettings.put(DISPLAY_TYPE,"dateTime");
		
		JSONObject yearSettings=new JSONObject();
		yearSettings.put(GRID_LINES,"WEEK");
		yearSettings.put(GROUPING,"MONTH");
		yearSettings.put(DISPLAY_TYPE,"frequency");
		
		viewJSON.put("DAY",daySettings);
		viewJSON.put("WEEK",weekSettings);
		viewJSON.put("MONTH",monthSettings);
		viewJSON.put("YEAR",yearSettings);
		
		settingsJSON.put("viewSettings", viewJSON);
		settingsJSON.put("moveType","askEachTime");
		
		return settingsJSON.toJSONString();
	}
	
	private static PMPlannerSettingsContext getDefaultSettings() {
		PMPlannerSettingsContext pmPLannerSettings = new PMPlannerSettingsContext();
		
		pmPLannerSettings.setSettingsJSON(DEFAULT_SETTING_JSON_STRING);
		return pmPLannerSettings;
	}

}
