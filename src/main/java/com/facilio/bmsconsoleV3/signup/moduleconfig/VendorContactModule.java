package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
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
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.v3.context.Constants;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class VendorContactModule extends BaseModuleConfig{
    public VendorContactModule(){
        setModuleName(FacilioConstants.ContextNames.VENDOR_CONTACT);
    }

    public void addData() throws Exception {
        addActivityModuleForVendorContacts();
        addSystemButtons();
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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
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

        FacilioForm vendorContactForm = new FacilioForm();
        vendorContactForm.setDisplayName("NEW VENDOR CONTACT");
        vendorContactForm.setName("default_vendorcontact_web");
        vendorContactForm.setModule(vendorContactModule);
        vendorContactForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        vendorContactForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> vendorContactFormFields = new ArrayList<>();

        vendorContactFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.FILE, "Upload Photo",FormField.Required.OPTIONAL, 1,2));
        vendorContactFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 2));
        //check
        vendorContactFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.PHONE, "Phone", FormField.Required.REQUIRED, 3, 2));
        vendorContactFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.REQUIRED, 4, 2));
        vendorContactFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED, "vendors", 5, 2));
        // CHANGE CURRENCY FIELD
        vendorContactFormFields.add(new FormField("rate", FacilioField.FieldDisplayType.TEXTBOX, "Rate per Hour", FormField.Required.OPTIONAL, 6, 2));
        vendorContactFormFields.add(new FormField("isPrimaryContact", FacilioField.FieldDisplayType.DECISION_BOX, "Primary Contact", FormField.Required.OPTIONAL, 7, 1));
        vendorContactFormFields.add(new FormField("dispatchable", FacilioField.FieldDisplayType.DECISION_BOX, "Dispatchable", FormField.Required.OPTIONAL, 8, 2));
        vendorContactFormFields.add(new FormField("trackGeoLocation", FacilioField.FieldDisplayType.DECISION_BOX, "Track Geolocation", FormField.Required.OPTIONAL, 9, 2));
        vendorContactFormFields.add(new FormField("territories",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Territories",FormField.Required.OPTIONAL,10,1));



        FormSection formSection = new FormSection("Vendor Contact Details", 1, vendorContactFormFields, true);
        formSection.setSectionType(FormSection.SectionType.FIELDS);
        vendorContactForm.setSections(Collections.singletonList(formSection));
        vendorContactForm.setIsSystemForm(true);
        vendorContactForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> vendorContactModuleForms = new ArrayList<>();
        vendorContactModuleForms.add(newVendorContactForm);
        vendorContactModuleForms.add(vendorContactForm);
        return vendorContactModuleForms;
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
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createVendorContactPage(app, module, false,true));
        }
        return appNameVsPage;
    }
    private static List<PagesContext> createVendorContactPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        if(app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FSM_APP)){
            return createFSMVendorContactPage(app,module,isTemplate,isDefault);
        }

        String pageName, pageDisplayName;
        pageName = module.getName()+ "defaultpage";
        pageDisplayName = "Default "+module.getDisplayName()+" Page ";
        return new ModulePages().addPage(pageName, pageDisplayName, "", null, isTemplate, isDefault, true)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryfieldswidget", "Contact Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app, module.getName()))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();

    }
    private static List<PagesContext> createFSMVendorContactPage(ApplicationContext app,FacilioModule module,boolean isTemplate,boolean isDefault) throws Exception{
        return new ModulePages()
                .addPage("vendorContact", "Default Vendor Contact Page","", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("vendorContactSummary", "Summary",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorSummaryFields", null, null)
                .addWidget("vendorSummaryFieldsWidget", "Vendor Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.ContextNames.VENDOR_CONTACT))
                .widgetDone()
                .sectionDone()
                .addSection("vendorLocationStatus", null, null)
                .addWidget("vendorLastKnownLocationWidget", "Last Known Location", PageWidget.WidgetType.VENDOR_CONTACT_LAST_KNOWN_LOCATION, "webVendorContactLastKnownLocation_4_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("vendorCurrentStatusWidget", "Current Status", PageWidget.WidgetType.VENDOR_CONATCT_CURRENT_STATUS, "webVendorContactCurrentStatus_4_6", 6, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("vendorSkill", "Skill",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorSkill", null, null)
                .addWidget("vendorSkillWidget", "Skills", PageWidget.WidgetType.VENDOR_CONTACT_SKILL, "flexibleVendorContactSkill_10", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addTab("vendorWorkSchedule", "Work Schedule",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorWorkSchedule", null, null)
                .addWidget("vendorWorkScheduleWidget", "Work Schedule", PageWidget.WidgetType.VENDOR_CONTACT_WORK_SCHEDULE, "flexibleVendorContactWorkSchedule_10", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addTab("vendorHistory", "History",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_10", 0, 0, null, getHistoryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }

    private static JSONObject getSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {
        if(app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FSM_APP)){
            return getFSMSummaryWidgetDetails(app,moduleName);
        }

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField vendorField = moduleBean.getField("vendor", moduleName);
        FacilioField isPrimaryContactField = moduleBean.getField("isPrimaryContact", moduleName);
        FacilioField emailField = moduleBean.getField("email", moduleName);
        FacilioField phoneField = moduleBean.getField("phone", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();

        SummaryWidgetGroup generalInformationWidgetGroup = new SummaryWidgetGroup();
        generalInformationWidgetGroup.setName("generalInformation");
        generalInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, vendorField, 1, 1, 1,"Vendor");
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, isPrimaryContactField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, emailField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, phoneField, 1, 4, 1);

        SummaryWidgetGroup systemInformationWidgetGroup = new SummaryWidgetGroup();
        systemInformationWidgetGroup.setName("systemInformation");
        systemInformationWidgetGroup.setDisplayName("System Information");
        systemInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedTimeField, 1, 4, 1);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationWidgetGroup);
        widgetGroupList.add(systemInformationWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }
    private static JSONObject getFSMSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField phoneField = moduleBean.getField("phone", moduleName);
        FacilioField emailField = moduleBean.getField("email", moduleName);
        FacilioField dispatchableField = moduleBean.getField("dispatchable", moduleName);
        FacilioField trackGeoLocationField = moduleBean.getField("trackGeoLocation", moduleName);
        FacilioField territoriesField = moduleBean.getField("territories", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, phoneField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, emailField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, dispatchableField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, trackGeoLocationField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, territoriesField, 2, 2, 1);

        SummaryWidgetGroup systemInformationWidgetGroup = new SummaryWidgetGroup();
        systemInformationWidgetGroup.setName("systemInformation");
        systemInformationWidgetGroup.setDisplayName("System Information");
        systemInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedTimeField, 1, 4, 1);



        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(systemInformationWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
       addSummaryFieldInWidgetGroup(widgetGroup,field,rowIndex,colIndex,colSpan,null);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan,String displayName) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            if(StringUtils.isNotEmpty(displayName)){
                summaryField.setDisplayName(displayName);
            }else {
                summaryField.setDisplayName(field.getDisplayName());
            }
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
        commentWidgetParam.put("notesModuleName", "peoplenotes");

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "peopleattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
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
                .addWidget("fieldUpdateWidget", "Field Update", PageWidget.WidgetType.ACTIVITY, isMobile ? "flexiblemobilecomment_8" : "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("location", "Location", "")
                .addWidget("locationWidget", "Location", PageWidget.WidgetType.VENDOR_LOCATION, isMobile ? "flexiblemobileattachment_8" : "flexibleVendorLocation_10", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
    public static void addSystemButtons() throws Exception {
        for(SystemButtonRuleContext btn:getSystemButtons()){
            SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.VENDOR_CONTACT, btn);
        }
    }
    public static List<SystemButtonRuleContext> getSystemButtons(){
        List<SystemButtonRuleContext> btnList = new ArrayList<>();

        SystemButtonRuleContext editButton = new SystemButtonRuleContext();
        editButton.setName("Edit");
        editButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editButton.setIdentifier("edit");
        editButton.setPermissionRequired(true);
        editButton.setPermission("UPDATE");

        SystemButtonRuleContext portalAccessButton = new SystemButtonRuleContext();
        portalAccessButton.setName("PortalAccess");
        portalAccessButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        portalAccessButton.setIdentifier(FacilioConstants.ContextNames.VENDORCONTACT_MODULE_PORTAL_BUTTON);
        portalAccessButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        portalAccessButton.setPermission("MANAGE_ACCESS");
        portalAccessButton.setPermissionRequired(true);

        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName("New Vendor Contact");
        createButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButton.setIdentifier("create");
        createButton.setPermissionRequired(true);
        createButton.setPermission("CREATE");
        btnList.add(createButton);


        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        btnList.add(listEditButton);


        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        btnList.add(listDeleteButton);



        SystemButtonRuleContext bulkDeleteButton = new SystemButtonRuleContext();
        bulkDeleteButton.setName("Delete");
        bulkDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        bulkDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkDeleteButton.setIdentifier("delete_bulk");
        bulkDeleteButton.setPermissionRequired(true);
        bulkDeleteButton.setPermission("DELETE");
        btnList.add(bulkDeleteButton);



        SystemButtonRuleContext exportAsCSVButton = new SystemButtonRuleContext();
        exportAsCSVButton.setName("Export As CSV");
        exportAsCSVButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsCSVButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCSVButton.setIdentifier("export_as_csv");
        exportAsCSVButton.setPermissionRequired(true);
        exportAsCSVButton.setPermission("EXPORT");
        btnList.add(exportAsCSVButton);



        SystemButtonRuleContext exportAsExcelButton = new SystemButtonRuleContext();
        exportAsExcelButton.setName("Export As Excel");
        exportAsExcelButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsExcelButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButton.setIdentifier("export_as_excel");
        exportAsExcelButton.setPermissionRequired(true);
        exportAsExcelButton.setPermission("EXPORT");
        btnList.add(exportAsExcelButton);


        btnList.add(editButton);
        btnList.add(portalAccessButton);

        return btnList;
    }
}

