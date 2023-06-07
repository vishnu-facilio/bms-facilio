package com.facilio.bmsconsole.commands;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class GetCustomPageCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(GetCustomPageCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long pageId = (Long) context.get(FacilioConstants.CustomPage.PAGE_ID);
        PagesContext customPage = CustomPageAPI.getCustomPage(pageId);

        if(customPage == null) {
            LOGGER.error("Page does not exists");
            throw new IllegalArgumentException("Page does not exists");
        }

        if(customPage.getModuleId()!=-1) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String moduleName = modBean.getModule(customPage.getModuleId()).getName();
            customPage.setModuleName(moduleName);
        }

        if(customPage.getCriteriaId()!=-1 && customPage.getCriteria() == null) {
            Criteria criteria = CriteriaAPI.getCriteria(customPage.getCriteriaId());
            customPage.setCriteria(criteria);
        }

        if(!customPage.getName().contains("__c")) {
            customPage.setIsSystemPage(true);
        }

        boolean excludeTabs = (boolean) context.getOrDefault(FacilioConstants.CustomPage.EXCLUDE_TABS, false);
        if(excludeTabs) {
            customPage.setPageSharing(SharingAPI.getSharing(customPage.getId(), ModuleFactory.getPageSharingModule(),
                    SingleSharingContext.class, FieldFactory.getPageSharingFields()));
        }
        context.put(FacilioConstants.CustomPage.CUSTOM_PAGE,customPage);
        return false;
    }
}