package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.action.TriggerAction;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerType;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecuteNonModuleTriggerCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(ExecuteNonModuleTriggerCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        TriggerType triggerType = (TriggerType) context.get(FacilioConstants.ContextNames.TRIGGER_TYPE);
        List<EventType> activities = CommonCommandUtil.getEventTypes(context);
        if (activities != null && activities.size() > 0) {

            List<BaseTriggerContext> triggers = TriggerUtil.getTriggers(null, activities, null, true, false, triggerType);
            TriggerUtil.executeTriggerActions(triggers, (FacilioContext) context, null, null, null);
        }
        return false;
    }
}
