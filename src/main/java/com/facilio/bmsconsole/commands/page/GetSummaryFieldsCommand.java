package com.facilio.bmsconsole.commands.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class GetSummaryFieldsCommand extends FacilioCommand {
	
	String moduleName;
	FacilioModule module;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		moduleName = (String) context.get(ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		module = modBean.getModule(moduleName);
				
		long recordId = (long) context.get(ContextNames.ID);
		
		long formId = (long) context.get(ContextNames.FORM_ID);
		long widgetId = (long) context.get(ContextNames.WIDGET_ID);
		
		if (widgetId != -1) {
			// TODO check if fields for the widget/record and return if any
		}
		
		
		ApplicationContext currentApp = AccountUtil.getCurrentApp();
		List<FormField> fields = null;
		if (currentApp.getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP)) {
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			if (orgId == 407 || orgId == 418) {
				fields = getFieldsForAtre(modBean);
			}
		}
		
		if (fields == null) {
			boolean isAtg = AccountUtil.getCurrentOrg().getOrgId() == 406;
			
			FacilioForm form = fetchForm(formId);
			if (form == null) {
				fields = getAllFields(modBean);
			}
			else {
				fields = form.getFields().stream().filter(formField -> formField.getField() != null && !formField.getField().isMainField() &&
						(formField.getHideField() == null || !formField.getHideField()))
						.collect(Collectors.toList());
				if (!isAtg) {
					
				}
			}
			
			if (!isAtg) {	// Sort based on form
				int count = Collections.max(fields, Comparator.comparing(s -> s.getSequenceNumber())).getSequenceNumber();
				addModuleAndSystemFields(modBean, fields, count);
				sort(fields);
			}
		}
		
		context.put("fields", fields);
		
		return false;
	}
	
	private FacilioForm fetchForm(long formId) throws Exception {
		FacilioChain chain = FacilioChainFactory.getFormMetaChain();
		Context formContext = chain.getContext();
		
		formContext.put(FacilioConstants.ContextNames.FORM_ID, formId);
		formContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();
		
		return (FacilioForm) formContext.get(FacilioConstants.ContextNames.FORM);
	}
	
	private List<FormField> getAllFields(ModuleBean modBean) throws Exception {
		List<FormField> fields = new ArrayList<>();
		List<FacilioField> allFields = modBean.getAllFields(moduleName);
		int count = 0;
		for(FacilioField field: allFields) {
			if (!field.isMainField()) {
				fields.add(FormsAPI.getFormFieldFromFacilioField(field, ++count));
			}
		}
		return fields;
	}
	
	private void sort(List<FormField> fields) {
		fields.sort(new Comparator<FormField>() {
			@Override
			public int compare(FormField f1, FormField f2) {
				if (f2.getDisplayTypeEnum() == FieldDisplayType.TEXTAREA) 
					return 1;
				if (f1.getDisplayTypeEnum() == FieldDisplayType.TEXTAREA) 
					return -1;
				return f1.getDisplayName().compareTo(f2.getDisplayName());
			}
		});
	}
	
	private void addModuleAndSystemFields(ModuleBean modBean, List<FormField> fields, int count) throws Exception {
		List<String> additionalFields = null;
		switch(moduleName) {
			case ContextNames.TENANT_UNIT_SPACE:
				additionalFields = tenantUnit;
				break;
		}
		
		if (additionalFields != null) {
			List<String> fieldNames = fields.stream().map(FormField::getName).collect(Collectors.toList());
			
			List<FacilioField> allFields = modBean.getAllFields(moduleName);
			for(FacilioField field: allFields) {
				String name = field.getName();
				if (additionalFields.contains(name) && !fieldNames.contains(name)) {
					fields.add(FormsAPI.getFormFieldFromFacilioField(field, ++count));
				}
			}
		}
		
		if (FieldUtil.isSystemFieldsPresent(module)) {
			for(String name: FieldFactory.getSystemFieldNames()) {
				FacilioField systemField = FieldFactory.getSystemField(name, module);
				fields.add(FormsAPI.getFormFieldFromFacilioField(systemField, ++count));
			}
		}
	}
	
	
	// To be removed once page db support is there
	private List<FormField> getFieldsForAtre (ModuleBean modBean) throws Exception {
		List<FormField> fields = new ArrayList<FormField>();
		
		List<FacilioField> allFields = modBean.getAllFields(moduleName);
		
		for(String name: FieldFactory.getSystemFieldNames()) {
			allFields.add(FieldFactory.getSystemField(name, module));
		}
		
		List<String> atreFields = getAtreFieldMap().get(moduleName);
		if (atreFields == null) {
			return null;
		}
		int count = 0;
		for(FacilioField field: allFields) {
			if (atreFields.contains(field.getName())) {
				fields.add(FormsAPI.getFormFieldFromFacilioField(field, ++count));
			}
		}
		
		return fields;
	}
	
	private static Map<String, List<String>> getAtreFieldMap() {
		Map<String, List<String>> fieldMap = new HashMap<>();
		fieldMap.put("tenantunit", tenantUnitAtre);
		fieldMap.put("tenantcontact", tenantContact);
		fieldMap.put("custom_contracts", customContracts);
		fieldMap.put("custom_contractunits", customContractunits);
		fieldMap.put("custom_payment", customPayment);
		fieldMap.put("custom_receipts", customReceipts);
		fieldMap.put("peopleannouncement", peopleAnnouncement);

		return fieldMap;
	}
	
	
	/******* Module Based Fields **********/
	
	private static final List<String> tenantUnit = Collections.unmodifiableList(Arrays.asList(new String[] {
            "siteId",
            "building",
            "floor"
    }));
	
	/************** Module Based End *********/
	
	
	
	
	
	/******* ATRE Fields.. TODO Remove *****************/
	private static final List<String> tenantUnitAtre = Collections.unmodifiableList(Arrays.asList(new String[] {
            "description",
            "area",
            "maxOccupancy",
            "singleline_1",
            "site",
            "building",
            "space"
    }));
	
	private static final List<String> tenantContact = Collections.unmodifiableList(Arrays.asList(new String[] {
			"email",
			"phone",
			"isPrimaryContact"
    }));
	
	private static final List<String> customContracts = Collections.unmodifiableList(Arrays.asList(new String[] {
			"number",
			"date_1",
			"multiline",
			"multiline_1",
			"number_1",
			"singleline_2",
			"number_3",
			"picklist",
			"picklist_2",
			"singleline_3",
			"number_2",
			"sysCreatedTime",
			"sysCreatedBy",
			"sysModifiedTime",
			"sysModifiedBy"
    }));

	private static final List<String> customContractunits = Collections.unmodifiableList(Arrays.asList(new String[] {
			"contractid",
			"siteId",
			"sysCreatedTime",
			"sysCreatedBy",
			"sysModifiedTime",
			"sysModifiedBy"
    }));
	
	private static final List<String> customPayment = Collections.unmodifiableList(Arrays.asList(new String[] {
			"number",
			"date_1",
			"date",
			"date_2",
			"singleline_2",
			"number_3",
			"singleline_1",
			"date_3",
			"sysModifiedBy",
			"sysCreatedTime",
			"sysModifiedTime",
			"sysCreatedBy",
			"number_2",
			"number_1"
    }));
	
	private static final List<String> customReceipts = Collections.unmodifiableList(Arrays.asList(new String[] {
			"singleline_2",
			"singleline_1",
			"date",
			"singleline_3",
			"singleline_4",
			"paymentmilestone",
			"number",
			"date_1",
			"sysModifiedBy",
			"sysCreatedTime",
			"sysModifiedTime",
			"sysCreatedBy"
    }));


	private static final List<String> peopleAnnouncement = Collections.unmodifiableList(Arrays.asList(new String[] {
			"category",
			"expiryDate",
			"sysCreatedTime",
			"sysCreatedBy"
	}));

	/******* ATRE End ******************/
}
