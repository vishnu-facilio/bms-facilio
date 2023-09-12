package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
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
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


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

        Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        Boolean isTemplate = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_TEMPLATE,false);
        customPage.setIsTemplate(isTemplate);

        if(!customPage.getIsTemplate()) {
            ApplicationContext app = appId == null || appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
            if (app == null) {
                app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            }
            appId = app.getId();

            customPage.setAppId(appId);
            long pageCount = CustomPageAPI.getPageCountForModuleInApp(moduleId, appId);
            long maxLimitForPage = 6L;  // maximum page allowed is limited to 6 excluding default page
            if(pageCount >= maxLimitForPage) {
                throw new IllegalArgumentException("New page creation can't be permitted as maximum limit attained");
            }

            PagesContext defaultPage = CustomPageAPI.getDefaultPage(appId, moduleId);
            Boolean isDefaultPage = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_DEFAULT_PAGE,false);
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

            double sequenceNumber = CustomPageAPI.getSequenceNumberForNewPage(defaultPage, appId, moduleId);
            customPage.setSequenceNumber(sequenceNumber);
            LOGGER.info("Sequence Number For Custom Page named --" + customPage.getDisplayName() + " is " + customPage.getSequenceNumber());
        }
        else {
            appId = -1L;
            customPage.setAppId(-1);
            customPage.setStatus(Boolean.FALSE);
            customPage.setStatus(isSystem != null && isSystem);
        }


        if (customPage.getCriteria() != null) {
            Criteria criteria = customPage.getCriteria();
            for (String key : criteria.getConditions().keySet()) {
                Condition condition = criteria.getConditions().get(key);
                if(condition.getField() == null && StringUtils.isEmpty(condition.getColumnName())) {
                    FacilioField field = modBean.getField(condition.getFieldName(), condition.getModuleName() != null ? condition.getModuleName() : moduleName);
                    condition.setField(field);
                }
            }
            long criteriaId = CriteriaAPI.addCriteria(criteria);
            customPage.setCriteriaId(criteriaId);
        }

        FacilioModule pagesModule = ModuleFactory.getPagesModule();
        FacilioField moduleIdField = FieldFactory.getNumberField("moduleId", "MODULEID", pagesModule);
        FacilioField appIdField = FieldFactory.getNumberField("appId", "APP_ID", pagesModule);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getEqualsCondition(appIdField,String.valueOf(appId)));
        criteria.addAndCondition(CriteriaAPI.getEqualsCondition(moduleIdField, String.valueOf(moduleId)));

        String name = StringUtils.isNotEmpty(customPage.getName()) ? customPage.getName() :
                StringUtils.isNotEmpty(customPage.getDisplayName())? customPage.getDisplayName(): "page";
        name = CustomPageAPI.getUniqueName(pagesModule, criteria, moduleIdField, moduleId, name, isSystem);
        if((isSystem != null && isSystem) && StringUtils.isNotEmpty(customPage.getName()) && !customPage.getName().equalsIgnoreCase(name)) {
            throw new IllegalArgumentException("linkName "+ customPage.getName()+" already exists, given linkName for customPage is invalid");
        }

        customPage.setIsSystemPage((isSystem != null && isSystem));
        customPage.setName(name);
        customPage.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
        customPage.setSysCreatedTime(System.currentTimeMillis());

        CustomPageAPI.insertCustomPageToDB(customPage);

        if(customPage.getIsTemplate() != null && customPage.getIsTemplate()) {
            FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(customPage.getAppDomainTypes()),"appDomains must be defined for template page");
            CustomPageAPI.insertTemplatePageAppDomains(customPage.getId(), customPage.getAppDomainTypes());
        }
        Map<String, Long> layoutMap = CustomPageAPI.createLayoutsForPage(customPage.getId());


        boolean isClonePage = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_CLONE_PAGE, false);
        if (!isClonePage && (isSystem == null || !isSystem) && (customPage.getIsTemplate() == null || !customPage.getIsTemplate())) {
            PagesUtil.cloneTemplateToPage(appId, moduleId, customPage.getId(), null);
//            PagesUtil.cloneTemplateToPage(appId, moduleId, customPage.getId(), PagesContext.PageLayoutType.MOBILE);
        }
        context.put(FacilioConstants.CustomPage.PAGE_ID,customPage.getId());
        context.put(FacilioConstants.CustomPage.LAYOUT_IDS, layoutMap);
        LOGGER.info("Custom page named -- "+customPage.getName()+" has been created");


        return false;
    }

}
