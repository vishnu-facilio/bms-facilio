package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateFlowPackageBeanImpl implements PackageBean<WorkflowRuleContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return WorkflowRuleAPI.getAllRuleIdVsModuleId(WorkflowRuleContext.RuleType.STATE_FLOW);
    }

    @Override
    public Map<Long, WorkflowRuleContext> fetchComponents(List<Long> ids) throws Exception {
        return WorkflowRuleAPI.getWorkFlowRules(WorkflowRuleContext.RuleType.STATE_FLOW, ids);
    }

    @Override
    public void convertToXMLComponent(WorkflowRuleContext stateflow, XMLBuilder element) throws Exception {

        element.element(PackageConstants.NAME).text(stateflow.getName());
        element.element(PackageConstants.DESCRIPTION).text(stateflow.getDescription());
        element.element(PackageConstants.MODULENAME).text(stateflow.getModuleName());
        element.element(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE)
                .text(stateflow.getActivityTypeEnum() != null ? stateflow.getActivityTypeEnum().name() : null);
        element.element("defaultStateflow").text(String.valueOf(((StateFlowRuleContext) stateflow).isDefaltStateFlow()));
        long statusId = ((StateFlowRuleContext)stateflow).getDefaultStateId();
        String defaultStateName = TicketAPI.getStatus(statusId).getStatus();
        element.element("defaultStateName").text(defaultStateName);
        element.element(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).text(stateflow.getRuleTypeEnum().name());
        element.element("draft").text(String.valueOf(((StateFlowRuleContext) stateflow).isDraft()));
        element.element("draftParentId").text(String.valueOf(((StateFlowRuleContext) stateflow).getDraftParentId()));
        element.element(PackageConstants.WorkFlowRuleConstants.EXECUTION_ORDER).text(String.valueOf(stateflow.getExecutionOrder()));
        element.element(PackageConstants.WorkFlowRuleConstants.STATUS).text(String.valueOf(stateflow.isActive()));
        element.element(PackageConstants.WorkFlowRuleConstants.ON_SUCCESS).text(String.valueOf(stateflow.isOnSuccess()));

        if (stateflow.getCriteria() != null) {
            element.addElement(PackageBeanUtil.constructBuilderFromCriteria(stateflow.getCriteria(), element.element(PackageConstants.CriteriaConstants.CRITERIA), stateflow.getModuleName()));
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
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        StateFlowRuleContext stateFlowRule;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder builder = idVsData.getValue();
            stateFlowRule = constructStateflowFromBuilder(builder, moduleBean);

            long stateFlowId = addOrUpdateStateflow(stateFlowRule);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), stateFlowId);
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        StateFlowRuleContext stateFlowRuleContext;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long ruleId = idVsData.getKey();
            if (ruleId == null || ruleId < 0) {
                continue;
            }

            XMLBuilder builder = idVsData.getValue();

            stateFlowRuleContext = constructStateflowFromBuilder(builder, moduleBean);
            stateFlowRuleContext.setId(ruleId);

            addOrUpdateStateflow(stateFlowRuleContext);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    private long addOrUpdateStateflow(StateFlowRuleContext stateFlowRuleContext) throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlow();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(stateFlowRuleContext.getModuleId());

        WorkflowRuleContext existingStateflow =  WorkflowRuleAPI.getWorkflowRule(stateFlowRuleContext.getName(),module,
                stateFlowRuleContext.getRuleTypeEnum());

        if (existingStateflow != null){
            stateFlowRuleContext.setId(existingStateflow.getId());
        }

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, stateFlowRuleContext);
        context.put(FacilioConstants.ContextNames.STATE_FLOW_PUBLISH,true);
        chain.execute();

        stateFlowRuleContext = (StateFlowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        return stateFlowRuleContext.getId();
    }

    public StateFlowRuleContext constructStateflowFromBuilder(XMLBuilder builder, ModuleBean moduleBean)throws Exception{

        String name = builder.getElement(PackageConstants.NAME).getText();
        String description = builder.getElement(PackageConstants.DESCRIPTION).getText();
        String moduleName = builder.getElement(PackageConstants.MODULENAME).getText();
        FacilioModule module = moduleBean.getModule(moduleName);
        String activityTypeStr = builder.getElement(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE).getText();
        int executionOrder = Integer.parseInt(builder.getElement(PackageConstants.WorkFlowRuleConstants.EXECUTION_ORDER).getText());
        boolean status = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.STATUS).getText());
        String ruleTypeStr = builder.getElement(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).getText();
        boolean onSuccess = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.ON_SUCCESS).getText());
        String defaultStateName = builder.getElement("defaultStateName").getText();
        FacilioStatus defaultStatus = TicketAPI.getStatus(module,defaultStateName);
        long defaultStateId = defaultStatus.getId();
        boolean draft = Boolean.parseBoolean(builder.getElement("draft").getText());
        long draftParentId = Long.parseLong(builder.getElement("draftParentId").getText());
        boolean defaultStateFlow = Boolean.parseBoolean(builder.getElement("defaultStateflow").getText());

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName(name);
        stateFlowRuleContext.setDescription(description);
        stateFlowRuleContext.setModuleName(moduleName);
        EventType activityType = StringUtils.isNotEmpty(activityTypeStr) ? EventType.valueOf(activityTypeStr) : null;
        stateFlowRuleContext.setActivityType(activityType);
        stateFlowRuleContext.setExecutionOrder(executionOrder);
        stateFlowRuleContext.setStatus(status);
        WorkflowRuleContext.RuleType ruleType = StringUtils.isNotEmpty(ruleTypeStr) ? WorkflowRuleContext.RuleType.valueOf(ruleTypeStr) : null;
        stateFlowRuleContext.setRuleType(ruleType);
        stateFlowRuleContext.setOnSuccess(onSuccess);
        stateFlowRuleContext.setDefaultStateId(defaultStateId);
        stateFlowRuleContext.setDraft(draft);
        stateFlowRuleContext.setDraftParentId(draftParentId);
        stateFlowRuleContext.setDefaltStateFlow(defaultStateFlow);

        XMLBuilder criteriaElement = builder.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            stateFlowRuleContext.setCriteria(criteria);
        }

        return stateFlowRuleContext;
    }
}
