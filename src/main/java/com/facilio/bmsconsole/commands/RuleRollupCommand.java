package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RuleRollupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
        Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
        RollupType rollupType = (RollupType) context.get(FacilioConstants.ContextNames.ROLL_UP_TYPE);
        if (id != null && id > 0) {
            if (startTime == null || startTime < 0) {
                throw new IllegalArgumentException("Start Time cannot be empty");
            }
            if (endTime == null || endTime < 0) {
                throw new IllegalArgumentException("End Time cannot be empty");
            }

            Object valid = null;
            if (rollupType == RollupType.RULE) {
                valid = WorkflowRuleAPI.getWorkflowRule(id);
            }
            else if (rollupType == RollupType.ASSET) {
                valid = AssetsAPI.getAssetInfo(id);
            }
            if (valid == null) {
                throw new IllegalArgumentException("Invalid rule");
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.READING_EVENT));

            List<FacilioField> fields = new ArrayList<>();
            FacilioField createdTimeField = fieldMap.get("createdTime");
            fields.add(createdTimeField);
            FacilioField groupConcatField = new FacilioField();
            groupConcatField.setName("severity");
            groupConcatField.setColumnName("GROUP_CONCAT(SEVERITY_ID SEPARATOR ',')");
            groupConcatField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            groupConcatField.setDataType(FieldType.STRING);
            fields.add(groupConcatField);
            SelectRecordsBuilder<ReadingEventContext> builder = new SelectRecordsBuilder<ReadingEventContext>()
                    .select(fields)
                    .beanClass(ReadingEventContext.class)
                    .module(modBean.getModule(FacilioConstants.ContextNames.READING_EVENT))
                    .groupBy(createdTimeField.getCompleteColumnName())
                    .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime + "," + endTime, DateOperators.BETWEEN))
                    ;

            if (rollupType == RollupType.RULE) {
                builder.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(id), NumberOperators.EQUALS));
            }
            else if (rollupType == RollupType.ASSET) {
                builder.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", String.valueOf(id), NumberOperators.EQUALS));
            }

            List<Map<String, Object>> maps = builder.getAsProps();

            AlarmSeverityContext clearSeverity = AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY);
            AlarmSeverityContext majorSeverity = AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.MAJOR_SEVERITY);
            String clearSeverityIdString = String.valueOf(clearSeverity.getId());

            List<BaseEventContext> list = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(maps)) {
                for (Map<String, Object> map : maps) {
                    Long createdTime = (Long) map.get("createdTime");
                    String severity = (String) map.get("severity");
                    String[] split = severity.split(",");
                    boolean clear = true;
                    for (String s : split) {
                        if (!(Objects.equals(s, clearSeverityIdString))) {
                            clear = false;
                            break;
                        }
                    }

                    list.add(getRuleRollupEvent(clear ? clearSeverity : majorSeverity, valid, createdTime, rollupType));
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                FacilioChain chain = TransactionChainFactory.getV2AddEventChain(true);
                Context chainContext = chain.getContext();
                chainContext.put(EventConstants.EventContextNames.EVENT_LIST, list);
                chain.execute();
            }
        }
        return false;
    }

    private BaseEventContext getRuleRollupEvent(AlarmSeverityContext alarmSeverityContext, Object obj, Long createdTime, RollupType rollupType) {
        BaseEventContext event = null;
        if (rollupType == RollupType.ASSET) {
            event = new AssetRollUpEvent();
        }
        else if (rollupType == RollupType.RULE) {
            event = new RuleRollUpEvent();
        }
        if (rollupType == RollupType.RULE) {
            ((RuleRollUpEvent) event).setRule((ReadingRuleContext) obj);
        }
        else if (rollupType == RollupType.ASSET) {
            event.setResource((ResourceContext) obj);
        }
        event.setSeverity(alarmSeverityContext);
        event.setSeverityString(alarmSeverityContext.getSeverity());
        event.setMessage(alarmSeverityContext.getSeverity().equalsIgnoreCase("Clear") ? "Rule Cleared": "Event Occurred");
        event.setCreatedTime(createdTime);
        return event;
    }

    public enum RollupType implements FacilioEnum {
        ASSET, RULE;

        public static RollupType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return null;
        }
    }
}
