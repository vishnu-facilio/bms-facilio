package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.TemplatePages.PlannedMaintenanceTemplatePageFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.jobPlan.AddJobPlanModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import static com.facilio.bmsconsole.util.SystemButtonApi.addSystemButton;

import java.util.*;

public class PlannedMaintenanceModule extends BaseModuleConfig{
    public PlannedMaintenanceModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> plannedMaintenance = new ArrayList<FacilioView>();
        plannedMaintenance.add(getAllPlannedMaintenanceView().setOrder(order++));
        plannedMaintenance.add(getActivePlannedMaintenanceView().setOrder(order++));
        plannedMaintenance.add(getInActivePlannedMaintenanceView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        groupDetails.put("appLinkNames", AddJobPlanModule.jobPlanSupportedApps);
        groupDetails.put("views", plannedMaintenance);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPlannedMaintenanceView() {
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Planned Maintenance");
        allView.setAppLinkNames(AddJobPlanModule.jobPlanSupportedApps);

        FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getInActivePlannedMaintenanceView() {
        Criteria criteria = getUnPublishedPlannedMaintenanceCriteria();
        FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(plannedMaintenanceModule);
        FacilioView allView = new FacilioView();
        allView.setName("inactive");
        allView.setDisplayName("Unpublished");
        allView.setCriteria(criteria);

        allView.setAppLinkNames(AddJobPlanModule.jobPlanSupportedApps);

        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getActivePlannedMaintenanceView() {
        Criteria criteria = getPublishedPlannedMaintenanceCriteria();
        FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(plannedMaintenanceModule);
        FacilioView allView = new FacilioView();
        allView.setName("active");
        allView.setDisplayName("Published");
        allView.setCriteria(criteria);
        allView.setAppLinkNames(AddJobPlanModule.jobPlanSupportedApps);

        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }

    public static Criteria getPublishedPlannedMaintenanceCriteria() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("PM_STATUS","pmStatus",String.valueOf(PlannedMaintenance.PMStatus.ACTIVE.getVal()), NumberOperators.EQUALS));
        criteria.setPattern("(1)");
        return criteria;
    }

    public static Criteria getUnPublishedPlannedMaintenanceCriteria() {
        Criteria criteria = new Criteria();

        criteria.addAndCondition(CriteriaAPI.getCondition("PM_STATUS","pmStatus",String.valueOf(PlannedMaintenance.PMStatus.IN_ACTIVE.getVal()), NumberOperators.EQUALS));
        criteria.setPattern("(1)");

        return criteria;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedMaintenance = modBean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);

        FacilioForm defaultPlannedMaintenanceForm = new FacilioForm();
        defaultPlannedMaintenanceForm.setDisplayName("Planned Maintenance");
        defaultPlannedMaintenanceForm.setName("default_plannedmaintenance_web");
        defaultPlannedMaintenanceForm.setModule(plannedMaintenance);
        defaultPlannedMaintenanceForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultPlannedMaintenanceForm.setShowInWeb(true);
        defaultPlannedMaintenanceForm.setAppLinkNamesForForm(AddJobPlanModule.jobPlanSupportedApps);

        List<FormField> defaultPlannedMaintenanceFormFields = new ArrayList<>();
        defaultPlannedMaintenanceFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", FormField.Required.REQUIRED, "tickettype", 1, 1));
        defaultPlannedMaintenanceFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 2, 1));
        defaultPlannedMaintenanceFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        defaultPlannedMaintenanceFormFields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "ticketcategory", 4, 2));
        defaultPlannedMaintenanceFormFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, "ticketpriority", 5, 3));
        defaultPlannedMaintenanceFormFields.add(new FormField("dueDuration", FacilioField.FieldDisplayType.DURATION, "Due Duration", FormField.Required.OPTIONAL, "duration", 6, 1));
        defaultPlannedMaintenanceFormFields.add(new FormField("estimatedWorkDuration", FacilioField.FieldDisplayType.DURATION, "Estimated Duration", FormField.Required.OPTIONAL, "duration", 7, 1));
        FormField groups = new FormField("assignmentGroup", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Team", FormField.Required.OPTIONAL, "groups", 8, 1);
        groups.addToConfig("isFiltersEnabled", true); // groups is special form field without actual field
        groups.addToConfig("lookupModuleName", "groups");
        defaultPlannedMaintenanceFormFields.add(groups);
        defaultPlannedMaintenanceFormFields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, "attachment", 9, 1));
        defaultPlannedMaintenanceForm.setFields(defaultPlannedMaintenanceFormFields);

        FormSection section = new FormSection("Default", 1, defaultPlannedMaintenanceFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        defaultPlannedMaintenanceForm.setSections(Collections.singletonList(section));
        defaultPlannedMaintenanceForm.setIsSystemForm(true);
        defaultPlannedMaintenanceForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(defaultPlannedMaintenanceForm);
    }


    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("assetCategory");
        fieldNames.add("spaceCategory");
        fieldNames.add("category");
        fieldNames.add("pmStatus");


        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

    /**
     * Page builder
     **/

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Map<String, List<PagesContext>> appNameVsPages = new HashMap<>();

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);


        FacilioModule plannedMaintenanceModule = modBean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);

        for (String appName : appLinkNames) {
            appNameVsPages.put(appName, createPlannedMaintenanceDefaultPage(ApplicationApi.getApplicationForLinkName(appName), plannedMaintenanceModule, false, true));
        }
        return appNameVsPages;
    }

    private List<PagesContext> createPlannedMaintenanceDefaultPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        String pageName, pageDisplayName;
        pageName = module.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "") + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";


        return new ModulePages()
                .addPage(pageName, pageDisplayName, "", null, isTemplate, isDefault, true)
                .addWebLayout()

                // Summary Tab
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)

                .addSection("summaryfields", null, null)
                .addWidget("plannedMaintenanceSummaryFieldsWidget", "Planned Maintenance Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET,
                        "flexiblewebsummaryfieldswidget_3", 0, 0, null,
                        PlannedMaintenanceTemplatePageFactory.getPMDetailsSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .addWidget("workOrderDetailsSummaryFieldsWidget", "Work Order Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET,
                        "flexiblewebsummaryfieldswidget_4", 0, 0, null,
                        PlannedMaintenanceTemplatePageFactory.getPMWorkorderDetailsSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()

                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, PlannedMaintenanceTemplatePageFactory.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                // Planner Tab
                .addTab("planner", "Planner", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("pmPlannerDetailsSection", null, null)
                .addWidget("pmPlannerTriggerDetailsWidget", null, PageWidget.WidgetType.PM_PLANNER_TRIGGER_DETAILS_WIDGET,
                        "flexiblewebpmPlannerTriggerDetails_4", 0, 0, null,
                        null)
                .widgetDone()
                .addWidget("ppResourcePlannerDetailsWidget", null, PageWidget.WidgetType.PM_RESOURCE_PLANNER_DETAILS_WIDGET,
                        "flexiblewebpmResourcePlannerDetails_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                // Scheduler tab
                .addTab("scheduler", "Scheduler", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("pmSchedulerDetailsSection", null, null)
                .addWidget("pmSchedulerDetailsWidget", null, PageWidget.WidgetType.PM_SCHEDULER_DETAILS_WIDGET,
                        "flexiblewebpmSchedulerDetails_11", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .layoutDone()
                .pageDone()
                .getCustomPages();
    }

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedMaintenanceModule = modBean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldList);
        addSystemButtons(modBean, plannedMaintenanceModule, fieldsMap);
    }

    private static void addSystemButtons(ModuleBean modBean, FacilioModule plannedMaintenanceModule, Map<String, FacilioField> fieldsMap) throws Exception {

        /** LIST PAGE SYSTEM BUTTONS **/
        // Table Top Bar buttons
        SystemButtonApi.addCreateButtonWithModuleDisplayName(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);

        // Export buttons
        SystemButtonRuleContext exportAsExcelButtonListTop = new SystemButtonRuleContext();
        exportAsExcelButtonListTop.setName("Export as excel");
        exportAsExcelButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS .getIndex());
        exportAsExcelButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButtonListTop.setIdentifier("export_as_excel");
        exportAsExcelButtonListTop.setPermissionRequired(true);
        exportAsExcelButtonListTop.setPermission("EXPORT");
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, exportAsExcelButtonListTop);

        SystemButtonRuleContext exportAsCsvButtonListTop = new SystemButtonRuleContext();
        exportAsCsvButtonListTop.setName("Export as CSV");
        exportAsCsvButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS .getIndex());
        exportAsCsvButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCsvButtonListTop.setIdentifier("export_as_csv");
        exportAsCsvButtonListTop.setPermissionRequired(true);
        exportAsCsvButtonListTop.setPermission("EXPORT");
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, exportAsCsvButtonListTop);

        // Table bar - Bulk action buttons
        SystemButtonRuleContext publishButtonListTop = new SystemButtonRuleContext();
        publishButtonListTop.setName("Publish");
        publishButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        publishButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        publishButtonListTop.setIdentifier("publishPM");
        publishButtonListTop.setPermissionRequired(true);
        publishButtonListTop.setPermission("UPDATE");
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, publishButtonListTop);

        SystemButtonRuleContext unPublishButtonListTop = new SystemButtonRuleContext();
        unPublishButtonListTop.setName("Unpublish");
        unPublishButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        unPublishButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        unPublishButtonListTop.setIdentifier("unpublishPM");
        unPublishButtonListTop.setPermissionRequired(true);
        unPublishButtonListTop.setPermission("UPDATE");
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, unPublishButtonListTop);

        SystemButtonRuleContext deleteButtonListTop = new SystemButtonRuleContext();
        deleteButtonListTop.setName("Delete");
        deleteButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        deleteButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        deleteButtonListTop.setIdentifier("delete_bulk");
        deleteButtonListTop.setPermissionRequired(true);
        deleteButtonListTop.setPermission("DELETE");
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, deleteButtonListTop);

        // Each Record wise
        SystemButtonRuleContext editButtonList = new SystemButtonRuleContext();
        editButtonList.setName("Edit");
        editButtonList.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editButtonList.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        editButtonList.setIdentifier("edit_list");
        editButtonList.setPermissionRequired(true);
        editButtonList.setPermission("UPDATE");
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, editButtonList);

        SystemButtonRuleContext deleteButtonList = new SystemButtonRuleContext();
        deleteButtonList.setName("Delete");
        deleteButtonList.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        deleteButtonList.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        deleteButtonList.setIdentifier("delete_list");
        deleteButtonList.setPermissionRequired(true);
        deleteButtonList.setPermission("DELETE");
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, deleteButtonList);


        /** SUMMARY PAGE SYSTEM BUTTONS **/
        SystemButtonRuleContext editButton = new SystemButtonRuleContext();
        editButton.setName("Edit");
        editButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editButton.setIdentifier("edit");
        editButton.setPermissionRequired(true);
        editButton.setPermission("UPDATE");
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, editButton);


        Criteria publishButtonCriteria = new Criteria();
        publishButtonCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("pmStatus"), PlannedMaintenance.PMStatus.IN_ACTIVE.getVal()+"",NumberOperators.EQUALS));
        SystemButtonRuleContext publishButton = new SystemButtonRuleContext();
        publishButton.setName("Publish");
        publishButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        publishButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        publishButton.setIdentifier("publishPM");
        publishButton.setPermissionRequired(true);
        publishButton.setPermission("UPDATE");
        publishButton.setCriteria(publishButtonCriteria);
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, publishButton);



        Criteria unPublishButtonCriteria = new Criteria();
        unPublishButtonCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("pmStatus"), PlannedMaintenance.PMStatus.ACTIVE.getVal()+"",NumberOperators.EQUALS));
        SystemButtonRuleContext unPublishButton = new SystemButtonRuleContext();
        unPublishButton.setName("Unpublish");
        unPublishButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        unPublishButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        unPublishButton.setIdentifier("unpublishPM");
        unPublishButton.setPermissionRequired(true);
        unPublishButton.setPermission("UPDATE");
        unPublishButton.setCriteria(unPublishButtonCriteria);
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, unPublishButton);

        SystemButtonRuleContext viewWorkOrdersButton = new SystemButtonRuleContext();
        viewWorkOrdersButton.setName("View Work Orders");
        viewWorkOrdersButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        viewWorkOrdersButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        viewWorkOrdersButton.setIdentifier("viewWorkOrders");
        addSystemButton(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, viewWorkOrdersButton);
    }

}

