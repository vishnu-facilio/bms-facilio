package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;

public class TransactionChainFactory extends FacilioChainFactory {

		public static Chain historicalFormulaCalculationChain() {
			Chain c = getDefaultChain();
			c.addCommand(new HistoricalFormulaCalculationCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain runThroughReadingRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new RunThroughReadingRulesCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getAddWorkOrderChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddRequesterCommand());
			c.addCommand(SetTableNamesCommand.getForWorkOrder());
			c.addCommand(new ValidateWorkOrderFieldsCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddWorkOrderCommand());
			c.addCommand(new AddAttachmentCommand());
			c.addCommand(new AttachmentContextCommand());
			c.addCommand(new AddAttachmentRelationshipCommand());
			c.addCommand(new AddTicketActivityCommand());
			c.addCommand(getAddTasksChain());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
							.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE, RuleType.SCHEDULED_RULE))
					);
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getUpdateWorkOrderChain() {
			Chain c = getTransactionChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrder());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new UpdateWorkOrderCommand());
			c.addCommand(new SendNotificationCommand());
			c.addCommand(new ClearAlarmOnWOCloseCommand());
			c.addCommand(new AddTicketActivityCommand());
//			c.addCommand(getAddOrUpdateReadingValuesChain());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE, RuleType.SCHEDULED_RULE))
					);
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
	
	    protected static Chain getDefaultChain()
	    {
	    	    return new FacilioChain(true);
	    }
}
