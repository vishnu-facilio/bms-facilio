package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;



public class GetTenantListCommand extends FacilioCommand{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		Boolean getCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		List<FacilioField> fields = null;
		
		if (getCount == null || !getCount) {
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (CollectionUtils.isEmpty(fields)) {
				fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
				context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fields);
			}
		}
		
		SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
														.module(module)
														.beanClass(TenantContext.class)
														.select(fields)
														;
		Long spaceId = (Long) context.get(FacilioConstants.ContextNames.SPACE_ID);
		if (spaceId != null && spaceId != -1) {
			String spaceTable = modBean.getModule(ContextNames.TENANT_SPACES).getTableName();
			List<FacilioField> spaceFields = modBean.getAllFields(ContextNames.TENANT_SPACES);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(spaceFields);
			
			builder.innerJoin(spaceTable).on(module.getTableName()+".ID="+fieldMap.get("tenant").getCompleteColumnName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("space"), String.valueOf(spaceId), BuildingOperator.BUILDING_IS));
		}
		
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		Criteria clientFilterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA);
		if(clientFilterCriteria != null) {
			builder.andCriteria(clientFilterCriteria);
		}
		
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		} 
		if (( filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			builder.andCriteria(view.getCriteria());
		}
		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		if (pagination != null && (getCount == null || !getCount)) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			builder.offset(offset);
			builder.limit(perPage);
		}
		else if (getCount != null && getCount) {
			builder.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));
		}
		
		
		if(getCount == null || !getCount) {
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			List<LookupField> additionaLookups = new ArrayList<LookupField>();
			LookupField contactField = (LookupField) fieldsAsMap.get("contact");
			additionaLookups.add(contactField);
			context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,additionaLookups);
		}
		
		if(getCount == null || !getCount) {
			
			List<LookupField> lookupFields = new ArrayList<>();
			for (FacilioField f : fields) {
				if (f instanceof LookupField) {
					lookupFields.add((LookupField) f);
				}
			}
			
			if (CollectionUtils.isNotEmpty(lookupFields)) {
		    	builder.fetchSupplements(lookupFields);
		    }
			
		}
	
		List<TenantContext> records = builder.get();
		if (getCount != null && getCount) {
			context.put(FacilioConstants.ContextNames.RECORD_COUNT, records.get(0).getId());
		}
		else {
			context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
		    TenantsAPI.loadTenantLookups(records);
		}
		
 	return false;
		
  }
	
}
