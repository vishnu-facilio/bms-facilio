package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ServiceCatalogAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.ServiceCatalogApi;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceCatalogItemPackageImpl implements PackageBean<ServiceCatalogContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    private Map<Long, Long> getCatalogItemIdVsModuleId() throws Exception {
        Map<Long, Long> catalogIdVsModuleId = new HashMap<>();

        FacilioModule module = ModuleFactory.getServiceCatalogModule();
        List<FacilioField> fields = FieldFactory.getServiceCatalogFields();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields);

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                catalogIdVsModuleId.put((Long) prop.get("id"), -1L);
            }
        }

        return catalogIdVsModuleId;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getCatalogItemIdVsModuleId();
    }

    @Override
    public Map<Long, ServiceCatalogContext> fetchComponents(List<Long> ids) throws Exception {
        return getServiceCatalogIdVsContextMap(ids);
    }

    private Map<Long, ServiceCatalogContext> getServiceCatalogIdVsContextMap(List<Long> ids) throws Exception {
        Map<Long, ServiceCatalogContext> serviceCatalogContextMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ids)) {
            List<Map<String, Object>> catogoryList = ServiceCatalogApi.getServiceCatalogList(ids);

            for (Map<String, Object> categoryMap : catogoryList) {
                Long id = (Long) categoryMap.get("id");
                ServiceCatalogContext context = FieldUtil.getAsBeanFromMap(categoryMap, ServiceCatalogContext.class);
                if (id != null && context != null) {
                    serviceCatalogContextMap.put(context.getId(), context);
                }
            }
        }
        return serviceCatalogContextMap;
    }

    @Override
    public void convertToXMLComponent(ServiceCatalogContext component, XMLBuilder element) throws Exception {
        element.element(PackageConstants.ServiceCatalogConstants.NAME).text(component.getName());
        element.element(PackageConstants.ServiceCatalogConstants.DESCRIPTION).text(component.getDescription());

        FileStore fs = FacilioFactory.getFileStore();
        FileInfo fileInfo = fs.getFileInfo(component.getPhotoId());

        ServiceCatalogGroupContext groupDetails = getServiceCatalogGroupDetail(component.getGroupId());

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule moduleDetails = modBean.getModule(component.getModuleId());

        FacilioForm formDetails = component.getForm();

        ApplicationContext appDetails = ApplicationApi.getApplicationForId(component.getAppId());

        SharingContext<SingleSharingContext> sharing = component.getSharing();

        element.element(PackageConstants.ServiceCatalogConstants.GROUP_LINK_NAME).text(groupDetails.getLinkName());
        element.element(PackageConstants.ServiceCatalogConstants.TYPE).text(String.valueOf(component.getType()));
        element.element(PackageConstants.ServiceCatalogConstants.MODULE_NAME).text(moduleDetails.getName());

        if(formDetails != null) {
            element.element(PackageConstants.ServiceCatalogConstants.FORM_NAME).text(formDetails.getName());
        }

        element.element(PackageConstants.ServiceCatalogConstants.EXTERNAL_URL).text(component.getExternalURL());
        element.element(PackageConstants.ServiceCatalogConstants.APP_NAME).text(appDetails.getLinkName());

        if (CollectionUtils.isNotEmpty(component.getSharing())) {
            PackageBeanUtil.constructBuilderFromSharingContext(sharing, element.element(PackageConstants.ServiceCatalogConstants.SHARING));
        }

        XMLBuilder attachmentList = element.element(PackageConstants.EmailConstants.ATTACHMENT_LIST);

        if (component.getPhotoId() > 0) {
            XMLBuilder attachment = attachmentList.element(PackageConstants.EmailConstants.ATTACHMENT);
            attachmentList.addElement(PackageFileUtil.constructMetaAttachmentXMLBuilder(ComponentType.SERVICE_CATALOG_ITEM, component.getId(), attachment, fileInfo));
        }

    }

    public static ServiceCatalogGroupContext getCategoryDetailsForLinkName(String linkName) throws Exception {

        List<FacilioField> fields = FieldFactory.getServiceCatalogGroupFields();

        GenericSelectRecordBuilder selectRecord = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogGroupModule().getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", linkName, StringOperators.IS));


        Map<String, Object> map = selectRecord.fetchFirst();
        return FieldUtil.getAsBeanFromMap(map, ServiceCatalogGroupContext.class);
    }

    private ServiceCatalogGroupContext getServiceCatalogGroupDetail(long id) throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getServiceCatalogGroupDetailChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        ServiceCatalogGroupContext serviceCatalogGroupContext = (ServiceCatalogGroupContext) context.get(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP);

        return serviceCatalogGroupContext;
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
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        List<ServiceCatalogContext> serviceCatalogs = FieldUtil.getAsBeanListFromMapList(getServiceCatalogProp(), ServiceCatalogContext.class);

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder catalogElement = idVsData.getValue();
            ServiceCatalogContext catalogDetails = constructCatalogContext(catalogElement);

            List<Map<String, Object>> attachmentList = new ArrayList<>();

            List<XMLBuilder> attachmentElements = catalogElement.getElementList(PackageConstants.EmailConstants.ATTACHMENT);

            for (XMLBuilder attachmentElement : attachmentElements) {
                if (attachmentElement != null) {
                    FileContext fileContext = PackageFileUtil.addMetaFileAndGetContext(attachmentElement);
                    if (fileContext != null) {
                        Map<String, Object> attachmentObj = new HashMap<>();
                        attachmentObj.put("fileId", fileContext.getFileId());
                        attachmentList.add(attachmentObj);
                    }
                }
            }

            Map<String, Object> photo = new HashMap<>();

            if (attachmentList.size() > 0) {
                photo = attachmentList.get(0);
            }


            boolean containsName = false;
            Long id = -1L;
            for (ServiceCatalogContext catalog : serviceCatalogs) {
                if (catalog.getName().equals(catalog.getName())) {
                    id = catalog.getId();
                    containsName = true;
                    break;
                }
            }

            if (!containsName) {
                long catagoryId = addOrUpdateServiceCatalog(catalogDetails, photo);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), catagoryId);
            } else {
                catalogDetails.setId(id);
                addOrUpdateServiceCatalog(catalogDetails, photo);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), id);
            }

        }

        return uniqueIdentifierVsComponentId;
    }

    private void addOrUpdateServiceCatalog(ServiceCatalogContext serviceCatalog) throws Exception {
        addOrUpdateServiceCatalog(serviceCatalog, null);
    }

    private long addOrUpdateServiceCatalog(ServiceCatalogContext serviceCatalog, Map<String, Object> photo) throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateServiceCatalogChain();
        FacilioContext context = chain.getContext();

        String moduleName = serviceCatalog.getModule().getName();

        if (!photo.isEmpty()) {
            long photoId = (long) photo.get("fileId");
            serviceCatalog.setPhotoId(photoId);
        }

        context.put(FacilioConstants.ContextNames.SERVICE_CATALOG, serviceCatalog);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();

        ServiceCatalogContext serviceCatalogContext = (ServiceCatalogContext) context.get(FacilioConstants.ContextNames.SERVICE_CATALOG);

        return serviceCatalogContext.getId();

    }

    private ServiceCatalogContext constructCatalogContext(XMLBuilder element) throws Exception {
        ServiceCatalogContext catalogContext = new ServiceCatalogContext();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        String name = element.getElement(PackageConstants.ServiceCatalogConstants.NAME).getText();
        String description = element.getElement(PackageConstants.ServiceCatalogConstants.DESCRIPTION).getText();
        String appLinkName = element.getElement(PackageConstants.ServiceCatalogConstants.APP_NAME).getText();
        String groupLinkName = element.getElement(PackageConstants.ServiceCatalogConstants.GROUP_LINK_NAME).getText();
        String moduleName = element.getElement(PackageConstants.ServiceCatalogConstants.MODULE_NAME).getText();

        FacilioModule moduleDetails = modBean.getModule(moduleName);

        String formName;
        if (element.getElement(PackageConstants.ServiceCatalogConstants.FORM_NAME) != null) {
            formName = element.getElement(PackageConstants.ServiceCatalogConstants.FORM_NAME).getText();
            FacilioForm formDetails = FormsAPI.getFormFromDB(formName, moduleDetails);
            catalogContext.setForm(formDetails);
            catalogContext.setFormId(formDetails.getId());
        }

        String externalURL = element.getElement(PackageConstants.ServiceCatalogConstants.EXTERNAL_URL).getText();
        String type = element.getElement(PackageConstants.ServiceCatalogConstants.TYPE).getText();
        ServiceCatalogContext.Type typeEnum = ServiceCatalogContext.Type.valueOf(Integer.parseInt(type));

        XMLBuilder sharingElement = element.getElement(PackageConstants.ServiceCatalogConstants.SHARING);

        if (sharingElement != null) {
            SharingContext<SingleSharingContext> sharingContexts = PackageBeanUtil.constructSharingContextFromBuilder(sharingElement, SingleSharingContext.class);
            catalogContext.setSharing(sharingContexts);
        }


        ServiceCatalogGroupContext groupDetails = getCategoryDetailsForLinkName(groupLinkName);

        ApplicationContext appDetails = ApplicationApi.getApplicationForLinkName(appLinkName);


        catalogContext.setName(name);
        catalogContext.setDescription(description);
        catalogContext.setAppId(appDetails.getId());
        catalogContext.setModuleId(moduleDetails.getModuleId());
        catalogContext.setGroupId(groupDetails.getId());
        catalogContext.setGroup(groupDetails);
        catalogContext.setModule(moduleDetails);
        catalogContext.setExternalURL(externalURL);
        catalogContext.setType(Integer.parseInt(type));
        catalogContext.setType(typeEnum);


        return catalogContext;
    }

    private List<Map<String, Object>> getServiceCatalogProp() throws Exception {
        FacilioModule module = ModuleFactory.getServiceCatalogModule();
        List<FacilioField> fields = FieldFactory.getServiceCatalogFields();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields);

        List<Map<String, Object>> props = builder.get();
        return props;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsXMLComponent : idVsXMLComponents.entrySet()) {
            long catalogId = idVsXMLComponent.getKey();
            XMLBuilder emailElement = idVsXMLComponent.getValue();
            ServiceCatalogContext catalog = constructCatalogContext(emailElement);
            catalog.setId(catalogId);
            addOrUpdateServiceCatalog(catalog);
        }

    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (Long id : ids) {
            FacilioChain chain = TransactionChainFactory.getDeleteServiceCatalogChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.ID, id);
            chain.execute();
        }
    }
}
