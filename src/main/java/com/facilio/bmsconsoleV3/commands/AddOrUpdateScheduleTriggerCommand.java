package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.util.ScheduledRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerFieldRelContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;

public class AddOrUpdateScheduleTriggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        BaseTriggerContext trigger = (BaseTriggerContext) context.get(TriggerUtil.TRIGGER_CONTEXT);
        TriggerFieldRelContext triggerFieldRel = (TriggerFieldRelContext) trigger;

        if (triggerFieldRel != null){
            triggerFieldRel.validateTrigger();

            if (TriggerUtil.isFieldTriggerPresent(trigger.getId())){
                TriggerUtil.deleteFieldTrigger(trigger.getId());
            }

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getTriggerFieldRelModule().getTableName())
                    .fields(FieldFactory.getTriggerFieldRelFields())
                    .addRecord(FieldUtil.getAsProperties(triggerFieldRel));

            builder.save();
            checkAndScheduleJob(trigger);
        }
        return false;
    }

    public void checkAndScheduleJob(BaseTriggerContext trigger) throws Exception{

        ScheduledRuleAPI.deleteScheduledTriggerJob(trigger.getId());

        if (trigger.isActive()) {
            ScheduledRuleAPI.addScheduledRuleJob(trigger);
        }
    }
}
