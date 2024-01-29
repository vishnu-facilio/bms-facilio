package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class GetFormMetaCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(GetFormMetaCommand.class);
	@Override
	public boolean executeCommand(Context context) throws Exception {
		String formName = (String) context.get(FacilioConstants.ContextNames.FORM_NAME);
		Long formId = (Long) context.getOrDefault(FacilioConstants.ContextNames.FORM_ID, -1l);
		Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1l);
		String appLinkName = (String) context.getOrDefault(ContextNames.APP_LINKNAME, null);
		Boolean forCreate = (context.get(ContextNames.FOR_CREATE) != null) ? (Boolean) context.get(ContextNames.FOR_CREATE): false;

		String formModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);	// TODO...needs to be mandatory
		FacilioModule formModule = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


		FacilioForm form = null;
		ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
		if(StringUtils.isEmpty(appLinkName) && app != null) {
			appLinkName =app.getLinkName();
		}
		if(formId != null && formId > 0) {
			form= FormsAPI.getFormFromDB(formId);
			if (form != null) {
				List<FormSection> newFormSection = new ArrayList<>();
				List<FormField> formFields = new ArrayList<>();
				for(FormSection formSection : form.getSections()) {
					if (formSection.getSubForm() != null) {
						List<FormSection> newSubFormSection = new ArrayList<>();
						List<FormField> subFormFields = new ArrayList<>();
						for (FormSection subFormSection : formSection.getSubForm().getSections()) {
							List<FormField> subFormSectionFields = subFormSection.getFields();
							if(subFormSectionFields == null){
								continue;
							}
							Map<String, FormField> formFieldMap = new HashMap<>();
							for (FormField formField : subFormSectionFields) {
								if (!formFieldMap.containsKey(formField.getName())) {
									formFieldMap.put(formField.getName(), formField);
								} else {
									LOGGER.info("formField " + formField.getName() + " duplicate for form id : " + formSection.getSubForm().getId());
									if (formField.getFieldId() != -1) {
										formFieldMap.put(formField.getName(), formField);
									}
								}
							}
							List<FormField> newFormFields = new ArrayList<FormField>(formFieldMap.values().stream().sorted(Comparator.comparingLong(FormField::getSequenceNumber)).collect(Collectors.toList()));
							newFormFields.removeIf(formField -> forCreate && formField.getDisplayTypeEnum() == FieldDisplayType.AUTO_NUMBER_FIELD);
							subFormSection.setFields(newFormFields);
							subFormFields.addAll((newFormFields));
							newSubFormSection.add(subFormSection);
						}
						formSection.getSubForm().setFields(subFormFields);
						formSection.getSubForm().setSections(newSubFormSection);
						newFormSection.add(formSection);
					} else {
						List<FormField> formSectionFields = formSection.getFields();

						Map<String, FormField> formFieldMap = new HashMap<>();
						if (formSectionFields != null){
							for (FormField formField : formSectionFields) {
								if (!formFieldMap.containsKey(formField.getName())) {
									formFieldMap.put(formField.getName(), formField);
								} else {
									LOGGER.info("formField " + formField.getName() + " duplicate for form id : " + formId);
									if (formField.getFieldId() != -1) {
										formFieldMap.put(formField.getName(), formField);
									}
								}
							}
						}
						List<FormField> newFormFields = new ArrayList<FormField>(formFieldMap.values().stream().sorted(Comparator.comparingLong(FormField::getSequenceNumber)).collect(Collectors.toList()));
						newFormFields.removeIf(formField -> forCreate && formField.getDisplayTypeEnum() == FieldDisplayType.AUTO_NUMBER_FIELD);
						formSection.setFields(newFormFields);
						formFields.addAll((newFormFields));
						newFormSection.add(formSection);
					}
					form.setSections(newFormSection);
					form.setFields(formFields);
				}

				context.put(ContextNames.MODULE_NAME, form.getModule().getName());
			}
		}
		else {
			if (formModuleName != null) {
				formModule = modBean.getModule(formModuleName);
				if (formName == null) {
					formName = FormFactory.getDefaultFormName(formModuleName,appLinkName);
				}
			}
			else if (formName == null) {
				return false;
			}
			form = FormsAPI.getFormFromDB(formName, formModule);
			if (form == null) {
				FacilioModule childModule = null;
				if (formModuleName != null) {
					form = FormFactory.getForm(formModuleName, formName);
					if (form == null) {
						childModule = formModule;
						form = getChildForm(childModule, appLinkName);
					}else{
						LOGGER.info("formFactoryTracking formName : "+ formName +"for module : "+ formModuleName + "get from form builder");
					}
				}
				else {
					form = FormFactory.getForm(formName);
					if (form == null) {
						if (formName.startsWith("default_")) {
							String modname = formName.replaceAll("default_", "");
							childModule = modBean.getModule(modname);
							form = getChildForm(childModule, appLinkName);
						}
					}
					else {
						form = new FacilioForm(FormFactory.getForm(formName));
						LOGGER.info("formFactoryTracking formName : "+ formName +"for module : "+ form.getModule().getName()  + " get from form builder");
					}
				}
				if (form == null) {
					return false;
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
				// For older webform client
				if (formModuleName == null && form != null && form.getModule() != null) {
					context.put(ContextNames.MODULE_NAME, form.getModule().getName());
				}
				if (formName.equalsIgnoreCase("web_pm")) {
					for (FormField f : form.getFields()) {
						if (StringUtils.isNotEmpty(f.getName()) && f.getName().equalsIgnoreCase("siteid")) {
							f.setName("site");
							break;
						}
					}
				}
			}
		}
		context.put(FacilioConstants.ContextNames.FORM, form);
		if (form != null) {
			if (AccountUtil.getCurrentUser() == null) {
				form.getFields().addAll(0, FormFactory.getRequesterFormFields(false, true));
				if (CollectionUtils.isNotEmpty(form.getSections())) {
					form.getSections().get(0).getFields().addAll(0, FormFactory.getRequesterFormFields(true, true));
				}
			}
		}

		return false;
	}

	private FacilioForm getChildForm(FacilioModule childModule, String appLinkName) throws Exception {
		FacilioForm form = null;
		if (childModule != null && childModule.getExtendModule() != null) {
			if (appLinkName == null) {
				appLinkName = ApplicationLinkNames.FACILIO_MAIN_APP;
			}
			form = FormsAPI.getDefaultFormFromDBOrFactory(childModule.getExtendModule(), appLinkName);
			if (form != null) {
				form.setDisplayName(childModule.getDisplayName());
			}
		}
		return form;
	}

	private void setFields(FacilioForm form, ModuleBean modBean, List<FormField> fields, String moduleName, FacilioModule childModule, int count) throws Exception {
		FormsAPI.setFieldDetails(modBean, fields, moduleName);
		if (form.getAppLinkName() != null && form.getAppLinkName() != ApplicationLinkNames.FACILIO_MAIN_APP) {
			return;
		}
//		if (form.getFormTypeEnum()  == FormType.PORTAL) {
//			return;
//		}

		if (count == -1) {
			count = Collections.max(fields, Comparator.comparing(s -> s.getSequenceNumber())).getSequenceNumber();
		}
		//commenting out as we force update tenant for wo and pm
//		if (AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_TENANTS) && (formName.equalsIgnoreCase("workOrderForm") || formName.equalsIgnoreCase("web_pm"))) {
//			  fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL, "tenant", ++count, 1));
//		}

		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN) && moduleName.equals("workorder") &&  (form.getName().equals("web_pm") || form.getName().equals("mobile_default") || form.getName().equals("default_workorder_web"))) {
			fields.add(new FormField("safetyPlan", FieldDisplayType.LOOKUP_SIMPLE, "Safety Plan", Required.OPTIONAL,"safetyPlan", 12, 1));
			FormsAPI.setFieldDetails(modBean,fields,moduleName);
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
			boolean isMultiSiteForm = form.getName().equalsIgnoreCase("multi_web_pm");
			if (form.getName().equalsIgnoreCase("web_pm") || isMultiSiteForm) { // Temp...showing custom fields in standard form...will be removed once action in pm
				FacilioForm defaultWoForm = form = FormsAPI.getDefaultFormFromDBOrFactory(modBean.getModule(moduleName), ApplicationLinkNames.FACILIO_MAIN_APP, true);
				long sitesCount = SpaceAPI.getSitesCount();
				customFields = defaultWoForm.getFields().stream().filter(field ->
					field.getField() != null && !field.getField().isDefault() &&
					(!isMultiSiteForm || field.getField().getDataTypeEnum() != FieldType.LOOKUP || sitesCount == 1)
				).map(field -> field.getField()).collect(Collectors.toList());
			}
			if(customFields != null && !customFields.isEmpty() && "space".equalsIgnoreCase(moduleName) ) {
				Set<Long> formFieldFieldIds = form.getFields().stream().map(FormField::getFieldId).collect(Collectors.toSet());
				customFields = customFields.stream().filter(field->formFieldFieldIds.contains(field.getFieldId())).collect(Collectors.toList());
			}
			if (customFields != null && !customFields.isEmpty() && !form.isIgnoreCustomFields()) {
				for (FacilioField f: customFields) {
					fields.add(FormsAPI.getFormFieldFromFacilioField(f, ++count));
				}
			}
		}
	}



}
