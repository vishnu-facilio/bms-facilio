package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.ScheduleTriggerRecordRelationContext;
import com.facilio.trigger.util.TriggerUtil;
import lombok.extern.log4j.Log4j;

@Log4j
public class ScheduleOneTimeTriggerExecutionJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TRIGGER_MAP)) {
                return;
            }

            Long id = jc.getJobId();
            ScheduleTriggerRecordRelationContext triggerRelRecord = getTriggerFromRecordRelTable(id);

            if(triggerRelRecord == null){
                LOGGER.info("No relationship found for Job Id " + id);
                return;
            }

            Long triggerId = triggerRelRecord.getTriggerId();
            Long recordId = triggerRelRecord.getRecordId();

            BaseTriggerContext trigger = TriggerUtil.getTrigger(triggerId, EventType.SCHEDULED);

            if(trigger == null || !trigger.isActive()){
                return;
            }

            ModuleBaseWithCustomFields record = getRecord(trigger,recordId);
            if(record != null){
                executeSingleTrigger(trigger,record);
            }
        }
        catch (Exception e) {
            LOGGER.fatal("Error occurred during scheduled Trigger execution for job : "+jc.getJobId(), e);
            CommonCommandUtil.emailException("ScheduledTriggerExecutionJob", "Error occurred during scheduled trigger execution for job : "+jc.getJobId(), e);
        }
    }

    public static ScheduleTriggerRecordRelationContext getTriggerFromRecordRelTable(Long id) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getScheduleTriggerRelationFields())
                .table(ModuleFactory.getScheduleTriggerRecordRelationModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getScheduleTriggerRecordRelationModule()));

        return FieldUtil.getAsBeanFromMap(builder.fetchFirst(),ScheduleTriggerRecordRelationContext.class);
    }

    private ModuleBaseWithCustomFields getRecord(BaseTriggerContext trigger, Long recordId) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long moduleId = trigger.getModuleId();
        FacilioModule module = moduleBean.getModule(moduleId);

        Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
        if (beanClassName == null) {
            beanClassName = ModuleBaseWithCustomFields.class;
        }
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .select(moduleBean.getAllFields(module.getName()))
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(recordId,module))
                .beanClass(beanClassName)
                ;

        return selectBuilder.fetchFirst();
    }

    private void executeSingleTrigger(BaseTriggerContext trigger,ModuleBaseWithCustomFields data) throws Exception {

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long moduleId = trigger.getModuleId();
        FacilioModule module = moduleBean.getModule(moduleId);

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.TRIGGER,trigger);
        context.put(FacilioConstants.ContextNames.RECORD,data);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());

        try {
            WorkflowRuleAPI.executeWorkflowRuleFromTrigger(context);
        }catch (Exception e) {
            LOGGER.fatal("Error occurred during scheduled trigger execution: "+ e);
        }
    }
}
