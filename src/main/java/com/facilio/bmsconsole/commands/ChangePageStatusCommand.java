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
            throw new IllegalArgumentException("Invalid Id to update page's status");
        }

        Boolean status = (Boolean) context.get(FacilioConstants.ContextNames.STATUS);
        if(status == null){
            throw new IllegalArgumentException("status can't be null");
        }

        PagesContext page = CustomPageAPI.getCustomPage(id);
        if(page == null){
            throw new IllegalArgumentException("Page does not exists");
        }

        if((page.getIsDefaultPage() || (page.getIsSystemPage()!=null && page.getIsSystemPage())) && !status){
            throw new IllegalArgumentException("System or Default page can't be disabled");
        }
        else {
            page.setStatus(status);
            CustomPageAPI.patchCustomPage(page);
        }
        return false;
    }
}
