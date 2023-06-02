package com.facilio.componentpackage.implementation;

import com.facilio.componentpackage.constants.PackageConstants.ModuleXMLConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.interfaces.PackageBean;
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

import java.util.*;
import java.util.stream.Collectors;

public class ModulePackageBeanImpl implements PackageBean<FacilioModule>  {
    public static final List<Integer> IGNORE_MODULE_TYPES = new ArrayList<Integer>(){{
        add(FacilioModule.ModuleType.NOTES.getValue());
        add(FacilioModule.ModuleType.PHOTOS.getValue());
        add(FacilioModule.ModuleType.ACTIVITY.getValue());
        add(FacilioModule.ModuleType.ATTACHMENTS.getValue());
        add(FacilioModule.ModuleType.LIVE_FORMULA.getValue());
        add(FacilioModule.ModuleType.RELATION_DATA.getValue());
        add(FacilioModule.ModuleType.ENUM_REL_MODULE.getValue());
        add(FacilioModule.ModuleType.SCHEDULED_FORMULA.getValue());
        add(FacilioModule.ModuleType.LOOKUP_REL_MODULE.getValue());
        add(FacilioModule.ModuleType.CLASSIFICATION_DATA.getValue());
        add(FacilioModule.ModuleType.LARGE_TEXT_DATA_MODULE.getValue());
        add(FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA.getValue());
        add(FacilioModule.ModuleType.CUSTOM_MODULE_DATA_FAILURE_CLASS_RELATIONSHIP.getValue());
    }};

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
        moduleElement.element(ModuleXMLConstants.STATEFLOW_ENABLED).text(String.valueOf(component.isStateFlowEnabled()));
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        Map<String, String> moduleNameVsErrorMessage = new HashMap<>();
        List<String> moduleNamesList = new ArrayList<>();

        for (XMLBuilder xmlBuilder : components) {
            XMLBuilder moduleElement = xmlBuilder.getElement(ComponentType.MODULE.getValue());
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
            FacilioModule module = moduleBean.getModule(moduleName);

            uniqueIdentifierVsModuleId.put(uniqueIdentifier, module.getModuleId());
        }
        return uniqueIdentifierVsModuleId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        String name, displayName, description, uniqueIdentifier;
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

                long newModuleId = addModule(displayName, description, moduleType, stateFlowEnabled);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), newModuleId);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        String name, displayName, description;
        boolean stateFlowEnabled;
        long moduleId;

        for (Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()) {
            moduleId = idVsComponent.getKey();
            XMLBuilder moduleElement = idVsComponent.getValue();

            name = moduleElement.getElement(PackageConstants.NAME).getText();
            description = moduleElement.getElement(ModuleXMLConstants.DESCRIPTION).getText();
            displayName = moduleElement.getElement(PackageConstants.DISPLAY_NAME).getText();
            stateFlowEnabled = Boolean.parseBoolean(moduleElement.getElement(ModuleXMLConstants.STATEFLOW_ENABLED).getText());

            updateModule(name, displayName, description, stateFlowEnabled);
        }
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
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(1), NumberOperators.NOT_EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("custom"), String.valueOf(fetchCustom), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("type"), StringUtils.join(IGNORE_MODULE_TYPES, ","), NumberOperators.NOT_EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                moduleIdVsParentIds.put((Long) prop.get("moduleId"), null);
            }
        }

        return moduleIdVsParentIds;
    }

    private long addModule(String displayName, String description, Integer moduleType, boolean stateFlowEnabled) throws Exception {
        FacilioChain addModulesChain = TransactionChainFactory.getAddModuleChain();

        FacilioContext context = addModulesChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
        context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, displayName);
        context.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, stateFlowEnabled);
        context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, null);
        context.put(FacilioConstants.ContextNames.FAILURE_REPORTING_ENABLED, false);

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
