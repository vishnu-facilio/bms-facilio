package com.facilio.remotemonitoring.handlers.ticketmodulecreate;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;

import java.util.HashMap;
import java.util.Map;

public interface TicketModuleRecordCreationHandler {
    default Map<String,Object> consructRecordPropsFromFieldMapping(FlaggedEventRuleContext flaggedEventRule, FlaggedEventContext flaggedEvent) throws Exception{
        return new HashMap<>();
    }

    default void updateInitialRecordStatus(Map<String,Object> recordAsProp, ModuleBaseWithCustomFields record) throws Exception{}

    default void sendClosureCommandToTicketModuleRecord(FlaggedEventRuleContext flaggedEventRule, FlaggedEventContext flaggedEvent) throws Exception{}

}
