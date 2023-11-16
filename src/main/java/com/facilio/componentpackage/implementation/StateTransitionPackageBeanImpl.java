package com.facilio.componentpackage.implementation;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
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
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
        Map<Long, Long> allRuleIdVsModuleId = WorkflowRuleAPI.getAllRuleIdVsModuleId(WorkflowRuleContext.RuleType.STATE_RULE);
        // TODO - To be removed after proper field migration done in "visitorinviterel" module
        if (MapUtils.isNotEmpty(allRuleIdVsModuleId)) {
            FacilioModule module = Constants.getModBean().getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
            allRuleIdVsModuleId.entrySet().removeIf(entry -> entry.getValue().equals(module.getModuleId()));
        }
        return allRuleIdVsModuleId;
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
        element.element(PackageConstants.WorkFlowRuleConstants.FROM_STATE_NAME).text(fromState.getStatus());
        long toStateId = ((AbstractStateTransitionRuleContext) stateflowTransition).getToStateId();
        FacilioStatus toState = TicketAPI.getStatus(toStateId);
        element.element(PackageConstants.WorkFlowRuleConstants.TO_STATE_NAME).text(toState.getStatus());
        long stateFlowId = ((AbstractStateTransitionRuleContext) stateflowTransition).getStateFlowId();
        StateFlowRuleContext stateflow = StateFlowRulesAPI.getStateFlowContext(stateFlowId);
        element.element(PackageConstants.WorkFlowRuleConstants.STATEFLOW_NAME).text(stateflow.getName());
        element.element(PackageConstants.WorkFlowRuleConstants.TYPE).text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).getType()));
        element.element(PackageConstants.NAME).text(stateflowTransition.getName());
        element.element(PackageConstants.WorkFlowRuleConstants.STATUS).text(String.valueOf(stateflowTransition.isActive()));
        element.element(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE)
                .text(stateflowTransition.getActivityTypeEnum() != null ? stateflowTransition.getActivityTypeEnum().name() : null);
        element.element(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).text(stateflowTransition.getRuleTypeEnum().name());
        element.element(PackageConstants.WorkFlowRuleConstants.SHOULD_EXECUTE_FORM_PERMALINK).text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).isShouldExecuteFromPermalink()));
        element.element(PackageConstants.WorkFlowRuleConstants.SHOW_IN_CLIENT_PORTAL).text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).isShowInClientPortal()));
        element.element(PackageConstants.WorkFlowRuleConstants.SHOW_IN_OCCUPANT_PORTAL).text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).isShowInOccupantPortal()));
        element.element(PackageConstants.WorkFlowRuleConstants.SHOW_IN_TENANT_PORTAL).text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).isShowInTenantPortal()));
        element.element(PackageConstants.WorkFlowRuleConstants.SHOW_IN_VENDOR_PORTAL).text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).isShowInVendorPortal()));
        if (StringUtils.isNotEmpty(stateflowTransition.getTime())) {
            element.element(PackageConstants.WorkFlowRuleConstants.JOB_TIME).text(stateflowTransition.getTime());
        }
        if (((ApproverWorkflowRuleContext)stateflowTransition).getAllApprovalRequired() != null){
            element.element(PackageConstants.WorkFlowRuleConstants.ALL_APPROVAL_REQUIRED).text(String.valueOf(((ApproverWorkflowRuleContext) stateflowTransition).getAllApprovalRequired()));
        }
        if (((ApproverWorkflowRuleContext) stateflowTransition).getApprovers() != null){
            PackageBeanUtil.constructBuilderFromSharingContext(((AbstractStateTransitionRuleContext) stateflowTransition).getApprovers(),element.element(PackageConstants.WorkFlowRuleConstants.APPROVER_LIST));
        }
        if (((AbstractStateTransitionRuleContext) stateflowTransition).getButtonType() > 0){
            element.element(PackageConstants.WorkFlowRuleConstants.BUTTON_TYPE).text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).getButtonType()));
        }
        if (((AbstractStateTransitionRuleContext) stateflowTransition).getDialogType() > 0){
            element.element(PackageConstants.WorkFlowRuleConstants.DIALOG_TYPE).text(String.valueOf(((AbstractStateTransitionRuleContext) stateflowTransition).getDialogType()));
        }
        if (((AbstractStateTransitionRuleContext) stateflowTransition).getFormId() > 0){
            long formId = ((AbstractStateTransitionRuleContext) stateflowTransition).getFormId();
            FacilioForm form = FormsAPI.getFormFromDB(formId);
            element.element(PackageConstants.WorkFlowRuleConstants.FORMNAME).text(form.getName());
        }
        if (((AbstractStateTransitionRuleContext) stateflowTransition).getFormModuleName() != null){
            element.element(PackageConstants.WorkFlowRuleConstants.FORM_MODULENAME).text(((AbstractStateTransitionRuleContext) stateflowTransition).getFormModuleName());
        }
        if (((StateflowTransitionContext) stateflowTransition).getIsOffline() != null){
            element.element(PackageConstants.WorkFlowRuleConstants.ISOFFLINE).text(String.valueOf(((StateflowTransitionContext) stateflowTransition).getIsOffline()));
        }
        element.element(PackageConstants.WorkFlowRuleConstants.SHOULD_FORM_INTERFACE_APPLY).text(String.valueOf(((StateflowTransitionContext) stateflowTransition).shouldFormInterfaceApply()));
        if (((StateflowTransitionContext) stateflowTransition).getLocationFieldId() > 0){
            long locationfieldId = ((StateflowTransitionContext) stateflowTransition).getLocationFieldId();
            FacilioField locationField = moduleBean.getField(locationfieldId);
            element.element(PackageConstants.WorkFlowRuleConstants.LOCATION_FIELD_NAME).text(locationField.getName());
        }
        if (((StateflowTransitionContext) stateflowTransition).getLocationLookupFieldId() > 0){
            long locationLookupFieldId = ((StateflowTransitionContext) stateflowTransition).getLocationLookupFieldId();
            FacilioField locationLookupField = moduleBean.getField(locationLookupFieldId);
            element.element(PackageConstants.WorkFlowRuleConstants.LOCATION_LOOKUP_FIELD_NAME).text(locationLookupField.getName());
            element.element(PackageConstants.WorkFlowRuleConstants.LOCATION_LOOKUP_FIELD_MODULENAME).text(locationLookupField.getModule().getName());
        }
        if (((StateflowTransitionContext) stateflowTransition).getRadius() != null){
            element.element(PackageConstants.WorkFlowRuleConstants.RADIUS).text(String.valueOf(((StateflowTransitionContext) stateflowTransition).getRadius()));
        }
        if (((StateflowTransitionContext) stateflowTransition).getQrFieldId() > 0){
            long qrFieldId = ((StateflowTransitionContext) stateflowTransition).getQrFieldId();
            FacilioField qrField = moduleBean.getField(qrFieldId);
            element.element(PackageConstants.WorkFlowRuleConstants.QR_FIELDNAME).text(qrField.getName());
        }
        if (((StateflowTransitionContext) stateflowTransition).getQrLookupFieldId() != null){
            long qrLookupFieldId = ((StateflowTransitionContext) stateflowTransition).getQrLookupFieldId();
            FacilioField qrLookupField = moduleBean.getField(qrLookupFieldId);
            element.element(PackageConstants.WorkFlowRuleConstants.QR_LOOKUP_FIELDNAME).text(qrLookupField.getName());
            element.element(PackageConstants.WorkFlowRuleConstants.QR_LOOKUP_FIELD_MODULENAME).text(qrLookupField.getModule().getName());
        }
        if (((StateflowTransitionContext) stateflowTransition).getScheduleTime() > 0){
            element.element(PackageConstants.WorkFlowRuleConstants.SCHEDULE_TIME).text(String.valueOf(((StateflowTransitionContext) stateflowTransition).getScheduleTime()));
        }
        if (stateflowTransition.getScheduleType() > 0){
            element.element("ScheduleType").text(String.valueOf(stateflowTransition.getScheduleType()));
        }
        if (stateflowTransition.getInterval() > -1){
            element.element("Interval").text(String.valueOf(stateflowTransition.getInterval()));
        }
        if (stateflowTransition.getDateFieldId() > 0 && stateflowTransition.getDateField() != null) {
            element.element(PackageConstants.WorkFlowRuleConstants.DATE_FIELD_NAME).text(stateflowTransition.getDateField().getName());
        }

        if (((StateflowTransitionContext) stateflowTransition).getValidations() != null){
            PackageBeanUtil.constructBuilderFromValidationList(((StateflowTransitionContext) stateflowTransition).getValidations(),element.element(PackageConstants.WorkFlowRuleConstants.VALIDATION_LIST));
        }
        if (CollectionUtils.isNotEmpty(((StateflowTransitionContext) stateflowTransition).getConfirmationDialogs())){
            PackageBeanUtil.constructBuilderFromConfirmationDialogList(((StateflowTransitionContext) stateflowTransition).getConfirmationDialogs(),element.element(PackageConstants.WorkFlowRuleConstants.CONFIRMATION_LIST));
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
        String fromStateName = builder.getElement(PackageConstants.WorkFlowRuleConstants.FROM_STATE_NAME).getText();
        FacilioStatus fromState = TicketAPI.getStatus(module,fromStateName);
        String toStateName = builder.getElement(PackageConstants.WorkFlowRuleConstants.TO_STATE_NAME).getText();
        FacilioStatus toState = TicketAPI.getStatus(module,toStateName);
        boolean shouldExecuteFromPermaLink = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.SHOULD_EXECUTE_FORM_PERMALINK).getText());
        boolean showInClientPortal = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.SHOW_IN_CLIENT_PORTAL).getText());
        boolean showInOccupantPortal = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.SHOW_IN_OCCUPANT_PORTAL).getText());
        boolean showInTenantPortal = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.SHOW_IN_TENANT_PORTAL).getText());
        boolean showInVendorPortal = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.SHOW_IN_VENDOR_PORTAL).getText());
        int type = Integer.parseInt(builder.getElement(PackageConstants.WorkFlowRuleConstants.TYPE).getText());
        String stateflowName = builder.getElement(PackageConstants.WorkFlowRuleConstants.STATEFLOW_NAME).getText();
        WorkflowRuleContext stateflow = WorkflowRuleAPI.getWorkflowRule(stateflowName, module,WorkflowRuleContext.RuleType.STATE_FLOW,false);



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


        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.ALL_APPROVAL_REQUIRED) != null){
            Boolean allApprovalRequired = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.ALL_APPROVAL_REQUIRED).getText());
            stateflowTransition.setAllApprovalRequired(allApprovalRequired);
        }

        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.JOB_TIME) != null){
            stateflowTransition.setTime(builder.getElement(PackageConstants.WorkFlowRuleConstants.JOB_TIME).getText());
        }

        XMLBuilder approverList = builder.getElement(PackageConstants.WorkFlowRuleConstants.APPROVER_LIST);
        if (approverList != null) {
            SharingContext<ApproverContext> approverContexts = PackageBeanUtil.constructSharingContextFromBuilder(approverList, ApproverContext.class);
            stateflowTransition.setApprovers(approverContexts);
        }

        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.BUTTON_TYPE) != null){
            int buttonType = Integer.parseInt(builder.getElement(PackageConstants.WorkFlowRuleConstants.BUTTON_TYPE).getText());
            stateflowTransition.setButtonType(buttonType);
        }
        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.DIALOG_TYPE) != null){
            int dialogType = Integer.parseInt(builder.getElement(PackageConstants.WorkFlowRuleConstants.DIALOG_TYPE).getText());
            stateflowTransition.setDialogType(dialogType);
        }
        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.FORMNAME) != null){
            String formName = builder.getElement(PackageConstants.WorkFlowRuleConstants.FORMNAME).getText();
            FacilioForm form = FormsAPI.getFormFromDB(formName,module);
            stateflowTransition.setFormId(form.getId());
            stateflowTransition.setForm(form);
        }

        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.FORM_MODULENAME) != null){
            String formModuleName = builder.getElement(PackageConstants.WorkFlowRuleConstants.FORM_MODULENAME).getText();
            stateflowTransition.setFormModuleName(formModuleName);
        }

        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.ISOFFLINE) != null){
            Boolean isOffline = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.ISOFFLINE).getText());
            stateflowTransition.setIsOffline(isOffline);
        }
        boolean shouldFormInterfaceApply = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.SHOULD_FORM_INTERFACE_APPLY).getText());
        stateflowTransition.setShouldFormInterfaceApply(shouldFormInterfaceApply);

        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.LOCATION_FIELD_NAME) != null){
            String locationFieldName = builder.getElement(PackageConstants.WorkFlowRuleConstants.LOCATION_FIELD_NAME).getText();
            FacilioField locationField = moduleBean.getField(locationFieldName,moduleName);
            stateflowTransition.setLocationFieldId(locationField.getFieldId());
        }
        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.LOCATION_LOOKUP_FIELD_NAME) != null){
            String locationLookupFieldName = builder.getElement(PackageConstants.WorkFlowRuleConstants.LOCATION_LOOKUP_FIELD_NAME).getText();
            String locationLookupFieldModuleName = builder.getElement(PackageConstants.WorkFlowRuleConstants.LOCATION_LOOKUP_FIELD_MODULENAME).getText();
            FacilioField locationLookupField = moduleBean.getField(locationLookupFieldName,locationLookupFieldModuleName);
            stateflowTransition.setLocationLookupFieldId(locationLookupField.getFieldId());
        }
        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.RADIUS) != null){
            Long radius = Long.valueOf(builder.getElement(PackageConstants.WorkFlowRuleConstants.RADIUS).getText());
            stateflowTransition.setRadius(radius);
        }
        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.QR_FIELDNAME) != null){
            String qrFieldName = builder.getElement(PackageConstants.WorkFlowRuleConstants.QR_FIELDNAME).getText();
            FacilioField qrField = moduleBean.getField(qrFieldName,moduleName);
            stateflowTransition.setQrFieldId(qrField.getFieldId());
        }
        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.QR_LOOKUP_FIELDNAME) != null){
            String qrLookupFieldName = builder.getElement(PackageConstants.WorkFlowRuleConstants.QR_LOOKUP_FIELDNAME).getText();
            String qrLookupFieldModuleName = builder.getElement(PackageConstants.WorkFlowRuleConstants.QR_LOOKUP_FIELD_MODULENAME).getText();
            FacilioField qrLookupField = moduleBean.getField(qrLookupFieldName,qrLookupFieldModuleName);
            stateflowTransition.setQrLookupFieldId(qrLookupField.getFieldId());
        }
        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.SCHEDULE_TIME) != null){
            int scheduleTime = Integer.parseInt(builder.getElement(PackageConstants.WorkFlowRuleConstants.SCHEDULE_TIME).getText());
            stateflowTransition.setScheduleTime(scheduleTime);
        }

        if (builder.getElement("ScheduleType") != null){
            int scheduleType = Integer.parseInt(builder.getElement("ScheduleType").getText());
            WorkflowRuleContext.ScheduledRuleType scheduledRuleType = WorkflowRuleContext.ScheduledRuleType.valueOf(scheduleType);
            stateflowTransition.setScheduleType(scheduleType);
            stateflowTransition.setScheduleType(scheduledRuleType);
        }

        if (builder.getElement("Interval") != null){
            long interval = Long.parseLong(builder.getElement("Interval").getText());
            stateflowTransition.setInterval(interval);
        }
        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.DATE_FIELD_NAME) != null) {
            String dateFieldName = builder.getElement(PackageConstants.WorkFlowRuleConstants.DATE_FIELD_NAME).getText();
            FacilioField dateField = (StringUtils.isNotEmpty(dateFieldName) && StringUtils.isNotEmpty(moduleName)) ? moduleBean.getField(dateFieldName, moduleName) : null;
            if (dateField != null) {
                stateflowTransition.setDateField(dateField);
                stateflowTransition.setDateFieldId(dateField.getFieldId());
            }
        }

        XMLBuilder validationList = builder.getElement(PackageConstants.WorkFlowRuleConstants.VALIDATION_LIST);
        if (validationList != null){
            List<ValidationContext> validationContextList =  PackageBeanUtil.constructValidationContextFromBuilder(validationList);
            stateflowTransition.setValidations(validationContextList);
        }
        XMLBuilder confirmationList = builder.getElement(PackageConstants.WorkFlowRuleConstants.CONFIRMATION_LIST);
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
