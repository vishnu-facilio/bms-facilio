package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.constants.FacilioConstants.Filters;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class FetchPlaceholderFieldsCommand extends FacilioCommand{
	
	private Map<String, Object> placeHolders = new HashMap<>();
	private Map<String, Object> moduleMap = new HashMap<>();
	ModuleBean modBean;

	@Override
	public boolean executeCommand(Context context) throws Exception {

		String moduleName = (String) context.get(ContextNames.MODULE_NAME);
		List<FilterFieldContext> filterFields =(List<FilterFieldContext>) context.get(Filters.FILTER_FIELDS);
		
		modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Map<String, Object>> mainFields = new ArrayList<>();
		placeHolders.put("fields", mainFields);
		placeHolders.put("moduleFields", moduleMap);
		handleFields(moduleName, filterFields, mainFields, true);

		context.put(Filters.PLACEHOLDER_FIELDS, placeHolders);
		return false;
	}
	
	private void handleFields(String moduleName, List<FilterFieldContext> filterFields, List<Map<String, Object>> placeHolderFields, boolean isMainModule) throws Exception {
		
		for(FilterFieldContext filterField: filterFields) {
			FacilioField field = filterField.getField();
			if (field instanceof LookupField) {
				if (isMainModule) {
					FacilioModule lookupModule = ((LookupField)field).getLookupModule();
					String lookupModuleName = lookupModule.getName();
					
					if (!AccountUtil.isModuleLicenseEnabled(lookupModuleName)) {
						continue;
					}
					
					Map moduleMap = (Map) placeHolders.get("moduleFields");
					if (!moduleMap.containsKey(lookupModuleName)) {
						boolean isAdded = handleSpecialModule(lookupModuleName, filterField, placeHolderFields, moduleMap);
						if (isAdded) {
							continue;
						}
						if(LookupSpecialTypeUtil.isSpecialType(lookupModuleName)) {
							String primaryFieldName = LookupSpecialTypeUtil.getPrimaryFieldName(lookupModuleName);
							createPlaceHolder(filterField, placeHolderFields, primaryFieldName);
						}
						else if (lookupModule.getTypeEnum() == ModuleType.BASE_ENTITY) {
							Map<String, Object> placeHolder = createPlaceHolder(filterField, placeHolderFields, null);
							placeHolder.put("module", lookupModuleName);
							
							List<FilterFieldContext> lookupFilterFields = getFilterFields(lookupModuleName);
							List<Map<String, Object>> lookupPlaceholderFields = new ArrayList();
							moduleMap.put(lookupModuleName, lookupPlaceholderFields);
							handleFields(lookupModuleName, lookupFilterFields, lookupPlaceholderFields, false);
						}
						else {
							FacilioField primaryField = modBean.getPrimaryField(lookupModuleName);
							createPlaceHolder(filterField, placeHolderFields,  primaryField.getName());
						}
					}
				}
			}
			else {
				createPlaceHolder(filterField, placeHolderFields, null);
			}
		}
	}
	
	
	private Map<String, Object> createPlaceHolder(FilterFieldContext filterField, List<Map<String, Object>> placeHolderFields, String primaryField) {
		Map<String, Object> placeHolder = new HashMap<>();
		placeHolder.put("name", filterField.getName());
		placeHolder.put("displayName", filterField.getDisplayName());
		if(primaryField != null) {
			placeHolder.put("primaryField", primaryField);
		}
		placeHolderFields.add(placeHolder);
		
		return placeHolder;
	}
	
    private static final List<String> USER_FIELDS_TO_INCLUDE = Arrays.asList(new String[] {"name", "email", "phone", "mobile"});
	
	private boolean handleSpecialModule(String moduleName, FilterFieldContext mainField, List<Map<String, Object>> placeHolderFields, Map moduleMap) throws Exception {
		List<FilterFieldContext> filterFields = new ArrayList<>();
		
		switch (moduleName) {
		case ContextNames.RESOURCE:
			filterFields.add(new FilterFieldContext(modBean.getPrimaryField(moduleName)));
			break;
			
		case ContextNames.USER:
			List<FacilioField> fields = LookupSpecialTypeUtil.getAllFields(ContextNames.USER);
			for (FacilioField field: fields) {
				if(USER_FIELDS_TO_INCLUDE.contains(field.getName())) {
					filterFields.add(new FilterFieldContext(field));
				}
			}

		default:
			break;
		}
		
		if (!filterFields.isEmpty()) {
			if (filterFields.size() == 1) {
				createPlaceHolder(mainField, placeHolderFields, filterFields.get(0).getName());
			}
			else {
				createPlaceHolder(mainField, placeHolderFields, null);
				
				List<Map<String, Object>> lookupPlaceholderFields = new ArrayList();
				moduleMap.put(moduleName, lookupPlaceholderFields);
				for(FilterFieldContext field: filterFields) {
					createPlaceHolder(field, lookupPlaceholderFields, null);
				}
			}
			return true;
		}
		
		return false;
	}
	
	private List<FilterFieldContext> getFilterFields(String moduleName) throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getFilterableFields();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.CRITERIA.getVal());
		chain.execute();

        return (List<FilterFieldContext>) context.get(Filters.FILTER_FIELDS);
	}

}
