package com.facilio.bmsconsole.commands;

import java.util.Collection;
import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.cards.util.CardLayout;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class GetCardDataCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		CardLayout cardLayout = (CardLayout) context.get(FacilioConstants.ContextNames.CARD_LAYOUT);
		WidgetCardContext cardContext = (WidgetCardContext) context.get(FacilioConstants.ContextNames.CARD_CONTEXT);
		WorkflowContext workflow = (WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
		List<Object> paramsList = (List<Object>) context.get(WorkflowV2Util.WORKFLOW_PARAMS);
				
		if (workflow != null) {
			FacilioChain workflowChain = TransactionChainFactory.getExecuteWorkflowChain();
			workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
			workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);
			
			workflowChain.execute();
			
			context.put(FacilioConstants.ContextNames.CARD_RETURN_VALUE, workflow.getReturnValue());
		}
		else {
			context.put(FacilioConstants.ContextNames.CARD_RETURN_VALUE, executeInternalAPI(cardLayout, cardContext.getCardParams()));
		}
		
		return false;
	}
	
	private JSONObject executeInternalAPI(CardLayout cardLayout, JSONObject cardParams) {
		
		JSONObject returnValue = new JSONObject();
		
		switch (cardLayout) {
		case PMREADINGS_LAYOUT_1: 
			{
				String title = (String) cardParams.get("title");
				Long pmId = (Long) cardParams.get("pmId");
				Long resourceId = (Long) cardParams.get("resourceId");
				String dateRange = (String) cardParams.get("dateRange");
				
				Operator dateOperator = DateOperators.getAllOperators().get(dateRange);
				
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID, pmId);
				context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
				context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
	
				FacilioChain pmReadingsChain = FacilioChainFactory.getPreventiveMaintenanceReadingsChain();
				try {
					pmReadingsChain.execute(context);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				Collection<WorkOrderContext> workOrderContexts = (Collection<WorkOrderContext>) context.get(ContextNames.RESULT);
	
				returnValue.put("title", title);
				returnValue.put("value", workOrderContexts);
				break;
			}
		case KPICARD_LAYOUT_1:
			{
				String title = (String) cardParams.get("title");
				String kpiType = (String) cardParams.get("kpiType");
				String dateRange = (String) cardParams.get("dateRange");
				String dateField = (String) cardParams.get("dateField");
				JSONObject kpiConfig = (JSONObject) cardParams.get("kpi");
				Long kpiId = (Long) kpiConfig.get("kpiId");
				Long parentId = (Long) kpiConfig.get("parentId");
				String yAggr = (String) kpiConfig.get("yAggr");
				
				Object cardValue = null;
				
				if ("module".equalsIgnoreCase(kpiType)) {
					try {
						KPIContext kpiContext = KPIUtil.getKPI(kpiId, false);
						if (dateRange != null) {
							kpiContext.setDateOperator((DateOperators) DateOperators.getAllOperators().get(dateRange));
						}
						cardValue = KPIUtil.getKPIValue(kpiContext);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if ("reading".equalsIgnoreCase(kpiType)) {
					try {
						cardValue = FormulaFieldAPI.getFormulaCurrentValue(kpiId, parentId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				returnValue.put("title", title);
				returnValue.put("value", cardValue);
				break;
			}
		default:
			break;
		}
		
		return returnValue;
	}
}
