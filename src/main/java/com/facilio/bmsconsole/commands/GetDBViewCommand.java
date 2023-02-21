package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetDBViewCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long viewId;
        FacilioView requestView = (FacilioView) context.get(FacilioConstants.ContextNames.NEW_CV);

        if (requestView != null){
            viewId = requestView.getId();
        } else{
            viewId = (long) context.get(FacilioConstants.ContextNames.VIEWID);
        }

        FacilioView view = ViewAPI.getView(viewId);

        if (view == null) {
            throw new IllegalArgumentException("View not found");
        }

        if (requestView!= null && requestView.getIncludeParentCriteria()) {
            FacilioView customView = new FacilioView(view);
            context.put(FacilioConstants.ViewConstants.PARENT_VIEW_OBJECT, customView);
        }

        context.put(FacilioConstants.ContextNames.EXISTING_CV, view);

        return false;
    }
}
