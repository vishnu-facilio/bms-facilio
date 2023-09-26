package com.facilio.bmsconsole.commands.form;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.fs.FileInfo;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.services.factory.FacilioFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormSourceType;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;

public class HandleFormFieldsCommand extends FacilioCommand {
	
	FormSourceType formSourceType;
	boolean isFromBuilder;
	Boolean forCreate;
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		forCreate = (context.get(ContextNames.FOR_CREATE) != null) ? (Boolean) context.get(ContextNames.FOR_CREATE): false;
		if (moduleName == null) {
			return false;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		if (form != null) {
			formSourceType = (FormSourceType) context.getOrDefault(ContextNames.FORM_SOURCE, FormSourceType.FROM_FORM);
			isFromBuilder = formSourceType == formSourceType.FROM_BUILDER;
			handleFields(module, form);
		}
		
		return false;
	}
	
	private void handleFields(FacilioModule module, FacilioForm form) throws Exception {
		boolean isAssetModule = AssetsAPI.isAssetsModule(module);
		for (FormSection section : form.getSections()) {
			if (section.getSubForm() != null) {
				FacilioForm subForm = section.getSubForm();
				handleFields(subForm.getModule(), subForm);
			}
			else if (section.getFields() != null) {
				List<FormField> sectionFields = section.getFields();
				handleRelationshipFields(module, sectionFields);
				for(FormField field: sectionFields) {
					if (formSourceType == FormSourceType.FROM_BULK_FORM) {
						field.setValue(null);
					}
					else if (!isFromBuilder) {
						handleDefaultValue(field);
					}
					else {
						handleFloorPlanConfig(field);
					}
					setLookupName(field, module.getName(), isFromBuilder);
					addFilters(module, field, isAssetModule);
					if(forCreate&&(field.getValue()!=null)&&(field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.TEXTAREA||field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.NOTES||field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.RICH_TEXT)&&field.getValue().toString().contains("\"fileId\"")) {
						org.json.JSONObject k= new org.json.JSONObject(field.getValue().toString());
						int fileId = (int) k.get("fileId");
						InputStream defaultValue = FacilioFactory.getFileStore().readFile((long)fileId);
						String value1= IOUtils.toString(defaultValue);
						field.setValue(value1);
					}
					setValidations(field);
				}
			}
		}
	}

	private void handleRelationshipFields(FacilioModule module, List<FormField> sectionFields) throws Exception {
		List<FormField> relationshipFieldFromFields = FormsAPI.getRelationshipFieldFromFields(sectionFields);
		if (CollectionUtils.isEmpty(relationshipFieldFromFields)) {
			return;
		}
		List<Long> relationMappingIds = relationshipFieldFromFields.stream().map(FormField::getRelationMappingId).collect(Collectors.toList());
		List<RelationRequestContext> relations = RelationUtil.getAllRelations(module, relationMappingIds);
		Map<Long, RelationRequestContext> relationMappingIdVsRelationRequest = RelationUtil.getRelationMappingIdVsRelationRequest(relations);

		for (FormField sectionField : sectionFields) {
			sectionField.setRelationData(relationMappingIdVsRelationRequest.get(sectionField.getRelationMappingId()));
		}
	}

	private void handleDefaultValue(FormField formField) throws Exception {
		Object value = formField.getValue();
		if(forCreate&&(formField.getValue()!=null)&&(formField.getDisplayTypeEnum() == FacilioField.FieldDisplayType.ATTACHMENT||formField.getDisplayTypeEnum() == FacilioField.FieldDisplayType.FILE) && !(StringUtils.isEmpty(formField.getValue().toString()))){
			org.json.JSONArray defaultval= new org.json.JSONArray(formField.getValue().toString());
			JSONArray orphanedArray = new JSONArray();
			for(Object val:defaultval){
				org.json.JSONObject fileVal=new org.json.JSONObject(val.toString());
				String fileIdString = fileVal.get("fileId").toString();
				int fileId = Integer.parseInt(fileIdString);
				FileInfo fileInfo= FacilioFactory.getFileStore().getFileInfo(fileId);
				String contentType = fileInfo.getContentType();
				String fileName = fileInfo.getFileName();

				try (InputStream defaultValue = FacilioFactory.getFileStore().readFile((long) fileId)){

					byte[] bytes = IOUtils.toByteArray(defaultValue);

					long orphanFileId = FacilioFactory.getFileStore().addOrphanedFileForFormFields(fileName, bytes, contentType);

					JSONObject orphanedObject = new JSONObject();
					orphanedObject.put("fileId", orphanFileId);
					orphanedObject.put("fileName", fileName);
					orphanedArray.add(orphanedObject);
				}
			}
		formField.setValue(orphanedArray.toString());
		}
		else if (formField.getField() != null && value != null) {
			switch(formField.getField().getDataTypeEnum()) {
				case DATE:
				case DATE_TIME:
				case LOOKUP:
					String val = value.toString();
					if (val.startsWith("${")) {
						value = FormsAPI.resolveDefaultValPlaceholder(val);
					}
					break;
			}
			formField.setValueObject(value);
		}
		
	}
	
	private void addFilters(FacilioModule module, FormField formField, boolean isAssetModule) {
		if (isAssetModule) {
			switch(formField.getName()) {
				case "rotatingItem":
					setRotatingFilter("itemType", formField);
					break;
				case "rotatingTool":
					setRotatingFilter("toolType", formField);
					break;
			}
		}
	}
	
	private void setLookupName(FormField formField, String moduleName, boolean isFromBuilder) throws Exception {
		FacilioField field = formField.getField();
		boolean isLookup = formField.getDisplayTypeEnum() == FieldDisplayType.LOOKUP_SIMPLE || formField.getDisplayTypeEnum() == FieldDisplayType.MULTI_LOOKUP_SIMPLE;
		// If its a lookup field or a special lookup form field
		if (isLookup && (!isFromBuilder || field == null)) {
			if (formField.getConfig() != null) {
				JSONObject config = formField.getConfig();
				boolean filterEnabled = (boolean) config.getOrDefault("isFiltersEnabled", false);
				if (filterEnabled) {
					String lookupName = (String) config.get("lookupModuleName");
					if (lookupName == null) { // Temp..needs to remove
						int filterValue = Integer.parseInt(formField.getConfig().get("filterValue").toString());
						lookupName = getModuleName(filterValue);
					}
					formField.setLookupModuleName(lookupName);
					return;
				}
			}
		}
	}
	
	// Temp..needs to remove
	private String getModuleName(int val) {
		Map<Integer, String> names= new HashMap<>();
		names.put(1, "building");
		names.put(2, "asset");
		names.put(3, "tenantcontact");
		names.put(4, "clientcontact");
		names.put(5, "vendorcontact");
		names.put(6, "employee");
		return names.get(val);
	}
	
	private void setRotatingFilter(String type, FormField formField) {
        JSONObject operator = new JSONObject();
        operator.put("operatorId", LookupOperator.LOOKUP.getOperatorId());
        JSONArray values = new JSONArray();
        operator.put("lookupOperatorId", BooleanOperators.IS.getOperatorId());
        operator.put("field", "isRotating");
        values.add(String.valueOf(true));
        operator.put("value", values);

        formField.addToFilters(type, operator);
    }

	private void handleFloorPlanConfig(FormField formField) {
		if (formField.getField() != null && formField.getField().getDataTypeEnum() == FieldType.LOOKUP) {
			FacilioModule module = ((LookupField)formField.getField()).getLookupModule();
			if (module.getName().equals(ContextNames.SPACE) || 
					(module.getExtendModule() != null && module.getExtendModule().getName().equals(ContextNames.SPACE))) {
				formField.setShowFloorPlanConfig(true);
			}
		}
	}
	
	private void setValidations(FormField formField) {
		setMaxLength(formField);
		if(formField.getDisplayTypeEnum() == FieldDisplayType.EMAIL) {
			formField.setEmailValidation();
		}
	}
	
	private void setMaxLength(FormField formField) {
		int maxLength = -1;
		if (formField.getDisplayTypeEnum() == FieldDisplayType.TEXTBOX) {
			maxLength = 255;
		}
		else if (formField.getField() != null && formField.getField().getDataTypeEnum() == FieldType.LARGE_TEXT) {
			maxLength = 32000;
		}
		else if (formField.getDisplayTypeEnum() == FieldDisplayType.TEXTAREA) {
			maxLength = 2000;
		}
		if (maxLength > 0) {
			formField.setMaxLength(maxLength);
		}
	}
}
