package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class ValidatePageDeletionCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(ValidatePageDeletionCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        PagesContext page = CustomPageAPI.getCustomPage(id);

        if (page == null) {
            LOGGER.error("Page not found");
            throw new IllegalArgumentException("Page not found");
        } else if (page.getIsDefaultPage()) {
            LOGGER.error("Default page cannot be deleted");
            throw new IllegalArgumentException("Default page cannot be deleted");
        }
        return false;
    }
}