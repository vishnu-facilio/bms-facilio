package com.facilio.componentpackage.implementation;

import com.facilio.componentpackage.constants.PackageConstants.ModuleConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.modules.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModulePackageBeanImpl implements PackageBean<FacilioModule>  {


    @Override
    public List<Long> fetchAllComponentIdsToPackage() throws Exception {
        return null;
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
            moduleList.forEach(module -> moduleIdVsModuleMap.put(module.getModuleId(), module));

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());
        }

        return moduleIdVsModuleMap;
    }

    @Override
    public XMLBuilder convertToXMLComponent(FacilioModule component) throws Exception {
        XMLBuilder moduleElement = XMLBuilder.create(ComponentType.MODULE.getValue());
        moduleElement.element(PackageConstants.NAME).text(component.getName());
        moduleElement.element(ModuleConstants.DESCRIPTION).text(component.getDescription());
        moduleElement.element(PackageConstants.DISPLAY_NAME).text(component.getDisplayName());
        moduleElement.element(ModuleConstants.MODULE_TYPE).text(String.valueOf(component.getType()));
        moduleElement.element(ModuleConstants.STATEFLOW_ENABLED).text(String.valueOf(component.isStateFlowEnabled()));

        return moduleElement;
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        Map<String, String> moduleNameVsErrorMessage = new HashMap<>();
        List<String> moduleNamesList = new ArrayList<>();

        for (XMLBuilder xmlBuilder : components) {
            for (XMLBuilder moduleElement : xmlBuilder.getElementList(ComponentType.MODULE.getValue())) {
                String moduleName = moduleElement.getElement(PackageConstants.NAME).getText();
                moduleNamesList.add(moduleName);
            }
        }

        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> moduleList = moduleBean.getModuleList(moduleNamesList);

        if (CollectionUtils.isNotEmpty(moduleList)) {
            for (FacilioModule module : moduleList) {
                moduleNameVsErrorMessage.put(module.getName(), ModuleConstants.DUPLICATE_MODULE_ERROR);
            }
        }

        return moduleNameVsErrorMessage;
    }

    @Override
    public Map<String, Long> createComponentFromXML(List<XMLBuilder> components) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        String displayName, description, uniqueIdentifier;
        boolean stateFlowEnabled;
        int moduleType;

        for (XMLBuilder xmlBuilder : components) {
            for (XMLBuilder moduleElement : xmlBuilder.getElementList(ComponentType.MODULE.getValue())) {
                description = moduleElement.getElement(ModuleConstants.DESCRIPTION).getText();
                moduleType = moduleElement.getElement(ModuleConstants.MODULE_TYPE).getText() != null ?
                        Integer.parseInt(moduleElement.getElement(ModuleConstants.MODULE_TYPE).getText()) :
                        FacilioModule.ModuleType.BASE_ENTITY.getValue();
                displayName = moduleElement.getElement(PackageConstants.DISPLAY_NAME).getText();
                stateFlowEnabled = Boolean.parseBoolean(moduleElement.getElement(ModuleConstants.STATEFLOW_ENABLED).getText());
                uniqueIdentifier = moduleElement.getElement(PackageConstants.UNIQUE_IDENTIFIER).getText();

                long newModuleId = addModule(displayName, description, moduleType, stateFlowEnabled);
                uniqueIdentifierVsComponentId.put(uniqueIdentifier, newModuleId);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> updateComponentFromXML(List<XMLBuilder> components) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        String name, displayName, description, uniqueIdentifier;
        boolean stateFlowEnabled;

        for (XMLBuilder xmlBuilder : components) {
            for (XMLBuilder moduleElement : xmlBuilder.getElementList(ComponentType.MODULE.getValue())) {
                name = moduleElement.getElement(PackageConstants.NAME).getText();
                description = moduleElement.getElement(ModuleConstants.DESCRIPTION).getText();
                displayName = moduleElement.getElement(PackageConstants.DISPLAY_NAME).getText();
                stateFlowEnabled = Boolean.parseBoolean(moduleElement.getElement(ModuleConstants.STATEFLOW_ENABLED).getText());
                uniqueIdentifier = moduleElement.getElement(PackageConstants.UNIQUE_IDENTIFIER).getText();

                long newModuleId = updateModule(name, displayName, description, stateFlowEnabled);
                uniqueIdentifierVsComponentId.put(uniqueIdentifier, newModuleId);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        // delete operation is not supported for modules
    }

    private long addModule(String displayName, String description, Integer moduleType, boolean stateFlowEnabled) throws Exception {
        FacilioChain addModulesChain = TransactionChainFactory.getAddModuleChain();

        FacilioContext context = addModulesChain.getContext();
        context.put(ModuleConstants.DESCRIPTION, description);
        context.put(PackageConstants.DISPLAY_NAME, displayName);
        context.put(ModuleConstants.STATEFLOW_ENABLED, stateFlowEnabled);
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
        context.put(PackageConstants.MODULENAME, moduleName);
        context.put(ModuleConstants.DESCRIPTION, description);
        context.put(PackageConstants.DISPLAY_NAME, displayName);
        context.put(ModuleConstants.STATEFLOW_ENABLED, stateFlowEnabled);

        updateModulesChain.execute();

        FacilioModule facilioModule = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        return facilioModule.getModuleId();
    }
}
