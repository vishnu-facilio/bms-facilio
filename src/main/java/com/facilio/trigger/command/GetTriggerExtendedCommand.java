package com.facilio.trigger.command;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.TriggerFieldRelContext;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetTriggerExtendedCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);

        if( (id <= 0) && (eventType.isPresent(EventType.CREATE.getValue()) || eventType.isPresent(EventType.EDIT.getValue()))){
            return false;
        }

        List<FacilioField> fields = FieldFactory.getTriggerFields();
        fields.addAll(FieldFactory.getTriggerFieldRelFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTriggerModule().getTableName())
                .select(fields)
                .innerJoin(ModuleFactory.getTriggerFieldRelModule().getTableName())
                .on("Facilio_Trigger.ID = " + ModuleFactory.getTriggerFieldRelModule().getTableName() + ".ID")
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getTriggerFieldRelModule()));

        TriggerFieldRelContext trigger = FieldUtil.getAsBeanFromMap(builder.fetchFirst(),TriggerFieldRelContext.class);
        context.put(FacilioConstants.ContextNames.TRIGGER,trigger);
        return false;
    }
}
