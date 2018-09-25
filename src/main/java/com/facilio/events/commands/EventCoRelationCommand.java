package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventRule;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class EventCoRelationCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRule rule = (EventRule) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(rule != null) {
			EventContext event = (EventContext) context.get(EventConstants.EventContextNames.EVENT);
			if(event.getEventStateEnum() != EventState.IGNORED) {
				long workflowId = rule.getCoRelWorkflowId();
				if (workflowId != -1) {
					WorkflowContext workflow = WorkflowUtil.getWorkflowContext(workflowId);
					if(workflow.isBooleanReturnWorkflow()) {
						Object result = WorkflowUtil.getWorkflowExpressionResult(workflow.getWorkflowString(), FieldUtil.getAsProperties(event));
						boolean resultBoolean;
						 if(result instanceof Boolean) {
							 resultBoolean = (Boolean) result;
						 }
						 else {
							 double resultDouble = (double) result;
							 resultBoolean = resultDouble == 1;
						 }
						if(resultBoolean) {
							switch(rule.getColRelActionEnum()) {
								case IGNORE:
									event.setEventState(EventState.IGNORED);
									break;
								case TRANSFORM:
									long coRelTransformTemplateId = rule.getCoRelTransformTemplateId();
									JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(coRelTransformTemplateId);
//									event = EventAPI.transformEvent(event, template);
									break;
							}
						}
					}
				}
				event.setInternalState(EventInternalState.CO_RELATION_DONE);
				context.put(EventConstants.EventContextNames.EVENT, event);
			}
		}
		
		return false;
	}

}
