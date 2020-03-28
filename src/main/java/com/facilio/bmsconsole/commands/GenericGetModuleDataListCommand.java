package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GenericGetModuleDataListCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(GenericGetModuleDataListCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		FacilioModule module = modBean.getModule(moduleName);

		boolean fetchCount = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);

		List<FacilioField> fields = null;
		if (!fetchCount) {
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (CollectionUtils.isEmpty(fields)) {
				fields = modBean.getAllFields(moduleName);
				context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fields);
			}
		}
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);


		Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
		if (beanClassName == null) {
			if (module.isCustom()) {
				beanClassName = CustomModuleData.class;
			}
			else {
				beanClassName = ModuleBaseWithCustomFields.class;
			}
		}
		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
															.module(module)
															.beanClass(beanClassName)
															.select(fields)
															;
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		else if (moduleName.equals(ContextNames.TICKET_PRIORITY)) {
			builder.orderBy("SEQUENCE_NUMBER");
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
		// TODO change this as excludeparentcriteria and include parent criteria by default
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (includeParentCriteria == null) {
			includeParentCriteria = false;
		}
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		} 
		if (( filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			builder.andCriteria(view.getCriteria());
		}
		
		List<LookupField>fetchLookup = (List<LookupField>) context.get(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST);
		if (CollectionUtils.isNotEmpty(fetchLookup) && !fetchCount) {
			builder.fetchSupplements(fetchLookup);
		}
		
		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
		}
		
		Criteria clientFilterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA);
		
		if(clientFilterCriteria != null) {
			builder.andCriteria(clientFilterCriteria);
		}

		if (!fetchCount && module.isCustom()) {
			List<LookupField> lookupFields = new ArrayList<>();
			for (FacilioField f : fields) {
				if (f instanceof LookupField) {
					lookupFields.add((LookupField) f);
				}
			}
			builder.fetchSupplements(lookupFields);
		}
		
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		if (pagination != null && !fetchCount) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			
			builder.offset(offset);
			builder.limit(perPage);
		}
		else if (fetchCount) {
			builder.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));
		}
		
		List<? extends ModuleBaseWithCustomFields> records = builder.get();
		context.put("query",builder.toString());
		/*LOGGER.info("- - - - select controllers - - - - "+builder.toString());*/
		if (fetchCount) {
			if (CollectionUtils.isNotEmpty(records)) {
				context.put(FacilioConstants.ContextNames.RECORD_COUNT, records.get(0).getId());
			}
		}
		else {
			ResourceAPI.loadModuleResources(records, fields);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
			if (records != null) {
				LOGGER.debug("No of records fetched for module : " + moduleName + " is " + records.size());
			}
		}

		return false;
	}
	
	

}
