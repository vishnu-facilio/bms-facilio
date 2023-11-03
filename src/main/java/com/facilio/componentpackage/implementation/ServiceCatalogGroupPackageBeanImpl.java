package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.actions.ServiceCatalogAction;
import com.facilio.bmsconsole.commands.AddOrUpdateServiceCatalogGroupCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.bmsconsole.util.ServiceCatalogApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceCatalogGroupPackageBeanImpl implements PackageBean<ServiceCatalogGroupContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getCatalogGroupIdVsModuleId();
    }

    private Map<Long, Long> getCatalogGroupIdVsModuleId() throws Exception {
        Map<Long, Long> groupIdVsModuleId = new HashMap<>();

        FacilioModule module = ModuleFactory.getServiceCatalogGroupModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(FieldFactory.getIdField(module));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields);

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                groupIdVsModuleId.put((Long) prop.get("id"), -1L);
            }
        }

        return groupIdVsModuleId;
    }

    @Override
    public Map<Long, ServiceCatalogGroupContext> fetchComponents(List<Long> ids) throws Exception {
        return getServiceCatagoryContextMap(ids);
    }

    private Map<Long, ServiceCatalogGroupContext> getServiceCatagoryContextMap(List<Long> ids) throws Exception {
        Map<Long, ServiceCatalogGroupContext> serviceCatalogGroupContextMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ids)) {
            List<Map<String, Object>> catogoryList = ServiceCatalogApi.getAllCategory(ids);

            for (Map<String, Object> categoryMap : catogoryList) {
                Long id = (Long) categoryMap.get("id");
                ServiceCatalogGroupContext context = FieldUtil.getAsBeanFromMap(categoryMap, ServiceCatalogGroupContext.class);
                if (id != null && context != null) {
                    serviceCatalogGroupContextMap.put(id, context);
                }
            }

        }
        return serviceCatalogGroupContextMap;
    }

    @Override
    public void convertToXMLComponent(ServiceCatalogGroupContext component, XMLBuilder element) throws Exception {
        element.element(PackageConstants.ServiceCatalogConstants.NAME).text(component.getName());
        element.element(PackageConstants.ServiceCatalogConstants.DESCRIPTION).text(component.getDescription());
        element.element(PackageConstants.ServiceCatalogConstants.LINK_NAME).text(component.getLinkName());
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
        List<ServiceCatalogGroupContext> serviceCatalogGroups = FieldUtil.getAsBeanListFromMapList(ServiceCatalogAction.getServiceCatalogGroupProp(), ServiceCatalogGroupContext.class);
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder emailElement = idVsData.getValue();
            ServiceCatalogGroupContext catagory = constructCatalogContext(emailElement);
            boolean containsName = false;
            Long id = -1L;
            for (ServiceCatalogGroupContext group : serviceCatalogGroups) {
                if (group.getName().equals(catagory.getName())) {
                    id = group.getId();
                    containsName = true;
                    break;
                }
            }

            AddOrUpdateServiceCatalogGroupCommand serviceCatalogGroupCommand = new AddOrUpdateServiceCatalogGroupCommand();
            if (serviceCatalogGroupCommand.checkComplaintType(catagory)) {
                continue;
            }

            if (!containsName) {
                long catagoryId = addOrUpdateServiceCatalogGroup(catagory);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), catagoryId);
            } else {
                catagory.setId(id);
                addOrUpdateServiceCatalogGroup(catagory);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), id);
            }

        }


        return uniqueIdentifierVsComponentId;
    }

    private long addOrUpdateServiceCatalogGroup(ServiceCatalogGroupContext catagory) throws Exception {

        ServiceCatalogGroupContext catalogGroupContext = AddOrUpdateServiceCatalogGroupCommand.addServiceCatalogGroupWithoutLinkName(catagory);
        return catalogGroupContext.getId();
    }

    private ServiceCatalogGroupContext constructCatalogContext(XMLBuilder emailElement) {
        ServiceCatalogGroupContext catagoryContext = new ServiceCatalogGroupContext();
        String name = emailElement.getElement(PackageConstants.ServiceCatalogConstants.NAME).getText();
        String description = emailElement.getElement(PackageConstants.ServiceCatalogConstants.DESCRIPTION).getText();
        String linkName = emailElement.getElement(PackageConstants.ServiceCatalogConstants.LINK_NAME).getText();
        catagoryContext.setName(name);
        catagoryContext.setDescription(description);
        catagoryContext.setLinkName(linkName);
        return catagoryContext;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsXMLComponent : idVsXMLComponents.entrySet()) {
            long catagoryId = idVsXMLComponent.getKey();
            XMLBuilder emailElement = idVsXMLComponent.getValue();
            ServiceCatalogGroupContext catagory = constructCatalogContext(emailElement);
            AddOrUpdateServiceCatalogGroupCommand serviceCatalogGroupCommand = new AddOrUpdateServiceCatalogGroupCommand();
            if (serviceCatalogGroupCommand.checkComplaintType(catagory)) {
                continue;
            }
            catagory.setId(catagoryId);
            addOrUpdateServiceCatalogGroup(catagory);
        }

    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (Long id : ids) {
            FacilioChain chain = TransactionChainFactory.getDeleteServiceCatalogGroupChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.ID, id);
            chain.execute();
        }

    }
}
