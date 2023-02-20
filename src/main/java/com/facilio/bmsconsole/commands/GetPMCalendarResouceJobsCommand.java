package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.util.FacilioUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.PMPlannerSettingsContext;
import com.facilio.bmsconsole.context.PMPlannerSettingsContext.PlannerType;
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
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class GetPMCalendarResouceJobsCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(GetPMCalendarResouceJobsCommand.class.getName());
	
	private Map<String, String> metricFieldMap = null;
	
	private List<String> selectedMetrics;
	private Map<String, String> metricFieldNameMap;
	
	Map<Long, String> resourceIdVsName;//in this case its either assets.ID or basespaces.ID
	
	boolean showCategory = false;
	boolean showFrequency = false;
	boolean showTimeMetric = false;
	PlannerType plannerType;
	int rowDefaultSpan;

	long totalRecordCount = 0;
	
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
		long floorId = (long) context.get(ContextNames.FLOOR_ID);
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
		LOGGER.debug("Data builder ticketFields: " + ticketFields);
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);
		LOGGER.debug("Data builder woFieldMap: " + woFieldMap);
		//can be asset or basespace module
		FacilioModule plannerResourceModule=null;
		List<FacilioField> plannerResourceFields;
		FacilioField plannerResourceIdField;
		Map<String, FacilioField> plannerResourceFieldMap;
		String plannerResourceTable;
		
		//can be asset_category or space_category
		FacilioModule plannerResourceCategoryModule=null;
		List<FacilioField> plannerResourceCategoryFields ;
		Map<String, FacilioField> plannerResourceCategoryFieldMap;
		FacilioField categoryNameField;
		String plannerResourceCategoryTable;
		
				
		FacilioModule spaceModule=modBean.getModule(ContextNames.SPACE);
		List<FacilioField> spaceFields=modBean.getAllFields(spaceModule.getName());
		Map<String,FacilioField> spaceFieldMap=FieldFactory.getAsMap(spaceFields);
		FacilioField spaceCategoryField=spaceFieldMap.get("spaceCategory");
		FacilioField spaceTypeField=spaceFieldMap.get("spaceType");
		String spaceTableName=spaceModule.getTableName();
		FacilioField spaceIdField=FieldFactory.getIdField(spaceModule);
		
		
		if(plannerType.equals(PMPlannerSettingsContext.PlannerType.ASSET_PLANNER))
		{
			
			plannerResourceModule = modBean.getModule(ContextNames.ASSET);
			
			plannerResourceCategoryModule=modBean.getModule(ContextNames.ASSET_CATEGORY);

		}
		else if(plannerType.equals(PMPlannerSettingsContext.PlannerType.SPACE_PLANNER)){//type = space planner
			plannerResourceModule = modBean.getModule(ContextNames.BASE_SPACE);
			plannerResourceCategoryModule=modBean.getModule(ContextNames.SPACE_CATEGORY);
		}
			
			plannerResourceFields = modBean.getAllFields(plannerResourceModule.getName());
			plannerResourceIdField = FieldFactory.getIdField(plannerResourceModule);
			plannerResourceFieldMap= FieldFactory.getAsMap(plannerResourceFields);
			plannerResourceTable= plannerResourceModule.getTableName();
		
			
			
			plannerResourceCategoryFields = modBean.getAllFields(plannerResourceCategoryModule.getName());
			plannerResourceCategoryFieldMap = FieldFactory.getAsMap(plannerResourceCategoryFields);
			plannerResourceCategoryTable=plannerResourceCategoryModule.getTableName();
			categoryNameField = plannerResourceCategoryFieldMap.get("name").clone();
			
			
			categoryNameField.setName(PMPlannerAPI.CATEGORY_NAME);

		
		FacilioModule resourceModule = modBean.getModule(ContextNames.RESOURCE);
		List<FacilioField> resourceFields = modBean.getAllFields(resourceModule.getName());
		Map<String, FacilioField> resourceFieldMap = FieldFactory.getAsMap(resourceFields);
		String resourceTable = resourceModule.getTableName();
		FacilioField resourceNameField = resourceFieldMap.get("name").clone();
		resourceNameField.setName(PMPlannerAPI.RESOURCE_NAME);

		
		
		
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
				.skipModuleCriteria();
		if(siteId > 0){
			commonBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(woModule), String.valueOf(siteId) , NumberOperators.EQUALS));
		}

		Criteria criteria = new Criteria();
		for(String metric: selectedMetrics) {
			FacilioField field = woFieldMap.get(metricFieldMap.get(metric));
			if (dateRange != null) {
				criteria.addOrCondition(CriteriaAPI.getCondition(field, dateRange.toString(), DateOperators.BETWEEN));
			}
			else {
				criteria.addOrCondition(CriteriaAPI.getCondition(field, operator));
			}
		}
		commonBuilder.andCriteria(criteria);
		
		if (filterCriteria != null) {
			commonBuilder.andCriteria(filterCriteria);
		}
		
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(resourceNameField);
		selectFields.add(plannerResourceIdField);
		selectFields.add(countField);
		if (showCategory) {
			selectFields.add(categoryNameField);
			//need basespace type also for space planner
			if(plannerType.equals(PlannerType.SPACE_PLANNER))
			{
			selectFields.add(spaceTypeField);
			}
		}
		if (showFrequency) {
			selectFields.add(frequencyField);
		}
		
		StringBuilder orderBy = new StringBuilder();
		if (showCategory) {//need to change settings variable names to refer to both space and asset
			//for space planner order by space_type from basespaces =>site/building/space
			if(plannerType.equals(PlannerType.SPACE_PLANNER))
			{
				orderBy.append(plannerResourceFieldMap.get("spaceType").getCompleteColumnName()).append(",");
			}
			orderBy.append(categoryNameField.getCompleteColumnName()).append(",");
			
			
		}
		orderBy.append(resourceNameField.getCompleteColumnName());
		if (showFrequency) {
			orderBy.append(",").append(frequencyField.getCompleteColumnName());
		}

		StringBuilder groupBy = new StringBuilder();
		groupBy.append(FieldFactory.getSiteIdField(woModule).getCompleteColumnName());
		groupBy.append(",").append(plannerResourceIdField.getCompleteColumnName());
		if (showFrequency) {
			groupBy.append(",").append(frequencyField.getColumnName());
		}
		
		// To get the title part bases on asset (left side of the calendar)
		SelectRecordsBuilder<ModuleBaseWithCustomFields> groupBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>(commonBuilder)
				.select(selectFields)
				.innerJoin(plannerResourceTable).on(resourceField.getCompleteColumnName() + "=" + plannerResourceTable + ".ID")
				.innerJoin(resourceTable).on(plannerResourceIdField.getCompleteColumnName() + "=" + resourceTable + ".ID")
				.groupBy(groupBy.toString())
				.orderBy(orderBy.toString())
				;
		
		if (showCategory) {
			if(plannerType.equals(PlannerType.ASSET_PLANNER))
			{
				groupBuilder.innerJoin(plannerResourceCategoryTable).on(plannerResourceFieldMap.get("category").getCompleteColumnName() + "=" + plannerResourceCategoryTable + ".ID");
			}
			else if(plannerType.equals(PlannerType.SPACE_PLANNER)){
				//get space category for all basespaces with type=4/space by joining basespace with space and spacecategory tables,order by spacetype and then spacecategory
				groupBuilder.leftJoin(spaceTableName).on(plannerResourceIdField.getCompleteColumnName() + "=" +spaceIdField.getCompleteColumnName());
				
				groupBuilder.leftJoin(plannerResourceCategoryTable).on(spaceCategoryField.getCompleteColumnName() + "=" + plannerResourceCategoryTable + ".ID");
			}
			
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
		if (floorId > 0) {
			groupBuilder.andCondition(CriteriaAPI.getCondition(resourceFieldMap.get("space"), String.valueOf(floorId), BuildingOperator.BUILDING_IS));
		}

		//for space this filter is never sent 
		if (categoryId > 0) {
			groupBuilder.andCondition(CriteriaAPI.getCondition(plannerResourceFieldMap.get("category"), String.valueOf(categoryId), NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = groupBuilder.getAsProps();
		
		//for space planner with category enabled, for basespace type other than 4(space) , and space(type 4) without category just put category as basespace type itself 
		if(plannerType.equals(PlannerType.SPACE_PLANNER)&&showCategory)
			
		props.forEach((prop)->{
//			System.out.println("Space Type="+prop.get("spaceType"));
//			System.out.println("Space Category="+prop.get("categoryName"));
			Integer spaceTypeNumVal=(Integer)prop.get("spaceType");
			//for basesspaces other than space and for type space without category
			if(spaceTypeNumVal!=SpaceType.SPACE.getIntVal()||prop.get("categoryName")==null)
			{
				String spaceTypeString=SpaceType.getType(spaceTypeNumVal).getStringVal();
				 
				prop.put("categoryName", spaceTypeString);
			}
			
		});
		
		LOGGER.debug("Group by builder: " + groupBuilder.toString());
		
		List<Long> resourceIds = null;
		if (CollectionUtils.isNotEmpty(props)) {
			resourceIds = new ArrayList<>();
			resourceIdVsName = new HashMap<>();
			
			List<Map<String, Object>> prevHeaderValues = new ArrayList<>();
			
			long prevAssetId = -1;
			for(Map<String, Object> prop: props) {
				
				long assetId = (Long) prop.get(plannerResourceIdField.getName());
				resourceIds.add(assetId);
				resourceIdVsName.put(assetId, (String) prop.get(resourceNameField.getName()));
				
				addTitles(titles, assetId, prevAssetId, headers, prop, prevHeaderValues, countField);
				
				prevAssetId = assetId;
			}
			
			LOGGER.debug("titles: " + titles.toString());
			
			selectFields = new ArrayList<>();
			selectFields.add(woFieldMap.get("subject"));
			selectFields.add(woFieldMap.get("assignedTo"));
			selectFields.add(woFieldMap.get("assignmentGroup"));
			selectFields.add(woFieldMap.get("priority"));
			selectFields.add(woFieldMap.get("category"));
			selectFields.add(woFieldMap.get("type"));
			selectFields.add(woFieldMap.get("moduleState"));
			selectFields.add(woFieldMap.get("createdTime")); //for backward compatibility
			selectFields.add(resourceField);
			selectFields.add(frequencyField);
			selectFields.add(FieldFactory.getIdField(woModule));

			LOGGER.debug("Data builder selectedMetrics: " + selectedMetrics);

			for (String metric : selectedMetrics) {
				selectFields.add(woFieldMap.get(metricFieldMap.get(metric)));
			}
			List<FacilioField> enumCustomFields = woFields.stream().filter(field -> !field.isDefault() && field instanceof EnumField).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(enumCustomFields)) {
				selectFields.addAll(enumCustomFields);
			}


			orderBy = new StringBuilder("FIELD(").append(resourceField.getCompleteColumnName()).append(", ")
					.append(StringUtils.join(resourceIds, ",")).append(")");

			if (showFrequency) {
				orderBy.append(",").append(frequencyField.getCompleteColumnName());
//				orderBy.append(",").append("FIELD(").append(triggerField.getColumnName()).append(",")
//				.append(StringUtils.join(triggerIds, ",")).append(")");
			}

			LOGGER.debug("Data builder SelectFields: " + selectFields);

			// To get the jobs data
			SelectRecordsBuilder<ModuleBaseWithCustomFields> dataBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>(commonBuilder)
					.select(selectFields)
					.innerJoin(pmTriggerTable).on(triggerField.getCompleteColumnName() + "=" + pmTriggerTable + ".ID")
					.andCondition(CriteriaAPI.getCondition(resourceField, resourceIds, NumberOperators.EQUALS))
					.orderBy(orderBy.toString())
					.limit((int) totalRecordCount);

			props = dataBuilder.getAsProps();

			LOGGER.debug("Data builder: " + dataBuilder.toString());
			LOGGER.debug("Data builder props: " + props);

			if (CollectionUtils.isNotEmpty(props)) {
				List<Map<String, Object>> row;
				long totalCount = 0;

				for (int i = 0, rowCount = titles.size(); i < rowCount; i++) {
					row = new ArrayList<>();
					datas.add(row);

					Map<String, Object> titleData = titles.get(i);
					List<Map<String, Object>> titleRow = (List<Map<String, Object>>) titleData.get("data");
					Map<String, Object> leafNode = titleRow.get(titleRow.size() - 1);
					long count = FacilioUtil.parseLong(leafNode.get("count"));
					List<Map<String, Object>> filteredList = props.subList((int) totalCount, (int) totalCount + (int) count);

					if (showTimeMetric) {
						for (int j = 0; j < selectedMetrics.size(); j++) {
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
						// Assuming only 1 metric will be selected when no calendar column is selected
						addData(row, filteredList, selectedMetrics.get(0), count, totalCount, false);
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
	
	private void addTitles(List<Map<String, Object>> titles, long assetId, long prevAssetId, List<Map<String, Object>> headers, Map<String, Object> prop, List<Map<String, Object>> prevHeaderValues, FacilioField countField) {
		Map<String, Object> row = new HashMap<>();
		List<Map<String, Object>> rowData = new ArrayList<>();
		row.put("data", rowData);
		row.put("id", assetId);
		if (showCategory) {
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
					long count = FacilioUtil.parseLong(prop.get(countField.getName()));
					metricObj.put("count",count);
					totalRecordCount += count;
					
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
						if (showCategory) {
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
				// If Asset column or previous column value changed
				if (!prevValue.equals(value) || parentValueChanged || (i == 0 && prevAssetId != assetId) ) {
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
					long count = FacilioUtil.parseLong(prop.get(countField.getName()));
					prevHeader.put("count", count);
					totalRecordCount += count;
				}
				row.put("editable", true);
			}
		}
	}

	private void addData(List<Map<String, Object>> row, List<Map<String, Object>> props, String metricField, long count, long totalCount, boolean isClone) {
		for (int j = 0; j < props.size(); j++) {
			Map<String, Object> prop = props.get(j);
			if (!isClone) {
				Map<String, Object> resource = (Map<String, Object>) prop.get("resource");
				if (plannerType.equals(plannerType.ASSET_PLANNER)) {
					prop.put("asset", resourceIdVsName.get(resource.get("id")));
				} else if (plannerType.equals(plannerType.SPACE_PLANNER)) {
					prop.put("space", resourceIdVsName.get(resource.get("id")));
				}
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
		plannerType=plannerSettings.getPlannerTypeEnum();
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
					showCategory = true;
					break;
					
				case PMPlannerAPI.FREQUENCY:
					showFrequency = true;
					break;
					
				case PMPlannerAPI.TIME_METRIC:
					showTimeMetric = true;
					break;
			}
		}
		
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
