package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BulkRelatedListContext;
import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetBulkRelatedListWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        boolean isFetchForClone = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, false);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        if((widgetId == null || widgetId <= 0)) {
            throw new IllegalArgumentException("Invalid widgetId to fetch related list");
        }

        BulkRelatedListContext bulkRelList = RelatedListWidgetUtil.getBulkRelatedListOfWidgetId(widgetId, widgetWrapperType);
        if(bulkRelList != null) {
            List<RelatedListWidgetContext> connectedAppRelListToRemove = new ArrayList<>();
            for (RelatedListWidgetContext relList : bulkRelList.getRelatedList()) {
                if(relList.getConnectedAppWidgetId() != null && relList.getConnectedAppWidgetId()>0 && !isFetchForClone) {
                    boolean canShowRelList = CustomPageAPI.validateConnectedAppWidgetCriteria(recordId,
                            moduleName, relList.getConnectedAppWidgetId());
                    if(!canShowRelList) {
                        connectedAppRelListToRemove.add(relList);
                    }
                }
            }
            bulkRelList.getRelatedList().removeAll(connectedAppRelListToRemove);
        }
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, bulkRelList);
        return false;
    }
}
