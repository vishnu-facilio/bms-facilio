package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
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
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class InductionResponseModule extends BaseModuleConfig{
    public InductionResponseModule(){
        setModuleName(FacilioConstants.Induction.INDUCTION_RESPONSE);
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.Induction.INDUCTION_RESPONSE);
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createInductionResponsePage(app, module, false,true));
        }
        return appNameVsPage;
    }

    public List<PagesContext> createInductionResponsePage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Induction.INDUCTION_RESPONSE_ACTIVITY);

        return new ModulePages()
                .addPage("inductionResponsedefaultpage", "Default Induction Response Page", "", null, isTemplate, isDefault, true)
                .addLayout(PagesContext.PageLayoutType.WEB)

                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summary", "", null)
                .addWidget("inductionSummaryWidget", "Induction", PageWidget.WidgetType.INDUCTION_RESPONSE_SUMMARY, "flexibleinductionresponsesummarywidget_5", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("notesandinformation", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("inductionNotesAndInformationWidget", "Field Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getInductionSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("commentsandattachments", "", null)
                .addWidget("inductionCommentsAndAttachmentsWidget", "", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getInductionWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inductionresponsehistory", null, null)
                .addWidget("inductionResponseHistoryWidget", "Induction", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .layoutDone()
                .pageDone().getCustomPages();
    }

    public JSONObject getInductionSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField workEndField = moduleBean.getField("actualWorkEnd", moduleName);
        FacilioField workStartField = moduleBean.getField("actualWorkStart", moduleName);
        FacilioField assignedTOField = moduleBean.getField("people", moduleName);
        FacilioField scheduleWorkEndField = moduleBean.getField("scheduledWorkEnd", moduleName);
        FacilioField scheduleWorkStartField = moduleBean.getField("scheduledWorkStart", moduleName);
        FacilioField vendorField = moduleBean.getField("vendor", moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, workEndField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, workStartField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, assignedTOField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, vendorField, 1, 4, 1);

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, scheduleWorkEndField, 2, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, scheduleWorkStartField, 2, 2, 1);


        widgetGroup.setName("generalinformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);
        widgetGroup.setSequenceNumber(1);

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysCreatedByField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysCreatedTimeField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysModifiedByField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysModifiedTimeField, 1, 4, 1);

        widgetGroup2.setName("systeminformation");
        widgetGroup2.setDisplayName("System Information");
        widgetGroup2.setColumns(4);
        widgetGroup2.setSequenceNumber(2);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup2);

        pageWidget.setDisplayName("Induction Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    public JSONObject getInductionWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("activityModuleName", "inductionResponsenotes");

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("activityModuleName","inductionResponseattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);

    }

    @Override
    public void addData() throws Exception {
        addSystemButtons();
    }

    public static void addSystemButtons() throws Exception {

        SystemButtonRuleContext conductInspectionSystemButton = new SystemButtonRuleContext();
        conductInspectionSystemButton.setName(FacilioConstants.Induction.ATTEND_INDUCTION_BUTTON);
        conductInspectionSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        conductInspectionSystemButton.setIdentifier(FacilioConstants.Induction.ATTEND_INDUCTION_IDENTIFIER);
        conductInspectionSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        conductInspectionSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
        conductInspectionSystemButton.setPermissionRequired(true);

        SystemButtonRuleContext downloadPdfSystemButton = new SystemButtonRuleContext();
        downloadPdfSystemButton.setName(FacilioConstants.Inspection.DOWNLOAD_PDF_BUTTON_TEXT);
        downloadPdfSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        downloadPdfSystemButton.setIdentifier(FacilioConstants.Inspection.DOWNLOAD_PDF_BUTTON_IDENTIFIER);
        downloadPdfSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        downloadPdfSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
        downloadPdfSystemButton.setPermissionRequired(true);

        SystemButtonRuleContext printSystemButton = new SystemButtonRuleContext();
        printSystemButton.setName(FacilioConstants.Inspection.PRINT_BUTTON_TEXT);
        printSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        printSystemButton.setIdentifier(FacilioConstants.Inspection.PRINT_BUTTON_IDENTIFIER);
        printSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        printSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
        printSystemButton.setPermissionRequired(true);

        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_RESPONSE,conductInspectionSystemButton);
        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_RESPONSE,downloadPdfSystemButton);
        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_RESPONSE,printSystemButton);

    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inductionResponse = new ArrayList<FacilioView>();
        inductionResponse.add(getAllInductionResponseViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Induction.INDUCTION_RESPONSE);
        groupDetails.put("views", inductionResponse);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInductionResponseViews() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Induction_Responses.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inductions");
        allView.setModuleName(FacilioConstants.Induction.INDUCTION_RESPONSE);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inductionResponseModule = modBean.getModule(FacilioConstants.Induction.INDUCTION_RESPONSE);

        FacilioForm inductionForm = new FacilioForm();
        inductionForm.setName("default_" + FacilioConstants.Induction.INDUCTION_RESPONSE + "_web");
        inductionForm.setModule(inductionResponseModule);
        inductionForm.setDisplayName("Standard");
        inductionForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        inductionForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        inductionForm.setShowInWeb(true);

        List<FormField> inductionFormFields = new ArrayList<>();
        int i = 1;
        inductionFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.OPTIONAL, i++, 1));
        inductionFormFields.add(new FormField("parent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parent", FormField.Required.OPTIONAL, i++, 1));
        inductionFormFields.add(new FormField("createdTime", FacilioField.FieldDisplayType.DATETIME, "Created Time", FormField.Required.REQUIRED, "building", i++, 2));
        inductionFormFields.add(new FormField("scheduledWorkStart", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start", FormField.Required.OPTIONAL, "site", i++, 3));
        inductionFormFields.add(new FormField("scheduledWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Scheduled End", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("actualWorkStart", FacilioField.FieldDisplayType.DATETIME, "Actual Start", FormField.Required.OPTIONAL, i++, 3));
        inductionFormFields.add(new FormField("actualWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Actual End", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("actualWorkDuration", FacilioField.FieldDisplayType.DATETIME, "Actual Duration", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("status", FacilioField.FieldDisplayType.DECISION_BOX, "Response Status", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("sourceType", FacilioField.FieldDisplayType.DECISION_BOX, "Source", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("resource", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space/Asset", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("people", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Assigned To", FormField.Required.OPTIONAL, i++, 2));
//        inductionForm.setFields(inductionFormFields);

        FormSection section = new FormSection("Default", 1, inductionFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        inductionForm.setSections(Collections.singletonList(section));
        inductionForm.setIsSystemForm(true);
        inductionForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(inductionForm);
    }
}
