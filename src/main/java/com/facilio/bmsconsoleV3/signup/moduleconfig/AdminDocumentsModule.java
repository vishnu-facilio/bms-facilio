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
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.util.SummaryWidgetUtil.addSummaryFieldInWidgetGroup;

public class AdminDocumentsModule extends BaseModuleConfig{
    public AdminDocumentsModule(){
        setModuleName(FacilioConstants.ContextNames.ADMIN_DOCUMENTS);
    }


    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception{
        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ADMIN_DOCUMENTS);
        for (String appName : appLinkNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createAdminDocsDefaultPage(app, module));
        }
        return appNameVsPage;
    }

    private List<PagesContext> createAdminDocsDefaultPage(ApplicationContext app, FacilioModule module) throws Exception {
        return new ModulePages()
                .addPage("adminDocumentsDefaultPage","Default Admin Documents page","",null, false, true,true)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                        .addTab("summary","Summary", PageTabContext.TabType.SIMPLE,true,null)
                            .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                                .addSection("summary","",null)
                                    .addWidget("summaryFieldsWidget", "Admin Document Details",PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_6", 0, 0, null,getSummaryWidgetDetails(module.getName(),app))
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
        addListButtons();
    }

    private void addListButtons() throws Exception {
        SystemButtonApi.addListEditButton(FacilioConstants.ContextNames.ADMIN_DOCUMENTS);
        SystemButtonApi.addListDeleteButton(FacilioConstants.ContextNames.ADMIN_DOCUMENTS);
    }

    public static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(moduleBean.getAllFields(moduleName));

        FacilioField categoryField = fieldMap.get("category");
        FacilioField fileField = fieldMap.get("file");
        FacilioField sysCreatedByField = fieldMap.get("sysCreatedBy");
        FacilioField createdTimeField = fieldMap.get("sysCreatedTime");
        FacilioField modifiedByField = fieldMap.get("sysModifiedBy");
        FacilioField modifiedTimeField = fieldMap.get("sysModifiedTime");
        FacilioField audienceField = fieldMap.get("audience");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("documentDetails");
        widgetGroup.setDisplayName("Document Details");

        addSummaryFieldInWidgetGroup(widgetGroup, categoryField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,fileField,1,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField,1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,createdTimeField,1,4,1);
        addSummaryFieldInWidgetGroup(widgetGroup,modifiedByField,2,1,1);
        addSummaryFieldInWidgetGroup(widgetGroup,modifiedTimeField,2,2,1);
        widgetGroup.setColumns(4);

        SummaryWidgetGroup audienceGroup = new SummaryWidgetGroup();
        audienceGroup.setName("accessibility");
        audienceGroup.setDisplayName("Accessibility");

        addSummaryFieldInWidgetGroup(audienceGroup, audienceField,1, 1, 1);
        audienceGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(audienceGroup);

        pageWidget.setDisplayName("Admin Documents");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }



    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> adminDocuments = new ArrayList<FacilioView>();
        adminDocuments.add(getAllAdminDocumentsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ADMIN_DOCUMENTS);
        groupDetails.put("views", adminDocuments);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAdminDocumentsView() {

        FacilioModule module = ModuleFactory.getAdminDocumentsModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Admin Documents");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule adminDocumentsModule = modBean.getModule(FacilioConstants.ContextNames.ADMIN_DOCUMENTS);

        FacilioForm adminDocumentsForm = new FacilioForm();
        adminDocumentsForm.setDisplayName("Admin Documents");
        adminDocumentsForm.setName("default_"+ FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS +"_web");
        adminDocumentsForm.setModule(adminDocumentsModule);
        adminDocumentsForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        adminDocumentsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> adminDocumentsFormFields = new ArrayList<>();
        adminDocumentsFormFields.add(new FormField("title", FacilioField.FieldDisplayType.TEXTBOX, "Title", FormField.Required.REQUIRED, 1, 1));
        adminDocumentsFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        adminDocumentsFormFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL, 3, 1));
        adminDocumentsFormFields.add(new FormField("file", FacilioField.FieldDisplayType.FILE, "File", FormField.Required.REQUIRED, 4, 1));
        FormField field = new FormField("audience", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Audience", FormField.Required.REQUIRED, "audience",5, 1);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        adminDocumentsFormFields.add(field);
//        adminDocumentsForm.setFields(adminDocumentsFormFields);

        FormSection section = new FormSection("Default", 1, adminDocumentsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        adminDocumentsForm.setSections(Collections.singletonList(section));
        adminDocumentsForm.setIsSystemForm(true);
        adminDocumentsForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(adminDocumentsForm);
    }
}
