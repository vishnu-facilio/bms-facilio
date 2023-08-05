package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class ServiceAppointmentModule extends BaseModuleConfig {
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

        SystemEnumField priority = FieldFactory.getDefaultField("priority","Priority","PRIORITY",FieldType.SYSTEM_ENUM);
        priority.setEnumName("ServiceOrderPriority");
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
        generalInformationFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        generalInformationFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        generalInformationFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.REQUIRED, 3, 2));
        generalInformationFields.add(new FormField("serviceOrder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Service Orders",FormField.Required.REQUIRED,4,3));

//        serviceAppointmentFormFields.add(new FormField("appointmentType",FacilioField.FieldDisplayType.SELECTBOX,"Appointment Type", FormField.Required.REQUIRED,3,3));
//        serviceAppointmentFormFields.add(new FormField("inspection", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Inspection",FormField.Required.OPTIONAL,8,3));
//        serviceAppointmentFormFields.add(new FormField("workorder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Workorder",FormField.Required.OPTIONAL,9,3));

        FormSection generalSection = new FormSection("General Information", 1, generalInformationFields, true);
        generalSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> assignmentFields = new ArrayList<>();
        assignmentFields.add(new FormField("fieldAgent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Field Agent", FormField.Required.OPTIONAL,1,2));
//        assignmentFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.ASSET,2, 2));

        FormSection assignmentDetailsSection = new FormSection("Assignment Details",2,assignmentFields,true);
        assignmentDetailsSection.setSectionType(FormSection.SectionType.FIELDS);


        List<FormField> taskFields = new ArrayList<>();
        taskFields.add(new FormField("serviceTasks",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Service Tasks", FormField.Required.REQUIRED,7,1));


        FormSection taskSection = new FormSection("Task",3,taskFields,true);
        taskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> appointmentFields = new ArrayList<>();
        appointmentFields.add(new FormField("scheduledStartTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start Time", FormField.Required.REQUIRED, 10, 3));
        appointmentFields.add(new FormField("scheduledEndTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled End Time", FormField.Required.REQUIRED, 11, 3));
        appointmentFields.add(new FormField("responseDueDuration", FacilioField.FieldDisplayType.DURATION, "Response Due Duration", FormField.Required.REQUIRED, 12, 4));
        appointmentFields.add(new FormField("resolutionDueDuration", FacilioField.FieldDisplayType.DURATION, "Resolution Due Duration", FormField.Required.REQUIRED, 13, 4));



        FormSection appointmentSection = new FormSection("Schedule And Appointment",1,appointmentFields,true);
        appointmentSection.setSectionType(FormSection.SectionType.FIELDS);



        List<FormSection> webServiceAppointmentFormSection = new ArrayList<>();
        webServiceAppointmentFormSection.add(generalSection);
        webServiceAppointmentFormSection.add(assignmentDetailsSection);
        webServiceAppointmentFormSection.add(taskSection);
        webServiceAppointmentFormSection.add(appointmentSection);


        serviceAppointmentForm.setSections(webServiceAppointmentFormSection);
        serviceAppointmentForm.setIsSystemForm(true);
        serviceAppointmentForm.setType(FacilioForm.Type.FORM);
        List<FacilioForm> serviceAppointmentModuleForms = new ArrayList<>();
        serviceAppointmentModuleForms.add(serviceAppointmentForm);
        return serviceAppointmentModuleForms;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> serviceAppointmentViews = new ArrayList<FacilioView>();
        serviceAppointmentViews.add(getServiceAppointmentViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        groupDetails.put("views", serviceAppointmentViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getServiceAppointmentViews() {

        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Service Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(serviceAppointmentModule), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Service Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setSortFields(sortFields);

        List<ViewField> serviceAppointmentViewFields = new ArrayList<>();

        serviceAppointmentViewFields.add(new ViewField("name","Name"));
        serviceAppointmentViewFields.add(new ViewField("site","Site"));
        serviceAppointmentViewFields.add(new ViewField("location","Location"));
        serviceAppointmentViewFields.add(new ViewField("appointmentType","Type"));
        serviceAppointmentViewFields.add(new ViewField("scheduledStartTime","Scheduled Start Time"));
        serviceAppointmentViewFields.add(new ViewField("scheduledEndTime","Scheduled End TIme"));
        serviceAppointmentViewFields.add(new ViewField("estimatedDuration","Estimated Duration"));
        serviceAppointmentViewFields.add(new ViewField("fieldAgent","Field Agent"));

        allView.setFields(serviceAppointmentViewFields);

        return allView;
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
                .addWebTab("serviceappointmentsummary", "SUMMARY", true, null)
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

                .addWebTab("serviceappointmentservicetasks", "SERVICE TASKS", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmentservicetasks", null, null)
                .addWidget("serviceappointmentservicetaskswidget", "Service Tasks", PageWidget.WidgetType.SERVICE_APPOINTMENT_SERVICE_TASKS, "serviceappointmentservicetasks_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceappointmentplans", "PLANS", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmentplans", null, null)
                .addWidget("serviceappointmentplanswidget", "Plans", PageWidget.WidgetType.SERVICE_APPOINTMENT_PLANS, "serviceappointmentplans_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceappointmentactuals", "ACTUALS", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmentactuals", null, null)
                .addWidget("serviceappointmentactualswidget", "Actuals", PageWidget.WidgetType.SERVICE_APPOINTMENT_ACTUALS, "serviceappointmentactuals_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceappointmenttimesheet", "TIME SHEET", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmenttimesheet", null, null)
                .addWidget("serviceappointmenttimesheetwidget", "Time Sheet", PageWidget.WidgetType.SERVICE_APPOINTMENT_TIMESHEET, "serviceappointmenttimesheet_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceappointmenttrip", "TRIP", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmenttrip", null, null)
                .addWidget("serviceappointmenttripwidget", "Trip", PageWidget.WidgetType.SERVICE_APPOINTMENT_TRIP, "serviceappointmenttrip_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addWebTab("serviceappointmentrelated", "RELATED", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceappointmentrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("serviceappointmentbulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("serviceappointmentrelatedlist", "Related List", "List of related records across modules")
                .addWidget("serviceappointmentbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceappointmenthistory", "HISTORY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_60", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .pageDone().getCustomPages();


    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField moduleStateField = moduleBean.getField("status", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);
        FacilioField appointmentTypeField = moduleBean.getField("appointmentType", moduleName);
        FacilioField descriptionField = moduleBean.getField("description", moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup siteInformationwidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup userDetailswidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup scheduleAndAppoinmentwidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup slaDetailswidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup systemDetailswidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, moduleStateField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, priorityField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, appointmentTypeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, descriptionField, 2, 1, 4);

        //Site Details

        FacilioField siteField = moduleBean.getField("site", moduleName);
        FacilioField locationField = moduleBean.getField("location", moduleName);
        FacilioField territoryField = moduleBean.getField("territory", moduleName);

        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, siteField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, locationField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, territoryField, 1, 3, 1);

        // User Details

        FacilioField fieldAgentField = moduleBean.getField("fieldAgent", moduleName);

        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, fieldAgentField, 1, 1, 1);

        // Schedule and Appointment

        FacilioField scheduledStartTimeField = moduleBean.getField("scheduledStartTime", moduleName);
        FacilioField scheduledEndTime = moduleBean.getField("scheduledEndTime", moduleName);


        addSummaryFieldInWidgetGroup(scheduleAndAppoinmentwidgetGroup, scheduledStartTimeField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(scheduleAndAppoinmentwidgetGroup, scheduledEndTime, 1, 2, 1);

        // SLA Details

        FacilioField actualStartTimeField = moduleBean.getField("actualStartTime", moduleName);
        FacilioField actualEndTimeField = moduleBean.getField("actualEndTime", moduleName);
        FacilioField responseDueTimeField = moduleBean.getField("responseDueTime", moduleName);
        FacilioField resolutionDueTimeField = moduleBean.getField("resolutionDueTime", moduleName);
        FacilioField responseDueStatusField = moduleBean.getField("responseDueStatus", moduleName);
        FacilioField resolutionDueStatusField = moduleBean.getField("resolutionDueStatus", moduleName);

        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, actualStartTimeField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, actualEndTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, responseDueTimeField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, resolutionDueTimeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, responseDueStatusField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, resolutionDueStatusField, 2, 2, 1);

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
        siteInformationwidgetGroup.setDisplayName("Site Details");
        siteInformationwidgetGroup.setColumns(4);

        userDetailswidgetGroup.setName("userDetails");
        userDetailswidgetGroup.setDisplayName("User Details");
        userDetailswidgetGroup.setColumns(4);

        scheduleAndAppoinmentwidgetGroup.setName("scheduleAndAppointment");
        scheduleAndAppoinmentwidgetGroup.setDisplayName("Schedule And Appointment");
        scheduleAndAppoinmentwidgetGroup.setColumns(4);

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
        widgetGroupList.add(scheduleAndAppoinmentwidgetGroup);
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
                .addSection("documents", "Documents", "")
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
            dispatch.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            dispatch.setPermissionRequired(true);
            Criteria dispatchCriteria = new Criteria();
            dispatchCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(scheduledStatus.getId()), PickListOperators.IS));
            dispatchCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            dispatchCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            dispatch.setCriteria(dispatchCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,dispatch);
        }

        ServiceAppointmentTicketStatusContext dispatchedStatus = statusMap.get(FacilioConstants.ServiceAppointment.DISPATCHED);
        if(dispatchedStatus != null){
            SystemButtonRuleContext startTrip = new SystemButtonRuleContext();
            startTrip.setName("Start Trip");
            startTrip.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
            startTrip.setIdentifier(FacilioConstants.ServiceAppointment.START_TRIP);
            startTrip.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            startTrip.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            startTrip.setPermissionRequired(true);
            Criteria startTripCriteria = new Criteria();
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(dispatchedStatus.getId()), PickListOperators.IS));
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), CommonOperators.IS_NOT_EMPTY));
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            startTrip.setCriteria(startTripCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,startTrip);

            SystemButtonRuleContext startWork = new SystemButtonRuleContext();
            startWork.setName("Start Work");
            startWork.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
            startWork.setIdentifier(FacilioConstants.ServiceAppointment.START_WORK);
            startWork.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            startWork.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            startWork.setPermissionRequired(true);
            Criteria startWorkCriteria = new Criteria();
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(dispatchedStatus.getId()), PickListOperators.IS));
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), CommonOperators.IS_NOT_EMPTY));
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            startWork.setCriteria(startWorkCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,startWork);
        }

        ServiceAppointmentTicketStatusContext enRouteStatus = statusMap.get(FacilioConstants.ServiceAppointment.EN_ROUTE);
        if(enRouteStatus != null){
            SystemButtonRuleContext endTrip = new SystemButtonRuleContext();
            endTrip.setName("End Trip");
            endTrip.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
            endTrip.setIdentifier(FacilioConstants.ServiceAppointment.END_TRIP);
            endTrip.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            endTrip.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            endTrip.setPermissionRequired(true);
            Criteria endTripCriteria = new Criteria();
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(enRouteStatus.getId()), PickListOperators.IS));
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), CommonOperators.IS_NOT_EMPTY));
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            endTrip.setCriteria(endTripCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,endTrip);
        }

        ServiceAppointmentTicketStatusContext inProgressStatus = statusMap.get(FacilioConstants.ServiceAppointment.IN_PROGRESS);
        if(inProgressStatus != null){
            SystemButtonRuleContext complete = new SystemButtonRuleContext();
            complete.setName("Complete");
            complete.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
            complete.setIdentifier(FacilioConstants.ServiceAppointment.COMPLETE);
            complete.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            complete.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            complete.setPermissionRequired(true);
            Criteria completeCriteria = new Criteria();
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(inProgressStatus.getId()), PickListOperators.IS));
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), CommonOperators.IS_NOT_EMPTY));
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            completeCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.ACTUAL_START_TIME), CommonOperators.IS_NOT_EMPTY));
            complete.setCriteria(completeCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,complete);
        }

        ServiceAppointmentTicketStatusContext completedStatus = statusMap.get(FacilioConstants.ServiceAppointment.COMPLETED);
        ServiceAppointmentTicketStatusContext cancelledStatus = statusMap.get(FacilioConstants.ServiceAppointment.CANCELLED);
        if(completedStatus != null && cancelledStatus != null){
            SystemButtonRuleContext cancel = new SystemButtonRuleContext();
            cancel.setName("Cancel");
            cancel.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
            cancel.setIdentifier(FacilioConstants.ServiceAppointment.CANCEL);
            cancel.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            cancel.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            cancel.setPermissionRequired(true);
            Criteria cancelCriteria = new Criteria();
            cancelCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(completedStatus.getId()), PickListOperators.ISN_T));
            cancelCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(cancelledStatus.getId()), PickListOperators.ISN_T));
            cancel.setCriteria(cancelCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,cancel);
        }

    }
}
