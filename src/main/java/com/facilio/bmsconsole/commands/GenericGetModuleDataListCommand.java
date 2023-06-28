package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.InventoryApi;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.util.CurrencyUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericGetModuleDataListCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(GenericGetModuleDataListCommand.class.getName());

	private boolean isPicklist = false;

	public GenericGetModuleDataListCommand() {

	}

	public GenericGetModuleDataListCommand(boolean isPicklist) {
		this.isPicklist = isPicklist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		FacilioModule module = modBean.getModule(moduleName);
		FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module name for data list");

		boolean fetchCount = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);

		List<FacilioField> fields = null;
		if (!fetchCount) {
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (CollectionUtils.isEmpty(fields)) {
				fields = modBean.getAllFields(moduleName);
				context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fields);
			}
			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CurrencyUtil.isMultiCurrencyEnabledModule(module)) {
				fields.addAll(FieldFactory.getCurrencyPropsFields(module));
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
		if (StringUtils.isEmpty(orderBy)) {
			orderBy = RecordAPI.getDefaultOrderByForModuleIfAny(moduleName);
		}
		if (StringUtils.isNotEmpty(orderBy)) {
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
		Criteria customFilterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.CUSTOM_FILTER_CRITERIA);
		// TODO change this as excludeparentcriteria and include parent criteria by default
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (includeParentCriteria == null) {
			includeParentCriteria = false;
		}
		if (filterCriteria != null && !filterCriteria.isEmpty()) {
			builder.andCriteria(filterCriteria);
		} 
		
		if (customFilterCriteria != null && !customFilterCriteria.isEmpty()) {
			builder.andCriteria(customFilterCriteria);
		}
		
		if (( filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			builder.andCriteria(view.getCriteria());
		}
		
		// TODO remove this and use FETCH_SUPPLEMENTS
		List<LookupField>fetchLookup = (List<LookupField>) context.get(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST);
		if (CollectionUtils.isNotEmpty(fetchLookup) && !fetchCount) {
			builder.fetchSupplements(fetchLookup);
		}
		
		List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (CollectionUtils.isNotEmpty(supplementFields) && !fetchCount) {
        		builder.fetchSupplements(supplementFields);
        }
		
		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null && !searchCriteria.isEmpty()) {
			builder.andCriteria(searchCriteria);
		}

		Criteria genericSearchCriteria = (Criteria) context.get(ContextNames.GENERIC_SEARCH_CRITERIA);
		if (genericSearchCriteria != null && !genericSearchCriteria.isEmpty()) {
			builder.andCriteria(genericSearchCriteria);
		}

		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
		if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCOPING)) {
			if (scopeCriteria != null && !scopeCriteria.isEmpty()) {
				builder.andCriteria(scopeCriteria);
			}
		}
		
		//TODO remove here and handle in select builder always
		boolean checkPermission = (boolean) context.getOrDefault("checkPermission", false);
		if (checkPermission) {
			setPermissionCriteria(moduleName, fields, builder);
		}
		
		Criteria clientFilterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA);
		
		if(clientFilterCriteria != null && !clientFilterCriteria.isEmpty()) {
			builder.andCriteria(clientFilterCriteria);
		}

		Object serverCriteria = context.get(ContextNames.FILTER_SERVER_CRITERIA);
		if (serverCriteria != null) {
			if (serverCriteria instanceof Criteria) {
				if (!((Criteria) serverCriteria).isEmpty()) {
					builder.andCriteria((Criteria) serverCriteria);
				}
			}
			else {
				builder.andCondition((Condition) serverCriteria);
			}
		}
		
		boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
		if (skipModuleCriteria) {
			builder.skipModuleCriteria();
		}
		

		if (!fetchCount && module.isCustom() && CollectionUtils.isEmpty(fetchLookup)) {
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
		
		List records = isPicklist ? builder.getAsProps() : builder.get();
		context.put("query",builder.toString());
		/*LOGGER.info("- - - - select controllers - - - - "+builder.toString());*/
		if (fetchCount) {
			if (CollectionUtils.isNotEmpty(records)) {
				context.put(FacilioConstants.ContextNames.RECORD_COUNT, ((ModuleBaseWithCustomFields) records.get(0)).getId());
			}
		}
		else {
			if (!isPicklist) {
				ResourceAPI.loadModuleResources(records, fields);
			}
			context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
			if (records != null) {
				LOGGER.debug("No of records fetched for module : " + moduleName + " is " + records.size());
			}
		}

		return false;
	}
	
	private void setPermissionCriteria(String moduleName, List<FacilioField> fields, SelectRecordsBuilder builder) throws Exception {
		boolean isInventory = false;
		if (InventoryApi.checkIfInventoryModule(moduleName)) {
			moduleName =  ContextNames.INVENTORY;
			isInventory = true;
		}
		
		if (AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO && AccountUtil.getCurrentUser().getRole() != null) {
			Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria(moduleName, "read");
			if(permissionCriteria != null) {
				// TODO remove special handling
				if (isInventory && !moduleName.equals(ContextNames.STORE_ROOM)) {
					Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
					FacilioField storeRoomField = fieldMap.get("storeRoom");
					if (storeRoomField != null) {
						builder.innerJoin("Store_room").on(storeRoomField.getCompleteColumnName()+"= Store_room.ID");
						Set<Long> storeIds = StoreroomApi.getStoreRoomList(null, false);
						if(CollectionUtils.isNotEmpty(storeIds)) {
							builder.andCondition(CriteriaAPI.getCondition(storeRoomField, storeIds, NumberOperators.EQUALS));
						}
					}
				}
				builder.andCriteria(permissionCriteria);
			}
		}
	}

}
