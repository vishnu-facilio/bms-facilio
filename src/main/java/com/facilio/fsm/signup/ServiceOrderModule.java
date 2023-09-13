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
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import java.util.*;

import static com.facilio.bmsconsole.util.SystemButtonApi.addSystemButton;

public class ServiceOrderModule extends BaseModuleConfig {

    public static List<String> serviceOrderSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP);
    public ServiceOrderModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
    }

    @Override
    public void addData() throws Exception {
        FacilioModule serviceOrderModule = constructServiceOrderModule();

        addActivityModuleForServiceOrder();

        addServiceOrderAttachmentsModule(Constants.getModBean(),AccountUtil.getCurrentOrg().getId(), serviceOrderModule);

        constructServiceOrderNotesModule(Constants.getModBean(), AccountUtil.getCurrentOrg().getId(), serviceOrderModule);

        addSystemButtons();

        addServiceOrderFieldInItemTransactions();
    }
    private void addServiceOrderFieldInItemTransactions() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTransactions = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);

        LookupField serviceOrder = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER",FieldType.LOOKUP);
        serviceOrder.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER),"Service Order module doesn't exist."));
        serviceOrder.setModule(itemTransactions);

        modBean.addField(serviceOrder);

    }
    public void addActivityModuleForServiceOrder() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule employee = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.SERVICE_ORDER_ACTIVITY,
                "Service Order Activity",
                "Service_Order_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );


        List<FacilioField> fields = new ArrayList<>();

        NumberField parentId = FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);

        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);

        fields.add(timefield);

        NumberField type = FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);

        LookupField doneBy = FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);

        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING);

        fields.add(info);

        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();

        modBean.addSubModule(employee.getModuleId(), module.getModuleId());
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

        LookupField parentId = (LookupField) FieldFactory.getField("parent", "PARENT Id", "PARENT_ID", serviceOrderModule, FieldType.LOOKUP);
        parentId.setDefault(true);
        parentId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        parentId.setLookupModule(serviceOrderModule);
        fields.add(parentId);

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

        LookupField tenant = FieldFactory.getDefaultField("tenant","Tenant","TENANT_ID",FieldType.LOOKUP);
        tenant.setLookupModule(bean.getModule(FacilioConstants.ContextNames.TENANT));
        fields.add(tenant);

        FacilioField subject = FieldFactory.getDefaultField("name","Name","NAME", FieldType.STRING,true);
        subject.setRequired(true);
        fields.add(subject);

        FacilioField description = FieldFactory.getDefaultField("description","Description","DESCRIPTION", FieldType.STRING,FacilioField.FieldDisplayType.TEXTAREA);
        fields.add(description);

        SystemEnumField category = FieldFactory.getDefaultField("category","Category","CATEGORY",FieldType.SYSTEM_ENUM);
        category.setEnumName("ServiceOrderCategory");
        fields.add(category);

        SystemEnumField maintenanceType = FieldFactory.getDefaultField("maintenanceType","Maintenance Type","MAINTENANCE_TYPE",FieldType.SYSTEM_ENUM);
        maintenanceType.setEnumName("ServiceOrderMaintenanceType");
        fields.add(maintenanceType);

        LookupField priority = FieldFactory.getDefaultField("priority","Priority","PRIORITY",FieldType.LOOKUP);
        priority.setLookupModule(bean.getModule(FacilioConstants.Priority.PRIORITY));
        priority.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        fields.add(priority);

        LookupField space = FieldFactory.getDefaultField("space","Space","SPACE_ID",FieldType.LOOKUP);
        space.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SPACE));
        fields.add(space);

        LookupField asset = FieldFactory.getDefaultField("asset","Asset","ASSET_ID",FieldType.LOOKUP);
        asset.setLookupModule(bean.getModule(FacilioConstants.ContextNames.ASSET));
        fields.add(asset);

//        SystemEnumField status = FieldFactory.getDefaultField("status","Status","STATUS_ID",FieldType.SYSTEM_ENUM);
//        status.setEnumName("ServiceOrderStatus");
//        fields.add(status);

        LookupField status = FieldFactory.getDefaultField("status","Status","STATUS_ID",FieldType.LOOKUP);
        status.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        status.setLookupModule(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS));
        fields.add(status);


        LookupField vendor = FieldFactory.getDefaultField("vendor","Vendor","VENDOR_ID",FieldType.LOOKUP);
        vendor.setLookupModule(bean.getModule(FacilioConstants.ContextNames.VENDORS));
        fields.add(vendor);

        FacilioField preferredstarttime = FieldFactory.getDefaultField("scheduledStartTime","Scheduled Start Time","SCHEDULED_START_TIME", FieldType.DATE_TIME);
        fields.add(preferredstarttime);

        FacilioField preferredendtime = FieldFactory.getDefaultField("scheduledEndTime","Scheduled End Time","SCHEDULED_END_TIME", FieldType.DATE_TIME);
        fields.add(preferredendtime);

        FacilioField autocreatesa = FieldFactory.getDefaultField("autoCreateSa","AutoCreate SA","AUTO_CREATE_SA", FieldType.BOOLEAN);
        fields.add(autocreatesa);

//        FacilioField isallserviceappointmentsclosed = FieldFactory.getDefaultField("isAllSACompleted","Is All SA Completed","IS_ALL_SA_COMPLETED", FieldType.BOOLEAN);
//        fields.add(isallserviceappointmentsclosed);
//
//        FacilioField isTaskInitiated = FieldFactory.getDefaultField("isTaskInitiated","Is Task Initiated","IS_TASK_INITIATED", FieldType.BOOLEAN);
//        fields.add(isTaskInitiated);

        FacilioField responseDueDate = FieldFactory.getDefaultField("responseDueDate","Response Due Date","RESPONSE_DUE_DATE", FieldType.DATE_TIME);
        fields.add(responseDueDate);

        FacilioField resolutionDueDate = FieldFactory.getDefaultField("resolutionDueDate","Resolution Due Date","RESOLUTION_DUE_DATE", FieldType.DATE_TIME);
        fields.add(resolutionDueDate);

        FacilioField responseDueDuration = FieldFactory.getDefaultField("responseDueDuration","Response Due Duration","RESPONSE_DUE_DURATION", FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION);
        fields.add(responseDueDuration);

        FacilioField resolutionDueDuration = FieldFactory.getDefaultField("resolutionDueDuration","Resolution Due Duration","RESOLUTION_DUE_DURATION", FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION);
        fields.add(resolutionDueDuration);

        FacilioField resolvedTime = FieldFactory.getDefaultField("resolvedTime","Resolved Time","RESOLVED_TIME", FieldType.DATE_TIME);
        fields.add(resolvedTime);

        SystemEnumField sourceType = FieldFactory.getDefaultField("sourceType","Source Type","SOURCE_TYPE",FieldType.SYSTEM_ENUM);
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

        LookupField sysdeletedby = FieldFactory.getDefaultField("sysdeletedby","Sys Deleted By","SYS_DELETED_BY",FieldType.LOOKUP);
        sysdeletedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(sysdeletedby);

        LookupField territory = FieldFactory.getDefaultField("territory","Territory","Territory",FieldType.LOOKUP);
        territory.setLookupModule(bean.getModule(FacilioConstants.Territory.TERRITORY));
        fields.add(territory);

        FacilioField sysdeleted = FieldFactory.getDefaultField("sysDeleted","Sys Deleted","SYS_DELETED", FieldType.BOOLEAN);
        fields.add(sysdeleted);

        FacilioField sysdeletedtime = FieldFactory.getDefaultField("sysDeletedTime","Sys Deleted Time","SYS_DELETED_TIME", FieldType.DATE_TIME);
        fields.add(sysdeletedtime);

        FacilioField actualstarttime = FieldFactory.getDefaultField("actualStartTime","Actual Start Time","ACTUAL_START_TIME", FieldType.DATE_TIME);
        fields.add(actualstarttime);

        FacilioField actualendtime = FieldFactory.getDefaultField("actualEndTime","Actual End Time","ACTUAL_END_TIME", FieldType.DATE_TIME);
        fields.add(actualendtime);

        FacilioField actualduration = FieldFactory.getDefaultField("actualDuration","Actual Duration","ACTUAL_DURATION", FieldType.NUMBER,FacilioField.FieldDisplayType.DURATION);
        fields.add(actualduration);

//        LookupField assignedto = FieldFactory.getDefaultField("assignedTo","Assigned To","ASSIGNED_TO",FieldType.LOOKUP);
//        assignedto.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
//        fields.add(assignedto);
//
//        LookupField assignedby = FieldFactory.getDefaultField("assignedBy","Assigned By","ASSIGNED_BY",FieldType.LOOKUP);
//        assignedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
//        fields.add(assignedby);

//        FacilioField duedate = FieldFactory.getDefaultField("dueDate","Due Date","DUE_DATE", FieldType.DATE_TIME);
//        fields.add(duedate);

//        FacilioField estimatedstarttime = FieldFactory.getDefaultField("estimatedStartTime","Estimated Start Time","ESTIMATED_START_TIME", FieldType.DATE_TIME);
//        fields.add(estimatedstarttime);
//
//        FacilioField estimatedendtime = FieldFactory.getDefaultField("estimatedEndTime","Estimated End Time","ESTIMATED_END_TIME", FieldType.DATE_TIME);
//        fields.add(estimatedendtime);
//
//        FacilioField estimatedduration = FieldFactory.getDefaultField("estimatedDuration","Estimated Duration","ESTIMATED_DURATION", FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION);
//        fields.add(estimatedduration);

//        FacilioField modulestate = FieldFactory.getDefaultField("modulestate","ModuleState","MODULE_STATE", FieldType.NUMBER);
//        fields.add(modulestate);

//        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", FieldType.LOOKUP);
//        moduleStateField.setDefault(true);
//        moduleStateField.setLookupModule(bean.getModule("ticketstatus"));
//        fields.add(moduleStateField);

        FacilioField noOfAttachments = FieldFactory.getDefaultField("noOfAttachments","No Of Attachments","NO_OF_ATTACHMENTS", FieldType.NUMBER);
        fields.add(noOfAttachments);

        FacilioField noOfClosedTasks = FieldFactory.getDefaultField("noOfClosedTasks","No Of ClosedTasks","NO_OF_CLOSED_TASKS", FieldType.NUMBER);
        fields.add(noOfClosedTasks);

        FacilioField noOfNotes = FieldFactory.getDefaultField("noOfNotes","No Of Notes","NO_OF_NOTES", FieldType.NUMBER);
        fields.add(noOfNotes);

        FacilioField noOfTasks = FieldFactory.getDefaultField("noOfTasks","No Of Tasks","NO_OF_TASKS", FieldType.NUMBER);
        fields.add(noOfTasks);

        FacilioField slapolicyid = FieldFactory.getDefaultField("slaPolicyId","Sla Policy Id","SLA_POLICY_ID", FieldType.NUMBER);
        fields.add(slapolicyid);

//        LookupField slapolicyid = FieldFactory.getDefaultField("slapolicyid","SlaPolicyId","SLA_POLICY_ID",FieldType.LOOKUP);
//        slapolicyid.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SLA_POLICY));
//        fields.add(slapolicyid);

//        FacilioField stateflowid = FieldFactory.getDefaultField("stateFlowId","State Flow Id","STATE_FLOW_ID", FieldType.NUMBER);
//        stateflowid.setDefault(true);
//        fields.add(stateflowid);

        //doubt need to remove?? starts
//        LookupField stateflowid = FieldFactory.getDefaultField("stateflowid","StateFlowId","STATE_FLOW_ID",FieldType.LOOKUP);
//        stateflowid.setLookupModule(bean.getModule(FacilioConstants.ContextNames.STATE_FLOW));
//        fields.add(stateflowid);
        //doubt need to remove?? ends

//        FacilioField approvalRuleId = FieldFactory.getDefaultField("approvalRuleId","Approval Rule Id","APPROVAL_RULE_ID", FieldType.NUMBER);
//        fields.add(approvalRuleId);
//
//        FacilioField approvalState = FieldFactory.getDefaultField("approvalState","Approval State","APPROVAL_STATE", FieldType.NUMBER);
//        fields.add(approvalState);

        FacilioField parentso = FieldFactory.getDefaultField("parentSo","Parent So","PARENT_SOID", FieldType.NUMBER);
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
//        SystemEnumField prerequeststatus = FieldFactory.getDefaultField("prerequestStatus","PreRequest Status","PRE_REQUEST_STATUS",FieldType.SYSTEM_ENUM);
//        prerequeststatus.setEnumName("ServiceOrderPrerequisiteStatus");
//        fields.add(prerequeststatus);
//        //doubt need to remove lookup?? starts
//
//        FacilioField prerequisiteapproved = FieldFactory.getDefaultField("prerequisiteApproved","PreRequisite Approved","PREREQUISITE_APPROVED", FieldType.BOOLEAN);
//        fields.add(prerequisiteapproved);
//
//        FacilioField prerequisiteenabled = FieldFactory.getDefaultField("prerequisiteEnabled","PreRequisite Enabled","PREREQUISITE_ENABLED", FieldType.BOOLEAN);
//        fields.add(prerequisiteenabled);

        FacilioField qrenabled = FieldFactory.getDefaultField("qrEnabled","Qr Enabled","QR_ENABLED", FieldType.BOOLEAN);
        fields.add(qrenabled);
//
//        LookupField requestedby = FieldFactory.getDefaultField("requestedBy","Requested by","REQUESTED_BY_ID",FieldType.LOOKUP);
//        requestedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
//        fields.add(requestedby);

        FacilioField localid = FieldFactory.getDefaultField("localId","Local Id","LOCAL_ID", FieldType.NUMBER);
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
        webWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> generalInformationFields = new ArrayList<>();
        generalInformationFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        generalInformationFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        generalInformationFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.REQUIRED, 3, 2));
        generalInformationFields.add(new FormField("maintenanceType", FacilioField.FieldDisplayType.SELECTBOX, "Maintenance Type", FormField.Required.OPTIONAL, 4, 2));
        generalInformationFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.REQUIRED, 5, 2));
        generalInformationFields.add(new FormField("autoCreateSa", FacilioField.FieldDisplayType.DECISION_BOX, "Auto Create SA", FormField.Required.OPTIONAL, 6, 2));

        FormSection generalInfoSection = new FormSection("General Information", 1, generalInformationFields, true);
        generalInfoSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> siteClientFields = new ArrayList<>();
        siteClientFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, FacilioConstants.ContextNames.SITE, 1, 1));
        siteClientFields.add(new FormField("space", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space", FormField.Required.OPTIONAL,FacilioConstants.ContextNames.SPACE, 2, 2));
        siteClientFields.add(new FormField("asset", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.ASSET,3, 2));
        siteClientFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.CLIENT, 4, 2));
        siteClientFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.TENANT, 5, 2));

        FormSection siteClientSection = new FormSection("Site & Client Information", 2, siteClientFields, true);
        siteClientSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> assignmentDetailFields = new ArrayList<>();
        assignmentDetailFields.add(new FormField("fieldAgent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Field Agent", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.PEOPLE,2, 2));
        assignmentDetailFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.VENDORS, 1, 2));

        FormSection assignmentDetailsSection = new FormSection("Assignment Details", 3, assignmentDetailFields, true);
        assignmentDetailsSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> lineItemFields = new ArrayList<>();
        lineItemFields.add(new FormField("serviceTask", FacilioField.FieldDisplayType.SERVICE_TASK_ITEMS, "Tasks", FormField.Required.OPTIONAL, 1, 1));

        FormSection serviceTaskSection = new FormSection("", 4, lineItemFields, false);
        serviceTaskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> scheduleAppointmentFields = new ArrayList<>();
        scheduleAppointmentFields.add(new FormField("scheduledStartTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start Time", FormField.Required.REQUIRED, 10, 2));
        scheduleAppointmentFields.add(new FormField("scheduledEndTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled End Time", FormField.Required.REQUIRED, 11, 2));
        scheduleAppointmentFields.add(new FormField("responseDueDuration", FacilioField.FieldDisplayType.DURATION, "Response Due Duration", FormField.Required.OPTIONAL, 8, 2));
        scheduleAppointmentFields.add(new FormField("resolutionDueDuration", FacilioField.FieldDisplayType.DURATION, "Resolution Due Duration", FormField.Required.OPTIONAL, 9, 2));

        FormSection scheduleAppointmentSection = new FormSection("Schedule and Appointment", 5, scheduleAppointmentFields, true);
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

    public static Criteria getAllOpenServiceOrders() throws Exception {
        ServiceOrderTicketStatusContext newState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.NEW);
        ServiceOrderTicketStatusContext scheduledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.SCHEDULED);
        ServiceOrderTicketStatusContext inprogressState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.IN_PROGRESS);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Criteria openCriteria = new Criteria();
        openCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"),Collections.singletonList(newState.getId()), PickListOperators.IS));
        openCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("status"),Collections.singletonList(scheduledState.getId()), PickListOperators.IS));
        openCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("status"),Collections.singletonList(inprogressState.getId()), PickListOperators.IS));
        openCriteria.setPattern("(1 or 2 or 3)");

        return openCriteria;
    }

    public static Criteria getAllCompletedServiceOrders() throws Exception {
        ServiceOrderTicketStatusContext completedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.COMPLETED);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Criteria completedCriteria = new Criteria();
        completedCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"),Collections.singletonList(completedState.getId()), PickListOperators.IS));
        completedCriteria.setPattern("(1)");

        return completedCriteria;
    }

    public static Criteria getAllClosedServiceOrders() throws Exception {
        ServiceOrderTicketStatusContext closedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Criteria closedCriteria = new Criteria();
        closedCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"),Collections.singletonList(closedState.getId()), PickListOperators.IS));
        closedCriteria.setPattern("(1)");

        return closedCriteria;
    }

    public static Criteria getAllCancelledServiceOrders() throws Exception {
        ServiceOrderTicketStatusContext cancelledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CANCELLED);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Criteria cancelledCriteria = new Criteria();
        cancelledCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"),Collections.singletonList(cancelledState.getId()), PickListOperators.IS));
        cancelledCriteria.setPattern("(1)");

        return cancelledCriteria;
    }

    private static FacilioView getAllServiceOrders() {
        FacilioModule serviceOrderModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, "Service Order", "ServiceOrders", FacilioModule.ModuleType.BASE_ENTITY, true);

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Service Orders");
        allView.setModuleName(serviceOrderModule.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name","Subject"));
        serviceOrderViewFields.add(new ViewField("site","Site"));
        serviceOrderViewFields.add(new ViewField("priority","Priority"));
        serviceOrderViewFields.add(new ViewField("space","Space"));
        serviceOrderViewFields.add(new ViewField("asset","Asset"));
        serviceOrderViewFields.add(new ViewField("status","Status"));
        allView.setFields(serviceOrderViewFields);

        return allView;
    }


    private static FacilioView getOpenServiceOrders() throws Exception {
        Criteria criteria = getAllOpenServiceOrders();
        FacilioModule serviceOrderModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, "Service Order", "ServiceOrders", FacilioModule.ModuleType.BASE_ENTITY, true);
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(serviceOrderModule);
        FacilioView allView = new FacilioView();
        allView.setName("open");
        allView.setDisplayName("Open");
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name","Subject"));
        serviceOrderViewFields.add(new ViewField("site","Site"));
        serviceOrderViewFields.add(new ViewField("priority","Priority"));
        serviceOrderViewFields.add(new ViewField("space","Space"));
        serviceOrderViewFields.add(new ViewField("asset","Asset"));
        serviceOrderViewFields.add(new ViewField("status","Status"));
        allView.setFields(serviceOrderViewFields);
        return allView;
    }

    private static FacilioView getCompletedServiceOrders() throws Exception {
        Criteria criteria = getAllCompletedServiceOrders();
        FacilioModule serviceOrderModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, "Service Order", "ServiceOrders", FacilioModule.ModuleType.BASE_ENTITY, true);
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(serviceOrderModule);
        FacilioView allView = new FacilioView();
        allView.setName("completed");
        allView.setDisplayName("Completed");
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name","Subject"));
        serviceOrderViewFields.add(new ViewField("site","Site"));
        serviceOrderViewFields.add(new ViewField("priority","Priority"));
        serviceOrderViewFields.add(new ViewField("space","Space"));
        serviceOrderViewFields.add(new ViewField("asset","Asset"));
        serviceOrderViewFields.add(new ViewField("status","Status"));
        allView.setFields(serviceOrderViewFields);
        return allView;
    }

    private static FacilioView getClosedServiceOrders() throws Exception {
        Criteria criteria = getAllClosedServiceOrders();
        FacilioModule serviceOrderModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, "Service Order", "ServiceOrders", FacilioModule.ModuleType.BASE_ENTITY, true);
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(serviceOrderModule);
        FacilioView allView = new FacilioView();
        allView.setName("closed");
        allView.setDisplayName("Closed");
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name","Subject"));
        serviceOrderViewFields.add(new ViewField("site","Site"));
        serviceOrderViewFields.add(new ViewField("priority","Priority"));
        serviceOrderViewFields.add(new ViewField("space","Space"));
        serviceOrderViewFields.add(new ViewField("asset","Asset"));
        serviceOrderViewFields.add(new ViewField("status","Status"));
        allView.setFields(serviceOrderViewFields);
        return allView;
    }

    private static FacilioView getCancelledServiceOrders() throws Exception {
        Criteria criteria = getAllCancelledServiceOrders();
        FacilioModule serviceOrderModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, "Service Order", "ServiceOrders", FacilioModule.ModuleType.BASE_ENTITY, true);
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(serviceOrderModule);
        FacilioView allView = new FacilioView();
        allView.setName("cancelled");
        allView.setDisplayName("Cancelled");
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name","Subject"));
        serviceOrderViewFields.add(new ViewField("site","Site"));
        serviceOrderViewFields.add(new ViewField("priority","Priority"));
        serviceOrderViewFields.add(new ViewField("space","Space"));
        serviceOrderViewFields.add(new ViewField("asset","Asset"));
        serviceOrderViewFields.add(new ViewField("status","Status"));
        allView.setFields(serviceOrderViewFields);
        return allView;
    }

    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;
        int order = 1;

        ArrayList<FacilioView> all = new ArrayList<FacilioView>();
        all.add(getAllServiceOrders().setOrder(order++));
        all.add(getOpenServiceOrders().setOrder(order++));
        all.add(getCompletedServiceOrders().setOrder(order++));
        all.add(getClosedServiceOrders().setOrder(order++));
        all.add(getCancelledServiceOrders().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "allserviceorders");
        groupDetails.put("displayName", "All Service Orders");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        groupDetails.put("views", all);
        groupVsViews.add(groupDetails);

        return groupVsViews;
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
        Map<String, List<PagesContext>> pageTemp = new HashMap<>();
        pageTemp.put(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, getSystemPage());
        pageTemp.put(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, getSystemPage());
        pageTemp.put(FacilioConstants.ApplicationLinkNames.FSM_APP, getSystemPage());

        return  pageTemp;
    }

    private static Boolean addSystemButtons() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        ServiceOrderTicketStatusContext newState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.NEW);
        ServiceOrderTicketStatusContext scheduledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.SCHEDULED);
        ServiceOrderTicketStatusContext inprogressState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.IN_PROGRESS);
        ServiceOrderTicketStatusContext completedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.COMPLETED);
        ServiceOrderTicketStatusContext cancelledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CANCELLED);
        ServiceOrderTicketStatusContext closedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED);

        /* PRIMARY BUTTON DECLARATIONS GOES HERE */
        Criteria inprogressCriteria = new Criteria();
        inprogressCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"),Collections.singletonList(inprogressState.getId()), PickListOperators.IS));

        SystemButtonRuleContext completeWork = new SystemButtonRuleContext();
        completeWork.setName("Complete Work");
        completeWork.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        completeWork.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        completeWork.setIdentifier("completeWork");
        completeWork.setPermissionRequired(true);
        completeWork.setPermission("COMPLETE_SERVICE_ORDER");
        completeWork.setCriteria(inprogressCriteria);
        addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,completeWork);

        Criteria completedCriteria = new Criteria();
        completedCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"),Collections.singletonList(completedState.getId()), PickListOperators.IS));

        SystemButtonRuleContext closeCompleteWork = new SystemButtonRuleContext();
        closeCompleteWork.setName("Close");
        closeCompleteWork.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        closeCompleteWork.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        closeCompleteWork.setIdentifier("closeSO");
        closeCompleteWork.setPermissionRequired(true);
        closeCompleteWork.setPermission("CLOSE_SERVICE_ORDER");
        closeCompleteWork.setCriteria(completedCriteria);
        addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,closeCompleteWork);

        /* MORE BUTTON DECLARATIONS GOES HERE */
        Criteria cancelButtonCriteria = new Criteria();
        List<Long> cancelAllowedStates = new ArrayList<>();
        cancelAllowedStates.add(newState.getId());
        cancelAllowedStates.add(scheduledState.getId());
        cancelAllowedStates.add(inprogressState.getId());
        cancelButtonCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"),cancelAllowedStates, PickListOperators.IS));

        SystemButtonRuleContext cancelWork = new SystemButtonRuleContext();
        cancelWork.setName("Cancel");
        cancelWork.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        cancelWork.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        cancelWork.setIdentifier("cancelSO");
        cancelWork.setPermissionRequired(true);
        cancelWork.setPermission("CANCEL_SERVICE_ORDER");
        cancelWork.setCriteria(cancelButtonCriteria);
        addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,cancelWork);

        return false;
    }
    private static List<PagesContext> getSystemPage() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ACTIVITY);
        return Collections.singletonList(new PagesContext(null, null,"", null, true, false, false)
                        .addWebLayout()
                .addTab("summary", "SUMMARY", PageTabContext.TabType.SIMPLE, true, null)
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
                .addTab("serviceTask", "SERVICE TASK", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("task", null, null)
                .addWidget("tasklist", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webtasklist_50_12", 0, 0,  null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("appointment", "SERVICE APPOINTMENT", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("appointments", null, null)
                .addWidget("appointments", "Appointments", PageWidget.WidgetType.SERVICE_ORDER_APPOINTMENTS, "appointments_50_12", 0, 0,  null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("plans", "PLANS", PageTabContext.TabType.SINGLE_WIDGET_TAB,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("plans", null, null)
                .addWidget("plans", "Plans", PageWidget.WidgetType.SERVICE_ORDER_PLANS, "plans_50_12", 0, 0,  null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("actuals", "ACTUALS", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("actuals", null, null)
                .addWidget("actuals", "Actuals", PageWidget.WidgetType.SERVICE_ORDER_ACTUALS, "actuals_50_12", 0, 0,  null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
//                .addTab("related", "RELATED", true, null)
//                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
//                .addSection("relatedlist", null, null)
//                .addWidget("bulkRelatedList", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(FacilioConstants.ContextNames.QUOTE))
//                .widgetDone()
//                .sectionDone()
//                .columnDone()
//                .tabDone()
                .addTab("history", "HISTORY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_50", 0, 0,  historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                        .layoutDone())
        ;
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioField> serviceOrderFields = moduleBean.getAllFields(FacilioConstants.ContextNames.SERVICE_ORDER);
        Map<String, FacilioField> serviceOrderFieldsMap = FieldFactory.getAsMap(serviceOrderFields);

        FacilioField nameField =  serviceOrderFieldsMap.get("name");
        FacilioField categoryField = serviceOrderFieldsMap.get("category");
        FacilioField statusField = serviceOrderFieldsMap.get("status");
        FacilioField priorityField = serviceOrderFieldsMap.get("priority");
        FacilioField sourceTypeField = serviceOrderFieldsMap.get("sourceType");
        FacilioField maintenanceTypeField = serviceOrderFieldsMap.get("maintenanceType");
        FacilioField autoCreateSaField = serviceOrderFieldsMap.get("autoCreateSa");
        FacilioField descriptionField = serviceOrderFieldsMap.get("description");
        FacilioField siteField = serviceOrderFieldsMap.get("site");
        FacilioField locationField = serviceOrderFieldsMap.get("location");
        FacilioField spaceField = serviceOrderFieldsMap.get("space");
        FacilioField assetField = serviceOrderFieldsMap.get("asset");
        FacilioField fieldAgentField = serviceOrderFieldsMap.get("fieldAgent");

//        FacilioField agentNameField = serviceOrderFieldsMap.get("role", FacilioConstants.ContextNames.PEOPLE);

//        SummaryWidgetGroupFields secondLevelLookupField = new SummaryWidgetGroupFields();
//        secondLevelLookupField.setParentLookupFieldId(fieldAgentField.getFieldId());
//        secondLevelLookupField.setFieldId(agentNameField.getFieldId());
//        secondLevelLookupField.setDisplayName();

        FacilioField vendorField = serviceOrderFieldsMap.get("vendor");
        FacilioField clientField = serviceOrderFieldsMap.get("client");
        FacilioField prefStartTimeField = serviceOrderFieldsMap.get("scheduledStartTime");
        FacilioField prefEndTimeField = serviceOrderFieldsMap.get("scheduledEndTime");
        FacilioField responseDueDurationField = serviceOrderFieldsMap.get("responseDueDuration");
        FacilioField resolutionDueDurationField = serviceOrderFieldsMap.get("resolutionDueDuration");
        FacilioField responseDueDateField = serviceOrderFieldsMap.get("responseDueDate");
        FacilioField resolutionDueDateField = serviceOrderFieldsMap.get("resolutionDueDate");
//        FacilioField responseDueStatusField = serviceOrderFieldsMap.get("status");
//        FacilioField resolutionDueStatusField = serviceOrderFieldsMap.get("status");
        FacilioField sysCreatedByField = serviceOrderFieldsMap.get("sysCreatedBy");
        FacilioField sysCreatedTimeField = serviceOrderFieldsMap.get("sysCreatedTime");
        FacilioField sysModifiedByField = serviceOrderFieldsMap.get("sysModifiedBy");
        FacilioField sysModifiedTimeField = serviceOrderFieldsMap.get("sysModifiedTime");

        SummaryWidget pageWidget = new SummaryWidget();

        // Group 1
        SummaryWidgetGroup generalInformationWidgetGroup = new SummaryWidgetGroup();
        generalInformationWidgetGroup.setDisplayName("General Information");

            // Row 1
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, descriptionField, 1 , 1, 4);

            // Row 2
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, priorityField,2 , 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, categoryField,2 , 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, statusField,2, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, sourceTypeField,2, 4, 1);

            // Row 3
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, maintenanceTypeField, 3 , 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, autoCreateSaField, 3, 2, 1);

        // Group 2
        SummaryWidgetGroup siteWidgetGroup = new SummaryWidgetGroup();
        siteWidgetGroup.setDisplayName("Site Information");

            // Row 1
        addSummaryFieldInWidgetGroup(siteWidgetGroup, siteField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(siteWidgetGroup, spaceField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(siteWidgetGroup, assetField, 1 , 3, 1);
        addSummaryFieldInWidgetGroup(siteWidgetGroup, locationField, 1 , 4, 1);

        // Group 3
        SummaryWidgetGroup userDetailsWidgetGroup = new SummaryWidgetGroup();
        userDetailsWidgetGroup.setDisplayName("User Details");

            // Row 1
        addSummaryFieldInWidgetGroup(userDetailsWidgetGroup, fieldAgentField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(userDetailsWidgetGroup, vendorField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(userDetailsWidgetGroup, clientField, 1, 3, 1);

        // Group 4
        SummaryWidgetGroup scheduleAndAppointmentWidgetGroup = new SummaryWidgetGroup();
        scheduleAndAppointmentWidgetGroup.setDisplayName("Schedule and Appointment");

            // Row 1
        addSummaryFieldInWidgetGroup(scheduleAndAppointmentWidgetGroup, prefStartTimeField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(scheduleAndAppointmentWidgetGroup, prefEndTimeField, 1, 2, 1);

        // Group 4
        SummaryWidgetGroup slaWidgetGroup = new SummaryWidgetGroup();
        slaWidgetGroup.setDisplayName("SLA Details");

            // Row 1
        addSummaryFieldInWidgetGroup(slaWidgetGroup, responseDueDurationField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(slaWidgetGroup, resolutionDueDurationField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(slaWidgetGroup, responseDueDateField, 1 , 3, 1);
        addSummaryFieldInWidgetGroup(slaWidgetGroup, resolutionDueDateField, 1 , 4, 1);

        // Group 5
        SummaryWidgetGroup systemWidgetGroup = new SummaryWidgetGroup();
        systemWidgetGroup.setDisplayName("System Details");

            // Row 1
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysModifiedByField,1, 3, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysModifiedTimeField, 1, 4, 1);

        // TODO: handle for showing custom fields

        generalInformationWidgetGroup.setName("generalInformation");
        generalInformationWidgetGroup.setColumns(4);

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
        widgetGroupList.add(generalInformationWidgetGroup);
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
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 4, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 4, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }

}
