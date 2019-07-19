package com.facilio.bmsconsole.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PMPlannerSettingsContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class PMPlannerAPI {
	
	private static final JSONObject DEFAULT_VIEW_SETTING=initDefaultViewSettings();
	private static final String DEFAULT_MOVE_TYPE="askEachTime";
	private static final JSONArray DEFAULT_COLUMN_SETTINGS=initDefaultColumnSettings();
	private static final JSONArray DEFAULT_TIME_METRIC_SETTINGS=initDefaultTimeMetricSettings();
	private static final JSONArray DEFAULT_LEGEND_SETTINGS=initDefaultLegendSettings();
	private static final String GRID_LINES="gridLines";
	private static final String GROUPING="grouping";
	private static final String DISPLAY_TYPE="displayType";

	// Do not change this name as it is used for db fetch also
	public static final String CATEGORY_NAME = "categoryName";
	public static final String RESOURCE_NAME = "resourceName";
	public static final String FREQUENCY = "frequency";
	public static final String TIME_METRIC = "timeMetric";
	
	public static final String PLANNED = "planned";
	private static final String ACTUAL = "actual";
	public static final String DUE = "due";
	private static final String RESOLVED = "resolved";
	
	private static Map<String, String> metricFieldMap;
	static {
		metricFieldMap = new HashMap<>();
		metricFieldMap.put(PLANNED, "createdTime");
		metricFieldMap.put(ACTUAL, "actualWorkStart");
		metricFieldMap.put(DUE, "dueDate");
		metricFieldMap.put(RESOLVED, "actualWorkEnd");
	}
	
	public static Map<String, String> getMetricFieldMap() {
		return metricFieldMap;
	}
	
			
	public static PMPlannerSettingsContext getPMPlannerSettings() throws Exception
	{
		FacilioModule plannerModule= ModuleFactory.getPMPlannerSettingsModule();
		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
				.table(plannerModule.getTableName())
				.select(FieldFactory.getPMPlannerSettingsFields());
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(plannerModule));
		List<Map<String, Object>> settingRows=builder.get();
		if(settingRows.isEmpty())
		{
			return getDefaultSettings();
		}
		else {
			return FieldUtil.getAsBeanFromMap(settingRows.get(0),PMPlannerSettingsContext.class);
		}
		
	}

	public static void updatePMPlannerSettings(PMPlannerSettingsContext settingsContext) throws Exception{
		FacilioModule plannerModule=ModuleFactory.getPMPlannerSettingsModule();
		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
				.table(plannerModule.getTableName())
				.select(FieldFactory.getPMPlannerSettingsFields());
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(plannerModule));
		List<Map<String, Object>> settingRows=builder.get();
		
		
		
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
	private static JSONObject initDefaultViewSettings()
	{
		JSONObject viewSettingsJSON=new JSONObject();
				
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
		
		viewSettingsJSON.put("DAY",daySettings);
		viewSettingsJSON.put("WEEK",weekSettings);
		viewSettingsJSON.put("MONTH",monthSettings);
		viewSettingsJSON.put("YEAR",yearSettings);
		
				
		return viewSettingsJSON;
	}
	private static JSONArray initDefaultColumnSettings()
	{	
		JSONArray columnSettings=new JSONArray();
		
		JSONObject categoryName=new JSONObject();
		categoryName.put("name", CATEGORY_NAME);
		categoryName.put("enabled", false);
		columnSettings.add(categoryName);
		
		JSONObject resourceName=new JSONObject();
		resourceName.put("name", RESOURCE_NAME);
		resourceName.put("mandatory", true);
		resourceName.put("enabled", true);
		columnSettings.add(resourceName);
		
		JSONObject frequency=new JSONObject();
		frequency.put("name", FREQUENCY);
		frequency.put("enabled", false);
		columnSettings.add(frequency);
		
		JSONObject timeMetric=new JSONObject();
		timeMetric.put("name", TIME_METRIC);
		timeMetric.put("enabled", false);
		columnSettings.add(timeMetric);
		
		
		return columnSettings;
		
	}
	private static JSONArray initDefaultTimeMetricSettings()
	{
		JSONArray timeMetricSettings=new JSONArray();
		
		JSONObject planned=new JSONObject();
		planned.put("name", PLANNED);
		planned.put("displayName","Planned");
		planned.put("enabled", true);
		timeMetricSettings.add(planned);
		
		JSONObject actual=new JSONObject();
		actual.put("name", ACTUAL);
		actual.put("displayName","Work Start");
		actual.put("enabled", false);
		timeMetricSettings.add(actual);
		
		JSONObject due=new JSONObject();
		due.put("name", DUE);
		due.put("displayName","Due");
		due.put("enabled", false);
		timeMetricSettings.add(due);
		
		JSONObject resolved=new JSONObject();
		resolved.put("name", RESOLVED);
		resolved.put("displayName","Resolved");
		resolved.put("enabled", false);
		timeMetricSettings.add(resolved);
		
		return timeMetricSettings;
		
		
	}
	private static JSONArray initDefaultLegendSettings() {
		
		JSONArray legendSettings=new JSONArray();
		
		JSONObject none=new JSONObject();
		none.put("name","none");
		legendSettings.add(none);
				
		return legendSettings;
	}
	private static PMPlannerSettingsContext getDefaultSettings() {
		PMPlannerSettingsContext pmPLannerSettings = new PMPlannerSettingsContext();
		pmPLannerSettings.setViewSettings(DEFAULT_VIEW_SETTING);
		pmPLannerSettings.setColumnSettings(DEFAULT_COLUMN_SETTINGS);
		pmPLannerSettings.setTimeMetricSettings(DEFAULT_TIME_METRIC_SETTINGS);
        pmPLannerSettings.setMoveType(DEFAULT_MOVE_TYPE);
        pmPLannerSettings.setLegendSettings(DEFAULT_LEGEND_SETTINGS);

		return pmPLannerSettings;
	}

}
