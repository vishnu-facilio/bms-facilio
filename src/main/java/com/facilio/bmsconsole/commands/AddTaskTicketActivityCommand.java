package com.facilio.bmsconsole.commands;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddTaskTicketActivityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		if((task != null && task.getParentTicketId() != -1) || (tasks != null && !tasks.isEmpty() && tasks.get(0).getParentTicketId() != -1)) {
			context.put(FacilioConstants.TicketActivity.MODIFIED_TIME, System.currentTimeMillis());
			context.put(FacilioConstants.TicketActivity.MODIFIED_USER, AccountUtil.getCurrentUser().getId());
			addActivity(context);
		}
		return false;
	}
	
	private void addActivity(Context context) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, RuntimeException {
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		if(tasks == null || tasks.isEmpty()) {
			TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			tasks = Collections.singletonList(task);
		}
		
		GenericInsertRecordBuilder insertActivityBuilder = new GenericInsertRecordBuilder()
																.table(ModuleFactory.getTicketActivityModule().getTableName())
																.fields(FieldFactory.getTicketActivityFields());
		for(TaskContext task : tasks) {
			TicketActivity activity = new TicketActivity();
			activity.setTicketId(task.getParentTicketId());
			activity.setModifiedTime((long) context.get(FacilioConstants.TicketActivity.MODIFIED_TIME));
			activity.setModifiedBy((long) context.get(FacilioConstants.TicketActivity.MODIFIED_USER));
			activity.setActivityType(ActivityType.ADD_TICKET_TASKS);
			activity.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			
			JSONObject info = new JSONObject();
			info.put("task", task.getSubject());
			activity.setInfo(info);
			
			insertActivityBuilder.addRecord(FieldUtil.getAsProperties(activity));
		}
		insertActivityBuilder.save();
	}
}
