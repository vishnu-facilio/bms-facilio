package com.facilio.componentpackage.implementation;

import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ScopingConfigCacheContext;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class UserScopingConfigPackageBeanImpl implements PackageBean<ScopingConfigContext> {

    UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
    List<ScopingContext>allUserScopingList = userScopeBean.getUserScopingList(null,-1,-1);
    Map<Long, ScopingContext> scopingIdIdVsContext = scopingIdIdVsContext = allUserScopingList.stream().collect(Collectors.toMap(ScopingContext::getId, Function.identity()));

    public UserScopingConfigPackageBeanImpl() throws Exception {
    }

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> userScopingConfigIdVsUserScopingId = new HashMap<>();
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");

        if(CollectionUtils.isNotEmpty(allUserScopingList)){
            for(ScopingContext userScope : allUserScopingList){
                if(userScope != null && userScope.getId() > 0){
                    long id = userScope.getId();
                    List<ScopingConfigCacheContext> scopingConfigList = userScopeBean.getScopingConfig(id);
                    if(CollectionUtils.isNotEmpty(scopingConfigList)){
                        scopingConfigList.forEach(config ->{
                            if(config != null && config.getId() > 0){
                                userScopingConfigIdVsUserScopingId.put(config.getId(),id);
                            }
                        });
                    }
                }
            }
        }

        return userScopingConfigIdVsUserScopingId;
    }

    @Override
    public Map<Long, ScopingConfigContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, ScopingConfigContext> userScopingIdsVsContext = new HashMap<>();

        List<ScopingConfigCacheContext> scopingConfigContextList = userScopeBean.getScopingConfig(ids);
        if(CollectionUtils.isNotEmpty(scopingConfigContextList)){
            scopingConfigContextList.forEach(config ->{
                if(config != null && config.getId() > 0 && ids.contains(config.getId())){
                    userScopingIdsVsContext.put(config.getId(), config);
                }
            });
        }
        return userScopingIdsVsContext;
    }

    @Override
    public void convertToXMLComponent(ScopingConfigContext component, XMLBuilder scopingConfigElement) throws Exception {
        long scopeId = component.getScopingId();
        ScopingContext scope = scopingIdIdVsContext.get(scopeId);
        if(scope != null && StringUtils.isNotEmpty(scope.getLinkName())) {
            String scopeLinkName = scope.getLinkName();

            scopingConfigElement.element(PackageConstants.UserScopingConfigConstants.SCOPE_NAME).text(scopeLinkName);
            scopingConfigElement.element(PackageConstants.UserScopingConfigConstants.FIELD_NAME).text(component.getFieldName());
            scopingConfigElement.element(PackageConstants.UserScopingConfigConstants.VALUE).text(component.getValue());
            scopingConfigElement.element(PackageConstants.UserScopingConfigConstants.CRITERIA_ID).text(String.valueOf(component.getCriteriaId()));
            scopingConfigElement.element(PackageConstants.UserScopingConfigConstants.VALUE_GENERATOR).text(component.getValueGenerator());

            long moduleId = component.getModuleId();
            FacilioModule module = Constants.getModBean().getModule(moduleId);
            String moduleName = module != null ? StringUtils.isNotEmpty(module.getName()) ? module.getName() : null : null;
            scopingConfigElement.element(PackageConstants.UserScopingConfigConstants.MODULE_NAME).text(moduleName);
            //Criteria
            scopingConfigElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(component.getCriteria(), scopingConfigElement.element(PackageConstants.CriteriaConstants.CRITERIA), moduleName));

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
//        Map<String, Long> uniqueIdentifierVsScopingConfigId = new HashMap<>();
//
//        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
//            String uniqueIdentifier = idVsData.getKey();
//            XMLBuilder scopingConfigXMLComponent = idVsData.getValue();
//            long scopingConfigId = Long.parseLong(scopingConfigXMLComponent.getElement(PackageConstants.UserScopingConfigConstants.ID).getText());
//
//            if (scopingConfigId > 0) {
//                uniqueIdentifierVsScopingConfigId.put(uniqueIdentifier, scopingConfigId);
//            } else {
//                LOGGER.info("###Sandbox - ScopingConfig with id not found - " + scopingConfigId);
//            }
//
//        }
//        return uniqueIdentifierVsScopingConfigId;
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsScopingConfigIds = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> uniqueIdentifierVsXMLBuilder : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = uniqueIdentifierVsXMLBuilder.getKey();
            XMLBuilder scopingConfigComponentElement = uniqueIdentifierVsXMLBuilder.getValue();
            ScopingConfigContext scopingConfigContext = new ScopingConfigContext();

            String linkName = scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.SCOPE_NAME).getText();
            if(StringUtils.isEmpty(linkName)){
                continue;
            }
            long scopeId = getScopeIdForLinkName(linkName);

            scopingConfigContext.setScopingId(scopeId);
            scopingConfigContext.setCriteriaId(Long.parseLong(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.CRITERIA_ID).getText()));
            scopingConfigContext.setFieldName(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.FIELD_NAME).getText());
            scopingConfigContext.setValue(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.VALUE).getText());
            scopingConfigContext.setValueGenerator(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.VALUE_GENERATOR).getText());
            scopingConfigContext.setCriteria(PackageBeanUtil.constructCriteriaFromBuilder(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.CRITERIA)));
            //moduleId
            String moduleName = scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.MODULE_NAME).getText();
            FacilioModule module = Constants.getModBean().getModule(moduleName);
            long moduleId = module != null? module.getModuleId() > 0? module.getModuleId() : -1L : -1L;
            scopingConfigContext.setModuleId(moduleId);

            long recordId = userScopeBean.addScopingConfigForApp(scopingConfigContext, true);
            uniqueIdentifierVsScopingConfigIds.put(uniqueIdentifier, recordId);
        }

        return uniqueIdentifierVsScopingConfigIds;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        Map<Long, List<ScopingConfigContext>> scopingIdVsScopingConfigContexts = new HashMap<>();

        for (Map.Entry<Long, XMLBuilder> idVsXMLBuilder : idVsXMLComponents.entrySet()) {
            XMLBuilder scopingConfigComponentElement = idVsXMLBuilder.getValue();
            ScopingConfigContext scopingConfigContext = new ScopingConfigContext();

            long scopingId = getScopeIdForLinkName(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.SCOPE_NAME).getText());

            if(scopingId > 0) {
                if(CollectionUtils.isEmpty(scopingIdVsScopingConfigContexts.get(scopingId))) {
                    List<ScopingConfigContext> scopingConfigContextList = new ArrayList<>();
                    scopingIdVsScopingConfigContexts.put(scopingId, scopingConfigContextList);
                }
            }else {
                continue;
            }

            long criteriaId = Long.parseLong(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.CRITERIA_ID).getText());
            Criteria criteria = criteriaId > 0 ? CriteriaAPI.getCriteria(criteriaId) : new Criteria();
            String moduleName = scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.MODULE_NAME).getText();
            FacilioModule module = Constants.getModBean().getModule(moduleName);
            long moduleId = module != null? module.getModuleId() > 0? module.getModuleId() : -1L : -1L;

            scopingConfigContext.setScopingId(scopingId);
            scopingConfigContext.setCriteriaId(criteriaId);
            scopingConfigContext.setCriteria(criteria);
            scopingConfigContext.setFieldName(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.FIELD_NAME).getText());
            scopingConfigContext.setValue(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.VALUE).getText());
            scopingConfigContext.setOperatorId(Long.parseLong(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.OPERATOR_ID).getText()));
            scopingConfigContext.setValueGenerator(scopingConfigComponentElement.getElement(PackageConstants.UserScopingConfigConstants.VALUE_GENERATOR).getText());
            scopingConfigContext.setModuleId(moduleId);

            List<ScopingConfigContext> scopingConfig = scopingIdVsScopingConfigContexts.get(scopingId);
            scopingConfig.add(scopingConfigContext);
            scopingIdVsScopingConfigContexts.put(scopingId,scopingConfig);
        }
        if(MapUtils.isNotEmpty(scopingIdVsScopingConfigContexts)) {
            for(Long scopeId : scopingIdVsScopingConfigContexts.keySet()) {
                List<ScopingConfigContext> scopingConfigContextLists = scopingIdVsScopingConfigContexts.get(scopeId);
                if(CollectionUtils.isNotEmpty(scopingConfigContextLists)) {
                    userScopeBean.updateScopingConfigForUserScoping(scopingConfigContextLists, scopeId);
                }
            }
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (Long scopingConfigId: ids) {
            userScopeBean.deleteScopingConfigForId(scopingConfigId);
        }
    }

    private long getScopeIdForLinkName(String linkName) throws Exception {
        if(StringUtils.isNotEmpty(linkName)) {
            ScopingContext scope = userScopeBean.getScopingForLinkname(linkName);
            if (scope != null && scope.getId() > 0) {
                return scope.getId();
            }
        }
        return -1L;
    }
}
