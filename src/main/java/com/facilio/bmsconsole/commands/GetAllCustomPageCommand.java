package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import java.util.List;
import java.util.Map;

public class GetAllCustomPageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if(module == null){
            throw new IllegalArgumentException("Invalid module");
        }
        long moduleId = module.getModuleId();

        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        ApplicationContext app = appId== null || appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
        if (app == null) {
            app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        appId = app.getId();

        PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.getOrDefault(FacilioConstants.CustomPage.LAYOUT_TYPE,
                PagesContext.PageLayoutType.WEB);
        List<Map<String, Object>> customPages = CustomPageAPI.getAllCustomPageForBuilder(appId, moduleId, layoutType);
        context.put(FacilioConstants.CustomPage.CUSTOM_PAGES,customPages);
        return false;
    }
}