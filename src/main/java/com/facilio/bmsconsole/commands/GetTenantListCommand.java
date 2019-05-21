package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;



public class GetTenantListCommand implements Command{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		Boolean getCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
		if (getCount != null && getCount) {
			fields = FieldFactory.getCountField(module);
		}
		SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
														.module(module)
														.beanClass(TenantContext.class)
														.select(fields)
														;
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
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
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			builder.offset(offset);
			builder.limit(perPage);
		}
		List<TenantContext> records = builder.get();
		if (getCount != null && getCount) {
			context.put(FacilioConstants.ContextNames.RECORD_COUNT, records.get(0).getData().get("count"));
		}
		else {
			context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
		    TenantsAPI.loadTenantLookups(records);
		}
		
 	return false;
		
  }
	
}
