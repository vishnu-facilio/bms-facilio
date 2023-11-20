package com.facilio.componentpackage.implementation;

import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.manager.NamedCondition;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.db.criteria.manager.NamedCriteria;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.db.criteria.Criteria;
import com.facilio.beans.ModuleBean;
import java.util.stream.Collectors;

import java.util.*;

@Log4j
public class NamedCriteriaPackageBeanImpl implements PackageBean<NamedCriteria> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, NamedCriteria> allNamedCriteriaAsMap = NamedCriteriaAPI.getAllNamedCriteriaAsMap(false);
        Map<Long, Long> criteriaIdVsModuleId = null;
        if (MapUtils.isNotEmpty(allNamedCriteriaAsMap)) {
            criteriaIdVsModuleId = allNamedCriteriaAsMap.values().stream()
                    .collect(Collectors.toMap(NamedCriteria::getId, NamedCriteria::getNamedCriteriaModuleId, (a, b) -> b));
        }
        return criteriaIdVsModuleId;
    }

    @Override
    public Map<Long, NamedCriteria> fetchComponents(List<Long> ids) throws Exception {
        return NamedCriteriaAPI.getCriteriaAsMap(ids);
    }

    @Override
    public void convertToXMLComponent(NamedCriteria namedCriteria, XMLBuilder element) throws Exception {
        // TODO - Handle SYSTEM_FUNCTION in NamedConditions
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = namedCriteria.getNamedCriteriaModuleId() > 0 ? moduleBean.getModule(namedCriteria.getNamedCriteriaModuleId()) : null;
        String moduleName = module != null ? module.getName() : null;
        element.element(PackageConstants.NamedCriteriaConstants.NAMED_CRITERIA_NAME).text(namedCriteria.getName());
        if (module != null) {
            element.element(PackageConstants.MODULENAME).text(moduleName);
        }
        element.element(PackageConstants.NamedCriteriaConstants.NAMED_CRITERIA_PATTERN).text(namedCriteria.getPattern());

        // NamedConditions
        if (MapUtils.isNotEmpty(namedCriteria.getConditions())) {
            XMLBuilder namedConditionsList = element.element(PackageConstants.NamedCriteriaConstants.NAMED_CONDITIONS_LIST);
            Map<String, NamedCondition> conditions = namedCriteria.getConditions();
            for (Map.Entry<String, NamedCondition> conditionObj : conditions.entrySet()) {
                XMLBuilder namedConditionElement = namedConditionsList.element(PackageConstants.NamedCriteriaConstants.NAMED_CONDITION);
                NamedCondition namedCondition = conditionObj.getValue();
                String sequenceNo = conditionObj.getKey();

                namedConditionElement.element(PackageConstants.NamedCriteriaConstants.NAMED_CONDITION_SEQUENCE).text(sequenceNo);
                namedConditionElement.element(PackageConstants.NamedCriteriaConstants.NAMED_CONDITION_NAME).text(namedCondition.getName());
                namedConditionElement.element(PackageConstants.NamedCriteriaConstants.NAMED_CONDITION_TYPE).text(namedCondition.getTypeEnum().name());

                switch (namedCondition.getTypeEnum()) {
                    case CRITERIA:
                        LOGGER.info("####Sandbox Tracking - Parsing Criteria - ModuleName - " + moduleName + " ButtonName - " + namedCriteria.getName());
                        namedConditionElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(namedCondition.getCriteria(),
                                namedConditionElement.element(PackageConstants.CriteriaConstants.CRITERIA), moduleName));
                        break;

                    case WORKFLOW:
                        namedConditionElement.addElement(PackageBeanUtil.constructBuilderFromWorkFlowContext(namedCondition.getWorkflowContext(),
                                namedConditionElement.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW_CONTEXT)));
                        break;

                    case SYSTEM_FUNCTION:
                    default:
                        break;
                }
            }
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
        // TODO - Use LinkName to fetch components instead of DisplayName
        Map<Long, NamedCriteria> allNamedCriteriaAsMap = NamedCriteriaAPI.getAllNamedCriteriaAsMap(false);
        Map<String, Map<String, Long>> moduleNameVsNameVsIdMap = new HashMap<>();

        if (!MapUtils.isNotEmpty(allNamedCriteriaAsMap)) {
            // no NamedCriteria in DB
            return null;
        } else {
            moduleNameVsNameVsIdMap = getModuleNameVsNameVsIdMap(allNamedCriteriaAsMap);
        }

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder element = idVsData.getValue();
            String moduleName = element.getElement(PackageConstants.MODULENAME).getText();
            String namedCriteriaName = element.getElement(PackageConstants.NamedCriteriaConstants.NAMED_CRITERIA_NAME).getText();

            if (StringUtils.isNotEmpty(moduleName) && moduleNameVsNameVsIdMap.containsKey(moduleName)
                    && moduleNameVsNameVsIdMap.get(moduleName).containsKey(namedCriteriaName)) {
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), moduleNameVsNameVsIdMap.get(moduleName).get(namedCriteriaName));
            }
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<Long, NamedCriteria> allNamedCriteriaAsMap = NamedCriteriaAPI.getAllNamedCriteriaAsMap(false);
        Map<String, Map<String, Long>> moduleNameVsNameVsIdMap = new HashMap<>();

        if (!MapUtils.isNotEmpty(allNamedCriteriaAsMap)) {
            // no NamedCriteria in DB
            return null;
        } else {
            moduleNameVsNameVsIdMap = getModuleNameVsNameVsIdMap(allNamedCriteriaAsMap);
        }

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            long criteriaId = -1;
            XMLBuilder element = idVsData.getValue();
            String moduleName = element.getElement(PackageConstants.MODULENAME).getText();
            FacilioModule module = StringUtils.isNotEmpty(moduleName) ? moduleBean.getModule(moduleName) : null;

            // check and add criteria
            NamedCriteria namedCriteria = constructNamedCriteriaFromBuilder(element, module);
            if (StringUtils.isNotEmpty(moduleName) && moduleNameVsNameVsIdMap.containsKey(moduleName)
                    && moduleNameVsNameVsIdMap.get(moduleName).containsKey(namedCriteria.getName())) {
                criteriaId = moduleNameVsNameVsIdMap.get(moduleName).get(namedCriteria.getName());
            }

            namedCriteria.setId(criteriaId);
            criteriaId = NamedCriteriaAPI.addOrUpdateNamedCriteria(namedCriteria, moduleName);

            uniqueIdentifierVsComponentId.put(idVsData.getKey(), criteriaId);
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long criteriaId = idVsData.getKey();
            XMLBuilder element = idVsData.getValue();
            String moduleName = element.getElement(PackageConstants.MODULENAME).getText();
            FacilioModule module = StringUtils.isNotEmpty(moduleName) ? moduleBean.getModule(moduleName) : null;

            NamedCriteria namedCriteria = constructNamedCriteriaFromBuilder(element, module);
            namedCriteria.setId(criteriaId);

            NamedCriteriaAPI.addOrUpdateNamedCriteria(namedCriteria, moduleName);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        NamedCriteriaAPI.bulkDeleteCriteria(ids);
    }

    private NamedCriteria constructNamedCriteriaFromBuilder(XMLBuilder element, FacilioModule module) throws Exception {
        String namedCriteriaName = element.getElement(PackageConstants.NamedCriteriaConstants.NAMED_CRITERIA_NAME).getText();
        String namedCriteriaPattern =  element.getElement(PackageConstants.NamedCriteriaConstants.NAMED_CRITERIA_PATTERN).getText();

        long moduleId = module != null ? module.getModuleId() : -1;

        NamedCriteria namedCriteria = new NamedCriteria();
        namedCriteria.setName(namedCriteriaName);
        namedCriteria.setPattern(namedCriteriaPattern);
        namedCriteria.setNamedCriteriaModuleId(moduleId);

        XMLBuilder namedConditionsListElement = element.getElement(PackageConstants.NamedCriteriaConstants.NAMED_CONDITIONS_LIST);
        if (namedConditionsListElement != null) {
            List<XMLBuilder> namedConditionsList = namedConditionsListElement.getFirstLevelElementListForTagName(PackageConstants.NamedCriteriaConstants.NAMED_CONDITION);
            Map<String, NamedCondition> namedConditions = new LinkedHashMap<>();
            for (XMLBuilder namedConditionElement : namedConditionsList) {
                int sequenceNo = Integer.parseInt(namedConditionElement.getElement(PackageConstants.NamedCriteriaConstants.NAMED_CONDITION_SEQUENCE).getText());
                String namedConditionStr = namedConditionElement.getElement(PackageConstants.NamedCriteriaConstants.NAMED_CONDITION_TYPE).getText();
                String conditionName = namedConditionElement.getElement(PackageConstants.NamedCriteriaConstants.NAMED_CONDITION_NAME).getText();
                NamedCondition.Type namedConditionType = StringUtils.isNotEmpty(namedConditionStr) ? NamedCondition.Type.valueOf(namedConditionStr) : null;

                NamedCondition namedCondition = new NamedCondition();
                namedCondition.setName(conditionName);
                namedCondition.setSequence(sequenceNo);
                namedCondition.setType(namedConditionType);

                if (namedConditionType != null) {
                    switch (namedConditionType) {
                        case CRITERIA:
                            XMLBuilder criteriaElement = namedConditionElement.getElement(PackageConstants.CriteriaConstants.CRITERIA);
                            if (criteriaElement != null) {
                                Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
                                namedCondition.setCriteria(criteria);
                            }
                            break;

                        case WORKFLOW:
                            XMLBuilder workFlowElement = namedConditionElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_CONTEXT);
                            if (workFlowElement != null) {
                                WorkflowContext workflowContext = PackageBeanUtil.constructWorkflowContextFromBuilder(workFlowElement);
                                namedCondition.setWorkflowContext(workflowContext);
                            }
                            break;

                        case SYSTEM_FUNCTION:
                        default:
                            break;
                    }
                }
                namedConditions.put(Integer.toString(sequenceNo), namedCondition);
            }
            namedCriteria.setConditions(namedConditions);
        }
        return namedCriteria;
    }

    private Map<String, Map<String, Long>> getModuleNameVsNameVsIdMap(Map<Long, NamedCriteria> allNamedCriteriaAsMap) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        Map<Long, Map<String, Long>> moduleIdVsNameVsIdMap = new HashMap<>();
        Map<String, Map<String, Long>> moduleNameVsNameVsIdMap = new HashMap<>();

        for (NamedCriteria namedCriteria : allNamedCriteriaAsMap.values()) {
            if (!moduleIdVsNameVsIdMap.containsKey(namedCriteria.getNamedCriteriaModuleId())) {
                moduleIdVsNameVsIdMap.put(namedCriteria.getNamedCriteriaModuleId(), new HashMap<>());
            }
            moduleIdVsNameVsIdMap.get(namedCriteria.getNamedCriteriaModuleId()).put(namedCriteria.getName(), namedCriteria.getId());
        }

        for (Long moduleId : moduleIdVsNameVsIdMap.keySet()) {
            FacilioModule facilioModule = moduleBean.getModule(moduleId);
            if (facilioModule != null) {
                moduleNameVsNameVsIdMap.put(facilioModule.getName(), moduleIdVsNameVsIdMap.get(moduleId));
            }
        }
        return moduleNameVsNameVsIdMap;
    }
}
