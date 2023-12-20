package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.List;

public class CalendarSlotCreationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long calendarId = (Long) context.get("calendarId");
        ZonedDateTime startDate = (ZonedDateTime) context.get("startDate");
        ZonedDateTime endDate = (ZonedDateTime) context.get("endDate");

        CalendarApi.eventSelectorMethod(calendarId,startDate,endDate);
        List<V3ControlActionTemplateContext> controlActionTemplateContextList = CalendarApi.getAssociatedControlActionTemplateList(calendarId);
        if(CollectionUtils.isNotEmpty(controlActionTemplateContextList)) {
            for (V3ControlActionTemplateContext controlActionTemplateContext : controlActionTemplateContextList) {
                FacilioChain inActivateControlActionTemplateChain = TransactionChainFactoryV3.getInActivateControlActionTemplateChain();
                FacilioContext facilioContext = new FacilioContext();
                facilioContext.put("controlActionTemplateId", controlActionTemplateContext.getId());
                facilioContext.put("ignoreActivityMsg", true);
                inActivateControlActionTemplateChain.setContext(facilioContext);
                inActivateControlActionTemplateChain.execute();

                FacilioChain activateControlActionTemplate = TransactionChainFactoryV3.getActivateControlActionTemplateChain();
                activateControlActionTemplate.setContext(facilioContext);
                activateControlActionTemplate.execute();
            }
        }
        return false;
    }
}
