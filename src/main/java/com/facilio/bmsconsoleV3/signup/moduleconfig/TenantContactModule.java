package com.facilio.bmsconsoleV3.signup.moduleconfig;

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
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class TenantContactModule extends BaseModuleConfig{
    public TenantContactModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT_CONTACT);
    }

    @Override
    public void addData() throws Exception {
        addSystemButtons();
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenantContact = new ArrayList<FacilioView>();
        tenantContact.add(getAllHiddenTenantContacts().setOrder(order++));
        tenantContact.add(getAllTenantContacts().setOrder(order++));
        tenantContact.add(getHiddenTenantContactListView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TENANT_CONTACT);
        groupDetails.put("views", tenantContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHiddenTenantContacts() {

        FacilioModule tenantContactModule = ModuleFactory.getTenantContactModule();

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(tenantContactModule);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenant Contacts");
        allView.setModuleName(tenantContactModule.getName());
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        allView.setHidden(true);

        return allView;
    }
    public static FacilioView getHiddenTenantContactListView() throws Exception {
        FacilioView tenantHistoryView = new FacilioView();
        tenantHistoryView.setName("tenantContactRelatedList");
        tenantHistoryView.setDisplayName("Tenant Contacts");
        tenantHistoryView.setHidden(true);


        FacilioModule tenantModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
        List<FacilioField> allFields =  Constants.getModBean().getAllFields(tenantModule.getName());

        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        String[] viewFieldNames = new String[]{"name","phone","email","isPrimaryContact"};
        List<ViewField> viewFields = new ArrayList<>();

        for(String viewFieldName:viewFieldNames){
            FacilioField field = fieldMap.get(viewFieldName);
            ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
            viewField.setFieldId(field.getFieldId());
            viewField.setFieldName(field.getName());
            viewFields.add(viewField);
        }

        tenantHistoryView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        tenantHistoryView.setAppLinkNames(appLinkNames);
        return tenantHistoryView;
    }

    private static FacilioView getAllTenantContacts() {

        FacilioModule tenantContactModule = ModuleFactory.getTenantContactModule();

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(tenantContactModule);


        FacilioView allView = new FacilioView();
        allView.setName("all-contacts");
        allView.setDisplayName("All Tenant Contacts");
        allView.setModuleName(tenantContactModule.getName());
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

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
        FacilioModule tenantContactModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);

        FacilioForm tenantContactForm = new FacilioForm();
        tenantContactForm.setDisplayName("NEW TENANT CONTACT");
        tenantContactForm.setName("default_tenantcontact_web");
        tenantContactForm.setModule(tenantContactModule);
        tenantContactForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        tenantContactForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> tenantContactFormfields = new ArrayList<>();
        tenantContactFormfields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        tenantContactFormfields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 1));
        tenantContactFormfields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL, 3, 1));
        tenantContactFormfields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.REQUIRED, "tenant", 4, 1));
        tenantContactFormfields.add(new FormField("isPrimaryContact", FacilioField.FieldDisplayType.DECISION_BOX, "Primary Contact", FormField.Required.OPTIONAL, 5, 1));
//        tenantContactForm.setFields(tenantContactFormfields);

        FormSection tenantContactFormSection = new FormSection("Default", 1, tenantContactFormfields, false);
        tenantContactFormSection.setSectionType(FormSection.SectionType.FIELDS);
        tenantContactForm.setSections(Collections.singletonList(tenantContactFormSection));
        tenantContactForm.setIsSystemForm(true);
        tenantContactForm.setType(FacilioForm.Type.FORM);

        FacilioForm tenantContactPortalForm = new FacilioForm();
        tenantContactPortalForm.setDisplayName("NEW TENANT CONTACT");
        tenantContactPortalForm.setName("default_tenantcontact_portal");
        tenantContactPortalForm.setModule(tenantContactModule);
        tenantContactPortalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        tenantContactPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> tenantContactPortalFormFields = new ArrayList<>();
        tenantContactPortalFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        tenantContactPortalFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 1));
        tenantContactPortalFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL, 3, 1));
        tenantContactPortalFormFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.REQUIRED, "tenant", 4, 1));
//        tenantContactPortalForm.setFields(tenantContactPortalFormFields);

        FormSection tenantContactPortalFormSection = new FormSection("Default", 1, tenantContactPortalFormFields, false);
        tenantContactPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        tenantContactPortalForm.setSections(Collections.singletonList(tenantContactPortalFormSection));
        tenantContactPortalForm.setIsSystemForm(true);
        tenantContactPortalForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> tenantContactModuleForms = new ArrayList<>();
        tenantContactModuleForms.add(tenantContactForm);
        tenantContactModuleForms.add(tenantContactPortalForm);

        return tenantContactModuleForms;
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {

        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();

        String[] appNames=new String[]{
                FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,
                FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,
                FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,
                FacilioConstants.ApplicationLinkNames.FSM_APP,
                FacilioConstants.ApplicationLinkNames.ENERGY_APP};

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());

        for(String appName:appNames){
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,buildTenantPage(app,module,false,true));
        }

        return appNameVsPage;
    }
    private List<PagesContext> buildTenantPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception{
        String pageName, pageDisplayName;
        pageName = module.getName()+ "defaultpage";
        pageDisplayName = "Default "+module.getDisplayName()+" Page ";
        return new ModulePages().addPage(pageName, pageDisplayName, "", null, isTemplate, isDefault, true)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryfieldswidget", "Contact Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField tenantField = moduleBean.getField("tenant", moduleName);
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

        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, tenantField, 1, 1, 1,null,"Tenant");
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

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, colSpan, null,null);
    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan, FacilioField lookupField,String displayName) {
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

            if (lookupField != null) {
                summaryField.setParentLookupFieldId(lookupField.getFieldId());
            }

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "peoplenotes");

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", "peopleattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("comments", "Notes", "")
                .addWidget("commentwidget", "Notes", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
    public static void addSystemButtons() throws Exception {
        for(SystemButtonRuleContext btn : getSystemButtons()){
            SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TENANT_CONTACT, btn);
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
        portalAccessButton.setIdentifier(FacilioConstants.ContextNames.TENANTCONTACT_MODULE_PORTAL_BUTTON);
        portalAccessButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        portalAccessButton.setPermission("MANAGE_ACCESS");
        portalAccessButton.setPermissionRequired(true);


        //LIST BUTTONS
        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName("Create");
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
