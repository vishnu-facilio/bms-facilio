package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.ScheduleTriggerRecordRelationContext;
import com.facilio.trigger.context.TriggerFieldRelContext;
import com.facilio.trigger.util.TriggerChainUtil;
import com.facilio.trigger.util.TriggerUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class CreateOneTimeTriggerExecutableJobsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        try {
            TriggerFieldRelContext trigger = (TriggerFieldRelContext) context.get(FacilioConstants.ContextNames.TRIGGER);

            if(trigger == null){
                return false;
            }

            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            boolean isNotValid = CollectionUtils.isEmpty(records) || trigger.getTimeValue() != null;

            updateTriggerEndTime(trigger);
            if (isNotValid) {
                return false;
            }

            List<Long> recordIds = records.stream().map(rec -> rec.getId()).collect(Collectors.toList());

            List<ScheduleTriggerRecordRelationContext> rels = TriggerUtil.getScheduleTriggerRecordRel(recordIds, trigger.getModuleId(), Collections.singletonList(trigger.getId()));

            List<Long> recordsToBeIgnored = rels.stream()
                    .filter(rel-> rel.getExecutionTime() > (System.currentTimeMillis() / 1000))
                    .map(ScheduleTriggerRecordRelationContext::getRecordId).collect(Collectors.toList());

            for (ModuleBaseWithCustomFields record : records) {
                if (recordsToBeIgnored.contains(record.getId())) {
                    continue;
                }

                long fieldId = trigger.getFieldId();
                FacilioField dateField = moduleBean.getField(fieldId);
                if (dateField != null) {
                    Long dateFieldValue = (Long) FieldUtil.getValue(record, dateField);

                    if (dateFieldValue == null) {
                        continue;
                    }

                    Long executionTime = TriggerUtil.getOneTimeTriggerExecutionTime(trigger, dateFieldValue);
                    executionTime = trigger.validatedExecutionTime(executionTime);

                    Long id = TriggerUtil.addScheduledTriggerRecordRelation(record.getId(), trigger, dateFieldValue, executionTime);
                    if (id != null) {
                        LOGGER.debug("One Time Job is created for id: " + id);
                    }
                }
            }
            context.put("executeTrigger", false);
        }catch (Exception e){
            LOGGER.error("Exception at CreateOneTimeExecutableJobs for Triggers:",e);
        }
        return false;
    }

    public static void updateTriggerEndTime(TriggerFieldRelContext trigger) throws Exception {
        TriggerFieldRelContext updatedTrigger = new TriggerFieldRelContext();
        updatedTrigger.setTriggerEndTime(trigger.getTriggerEndTime() / 1000);
        updatedTrigger.setId(trigger.getId());

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getTriggerFieldRelModule().getTableName())
                .fields(FieldFactory.getTriggerFieldRelFields())
                .andCondition(CriteriaAPI.getIdCondition(trigger.getId(),ModuleFactory.getTriggerFieldRelModule()));

        builder.update(FieldUtil.getAsProperties(updatedTrigger));

    }
}
