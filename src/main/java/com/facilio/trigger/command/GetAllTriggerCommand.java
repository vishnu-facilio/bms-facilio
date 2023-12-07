package com.facilio.trigger.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerType;
import com.facilio.trigger.util.TriggerChainUtil;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetAllTriggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module name");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTriggerModule().getTableName())
                .select(FieldFactory.getTriggerFields())
                .andCondition(CriteriaAPI.getCondition("MODULE_ID","moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        List<BaseTriggerContext> triggers = FieldUtil.getAsBeanListFromMapList(builder.get(),BaseTriggerContext.class);

        List<BaseTriggerContext> triggerList = new ArrayList<>();
        Map<EventType,List<BaseTriggerContext>> eventVsTrigger = triggers.stream().collect(Collectors.groupingBy(BaseTriggerContext::getEventTypeEnum));
        for (EventType eventType : eventVsTrigger.keySet()) {
            FacilioChain triggerChain = TriggerChainUtil.getTriggerListChain(eventType);
            FacilioContext triggerContext = triggerChain.getContext();
            triggerContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            triggerContext.put(FacilioConstants.ContextNames.TRIGGERS, eventVsTrigger.get(eventType));
            triggerContext.put(FacilioConstants.ContextNames.EVENT_TYPE,eventType);
            triggerChain.execute();
            List<BaseTriggerContext> triggerContextList = (List<BaseTriggerContext>) triggerContext.get(TriggerUtil.TRIGGERS_LIST);
            triggerList = CollectionUtils.isNotEmpty(triggerList) ? triggerList : new ArrayList<>();
            if (CollectionUtils.isNotEmpty(triggerContextList)){
                triggerList.addAll(triggerContextList);
            }
        }

        if(triggers != null && !triggers.isEmpty()) {
            TriggerUtil.fillTriggerExtras(triggers,true, TriggerType.MODULE_TRIGGER);
        }

        context.put(TriggerUtil.TRIGGERS_LIST,triggerList);

        return false;
    }
}
