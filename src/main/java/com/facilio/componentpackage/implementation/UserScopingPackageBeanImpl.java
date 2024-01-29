package com.facilio.componentpackage.implementation;

import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ScopingConfigCacheContext;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.context.ScopingExtendedPropContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class UserScopingPackageBeanImpl implements PackageBean<ScopingExtendedPropContext> {

    UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
    List<ScopingContext> allUserScopingList = userScopeBean.getUserScopingList(null, -1, -1);
    Map<Long, ScopingContext> scopingIdIdVsContext = allUserScopingList.stream().collect(Collectors.toMap(ScopingContext::getId, Function.identity()));

    public UserScopingPackageBeanImpl() throws Exception {
    }

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> userScopingIds = new HashMap<>();
        List<ScopingContext> allUserScopingList = userScopeBean.getUserScopingList(null, -1, -1);

        if (CollectionUtils.isNotEmpty(allUserScopingList)) {
            userScopingIds = allUserScopingList.stream()
                    .collect(Collectors.toMap(ScopingContext::getId, v -> -1L));
        }
        return userScopingIds;
    }

    @Override
    public Map<Long, ScopingExtendedPropContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, ScopingExtendedPropContext> userScopingIdsVsContext = new HashMap<>();
        List<ScopingContext> allUserScopingList = userScopeBean.getUserScopingList(null, -1, -1);
        Map<Long, List<ScopingConfigContext>> basePermissionContext = getAllScopingConfig(ids);
        Map<Long, List<Long>> peoplePermissionSetMapping = getAllPeopleScopingId(ids);

        if (CollectionUtils.isNotEmpty(allUserScopingList)) {
            for (ScopingContext scope : allUserScopingList) {
                Map<String, Object> scopeProperty = FieldUtil.getAsProperties(scope);
                ScopingExtendedPropContext scopeExProp = FieldUtil.getAsBeanFromMap(scopeProperty, ScopingExtendedPropContext.class);

                scopeExProp.setScopingConfigList(basePermissionContext.get(scope.getId()));
                scopeExProp.setPeopleIds(peoplePermissionSetMapping.get(scope.getId()));

                userScopingIdsVsContext.put(scope.getId(), scopeExProp);
            }
        }
        return userScopingIdsVsContext;
    }

    @Override
    public void convertToXMLComponent(ScopingExtendedPropContext component, XMLBuilder UserScopingElement) throws Exception {
        if (component != null) {
            UserScopingElement.element(PackageConstants.UserScopingConstants.DISPLAY_NAME).text(component.getScopeName());
            UserScopingElement.element(PackageConstants.UserScopingConstants.DESCRIPTION).text(component.getDescription());
            UserScopingElement.element(PackageConstants.UserScopingConstants.SCOPE_LINK_NAME).text(component.getLinkName());
            UserScopingElement.element(PackageConstants.UserScopingConstants.STATUS).text(String.valueOf(component.isStatus()));
            UserScopingElement.element(PackageConstants.UserScopingConstants.IS_DEFAULT).text(String.valueOf(component.isDefault()));
            UserScopingElement.element(PackageConstants.UserScopingConstants.APPLICATION_NAME).text(ApplicationApi.getApplicationLinkName(component.getApplicationId()));
            UserScopingElement.addElement(getScopingConfigContextXMLBuilder(component.getScopingConfigList(), UserScopingElement));
            UserScopingElement.addElement(getPeopleUserScopingXMLBuilder(component, UserScopingElement));
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
            scopingContext.setLinkName(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.SCOPE_LINK_NAME).getText());
            scopingContext.setIsDefault(Boolean.valueOf(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.IS_DEFAULT).getText()));
            String applicationLinkName = userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.APPLICATION_NAME).getText();
            if (StringUtils.isNotEmpty(applicationLinkName)) {
                scopingContext.setApplicationId(ApplicationApi.getApplicationIdForLinkName(applicationLinkName));
            }

            long id = userScopeBean.addUserScoping(scopingContext);

            addScopingConfig(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.USER_SCOPING_CONFIGS));
            addOrUpdatePeopleScopingConfig(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.PEOPLE_USER_SCOPING_CONFIGS));
            uniqueIdentifierVsUserScopingIds.put(uniqueIdentifier, id);
        }

        return uniqueIdentifierVsUserScopingIds;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()) {
            XMLBuilder userScopingComponentElement = idVsComponent.getValue();
            ScopingContext userScopingContext = new ScopingContext();

            if (userScopingComponentElement != null) {
                userScopingContext.setScopeName(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.DISPLAY_NAME).getText());
                userScopingContext.setDescription(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.DESCRIPTION).getText());
                userScopingContext.setStatus(Boolean.valueOf(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.STATUS).getText()));
                userScopingContext.setLinkName(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.SCOPE_LINK_NAME).getText());
                userScopingContext.setIsDefault(Boolean.valueOf(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.IS_DEFAULT).getText()));
                String applicationLinkName = userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.APPLICATION_NAME).getText();
                if (StringUtils.isNotEmpty(applicationLinkName)) {
                    userScopingContext.setApplicationId(ApplicationApi.getApplicationIdForLinkName(applicationLinkName));
                }

                addScopingConfig(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.USER_SCOPING_CONFIGS));
                addOrUpdatePeopleScopingConfig(userScopingComponentElement.getElement(PackageConstants.UserScopingConstants.PEOPLE_USER_SCOPING_CONFIGS));

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
        for (Long userScopingId : ids) {
            userScopeBean.deleteUserScoping(userScopingId);
        }
    }

    private Map<Long, List<ScopingConfigContext>> getAllScopingConfig(List<Long> ids) throws Exception {
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        List<ScopingConfigCacheContext> scopingConfigContextList = userScopeBean.getScopingConfig(ids);

        return CollectionUtils.isNotEmpty(scopingConfigContextList) ? scopingConfigContextList.stream().collect(Collectors.groupingBy(ScopingConfigContext::getScopingId)) : new HashMap<>();
    }

    private Map<Long, List<Long>> getAllPeopleScopingId(List<Long> ids) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getPeopleUserScopingFields())
                .table(ModuleFactory.getPeopleUserScopingModule().getTableName())
                .andCondition(CriteriaAPI.getConditionFromList("USER_SCOPING_ID", "userScopingId", ids, NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectRecordBuilder.get();

        return CollectionUtils.isNotEmpty(props) ? props.stream().collect(Collectors.groupingBy(
                map -> (Long) map.get("userScopingId"),
                Collectors.mapping(map -> (Long) map.get("peopleId"), Collectors.toList()))) : new HashMap<>();
    }

    private XMLBuilder getScopingConfigContextXMLBuilder(List<ScopingConfigContext> scopingConfigComponents, XMLBuilder scopingConfigElement) throws Exception {
        XMLBuilder userScopingConfigsList = scopingConfigElement.element(PackageConstants.UserScopingConstants.USER_SCOPING_CONFIGS);

        if (CollectionUtils.isNotEmpty(scopingConfigComponents)) {
            for (ScopingConfigContext scopeConfig : scopingConfigComponents) {
                if (scopeConfig != null) {
                    long scopeId = scopeConfig.getScopingId();
                    ScopingContext scope = scopingIdIdVsContext.get(scopeId);
                    if (scope != null && StringUtils.isNotEmpty(scope.getLinkName())) {
                        XMLBuilder userScopingConfig = userScopingConfigsList.element(PackageConstants.UserScopingConfigConstants.USER_SCOPING_CONFIG);

                        String scopeLinkName = scope.getLinkName();
                        userScopingConfig.element(PackageConstants.UserScopingConfigConstants.SCOPE_LINK_NAME).text(scopeLinkName);
                        userScopingConfig.element(PackageConstants.UserScopingConfigConstants.FIELD_NAME).text(scopeConfig.getFieldName());
                        userScopingConfig.element(PackageConstants.UserScopingConfigConstants.VALUE).text(scopeConfig.getValue());
                        userScopingConfig.element(PackageConstants.UserScopingConfigConstants.CRITERIA_ID).text(String.valueOf(scopeConfig.getCriteriaId()));
                        userScopingConfig.element(PackageConstants.UserScopingConfigConstants.VALUE_GENERATOR).text(scopeConfig.getValueGenerator());

                        long moduleId = scopeConfig.getModuleId();
                        FacilioModule module = Constants.getModBean().getModule(moduleId);
                        String moduleName = module != null ? StringUtils.isNotEmpty(module.getName()) ? module.getName() : null : null;
                        userScopingConfig.element(PackageConstants.UserScopingConfigConstants.MODULE_NAME).text(moduleName);
                        //Criteria
                        LOGGER.info("####Sandbox Tracking - Parsing Criteria - ModuleName - " + moduleName + " Scoping - " + scopeLinkName);
                        userScopingConfig.addElement(PackageBeanUtil.constructBuilderFromCriteria(scopeConfig.getCriteria(), userScopingConfig.element(PackageConstants.CriteriaConstants.CRITERIA), moduleName));
                    }
                }
            }
        }
        return userScopingConfigsList;
    }

    private XMLBuilder getPeopleUserScopingXMLBuilder(ScopingExtendedPropContext userScopingComponent, XMLBuilder userScopingElement) throws Exception {
        XMLBuilder peopleUserScopingConfigsList = userScopingElement.element(PackageConstants.UserScopingConstants.PEOPLE_USER_SCOPING_CONFIGS);
        List<Long> peopleIds = userScopingComponent.getPeopleIds();
        if (CollectionUtils.isNotEmpty(peopleIds)) {
            for (Long peopleId : peopleIds) {
                if (peopleId > 0) {
                    XMLBuilder peopleUserScopingConfig = peopleUserScopingConfigsList.element(PackageConstants.PeopleUserScopingConstants.PEOPLE_USER_SCOPING_CONFIG);
                    String peopleMail = PackageUtil.getPeopleMail(peopleId);
                    String scopeLinkName = userScopingComponent.getLinkName();

                    peopleUserScopingConfig.element(PackageConstants.PeopleUserScopingConstants.PEOPLE_MAIL).text(peopleMail);
                    peopleUserScopingConfig.element(PackageConstants.PeopleUserScopingConstants.SCOPE_LINK_NAME).text(scopeLinkName);
                }
            }
        }
        return peopleUserScopingConfigsList;
    }

    private void addScopingConfig(XMLBuilder scopingConfigListElement) throws Exception {
        List<XMLBuilder> scopingConfigs = scopingConfigListElement.getFirstLevelElementListForTagName(PackageConstants.UserScopingConfigConstants.USER_SCOPING_CONFIG);
        List<ScopingConfigContext> scopingConfigList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(scopingConfigs)) {
            for (XMLBuilder scopingConfig : scopingConfigs) {
                ScopingConfigContext scopingConfigContext = new ScopingConfigContext();

                String linkName = scopingConfig.getElement(PackageConstants.UserScopingConfigConstants.SCOPE_LINK_NAME).getText();
                if (StringUtils.isEmpty(linkName)) {
                    continue;
                }
                long scopeId = getScopeIdForLinkName(linkName);

                scopingConfigContext.setScopingId(scopeId);
                scopingConfigContext.setCriteriaId(Long.parseLong(scopingConfig.getElement(PackageConstants.UserScopingConfigConstants.CRITERIA_ID).getText()));
                scopingConfigContext.setFieldName(scopingConfig.getElement(PackageConstants.UserScopingConfigConstants.FIELD_NAME).getText());
                scopingConfigContext.setValue(scopingConfig.getElement(PackageConstants.UserScopingConfigConstants.VALUE).getText());
                scopingConfigContext.setValueGenerator(scopingConfig.getElement(PackageConstants.UserScopingConfigConstants.VALUE_GENERATOR).getText());
                scopingConfigContext.setCriteria(PackageBeanUtil.constructCriteriaFromBuilder(scopingConfig.getElement(PackageConstants.UserScopingConfigConstants.CRITERIA)));
                //moduleId
                String moduleName = scopingConfig.getElement(PackageConstants.UserScopingConfigConstants.MODULE_NAME).getText();
                FacilioModule module = Constants.getModBean().getModule(moduleName);
                long moduleId = module != null ? module.getModuleId() > 0 ? module.getModuleId() : -1L : -1L;
                scopingConfigContext.setModuleId(moduleId);

                scopingConfigList.add(scopingConfigContext);
            }
        }
        userScopeBean.addScopingConfigForApp(scopingConfigList);
    }

    private void addOrUpdatePeopleScopingConfig(XMLBuilder PeopleScopingConfigListElement) throws Exception {
        List<XMLBuilder> peopleScopingConfigList = PeopleScopingConfigListElement.getFirstLevelElementListForTagName(PackageConstants.PeopleUserScopingConstants.PEOPLE_USER_SCOPING_CONFIG);
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        if (CollectionUtils.isNotEmpty(peopleScopingConfigList)) {
            for (XMLBuilder uniqueIdentifierVsXMLBuilder : peopleScopingConfigList) {
                String peopleMail = uniqueIdentifierVsXMLBuilder.getElement(PackageConstants.PeopleUserScopingConstants.PEOPLE_MAIL).getText();
                Long peopleId = PackageUtil.getPeopleId(peopleMail);

                String scopeLinkName = uniqueIdentifierVsXMLBuilder.getElement(PackageConstants.PeopleUserScopingConstants.SCOPE_LINK_NAME).getText();
                ScopingContext scope = userScopeBean.getScopingForLinkname(scopeLinkName);
                Long scopeId = scope != null ? scope.getId() : -1L;

                if (peopleId < 0 || scopeId < 0) {
                    LOGGER.info("####Sandbox - Skipping add PeopleUser Scoping Config since peopleId or scopeId is null for peopleMail " + peopleMail + " scopeLinkName " + scopeLinkName);
                    continue;
                }
                userScopeBean.updatePeopleScoping(peopleId, scopeId);
            }
        }
    }

    private long getScopeIdForLinkName(String linkName) throws Exception {
        if (StringUtils.isNotEmpty(linkName)) {
            ScopingContext scope = userScopeBean.getScopingForLinkname(linkName);
            if (scope != null && scope.getId() > 0) {
                return scope.getId();
            }
        }
        return -1L;
    }

}
