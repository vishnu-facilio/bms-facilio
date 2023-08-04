package com.facilio.bmsconsole.monitoring;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkflowRuleActionLogContext;
import com.facilio.bmsconsole.context.WorkflowRuleLogContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MonitoringAPI {
    public static final Logger LOGGER = LogManager.getLogger(MonitoringAPI.class.getName());

    public static long getPreOpenWoCount(Long currentTime, long orgId) {
        long preOpenWoCount = 0;

        try {
            AccountUtil.setCurrentAccount(orgId);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
            List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

            if (workOrderModule != null) {
                SelectRecordsBuilder<V3WorkOrderContext> builder = new SelectRecordsBuilder<V3WorkOrderContext>()
                        .select(new HashSet<>())
                        .module(workOrderModule)
                        .beanClass(V3WorkOrderContext.class)
                        .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(workOrderModule))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(DateTimeUtil.getLastNHour(currentTime, 1)), NumberOperators.GREATER_THAN_EQUAL))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(currentTime), NumberOperators.LESS_THAN_EQUAL))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), CommonOperators.IS_EMPTY))
                        .skipModuleCriteria();

                List<Map<String, Object>> props = builder.getAsProps();
                Long count = CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
                preOpenWoCount += count;
            } else {
                throw new NullPointerException();
            }
            return preOpenWoCount;
        } catch (Exception e) {
            LOGGER.error("Error occurred during fetching count of pre-open workorders, " + e);
            return -1;
        }
    }

    public static long getPreOpenInspectionCount(Long currentTime, long orgId) {
        long preOpenInspectionCount = 0;

        try {
            AccountUtil.setCurrentAccount(orgId);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule moduleInspection = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);
            if (moduleInspection != null) {
                SelectRecordsBuilder<InspectionResponseContext> builder = new SelectRecordsBuilder<InspectionResponseContext>()
                        .select(new HashSet<>())
                        .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(moduleInspection))
                        .module(moduleInspection)
                        .beanClass(InspectionResponseContext.class)
                        .andCondition(CriteriaAPI.getCondition("Inspection_Responses.CREATED_TIME", AgentKeys.CREATED_TIME, String.valueOf(DateTimeUtil.getLastNHour(currentTime, 1)), NumberOperators.GREATER_THAN_EQUAL))
                        .andCondition(CriteriaAPI.getCondition("Inspection_Responses.STATUS", AgentKeys.COMMAND_STATUS, 1 + "", NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition("Inspection_Responses.CREATED_TIME", AgentKeys.CREATED_TIME, String.valueOf(currentTime), NumberOperators.LESS_THAN_EQUAL))
                        .skipModuleCriteria();
                List<Map<String, Object>> props = builder.getAsProps();
                Long count = CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
                preOpenInspectionCount += count;
            } else {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred during fetching count of pre-open Inspections, " + e);
            return -1;
        }
        return preOpenInspectionCount;
    }

    public static long getFailedWorkflowCount(Long currentTime, long orgId) {
        long failedWorkflowCount = 0;
        try {
            AccountUtil.setCurrentAccount(orgId);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule WorkflowRuleLogsModule = modBean.getModule(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGS);
            FacilioModule workFlowRuleActionModule = modBean.getModule(FacilioConstants.ContextNames.WORKFLOW_RULE_ACTION_LOGS);
            List<FacilioField> workFlowRuleLogsFields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGS);
            Map<String, FacilioField> workFlowRuleLogsFieldMap = FieldFactory.getAsMap(workFlowRuleLogsFields);
            List<FacilioField> workFlowRuleActionLogsFields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.WORKFLOW_RULE_ACTION_LOGS);
            Map<String, FacilioField> workFlowRuleActionLogsFieldMap = FieldFactory.getAsMap(workFlowRuleActionLogsFields);

            if (WorkflowRuleLogsModule != null && workFlowRuleActionModule != null) {
                SelectRecordsBuilder<WorkflowRuleLogContext> builder = new SelectRecordsBuilder<WorkflowRuleLogContext>()
                        .select(new HashSet<>())
                        .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(WorkflowRuleLogsModule))
                        .module(WorkflowRuleLogsModule)
                        .beanClass(WorkflowRuleLogContext.class)
                        .innerJoin(workFlowRuleActionModule.getTableName()).on("Workflow_Rule_Logs.ID = Workflow_Rule_Action_Logs.WORKFLOW_RULE_LOG_ID")
                        .andCondition(CriteriaAPI.getCondition(workFlowRuleLogsFieldMap.get("ruleStatus"), String.valueOf(WorkflowRuleLogContext.RuleStatus.WARNING.getIndex()), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition(workFlowRuleActionLogsFieldMap.get("actionStatus"), String.valueOf(WorkflowRuleActionLogContext.ActionStatus.FAILED.getIndex()), NumberOperators.EQUALS))
                        .skipModuleCriteria();
                List<Map<String, Object>> props = builder.getAsProps();
                Long count = CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
                failedWorkflowCount += count;
            } else {
                throw new NullPointerException();
            }
            return failedWorkflowCount;

        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching Failed workflow count, " + e);
            return -1;
        }

    }

    public static String getKeyName(long orgId) {
        try {
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(IAMAccountConstants.getOrgFields())
                    .table(IAMAccountConstants.getOrgModule().getTableName());

            selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
            selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));

            List<Map<String, Object>> props = selectBuilder.get();
            if (props.isEmpty()) {
                return "null";
            }
            Map<String, Object> props1 = props.get(0);
            String domainName = (String) props1.get("domain");
            return domainName;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching keyName, " + e);
            return "null";
        }
    }
}
