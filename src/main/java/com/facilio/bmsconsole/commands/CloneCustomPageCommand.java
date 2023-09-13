package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

@Log4j
public class CloneCustomPageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        long appId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
        long pageId = (long) context.get(FacilioConstants.ContextNames.ID);

        ApplicationContext app =  ApplicationApi.getApplicationForId(appId);
        if (app == null) {
            app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        long clonedPageId = PagesUtil.clonePage(module, app.getId(), pageId);

        PagesContext clonedPage = CustomPageAPI.getCustomPage(clonedPageId);
        context.put(FacilioConstants.CustomPage.CLONED_PAGE, clonedPage);
        return false;
    }
}
