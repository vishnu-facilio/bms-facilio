package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.UtilityAsset;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;



public class GetTenantListCommand implements Command{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> fields = null;
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
														.module(module)
														.beanClass(TenantContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.TENANT))
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
		TenantsAPI.loadTenantLookups(records);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
 	return false;
		
  }
	
}
