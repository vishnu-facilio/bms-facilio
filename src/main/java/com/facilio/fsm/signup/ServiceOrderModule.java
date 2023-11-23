package com.facilio.fsm.signup;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fsm.context.ServiceOrderContext;
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

    public static List<String> serviceOrderSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP);

    public ServiceOrderModule() {
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
    }

    @Override
    public void addData() throws Exception {
        FacilioModule serviceOrderModule = constructServiceOrderModule();

        addActivityModuleForServiceOrder();

        addServiceOrderAttachmentsModule(Constants.getModBean(), AccountUtil.getCurrentOrg().getId(), serviceOrderModule);

        constructServiceOrderNotesModule(Constants.getModBean(), AccountUtil.getCurrentOrg().getId(), serviceOrderModule);

        addSystemButtons();

        addServiceOrderFieldInItemTransactions();

        addSLA();
    }

    private void addServiceOrderFieldInItemTransactions() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTransactions = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);

        LookupField serviceOrder = FieldFactory.getDefaultField("serviceOrder", "Work Order", "SERVICE_ORDER", FieldType.LOOKUP);
        serviceOrder.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER), "Work Order module doesn't exist."));
        serviceOrder.setModule(itemTransactions);

        modBean.addField(serviceOrder);

    }

    public void addActivityModuleForServiceOrder() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule employee = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.SERVICE_ORDER_ACTIVITY,
                "Work Order Activity",
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

    private FacilioModule constructServiceOrderNotesModule(ModuleBean modBean, long orgId, FacilioModule serviceOrderModule) throws Exception {
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
                "title", "Title", "TITLE", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, false, orgId);
        fields.add(titleField);

        StringField bodyField = SignupUtil.getStringField(serviceOrderNotesModule,
                "body", "Body", "BODY", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false, orgId);
        fields.add(bodyField);

        StringField bodyHtmlField = SignupUtil.getStringField(serviceOrderNotesModule,
                "bodyHTML", "Body HTML", "BODY_HTML", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false, orgId);
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
        FacilioModule serviceOrderAttachmentsModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ATTACHMENTS, "Work Order Attachments",
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
                "createdTime", "Created Time", "CREATED_TIME",
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

    private FacilioModule constructServiceOrderModule() throws Exception {
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        FacilioModule serviceOrderModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, "Work Order", "ServiceOrders", FacilioModule.ModuleType.BASE_ENTITY, true);

        List<FacilioField> serviceOrderFieldsList = new ArrayList<>();

        FacilioField subject = FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true);
        subject.setRequired(true);
        serviceOrderFieldsList.add(subject);

        LookupField site = FieldFactory.getDefaultField("site", "Site", "SITE_ID", FieldType.LOOKUP);
        site.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SITE));
        serviceOrderFieldsList.add(site);

        LookupField location = FieldFactory.getDefaultField("location","Location","LOCATION_ID",FieldType.LOOKUP);
        location.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        location.setLookupModule(bean.getModule("location"));
        serviceOrderFieldsList.add(location);

        LookupField client = FieldFactory.getDefaultField("client", "Client", "CLIENT_ID", FieldType.LOOKUP);
        client.setLookupModule(bean.getModule(FacilioConstants.ContextNames.CLIENT));
        serviceOrderFieldsList.add(client);

        LookupField tenant = FieldFactory.getDefaultField("tenant", "Tenant", "TENANT_ID", FieldType.LOOKUP);
        tenant.setLookupModule(bean.getModule(FacilioConstants.ContextNames.TENANT));
        serviceOrderFieldsList.add(tenant);

        FacilioField description = FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA);
        serviceOrderFieldsList.add(description);

        SystemEnumField category = FieldFactory.getDefaultField("category", "Category", "CATEGORY", FieldType.SYSTEM_ENUM);
        category.setEnumName("ServiceAppointmentCategory");
        serviceOrderFieldsList.add(category);

        SystemEnumField maintenanceType = FieldFactory.getDefaultField("maintenanceType", "Maintenance Type", "MAINTENANCE_TYPE", FieldType.SYSTEM_ENUM);
        maintenanceType.setEnumName("ServiceOrderMaintenanceType");
        serviceOrderFieldsList.add(maintenanceType);

        LookupField priority = FieldFactory.getDefaultField("priority", "Priority", "PRIORITY", FieldType.LOOKUP);
        priority.setLookupModule(bean.getModule(FacilioConstants.Priority.PRIORITY));
        priority.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        serviceOrderFieldsList.add(priority);

        LookupField space = FieldFactory.getDefaultField("space", "Space", "SPACE_ID", FieldType.LOOKUP);
        space.setLookupModule(bean.getModule(FacilioConstants.ContextNames.BASE_SPACE));
        serviceOrderFieldsList.add(space);

        LookupField asset = FieldFactory.getDefaultField("asset", "Asset", "ASSET_ID", FieldType.LOOKUP);
        asset.setLookupModule(bean.getModule(FacilioConstants.ContextNames.ASSET));
        serviceOrderFieldsList.add(asset);

        LookupField status = FieldFactory.getDefaultField("status", "Status", "STATUS_ID", FieldType.LOOKUP);
        status.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        status.setLookupModule(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS));
        serviceOrderFieldsList.add(status);

        LookupField vendor = FieldFactory.getDefaultField("vendor", "Vendor", "VENDOR_ID", FieldType.LOOKUP);
        vendor.setLookupModule(bean.getModule(FacilioConstants.ContextNames.VENDORS));
        serviceOrderFieldsList.add(vendor);

        FacilioField autocreatesa = FieldFactory.getDefaultField("autoCreateSa", "Auto Create Appointment", "AUTO_CREATE_SA", FieldType.BOOLEAN);
        serviceOrderFieldsList.add(autocreatesa);

        SystemEnumField sourceType = FieldFactory.getDefaultField("sourceType", "Source Type", "SOURCE_TYPE", FieldType.SYSTEM_ENUM);
        sourceType.setEnumName("ServiceOrderSourceType");
        serviceOrderFieldsList.add(sourceType);

        LookupField territory = FieldFactory.getDefaultField("territory", "Territory", "TERRITORY", FieldType.LOOKUP);
        territory.setLookupModule(bean.getModule(FacilioConstants.Territory.TERRITORY));
        serviceOrderFieldsList.add(territory);

        FacilioField slapolicyid = FieldFactory.getDefaultField("slaPolicyId", "SLA Policy", "SLA_POLICY_ID", FieldType.NUMBER);
        serviceOrderFieldsList.add(slapolicyid);

        LookupField fieldAgent = FieldFactory.getDefaultField("fieldAgent", "Field Agent", "FIELD_AGENT", FieldType.LOOKUP);
        fieldAgent.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        serviceOrderFieldsList.add(fieldAgent);


        FacilioField preferredstarttime = FieldFactory.getDefaultField("preferredStartTime", "Preferred Start Time", "PREFERRED_START_TIME", FieldType.DATE_TIME);
        serviceOrderFieldsList.add(preferredstarttime);

        FacilioField preferredendtime = FieldFactory.getDefaultField("preferredEndTime", "Preferred End Time", "PREFERRED_END_TIME", FieldType.DATE_TIME);
        serviceOrderFieldsList.add(preferredendtime);

        FacilioField resolutionDueDate = FieldFactory.getDefaultField("resolutionDueDate", "Resolution Due Date", "RESOLUTION_DUE_DATE", FieldType.DATE_TIME);
        serviceOrderFieldsList.add(resolutionDueDate);

        FacilioField resolutionDueDuration = FieldFactory.getDefaultField("resolutionDueDuration", "Resolution Due Duration", "RESOLUTION_DUE_DURATION", FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION);
        serviceOrderFieldsList.add(resolutionDueDuration);

        FacilioField resolvedTime = FieldFactory.getDefaultField("resolvedTime", "Resolved Time", "RESOLVED_TIME", FieldType.DATE_TIME);
        serviceOrderFieldsList.add(resolvedTime);

        FacilioField actualstarttime = FieldFactory.getDefaultField("actualStartTime", "Actual Start Time", "ACTUAL_START_TIME", FieldType.DATE_TIME);
        serviceOrderFieldsList.add(actualstarttime);

        FacilioField actualendtime = FieldFactory.getDefaultField("actualEndTime", "Actual End Time", "ACTUAL_END_TIME", FieldType.DATE_TIME);
        serviceOrderFieldsList.add(actualendtime);

        serviceOrderFieldsList.add(FieldFactory.getDefaultField("createdTime","SO Created Time","SO_CREATED_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));

        FacilioField actualduration = FieldFactory.getDefaultField("actualDuration", "Actual Duration", "ACTUAL_DURATION", FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION);
        serviceOrderFieldsList.add(actualduration);

        FacilioField localid = FieldFactory.getDefaultField("localId", "Local Id", "LOCAL_ID", FieldType.NUMBER);
        serviceOrderFieldsList.add(localid);

        FacilioField resolutionDueStatusField = SignupUtil.getSystemEnumField(serviceOrderModule, "resolutionDueStatus", "Resolution Due Status",
                "RESOLUTION_DUE_STATUS", "ServiceOrderRequestResponseStatus", FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        serviceOrderFieldsList.add(resolutionDueStatusField);

        FacilioModule ticketStatusModule = bean.getModule("ticketstatus");

        NumberField stateFlowIdField = SignupUtil.getNumberField(serviceOrderModule,
                "stateFlowId", "State Flow ID", "STATE_FLOW_ID",
                FacilioField.FieldDisplayType.NUMBER, false, false, true, orgId);
        serviceOrderFieldsList.add(stateFlowIdField);

        LookupField moduleStateField = SignupUtil.getLookupField(serviceOrderModule,ticketStatusModule,
                "moduleState", "Module State", "MODULE_STATE", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        serviceOrderFieldsList.add(moduleStateField);

        NumberField approvalFlowIdField = SignupUtil.getNumberField(serviceOrderModule,
                "approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID",
                FacilioField.FieldDisplayType.NUMBER, false, false, true, orgId);
        serviceOrderFieldsList.add(approvalFlowIdField);

        LookupField approvalStateField = SignupUtil.getLookupField(serviceOrderModule, ticketStatusModule,
                "approvalStatus", "Approval Status", "APPROVAL_STATE", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        serviceOrderFieldsList.add(approvalStateField);

        // Total no. of Fields = 34
        serviceOrderModule.setFields(serviceOrderFieldsList);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        return serviceOrderModule;
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
        webWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> generalInformationFields = new ArrayList<>();
        generalInformationFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        generalInformationFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        generalInformationFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL, 3, 2));
        generalInformationFields.add(new FormField("maintenanceType", FacilioField.FieldDisplayType.SELECTBOX, "Maintenance Type", FormField.Required.OPTIONAL, 4, 2));
        generalInformationFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.REQUIRED, 5, 2));
        generalInformationFields.add(new FormField("autoCreateSa", FacilioField.FieldDisplayType.DECISION_BOX, "Auto Create Appointment", FormField.Required.OPTIONAL, 6, 2));

        FormSection generalInfoSection = new FormSection("General Information", 1, generalInformationFields, true);
        generalInfoSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> siteClientFields = new ArrayList<>();
        siteClientFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, FacilioConstants.ContextNames.SITE, 1, 1));
        siteClientFields.add(new FormField("space", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.SPACE, 2, 2));
        siteClientFields.add(new FormField("asset", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.ASSET, 3, 2));
        siteClientFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.CLIENT, 4, 2));
        siteClientFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.TENANT, 5, 2));

        FormSection siteClientSection = new FormSection("Site & Client Information", 2, siteClientFields, true);
        siteClientSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> assignmentDetailFields = new ArrayList<>();
        assignmentDetailFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.VENDORS, 1, 2));
        assignmentDetailFields.add(new FormField("fieldAgent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Field Agent", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.PEOPLE, 2, 2));


        FormSection assignmentDetailsSection = new FormSection("Assignment Details", 3, assignmentDetailFields, true);
        assignmentDetailsSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> lineItemFields = new ArrayList<>();
        lineItemFields.add(new FormField("serviceTask", FacilioField.FieldDisplayType.SERVICE_TASK_ITEMS, "Tasks", FormField.Required.OPTIONAL, 1, 1));

        FormSection serviceTaskSection = new FormSection("", 4, lineItemFields, false);
        serviceTaskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> scheduleAppointmentFields = new ArrayList<>();
        scheduleAppointmentFields.add(new FormField("preferredStartTime", FacilioField.FieldDisplayType.DATETIME, "Preferred Start Time", FormField.Required.REQUIRED, 10, 2));
        scheduleAppointmentFields.add(new FormField("preferredEndTime", FacilioField.FieldDisplayType.DATETIME, "Preferred End Time", FormField.Required.REQUIRED, 11, 2));
//        scheduleAppointmentFields.add(new FormField("resolutionDueDuration", FacilioField.FieldDisplayType.DURATION, "Resolution Due Duration", FormField.Required.OPTIONAL, 12, 2));

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

    public static Criteria getOpenServiceOrdersViewCriteria(FacilioModule module, Map<String, FacilioField> fieldMap) throws Exception {
        ServiceOrderTicketStatusContext newState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.NEW);
        ServiceOrderTicketStatusContext scheduledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.SCHEDULED);
        ServiceOrderTicketStatusContext inProgressState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.IN_PROGRESS);

        Criteria openCriteria = new Criteria();
        openCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(newState.getId()), PickListOperators.IS));
        openCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(scheduledState.getId()), PickListOperators.IS));
        openCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(inProgressState.getId()), PickListOperators.IS));
        openCriteria.setPattern("(1 or 2 or 3)");

        return openCriteria;
    }

    public static Criteria getNewServiceOrdersViewCriteria(FacilioModule module, Map<String, FacilioField> fieldMap) throws Exception {
        ServiceOrderTicketStatusContext newState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.NEW);

        Criteria openCriteria = new Criteria();
        openCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(newState.getId()), PickListOperators.IS));
        openCriteria.setPattern("(1)");

        return openCriteria;
    }

    public static Criteria getOverdueServiceOrdersViewCriteria(FacilioModule module, Map<String, FacilioField> fieldMap) throws Exception {
        ServiceOrderContext.ServiceOrderRequestResponseStatus overdueResponseRequestStatus = ServiceOrderContext.ServiceOrderRequestResponseStatus.BREACHED;

        Criteria openCriteria = new Criteria();
        openCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resolutionDueStatus"), overdueResponseRequestStatus.getIndex().toString(), PickListOperators.IS));
        return openCriteria;
    }

    public static Criteria getUpcomingServiceOrdersViewCriteria(Map<String, FacilioField> fieldMap) throws Exception {
        ServiceOrderTicketStatusContext upcomingState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.UPCOMING);

        Criteria openCriteria = new Criteria();
        openCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(upcomingState.getId()), PickListOperators.IS));
        openCriteria.setPattern("(1)");

        return openCriteria;
    }
    public static Criteria getAllServiceOrdersViewCriteria(Map<String, FacilioField> fieldMap) throws Exception {
        ServiceOrderTicketStatusContext upcomingState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.UPCOMING);

        Criteria openCriteria = new Criteria();
        openCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(upcomingState.getId()), PickListOperators.ISN_T));
        openCriteria.setPattern("(1)");

        return openCriteria;
    }
    public static Criteria getCompletedServiceOrdersViewCriteria(FacilioModule module, Map<String, FacilioField> fieldMap) throws Exception {
        ServiceOrderTicketStatusContext completedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.COMPLETED);

        Criteria completedCriteria = new Criteria();
        completedCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(completedState.getId()), PickListOperators.IS));
        completedCriteria.setPattern("(1)");

        return completedCriteria;
    }

    public static Criteria getAllClosedServiceOrdersViewCriteria(FacilioModule module, Map<String, FacilioField> fieldMap) throws Exception {
        ServiceOrderTicketStatusContext closedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED);

        Criteria closedCriteria = new Criteria();
        closedCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(closedState.getId()), PickListOperators.IS));
        closedCriteria.setPattern("(1)");

        return closedCriteria;
    }

    public static Criteria getCancelledServiceOrdersViewCriteria(FacilioModule module, Map<String, FacilioField> fieldMap) throws Exception {
        ServiceOrderTicketStatusContext cancelledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CANCELLED);

        Criteria cancelledCriteria = new Criteria();
        cancelledCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(cancelledState.getId()), PickListOperators.IS));
        cancelledCriteria.setPattern("(1)");

        return cancelledCriteria;
    }

    private static FacilioView getAllServiceOrdersView(FacilioModule module, Map<String, FacilioField> fieldMap, List<Role> roles) throws Exception {
        Criteria criteria = getAllServiceOrdersViewCriteria(fieldMap);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all_service_orders_view");
        allView.setDisplayName("All Work Orders");
        allView.setModuleName(FacilioConstants.ContextNames.SERVICE_ORDER);
        allView.setSortFields(sortFields);
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        SharingContext<SingleSharingContext> viewSharing = new SharingContext<>();

        // Add the super admin and Dispatcher for view sharing permission.
        for (Role role : roles) {
            if (role.isPrevileged()) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            } else if (role.getName().equals("Dispatcher")) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            }
        }

        allView.setViewSharing(viewSharing);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name", "Subject"));
        serviceOrderViewFields.add(new ViewField("site", "Site"));
        serviceOrderViewFields.add(new ViewField("priority", "Priority"));
        serviceOrderViewFields.add(new ViewField("space", "Space"));
        serviceOrderViewFields.add(new ViewField("asset", "Asset"));
        serviceOrderViewFields.add(new ViewField("status", "Status"));
        serviceOrderViewFields.add(new ViewField("servicePlannedMaintenance", "Planned Maintenance"));

        allView.setFields(serviceOrderViewFields);

        return allView;
    }

    private static FacilioView getUpcomingServiceOrdersView(FacilioModule serviceOrderModule, Map<String, FacilioField> serviceOrderFieldsMap, List<Role> roles) throws Exception {
        Criteria criteria = getUpcomingServiceOrdersViewCriteria(serviceOrderFieldsMap);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("preferredStartTime", "PREFERRED_START_TIME", FieldType.DATE_TIME), true));

        FacilioView allView = new FacilioView();
        allView.setName("upcoming_service_orders_view");
        allView.setDisplayName("Upcoming Work Orders");
        allView.setSortFields(sortFields);
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        SharingContext<SingleSharingContext> viewSharing = new SharingContext<>();

        // Add the super admin and Dispatcher for view sharing permission.
        for (Role role : roles) {
            if (role.isPrevileged()) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            } else if (role.getName().equals("Dispatcher")) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            }
        }

        allView.setViewSharing(viewSharing);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name", "Subject"));
        serviceOrderViewFields.add(new ViewField("site", "Site"));
        serviceOrderViewFields.add(new ViewField("priority", "Priority"));
        serviceOrderViewFields.add(new ViewField("space", "Space"));
        serviceOrderViewFields.add(new ViewField("asset", "Asset"));
        serviceOrderViewFields.add(new ViewField("status", "Status"));
        serviceOrderViewFields.add(new ViewField("servicePlannedMaintenance", "Planned Maintenance"));

        allView.setFields(serviceOrderViewFields);
        return allView;
    }
    private static FacilioView getOverdueServiceOrdersView(FacilioModule serviceOrderModule, Map<String, FacilioField> serviceOrderFieldsMap, List<Role> roles) throws Exception {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        Criteria criteria = getOverdueServiceOrdersViewCriteria(serviceOrderModule, serviceOrderFieldsMap);

        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(serviceOrderModule);

        FacilioView allView = new FacilioView();
        allView.setName("over_service_orders_view");
        allView.setDisplayName("Overdue Work Orders");
        allView.setSortFields(sortFields);
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        SharingContext<SingleSharingContext> viewSharing = new SharingContext<>();

        // Add the super admin and Dispatcher for view sharing permission.
        for (Role role : roles) {
            if (role.isPrevileged()) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            } else if (role.getName().equals("Dispatcher")) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            }
        }

        allView.setViewSharing(viewSharing);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name", "Subject"));
        serviceOrderViewFields.add(new ViewField("site", "Site"));
        serviceOrderViewFields.add(new ViewField("priority", "Priority"));
        serviceOrderViewFields.add(new ViewField("space", "Space"));
        serviceOrderViewFields.add(new ViewField("asset", "Asset"));
        serviceOrderViewFields.add(new ViewField("status", "Status"));
        serviceOrderViewFields.add(new ViewField("servicePlannedMaintenance", "Planned Maintenance"));

        allView.setFields(serviceOrderViewFields);
        return allView;
    }


    private static FacilioView getOpenServiceOrdersView(FacilioModule serviceOrderModule, Map<String, FacilioField> serviceOrderFieldsMap, List<Role> roles) throws Exception {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));
        Criteria criteria = getOpenServiceOrdersViewCriteria(serviceOrderModule, serviceOrderFieldsMap);

        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(serviceOrderModule);

        FacilioView allView = new FacilioView();
        allView.setName("open_service_orders_view");
        allView.setDisplayName("Open Work Orders");
        allView.setSortFields(sortFields);
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        SharingContext<SingleSharingContext> viewSharing = new SharingContext<>();

        // Add the super admin and Dispatcher for view sharing permission.
        for (Role role : roles) {
            if (role.isPrevileged()) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            } else if (role.getName().equals("Dispatcher")) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            }
        }

        allView.setViewSharing(viewSharing);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name", "Subject"));
        serviceOrderViewFields.add(new ViewField("site", "Site"));
        serviceOrderViewFields.add(new ViewField("priority", "Priority"));
        serviceOrderViewFields.add(new ViewField("space", "Space"));
        serviceOrderViewFields.add(new ViewField("asset", "Asset"));
        serviceOrderViewFields.add(new ViewField("status", "Status"));
        serviceOrderViewFields.add(new ViewField("servicePlannedMaintenance", "Planned Maintenance"));

        allView.setFields(serviceOrderViewFields);
        return allView;
    }

    private static FacilioView getNewServiceOrdersView(FacilioModule serviceOrderModule, Map<String, FacilioField> serviceOrderFieldsMap, List<Role> roles) throws Exception {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));
        Criteria criteria = getNewServiceOrdersViewCriteria(serviceOrderModule, serviceOrderFieldsMap);

        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(serviceOrderModule);

        FacilioView allView = new FacilioView();
        allView.setName("new_service_orders_view");
        allView.setDisplayName("New Work Orders");
        allView.setSortFields(sortFields);
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        SharingContext<SingleSharingContext> viewSharing = new SharingContext<>();

        // Add the super admin and Dispatcher for view sharing permission.
        for (Role role : roles) {
            if (role.isPrevileged()) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            } else if (role.getName().equals("Dispatcher")) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            }
        }

        allView.setViewSharing(viewSharing);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name", "Subject"));
        serviceOrderViewFields.add(new ViewField("site", "Site"));
        serviceOrderViewFields.add(new ViewField("priority", "Priority"));
        serviceOrderViewFields.add(new ViewField("space", "Space"));
        serviceOrderViewFields.add(new ViewField("asset", "Asset"));
        serviceOrderViewFields.add(new ViewField("status", "Status"));
        serviceOrderViewFields.add(new ViewField("servicePlannedMaintenance", "Planned Maintenance"));

        allView.setFields(serviceOrderViewFields);
        return allView;
    }

    private static FacilioView getCompletedServiceOrdersView(FacilioModule serviceOrderModule, Map<String, FacilioField> serviceOrderFieldsMap, List<Role> roles) throws Exception {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));
        Criteria criteria = getCompletedServiceOrdersViewCriteria(serviceOrderModule, serviceOrderFieldsMap);

        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(serviceOrderModule);

        FacilioView allView = new FacilioView();
        allView.setName("completed_service_orders");
        allView.setDisplayName("Completed Work Orders");
        allView.setSortFields(sortFields);
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        SharingContext<SingleSharingContext> viewSharing = new SharingContext<>();

        // Add the super admin and Dispatcher for view sharing permission.
        for (Role role : roles) {
            if (role.isPrevileged()) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            } else if (role.getName().equals("Dispatcher")) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            }
        }

        allView.setViewSharing(viewSharing);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name", "Subject"));
        serviceOrderViewFields.add(new ViewField("site", "Site"));
        serviceOrderViewFields.add(new ViewField("priority", "Priority"));
        serviceOrderViewFields.add(new ViewField("space", "Space"));
        serviceOrderViewFields.add(new ViewField("asset", "Asset"));
        serviceOrderViewFields.add(new ViewField("status", "Status"));
        serviceOrderViewFields.add(new ViewField("servicePlannedMaintenance", "Planned Maintenance"));

        allView.setFields(serviceOrderViewFields);
        return allView;
    }

    private static FacilioView getClosedServiceOrdersView(FacilioModule serviceOrderModule, Map<String, FacilioField> serviceOrderFieldsMap, List<Role> roles) throws Exception {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));
        Criteria criteria = getAllClosedServiceOrdersViewCriteria(serviceOrderModule, serviceOrderFieldsMap);

        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(serviceOrderModule);

        FacilioView allView = new FacilioView();
        allView.setName("closed_service_orders_view");
        allView.setDisplayName("Closed Work Orders");
        allView.setSortFields(sortFields);
        allView.setCriteria(criteria);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        SharingContext<SingleSharingContext> viewSharing = new SharingContext<>();

        // Add the super admin and Dispatcher for view sharing permission.
        for (Role role : roles) {
            if (role.isPrevileged()) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            } else if (role.getName().equals("Dispatcher")) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            }
        }

        allView.setViewSharing(viewSharing);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name", "Subject"));
        serviceOrderViewFields.add(new ViewField("site", "Site"));
        serviceOrderViewFields.add(new ViewField("priority", "Priority"));
        serviceOrderViewFields.add(new ViewField("space", "Space"));
        serviceOrderViewFields.add(new ViewField("asset", "Asset"));
        serviceOrderViewFields.add(new ViewField("status", "Status"));
        serviceOrderViewFields.add(new ViewField("servicePlannedMaintenance", "Planned Maintenance"));

        allView.setFields(serviceOrderViewFields);
        return allView;
    }

    private static FacilioView getCancelledServiceOrdersView(FacilioModule serviceOrderModule, Map<String, FacilioField> serviceOrderFieldsMap, List<Role> roles) throws Exception {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));
        Criteria criteria = getCancelledServiceOrdersViewCriteria(serviceOrderModule, serviceOrderFieldsMap);

        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(serviceOrderModule);

        FacilioView allView = new FacilioView();
        allView.setName("cancelled_service_orders_view");
        allView.setDisplayName("Cancelled Work Orders");
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceOrderModule.serviceOrderSupportedApps);

        SharingContext<SingleSharingContext> viewSharing = new SharingContext<>();

        // Add the super admin and Dispatcher for view sharing permission.
        for (Role role : roles) {
            if (role.isPrevileged()) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            } else if (role.getName().equals("Dispatcher")) {
                SingleSharingContext sharingContext = new SingleSharingContext();
                sharingContext.setType(SingleSharingContext.SharingType.ROLE);
                sharingContext.setRoleId(role.getRoleId());
                viewSharing.add(sharingContext);
            }
        }

        allView.setViewSharing(viewSharing);

        List<ViewField> serviceOrderViewFields = new ArrayList<>();
        serviceOrderViewFields.add(new ViewField("name", "Subject"));
        serviceOrderViewFields.add(new ViewField("site", "Site"));
        serviceOrderViewFields.add(new ViewField("priority", "Priority"));
        serviceOrderViewFields.add(new ViewField("space", "Space"));
        serviceOrderViewFields.add(new ViewField("asset", "Asset"));
        serviceOrderViewFields.add(new ViewField("status", "Status"));
        serviceOrderViewFields.add(new ViewField("servicePlannedMaintenance", "Planned Maintenance"));

        allView.setFields(serviceOrderViewFields);
        return allView;
    }

    public List<Map<String, Object>> getViewsAndGroups() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        List<FacilioField> serviceOrderFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        Map<String, FacilioField> serviceOrderFieldsMap = FieldFactory.getAsMap(serviceOrderFields);

        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);
        List<Role> roles = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getOrgId()).getRolesForApps((app == null || app.getId() <= 0) ? null : Collections.singletonList(app.getId()));

        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;
        int order = 1;

        ArrayList<FacilioView> all = new ArrayList<>();
        all.add(getOpenServiceOrdersView(serviceOrderModule, serviceOrderFieldsMap, roles).setOrder(order++));
        all.add(getNewServiceOrdersView(serviceOrderModule, serviceOrderFieldsMap, roles).setOrder(order++));
        all.add(getAllServiceOrdersView(serviceOrderModule, serviceOrderFieldsMap, roles).setOrder(order++));
        all.add(getUpcomingServiceOrdersView(serviceOrderModule, serviceOrderFieldsMap, roles).setOrder(order++));
        all.add(getOverdueServiceOrdersView(serviceOrderModule, serviceOrderFieldsMap, roles).setOrder(order++));
        all.add(getCompletedServiceOrdersView(serviceOrderModule, serviceOrderFieldsMap, roles).setOrder(order++));
        all.add(getClosedServiceOrdersView(serviceOrderModule, serviceOrderFieldsMap, roles).setOrder(order++));
        all.add(getCancelledServiceOrdersView(serviceOrderModule, serviceOrderFieldsMap, roles).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "default_all_service_orders_views");
        groupDetails.put("displayName", "Default");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        groupDetails.put("views", all);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, getSystemPage(app, module, false, true));
        }
        return appNameVsPage;
    }

    private static Boolean addSystemButtons() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        ServiceOrderTicketStatusContext newState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.NEW);
        ServiceOrderTicketStatusContext scheduledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.SCHEDULED);
        ServiceOrderTicketStatusContext inprogressState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.IN_PROGRESS);
        ServiceOrderTicketStatusContext completedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.COMPLETED);
        ServiceOrderTicketStatusContext cancelledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CANCELLED);
        ServiceOrderTicketStatusContext closedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED);
        ServiceOrderTicketStatusContext upcomingState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.UPCOMING);


        /* PRIMARY BUTTON DECLARATIONS GOES HERE */


        Criteria inprogressCriteria = new Criteria();
        inprogressCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(inprogressState.getId()), PickListOperators.IS));

        SystemButtonRuleContext completeWork = new SystemButtonRuleContext();
        completeWork.setName("Complete Work");
        completeWork.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        completeWork.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        completeWork.setIdentifier("completeWork");
        completeWork.setPermissionRequired(true);
        completeWork.setPermission("COMPLETE");
        completeWork.setCriteria(inprogressCriteria);
        addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, completeWork);

        Criteria completedCriteria = new Criteria();
        completedCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(completedState.getId()), PickListOperators.IS));

        SystemButtonRuleContext closeCompleteWork = new SystemButtonRuleContext();
        closeCompleteWork.setName("Close");
        closeCompleteWork.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        closeCompleteWork.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        closeCompleteWork.setIdentifier("closeSO");
        closeCompleteWork.setPermissionRequired(true);
        closeCompleteWork.setPermission("CLOSE");
        closeCompleteWork.setCriteria(completedCriteria);
        addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, closeCompleteWork);

        /* MORE BUTTON DECLARATIONS GOES HERE */
        Criteria cancelButtonCriteria = new Criteria();
        List<Long> cancelAllowedStates = new ArrayList<>();
        cancelAllowedStates.add(newState.getId());
        cancelAllowedStates.add(scheduledState.getId());
        cancelAllowedStates.add(inprogressState.getId());
        cancelAllowedStates.add(upcomingState.getId());
        cancelButtonCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), cancelAllowedStates, PickListOperators.IS));

        SystemButtonRuleContext cancelWork = new SystemButtonRuleContext();
        cancelWork.setName("Cancel");
        cancelWork.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        cancelWork.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        cancelWork.setIdentifier("cancelSO");
        cancelWork.setPermissionRequired(true);
        cancelWork.setPermission("CANCEL");
        cancelWork.setCriteria(cancelButtonCriteria);
        addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, cancelWork);

        SystemButtonRuleContext create = new SystemButtonRuleContext();
        create.setName("Create Work Order");
        create.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        create.setIdentifier(FacilioConstants.ContextNames.CREATE);
        create.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        create.setPermission("CREATE");
        create.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,create);

        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit_list");
        edit.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        edit.setPermission("UPDATE");
        edit.setPermissionRequired(true);
        edit.setCriteria(getEditCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,edit);

        SystemButtonRuleContext summaryEditButton = new SystemButtonRuleContext();
        summaryEditButton.setName("Edit");
        summaryEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        summaryEditButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        summaryEditButton.setIdentifier("edit_summary");
        summaryEditButton.setPermissionRequired(true);
        summaryEditButton.setPermission("UPDATE");
        summaryEditButton.setCriteria(getEditCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,summaryEditButton);

        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        listDeleteButton.setCriteria(getDeleteCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,listDeleteButton);


        SystemButtonRuleContext bulkDeleteButton = new SystemButtonRuleContext();
        bulkDeleteButton.setName("Delete");
        bulkDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        bulkDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkDeleteButton.setIdentifier("delete_bulk");
        bulkDeleteButton.setPermissionRequired(true);
        bulkDeleteButton.setPermission("DELETE");
        bulkDeleteButton.setCriteria(getDeleteCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,bulkDeleteButton);

        SystemButtonApi.addExportAsCSV(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);



        SystemButtonRuleContext cancelInList = new SystemButtonRuleContext();
        cancelInList.setName("Cancel");
        cancelInList.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        cancelInList.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        cancelInList.setIdentifier("cancelSO");
        cancelInList.setPermissionRequired(true);
        cancelInList.setPermission("CANCEL");
        cancelInList.setCriteria(cancelButtonCriteria);
        addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, cancelInList);

        Criteria rescheduleCriteria = new Criteria();
        rescheduleCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Collections.singletonList(upcomingState.getId()), PickListOperators.IS));

        SystemButtonRuleContext reschedule = new SystemButtonRuleContext();
        reschedule.setName("Reschedule");
        reschedule.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        reschedule.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        reschedule.setIdentifier("reschedule");
        reschedule.setPermissionRequired(true);
        reschedule.setPermission("UPDATE");
        reschedule.setCriteria(rescheduleCriteria);
        addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, reschedule);
        return false;
    }

    private static List<PagesContext> getSystemPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ATTACHMENTS);
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ACTIVITY);
        return new ModulePages()
                .addPage("serviceOrder", "Default Work Order Page", "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryFields", null, null)
                .addWidget("summaryFieldsWidget", "Work Order Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,serviceOrderModule.getName()))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("serviceTask", "Task", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("task", null, null)
                .addWidget("taskList", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webTaskList_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("appointment", "Appointment", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("appointments", null, null)
                .addWidget("appointments", "Appointments", PageWidget.WidgetType.SERVICE_ORDER_APPOINTMENTS, "webAppointments_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("plans", "Plans", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("plans", null, null)
                .addWidget("servicePlans", "Plans", PageWidget.WidgetType.SERVICE_ORDER_PLANS, "webServicePlans_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("actuals", "Actuals", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("actuals", null, null)
                .addWidget("serviceActuals", "Actuals", PageWidget.WidgetType.SERVICE_ORDER_ACTUALS, "webServiceActuals_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()

                .addMobileLayout()

                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summary", null, null)
                .addWidget("summaryFieldsWidget", null, PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblemobilesummaryfieldswidget_14", 0, 0, null, getSummaryWidgetDetails(app,serviceOrderModule.getName()))
                .widgetDone()
                .addWidget("serviceOrderResourceWidget", null, PageWidget.WidgetType.RESOURCE_WIDGET, "mobileFlexibleResourceWidget_6", 0, 15, null, null)
                .widgetDone()
                .addWidget("serviceOrderLocationWidget", null, PageWidget.WidgetType.LOCATION_WIDGET, "mobileFlexibleLocationWidget_9", 0, 21, null, null)
                .widgetDone()
                .addWidget("serviceOrderUserWidget", null, PageWidget.WidgetType.USER_WIDGET, "mobileFlexibleUserWidget_7", 0, 30, null, null)
                .widgetDone()
                .addWidget("attachment", null, PageWidget.WidgetType.ATTACHMENT, "flexiblemobileattachment_8", 0, 37, attachmentWidgetParam, null)
                .widgetDone()
                .addWidget("comment", null, PageWidget.WidgetType.COMMENT, "flexiblemobilecomment_8", 0, 45, commentWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceTask", "Service Task", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceTask", null, null)
                .addWidget("serviceOrderServiceTaskListWidget", null, PageWidget.WidgetType.SERVICE_TASK_LIST_WIDGET, "mobileServiceTaskListWidget_16", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointment", "Service Appointment", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointment", null, null)
                .addWidget("serviceOrderServiceAppointmentListWidget", null, PageWidget.WidgetType.SERVICE_APPOINTMENT_LIST, "mobileServiceAppointmentListWidget_16", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("plans", "Plans",PageTabContext.TabType.SIMPLE,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("plans", null, null)
                .addWidget("serviceOrderPlansCostWidget", "Plans Cost", PageWidget.WidgetType.PLANS_COST_WIDGET, "mobilePlansCostWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("plansWidgetGroup", "Plans Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_16", 0, 2, null, getMobilePlansWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("actuals", "Actuals",PageTabContext.TabType.SIMPLE,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("actuals", null, null)
                .addWidget("serviceOrderActualsCostWidget", "Actuals Cost", PageWidget.WidgetType.ACTUALS_COST, "mobileActualsCostWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("actualsWidgetGroup", "Actuals Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_16", 0, 2, null, getMobileActualsWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addTab("history", "History",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblemobileactivity_16", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .layoutDone()
                .pageDone().getCustomPages();
    }

    private static JSONObject getMobilePlansWidgetGroup() throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("plannedItems", "Items", "")
                .addWidget("serviceOrderPlannedItemsListWidget", "Items", PageWidget.WidgetType.PLANNED_ITEMS_LIST,"flexiblemobileplanneditems_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("plannedTools", "Tools", "")
                .addWidget("serviceOrderPlannedToolsListWidget", "Tools", PageWidget.WidgetType.PLANNED_TOOLS_LIST,"flexiblemobileplannedtools_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("plannedServices", "Services", "")
                .addWidget("serviceOrderPlannedServicesListWidget", "Services", PageWidget.WidgetType.PLANNED_SERVICES_LIST,"flexiblemobileplannedservices_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getMobileActualsWidgetGroup() throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("actualItems", "Items", "")
                .addWidget("serviceOrderActualItemsListWidget", "Items", PageWidget.WidgetType.ACTUAL_ITEMS_LIST,"flexiblemobileactualitems_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("actualTools", "Tools", "")
                .addWidget("serviceOrderActualToolsListWidget", "Tools", PageWidget.WidgetType.ACTUAL_TOOLS_LIST,"flexiblemobileactualtools_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("actualServices", "Services", "")
                .addWidget("serviceOrderActualServicesListWidget", "Services", PageWidget.WidgetType.ACTUAL_SERVICES_LIST,"flexiblemobileactualservices_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioField> serviceOrderFields = moduleBean.getAllFields(FacilioConstants.ContextNames.SERVICE_ORDER);
        Map<String, FacilioField> serviceOrderFieldsMap = FieldFactory.getAsMap(serviceOrderFields);

        FacilioField categoryField = serviceOrderFieldsMap.get("category");
        FacilioField statusField = serviceOrderFieldsMap.get("status");
        FacilioField priorityField = serviceOrderFieldsMap.get("priority");
        FacilioField sourceTypeField = serviceOrderFieldsMap.get("sourceType");
        FacilioField servicePlannedMaintenance = serviceOrderFieldsMap.get("servicePlannedMaintenance");
        FacilioField maintenanceTypeField = serviceOrderFieldsMap.get("maintenanceType");
        FacilioField autoCreateSaField = serviceOrderFieldsMap.get("autoCreateSa");
        FacilioField descriptionField = serviceOrderFieldsMap.get("description");
        FacilioField siteField = serviceOrderFieldsMap.get("site");
        FacilioField locationField = serviceOrderFieldsMap.get("location");
        FacilioField spaceField = serviceOrderFieldsMap.get("space");
        FacilioField assetField = serviceOrderFieldsMap.get("asset");
        FacilioField territoryField = serviceOrderFieldsMap.get("territory");
        FacilioField fieldAgentField = serviceOrderFieldsMap.get("fieldAgent");

        FacilioField vendorField = serviceOrderFieldsMap.get("vendor");
        FacilioField clientField = serviceOrderFieldsMap.get("client");
        FacilioField prefStartTimeField = serviceOrderFieldsMap.get("preferredStartTime");
        FacilioField prefEndTimeField = serviceOrderFieldsMap.get("preferredEndTime");
        FacilioField resolutionDueDurationField = serviceOrderFieldsMap.get("resolutionDueDuration");
        FacilioField resolutionDueDateField = serviceOrderFieldsMap.get("resolutionDueDate");
        FacilioField sysCreatedByField = serviceOrderFieldsMap.get("sysCreatedBy");
        FacilioField sysCreatedTimeField = serviceOrderFieldsMap.get("sysCreatedTime");
        FacilioField sysModifiedByField = serviceOrderFieldsMap.get("sysModifiedBy");
        FacilioField sysModifiedTimeField = serviceOrderFieldsMap.get("sysModifiedTime");

        SummaryWidget pageWidget = new SummaryWidget();

        // Group 1
        SummaryWidgetGroup generalInformationWidgetGroup = new SummaryWidgetGroup();
        generalInformationWidgetGroup.setDisplayName("General Information");

        // Row 1
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, descriptionField, 1, 1, 4);

        // Row 2
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, priorityField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, categoryField, 2, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, statusField, 2, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, sourceTypeField, 2, 4, 1);

        // Row 3
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, servicePlannedMaintenance, 3, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, maintenanceTypeField, 3, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, autoCreateSaField, 3, 3, 1);

        // Group 2
        SummaryWidgetGroup siteWidgetGroup = new SummaryWidgetGroup();
        siteWidgetGroup.setDisplayName("Site Information");

        // Row 1
        addSummaryFieldInWidgetGroup(siteWidgetGroup, siteField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(siteWidgetGroup, spaceField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(siteWidgetGroup, assetField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(siteWidgetGroup, locationField, 1, 4, 1);

        // Row 2
        addSummaryFieldInWidgetGroup(siteWidgetGroup, territoryField, 2, 1,1);


        // Group 3
        SummaryWidgetGroup userDetailsWidgetGroup = new SummaryWidgetGroup();
        userDetailsWidgetGroup.setDisplayName("User Details");

        // Row 1
        addSummaryFieldInWidgetGroup(userDetailsWidgetGroup, fieldAgentField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(userDetailsWidgetGroup, vendorField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(userDetailsWidgetGroup, clientField, 1, 3, 1);

        // Group 4
        SummaryWidgetGroup scheduleAndAppointmentWidgetGroup = new SummaryWidgetGroup();
        scheduleAndAppointmentWidgetGroup.setDisplayName("Schedule and Appointment");

        // Row 1
        addSummaryFieldInWidgetGroup(scheduleAndAppointmentWidgetGroup, prefStartTimeField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(scheduleAndAppointmentWidgetGroup, prefEndTimeField, 1, 2, 1);

        // Group 4
        SummaryWidgetGroup slaWidgetGroup = new SummaryWidgetGroup();
        slaWidgetGroup.setDisplayName("SLA Details");

        // Row 1
        addSummaryFieldInWidgetGroup(slaWidgetGroup, resolutionDueDurationField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(slaWidgetGroup, resolutionDueDateField, 1, 2, 1);

        // Group 5
        SummaryWidgetGroup systemWidgetGroup = new SummaryWidgetGroup();
        systemWidgetGroup.setDisplayName("System Details");

        // Row 1
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysModifiedByField, 1, 3, 1);
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
        pageWidget.setDisplayName("Work Order Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 4, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 4, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }

    public void addSLA() throws Exception {
        SLAEntityContext sla = new SLAEntityContext();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);
        List<FacilioField> soFields = moduleBean.getAllFields(FacilioConstants.ContextNames.SERVICE_ORDER);
        Map<String, FacilioField> soFieldsMap = FieldFactory.getAsMap(soFields);

        FacilioField preferredStartTime = soFieldsMap.get("preferredStartTime");
        FacilioField resolutionDueDate = soFieldsMap.get("resolutionDueDate");

        Criteria slaCriteria = new Criteria();
        slaCriteria.addAndCondition(CriteriaAPI.getCondition(soFieldsMap.get("actualEndTime"), String.valueOf(true), CommonOperators.IS_EMPTY));

        long criteriaId = CriteriaAPI.addCriteria(slaCriteria);
        if (criteriaId > 0) {

            sla.setName("Resolution Due Date");
            sla.setDescription("Resolution due date");
            sla.setModuleId(module.getModuleId());
            sla.setBaseFieldId(preferredStartTime.getId());
            sla.setDueFieldId(resolutionDueDate.getId());
            sla.setCriteriaId(criteriaId);

            Map<String, Object> slaEntityMap = FieldUtil.getAsProperties(sla);
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getSLAEntityModule().getTableName())
                    .fields(FieldFactory.getSLAEntityFields());
            builder.insert(slaEntityMap);
        }
    }
    public static Criteria getEditCriteria() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        LookupField status = new LookupField();
        status.setName("status");
        status.setColumnName("STATUS");
        status.setDataType(FieldType.LOOKUP);
        status.setModule(moduleBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER));
        status.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS));

        LookupField recordLocked = new LookupField();
        recordLocked.setName("recordLocked");
        recordLocked.setColumnName("RECORD_LOCKED");
        recordLocked.setDataType(FieldType.BOOLEAN);
        recordLocked.setModule(moduleBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS));

        Criteria oneLevelCriteria=new Criteria();
        oneLevelCriteria.addAndCondition(CriteriaAPI.getCondition(recordLocked,String.valueOf(false), BooleanOperators.IS));

        Condition statusCondition=new Condition();
        statusCondition.setOperator(LookupOperator.LOOKUP);
        statusCondition.setField(status);
        statusCondition.setCriteriaValue(oneLevelCriteria);

        Criteria statusCriteria = new Criteria();
        statusCriteria.addAndCondition(statusCondition);

        return statusCriteria;
    }

    public static Criteria getDeleteCriteria() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        LookupField status = new LookupField();
        status.setName("status");
        status.setColumnName("STATUS");
        status.setDataType(FieldType.LOOKUP);
        status.setModule(moduleBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER));
        status.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS));

        LookupField recordLocked = new LookupField();
        recordLocked.setName("deleteLocked");
        recordLocked.setColumnName("DELETE_LOCKED");
        recordLocked.setDataType(FieldType.BOOLEAN);
        recordLocked.setModule(moduleBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS));

        Criteria oneLevelCriteria=new Criteria();
        oneLevelCriteria.addAndCondition(CriteriaAPI.getCondition(recordLocked,String.valueOf(false),BooleanOperators.IS));

        Condition statusCondition=new Condition();
        statusCondition.setOperator(LookupOperator.LOOKUP);
        statusCondition.setField(status);
        statusCondition.setCriteriaValue(oneLevelCriteria);

        Criteria statusCriteria = new Criteria();
        statusCriteria.addAndCondition(statusCondition);

        return statusCriteria;

    }
}
