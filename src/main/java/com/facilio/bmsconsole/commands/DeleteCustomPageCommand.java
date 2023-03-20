package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageTabsContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
public class DeleteCustomPageCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(DeleteCustomPageCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long id = (Long) context.get(FacilioConstants.CustomPage.PAGE_ID);
        PagesContext page = CustomPageAPI.getCustomPage(id);

        if (page == null) {
            LOGGER.error("Page not found");
            throw new IllegalArgumentException("Page not found");
        } else if (page.getIsDefaultPage()) {
            LOGGER.error("Default page cannot be deleted");
            throw new IllegalArgumentException("Default page cannot be deleted");
        }
        if (id > 0) {
            CustomPageAPI.deletePage(Collections.singletonList(id), ModuleFactory.getPagesModule());
        }

        return false;
    }
}