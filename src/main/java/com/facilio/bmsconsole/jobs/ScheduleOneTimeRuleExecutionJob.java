package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.WorkflowRuleRecordRelationshipContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class ScheduleOneTimeRuleExecutionJob extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(ScheduleOneTimeRuleExecutionJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            Long id = jc.getJobId();
            WorkflowRuleRecordRelationshipContext ruleRecordRel = ScheduleOneTimeRuleExecutionJob.getRuleFromRuleAndRecordRelationshipTable(id);

            if(ruleRecordRel == null){
                LOGGER.info("No relationship found for Job Id " + id);
                return;
            }

            Long ruleId = ruleRecordRel.getRuleId();
            Long recordId = ruleRecordRel.getRecordId();

            WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(ruleId);

            if(rule == null || !rule.isActive()){
                return;
            }

            ModuleBaseWithCustomFields record = getRecord(rule,recordId);
            if(record != null){
                executeSingleWorkflowRuleCommand(rule,record);
            }
        }
        catch (Exception e) {
            LOGGER.fatal("Error occurred during scheduled rule execution for job : "+jc.getJobId(), e);
            CommonCommandUtil.emailException("ScheduledRuleExecutionJob", "Error occurred during scheduled rule execution for job : "+jc.getJobId(), e);
        }
    }

    public static WorkflowRuleRecordRelationshipContext getRuleFromRuleAndRecordRelationshipTable(Long id) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getScheduleOneTimeRuleExecutionFields())
                .table(ModuleFactory.getscheduleRuleRecordRelationModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getscheduleRuleRecordRelationModule()));

        return FieldUtil.getAsBeanFromMap(builder.fetchFirst(),WorkflowRuleRecordRelationshipContext.class);
    }

    private ModuleBaseWithCustomFields getRecord(WorkflowRuleContext rule, Long recordId) throws Exception {
        FacilioModule module = rule.getModule();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if(module == null){
            module = modBean.getModule(rule.getModuleId());
        }

        Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
        if (beanClassName == null) {
            beanClassName = ModuleBaseWithCustomFields.class;
        }
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .select(modBean.getAllFields(module.getName()))
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(recordId,module))
                .beanClass(beanClassName)
                ;

        if (rule.getCriteria() != null) {
            selectBuilder.andCriteria(rule.getCriteria());
        }

        return selectBuilder.fetchFirst();
    }

    private void executeSingleWorkflowRuleCommand(WorkflowRuleContext rule,ModuleBaseWithCustomFields data) throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE,rule);
        context.put(FacilioConstants.ContextNames.RECORD,data);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,rule.getModule().getName());

        try {
            WorkflowRuleAPI.executeSingleWorkflowRuleCommand(context);
        }catch (Exception e) {
            LOGGER.fatal("Error occurred during scheduled rule execution: "+ e);
        }
    }
}
