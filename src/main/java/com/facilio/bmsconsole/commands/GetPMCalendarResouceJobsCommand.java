package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlannerSettingsContext;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.PMPlannerAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class GetPMCalendarResouceJobsCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(GetPMCalendarResouceJobsCommand.class.getName());
	
	private Map<String, String> metricFieldMap = null;
	
	private List<String> selectedMetrics;
	private Map<String, String> metricFieldNameMap;
	
	Map<Long, String> assetIdVsName;
	
	boolean showAssetCategory = false;
	boolean showFrequency = false;
	boolean showTimeMetric = false;
	
	int rowDefaultSpan;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		metricFieldMap = PMPlannerAPI.getMetricFieldMap();
		
		handleSettings(context);
		
		List<Map<String, Object>> titles = new ArrayList<>();
		List<List<Map<String, Object>>> datas = new ArrayList<>();
		
		// header name should be fieldName
		List<Map<String, Object>> headers = getHeaders();
		
		rowDefaultSpan = showTimeMetric ? selectedMetrics.size() : 1;

		long siteId = (long) context.get(ContextNames.SITE_ID);
		long buildingId = (long) context.get(ContextNames.BUILDING_ID);
		long categoryId = (long) context.get(ContextNames.CATEGORY_ID);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		
		DateOperators operator = DateOperators.CURRENT_YEAR;
		DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		if (dateRange == null) {
			Integer dateOperatorInt = (Integer) context.get(FacilioConstants.ContextNames.DATE_OPERATOR);
			if (dateOperatorInt != null && dateOperatorInt > -1) {
				String dateOperatorValue = (String) context.get(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE);
				operator = (DateOperators) Operator.getOperator(dateOperatorInt);
				dateRange = operator.getRange(dateOperatorValue);
			}
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule woModule = modBean.getModule(ContextNames.WORK_ORDER);
		List<FacilioField> woFields = new ArrayList<>(modBean.getAllFields(woModule.getName()));

		FacilioModule ticketModule = modBean.getModule(ContextNames.TICKET);
		List<FacilioField> ticketFields = modBean.getAllFields(ticketModule.getName());
		woFields.addAll(ticketFields);
		
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);

		FacilioModule assetModule = modBean.getModule(ContextNames.ASSET);
		List<FacilioField> assetFields = modBean.getAllFields(assetModule.getName());
		FacilioField assetIdField = FieldFactory.getIdField(assetModule);
		Map<String, FacilioField> assetFieldMap = FieldFactory.getAsMap(assetFields);
		String assetTable = assetModule.getTableName();

		FacilioModule resourceModule = modBean.getModule(ContextNames.RESOURCE);
		List<FacilioField> resourceFields = modBean.getAllFields(resourceModule.getName());
		Map<String, FacilioField> resourceFieldMap = FieldFactory.getAsMap(resourceFields);
		String resourceTable = resourceModule.getTableName();
		FacilioField resourceNameField = resourceFieldMap.get("name").clone();
		resourceNameField.setName(PMPlannerAPI.RESOURCE_NAME);

		FacilioModule assetCategoryModule = modBean.getModule(ContextNames.ASSET_CATEGORY);
		List<FacilioField> assetCategoryFields = modBean.getAllFields(assetCategoryModule.getName());
		Map<String, FacilioField> assetCategoryFieldMap = FieldFactory.getAsMap(assetCategoryFields);
		FacilioField categoryNameField = assetCategoryFieldMap.get("name").clone();
		categoryNameField.setName(PMPlannerAPI.CATEGORY_NAME);
		
		String assetCategoryTable = assetCategoryModule.getTableName();

		FacilioModule pmTriggerModule = ModuleFactory.getPMTriggersModule();
		List<FacilioField> pmTriggerFields = FieldFactory.getPMTriggerFields();
		Map<String, FacilioField> pmTriggerFieldMap = FieldFactory.getAsMap(pmTriggerFields);
		String pmTriggerTable = pmTriggerModule.getTableName();
		FacilioField frequencyField = pmTriggerFieldMap.get("frequency");

		FacilioField countField = FieldFactory.getCountField(woModule).get(0);
		
		FacilioField triggerField = woFieldMap.get("trigger");
		FacilioField resourceField = woFieldMap.get("resource");


		SelectRecordsBuilder<ModuleBaseWithCustomFields> commonBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(woModule)
				.andCondition(CriteriaAPI.getCondition(woFieldMap.get("pm"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(woModule), String.valueOf(siteId) , NumberOperators.EQUALS))
				;
		if (dateRange != null) {
			commonBuilder.andCondition(CriteriaAPI.getCondition(woFieldMap.get("createdTime"), dateRange.toString(), DateOperators.BETWEEN));
		}
		else {
			commonBuilder.andCondition(CriteriaAPI.getCondition(woFieldMap.get("createdTime"), operator));
		}
		if (filterCriteria != null) {
			commonBuilder.andCriteria(filterCriteria);
		}
		
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(resourceNameField);
		selectFields.add(assetIdField);
		selectFields.add(countField);
		if (showAssetCategory) {
			selectFields.add(categoryNameField);
		}
		if (showFrequency) {
			selectFields.add(frequencyField);
		}
		
		StringBuilder orderBy = new StringBuilder();
		if (showAssetCategory) {
			orderBy.append(assetCategoryFieldMap.get("name").getCompleteColumnName()).append(",");
		}
		orderBy.append(resourceNameField.getCompleteColumnName());
		if (showFrequency) {
			orderBy.append(",").append(frequencyField.getCompleteColumnName());
		}

		StringBuilder groupBy = new StringBuilder();
		groupBy.append(assetIdField.getCompleteColumnName());
		if (showFrequency) {
			groupBy.append(",").append(frequencyField.getColumnName());
		}
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> groupBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>(commonBuilder)
				.select(selectFields)
				.innerJoin(assetTable).on(resourceField.getCompleteColumnName() + "=" + assetTable + ".ID")
				.innerJoin(resourceTable).on(assetIdField.getCompleteColumnName() + "=" + resourceTable + ".ID")
				.groupBy(groupBy.toString())
				.orderBy(orderBy.toString())
				;
		
		if (showAssetCategory) {
			groupBuilder.innerJoin(assetCategoryTable).on(assetFieldMap.get("category").getCompleteColumnName() + "=" + assetCategoryTable + ".ID");
		}
		if (showFrequency) {
			groupBuilder.innerJoin(pmTriggerTable).on(triggerField.getCompleteColumnName() + "=" + pmTriggerTable + ".ID");
		}
		else {
			groupBuilder.andCondition(CriteriaAPI.getCondition(triggerField, CommonOperators.IS_NOT_EMPTY));
		}
		if (buildingId > 0) {
			groupBuilder.andCondition(CriteriaAPI.getCondition(resourceFieldMap.get("space"), String.valueOf(buildingId), BuildingOperator.BUILDING_IS));
		}
		if (categoryId > 0) {
			groupBuilder.andCondition(CriteriaAPI.getCondition(assetFieldMap.get("category"), String.valueOf(categoryId), NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = groupBuilder.getAsProps();
		
		LOGGER.debug("Group by builder: " + groupBuilder.toString());
		
		List<Long> assetIds = null;
		if (CollectionUtils.isNotEmpty(props)) {
			assetIds = new ArrayList<>();
			assetIdVsName = new HashMap<>();
			
			List<Map<String, Object>> prevHeaderValues = new ArrayList<>();
			
			for(Map<String, Object> prop: props) {
				
				long assetId = (Long) prop.get(assetIdField.getName());
				assetIds.add(assetId);
				assetIdVsName.put(assetId, (String) prop.get(resourceNameField.getName()));
				
				addTitles(titles, assetId, headers, prop, prevHeaderValues, countField);
			}
			
			LOGGER.debug("titles: " + titles.toString());
			
			selectFields = new ArrayList<>();
			selectFields.add(woFieldMap.get("subject"));
			selectFields.add(woFieldMap.get("createdTime"));
			selectFields.add(woFieldMap.get("assignedTo"));
			selectFields.add(woFieldMap.get("assignmentGroup"));
			selectFields.add(woFieldMap.get("priority"));
			selectFields.add(woFieldMap.get("category"));
			selectFields.add(woFieldMap.get("type"));
			selectFields.add(resourceField);
			selectFields.add(frequencyField);
			selectFields.add(FieldFactory.getIdField(woModule));
			if (showTimeMetric) {
				for(String metric: selectedMetrics) {
					if (!metric.equals("createdTime")) {
						selectFields.add(woFieldMap.get(metricFieldMap.get(metric)));
					}
				}
			}
			
			
			orderBy = new StringBuilder("FIELD(").append(resourceField.getCompleteColumnName()).append(", ")
					.append(StringUtils.join(assetIds, ",")).append(")");
			
			if (showFrequency) {
				orderBy.append(",").append(frequencyField.getCompleteColumnName());
//				orderBy.append(",").append("FIELD(").append(triggerField.getColumnName()).append(",")
//				.append(StringUtils.join(triggerIds, ",")).append(")");
			}
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> dataBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>(commonBuilder)
					.select(selectFields)
					.innerJoin(pmTriggerTable).on(triggerField.getCompleteColumnName() + "=" + pmTriggerTable + ".ID")
					.andCondition(CriteriaAPI.getCondition(resourceField, assetIds, NumberOperators.EQUALS))
					.orderBy(orderBy.toString())
					;
			
			props = dataBuilder.getAsProps();
			
			LOGGER.debug("Data builder: " + dataBuilder.toString());
			
			if (CollectionUtils.isNotEmpty(props)) {
				List<Map<String, Object>> row;
				int totalCount = 0;
				
				for(int i = 0, rowCount = titles.size(); i < rowCount; i++) {
					row = new ArrayList<>();
					datas.add(row);
					
					Map<String, Object> titleData = titles.get(i);
					List<Map<String, Object>> titleRow = (List<Map<String, Object>>) titleData.get("data");
					Map<String, Object> leafNode = titleRow.get(titleRow.size() - 1);
					int count = (int) (long) leafNode.get("count");
					
					List<Map<String, Object>> filteredList = props.subList(totalCount, totalCount+count);
					
					if (showTimeMetric) {
						for(int j = 0; j < selectedMetrics.size(); j++) {
							String metric = selectedMetrics.get(j);
							
							if (j != 0) {
								row = new ArrayList<>();
								datas.add(row);
								sort(row);
								
								i++;
							}
							
							addData(row, new ArrayList<>(filteredList), metric, count, totalCount, j != 0);
							sort(row);
							
						}
					}
					else {
						addData(row, filteredList, PMPlannerAPI.PLANNED, count, totalCount, false);
						sort(row);
					}
					
					totalCount += count;
				}
			}
		}

		JSONObject result = new JSONObject();
		result.put("resourceHeaders", headers);
		result.put("resourceTitles", titles);
		result.put("data", datas);

		context.put(ContextNames.RESULT, result);

		return false;
	}
	
	private void addTitles(List<Map<String, Object>> titles, long assetId, List<Map<String, Object>> headers, Map<String, Object> prop, List<Map<String, Object>> prevHeaderValues, FacilioField countField) {
		Map<String, Object> row = new HashMap<>();
		List<Map<String, Object>> rowData = new ArrayList<>();
		row.put("data", rowData);
		row.put("id", assetId);
		if (showAssetCategory) {
			row.put("resourceGroup", prop.get(PMPlannerAPI.CATEGORY_NAME));
		}
		titles.add(row);
		
		boolean parentValueChanged = false;
		
		for(int i = 0, size = headers.size(); i < size; i++) {
			Map<String, Object> header = headers.get(i);
			
			String name = (String) header.get("name");
			String value;
			if (name.equals(PMPlannerAPI.FREQUENCY)) {
				FacilioFrequency frequency = FacilioFrequency.valueOf((int) prop.get(name));
				value = frequency.getName();
			}
			else {
				value = String.valueOf(prop.get(name));
			}
			if (name.equals(PMPlannerAPI.TIME_METRIC)) {
				for(int j = 0; j < selectedMetrics.size(); j++) {
					String metric = selectedMetrics.get(j);
					
					Map<String, Object> metricObj = new HashMap<>();
					metricObj.put("name", metricFieldNameMap.get(metric));
					metricObj.put("rowSpan", 1);
					metricObj.put("count", prop.get(countField.getName()));
					
					boolean enabled = metric.equals(PMPlannerAPI.PLANNED) || metric.equals(PMPlannerAPI.DUE);
					
					if (j == 0) {
						if (enabled) {
							row.put("editable", true);
						}
						rowData.add(metricObj);
					}
					else {
						List<Map<String, Object>> newRowData = new ArrayList<>();
						newRowData.add(metricObj);
						row = new HashMap<>();
						row.put("data", newRowData);
						row.put("id", assetId);
						if (enabled) {
							row.put("editable", true);
						}
						if (showAssetCategory) {
							row.put("resourceGroup", prop.get(PMPlannerAPI.CATEGORY_NAME));
						}
						titles.add(row);
					}
				}
			}
			else {
				Map<String, Object> prevHeader;
				if (prevHeaderValues.size() < (i+1) ) {
					prevHeader = new HashMap<>();
					prevHeaderValues.add(prevHeader);
				}
				else {
					prevHeader = prevHeaderValues.get(i);
				}
				
				String prevValue = (String) prevHeader.getOrDefault("name", "");
				if (!prevValue.equals(value) || parentValueChanged || i == 0) {	// If Asset column or previous column value changed
					prevHeader = new HashMap<>();
					prevHeader.put("name", value);
					prevHeader.put("rowSpan", rowDefaultSpan);
					rowData.add(prevHeader);
					prevHeaderValues.add(i,  prevHeader);
					parentValueChanged = true;
				}
				else {
					int rowSpan = (int) prevHeader.get("rowSpan");
					prevHeader.put("rowSpan", rowSpan + rowDefaultSpan);
				}
				if (i + 1 == size) {
					prevHeader.put("count", prop.get(countField.getName()));
				}
				row.put("editable", true);
			}
		}
	}
	
	private void addData(List<Map<String, Object>> row, List<Map<String, Object>> props, String metricField, long count, int totalCount, boolean isClone) {
		for(int j = 0; j < props.size(); j++) {
			Map<String, Object> prop = props.get(j);
			if (!isClone) {
				Map<String, Object> resource = (Map<String, Object>) prop.get("resource");
				prop.put("asset", assetIdVsName.get(resource.get("id")));
			}
			prop.put("time", metricField);
			
			prop = new HashMap<>(prop);
			Long date = (Long) prop.get(metricFieldMap.get(metricField));
			if (date != null && date != -1) {
				prop.put("start", date);
				row.add(prop);
			}
			
		}
	}
	
	private List<Map<String, Object>> getHeaders() {
		List<Map<String, Object>> headers = new ArrayList<>();
		headers.add(Collections.singletonMap("name", PMPlannerAPI.RESOURCE_NAME));
		if (showFrequency) {
			headers.add(Collections.singletonMap("name", PMPlannerAPI.FREQUENCY));
		}
		if (showTimeMetric) {
			headers.add(Collections.singletonMap("name", PMPlannerAPI.TIME_METRIC));
		}
		return headers;
	}
	
	private void handleSettings(Context context) {
		PMPlannerSettingsContext plannerSettings = (PMPlannerSettingsContext) context.get(ContextNames.PM_PLANNER_SETTINGS);
		
		JSONArray columnSettings = plannerSettings.getColumnSettings();
		for(int i=0; i < columnSettings.size(); i++) {
			JSONObject columnObj = (JSONObject) columnSettings.get(i);
			boolean enabled = (boolean) columnObj.get("enabled");
			if (!enabled) {
				continue;
			}
			
			String name = (String) columnObj.get("name");
			switch (name) {
				case PMPlannerAPI.CATEGORY_NAME:
					showAssetCategory = true;
					break;
					
				case PMPlannerAPI.FREQUENCY:
					showFrequency = true;
					break;
					
				case PMPlannerAPI.TIME_METRIC:
					showTimeMetric = true;
					break;
			}
		}
		
		if (showTimeMetric) {
			selectedMetrics = new ArrayList<>();
			metricFieldNameMap = new HashMap<>();
			JSONArray metricSettings = plannerSettings.getTimeMetricSettings();
			for(int i=0; i < metricSettings.size(); i++) {
				JSONObject metricObj = (JSONObject) metricSettings.get(i);
				boolean enabled = (boolean) metricObj.get("enabled");
				if (!enabled) {
					continue;
				}
				
				String name = (String) metricObj.get("name");
				String displayName = (String) metricObj.get("displayName");
				selectedMetrics.add(name);
				metricFieldNameMap.put(name, displayName);
			}
		}
	}
	
	private void sort(List<Map<String, Object>> props) {
		props.sort((m1,m2) -> {
			long d1 = (long) m1.get("start");
			long d2 = (long) m2.get("start");
			if(d1 == d2){
		         return 0;
		    }
			return d1 < d2 ? -1 : 1;
		});
	}

}
