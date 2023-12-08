package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections.CollectionUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReportFolderPackageBeanImpl implements PackageBean<ReportFolderContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long,Long> reportFolderIds = new HashMap<>();
        FacilioModule module = ModuleFactory.getReportFolderModule();
        List<Map<String, Object>> props = DashboardUtil.getIdAndLinkNameAsProp(module);
        if(CollectionUtils.isNotEmpty(props)) {
            props.forEach(prop -> reportFolderIds.put((Long) prop.get(PackageConstants.DashboardConstants.ID),-1L));
        }
        return reportFolderIds;
    }

    @Override
    public Map<Long, ReportFolderContext> fetchComponents(List<Long> ids) throws Exception {
        return ReportUtil.getReportFolderWithIds(ids);
    }

    @Override
    public void convertToXMLComponent(ReportFolderContext component, XMLBuilder reportFolderElement) throws Exception {
        reportFolderElement.element(PackageConstants.DashboardConstants.NAME).text(component.getName());
        reportFolderElement.element(PackageConstants.DashboardConstants.APP_NAME).text(component.getAppName());
        reportFolderElement.element(PackageConstants.DashboardConstants.MODULE_NAME).text(String.valueOf(component.getModuleName()));
        reportFolderElement.element(PackageConstants.DashboardConstants.MODIFIED_TIME).text(String.valueOf(component.getModifiedTime()));
        reportFolderElement.element(PackageConstants.DashboardConstants.LINK_NAME).text(component.getLinkName());
        if(component.getFolderType()>0){
            reportFolderElement.element(PackageConstants.ReportsConstants.FOLDER_TYPE).text(String.valueOf(component.getFolderType()));
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
            XMLBuilder reportFolderElement = idVsData.getValue();
            ReportFolderContext folderContext = createFolderContext(reportFolderElement);
            FacilioChain chain = TransactionChainFactoryV3.getCreateReportFolderChain();
            FacilioContext context = chain.getContext();
            String moduleName = reportFolderElement.getElement(PackageConstants.DashboardConstants.MODULE_NAME).getText();
            context.put(PackageConstants.ReportsConstants.REPORT_FOLDER, folderContext);
            context.put(PackageConstants.ReportsConstants.ACTION_TYPE,PackageConstants.ReportsConstants.ADD);
            context.put(PackageConstants.MODULENAME,moduleName);
            chain.execute(context);
            ReportFolderContext reportFolderContext = (ReportFolderContext) context.get(PackageConstants.ReportsConstants.REPORT_FOLDER);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(),reportFolderContext.getId());
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for(Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()){
            XMLBuilder reportFolderElement = idVsComponent.getValue();
            ReportFolderContext folderContext = createFolderContext(reportFolderElement);
            folderContext.setId(idVsComponent.getKey());
            FacilioChain chain = TransactionChainFactoryV3.getCreateReportFolderChain();
            FacilioContext context = chain.getContext();
            context.put(PackageConstants.ReportsConstants.REPORT_FOLDER, folderContext);
            context.put(PackageConstants.ReportsConstants.ACTION_TYPE,"UPDATE");
            chain.execute();
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for(Long id: ids){
            ReportFolderContext folderContext = new ReportFolderContext();
            folderContext.setId(id);
            FacilioChain chain = TransactionChainFactoryV3.getCreateReportFolderChain();
            FacilioContext context = chain.getContext();
            context.put(PackageConstants.ReportsConstants.REPORT_FOLDER, folderContext);
            context.put(PackageConstants.ReportsConstants.ACTION_TYPE,"DELETE");
            chain.execute();
        }
    }
    private ReportFolderContext createFolderContext(XMLBuilder reportFolderElement) throws Exception {
        String name,appName,linkName;
        long appId;
        ReportFolderContext folderContext = new ReportFolderContext();
        name = reportFolderElement.getElement(PackageConstants.DashboardConstants.NAME).getText();
        appName = reportFolderElement.getElement(PackageConstants.DashboardConstants.APP_NAME).getText();
        if(reportFolderElement.getElement(PackageConstants.ReportsConstants.FOLDER_TYPE)!=null){
            int folderType = Integer.parseInt(reportFolderElement.getElement(PackageConstants.ReportsConstants.FOLDER_TYPE).getText());
            folderContext.setFolderType(folderType);
        }
        linkName = reportFolderElement.getElement(PackageConstants.DashboardConstants.LINK_NAME).getText();
        appId = PackageBeanUtil.getAppNameVsAppId().get(appName);
        folderContext.setName(name);
        folderContext.setAppId(appId);
        folderContext.setLinkName(linkName);
        return folderContext;
    }
}
