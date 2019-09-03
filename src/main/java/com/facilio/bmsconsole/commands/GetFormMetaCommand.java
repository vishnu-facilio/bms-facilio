package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class GetFormMetaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String formName = (String) context.get(FacilioConstants.ContextNames.FORM_NAME);
		Long formId = (Long) context.get(FacilioConstants.ContextNames.FORM_ID);
		String formModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);	// TODO...needs to be mandatory
		FacilioModule formModule = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (formModuleName != null) {
			formModule = modBean.getModule(formModuleName);
			if (formId == -1 && formName == null) {
				formName = FormFactory.getDefaultFormName(formModuleName, FormType.WEB.getStringVal());
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
				String moduleName = form.getModule().getName();
				form.setModule(modBean.getModule(moduleName));
				
				if (form.getSections() == null) {
					List<FormSection> sections = new ArrayList<>();
					form.setSections(sections);
					FormSection section = new FormSection("Default", 1, form.getFields(), false);
					sections.add(section);
				}
				
				boolean isFirstSection = true;
				for(FormSection section: form.getSections()) {
					List<FormField> fields = section.getFields();
					if (isFirstSection) {
						setFields(form, modBean, fields, formModuleName, childModule);
						isFirstSection = false;
					}
					else {
						FormsAPI.setFieldDetails(modBean, fields, moduleName);
					}
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
		else if(formId != null) {
			form= FormsAPI.getFormFromDB(formId);
			context.put(FacilioConstants.ContextNames.FORM, form);
		}
		if (form != null) {
			for(FormField field: form.getFields()) {
				if (field.getValue() != null && field.getField() != null && field.getField().getDataTypeEnum() == FieldType.LOOKUP && ((LookupField)field.getField()).getLookupModule().getName().equals(ContextNames.RESOURCE)) {
					ResourceContext resource = ResourceAPI.getResource(Long.parseLong(field.getValue().toString()));
					field.setValue(resource);
				}
			}
			if (AccountUtil.getCurrentUser() == null && AccountUtil.getCurrentOrg().getOrgId() != 104) {
				form.getFields().addAll(0, FormFactory.getRequesterFormFields(false));
				if (CollectionUtils.isNotEmpty(form.getSections())) {
					form.getSections().get(0).getFields().addAll(0, FormFactory.getRequesterFormFields(true));
				}
			}
		}
		
		return false;
	}
	
	private FacilioForm getChildForm(FacilioModule childModule) {
		FacilioForm form = null;
		if (childModule != null && childModule.getExtendModule() != null) {
			String extendedModName = childModule.getExtendModule().getName();
			// TODO get with type
			form = new FacilioForm(FormFactory.getForm("default_" +extendedModName));
			form.setDisplayName(childModule.getDisplayName());
		}
		return form;
	}
	
	private void setFields(FacilioForm form, ModuleBean modBean, List<FormField> fields, String moduleName, FacilioModule childModule) throws Exception {
		FormsAPI.setFieldDetails(modBean, fields, moduleName);
		if (form.getFormTypeEnum()  == FormType.PORTAL) {
			return;
		}

		int count = fields.size();
		//commenting out as we force update tenant for wo and pm
//		if (AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_TENANTS) && (formName.equalsIgnoreCase("workOrderForm") || formName.equalsIgnoreCase("web_pm"))) {
//			  fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL, "tenant", ++count, 1));
//		}
		
		if (childModule != null) {
			List<FacilioField> facilioFields = modBean.getAllFields(childModule.getName());
			for (FacilioField f: facilioFields) {
				if ((f.getModule().equals(childModule)) || !f.isDefault()) {
					count = count + 1;
					fields.add(FormsAPI.getFormFieldFromFacilioField(f, count));									
				}
			}
		}
		else {
			List<FacilioField> customFields = modBean.getAllCustomFields(moduleName);
			if (customFields != null && !customFields.isEmpty()) {
				for (FacilioField f: customFields) {
					count = count + 1;
					fields.add(FormsAPI.getFormFieldFromFacilioField(f, count));
				}
			}
		}
	}

}
