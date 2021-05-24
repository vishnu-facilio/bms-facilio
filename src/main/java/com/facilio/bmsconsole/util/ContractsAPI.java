package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.ContractAssociatedTermsContext;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.LabourContractLineItemContext;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.context.PurchaseContractLineItemContext;
import com.facilio.bmsconsole.context.RentalLeaseContractLineItemsContext;
import com.facilio.bmsconsole.context.WarrantyContractLineItemContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.ScheduledRuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.LookupFieldMeta;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;

public class ContractsAPI {

	public static List<Long> fetchAssociatedContractIds(Long assetId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		SelectRecordsBuilder<ContractAssociatedAssetsContext> builder = new SelectRecordsBuilder<ContractAssociatedAssetsContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition("ASSET_ID", "assetId", String.valueOf(assetId),NumberOperators.EQUALS));
				;

		List<ContractAssociatedAssetsContext> list = builder.get();
		List<Long> contractIds = new ArrayList<Long>();
		for(ContractAssociatedAssetsContext associatedAsset : list) {
			contractIds.add(associatedAsset.getContractId());
		}
		return contractIds;
			                 
	}
	
	public static List<ContractAssociatedAssetsContext> fetchAssociatedAssets(Long contractId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ContractAssociatedAssetsContext> builder = new SelectRecordsBuilder<ContractAssociatedAssetsContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition("CONTRACT_ID", "contractId", String.valueOf(contractId),NumberOperators.EQUALS))
				.fetchSupplement((LookupField) fieldsAsMap.get("asset"))
		;
		List<ContractAssociatedAssetsContext> list = builder.get();
				
		
		return list;
			                 
	}
	public static List<ContractAssociatedTermsContext> fetchAssociatedTerms(Long contractId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ContractAssociatedTermsContext> builder = new SelectRecordsBuilder<ContractAssociatedTermsContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition("CONTRACT_ID", "contractId", String.valueOf(contractId),NumberOperators.EQUALS))
				.fetchSupplement((LookupField) fieldsAsMap.get("terms"))
		;
		List<ContractAssociatedTermsContext> list = builder.get();
				
		
		return list;
			                 
	}
	
	public static void updateTermsAssociated(Long id, List<ContractAssociatedTermsContext> associatedTerms) throws Exception {
		if(CollectionUtils.isNotEmpty(associatedTerms)) {
			for(ContractAssociatedTermsContext term : associatedTerms) {
				term.setContractId(id);
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			addRecord(false,associatedTerms , module, fields);
		}
	}

	public static void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields, List<SupplementRecord> supplements) throws Exception {
		if(CollectionUtils.isNotEmpty(list)) {
			InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
					.module(module)
					.fields(fields);
			if(isLocalIdNeeded) {
				insertRecordBuilder.withLocalId();
			}
			if(CollectionUtils.isNotEmpty(supplements)){
				insertRecordBuilder.insertSupplements(supplements);
			}
			insertRecordBuilder.addRecords(list);
			insertRecordBuilder.save();
		}
	}
	public static void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		if(CollectionUtils.isNotEmpty(list)) {
			addRecord(isLocalIdNeeded, list, module, fields, null);
		}
	}
	
	public static ContractsContext getContractDetails(long contractId) throws Exception {
		
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTRACTS);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<ContractsContext> builder = new SelectRecordsBuilder<ContractsContext>()
					.module(module)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
					.select(fields)
				    .andCondition(CriteriaAPI.getIdCondition(contractId, module))
			;
			LookupFieldMeta vendorField = new LookupFieldMeta((LookupField) fieldsAsMap.get("vendor"));
			
			List<LookupField> additionaLookups = new ArrayList<LookupField>();
			additionaLookups.add(vendorField);
			builder.fetchSupplements(additionaLookups);
			List<ContractsContext> contracts = builder.get();
			if(CollectionUtils.isNotEmpty(contracts)) {
				return contracts.get(0);	
			}
			throw new IllegalArgumentException("Appropriate contract not found");
	}
	
	public static List<PurchaseContractLineItemContext> getPurchaseContractLineItems(long contractId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_CONTRACTS_LINE_ITEMS;
		List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<PurchaseContractLineItemContext> builder = new SelectRecordsBuilder<PurchaseContractLineItemContext>()
				.moduleName(lineItemModuleName)
				.select(fields)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
				.andCondition(CriteriaAPI.getCondition("PURCHASE_CONTRACT", "purchaseContractId", String.valueOf(contractId), NumberOperators.EQUALS))
				.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
				(LookupField) fieldsAsMap.get("toolType")));
		return builder.get();
	}
	
	public static List<LabourContractLineItemContext> getLabourContractLineItems(long contractId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String lineItemModuleName = FacilioConstants.ContextNames.LABOUR_CONTRACTS_LINE_ITEMS;
		List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<LabourContractLineItemContext> builder = new SelectRecordsBuilder<LabourContractLineItemContext>()
				.moduleName(lineItemModuleName)
				.select(fields)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
				.andCondition(CriteriaAPI.getCondition("LABOUR_CONTRACT", "labourContractId", String.valueOf(contractId), NumberOperators.EQUALS))
				.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("labour")));
		return builder.get();
	}
	
	public static List<WarrantyContractLineItemContext> getWarrantyContractLineItems(long contractId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String lineItemModuleName = FacilioConstants.ContextNames.WARRANTY_CONTRACTS_LINE_ITEMS;
		List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<WarrantyContractLineItemContext> builder = new SelectRecordsBuilder<WarrantyContractLineItemContext>()
				.moduleName(lineItemModuleName)
				.select(fields)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
				.andCondition(CriteriaAPI.getCondition("WARRANTY_CONTRACT", "warrantyContractId", String.valueOf(contractId), NumberOperators.EQUALS))
				.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("service")));
		return builder.get();
	}
	
	public static List<RentalLeaseContractLineItemsContext> getRentalContractLineItems(long contractId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String lineItemModuleName = FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS_LINE_ITEMS;
		List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<RentalLeaseContractLineItemsContext> builder = new SelectRecordsBuilder<RentalLeaseContractLineItemsContext>()
				.moduleName(lineItemModuleName)
				.select(fields)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
				.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
						(LookupField) fieldsAsMap.get("toolType")));
		return builder.get();
	}

	public static void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields, boolean isChangeSetNeeded, FacilioContext context, List<SupplementRecord> supplements) throws Exception {
		if(data != null) {
			UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));

			if (isChangeSetNeeded) {
				updateRecordBuilder.withChangeSet(ContractsContext.class);
			}

			if(!supplements.isEmpty()) {
				updateRecordBuilder.updateSupplements(supplements);
			}

			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateRecordBuilder.update(data));
			context.put(FacilioConstants.ContextNames.CHANGE_SET, updateRecordBuilder.getChangeSet());
		}

	}
	
	public static void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields, boolean isChangeSetNeeded, FacilioContext context) throws Exception {
		if(data != null) {
			updateRecord(data, module, fields, isChangeSetNeeded, context, null);
		}
		
	}
	
	public static void updateAssetsAssociated(long recordId, List<ContractAssociatedAssetsContext> assets) throws Exception {
		if(CollectionUtils.isNotEmpty(assets)) {
			for(ContractAssociatedAssetsContext asset : assets) {
				asset.setContractId(recordId);
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			ContractsAPI.addRecord(false,assets , module, fields);
		}
	}
		
	public static Long saveExpiryPrefs (Map<String, Object> map, Long contractId, String moduleName) throws Exception {
		ContractsContext contract = getContractDetails(contractId);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Expiry Date Notification");
		workflowRuleContext.setRuleType(RuleType.RECORD_SPECIFIC_RULE);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.SCHEDULED);
		workflowRuleContext.setScheduleType(ScheduledRuleType.BEFORE);
		workflowRuleContext.setTime((String)map.get("time"));
		
		Condition condition = new Condition();
		condition.setFieldName("status");
		condition.setValue(String.valueOf(ContractsContext.Status.APPROVED.getValue()));
		condition.setOperator(EnumOperators.IS);
		condition.setColumnName("Contracts.STATUS");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.setPattern("(1)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		JSONObject json = new JSONObject();
		List<String> ouIdList = (List<String>)map.get("to");
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		
		StringJoiner userEmailStr = new StringJoiner(",");
		for(String ouId : ouIdList) {
			User user = userBean.getUser(Long.parseLong(ouId), false);
			if(user != null) {
				userEmailStr.add(user.getEmail());
			}
		}
		json.put("to", userEmailStr);
		json.put("subject", "Expiry notification");
		json.put("name", "Expiry template");
		String contractTypeName = contract.getContractTypeEnum().name();
		String message = "Hi,\n\nYour "+ contractTypeName.substring(0, 1).toUpperCase() + contractTypeName.substring(1).toLowerCase()+" contract - " + contract.getName() +" from the vendor "+ contract.getVendor().getName() +" is about to expire on "+ DateTimeUtil.getFormattedTime(contract.getEndDate(), "dd-MMM-yyyy") +". Please login to Facilio for more details.\n\nRegards,\nTeam Facilio";
		json.put("message", message);
		WorkflowContext workflow = new WorkflowContext();
		ParameterContext param = new ParameterContext();
		param.setName("org.domain");
		param.setTypeString("String");
		
		workflow.setParameters(Collections.singletonList(param));
		json.put("workflow", FieldUtil.getAsProperties(workflow));
		emailAction.setTemplateJson(json);
		
		workflowRuleContext.setDateFieldId(fieldsMap.get("endDate").getFieldId());
		int days = ((Number) map.get("days")).intValue();
		workflowRuleContext.setInterval(1 * days * 24 * 60 * 60);
		workflowRuleContext.setParentId(contractId);
	   
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static Long saveRenewalPrefs (Map<String, Object> map, Long contractId, String moduleName) throws Exception {
		ContractsContext contract = getContractDetails(contractId);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Renewal Date Notification");
		workflowRuleContext.setRuleType(RuleType.RECORD_SPECIFIC_RULE);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.SCHEDULED);
		workflowRuleContext.setScheduleType(ScheduledRuleType.BEFORE);
		workflowRuleContext.setTime((String)map.get("time"));
		
		Condition condition = new Condition();
		condition.setFieldName("status");
		condition.setValue(String.valueOf(ContractsContext.Status.APPROVED.getValue()));
		condition.setOperator(EnumOperators.IS);
		condition.setColumnName("Contracts.STATUS");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.setPattern("(1)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		JSONObject json = new JSONObject();
		List<String> ouIdList = (List<String>)map.get("to");
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		
		StringJoiner userEmailStr = new StringJoiner(",");
		for(String ouId : ouIdList) {
			User user = userBean.getUser(Long.parseLong(ouId), false);
			if(user != null) {
				userEmailStr.add(user.getEmail());
			}
		}
		json.put("to", userEmailStr);
		json.put("subject", "Renewal notification");
		json.put("name", "Renewal template");
		String contractTypeName = contract.getContractTypeEnum().name();
		String message = "Hi,\n\nYour "+ contractTypeName.substring(0, 1).toUpperCase() + contractTypeName.substring(1).toLowerCase()+" contract - " + contract.getName() + " from the vendor "+ contract.getVendor().getName() +" needs to be renewed on "+ DateTimeUtil.getFormattedTime(contract.getRenewalDate(), "dd-MMM-yyyy") +". Please login to Facilio for more details.\n\nRegards,\nTeam Facilio";
		json.put("message", message);
		WorkflowContext workflow = new WorkflowContext();
		ParameterContext param = new ParameterContext();
		param.setName("org.domain");
		param.setTypeString("String");
		
		workflow.setParameters(Collections.singletonList(param));
		json.put("workflow", FieldUtil.getAsProperties(workflow));
		emailAction.setTemplateJson(json);
		
		workflowRuleContext.setDateFieldId(fieldsMap.get("renewalDate").getFieldId());
		int days = ((Number) map.get("days")).intValue();
		workflowRuleContext.setInterval(1 * days * 24 * 60 * 60);
		workflowRuleContext.setParentId(contractId);
	   
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

		FacilioChain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static Preference getExpiryNotificationPref() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("Expiry Date Notification Preference");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("days", FieldDisplayType.NUMBER, "How many days before the expiry date has to be notified?", Required.REQUIRED, 1, 1));
		fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User", Required.REQUIRED,"users", 1, 1));
		fields.add(new FormField("time", FieldDisplayType.TIME, "Enter Time", Required.REQUIRED,1, 1));
		
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		form.setFields(fields);
		form.setLabelPosition(LabelPosition.TOP);
		return new Preference("expireDateNotification", "Expiry Date Notifications", form, "Expiry Date Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveExpiryPrefs(map, recordId, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}

			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}

		};
		
	}
	
	public static Preference getRenewalNotificationPref() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("Renewal Date Notification Preference");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("days", FieldDisplayType.NUMBER, "How many days before the renewal date has to be notified?", Required.REQUIRED, 1, 1));
		fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User", Required.REQUIRED,"users", 1, 1));
		fields.add(new FormField("time", FieldDisplayType.TIME, "Enter Time", Required.REQUIRED,1, 1));
		
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		form.setFields(fields);
		form.setLabelPosition(LabelPosition.TOP);
		return new Preference("renewalDateNotification", "Renewal Date Notifications", form, "Renewal Date Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveRenewalPrefs(map, recordId, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}

			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}

		};
		
	}
	
	public static Preference getPaymentNotificationPref() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("Payment Notification Preference");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("days", FieldDisplayType.NUMBER, "How many days before the payment date has to be notified?", Required.REQUIRED, 1, 1));
		fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User", Required.REQUIRED,"users", 1, 1));
		//fields.add(new FormField("time", FieldDisplayType.TIME, "Enter Time", Required.REQUIRED,1, 1));
		
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		form.setFields(fields);
		form.setLabelPosition(LabelPosition.TOP);
		return new Preference("paymentNotification", "Payment Notifications", form, "Payment Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = savePaymentPrefs(map, recordId, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}

			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}

		};
		
	}
	
	public static Long savePaymentPrefs (Map<String, Object> map, Long contractId, String moduleName) throws Exception {
		ContractsContext contract = getContractDetails(contractId);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Payment Date Notification");
		workflowRuleContext.setRuleType(RuleType.RECORD_SPECIFIC_RULE);
		
		WorkflowEventContext event = new WorkflowEventContext();
		event.setModuleName(module.getName());
		event.setActivityType(EventType.SCHEDULED);
		workflowRuleContext.setEvent(event);
		workflowRuleContext.setScheduleType(ScheduledRuleType.BEFORE);
//		workflowRuleContext.setTime((String)map.get("time"));
		
		Condition condition = new Condition();
		condition.setFieldName("status");
		condition.setValue(String.valueOf(ContractsContext.Status.APPROVED.getValue()));
		condition.setOperator(EnumOperators.IS);
		condition.setColumnName("Contracts.STATUS");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.setPattern("(1)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		JSONObject json = new JSONObject();
		List<String> ouIdList = (List<String>)map.get("to");
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		
		StringJoiner userEmailStr = new StringJoiner(",");
		for(String ouId : ouIdList) {
			User user = userBean.getUser(Long.parseLong(ouId), false);
			if(user != null) {
				userEmailStr.add(user.getEmail());
			}
		}
		json.put("to", userEmailStr);
		json.put("subject", "Payment notification");
		json.put("name", "Payment template");
		String contractTypeName = contract.getContractTypeEnum().name();
		String message = "Hi,\n\nThe next Payment for "+ contractTypeName.substring(0, 1).toUpperCase() + contractTypeName.substring(1).toLowerCase()+" contract - " + contract.getName() + " from the vendor "+ contract.getVendor().getName() +" is scheduled on "+ DateTimeUtil.getFormattedTime(contract.getNextPaymentDate(), "dd-MMM-yyyy") +". Please login to Facilio for more details.\n\nRegards,\nTeam Facilio";
		json.put("message", message);
		WorkflowContext workflow = new WorkflowContext();
		ParameterContext param = new ParameterContext();
		param.setName("org.domain");
		param.setTypeString("String");
		
		workflow.setParameters(Collections.singletonList(param));
		json.put("workflow", FieldUtil.getAsProperties(workflow));
		emailAction.setTemplateJson(json);
		
		workflowRuleContext.setDateFieldId(fieldsMap.get("nextPaymentDate").getFieldId());
		int days = ((Number) map.get("days")).intValue();
		workflowRuleContext.setInterval(1 * days * 24 * 60 * 60);
		workflowRuleContext.setParentId(contractId);
	   
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
		
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
}
