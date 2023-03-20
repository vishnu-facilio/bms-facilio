package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.actions.CustomPageAction;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class ChangePageStatusCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(ChangePageStatusCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id <= 0) {
            LOGGER.error("Invalid Id");
            throw new IllegalArgumentException("Invalid Id");
        }

        Boolean status = (Boolean) context.get(FacilioConstants.ContextNames.STATUS);
        if(status == null){
            LOGGER.error("Invalid status");
            throw new IllegalArgumentException("Invalid status");
        }

        PagesContext page = CustomPageAPI.getCustomPage(id);
        if(page == null){
            LOGGER.error("Page does not exists");
            throw new IllegalArgumentException("Page does not exists");
        }

        if(page.getIsDefaultPage()){
             LOGGER.info("Default Page status can't be changed");
        }
        else {
            page.setStatus(status);
            CustomPageAPI.patchCustomPage(page);
        }
        return false;
    }
}
