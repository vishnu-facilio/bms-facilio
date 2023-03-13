package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.placeholder.enums.PlaceholderSourceType;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.constants.FacilioConstants.Filters;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;

@SuppressWarnings("unchecked")
public class FetchPlaceholderFieldsCommand extends FacilioCommand {
	
	private Map<String, Object> placeHolders = new HashMap<>();
	private Map<String, Object> moduleMap = new HashMap<>();
	ModuleBean modBean;
    private PlaceholderSourceType placeholderSourceType;
    boolean isIdPrimaryField = false;

	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		String moduleName = (String) context.get(ContextNames.MODULE_NAME);
		List<FilterFieldContext> filterFields =(List<FilterFieldContext>) context.get(Filters.FILTER_FIELDS);
		placeholderSourceType = (PlaceholderSourceType) context.get(ContextNames.SOURCE_TYPE);
		if (placeholderSourceType == null) {
			placeholderSourceType = PlaceholderSourceType.SCRIPT;
		}
		isIdPrimaryField = placeholderSourceType == PlaceholderSourceType.FORM_RULE;
		
		FacilioForm form = (FacilioForm) context.get(ContextNames.FORM);
		List<FacilioField> fields = form != null ? getFormFields(form) : getFieldsFromFilterFields(filterFields);
		
		modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Map<String, Object>> mainFields = new ArrayList<>();
		placeHolders.put("fields", mainFields);
		placeHolders.put("moduleFields", moduleMap);
		handleFields(moduleName, fields, mainFields, true);

		context.put(Filters.PLACEHOLDER_FIELDS, placeHolders);
		return false;
	}
	
	private List<FacilioField> getFormFields(FacilioForm form) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		for (FormField formField: form.getFields()) {
			if (formField.getField() != null) {
				fields.add(formField.getField());
			}
			else if (formField.getName().equals("siteId")) {
				FacilioField siteField = FieldFactory.getSiteIdField();
				fields.add(siteField);
			}
		}
		return fields;
	}
	
	private List<FacilioField> getFieldsFromFilterFields(List<FilterFieldContext> filterFields) {
		return filterFields.stream().map(FilterFieldContext::getField).collect(Collectors.toList());
	}
	
	private void handleFields(String moduleName, List<FacilioField> fields, List<Map<String, Object>> placeHolderFields, boolean isMainModule) throws Exception {
		
		for(FacilioField field : fields) {
			if (excludeField(field)) {
				continue;
			}
			if (field instanceof LookupField) {
				FacilioModule lookupModule = ((LookupField)field).getLookupModule();
				String lookupModuleName = lookupModule.getName();
				
				if (!AccountUtil.isModuleLicenseEnabled(lookupModuleName)) {
					continue;
				}
				if (isMainModule) {
					
					boolean isAdded = handleSpecialModule(lookupModuleName, field, placeHolderFields, moduleMap);
					if (isAdded) {
						continue;
					}
					
					// For 1st level lookup
					if (lookupModule.getTypeEnum() == ModuleType.BASE_ENTITY) {
						Map<String, Object> placeHolder = createPlaceHolder(field, placeHolderFields, null);
						placeHolder.put("module", lookupModuleName);
						Map moduleMap = (Map) placeHolders.get("moduleFields");

						if (!moduleMap.containsKey(lookupModuleName)) {
							List<Map<String, Object>> lookupPlaceholderFields = new ArrayList<>();
							moduleMap.put(lookupModuleName, lookupPlaceholderFields);
							List<FacilioField> lookupFilterFields = getFilterFields(lookupModuleName);
							// Fetching fields for lookup module
							handleFields(lookupModuleName, lookupFilterFields, lookupPlaceholderFields, false);

						}
						continue;
					}
				}
					createLookupPlaceholder(lookupModuleName, field, placeHolderFields);
			}
			else {
				createPlaceHolder(field, placeHolderFields, null);
			}
		}
	}
	
	private void createLookupPlaceholder(String lookupModuleName, FacilioField field, List<Map<String, Object>> placeHolderFields) throws Exception {
		if(LookupSpecialTypeUtil.isSpecialType(lookupModuleName)) {
			String primaryFieldName = LookupSpecialTypeUtil.getPrimaryFieldName(lookupModuleName);
			createPlaceHolder(field, placeHolderFields, primaryFieldName);
		}
		else {
			FacilioField primaryField = modBean.getPrimaryField(lookupModuleName);
			createPlaceHolder(field, placeHolderFields,  primaryField.getName());
		}
	}
	
	private Map<String, Object> createPlaceHolder(FacilioField field, List<Map<String, Object>> placeHolderFields, String primaryField) {
		Map<String, Object> placeHolder = new HashMap<>();
		String name = field.getName();
		placeHolder.put("name", name);
		placeHolder.put("displayName", field.getDisplayName());
		placeHolder.put("dataType", field.getDataTypeEnum().name());
		placeHolder.put("fieldId", field.getFieldId());
		FieldDisplayType displayType = field.getDisplayType();
		if (displayType == null) {
			FacilioField.FieldDisplayType type = FieldFactory.getDefaultDisplayTypeFromDataType(field.getDataTypeEnum());
            if (type != null) {
            	displayType = type;
            }
		}
		placeHolder.put("displayType", displayType != null ? displayType.name() : null);
		if(primaryField != null) {
			if (isIdPrimaryField) {
				primaryField =  "id";
			}
			placeHolder.put("primaryField", primaryField);
		}
		placeHolderFields.add(placeHolder);
		
		return placeHolder;
	}
	
    private static final List<String> USER_FIELDS_TO_INCLUDE = Arrays.asList(new String[] {"name", "email", "phone", "mobile"});
	
	private boolean handleSpecialModule(String moduleName, FacilioField mainField, List<Map<String, Object>> placeHolderFields, Map moduleMap) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		
		switch (moduleName) {
		case ContextNames.RESOURCE:
			if (isIdPrimaryField) {
				fields.add(FieldFactory.getIdField());
			}
			else {
				fields.add(modBean.getPrimaryField(moduleName));
			}
			break;
			
		case ContextNames.USERS:
			List<FacilioField> userFields = IAMAccountConstants.getAccountsUserFields();
			userFields.addAll(AccountConstants.getAppOrgUserFields());
			for (FacilioField field: userFields) {
				if(USER_FIELDS_TO_INCLUDE.contains(field.getName())) {
					fields.add(field);
				}
				else if (field.getName().equals("ouid") && isIdPrimaryField) {
					fields.add(field);
				}
			}

		default:
			break;
		}
		
		if (!fields.isEmpty()) {
			if (fields.size() == 1) {
				createPlaceHolder(mainField, placeHolderFields, fields.get(0).getName());
			}
			else {
				Map<String, Object> placeHolder = createPlaceHolder(mainField, placeHolderFields, null);
				placeHolder.put("module", moduleName);
				if (!moduleMap.containsKey(moduleName)) {
					List<Map<String, Object>> lookupPlaceholderFields = new ArrayList<>();
					moduleMap.put(moduleName, lookupPlaceholderFields);
					for(FacilioField field: fields) {
						createPlaceHolder(field, lookupPlaceholderFields, null);
					}
				}
			}
			return true;
		}
		
		return false;
	}
	
	private boolean excludeField(FacilioField field) {
		if (field.getDataTypeEnum().isRelRecordField()) {
			return true;
		}
		
		switch (field.getDataTypeEnum()) {
			case FILE:
			case MISC:
				return true;
			default:
				break;
		}
		return false;
	}
	
	private List<FacilioField> getFilterFields(String moduleName) throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getFilterableFields();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.CRITERIA.getVal());
		chain.execute();

		List<FilterFieldContext> filterFields = (List<FilterFieldContext>) context.get(Filters.FILTER_FIELDS);
        return getFieldsFromFilterFields(filterFields);
	}

}
