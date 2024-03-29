package com.facilio.trigger.command;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerFieldRelContext;
import com.facilio.trigger.context.TriggerType;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class GetExtendedScheduledTriggersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<BaseTriggerContext> triggerList = (List<BaseTriggerContext>) context.get(FacilioConstants.ContextNames.TRIGGERS);
        List<Long> fieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.FIELD_IDS);
        Long timeInterval = (Long) context.get(FacilioConstants.ContextNames.TIME_INTERVAL);
        LocalTime timeValue = (LocalTime) context.get(FacilioConstants.ContextNames.TIME_VALUE);

        WorkflowRuleContext.ScheduledRuleType scheduledRuleType = (WorkflowRuleContext.ScheduledRuleType) context.get(FacilioConstants.ContextNames.SCHEDULE_RULE_TYPE);
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
        if (scheduledRuleType != null) {
            criteria.addAndCondition(CriteriaAPI.getCondition("SCHEDULE_TYPE", "scheduleType", String.valueOf(scheduledRuleType.getValue()), NumberOperators.EQUALS));
        }
        if (timeValue != null){
            criteria.addAndCondition(CriteriaAPI.getCondition("TIME_VALUE", "timeValue", String.valueOf(timeValue), StringOperators.IS));
        }
        if (timeInterval != null){
            criteria.addAndCondition(CriteriaAPI.getCondition("TIME_INTERVAL", "timeInterval", String.valueOf(timeInterval), NumberOperators.EQUALS));
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTriggerFieldRelModule().getTableName())
                .select(fields)
                .innerJoin(ModuleFactory.getTriggerModule().getTableName())
                .on(ModuleFactory.getTriggerFieldRelModule().getTableName() + ".ID = " + "Facilio_Trigger.ID ")
                .andCondition(CriteriaAPI.getIdCondition(triggerIds, ModuleFactory.getTriggerFieldRelModule()))
                .andCondition(CriteriaAPI.getCondition("SCHEDULE_TYPE","SCHEDULE_TYPE",null, CommonOperators.IS_NOT_EMPTY));

        if (!criteria.isEmpty()) {
            builder.andCriteria(criteria);
        }
        builder.orderBy("EXECUTION_ORDER");

        List<TriggerFieldRelContext> triggers = FieldUtil.getAsBeanListFromMapList(builder.get(), TriggerFieldRelContext.class);

        if(triggers != null && !triggers.isEmpty()) {
            TriggerUtil.fillTriggerExtras(triggers,true, TriggerType.MODULE_TRIGGER);
        }

        context.put(TriggerUtil.TRIGGERS_LIST, triggers);
        return false;
    }
}
