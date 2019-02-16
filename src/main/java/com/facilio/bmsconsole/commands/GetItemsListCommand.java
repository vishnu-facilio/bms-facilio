package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemsContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.unitconversion.Unit;

public class GetItemsListCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		Boolean getCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		List<FacilioField> fields;
		if (getCount != null && getCount) {
			fields = FieldFactory.getCountField(module);
		}
		else {
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		}
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		SelectRecordsBuilder<ItemsContext> builder = new SelectRecordsBuilder<ItemsContext>()
															.module(module)
															.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName))
															.select(fields)
															;
		
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		
		Integer maxLevel = (Integer) context.get(FacilioConstants.ContextNames.MAX_LEVEL);
		if(maxLevel != null && maxLevel != -1) {
			builder.maxLevel(maxLevel);
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
		if (( filters == null || includeParentCriteria) && view != null) {
		Criteria criteria = view.getCriteria();
		builder.andCriteria(criteria);
		}
		
		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}
		
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
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
		
		
		List<ItemsContext> records = builder.get();
		
		if (records != null && !records.isEmpty()) {
			if (getCount != null && getCount) {
				context.put(FacilioConstants.ContextNames.RECORD_COUNT, records.get(0).getData().get("count"));
			}
			else {
				for(ItemsContext storeroom: records) {
					if (storeroom.getIssuingUnit() > 0) {
						storeroom.setIssuingUnit(Unit.valueOf(storeroom.getIssuingUnit()));
					}
					if (storeroom.getOrderingUnit() > 0) {
						storeroom.setOrderingUnit(Unit.valueOf(storeroom.getIssuingUnit()));
					}
				}				
				context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
			}
		}
		
		long timeTaken = System.currentTimeMillis() - startTime;
		return false;
	}
}
