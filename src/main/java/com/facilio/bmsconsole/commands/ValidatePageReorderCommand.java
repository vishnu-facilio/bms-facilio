package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class ValidatePageReorderCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(ValidatePageReorderCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long previousId = (Long) context.get(FacilioConstants.CustomPage.PREVIOUS_ID);
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long nextId = (Long) context.get(FacilioConstants.CustomPage.NEXT_ID);

        List<PagesContext> pages = CustomPageAPI.getAllRelatedPagesToOrder(id);
        double previousSequenceNumber = 0, nextSequenceNumber = 0, count = -1;

        if(CollectionUtils.isNotEmpty(pages)) {
            for (PagesContext page : pages) {

                if (page.getId() == previousId) {
                    previousSequenceNumber = page.getSequenceNumber();

                    if (page.getIsDefaultPage()) {
                        LOGGER.info("Invalid reordering attempted by putting page next to default page");
                        throw new IllegalArgumentException("Invalid reordering attempted");
                    }

                }

                if (page.getId() == nextId) {
                    nextSequenceNumber = page.getSequenceNumber();
                }

                if (page.getId() == id) {
                    context.put(FacilioConstants.ContextNames.MODULE_ID, page.getModuleId());
                    context.put(FacilioConstants.ContextNames.APP_ID, page.getAppId());
                    if (page.getIsDefaultPage()) {
                        LOGGER.info("Default Page cannot be reordered");
                        throw new IllegalArgumentException("Default Page cannot be reordered");
                    }
                }
            }
        }

        double finalNextSequenceNumber = nextSequenceNumber;
        double finalPreviousSequenceNumber = previousSequenceNumber;

        if(((previousId > 0 && nextId > 0 ) && (previousSequenceNumber > 0 && nextSequenceNumber > 0) && (previousSequenceNumber < nextSequenceNumber)) ||
                ((previousId > 0 && nextId <= 0) && (previousSequenceNumber > 0 && nextSequenceNumber == 0)) ||
                ((nextId > 0 && previousId <= 0) && (nextSequenceNumber > 0 && previousSequenceNumber == 0))) {
            if(previousId > 0 && nextId > 0){
                count = pages.stream().filter(f -> f.getSequenceNumber() < finalNextSequenceNumber && f.getSequenceNumber() > finalPreviousSequenceNumber).count();
            }
            else if (previousId <= 0) {
                count = pages.stream().filter(f -> f.getSequenceNumber() < finalNextSequenceNumber).count();
            }
            else {
                count = pages.stream().filter(f -> f.getSequenceNumber() > finalPreviousSequenceNumber).count();
            }
        }

        if(count != 0){
            LOGGER.error("Invalid Input for reordering pages");
            throw new IllegalArgumentException("Invalid Input");
        }
        return false;
    }
}
