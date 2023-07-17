package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.*;

public class ServiceAppointmentModule extends BaseModuleConfig {
    public ServiceAppointmentModule(){setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);}
        @Override
    public void addData() throws Exception {
            addServiceAppointmentModule();
            addServiceAppointmentTaskModule();
            addServiceTasksField();
            addServiceAppointmentFieldInServiceTask();
            addStateFlow();
            addServiceAppointmentCraftModule();
            addCraftField();
            addServiceAppointmentFieldInCraft();
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

        FacilioField scheduledStartTime = FieldFactory.getDefaultField("scheduledStartTime","Scheduled Start Time","SCHEDULED_START_TIME", FieldType.DATE_TIME);
        serviceAppointmentFields.add(scheduledStartTime);

        FacilioField scheduledEndTime = FieldFactory.getDefaultField("scheduledEndTime","Scheduled End Time","SCHEDULED_END_TIME", FieldType.DATE_TIME);
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
        serviceAppointmentForm.setName("default_asset_web");
        serviceAppointmentForm.setModule(serviceAppointmentModule);
        serviceAppointmentForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        serviceAppointmentForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        List<FormField> serviceAppointmentFormFields = new ArrayList<>();
        serviceAppointmentFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        serviceAppointmentFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        serviceAppointmentFormFields.add(new FormField("appointmentType",FacilioField.FieldDisplayType.SELECTBOX,"Appointment Type", FormField.Required.REQUIRED,3,3));
        serviceAppointmentFormFields.add(new FormField("serviceOrder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Service Orders",FormField.Required.OPTIONAL,6,3));
        serviceAppointmentFormFields.add(new FormField("serviceTasks",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Service Tasks", FormField.Required.OPTIONAL,7,1));
        serviceAppointmentFormFields.add(new FormField("inspection", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Inspection",FormField.Required.OPTIONAL,8,3));
        serviceAppointmentFormFields.add(new FormField("workorder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Workorder",FormField.Required.OPTIONAL,9,3));
        serviceAppointmentFormFields.add(new FormField("scheduledStartTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start Time", FormField.Required.OPTIONAL, 10, 3));
        serviceAppointmentFormFields.add(new FormField("scheduledEndTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled End Time", FormField.Required.OPTIONAL, 11, 3));
        serviceAppointmentFormFields.add(new FormField("estimatedDuration", FacilioField.FieldDisplayType.DURATION,"Estimated Duration", FormField.Required.OPTIONAL,12,3));
        FormSection section = new FormSection("Default", 1, serviceAppointmentFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        serviceAppointmentForm.setSections(Collections.singletonList(section));
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
    private static void addServiceAppointmentCraftModule() throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule craftModule = modBean.getModule(FacilioConstants.ContextNames.SKILL);
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        FacilioModule serviceAppointmentCraftModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_CRAFT,"Service Appointment Crafts","SERVICE_APPOINTMENT_CRAFT_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField craftField = new LookupField(serviceAppointmentCraftModule,"right","Crafts",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"CRAFT_ID",FieldType.LOOKUP,true,false,true,false,"Crafts",craftModule);
        fields.add(craftField);
        LookupField serviceAppointmentField = new LookupField(serviceAppointmentCraftModule,"left","Service Appointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID",FieldType.LOOKUP,true,false,true,true,"Service Appointments",serviceAppointmentModule);
        fields.add(serviceAppointmentField);
        serviceAppointmentCraftModule.setFields(fields);
        modules.add(serviceAppointmentCraftModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    private static void addCraftField() throws Exception{
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField>fields = new ArrayList<>();
        MultiLookupField multiLookupCraftField = FieldFactory.getDefaultField("crafts", "Crafts", null, FieldType.MULTI_LOOKUP);
        multiLookupCraftField.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        multiLookupCraftField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupCraftField.setLookupModule( modBean.getModule(FacilioConstants.ContextNames.SKILL));
        multiLookupCraftField.setRelModule(modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_CRAFT));
        multiLookupCraftField.setRelModuleId(modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_CRAFT).getModuleId());
        fields.add(multiLookupCraftField);
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }
    private void addServiceAppointmentFieldInCraft() throws Exception{
        ModuleBean bean = Constants.getModBean();
        LookupField serviceAppointment = FieldFactory.getDefaultField("serviceAppointment","Service Appointment","SERVICE_APPOINTMENT",FieldType.LOOKUP);
        serviceAppointment.setModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.SKILL),"Craft module doesn't exist."));
        serviceAppointment.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT),"Service Appointment module doesn't exist."));
        bean.addField(serviceAppointment);
    }

}
