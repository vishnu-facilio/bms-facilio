package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;

public class AddTicketActivityCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> ids = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(ids != null && !ids.isEmpty()) 
		{
			EventType activityType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			
			if(activityType != null) {
				context.put(FacilioConstants.TicketActivity.MODIFIED_TIME, System.currentTimeMillis());
				Boolean isPublicRequest = (Boolean) context.get(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST);
				// Temp handling for KFI portal 
				if (isPublicRequest != null && isPublicRequest && AccountUtil.getCurrentUser() == null) {
					User user = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
					AccountUtil.getCurrentAccount().setUser(user);
				}
				context.put(FacilioConstants.TicketActivity.MODIFIED_USER, AccountUtil.getCurrentUser().getId());
				
				computeAndAddActivity(context);
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Long, ? extends TicketContext> getNewTickets(List<Long> ids, FacilioModule module) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		
		String id = StringUtils.join(ids, ",");
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(id);
		
		SelectRecordsBuilder<? extends TicketContext> builder = new SelectRecordsBuilder<TicketContext>()
																.module(module)
																.select(fields)
																.beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
																.andCondition(idCondition);
																
		
		return builder.getAsMap();
		
		
	}
	
	private void computeAndAddActivity(Context context) throws Exception {
		List<Long> ids = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		long modifiedTime = (long) context.get(FacilioConstants.TicketActivity.MODIFIED_TIME);
		long modifiedBy = (long) context.get(FacilioConstants.TicketActivity.MODIFIED_USER);
		EventType activityType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		Map<Long, ? extends TicketContext> newTickets = getNewTickets(ids, module);
		List<? extends TicketContext> oldTickets = (List<? extends TicketContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldsMap = new HashMap<>();
		for(FacilioField field : fields)
		{
			fieldsMap.put(field.getName(), field);
		}
		Map<String, LookupField> lookupFields = getLookupFields(moduleName);
		
		boolean isUpdate = false;
		Map<Long, TicketContext> oldTicketMap = new HashMap<>();
		
		if(oldTickets != null && !oldTickets.isEmpty()) {
			isUpdate = true;
			for(TicketContext oldTicket : oldTickets) {
				oldTicketMap.put(oldTicket.getId(), oldTicket);
			}
		}
		
		GenericInsertRecordBuilder insertActivityBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getTicketActivityModule().getTableName())
														.fields(FieldFactory.getTicketActivityFields());
		
		for(long recordId : ids) {
			Map<String, Object> newTicketProps = FieldUtil.getAsProperties(newTickets.get(recordId));
			if(newTicketProps != null) {
				TicketActivity activity = new TicketActivity();
				activity.setTicketId(recordId);
				activity.setModifiedTime(modifiedTime);
				activity.setModifiedBy(modifiedBy);
				activity.setActivityType(activityType);
				activity.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				
				JSONObject info = new JSONObject();
				activity.setInfo(info);
				
				info.put(FacilioConstants.ContextNames.MODULE_NAME, module.getDisplayName());
				JSONArray updatedFields = new JSONArray();
				if(isUpdate) {
					Map<String, Object> oldTicketProps = FieldUtil.getAsProperties(oldTicketMap.get(recordId));
					if(oldTicketProps != null) {
						MapDifference<String, Object> difference = Maps.difference(oldTicketProps, newTicketProps);
						for(Map.Entry<String, Object> entry : difference.entriesOnlyOnLeft().entrySet()) {
							JSONObject fieldProp = new JSONObject();
							fieldProp.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, entry.getKey());
							fieldProp.put("oldValue", getFieldValue(lookupFields, entry.getKey(), entry.getValue(), moduleName));
							updatedFields.add(fieldProp);
						}
						for(Map.Entry<String, Object> entry : difference.entriesOnlyOnRight().entrySet()) {
							JSONObject fieldProp = new JSONObject();
							fieldProp.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, entry.getKey());
							fieldProp.put("newValue", getFieldValue(lookupFields, entry.getKey(), entry.getValue(), moduleName));
							updatedFields.add(fieldProp);
						}
						for(Map.Entry<String, ValueDifference<Object>> entry : difference.entriesDiffering().entrySet()) {
							JSONObject fieldProp = new JSONObject();
							fieldProp.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, entry.getKey());
							fieldProp.put("oldValue", getFieldValue(lookupFields, entry.getKey(), entry.getValue().leftValue(), moduleName));
							fieldProp.put("newValue", getFieldValue(lookupFields, entry.getKey(), entry.getValue().rightValue(), moduleName));
							updatedFields.add(fieldProp);
						}
						info.put("updatedFields", updatedFields);
						insertActivityBuilder.addRecord(FieldUtil.getAsProperties(activity));
					}
				}
				else {
					for(Map.Entry<String, Object> entry : newTicketProps.entrySet()) {
						if(!"orgId".equals(entry.getKey()) && !"id".equals(entry.getKey()) && !"moduleId".equals(entry.getKey())) {
							if(!fieldsMap.containsKey(entry.getKey()))
							{
								continue;
							}
							JSONObject fieldProp = new JSONObject();
							fieldProp.put("fieldName", fieldsMap.get(entry.getKey()).getDisplayName());
							fieldProp.put("newValue", getFieldValue(lookupFields, entry.getKey(), entry.getValue(), moduleName));
							updatedFields.add(fieldProp);
						}
					}
					info.put("updatedFields", updatedFields);
					insertActivityBuilder.addRecord(FieldUtil.getAsProperties(activity));
				}
			}
		}
		insertActivityBuilder.save();
	}
	
	private Object getFieldValue(Map<String, LookupField> lookupFields, String fieldName, Object value, String moduleName) throws Exception {
		LookupField lookupField = lookupFields.get(fieldName);
		if(lookupField != null) {
			long recordId = (long) ((Map<String, Object>) value).get("id");
			if(FacilioConstants.ContextNames.USERS.equals(lookupField.getSpecialType())) {
				JSONObject obj = new JSONObject();
				obj.put("module", FacilioConstants.ContextNames.USER);
				obj.put("id", recordId);
				return obj;
			}
			else if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
				return LookupSpecialTypeUtil.getPrimaryFieldValue(lookupField.getSpecialType(), recordId);
			}
			else {
				return getPrimaryFieldValue(lookupField.getLookupModule(), recordId);
			}
		}
		else {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField fields = modBean.getField(fieldName, moduleName);
				if (fields != null && fields.getDataTypeEnum() == FieldType.ENUM) {
					EnumField enums = (EnumField) fields;
					return enums.getEnumMap().get(value);
				}
			return value;
		}
	}
	
	private Map<String, LookupField> getLookupFields(String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		
		Map<String, LookupField> lookupFields = new HashMap<>();
		if(fields != null && !fields.isEmpty()) {
			for(FacilioField field : fields) {
				if(field.getDataTypeEnum() == FieldType.LOOKUP) {
					lookupFields.put(field.getName(), (LookupField) field);
				}
			}
		}
		return lookupFields;
	}
	
	private Object getPrimaryFieldValue(FacilioModule module, long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField defaultField = modBean.getPrimaryField(module.getName());
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																				.select(Collections.singletonList(defaultField))
																				.module(module)
																				.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		List<Map<String, Object>> props = selectBuilder.getAsProps();
		
		if(props != null && !props.isEmpty()) {
			return props.get(0).get(defaultField.getName());
		}
		return null;
	}
}
