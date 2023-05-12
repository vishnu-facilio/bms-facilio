package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BulkRelatedListContext;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetBulkRelatedListWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long pageWidgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        if(pageWidgetId == null || pageWidgetId <= 0) {
            throw new IllegalArgumentException("Invalid PageSectionWidget id to fetch related list");
        }

        BulkRelatedListContext bulkRelList = RelatedListWidgetUtil.getBulkRelatedListOfWidgetId(pageWidgetId);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, bulkRelList);
        return false;
    }
}
