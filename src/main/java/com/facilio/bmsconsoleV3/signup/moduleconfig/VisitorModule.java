package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.VisitorManagementModulePageUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.util.AddModuleViewsAndGroups;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class VisitorModule extends BaseModuleConfig{
    public VisitorModule(){
        setModuleName(FacilioConstants.ContextNames.VISITOR);
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
        ArrayList<FacilioView> visitor = new ArrayList<FacilioView>();
        visitor.add(getAllVisitorsView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VISITOR);
        groupDetails.put("views", visitor);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllVisitorsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Visitors");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule visitorModule = modBean.getModule(FacilioConstants.ContextNames.VISITOR);

        FacilioForm visitorKioskForm = new FacilioForm();
        visitorKioskForm.setDisplayName("VISITOR");
        visitorKioskForm.setName("default_visitor_web");
        visitorKioskForm.setModule(visitorModule);
        visitorKioskForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        visitorKioskForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> visitorKioskFormFields = new ArrayList<>();
        visitorKioskFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.NUMBER, "Enter your mobile number", FormField.Required.REQUIRED, 1, 1));
        visitorKioskFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Hi,What is your full name?", FormField.Required.REQUIRED, 1, 1));
        visitorKioskFormFields.add(new FormField("email", FacilioField.FieldDisplayType.EMAIL, "What is your email id?", FormField.Required.OPTIONAL, 2, 1));

        FormSection visitorKioskFormSection = new FormSection("Default", 1, visitorKioskFormFields, false);
        visitorKioskFormSection.setSectionType(FormSection.SectionType.FIELDS);
        visitorKioskForm.setSections(Collections.singletonList(visitorKioskFormSection));
        visitorKioskForm.setIsSystemForm(true);
        visitorKioskForm.setType(FacilioForm.Type.FORM);

        FacilioForm visitorForm = new FacilioForm();
        visitorForm.setDisplayName("VISITOR");
        visitorForm.setName("portal_visitor_web");
        visitorForm.setModule(visitorModule);
        visitorForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        visitorForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> visitorFormFields = new ArrayList<>();
        visitorFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.IMAGE,"Visitor Photo", FormField.Required.OPTIONAL,1,1));
        visitorFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        visitorFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.REQUIRED, 3, 1));
        visitorFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.REQUIRED, 4, 1));
        visitorFormFields.add(new FormField("location", FacilioField.FieldDisplayType.ADDRESS, "Location", FormField.Required.OPTIONAL, 5, 1));

        FormSection visitorFormSection = new FormSection("Default", 1, visitorFormFields, false);
        visitorFormSection.setSectionType(FormSection.SectionType.FIELDS);
        visitorForm.setSections(Collections.singletonList(visitorFormSection));
        visitorForm.setIsSystemForm(true);
        visitorForm.setType(FacilioForm.Type.FORM);


        FacilioForm portalVisitorForm = new FacilioForm();
        portalVisitorForm.setDisplayName("VISITOR");
        portalVisitorForm.setName("default_portal_visitor_web");
        portalVisitorForm.setModule(visitorModule);
        portalVisitorForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        portalVisitorForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> portalVisitorFormFields = new ArrayList<>();
        portalVisitorFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.IMAGE,"Visitor Photo", FormField.Required.OPTIONAL,1,1));
        portalVisitorFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        portalVisitorFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.REQUIRED, 3, 1));
        portalVisitorFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.REQUIRED, 4, 1));
        portalVisitorFormFields.add(new FormField("location", FacilioField.FieldDisplayType.ADDRESS, "Location", FormField.Required.OPTIONAL, 5, 1));

        FormSection portalVisitorFormSection = new FormSection("Default", 1, portalVisitorFormFields, false);
        portalVisitorFormSection.setSectionType(FormSection.SectionType.FIELDS);
        portalVisitorForm.setSections(Collections.singletonList(portalVisitorFormSection));
        portalVisitorForm.setIsSystemForm(true);
        portalVisitorForm.setType(FacilioForm.Type.FORM);


        List<FacilioForm> visitorModuleForms = new ArrayList<>();
        visitorModuleForms.add(visitorKioskForm);
        visitorModuleForms.add(visitorForm);
        visitorModuleForms.add(portalVisitorForm);

        return visitorModuleForms;
    }
    public static void addSystemButton(String moduleName) throws Exception{

        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName("New Visitors");
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
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {

        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String>  appNameList=new ArrayList<>();
        appNameList.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
        for(String appName:appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
                appNameVsPage.put(appName, buildVisitorPage(app, module, false, true));
        }
        return appNameVsPage;
    }
    private List<PagesContext> buildVisitorPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName() + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";
        JSONObject viewParam = new JSONObject();
        viewParam.put("viewName", "VisitorLogListView");
        viewParam.put("viewModuleName","visitorlog");
        return new ModulePages()
                .addPage(pageName, pageDisplayName, "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("visitorSummaryDetails", "Visitor Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, VisitorManagementModulePageUtil.getSummaryWidgetDetailsForVisitorModule(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("visitsDetails", null, null)
                .addWidget("visitsDetails", "Visits", PageWidget.WidgetType.VISITOR_LIST_WIDGET, "flexiblewebvisitorwidget_6", 0, 0, viewParam, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("visitorcommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, VisitorManagementModulePageUtil.getWidgetGroup(false,module.getName()))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }
}
