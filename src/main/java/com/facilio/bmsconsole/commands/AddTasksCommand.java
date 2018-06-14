package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;


public class AddTasksCommand implements Command {
	
	private static final Logger LOGGER = Logger.getLogger(AddTasksCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(taskMap != null && !taskMap.isEmpty()) {
			Map<String, TaskSectionContext> sections = (Map<String, TaskSectionContext>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS);
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<TaskContext> builder = new InsertRecordBuilder<TaskContext>()
															.module(module)
															.fields(fields);
			taskMap.forEach((sectionName, tasks) -> {
				long sectionId = -1;
				if(!sectionName.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
					sectionId = sections.get(sectionName).getId();
				}
				for(TaskContext task : tasks) {
					task.setCreatedTime(System.currentTimeMillis());
					task.setSectionId(sectionId);
					if(workOrder != null) {
						task.setParentTicketId(workOrder.getId());
					}
					task.setSourceType(TicketContext.SourceType.TASK);
					
					builder.addRecord(task);
				}
			});
			builder.save();
			
			for (Entry<String, List<TaskContext>> entry: taskMap.entrySet()) {
				for (TaskContext task: entry.getValue()) {
					List<ReadingRuleContext> rules = task.getReadingRules();
					Chain c = FacilioChainFactory.getAddWorkflowRuleChain();
					for (int i = 0; i < rules.size(); ++i) {
						ReadingRuleContext rule = rules.get(i);
						rule.setRuleType(WorkflowRuleContext.RuleType.VALIDATION_RULE);
						context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
						context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, task.getActionsList().get(i));
						try {
							c.execute(context);
						} catch (Exception e) {
							String ex = ExceptionUtils.getStackTrace(e);
							LOGGER.severe(ex);
							throw e;
						}
					}
				}
			}
			context.put(FacilioConstants.ContextNames.TASK_LIST, builder.getRecords());
		}
		else {
//			throw new IllegalArgumentException("Task list cannot be null/ empty");
		}
		return false;
	}
}
