package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PageTabsContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class ValidatePageTabsReorderCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(ValidatePageTabsReorderCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long previousId = (Long) context.get(FacilioConstants.CustomPage.PREVIOUS_ID);
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long nextId = (Long) context.get(FacilioConstants.CustomPage.NEXT_ID);

        List<PageTabsContext> tabs = CustomPageAPI.getAllRelatedTabsToOrder(id);
        double previousSequenceNumber = 0, nextSequenceNumber = 0, count = -1;

        if(CollectionUtils.isNotEmpty(tabs)) {
            for (PageTabsContext tab : tabs) {

                if (tab.getId() == previousId) {
                    previousSequenceNumber = tab.getSequenceNumber();
                }

                if (tab.getId() == nextId) {
                    nextSequenceNumber = tab.getSequenceNumber();
                }

                if (previousSequenceNumber > 0 && nextSequenceNumber > 0) {
                    break;
                }

                Long pageId = (Long) context.get(FacilioConstants.CustomPage.PAGE_ID);
                if(pageId == null || pageId <= 0){
                    pageId = tab.getPageId();
                    context.put(FacilioConstants.CustomPage.PAGE_ID,pageId);
                }
            }
        }


        double finalNextSequenceNumber = nextSequenceNumber;
        double finalPreviousSequenceNumber = previousSequenceNumber;

        if(((previousId > 0 && nextId > 0 ) && (previousSequenceNumber > 0 && nextSequenceNumber > 0) && (previousSequenceNumber < nextSequenceNumber)) ||
                ((previousId > 0 && nextId <= 0) && (previousSequenceNumber > 0 && nextSequenceNumber == 0)) ||
                ((nextId > 0 && previousId <= 0) && (nextSequenceNumber > 0 && previousSequenceNumber == 0))) {
            if (previousId > 0 && nextId > 0) {
                count = tabs.stream().filter(f -> f.getSequenceNumber() < finalNextSequenceNumber && f.getSequenceNumber() > finalPreviousSequenceNumber).count();
            } else if (previousId <= 0) {
                count = tabs.stream().filter(f -> f.getSequenceNumber() < finalNextSequenceNumber).count();
            } else {
                count = tabs.stream().filter(f -> f.getSequenceNumber() > finalPreviousSequenceNumber).count();
            }
        }

        if(count != 0 ){
            LOGGER.error("Invalid Input for reordering tabs");
            throw new IllegalArgumentException("Invalid Input");
        }
        return false;
    }
}
