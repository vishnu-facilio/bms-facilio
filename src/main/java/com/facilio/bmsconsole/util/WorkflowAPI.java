package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class WorkflowAPI {
	
	public static long addWorkflowRule(WorkflowRuleContext rule) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			Map<String, Object> ruleProps = FieldUtil.getAsProperties(rule);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.connection(conn)
														.table("Workflow_Rule")
														.fields(FieldFactory.getWorkflowRuleFields())
														.addRecord(ruleProps);
			insertBuilder.save();
			return (long) ruleProps.get("id");
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static int updateWorkflowRule(long orgId, WorkflowRuleContext rule, long id) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			Map<String, Object> ruleProps = FieldUtil.getAsProperties(rule);
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.connection(conn)
														.table("Workflow_Rule")
														.fields(FieldFactory.getWorkflowRuleFields())
														.andCustomWhere("ORGID = ? AND ID = ?", orgId, id);
			return updateBuilder.update(ruleProps);
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(long orgId) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
					.connection(conn)
					.table("Workflow_Rule")
					.select(FieldFactory.getWorkflowRuleFields())
					.andCustomWhere("Workflow_Rule.ORGID = ?", orgId);
			return getWorkFlowsFromMapList(ruleBuilder.get(), orgId, conn);
		}
		catch(SQLException e) {
			throw e;
		}
	}
	
	public static List<WorkflowRuleContext> getActiveWorkflowRulesFromEvent(long orgId, long moduleId, int eventType) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
					.connection(conn)
					.table("Workflow_Rule")
					.select(FieldFactory.getWorkflowRuleFields())
					.innerJoin("Event")
					.on("Workflow_Rule.EVENT_ID = Event.ID")
					.andCustomWhere("Workflow_Rule.ORGID = ? AND Event.MODULEID = ? AND ? & Event.EVENT_TYPE = ? AND Workflow_Rule.STATUS = true", orgId, moduleId, eventType, eventType)
					.orderBy("EXECUTION_ORDER");
			return getWorkFlowsFromMapList(ruleBuilder.get(), orgId, conn);
		}
		catch(SQLException e) {
			throw e;
		}
	}
	
	private static List<WorkflowRuleContext> getWorkFlowsFromMapList(List<Map<String, Object>> props, long orgId, Connection conn) throws Exception {
		if(props != null && props.size() > 0) {
			List<WorkflowRuleContext> workflows = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				WorkflowRuleContext workflow = new WorkflowRuleContext();
				BeanUtils.populate(workflow, prop);
				long criteriaId = workflow.getCriteriaId();
				workflow.setCriteria(CriteriaAPI.getCriteria(orgId, criteriaId, conn));
				
				/*if(workflow.getCriteria() == null) {
					throw new RuntimeException("Criteria cannot be null for WorkflowRule : "+workflow.getId());
				}*/
				
				workflows.add(workflow);
			}
			return workflows;
		}
		return null;
	}
}
