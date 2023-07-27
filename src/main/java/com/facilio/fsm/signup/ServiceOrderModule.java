package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.qa.context.PageContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class ServiceOrderModule extends BaseModuleConfig {
    public ServiceOrderModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
    }

    @Override
    public void addData() throws Exception {
        FacilioModule serviceOrderModule = constructServiceOrderModule();

        addServiceOrderAttachmentsModule(Constants.getModBean(),AccountUtil.getCurrentOrg().getId(), serviceOrderModule);

        constructServiceOrderNotesModule(Constants.getModBean(), AccountUtil.getCurrentOrg().getId(), serviceOrderModule);
    }

    private FacilioModule constructServiceOrderNotesModule(ModuleBean modBean,long orgId, FacilioModule serviceOrderModule) throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule serviceOrderNotesModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_NOTES, "ServiceOrder Notes",
                "ServiceOrder_Notes", FacilioModule.ModuleType.NOTES, null,
                null);

        List<FacilioField> fields = new ArrayList<>();

        FacilioField createdTimeField = new FacilioField(serviceOrderNotesModule, "createdTime", "Created Time",
                FacilioField.FieldDisplayType.DATETIME, "CREATED_TIME", FieldType.DATE_TIME,
                true, false, true, false);
        fields.add(createdTimeField);

        LookupField createdByField = SignupUtil.getLookupField(serviceOrderNotesModule, null, "createdBy",
                "Created By", "CREATED_BY", "users", FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(createdByField);

        NumberField parentIdField = SignupUtil.getNumberField(serviceOrderNotesModule,
                "parentId", "Parent", "PARENT_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(parentIdField);

        StringField titleField = SignupUtil.getStringField(serviceOrderNotesModule,
                "title", "Title",  "TITLE", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, false,orgId);
        fields.add(titleField);

        StringField bodyField = SignupUtil.getStringField(serviceOrderNotesModule,
                "body", "Body", "BODY", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false,orgId);
        fields.add(bodyField);

        StringField bodyHtmlField = SignupUtil.getStringField(serviceOrderNotesModule,
                "bodyHTML", "Body HTML", "BODY_HTML", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false,orgId);
        fields.add(bodyHtmlField);

        LookupField parentNote = SignupUtil.getLookupField(serviceOrderNotesModule, serviceOrderModule, "parentNote", "Parent Note",
                "PARENT_NOTE", null, FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(parentNote);

        serviceOrderNotesModule.setFields(fields);

        SignupUtil.addModules(serviceOrderNotesModule);

        modBean.addSubModule(serviceOrderModule.getModuleId(), serviceOrderNotesModule.getModuleId());

        return serviceOrderNotesModule;
    }

    private FacilioModule addServiceOrderAttachmentsModule(ModuleBean modBean, long orgId, FacilioModule serviceOrderModule) throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule serviceOrderAttachmentsModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ATTACHMENTS, "Service Order Attachments",
                "ServiceOrder_Attachments", FacilioModule.ModuleType.ATTACHMENTS, null,
                null);

        List<FacilioField> fields = new ArrayList<>();

        NumberField fieldIdField = SignupUtil.getNumberField(serviceOrderAttachmentsModule, "fileId", "File ID",
                "FILE_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fieldIdField.setMainField(true);
        fields.add(fieldIdField);

        LookupField parentIdField = SignupUtil.getLookupField(serviceOrderAttachmentsModule, serviceOrderModule, "parentId",
                "Parent", "PARENT_ID", "serviceOrder", FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(parentIdField);

        FacilioField createdTimeField = SignupUtil.getNumberField(serviceOrderAttachmentsModule,
                "createdTime", "Created Time","CREATED_TIME",
                FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(createdTimeField);

        FacilioField attachmentTypeField = SignupUtil.getNumberField(serviceOrderAttachmentsModule,
                "type", "Type", "ATTACHMENT_TYPE",
                FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(attachmentTypeField);


        serviceOrderAttachmentsModule.setFields(fields);

        SignupUtil.addModules(serviceOrderAttachmentsModule);

        modBean.addSubModule(serviceOrderModule.getModuleId(), serviceOrderAttachmentsModule.getModuleId());

        return serviceOrderAttachmentsModule;
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

        FacilioField isallserviceappointmentsclosed = FieldFactory.getDefaultField("isAllSACompleted","Is All SA Completed","IS_ALL_SA_COMPLETED", FieldType.BOOLEAN);
        fields.add(isallserviceappointmentsclosed);

        FacilioField isTaskInitiated = FieldFactory.getDefaultField("isTaskInitiated","Is Task Initiated","IS_TASK_INITIATED", FieldType.BOOLEAN);
        fields.add(isTaskInitiated);

        FacilioField responseDueDate = FieldFactory.getDefaultField("responseDueDate","ResponseDueDate","RESPONSE_DUE_DATE", FieldType.DATE_TIME);
        fields.add(responseDueDate);

        FacilioField resolutionDueDate = FieldFactory.getDefaultField("resolutionDueDate","ResolutionDueDate","RESOLUTION_DUE_DATE", FieldType.DATE_TIME);
        fields.add(resolutionDueDate);

        FacilioField responseDueDuration = FieldFactory.getDefaultField("responseDueDuration","ResponseDueDuration","RESPONSE_DUE_DURATION", FieldType.DATE_TIME);
        fields.add(responseDueDuration);

        FacilioField resolutionDueDuration = FieldFactory.getDefaultField("resolutionDueDuration","ResolutionDueDuration","RESOLUTION_DUE_DURATION", FieldType.DATE_TIME);
        fields.add(resolutionDueDuration);

        FacilioField resolvedTime = FieldFactory.getDefaultField("resolvedTime","ResolvedTime","RESOLVED_TIME", FieldType.DATE_TIME);
        fields.add(resolvedTime);

        SystemEnumField sourceType = FieldFactory.getDefaultField("sourceType","SourceType","SOURCE_TYPE",FieldType.SYSTEM_ENUM);
        sourceType.setEnumName("ServiceOrderSourceType");
        fields.add(sourceType);

//        LookupField createdby = FieldFactory.getDefaultField("createdBy","CreatedBy","SYS_CREATED_BY",FieldType.LOOKUP);
//        createdby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
//        fields.add(createdby);
//
//        FacilioField createdTime = FieldFactory.getDefaultField("createdTime","CreatedTime","SYS_CREATED_TIME", FieldType.DATE_TIME);
//        fields.add(createdTime);
//
//        LookupField modifiedby = FieldFactory.getDefaultField("modifiedBy","ModifiedBy","SYS_MODIFIED_BY",FieldType.LOOKUP);
//        modifiedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
//        fields.add(modifiedby);
//
//        FacilioField modifiedTime = FieldFactory.getDefaultField("modifiedTime","ModifiedTime","SYS_MODIFIED_TIME", FieldType.DATE_TIME);
//        fields.add(modifiedTime);

        LookupField sysdeletedby = FieldFactory.getDefaultField("sysdeletedby","SysDeletedBy","SYS_DELETED_BY",FieldType.LOOKUP);
        sysdeletedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(sysdeletedby);

        LookupField territory = FieldFactory.getDefaultField("territory","Territory","Territory",FieldType.LOOKUP);
        territory.setLookupModule(bean.getModule(FacilioConstants.Territory.TERRITORY));
        fields.add(territory);

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

        LookupField fieldAgent = FieldFactory.getDefaultField("fieldAgent","Field Agent","FIELD_AGENT",FieldType.LOOKUP);
        fieldAgent.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(fieldAgent);

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

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);

        FacilioForm webWorkOrderForm = new FacilioForm();
        webWorkOrderForm.setDisplayName("Standard");
        webWorkOrderForm.setName("default_serviceorder_web");
        webWorkOrderForm.setModule(serviceOrderModule);
        webWorkOrderForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        webWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> generalInformationFields = new ArrayList<>();
        generalInformationFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        generalInformationFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        generalInformationFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.REQUIRED, 3, 2));
        generalInformationFields.add(new FormField("maintenanceType", FacilioField.FieldDisplayType.SELECTBOX, "Maintenance Type", FormField.Required.OPTIONAL, 4, 2));
        generalInformationFields.add(new FormField("priority", FacilioField.FieldDisplayType.SELECTBOX, "Priority", FormField.Required.REQUIRED, 5, 2));
        generalInformationFields.add(new FormField("autoCreateSa", FacilioField.FieldDisplayType.DECISION_BOX, "Auto Create SA", FormField.Required.OPTIONAL, 6, 2));

        FormSection generalInfoSection = new FormSection("General Information", 1, generalInformationFields, true);
        generalInfoSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> siteClientFields = new ArrayList<>();
        siteClientFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, FacilioConstants.ContextNames.SITE, 1, 2));
        siteClientFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.CLIENT, 2, 2));
        siteClientFields.add(new FormField("space", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space", FormField.Required.OPTIONAL,FacilioConstants.ContextNames.SPACE, 3, 2));
        siteClientFields.add(new FormField("asset", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.ASSET,4, 2));

        FormSection siteClientSection = new FormSection("Site & Client Information", 2, siteClientFields, true);
        siteClientSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> assignmentDetailFields = new ArrayList<>();
        assignmentDetailFields.add(new FormField("fieldAgent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Field Agent", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.PEOPLE,1, 2));
        assignmentDetailFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.VENDORS, 2, 2));

        FormSection assignmentDetailsSection = new FormSection("Assignment Details", 3, assignmentDetailFields, true);
        assignmentDetailsSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> lineItemFields = new ArrayList<>();
        lineItemFields.add(new FormField("serviceTask", FacilioField.FieldDisplayType.SERVICE_TASK_ITEMS, "Tasks", FormField.Required.OPTIONAL, 1, 1));

        FormSection serviceTaskSection = new FormSection("", 4, lineItemFields, true);
        serviceTaskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> scheduleAppointmentFields = new ArrayList<>();
        scheduleAppointmentFields.add(new FormField("preferredStartTime", FacilioField.FieldDisplayType.DATETIME, "Preferred Start Time", FormField.Required.REQUIRED, 10, 2));
        scheduleAppointmentFields.add(new FormField("preferredEndTime", FacilioField.FieldDisplayType.DATETIME, "Preferred End Time", FormField.Required.REQUIRED, 11, 2));
        scheduleAppointmentFields.add(new FormField("responseDueDuration", FacilioField.FieldDisplayType.DATETIME, "Response Due Duration", FormField.Required.OPTIONAL, 8, 2));
        scheduleAppointmentFields.add(new FormField("resolutionDueDuration", FacilioField.FieldDisplayType.DATETIME, "Resolution Due Duration", FormField.Required.OPTIONAL, 9, 2));

        FormSection scheduleAppointmentSection = new FormSection("Schedule and Appointment", 4, scheduleAppointmentFields, true);
        scheduleAppointmentSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> webWorkOrderFormSections = new ArrayList<>();
        webWorkOrderFormSections.add(generalInfoSection);
        webWorkOrderFormSections.add(siteClientSection);
        webWorkOrderFormSections.add(assignmentDetailsSection);
        webWorkOrderFormSections.add(serviceTaskSection);
        webWorkOrderFormSections.add(scheduleAppointmentSection);

        webWorkOrderForm.setSections(webWorkOrderFormSections);
        webWorkOrderForm.setIsSystemForm(true);
        webWorkOrderForm.setType(FacilioForm.Type.FORM);

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
        FacilioModule serviceOrderModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, "Service Order", "ServiceOrders", FacilioModule.ModuleType.BASE_ENTITY, true);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setName("SYS_CREATED_TIME");
        createdTime.setColumnName("SYS_CREATED_TIME");
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
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        if(serviceOrderModule!=null && serviceOrderModule.getModuleId()>0) {
            String New = addStateForServiceOrderStateFlow("New");
            String inProgress = addStateForServiceOrderStateFlow("In Progress");
            String completed = addStateForServiceOrderStateFlow("Completed");
            String cannotComplete = addStateForServiceOrderStateFlow("Cannot Complete");
            String closed = addStateForServiceOrderStateFlow("Closed");
//            String onHold = addStateForServiceOrderStateFlow("On Hold");
            String cancelled = addStateForServiceOrderStateFlow("Cancelled");
            long stateFlowId = addServiceOrdersStateFlow(New);
            if(stateFlowId > 0){
                // transition from unPublished to awaitingVendorQuotes
                Criteria completedCriteria = new Criteria();
                completedCriteria.addAndCondition(CriteriaAPI.getCondition("ServiceOrders.IS_ALL_SA_COMPLETED", "isAllSACompleted",String.valueOf(true), BooleanOperators.IS));
                addStateFlowTransitions(serviceOrderModule,stateFlowId,inProgress,completed,"Complete ServiceOrder",completedCriteria,false);
                Criteria inProgressCriteria = new Criteria();
                inProgressCriteria.addAndCondition(CriteriaAPI.getCondition("ServiceOrders.IS_TASK_INITIATED", "isTaskInitiated",String.valueOf(true), BooleanOperators.IS));
                addStateFlowTransitions(serviceOrderModule,stateFlowId,New,inProgress,"Initiate ServiceOrder",inProgressCriteria,false);
                addStateFlowTransitions(serviceOrderModule,stateFlowId,inProgress,cannotComplete,"Cannot Complete",null,true);
                addStateFlowTransitions(serviceOrderModule,stateFlowId,completed,closed,"Close ServiceOrder",null,true);
                addStateFlowTransitions(serviceOrderModule,stateFlowId,New,cancelled,"Cancel ServiceOrder",null,true);
                //IS_TASK_INITIATED
            }
        }
    }

    private static void addStateFlowTransitions(FacilioModule serviceOrderModule, long stateFlowId, String fromState, String toState, String transitionName, Criteria criteria, Boolean transitionType) throws Exception {
        StateflowTransitionContext transition = new StateflowTransitionContext();
        transition.setActivityType(EventType.STATE_TRANSITION);
        transition.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        transition.setType(transitionType ? AbstractStateTransitionRuleContext.TransitionType.NORMAL : AbstractStateTransitionRuleContext.TransitionType.CONDITIONED);
        transition.setStatus(true);
        transition.setName(transitionName);
        transition.setModuleId(serviceOrderModule.getModuleId());
        transition.setStateFlowId(stateFlowId);
        FacilioStatus fromStatus = TicketAPI.getStatus(serviceOrderModule,fromState);
        FacilioStatus toStatus = TicketAPI.getStatus(serviceOrderModule,toState);

        if(fromStatus!=null){
            transition.setFromStateId(fromStatus.getId());
        }
        if(toStatus!=null){
            transition.setToStateId(toStatus.getId());
        }
        transition.setCriteria(criteria);
        FacilioChain chain =TransactionChainFactory.getAddOrUpdateStateFlowTransition();
        chain.getContext().put(FacilioConstants.ContextNames.WORKFLOW_RULE,transition);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE,serviceOrderModule);
        chain.execute();
    }

    private static String addStateForServiceOrderStateFlow(String stateDisplayName) throws Exception {
        FacilioStatus state = new FacilioStatus();
        state.setDisplayName(stateDisplayName);
        state.setTypeCode(1);
        state.setRecordLocked(false);
        state.setTimerEnabled(false);
        state.setRequestedState(false);
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateTicketStatusChain();
        chain.getContext().put(FacilioConstants.ContextNames.TICKET_STATUS,state);
        chain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        chain.execute();
        return state.getStatus();
    }

    private static long addServiceOrdersStateFlow(String defaultState) throws Exception {
        StateFlowRuleContext stateFlow = new StateFlowRuleContext();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioStatus defaultStatus = TicketAPI.getStatus(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER) ,defaultState);
        if(defaultStatus!=null){
            stateFlow.setName("Default Stateflow");
            stateFlow.setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
            stateFlow.setDefaultStateId(defaultStatus.getId());
            stateFlow.setDefaltStateFlow(true);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("ServiceOrders.NAME", "name",null, CommonOperators.IS_NOT_EMPTY));
            stateFlow.setCriteria(criteria);
            FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlow();
            chain.getContext().put(FacilioConstants.ContextNames.RECORD,stateFlow);
            chain.execute();
            return stateFlow.getId();
        }
        return -1;
    }

    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);
        Map<String, List<PagesContext>> pageTemp = new HashMap<>();
        List<PagesContext> pages = Collections.singletonList(new PagesContext(null, null,"", null, true, false, false)
                .addWebTab("summary", "SUMMARY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Summary Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(serviceOrderModule.getName()))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 4, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addWebTab("task", "TASK", true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("task", null, null)
                .addWidget("tasklist", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webtasklist_50_12", 0, 0,  null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone());
        pageTemp.put(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, pages);
        pageTemp.put(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, pages);

        return  pageTemp;
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField categoryField = moduleBean.getField("category", moduleName);
        FacilioField statusField = moduleBean.getField("status", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);

        FacilioField sourceTypeField = moduleBean.getField("sourceType", moduleName);
        FacilioField maintenanceTypeField = moduleBean.getField("maintenanceType", moduleName);
//        FacilioField priorityField = moduleBean.getField("priority", moduleName);
        FacilioField acsaField = moduleBean.getField("autoCreateSa", moduleName);

        FacilioField descriptionField = moduleBean.getField("description", moduleName);

        FacilioField siteField = moduleBean.getField("site", moduleName);
        FacilioField locationField = moduleBean.getField("location", moduleName);
        FacilioField spaceField = moduleBean.getField("space", moduleName);
        FacilioField assetField = moduleBean.getField("asset", moduleName);

        FacilioField fieldAgentField = moduleBean.getField("fieldAgent", moduleName);
//        FacilioField agentNameField = moduleBean.getField("role", FacilioConstants.ContextNames.PEOPLE);

//        SummaryWidgetGroupFields secondLevelLookupField = new SummaryWidgetGroupFields();
//        secondLevelLookupField.setParentLookupFieldId(fieldAgentField.getFieldId());
//        secondLevelLookupField.setFieldId(agentNameField.getFieldId());
//        secondLevelLookupField.setDisplayName();

        FacilioField vendorField = moduleBean.getField("vendor", moduleName);
        FacilioField clientField = moduleBean.getField("client", moduleName);

        FacilioField prefStartTimeField = moduleBean.getField("preferredStartTime", moduleName);
        FacilioField prefEndTimeField = moduleBean.getField("preferredEndTime", moduleName);

        FacilioField responseDueDurationField = moduleBean.getField("responseDueDate", moduleName);
        FacilioField resolutionDueDurationField = moduleBean.getField("resolutionDueDate", moduleName);
        FacilioField responseDueDateField = moduleBean.getField("responseDueDate", moduleName);
        FacilioField resolutionDueDateField = moduleBean.getField("resolutionDueDate", moduleName);
        FacilioField responseDueStatusField = moduleBean.getField("status", moduleName);
        FacilioField resolutionDueStatusField = moduleBean.getField("status", moduleName);

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setDisplayName("General Information");

        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, categoryField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, statusField, 1 , 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, priorityField, 1 , 4, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, sourceTypeField, 2 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, maintenanceTypeField, 2 , 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, acsaField, 2 , 4, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, descriptionField, 3 , 1, 4);

        SummaryWidgetGroup siteWidgetGroup = new SummaryWidgetGroup();
        siteWidgetGroup.setDisplayName("Site Information");

        addSummaryFieldInWidgetGroup(siteWidgetGroup, siteField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(siteWidgetGroup, locationField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(siteWidgetGroup, spaceField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(siteWidgetGroup, assetField, 1 , 4, 1);

        SummaryWidgetGroup userDetailsWidgetGroup = new SummaryWidgetGroup();
        userDetailsWidgetGroup.setDisplayName("User Details");

        addSummaryFieldInWidgetGroup(userDetailsWidgetGroup, fieldAgentField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(userDetailsWidgetGroup, vendorField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(userDetailsWidgetGroup, clientField, 1, 3, 1);

        SummaryWidgetGroup scheduleAndAppointmentWidgetGroup = new SummaryWidgetGroup();
        scheduleAndAppointmentWidgetGroup.setDisplayName("Schedule and Appointment");

        addSummaryFieldInWidgetGroup(scheduleAndAppointmentWidgetGroup, prefStartTimeField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(scheduleAndAppointmentWidgetGroup, prefEndTimeField, 1, 2, 1);

        SummaryWidgetGroup slaWidgetGroup = new SummaryWidgetGroup();
        slaWidgetGroup.setDisplayName("SLA Details");

        addSummaryFieldInWidgetGroup(slaWidgetGroup, responseDueDurationField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(slaWidgetGroup, resolutionDueDurationField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(slaWidgetGroup, responseDueDateField, 1 , 3, 1);
        addSummaryFieldInWidgetGroup(slaWidgetGroup, resolutionDueDateField, 1 , 4, 1);

        addSummaryFieldInWidgetGroup(slaWidgetGroup, responseDueStatusField, 2 , 1, 1);
        addSummaryFieldInWidgetGroup(slaWidgetGroup, resolutionDueStatusField, 2, 2, 1);

        SummaryWidgetGroup systemWidgetGroup = new SummaryWidgetGroup();
        systemWidgetGroup.setDisplayName("System Details");

        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysModifiedByField,1, 3, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysModifiedTimeField, 1, 4, 1);


        widgetGroup.setName("generalInformation");
        widgetGroup.setColumns(4);

        siteWidgetGroup.setName("siteInformation");
        siteWidgetGroup.setColumns(4);

        userDetailsWidgetGroup.setName("userDetails");
        userDetailsWidgetGroup.setColumns(4);

        scheduleAndAppointmentWidgetGroup.setName("scheduleAndAppointment");
        scheduleAndAppointmentWidgetGroup.setColumns(4);

        slaWidgetGroup.setName("slaDetails");
        slaWidgetGroup.setColumns(4);

        systemWidgetGroup.setName("systemDetails");
        systemWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(siteWidgetGroup);
        widgetGroupList.add(userDetailsWidgetGroup);
        widgetGroupList.add(scheduleAndAppointmentWidgetGroup);
        widgetGroupList.add(slaWidgetGroup);
        widgetGroupList.add(systemWidgetGroup);

        pageWidget.setName("serviceOrderDetails");
        pageWidget.setDisplayName("Service Order Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if(field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if(widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            }
            else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }

}
