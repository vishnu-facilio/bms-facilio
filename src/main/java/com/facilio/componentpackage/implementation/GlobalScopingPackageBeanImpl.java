package com.facilio.componentpackage.implementation;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ValueGeneratorBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.commands.AddOrUpdateScopeVariable;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeValueGeneratorExtendedProp;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class GlobalScopingPackageBeanImpl implements PackageBean<GlobalScopeValueGeneratorExtendedProp> {

    GlobalScopeBean globalScopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");

    List<GlobalScopeVariableContext> allScopeVariable = globalScopeBean.getAllScopeVariable(null, -1, -1, null, true);
    Map<Long, GlobalScopeVariableContext> idVsScopeVariables = allScopeVariable.stream().collect(Collectors.toMap(GlobalScopeVariableContext::getId, Function.identity()));

    public GlobalScopingPackageBeanImpl() throws Exception {
    }

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> scopeVariablesIds = new HashMap<>();

        if (CollectionUtils.isNotEmpty(allScopeVariable)) {
            for (GlobalScopeVariableContext scopeVariable : allScopeVariable) {
                scopeVariablesIds.put(scopeVariable.getId(), -1L);
            }
        }
        return scopeVariablesIds;
    }

    @Override
    public Map<Long, GlobalScopeValueGeneratorExtendedProp> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, GlobalScopeValueGeneratorExtendedProp> scopeVariables = new HashMap<>();
        Map<Long, ValueGeneratorContext> allValueGeneratorMap = getAllValueGenerator();

        for (GlobalScopeVariableContext scope : allScopeVariable) {
            if (scope != null) {
                GlobalScopeValueGeneratorExtendedProp scopeExtendedProp = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(scope), GlobalScopeValueGeneratorExtendedProp.class);
                if (scope.getValueGeneratorId() != null && scope.getValueGeneratorId() > 0) {
                    if (allValueGeneratorMap.containsKey(scope.getValueGeneratorId())) {
                        scopeExtendedProp.setValueGeneratorContext(allValueGeneratorMap.get(scope.getValueGeneratorId()));
                    } else {
                        LOGGER.info("###Sandbox - tracking ValueGeneratorContext missing for ValueGeneratorId - " + scope.getValueGeneratorId() + " for scopeLinkName - " + scope.getLinkName());
                    }
                }
                scopeVariables.put(scope.getId(), scopeExtendedProp);
            }
        }
        return scopeVariables;
    }

    @Override
    public void convertToXMLComponent(GlobalScopeValueGeneratorExtendedProp component, XMLBuilder scopeVariableElement) throws Exception {
        scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.LINK_NAME).text(component.getLinkName());
        scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.DISPLAY_NAME).text(component.getDisplayName());
        scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.DESCRIPTION).text(component.getDescription());
        scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.SHOW_SWITCH).text(String.valueOf(component.getShowSwitch()));
        scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.STATUS).text(String.valueOf(component.getStatus()));
        scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.DELETED).text(String.valueOf(component.getDeleted()));
        scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.TYPE).text(String.valueOf(component.getType()));

        ApplicationContext applicationContext = ApplicationApi.getApplicationForId(component.getAppId());
        if (applicationContext != null && StringUtils.isNotEmpty(applicationContext.getLinkName())) {
            scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.APP_LINK_NAME).text(applicationContext.getLinkName());
        } else {
            throw new IllegalArgumentException("ApplicationLinkName can't be Empty");
        }

        Long applicableModuleId = component.getApplicableModuleId();
        String applicableModuleName = component.getApplicableModuleName();
        if ((applicableModuleId != null && applicableModuleId > 0)) {
            ModuleBean modbean = Constants.getModBean();
            FacilioModule module = modbean.getModule(applicableModuleId);
            if (module != null && StringUtils.isNotEmpty(module.getName())) {
                scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.APPLICABLE_MODULE_NAME).text(module.getName());
            } else {
                throw new IllegalArgumentException("Module cannot be null for ModuleId - " + applicableModuleId);
            }
        } else if (StringUtils.isNotEmpty(applicableModuleName)) {
            scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.APPLICABLE_MODULE_NAME).text(applicableModuleName);
        } else {
            throw new IllegalArgumentException("ApplicableModuleId or ApplicableModuleName cannot be null");
        }

        ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
        if (valGenBean != null && component.getValueGeneratorId() != null) {
            ValueGeneratorContext valGen = valGenBean.getValueGenerator(component.getValueGeneratorId());
            if (valGen != null && StringUtils.isNotEmpty(valGen.getLinkName())) {
                scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.VALUE_GENERATOR_LINK_NAME).text(valGen.getLinkName());
            }
        }
        scopeVariableElement.addElement(getScopeVariableModulesFieldsListXMLBuilder(scopeVariableElement, component.getScopeVariableModulesFieldsList()));
        scopeVariableElement.addElement(getValueGeneratorXMLBuilder(component.getValueGeneratorContext(), scopeVariableElement));
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        List<Long> missingScopeVariableIds = new ArrayList<>(componentIds);
        GlobalScopeBean globalScopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        for (Long scopeVariableId : componentIds) {
            GlobalScopeVariableContext scopeVariableContext = globalScopeBean.getScopeVariable(scopeVariableId);
            if (scopeVariableContext == null) {
                missingScopeVariableIds.add(scopeVariableId);
            }
        }
        return missingScopeVariableIds;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        GlobalScopeBean globalScopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        Map<String, Long> uniqueIdentifierVsGlobalScopeVariableId = new HashMap<>();
        List<String> defaultGlobalScopeVariable = PackageConstants.GlobalScopeVariableConstants.DEFAULT_CREATED_GLOBAL_SCOPE_VARIABLE;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder globalScopeVariableElement = idVsData.getValue();
            String linkName = globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.LINK_NAME).getText();

            if (CollectionUtils.isNotEmpty(defaultGlobalScopeVariable) && defaultGlobalScopeVariable.contains(linkName)) {
                GlobalScopeVariableContext scopeVariable = globalScopeBean.getScopeVariable(linkName);
                if (scopeVariable != null) {
                    uniqueIdentifierVsGlobalScopeVariableId.put(uniqueIdentifier, scopeVariable.getId());
                } else {
                    LOGGER.info("###Sandbox - GlobalScopeVariable with linkName not found - " + linkName);
                }
            }
        }
        return uniqueIdentifierVsGlobalScopeVariableId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsGlobalScopeVariableIds = new HashMap<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");

        Map<String, GlobalScopeVariableContext> nonDeletedGlobalScopeVariableList = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder globalScopeVariableElement = idVsData.getValue();
            GlobalScopeVariableContext globalScopeVariableContext = new GlobalScopeVariableContext();
            if (globalScopeVariableElement != null) {
                String linkName = globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.LINK_NAME).getText();
                List<String> defaultGlobalScopeVariable = PackageConstants.GlobalScopeVariableConstants.DEFAULT_CREATED_GLOBAL_SCOPE_VARIABLE;
                if (CollectionUtils.isNotEmpty(defaultGlobalScopeVariable) && defaultGlobalScopeVariable.contains(linkName)) {
                    GlobalScopeVariableContext defaultScopeVariable = scopeBean.getScopeVariable(linkName);
                    if (defaultScopeVariable != null && defaultScopeVariable.getId() > 0) {
                        globalScopeVariableContext.setId(defaultScopeVariable.getId());
                    }
                    globalScopeVariableContext.setLinkName(linkName);
                }
                String applicableModuleName = globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.APPLICABLE_MODULE_NAME).getText();
                FacilioModule applicableModule = modBean.getModule(applicableModuleName);
                Long applicableModuleId = applicableModule != null ? applicableModule.getModuleId() : null;

                if (applicableModuleId != null && applicableModuleId > 0) {
                    globalScopeVariableContext.setApplicableModuleId(applicableModuleId);
                } else if (StringUtils.isNotEmpty(applicableModuleName)) {
                    globalScopeVariableContext.setApplicableModuleName(applicableModuleName);
                } else {
                    LOGGER.info("####Sandbox - GlobalScopeVariable since applicableModuleName and applicableModuleId both are null ");
                }
                globalScopeVariableContext.setDeleted(Boolean.valueOf(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.DELETED).getText()));
                globalScopeVariableContext.setStatus(Boolean.valueOf(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.STATUS).getText()));
                globalScopeVariableContext.setShowSwitch(Boolean.valueOf(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.SHOW_SWITCH).getText()));
                globalScopeVariableContext.setAppId(ApplicationApi.getApplicationIdForLinkName(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.APP_LINK_NAME).getText()));
                globalScopeVariableContext.setDescription(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.DESCRIPTION).getText());
                globalScopeVariableContext.setDisplayName(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.DISPLAY_NAME).getText());
                globalScopeVariableContext.setType(Integer.valueOf(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.TYPE).getText()));

                XMLBuilder scopeVariableModulesFieldsBuilderList = globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.SCOPE_VARIABLE_MODULES_FIELDS_LIST);
                if (scopeVariableModulesFieldsBuilderList != null) {
                    List<ScopeVariableModulesFields> scopeVariableModulesFieldsList = getScopeVariableModulesFieldsList(scopeVariableModulesFieldsBuilderList);
                    if (CollectionUtils.isNotEmpty(scopeVariableModulesFieldsList)) {
                        globalScopeVariableContext.setScopeVariableModulesFieldsList(scopeVariableModulesFieldsList);
                    }
                }

                if (BooleanUtils.isTrue(globalScopeVariableContext.getDeleted())) {
                    Long newDeletedGlobalScopeVariableId = addOrUpdateGlobalScopeVariable(globalScopeVariableContext);
                    uniqueIdentifierVsGlobalScopeVariableIds.put(idVsData.getKey(), newDeletedGlobalScopeVariableId);
                } else {
                    nonDeletedGlobalScopeVariableList.put(idVsData.getKey(), globalScopeVariableContext);
                }
            }
            String valGenLinkName = globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.VALUE_GENERATOR_LINK_NAME) != null ? globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.VALUE_GENERATOR_LINK_NAME).getText() : null;
            if (StringUtils.isNotEmpty(valGenLinkName)) {
                ValueGeneratorContext valueGen = getValueGeneratorContextFromXMLBuilder(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.VALUE_GENERATOR));
                long valGenId = checkAndAddOrUpdate(valueGen, valGenLinkName);
                if (valGenId < 0) {
                    LOGGER.info("####SandBox - Skipping add of scopeVariable because ValueGeneratorId is null for ValueGeneratorLinkName - " + valGenLinkName);
                    continue;
                }
                globalScopeVariableContext.setValueGeneratorId(valGenId);
            } else {
                globalScopeVariableContext.setValueGeneratorId(null);
            }
        }
        for (Map.Entry<String, GlobalScopeVariableContext> idVsContext : nonDeletedGlobalScopeVariableList.entrySet()) {
            Long newGlobalScopeVariableId = addOrUpdateGlobalScopeVariable(idVsContext.getValue());
            uniqueIdentifierVsGlobalScopeVariableIds.put(idVsContext.getKey(), newGlobalScopeVariableId);
        }
        return uniqueIdentifierVsGlobalScopeVariableIds;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");

        for (Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()) {
            XMLBuilder globalScopeVariableElement = idVsComponent.getValue();
            GlobalScopeVariableContext globalScopeVariableContext = new GlobalScopeVariableContext();

            String applicableModuleName = globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.APPLICABLE_MODULE_NAME).getText();
            FacilioModule applicableModule = modBean.getModule(applicableModuleName);
            long applicableModuleId = applicableModule.getModuleId();

            globalScopeVariableContext.setApplicableModuleId(applicableModuleId);
            globalScopeVariableContext.setStatus(Boolean.valueOf(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.STATUS).getText()));
            globalScopeVariableContext.setShowSwitch(Boolean.valueOf(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.SHOW_SWITCH).getText()));
            globalScopeVariableContext.setAppId(ApplicationApi.getApplicationIdForLinkName(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.APP_LINK_NAME).getText()));
            globalScopeVariableContext.setDescription(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.DESCRIPTION).getText());
            globalScopeVariableContext.setDisplayName(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.DISPLAY_NAME).getText());
            globalScopeVariableContext.setType(Integer.valueOf(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.TYPE).getText()));

            String valGenLinkName = globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.VALUE_GENERATOR_LINK_NAME).getText();
            if (StringUtils.isNotEmpty(valGenLinkName)) {
                ValueGeneratorContext valueGen = getValueGeneratorContextFromXMLBuilder(globalScopeVariableElement.getElement(PackageConstants.GlobalScopeVariableConstants.VALUE_GENERATOR));
                long valGenId = checkAndAddOrUpdate(valueGen, valGenLinkName);
                if (valGenId < 0) {
                    LOGGER.info("####SandBox - Skipping add of scopeVariable because ValueGeneratorId is null for ValueGeneratorLinkName - " + valGenLinkName);
                    continue;
                }
                globalScopeVariableContext.setValueGeneratorId(valGenId);
            } else {
                globalScopeVariableContext.setValueGeneratorId(null);
            }
            addOrUpdateGlobalScopeVariable(globalScopeVariableContext);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        List<GlobalScopeVariableContext> allScopeVariable = scopeBean.getAllScopeVariable();

        if (CollectionUtils.isNotEmpty(allScopeVariable)) {
            for (GlobalScopeVariableContext scopeVariable : allScopeVariable) {
                if (scopeVariable != null && scopeVariable.getId() > 0 && ids.contains(scopeVariable.getId()) && scopeVariable.getAppId() > 0) {
                    scopeBean.deleteScopeVariable(scopeVariable.getId(), scopeVariable.getAppId());
                }
            }
        }

    }

    private XMLBuilder getScopeVariableModulesFieldsListXMLBuilder(XMLBuilder builder, List<ScopeVariableModulesFields> component) throws Exception {
        XMLBuilder ScopeVariableModulesFieldsList = builder.element(PackageConstants.GlobalScopeVariableConstants.SCOPE_VARIABLE_MODULES_FIELDS_LIST);

        for (ScopeVariableModulesFields modulesField : component) {
            if (modulesField != null) {
                XMLBuilder modulesFields = ScopeVariableModulesFieldsList.element(PackageConstants.GlobalScopeVariableConstants.MODULES_FIELD);

                Long moduleId = modulesField.getModuleId();
                moduleId = moduleId > 0 ? moduleId : -1L;
                FacilioModule module = Constants.getModBean().getModule(moduleId);
                String moduleName = module != null ? module.getName() : "";

                Long scopeVariableId = modulesField.getScopeVariableId();
                GlobalScopeVariableContext scopeVariable = idVsScopeVariables.get(scopeVariableId);
                String scopeVariableLinkName = scopeVariable != null ? scopeVariable.getLinkName() : "";

                modulesFields.element(PackageConstants.GlobalScopeVariableConstants.MODULES_NAME).text(moduleName);
                modulesFields.element(PackageConstants.GlobalScopeVariableConstants.FIELD_NAME).text(modulesField.getFieldName());
                modulesFields.element(PackageConstants.GlobalScopeVariableConstants.LINK_NAME).text(scopeVariableLinkName);
            }
        }

        return ScopeVariableModulesFieldsList;
    }

    private List<ScopeVariableModulesFields> getScopeVariableModulesFieldsList(XMLBuilder builder) throws Exception {
        List<ScopeVariableModulesFields> modulesFieldsList = new ArrayList<>();
        List<XMLBuilder> modulesFieldList = builder.getFirstLevelElementListForTagName(PackageConstants.GlobalScopeVariableConstants.MODULES_FIELD);
        if (CollectionUtils.isNotEmpty(modulesFieldList)) {
            for (XMLBuilder moduleFieldXMLBuilder : modulesFieldList) {
                if (moduleFieldXMLBuilder != null) {
                    ScopeVariableModulesFields modulesField = new ScopeVariableModulesFields();

                    XMLBuilder moduleNameBuilder = moduleFieldXMLBuilder.getElement(PackageConstants.GlobalScopeVariableConstants.MODULES_NAME);
                    if (moduleNameBuilder == null) {
                        continue;
                    }
                    String moduleName = moduleNameBuilder.getText();
                    FacilioModule module = Constants.getModBean().getModule(moduleName);
                    Long moduleId = module != null ? module.getModuleId() : -1L;

                    String fieldName = moduleFieldXMLBuilder.getElement(PackageConstants.GlobalScopeVariableConstants.FIELD_NAME).getText();
                    fieldName = StringUtils.isNotEmpty(fieldName) ? fieldName : "";

                    if (StringUtils.isEmpty(moduleName) || module == null) {
                        LOGGER.info("####Sandbox - Skipping add GlobalScopeVariable since module is null for - " + moduleName);
                        return null;
                    }
                    if (StringUtils.isEmpty(fieldName) || Constants.getModBean().getField(fieldName, module.getName()) == null) {
                        LOGGER.info("####Sandbox - Skipping add GlobalScopeVariable since field is null for - " + fieldName);
                        return null;
                    }

                    String linkName = moduleFieldXMLBuilder.getElement(PackageConstants.GlobalScopeVariableConstants.LINK_NAME).getText();
                    GlobalScopeBean globalScopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
                    GlobalScopeVariableContext scopeVariableContext = globalScopeBean.getScopeVariable(linkName);
                    Long scopeVariableId = scopeVariableContext != null ? scopeVariableContext.getId() : -1L;

                    modulesField.setModuleId(moduleId);
                    modulesField.setFieldName(fieldName);
                    modulesField.setScopeVariableId(scopeVariableId);

                    modulesFieldsList.add(modulesField);
                }
            }
        }
        return modulesFieldsList;
    }

    public Long addOrUpdateGlobalScopeVariable(GlobalScopeVariableContext scopeVariableContext) throws Exception {
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new AddOrUpdateScopeVariable());
        FacilioContext context = c.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, scopeVariableContext);
        c.execute();
        Long id = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        return id > 0 ? id : -1L;
    }

    private Map<Long, ValueGeneratorContext> getAllValueGenerator() throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getValueGeneratorModule().getTableName())
                .select(FieldFactory.getValueGeneratorFields());
        List<Map<String, Object>> props = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {
            List<ValueGeneratorContext> valueGenerators = FieldUtil.getAsBeanListFromMapList(props, ValueGeneratorContext.class);

            return valueGenerators.stream().collect(Collectors.toMap(ValueGeneratorContext::getId, Function.identity()));
        }
        return new HashMap<>();
    }

    private XMLBuilder getValueGeneratorXMLBuilder(ValueGeneratorContext ValueGenerator, XMLBuilder scopeVariableElement) throws Exception {
        XMLBuilder valueGeneratorElement = scopeVariableElement.element(PackageConstants.GlobalScopeVariableConstants.VALUE_GENERATOR);
        if (ValueGenerator != null) {
            valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.DISPLAY_NAME).text(ValueGenerator.getDisplayName());
            valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.LINK_NAME).text(ValueGenerator.getLinkName());
            valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.VALUE_GENERATOR_TYPE).text(ValueGenerator.getValueGeneratorType().getValue());
            valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.IS_HIDDEN).text(String.valueOf(ValueGenerator.getIsHidden()));
            valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.IS_SYSTEM).text(String.valueOf(ValueGenerator.getIsSystem()));
            valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.IS_CONSTANT).text(String.valueOf(ValueGenerator.getIsConstant()));
            valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.SPECIAL_MODULE_NAME).text(ValueGenerator.getSpecialModuleName());
            valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.OPERATOR_ID).text(String.valueOf(ValueGenerator.getOperatorId()));

            Long moduleId = ValueGenerator.getModuleId();
            String moduleName = null;
            if (moduleId != null && moduleId > 0) {
                FacilioModule module = Constants.getModBean().getModule(moduleId);
                moduleName = module != null ? module.getName() : "";
            }

            valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.MODULE_NAME).text(moduleName);
        }
        return valueGeneratorElement;
    }

    private ValueGeneratorContext getValueGeneratorContextFromXMLBuilder(XMLBuilder valueGeneratorElement) throws Exception {
        ValueGeneratorContext valueGeneratorContext = null;
        if (valueGeneratorElement != null) {
            valueGeneratorContext = new ValueGeneratorContext();
            valueGeneratorContext.setLinkName(valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.LINK_NAME).getText());
            valueGeneratorContext.setDisplayName(valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.DISPLAY_NAME).getText());
            valueGeneratorContext.setIsConstant(Boolean.parseBoolean(valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.IS_CONSTANT).getText()));
            valueGeneratorContext.setIsSystem(Boolean.parseBoolean(valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.IS_SYSTEM).getText()));
            valueGeneratorContext.setIsHidden(Boolean.parseBoolean(valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.IS_HIDDEN).getText()));
            String valueGeneratorType = valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.VALUE_GENERATOR_TYPE).getText();
            valueGeneratorContext.setValueGeneratorType(ValueGeneratorContext.ValueGeneratorType.valueOf(valueGeneratorType));
            valueGeneratorContext.setOperatorId(Integer.valueOf(valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.OPERATOR_ID).getText()));

            String moduleName = valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.MODULE_NAME).getText();
            String specialModuleName = valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.SPECIAL_MODULE_NAME).getText();
            if (StringUtils.isNotEmpty(moduleName)) {
                FacilioModule module = Constants.getModBean().getModule(moduleName);
                long moduleId = module != null ? module.getModuleId() : -1L;
                valueGeneratorContext.setModuleId(moduleId);
            } else if (StringUtils.isNotEmpty(specialModuleName)) {
                valueGeneratorContext.setSpecialModuleName(specialModuleName);
            } else {
                LOGGER.info("###Sandbox - Error ValueGeneratorContext specialModuleName is null and moduleId is null for moduleName - " + moduleName + " for ValueGeneratorLinkName - " + valueGeneratorContext.getLinkName());
            }
        }
        return valueGeneratorContext;
    }

    private long checkAndAddOrUpdate(ValueGeneratorContext valueGenerator, String linkName) throws Exception {
        if (linkName.equals(valueGenerator.getLinkName())) {
            ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
            ValueGeneratorContext existingValueGen = valGenBean.getValueGenerator(linkName);

            if (existingValueGen != null && existingValueGen.getId() > 0) {
                GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getValueGeneratorModule().getTableName())
                        .fields(FieldFactory.getValueGeneratorFields())
                        .andCondition(CriteriaAPI.getIdCondition(existingValueGen.getId(), ModuleFactory.getValueGeneratorModule()));

                Map<String, Object> prop = FieldUtil.getAsProperties(valueGenerator);
                updateRecordBuilder.update(prop);

                return existingValueGen.getId();
            } else {
                List<Map<String, Object>> props = valGenBean.addValueGenerators(Arrays.asList(valueGenerator));
                Map<String, Object> propMap = props.get(0);
                long id = (Long) propMap.get("id");

                return id;
            }
        }
        return -1L;
    }
}
