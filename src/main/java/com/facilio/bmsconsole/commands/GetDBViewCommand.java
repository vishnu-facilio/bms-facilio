package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class GetDBViewCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long viewId = -1;
        FacilioView view = null;
        String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioView requestView = (FacilioView) context.get(FacilioConstants.ContextNames.NEW_CV);

        if (requestView != null){
            viewId = requestView.getId();
        } else if (context.containsKey(FacilioConstants.ContextNames.VIEWID)){
            viewId = (long) context.get(FacilioConstants.ContextNames.VIEWID);
        }

        if (viewId > 0) {
            view = ViewAPI.getView(viewId);
        } else if (StringUtils.isNotEmpty(viewName) && StringUtils.isNotEmpty(moduleName)) {
            view = ViewAPI.getView(viewName, moduleName, AccountUtil.getCurrentOrg().getOrgId(), -1);
            if (view == null) {
                context.put(FacilioConstants.ContextNames.SKIP_VALIDATION, true);
                return false;
            }
        }

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
