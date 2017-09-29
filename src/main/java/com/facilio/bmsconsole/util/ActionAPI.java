package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class ActionAPI {
	public static List<ActionContext> getActionsFromWorkflowRule(long orgId, long workflowRuleId) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(FieldFactory.getActionFields())
					.table("Action")
					.innerJoin("Workflow_Rule_Action")
					.on("Action.ID = Workflow_Rule_Action.ACTION_ID")
					.andCustomWhere("Workflow_Rule_Action.WORKFLOW_RULE_ID = ?", workflowRuleId);
			return getActionsFromPropList(actionBuilder.get());
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private static List<ActionContext> getActionsFromPropList(List<Map<String, Object>> props) throws IllegalAccessException, InvocationTargetException {
		if(props != null && props.size() > 0) {
			List<ActionContext> actions = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				ActionContext action = new ActionContext();
				BeanUtils.populate(action, prop);
				if(action.getTemplateId() != -1) {
					action.setTemplate(null); //Template should be obtained from some api
				}
				actions.add(action);
			}
			return actions;
		}
		return null;
	}
}
