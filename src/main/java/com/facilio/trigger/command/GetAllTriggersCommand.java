package com.facilio.trigger.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
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
import org.apache.commons.collections.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetAllTriggersCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module name");
        }
        
        TriggerType triggerType = (TriggerType) context.get(FacilioConstants.ContextNames.TRIGGER_TYPE);
        Criteria extendedCriteria = (Criteria) context.get(ContextNames.CRITERIA);

        List<BaseTriggerContext> triggers;
        if (triggerType != null) {
        	triggers = TriggerUtil.getTriggers(module, null, null, false, true, extendedCriteria, false, triggerType);
        }
        else {
        	triggers = TriggerUtil.getTriggers(module, null, null, false, true, extendedCriteria, false);
        }
        if (CollectionUtils.isNotEmpty(triggers)) {
            Iterator<BaseTriggerContext> iterator = triggers.iterator();
            while (iterator.hasNext()) {
                BaseTriggerContext next = iterator.next();
                if (next.isInternal()) {
                    iterator.remove();
                }
            }
        }
        context.put(TriggerUtil.TRIGGERS_LIST, triggers);
        return false;
    }
}
