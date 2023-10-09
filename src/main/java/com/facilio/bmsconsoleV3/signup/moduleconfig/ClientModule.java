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

import java.util.*;

public class ClientModule extends BaseModuleConfig{

    public ClientModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.CLIENT);
    }
    @Override
    public void addData() throws Exception {
        addSystemButton();
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

        ArrayList<FacilioView> rmClient = new ArrayList<>();
        rmClient.add(getRemoteMonitoringAllClientView().setOrder(order++));

        Map<String, Object> rmGroupDetails;
        rmGroupDetails = new HashMap<>();
        rmGroupDetails.put("name", "systemviews");
        rmGroupDetails.put("displayName", "System Views");
        rmGroupDetails.put("moduleName", FacilioConstants.ContextNames.CLIENT);
        rmGroupDetails.put("views", rmClient);
        groupVsViews.add(rmGroupDetails);

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
            if(app!=null) {
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
        List<String> moduleToRemove=new ArrayList<>();
        moduleToRemove.add(FacilioConstants.ContextNames.WORK_ORDER);
        moduleToRemove.add(FacilioConstants.ContextNames.CLIENT_CONTACT);
        moduleToRemove.add(FacilioConstants.ContextNames.SITE);
        moduleToRemove.add("contact");

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

    private static FacilioView getAllClientView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Clients");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        return allView;
    }

    private static FacilioView getRemoteMonitoringAllClientView() {

        FacilioView allView = new FacilioView();
        allView.setName("all-remote-monitoring-client");
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
        clientForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

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
    private static void addSystemButton() throws Exception{
        SystemButtonRuleContext editButton = new SystemButtonRuleContext();
        editButton.setName("Edit");
        editButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editButton.setIdentifier(FacilioConstants.ContextNames.CLIENT_MODULE_EDIT_BUTTON);
        editButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editButton.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.CLIENT,editButton);

    }
}
