package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.constants.PackageConstants.DashboardConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.facilio.componentpackage.utils.PackageBeanUtil.getNewRecordIds;
import static com.facilio.v3.util.ChainUtil.getModule;

public class DashboardPackageBeanImpl implements PackageBean<DashboardContext> {

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long,Long> dashboardIds = new HashMap<>();
        FacilioModule module = ModuleFactory.getDashboardModule();
        List<Map<String, Object>> props = DashboardUtil.getIdAndLinkNameAsProp(module);
        if(CollectionUtils.isNotEmpty(props)) {
            props.forEach(prop -> dashboardIds.put((Long) prop.get(PackageConstants.DashboardConstants.ID),-1L));
        }
        return dashboardIds;
    }

    @Override
    public Map<Long, DashboardContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long,String> folderIdVsLinkName = DashboardUtil.getIdVsNameMap(ModuleFactory.getDashboardFolderModule());
        Map<Long,String> tabIdsVsLinkName = DashboardUtil.getIdVsNameMap(ModuleFactory.getDashboardTabModule());
        return DashboardUtil.getDashboardWithIds(ids,folderIdVsLinkName,tabIdsVsLinkName);
    }

    @Override
    public void convertToXMLComponent(DashboardContext component, XMLBuilder dashboardContextElement) throws Exception {


        dashboardContextElement.element(DashboardConstants.MODULE_NAME).text(component.getModuleName());
        dashboardContextElement.element(DashboardConstants.NAME).text(component.getDashboardName());
        dashboardContextElement.element(DashboardConstants.LINK_NAME).text(component.getLinkName());
        dashboardContextElement.element(DashboardConstants.FOLDER_LINK_NAME).text(component.getFolderLinkName());
        dashboardContextElement.element(DashboardConstants.DASHBOARD_URL).text(component.getDashboardUrl());
        if(component.getBaseSpaceId()!=null){
            dashboardContextElement.element(DashboardConstants.BASE_SPACE_ID).text(String.valueOf(component.getBaseSpaceId()));
        }
        if(component.getPublishStatus()!=null){
            dashboardContextElement.element(DashboardConstants.PUBLISH_STATUS).text(String.valueOf(component.getPublishStatus()));
        }
        if(component.getDisplayOrder()!=null){
            dashboardContextElement.element(DashboardConstants.DISPLAY_ORDER).text(String.valueOf(component.getDisplayOrder()));
        }
        dashboardContextElement.element(DashboardConstants.DASHBOARD_TAB_PLACEMENT).text(String.valueOf(component.getDashboardTabPlacement()));
        dashboardContextElement.element(DashboardConstants.DATE_OPERATOR).text(String.valueOf(component.getDateOperator()));
        dashboardContextElement.element(DashboardConstants.SHOW_HIDE_MOBILE).text(String.valueOf(component.getMobileEnabled()));
        dashboardContextElement.element(DashboardConstants.DATE_VALUE).text(component.getDateValue());
        dashboardContextElement.element(DashboardConstants.IS_TAB_ENABLED).text(String.valueOf(component.isTabEnabled()));
        dashboardContextElement.element(DashboardConstants.CLIENT_META_JSON).cData(component.getClientMetaJsonString());
        dashboardContextElement.element(DashboardConstants.LOCKED).text(String.valueOf(component.getLocked()));
        if(CollectionUtils.isNotEmpty(component.getNewSharingContext())){
            PackageBeanUtil.constructBuilderFromSharingContext(component.getNewSharingContext(), dashboardContextElement.element(DashboardConstants.DASHBOARD_SHARING));
        }
        if(component.getDashboardFilter() !=null){
            DashboardFilterContext filterContext = component.getDashboardFilter();
            createFilterAsXML(dashboardContextElement,filterContext);
        }

        if(component.getDashboardTabContexts()!=null){
            List<DashboardTabContext> tabs= component.getDashboardTabContexts();
            XMLBuilder tabElementsList  = dashboardContextElement.element(DashboardConstants.TAB_ELEMENTS_LIST);
            for(DashboardTabContext tab : tabs){
                XMLBuilder dashboardTabElement = tabElementsList.element(DashboardConstants.DASHBOARD_TAB);
                dashboardTabElement.element(DashboardConstants.NAME).text(tab.getName());
                if(tab.getSubTabLinkName()!=null){
                    dashboardTabElement.element(DashboardConstants.SUB_TAB_LINK_NAME).text(tab.getSubTabLinkName());
                }
                dashboardTabElement.element(DashboardConstants.LINK_NAME).text(tab.getLinkName());
                dashboardTabElement.element(DashboardConstants.SEQUENCE).text(String.valueOf(tab.getSequence()));
                if(tab.getDashboardFilter() !=null){
                    DashboardFilterContext filterContext = tab.getDashboardFilter();
                    createFilterAsXML(dashboardTabElement,filterContext);
                }
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
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String,Long> uniqueIdentifierVsComponentId = new HashMap<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()){
            XMLBuilder dashboardElement = idVsData.getValue();
            DashboardContext dashboard = createDashboardContext(dashboardElement,null);
            FacilioChain addDashboardChain =  TransactionChainFactoryV3.getAddOrUpdateDashboardAndTabChain();
            FacilioContext context = addDashboardChain.getContext();
            context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
            addDashboardChain.execute();
            DashboardContext dashboardContext = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(),dashboardContext.getId());
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        Map<String,Long> tabNameVsIds = DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardTabModule());
        Map<String,Long> filterNameVsIds = DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardFilterModule());
        for(Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()){
            XMLBuilder dashboardElement = idVsComponent.getValue();
            DashboardContext dashboard = createDashboardContext(dashboardElement,tabNameVsIds);
            dashboard.setId(idVsComponent.getKey());
            FacilioChain updateDashboardChain = TransactionChainFactoryV3.getAddOrUpdateDashboardAndTabChain();
            FacilioContext context = updateDashboardChain.getContext();
            context.put(DashboardConstants.FILTER_MAP,filterNameVsIds);
            context.put(DashboardConstants.TAB_MAP,tabNameVsIds);
            context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
            updateDashboardChain.execute();
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        List<FacilioField> updateField = new ArrayList<>();
        Map<String,FacilioField> tabFieldAsMap = FieldFactory.getAsMap(FieldFactory.getDashboardTabFields());
        updateField.add(tabFieldAsMap.get(DashboardConstants.DASHBOARD_TAB_ID));
        Map<String,Long> tabNameVsIds = DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardTabModule());

        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
        List<GenericUpdateRecordBuilder.BatchUpdateContext> dashboardBatchUpdateList = new ArrayList<>();
        for(Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()){
            XMLBuilder dashboardElement = idVsComponent.getValue();
            XMLBuilder tabElementsList = dashboardElement.getElement(DashboardConstants.TAB_ELEMENTS_LIST);
            if(tabElementsList!=null){
                List<XMLBuilder> tabElements = tabElementsList.getElementList(DashboardConstants.DASHBOARD_TAB);
                for(XMLBuilder tab : tabElements){
                    if(tab.getElement(DashboardConstants.SUB_TAB_LINK_NAME)!=null){
                        GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
                        updateVal.addWhereValue(tabFieldAsMap.get(DashboardConstants.ID).getName(),tabNameVsIds.get(tab.getElement(DashboardConstants.LINK_NAME).getText()));
                        updateVal.addUpdateValue(tabFieldAsMap.get(DashboardConstants.DASHBOARD_TAB_ID).getName(),tabNameVsIds.get(tab.getElement(DashboardConstants.SUB_TAB_LINK_NAME).getText()));
                        batchUpdateList.add(updateVal);
                    }
                }
            }
            if(dashboardElement.getElement(DashboardConstants.BASE_SPACE_ID)!=null){
                Long dashboardId = idVsComponent.getKey();
                Long baseSpaceId = Long.valueOf(dashboardElement.getElement(DashboardConstants.BASE_SPACE_ID).getText());
                List<Long> baseSpaceIdsList = Collections.singletonList(baseSpaceId);
                Map<Long,Long> baseSpaceIdMap = getNewRecordIds("basespace",baseSpaceIdsList);
                Long newBaseSpaceId = baseSpaceIdMap.get(baseSpaceId);
                GenericUpdateRecordBuilder.BatchUpdateContext updateDashboardValue = new GenericUpdateRecordBuilder.BatchUpdateContext();
                updateDashboardValue.addWhereValue("id",dashboardId);
                updateDashboardValue.addUpdateValue("baseSpaceId",newBaseSpaceId);
                dashboardBatchUpdateList.add(updateDashboardValue);
            }

        }
        if(CollectionUtils.isNotEmpty(batchUpdateList)){
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .fields(updateField)
                    .table(ModuleFactory.getDashboardTabModule().getTableName());
            updateRecordBuilder.batchUpdate(Collections.singletonList(tabFieldAsMap.get(DashboardConstants.ID)),batchUpdateList);
        }
        Map<String,FacilioField> dashboardFields = FieldFactory.getAsMap(FieldFactory.getDashboardFields());
        if(CollectionUtils.isNotEmpty(dashboardBatchUpdateList)){
            GenericUpdateRecordBuilder updateDashboardBuilder = new GenericUpdateRecordBuilder()
                    .fields(Collections.singletonList(dashboardFields.get("baseSpaceId")))
                    .table(ModuleFactory.getDashboardModule().getTableName());
            updateDashboardBuilder.batchUpdate(Collections.singletonList(dashboardFields.get("id")),dashboardBatchUpdateList);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for(Long id :ids){
            List<DashboardTabContext>  tabs = DashboardUtil.getDashboardTabs(id);
            if (CollectionUtils.isEmpty(tabs)) {
                for (DashboardTabContext tab : tabs){
                    Long tabId = tab.getId();
                    if(tabId>0) {
                        DashboardUtil.deleteDashboardTab(tabId);
                    }
                }
            }
            FacilioChain deleteDashboardChain = TransactionChainFactory.getDeleteDashboardChain();
            FacilioContext context = deleteDashboardChain.getContext();
            context.put(FacilioConstants.ContextNames.DASHBOARD_ID,id);
            deleteDashboardChain.execute();
        }
    }

    private DashboardContext createDashboardContext(XMLBuilder dashboardElement,Map<String,Long> tabNameVsIds) throws Exception {

        DashboardContext dashboard = new DashboardContext();

        String folderLinkName = dashboardElement.getElement(DashboardConstants.FOLDER_LINK_NAME).getText();
        Map<String,Long> linkNameVsFolderId = DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardFolderModule());
        Long dashboardFolderId = linkNameVsFolderId.get(folderLinkName);
        String moduleName = dashboardElement.getElement(DashboardConstants.MODULE_NAME).getText();
        FacilioModule module = getModule(moduleName);
        if(module !=null){
            long moduleId = module.getModuleId();
            dashboard.setModuleId(moduleId);
        }

        String dashboardName = dashboardElement.getElement(DashboardConstants.NAME).getText();
        if(dashboardElement.getElement(DashboardConstants.PUBLISH_STATUS)!=null){
            Integer publishStatus = Integer.parseInt(dashboardElement.getElement(DashboardConstants.PUBLISH_STATUS).getText());
            dashboard.setPublishStatus(publishStatus);
        }
        if(dashboardElement.getElement(DashboardConstants.DISPLAY_ORDER)!=null){
             Integer displayOrder = Integer.parseInt(dashboardElement.getElement(DashboardConstants.DISPLAY_ORDER).getText());
             dashboard.setDisplayOrder(displayOrder);
        }

        String dashboardUrl = dashboardElement.getElement(DashboardConstants.DASHBOARD_URL).getText();
        Boolean showHideMobile = Boolean.parseBoolean(dashboardElement.getElement(DashboardConstants.SHOW_HIDE_MOBILE).getText());
        int dateOperator = Integer.parseInt(dashboardElement.getElement(DashboardConstants.DATE_OPERATOR).getText());
        String dateValue = dashboardElement.getElement(DashboardConstants.DATE_VALUE).getText();
        Boolean isTabEnabled = Boolean.parseBoolean(dashboardElement.getElement(DashboardConstants.IS_TAB_ENABLED).getText());
        int dashboardTabPlacement = Integer.parseInt(dashboardElement.getElement(DashboardConstants.DASHBOARD_TAB_PLACEMENT).getText());
        String clientMetaJson = dashboardElement.getElement(DashboardConstants.CLIENT_META_JSON).getCData();
        Boolean locked = Boolean.parseBoolean(dashboardElement.getElement(DashboardConstants.LOCKED).getText());
        String linkName = dashboardElement.getElement(DashboardConstants.LINK_NAME).getText();
        XMLBuilder tabElementsList = dashboardElement.getElement(DashboardConstants.TAB_ELEMENTS_LIST);
        XMLBuilder timeLineFilterElement = dashboardElement.getChildElement(DashboardConstants.DASHBOARD_FILTER);
        XMLBuilder dashboardSharing = dashboardElement.getElement(DashboardConstants.DASHBOARD_SHARING);

        dashboard.setLinkName(linkName);
        dashboard.setDashboardFolderId(dashboardFolderId);
        dashboard.setDashboardName(dashboardName);
        dashboard.setDashboardUrl(dashboardUrl);
        dashboard.setMobileEnabled(showHideMobile);
        dashboard.setDateOperator(dateOperator);
        dashboard.setDateValue(dateValue);
        dashboard.setTabEnabled(isTabEnabled);
        dashboard.setDashboardTabPlacement(dashboardTabPlacement);
        dashboard.setClientMetaJsonString(clientMetaJson);
        dashboard.setLocked(locked);

        if(dashboardSharing!=null){
            SharingContext<SingleSharingContext> sharingContexts = PackageBeanUtil.constructSharingContextFromBuilder(dashboardSharing,SingleSharingContext.class);
            List<DashboardSharingContext> sharingContextList = constructSharingContext(sharingContexts);
            dashboard.setDashboardSharingContext(sharingContextList);
        }

        if(timeLineFilterElement!=null){
            DashboardFilterContext filterContext = createFilterContext(timeLineFilterElement);
            dashboard.setDashboardFilter(filterContext);
        }
        if(tabElementsList !=null){
            List<DashboardTabContext> tabContextList = new ArrayList<>();
            List<XMLBuilder> tabElements = tabElementsList.getElementList(DashboardConstants.DASHBOARD_TAB);
            for(XMLBuilder tab : tabElements){

                DashboardTabContext tabContext = createTabContext(tab, tabNameVsIds);
                tabContextList.add(tabContext);
                XMLBuilder filterElement = tab.getElement(DashboardConstants.DASHBOARD_FILTER);
                if(filterElement !=null){
                    DashboardFilterContext filterContext = createFilterContext(filterElement);
                    tabContext.setDashboardFilter(filterContext);
                }
            }
            dashboard.setDashboardTabContexts(tabContextList);
        }

        return dashboard;
    }

    public DashboardTabContext createTabContext(XMLBuilder dashboardTabElement,Map<String,Long> tabNameVsIds) throws Exception {

        DashboardTabContext tabContext = new DashboardTabContext();
        tabContext.setLinkName(dashboardTabElement.getElement(DashboardConstants.LINK_NAME).getText());
        if(tabNameVsIds!=null){
            long dashboardTabId = tabNameVsIds.get(dashboardTabElement.getElement(DashboardConstants.SUB_TAB_LINK_NAME).getText());
            tabContext.setDashboardTabId(dashboardTabId);
        }
        String name = dashboardTabElement.getElement(DashboardConstants.NAME).getText();
        tabContext.setName(name);
        tabContext.setSequence(Integer.parseInt(dashboardTabElement.getElement(DashboardConstants.SEQUENCE).getText()));
        return tabContext;
    }

    public DashboardFilterContext createFilterContext(XMLBuilder timeLineFilterElement) throws Exception {

        DashboardFilterContext dashboardFilterContext = new DashboardFilterContext();
        if(timeLineFilterElement.getElement(PackageConstants.DashboardConstants.DATE_VALUE)!=null){
            String dateValue = timeLineFilterElement.getElement(PackageConstants.DashboardConstants.DATE_VALUE).getText();
            dashboardFilterContext.setDateValue(dateValue);
        }

        String dateLabel = timeLineFilterElement.getElement(PackageConstants.DashboardConstants.DATE_LABEL).getText();
        if(timeLineFilterElement.getElement(PackageConstants.DashboardConstants.TIME_LINE_FILTER_ENABLE)!=null){
            Boolean timeLineFilterEnabled = Boolean.parseBoolean(timeLineFilterElement.getElement(PackageConstants.DashboardConstants.TIME_LINE_FILTER_ENABLE).getText());
            dashboardFilterContext.setIsTimelineFilterEnabled(timeLineFilterEnabled);
        }
        if(timeLineFilterElement.getElement(PackageConstants.DashboardConstants.WIDGET_TIMELINE_FILTER_STATUS)!=null){
            boolean widgetFilterStatus = Boolean.parseBoolean(timeLineFilterElement.getElement(PackageConstants.DashboardConstants.WIDGET_TIMELINE_FILTER_STATUS).getText());
            dashboardFilterContext.setHideFilterInsideWidgets(widgetFilterStatus);
        }
        if(timeLineFilterElement.getElement(DashboardConstants.LINK_NAME)!=null) {
            String linkName = timeLineFilterElement.getElement(DashboardConstants.LINK_NAME).getText();
            dashboardFilterContext.setLinkName(linkName);
        }
        long dateOperator = Long.parseLong(timeLineFilterElement.getElement(PackageConstants.DashboardConstants.DATE_OPERATOR).getText());

        dashboardFilterContext.setDateLabel(dateLabel);
        dashboardFilterContext.setDateOperator(dateOperator);
        return dashboardFilterContext;

    }
    public static List<DashboardSharingContext> constructSharingContext(SharingContext<SingleSharingContext> shareContext) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<DashboardSharingContext> dashboardSharingContextList = new ArrayList<>();
        for(SingleSharingContext shareObj: shareContext){
            Map<String,Object> prop = FieldUtil.getAsProperties(shareObj);
            NewDashboardSharingContext newShareObj = FieldUtil.getAsBeanFromMap(prop,NewDashboardSharingContext.class);
            DashboardSharingContext dashboardShareContext = new DashboardSharingContext();
            dashboardShareContext.setOrgUserId(newShareObj.getUserId());
            dashboardShareContext.setRoleId(newShareObj.getRoleId());
            dashboardShareContext.setGroupId(newShareObj.getGroupId());
            dashboardShareContext.setSharingType(newShareObj.getType());
            dashboardShareContext.setLocked(newShareObj.getLocked());
            dashboardSharingContextList.add(dashboardShareContext);
        }
        return dashboardSharingContextList;
    }

    public static void createFilterAsXML(XMLBuilder dashboardContextElement, DashboardFilterContext filterContext) throws Exception{
        XMLBuilder timeLineFilterElement =  dashboardContextElement.element(DashboardConstants.DASHBOARD_FILTER);
        if(filterContext.getDateValue()!=null){
            timeLineFilterElement.element(DashboardConstants.DATE_VALUE).text(filterContext.getDateValue());
        }
        timeLineFilterElement.element(DashboardConstants.WIDGET_TIMELINE_FILTER_STATUS).text(String.valueOf(filterContext.isHideFilterInsideWidgets()));
        timeLineFilterElement.element(DashboardConstants.TIME_LINE_FILTER_ENABLE).text(String.valueOf(filterContext.getIsTimelineFilterEnabled()));
        timeLineFilterElement.element(DashboardConstants.DATE_OPERATOR).text(String.valueOf(filterContext.getDateOperator()));
        timeLineFilterElement.element(DashboardConstants.DATE_LABEL).text(filterContext.getDateLabel());
        timeLineFilterElement.element(DashboardConstants.LINK_NAME).text(filterContext.getLinkName());
    }
}
