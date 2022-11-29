package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.DisplayNameToLinkNameUtil;
import org.apache.commons.chain.Context;


public class SetPriorityNameCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception{
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        TicketPriorityContext ticketPriorityContext = (TicketPriorityContext) context.get(FacilioConstants.ContextNames.RECORD);
            if(ticketPriorityContext != null && ticketPriorityContext.getPriority() == null || ticketPriorityContext.getPriority().isEmpty()) {
                if(ticketPriorityContext.getDisplayName() != null && !ticketPriorityContext.getDisplayName().isEmpty()) {
                    String linkName=DisplayNameToLinkNameUtil.getLinkName(ticketPriorityContext.getDisplayName(), moduleName ,"priority");
                    ticketPriorityContext.setPriority(linkName);
                }
            }
        return false;
    }

}
