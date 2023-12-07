package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerFieldRelContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;


public class AddOrUpdateTriggerFieldRelCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        BaseTriggerContext trigger = (BaseTriggerContext) context.get(TriggerUtil.TRIGGER_CONTEXT);
        TriggerFieldRelContext fieldTrigger = (TriggerFieldRelContext) trigger;

        if (fieldTrigger != null){
            fieldTrigger.validateTrigger();

            if (TriggerUtil.isFieldTriggerPresent(fieldTrigger.getId())){
                TriggerUtil.deleteFieldTrigger(fieldTrigger.getId());
            }

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getTriggerFieldRelModule().getTableName())
                    .fields(FieldFactory.getTriggerFieldRelFields())
                    .addRecord(FieldUtil.getAsProperties(fieldTrigger));

            builder.save();
        }
        return false;
    }

}
