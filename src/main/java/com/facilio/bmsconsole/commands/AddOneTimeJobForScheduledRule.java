package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkflowRuleRecordRelationshipContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.wmsv2.constants.Topics.AddOneTimeJobForScheduledRule.addOneTimeJobForScheduledRule;

public class AddOneTimeJobForScheduledRule extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(AddOneTimeJobForScheduledRule.class.getName());

    private WorkflowRuleRecordRelationshipContext.EventType eventType;

    public AddOneTimeJobForScheduledRule(WorkflowRuleRecordRelationshipContext.EventType eventType){
        this.eventType = eventType;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        try {
            if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_SCHEDULED_WORKFLOW_RULE) || context == null) {
                return false;
            }

            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

            if (moduleName == null) {
                LOGGER.debug("Module Name is empty for addOneTimeJobForScheduledRuleOnUpdate");
                return false;
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            if (module == null) {
                LOGGER.debug("Module not found for addOneTimeJobForScheduledRuleOnUpdate");
                return false;
            }

            List<? extends ModuleBaseWithCustomFields> records = Constants.getRecordsFromContext((FacilioContext) context);

            if(CollectionUtils.isEmpty(records)){
                return false;
            }

            if (eventType == WorkflowRuleRecordRelationshipContext.EventType.DELETE) {
                List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
                deleteScheduledJobsForWorkFlowRule(module.getModuleId(), recordIds);
                return false;
            }

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("JOB_TIME", "time", null, CommonOperators.IS_EMPTY));
            criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", null, CommonOperators.IS_EMPTY));

            List<? extends WorkflowRuleContext> rules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module, Collections.singletonList(EventType.SCHEDULED), criteria, false, true);

            if (CollectionUtils.isEmpty(rules)) {
                return false;
            }

            for (ModuleBaseWithCustomFields record : records) {
                List<Long> filteredRuleIds = new ArrayList<>();

                Map<String, Object> finalRecordObject = new HashMap<>();
                finalRecordObject.put("id", record.getId());

                for (WorkflowRuleContext rule : rules) {
                    if (rule.canAddOneTimeJob(record)) {
                        Long dateFieldValue = (Long) FieldUtil.getValue(record, rule.getDateField());
                        filteredRuleIds.add(rule.getId());
                        finalRecordObject.put(rule.getDateField().getName(), dateFieldValue);
                    }
                }

                if (CollectionUtils.isNotEmpty(filteredRuleIds)) {
                    JSONObject content = new JSONObject();
                    content.put("moduleName", module.getName());
                    content.put("record", finalRecordObject);
                    content.put("ruleIds", filteredRuleIds);
                    content.put("eventType", eventType.getName());

                    Message message = new Message();
                    message.setTopic(addOneTimeJobForScheduledRule + "/" + module.getModuleId() + "/" + record.getId());
                    message.setContent(content);
                    WmsBroadcaster.getBroadcaster().sendMessage(message);
                }
            }
        }catch (Exception e){
            LOGGER.error("Exception at AddOneTimeJobForScheduledRule : ",e);
        }

        return false;
    }

    public static void deleteScheduledJobsForWorkFlowRule(Long moduleId, List<Long> recordIds) throws Exception {

        if(recordIds == null){
            return;
        }

        List<WorkflowRuleRecordRelationshipContext> ruleRecordRelList = WorkflowRuleAPI.getRuleFromRuleAndRecordRelationshipTable(recordIds,moduleId);

        if (ruleRecordRelList == null || ruleRecordRelList.isEmpty()) {
            return;
        }

        int rowsDeleted = WorkflowRuleAPI.deleteRuleFromRuleAndRecordRelationshipTable(ruleRecordRelList);
        LOGGER.info("Deleted job count" + rowsDeleted);
    }
}
