package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.facilio.beans.WebTabBean;
import com.facilio.fw.BeanFactory;

import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppLayoutPackageBeanImpl implements PackageBean<ApplicationLayoutContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getAllLayoutIds(true);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getAllLayoutIds(false);
    }

    @Override
    public Map<Long, ApplicationLayoutContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, ApplicationLayoutContext> layoutIdVsLayout = new HashMap<>();

        if (CollectionUtils.isNotEmpty(ids)) {
            List<ApplicationLayoutContext> layouts = ApplicationApi.getLayouts(ids);
            if (CollectionUtils.isNotEmpty(layouts)) {
                layouts.forEach(layout -> layoutIdVsLayout.put(layout.getId(), layout));
            }
            return layoutIdVsLayout;
        }
        return null;
    }

    @Override
    public void convertToXMLComponent(ApplicationLayoutContext component, XMLBuilder applicationLayoutElement) throws Exception {
        long appId = component.getApplicationId();
        ApplicationContext application = ApplicationApi.getApplicationForId(appId);
        applicationLayoutElement.element(PackageConstants.AppXMLConstants.APP_TYPE).text(component.getAppType());
        applicationLayoutElement.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(application.getLinkName());
        applicationLayoutElement.element(PackageConstants.AppXMLConstants.LAYOUT_TYPE).text(component.getAppLayoutTypeEnum().name());
        applicationLayoutElement.element(PackageConstants.AppXMLConstants.DEVICE_TYPE).text(component.getLayoutDeviceTypeEnum().name());
        applicationLayoutElement.element(PackageConstants.AppXMLConstants.VERSION_NUMBER).text(String.valueOf(component.getVersionNumber()));
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
        Map<Long, Map<Integer, Long>> appIdVsDeviceIdVsLayoutId = PackageBeanUtil.getAllLayoutConfiguration();

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ApplicationLayoutContext layoutContext;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            long layoutId = -1;
            XMLBuilder layoutElement = idVsData.getValue();
            layoutContext = getLayoutFromXMLComponent(layoutElement, appNameVsAppId);

            long applicationId = layoutContext.getApplicationId();
            int layoutDeviceTypeInt = layoutContext.getLayoutDeviceType();

            if (MapUtils.isNotEmpty(appIdVsDeviceIdVsLayoutId) && appIdVsDeviceIdVsLayoutId.containsKey(applicationId)) {
                if (appIdVsDeviceIdVsLayoutId.get(applicationId).containsKey(layoutDeviceTypeInt)) {
                    layoutId = appIdVsDeviceIdVsLayoutId.get(applicationId).get(layoutDeviceTypeInt);
                }
            }

            if (layoutId > 0) {
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), layoutId);
            }
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        Map<Long, Map<Integer, Long>> appIdVsDeviceIdVsLayoutId = PackageBeanUtil.getAllLayoutConfiguration();

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ApplicationLayoutContext layoutContext;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            long layoutId = -1;
            XMLBuilder layoutElement = idVsData.getValue();
            layoutContext = getLayoutFromXMLComponent(layoutElement, appNameVsAppId);

            // check and add layoutContext
            long applicationId = layoutContext.getApplicationId();
            int layoutDeviceTypeInt = layoutContext.getLayoutDeviceType();

            if (MapUtils.isNotEmpty(appIdVsDeviceIdVsLayoutId) && appIdVsDeviceIdVsLayoutId.containsKey(applicationId)) {
                if (appIdVsDeviceIdVsLayoutId.get(applicationId).containsKey(layoutDeviceTypeInt)) {
                    layoutId = appIdVsDeviceIdVsLayoutId.get(applicationId).get(layoutDeviceTypeInt);
                }
            }

            if (layoutId < 0) {
                layoutId = ApplicationApi.getAddApplicationLayout(layoutContext);
            } else {
                layoutContext.setId(layoutId);
                updateApplicationLayoutCommand(layoutContext);
            }
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), layoutId);
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();

        ApplicationLayoutContext layoutContext;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long layoutId = idVsData.getKey();
            XMLBuilder layoutElement = idVsData.getValue();

            layoutContext = getLayoutFromXMLComponent(layoutElement, appNameVsAppId);
            layoutContext.setId(layoutId);

            updateApplicationLayoutCommand(layoutContext);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        if(!PackageUtil.isInstallThread()) {
            return;
        }
        List<WebTabContext> allWebTabs = ApplicationApi.getAllWebTabs();
        List<WebTabGroupContext> webTabGroups = ApplicationApi.getWebTabgroups();
        WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");

        Map<String, Long> webTabUidVsCompIdMap = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.WEBTAB);
        Map<String, Long> webTabGroupUidVsCompIdMap = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.WEBTAB_GROUP);

        if (CollectionUtils.isNotEmpty(allWebTabs) && MapUtils.isNotEmpty(webTabUidVsCompIdMap)) {
            List<Long> allWebTabIds = allWebTabs.stream().map(WebTabContext::getId).collect(Collectors.toList());
            allWebTabIds.removeAll(webTabUidVsCompIdMap.values());
            for (long id : allWebTabIds) {
                tabBean.deleteTab(id);
            }
        }

        if (CollectionUtils.isNotEmpty(webTabGroups) && MapUtils.isNotEmpty(webTabGroupUidVsCompIdMap)) {
            List<Long> allWebTabGroupIds = webTabGroups.stream().map(WebTabGroupContext::getId).collect(Collectors.toList());
            allWebTabGroupIds.removeAll(webTabGroupUidVsCompIdMap.values());
            for (long id : allWebTabGroupIds) {
                tabBean.deleteWebTabGroup(id);
            }
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }
    
    private Map<Long, Long> getAllLayoutIds(boolean fetchSystem) throws Exception {
        Map<Long, Long> layoutIdVsAppId = null;
        List<Long> applicationIds = ApplicationApi.getApplicationIds(fetchSystem, true);

        if (CollectionUtils.isNotEmpty(applicationIds)) {
            layoutIdVsAppId = new HashMap<>();

            for (long appId : applicationIds) {
                List<ApplicationLayoutContext> layoutsForAppId = ApplicationApi.getLayoutsForAppId(appId);
                for (ApplicationLayoutContext layoutContext : layoutsForAppId) {
                    layoutIdVsAppId.put(layoutContext.getId(), appId);
                }
            }
        }

        return layoutIdVsAppId;
    }
    
    private void updateApplicationLayoutCommand(ApplicationLayoutContext layoutContext) throws Exception {
        FacilioModule module = ModuleFactory.getApplicationLayoutModule();
        List<FacilioField> fields = FieldFactory.getApplicationLayoutFields();
        
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(layoutContext.getId(), module));
        
        builder.update(FieldUtil.getAsProperties(layoutContext));
    }

    private ApplicationLayoutContext getLayoutFromXMLComponent(XMLBuilder layoutElement, Map<String, Long> appNameVsAppId) {
        String appType, appLinkName, layoutTypeStr, deviceTypeStr;
        ApplicationLayoutContext.LayoutDeviceType layoutDeviceType;
        ApplicationLayoutContext.AppLayoutType layoutType;
        ApplicationLayoutContext layoutContext;
        long applicationId;
        int versionNumber;

        appType = layoutElement.getElement(PackageConstants.AppXMLConstants.APP_TYPE).getText();
        layoutTypeStr = layoutElement.getElement(PackageConstants.AppXMLConstants.LAYOUT_TYPE).getText();
        deviceTypeStr = layoutElement.getElement(PackageConstants.AppXMLConstants.DEVICE_TYPE).getText();
        appLinkName = layoutElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();
        versionNumber = Integer.parseInt(layoutElement.getElement(PackageConstants.AppXMLConstants.VERSION_NUMBER).getText());

        layoutType = ApplicationLayoutContext.AppLayoutType.valueOf(layoutTypeStr);
        layoutDeviceType = StringUtils.isNotEmpty(deviceTypeStr) ? ApplicationLayoutContext.LayoutDeviceType.valueOf(deviceTypeStr) : null;
        applicationId = appNameVsAppId.containsKey(appLinkName) ? appNameVsAppId.get(appLinkName) : -1;

        layoutContext = new ApplicationLayoutContext(applicationId, layoutType, layoutDeviceType, appType);
        layoutContext.setVersionNumber(versionNumber);

        return layoutContext;
    }
}
