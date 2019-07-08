package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.NotificationPreference;
import com.facilio.bmsconsole.context.NotificationPreferenceFactory;
import com.facilio.bmsconsole.util.SingleRecordRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;

public class AddNotificationWorkflowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		Map<String, Object> map = (Map<String, Object>) context.get("value");
		String moduleName = (String) context.get("moduleName");
		long recordId = (long) context.get("recordId");
		String name = (String) context.get("name");
		
		NotificationPreference notificationPreference = NotificationPreferenceFactory.getNotificationPreference(moduleName, name);
		if (notificationPreference != null) {
			WorkflowRuleContext workflowRuleContext = notificationPreference.substitute(map, null);
			workflowRuleContext.setParentId(recordId);
			SingleRecordRuleAPI.addWorkflowRule(workflowRuleContext);
		}
		
		return false;
	}

}
