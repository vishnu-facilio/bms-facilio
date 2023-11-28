package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ClientModulePageUtil;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.remotemonitoring.signup.*;

import java.util.*;

public class ClientModule extends BaseModuleConfig{

    public ClientModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.CLIENT);
    }
    @Override
    public void addData() throws Exception {
        addSystemButton(getModuleName());
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> clientContact = new ArrayList<FacilioView>();
        clientContact.add(getAllClientView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CLIENT);
        groupDetails.put("views", clientContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNameList = new ArrayList<>();
        appNameList.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        appNameList.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CLIENT);
        for (String appName : appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            if(appName.equals(FacilioConstants.ApplicationLinkNames.ENERGY_APP) || appName.equals(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING) || appName.equals(FacilioConstants.ApplicationLinkNames.FSM_APP)) {
                appNameVsPage.put(appName, buildClientPageWithoutWorkorderWidget(app, module, false, true));
            }else {
                appNameVsPage.put(appName, buildClientPage(app, module, false, true));
            }
        }
        return appNameVsPage;
    }
    private List<PagesContext> buildClientPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName() + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule clientModule=modBean.getModule(getModuleName());
        List<String> moduleToRemove=getModuleTobeRemovedInRelated(app.getLinkName());

        return  new ModulePages()
                .addPage(pageName, pageDisplayName,"", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryFields", null, null)
                .addWidget("clientDetails", "Client Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, ClientModulePageUtil.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("workorderFields", null, null)
                .addWidget("workorderDetails", "Workorders", PageWidget.WidgetType.RELATED_LIST, "flexiblewebrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.getSingleRelatedListForModule(clientModule,"workorder","client"))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("commentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, ClientModulePageUtil.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("sites", "Sites", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("siteFields", null, null)
                .addWidget("siteDetails", "Sites", PageWidget.WidgetType.SITE_LIST_WIDGET, "flexiblewebsitelistwidget_6", 0, 0, null, RelatedListWidgetUtil.getSingleRelatedListForModule(clientModule,"site","client"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("clientContacts", "Contacts", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("clientconatactfields", null, null)
                .addWidget("clientcontactdetails", "Contacts", PageWidget.WidgetType.CLIENT_CONTACT_LIST_WIDGET, "flexibleclientcontactlistwidget_6", 0, 0, null, RelatedListWidgetUtil.getSingleRelatedListForModule(clientModule,"clientcontact","client"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("clientRelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("clientBulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("clientRelatedlist", "Related List", "List of related records across modules")
                .addWidget("clientBulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module,false,null,moduleToRemove))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }
    private List<PagesContext> buildClientPageWithoutWorkorderWidget(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName() + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule clientModule=modBean.getModule(getModuleName());

        List<String> moduleToRemove=getModuleTobeRemovedInRelated(app.getLinkName());

        return  new ModulePages()
                .addPage(pageName, pageDisplayName,"", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryFields", null, null)
                .addWidget("clientDetails", "Client Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, ClientModulePageUtil.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("commentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, ClientModulePageUtil.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("sites", "Sites", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("siteFields", null, null)
                .addWidget("siteDetails", "Sites", PageWidget.WidgetType.SITE_LIST_WIDGET, "flexiblewebsitelistwidget_6", 0, 0, null, RelatedListWidgetUtil.getSingleRelatedListForModule(clientModule,"site","client"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("clientContacts", "Contacts", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("clientconatactfields", null, null)
                .addWidget("clientcontactdetails", "Contacts", PageWidget.WidgetType.CLIENT_CONTACT_LIST_WIDGET, "flexibleclientcontactlistwidget_6", 0, 0, null, RelatedListWidgetUtil.getSingleRelatedListForModule(clientModule,"clientcontact","client"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("clientRelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("clientBulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("clientRelatedlist", "Related List", "List of related records across modules")
                .addWidget("clientBulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module,false,null,moduleToRemove))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }
    public static List<String> getModuleTobeRemovedInRelated(String appName){
        List<String> moduleTobeRemovedInRelated=new ArrayList<>();

        if(appName.equals(FacilioConstants.ApplicationLinkNames.FSM_APP)){  // serive order and service Appointment

            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.WORK_ORDER);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.CLIENT_CONTACT);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.SITE);
            moduleTobeRemovedInRelated.add(AlarmCategoryModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(AlarmDefinitionModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(AlarmDefinitionMappingModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(RawAlarmModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(FlaggedEventRuleModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(FlaggedEventModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(AlarmFilterRuleModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(FilteredAlarmModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(AlarmDefinitionTaggingModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.CONTACT);
            moduleTobeRemovedInRelated.add(AlarmAssetTaggingModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);

        }else if(appName.equals(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)){  // alarmCategory alarmDefinition alarmDefinitionMapping rawAlarm flaggedEventRule flaggedEvent alarmFilterRule filteredAlarm alarmDefinitionTagging alarmAssetTagging

            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.WORK_ORDER);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.CLIENT_CONTACT);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.SITE);
            moduleTobeRemovedInRelated.add(AlarmDefinitionModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.SERVICE_ORDER);
            moduleTobeRemovedInRelated.add(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.CONTACT);
            moduleTobeRemovedInRelated.add(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);

        }else{          // calender

            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.WORK_ORDER);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.CLIENT_CONTACT);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.SITE);
            moduleTobeRemovedInRelated.add(AlarmCategoryModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(AlarmDefinitionModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(AlarmDefinitionMappingModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(RawAlarmModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(FlaggedEventRuleModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(FlaggedEventModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(AlarmFilterRuleModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(FilteredAlarmModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(AlarmDefinitionTaggingModule.MODULE_NAME);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.SERVICE_ORDER);
            moduleTobeRemovedInRelated.add(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
            moduleTobeRemovedInRelated.add(FacilioConstants.ContextNames.CONTACT);
            moduleTobeRemovedInRelated.add(AlarmAssetTaggingModule.MODULE_NAME);

        }
        return moduleTobeRemovedInRelated;
    }

    private static FacilioView getAllClientView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Clients");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP,FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return allView;
    }

    private static FacilioView getRemoteMonitoringAllClientView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Clients");
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING));
        allView.setModuleName(FacilioConstants.ContextNames.CLIENT);
        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule clientModule = modBean.getModule(FacilioConstants.ContextNames.CLIENT);

        FacilioForm clientForm = new FacilioForm();
        clientForm.setDisplayName("NEW CLIENT");
        clientForm.setName("default_client_web");
        clientForm.setModule(clientModule);
        clientForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        clientForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> clientFormFields = new ArrayList<>();
        clientFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        clientFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        clientFormFields.add(new FormField("website", FacilioField.FieldDisplayType.TEXTBOX, "Website", FormField.Required.OPTIONAL, 3, 1));
        clientFormFields.add(new FormField("primaryContactName", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Name", FormField.Required.REQUIRED, 4, 1));
        clientFormFields.add(new FormField("primaryContactEmail", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact E-mail", FormField.Required.OPTIONAL, 5, 1));
        clientFormFields.add(new FormField("primaryContactPhone", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Phone", FormField.Required.REQUIRED, 6, 1));
        clientFormFields.add(new FormField("address", FacilioField.FieldDisplayType.ADDRESS, "Address", FormField.Required.OPTIONAL, 7, 1));
//        clientForm.setFields(clientFormFields);

        FormSection Section = new FormSection("Default", 1, clientFormFields, false);
        Section.setSectionType(FormSection.SectionType.FIELDS);
        clientForm.setSections(Collections.singletonList(Section));
        clientForm.setIsSystemForm(true);
        clientForm.setType(FacilioForm.Type.FORM);


        //Client form remote monitoring app
        FacilioForm RMClientForm = new FacilioForm();
        RMClientForm.setDisplayName("NEW CLIENT");
        RMClientForm.setName("rm_client_web");
        RMClientForm.setModule(clientModule);
        RMClientForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        RMClientForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING));

        List<FormField> RMClientFormFields = new ArrayList<>();
        RMClientFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        RMClientFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        RMClientFormFields.add(new FormField("website", FacilioField.FieldDisplayType.TEXTBOX, "Website", FormField.Required.OPTIONAL, 3, 1));
        RMClientFormFields.add(new FormField("primaryContactName", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Name", FormField.Required.REQUIRED, 4, 1));
        RMClientFormFields.add(new FormField("primaryContactEmail", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact E-mail", FormField.Required.OPTIONAL, 5, 1));
        RMClientFormFields.add(new FormField("primaryContactPhone", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Phone", FormField.Required.REQUIRED, 6, 1));
        RMClientFormFields.add(new FormField("address", FacilioField.FieldDisplayType.ADDRESS, "Address", FormField.Required.OPTIONAL, 7, 1));

        FormSection rmClientFormSection = new FormSection("Default", 1, clientFormFields, false);
        rmClientFormSection.setSectionType(FormSection.SectionType.FIELDS);
        RMClientForm.setSections(Collections.singletonList(Section));
        RMClientForm.setIsSystemForm(true);
        RMClientForm.setType(FacilioForm.Type.FORM);

        return Arrays.asList(clientForm,RMClientForm);
    }
    private static void addSystemButton(String moduleName) throws Exception{

        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName("New Clients");
        createButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButton.setIdentifier("create");
        createButton.setPermissionRequired(true);
        createButton.setPermission("CREATE");
        SystemButtonApi.addSystemButton(moduleName,createButton);

        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(moduleName,listEditButton);

        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        SystemButtonApi.addSystemButton(moduleName,listDeleteButton);

        SystemButtonRuleContext bulkDeleteButton = new SystemButtonRuleContext();
        bulkDeleteButton.setName("Delete");
        bulkDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        bulkDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkDeleteButton.setIdentifier("delete_bulk");
        bulkDeleteButton.setPermissionRequired(true);
        bulkDeleteButton.setPermission("DELETE");
        SystemButtonApi.addSystemButton(moduleName,bulkDeleteButton);

        SystemButtonRuleContext exportAsCSVButton = new SystemButtonRuleContext();
        exportAsCSVButton.setName("Export As CSV");
        exportAsCSVButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsCSVButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCSVButton.setIdentifier("export_as_csv");
        exportAsCSVButton.setPermissionRequired(true);
        exportAsCSVButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(moduleName,exportAsCSVButton);

        SystemButtonRuleContext exportAsExcelButton = new SystemButtonRuleContext();
        exportAsExcelButton.setName("Export As Excel");
        exportAsExcelButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsExcelButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButton.setIdentifier("export_as_excel");
        exportAsExcelButton.setPermissionRequired(true);
        exportAsExcelButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(moduleName,exportAsExcelButton);

        SystemButtonRuleContext summaryEditButton = new SystemButtonRuleContext();
        summaryEditButton.setName("Edit");
        summaryEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        summaryEditButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        summaryEditButton.setIdentifier("edit_summary");
        summaryEditButton.setPermissionRequired(true);
        summaryEditButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(moduleName,summaryEditButton);
    }
}
