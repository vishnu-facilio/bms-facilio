package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class ChangePageTabStatusCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(ChangePageTabStatusCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id <= 0) {
            LOGGER.error("Invalid Id");
            throw new IllegalArgumentException("Invalid Id");
        }

        Boolean status = (Boolean) context.get(FacilioConstants.ContextNames.STATUS);
        if (status == null) {
            LOGGER.error("Invalid status");
            throw new IllegalArgumentException("Invalid status");
        }

        PageTabContext tab = new PageTabContext();
        tab.setId(id);
        tab.setStatus(status);
        CustomPageAPI.patchPageTab(tab);
        return false;
    }
}
