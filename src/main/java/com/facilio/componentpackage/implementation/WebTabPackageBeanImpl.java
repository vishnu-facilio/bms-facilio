package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class WebTabPackageBeanImpl implements PackageBean<WebTabContext> {
    // TODO - Handle WEb Tab Permission
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getWebTabIdVsAppId();
    }

    @Override
    public Map<Long, WebTabContext> fetchComponents(List<Long> ids) throws Exception {
        List<WebTabContext> webTabs = getWebTabs(ids);

        Map<Long, WebTabContext> tabIdVsTab = new HashMap<>();
        if (CollectionUtils.isNotEmpty(webTabs)) {
            webTabs.forEach(tab -> tabIdVsTab.put(tab.getId(), tab));
        }

        return tabIdVsTab;
    }

    @Override
    public void convertToXMLComponent(WebTabContext component, XMLBuilder webTabElement) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        long appId = component.getApplicationId();
        ApplicationContext application = ApplicationApi.getApplicationForId(appId);

        webTabElement.element(PackageConstants.NAME).text(component.getName());
        webTabElement.element(PackageConstants.AppXMLConstants.ROUTE).text(component.getRoute());
        webTabElement.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(application.getLinkName());
        webTabElement.element(PackageConstants.AppXMLConstants.TAB_TYPE).text(component.getTypeEnum().name());
        webTabElement.element(PackageConstants.AppXMLConstants.CONFIG).text(String.valueOf(component.getConfig()));
        webTabElement.element(PackageConstants.AppXMLConstants.ICON_TYPE).text(String.valueOf(component.getIconType()));
        if (component.getIconTypeEnum() != null) {
            webTabElement.element(PackageConstants.AppXMLConstants.ICON_TYPE_ENUM).text(component.getIconTypeEnum().name());
        } else {
            webTabElement.element(PackageConstants.AppXMLConstants.ICON_TYPE_ENUM).text("");
        }
        webTabElement.element(PackageConstants.AppXMLConstants.FEATURE_LICENSE).text(String.valueOf(component.getFeatureLicense()));

        if (CollectionUtils.isNotEmpty(component.getModuleIds())) {
            Criteria moduleIdCriteria = new Criteria();
            moduleIdCriteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", StringUtils.join(component.getModuleIds(), ","), NumberOperators.EQUALS));

            List<FacilioModule> moduleList = moduleBean.getModuleList(moduleIdCriteria);
            if (CollectionUtils.isNotEmpty(moduleList)) {
                XMLBuilder modulesListElement = webTabElement.element(PackageConstants.AppXMLConstants.MODULE_LIST);
                for (FacilioModule module : moduleList) {
                    modulesListElement.element("value").text(module.getName());
                }
            }
        }

        if (CollectionUtils.isNotEmpty(component.getSpecialTypeModules())) {
            XMLBuilder splModulesElement = webTabElement.element(PackageConstants.AppXMLConstants.SPEICAL_MODULES);
            for (String splModuleName : component.getSpecialTypeModules()) {
                splModulesElement.element("value").text(splModuleName);
            }
        }

        List<Map<String, Object>> tabGroupsForTabId = ApplicationApi.getTabGroupsForTabId(component);
        if (CollectionUtils.isNotEmpty(tabGroupsForTabId)) {
            XMLBuilder webTabGroupsListElement = webTabElement.element(PackageConstants.AppXMLConstants.WEBTAB_GROUPS_LIST);
            for (Map<String, Object> tabGroup : tabGroupsForTabId) {
                XMLBuilder webTabGroupElement = webTabGroupsListElement.element(PackageConstants.AppXMLConstants.WEBTAB_GROUP);
                webTabGroupElement.element(PackageConstants.AppXMLConstants.ROUTE).text(String.valueOf(tabGroup.get("route")));
                webTabGroupElement.element(PackageConstants.AppXMLConstants.TAB_ORDER).text(String.valueOf(tabGroup.get("order")));

                int layoutDeviceTypeInt = Integer.parseInt(String.valueOf(tabGroup.get("layoutDeviceType")));
                ApplicationLayoutContext.LayoutDeviceType layoutDeviceType = ApplicationLayoutContext.LayoutDeviceType.valueOf(layoutDeviceTypeInt);

                XMLBuilder applicationLayoutElement = webTabGroupElement.element(PackageConstants.AppXMLConstants.LAYOUT);
                applicationLayoutElement.element(PackageConstants.AppXMLConstants.DEVICE_TYPE).text(layoutDeviceType.name());
                applicationLayoutElement.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(application.getLinkName());
                applicationLayoutElement.element(PackageConstants.AppXMLConstants.APP_TYPE).text(String.valueOf(tabGroup.get("appType")));
            }
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        WebTabContext webTabContext;
        long webTabId;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder webTabElement = idVsData.getValue();
            webTabContext = getWebTabContextFromXMLBuilder(webTabElement, appNameVsAppId, true);

            webTabId = ApplicationApi.getTabIdForRoute(webTabContext);

            if (webTabId > 0) {
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), webTabId);
            }
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        Map<String, Long> webTabGroupUidVsCompIdMap = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.WEBTAB_GROUP);

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        WebTabContext webTabContext;
        long webTabId;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder webTabElement = idVsData.getValue();
            webTabContext = getWebTabContextFromXMLBuilder(webTabElement, appNameVsAppId, false);

            webTabId = ApplicationApi.getTabIdForRoute(webTabContext);
            if (webTabId > 0) {
                webTabContext.setId(webTabId);
            }

            webTabId = addOrUpdateTab(webTabContext);

            uniqueIdentifierVsComponentId.put(idVsData.getKey(), webTabId);

            XMLBuilder webTabGroupElements = webTabElement.getElement(PackageConstants.AppXMLConstants.WEBTAB_GROUPS_LIST);
            if (webTabGroupElements != null) {
                List<XMLBuilder> tabGroupElementsElementList = webTabGroupElements.getElementList(PackageConstants.AppXMLConstants.WEBTAB_GROUP);
                List<WebtabWebgroupContext> webTabWebGroupContextList = getWebTabWebGroupContextFromBuilder(tabGroupElementsElementList, appNameVsAppId, webTabId);

                associateTabsAndGroup(webTabId, webTabWebGroupContextList, webTabGroupUidVsCompIdMap.values());
            }
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        Map<String, Long> webTabGroupUidVsCompIdMap = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.WEBTAB_GROUP);
        WebTabContext webTabContext;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long webTabId = idVsData.getKey();
            XMLBuilder webTabElement = idVsData.getValue();
            webTabContext = getWebTabContextFromXMLBuilder(webTabElement, appNameVsAppId, false);
            webTabContext.setId(idVsData.getKey());
            addOrUpdateTab(webTabContext);

            XMLBuilder webTabGroupElements = webTabElement.getElement(PackageConstants.AppXMLConstants.WEBTAB_GROUPS_LIST);

            if (webTabGroupElements != null) {
                List<XMLBuilder> tabGroupElementsElementList = webTabGroupElements.getElementList(PackageConstants.AppXMLConstants.WEBTAB_GROUP);
                List<WebtabWebgroupContext> webTabWebGroupContextList = getWebTabWebGroupContextFromBuilder(tabGroupElementsElementList, appNameVsAppId, webTabId);

                associateTabsAndGroup(webTabId, webTabWebGroupContextList, webTabGroupUidVsCompIdMap.values());
            }
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
        for (long id : ids) {
            tabBean.deleteTab(id);
        }
    }

    private Map<Long, Long> getWebTabIdVsAppId() throws Exception {
        FacilioModule facilioModule = ModuleFactory.getWebTabModule();
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(facilioModule));
            add(FieldFactory.getNumberField("applicationId", "APPLICATION_ID", facilioModule));
        }};

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(facilioModule.getTableName())
                .select(selectableFields);

        List<Map<String, Object>> propsList = builder.get();

        Map<Long, Long> webTabIdVsAppId = null;
        if (CollectionUtils.isNotEmpty(propsList)) {
            long tabId, applicationId;
            webTabIdVsAppId = new HashMap<>();
            for (Map<String, Object> prop : propsList) {
                tabId = prop.containsKey("id") ? (Long) prop.get("id") : -1;
                applicationId = prop.containsKey("applicationId") ? (Long) prop.get("applicationId") : -1;
                webTabIdVsAppId.put(tabId, applicationId);
            }
        }

        return webTabIdVsAppId;
    }

    private List<WebTabContext> getWebTabs(List<Long> ids) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName())
                .select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getWebTabModule()));

        List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);

        if (CollectionUtils.isNotEmpty(webTabs)) {
            for (WebTabContext webTabContext : webTabs) {
                List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = ApplicationApi.getTabIdModules(webTabContext.getId());

                List<Long> moduleIds = new ArrayList<>();
                List<String> specialTypes = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(tabIdAppIdMappingContextList)) {
                    for (TabIdAppIdMappingContext tabIdAppIdMappingContext : tabIdAppIdMappingContextList) {
                        if (tabIdAppIdMappingContext.getModuleId() > 0) {
                            moduleIds.add(tabIdAppIdMappingContext.getModuleId());
                        }
                        if (tabIdAppIdMappingContext.getSpecialType() != null && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("null")
                                && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("")) {
                            specialTypes.add(tabIdAppIdMappingContext.getSpecialType());
                        }
                    }
                }
                webTabContext.setModuleIds(moduleIds);
                webTabContext.setSpecialTypeModules(specialTypes);
            }
        }

        return webTabs;
    }

    private WebTabContext getWebTabContextFromXMLBuilder(XMLBuilder webTabElement, Map<String, Long> appNameVsAppId, boolean getBasicDetails) throws Exception {
        String name, route, config, iconTypeString, appLinkName, typeString;
        int iconTypeInt, featureLicense;
        WebTabContext webTabContext;
        WebTabContext.Type type;
        long applicationId;
        IconType iconType;

        name = webTabElement.getElement(PackageConstants.NAME).getText();
        route = webTabElement.getElement(PackageConstants.AppXMLConstants.ROUTE).getText();
        config = webTabElement.getElement(PackageConstants.AppXMLConstants.CONFIG).getText();
        typeString = webTabElement.getElement(PackageConstants.AppXMLConstants.TAB_TYPE).getText();
        appLinkName = webTabElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();
        iconTypeString = webTabElement.getElement(PackageConstants.AppXMLConstants.ICON_TYPE_ENUM).getText();
        iconTypeInt = Integer.parseInt(webTabElement.getElement(PackageConstants.AppXMLConstants.ICON_TYPE).getText());
        featureLicense = Integer.parseInt(webTabElement.getElement(PackageConstants.AppXMLConstants.FEATURE_LICENSE).getText());

        applicationId = appNameVsAppId.containsKey(appLinkName) ? appNameVsAppId.get(appLinkName) : -1;
        iconType = StringUtils.isNotEmpty(iconTypeString) ? IconType.valueOf(iconTypeString) : null;
        type = WebTabContext.Type.valueOf(typeString);

        webTabContext = new WebTabContext(name, route, type, null, config, featureLicense, null, applicationId);
        webTabContext.setIconTypeEnum(iconType);
        webTabContext.setIconType(iconTypeInt);

        if (!getBasicDetails) {
            XMLBuilder specialModulesBuilder = webTabElement.getElement(PackageConstants.AppXMLConstants.SPEICAL_MODULES);
            if (specialModulesBuilder != null) {
                webTabContext.setSpecialTypeModules(new ArrayList<>());
                List<XMLBuilder> specialModulesBuilderList = specialModulesBuilder.getElementList("value");
                for (XMLBuilder specialModuleBuilder : specialModulesBuilderList) {
                    String splModuleName = specialModuleBuilder.getText();
                    webTabContext.getSpecialTypeModules().add(splModuleName);
                }
            }

            XMLBuilder modulesBuilder = webTabElement.getElement(PackageConstants.AppXMLConstants.MODULE_LIST);
            if (modulesBuilder != null) {
                ModuleBean moduleBean = Constants.getModBean();
                webTabContext.setModuleIds(new ArrayList<>());
                webTabContext.setModules(new ArrayList<>());
                List<XMLBuilder> modulesBuilderList = modulesBuilder.getElementList("value");
                for (XMLBuilder moduleBuilder : modulesBuilderList) {
                    String moduleName = moduleBuilder.getText();
                    FacilioModule module = moduleBean.getModule(moduleName);
                    webTabContext.getModuleIds().add(module.getModuleId());
                    webTabContext.getModules().add(module);
                }
            }
        }

        return webTabContext;
    }

    private List<WebtabWebgroupContext> getWebTabWebGroupContextFromBuilder(List<XMLBuilder> tabGroupElementsElementList, Map<String, Long> appNameVsAppId, long webTabId) throws Exception {
        List<WebtabWebgroupContext> webtabWebgroupContextList = new ArrayList<>();

        ApplicationLayoutContext.LayoutDeviceType layoutDeviceType;
        String appLinkName, groupRoute, appType, deviceTypeStr;
        WebtabWebgroupContext webtabWebgroupContext;
        WebTabGroupContext webTabGroupContext;
        int order;

        for (XMLBuilder tabGroupElement : tabGroupElementsElementList) {
            long applicationId, tabGroupId = -1, layoutId = -1;
            groupRoute = tabGroupElement.getElement(PackageConstants.AppXMLConstants.ROUTE).getText();
            order = Integer.parseInt(tabGroupElement.getElement(PackageConstants.AppXMLConstants.TAB_ORDER).getText());

            XMLBuilder layoutElement = tabGroupElement.getElement(PackageConstants.AppXMLConstants.LAYOUT);
            if (layoutElement != null) {
                appType = layoutElement.getElement(PackageConstants.AppXMLConstants.APP_TYPE).getText();
                deviceTypeStr = layoutElement.getElement(PackageConstants.AppXMLConstants.DEVICE_TYPE).getText();
                appLinkName = layoutElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();

                layoutDeviceType = ApplicationLayoutContext.LayoutDeviceType.valueOf(deviceTypeStr);
                applicationId = appNameVsAppId.containsKey(appLinkName) ? appNameVsAppId.get(appLinkName) : -1;

                ApplicationLayoutContext layoutsForAppLayoutType = ApplicationApi.getLayoutForAppTypeDeviceType(applicationId, appType, layoutDeviceType.getIndex());
                if (layoutsForAppLayoutType != null) {
                    layoutId = layoutsForAppLayoutType.getId();
                }
            }

            if (layoutId > 0) {
                webTabGroupContext = new WebTabGroupContext();
                webTabGroupContext.setLayoutId(layoutId);
                webTabGroupContext.setRoute(groupRoute);

                tabGroupId = ApplicationApi.getTabGroupIdForRoute(webTabGroupContext);
            }

            if (tabGroupId > 0) {
                webtabWebgroupContext = new WebtabWebgroupContext();
                webtabWebgroupContext.setWebTabGroupId(tabGroupId);
                webtabWebgroupContext.setWebTabId(webTabId);
                webtabWebgroupContext.setOrder(order);

                webtabWebgroupContextList.add(webtabWebgroupContext);
            }
        }
        return webtabWebgroupContextList;
    }

    private long addOrUpdateTab(WebTabContext webTabContext) throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
        context.put(FacilioConstants.ContextNames.NEW_PERMISSIONS, webTabContext.getPermissions());
        chain.execute();

        webTabContext = (WebTabContext) context.get(FacilioConstants.ContextNames.WEB_TAB);
        long tabId = webTabContext.getId();

        return tabId;
    }

    private void associateTabsAndGroup(long webTabId, List<WebtabWebgroupContext> webTabWebGroupContexts, Collection<Long> webTabGroupIds) throws Exception {
        FacilioChain createAndAssociateTabGroupChain = TransactionChainFactory.getUpdateWebTabWebGroupChain();

        FacilioContext chainContext = createAndAssociateTabGroupChain.getContext();
        chainContext.put(FacilioConstants.ContextNames.WEB_TAB_ID, webTabId);
        chainContext.put(PackageConstants.AppXMLConstants.WEB_TAB_GROUP_IDS, webTabGroupIds);
        chainContext.put(FacilioConstants.ContextNames.WEB_TAB_WEB_GROUP, webTabWebGroupContexts);

        createAndAssociateTabGroupChain.execute();
    }
}
