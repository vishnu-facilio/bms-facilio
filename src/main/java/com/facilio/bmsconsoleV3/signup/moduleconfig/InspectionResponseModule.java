package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class InspectionResponseModule extends BaseModuleConfig{
    public InspectionResponseModule(){
        setModuleName(FacilioConstants.Inspection.INSPECTION_RESPONSE);
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);
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
            appNameVsPage.put(appName,createInspectionResponsePage(app, module, false,true));
        }
        return appNameVsPage;
    }

    public List<PagesContext> createInspectionResponsePage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceRequest = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Inspection.INSPECTION_RESPONSE_ACTIVITY);

        return new ModulePages()
                .addPage("inspectionResponsedefaultpage", "Default Inspection Response Page", "", null, isTemplate, isDefault, true)
                .addLayout(PagesContext.PageLayoutType.WEB)

                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summary", "", null)
                .addWidget("inspectionSummaryWidget", "Inspection", PageWidget.WidgetType.INSPECTION_RESPONSE_SUMMARY, "flexibleinspectionresponsesummarywidget_5", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("notesandinformation", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("inspectionNotesAndInformationWidget", "Field Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getInspectionSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("commentsandattachments", "", null)
                .addWidget("inspectionCommentsAndAttachmentsWidget", "", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getInspectionWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("related", "Related", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inspectionresponserelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("inspectionresponsebulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("inspectionresponserelatedlist", "Related List", "List of related records across modules")
                .addWidget("inspectionresponsebulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inspectionresponsehistory", null, null)
                .addWidget("inspectionResponseHistoryWidget", "Inspection", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .layoutDone()
                .pageDone().getCustomPages();
    }

    public JSONObject getInspectionSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField workEndField = moduleBean.getField("actualWorkEnd", moduleName);
        FacilioField workStartField = moduleBean.getField("actualWorkStart", moduleName);
        FacilioField assignedTOField = moduleBean.getField("assignedTo", moduleName);
        FacilioField typeField = moduleBean.getField("type", moduleName);
        FacilioField scheduleWorkEndField = moduleBean.getField("scheduledWorkEnd", moduleName);
        FacilioField scheduleWorkStartField = moduleBean.getField("scheduledWorkStart", moduleName);
        FacilioField categoryField = moduleBean.getField("category", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);
        FacilioField siteField = moduleBean.getField("siteId", moduleName);
        FacilioField spaceAssetField = moduleBean.getField("resource", moduleName);
        FacilioField tenantField = moduleBean.getField("tenant", moduleName);
        FacilioField vendorField = moduleBean.getField("vendor", moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, workEndField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, workStartField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, assignedTOField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, typeField, 1, 4, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, scheduleWorkEndField, 2, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, scheduleWorkStartField, 2, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, categoryField, 2, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, priorityField, 2, 4, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, siteField, 3, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, spaceAssetField, 3, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, tenantField, 3, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, vendorField, 3, 4, 1);


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

        pageWidget.setDisplayName("Inspection Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    public  JSONObject getInspectionWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("activityModuleName", FacilioConstants.Inspection.INSPECTION_RESPONSE_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("activityModuleName",FacilioConstants.Inspection.INSPECTION_RESPONSE_ATTACHMENTS);

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

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SystemButtonRuleContext conductInspectionSystemButton = new SystemButtonRuleContext();
        conductInspectionSystemButton.setName(FacilioConstants.Inspection.CONDUCT_INSPECTION_BUTTON_TEXT);
        conductInspectionSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        conductInspectionSystemButton.setIdentifier(FacilioConstants.Inspection.CONDUCT_INSPECTION_BUTTON_IDENTIFIER);
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

        SystemButtonApi.addSystemButton(FacilioConstants.Inspection.INSPECTION_RESPONSE,conductInspectionSystemButton);
        SystemButtonApi.addSystemButton(FacilioConstants.Inspection.INSPECTION_RESPONSE,downloadPdfSystemButton);
        SystemButtonApi.addSystemButton(FacilioConstants.Inspection.INSPECTION_RESPONSE,printSystemButton);

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inspectionResponse = new ArrayList<FacilioView>();
        inspectionResponse.add(getAllInspectionResponseViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Inspection.INSPECTION_RESPONSE);
        groupDetails.put("views", inspectionResponse);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInspectionResponseViews() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Inspection_Responses.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inspections");
        allView.setModuleName(FacilioConstants.Inspection.INSPECTION_RESPONSE);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inspectionResponseModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);

        FacilioForm inspectionForm = new FacilioForm();
        inspectionForm.setName("default_"+FacilioConstants.Inspection.INSPECTION_RESPONSE+"_web");
        inspectionForm.setModule(inspectionResponseModule);
        inspectionForm.setDisplayName("Standard");
        inspectionForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        inspectionForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        inspectionForm.setShowInWeb(true);

        List<FormField> inspectionFormFields = new ArrayList<>();
        int i = 1;
        inspectionFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.OPTIONAL, i++, 1));
        inspectionFormFields.add(new FormField("parent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parent", FormField.Required.OPTIONAL, i++, 1));
        inspectionFormFields.add(new FormField("createdTime", FacilioField.FieldDisplayType.DATETIME, "Created Time", FormField.Required.REQUIRED,  "building",i++, 2));
        inspectionFormFields.add(new FormField("scheduledWorkStart", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start", FormField.Required.OPTIONAL, "site",i++, 3));
        inspectionFormFields.add(new FormField("scheduledWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Scheduled End", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("actualWorkStart", FacilioField.FieldDisplayType.DATETIME, "Actual Start", FormField.Required.OPTIONAL, i++, 3));
        inspectionFormFields.add(new FormField("actualWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Actual End", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("actualWorkDuration", FacilioField.FieldDisplayType.DATETIME, "Actual Duration", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("status", FacilioField.FieldDisplayType.DECISION_BOX, "Response Status", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("sourceType", FacilioField.FieldDisplayType.DECISION_BOX, "Source", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("resource", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space/Asset", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("assignedTo", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Assigned To", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("assignmentGroup", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Team", FormField.Required.OPTIONAL, i++, 2));
//        inspectionForm.setFields(inspectionFormFields);

        FormSection section = new FormSection("Default", 1, inspectionFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        inspectionForm.setSections(Collections.singletonList(section));
        inspectionForm.setIsSystemForm(true);
        inspectionForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(inspectionForm);
    }
}
