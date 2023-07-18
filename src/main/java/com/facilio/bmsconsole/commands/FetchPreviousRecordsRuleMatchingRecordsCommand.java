package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchPreviousRecordsRuleMatchingRecordsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);

        List<? extends ModuleBaseWithCustomFields> records = getRecords(rule, (JobContext) context.get(FacilioConstants.Job.JOB_CONTEXT));
        // LOGGER.info("Matching records of rule : "+rule.getId()+" is : "+records);

        context.put(FacilioConstants.ContextNames.MODULE_NAME, rule.getModule().getName());
        context.put(FacilioConstants.ContextNames.RECORD_LIST, records);

        return false;
    }

    private List<? extends ModuleBaseWithCustomFields> getRecords(WorkflowRuleContext rule, JobContext jobContext) throws Exception {
        Long endTime = (jobContext.getNextExecutionTime() * 1000) - 1;
        switch (rule.getScheduleTypeEnum()) {
            case BEFORE:
                long interval = rule.getInterval() * 1000;
                endTime = endTime + interval;
                break;
            case AFTER:
                interval = rule.getInterval() * 1000;
                endTime = endTime - interval;
                break;
        }

        rule.setRuleEndTime(endTime);

        FacilioField dateField = rule.getDateField();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .module(rule.getModule())
                .beanClass(FacilioConstants.ContextNames.getClassFromModule(rule.getModule()))
                .select(modBean.getAllFields(rule.getModuleName()))
                .andCondition(CriteriaAPI.getCondition(dateField, CommonOperators.IS_NOT_EMPTY))
                .andCondition(CriteriaAPI.getCondition(dateField, String.valueOf(endTime), DateOperators.IS_BEFORE));
        if (rule.getCriteria() != null) {
            builder.andCriteria(rule.getCriteria());
        }
        return builder.get();
    }
}
