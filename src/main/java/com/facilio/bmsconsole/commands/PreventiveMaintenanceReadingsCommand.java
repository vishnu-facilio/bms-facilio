package com.facilio.bmsconsole.commands;
import java.util.*;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class PreventiveMaintenanceReadingsCommand extends FacilioCommand {

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
		
		List<FacilioField> selectFields = new ArrayList<FacilioField>(workorderFields);
		selectFields.addAll(taskFields);
		
		Map<String, FacilioField> taskFieldMap = FieldFactory.getAsMap(taskFields);
		Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);
		
		SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>();
				selectBuilder.module(workorderModule)
				.select(selectFields).beanClass(WorkOrderContext.class)
				.innerJoin(taskModule.getTableName())
				.on(workorderModule.getTableName() + ".ID = " + taskModule.getTableName() + ".PARENT_TICKET_ID");
				if (startTime != -1) {
					selectBuilder.andCondition(CriteriaAPI.getCondition(workorderFieldMap.get("createdTime"),startTime + "," + endTime, DateOperators.BETWEEN));
				} else {
					selectBuilder.limit(100);
				}
				selectBuilder
				.andCondition(CriteriaAPI.getCondition(workorderFieldMap.get("resource"), resourceId + "" , NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(workorderFieldMap.get("pm"), pmId + "" , NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(taskFieldMap.get("inputType"), TaskContext.InputType.READING.getVal() + "", NumberOperators.EQUALS));


		Map<Long, WorkOrderContext> woMap = new HashMap<>();
		Map<Long, List<TaskContext>> taskMap = new HashMap<>();

		List<Map<String, Object>> props = selectBuilder.getAsProps();

		for (Map<String, Object> prop: props) {
			WorkOrderContext workOrderContext = FieldUtil.getAsBeanFromMap(prop, WorkOrderContext.class);
			TaskContext task = FieldUtil.getAsBeanFromMap(prop, TaskContext.class);

			if (woMap.get(workOrderContext.getId()) == null) {
				woMap.put(workOrderContext.getId(), workOrderContext);
			}

			if (taskMap.get(task.getParentTicketId()) == null) {
				taskMap.put(task.getParentTicketId(), new ArrayList<>());
			}
			taskMap.get(task.getParentTicketId()).add(task);
		}

		Collection<WorkOrderContext> workOrderContexts = woMap.values();

		Iterator<WorkOrderContext> iterator = workOrderContexts.iterator();

		while (iterator.hasNext()) {
			WorkOrderContext workOrder = iterator.next();
			List<TaskContext> taskContexts = taskMap.get(workOrder.getId());

			Map<Long, List<TaskContext>> woTasksMap = new HashMap<>();
			workOrder.setTasks(woTasksMap);
			for (TaskContext taskContext: taskContexts) {
				long sectionId = taskContext.getSectionId();

				if (woTasksMap.get(sectionId) == null) {
					woTasksMap.put(sectionId, new ArrayList<>());
				}

				woTasksMap.get(sectionId).add(taskContext);
			}
		}

		context.put(FacilioConstants.ContextNames.RESULT, workOrderContexts);
		return false;
	}
	
}