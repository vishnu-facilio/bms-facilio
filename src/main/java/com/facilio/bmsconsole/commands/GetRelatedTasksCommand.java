package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;

public class GetRelatedTasksCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(moduleName == null || recordId == null) {
			return false;
		}
		
		//Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		Map<Long, TaskSectionContext> sections = TicketAPI.getRelatedTaskSections(recordId);
		context.put(FacilioConstants.ContextNames.TASK_SECTIONS, sections);
		List<TaskContext> tasks = TicketAPI.getRelatedTasks(recordId);
		context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
		context.put(FacilioConstants.ContextNames.TASK_MAP, TicketAPI.groupTaskBySection(tasks));
		return false;
	}

}
