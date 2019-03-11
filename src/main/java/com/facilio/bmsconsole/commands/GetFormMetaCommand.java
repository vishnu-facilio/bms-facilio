package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioField.FieldDisplayType;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetFormMetaCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String[] formNames = (String[]) context.get(FacilioConstants.ContextNames.FORM_NAMES);
		Long formId = (Long) context.get(FacilioConstants.ContextNames.FORM_ID);
		List<FacilioForm> forms = new ArrayList<>();
		if (formNames != null && formNames.length > 0) {
			for (String formName: formNames) {
				FacilioForm form = getFormFromDB(formName);
				if (form == null) {
					if (FormFactory.getForm(formName) == null && !(formName.startsWith("default"))) {
//						throw new IllegalArgumentException("Invalid Form " + formName);
					}
					else if (FormFactory.getForm(formName) == null && formName.startsWith("default")) {
						ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						String modname = formName.replaceAll("default_", ""); 
						FacilioModule module = bean.getModule(modname);
						String extendedModName = module.getExtendModule().getName();
						formName = "default_"+extendedModName;
					}
					form = new FacilioForm(FormFactory.getForm(formName));
					List<FormField> fields = form.getFields();
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					String moduleName = form.getModule().getName();
					form.setModule(modBean.getModule(moduleName));
					
					for (FormField f : fields) {
						String fieldName = f.getName();
						FacilioField field = modBean.getField(fieldName, moduleName);
						if (field != null) {
							f.setField(field);
							f.setFieldId(field.getFieldId());
						}
					}

					int count = fields.size();
					try {
						if (AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_TENANTS) && (formName.equalsIgnoreCase("workOrderForm") || formName.equalsIgnoreCase("web_pm"))) {
						  fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL, "tenant", ++count, 1));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<FacilioField> customFields = modBean.getAllCustomFields(moduleName);
					if (customFields != null && !customFields.isEmpty()) {
						for (FacilioField f: customFields) {
							count = count + 1;
							FormField formFields = new FormField(f.getName(), f.getDisplayType(), f.getDisplayName(), FormField.Required.OPTIONAL, count, 1);
							formFields.setField(f);
							formFields.setFieldId(f.getFieldId());
							fields.add(formFields);
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
				forms.add(form);
			}
			context.put(FacilioConstants.ContextNames.FORMS, forms);
		}
		else if(formId != null) {
			FacilioForm form= FormsAPI.getFormFromDB(formId);
			forms.add(form);
			context.put(FacilioConstants.ContextNames.FORMS, forms);
		}
		return false;
	}
	
	public static FacilioForm getFormFromDB(String formName) throws Exception {
		Criteria formNameCriteria = new Criteria();
		Condition condition = new Condition();
		condition.setColumnName("NAME");
		condition.setFieldName("name");
		condition.setOperator(StringOperators.IS);
		condition.setValue(formName);
		formNameCriteria.addAndCondition(condition);
		
		List<FacilioForm> forms = FormsAPI.getFormFromDB(formNameCriteria);
		if (forms == null || forms.isEmpty()) {
			return null;
		}
		return forms.get(0);
	}

}
