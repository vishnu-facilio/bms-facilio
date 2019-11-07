package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.cards.util.CardLayout;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class GetCardWorkflowCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WidgetCardContext cardContext = (WidgetCardContext) context.get(FacilioConstants.ContextNames.CARD_CONTEXT);
		Long cardId = (Long) context.get(FacilioConstants.ContextNames.CARD_ID);
		
		if (cardContext != null) {
			List<Object> paramsList = new ArrayList<Object>();
			paramsList.add(cardContext.getCardParams());
			
			WorkflowContext workflow = getWorkflowContext(cardContext);
			
			context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
			context.put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);
		}
		else if (cardId != null) {
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getWidgetCardModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(), cardId + "", NumberOperators.EQUALS));
			
			List<Map<String,Object>> props = builder.get();
			if (props != null && !props.isEmpty()) {
				WidgetCardContext widgetCardContext = FieldUtil.getAsBeanFromMap(props.get(0), WidgetCardContext.class);
				
				List<Object> paramsList = new ArrayList<Object>();
				paramsList.add(widgetCardContext.getCardParams());
				
				WorkflowContext workflow = getWorkflowContext(widgetCardContext);
				
				context.put(FacilioConstants.ContextNames.CARD_CONTEXT, widgetCardContext);
				context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
				context.put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);
			}
			else {
				
			}
		}
		return false;
	}
	
	
	private WorkflowContext getWorkflowContext(WidgetCardContext cardContext) throws Exception {
		
		CardLayout cl = CardLayout.getCardLayout(cardContext.getCardLayout());
		
		WorkflowContext workflow = new WorkflowContext();
		workflow.setIsV2Script(true);
		
		if (cardContext.getScriptMode() == WidgetCardContext.ScriptMode.CUSTOM_SCRIPT) {
			if (cardContext.getCustomScript() != null) {
				workflow.setWorkflowV2String(cardContext.getCustomScript());
			}
			else if (cardContext.getCustomScriptId() != null) {
				workflow = WorkflowUtil.getWorkflowContext(cardContext.getCustomScriptId());
			}
		}
		else {
			workflow.setWorkflowV2String(cl.getScript());
		}
		
		return workflow;
	}
}
