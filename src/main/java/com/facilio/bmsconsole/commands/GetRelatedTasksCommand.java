package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetRelatedTasksCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(recordId == null) {
			return false;
		}
		
		//Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		Map<Long, TaskSectionContext> sections = TicketAPI.getRelatedTaskSections(recordId);
		Map<Long, TaskSectionContext> taskSections =sections.entrySet().stream().filter(en->!en.getValue().isPreRequest()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
		
		context.put(FacilioConstants.ContextNames.TASK_SECTIONS, taskSections);
		Map<Long, TaskSectionContext> preRequestSections =sections.entrySet().stream().filter(en->en.getValue().isPreRequest()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
		context.put(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS, preRequestSections);
		
		
		List<TaskContext> alltasks = TicketAPI.getRelatedTasks(recordId);
		List<TaskContext> tasks=alltasks.stream().filter(t-> !t.isPreRequest()).collect(Collectors.toList());
		context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
		List<TaskContext> preRequests=alltasks.stream().filter(t-> t.isPreRequest()).collect(Collectors.toList());
		context.put(FacilioConstants.ContextNames.PRE_REQUEST_LIST, preRequests);
		context.put(FacilioConstants.ContextNames.TASK_MAP, TicketAPI.groupTaskBySection(tasks));
		context.put(FacilioConstants.ContextNames.PRE_REQUEST_MAP, TicketAPI.groupPreRequestBySection(preRequests));
		CommonCommandUtil.loadTaskLookups(tasks);
		
		return false;
	}

}
