package com.facilio.componentpackage.implementation;

import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.bmsconsole.util.ApplicationApi;
import org.apache.commons.lang3.StringUtils;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.FieldUtil;

import java.util.*;


public class AppPackageBeanImpl implements PackageBean<ApplicationContext> {

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getAppIdVsParentId(true);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getAppIdVsParentId(false);
    }

    @Override
    public Map<Long, ApplicationContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, ApplicationContext> appIdVsApplicationMap = new HashMap<>();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getApplicationFields())
                .table(ModuleFactory.getApplicationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(ids, ","), NumberOperators.EQUALS));

        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(), ApplicationContext.class);

        if (CollectionUtils.isNotEmpty(applications)) {
            applications.forEach(application -> appIdVsApplicationMap.put(application.getId(), application));
        }

        return appIdVsApplicationMap;
    }

    @Override
    public void convertToXMLComponent(ApplicationContext component, XMLBuilder applicationElement) throws Exception {
        // TODO - Handle Scoping
        applicationElement.element(PackageConstants.AppXMLConstants.CONFIG).text(component.getConfig());
        applicationElement.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(component.getLinkName());
        applicationElement.element(PackageConstants.AppXMLConstants.APPLICATION_NAME).text(component.getName());
        applicationElement.element(PackageConstants.AppXMLConstants.DESCRIPTION).text(component.getDescription());
        applicationElement.element(PackageConstants.AppXMLConstants.IS_DEFAULT).text(String.valueOf(component.isDefault()));
        applicationElement.element(PackageConstants.AppXMLConstants.DOMAIN_TYPE).text(String.valueOf(component.getDomainType()));
        applicationElement.element(PackageConstants.AppXMLConstants.APP_CATEGORY).text(String.valueOf(component.getAppCategory()));
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        Map<String, String> appLinkNameVsErrorMessage = new HashMap<>();
        List<String> appLinkNames = new ArrayList<>();

        for (XMLBuilder appElement : components) {
            String appLinkName = appElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();
            appLinkNames.add(appLinkName);
        }

        List<ApplicationContext> applicationsForLinkNames = ApplicationApi.getApplicationForLinkNames(appLinkNames);

        if (CollectionUtils.isNotEmpty(applicationsForLinkNames)) {
            for (ApplicationContext application : applicationsForLinkNames) {
                appLinkNameVsErrorMessage.put(application.getName(), PackageConstants.AppXMLConstants.DUPLICATE_APPLICATION_ERROR);
            }
        }

        return appLinkNameVsErrorMessage;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsAppId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder applicationElement = idVsData.getValue();
            String appLinkName = applicationElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();
            boolean systemApp = Boolean.parseBoolean(applicationElement.getElement(PackageConstants.AppXMLConstants.IS_DEFAULT).getText());

            if (systemApp) {
                ApplicationContext application = ApplicationApi.getApplicationForLinkName(appLinkName);
                if (application != null) {
                    uniqueIdentifierVsAppId.put(uniqueIdentifier, application.getId());
                }
            }
        }
        return uniqueIdentifierVsAppId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        String displayName, linkName, config, description;
        int domainTYpe, appCategory;
        boolean isDefault;

        ApplicationContext applicationContext;
        long appId;
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder applicationElement = idVsData.getValue();
            config = applicationElement.getElement(PackageConstants.AppXMLConstants.CONFIG).getText();
            linkName = applicationElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();
            description = applicationElement.getElement(PackageConstants.AppXMLConstants.DESCRIPTION).getText();
            displayName= applicationElement.getElement(PackageConstants.AppXMLConstants.APPLICATION_NAME).getText();
            domainTYpe = Integer.parseInt(applicationElement.getElement(PackageConstants.AppXMLConstants.DOMAIN_TYPE).getText());
            appCategory = Integer.parseInt(applicationElement.getElement(PackageConstants.AppXMLConstants.APP_CATEGORY).getText());
            isDefault = Boolean.parseBoolean(applicationElement.getElement(PackageConstants.AppXMLConstants.IS_DEFAULT).getText());

            // While creating custom app from ui, layoutType is set to 2
            applicationContext = new ApplicationContext(-1, displayName, isDefault, domainTYpe, linkName, -1, description, appCategory);
            applicationContext.setConfig(config);

            appId = add(applicationContext);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), appId);
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> uniqueIdentifierVsComponents) throws Exception {
        String displayName, linkName, description;
        ApplicationContext applicationContext;
        long appId;

        for (Map.Entry<Long, XMLBuilder> idVsData : uniqueIdentifierVsComponents.entrySet()) {
            appId = idVsData.getKey();
            XMLBuilder applicationElement = idVsData.getValue();
            linkName = applicationElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();
            description = applicationElement.getElement(PackageConstants.AppXMLConstants.DESCRIPTION).getText();
            displayName= applicationElement.getElement(PackageConstants.AppXMLConstants.APPLICATION_NAME).getText();

            applicationContext = new ApplicationContext();
            applicationContext.setId(appId);
            applicationContext.setName(displayName);
            applicationContext.setLinkName(linkName);
            applicationContext.setDescription(description);

            update(applicationContext);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        // TODO - Find impact of deleting RELATED_APPLICATION_ID
    }

    public static Map<Long, Long> getAppIdVsParentId(boolean fetchSystem) throws Exception {
        List<Long> applicationIds = ApplicationApi.getAllApplicationIds(fetchSystem);

        Map<Long, Long> appIdVsParentId = new HashMap<>();
        for (Long appId : applicationIds) {
            appIdVsParentId.put(appId, -1L);
        }
        return appIdVsParentId;
    }

    public static long add(ApplicationContext application) throws Exception {
        ApplicationContext dbApplication = ApplicationApi.getApplicationForLinkName(application.getLinkName());
        if (dbApplication != null) {
            application.setId(dbApplication.getId());
            return dbApplication.getId();
        }

        Map<String, Object> props = FieldUtil.getAsProperties(application);
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .fields(FieldFactory.getApplicationFields());
        return builder.insert(props);
    }

    public static void update(ApplicationContext application) throws Exception {
        FacilioModule applicationModule = ModuleFactory.getApplicationModule();
        Map<String, Object> props = FieldUtil.getAsProperties(application);

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(applicationModule.getTableName())
                .fields(FieldFactory.getApplicationFields())
                .andCondition(CriteriaAPI.getIdCondition(application.getId(), applicationModule));

        builder.update(props);
    }
}
