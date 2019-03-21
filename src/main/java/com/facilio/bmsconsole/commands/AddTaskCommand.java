package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.activity.ActivityType;

public class AddTaskCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);

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
			JSONObject info = new JSONObject();
			context.put(FacilioConstants.ContextNames.RECORD_ID, taskId);
//			context.put(FacilioConstants.ContextNames.IDS_TO_UPDATE_TASK_COUNT, Collections.singletonList(task.getParentTicketId()));
			FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.IDS_TO_UPDATE_TASK_COUNT, Collections.singletonList(task.getParentTicketId()));
			FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			if (task != null) {
				info.put("Task", task.getSubject().toString());
				info.put("actype", "add");
				JSONObject newinfo = new JSONObject();
                newinfo.put("Task",info);
			CommonCommandUtil.addActivityToContext(task.getParentTicketId(), -1, WorkOrderActivityType.ADD, newinfo, (FacilioContext) context);
			}
		}
		else {
			throw new IllegalArgumentException("Task Object cannot be null");
		}
		
		return false;
	}
}
