package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class SetFlaggedAlarmProcessTicketModuleIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for (FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                String ticketModuleName = flaggedEventRule.getTicketModuleName();
                if(StringUtils.isNotEmpty(ticketModuleName)){
                    FacilioModule ticketModule = modBean.getModule(ticketModuleName);
                    long ticketModuleId = ticketModule.getModuleId();
                    flaggedEventRule.setTicketModuleId(ticketModuleId);
                }
            }
        }
        return false;
    }
}
