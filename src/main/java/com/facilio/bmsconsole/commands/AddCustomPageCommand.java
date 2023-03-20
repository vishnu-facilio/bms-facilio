package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.Map;
import java.util.Objects;
public class AddCustomPageCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(AddCustomPageCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        PagesContext customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);
        if(customPage == null){
            LOGGER.error("Custom page cannot be null, so unable to create");
            throw new IllegalArgumentException("Custom page cannot be null");
        }

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Long moduleId;
        if (customPage.getModuleId() == -1) {

            if(moduleName == null || moduleName.isEmpty()){
                LOGGER.error("Module does not exist");
                throw new IllegalArgumentException("Module does not exist");
            }

            FacilioModule module = modBean.getModule(moduleName);
            moduleId = module.getModuleId();
            customPage.setModuleId(moduleId);
            context.put(FacilioConstants.ContextNames.MODULE_ID, moduleId);
        }
        else {
            moduleId = customPage.getModuleId();
        }

        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        ApplicationContext app = appId == null || appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
        if (app == null) {
            app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        appId = app.getId();
        customPage.setAppId(appId);

        Boolean isTemplate = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_TEMPLATE,false);
        customPage.setIsTemplate(isTemplate);

        PagesContext defaultPage = CustomPageAPI.getDefaultPage(appId, moduleId);
        Boolean isDefaultPage = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_DEFAULT_PAGE,false);

        customPage.setStatus(Boolean.FALSE);

        if(!isTemplate) {
            if (defaultPage == null && !isDefaultPage) {
                isDefaultPage = true;
            }

            if (isDefaultPage) {
                if (defaultPage != null) {
                    LOGGER.error("Only one default page is allowed per module so another one cannot be created");
                    isDefaultPage = false;
                }
                else {
                    customPage.setStatus(Boolean.TRUE);
                }
            }
            customPage.setIsDefaultPage(isDefaultPage);
        }
        else{
            customPage.setStatus(Boolean.FALSE);
            customPage.setIsDefaultPage(Boolean.FALSE);
        }


        if (customPage.getCriteria() != null) {
            Criteria criteria = customPage.getCriteria();
            criteria.validatePattern();
            for (String key : criteria.getConditions().keySet()) {
                Condition condition = criteria.getConditions().get(key);
                FacilioField field = modBean.getField(condition.getFieldName(), moduleName);
                condition.setField(field);
            }
            long criteriaId = CriteriaAPI.addCriteria(criteria, Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId());
            customPage.setCriteriaId(criteriaId);
        }

        FacilioModule pagesModule = ModuleFactory.getPagesModule();
        FacilioField moduleIdField = FieldFactory.getNumberField("moduleId", "MODULEID", pagesModule);
        FacilioField appIdField = FieldFactory.getNumberField("appId", "APP_ID", pagesModule);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getEqualsCondition(appIdField,String.valueOf(appId)));
        criteria.addAndCondition(CriteriaAPI.getEqualsCondition(moduleIdField, String.valueOf(moduleId)));

        if(!customPage.getIsTemplate()) {
            double sequenceNumber = CustomPageAPI.getSequenceNumberForNewPage(defaultPage, appId, moduleId);
            customPage.setSequenceNumber(sequenceNumber);
            LOGGER.info("Sequence Number For Custom Page named --" + customPage.getDisplayName() + " is " + customPage.getSequenceNumber());
        }

        String name = customPage.getDisplayName() != null ? customPage.getDisplayName() : "page";
        name = CustomPageAPI.getUniqueName(pagesModule, criteria, moduleIdField, moduleId, name);

        customPage.setName(name);
        customPage.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
        customPage.setSysCreatedTime(System.currentTimeMillis());

        customPage = CustomPageAPI.insertCustomPageToDB(customPage);
        context.put(FacilioConstants.CustomPage.PAGE_ID,customPage.getId());
        LOGGER.info("Custom page named --"+customPage.getName()+" has been created");


        return false;
    }

}
