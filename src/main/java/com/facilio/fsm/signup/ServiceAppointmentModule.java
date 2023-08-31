package com.facilio.fsm.signup;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fsm.context.PriorityContext;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class ServiceAppointmentModule extends BaseModuleConfig {
    public static List<String> serviceAppointmentSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP);
    public ServiceAppointmentModule(){setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);}
        @Override
    public void addData() throws Exception {
            addServiceAppointmentModule();
            addServiceAppointmentTaskModule();
            addServiceTasksField();
            addServiceAppointmentFieldInServiceTask();
//            addStateFlow();
            addServiceAppointmentSkillModule();
            addServiceSkillField();
            addActivityModuleForServiceAppointment();
            SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
            addSystemButtons();
    }
    private void addServiceAppointmentModule() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<FacilioField> serviceAppointmentFields = new ArrayList<>();

        NumberField localId = new NumberField(serviceAppointmentModule,"localId","Id", FacilioField.FieldDisplayType.NUMBER,"LOCAL_ID",FieldType.NUMBER,false,false,true,false);
        serviceAppointmentFields.add(localId);
        ModuleLocalIdUtil.insertModuleLocalId(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);

        serviceAppointmentFields.add(new StringField(serviceAppointmentModule,"code","Code",FacilioField.FieldDisplayType.TEXTBOX,"CODE", FieldType.STRING,true,false,true,false));

        serviceAppointmentFields.add(new StringField(serviceAppointmentModule,"name","Name",FacilioField.FieldDisplayType.TEXTBOX,"NAME", FieldType.STRING,true,false,true,true));

        serviceAppointmentFields.add(new StringField(serviceAppointmentModule,"description","Description",FacilioField.FieldDisplayType.TEXTAREA,"DESCRIPTION",FieldType.STRING,false,false,true,false));

        SystemEnumField typeField = (SystemEnumField) FieldFactory.getDefaultField("appointmentType", "Appointment Type", "APPOINTMENT_TYPE", FieldType.SYSTEM_ENUM);
        typeField.setEnumName("AppointmentType");
        serviceAppointmentFields.add(typeField);

        SystemEnumField categoryField = (SystemEnumField) FieldFactory.getDefaultField("category", "Category", "CATEGORY", FieldType.SYSTEM_ENUM);
        categoryField.setEnumName("Category");
        serviceAppointmentFields.add(categoryField);

        LookupField serviceWorkorder = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER_ID",FieldType.LOOKUP);
        serviceWorkorder.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER));
        serviceAppointmentFields.add(serviceWorkorder);

        LookupField inspection = FieldFactory.getDefaultField("inspection","Inspection","INSPECTION_RESPONSE_ID",FieldType.LOOKUP);
        inspection.setLookupModule(moduleBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE));
        serviceAppointmentFields.add(inspection);

        LookupField workorder = FieldFactory.getDefaultField("workorder","Workorder","WORKORDER_ID",FieldType.LOOKUP);
        workorder.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        serviceAppointmentFields.add(workorder);

        LookupField fieldAgent = FieldFactory.getDefaultField("fieldAgent","Field Agent","PEOPLE_ID",FieldType.LOOKUP);
        fieldAgent.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        serviceAppointmentFields.add(fieldAgent);

        FacilioField actualStartTime = FieldFactory.getDefaultField("actualStartTime","Actual Start Time","ACTUAL_START_TIME", FieldType.DATE_TIME);
        serviceAppointmentFields.add(actualStartTime);

        FacilioField actualEndTime = FieldFactory.getDefaultField("actualEndTime","Actual End Time","ACTUAL_END_TIME", FieldType.DATE_TIME);
        serviceAppointmentFields.add(actualEndTime);

        FacilioField actualDuration = FieldFactory.getDefaultField("actualDuration","Actual Duration","ACTUAL_DURATION", FieldType.NUMBER);
        actualDuration.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        serviceAppointmentFields.add(actualDuration);

        FacilioField isAllTasksClosed = FieldFactory.getDefaultField("isAllTasksClosed","All Tasks Closed","IS_ALL_TASKS_CLOSED", FieldType.BOOLEAN);
        serviceAppointmentFields.add(isAllTasksClosed);

        FacilioField scheduledStartTime = FieldFactory.getDefaultField(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME,"Scheduled Start Time","SCHEDULED_START_TIME", FieldType.DATE_TIME);
        serviceAppointmentFields.add(scheduledStartTime);

        FacilioField scheduledEndTime = FieldFactory.getDefaultField(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME,"Scheduled End Time","SCHEDULED_END_TIME", FieldType.DATE_TIME);
        serviceAppointmentFields.add(scheduledEndTime);

        FacilioField estimatedDuration = FieldFactory.getDefaultField("estimatedDuration","Estimated Duration","ESTIMATED_DURATION", FieldType.NUMBER);
        estimatedDuration.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        serviceAppointmentFields.add(estimatedDuration);

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        serviceAppointmentFields.add(moduleStateField);

        NumberField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        serviceAppointmentFields.add(stateFlowIdField);

        LookupField approvalStateField = FieldFactory.getDefaultField("approvalStatus", "Approval Status", "APPROVAL_STATE", FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        serviceAppointmentFields.add(approvalStateField);

        NumberField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        serviceAppointmentFields.add(approvalFlowIdField);

        LookupField siteId = FieldFactory.getDefaultField("site","Site","SITE_ID",FieldType.LOOKUP);
        siteId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        siteId.setLookupModule(moduleBean.getModule("site"));
        serviceAppointmentFields.add(siteId);

        LookupField locationId = FieldFactory.getDefaultField("location","Location","LOCATION_ID",FieldType.LOOKUP);
        locationId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        locationId.setLookupModule(moduleBean.getModule("location"));
        serviceAppointmentFields.add(locationId);

        FacilioField isDefault = FieldFactory.getDefaultField("isDefault","Is Default Appointment","IS_DEFAULT", FieldType.BOOLEAN);
        serviceAppointmentFields.add(isDefault);

        FacilioField responseDueTime = FieldFactory.getDefaultField("responseDueTime","Response Due Time","RESPONSE_DUE_TIME", FieldType.DATE_TIME);
        serviceAppointmentFields.add(responseDueTime);

        FacilioField resolutionDueTime = FieldFactory.getDefaultField("resolutionDueTime","Resolution Due Time","RESOLUTION_DUE_TIME", FieldType.DATE_TIME);
        serviceAppointmentFields.add(resolutionDueTime);

        FacilioField responseDueDuration = FieldFactory.getDefaultField("responseDueDuration","Response Due Duration","RESPONSE_DUE_DURATION", FieldType.NUMBER);
        serviceAppointmentFields.add(responseDueDuration);

        FacilioField resolutionDueDuration = FieldFactory.getDefaultField("resolutionDueDuration","Resolution Due Duration","RESOLUTION_DUE_DURATION", FieldType.NUMBER);
        serviceAppointmentFields.add(resolutionDueDuration);

        LookupField priority = FieldFactory.getDefaultField("priority","Priority","PRIORITY",FieldType.LOOKUP);
        priority.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        priority.setLookupModule(moduleBean.getModule(FacilioConstants.Priority.PRIORITY));
        serviceAppointmentFields.add(priority);

        LookupField territory = FieldFactory.getDefaultField("territory","Territory","TERRITORY_ID",FieldType.LOOKUP);
        territory.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        territory.setLookupModule(moduleBean.getModule("territory"));
        serviceAppointmentFields.add(territory);

        SystemEnumField responseDueStatus = FieldFactory.getDefaultField("responseDueStatus","Response Due Status","RESPONSE_DUE_STATUS",FieldType.SYSTEM_ENUM);
        responseDueStatus.setEnumName("AppointmentDueStatus");
        serviceAppointmentFields.add(responseDueStatus);

        SystemEnumField resolutionDueStatus = FieldFactory.getDefaultField("resolutionDueStatus","Resolution Due Status","RESOLUTION_DUE_STATUS",FieldType.SYSTEM_ENUM);
        resolutionDueStatus.setEnumName("AppointmentDueStatus");
        serviceAppointmentFields.add(resolutionDueStatus);

        FacilioField mismatch = FieldFactory.getDefaultField("mismatch","IS MISMATCH","MISMATCH", FieldType.BOOLEAN);
        serviceAppointmentFields.add(mismatch);

        LookupField status = FieldFactory.getDefaultField("status","Appointment Status","STATUS",FieldType.LOOKUP);
        status.setRequired(true);
        status.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        status.setLookupModule(moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS));
        serviceAppointmentFields.add(status);

        LookupField client = FieldFactory.getDefaultField("client","Client","CLIENT_ID",FieldType.LOOKUP);
        client.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        client.setLookupModule(moduleBean.getModule("client"));
        serviceAppointmentFields.add(client);

        LookupField vendor = FieldFactory.getDefaultField("vendor","Vendor","VENDOR_ID",FieldType.LOOKUP);
        vendor.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        vendor.setLookupModule(moduleBean.getModule("vendors"));
        serviceAppointmentFields.add(vendor);

        LookupField tenant = FieldFactory.getDefaultField("tenant","Tenant","TENANT_ID",FieldType.LOOKUP);
        tenant.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        tenant.setLookupModule(moduleBean.getModule("tenant"));
        serviceAppointmentFields.add(tenant);

        LookupField space = FieldFactory.getDefaultField("space","Space","SPACE_ID",FieldType.LOOKUP);
        space.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        space.setLookupModule(moduleBean.getModule("space"));
        serviceAppointmentFields.add(space);

        LookupField asset = FieldFactory.getDefaultField("asset","Asset","ASSET_ID",FieldType.LOOKUP);
        asset.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        asset.setLookupModule(moduleBean.getModule("asset"));
        serviceAppointmentFields.add(asset);

        NumberField slaPolicy = FieldFactory.getDefaultField("slaPolicy", "SLA Policy", "SLA_POLICY_ID", FieldType.NUMBER);
        slaPolicy.setDefault(true);
        serviceAppointmentFields.add(slaPolicy);

        FacilioField estimatedCost = FieldFactory.getDefaultField("estimatedCost", "Estimated Cost", "ESTIMATED_COST", FieldType.DECIMAL);
        serviceAppointmentFields.add(estimatedCost);

        FacilioField actualCost = FieldFactory.getDefaultField("actualCost", "Actual Cost", "ACTUAL_COST", FieldType.DECIMAL);
        serviceAppointmentFields.add(actualCost);

        serviceAppointmentModule.setFields(serviceAppointmentFields);
        modules.add(serviceAppointmentModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    private void addServiceAppointmentTaskModule() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        FacilioModule serviceAppointmentTaskModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK,"Service Appointment Tasks","SERVICE_APPOINTMENT_TASK_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField serviceTaskField = new LookupField(serviceAppointmentTaskModule,"right","Service Tasks",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_TASK_ID",FieldType.LOOKUP,true,false,true,false,"Service Tasks",serviceTaskModule);
        fields.add(serviceTaskField);
        LookupField serviceAppointmentField = new LookupField(serviceAppointmentTaskModule,"left","Service Appointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID",FieldType.LOOKUP,true,false,true,true,"Service Appointments",serviceAppointmentModule);
        fields.add(serviceAppointmentField);
        serviceAppointmentTaskModule.setFields(fields);
        modules.add(serviceAppointmentTaskModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    private void addServiceTasksField() throws Exception{
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField>fields = new ArrayList<>();
        MultiLookupField multiLookupTasksField = FieldFactory.getDefaultField("serviceTasks", "Service Tasks", null, FieldType.MULTI_LOOKUP);
        multiLookupTasksField.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        multiLookupTasksField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupTasksField.setLookupModule( modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK));
        multiLookupTasksField.setRelModule(modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK));
        multiLookupTasksField.setRelModuleId(modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK).getModuleId());
        fields.add(multiLookupTasksField);
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }
    private void addServiceAppointmentFieldInServiceTask() throws Exception{
        ModuleBean bean = Constants.getModBean();
        LookupField serviceAppointment = FieldFactory.getDefaultField("serviceAppointment","Service Appointment","SERVICE_APPOINTMENT",FieldType.LOOKUP);
        serviceAppointment.setModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK),"Service Task module doesn't exist."));
        serviceAppointment.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT),"Service Appointment module doesn't exist."));
        bean.addField(serviceAppointment);
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        FacilioForm serviceAppointmentForm =new FacilioForm();
        serviceAppointmentForm.setDisplayName("Standard");
        serviceAppointmentForm.setName("default_serviceappointment_web");
        serviceAppointmentForm.setModule(serviceAppointmentModule);
        serviceAppointmentForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        serviceAppointmentForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        List<FormField> generalInformationFields = new ArrayList<>();

        generalInformationFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX,"Name",FormField.Required.OPTIONAL,1,1));
        generalInformationFields.add(new FormField("serviceOrder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Service Orders",FormField.Required.REQUIRED,2,3));
        generalInformationFields.add(new FormField("client",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Client",FormField.Required.OPTIONAL,3,3));
        generalInformationFields.add(new FormField("description",FacilioField.FieldDisplayType.TEXTAREA,"Description",FormField.Required.OPTIONAL,4,1));
        generalInformationFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.REQUIRED, 5, 3));
        generalInformationFields.add(new FormField("fieldAgent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Field Agent", FormField.Required.OPTIONAL,6,2));
        generalInformationFields.add(new FormField("scheduledStartTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start Time", FormField.Required.REQUIRED, 7, 3));
        generalInformationFields.add(new FormField("scheduledEndTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled End Time", FormField.Required.REQUIRED, 8, 3));

        FormSection generalSection = new FormSection("General Information", 1, generalInformationFields, true);
        generalSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> serviceTasksFields = new ArrayList<>();

        serviceTasksFields.add(new FormField("serviceTasks", FacilioField.FieldDisplayType.SERVICE_APPOINTMENT_TASKS, "Tasks", FormField.Required.OPTIONAL, 1, 1));
        FormSection serviceTaskSection = new FormSection("Service Task Details", 2, serviceTasksFields, true);

        serviceTaskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> responseInfoFields=new ArrayList<>();

        responseInfoFields.add(new FormField("responseDueDuration",FacilioField.FieldDisplayType.DURATION,"Response Due Duration",FormField.Required.REQUIRED,1,3));
        responseInfoFields.add(new FormField("resolutionDueDuration",FacilioField.FieldDisplayType.DURATION,"Resolution Due Duration", FormField.Required.REQUIRED, 2, 3));

        FormSection responseInfoSection=new FormSection("Response Information",3,responseInfoFields,true);

        List<FormSection> webServiceAppointmentFormSection = new ArrayList<>();
        webServiceAppointmentFormSection.add(generalSection);
        webServiceAppointmentFormSection.add(serviceTaskSection);
        webServiceAppointmentFormSection.add(responseInfoSection);

        serviceAppointmentForm.setSections(webServiceAppointmentFormSection);
        serviceAppointmentForm.setIsSystemForm(true);
        serviceAppointmentForm.setType(FacilioForm.Type.FORM);
        List<FacilioForm> serviceAppointmentModuleForms = new ArrayList<>();
        serviceAppointmentModuleForms.add(serviceAppointmentForm);
        return serviceAppointmentModuleForms;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> serviceAppointmentViews = new ArrayList<FacilioView>();
        serviceAppointmentViews.add(getOpenServiceAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getMyOpenAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getAllServiceAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getScheduledServiceAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getHighPriorityScheduledAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getOverdueAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getMyOverdueAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getCompletedServiceAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getMyClosedServiceAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getCancelledServiceAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getMyCancelledServiceAppointmentViews().setOrder(order++));



        groupDetails = new HashMap<>();
        groupDetails.put("name", "default");
        groupDetails.put("displayName", "Default");
        groupDetails.put("moduleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        groupDetails.put("views", serviceAppointmentViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getOpenServiceAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.SCHEDULED));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.DISPATCHED));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.IN_PROGRESS));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.EN_ROUTE));

        FacilioView allView = new FacilioView();
        allView.setName("openAppointments");
        allView.setDisplayName("Open Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);


        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }

    private FacilioView getAllServiceAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        FacilioView allView = new FacilioView();
        allView.setName("allAppointments");
        allView.setDisplayName("All Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);

        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }

    private FacilioView getScheduledServiceAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.SCHEDULED));

        FacilioView allView = new FacilioView();
        allView.setName("scheduledAppointments");
        allView.setDisplayName("Scheduled Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);


        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        allView.setViewSharing(getSharingContext(roles));


        return allView;
    }

    private FacilioView getHighPriorityScheduledAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        PriorityContext priority= ServiceAppointmentUtil.getPriority(FacilioConstants.Priority.HIGH);

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.SCHEDULED));
        criteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.PRIORITY), String.valueOf(priority.getId()),NumberOperators.EQUALS));

        FacilioView allView = new FacilioView();
        allView.setName("highPriorityScheduledAppointments");
        allView.setDisplayName("High Priority Scheduled Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);


        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }

    private FacilioView getMyOpenAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.DISPATCHED));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.IN_PROGRESS));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.EN_ROUTE));

        Criteria fieldAgentCriteria = new Criteria();
        fieldAgentCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.IS));

        criteria.andCriteria(fieldAgentCriteria);

        FacilioView allView = new FacilioView();
        allView.setName("myOpenAppointments");
        allView.setDisplayName("My Open Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);


        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FIELD_AGENT);
        roles.add(FacilioConstants.DefaultRoleNames.ASSISTANT_FIELD_AGENT);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }


    private FacilioView getOverdueAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        Integer overDue = ServiceAppointmentContext.DueStatus.OVERDUE.getIndex();

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.DISPATCHED));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.IN_PROGRESS));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.EN_ROUTE));

        Criteria dueStatus = new Criteria();
        dueStatus.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.RESPONSE_DUE_STATUS), String.valueOf(overDue),NumberOperators.EQUALS));
        dueStatus.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.RESPONSE_DUE_STATUS), String.valueOf(overDue),NumberOperators.EQUALS));

        criteria.andCriteria(dueStatus);

        FacilioView allView = new FacilioView();
        allView.setName("overdueAppointments");
        allView.setDisplayName("Overdue Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);


        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }

    private FacilioView getMyOverdueAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        Integer overDue = ServiceAppointmentContext.DueStatus.OVERDUE.getIndex();

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.DISPATCHED));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.IN_PROGRESS));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.EN_ROUTE));

        Criteria dueStatus = new Criteria();
        dueStatus.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.RESPONSE_DUE_STATUS), String.valueOf(overDue),NumberOperators.EQUALS));
        dueStatus.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.RESPONSE_DUE_STATUS), String.valueOf(overDue),NumberOperators.EQUALS));

        Criteria loggedInPeople = new Criteria();
        loggedInPeople.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.IS));

        criteria.andCriteria(dueStatus);
        criteria.andCriteria(loggedInPeople);


        FacilioView allView = new FacilioView();
        allView.setName("myOverdueAppointments");
        allView.setDisplayName("My Overdue Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);


        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FIELD_AGENT);
        roles.add(FacilioConstants.DefaultRoleNames.ASSISTANT_FIELD_AGENT);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }
    private FacilioView getCompletedServiceAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.COMPLETED));

        FacilioView allView = new FacilioView();
        allView.setName("completedAppointments");
        allView.setDisplayName("Completed Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);


        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        allView.setViewSharing(getSharingContext(roles));


        return allView;
    }

    private FacilioView getMyClosedServiceAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.COMPLETED));
        criteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.IS));

        FacilioView allView = new FacilioView();
        allView.setName("closedAppointments");
        allView.setDisplayName("My Closed Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);


        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FIELD_AGENT);
        roles.add(FacilioConstants.DefaultRoleNames.ASSISTANT_FIELD_AGENT);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }

    private FacilioView getCancelledServiceAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.CANCELLED));

        FacilioView allView = new FacilioView();
        allView.setName("cancelledappointments");
        allView.setDisplayName("Cancelled Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);


        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        allView.setViewSharing(getSharingContext(roles));


        return allView;
    }

    private FacilioView getMyCancelledServiceAppointmentViews() throws Exception{

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.CANCELLED));
        criteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.IS));

        FacilioView allView = new FacilioView();
        allView.setName("myCancelledAppointments");
        allView.setDisplayName("My Cancelled Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServiceAppointmentModule.serviceAppointmentSupportedApps);


        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("priority","Priority"));
        serviceAppointmentViewFields.add(new ViewField("space","Space"));
        serviceAppointmentViewFields.add(new ViewField("asset","Asset"));
        serviceAppointmentViewFields.add(new ViewField("status","Appointment Status"));
        serviceAppointmentViewFields.add(new ViewField("actualCost","Actual Cost"));

        allView.setFields(serviceAppointmentViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FIELD_AGENT);
        roles.add(FacilioConstants.DefaultRoleNames.ASSISTANT_FIELD_AGENT);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }

    private static Condition getServiceAppointmentConditions(String appointmentStatus) throws Exception{
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);

        FacilioField statusField = moduleBean.getField("status", module.getName());

        Map<String, ServiceAppointmentTicketStatusContext> statusMap = ServiceAppointmentUtil.getStatusMap(null);
        ServiceAppointmentTicketStatusContext status = statusMap.get(appointmentStatus);

        Condition allView = new Condition();
        allView.setField(statusField);
        allView.setOperator(NumberOperators.EQUALS);
        allView.setValue(String.valueOf(status.getId()));

        return allView;
    }

    private static  SharingContext<SingleSharingContext> getSharingContext(List<String> roles)throws Exception{
        SharingContext<SingleSharingContext> sharingContext = new SharingContext<>();
        for(String role: roles){
            Role fsmRole = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), role);
            SingleSharingContext context = new SingleSharingContext();
            context.setType(SingleSharingContext.SharingType.ROLE.ordinal()+1);
            context.setRoleId(fsmRole.getRoleId());
            sharingContext.add(context);
        }
        return sharingContext;
    }


    private static void addStateFlow() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        FacilioStatus scheduledStatus = new FacilioStatus();
        scheduledStatus.setStatus("scheduled");
        scheduledStatus.setDisplayName("Scheduled");
        scheduledStatus.setTypeCode(1);
        TicketAPI.addStatus(scheduledStatus, serviceAppointmentModule);

        FacilioStatus dispatchedStatus = new FacilioStatus();
        dispatchedStatus.setStatus("dispatched");
        dispatchedStatus.setDisplayName("Dispatched");
        dispatchedStatus.setTypeCode(2);
        TicketAPI.addStatus(dispatchedStatus, serviceAppointmentModule);

        FacilioStatus inProgressStatus = new FacilioStatus();
        inProgressStatus.setStatus("inProgress");
        inProgressStatus.setDisplayName("In Progress");
        inProgressStatus.setTypeCode(3);
        TicketAPI.addStatus(inProgressStatus, serviceAppointmentModule);

        FacilioStatus completedStatus = new FacilioStatus();
        completedStatus.setStatus("completed");
        completedStatus.setDisplayName("Completed");
        completedStatus.setTypeCode(4);
        TicketAPI.addStatus(completedStatus, serviceAppointmentModule);

        FacilioStatus cannotCompleteStatus = new FacilioStatus();
        cannotCompleteStatus.setStatus("cannotComplete");
        cannotCompleteStatus.setDisplayName("Cannot Complete");
        cannotCompleteStatus.setTypeCode(5);
        TicketAPI.addStatus(cannotCompleteStatus, serviceAppointmentModule);

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName("Default Stateflow");
        stateFlowRuleContext.setModuleId(serviceAppointmentModule.getModuleId());
        stateFlowRuleContext.setModule(serviceAppointmentModule);
        stateFlowRuleContext.setActivityType(EventType.CREATE);
        stateFlowRuleContext.setExecutionOrder(1);
        stateFlowRuleContext.setStatus(true);
        stateFlowRuleContext.setDefaltStateFlow(true);
        stateFlowRuleContext.setDefaultStateId(scheduledStatus.getId());
        stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);
    }
    private static void addServiceAppointmentSkillModule() throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule serviceSkillModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL);
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        FacilioModule serviceAppointmentSkillModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_SKILL,"Service Appointment Skills","SERVICE_APPOINTMENT_SKILL_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField serviceSkillField = new LookupField(serviceAppointmentSkillModule,"right","Service Skills",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_SKILL_ID",FieldType.LOOKUP,true,false,true,false,"Service Skills",serviceSkillModule);
        fields.add(serviceSkillField);
        LookupField serviceAppointmentField = new LookupField(serviceAppointmentSkillModule,"left","Service Appointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID",FieldType.LOOKUP,true,false,true,true,"Service Appointments",serviceAppointmentModule);
        fields.add(serviceAppointmentField);
        serviceAppointmentSkillModule.setFields(fields);
        modules.add(serviceAppointmentSkillModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    private static void addServiceSkillField() throws Exception{
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField>fields = new ArrayList<>();
        MultiLookupField multiLookupSkillField = FieldFactory.getDefaultField("skills", "Skills", null, FieldType.MULTI_LOOKUP);
        multiLookupSkillField.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        multiLookupSkillField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupSkillField.setLookupModule( modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL));
        multiLookupSkillField.setRelModule(modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_SKILL));
        multiLookupSkillField.setRelModuleId(modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_SKILL).getModuleId());
        fields.add(multiLookupSkillField);
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }

    public void addActivityModuleForServiceAppointment() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule serviceAppointment = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);

        FacilioModule module = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY,
                "Service Appointment Activity",
                "Service_Appointment_Activity",
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

        modBean.addSubModule(serviceAppointment.getModuleId(), module.getModuleId());
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createServiceAppointmentPage(app, module, false, true));
        }
        return appNameVsPage;
    }
    private static List<PagesContext> createServiceAppointmentPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY);

        return new ModulePages()
                .addPage("serviceAppointment", "Service Appointment","", null, isTemplate, isDefault, false)
                .addWebTab("serviceappointmentsummary", "Summary", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmentsummaryfields", null, null)
                .addWidget("serviceappointmentsummaryfieldswidget", "Service Appointment Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceappointmentservicetasks", "Service Task", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmentservicetasks", null, null)
                .addWidget("serviceappointmentservicetaskswidget", "Service Task", PageWidget.WidgetType.SERVICE_APPOINTMENT_SERVICE_TASKS, "serviceappointmentservicetasks_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceappointmentplans", "Plans", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmentplans", null, null)
                .addWidget("serviceappointmentplanswidget", "Plans", PageWidget.WidgetType.SERVICE_APPOINTMENT_PLANS, "serviceappointmentplans_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceappointmentactuals", "Actuals", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmentactuals", null, null)
                .addWidget("serviceappointmentactualswidget", "Actuals", PageWidget.WidgetType.SERVICE_APPOINTMENT_ACTUALS, "serviceappointmentactuals_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceappointmenttimesheet", "Time Sheet", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmenttimesheet", null, null)
                .addWidget("serviceappointmenttimesheetwidget", "Time Sheet", PageWidget.WidgetType.SERVICE_APPOINTMENT_TIMESHEET, "serviceappointmenttimesheet_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceappointmenttrip", "Trip", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmenttrip", null, null)
                .addWidget("serviceappointmenttripwidget", "Trip", PageWidget.WidgetType.SERVICE_APPOINTMENT_TRIP, "serviceappointmenttrip_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


//                .addWebTab("serviceappointmentrelated", "Related", true, null)
//                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
//                .addSection("serviceappointmentrelationships", "Relationships", "List of relationships and types between records across modules")
//                .addWidget("serviceappointmentbulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0, null, null)
//                .widgetDone()
//                .sectionDone()
//                .addSection("serviceappointmentrelatedlist", "Related List", "List of related records across modules")
//                .addWidget("serviceappointmentbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT))
//                .widgetDone()
//                .sectionDone()
//                .columnDone()
//                .tabDone()

                .addWebTab("serviceappointmenthistory", "History", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_50", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .pageDone().getCustomPages();


    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("serviceOrder", moduleName);
        FacilioField descriptionField = moduleBean.getField("description", moduleName);
        FacilioField category = moduleBean.getField("category", moduleName);
        FacilioField priority = moduleBean.getField("priority", moduleName);
        FacilioField scheduledStartTimeField = moduleBean.getField("scheduledStartTime", moduleName);
        FacilioField scheduledEndTime = moduleBean.getField("scheduledEndTime", moduleName);
        FacilioField estimatedDuration = moduleBean.getField("estimatedDuration", moduleName);
        FacilioField actualStartTimeField = moduleBean.getField("actualStartTime", moduleName);
        FacilioField actualEndTimeField = moduleBean.getField("actualEndTime", moduleName);
        FacilioField actualDuration = moduleBean.getField("actualDuration", moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup siteInformationwidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup userDetailswidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup slaDetailswidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup systemDetailswidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, nameField, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, descriptionField, 2, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, category, 3, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, priority, 3, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, scheduledStartTimeField, 3, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, scheduledEndTime, 3, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, estimatedDuration, 4, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, actualStartTimeField, 4, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, actualEndTimeField, 4, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, actualDuration, 4, 4, 1);

        //Site Details

        FacilioField siteField = moduleBean.getField("site", moduleName);
        FacilioField locationField = moduleBean.getField("location", moduleName);
        FacilioField space = moduleBean.getField("space", moduleName);
        FacilioField asset = moduleBean.getField("asset", moduleName);


        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, siteField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, locationField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, space, 1, 3, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, asset, 1, 4, 1);


        // User Details

        FacilioField fieldAgentField = moduleBean.getField("fieldAgent", moduleName);
        FacilioField vendor = moduleBean.getField("vendor", moduleName);
        FacilioField client = moduleBean.getField("client", moduleName);

        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, fieldAgentField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, vendor, 1, 2, 1);
        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, client, 1, 3, 1);

        // SLA Details

        FacilioField responseDueDuration = moduleBean.getField("responseDueDuration", moduleName);
        FacilioField resolutionDueDuration = moduleBean.getField("resolutionDueDuration", moduleName);
        FacilioField responseDueTimeField = moduleBean.getField("responseDueTime", moduleName);
        FacilioField resolutionDueTimeField = moduleBean.getField("resolutionDueTime", moduleName);
        FacilioField responseDueStatus=moduleBean.getField("responseDueStatus",moduleName);
        FacilioField resolutionDueStatus=moduleBean.getField("resolutionDueStatus",moduleName);

        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, responseDueDuration, 1, 1, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, resolutionDueDuration, 1, 2, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, responseDueTimeField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, resolutionDueTimeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup,responseDueStatus,2,1,1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup,resolutionDueStatus,2,2,1);

        // System Details

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysModifiedTimeField, 1, 4, 1);


        generalInformationwidgetGroup.setName("moduleDetails");
        generalInformationwidgetGroup.setDisplayName("General Information");
        generalInformationwidgetGroup.setColumns(4);

        siteInformationwidgetGroup.setName("siteDetails");
        siteInformationwidgetGroup.setDisplayName("Site Information");
        siteInformationwidgetGroup.setColumns(4);

        userDetailswidgetGroup.setName("userDetails");
        userDetailswidgetGroup.setDisplayName("User Details");
        userDetailswidgetGroup.setColumns(4);

        slaDetailswidgetGroup.setName("slaDetails");
        slaDetailswidgetGroup.setDisplayName("SLA Details");
        slaDetailswidgetGroup.setColumns(4);

        systemDetailswidgetGroup.setName("systemDetails");
        systemDetailswidgetGroup.setDisplayName("System Details");
        systemDetailswidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationwidgetGroup);
        widgetGroupList.add(siteInformationwidgetGroup);
        widgetGroupList.add(userDetailswidgetGroup);
        widgetGroupList.add(slaDetailswidgetGroup);
        widgetGroupList.add(systemDetailswidgetGroup);


        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
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

    private static JSONObject getSummaryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static void addSystemButtons() throws Exception {

        Map<String, ServiceAppointmentTicketStatusContext> statusMap = ServiceAppointmentUtil.getStatusMap(null);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        ServiceAppointmentTicketStatusContext scheduledStatus = statusMap.get(FacilioConstants.ServiceAppointment.SCHEDULED);
        if(scheduledStatus != null) {
            SystemButtonRuleContext dispatch = new SystemButtonRuleContext();
            dispatch.setName("Dispatch");
            dispatch.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
            dispatch.setIdentifier(FacilioConstants.ServiceAppointment.DISPATCH);
            dispatch.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            dispatch.setPermission("DISPATCH");
            dispatch.setPermissionRequired(true);
            Criteria dispatchCriteria = new Criteria();
            dispatchCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(scheduledStatus.getId()), PickListOperators.IS));
            dispatchCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            dispatchCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            dispatch.setCriteria(dispatchCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,dispatch);
        }

        ServiceAppointmentTicketStatusContext enRouteStatus = statusMap.get(FacilioConstants.ServiceAppointment.EN_ROUTE);
        if(enRouteStatus != null) {
            SystemButtonRuleContext endTrip = new SystemButtonRuleContext();
            endTrip.setName("End Trip");
            endTrip.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
            endTrip.setIdentifier(FacilioConstants.ServiceAppointment.END_TRIP);
            endTrip.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//            endTrip.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            endTrip.setPermissionRequired(false);
            Criteria endTripCriteria = new Criteria();
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(enRouteStatus.getId()), PickListOperators.IS));
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.IS));
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            endTrip.setCriteria(endTripCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,endTrip);
        }

        ServiceAppointmentTicketStatusContext dispatchedStatus = statusMap.get(FacilioConstants.ServiceAppointment.DISPATCHED);
        if(dispatchedStatus != null) {
        SystemButtonRuleContext startWork = new SystemButtonRuleContext();
            startWork.setName("Start Work");
            startWork.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
            startWork.setIdentifier(FacilioConstants.ServiceAppointment.START_WORK);
            startWork.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//            startWork.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            startWork.setPermission("START_WORK_ALL");
            startWork.setPermissionRequired(true);
            Criteria startWorkCriteria = new Criteria();
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(dispatchedStatus.getId()), PickListOperators.IS));
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), CommonOperators.IS_NOT_EMPTY));
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.ISN_T));
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            startWork.setCriteria(startWorkCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,startWork);

            SystemButtonRuleContext startWorkOwn = new SystemButtonRuleContext();
            startWorkOwn.setName("Start Work");
            startWorkOwn.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
            startWorkOwn.setIdentifier(FacilioConstants.ServiceAppointment.START_WORK_OWN);
            startWorkOwn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            startWorkOwn.setPermission("START_WORK_OWN");
            startWorkOwn.setPermissionRequired(true);
            Criteria startWorkOwnCriteria = new Criteria();
            startWorkOwnCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(dispatchedStatus.getId()), PickListOperators.IS));
            startWorkOwnCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.IS));
            startWorkOwnCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            startWorkOwnCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            startWorkOwn.setCriteria(startWorkOwnCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,startWorkOwn);

        }

        ServiceAppointmentTicketStatusContext inProgressStatus = statusMap.get(FacilioConstants.ServiceAppointment.IN_PROGRESS);
        if(inProgressStatus != null){
            SystemButtonRuleContext complete = new SystemButtonRuleContext();
            complete.setName("Complete");
            complete.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
            complete.setIdentifier(FacilioConstants.ServiceAppointment.COMPLETE);
            complete.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//            complete.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            complete.setPermission("COMPLETE_ALL");
            complete.setPermissionRequired(true);
            Criteria completeCriteria = new Criteria();
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(inProgressStatus.getId()), PickListOperators.IS));
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), CommonOperators.IS_NOT_EMPTY));
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.ISN_T));
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.ACTUAL_START_TIME), CommonOperators.IS_NOT_EMPTY));
            complete.setCriteria(completeCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,complete);

            SystemButtonRuleContext completeOwn = new SystemButtonRuleContext();
            completeOwn.setName("Complete");
            completeOwn.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
            completeOwn.setIdentifier(FacilioConstants.ServiceAppointment.COMPLETE_OWN);
            completeOwn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            completeOwn.setPermission("COMPLETE_OWN");
            completeOwn.setPermissionRequired(true);
            Criteria completeOwnCriteria = new Criteria();
            completeOwnCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(inProgressStatus.getId()), PickListOperators.IS));
            completeOwnCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.IS));
            completeOwnCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            completeOwnCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            completeOwnCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.ACTUAL_START_TIME), CommonOperators.IS_NOT_EMPTY));
            completeOwn.setCriteria(completeOwnCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,completeOwn);

        }

        if(dispatchedStatus != null) {
            SystemButtonRuleContext startTrip = new SystemButtonRuleContext();
            startTrip.setName("Start Trip");
            startTrip.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
            startTrip.setIdentifier(FacilioConstants.ServiceAppointment.START_TRIP);
            startTrip.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//            startTrip.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            startTrip.setPermissionRequired(false);
            Criteria startTripCriteria = new Criteria();
            List<Long> statusIds = new ArrayList<>();
            statusIds.add(dispatchedStatus.getId());
            if(inProgressStatus != null) {
                statusIds.add(inProgressStatus.getId());
            }
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS), statusIds, PickListOperators.IS));
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE, PickListOperators.IS));
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            startTrip.setCriteria(startTripCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, startTrip);
        }

        ServiceAppointmentTicketStatusContext completedStatus = statusMap.get(FacilioConstants.ServiceAppointment.COMPLETED);
        ServiceAppointmentTicketStatusContext cancelledStatus = statusMap.get(FacilioConstants.ServiceAppointment.CANCELLED);
        if(completedStatus != null && cancelledStatus != null){
            SystemButtonRuleContext cancel = new SystemButtonRuleContext();
            cancel.setName("Cancel");
            cancel.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
            cancel.setIdentifier(FacilioConstants.ServiceAppointment.CANCEL);
            cancel.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            cancel.setPermission("CANCEL");
            cancel.setPermissionRequired(true);
            Criteria cancelCriteria = new Criteria();
            cancelCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(completedStatus.getId()), PickListOperators.ISN_T));
            cancelCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(cancelledStatus.getId()), PickListOperators.ISN_T));
            cancel.setCriteria(cancelCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,cancel);
        }

    }
}
