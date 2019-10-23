package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
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
		long assetCategoryId = (long) context.get(ContextNames.CATEGORY_ID);
		String groupBy = (String) context.get("groupBy");
		FacilioFrequency frequency = (FacilioFrequency) context.get(ContextNames.FREQUENCY);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		
		
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
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.addAll(formulaFields);
		selectFields.add(resourceNameField);
		selectFields.add(rdmFieldMap.get("value"));
		selectFields.add(rdmFieldMap.get("ttime"));
		selectFields.add(rdmFieldMap.get("resourceId"));
		
		String orderBy = groupBy.equals(ContextNames.KPI) ? fieldMap.get("name").getCompleteColumnName() : resourceNameField.getCompleteColumnName();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(formulaTable)
				.innerJoin(rdmModule.getTableName()).on(fieldMap.get("readingFieldId").getCompleteColumnName()+"="+rdmFieldMap.get("fieldId").getCompleteColumnName())
				.innerJoin(resourceTable).on(rdmFieldMap.get("resourceId").getCompleteColumnName()+"="+resourceTable+".ID")
				.select(selectFields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formulaFieldType"), String.valueOf(FormulaFieldType.ENPI.getValue()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("frequency"), String.valueOf(frequency.getValue()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("siteId"), String.valueOf(siteId), NumberOperators.EQUALS))
				.orderBy(orderBy)
				;
		
		/*SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
									.module(resourceModule)
									.innerJoin(rdmModule.getTableName()).on(resourceTable+".ID="+rdmFieldMap.get("resourceId").getCompleteColumnName())
									.innerJoin(formulaTable).on(fieldMap.get("readingFieldId").getCompleteColumnName()+"="+rdmFieldMap.get("fieldId").getCompleteColumnName())
									.select(selectFields)
									.andCondition(CriteriaAPI.getCondition(fieldMap.get("formulaFieldType"), String.valueOf(FormulaFieldType.ENPI.getValue()), NumberOperators.EQUALS))
									.andCondition(CriteriaAPI.getCondition(fieldMap.get("frequency"), String.valueOf(frequency.getValue()), NumberOperators.EQUALS))
									.andCondition(CriteriaAPI.getCondition(fieldMap.get("siteId"), String.valueOf(siteId), NumberOperators.EQUALS))
									.orderBy(orderBy)
									;*/
		
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		
		if (buildingId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(resourceFieldMap.get("space"), String.valueOf(buildingId), BuildingOperator.BUILDING_IS));
		}
		if (assetCategoryId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("assetCategoryId"), String.valueOf(assetCategoryId), NumberOperators.EQUALS));
		}
		
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
		
		List<Map<String, Object>> kpis = builder.get();
		if (CollectionUtils.isNotEmpty(kpis)) {
			
		}
		
		context.put(ContextNames.KPI_LIST, kpis);
		
		return false;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private long buildingId = -1;
	public long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(long buildingId) {
		this.buildingId = buildingId;
	}
	
	private long categoryId = -1;
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

}
