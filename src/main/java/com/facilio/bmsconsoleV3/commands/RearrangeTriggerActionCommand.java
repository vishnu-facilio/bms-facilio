package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerActionContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RearrangeTriggerActionCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long triggerId = (long) context.get(FacilioConstants.ContextNames.ID);
        List<TriggerActionContext> actionList = (List<TriggerActionContext>) context.get(FacilioConstants.ContextNames.ACTIONS_LIST);
        if (triggerId > 0 && CollectionUtils.isNotEmpty(actionList)) {
            BaseTriggerContext trigger = TriggerUtil.getTrigger(triggerId);
            if (trigger == null) {
                throw new IllegalArgumentException("Invalid trigger");
            }

            List<TriggerActionContext> triggerActions = trigger.getTriggerActions();
            if (CollectionUtils.isEmpty(triggerActions)) {
                // there is no trigger actions associated with this trigger
                return false;
            }

            if (triggerActions.size() != actionList.size()) {
                throw new IllegalArgumentException("Re-arrange size mismatch");
            }

            boolean containsAll = triggerActions.stream().map(TriggerActionContext::getId).collect(Collectors.toList())
                    .containsAll(actionList.stream().map(TriggerActionContext::getId).collect(Collectors.toList()));
            if (!containsAll) {
                throw new IllegalArgumentException("Invalid trigger actions");
            }

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getTriggerActionFields());
            int counter = 1;
            for (TriggerActionContext triggerAction : actionList) {
                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getTriggerActionModule().getTableName())
                        .fields(Collections.singletonList(fieldMap.get("executionOrder")))
                        .andCondition(CriteriaAPI.getIdCondition(triggerAction.getId(), ModuleFactory.getTriggerActionModule()));
                Map<String, Object> map = new HashMap<>();
                map.put("executionOrder", counter++);
                builder.update(map);
            }
        }
        return false;
    }
}
