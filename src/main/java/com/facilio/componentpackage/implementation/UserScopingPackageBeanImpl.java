package com.facilio.componentpackage.implementation;

import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.fw.BeanFactory;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class UserScopingPackageBeanImpl implements PackageBean<ScopingContext> {

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> userScopingIds = new HashMap<>();
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        List<ScopingContext> allUserScopingList = userScopeBean.getUserScopingList(null,-1,-1);

        if(CollectionUtils.isNotEmpty(allUserScopingList)) {
            userScopingIds = allUserScopingList.stream()
                    .collect(Collectors.toMap(ScopingContext::getId, v-> -1L));
        }
        return userScopingIds;
    }

    @Override
    public Map<Long, ScopingContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, ScopingContext> userScopingIdsVsContext = new HashMap<>();
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        List<ScopingContext> allUserScopingList = userScopeBean.getUserScopingList(null,-1,-1);

        if(CollectionUtils.isNotEmpty(allUserScopingList)) {
            allUserScopingList.forEach(scope -> {
                if(ids.contains(scope.getId())){
                    userScopingIdsVsContext.put(scope.getId(), scope);
                }
            });
        }
        return userScopingIdsVsContext;
    }

    @Override
    public void convertToXMLComponent(ScopingContext component, XMLBuilder UserScopingElement) throws Exception {
        if(component != null) {
            UserScopingElement.element(PackageConstants.UserScopingConstants.DISPLAY_NAME).text(component.getScopeName());
            UserScopingElement.element(PackageConstants.UserScopingConstants.DESCRIPTION).text(component.getDescription());
            UserScopingElement.element(PackageConstants.UserScopingConstants.LINK_NAME).text(component.getLinkName());
            UserScopingElement.element(PackageConstants.UserScopingConstants.STATUS).text(String.valueOf(component.isStatus()));
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
//        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
//        Map<String, Long> uniqueIdentifierVsUserScopingId = new HashMap<>();
//
//        List<ScopingContext> allUserScopingList = userScopeBean.getUserScopingList(null,-1,-1);
//        Map<Long, ScopingContext> idsVsScopingContext = allUserScopingList.stream().collect(Collectors.toMap(ScopingContext::getId, Function.identity()));
//
//        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
//            String uniqueIdentifier = idVsData.getKey();
//            XMLBuilder permissionSetComponentElement = idVsData.getValue();
//
//            long scopeId =  Long.parseLong(permissionSetComponentElement.getElement(PackageConstants.UserScopingConstants.ID).getText());
//            ScopingContext userScopingContext = idsVsScopingContext.get(scopeId);
//
//            if (userScopingContext != null && userScopingContext.getId() == scopeId) {
//                uniqueIdentifierVsUserScopingId.put(uniqueIdentifier, scopeId);
//            } else {
//                LOGGER.info("###Sandbox - UserScoping with id not found - " + scopeId);
//            }
//        }
//        return uniqueIdentifierVsUserScopingId;
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsUserScopingIds = new HashMap<>();
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder userScopingComponentElement = idVsData.getValue();
            ScopingContext scopingContext = new ScopingContext();

            scopingContext.setScopeName(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.DISPLAY_NAME).getText());
            scopingContext.setDescription(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.DESCRIPTION).getText());
            scopingContext.setStatus(Boolean.valueOf(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.STATUS).getText()));
            scopingContext.setLinkName(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.LINK_NAME).getText());
            // update id
            long id = userScopeBean.addUserScoping(scopingContext);
            uniqueIdentifierVsUserScopingIds.put(uniqueIdentifier, id);
        }

        return uniqueIdentifierVsUserScopingIds;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        for (Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()) {
            XMLBuilder userScopingComponentElement = idVsComponent.getValue();
            ScopingContext userScopingContext = new ScopingContext();

            if(userScopingComponentElement != null) {
                userScopingContext.setScopeName(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.DISPLAY_NAME).getText());
                userScopingContext.setDescription(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.DESCRIPTION).getText());
                userScopingContext.setStatus(Boolean.valueOf(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.STATUS).getText()));
                userScopingContext.setLinkName(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.LINK_NAME).getText());

                userScopeBean.updateUserScoping(userScopingContext);
            }
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        for (Long userScopingId: ids) {
            userScopeBean.deleteUserScoping(userScopingId);
        }
    }

}
