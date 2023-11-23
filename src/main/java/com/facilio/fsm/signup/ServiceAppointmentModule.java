package com.facilio.fsm.signup;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.*;
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
import com.facilio.db.criteria.operators.*;
import com.facilio.fsm.context.*;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import com.twilio.twiml.voice.Prompt;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceAppointmentModule extends BaseModuleConfig {
    public static List<String> serviceAppointmentSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP);
    public ServiceAppointmentModule(){setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);}
        @Override
    public void addData() throws Exception {
            addServiceAppointmentModule();
            addServiceAppointmentTaskModule();
            addServiceTasksField();
            addServiceAppointmentFieldInServiceTask();
            addServiceAppointmentSkillModule();
            addServiceSkillField();
            addActivityModuleForServiceAppointment();
            SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
            addSystemButtons();
            addServiceTaskSystemButtons();
            addSLA();
    }
    private void addServiceAppointmentModule() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule serviceAppointmentModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,"Appointment","SERVICE_APPOINTMENT", FacilioModule.ModuleType.BASE_ENTITY,true);
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
        categoryField.setEnumName("ServiceAppointmentCategory");
        serviceAppointmentFields.add(categoryField);

        LookupField serviceWorkorder = FieldFactory.getDefaultField("serviceOrder","Work Order","SERVICE_ORDER_ID",FieldType.LOOKUP);
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

        FacilioField resolutionDueTime = FieldFactory.getDefaultField("resolutionDueTime","Resolution Due Time","RESOLUTION_DUE_TIME", FieldType.DATE_TIME);
        serviceAppointmentFields.add(resolutionDueTime);

        FacilioField resolutionDueDuration = FieldFactory.getDefaultField("resolutionDueDuration","Resolution Due Duration","RESOLUTION_DUE_DURATION", FieldType.NUMBER);
        resolutionDueDuration.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        serviceAppointmentFields.add(resolutionDueDuration);

        LookupField priority = FieldFactory.getDefaultField("priority","Priority","PRIORITY",FieldType.LOOKUP);
        priority.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        priority.setLookupModule(moduleBean.getModule(FacilioConstants.Priority.PRIORITY));
        serviceAppointmentFields.add(priority);

        LookupField territory = FieldFactory.getDefaultField("territory","Territory","TERRITORY_ID",FieldType.LOOKUP);
        territory.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        territory.setLookupModule(moduleBean.getModule("territory"));
        serviceAppointmentFields.add(territory);

        SystemEnumField resolutionDueStatus = FieldFactory.getDefaultField("resolutionDueStatus","Resolution SLA Status","RESOLUTION_DUE_STATUS",FieldType.SYSTEM_ENUM);
        resolutionDueStatus.setEnumName("AppointmentDueStatus");
        serviceAppointmentFields.add(resolutionDueStatus);

        FacilioField mismatch = FieldFactory.getDefaultField("mismatch","Mismatch","MISMATCH", FieldType.BOOLEAN);
        serviceAppointmentFields.add(mismatch);

        MultiEnumField mismatchType = FieldFactory.getDefaultField("mismatchType","Mismatch Type",null, FieldType.MULTI_ENUM, FacilioField.FieldDisplayType.SELECTBOX);
        List<String> types = Arrays.asList( "Territory", "Availability", "Skills");
        List<EnumFieldValue<Integer>> typeValues = types.stream().map( val -> {
            int index = types.indexOf(val)+1;
            return new EnumFieldValue<>(index, val, index, true);
        }).collect(Collectors.toList());
        mismatchType.setValues(typeValues);
        serviceAppointmentFields.add(mismatchType);

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
        space.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.BASE_SPACE));
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

        FacilioField dispatched = FieldFactory.getDefaultField("dispatched","Dispatched","DISPATCHED", FieldType.BOOLEAN);
        serviceAppointmentFields.add(dispatched);

        FacilioField dispatchedTime = FieldFactory.getDefaultField("dispatchedTime","Dispatched Time","DISPATCHED_TIME", FieldType.DATE_TIME);
        serviceAppointmentFields.add(dispatchedTime);

        LookupField dispatchedBy = FieldFactory.getDefaultField("dispatchedBy","Dispatched By","DISPATCHED_BY",FieldType.LOOKUP);
        dispatchedBy.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        serviceAppointmentFields.add(dispatchedBy);

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
        FacilioModule serviceAppointmentTaskModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK,"Appointment Tasks","SERVICE_APPOINTMENT_TASK_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField serviceTaskField = new LookupField(serviceAppointmentTaskModule,"right","Task",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_TASK_ID",FieldType.LOOKUP,true,false,true,false,"Tasks",serviceTaskModule);
        fields.add(serviceTaskField);
        LookupField serviceAppointmentField = new LookupField(serviceAppointmentTaskModule,"left","Appointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID",FieldType.LOOKUP,true,false,true,true,"Appointments",serviceAppointmentModule);
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
        MultiLookupField multiLookupTasksField = FieldFactory.getDefaultField("serviceTasks", "Tasks", null, FieldType.MULTI_LOOKUP);
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
        LookupField serviceAppointment = FieldFactory.getDefaultField("serviceAppointment","Appointment","SERVICE_APPOINTMENT",FieldType.LOOKUP);
        serviceAppointment.setModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK),"Tasks module doesn't exist."));
        serviceAppointment.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT),"Appointment module doesn't exist."));
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

        generalInformationFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX,"Name",FormField.Required.REQUIRED,1,1));
        generalInformationFields.add(new FormField("serviceOrder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Work Order",FormField.Required.REQUIRED,2,3));
        generalInformationFields.add(new FormField("client",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Client",FormField.Required.OPTIONAL,3,3));
        generalInformationFields.add(new FormField("description",FacilioField.FieldDisplayType.TEXTAREA,"Description",FormField.Required.OPTIONAL,4,1));
        generalInformationFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL, 5, 3));
        generalInformationFields.add(new FormField("fieldAgent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Field Agent", FormField.Required.OPTIONAL,6,2));

        FormSection generalSection = new FormSection("General Information", 1, generalInformationFields, true);
        generalSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> serviceTasksFields = new ArrayList<>();

        serviceTasksFields.add(new FormField("serviceTasks", FacilioField.FieldDisplayType.SERVICE_APPOINTMENT_TASKS, "Tasks", FormField.Required.OPTIONAL, 1, 1));
        FormSection serviceTaskSection = new FormSection("Task Details", 2, serviceTasksFields, false);

        serviceTaskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> scheduleInfoFields=new ArrayList<>();
        scheduleInfoFields.add(new FormField("scheduledStartTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start Time", FormField.Required.REQUIRED, 1, 3));
        scheduleInfoFields.add(new FormField("scheduledEndTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled End Time", FormField.Required.REQUIRED, 2, 3));
//        scheduleInfoFields.add(new FormField("resolutionDueDuration",FacilioField.FieldDisplayType.DURATION,"Resolution Due Duration", FormField.Required.OPTIONAL, 3, 3));

        FormSection scheduleInfoSection=new FormSection("Schedule Information",3,scheduleInfoFields,true);

        List<FormSection> webServiceAppointmentFormSection = new ArrayList<>();
        webServiceAppointmentFormSection.add(generalSection);
        webServiceAppointmentFormSection.add(serviceTaskSection);
        webServiceAppointmentFormSection.add(scheduleInfoSection);

        serviceAppointmentForm.setSections(webServiceAppointmentFormSection);
        serviceAppointmentForm.setIsSystemForm(true);
        serviceAppointmentForm.setType(FacilioForm.Type.FORM);

        List<FormRuleContext> formRules = new ArrayList<>();

        List<ServiceOrderTicketStatusContext> closedTypeStatus = ServiceOrderAPI.getStatusOfStatusType(ServiceOrderTicketStatusContext.StatusType.CLOSED);
        if(CollectionUtils.isNotEmpty(closedTypeStatus)) {
            List<Long> statusIds = closedTypeStatus.stream().map(ServiceOrderTicketStatusContext::getId).collect(Collectors.toList());
            formRules.add(addServiceOrderFilterRule(statusIds));
        }

        ServiceAppointmentTicketStatusContext scheduledStatus = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.SCHEDULED);
        if(scheduledStatus != null){
            formRules.add(addEditFieldDisableRule(scheduledStatus.getId()));

        }
        if(CollectionUtils.isNotEmpty(formRules)){
            serviceAppointmentForm.setDefaultFormRules(formRules);
        }

        List<FacilioForm> serviceAppointmentModuleForms = new ArrayList<>();
        serviceAppointmentModuleForms.add(serviceAppointmentForm);
        return serviceAppointmentModuleForms;
    }

    private FormRuleContext addEditFieldDisableRule(long statusId) {

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Disabling field edit based on status");
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        singleRule.setExecuteType(FormRuleContext.ExecuteType.EDIT.getIntVal());
        singleRule.setTriggerFields(new ArrayList<>());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getConditionFromList("SERVICE_APPOINTMENT.STATUS","status", Collections.singletonList(statusId), PickListOperators.ISN_T));
        singleRule.setCriteria(criteria);

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext filterAction = new FormRuleActionContext();
        filterAction.setActionType(FormActionType.DISABLE_FIELD.getVal());

        List<FormRuleActionFieldsContext> actionFields = new ArrayList<>();
        FormRuleActionFieldsContext workOrderField = new FormRuleActionFieldsContext();
        workOrderField.setFormFieldName("Work Order");
        actionFields.add(workOrderField);
        FormRuleActionFieldsContext fieldAgentField = new FormRuleActionFieldsContext();
        fieldAgentField.setFormFieldName("Field Agent");
        actionFields.add(fieldAgentField);

        filterAction.setFormRuleActionFieldsContext(actionFields);

        actions.add(filterAction);

        singleRule.setActions(actions);
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return singleRule;
    }

    private FormRuleContext addServiceOrderFilterRule(List<Long> statusIds) {

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Work Order Filter Rule");
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_FORM.getIntVal());
        singleRule.setExecuteType(FormRuleContext.ExecuteType.CREATE_AND_EDIT.getIntVal());
        singleRule.setTriggerFields(new ArrayList<>());

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext filterAction = new FormRuleActionContext();
        filterAction.setActionType(FormActionType.APPLY_FILTER.getVal());

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();

        actionField.setFormFieldName("Work Order");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getConditionFromList("ServiceOrders.STATUS_ID","status", statusIds, PickListOperators.ISN_T));

        actionField.setCriteria(criteria);

        filterAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(filterAction);

        singleRule.setActions(actions);
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return singleRule;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> serviceAppointmentViews = new ArrayList<FacilioView>();
        serviceAppointmentViews.add(getOpenServiceAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getMyAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getMyOpenAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getAllServiceAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getScheduledServiceAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getHighPriorityScheduledAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getOverdueAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getMyOverdueAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getCompletedServiceAppointmentViews().setOrder(order++));
        serviceAppointmentViews.add(getMyCompletedServiceAppointmentViews().setOrder(order++));
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

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

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

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

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

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

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

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

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

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

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

    private FacilioView getMyAppointmentViews() throws Exception{

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        Criteria fieldAgentCriteria = new Criteria();
        fieldAgentCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.IS));

        FacilioView allView = new FacilioView();
        allView.setName("myAppointments");
        allView.setDisplayName("My Appointments");
        allView.setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        allView.setCriteria(fieldAgentCriteria);
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
        roles.add(FacilioConstants.DefaultRoleNames.FIELD_AGENT);
        roles.add(FacilioConstants.DefaultRoleNames.ASSISTANT_FIELD_AGENT);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }

    private FacilioView getOverdueAppointmentViews() throws Exception{

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        Integer overDue = ServiceAppointmentContext.DueStatus.BREACHED.getIndex();

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.DISPATCHED));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.IN_PROGRESS));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.EN_ROUTE));

        Criteria dueStatus = new Criteria();
        dueStatus.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.RESOLUTION_DUE_STATUS), String.valueOf(overDue),NumberOperators.EQUALS));
        dueStatus.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.RESOLUTION_DUE_STATUS), String.valueOf(overDue),NumberOperators.EQUALS));

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

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        Integer overDue = ServiceAppointmentContext.DueStatus.BREACHED.getIndex();

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.DISPATCHED));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.IN_PROGRESS));
        criteria.addOrCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.EN_ROUTE));

        Criteria dueStatus = new Criteria();
        dueStatus.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.RESOLUTION_DUE_STATUS), String.valueOf(overDue),NumberOperators.EQUALS));
        dueStatus.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.RESOLUTION_DUE_STATUS), String.valueOf(overDue),NumberOperators.EQUALS));

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

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

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

    private FacilioView getMyCompletedServiceAppointmentViews() throws Exception{

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> saFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        Criteria criteria=new Criteria();
        criteria.addAndCondition(getServiceAppointmentConditions(FacilioConstants.ServiceAppointment.COMPLETED));
        criteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.IS));

        FacilioView allView = new FacilioView();
        allView.setName("myCompletedAppointments");
        allView.setDisplayName("My Completed Appointments");
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

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

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

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

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

    private static void addServiceAppointmentSkillModule() throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule serviceSkillModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL);
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        FacilioModule serviceAppointmentSkillModule = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_SKILL,"Appointment Skills","SERVICE_APPOINTMENT_SKILL_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField serviceSkillField = new LookupField(serviceAppointmentSkillModule,"right","Skills",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_SKILL_ID",FieldType.LOOKUP,true,false,true,false,"Skills",serviceSkillModule);
        fields.add(serviceSkillField);
        LookupField serviceAppointmentField = new LookupField(serviceAppointmentSkillModule,"left","Appointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID",FieldType.LOOKUP,true,false,true,true,"Appointments",serviceAppointmentModule);
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
                "Appointment Activity",
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

        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ATTACHMENTS);

        return new ModulePages()
                .addPage("serviceAppointment", "Default Appointment Page","", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("serviceAppointmentSummary", "Summary",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentSummaryFields", null, null)
                .addWidget("serviceAppointmentSummaryFieldsWidget", "Appointment Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentServiceTasks", "Tasks",PageTabContext.TabType.SINGLE_WIDGET_TAB,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentServiceTasks", null, null)
                .addWidget("serviceAppointmentServiceTasksWidget", "Tasks", PageWidget.WidgetType.SERVICE_APPOINTMENT_SERVICE_TASKS, "webServiceAppointmentServiceTasks_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentPlans", "Plans",PageTabContext.TabType.SINGLE_WIDGET_TAB,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentPlans", null, null)
                .addWidget("serviceAppointmentPlansWidget", "Plans", PageWidget.WidgetType.SERVICE_APPOINTMENT_PLANS, "webServiceAppointmentPlans_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentActuals", "Actuals",PageTabContext.TabType.SINGLE_WIDGET_TAB,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentActuals", null, null)
                .addWidget("serviceAppointmentActualsWidget", "Actuals", PageWidget.WidgetType.SERVICE_APPOINTMENT_ACTUALS, "webServiceAppointmentActuals_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentTimeSheet", "Time Sheet",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentTimeSheet", null, null)
                .addWidget("serviceAppointmentTimeSheetWidget", "Time Sheet", PageWidget.WidgetType.SERVICE_APPOINTMENT_TIMESHEET, "webServiceAppointmentTimeSheet_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentTrip", "Trip",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentTrip", null, null)
                .addWidget("serviceAppointmentTripWidget", "Trip", PageWidget.WidgetType.SERVICE_APPOINTMENT_TRIP, "webServiceAppointmentTrip_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentHistory", "History",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()

                .addMobileLayout()

                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summary", null, null)
                .addWidget("summaryFieldsWidget", null, PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblemobilesummaryfieldswidget_14", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT))
                .widgetDone()
                .addWidget("serviceAppointmentResourceWidget", null, PageWidget.WidgetType.RESOURCE_WIDGET, "mobileFlexibleResourceWidget_6", 0, 15, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentLocationWidget", null, PageWidget.WidgetType.LOCATION_WIDGET, "mobileFlexibleLocationWidget_9", 0, 21, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentUserWidget", null, PageWidget.WidgetType.USER_WIDGET, "mobileFlexibleUserWidget_7", 0, 30, null, null)
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
                .addWidget("serviceAppointmentServiceTaskListWidget", null, PageWidget.WidgetType.SERVICE_TASK_LIST_WIDGET, "mobileServiceTaskListWidget_16", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("plans", "Plans",PageTabContext.TabType.SIMPLE,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("plans", null, null)
                .addWidget("serviceAppointmentPlansCostWidget", "Plans Cost", PageWidget.WidgetType.PLANS_COST_WIDGET, "mobilePlansCostWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentplansWidgetGroup", "Plans Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_16", 0, 2, null, getMobilePlansWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("actuals", "Actuals",PageTabContext.TabType.SIMPLE,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("actuals", null, null)
                .addWidget("serviceAppointmentActualsCostWidget", "Actuals Cost", PageWidget.WidgetType.ACTUALS_COST, "mobileActualsCostWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentActualsWidgetGroup", "Actuals Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_16", 0, 2, null, getMobileActualsWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("timeSheet", "Time Sheet",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("timeSheet", null, null)
                .addWidget("serviceAppointmentTimeSheetOverviewWidget", "Time Sheet Overview", PageWidget.WidgetType.TIME_SHEET_OVERVIEW, "mobileTimeSheetOverviewWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentTimeSheetListWidget", "Time Sheet List", PageWidget.WidgetType.TIME_SHEET_LIST, "mobileTimeSheetListWidget_16", 0, 2, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("trip", "Trip",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("trip", null, null)
                .addWidget("serviceAppointmentTripOverviewWidget", "Trip Overview", PageWidget.WidgetType.TRIP_OVERVIEW, "mobileTripOverviewWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentTripListWidget", "Trips", PageWidget.WidgetType.TRIP_LIST, "mobileTripListWidget_16", 0, 2, null, null)
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
                .pageDone()
                .getCustomPages();


    }

    private static JSONObject getSummaryWidgetDetails(ApplicationContext app,String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioField> serviceAppointmentFields = moduleBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String, FacilioField> serviceAppointmentFieldsMap = FieldFactory.getAsMap(serviceAppointmentFields);


        FacilioField nameField = serviceAppointmentFieldsMap.get("serviceOrder");
        FacilioField descriptionField = serviceAppointmentFieldsMap.get("description");
        FacilioField category = serviceAppointmentFieldsMap.get("category");
        FacilioField priority = serviceAppointmentFieldsMap.get("priority");
        FacilioField scheduledStartTimeField = serviceAppointmentFieldsMap.get("scheduledStartTime");
        FacilioField scheduledEndTime = serviceAppointmentFieldsMap.get("scheduledEndTime");
        FacilioField estimatedDuration = serviceAppointmentFieldsMap.get("estimatedDuration");
        FacilioField actualStartTimeField = serviceAppointmentFieldsMap.get("actualStartTime");
        FacilioField actualEndTimeField = serviceAppointmentFieldsMap.get("actualEndTime");
        FacilioField actualDuration = serviceAppointmentFieldsMap.get("actualDuration");


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

        FacilioField siteField = serviceAppointmentFieldsMap.get("site");
        FacilioField locationField = serviceAppointmentFieldsMap.get("location");
        FacilioField territoryField = serviceAppointmentFieldsMap.get("territory");
        FacilioField space = serviceAppointmentFieldsMap.get("space");
        FacilioField asset = serviceAppointmentFieldsMap.get("asset");


        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, siteField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, locationField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, space, 1, 3, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, asset, 1, 4, 1);

        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, territoryField, 2, 1,1);


        // User Details

        FacilioField fieldAgentField = serviceAppointmentFieldsMap.get("fieldAgent");
        FacilioField vendor = serviceAppointmentFieldsMap.get("vendor");
        FacilioField client =serviceAppointmentFieldsMap.get("client");

        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, fieldAgentField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, vendor, 1, 2, 1);
        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, client, 1, 3, 1);

        // SLA Details

        FacilioField resolutionDueDuration = serviceAppointmentFieldsMap.get("resolutionDueDuration");
        FacilioField resolutionDueTimeField = serviceAppointmentFieldsMap.get("resolutionDueTime");
        FacilioField resolutionDueStatus=serviceAppointmentFieldsMap.get("resolutionDueStatus");

        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, resolutionDueDuration, 1, 1, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, resolutionDueTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup,resolutionDueStatus,1,3,1);

        // System Details

        FacilioField sysCreatedByField = serviceAppointmentFieldsMap.get("sysCreatedBy");
        FacilioField sysCreatedTimeField = serviceAppointmentFieldsMap.get("sysCreatedTime");
        FacilioField sysModifiedByField = serviceAppointmentFieldsMap.get("sysModifiedBy");
        FacilioField sysModifiedTimeField = serviceAppointmentFieldsMap.get("sysModifiedTime");

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

    private static JSONObject getSummaryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getMobilePlansWidgetGroup() throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("plannedItems", "Items", "")
                .addWidget("plannedItemsListWidget", "Items", PageWidget.WidgetType.PLANNED_ITEMS_LIST,"flexiblemobileplanneditems_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("plannedTools", "Tools", "")
                .addWidget("plannedTollsListWidget", "Tools", PageWidget.WidgetType.PLANNED_TOOLS_LIST,"flexiblemobileplannedtools_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("plannedServices", "Services", "")
                .addWidget("plannedServicesListWidget", "Services", PageWidget.WidgetType.PLANNED_SERVICES_LIST,"flexiblemobileplannedservices_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getMobileActualsWidgetGroup() throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("actualItems", "Items", "")
                .addWidget("actualItemsListWidget", "Items", PageWidget.WidgetType.ACTUAL_ITEMS_LIST,"flexiblemobileactualitems_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("actualTools", "Tools", "")
                .addWidget("actualTollsListWidget", "Tools", PageWidget.WidgetType.ACTUAL_TOOLS_LIST,"flexiblemobileactualtools_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("actualServices", "Services", "")
                .addWidget("actualServicesListWidget", "Services", PageWidget.WidgetType.ACTUAL_SERVICES_LIST,"flexiblemobileactualservices_16", 0, 0, null, null)
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
            endTrip.setPermission("EXECUTE");
            endTrip.setPermissionRequired(true);
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
            startWork.setPermission("EXECUTE");
            startWork.setPermissionRequired(true);
            Criteria startWorkCriteria = new Criteria();
            List<Long> allowedStatusIds = new ArrayList<>();
            if(enRouteStatus != null) {
                allowedStatusIds.add(enRouteStatus.getId());
            }
            allowedStatusIds.add(dispatchedStatus.getId());
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),allowedStatusIds, PickListOperators.IS));
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), CommonOperators.IS_NOT_EMPTY));
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            startWork.setCriteria(startWorkCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,startWork);
        }

        ServiceAppointmentTicketStatusContext inProgressStatus = statusMap.get(FacilioConstants.ServiceAppointment.IN_PROGRESS);
        if(inProgressStatus != null){
            SystemButtonRuleContext complete = new SystemButtonRuleContext();
            complete.setName("Complete Work");
            complete.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
            complete.setIdentifier(FacilioConstants.ServiceAppointment.COMPLETE);
            complete.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            complete.setPermission("EXECUTE");
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

        if(dispatchedStatus != null) {
            SystemButtonRuleContext startTrip = new SystemButtonRuleContext();
            startTrip.setName("Start Trip");
            startTrip.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
            startTrip.setIdentifier(FacilioConstants.ServiceAppointment.START_TRIP);
            startTrip.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            startTrip.setPermission("EXECUTE");
            startTrip.setPermissionRequired(true);
            Criteria startTripCriteria = new Criteria();
            List<Long> statusIds = new ArrayList<>();
            statusIds.add(dispatchedStatus.getId());
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS), statusIds, PickListOperators.IS));
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.FIELD_AGENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE, PickListOperators.IS));
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
            startTripCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
            startTripCriteria.andCriteria(getTrackGeoLocationCriteria());
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
        SystemButtonRuleContext redispatch = new SystemButtonRuleContext();
        redispatch.setName("Redispatch");
        redispatch.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        redispatch.setIdentifier(FacilioConstants.ServiceAppointment.REDISPATCH);
        redispatch.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        redispatch.setPermission("DISPATCH");
        redispatch.setPermissionRequired(true);
        List<Long> statusIds = new ArrayList<>();
        if(dispatchedStatus != null) {
            statusIds.add(dispatchedStatus.getId());
        }
        if(enRouteStatus != null) {
            statusIds.add(enRouteStatus.getId());
        }
        Criteria redispatchCriteria = new Criteria();
        redispatchCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),statusIds, PickListOperators.IS));
        redispatchCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.ACTUAL_START_TIME), CommonOperators.IS_EMPTY));
        redispatch.setCriteria(redispatchCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,redispatch);

        SystemButtonRuleContext reschedule = new SystemButtonRuleContext();
        reschedule.setName("Reschedule");
        reschedule.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        reschedule.setIdentifier(FacilioConstants.ServiceAppointment.RESCHEDULE);
        reschedule.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        reschedule.setPermission("DISPATCH");
        reschedule.setPermissionRequired(true);
        List<Long> statusIdList = new ArrayList<>();
        if(scheduledStatus != null) {
            statusIdList.add(scheduledStatus.getId());
        }
        if(dispatchedStatus != null) {
            statusIdList.add(dispatchedStatus.getId());
        }
        if(inProgressStatus != null) {
            statusIdList.add(inProgressStatus.getId());
        }
        if(enRouteStatus != null) {
            statusIdList.add(enRouteStatus.getId());
        }
        Criteria rescheduleCriteria = new Criteria();
        rescheduleCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ContextNames.STATUS),statusIdList, PickListOperators.IS));
        rescheduleCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), CommonOperators.IS_NOT_EMPTY));
        rescheduleCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), CommonOperators.IS_NOT_EMPTY));
        reschedule.setCriteria(rescheduleCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,reschedule);

        SystemButtonRuleContext create = new SystemButtonRuleContext();
        create.setName("Create Appointment");
        create.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        create.setIdentifier(FacilioConstants.ContextNames.CREATE);
        create.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        create.setPermission("CREATE");
        create.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,create);

        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit_list");
        edit.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        edit.setPermission("UPDATE");
        edit.setPermissionRequired(true);
        edit.setCriteria(getEditCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,edit);

        SystemButtonRuleContext summaryEditButton = new SystemButtonRuleContext();
        summaryEditButton.setName("Edit");
        summaryEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        summaryEditButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        summaryEditButton.setIdentifier("edit_summary");
        summaryEditButton.setPermissionRequired(true);
        summaryEditButton.setPermission("UPDATE");
        summaryEditButton.setCriteria(getEditCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,summaryEditButton);

        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        listDeleteButton.setCriteria(getDeleteCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,listDeleteButton);


        SystemButtonRuleContext bulkDeleteButton = new SystemButtonRuleContext();
        bulkDeleteButton.setName("Delete");
        bulkDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        bulkDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkDeleteButton.setIdentifier("delete_bulk");
        bulkDeleteButton.setPermissionRequired(true);
        bulkDeleteButton.setPermission("DELETE");
        bulkDeleteButton.setCriteria(getDeleteCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,bulkDeleteButton);

        SystemButtonApi.addExportAsCSV(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        

    }

    public static void addServiceTaskSystemButtons() throws Exception {
        SystemButtonRuleContext startWork = new SystemButtonRuleContext();
        startWork.setName("Start Task");
        startWork.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        startWork.setIdentifier(FacilioConstants.ServiceAppointment.START_WORK);
        startWork.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        startWork.setPermission("EXECUTE");
        startWork.setPermissionRequired(true);
        Criteria startWorkCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.DISPATCHED);
        if(startWorkCriteria!=null){
            startWork.setCriteria(startWorkCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,startWork);

        SystemButtonRuleContext pause = new SystemButtonRuleContext();
        pause.setName("Pause Task");
        pause.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        pause.setIdentifier(FacilioConstants.ServiceAppointment.PAUSE);
        pause.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        pause.setPermission("EXECUTE");
        pause.setPermissionRequired(true);
        Criteria pauseCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS);
        if(pauseCriteria!=null) {
            pause.setCriteria(pauseCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,pause);

        SystemButtonRuleContext resume = new SystemButtonRuleContext();
        resume.setName("Resume Task");
        resume.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        resume.setIdentifier(FacilioConstants.ServiceAppointment.RESUME);
        resume.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        resume.setPermission("EXECUTE");
        resume.setPermissionRequired(true);
        Criteria resumeCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.ON_HOLD);
        if(resumeCriteria!=null){
            resume.setCriteria(resumeCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,resume);

        SystemButtonRuleContext complete = new SystemButtonRuleContext();
        complete.setName("Complete Task");
        complete.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        complete.setIdentifier(FacilioConstants.ServiceAppointment.COMPLETE);
        complete.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        complete.setPermission("EXECUTE");
        complete.setPermissionRequired(true);
        Criteria completeCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS);
        if(completeCriteria!=null){
            complete.setCriteria(completeCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,complete);

        //system buttons for summary
        SystemButtonRuleContext startWorkButton = new SystemButtonRuleContext();
        startWorkButton.setName("Start Task");
        startWorkButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        startWorkButton.setIdentifier(FacilioConstants.ServiceAppointment.START_WORK);
        startWorkButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        startWorkButton.setPermission("EXECUTE");
        startWorkButton.setPermissionRequired(true);
        Criteria dispatchCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.DISPATCHED);
        if(dispatchCriteria!=null){
            startWorkButton.setCriteria(dispatchCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,startWorkButton);

        SystemButtonRuleContext pauseButton = new SystemButtonRuleContext();
        pauseButton.setName("Pause Task");
        pauseButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        pauseButton.setIdentifier(FacilioConstants.ServiceAppointment.PAUSE);
        pauseButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        pauseButton.setPermission("EXECUTE");
        pauseButton.setPermissionRequired(true);
        Criteria inprogressCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS);
        if(inprogressCriteria!=null) {
            pauseButton.setCriteria(inprogressCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,pauseButton);

        SystemButtonRuleContext resumeButton = new SystemButtonRuleContext();
        resumeButton.setName("Resume Task");
        resumeButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        resumeButton.setIdentifier(FacilioConstants.ServiceAppointment.RESUME);
        resumeButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        resumeButton.setPermission("EXECUTE");
        resumeButton.setPermissionRequired(true);
        Criteria onHoldCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.ON_HOLD);
        if(onHoldCriteria!=null){
            resumeButton.setCriteria(onHoldCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,resumeButton);

        SystemButtonRuleContext completeButton = new SystemButtonRuleContext();
        completeButton.setName("Complete Task");
        completeButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        completeButton.setIdentifier(FacilioConstants.ServiceAppointment.COMPLETE);
        completeButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        completeButton.setPermission("EXECUTE");
        completeButton.setPermissionRequired(true);
        Criteria criteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS);
        if(criteria!=null){
            completeButton.setCriteria(criteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,completeButton);
    }
    private static Criteria getCriteria(String status) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> stFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        Map<String,FacilioField> stFieldMap = FieldFactory.getAsMap(stFields);
        ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(status);
        if(taskStatus!=null){
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(stFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(taskStatus.getId()), PickListOperators.IS));
            return criteria;
        }
        return null;
    }

    public static Criteria getTrackGeoLocationCriteria() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        LookupField trackGeoLocation = new LookupField();
        trackGeoLocation.setName("trackGeoLocation");
        trackGeoLocation.setColumnName("TRACK_GEO_LOCATION");
        trackGeoLocation.setDataType(FieldType.BOOLEAN);
        trackGeoLocation.setModule(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE));

        Condition trackLocationCondition = new Condition();
        trackLocationCondition.setField(trackGeoLocation);
        trackLocationCondition.setOperator(BooleanOperators.IS);
        trackLocationCondition.setValue(String.valueOf(true));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(trackLocationCondition);

        LookupField fieldAgent = new LookupField();
        fieldAgent.setName("fieldAgent");
        fieldAgent.setColumnName("PEOPLE_ID");
        fieldAgent.setDataType(FieldType.LOOKUP);
        fieldAgent.setModule(moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
        fieldAgent.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE));

        Condition fieldAgentCondition = new Condition();
        fieldAgentCondition.setField(fieldAgent);
        fieldAgentCondition.setOperator(LookupOperator.LOOKUP);
        fieldAgentCondition.setCriteriaValue(criteria);

        Criteria fieldAgentCriteria = new Criteria();
        criteria.addAndCondition(fieldAgentCondition);

        return fieldAgentCriteria;
    }
    public void addSLA() throws Exception{
        SLAEntityContext sla = new SLAEntityContext();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        List<FacilioField> saFields = moduleBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        FacilioField scheduledStartTime = saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME);
        FacilioField resolutionDueTime = saFieldMap.get("resolutionDueTime");

        Criteria slaCriteria = new Criteria();
        slaCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get("actualEndTime"), String.valueOf(true), CommonOperators.IS_EMPTY));
        long criteriaId = CriteriaAPI.addCriteria(slaCriteria);
        if(criteriaId >0) {

            sla.setName("Resolution Due Date");
            sla.setDescription("Resolution due date");
            sla.setModuleId(module.getModuleId());
            sla.setBaseFieldId(scheduledStartTime.getId());
            sla.setDueFieldId(resolutionDueTime.getId());
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
        status.setModule(moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
        status.setLookupModule(moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS));

        LookupField recordLocked = new LookupField();
        recordLocked.setName("recordLocked");
        recordLocked.setColumnName("RECORD_LOCKED");
        recordLocked.setDataType(FieldType.BOOLEAN);
        recordLocked.setModule(moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS));

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

    public static Criteria getDeleteCriteria() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        LookupField status = new LookupField();
        status.setName("status");
        status.setColumnName("STATUS");
        status.setDataType(FieldType.LOOKUP);
        status.setModule(moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
        status.setLookupModule(moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS));

        LookupField recordLocked = new LookupField();
        recordLocked.setName("deleteLocked");
        recordLocked.setColumnName("DELETE_LOCKED");
        recordLocked.setDataType(FieldType.BOOLEAN);
        recordLocked.setModule(moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS));

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
