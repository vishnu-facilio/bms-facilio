package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PageSectionContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class ValidatePageSectionsReorderCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(ValidatePageSectionsReorderCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long previousId = (Long) context.get(FacilioConstants.CustomPage.PREVIOUS_ID);
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long nextId = (Long) context.get(FacilioConstants.CustomPage.NEXT_ID);

        List<PageSectionContext> sections = CustomPageAPI.getAllRelatedSectionsToOrder(id);
        double previousSequenceNumber = 0, nextSequenceNumber = 0, count = -1;

        if(CollectionUtils.isNotEmpty(sections)) {
            for (PageSectionContext section : sections) {

                if (section.getId() == previousId) {
                    previousSequenceNumber = section.getSequenceNumber();
                }

                if (section.getId() == nextId) {
                    nextSequenceNumber = section.getSequenceNumber();
                }

                if (previousSequenceNumber > 0 && nextSequenceNumber > 0) {
                    break;
                }

                Long columnId = (Long) context.get(FacilioConstants.CustomPage.COLUMN_ID);
                if(columnId == null || columnId <= 0){
                    columnId = section.getColumnId();
                    context.put(FacilioConstants.CustomPage.COLUMN_ID, columnId);
                }
            }
        }


        double finalNextSequenceNumber = nextSequenceNumber;
        double finalPreviousSequenceNumber = previousSequenceNumber;

        if(((previousId > 0 && nextId > 0 ) && (previousSequenceNumber > 0 && nextSequenceNumber > 0) && (previousSequenceNumber < nextSequenceNumber)) ||
                ((previousId > 0 && nextId <= 0) && (previousSequenceNumber > 0 && nextSequenceNumber == 0)) ||
                ((nextId > 0 && previousId <= 0) && (nextSequenceNumber > 0 && previousSequenceNumber == 0))) {
            if (previousId > 0 && nextId > 0) {
                count = sections.stream().filter(f -> f.getSequenceNumber() < finalNextSequenceNumber && f.getSequenceNumber() > finalPreviousSequenceNumber).count();
            } else if (previousId <= 0) {
                count = sections.stream().filter(f -> f.getSequenceNumber() < finalNextSequenceNumber).count();
            } else {
                count = sections.stream().filter(f -> f.getSequenceNumber() > finalPreviousSequenceNumber).count();
            }
        }

        if(count != 0 ){
            LOGGER.error("Invalid Input for reordering sections");
            throw new IllegalArgumentException("Invalid Input");
        }
        return false;
    }
}
