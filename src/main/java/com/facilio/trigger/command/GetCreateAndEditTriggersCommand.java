package com.facilio.trigger.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerType;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetCreateAndEditTriggersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module name");
        }

        Criteria criteria = new Criteria();
        if (eventType.equals(EventType.CREATE_OR_EDIT)) {
            criteria.addOrCondition(CriteriaAPI.getCondition("EVENT_TYPE", "eventType", String.valueOf(EventType.CREATE.getValue()), NumberOperators.EQUALS));
            criteria.addOrCondition(CriteriaAPI.getCondition("EVENT_TYPE", "eventType", String.valueOf(EventType.EDIT.getValue()), NumberOperators.EQUALS));
        } else {
            criteria.addAndCondition(CriteriaAPI.getCondition("EVENT_TYPE", "eventType", String.valueOf(eventType.getValue()), NumberOperators.EQUALS));

        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTriggerModule().getTableName())
                .select(FieldFactory.getTriggerFields())
                .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        if (!criteria.isEmpty()) {
            builder.andCriteria(criteria);
        }

        builder.orderBy("EXECUTION_ORDER");

        List<BaseTriggerContext> triggers = FieldUtil.getAsBeanListFromMapList(builder.get(), BaseTriggerContext.class);

        if(triggers != null && !triggers.isEmpty()) {
            TriggerUtil.fillTriggerExtras(triggers,true, TriggerType.MODULE_TRIGGER);
        }

        context.put(TriggerUtil.TRIGGERS_LIST, triggers);
        return false;
    }
}
