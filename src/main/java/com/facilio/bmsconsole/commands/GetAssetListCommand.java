package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetAssetListCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(GetAssetListCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
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
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		} else if (filters == null && view != null) {
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
		
		Boolean withReadings = (Boolean) context.get(FacilioConstants.ContextNames.WITH_READINGS);
		if (withReadings != null && withReadings) {
			builder.andCustomWhere("exists(select 1 from Reading_Data_Meta r where r.ORGID=? and r.RESOURCE_ID=Assets.ID and r.VALUE <> -1 and r.VALUE IS NOT NULL)", AccountUtil.getCurrentOrg().getId());
		}
		
		long getStartTime = System.currentTimeMillis();
		List<? extends ModuleBaseWithCustomFields> records = builder.get();
		long getTimeTaken = System.currentTimeMillis() - getStartTime;
		if (AccountUtil.getCurrentOrg().getId() == 114) {
			LOGGER.info("Time taken to execute Fetch assets in GetAssetListCommand : "+getTimeTaken);
		}
		
		context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
		
		long timeTaken = System.currentTimeMillis() - startTime;
		if (AccountUtil.getCurrentOrg().getId() == 114) {
			LOGGER.info("Time taken to execute GetAssetListCommand : "+timeTaken);
		}
		return false;
	}
}
