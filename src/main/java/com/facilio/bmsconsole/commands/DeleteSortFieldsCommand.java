package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class DeleteSortFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1l);
        long viewId = (long) context.getOrDefault(FacilioConstants.ContextNames.VIEWID, -1l);

        FacilioView view = null;
        if (viewId != -1) {
            view = ViewAPI.getView(viewId);
        } else if (viewName != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            view = ViewAPI.getView(viewName, module.getModuleId(), AccountUtil.getCurrentOrg().getOrgId(), appId);
        }

        if (view == null) {
            throw new IllegalArgumentException("View not found");
        }

        ViewAPI.deleteViewSortColumns(view.getId());

        return false;
    }
}
