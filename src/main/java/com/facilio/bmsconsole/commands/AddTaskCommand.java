package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.modules.FacilioField;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class AddTaskCommand implements Command, PostTransactionCommand {
	
	private List<Long> idsToUpdateTaskCount;
	private String moduleName;

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
//		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);

		if(task != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			task.setCreatedTime(System.currentTimeMillis());
			task.setCreatedBy(AccountUtil.getCurrentUser());
			task.setStatusNew(TaskStatus.OPEN);
			
			InsertRecordBuilder<TaskContext> builder = new InsertRecordBuilder<TaskContext>()
															.moduleName(moduleName)
															.table(dataTableName)
															.withLocalId()
															.fields(fields);
			
			long taskId = builder.insert(task);
			task.setId(taskId);
//			JSONObject info = new JSONObject();
			context.put(FacilioConstants.ContextNames.RECORD_ID, taskId);
//			context.put(FacilioConstants.ContextNames.IDS_TO_UPDATE_TASK_COUNT, Collections.singletonList(task.getParentTicketId()));
			
			idsToUpdateTaskCount = Collections.singletonList(task.getParentTicketId());
			this.moduleName = moduleName;
			
//			FacilioChain.addPostTransactionListObject(FacilioConstants.ContextNames.IDS_TO_UPDATE_TASK_COUNT, task.getParentTicketId());
//			FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
//			if (task != null) {
//				info.put("Task", task.getSubject().toString());
//			CommonCommandUtil.addActivityToContext(task.getParentTicketId(), -1, WorkOrderActivityType.ADD_TASK, info, (FacilioContext) context);
//			}
			List<TaskContext> tasks = Collections.singletonList(task);

            List<Object> tasklist = new ArrayList<Object>();
			if (!tasks.isEmpty()) {
				for(TaskContext singletask:tasks) {
					JSONObject singleinfo = new JSONObject();
					singleinfo.put("Task", singletask.getSubject().toString());
				    tasklist.add(singleinfo);
				}
				JSONObject newinfo = new JSONObject();
                newinfo.put("Task",tasklist);
			CommonCommandUtil.addActivityToContext(tasks.get(0).getParentTicketId(), -1, WorkOrderActivityType.ADD_TASK, newinfo, (FacilioContext) context);
			}
		}
		else {
			throw new IllegalArgumentException("Task Object cannot be null");
		}
		
		return false;
	}
	
	@Override
	public boolean postExecute() throws Exception {
		TicketAPI.updateTaskCount(idsToUpdateTaskCount);
		return false;
	}
}
