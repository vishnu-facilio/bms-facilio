package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.trigger.context.TriggerFieldRelContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.List;

@Log4j
public class FetchScheduleTriggerMatchingRecordsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        TriggerFieldRelContext trigger = (TriggerFieldRelContext) context.get(FacilioConstants.ContextNames.TRIGGER);
        trigger.setScheduleTypeEnum(TriggerFieldRelContext.ScheduledRuleType.valueOf(trigger.getScheduleType()));
        List<? extends ModuleBaseWithCustomFields> records = getRecords(trigger, (JobContext) context.get(FacilioConstants.Job.JOB_CONTEXT));
         LOGGER.info("Matching records of Trigger : "+trigger.getId()+" is : "+records);
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(trigger.getModuleId());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
        context.put(FacilioConstants.ContextNames.DATE_RANGE, getRange(trigger, (JobContext) context.get(FacilioConstants.Job.JOB_CONTEXT)));

        return false;
    }

    private DateRange getRange(TriggerFieldRelContext trigger, JobContext jc) {
        long startTime = -1, endTime = -1;
        if(trigger.getTimeValue() == null){ //DATE_TIME field
            startTime = jc.getExecutionTime() * 1000;
            endTime =  (jc.getNextExecutionTime() * 1000) - 1;
        }
        else { //DATE Field
            startTime = DateTimeUtil.getDayStartTime();
            endTime = DateTimeUtil.getDayStartTime(1) - 1;
        }

        switch (trigger.getScheduleTypeEnum()) {
            case BEFORE:
                long interval = trigger.getTimeInterval() * 1000;
                return new DateRange(startTime + interval, endTime + interval);
            case ON:
                return new DateRange(startTime, endTime);
            case AFTER:
                interval = trigger.getTimeInterval() * 1000;
                return new DateRange(startTime - interval, endTime - interval);
        }
        return null;
    }

    private List<? extends ModuleBaseWithCustomFields> getRecords(TriggerFieldRelContext trigger, JobContext jc) throws Exception {
        long dateFieldId = trigger.getFieldId();
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(trigger.getModuleId());
        FacilioField dateField = moduleBean.getField(dateFieldId);
        DateRange range = getRange(trigger, jc);
         LOGGER.info("Range for trigger : "+trigger.getId()+" is "+range.toString());
         LOGGER.info("Date field id : "+trigger.getId()+" is "+trigger.getFieldId());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
        if (beanClassName == null) {
            beanClassName = ModuleBaseWithCustomFields.class;
        }
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .select(modBean.getAllFields(module.getName()))
                .module(module)
                .andCondition(CriteriaAPI.getCondition(dateField, range.toString(), DateOperators.BETWEEN))
                .beanClass(beanClassName)
                ;

        List<ModuleBaseWithCustomFields> records = selectBuilder.get();
        trigger.setTriggerEndTime(range.getEndTime());
        return records;
    }


}
