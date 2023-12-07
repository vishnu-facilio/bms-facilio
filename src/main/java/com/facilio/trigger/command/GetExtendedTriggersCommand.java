package com.facilio.trigger.command;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerActionContext;
import com.facilio.trigger.context.TriggerFieldRelContext;
import com.facilio.trigger.context.TriggerType;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetExtendedTriggersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<BaseTriggerContext> triggerList = (List<BaseTriggerContext>) context.get(FacilioConstants.ContextNames.TRIGGERS);
        List<Long> fieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.FIELD_IDS);
        if (CollectionUtils.isEmpty(triggerList)) {
            return false;
        }
        List<Long> triggerIds = triggerList.stream().map(BaseTriggerContext::getId).collect(Collectors.toList());

        List<FacilioField> fields = FieldFactory.getTriggerFields();
        fields.addAll(FieldFactory.getTriggerFieldRelFields());
        Criteria criteria = new Criteria();
        if (CollectionUtils.isNotEmpty(fieldIds)) {
            criteria.addOrCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", StringUtils.join(fieldIds, ','), NumberOperators.EQUALS));
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTriggerFieldRelModule().getTableName())
                .select(fields)
                .innerJoin(ModuleFactory.getTriggerModule().getTableName())
                .on(ModuleFactory.getTriggerFieldRelModule().getTableName() + ".ID = " + "Facilio_Trigger.ID ")
                .andCondition(CriteriaAPI.getIdCondition(triggerIds, ModuleFactory.getTriggerFieldRelModule()))
                .andCondition(CriteriaAPI.getCondition("SCHEDULE_TYPE","SCHEDULE_TYPE",null, CommonOperators.IS_EMPTY));

        if (!criteria.isEmpty()) {
            builder.andCriteria(criteria);
        }
        builder.orderBy("EXECUTION_ORDER");

        List<TriggerFieldRelContext> triggers = FieldUtil.getAsBeanListFromMapList(builder.get(), TriggerFieldRelContext.class);

        if(triggers != null && !triggers.isEmpty()) {
            TriggerUtil.fillTriggerExtras(triggers,true,TriggerType.MODULE_TRIGGER);
        }

        context.put(TriggerUtil.TRIGGERS_LIST, triggers);
        return false;
    }
}
