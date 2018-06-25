package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;

public class AddTaskTicketActivityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ActivityType activityType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		List<TaskContext> oldTickets = (List<TaskContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		if((task != null && (task.getParentTicketId() != -1 || oldTickets != null)) || (tasks != null && !tasks.isEmpty() && tasks.get(0).getParentTicketId() != -1)) {
			context.put(FacilioConstants.TicketActivity.MODIFIED_TIME, System.currentTimeMillis());
			context.put(FacilioConstants.TicketActivity.MODIFIED_USER, AccountUtil.getCurrentUser().getId());
			addActivity(context);
		}
		return false;
	}
	
	private void addActivity(Context context) throws Exception {
		List<TaskContext> oldTickets = (List<TaskContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		boolean isUpdate = false;
		Map<Long, TaskContext> oldTicketMap = new HashMap<>();
		if(oldTickets != null && !oldTickets.isEmpty()) {
			isUpdate = true;
			for(TaskContext oldTicket : oldTickets) {
				oldTicketMap.put(oldTicket.getId(), oldTicket);
			}
		}
		
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		Map<Long, TaskContext> newTicketMap = new HashMap<>();
		if(tasks == null || tasks.isEmpty()) {
			TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			tasks = Collections.singletonList(task);
		}
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Map<String, LookupField> lookupFields = getLookupFields(moduleName);
		GenericInsertRecordBuilder insertActivityBuilder = new GenericInsertRecordBuilder()
																.table(ModuleFactory.getTicketActivityModule().getTableName())
																.fields(FieldFactory.getTicketActivityFields());
		
		if (isUpdate) {
			TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			Map<String, Object> newTicketProps = FieldUtil.getAsProperties(task);
			newTicketProps.remove("id");
			List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			boolean bulkAction = false;
			if(context.get(FacilioConstants.ContextNames.IS_BULK_ACTION) != null) {
				bulkAction = (boolean) context.get(FacilioConstants.ContextNames.IS_BULK_ACTION);
			}
			if(bulkAction == true) {
				TicketActivity activity = new TicketActivity();
				long parentTicketId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
				activity.setTicketId(parentTicketId);
				activity.setModifiedTime((long) context.get(FacilioConstants.TicketActivity.MODIFIED_TIME));
				activity.setModifiedBy((long) context.get(FacilioConstants.TicketActivity.MODIFIED_USER));
				activity.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				activity.setActivityType(ActivityType.CLOSE_ALL_TASK);
				JSONObject info = new JSONObject();
				JSONArray updatedFields = new JSONArray();
				JSONObject taskJson = new JSONObject();
				info.put("taskIds", recordIds);
				info.put("task", taskJson);
				info.put("updatedFields", updatedFields);
				activity.setInfo(info);
				insertActivityBuilder.addRecord(FieldUtil.getAsProperties(activity));
			}
			else if (recordIds != null && !recordIds.isEmpty()) {
				for (Long recordId : recordIds) {
					TaskContext oldTask = oldTicketMap.get(recordId);
					TicketActivity activity = new TicketActivity();
					activity.setTicketId(oldTask.getParentTicketId());
					activity.setModifiedTime((long) context.get(FacilioConstants.TicketActivity.MODIFIED_TIME));
					activity.setModifiedBy((long) context.get(FacilioConstants.TicketActivity.MODIFIED_USER));
					activity.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
					activity.setActivityType(ActivityType.UPDATE_TICKET_TASK);
					
					Map<String, Object> oldTicketProps;
					if (task.getId() == -1)
					{
						oldTicketProps = FieldUtil.getAsProperties(oldTicketMap.get(recordId));
					}
					else
					{
						oldTicketProps = FieldUtil.getAsProperties(oldTicketMap.get(task.getId()));
					}
					
					Iterator<String> keys = oldTicketProps.keySet().iterator();
					while (keys.hasNext()) {
						String key = keys.next();
						if (!newTicketProps.containsKey(key)) {
							keys.remove();
						}
					}
					JSONArray updatedFields = new JSONArray();
					if(oldTicketProps != null) {
						MapDifference<String, Object> difference = Maps.difference(oldTicketProps, newTicketProps);
//						for(Map.Entry<String, Object> entry : difference.entriesOnlyOnLeft().entrySet()) {
//							JSONObject fieldProp = new JSONObject();
//							fieldProp.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, entry.getKey());
//							fieldProp.put("oldValue", getFieldValue(lookupFields, entry.getKey(), entry.getValue()));
//							updatedFields.add(fieldProp);
//						}
						for(Map.Entry<String, Object> entry : difference.entriesOnlyOnRight().entrySet()) {
							JSONObject fieldProp = new JSONObject();
							fieldProp.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, entry.getKey());
							fieldProp.put("newValue", getFieldValue(lookupFields, entry.getKey(), entry.getValue()));
							updatedFields.add(fieldProp);
						}
						for(Map.Entry<String, ValueDifference<Object>> entry : difference.entriesDiffering().entrySet()) {
							JSONObject fieldProp = new JSONObject();
							fieldProp.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, entry.getKey());
							fieldProp.put("oldValue", getFieldValue(lookupFields, entry.getKey(), entry.getValue().leftValue()));
							fieldProp.put("newValue", getFieldValue(lookupFields, entry.getKey(), entry.getValue().rightValue()));
							updatedFields.add(fieldProp);
						}
					}
					JSONObject info = new JSONObject();
					JSONObject taskJson = new JSONObject();
					taskJson.put("subject", oldTask.getSubject());
					taskJson.put("id", oldTask.getId());
					info.put("updatedFields", updatedFields);
					info.put("task", taskJson);
					activity.setInfo(info);
					insertActivityBuilder.addRecord(FieldUtil.getAsProperties(activity));
				}
			}
		}
		else {
			for(TaskContext task : tasks) {
				TicketActivity activity = new TicketActivity();
				activity.setTicketId(task.getParentTicketId());
				activity.setModifiedTime((long) context.get(FacilioConstants.TicketActivity.MODIFIED_TIME));
				activity.setModifiedBy((long) context.get(FacilioConstants.TicketActivity.MODIFIED_USER));
				activity.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				
				JSONObject info = new JSONObject();
				activity.setActivityType(ActivityType.ADD_TICKET_TASKS);
				info.put("task", task.getSubject());
				activity.setInfo(info);
				insertActivityBuilder.addRecord(FieldUtil.getAsProperties(activity));
			}
		}
		insertActivityBuilder.save();
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
	private Object getFieldValue(Map<String, LookupField> lookupFields, String fieldName, Object value) throws Exception {
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
			return value;
		}
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
