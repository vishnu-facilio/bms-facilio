package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class GetPageForRecordCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(GetPageForRecordCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        if(recordId <= 0){
            LOGGER.error("Record Id can't be Null");
            throw new IllegalArgumentException("Record Id can't be null");
        }

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if(module == null){
            throw new IllegalArgumentException("Invalid Module");
        }

        FacilioContext recordContext = V3Util.getSummary(moduleName, Collections.singletonList(recordId));
        Map<String,Object> recordMap = (Map<String,Object>) recordContext.get("recordMap");

        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        ApplicationContext app = appId == null || appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
        if (app == null) {
            app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        appId = app.getId();

        PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.getOrDefault(FacilioConstants.CustomPage.LAYOUT_TYPE,
                PagesContext.PageLayoutType.WEB);
        List<PagesContext>  customPages = CustomPageAPI.getAllCustomPage(appId, module.getModuleId(), layoutType);
        boolean criteriaflag = false;

        if (CollectionUtils.isNotEmpty(customPages)) {

            for (PagesContext customPage : customPages) {

                if (customPage.getIsDefaultPage() || customPage.getCriteria() == null) {
                    context.put(FacilioConstants.CustomPage.PAGE_ID, customPage.getId());
                    context.put(FacilioConstants.CustomPage.CUSTOM_PAGE, customPage);
                    break;
                }
                else {
                    recordMap=FieldUtil.getAsProperties(CriteriaAPI.setLookupFieldsData(customPage.getCriteria(),recordMap));
                    criteriaflag = customPage.getCriteria().computePredicate(recordMap).evaluate(((ArrayList<ModuleBaseWithCustomFields>)recordMap.get(moduleName)).get(0));
                    if (criteriaflag) {
                        context.put(FacilioConstants.CustomPage.PAGE_ID, customPage.getId());
                        context.put(FacilioConstants.CustomPage.CUSTOM_PAGE, customPage);
                        break;
                    }
                }
            }
        }

        return false;
    }
}