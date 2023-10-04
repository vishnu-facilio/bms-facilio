package com.facilio.componentpackage.implementation;

import com.facilio.componentpackage.constants.PackageConstants.ModuleXMLConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.modules.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.lang3.StringUtils;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldFactory;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.chain.FacilioContext;
import com.facilio.chain.FacilioChain;
import com.facilio.beans.ModuleBean;
import lombok.extern.log4j.Log4j;

import java.util.*;

@Log4j
public class ModulePackageBeanImpl implements PackageBean<FacilioModule>  {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getModuleIdVsParents(false);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getModuleIdVsParents(true);
    }

    @Override
    public Map<Long, FacilioModule> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, FacilioModule> moduleIdVsModuleMap = new HashMap<>();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);
        ModuleBean moduleBean = Constants.getModBean();

        List<Long> idsSubList;
        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);
            Criteria moduleIdCriteria = new Criteria();
            FacilioField moduleIdField = FieldFactory.getModuleIdField(ModuleFactory.getModuleModule());
            moduleIdCriteria.addAndCondition(CriteriaAPI.getCondition(moduleIdField, idsSubList, NumberOperators.EQUALS));

            List<FacilioModule> moduleList = moduleBean.getModuleList(moduleIdCriteria);
            if (CollectionUtils.isNotEmpty(moduleList)) {
                moduleList.forEach(module -> moduleIdVsModuleMap.put(module.getModuleId(), module));
            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());
        }

        return moduleIdVsModuleMap;
    }

    @Override
    public void convertToXMLComponent(FacilioModule component, XMLBuilder moduleElement) throws Exception {
        moduleElement.element(PackageConstants.NAME).text(component.getName());
        moduleElement.element(ModuleXMLConstants.DESCRIPTION).text(component.getDescription());
        moduleElement.element(PackageConstants.DISPLAY_NAME).text(component.getDisplayName());
        moduleElement.element(ModuleXMLConstants.MODULE_TYPE).text(String.valueOf(component.getType()));
        moduleElement.element(PackageConstants.IS_CUSTOM).text(String.valueOf(component.isCustom()));
        moduleElement.element(ModuleXMLConstants.STATEFLOW_ENABLED).text(String.valueOf(component.isStateFlowEnabled()));
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        Map<String, String> moduleNameVsErrorMessage = new HashMap<>();
        List<String> moduleNamesList = new ArrayList<>();

        for (XMLBuilder moduleElement : components) {
            String moduleName = moduleElement.getElement(PackageConstants.NAME).getText();
            moduleNamesList.add(moduleName);
        }

        int fromIndex = 0;
        int toIndex = Math.min(moduleNamesList.size(), 250);
        ModuleBean moduleBean = Constants.getModBean();

        List<String> namesSubList;
        while (fromIndex < moduleNamesList.size()) {
            namesSubList = moduleNamesList.subList(fromIndex, toIndex);

            Criteria moduleNameCriteria = new Criteria();
            FacilioField moduleNameField = FieldFactory.getStringField("NAME", "name", ModuleFactory.getModuleModule());
            moduleNameCriteria.addAndCondition(CriteriaAPI.getCondition(moduleNameField, StringUtils.join(namesSubList, ","), StringOperators.IS));

            List<FacilioModule> moduleList = moduleBean.getModuleList(moduleNameCriteria);

            if (CollectionUtils.isNotEmpty(moduleList)) {
                for (FacilioModule module : moduleList) {
                    moduleNameVsErrorMessage.put(module.getName(), ModuleXMLConstants.DUPLICATE_MODULE_ERROR);
                }
            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), moduleNamesList.size());
        }

        return moduleNameVsErrorMessage;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        Criteria moduleIdCriteria = new Criteria();
        moduleIdCriteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", StringUtils.join(componentIds, ","), NumberOperators.EQUALS));

        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> dbModuleList = moduleBean.getModuleList(moduleIdCriteria);

        List<Long> missingComponentIds = new ArrayList<>(componentIds);
        List<Long> availableComponentIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dbModuleList)) {
            dbModuleList.forEach(module -> availableComponentIds.add(module.getModuleId()));
        }

        missingComponentIds.removeAll(availableComponentIds);

        return missingComponentIds;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        Map<String, Long> uniqueIdentifierVsModuleId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder moduleElement = idVsData.getValue();
            String moduleName = moduleElement.getElement(PackageConstants.NAME).getText();
            boolean isCustom = Boolean.parseBoolean(moduleElement.getElement(PackageConstants.IS_CUSTOM).getText());

            if (!isCustom) {
                FacilioModule module = moduleBean.getModule(moduleName);
                if (module != null) {
                    uniqueIdentifierVsModuleId.put(uniqueIdentifier, module.getModuleId());
                } else {
                    LOGGER.info("###Sandbox - Module not found - " + moduleName);
                }
            }
        }
        return uniqueIdentifierVsModuleId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        String name, displayName, description;
        boolean stateFlowEnabled;
        int moduleType;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder moduleElement = idVsData.getValue();
            if (moduleElement != null) {
                name = moduleElement.getElement(PackageConstants.NAME).getText();
                description = moduleElement.getElement(ModuleXMLConstants.DESCRIPTION).getText();
                moduleType = moduleElement.getElement(ModuleXMLConstants.MODULE_TYPE).getText() != null ?
                        Integer.parseInt(moduleElement.getElement(ModuleXMLConstants.MODULE_TYPE).getText()) :
                        FacilioModule.ModuleType.BASE_ENTITY.getValue();
                displayName = moduleElement.getElement(PackageConstants.DISPLAY_NAME).getText();
                stateFlowEnabled = Boolean.parseBoolean(moduleElement.getElement(ModuleXMLConstants.STATEFLOW_ENABLED).getText());

                long newModuleId = addModule(name, displayName, description, moduleType, stateFlowEnabled);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), newModuleId);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        String name, displayName, description;
        boolean stateFlowEnabled, isCustom;

        for (Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()) {
            XMLBuilder moduleElement = idVsComponent.getValue();

            name = moduleElement.getElement(PackageConstants.NAME).getText();
            description = moduleElement.getElement(ModuleXMLConstants.DESCRIPTION).getText();
            displayName = moduleElement.getElement(PackageConstants.DISPLAY_NAME).getText();
            stateFlowEnabled = Boolean.parseBoolean(moduleElement.getElement(ModuleXMLConstants.STATEFLOW_ENABLED).getText());
            isCustom = Boolean.parseBoolean(moduleElement.getElement(PackageConstants.IS_CUSTOM).getText());

            if (isCustom) {
                updateModule(name, displayName, description, stateFlowEnabled);
            }
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        // delete operation is not supported for modules
    }

    private Map<Long, Long> getModuleIdVsParents(boolean fetchCustom) throws Exception {
        Map<Long, Long> moduleIdVsParentIds = new HashMap<>();
        FacilioModule moduleModule = ModuleFactory.getModuleModule();
        List<FacilioField> moduleFields = FieldFactory.getModuleFields();
        FacilioField moduleIdField = FieldFactory.getModuleIdField(moduleModule);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(moduleFields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(moduleModule.getTableName())
                .select(Collections.singletonList(moduleIdField))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("custom"), String.valueOf(fetchCustom), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("tableName"), "AssetCustomModuleData", StringOperators.ISN_T))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("type"), StringUtils.join(PackageBeanUtil.INCLUDE_MODULE_TYPES, ","), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                moduleIdVsParentIds.put((Long) prop.get("moduleId"), -1L);
            }
        }

        return moduleIdVsParentIds;
    }

    private long addModule(String name, String displayName, String description, Integer moduleType, boolean stateFlowEnabled) throws Exception {
        FacilioChain addModulesChain = TransactionChainFactory.getAddModuleChain();

        FacilioContext context = addModulesChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, name);
        context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
        context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, displayName);
        context.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, stateFlowEnabled);
        context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, null);
        context.put(FacilioConstants.ContextNames.FAILURE_REPORTING_ENABLED, false);
        context.put(PackageConstants.USE_LINKNAME_FROM_CONTEXT, true);
        context.put(FacilioConstants.Module.SKIP_EXISTING_MODULE_WITH_SAME_NAME_CHECK, true);
        addModulesChain.execute();

        FacilioModule newModule = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        return newModule.getModuleId();
    }

    private long updateModule(String moduleName, String displayName, String description, boolean stateFlowEnabled) throws Exception {
        FacilioChain updateModulesChain = TransactionChainFactory.getUpdateModuleChain();

        FacilioContext context = updateModulesChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
        context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, displayName);
        context.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, stateFlowEnabled);

        updateModulesChain.execute();

        FacilioModule facilioModule = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        return facilioModule.getModuleId();
    }
}
