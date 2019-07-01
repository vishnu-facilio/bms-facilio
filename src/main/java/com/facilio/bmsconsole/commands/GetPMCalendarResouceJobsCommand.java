package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
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

public class GetPMCalendarResouceJobsCommand implements Command {
	
	private static final String CATEGORY_NAME = "categoryName";
	private static final String RESOURCE_NAME = "resourceName";
	private static final String FREQUENCY = "frequency";
	private static final String TIME_METRIC = "timeMetric";
	private static final String PLANNED = "Planned";
	private static final String ACTUAL = "Actual";
	
	private static final String PLANNED_FIELD = "createdTime";
	private static final String ACTUAL_FIELD = "actualWorkStart";
	
	
	Map<Long, String> assetIdVsName;
	
	// TODO get from planner settings
	boolean showAssetCategory = true;
	boolean showFrequency = true;
	boolean showTimeMetric = true;
	
	int rowDefaultSpan;

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		List<List<Map<String, Object>>> titles = new ArrayList<>();
		List<List<Map<String, Object>>> datas = new ArrayList<>();
		
		// header name should be fieldName
		List<Map<String, Object>> headers = getHeaders();
		
		rowDefaultSpan = showTimeMetric ? 2 : 1;

		long siteId = (long) context.get(ContextNames.SITE_ID);
		
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
		resourceNameField.setName(RESOURCE_NAME);

		FacilioModule assetCategoryModule = modBean.getModule(ContextNames.ASSET_CATEGORY);
		List<FacilioField> assetCategoryFields = modBean.getAllFields(assetCategoryModule.getName());
		Map<String, FacilioField> assetCategoryFieldMap = FieldFactory.getAsMap(assetCategoryFields);
		FacilioField categoryNameField = assetCategoryFieldMap.get("name").clone();
		categoryNameField.setName(CATEGORY_NAME);
		
		String assetCategoryTable = assetCategoryModule.getTableName();

		FacilioModule pmTriggerModule = ModuleFactory.getPMTriggersModule();
		List<FacilioField> pmTriggerFields = FieldFactory.getPMTriggerFields();
		Map<String, FacilioField> pmTriggerFieldMap = FieldFactory.getAsMap(pmTriggerFields);
		String pmTriggerTable = pmTriggerModule.getTableName();

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
		if (showFrequency) {
			commonBuilder.innerJoin(pmTriggerTable).on(triggerField.getCompleteColumnName() + "=" + pmTriggerTable + ".ID");
		}
		
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(resourceNameField);
		selectFields.add(assetIdField);
		selectFields.add(countField);
		if (showAssetCategory) {
			selectFields.add(categoryNameField);
		}
		if (showFrequency) {
			selectFields.add(pmTriggerFieldMap.get("frequency"));
		}
		
		StringBuilder orderBy = new StringBuilder();
		if (showAssetCategory) {
			orderBy.append(assetCategoryFieldMap.get("name").getCompleteColumnName()).append(",");
		}
		orderBy.append(resourceNameField.getCompleteColumnName());
		if (showFrequency) {
			orderBy.append(",").append(pmTriggerFieldMap.get("frequency").getCompleteColumnName());
		}

		StringBuilder groupBy = new StringBuilder();
		groupBy.append(assetIdField.getCompleteColumnName());
		if (showFrequency) {
			groupBy.append(",").append(pmTriggerFieldMap.get("frequency").getColumnName());
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
		
		List<Map<String, Object>> props = groupBuilder.getAsProps();
		
		List<Long> assetIds = null;
		if (CollectionUtils.isNotEmpty(props)) {
			assetIds = new ArrayList<>();
			assetIdVsName = new HashMap<>();
			
			List<Map<String, Object>> prevHeaderValues = new ArrayList<>();
			
			for(Map<String, Object> prop: props) {
				
				long assetId = (Long) prop.get(assetIdField.getName());
				assetIds.add(assetId);
				assetIdVsName.put(assetId, (String) prop.get(resourceNameField.getName()));
				
				addTitles(titles, headers, prop, prevHeaderValues, countField);
			}
			
			
			selectFields = new ArrayList<>();
			selectFields.add(woFieldMap.get("subject"));
			selectFields.add(woFieldMap.get("createdTime"));
			selectFields.add(woFieldMap.get("assignedTo"));
			selectFields.add(woFieldMap.get("assignmentGroup"));
			selectFields.add(woFieldMap.get("priority"));
			selectFields.add(woFieldMap.get("category"));
			selectFields.add(woFieldMap.get("type"));
			selectFields.add(resourceField);
			selectFields.add(FieldFactory.getIdField(woModule));
			if (showTimeMetric) {
				if (!ACTUAL_FIELD.equals("createdTime")) {
					selectFields.add(woFieldMap.get(ACTUAL_FIELD));
				}
				if (!PLANNED_FIELD.equals("createdTime")) {
					selectFields.add(woFieldMap.get(PLANNED_FIELD));
				}
			}
			if (showFrequency) {
				selectFields.add(pmTriggerFieldMap.get("frequency"));
			}
			
			
			orderBy = new StringBuilder("FIELD(").append(resourceField.getCompleteColumnName()).append(", ")
					.append(StringUtils.join(assetIds, ",")).append(")");
			
			if (showFrequency) {
				orderBy.append(",").append(pmTriggerFieldMap.get("frequency").getCompleteColumnName());
//				orderBy.append(",").append("FIELD(").append(triggerField.getColumnName()).append(",")
//				.append(StringUtils.join(triggerIds, ",")).append(")");
			}
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> dataBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>(commonBuilder)
					.select(selectFields)
					.orderBy(orderBy.toString())
					;
			
			props = dataBuilder.getAsProps();
			
			if (CollectionUtils.isNotEmpty(props)) {
				List<Map<String, Object>> row;
				int totalCount = 0;
				
				for(int i = 0, rowCount = titles.size(); i < rowCount; i++) {
					row = new ArrayList<>();
					datas.add(row);
					
					List<Map<String, Object>> title = titles.get(i);
					Map<String, Object> leafNode = title.get(title.size() - 1);
					int count = (int) (long) leafNode.get("count");
					
					List<Map<String, Object>> filteredList = IntStream.range(totalCount, totalCount+count)
						             .mapToObj(props::get)
						             .collect(Collectors.toList());
					
					if (showTimeMetric) {
						addData(row, filteredList, PLANNED_FIELD, count, totalCount, false);
						sort(row);
						
						row = new ArrayList<>();
						datas.add(row);
						sort(row);
						
						addData(row, new ArrayList<>(filteredList), ACTUAL_FIELD, count, totalCount, true);
						sort(row);
						i++;
					}
					else {
						addData(row, props, "createdTime", count, totalCount, false);
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
	
	private void addTitles(List<List<Map<String, Object>>> titles, List<Map<String, Object>> headers, Map<String, Object> prop, List<Map<String, Object>> prevHeaderValues, FacilioField countField) {
		List<Map<String, Object>> row = new ArrayList<>();
		titles.add(row);
		
		boolean parentValueChanged = false;
		
		for(int i = 0, size = headers.size(); i < size; i++) {
			Map<String, Object> header = headers.get(i);
			
			String name = (String) header.get("name");
			String value;
			if (name.equals(FREQUENCY)) {
				FacilioFrequency frequency = FacilioFrequency.valueOf((int) prop.get(name));
				value = frequency.getName();
			}
			else {
				value = String.valueOf(prop.get(name));
			}
			if (name.equals(TIME_METRIC)) {
				
				Map<String, Object> plannedObj = new HashMap<>();
				plannedObj.put("name", PLANNED);
				plannedObj.put("rowSpan", 1);
				plannedObj.put("count", prop.get(countField.getName()));
				row.add(plannedObj);
				
				List<Map<String, Object>> actualRow = new ArrayList<>();
				Map<String, Object> actualObj = new HashMap<>();
				actualObj.put("name", ACTUAL);
				actualObj.put("rowSpan", 1);
				actualObj.put("count", prop.get(countField.getName()));
				actualRow.add(actualObj);
				titles.add(actualRow);
				continue;
			}
			
			
			Map<String, Object> prevHeader;
			if (prevHeaderValues.size() < (i+1) ) {
				prevHeader = new HashMap<>();
				prevHeaderValues.add(prevHeader);
			}
			else {
				prevHeader = prevHeaderValues.get(i);
			}
			
			String prevValue = (String) prevHeader.getOrDefault("name", "");
			if (!prevValue.equals(value) || parentValueChanged) {
				prevHeader = new HashMap<>();
				prevHeader.put("name", value);
				prevHeader.put("rowSpan", rowDefaultSpan);
				row.add(prevHeader);
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
		}
	}
	
	private void addData(List<Map<String, Object>> row, List<Map<String, Object>> props, String field, long count, int totalCount, boolean isClone) {
		for(int j = 0; j < props.size(); j++) {
			Map<String, Object> prop = props.get(j);
			if (!isClone) {
				Map<String, Object> resource = (Map<String, Object>) prop.get("resource");
				prop.put("asset", assetIdVsName.get(resource.get("id")));
			}
			if (showTimeMetric) {
				if (field.equals(PLANNED_FIELD)) {
					prop.put("time", PLANNED);
				}
				if (field.equals(ACTUAL_FIELD)) {
					prop.put("time", ACTUAL);
				}
			}
			
			prop = new HashMap<>(prop);
			Long date = (Long) prop.get(field);
			if (date != null && date != -1) {
				prop.put("start", date);
				row.add(prop);
			}
			
		}
	}
	
	private List<Map<String, Object>> getHeaders() {
		List<Map<String, Object>> headers = new ArrayList<>();
		if (showAssetCategory) {
			headers.add(Collections.singletonMap("name", CATEGORY_NAME));
		}
		headers.add(Collections.singletonMap("name", RESOURCE_NAME));
		if (showFrequency) {
			headers.add(Collections.singletonMap("name", FREQUENCY));
		}
		if (showTimeMetric) {
			headers.add(Collections.singletonMap("name", TIME_METRIC));
		}
		return headers;
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
