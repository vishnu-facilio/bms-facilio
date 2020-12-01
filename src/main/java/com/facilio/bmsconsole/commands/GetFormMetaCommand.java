package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.modules.FieldType;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateTimeUtil;

public class GetFormMetaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String formName = (String) context.get(FacilioConstants.ContextNames.FORM_NAME);
		Long formId = (Long) context.getOrDefault(FacilioConstants.ContextNames.FORM_ID, -1l);
		String formModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);	// TODO...needs to be mandatory
		FacilioModule formModule = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (formModuleName != null) {
			formModule = modBean.getModule(formModuleName);
			if (formId == -1 && formName == null) {
				FormType formType = (FormType) context.get(FacilioConstants.ContextNames.FORM_TYPE);
				formName = FormFactory.getDefaultFormName(formModuleName,formType != null ? formType : FormType.WEB);
			}
		}
		
		FacilioForm form = null;
		if (formName != null) {

			form = FormsAPI.getFormFromDB(formName, formModule);
			if (form == null) {
				FacilioModule childModule = null;
				if (formModuleName != null) {
					form = FormFactory.getForm(formModuleName, formName);
					if (form == null) {
						childModule = formModule;
						form = getChildForm(childModule);
					}
				}
				else {
					form = FormFactory.getForm(formName);
					if (form == null) {
						if (formName.startsWith("default_")) {
							String modname = formName.replaceAll("default_", "");
							childModule = modBean.getModule(modname);
							form = getChildForm(childModule);
						}
					}
					else {
						form = new FacilioForm(FormFactory.getForm(formName));
					}
				}
				if (form == null) {
					throw new IllegalArgumentException("Invalid Form " + formName);
				}
				form.setModule(modBean.getModule(form.getModule().getName()));
				
				if (formModuleName == null) {
					List<FormField> fields = new ArrayList<>(form.getFields());
					form.setFields(fields);
					setFields(form, modBean, fields, form.getModule().getName(), childModule, -1);
				}
				else if (form.getSections() != null) {
					boolean isFirstSection = true;
					int sequenceNumber = Collections.max(FormsAPI.getFormFieldsFromSections(form.getSections()), Comparator.comparing(s -> s.getSequenceNumber())).getSequenceNumber();
					for(FormSection section: form.getSections()) {
						List<FormField> fields = section.getFields();
						if (isFirstSection) {
							setFields(form, modBean, fields, formModuleName, childModule, sequenceNumber);
							isFirstSection = false;
						}
						else {
							FormsAPI.setFieldDetails(modBean, fields, formModuleName);
						}
					}
					form.setFields(FormsAPI.getFormFieldsFromSections(form.getSections()));
				}
			}
			else {
				if (formName.equalsIgnoreCase("web_pm")) {
					for (FormField f : form.getFields()) {
						if (StringUtils.isNotEmpty(f.getName()) && f.getName().equalsIgnoreCase("siteid")) {
							f.setName("site");
							break;
						}
					}
				}
			}
			context.put(FacilioConstants.ContextNames.FORM, form);
		}
		else if(formId != null && formId > 0) {
			form= FormsAPI.getFormFromDB(formId);
			context.put(FacilioConstants.ContextNames.FORM, form);
		}
		if (form != null) {
			for(FormField field: form.getFields()) {
				handleDefaultValue(field);
			}
			if (AccountUtil.getCurrentUser() == null) {
				form.getFields().addAll(0, FormFactory.getRequesterFormFields(false, true));
				if (CollectionUtils.isNotEmpty(form.getSections())) {
					form.getSections().get(0).getFields().addAll(0, FormFactory.getRequesterFormFields(true, true));
				}
			}
			if (formName != null && formName.equalsIgnoreCase("multi_web_pm")) {
				List<FormField> formFields = new ArrayList<>();
				for (FormField f : form.getFields()) {
					FacilioField field = f.getField();
					if (field != null && !field.isDefault() && field.getDataTypeEnum() == FieldType.LOOKUP) {
						continue;
					}
					formFields.add(f);
				}
				form.setFields(formFields);
			}
		}
		
		return false;
	}
	
	private FacilioForm getChildForm(FacilioModule childModule) throws Exception {
		FacilioForm form = null;
		if (childModule != null && childModule.getExtendModule() != null) {
			form = FormsAPI.getDefaultFormFromDBOrFactory(childModule.getExtendModule(), FormType.WEB);
			form.setDisplayName(childModule.getDisplayName());
		}
		return form;
	}
	
	private void setFields(FacilioForm form, ModuleBean modBean, List<FormField> fields, String moduleName, FacilioModule childModule, int count) throws Exception {
		FormsAPI.setFieldDetails(modBean, fields, moduleName);
		if (form.getFormTypeEnum()  == FormType.PORTAL) {
			return;
		}

		if (count == -1) {
			count = Collections.max(fields, Comparator.comparing(s -> s.getSequenceNumber())).getSequenceNumber();
		}
		//commenting out as we force update tenant for wo and pm
//		if (AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_TENANTS) && (formName.equalsIgnoreCase("workOrderForm") || formName.equalsIgnoreCase("web_pm"))) {
//			  fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL, "tenant", ++count, 1));
//		}

		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN) && moduleName.equals("workorder") &&  (form.getName().equals("web_pm") || form.getName().equals("mobile_default") || form.getName().equals("default_workorder_web"))) {
			fields.add(new FormField("safetyPlan", FieldDisplayType.LOOKUP_SIMPLE, "Safety Plan", Required.OPTIONAL,"safetyPlan", 12, 1));
		}

		
		if (childModule != null) {
			if (form.getId() == -1) {
				List<FacilioField> facilioFields = modBean.getAllFields(childModule.getName());
				for (FacilioField f: facilioFields) {
					if ((f.getModule().equals(childModule)) || !f.isDefault()) {
						fields.add(FormsAPI.getFormFieldFromFacilioField(f, ++count));									
					}
				}
			}
		}
		else {
			List<FacilioField> customFields = modBean.getAllCustomFields(moduleName);
			if (form.getName().equalsIgnoreCase("web_pm") || form.getName().equalsIgnoreCase("multi_web_pm")) { // Temp...showing custom fields in standard form...will be removed once action in pm
				FacilioForm defaultWoForm = form = FormsAPI.getDefaultFormFromDBOrFactory(modBean.getModule(moduleName), FormType.WEB, true);
				customFields = defaultWoForm.getFields().stream().filter(field -> field.getField() != null && !field.getField().isDefault())
								.map(field -> field.getField()).collect(Collectors.toList());
			}
			if (customFields != null && !customFields.isEmpty()) {
				for (FacilioField f: customFields) {
					fields.add(FormsAPI.getFormFieldFromFacilioField(f, ++count));
				}
			}
		}
	}
	
	private void handleDefaultValue(FormField formField) throws Exception {
		if (formField.getField() != null) {
			Object value = null;
			switch(formField.getField().getDataTypeEnum()) {
				case DATE:
				case DATE_TIME:
					if (formField.getConfig() != null) {
						Boolean setToday = (Boolean) formField.getConfig().get("setToday");
						if (setToday != null && setToday) {
							value = DateTimeUtil.getDayStartTime();
						}
						else if (formField.getConfig().containsKey("dayCount")) {
							Integer dayCount = Integer.parseInt(formField.getConfig().get("dayCount").toString());
							value = DateTimeUtil.addDays(DateTimeUtil.getDayStartTime(), dayCount);
						}
					}
					break;
					
				case LOOKUP:
					if (formField.getValue() != null && ((LookupField)formField.getField()).getLookupModule().getName().equals(ContextNames.RESOURCE)) {
						String val = formField.getValue().toString();
						if (StringUtils.isNumeric(val)) {
							value = ResourceAPI.getResource(Long.parseLong(val.toString()));
						}
					}
					break;
			}
			
			if (value != null) {
				formField.setValueObject(value);
			}
		}
		
	}

}
