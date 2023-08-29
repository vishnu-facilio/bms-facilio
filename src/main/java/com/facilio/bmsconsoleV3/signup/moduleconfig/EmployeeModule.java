package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.CreateDefaultPageCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class EmployeeModule extends BaseModuleConfig{
    public EmployeeModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.EMPLOYEE);
    }

    public void addData() throws Exception {
        addActivityModuleForEmployee();
        SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.EMPLOYEE));
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> employee = new ArrayList<FacilioView>();
        employee.add(getAllHiddenEmployees().setOrder(order++));
        employee.add(getAllEmployees().setOrder(order++));
//        employee.add(getAllEmployeesForFsm());

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.EMPLOYEE);
        groupDetails.put("views", employee);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHiddenEmployees() {

        FacilioModule employeeModule = ModuleFactory.getEmployeeModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Employees");
        allView.setModuleName(employeeModule.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        allView.setHidden(true);
        return allView;
    }

    private static FacilioView getAllEmployees() {

        FacilioModule employeeModule = ModuleFactory.getEmployeeModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all-employees");
        allView.setDisplayName("All Employees");
        allView.setModuleName(employeeModule.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return allView;
    }
//    private static FacilioView getAllEmployeesForFsm(){
//
//        FacilioModule employeeModule = ModuleFactory.getEmployeeModule();
//
//        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
//
//        FacilioView allView = new FacilioView();
//        allView.setName("all-employees");
//        allView.setDisplayName("All Employees");
//        allView.setModuleName(employeeModule.getName());
//        allView.setSortFields(sortFields);
//        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP));
//        return allView;
//    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule employeeModule = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);

        FacilioForm employeeContactForm = new FacilioForm();
        employeeContactForm.setDisplayName("NEW EMPLOYEE");
        employeeContactForm.setName("default_employee_web");
        employeeContactForm.setModule(employeeModule);
        employeeContactForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        employeeContactForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> employeeContactFormFields = new ArrayList<>();
        employeeContactFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        employeeContactFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 1));
        employeeContactFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL, 3, 1));
        employeeContactFormFields.add(new FormField("isAssignable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Assignable", FormField.Required.OPTIONAL, 4, 2));
        employeeContactFormFields.add(new FormField("isLabour", FacilioField.FieldDisplayType.DECISION_BOX, "Is Labour", FormField.Required.OPTIONAL, 5, 3));
//        employeeContactForm.setFields(employeeContactFormFields);

        FormSection section = new FormSection("Default", 1, employeeContactFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        employeeContactForm.setSections(Collections.singletonList(section));
        employeeContactForm.setIsSystemForm(true);
        employeeContactForm.setType(FacilioForm.Type.FORM);

        FacilioForm employeeForm = new FacilioForm();
        employeeForm.setDisplayName("NEW EMPLOYEE");
        employeeForm.setName("default_employee_fsm_web");
        employeeForm.setModule(employeeModule);
        employeeForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        employeeForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> employeeFormFields = new ArrayList<>();
        // check this
        employeeFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.FILE, "Upload Photo",FormField.Required.OPTIONAL, 1,2));
        employeeFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 2));
        //
        employeeFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.PHONE, "Phone", FormField.Required.REQUIRED, 3, 2));
        employeeFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.REQUIRED, 4, 2));
        employeeFormFields.add(new FormField("designation", FacilioField.FieldDisplayType.TEXTBOX, "Designation", FormField.Required.REQUIRED, 5, 2));

        // CHANGE CURRENCY FIELD
        employeeFormFields.add(new FormField("rate", FacilioField.FieldDisplayType.TEXTBOX, "Rate per Hour", FormField.Required.OPTIONAL, 6, 2));
        employeeFormFields.add(new FormField("dispatchable", FacilioField.FieldDisplayType.DECISION_BOX, "Dispatchable", FormField.Required.OPTIONAL, 7, 2));
        employeeFormFields.add(new FormField("trackGeoLocation", FacilioField.FieldDisplayType.DECISION_BOX, "Track Geolocation", FormField.Required.OPTIONAL, 8, 2));

        FormSection employeeSection = new FormSection("Employee Details", 1, employeeFormFields, true);
        employeeSection.setSectionType(FormSection.SectionType.FIELDS);
        employeeForm.setSections(Collections.singletonList(employeeSection));
        employeeForm.setIsSystemForm(true);
        employeeForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> employeeModuleForms = new ArrayList<>();
        employeeModuleForms.add(employeeContactForm);
        employeeModuleForms.add(employeeForm);
        return employeeModuleForms;
    }

    public void addActivityModuleForEmployee() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule employee = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.EMPLOYEE_ACTIVITY,
                "Employee Activity",
                "Employee_Activity",
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

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module=ModuleFactory.getEmployeeModule();

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createEmployeePage(app, module, false,true));
        }
        return appNameVsPage;

    }
    private static List<PagesContext> createEmployeePage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        return new ModulePages()
                .addPage("employee", "Employee","", null, isTemplate, isDefault, false)
                .addWebTab("employeesummary", "SUMMARY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("employeesummaryfields", null, null)
                .addWidget("employeesummaryfieldswidget", "Employee Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfiledswidget_22", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ContextNames.EMPLOYEE))
                .widgetDone()
                .sectionDone()
                .addSection("employeelocationstatus", null, null)
                .addWidget("employeelastknowlocationwidget", "Last Known Location", PageWidget.WidgetType.LAST_KNOW_LOCATION, "weblastknowlocation_22_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("employeecurrentstatuswidget", "Employee Current Status", PageWidget.WidgetType.CURRENT_STATUS, "webcurrentstatus_22_6", 6, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("employeeskill", "SKILL", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("employeeskill", null, null)
                .addWidget("employeeskillwidget", "Skill", PageWidget.WidgetType.SKILL, "flexiblewebskill_48", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("employeeworkschedule", "WORK SCHEDULE", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("employeeworkschedule", null, null)
                .addWidget("employeeworkschedulewidget", "Work Schedule", PageWidget.WidgetType.WORK_SCHEDULE, "flexiblewebworkschedule_50", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("employeerelated", "RELATED", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("employeerelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("employeebulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("employeerelatedlist", "Related List", "List of related records across modules")
                .addWidget("employeebulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(FacilioConstants.ContextNames.EMPLOYEE))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("employeehistory", "HISTORY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_50", 0, 0, null, getHistoryWidgetGroup(false))
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
        FacilioField phoneField = moduleBean.getField("phone", moduleName);
        FacilioField emailField = moduleBean.getField("email", moduleName);
        FacilioField roleField = moduleBean.getField("roleId", moduleName);
        FacilioField dispatchableField = moduleBean.getField("dispatchable", moduleName);
        FacilioField trackGeoLocationField = moduleBean.getField("trackGeoLocation", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, phoneField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, emailField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, roleField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, dispatchableField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, trackGeoLocationField, 2, 2, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

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
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.EMPLOYEE_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.EMPLOYEE_ATTACHMENTS);

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

    private static JSONObject getHistoryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.EMPLOYEE_ACTIVITY);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("fieldUpdate", "Field Update", "")
                .addWidget("fieldUpdatewidget", "Field Update", PageWidget.WidgetType.ACTIVITY, isMobile ? "flexiblemobilecomment_8" : "flexiblewebactivity_50", 0, 0, historyWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("location", "Location", "")
                .addWidget("locationwidget", "Location", PageWidget.WidgetType.EMPLOYEE_LOCATION, isMobile ? "flexiblemobileattachment_8" : "flexiblewebactivity_50", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}

