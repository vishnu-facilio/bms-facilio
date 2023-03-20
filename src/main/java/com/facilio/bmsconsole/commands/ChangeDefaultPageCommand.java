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
import org.apache.log4j.Logger;

public class ChangeDefaultPageCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(ChangeDefaultPageCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        long moduleId = 0;
        if(module != null){
            moduleId = module.getModuleId();
        }

        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if(id < 0){
            LOGGER.error("Invalid Page Id");
            throw new IllegalArgumentException("Invalid Page Id");
        }

        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);

        PagesContext newDefaultPage = CustomPageAPI.getCustomPage(id);
        if(newDefaultPage == null){
            LOGGER.error("Page not found");
            throw new IllegalArgumentException("Page not found");
        }

        if(appId == null || appId <= 0 || appId != newDefaultPage.getAppId()){
            appId = newDefaultPage.getAppId();
        }

        if(moduleId <= 0 || moduleId != newDefaultPage.getModuleId()){
            moduleId = newDefaultPage.getModuleId();
        }

        double newSequenceNumber = 0;
        PagesContext existingDefaultPage = CustomPageAPI.getDefaultPage(appId, moduleId);
        if(existingDefaultPage != null) {

            existingDefaultPage.setIsDefaultPage(Boolean.FALSE);
            CustomPageAPI.updateFetchedPage(existingDefaultPage);

            newSequenceNumber = existingDefaultPage.getSequenceNumber() != -1? existingDefaultPage.getSequenceNumber() + 10 : 0;

            newDefaultPage.setSequenceNumber( newSequenceNumber);
            newDefaultPage.setIsDefaultPage(Boolean.TRUE);
            newDefaultPage.setStatus(Boolean.TRUE);
            CustomPageAPI.updateFetchedPage(newDefaultPage);
        }
        else{
            LOGGER.info("No Default Page Found");
        }

        return false;
    }
}
