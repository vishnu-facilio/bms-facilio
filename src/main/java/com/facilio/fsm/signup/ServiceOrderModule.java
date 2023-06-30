package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;

import java.util.*;

public class ServiceOrderModule extends BaseModuleConfig {
    public ServiceOrderModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
    }

    @Override
    public void addData() throws Exception {
        FacilioModule serviceOrderModule = constructServiceOrderModule();
        serviceOrderModule.setStateFlowEnabled(true);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderModule));
//        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
        addStateFlow();
    }

    private FacilioModule constructServiceOrderModule() throws Exception{
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, "Service Order", "ServiceOrders", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField site = FieldFactory.getDefaultField("site","Site","SITE_ID",FieldType.LOOKUP);
        site.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SITE));
        fields.add(site);

        LookupField client = FieldFactory.getDefaultField("client","Client","CLIENT_ID",FieldType.LOOKUP);
        client.setLookupModule(bean.getModule(FacilioConstants.ContextNames.CLIENT));
        fields.add(client);

        FacilioField subject = FieldFactory.getDefaultField("name","Name","NAME", FieldType.STRING,true);
        subject.setRequired(true);
        fields.add(subject);

        FacilioField description = FieldFactory.getDefaultField("description","Description","DESCRIPTION", FieldType.STRING,FacilioField.FieldDisplayType.TEXTAREA);
        fields.add(description);

        SystemEnumField category = FieldFactory.getDefaultField("category","Category","CATEGORY",FieldType.SYSTEM_ENUM);
        category.setEnumName("ServiceOrderCategory");
        fields.add(category);

        SystemEnumField maintenanceType = FieldFactory.getDefaultField("maintenanceType","MaintenanceType","MAINTENANCE_TYPE",FieldType.SYSTEM_ENUM);
        maintenanceType.setEnumName("ServiceOrderMaintenanceType");
        fields.add(maintenanceType);

        SystemEnumField priority = FieldFactory.getDefaultField("priority","Priority","PRIORITY",FieldType.SYSTEM_ENUM);
        priority.setEnumName("ServiceOrderPriority");
        fields.add(priority);

        LookupField space = FieldFactory.getDefaultField("space","Space","SPACE_ID",FieldType.LOOKUP);
        space.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SPACE));
        fields.add(space);

        LookupField asset = FieldFactory.getDefaultField("asset","Asset","ASSET_ID",FieldType.LOOKUP);
        asset.setLookupModule(bean.getModule(FacilioConstants.ContextNames.ASSET));
        fields.add(asset);

        SystemEnumField status = FieldFactory.getDefaultField("status","Status","STATUS_ID",FieldType.SYSTEM_ENUM);
        status.setEnumName("ServiceOrderStatus");
        fields.add(status);

        LookupField vendor = FieldFactory.getDefaultField("vendor","Vendor","VENDOR_ID",FieldType.LOOKUP);
        vendor.setLookupModule(bean.getModule(FacilioConstants.ContextNames.VENDORS));
        fields.add(vendor);

        FacilioField preferredstarttime = FieldFactory.getDefaultField("preferredStartTime","PreferredStartTime","PREFERRED_START_TIME", FieldType.DATE_TIME);
        fields.add(preferredstarttime);

        FacilioField preferredendtime = FieldFactory.getDefaultField("preferredEndTime","PreferredEndTime","PREFERRED_END_TIME", FieldType.DATE_TIME);
        fields.add(preferredendtime);

        FacilioField autocreatesa = FieldFactory.getDefaultField("autoCreateSa","AutoCreateSA","AUTO_CREATE_SA", FieldType.BOOLEAN);
        fields.add(autocreatesa);

        FacilioField isallserviceappointmentsclosed = FieldFactory.getDefaultField("isAllSAClosed","Is All SA Closed","IS_ALL_SA_CLOSED", FieldType.BOOLEAN);
        fields.add(isallserviceappointmentsclosed);

        FacilioField responseDueDate = FieldFactory.getDefaultField("responseDueDate","ResponseDueDate","RESPONSE_DUE_DATE", FieldType.DATE_TIME);
        fields.add(responseDueDate);

        FacilioField resolutionDueDate = FieldFactory.getDefaultField("resolutionDueDate","ResolutionDueDate","RESOLUTION_DUE_DATE", FieldType.DATE_TIME);
        fields.add(resolutionDueDate);

        FacilioField resolvedTime = FieldFactory.getDefaultField("resolvedTime","ResolvedTime","RESOLVED_TIME", FieldType.DATE_TIME);
        fields.add(resolvedTime);

        SystemEnumField sourceType = FieldFactory.getDefaultField("sourceType","SourceType","SOURCE_TYPE",FieldType.SYSTEM_ENUM);
        sourceType.setEnumName("ServiceOrderSourceType");
        fields.add(sourceType);

        LookupField createdby = FieldFactory.getDefaultField("createdBy","CreatedBy","CREATED_BY",FieldType.LOOKUP);
        createdby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(createdby);

        FacilioField createdTime = FieldFactory.getDefaultField("createdTime","CreatedTime","CREATED_TIME", FieldType.DATE_TIME);
        fields.add(createdTime);

        LookupField modifiedby = FieldFactory.getDefaultField("modifiedBy","ModifiedBy","MODIFIED_BY",FieldType.LOOKUP);
        modifiedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(modifiedby);

        FacilioField modifiedTime = FieldFactory.getDefaultField("modifiedTime","ModifiedTime","MODIFIED_TIME", FieldType.DATE_TIME);
        fields.add(modifiedTime);

        LookupField sysdeletedby = FieldFactory.getDefaultField("sysdeletedby","SysDeletedBy","SYS_DELETED_BY",FieldType.LOOKUP);
        sysdeletedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(sysdeletedby);

        FacilioField sysdeleted = FieldFactory.getDefaultField("sysDeleted","SysDeleted","SYS_DELETED", FieldType.BOOLEAN);
        fields.add(sysdeleted);

        FacilioField sysdeletedtime = FieldFactory.getDefaultField("sysDeletedTime","SysDeletedTime","SYS_DELETED_TIME", FieldType.DATE_TIME);
        fields.add(sysdeletedtime);

        FacilioField actualstarttime = FieldFactory.getDefaultField("actualStartTime","ActualStartTime","ACTUAL_START_TIME", FieldType.DATE_TIME);
        fields.add(actualstarttime);

        FacilioField actualendtime = FieldFactory.getDefaultField("actualEndTime","ActualEndTime","ACTUAL_END_TIME", FieldType.DATE_TIME);
        fields.add(actualendtime);

        FacilioField actualduration = FieldFactory.getDefaultField("actualDuration","ActualDuration","ACTUAL_DURATION", FieldType.DATE_TIME);
        fields.add(actualduration);

        LookupField assignedto = FieldFactory.getDefaultField("assignedTo","AssignedTo","ASSIGNED_TO",FieldType.LOOKUP);
        assignedto.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(assignedto);

        LookupField assignedby = FieldFactory.getDefaultField("assignedBy","AssignedBy","ASSIGNED_BY",FieldType.LOOKUP);
        assignedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(assignedby);

        FacilioField duedate = FieldFactory.getDefaultField("dueDate","DueDate","DUE_DATE", FieldType.DATE_TIME);
        fields.add(duedate);

        FacilioField estimatedstarttime = FieldFactory.getDefaultField("estimatedStartTime","EstimatedStartTime","ESTIMATED_START_TIME", FieldType.DATE_TIME);
        fields.add(estimatedstarttime);

        FacilioField estimatedendtime = FieldFactory.getDefaultField("estimatedEndTime","EstimatedEndTime","ESTIMATED_END_TIME", FieldType.DATE_TIME);
        fields.add(estimatedendtime);

        FacilioField estimatedduration = FieldFactory.getDefaultField("estimatedDuration","EstimatedDuration","ESTIMATED_DURATION", FieldType.DATE_TIME);
        fields.add(estimatedduration);

//        FacilioField modulestate = FieldFactory.getDefaultField("modulestate","ModuleState","MODULE_STATE", FieldType.NUMBER);
//        fields.add(modulestate);

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setLookupModule(bean.getModule("ticketstatus"));
        fields.add(moduleStateField);

        FacilioField noOfAttachments = FieldFactory.getDefaultField("noOfAttachments","NoOfAttachments","NO_OF_ATTACHMENTS", FieldType.NUMBER);
        fields.add(noOfAttachments);

        FacilioField noOfClosedTasks = FieldFactory.getDefaultField("noOfClosedTasks","NoOfClosedTasks","NO_OF_CLOSED_TASKS", FieldType.NUMBER);
        fields.add(noOfClosedTasks);

        FacilioField noOfNotes = FieldFactory.getDefaultField("noOfNotes","NoOfNotes","NO_OF_NOTES", FieldType.NUMBER);
        fields.add(noOfNotes);

        FacilioField noOfTasks = FieldFactory.getDefaultField("noOfTasks","NoOfTasks","NO_OF_TASKS", FieldType.NUMBER);
        fields.add(noOfTasks);

        FacilioField slapolicyid = FieldFactory.getDefaultField("slaPolicyId","SlaPolicyId","SLA_POLICY_ID", FieldType.NUMBER);
        fields.add(slapolicyid);

//        LookupField slapolicyid = FieldFactory.getDefaultField("slapolicyid","SlaPolicyId","SLA_POLICY_ID",FieldType.LOOKUP);
//        slapolicyid.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SLA_POLICY));
//        fields.add(slapolicyid);

        FacilioField stateflowid = FieldFactory.getDefaultField("stateFlowId","StateFlowId","STATE_FLOW_ID", FieldType.NUMBER);
        stateflowid.setDefault(true);
        fields.add(stateflowid);

        //doubt need to remove?? starts
//        LookupField stateflowid = FieldFactory.getDefaultField("stateflowid","StateFlowId","STATE_FLOW_ID",FieldType.LOOKUP);
//        stateflowid.setLookupModule(bean.getModule(FacilioConstants.ContextNames.STATE_FLOW));
//        fields.add(stateflowid);
        //doubt need to remove?? ends

        FacilioField approvalRuleId = FieldFactory.getDefaultField("approvalRuleId","ApprovalRuleId","APPROVAL_RULE_ID", FieldType.NUMBER);
        fields.add(approvalRuleId);

        FacilioField approvalState = FieldFactory.getDefaultField("approvalState","ApprovalState","APPROVAL_STATE", FieldType.NUMBER);
        fields.add(approvalState);

        FacilioField parentso = FieldFactory.getDefaultField("parentSo","ParentSo","PARENT_SOID", FieldType.NUMBER);
        fields.add(parentso);

        LookupField pm = FieldFactory.getDefaultField("pm","PM","PM_ID",FieldType.LOOKUP);
        pm.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE));
        fields.add(pm);

        //doubt need to remove?? starts
//        LookupField pmresourceplanner = FieldFactory.getDefaultField("pmresourceplanner","PmResourcePlanner","PmResourcePlanner",FieldType.LOOKUP);
//        pmresourceplanner.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE));
//        fields.add(pmresourceplanner);
        //doubt need to remove?? ends

        //doubt need to remove lookup?? starts
        SystemEnumField prerequeststatus = FieldFactory.getDefaultField("prerequestStatus","PreRequestStatus","PRE_REQUEST_STATUS",FieldType.SYSTEM_ENUM);
        prerequeststatus.setEnumName("ServiceOrderPrerequisiteStatus");
        fields.add(prerequeststatus);
        //doubt need to remove lookup?? starts

        FacilioField prerequisiteapproved = FieldFactory.getDefaultField("prerequisiteApproved","PreRequisiteApproved","PREREQUISITE_APPROVED", FieldType.BOOLEAN);
        fields.add(prerequisiteapproved);

        FacilioField prerequisiteenabled = FieldFactory.getDefaultField("prerequisiteEnabled","PreRequisiteEnabled","PREREQUISITE_ENABLED", FieldType.BOOLEAN);
        fields.add(prerequisiteenabled);

        FacilioField qrenabled = FieldFactory.getDefaultField("qrEnabled","QrEnabled","QR_ENABLED", FieldType.BOOLEAN);
        fields.add(qrenabled);

        LookupField requestedby = FieldFactory.getDefaultField("requestedBy","RequestedBy","REQUESTED_BY_ID",FieldType.LOOKUP);
        requestedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(requestedby);

        FacilioField localid = FieldFactory.getDefaultField("localId","LocalId","LOCAL_ID", FieldType.NUMBER);
        fields.add(localid);

        module.setFields(fields);

        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);
        //FacilioModule serviceOrderModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, "Service Order", "ServiceOrders", FacilioModule.ModuleType.BASE_ENTITY,true);

        FacilioForm webWorkOrderForm = new FacilioForm();
        webWorkOrderForm.setDisplayName("Standard");
        webWorkOrderForm.setName("default_serviceorder_web");
        webWorkOrderForm.setModule(serviceOrderModule);
        webWorkOrderForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        webWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> webWorkOrderFormDefaultFields = new ArrayList<>();
        webWorkOrderFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        webWorkOrderFormDefaultFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 2, 1));
        webWorkOrderFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        webWorkOrderFormDefaultFields.add(new FormField("maintenancetype", FacilioField.FieldDisplayType.SELECTBOX, "Maintenance Type", FormField.Required.OPTIONAL, 4, 1));
        webWorkOrderFormDefaultFields.add(new FormField("priority", FacilioField.FieldDisplayType.SELECTBOX, "Priority", FormField.Required.OPTIONAL, 5, 1));
        webWorkOrderFormDefaultFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, "vendors", 6, 1));


        List<FormField> webWorkOrderFormFields = new ArrayList<>();
        webWorkOrderFormFields.addAll(webWorkOrderFormDefaultFields);

        FormSection defaultSection = new FormSection("SERVICEORDER", 1, webWorkOrderFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);


//        List<FormSection> webWorkOrderFormSections = new ArrayList<>();
//        webWorkOrderFormSections.add(defaultSection);

        webWorkOrderForm.setSections(Collections.singletonList(defaultSection));
        webWorkOrderForm.setIsSystemForm(true);
        webWorkOrderForm.setType(FacilioForm.Type.FORM);

//        List<FacilioForm> serviceOrderModuleForms = new ArrayList<>();
//        serviceOrderModuleForms.add(webWorkOrderForm);

        return Collections.singletonList(webWorkOrderForm);
    }


    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;
        int order = 1;

        ArrayList<FacilioView> all = new ArrayList<FacilioView>();
        all.add(getAllServiceOrders().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "allserviceorders");
        groupDetails.put("displayName", "All Service Orders");
        groupDetails.put("views", all);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }


    private static FacilioView getAllServiceOrders() {
        FacilioModule serviceOrderModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, "Service Order", "ServiceOrders", FacilioModule.ModuleType.BASE_ENTITY,true);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setName("CREATED_TIME");
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(serviceOrderModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Service Orders");
//        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static void addStateFlow() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioStatus newStatus = new FacilioStatus();
        newStatus.setStatus("new");
        newStatus.setDisplayName("New");
        newStatus.setTypeCode(1);
        TicketAPI.addStatus(newStatus, serviceAppointmentModule);

        FacilioStatus inProgressStatus = new FacilioStatus();
        inProgressStatus.setStatus("inProgress");
        inProgressStatus.setDisplayName("In Progress");
        inProgressStatus.setTypeCode(2);
        TicketAPI.addStatus(inProgressStatus, serviceAppointmentModule);

        FacilioStatus completedStatus = new FacilioStatus();
        completedStatus.setStatus("completed");
        completedStatus.setDisplayName("Completed");
        completedStatus.setTypeCode(3);
        TicketAPI.addStatus(completedStatus, serviceAppointmentModule);

        FacilioStatus cannotCompleteStatus = new FacilioStatus();
        cannotCompleteStatus.setStatus("cannotComplete");
        cannotCompleteStatus.setDisplayName("Cannot Complete");
        cannotCompleteStatus.setTypeCode(4);
        TicketAPI.addStatus(cannotCompleteStatus, serviceAppointmentModule);

        FacilioStatus closedStatus = new FacilioStatus();
        closedStatus.setStatus("closed");
        closedStatus.setDisplayName("Closed");
        closedStatus.setTypeCode(5);
        TicketAPI.addStatus(closedStatus, serviceAppointmentModule);

        FacilioStatus onHoldStatus = new FacilioStatus();
        onHoldStatus.setStatus("onHold");
        onHoldStatus.setDisplayName("On Hold");
        onHoldStatus.setTypeCode(6);
        TicketAPI.addStatus(onHoldStatus, serviceAppointmentModule);

        FacilioStatus cancelledStatus = new FacilioStatus();
        cancelledStatus.setStatus("cancelled");
        cancelledStatus.setDisplayName("Cancelled");
        cancelledStatus.setTypeCode(7);
        TicketAPI.addStatus(cancelledStatus, serviceAppointmentModule);

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName("Default Stateflow");
        stateFlowRuleContext.setModuleId(serviceAppointmentModule.getModuleId());
        stateFlowRuleContext.setModule(serviceAppointmentModule);
        stateFlowRuleContext.setActivityType(EventType.CREATE);
        stateFlowRuleContext.setExecutionOrder(1);
        stateFlowRuleContext.setStatus(true);
        stateFlowRuleContext.setDefaltStateFlow(true);
        stateFlowRuleContext.setDefaultStateId(newStatus.getId());
        stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);
    }
}
