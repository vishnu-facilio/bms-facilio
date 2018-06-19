package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.EnumField;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.workflows.util.WorkflowUtil;

public class GetTaskInputDataCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(GetTaskInputDataCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		if(tasks == null) {
			TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			if(task != null) {
				tasks = Collections.singletonList(task);
			}
		}
		if(tasks != null && !tasks.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(TaskContext task : tasks) {
				switch(task.getInputTypeEnum()) {
					case NUMBER:
						Criteria criteria = new Criteria();
						criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", Long.toString(task.getReadingFieldId()), NumberOperators.EQUALS));
						criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.VALIDATION_RULE.getIntVal()), NumberOperators.EQUALS));
						List<ReadingRuleContext> readingRules = WorkflowRuleAPI.getReadingRules(criteria);
						if (readingRules != null && !readingRules.isEmpty()) {
							for (ReadingRuleContext r:  readingRules) {
								long workflowId = r.getWorkflowId();
								if (workflowId != -1) {
									r.setWorkflow(WorkflowUtil.getWorkflowContext(workflowId, true));
								}
							}
						}
						task.setReadingRules(readingRules);
					case TEXT:
					case READING:
					case BOOLEAN:
						task.setReadingField(modBean.getField(task.getReadingFieldId()));
						break;
					case RADIO:
						if (task.getReadingFieldId() != -1) {
							task.setReadingField(modBean.getField(task.getReadingFieldId()));
							task.setOptions(((EnumField)task.getReadingField()).getValues());
						}
						else {
							task.setOptions(TicketAPI.getTaskInputOptions(task.getId()));
						}
						break;
//					case CHECKBOX:
//						task.setOptions(TicketAPI.getTaskInputOptions(task.getId()));
//						if(task.getInputValue() != null && !task.getInputValue().isEmpty()) {
//							task.setInputValues(Arrays.asList(task.getInputValue().split("\\s*,\\s*")));
//						}
//						else {
//							task.setInputValues(Collections.emptyList());
//						}
//						break;
					default:
						break;
				}
			}
		}
		return false;
	}
}
