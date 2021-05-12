package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.InventoryCategoryContext;
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.context.InventoryVendorContext;
import com.facilio.bmsconsole.context.ItemStatusContext;
import com.facilio.bmsconsole.context.ItemTypesCategoryContext;
import com.facilio.bmsconsole.context.ItemTypesStatusContext;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolStatusContext;
import com.facilio.bmsconsole.context.ToolTypesCategoryContext;
import com.facilio.bmsconsole.context.ToolTypesStatusContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.FieldChangeFieldContext;
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
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;

public class InventoryApi {
	
	public static InventoryContext getInventory(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY);
		
		SelectRecordsBuilder<InventoryContext> selectBuilder = new SelectRecordsBuilder<InventoryContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(InventoryContext.class)
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		
		List<InventoryContext> inventories = selectBuilder.get();
		
		if(inventories != null && !inventories.isEmpty()) {
			return inventories.get(0);
		}
		return null;
	}
	
	public static VendorContext getVendor(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDORS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VENDORS);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		
		SelectRecordsBuilder<VendorContext> selectBuilder = new SelectRecordsBuilder<VendorContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(VendorContext.class)
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		
		LookupField registeredBy = (LookupField) fieldsAsMap.get("registeredBy");
		selectBuilder.fetchSupplement(registeredBy);
		
		VendorContext vendor = selectBuilder.fetchFirst();
		
		return vendor;
	}
	
	public static Map<Long, InventoryVendorContext> getInventoryVendorMap(Collection<Long> idList) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<InventoryVendorContext> selectBuilder = new SelectRecordsBuilder<InventoryVendorContext>()
																		.select(modBean.getAllFields("inventory_vendors"))
																		.moduleName("inventory_vendors")
																		.beanClass(InventoryVendorContext.class);
		return selectBuilder.getAsMap();
	}
	
public static List<InventoryVendorContext> getInventoryVendorList() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<InventoryVendorContext> selectBuilder = new SelectRecordsBuilder<InventoryVendorContext>()
																		.select(modBean.getAllFields("inventory_vendors"))
																		.moduleName("inventory_vendors")
																		.beanClass(InventoryVendorContext.class);
		return selectBuilder.get();
	}

	public static List<InventoryCategoryContext> getInventoryCategoryList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<InventoryCategoryContext> selectBuilder = new SelectRecordsBuilder<InventoryCategoryContext>()
				.select(modBean.getAllFields("inventoryCategory")).moduleName("inventoryCategory")
				.beanClass(InventoryCategoryContext.class);
		return selectBuilder.get();
	}

	public static List<ItemTypesCategoryContext> getItemTypesCategoryList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ItemTypesCategoryContext> selectBuilder = new SelectRecordsBuilder<ItemTypesCategoryContext>()
				.select(modBean.getAllFields("itemTypesCategory")).moduleName("itemTypesCategory")
				.beanClass(ItemTypesCategoryContext.class);
		return selectBuilder.get();
	}

	public static List<ToolTypesCategoryContext> getToolTypesCategoryList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ToolTypesCategoryContext> selectBuilder = new SelectRecordsBuilder<ToolTypesCategoryContext>()
				.select(modBean.getAllFields("toolTypesCategory")).moduleName("toolTypesCategory")
				.beanClass(ToolTypesCategoryContext.class);
		return selectBuilder.get();
	}
	
	public static List<ItemTypesStatusContext> getItemTypesStatusList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ItemTypesStatusContext> selectBuilder = new SelectRecordsBuilder<ItemTypesStatusContext>()
				.select(modBean.getAllFields("itemTypesStatus")).moduleName("itemTypesStatus")
				.beanClass(ItemTypesStatusContext.class);
		return selectBuilder.get();
	}
	
	public static List<ToolTypesStatusContext> getToolTypesStatusList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ToolTypesStatusContext> selectBuilder = new SelectRecordsBuilder<ToolTypesStatusContext>()
				.select(modBean.getAllFields("toolTypesStatus")).moduleName("toolTypesStatus")
				.beanClass(ToolTypesStatusContext.class);
		return selectBuilder.get();
	}
	
	public static List<ItemStatusContext> getItemStatusList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ItemStatusContext> selectBuilder = new SelectRecordsBuilder<ItemStatusContext>()
				.select(modBean.getAllFields("itemStatus")).moduleName("itemStatus")
				.beanClass(ItemStatusContext.class);
		return selectBuilder.get();
	}
	
	public static List<ToolStatusContext> getToolStatusList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ToolStatusContext> selectBuilder = new SelectRecordsBuilder<ToolStatusContext>()
				.select(modBean.getAllFields("toolStatus")).moduleName("toolStatus")
				.beanClass(ToolStatusContext.class);
		return selectBuilder.get();
	}
	
	public static Preference getStoreRoomOutOfStockNotificationPref() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("Notify me when Item is out of Stock");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User", Required.REQUIRED, "users", 1, 1));
		
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		form.setFields(fields);
		form.setLabelPosition(LabelPosition.TOP);
		return new Preference("outOfStockNotification", "Out Of Stock Notifications", form, "Out Of Stock Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				Long ruleId1 = saveItemOutOfStockPrefs(map, recordId);
				Long ruleId2 = saveToolOutOfStockPrefs(map, recordId);
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId1);
				ruleIdList.add(ruleId2);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}

			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}

		};
	}
	
	public static Preference getStoreRoomMinQtyNotificationPref() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("Notify me when Item nears Minimum Limit");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User", Required.REQUIRED, "users", 1, 1));
		
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		form.setFields(fields);
		form.setLabelPosition(LabelPosition.TOP);
		return new Preference("minQtyNotification", "Minimum Quantity Notifications", form, "Minimum Quantity Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				Long ruleId1 = saveItemMinQtyPrefs(map, recordId);
				Long ruleId2 = saveToolMinQtyPrefs(map, recordId);
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId1);
				ruleIdList.add(ruleId2);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}

			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}

		};
	}
	
	public static StoreRoomContext getStoreRoomDetails(long storeRoomId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<StoreRoomContext> builder = new SelectRecordsBuilder<StoreRoomContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
			    .andCondition(CriteriaAPI.getIdCondition(storeRoomId, module))
		;
			
		List<StoreRoomContext> storeRooms = builder.get();
		if(CollectionUtils.isNotEmpty(storeRooms)) {
			return storeRooms.get(0);	
		}
		throw new IllegalArgumentException("Appropriate StoreRoom not found");
}
	
	@SuppressWarnings("unchecked")
	public static Long saveItemOutOfStockPrefs (Map<String, Object> map, Long storeRoomId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule storeModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> storeFields = modBean.getAllFields(storeModule.getName());
		Map<String,FacilioField> storeFieldsMap = FieldFactory.getAsMap(storeFields);
		FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> itemFields = modBean.getAllFields(itemModule.getName());
		Map<String,FacilioField> itemFieldsMap = FieldFactory.getAsMap(itemFields);
		
		StoreRoomContext storeRoomDetails = getStoreRoomDetails(storeRoomId);
		
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Item Out of Stock");
		workflowRuleContext.setScheduleType(ScheduledRuleType.AFTER);
		workflowRuleContext.setRuleType(RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE);
		
		WorkflowEventContext event = new WorkflowEventContext();
		event.setModuleName(FacilioConstants.ContextNames.ITEM);
		event.setActivityType(EventType.FIELD_CHANGE);
		workflowRuleContext.setEvent(event);
		
		Condition condition1 = new Condition();
		condition1.setFieldName("quantity");
		condition1.setValue("0");
		condition1.setOperator(NumberOperators.LESS_THAN_EQUAL);
		condition1.setColumnName("Item.QUANTITY");
		
		Condition condition2 = new Condition();
		condition2.setFieldName("storeRoom");
		condition2.setValue(String.valueOf(storeRoomId));
		condition2.setOperator(PickListOperators.IS);
		condition2.setColumnName("Item.STORE_ROOM_ID");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition1);
		criteria.addConditionMap(condition2);
		criteria.setPattern("(1 AND 2)");
		
		workflowRuleContext.setCriteria(criteria);
		if (storeRoomDetails.getSite() != null && storeRoomDetails.getSite().getId() > 0) {
			workflowRuleContext.setSiteId(storeRoomDetails.getSite().getId());
		}		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
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
		json.put("subject", "Alert: ${item.itemType.name} - Out Of Stock");
		json.put("name", "Storeroom Notification Template");
		String message = "Hi,\nThis is to Notify you that #${item.id} ${item.itemType.name} in ${item.storeRoom.name} is currently Out of Stock.\nPlease click the link https://app.facilio.com/app/at/purchase/new/po?create=new to initiate new stock request.\nRegards,\n Facilio";
		json.put("message", message);
		WorkflowContext workflow = new WorkflowContext();
		
		List<ParameterContext> parameters = new ArrayList<ParameterContext>();
		
		ParameterContext param1 = new ParameterContext();
		param1.setName("org.domain");
		param1.setTypeString("String");
		
		ParameterContext param2 = new ParameterContext();
		param2.setName("item.quantity");
		param2.setTypeString("String");
		
		ParameterContext param3 = new ParameterContext();
		param3.setName("item.itemType.name");
		param3.setTypeString("String");
		
		ParameterContext param4 = new ParameterContext();
		param4.setName("item.storeRoom.name");
		param4.setTypeString("String");
		
		ParameterContext param5 = new ParameterContext();
		param5.setName("item.id");
		param5.setTypeString("String");
		
		parameters.add(param1);
		parameters.add(param2);
		parameters.add(param3);
		parameters.add(param4);
		parameters.add(param5);
		
		workflow.setParameters(parameters);
		
	
		JSONArray jsonExpArray = new JSONArray();
		JSONObject exp1 = new JSONObject();
		exp1.put("name", "org.domain");
		exp1.put("constant", "${org.domain}");
		JSONObject exp2 = new JSONObject();
		exp2.put("name", "item.quantity");
		exp2.put("constant", "${item.quantity}");
		JSONObject exp3 = new JSONObject();
		exp3.put("name", "item.itemType.name");
		exp3.put("constant", "${item.itemType.name}");
		JSONObject exp4 = new JSONObject();
		exp4.put("name", "item.storeRoom.name");
		exp4.put("constant", "${item.storeRoom.name}");
		JSONObject exp5 = new JSONObject();
		exp5.put("name", "item.id");
		exp5.put("constant", "${item.id}");
		jsonExpArray.add(exp1);
		jsonExpArray.add(exp2);
		jsonExpArray.add(exp3);
		jsonExpArray.add(exp4);
		jsonExpArray.add(exp5);
	    
		
		workflow.setExpressions(jsonExpArray);
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<FieldChangeFieldContext> fieldsChanged = new ArrayList<FieldChangeFieldContext>();
		FacilioField fieldChangeId = fieldMap.get("quantity");
    	FieldChangeFieldContext field = new FieldChangeFieldContext();
		field.setFieldId(fieldChangeId.getFieldId());
		fieldsChanged.add(field);
		workflowRuleContext.setFields(fieldsChanged);
		
		
		json.put("workflow", FieldUtil.getAsProperties(workflow));
		emailAction.setTemplateJson(json);

		//add rule,action and job
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();

		chain.getContext().put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
		chain.getContext().put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
		
		chain.execute();
	
		return (Long) chain.getContext().get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static Long saveToolOutOfStockPrefs (Map<String, Object> map, Long storeRoomId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule storeModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> storeFields = modBean.getAllFields(storeModule.getName());
		Map<String,FacilioField> storeFieldsMap = FieldFactory.getAsMap(storeFields);
		FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> toolFields = modBean.getAllFields(toolModule.getName());
		Map<String,FacilioField> toolFieldsMap = FieldFactory.getAsMap(toolFields);
		
		StoreRoomContext storeRoomDetails = getStoreRoomDetails(storeRoomId);
		
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Tool Out of Stock");
		workflowRuleContext.setScheduleType(ScheduledRuleType.AFTER);
		workflowRuleContext.setRuleType(RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE);
		
		WorkflowEventContext event = new WorkflowEventContext();
		event.setModuleName(FacilioConstants.ContextNames.TOOL);
		event.setActivityType(EventType.FIELD_CHANGE);
		workflowRuleContext.setEvent(event);
		
		Condition condition1 = new Condition();
		condition1.setFieldName("currentQuantity");
		condition1.setValue("0");
		condition1.setOperator(NumberOperators.LESS_THAN_EQUAL);
		condition1.setColumnName("Tool.CURRENT_QUANTITY");
		
		Condition condition2 = new Condition();
		condition2.setFieldName("storeRoom");
		condition2.setValue(String.valueOf(storeRoomId));
		condition2.setOperator(PickListOperators.IS);
		condition2.setColumnName("Tool.STORE_ROOM_ID");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition1);
		criteria.addConditionMap(condition2);
		criteria.setPattern("(1 AND 2)");
		
		workflowRuleContext.setCriteria(criteria);
		if (storeRoomDetails.getSite() != null && storeRoomDetails.getSite().getId() > 0) {
			workflowRuleContext.setSiteId(storeRoomDetails.getSite().getId());
		}
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
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
		json.put("subject", "Alert: ${tool.toolType.name} - Out Of Stock");
		json.put("name", "Storeroom Notification Template");
		String message = "Hi,\nThis is to Notify you that #${tool.id} ${tool.toolType.name} in ${tool.storeRoom.name} is currently Out of Stock.\nPlease click the link https://app.facilio.com/app/at/purchase/new/po?create=new to initiate new stock request.\nRegards,\n Facilio";
		json.put("message", message);
		WorkflowContext workflow = new WorkflowContext();
		
		List<ParameterContext> parameters = new ArrayList<ParameterContext>();
		
		ParameterContext param1 = new ParameterContext();
		param1.setName("org.domain");
		param1.setTypeString("String");
		
		ParameterContext param2 = new ParameterContext();
		param2.setName("tool.currentQuantity");
		param2.setTypeString("String");
		
		ParameterContext param3 = new ParameterContext();
		param3.setName("tool.toolType.name");
		param3.setTypeString("String");
		
		ParameterContext param4 = new ParameterContext();
		param4.setName("tool.storeRoom.name");
		param4.setTypeString("String");
		
		ParameterContext param5 = new ParameterContext();
		param5.setName("tool.id");
		param5.setTypeString("String");
		
		parameters.add(param1);
		parameters.add(param2);
		parameters.add(param3);
		parameters.add(param4);
		parameters.add(param5);
		
		workflow.setParameters(parameters);
		
	
		JSONArray jsonExpArray = new JSONArray();
		JSONObject exp1 = new JSONObject();
		exp1.put("name", "org.domain");
		exp1.put("constant", "${org.domain}");
		JSONObject exp2 = new JSONObject();
		exp2.put("name", "tool.currentQuantity");
		exp2.put("constant", "${tool.currentQuantity}");
		JSONObject exp3 = new JSONObject();
		exp3.put("name", "tool.toolType.name");
		exp3.put("constant", "${tool.toolType.name}");
		JSONObject exp4 = new JSONObject();
		exp4.put("name", "tool.storeRoom.name");
		exp4.put("constant", "${tool.storeRoom.name}");
		JSONObject exp5 = new JSONObject();
		exp5.put("name", "tool.id");
		exp5.put("constant", "${tool.id}");
		jsonExpArray.add(exp1);
		jsonExpArray.add(exp2);
		jsonExpArray.add(exp3);
		jsonExpArray.add(exp4);
		jsonExpArray.add(exp5);
	    
		
		workflow.setExpressions(jsonExpArray);
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<FieldChangeFieldContext> fieldsChanged = new ArrayList<FieldChangeFieldContext>();
		FacilioField fieldChangeId = fieldMap.get("currentQuantity");
    	FieldChangeFieldContext field = new FieldChangeFieldContext();
		field.setFieldId(fieldChangeId.getFieldId());
		fieldsChanged.add(field);
		workflowRuleContext.setFields(fieldsChanged);
		
		
		json.put("workflow", FieldUtil.getAsProperties(workflow));
		emailAction.setTemplateJson(json);

		//add rule,action and job
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();

		chain.getContext().put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
		chain.getContext().put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
		
		chain.execute();
	
		return (Long) chain.getContext().get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static Long saveItemMinQtyPrefs (Map<String, Object> map, Long storeRoomId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule storeModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> storeFields = modBean.getAllFields(storeModule.getName());
		Map<String,FacilioField> storeFieldsMap = FieldFactory.getAsMap(storeFields);
		FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> itemFields = modBean.getAllFields(itemModule.getName());
		Map<String,FacilioField> itemFieldsMap = FieldFactory.getAsMap(itemFields);
		
		StoreRoomContext storeRoomDetails = getStoreRoomDetails(storeRoomId);
		
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Item reaching Minimum Quantity");
		workflowRuleContext.setScheduleType(ScheduledRuleType.AFTER);
		workflowRuleContext.setRuleType(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE);
		
		WorkflowEventContext event = new WorkflowEventContext();
		event.setModuleName(FacilioConstants.ContextNames.ITEM);
		event.setActivityType(EventType.FIELD_CHANGE);
		workflowRuleContext.setEvent(event);
		
		Condition condition1 = new Condition();
		condition1.setFieldName("quantity");
		condition1.setValue("item.minimumQuantity");
		condition1.setOperatorId(77);
		condition1.setColumnName("Item.QUANTITY");
		
		Condition condition2 = new Condition();
		condition2.setFieldName("storeRoom");
		condition2.setValue(String.valueOf(storeRoomId));
		condition2.setOperator(PickListOperators.IS);
		condition2.setColumnName("Item.STORE_ROOM_ID");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition1);
		criteria.addConditionMap(condition2);
		criteria.setPattern("(1 AND 2)");
		
		workflowRuleContext.setCriteria(criteria);
		if (storeRoomDetails.getSite() != null && storeRoomDetails.getSite().getId() > 0) {
			workflowRuleContext.setSiteId(storeRoomDetails.getSite().getId());
		}
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
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
		json.put("subject", "Alert: ${item.itemType.name} - About to Reach Out Of Stock");
		json.put("name", "Storeroom Notification Template");
		String message = "Hi,\nThis is to Notify you that #${item.id} ${item.itemType.name} in ${item.storeRoom.name} is nearing Lowest Inventory limit.\nPlease click the link https://app.facilio.com/app/at/purchase/new/po?create=new to initiate new stock request.\nRegards,\n Facilio";
		json.put("message", message);
		WorkflowContext workflow = new WorkflowContext();
		
		List<ParameterContext> parameters = new ArrayList<ParameterContext>();
		
		ParameterContext param1 = new ParameterContext();
		param1.setName("org.domain");
		param1.setTypeString("String");
		
		ParameterContext param2 = new ParameterContext();
		param2.setName("item.quantity");
		param2.setTypeString("String");
		
		ParameterContext param3 = new ParameterContext();
		param3.setName("item.itemType.name");
		param3.setTypeString("String");
		
		ParameterContext param4 = new ParameterContext();
		param4.setName("item.storeRoom.name");
		param4.setTypeString("String");
		
		ParameterContext param5 = new ParameterContext();
		param5.setName("item.id");
		param5.setTypeString("String");
		
		parameters.add(param1);
		parameters.add(param2);
		parameters.add(param3);
		parameters.add(param4);
		parameters.add(param5);
		
		workflow.setParameters(parameters);
		
	
		JSONArray jsonExpArray = new JSONArray();
		JSONObject exp1 = new JSONObject();
		exp1.put("name", "org.domain");
		exp1.put("constant", "${org.domain}");
		JSONObject exp2 = new JSONObject();
		exp2.put("name", "item.quantity");
		exp2.put("constant", "${item.quantity}");
		JSONObject exp3 = new JSONObject();
		exp3.put("name", "item.itemType.name");
		exp3.put("constant", "${item.itemType.name}");
		JSONObject exp4 = new JSONObject();
		exp4.put("name", "item.storeRoom.name");
		exp4.put("constant", "${item.storeRoom.name}");
		JSONObject exp5 = new JSONObject();
		exp5.put("name", "item.id");
		exp5.put("constant", "${item.id}");
		jsonExpArray.add(exp1);
		jsonExpArray.add(exp2);
		jsonExpArray.add(exp3);
		jsonExpArray.add(exp4);
		jsonExpArray.add(exp5);
	    
		
		workflow.setExpressions(jsonExpArray);
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<FieldChangeFieldContext> fieldsChanged = new ArrayList<FieldChangeFieldContext>();
		if(fieldMap.containsKey("isUnderstocked")) {
			FacilioField fieldChangeId = fieldMap.get("isUnderstocked");
	    	FieldChangeFieldContext field = new FieldChangeFieldContext();
			field.setFieldId(fieldChangeId.getFieldId());
			fieldsChanged.add(field);
		}
		workflowRuleContext.setFields(fieldsChanged);
		
		
		json.put("workflow", FieldUtil.getAsProperties(workflow));
		emailAction.setTemplateJson(json);

		//add rule,action and job
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();

		chain.getContext().put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
		chain.getContext().put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
		
		chain.execute();
	
		return (Long) chain.getContext().get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static Long saveToolMinQtyPrefs (Map<String, Object> map, Long storeRoomId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule storeModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> storeFields = modBean.getAllFields(storeModule.getName());
		Map<String,FacilioField> storeFieldsMap = FieldFactory.getAsMap(storeFields);
		FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> toolFields = modBean.getAllFields(toolModule.getName());
		Map<String,FacilioField> toolFieldsMap = FieldFactory.getAsMap(toolFields);
		
		StoreRoomContext storeRoomDetails = getStoreRoomDetails(storeRoomId);
		
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Tool reaching Minimum Quantity");
		workflowRuleContext.setScheduleType(ScheduledRuleType.AFTER);
		workflowRuleContext.setRuleType(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE);
		
		WorkflowEventContext event = new WorkflowEventContext();
		event.setModuleName(FacilioConstants.ContextNames.TOOL);
		event.setActivityType(EventType.FIELD_CHANGE);
		workflowRuleContext.setEvent(event);
		
		Condition condition1 = new Condition();
		condition1.setFieldName("currentQuantity");
		condition1.setValue("tool.minimumQuantity");
		condition1.setOperatorId(77);
		condition1.setColumnName("Tool.CURRENT_QUANTITY");
		
		Condition condition2 = new Condition();
		condition2.setFieldName("storeRoom");
		condition2.setValue(String.valueOf(storeRoomId));
		condition2.setOperator(PickListOperators.IS);
		condition2.setColumnName("Tool.STORE_ROOM_ID");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition1);
		criteria.addConditionMap(condition2);
		criteria.setPattern("(1 AND 2)");
		
		workflowRuleContext.setCriteria(criteria);
		if (storeRoomDetails.getSite() != null && storeRoomDetails.getSite().getId() > 0) {
			workflowRuleContext.setSiteId(storeRoomDetails.getSite().getId());
		}
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
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
		json.put("subject", "Alert: ${tool.toolType.name} - About to Reach Out Of Stock");
		json.put("name", "Storeroom Notification Template");
		String message = "Hi,\nThis is to Notify you that #${tool.id} ${tool.toolType.name} in ${tool.storeRoom.name} is nearing Lowest Inventory limit.\nPlease click the link https://app.facilio.com/app/at/purchase/new/po?create=new to initiate new stock request.\nRegards,\n Facilio";
		json.put("message", message);
		WorkflowContext workflow = new WorkflowContext();
		
		List<ParameterContext> parameters = new ArrayList<ParameterContext>();
		
		ParameterContext param1 = new ParameterContext();
		param1.setName("org.domain");
		param1.setTypeString("String");
		
		ParameterContext param2 = new ParameterContext();
		param2.setName("tool.currentQuantity");
		param2.setTypeString("String");
		
		ParameterContext param3 = new ParameterContext();
		param3.setName("tool.toolType.name");
		param3.setTypeString("String");
		
		ParameterContext param4 = new ParameterContext();
		param4.setName("tool.storeRoom.name");
		param4.setTypeString("String");
		
		ParameterContext param5 = new ParameterContext();
		param5.setName("tool.id");
		param5.setTypeString("String");
		
		parameters.add(param1);
		parameters.add(param2);
		parameters.add(param3);
		parameters.add(param4);
		parameters.add(param5);
		
		workflow.setParameters(parameters);
		
	
		JSONArray jsonExpArray = new JSONArray();
		JSONObject exp1 = new JSONObject();
		exp1.put("name", "org.domain");
		exp1.put("constant", "${org.domain}");
		JSONObject exp2 = new JSONObject();
		exp2.put("name", "tool.currentQuantity");
		exp2.put("constant", "${tool.currentQuantity}");
		JSONObject exp3 = new JSONObject();
		exp3.put("name", "tool.toolType.name");
		exp3.put("constant", "${tool.toolType.name}");
		JSONObject exp4 = new JSONObject();
		exp4.put("name", "tool.storeRoom.name");
		exp4.put("constant", "${tool.storeRoom.name}");
		JSONObject exp5 = new JSONObject();
		exp5.put("name", "tool.id");
		exp5.put("constant", "${tool.id}");
		jsonExpArray.add(exp1);
		jsonExpArray.add(exp2);
		jsonExpArray.add(exp3);
		jsonExpArray.add(exp4);
		jsonExpArray.add(exp5);
	    
		
		workflow.setExpressions(jsonExpArray);
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<FieldChangeFieldContext> fieldsChanged = new ArrayList<FieldChangeFieldContext>();
		if(fieldMap.containsKey("isUnderstocked")) {
			FacilioField fieldChangeId = fieldMap.get("isUnderstocked");
	    	FieldChangeFieldContext field = new FieldChangeFieldContext();
			field.setFieldId(fieldChangeId.getFieldId());
			fieldsChanged.add(field);
		}
		workflowRuleContext.setFields(fieldsChanged);
		
		
		json.put("workflow", FieldUtil.getAsProperties(workflow));
		emailAction.setTemplateJson(json);

		//add rule,action and job
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();

		chain.getContext().put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
		chain.getContext().put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
		
		chain.execute();
	
		return (Long) chain.getContext().get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	

	public static Preference getRegisterVendorMailNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("registerVendor_MailNotification", "Register Vendor_Email", form, "Notify Vendor to upload COI when they are registered") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveRegisterVendorMailNotificationPrefs(map, module.getName());
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
	
	public static Long saveRegisterVendorMailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Vendor Registration Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition hasInsurance = new Condition();
		hasInsurance.setFieldName("hasInsurance");
		hasInsurance.setOperator(BooleanOperators.IS);
		hasInsurance.setValue("false");
		hasInsurance.setColumnName("Vendors.HAS_INSURANCE");
		
		
		Condition email = new Condition();
		email.setFieldName("primaryContactEmail");
		email.setOperator(CommonOperators.IS_NOT_EMPTY);
		email.setValue("");
		email.setColumnName("Vendors.PRIMARY_CONTACT_EMAIL");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(hasInsurance);
		criteria.addConditionMap(email);
		
		criteria.setPattern("(1 and 2)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(116);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}


	public static boolean checkIfInventoryModule(String modName){
		
		//temp soln until these modules are moved to v3

		if(modName.equals(FacilioConstants.ContextNames.STORE_ROOM) || modName.equals(FacilioConstants.ContextNames.ITEM) || modName.equals(FacilioConstants.ContextNames.PURCHASED_ITEM) || modName.equals(FacilioConstants.ContextNames.TOOL) || modName.equals(FacilioConstants.ContextNames.ITEM_TRANSACTIONS) || modName.equals(FacilioConstants.ContextNames.TOOL_TRANSACTIONS) || modName.equals(FacilioConstants.ContextNames.SERVICE)) {
			return true;
		}
		return false;

	}
	
}
