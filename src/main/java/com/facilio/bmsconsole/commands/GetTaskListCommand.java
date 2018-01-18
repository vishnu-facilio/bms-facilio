package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

public class GetTaskListCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
				.table(dataTableName)
				.moduleName(moduleName)
				.beanClass(TaskContext.class)
				.select(fields)
				.orderBy("ID");
		
		if(view != null) {
			Criteria criteria = view.getCriteria();
			builder.andCriteria(criteria);
		}

		List<TaskContext> tasks = builder.get();
		Map<Long, List<TaskContext>> taskMap = TicketAPI.groupTaskBySection(tasks);
		context.put(FacilioConstants.ContextNames.TASK_MAP, taskMap);
		Map<Long, TaskSectionContext> sections = TicketAPI.getTaskSections(taskMap.keySet().stream().collect(Collectors.toList()));
		context.put(FacilioConstants.ContextNames.TASK_SECTIONS, sections);
		
		return false;
	}

}
