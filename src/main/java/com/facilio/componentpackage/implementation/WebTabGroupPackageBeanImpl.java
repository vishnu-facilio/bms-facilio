package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.MapUtils;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.lang3.StringUtils;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.chain.FacilioContext;
import com.facilio.bmsconsole.context.*;
import com.facilio.chain.FacilioChain;

import java.util.*;

public class WebTabGroupPackageBeanImpl implements PackageBean<WebTabGroupContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getWebTabGroupIdVsLayoutId();
    }

    @Override
    public Map<Long, WebTabGroupContext> fetchComponents(List<Long> ids) throws Exception {
        List<WebTabGroupContext> webTabGroups = ApplicationApi.getWebTabGroups(ids);

        Map<Long, WebTabGroupContext> groupIdVsGroup = new HashMap<>();
        if (CollectionUtils.isNotEmpty(webTabGroups)) {
            webTabGroups.forEach(group -> groupIdVsGroup.put(group.getId(), group));
        }

        return groupIdVsGroup;
    }

    @Override
    public void convertToXMLComponent(WebTabGroupContext component, XMLBuilder webTabGroupElement) throws Exception {
        webTabGroupElement.element(PackageConstants.NAME).text(component.getName());
        webTabGroupElement.element(PackageConstants.AppXMLConstants.ROUTE).text(component.getRoute());
        webTabGroupElement.element(PackageConstants.AppXMLConstants.ICON_TYPE).text(String.valueOf(component.getIconType()));
        if (component.getIconTypeEnum() != null) {
            webTabGroupElement.element(PackageConstants.AppXMLConstants.ICON_TYPE_ENUM).text(component.getIconTypeEnum().name());
        } else {
            webTabGroupElement.element(PackageConstants.AppXMLConstants.ICON_TYPE_ENUM).text("");
        }
        webTabGroupElement.element(PackageConstants.AppXMLConstants.TABGROUP_ORDER).text(String.valueOf(component.getOrder()));
        webTabGroupElement.element(PackageConstants.AppXMLConstants.FEATURE_LICENSE).text(String.valueOf(component.getFeatureLicense()));

        List<ApplicationLayoutContext> layouts = ApplicationApi.getLayouts(Collections.singletonList(component.getLayoutId()));
        if (CollectionUtils.isNotEmpty(layouts)) {
            ApplicationLayoutContext layoutContext = layouts.get(0);
            ApplicationContext application = ApplicationApi.getApplicationForId(layoutContext.getApplicationId());

            XMLBuilder applicationLayoutElement = webTabGroupElement.element(PackageConstants.AppXMLConstants.LAYOUT);
            applicationLayoutElement.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(application.getLinkName());
            applicationLayoutElement.element(PackageConstants.AppXMLConstants.DEVICE_TYPE).text(layoutContext.getLayoutDeviceTypeEnum().name());
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
        Map<Long, Map<String, Long>> layoutIdVsRouteVsGroupId = PackageBeanUtil.getAllWebTabGroups();
        Map<Long, Map<Integer, Long>> appIdVsDeviceIdVsLayoutId = PackageBeanUtil.getAllLayoutConfiguration();

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        WebTabGroupContext webTabGroupContext;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            long webTabGroupId = -1, layoutId = -1;
            XMLBuilder tabGroupElement = idVsData.getValue();
            webTabGroupContext = getWebTabGroupContextFromXMLBuilder(tabGroupElement, appNameVsAppId, appIdVsDeviceIdVsLayoutId);

            layoutId = webTabGroupContext.getLayoutId();
            if (layoutId > 0) {
                if (MapUtils.isNotEmpty(layoutIdVsRouteVsGroupId) && layoutIdVsRouteVsGroupId.containsKey(layoutId)) {
                    if (layoutIdVsRouteVsGroupId.get(layoutId).containsKey(webTabGroupContext.getRoute())) {
                        webTabGroupId = layoutIdVsRouteVsGroupId.get(layoutId).get(webTabGroupContext.getRoute());
                    }
                }
            }

            if (webTabGroupId > 0) {
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), webTabGroupId);
            }
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        Map<Long, Map<String, Long>> layoutIdVsRouteVsGroupId = PackageBeanUtil.getAllWebTabGroups();
        Map<Long, Map<Integer, Long>> appIdVsDeviceIdVsLayoutId = PackageBeanUtil.getAllLayoutConfiguration();

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        WebTabGroupContext webTabGroupContext;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            long webTabGroupId = -1, layoutId = -1;
            XMLBuilder tabGroupElement = idVsData.getValue();
            webTabGroupContext = getWebTabGroupContextFromXMLBuilder(tabGroupElement, appNameVsAppId, appIdVsDeviceIdVsLayoutId);

            // check and add
            layoutId = webTabGroupContext.getLayoutId();
            if (layoutId > 0) {
                if (MapUtils.isNotEmpty(layoutIdVsRouteVsGroupId) && layoutIdVsRouteVsGroupId.containsKey(layoutId)) {
                    if (layoutIdVsRouteVsGroupId.get(layoutId).containsKey(webTabGroupContext.getRoute())) {
                        webTabGroupId = layoutIdVsRouteVsGroupId.get(layoutId).get(webTabGroupContext.getRoute());
                    }
                }
            }
            if (webTabGroupId > 0) {
                webTabGroupContext.setId(webTabGroupId);
            }

            webTabGroupId = addOrUpdateTabGroup(webTabGroupContext);

            uniqueIdentifierVsComponentId.put(idVsData.getKey(), webTabGroupId);
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        Map<Long, Map<Integer, Long>> appIdVsDeviceIdVsLayoutId = PackageBeanUtil.getAllLayoutConfiguration();
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        WebTabGroupContext webTabGroupContext;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            XMLBuilder tabGroupElement = idVsData.getValue();
            webTabGroupContext = getWebTabGroupContextFromXMLBuilder(tabGroupElement, appNameVsAppId, appIdVsDeviceIdVsLayoutId);
            webTabGroupContext.setId(idVsData.getKey());
            addOrUpdateTabGroup(webTabGroupContext);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (long groupId : ids) {
            deleteTabGroup(groupId);
        }
    }

    private Map<Long, Long> getWebTabGroupIdVsLayoutId() throws Exception {
        FacilioModule webTabGroupModule = ModuleFactory.getWebTabGroupModule();
        List<Long> applicationIds = ApplicationApi.getAllApplicationIds(true);

        String webTabGroupTableName = webTabGroupModule.getTableName();
        String applicationLayoutTableName = ModuleFactory.getApplicationLayoutModule().getTableName();

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(webTabGroupModule));
            add(FieldFactory.getNumberField("layoutId", "LAYOUT_ID", webTabGroupModule));
        }};

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(webTabGroupTableName)
                .select(selectableFields)
                .innerJoin(applicationLayoutTableName)
                .on(applicationLayoutTableName + ".ID = " + webTabGroupTableName + ".LAYOUT_ID")
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", StringUtils.join(applicationIds, ","), NumberOperators.EQUALS));

        List<Map<String, Object>> propsList = builder.get();

        Map<Long, Long> groupIdVsLayoutId = null;
        if (CollectionUtils.isNotEmpty(propsList)) {
            long groupId, layoutId;
            groupIdVsLayoutId = new HashMap<>();
            for (Map<String, Object> prop : propsList) {
                groupId = prop.containsKey("id") ? (Long) prop.get("id") : -1;
                layoutId = prop.containsKey("layoutId") ? (Long) prop.get("layoutId") : -1;
                groupIdVsLayoutId.put(groupId, layoutId);
            }
        }

        return groupIdVsLayoutId;
    }

    private WebTabGroupContext getWebTabGroupContextFromXMLBuilder(XMLBuilder tabGroupElement, Map<String, Long> appNameVsAppId, Map<Long, Map<Integer, Long>> appIdVsDeviceIdVsLayoutId) throws Exception {
        String name, route, iconTypeString, appType, appLinkName, deviceTypeStr;
        ApplicationLayoutContext.LayoutDeviceType layoutDeviceType;
        long featureLicense, layoutId = -1, applicationId;
        WebTabGroupContext webTabGroupContext;
        int order, iconTypeInt, layoutDeviceTypeInt;
        IconType iconType;

        name = tabGroupElement.getElement(PackageConstants.NAME).getText();
        route = tabGroupElement.getElement(PackageConstants.AppXMLConstants.ROUTE).getText();
        iconTypeString = tabGroupElement.getElement(PackageConstants.AppXMLConstants.ICON_TYPE_ENUM).getText();
        iconTypeInt = Integer.parseInt(tabGroupElement.getElement(PackageConstants.AppXMLConstants.ICON_TYPE).getText());
        order = Integer.parseInt(tabGroupElement.getElement(PackageConstants.AppXMLConstants.TABGROUP_ORDER).getText());
        featureLicense = Long.parseLong(tabGroupElement.getElement(PackageConstants.AppXMLConstants.FEATURE_LICENSE).getText());
        iconType = StringUtils.isNotEmpty(iconTypeString) ? IconType.valueOf(iconTypeString) : null;

        XMLBuilder layoutElement = tabGroupElement.getElement(PackageConstants.AppXMLConstants.LAYOUT);
        if (layoutElement != null) {
            deviceTypeStr = layoutElement.getElement(PackageConstants.AppXMLConstants.DEVICE_TYPE).getText();
            appLinkName = layoutElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();

            layoutDeviceType = StringUtils.isNotEmpty(deviceTypeStr) ? ApplicationLayoutContext.LayoutDeviceType.valueOf(deviceTypeStr) : null;
            applicationId = appNameVsAppId.containsKey(appLinkName) ? appNameVsAppId.get(appLinkName) : -1;
            layoutDeviceTypeInt = (layoutDeviceType != null) ? layoutDeviceType.getIndex() : 0;

            if (MapUtils.isNotEmpty(appIdVsDeviceIdVsLayoutId) && appIdVsDeviceIdVsLayoutId.containsKey(applicationId)) {
                if (appIdVsDeviceIdVsLayoutId.get(applicationId).containsKey(layoutDeviceTypeInt)) {
                    layoutId = appIdVsDeviceIdVsLayoutId.get(applicationId).get(layoutDeviceTypeInt);
                }
            }
        }

        webTabGroupContext = new WebTabGroupContext(null, name, route, iconTypeInt, order, featureLicense, layoutId, iconType);

        return webTabGroupContext;
    }

    private long addOrUpdateTabGroup(WebTabGroupContext webTabGroupContext) throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
        context.put(FacilioConstants.ContextNames.USE_ORDER_FROM_CONTEXT, true);
        chain.execute();

        webTabGroupContext = (WebTabGroupContext) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP);
        long groupId = webTabGroupContext.getId();
        return groupId;
    }

    private void deleteTabGroup(long groupId) throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteTabGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, groupId);
        chain.execute();
    }
}
