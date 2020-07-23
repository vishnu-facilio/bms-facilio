package com.facilio.bmsconsole.commands;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class PreventiveMaintenanceReadingsCommand extends FacilioCommand {


	private static final Logger LOGGER = Logger.getLogger(PreventiveMaintenanceReadingsCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		long resourceId = (Long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
		long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule("workorder");
		FacilioModule taskModule = modBean.getModule("task");
		
		List<FacilioField> workorderFields = modBean.getAllFields("workorder"); 
		List<FacilioField> taskFields = modBean.getAllFields("task");
		
		Map<String, FacilioField> taskFieldMap = FieldFactory.getAsMap(taskFields);
		Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);


		SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<>();
				selectBuilder.module(workorderModule)
				.select(workorderFields)
				.beanClass(WorkOrderContext.class);
				if (startTime != -1) {
					selectBuilder.andCondition(CriteriaAPI.getCondition(workorderFieldMap.get("createdTime"),startTime + "," + endTime, DateOperators.BETWEEN));
				} else {
					selectBuilder.limit(100);
				}
				selectBuilder
				.andCondition(CriteriaAPI.getCondition(workorderFieldMap.get("resource"), resourceId + "" , NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(workorderFieldMap.get("pm"), pmId + "" , NumberOperators.EQUALS))
				;

		Map<Long, List<TaskContext>> taskMap = new HashMap<>();

		List<WorkOrderContext> workOrderContextList = selectBuilder.get();
		if (workOrderContextList.isEmpty()) {
			return false;
		}

		List<Long> parentTicketIds = workOrderContextList.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());

		// List<Long> inputTypes = Arrays.asList(new Long(TaskContext.InputType.READING.getVal()), new Long (TaskContext.InputType.NUMBER.getVal()));

		SelectRecordsBuilder<TaskContext> taskSelectBuilder = new SelectRecordsBuilder<>();
		taskSelectBuilder.module(taskModule)
				.select(taskFields)
				.beanClass(TaskContext.class)
				.andCondition(CriteriaAPI.getCondition(taskFieldMap.get("parentTicketId"), parentTicketIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(taskFieldMap.get("inputType"), CommonOperators.IS_NOT_EMPTY));

		List<TaskContext> taskContextList = taskSelectBuilder.get();

		for (TaskContext taskContext: taskContextList) {
			if (!taskMap.containsKey(taskContext.getParentTicketId())) {
				taskMap.put(taskContext.getParentTicketId(), new ArrayList<>());
			}
			taskMap.get(taskContext.getParentTicketId()).add(taskContext);
		}

		Iterator<WorkOrderContext> workOrderItr = workOrderContextList.iterator();

		while (workOrderItr.hasNext()) {
			WorkOrderContext workOrder = workOrderItr.next();
			List<TaskContext> woTaskContexts = taskMap.get(workOrder.getId());

			if (CollectionUtils.isEmpty(woTaskContexts)) {
				workOrderItr.remove();
				continue;
			}

			Map<Long, List<TaskContext>> woTasksMap = new HashMap<>();
			workOrder.setTasks(woTasksMap);

			for (TaskContext taskContext: woTaskContexts) {
				long sectionId = taskContext.getSectionId();

				if (woTasksMap.get(sectionId) == null) {
					woTasksMap.put(sectionId, new ArrayList<>());
				}

				woTasksMap.get(sectionId).add(taskContext);
			}
		}

		context.put(FacilioConstants.ContextNames.RESULT, workOrderContextList);
		return false;
	}
	
}