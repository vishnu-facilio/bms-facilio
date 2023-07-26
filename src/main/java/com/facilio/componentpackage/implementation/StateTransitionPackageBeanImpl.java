package com.facilio.componentpackage.implementation;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateTransitionPackageBeanImpl implements PackageBean<WorkflowRuleContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return WorkflowRuleAPI.getAllRuleIdVsModuleId(WorkflowRuleContext.RuleType.STATE_RULE);
    }

    @Override
    public Map<Long, WorkflowRuleContext> fetchComponents(List<Long> ids) throws Exception {
        return WorkflowRuleAPI.getWorkFlowRules(WorkflowRuleContext.RuleType.STATE_RULE, ids);
    }

    @Override
    public void convertToXMLComponent(WorkflowRuleContext stateflowTransition, XMLBuilder element) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        element.element(PackageConstants.MODULENAME).text(stateflowTransition.getModuleName());
        long formStateId = ((AbstractStateTransitionRuleContext) stateflowTransition).getFromStateId();
        FacilioStatus fromState = TicketAPI.getStatus(formStateId);
        element.element("fromStateName").text(fromState.getStatus());
        long toStateId = ((AbstractStateTransitionRuleContext) stateflowTransition).getToStateId();
        FacilioStatus toState = TicketAPI.getStatus(toStateId);
        element.element("toStateName").text(toState.getStatus());
        long stateFlowId = ((AbstractStateTransitionRuleContext) stateflowTransition).getStateFlowId();
        StateFlowRuleContext stateflow = StateFlowRulesAPI.getStateFlowContext(stateFlowId);
        element.element("stateflowName").text(stateflow.getName());
        element.element("type").text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).getType()));
        element.element("name").text(stateflowTransition.getName());
        element.element(PackageConstants.WorkFlowRuleConstants.STATUS).text(String.valueOf(stateflowTransition.isActive()));
        element.element(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE)
                .text(stateflowTransition.getActivityTypeEnum() != null ? stateflowTransition.getActivityTypeEnum().name() : null);
        element.element(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).text(stateflowTransition.getRuleTypeEnum().name());
        element.element("shouldExecuteFromPermalink").text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).isShouldExecuteFromPermalink()));
        element.element("showInClientPortal").text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).isShowInClientPortal()));
        element.element("showInOccupantPortal").text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).isShowInOccupantPortal()));
        element.element("showInTenantPortal").text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).isShowInTenantPortal()));
        element.element("showInVendorPortal").text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).isShowInVendorPortal()));

        if (((ApproverWorkflowRuleContext)stateflowTransition).getAllApprovalRequired() != null){
            element.element("allApprovalRequired").text(String.valueOf(((ApproverWorkflowRuleContext) stateflowTransition).getAllApprovalRequired()));
        }
        if (((ApproverWorkflowRuleContext) stateflowTransition).getApprovers() != null){
            XMLBuilder approverList = element.element("ApproverList");
            XMLBuilder approverElemet = approverList.element("approvers");
            SharingContext<ApproverContext> approvers = ((AbstractStateTransitionRuleContext) stateflowTransition).getApprovers();
            for (ApproverContext approverContext : approvers) {
                long userId = approverContext.getUserId();
                User user = AccountUtil.getUserBean().getUser(userId, true);
                if (user != null ) {
                    approverElemet.element("userName").text(user.getName());
                }
                approverElemet.element("type").text(String.valueOf(approverContext.getType()));

            }
        }
        if (((AbstractStateTransitionRuleContext) stateflowTransition).getButtonType() > 0){
            element.element("buttonType").text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).getButtonType()));
        }
        if (((AbstractStateTransitionRuleContext) stateflowTransition).getDialogType() > 0){
            element.element("dialogType").text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).getDialogType()));
        }
        if (((AbstractStateTransitionRuleContext) stateflowTransition).getFormId() > 0){
            long formId = ((AbstractStateTransitionRuleContext) stateflowTransition).getFormId();
            FacilioForm form = FormsAPI.getFormFromDB(formId);
            element.element("formName").text(form.getName());
        }
        if (((AbstractStateTransitionRuleContext) stateflowTransition).getFormModuleName() != null){
            element.element("formModuleName").text(((AbstractStateTransitionRuleContext) stateflowTransition).getFormModuleName());
        }
        if (((StateflowTransitionContext) stateflowTransition).getIsOffline() != null){
            element.element("isOffline").text(String.valueOf(((StateflowTransitionContext) stateflowTransition).getIsOffline()));
        }
        element.element("shouldFormInterfaceApply").text(String.valueOf(((StateflowTransitionContext) stateflowTransition).shouldFormInterfaceApply()));
        if (((StateflowTransitionContext) stateflowTransition).getLocationFieldId() > 0){
            long locationfieldId = ((StateflowTransitionContext) stateflowTransition).getLocationFieldId();
            FacilioField locationField = moduleBean.getField(locationfieldId);
            element.element("locationFieldName").text(locationField.getName());
        }
        if (((StateflowTransitionContext) stateflowTransition).getLocationLookupFieldId() > 0){
            long locationLookupFieldId = ((StateflowTransitionContext) stateflowTransition).getLocationLookupFieldId();
            FacilioField locationLookupField = moduleBean.getField(locationLookupFieldId);
            element.element("locationLookupFieldName").text(locationLookupField.getName());
            element.element("locationLookupFieldModuleName").text(locationLookupField.getModule().getName());
        }
        if (((StateflowTransitionContext) stateflowTransition).getRadius() != null){
            element.element("radius").text(String.valueOf(((StateflowTransitionContext) stateflowTransition).getRadius()));
        }
        if (((StateflowTransitionContext) stateflowTransition).getQrFieldId() > 0){
            long qrFieldId = ((StateflowTransitionContext) stateflowTransition).getQrFieldId();
            FacilioField qrField = moduleBean.getField(qrFieldId);
            element.element("qrFieldName").text(qrField.getName());
        }
        if (((StateflowTransitionContext) stateflowTransition).getQrLookupFieldId() != null){
            long qrLookupFieldId = ((StateflowTransitionContext) stateflowTransition).getQrLookupFieldId();
            FacilioField qrLookupField = moduleBean.getField(qrLookupFieldId);
            element.element("qrLookupFieldName").text(qrLookupField.getName());
            element.element("qrLookupFieldModuleName").text(qrLookupField.getModule().getName());
        }
        if (((StateflowTransitionContext) stateflowTransition).getScheduleTime() > 0){
            element.element("scheduleTime").text(String.valueOf(((StateflowTransitionContext) stateflowTransition).getScheduleTime()));
        }


        if (((StateflowTransitionContext) stateflowTransition).getValidations() != null){
            PackageBeanUtil.constructBuilderFromValidationList(((StateflowTransitionContext) stateflowTransition).getValidations(),element.element("validationList"));
        }
        if (CollectionUtils.isNotEmpty(((StateflowTransitionContext) stateflowTransition).getConfirmationDialogs())){
            PackageBeanUtil.constructBuilderFromConfirmationDialogList(((StateflowTransitionContext) stateflowTransition).getConfirmationDialogs(),element.element("confirmationList"));
        }
        if (CollectionUtils.isNotEmpty(stateflowTransition.getActions())) {
            PackageBeanUtil.constructBuilderFromActionsList(stateflowTransition.getActions(), element.element(PackageConstants.WorkFlowRuleConstants.ACTIONS_LIST));
        }

        if (stateflowTransition.getCriteria() != null) {
            element.addElement(PackageBeanUtil.constructBuilderFromCriteria(stateflowTransition.getCriteria(), element.element(PackageConstants.CriteriaConstants.CRITERIA), stateflowTransition.getModuleName()));
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
        StateflowTransitionContext stateflowTransition;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder builder = idVsData.getValue();
            stateflowTransition = constructStateTransitionFromBuilder(builder, moduleBean);

            long ruleId = addOrUpdateStateTransition(stateflowTransition);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), ruleId);
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        StateflowTransitionContext stateflowTransition;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long ruleId = idVsData.getKey();
            if (ruleId == null || ruleId < 0) {
                continue;
            }

            XMLBuilder builder = idVsData.getValue();

            stateflowTransition = constructStateTransitionFromBuilder(builder, moduleBean);
            stateflowTransition.setId(ruleId);

            addOrUpdateStateTransition(stateflowTransition);
        }
    }

    private long addOrUpdateStateTransition(StateflowTransitionContext stateTransition) throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlowTransition();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(stateTransition.getModuleId());

        WorkflowRuleContext existingStateTransition = WorkflowRuleAPI.getWorkflowRule(stateTransition.getName(),module,
                stateTransition.getRuleTypeEnum());

        if (existingStateTransition != null){
            stateTransition.setId(existingStateTransition.getId());
        }

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateTransition);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, stateTransition.getModuleName());
        chain.execute();

        stateTransition = (StateflowTransitionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        return stateTransition.getId();
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    private StateflowTransitionContext constructStateTransitionFromBuilder(XMLBuilder builder, ModuleBean moduleBean) throws Exception{

        String name = builder.getElement(PackageConstants.NAME).getText();
        String moduleName = builder.getElement(PackageConstants.MODULENAME).getText();
        FacilioModule module = moduleBean.getModule(moduleName);
        String activityTypeStr = builder.getElement(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE).getText();
        boolean status = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.STATUS).getText());
        String ruleTypeStr = builder.getElement(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).getText();
        String fromStateName = builder.getElement("fromStateName").getText();
        FacilioStatus fromState = TicketAPI.getStatus(module,fromStateName);
        String toStateName = builder.getElement("toStateName").getText();
        FacilioStatus toState = TicketAPI.getStatus(module,toStateName);
        boolean shouldExecuteFromPermaLink = Boolean.parseBoolean(builder.getElement("shouldExecuteFromPermalink").getText());
        boolean showInClientPortal = Boolean.parseBoolean(builder.getElement("showInClientPortal").getText());
        boolean showInOccupantPortal = Boolean.parseBoolean(builder.getElement("showInOccupantPortal").getText());
        boolean showInTenantPortal = Boolean.parseBoolean(builder.getElement("showInTenantPortal").getText());
        boolean showInVendorPortal = Boolean.parseBoolean(builder.getElement("showInVendorPortal").getText());
        int type = Integer.parseInt(builder.getElement("type").getText());
        String stateflowName = builder.getElement("stateflowName").getText();
        WorkflowRuleContext stateflow = WorkflowRuleAPI.getWorkflowRule(stateflowName, module,WorkflowRuleContext.RuleType.STATE_FLOW);



        StateflowTransitionContext stateflowTransition = new StateflowTransitionContext();
        stateflowTransition.setName(name);
        stateflowTransition.setModuleName(moduleName);
        EventType activityType = StringUtils.isNotEmpty(activityTypeStr) ? EventType.valueOf(activityTypeStr) : null;
        stateflowTransition.setActivityType(activityType);
        stateflowTransition.setStatus(status);
        WorkflowRuleContext.RuleType ruleType = StringUtils.isNotEmpty(ruleTypeStr) ? WorkflowRuleContext.RuleType.valueOf(ruleTypeStr) : null;
        stateflowTransition.setRuleType(ruleType);
        stateflowTransition.setFromStateId(fromState.getId());
        stateflowTransition.setToStateId(toState.getId());
        stateflowTransition.setShouldExecuteFromPermalink(shouldExecuteFromPermaLink);
        stateflowTransition.setShowInClientPortal(showInClientPortal);
        stateflowTransition.setShowInTenantPortal(showInTenantPortal);
        stateflowTransition.setShowInOccupantPortal(showInOccupantPortal);
        stateflowTransition.setShowInVendorPortal(showInVendorPortal);
        stateflowTransition.setType(type);
        stateflowTransition.setStateFlowId(stateflow.getId());


        if (builder.getElement("allApprovalRequired") != null){
            Boolean allApprovalRequired = Boolean.parseBoolean(builder.getElement("allApprovalRequired").getText());
            stateflowTransition.setAllApprovalRequired(allApprovalRequired);
        }

        XMLBuilder approverList = builder.getElement("ApproverList");
        if (approverList != null){
            List<XMLBuilder> approverElement = approverList.getElementList("approvers");
            SharingContext<ApproverContext> approverContextList = new SharingContext<>();
            for (XMLBuilder approver : approverElement){
                ApproverContext approverContext = new ApproverContext();
                String userName = approver.getElement("userName").getText();
                if (userName != null) {
                   long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
                    User user = AccountUtil.getUserBean().getAppUserForUserName(userName, appId, AccountUtil.getCurrentOrg().getOrgId());
                    if (user != null){
                        approverContext.setUserId(user.getOuid());
                    }
                }
                int approverType = Integer.parseInt(approver.getElement("type").getText());
                approverContext.setType(approverType);
                approverContextList.add(approverContext);
            }
        }


        if (builder.getElement("buttonType") != null){
            int buttonType = Integer.parseInt(builder.getElement("buttonType").getText());
            stateflowTransition.setButtonType(buttonType);
        }
        if (builder.getElement("dialogType") != null){
            int dialogType = Integer.parseInt(builder.getElement("dialogType").getText());
            stateflowTransition.setDialogType(dialogType);
        }
        if (builder.getElement("formName") != null){
            String formName = builder.getElement("formName").getText();
            FacilioForm form = FormsAPI.getFormFromDB(formName,module);
            stateflowTransition.setFormId(form.getId());
        }

        if (builder.getElement("formModuleName") != null){
            String formModuleName = builder.getElement("formModuleName").getText();
            stateflowTransition.setFormModuleName(formModuleName);
        }

        if (builder.getElement("isOffline") != null){
            Boolean isOffline = Boolean.parseBoolean(builder.getElement("isOffline").getText());
            stateflowTransition.setIsOffline(isOffline);
        }
        boolean shouldFormInterfaceApply = Boolean.parseBoolean(builder.getElement("shouldFormInterfaceApply").getText());
        stateflowTransition.setShouldFormInterfaceApply(shouldFormInterfaceApply);

        if (builder.getElement("locationFieldName") != null){
            String locationFieldName = builder.getElement("locationFieldName").getText();
            FacilioField locationField = moduleBean.getField(locationFieldName,moduleName);
            stateflowTransition.setLocationFieldId(locationField.getFieldId());
        }
        if (builder.getElement("locationLookupFieldName") != null){
            String locationLookupFieldName = builder.getElement("locationLookupFieldName").getText();
            String locationLookupFieldModuleName = builder.getElement("locationLookupFieldModuleName").getText();
            FacilioField locationLookupField = moduleBean.getField(locationLookupFieldName,locationLookupFieldModuleName);
            stateflowTransition.setLocationLookupFieldId(locationLookupField.getFieldId());
        }
        if (builder.getElement("radius") != null){
            Long radius = Long.valueOf(builder.getElement("radius").getText());
            stateflowTransition.setRadius(radius);
        }
        if (builder.getElement("qrFieldName") != null){
            String qrFieldName = builder.getElement("qrFieldName").getText();
            FacilioField qrField = moduleBean.getField(qrFieldName,moduleName);
            stateflowTransition.setQrFieldId(qrField.getFieldId());
        }
        if (builder.getElement("qrLookupFieldName") != null){
            String qrLookupFieldName = builder.getElement("qrLookupFieldName").getText();
            String qrLookupFieldModuleName = builder.getElement("qrLookupFieldModuleName").getText();
            FacilioField qrLookupField = moduleBean.getField(qrLookupFieldName,qrLookupFieldModuleName);
            stateflowTransition.setQrLookupFieldId(qrLookupField.getFieldId());
        }
        if (builder.getElement("scheduleTime") != null){
            int scheduleTime = Integer.parseInt(builder.getElement("scheduleTime").getText());
            stateflowTransition.setScheduleTime(scheduleTime);
        }

        XMLBuilder validationList = builder.getElement("validationList");
        if (validationList != null){
            List<ValidationContext> validationContextList =  PackageBeanUtil.constructValidationContextFromBuilder(validationList);
            stateflowTransition.setValidations(validationContextList);
        }
        XMLBuilder confirmationList = builder.getElement("confirmationList");
        if (confirmationList != null){
            List<ConfirmationDialogContext> confirmationDialogContextList = PackageBeanUtil.constructConfirmationDialogFromBuilder(confirmationList);
            stateflowTransition.setConfirmationDialogs(confirmationDialogContextList);
        }

        XMLBuilder actionsList = builder.getElement(PackageConstants.WorkFlowRuleConstants.ACTIONS_LIST);
        if (actionsList != null) {
            List<ActionContext> actionContextList = PackageBeanUtil.constructActionContextsFromBuilder(actionsList);
            stateflowTransition.setActions(actionContextList);
        }

        XMLBuilder criteriaElement = builder.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            stateflowTransition.setCriteria(criteria);
        }

        return stateflowTransition;

    }

}
