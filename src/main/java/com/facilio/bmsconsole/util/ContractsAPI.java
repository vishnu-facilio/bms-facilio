package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.ContractAssociatedTermsContext;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.LabourContractLineItemContext;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.context.PurchaseContractLineItemContext;
import com.facilio.bmsconsole.context.RentalLeaseContractLineItemsContext;
import com.facilio.bmsconsole.context.WarrantyContractLineItemContext;
import com.facilio.bmsconsole.forms.FacilioForm;
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
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;

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
				.fetchLookup((LookupField) fieldsAsMap.get("asset"))
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
				.fetchLookup((LookupField) fieldsAsMap.get("terms"))
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
	public static void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
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
				.fetchLookups(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
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
				.fetchLookups(Arrays.asList((LookupField) fieldsAsMap.get("labour")));
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
				.fetchLookups(Arrays.asList((LookupField) fieldsAsMap.get("service")));
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
				.fetchLookups(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
						(LookupField) fieldsAsMap.get("toolType")));
		return builder.get();
	}
	
	public static void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields, boolean isChangeSetNeeded, FacilioContext context) throws Exception {
		
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		
		if (isChangeSetNeeded) {
			updateRecordBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
		}
		
		
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateRecordBuilder.update(data));
		context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, updateRecordBuilder.getChangeSet());
		
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
		
	public static WorkflowRuleContext saveExpiryPrefs (Map<String, Object> map, Long contractId) throws Exception {
		ContractsContext contract = getContractDetails(contractId);
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Expiry Date Notification");
		workflowRuleContext.setRuleType(RuleType.RECORD_SPECIFIC_RULE);
		
		WorkflowEventContext event = new WorkflowEventContext();
		event.setModuleName(FacilioConstants.ContextNames.CONTRACTS);
		event.setActivityType(EventType.SCHEDULED);
		workflowRuleContext.setEvent(event);
		workflowRuleContext.setScheduleType(ScheduledRuleType.ON);
		workflowRuleContext.setTime("10:00");
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		JSONObject json = new JSONObject();
		json.put("to", map.get("to"));
		json.put("subject", "Expiry notification");
		String message = "Your contract expires at ${expiryDate}";
		Map<String, Object> substitutorMap = new HashMap<String, Object>();
		substitutorMap.put("expiryDate", contract.getEndDate());
		StringSubstitutor.replace(message, substitutorMap);
		json.put("message", message);
		emailAction.setTemplateJson(json);
		workflowRuleContext.addAction(emailAction);
		
		workflowRuleContext.setDateFieldId(1422);
		int days = ((Number) map.get("days")).intValue();
		workflowRuleContext.setInterval(-1 * days * 24 * 60 * 60);
		
		workflowRuleContext.setParentId(contractId);
		SingleRecordRuleAPI.addWorkflowRule(workflowRuleContext);
		return workflowRuleContext;
	}
	
	public static Preference getExpiryNotificationPref() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("Sample");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("days", FieldDisplayType.NUMBER, "How many days before?", Required.REQUIRED, 1, 1));
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		return new Preference("expireDateNotification", form) {
			@Override
			public WorkflowRuleContext subsituteAndEnable(Map<String, Object> map, Long recordId) throws Exception {
				return ContractsAPI.saveExpiryPrefs(map, recordId);
			}
		};
		
	}
	
	public static Preference getExpiryNotificationPref2() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("Sample");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("days", FieldDisplayType.NUMBER, "How many days before?", Required.REQUIRED, 1, 1));
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		return new Preference("expireDateNotification", form) {
			@Override
			public WorkflowRuleContext subsituteAndEnable(Map<String, Object> map, Long recordId) throws Exception {
				return null;
			}
		};
		
	}

}
