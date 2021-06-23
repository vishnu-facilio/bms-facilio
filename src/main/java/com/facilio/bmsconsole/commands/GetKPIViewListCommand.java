package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetKPIViewListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		long siteId = (long) context.get(ContextNames.SITE_ID);
		long buildingId = (long) context.get(ContextNames.BUILDING_ID);
		long floorId = (long) context.get(ContextNames.FLOOR_ID);
		long assetCategoryId = (long) context.get(ContextNames.CATEGORY_ID);
		
		List<Long> siteIds = (List<Long>) context.get(ContextNames.SITE_LIST);
		List<Long> buildingIds = (List<Long>) context.get(ContextNames.BUILDING_LIST);
		List<Long> floorIds = (List<Long>) context.get(ContextNames.FLOOR_LIST);
		List<Long> assetCategoryIds = (List<Long>) context.get(ContextNames.PARENT_CATEGORY_IDS);
		String groupBy = (String) context.get("groupBy");
		FacilioFrequency frequency = (FacilioFrequency) context.get(ContextNames.FREQUENCY);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		List<Long> resourceIds = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
		boolean fetchCount = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
		
		
		FacilioModule formulaModule = ModuleFactory.getFormulaFieldModule();
		String formulaTable = formulaModule.getTableName();
		List<FacilioField> formulaFields = FieldFactory.getFormulaFieldFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(formulaFields);
		
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		String resourceTable = resourceModule.getTableName();
		Map<String, FacilioField> resourceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(resourceModule.getName()));
		FacilioField resourceNameField = resourceFieldMap.get("name").clone();
		resourceNameField.setName("resourceName");
		
		FacilioModule rdmModule = ModuleFactory.getReadingDataMetaModule();
		Map<String, FacilioField> rdmFieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(formulaTable)
				.innerJoin(rdmModule.getTableName()).on(fieldMap.get("readingFieldId").getCompleteColumnName()+"="+rdmFieldMap.get("fieldId").getCompleteColumnName())
				.innerJoin(resourceTable).on(rdmFieldMap.get("resourceId").getCompleteColumnName()+"="+resourceTable+".ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formulaFieldType"), String.valueOf(FormulaFieldType.ENPI.getValue()),  NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("frequency"), String.valueOf(frequency.getValue()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("value"), "-1", StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("value"), CommonOperators.IS_NOT_EMPTY));
				;
		
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		if (view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			builder.andCriteria(view.getCriteria());
		}
		
		if (siteId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("siteId"), String.valueOf(siteId), NumberOperators.EQUALS));
		}
		if (floorId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(resourceFieldMap.get("space"), String.valueOf(floorId), BuildingOperator.BUILDING_IS));
		}
		else if (buildingId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(resourceFieldMap.get("space"), String.valueOf(buildingId), BuildingOperator.BUILDING_IS));
		}
		if (assetCategoryId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("assetCategoryId"), String.valueOf(assetCategoryId), NumberOperators.EQUALS));
		}
		
		if (CollectionUtils.isNotEmpty(siteIds)) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("siteId"), siteIds, NumberOperators.EQUALS));
		}
		
		List<Long> spaceIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(buildingIds)) {
			spaceIds.addAll(buildingIds);
		}
		if (CollectionUtils.isNotEmpty(floorIds)) {
			spaceIds.addAll(floorIds);
		}
		if (!spaceIds.isEmpty()) {
			builder.andCondition(CriteriaAPI.getCondition(resourceFieldMap.get("space"), spaceIds, BuildingOperator.BUILDING_IS));
		}
		
		if (CollectionUtils.isNotEmpty(assetCategoryIds)) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("assetCategoryId"), assetCategoryIds, NumberOperators.EQUALS));
		}
		
		if (CollectionUtils.isNotEmpty(resourceIds)) {
			builder.andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("resourceId"), resourceIds, PickListOperators.IS));
		}
		
		if (!fetchCount) {
			List<FacilioField> selectFields = new ArrayList<>();
			selectFields.add(fieldMap.get("id"));
			selectFields.add(fieldMap.get("name"));
			selectFields.add(fieldMap.get("kpiCategory"));
			selectFields.add(fieldMap.get("readingFieldId"));
			selectFields.add(fieldMap.get("minTarget"));
			selectFields.add(fieldMap.get("target"));
			selectFields.add(resourceNameField);
			selectFields.add(rdmFieldMap.get("value"));
			selectFields.add(rdmFieldMap.get("ttime"));
			selectFields.add(rdmFieldMap.get("resourceId"));
			
			String orderBy = groupBy.equals(ContextNames.KPI) ? fieldMap.get("name").getCompleteColumnName() : resourceNameField.getCompleteColumnName();
			builder.select(selectFields).orderBy(orderBy);
			
			JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
			if (pagination != null) {
				int page = (int) pagination.get("page");
				int perPage = (int) pagination.get("perPage");
				
				if (perPage != -1) {
					int offset = ((page-1) * perPage);
					if (offset < 0) {
						offset = 0;
					}
					
					builder.offset(offset);
					builder.limit(perPage);
				}
			}
		}
		else {
			builder.select(new HashSet<>()).aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(formulaModule));
		}
		
		List<Map<String, Object>> kpis = builder.get();
		if (fetchCount) {
			if (CollectionUtils.isNotEmpty(kpis)) {
				context.put(FacilioConstants.ContextNames.RECORD_COUNT, kpis.get(0).get("id"));
			}
		}
		else {
			context.put(ContextNames.KPI_LIST, kpis);
		}
		
		return false;
	}

}
