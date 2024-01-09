package com.facilio.fsm.signup;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.fsm.context.ServicePMTemplateContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.bmsconsole.util.SystemButtonApi.addSystemButton;
import static com.facilio.util.SummaryWidgetUtil.addSummaryFieldInWidgetGroup;

public class ServicePlannedMaintenanceModule extends BaseModuleConfig {
    public static List<String> servicePMSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP);

    public ServicePlannedMaintenanceModule(){
        setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
    }
    @Override
    public void addData() throws Exception {
        FacilioModule servicePlannedMaintenanceModule = constructServicePlannedMaintenanceModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(servicePlannedMaintenanceModule));
        addModuleChain.execute();

        addPlannedMaintenanceLookupField();
        addNightlyJob();
        addActivityModuleForServicePM();
        SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE));
        addSystemButtons();
    }
    private FacilioModule constructServicePlannedMaintenanceModule() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE, "Planned Maintenance", "Service_Planned_Maintenance", FacilioModule.ModuleType.BASE_ENTITY,bean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER),true);
        module.setDescription("Plan and execute scheduled maintenance for field service maintenance.");
        module.setHideFromParents(true);
        List<FacilioField> fields = new ArrayList<>();
        FacilioField name = FieldFactory.getDefaultField("pmName","Name","NAME", FieldType.STRING,true);
        name.setRequired(true);
        fields.add(name);
        fields.add(FieldFactory.getDefaultField("pmDescription","Description","DESCRIPTION",FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));

        SystemEnumField pmType = FieldFactory.getDefaultField("pmType","PM Type","PM_TYPE",FieldType.SYSTEM_ENUM);
        pmType.setEnumName("PMType");
        fields.add(pmType);
        SystemEnumField triggerType = FieldFactory.getDefaultField("triggerType","Trigger Type","TRIGGER_TYPE",FieldType.SYSTEM_ENUM);
        triggerType.setEnumName("TriggerType");
        fields.add(triggerType);
        fields.add(FieldFactory.getDefaultField("isPublished","Published","IS_PUBLISHED", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("leadTime","Lead Time (Days)","LEAD_TIME",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("counter","Counter","COUNTER",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("previewPeriod","Forecast Period (Days)","PREVIEW_PERIOD",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("nextRun","Next Run","NEXT_RUN",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));
        fields.add(FieldFactory.getDefaultField("lastRun","Last Run","LAST_RUN",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));
        fields.add(FieldFactory.getDefaultField("estimatedDuration", "Estimated Duration", "ESTIMATED_DURATION", FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        module.setFields(fields);
        return module;
    }
    private void addPlannedMaintenanceLookupField()throws Exception{
        ModuleBean bean = Constants.getModBean();
        FacilioModule servicePMTriggerModule = bean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER);
        FacilioModule serviceOrderModule = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioModule servicePMModule = bean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        bean.addSubModule(serviceOrderModule.getModuleId(), servicePMModule.getModuleId(),0);
        bean.addSubModule(servicePMTriggerModule.getModuleId(), servicePMModule.getModuleId(),0);

        LookupField servicePlannedMaintenance = FieldFactory.getDefaultField("servicePlannedMaintenance","Planned Maintenance","SERVICE_PM", FieldType.LOOKUP);
        servicePlannedMaintenance.setLookupModule(Objects.requireNonNull(servicePMModule,"Service PM module doesn't exist."));

        // adding in service pm trigger
        servicePlannedMaintenance.setModule(servicePMTriggerModule);
        bean.addField(servicePlannedMaintenance);
        // adding in service order
        servicePlannedMaintenance.setModule(serviceOrderModule);
        bean.addField(servicePlannedMaintenance);
    }
    public void addNightlyJob() throws Exception {
        ScheduleInfo si = new ScheduleInfo();
        si.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        si.setTimes(Collections.singletonList("00:01"));

        FacilioTimer.scheduleCalendarJob(AccountUtil.getCurrentOrg().getId(), "ServicePMNightlyScheduler", DateTimeUtil.getCurrenTime(), si, "facilio");
    }
    public void addActivityModuleForServicePM() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePlannedMaintenance = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        FacilioModule module = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_ACTIVITY, "PM Activity", "Service_PM_Activity", FacilioModule.ModuleType.ACTIVITY);

        List<FacilioField> fields = new ArrayList<>();

        fields.add(FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING));
        LookupField doneBy = FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);

        module.setFields(fields);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
        modBean.addSubModule(servicePlannedMaintenance.getModuleId(), module.getModuleId());
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePMModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);

        FacilioForm servicePMForm = new FacilioForm();
        servicePMForm.setDisplayName("Standard");
        servicePMForm.setName("default_servicePlannedMaintenance_web");
        servicePMForm.setModule(servicePMModule);
        servicePMForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        servicePMForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> servicePMSectionFields = new ArrayList<>();
        servicePMSectionFields.add(new FormField("pmName", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        servicePMSectionFields.add(new FormField("pmDescription", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        servicePMSectionFields.add(new FormField("pmType", FacilioField.FieldDisplayType.SELECTBOX, "PM Type", FormField.Required.REQUIRED, 3, 2));
        servicePMSectionFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, FacilioConstants.ContextNames.SITE, 4, 2));
        FormField spaceField = new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Space", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.SPACE, 5, 1);
        spaceField.setHideField(true);
        servicePMSectionFields.add(spaceField);
        FormField assetField = new FormField("asset", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.ASSET, 6, 1);
        assetField.setHideField(true);
        servicePMSectionFields.add(assetField);
        servicePMSectionFields.add(new FormField("servicePlan", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Job Plan", FormField.Required.OPTIONAL, FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN, 7, 2));
        servicePMSectionFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.CLIENT, 8, 2));
        servicePMSectionFields.add(new FormField("resolutionDueDuration", FacilioField.FieldDisplayType.DURATION, "Resolution Due Duration", FormField.Required.OPTIONAL, 9, 2));
        servicePMSectionFields.add(new FormField("estimatedDuration", FacilioField.FieldDisplayType.DURATION, "Estimated Duration", FormField.Required.REQUIRED, 10, 2));
        servicePMSectionFields.add(new FormField("leadTime", FacilioField.FieldDisplayType.NUMBER, "Lead Time (Days)", FormField.Required.OPTIONAL, 11, 2));
        servicePMSectionFields.add(new FormField("previewPeriod", FacilioField.FieldDisplayType.NUMBER, "Forecast Period (Days)", FormField.Required.OPTIONAL, 12, 2));

        FormSection servicePMSection = new FormSection("PM Information", 1, servicePMSectionFields, true);
        servicePMSection.setSectionType(FormSection.SectionType.FIELDS);


        FormField servicePMTriggerField= new FormField("servicePMTrigger", FacilioField.FieldDisplayType.SERVICE_PM_TRIGGER, "PM Trigger", FormField.Required.REQUIRED, 2, 1);

        FormSection triggerSection = new FormSection("Recurring Schedule Information", 2, Collections.singletonList(servicePMTriggerField), true);
        triggerSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> serviceOrderSectionFields = new ArrayList<>();
        serviceOrderSectionFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        serviceOrderSectionFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Work Order Description", FormField.Required.OPTIONAL, 2, 1));
        serviceOrderSectionFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.REQUIRED, 3, 2));
        serviceOrderSectionFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.REQUIRED, 4, 2));
        serviceOrderSectionFields.add(new FormField("fieldAgent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Field Agent", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.PEOPLE, 5, 2));
        serviceOrderSectionFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.VENDORS, 6, 2));
        serviceOrderSectionFields.add(new FormField("autoCreateSa", FacilioField.FieldDisplayType.DECISION_BOX, "Auto Create Appointment", FormField.Required.OPTIONAL, 7, 2));

        FormSection serviceOrderSection = new FormSection("Work Order Information", 3, serviceOrderSectionFields, true);
        serviceOrderSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> servicePMSections = new ArrayList<>();
        servicePMSections.add(servicePMSection);
        servicePMSections.add(triggerSection);
        servicePMSections.add(serviceOrderSection);

        servicePMForm.setSections(servicePMSections);
        servicePMForm.setIsSystemForm(true);
        servicePMForm.setType(FacilioForm.Type.FORM);

        FormRuleContext assetFieldRule = addShowAssetFieldRule();
        FormRuleContext spaceFieldRule = addShowSpaceFieldRule();
        FormRuleContext addHideAssetFieldRule = addHideAssetFieldRule();
        FormRuleContext addHideSpaceFieldRule = addHideSpaceFieldRule();
        List<FormRuleContext> rules = new ArrayList<>();
        rules.add(assetFieldRule);
        rules.add(spaceFieldRule);
        rules.add(addHideAssetFieldRule);
        rules.add(addHideSpaceFieldRule);
        servicePMForm.setDefaultFormRules(rules);
        return Collections.singletonList(servicePMForm);
    }

    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, getSystemPage(app, module, false, true));
        }
        return appNameVsPage;
    }
    private static List<PagesContext> getSystemPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_ACTIVITY);
        return new ModulePages()
                .addPage("servicePlannedMaintenance", "Default PM Page", "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryFields", null, null)
                .addWidget("summaryFieldsWidget", "PM Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null,  getSummaryWidgetDetails(app,FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("servicePlan", "Job Plan", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicePlanTasks", null, null)
                .addWidget("servicePlanTasks", "Tasks", PageWidget.WidgetType.SERVICE_PLAN_TASKS, "webServicePlanTasks_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("servicePlanInventory", null, null)
                .addWidget("servicePlanInventory", "Inventory", PageWidget.WidgetType.SERVICE_PLAN_INVENTORY, "webServicePlanInventory_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("serviceOrders", "Work Orders", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceOrders", null, null)
                .addWidget("serviceOrders", "Work Orders", PageWidget.WidgetType.SERVICE_PM_SERVICE_ORDERS, "webPMServiceOrders_10_12", 0, 0, null, null)
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
                .pageDone().getCustomPages();
    }
    private static JSONObject getSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> servicePMFields = modBean.getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        Map<String, FacilioField> servicePMFieldsMap = FieldFactory.getAsMap(servicePMFields);
        List<FacilioField> servicePMTriggerFields = modBean.getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER);
        Map<String, FacilioField> servicePMTriggerFieldsMap = FieldFactory.getAsMap(servicePMTriggerFields);
        FacilioField descriptionField = servicePMFieldsMap.get("pmDescription");
        FacilioField pmTypeField = servicePMFieldsMap.get("pmType");
        FacilioField siteField = servicePMFieldsMap.get("site");
        FacilioField assetField = servicePMFieldsMap.get("asset");
        FacilioField spaceField = servicePMFieldsMap.get("space");
        FacilioField clientField = servicePMFieldsMap.get("client");
        FacilioField counterField = servicePMFieldsMap.get("counter");
        FacilioField resolutionDueDurationField = servicePMFieldsMap.get("resolutionDueDuration");
        FacilioField servicePlanField = servicePMFieldsMap.get("servicePlan");
        FacilioField estimatedDurationField = servicePMFieldsMap.get("estimatedDuration");
        FacilioField leadTimeField = servicePMFieldsMap.get("leadTime");
        FacilioField previewPeriodField = servicePMFieldsMap.get("previewPeriod");
        FacilioField servicePMTriggerField = servicePMFieldsMap.get("servicePMTrigger");
        FacilioField nextRunField = servicePMFieldsMap.get("nextRun");
        FacilioField lastRunField = servicePMFieldsMap.get("lastRun");
        FacilioField triggerName = servicePMTriggerFieldsMap.get("name");
        FacilioField triggerFrequency = servicePMTriggerFieldsMap.get("frequency");
        FacilioField triggerStartTime = servicePMTriggerFieldsMap.get("startTime");
        FacilioField triggerEndTime = servicePMTriggerFieldsMap.get("endTime");
        FacilioField soName = servicePMFieldsMap.get("name");
        FacilioField soDescription = servicePMFieldsMap.get("description");
        FacilioField soCategory = servicePMFieldsMap.get("category");
        FacilioField soPriority = servicePMFieldsMap.get("priority");
        FacilioField soFieldAgent = servicePMFieldsMap.get("fieldAgent");
        FacilioField soVendor = servicePMFieldsMap.get("vendor");
        FacilioField soAutoCreateSa = servicePMFieldsMap.get("autoCreateSa");
        FacilioField servicePMTemplate = servicePMFieldsMap.get("servicePMTemplate");

        SummaryWidget pageWidget = new SummaryWidget();

        // Group 1
        SummaryWidgetGroup servicePMInfoWidgetGroup = new SummaryWidgetGroup();
        servicePMInfoWidgetGroup.setName("servicePMInformation");
        servicePMInfoWidgetGroup.setDisplayName("PM Information");
        servicePMInfoWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, descriptionField, 1, 1, 4);

        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, pmTypeField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, siteField, 2, 2, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, assetField, 2, 3, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, spaceField, 2, 4, 1);

        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, clientField, 3, 1, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, servicePlanField, 3, 2, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, estimatedDurationField, 3, 3, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, resolutionDueDurationField, 3, 4, 1);

        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, leadTimeField, 4, 1, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, previewPeriodField, 4, 2, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, lastRunField, 4, 3, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, nextRunField, 4, 4, 1);

        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, servicePMTemplate, 5, 1, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, counterField, 5, 2, 1);

        // Group 2
        SummaryWidgetGroup scheduleInfoWidgetGroup = new SummaryWidgetGroup();
        scheduleInfoWidgetGroup.setName("recurringScheduleInformation");
        scheduleInfoWidgetGroup.setDisplayName("Recurring Schedule Information");
        scheduleInfoWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(scheduleInfoWidgetGroup,"Schedule Info", triggerName, 1, 1, 4,servicePMTriggerField);
        addSummaryFieldInWidgetGroup(scheduleInfoWidgetGroup,triggerFrequency.getDisplayName(), triggerFrequency, 2, 1, 1,servicePMTriggerField);
        addSummaryFieldInWidgetGroup(scheduleInfoWidgetGroup,triggerStartTime.getDisplayName(), triggerStartTime, 2, 2, 1,servicePMTriggerField);
        addSummaryFieldInWidgetGroup(scheduleInfoWidgetGroup,triggerEndTime.getDisplayName(), triggerEndTime, 2, 3, 1,servicePMTriggerField);

        SummaryWidgetGroup serviceOrderWidgetGroup = new SummaryWidgetGroup();
        serviceOrderWidgetGroup.setName("serviceOrderInformation");
        serviceOrderWidgetGroup.setDisplayName("Work Order Information");
        serviceOrderWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,"Subject", soName, 1, 1, 4);
        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soDescription.getDisplayName(), soDescription, 2, 1, 4);
        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soCategory.getDisplayName(), soCategory, 3, 1, 1);
        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soPriority.getDisplayName(), soPriority, 3, 2, 1);
        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soFieldAgent.getDisplayName(), soFieldAgent, 3, 3, 1);
        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soVendor.getDisplayName(), soVendor, 3, 4, 1);
        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soAutoCreateSa.getDisplayName(), soAutoCreateSa, 4, 1, 1);


        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(servicePMInfoWidgetGroup);
        widgetGroupList.add(scheduleInfoWidgetGroup);
        widgetGroupList.add(serviceOrderWidgetGroup);

        pageWidget.setName("servicePMDetails");
        pageWidget.setDisplayName("PM Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE_ATTACHMENTS);

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

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> servicePMViews = new ArrayList<FacilioView>();
        servicePMViews.add(getAllServicePMViews().setOrder(order++));
        servicePMViews.add(getPublishedServicePMViews().setOrder(order++));
        servicePMViews.add(getUnPublishedServicePMViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "default");
        groupDetails.put("displayName", "Default");
        groupDetails.put("moduleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        groupDetails.put("views", servicePMViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private FacilioView getAllServicePMViews() throws Exception{
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All PMs");
        allView.setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(ServicePlannedMaintenanceModule.servicePMSupportedApps);


        List<ViewField> servicePMViewFields = new ArrayList<>();

        servicePMViewFields.add(new ViewField("pmName","Name"));
        servicePMViewFields.add(new ViewField("pmDescription","Description"));
        servicePMViewFields.add(new ViewField("pmType","PM Type"));
        servicePMViewFields.add(new ViewField("servicePMTrigger","Recurring Schedule"));
        servicePMViewFields.add(new ViewField("isPublished","Published"));
        servicePMViewFields.add(new ViewField("leadTime","Lead Time"));
        servicePMViewFields.add(new ViewField("previewPeriod","Forecast Period (Days)"));
        servicePMViewFields.add(new ViewField("nextRun","Next Run"));
        servicePMViewFields.add(new ViewField("lastRun","Last Run"));
        servicePMViewFields.add(new ViewField("servicePMTemplate","Master PM"));


        allView.setFields(servicePMViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }

    private FacilioView getPublishedServicePMViews() throws Exception{
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("IS_PUBLISHED","isPublished",String.valueOf(true), BooleanOperators.IS));

        FacilioView publishedView = new FacilioView();
        publishedView.setName("publishedPMs");
        publishedView.setDisplayName("Published PMs");
        publishedView.setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        publishedView.setSortFields(sortFields);
        publishedView.setCriteria(criteria);
        publishedView.setAppLinkNames(ServicePlannedMaintenanceModule.servicePMSupportedApps);


        List<ViewField> servicePMViewFields = new ArrayList<>();

        servicePMViewFields.add(new ViewField("pmName","Name"));
        servicePMViewFields.add(new ViewField("pmDescription","Description"));
        servicePMViewFields.add(new ViewField("pmType","PM Type"));
        servicePMViewFields.add(new ViewField("servicePMTrigger","Recurring Schedule"));
        servicePMViewFields.add(new ViewField("isPublished","Published"));
        servicePMViewFields.add(new ViewField("leadTime","Lead Time"));
        servicePMViewFields.add(new ViewField("previewPeriod","Forecast Period (Days)"));
        servicePMViewFields.add(new ViewField("nextRun","Next Run"));
        servicePMViewFields.add(new ViewField("lastRun","Last Run"));
        servicePMViewFields.add(new ViewField("servicePMTemplate","Master PM"));

        publishedView.setFields(servicePMViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        publishedView.setViewSharing(getSharingContext(roles));

        return publishedView;
    }

    private FacilioView getUnPublishedServicePMViews() throws Exception{
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("IS_PUBLISHED","isPublished",String.valueOf(false), BooleanOperators.IS));

        FacilioView unPublishedView = new FacilioView();
        unPublishedView.setName("unPublishedPMs");
        unPublishedView.setDisplayName("Unpublished PMs");
        unPublishedView.setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        unPublishedView.setSortFields(sortFields);
        unPublishedView.setCriteria(criteria);
        unPublishedView.setAppLinkNames(ServicePlannedMaintenanceModule.servicePMSupportedApps);


        List<ViewField> servicePMViewFields = new ArrayList<>();

        servicePMViewFields.add(new ViewField("pmName","Name"));
        servicePMViewFields.add(new ViewField("pmDescription","Description"));
        servicePMViewFields.add(new ViewField("pmType","PM Type"));
        servicePMViewFields.add(new ViewField("servicePMTrigger","Recurring Schedule"));
        servicePMViewFields.add(new ViewField("isPublished","Published"));
        servicePMViewFields.add(new ViewField("leadTime","Lead Time"));
        servicePMViewFields.add(new ViewField("previewPeriod","Forecast Period (Days)"));
        servicePMViewFields.add(new ViewField("nextRun","Next Run"));
        servicePMViewFields.add(new ViewField("lastRun","Last Run"));
        servicePMViewFields.add(new ViewField("servicePMTemplate","Master PM"));

        unPublishedView.setFields(servicePMViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        unPublishedView.setViewSharing(getSharingContext(roles));

        return unPublishedView;
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
    private static void addSystemButtons() throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("IS_PUBLISHED","isPublished",String.valueOf(false), BooleanOperators.IS));

        SystemButtonRuleContext publish = new SystemButtonRuleContext();
        publish.setName("Publish");
        publish.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        publish.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        publish.setIdentifier("publish");
        publish.setCriteria(criteria);
        addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE, publish);

        SystemButtonRuleContext create = new SystemButtonRuleContext();
        create.setName("Create PM");
        create.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        create.setIdentifier(FacilioConstants.ContextNames.CREATE);
        create.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        create.setPermission("CREATE");
        create.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,create);

        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit_list");
        edit.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        edit.setPermission("UPDATE");
        edit.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,edit);

        SystemButtonRuleContext summaryEditButton = new SystemButtonRuleContext();
        summaryEditButton.setName("Edit");
        summaryEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        summaryEditButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        summaryEditButton.setIdentifier("edit_summary");
        summaryEditButton.setPermissionRequired(true);
        summaryEditButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,summaryEditButton);

        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,listDeleteButton);


        SystemButtonRuleContext bulkDeleteButton = new SystemButtonRuleContext();
        bulkDeleteButton.setName("Delete");
        bulkDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        bulkDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkDeleteButton.setIdentifier("delete_bulk");
        bulkDeleteButton.setPermissionRequired(true);
        bulkDeleteButton.setPermission("DELETE");
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,bulkDeleteButton);

        SystemButtonApi.addExportAsCSV(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
    }
    private FormRuleContext addShowSpaceFieldRule(){

        FormRuleContext showRule = new FormRuleContext();
        showRule.setName("Space Show Rule");
        showRule.setDescription("Rule To Show Space field.");
        showRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        showRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        showRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("PM_TYPE","pmType",String.valueOf(ServicePMTemplateContext.MasterPMType.SPACE.getIndex()), EnumOperators.IS));
        showRule.setCriteria(criteria);

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("PM Type");
        showRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
        actionField.setFormFieldName("Space");

        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.SHOW_FIELD.getVal());

        showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        FormRuleActionContext mandatoryAction = new FormRuleActionContext();
        mandatoryAction.setActionType(FormActionType.SET_MANDATORY.getVal());

        mandatoryAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
        actions.add(showAction);
        actions.add(mandatoryAction);
        showRule.setActions(actions);
        showRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return showRule;
    }
    private FormRuleContext addShowAssetFieldRule(){

        FormRuleContext showRule = new FormRuleContext();
        showRule.setName("Asset Show Rule");
        showRule.setDescription("Rule To Show Asset field.");
        showRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        showRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        showRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("PM_TYPE","pmType",String.valueOf(ServicePMTemplateContext.MasterPMType.ASSET.getIndex()), EnumOperators.IS));
        showRule.setCriteria(criteria);

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("PM Type");
        showRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
        actionField.setFormFieldName("Asset");

        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.SHOW_FIELD.getVal());
        showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        FormRuleActionContext mandatoryAction = new FormRuleActionContext();
        mandatoryAction.setActionType(FormActionType.SET_MANDATORY.getVal());
        mandatoryAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(showAction);
        actions.add(mandatoryAction);
        showRule.setActions(actions);
        showRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return showRule;
    }
    private FormRuleContext addHideAssetFieldRule(){

        FormRuleContext showRule = new FormRuleContext();
        showRule.setName("Asset Hide Rule");
        showRule.setDescription("Rule To Hide Asset field.");
        showRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        showRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        showRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("PM_TYPE","pmType",String.valueOf(ServicePMTemplateContext.MasterPMType.ASSET.getIndex()), EnumOperators.ISN_T));
        showRule.setCriteria(criteria);

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("PM Type");
        showRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();

        actionField.setFormFieldName("Asset");

        FormRuleActionContext hideAction = new FormRuleActionContext();
        hideAction.setActionType(FormActionType.HIDE_FIELD.getVal());
        hideAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        FormRuleActionContext mandatoryAction = new FormRuleActionContext();
        mandatoryAction.setActionType(FormActionType.REMOVE_MANDATORY.getVal());
        mandatoryAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(hideAction);
        actions.add(mandatoryAction);
        showRule.setActions(actions);
        showRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return showRule;
    }
    private FormRuleContext addHideSpaceFieldRule(){

        FormRuleContext showRule = new FormRuleContext();
        showRule.setName("Space Hide Rule");
        showRule.setDescription("Rule To Hide Space field.");
        showRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        showRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        showRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("PM_TYPE","pmType",String.valueOf(ServicePMTemplateContext.MasterPMType.SPACE.getIndex()), EnumOperators.ISN_T));
        showRule.setCriteria(criteria);

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("PM Type");
        showRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
        actionField.setFormFieldName("Space");

        FormRuleActionContext hideAction = new FormRuleActionContext();
        hideAction.setActionType(FormActionType.HIDE_FIELD.getVal());
        hideAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        FormRuleActionContext mandatoryAction = new FormRuleActionContext();
        mandatoryAction.setActionType(FormActionType.REMOVE_MANDATORY.getVal());
        mandatoryAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(hideAction);
        actions.add(mandatoryAction);
        showRule.setActions(actions);
        showRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return showRule;
    }
}
