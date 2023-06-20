package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
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

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderModule));
//        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
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

        FacilioField subject = FieldFactory.getDefaultField("subject","Subject","SUBJECT", FieldType.STRING,true);
        subject.setRequired(true);
        fields.add(subject);

        FacilioField description = FieldFactory.getDefaultField("description","Description","DESCRIPTION", FieldType.STRING,FacilioField.FieldDisplayType.TEXTAREA);
        fields.add(description);

        SystemEnumField category = FieldFactory.getDefaultField("category","Category","CATEGORY",FieldType.SYSTEM_ENUM);
        category.setEnumName("ServiceOrderCategory");
        fields.add(category);

        SystemEnumField maintenanceType = FieldFactory.getDefaultField("maintenancetype","MaintenanceType","MAINTENANCE_TYPE",FieldType.SYSTEM_ENUM);
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

        FacilioField preferredstarttime = FieldFactory.getDefaultField("preferredstarttime","PreferredStartTime","PREFERRED_START_TIME", FieldType.DATE_TIME);
        fields.add(preferredstarttime);

        FacilioField preferredendtime = FieldFactory.getDefaultField("preferredendtime","PreferredEndTime","PREFERRED_END_TIME", FieldType.DATE_TIME);
        fields.add(preferredendtime);

        FacilioField autocreatesa = FieldFactory.getDefaultField("autocreatesa","AutoCreateSA","AUTO_CREATE_SA", FieldType.BOOLEAN);
        fields.add(autocreatesa);

        FacilioField responseDueDate = FieldFactory.getDefaultField("responseduedate","ResponseDueDate","RESPONSE_DUE_DATE", FieldType.DATE_TIME);
        fields.add(responseDueDate);

        FacilioField resolutionDueDate = FieldFactory.getDefaultField("resolutionduedate","ResolutionDueDate","RESOLUTION_DUE_DATE", FieldType.DATE_TIME);
        fields.add(resolutionDueDate);

        FacilioField resolvedTime = FieldFactory.getDefaultField("resolvedtime","ResolvedTime","RESOLVED_TIME", FieldType.DATE_TIME);
        fields.add(resolvedTime);

        SystemEnumField sourceType = FieldFactory.getDefaultField("sourceType","SourceType","SOURCE_TYPE",FieldType.SYSTEM_ENUM);
        sourceType.setEnumName("ServiceOrderSourceType");
        fields.add(sourceType);

        LookupField createdby = FieldFactory.getDefaultField("createdby","CreatedBy","CREATED_BY",FieldType.LOOKUP);
        createdby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(createdby);

        FacilioField createdTime = FieldFactory.getDefaultField("createdtime","CreatedTime","CREATED_TIME", FieldType.DATE_TIME);
        fields.add(createdTime);

        LookupField modifiedby = FieldFactory.getDefaultField("modifiedby","ModifiedBy","MODIFIED_BY",FieldType.LOOKUP);
        modifiedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(modifiedby);

        FacilioField modifiedTime = FieldFactory.getDefaultField("modifiedtime","ModifiedTime","MODIFIED_TIME", FieldType.DATE_TIME);
        fields.add(modifiedTime);

        LookupField sysdeletedby = FieldFactory.getDefaultField("sysdeletedby","SysDeletedBy","SYS_DELETED_BY",FieldType.LOOKUP);
        sysdeletedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(sysdeletedby);

        FacilioField sysdeleted = FieldFactory.getDefaultField("sysdeleted","SysDeleted","SYS_DELETED", FieldType.BOOLEAN);
        fields.add(sysdeleted);

        FacilioField sysdeletedtime = FieldFactory.getDefaultField("sysdeletedtime","SysDeletedTime","SYS_DELETED_TIME", FieldType.DATE_TIME);
        fields.add(sysdeletedtime);

        FacilioField actualstarttime = FieldFactory.getDefaultField("actualstarttime","ActualStartTime","ACTUAL_START_TIME", FieldType.DATE_TIME);
        fields.add(actualstarttime);

        FacilioField actualendtime = FieldFactory.getDefaultField("actualendtime","ActualEndTime","ACTUAL_END_TIME", FieldType.DATE_TIME);
        fields.add(actualendtime);

        FacilioField actualduration = FieldFactory.getDefaultField("actualduration","ActualDuration","ACTUAL_DURATION", FieldType.DATE_TIME);
        fields.add(actualduration);

        LookupField assignedto = FieldFactory.getDefaultField("assignedto","AssignedTo","ASSIGNED_TO",FieldType.LOOKUP);
        assignedto.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(assignedto);

        LookupField assignedby = FieldFactory.getDefaultField("assignedby","AssignedBy","ASSIGNED_BY",FieldType.LOOKUP);
        assignedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(assignedby);

        FacilioField duedate = FieldFactory.getDefaultField("duedate","DueDate","DUE_DATE", FieldType.DATE_TIME);
        fields.add(duedate);

        FacilioField estimatedstarttime = FieldFactory.getDefaultField("estimatedstarttime","EstimatedStartTime","ESTIMATED_START_TIME", FieldType.DATE_TIME);
        fields.add(estimatedstarttime);

        FacilioField estimatedendtime = FieldFactory.getDefaultField("estimatedendtime","EstimatedEndTime","ESTIMATED_END_TIME", FieldType.DATE_TIME);
        fields.add(estimatedendtime);

        FacilioField estimatedduration = FieldFactory.getDefaultField("estimatedduration","EstimatedDuration","ESTIMATED_DURATION", FieldType.DATE_TIME);
        fields.add(estimatedduration);

        FacilioField modulestate = FieldFactory.getDefaultField("modulestate","ModuleState","MODULE_STATE", FieldType.NUMBER);
        fields.add(modulestate);

        FacilioField noOfAttachments = FieldFactory.getDefaultField("noofattachments","NoOfAttachments","NO_OF_ATTACHMENTS", FieldType.NUMBER);
        fields.add(noOfAttachments);

        FacilioField noOfClosedTasks = FieldFactory.getDefaultField("noofclosedtasks","NoOfClosedTasks","NO_OF_CLOSED_TASKS", FieldType.NUMBER);
        fields.add(noOfClosedTasks);

        FacilioField noOfNotes = FieldFactory.getDefaultField("noofnotes","NoOfNotes","NO_OF_NOTES", FieldType.NUMBER);
        fields.add(noOfNotes);

        FacilioField noOfTasks = FieldFactory.getDefaultField("nooftasks","NoOfTasks","NO_OF_TASKS", FieldType.NUMBER);
        fields.add(noOfTasks);

        FacilioField slapolicyid = FieldFactory.getDefaultField("slapolicyid","SlaPolicyId","SLA_POLICY_ID", FieldType.NUMBER);
        fields.add(slapolicyid);

//        LookupField slapolicyid = FieldFactory.getDefaultField("slapolicyid","SlaPolicyId","SLA_POLICY_ID",FieldType.LOOKUP);
//        slapolicyid.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SLA_POLICY));
//        fields.add(slapolicyid);

        FacilioField stateflowid = FieldFactory.getDefaultField("stateflowid","StateFlowId","STATE_FLOW_ID", FieldType.NUMBER);
        fields.add(stateflowid);

        //doubt need to remove?? starts
//        LookupField stateflowid = FieldFactory.getDefaultField("stateflowid","StateFlowId","STATE_FLOW_ID",FieldType.LOOKUP);
//        stateflowid.setLookupModule(bean.getModule(FacilioConstants.ContextNames.STATE_FLOW));
//        fields.add(stateflowid);
        //doubt need to remove?? ends

        FacilioField approvalRuleId = FieldFactory.getDefaultField("approvalruleid","ApprovalRuleId","APPROVAL_RULE_ID", FieldType.NUMBER);
        fields.add(approvalRuleId);

        FacilioField approvalState = FieldFactory.getDefaultField("approvalstate","ApprovalState","APPROVAL_STATE", FieldType.NUMBER);
        fields.add(approvalState);

        FacilioField parentso = FieldFactory.getDefaultField("parentso","ParentSo","PARENT_SOID", FieldType.NUMBER);
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
        SystemEnumField prerequeststatus = FieldFactory.getDefaultField("prerequeststatus","PreRequestStatus","PRE_REQUEST_STATUS",FieldType.SYSTEM_ENUM);
        prerequeststatus.setEnumName("ServiceOrderPrerequisiteStatus");
        fields.add(prerequeststatus);
        //doubt need to remove lookup?? starts

        FacilioField prerequisiteapproved = FieldFactory.getDefaultField("prerequisiteapproved","PreRequisiteApproved","PREREQUISITE_APPROVED", FieldType.BOOLEAN);
        fields.add(prerequisiteapproved);

        FacilioField prerequisiteenabled = FieldFactory.getDefaultField("prerequisiteenabled","PreRequisiteEnabled","PREREQUISITE_ENABLED", FieldType.BOOLEAN);
        fields.add(prerequisiteenabled);

        FacilioField qrenabled = FieldFactory.getDefaultField("qrenabled","QrEnabled","QR_ENABLED", FieldType.BOOLEAN);
        fields.add(qrenabled);

        LookupField requestedby = FieldFactory.getDefaultField("requestedby","RequestedBy","REQUESTED_BY_ID",FieldType.LOOKUP);
        requestedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(requestedby);

        FacilioField localid = FieldFactory.getDefaultField("localid","LocalId","LOCAL_ID", FieldType.NUMBER);
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
        webWorkOrderForm.setName("default_workorder_web");
        webWorkOrderForm.setModule(serviceOrderModule);
        webWorkOrderForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        webWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> webWorkOrderFormDefaultFields = new ArrayList<>();
        webWorkOrderFormDefaultFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        webWorkOrderFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));


        List<FormField> webWorkOrderFormFields = new ArrayList<>();
        webWorkOrderFormFields.addAll(webWorkOrderFormDefaultFields);

        FormSection defaultSection = new FormSection("WORKORDER", 1, webWorkOrderFormDefaultFields, true);
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
}
