package com.facilio.componentpackage.implementation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.fw.BeanFactory;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;

import java.util.*;

@Log4j
public class PermissionSetPackageImpl implements PackageBean<PermissionSetContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getPermissionSetComponent(true, false);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getPermissionSetComponent(false, true);
    }

    @Override
    public Map<Long, PermissionSetContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, PermissionSetContext> permissionSetIdsVsContext = new HashMap<>();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        for (Long id : ids) {
            PermissionSetContext permissionSet = permissionSetBean.getPermissionSet(id);
            if(permissionSet != null){
                permissionSetIdsVsContext.put(id,permissionSet);
            }
        }
        return permissionSetIdsVsContext;
    }

    @Override
    public void convertToXMLComponent(PermissionSetContext component, XMLBuilder permissionSetElement) throws Exception {
        permissionSetElement.element(PackageConstants.PermissionSetConstants.LINK_NAME).text(component.getLinkName());
        permissionSetElement.element(PackageConstants.PermissionSetConstants.DISPLAY_NAME).text(component.getDisplayName());
        permissionSetElement.element(PackageConstants.PermissionSetConstants.DESCRIPTION).text(component.getDescription());
        permissionSetElement.element(PackageConstants.PermissionSetConstants.STATUS).text(String.valueOf(component.getStatus()));
        permissionSetElement.element(PackageConstants.PermissionSetConstants.IS_PRIVILEGED).text(String.valueOf(component.isPrivileged()));
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> permissionSetComponentIds) throws Exception {
        List<Long> deletedPermissionSetComponentIds = new ArrayList<>();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        List<PermissionSetContext> existingPermissionSetList = permissionSetBean.getPermissionSetsList(-1,-1,null,false);

        existingPermissionSetList.forEach(set -> {
            if(set.getSysDeletedTime() > 0 || set.getSysDeletedTime() != -1){
                deletedPermissionSetComponentIds.add(set.getId());
            }
        });

        return deletedPermissionSetComponentIds;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        Map<String, Long> uniqueIdentifierVsPermissionSetId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder permissionSetComponentElement = idVsData.getValue();
            String linkName = permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.LINK_NAME).getText();
            boolean isPriviliged = Boolean.valueOf(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.IS_PRIVILEGED).getText());

            if(!isPriviliged) {
                PermissionSetContext permissionSet = permissionSetBean.getPermissionSet(linkName);
                if (permissionSet != null && permissionSet.getId() > 0) {
                    uniqueIdentifierVsPermissionSetId.put(uniqueIdentifier, permissionSet.getId());
                } else {
                    LOGGER.info("###Sandbox - PermissionSet with linkName not found - " + linkName);
                }
            }
        }
        return uniqueIdentifierVsPermissionSetId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsPermissionIds = new HashMap<>();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder permissionSetComponentElement = idVsData.getValue();
            PermissionSetContext permissionSetContext = new PermissionSetContext();
            boolean isPriviliged = Boolean.parseBoolean(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.IS_PRIVILEGED).getText());

            if(permissionSetComponentElement != null && !isPriviliged){
                permissionSetContext.setDisplayName(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.DISPLAY_NAME).getText());
                permissionSetContext.setDescription(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.DESCRIPTION).getText());
                permissionSetContext.setStatus(Boolean.valueOf(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.STATUS).getText()));
                permissionSetContext.setLinkName(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.LINK_NAME).getText());
                permissionSetContext.setSysCreatedBy(Objects.requireNonNull(AccountUtil.getCurrentUser()).getId());
                permissionSetContext.setIsPrivileged(isPriviliged);

                Long newPermissionSetId = permissionSetBean.addPermissionSet(permissionSetContext);
                uniqueIdentifierVsPermissionIds.put(idVsData.getKey(), newPermissionSetId);
            }
        }
        return uniqueIdentifierVsPermissionIds;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        for (Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()) {
            XMLBuilder permissionSetComponentElement = idVsComponent.getValue();
            PermissionSetContext permissionSetContext = new PermissionSetContext();
            boolean isPriviliged = Boolean.parseBoolean(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.IS_PRIVILEGED).getText());

            if(permissionSetComponentElement != null && !isPriviliged) {
                permissionSetContext.setDisplayName(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.DISPLAY_NAME).getText());
                permissionSetContext.setDescription(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.DESCRIPTION).getText());
                permissionSetContext.setStatus(Boolean.valueOf(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.STATUS).getText()));
                permissionSetContext.setLinkName(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.LINK_NAME).getText());
                permissionSetContext.setIsPrivileged(isPriviliged);

                permissionSetBean.updatePermissionSet(permissionSetContext);
            }
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        for (Long permissionSetId: ids) {
            permissionSetBean.deletePermissionSet(permissionSetId);
        }

    }

    private Map<Long, Long> getPermissionSetComponent(boolean getPrivilegedSet, boolean fetchDeleted) throws Exception {

        Map<Long, Long> permissionSetIdsMap = new HashMap<>();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        List<PermissionSetContext> existingPermissionSetList = permissionSetBean.getPermissionSetsList(-1,-1,null,fetchDeleted);
        existingPermissionSetList.forEach(permissionSet -> {
            if(!(getPrivilegedSet ^ permissionSet.isPrivileged())){
                permissionSetIdsMap.put(permissionSet.getId(), -1l);
            }
        });

        return permissionSetIdsMap;
    }

}
