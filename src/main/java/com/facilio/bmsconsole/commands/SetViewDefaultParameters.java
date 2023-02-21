package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class SetViewDefaultParameters extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.NEW_CV);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);

        view.setOwnerId(AccountUtil.getCurrentUser().getId());
        if (view.getIsLocked() == null) {
            view.setLocked(false);
        }

        if (view.getIncludeParentCriteria()) {
            String parentViewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
            if (StringUtils.isNotEmpty(parentViewName)) {
                FacilioView parentView;
                ModuleBean moduleBean = Constants.getModBean();
                long orgId = AccountUtil.getCurrentOrg().getOrgId();
                FacilioModule module = moduleBean.getModule(moduleName);

                if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
                    parentView = ViewAPI.getView(viewName, -1, moduleName, orgId, view.getAppId());
                } else {
                    parentView = ViewAPI.getView(viewName, module.getModuleId(), moduleName, orgId, view.getAppId());
                }
                context.put(FacilioConstants.ViewConstants.PARENT_VIEW_OBJECT, parentView);
            }
        }

        return false;
    }
}
