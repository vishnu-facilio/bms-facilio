package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class GetReceivablesListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (StringUtils.isNotEmpty(moduleName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
			
			Boolean getCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
			List<FacilioField> fields;
			if (getCount == null) {
				getCount = false;
			}
			if (getCount) {
				fields = FieldFactory.getCountField(module);
			} else {
				fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			}
			
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<ReceivableContext> builder = new SelectRecordsBuilder<ReceivableContext>()
					.module(module)
					.select(fields)
					.beanClass(ReceivableContext.class)
					;
			

			if (getCount) {
				builder.setAggregation();
			}
			else {
				builder.fetchLookup((LookupField)fieldsAsMap.get("poId"));
				
			}
			String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
			if (getCount!=null && !getCount && orderBy != null && !orderBy.isEmpty()) {
				builder.orderBy(orderBy);
			}

			List<Long> idsToSelect = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			if (idsToSelect != null && !idsToSelect.isEmpty()) {
				builder.andCondition(CriteriaAPI.getIdCondition(idsToSelect, module));
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

			Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
			if (scopeCriteria != null) {
				builder.andCriteria(scopeCriteria);
			}

			JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
			if (pagination != null) {
				int page = (int) pagination.get("page");
				int perPage = (int) pagination.get("perPage");

				int offset = ((page - 1) * perPage);
				if (offset < 0) {
					offset = 0;
				}

				builder.offset(offset);
				builder.limit(perPage);
			}

		
			Long poId = (Long)context.get(FacilioConstants.ContextNames.PO_ID);
			Long receivableId = (Long)context.get(FacilioConstants.ContextNames.ID);
			
			if(poId != null) {
				builder.andCondition(CriteriaAPI.getCondition("PO_ID", "poId", String.valueOf(poId),NumberOperators.EQUALS ));
			}
			if(receivableId != null) {
				builder.andCondition(CriteriaAPI.getIdCondition(receivableId, module));
			}
			
			List<ReceivableContext> records = builder.get();
			
			if (records != null && !records.isEmpty()) {
				if (getCount != null && getCount) {
					context.put(FacilioConstants.ContextNames.RECORD_COUNT, records.get(0).getData().get("count"));
				} else {
					context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
				}
			}
		}
		return false;
	}

}
