package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
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

public class VendorContactModule extends BaseModuleConfig{
    public VendorContactModule(){
        setModuleName(FacilioConstants.ContextNames.VENDOR_CONTACT);
    }

    public void addData() throws Exception {
        addActivityModuleForVendorContacts();
        SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.VENDOR_CONTACT));
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> vendorContact = new ArrayList<FacilioView>();
        vendorContact.add(getAllHiddenVendorContacts().setOrder(order++));
        vendorContact.add(getAllVendorContacts().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VENDOR_CONTACT);
        groupDetails.put("views", vendorContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHiddenVendorContacts() {

        FacilioModule vendorContactModule = ModuleFactory.getVendorContactModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Vendor Contacts");
        allView.setModuleName(vendorContactModule.getName());
        allView.setSortFields(sortFields);

        allView.setHidden(true);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAllVendorContacts() {

        FacilioModule vendorContactModule = ModuleFactory.getVendorContactModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all-contacts");
        allView.setDisplayName("All Vendor Contacts");
        allView.setModuleName(vendorContactModule.getName());
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
        FacilioModule vendorContactModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);

        FacilioForm newVendorContactForm = new FacilioForm();
        newVendorContactForm.setDisplayName("NEW VENDOR CONTACT");
        newVendorContactForm.setName("default_vendorcontact_web");
        newVendorContactForm.setModule(vendorContactModule);
        newVendorContactForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        newVendorContactForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> newVendorContactFormFields = new ArrayList<>();
        newVendorContactFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        newVendorContactFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 1));
        newVendorContactFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL, 3, 1));
        newVendorContactFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED, "vendors", 4, 1));
        newVendorContactFormFields.add(new FormField("isPrimaryContact", FacilioField.FieldDisplayType.DECISION_BOX, "Primary Contact", FormField.Required.OPTIONAL, 5, 1));
//        newVendorContactForm.setFields(newVendorContactFormFields);

        FormSection section = new FormSection("Default", 1, newVendorContactFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        newVendorContactForm.setSections(Collections.singletonList(section));
        newVendorContactForm.setIsSystemForm(true);
        newVendorContactForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(newVendorContactForm);
    }

    public void addActivityModuleForVendorContacts() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule vc = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.VENDOR_CONTACTS_ACTIVITY,
                "Vendor Contact Activity",
                "Vendor_Contacts_Activity",
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

        modBean.addSubModule(vc.getModuleId(), module.getModuleId());
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module=ModuleFactory.getVendorContactModule();

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createVendorPage(app, module, false,true));
        }
        return appNameVsPage;
    }
    private static List<PagesContext> createVendorPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        return new ModulePages()
                .addPage("vendor", "Vendor","", null, isTemplate, isDefault, false)
                .addWebTab("vendorsummary", "SUMMARY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorsummaryfields", null, null)
                .addWidget("vendorsummaryfieldswidget", "Vendor Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfiledswidget_22", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ContextNames.VENDOR_CONTACT))
                .widgetDone()
                .sectionDone()
                .addSection("vendorlocationstatus", null, null)
                .addWidget("vendorlastknowlocationwidget", "Last Know Location", PageWidget.WidgetType.LAST_KNOW_LOCATION, "weblastknowlocation_22_6", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ContextNames.VENDOR_CONTACT))
                .widgetDone()
                .addWidget("vendorcurrentstatuswidget", "Vendor Current Status", PageWidget.WidgetType.CURRENT_STATUS, "webcurrentstatus_22_6", 6, 0, null, getSummaryWidgetDetails(FacilioConstants.ContextNames.VENDOR_CONTACT))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("vendorskill", "SKILL", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorskill", null, null)
                .addWidget("vendorskillwidget", "Skill", PageWidget.WidgetType.SKILL, "flexiblewebskill_60", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ContextNames.VENDOR_CONTACT))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("vendorterritories", "TERRITORIES", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorterritories", null, null)
                .addWidget("vendorterritorieswidget", "Territories", PageWidget.WidgetType.TERRITORIES, "flexiblewebterritories_60", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ContextNames.VENDOR_CONTACT))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("vendorworkschedule", "WORK SCHEDULE", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorworkschedule", null, null)
                .addWidget("vendorworkschedulewidget", "Work Schedule", PageWidget.WidgetType.WORK_SCHEDULE, "flexiblewebworkschedule_60", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ContextNames.VENDOR_CONTACT))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("vendorrelated", "RELATED", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("vendorbulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("vendorrelatedlist", "Related List", "List of related records across modules")
                .addWidget("vendorbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(FacilioConstants.ContextNames.VENDOR_CONTACT))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("vendorhistory", "HISTORY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_60", 0, 0, null, getHistoryWidgetGroup(false))
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
        addSummaryFieldInWidgetGroup(widgetGroup, dispatchableField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, trackGeoLocationField, 2, 1, 1);


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
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.VENDOR_CONTACT_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.VENDOR_CONTACT_ATTACHMENTS);

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
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.VENDOR_CONTACTS_ACTIVITY);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("fieldUpdate", "Field Update", "")
                .addWidget("fieldUpdatewidget", "Field Update", PageWidget.WidgetType.ACTIVITY, isMobile ? "flexiblemobilecomment_8" : "flexiblewebactivity_60", 0, 0, historyWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("location", "Location", "")
                .addWidget("locationwidget", "Location", PageWidget.WidgetType.VENDOR_LOCATION, isMobile ? "flexiblemobileattachment_8" : "flexiblewebactivity_60", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}

