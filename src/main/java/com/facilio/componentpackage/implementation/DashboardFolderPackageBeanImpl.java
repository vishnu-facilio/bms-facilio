package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DashboardFolderContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.constants.PackageConstants.DashboardConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashboardFolderPackageBeanImpl implements PackageBean<DashboardFolderContext> {

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long,Long> dashboardFolderIds = new HashMap<>();
        FacilioModule module = ModuleFactory.getDashboardFolderModule();
        List<Map<String, Object>> props = DashboardUtil.getIdAndLinkNameAsProp(module);
        if(CollectionUtils.isNotEmpty(props)) {
            props.forEach(prop -> dashboardFolderIds.put((Long) prop.get(PackageConstants.DashboardConstants.ID),-1L));
        }
        return dashboardFolderIds;
    }


    @Override
    public Map<Long, DashboardFolderContext> fetchComponents(List<Long> ids) throws Exception {
        return DashboardUtil.getDashboardFolderWithIds(ids);
    }

    @Override
    public void convertToXMLComponent(DashboardFolderContext component, XMLBuilder dashboardFolderElement) throws Exception {
        dashboardFolderElement.element(DashboardConstants.NAME).text(component.getName());
        dashboardFolderElement.element(DashboardConstants.APP_NAME).text(component.getAppName());
        dashboardFolderElement.element(DashboardConstants.MODULE_NAME).text(String.valueOf(component.getModuleName()));
        dashboardFolderElement.element(DashboardConstants.DISPLAY_ORDER).text(String.valueOf(component.getDisplayOrder()));
        dashboardFolderElement.element(DashboardConstants.LINK_NAME).text(component.getLinkName());
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
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        Map<String,Long> uniqueIdentifierVsComponentId = new HashMap<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()){
            XMLBuilder dashboardFolderElement = idVsData.getValue();
            DashboardFolderContext folderContext = createFolderContext(dashboardFolderElement,appNameVsAppId);
            FacilioChain chain = TransactionChainFactory.getAddDashboardFolderChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.DASHBOARD_FOLDER, folderContext);
            chain.execute(context);
            DashboardFolderContext dashboardFolderContext = (DashboardFolderContext) context.get(FacilioConstants.ContextNames.DASHBOARD_FOLDER);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(),dashboardFolderContext.getId());
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        for(Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()){
            XMLBuilder dashboardFolderElement = idVsComponent.getValue();
            DashboardFolderContext folderContext = createFolderContext(dashboardFolderElement,appNameVsAppId);
            folderContext.setId(idVsComponent.getKey());
            FacilioChain chain = TransactionChainFactory.getUpdateDashboardFolderChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.DASHBOARD_FOLDERS, Collections.singletonList(folderContext));
            chain.execute();
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        FacilioModule module = ModuleFactory.getDashboardFolderModule();
        DashboardUtil.deleteRecords(ids,module);
    }
    private DashboardFolderContext createFolderContext(XMLBuilder dashboardFolderElement,Map<String,Long> appNameVsAppId) throws Exception {
        String name,moduleName,appName,linkName;
        long appId,moduleId=-1,displayOrder;
        name = dashboardFolderElement.getElement(DashboardConstants.NAME).getText();
        moduleName = dashboardFolderElement.getElement(DashboardConstants.MODULE_NAME).getText();
        appName = dashboardFolderElement.getElement(DashboardConstants.APP_NAME).getText();
        displayOrder = Long.parseLong(dashboardFolderElement.getElement(DashboardConstants.DISPLAY_ORDER).getText());
        linkName = dashboardFolderElement.getElement(DashboardConstants.LINK_NAME).getText();
        DashboardFolderContext folderContext = new DashboardFolderContext();
        appId = appNameVsAppId.get(appName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if(module !=null){
            moduleId = module.getModuleId();
        }
        folderContext.setName(name);
        folderContext.setAppId(appId);
        folderContext.setModuleId(moduleId);
        folderContext.setDisplayOrder((int) displayOrder);
        folderContext.setLinkName(linkName);
        return folderContext;
    }
}
