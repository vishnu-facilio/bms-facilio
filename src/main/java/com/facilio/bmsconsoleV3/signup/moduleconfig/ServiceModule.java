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
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.*;

public class ServiceModule extends BaseModuleConfig{
    public ServiceModule(){
        setModuleName(FacilioConstants.ContextNames.SERVICE);
    }

    @Override
    public void addData() throws Exception {
        addSystemButtons();
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module=ModuleFactory.getServiceModule();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createServicePage(app, module, false,true));
        }
        return appNameVsPage;
    }
    public static List<PagesContext> createServicePage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        return new ModulePages()
                .addPage("itemtypesdefaultpage", "Default Service Page", "", null, isTemplate, isDefault, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicesummary", null, null)
                .addWidget("servicesummary", "Service Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
//                .addSection("widgetGroup", null, null)
//                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup())
//                .widgetDone()
//                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("vendors", "Vendors", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicevendors", null, null)
                .addWidget("servicevendors", "Vendors", PageWidget.WidgetType.SERVICE_VENDORS, "flexiblewebservicevendors_11", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone().pageDone().getCustomPages();
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        //general information

        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField duration = moduleBean.getField("duration", moduleName);
        FacilioField paymentType = moduleBean.getField("paymentType", moduleName);
        FacilioField buyingPrice = moduleBean.getField("buyingPrice", moduleName);
        FacilioField sellingPrice = moduleBean.getField("sellingPrice", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField sysModifiedBy = moduleBean.getField("sysModifiedBy", moduleName);

        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, duration, 2, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, paymentType, 2, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, buyingPrice, 2, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sellingPrice, 2, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sysCreatedTime, 3, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sysCreatedBy, 3, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sysModifiedTime, 3, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sysModifiedBy, 3, 4, 1);


        generalInformationwidgetGroup.setName("moduleDetails");
        generalInformationwidgetGroup.setDisplayName("Primary Details");
        generalInformationwidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationwidgetGroup);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("primaryDetails");
        widgetGroup.setDisplayName("Primary Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }
    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.SERVICE_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.SERVICE_ATTACHMENTS);
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT,  "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT,  "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, colSpan, null);
    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan, FacilioField lookupField) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
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

        @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> service = new ArrayList<FacilioView>();
        service.add(getAllServiceView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SERVICE);
        groupDetails.put("views", service);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllServiceView() {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("name");
        name.setModule(ModuleFactory.getServiceModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Service");
        allView.setSortFields(Arrays.asList(new SortField(name, true)));

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
        FacilioModule serviceModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE);

        FacilioForm serviceModuleForm = new FacilioForm();
        serviceModuleForm.setDisplayName("Service");
        serviceModuleForm.setName("default_service_web");
        serviceModuleForm.setModule(serviceModule);
        serviceModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        serviceModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> serviceModuleFormFields = new ArrayList<>();
        serviceModuleFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        serviceModuleFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        serviceModuleFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.NUMBER, "Duration(Hr)", FormField.Required.REQUIRED, 3, 1));
        serviceModuleFormFields.add(new FormField("paymentType", FacilioField.FieldDisplayType.SELECTBOX, "Payment Type", FormField.Required.REQUIRED, 4, 1));
        serviceModuleFormFields.add(new FormField("buyingPrice", FacilioField.FieldDisplayType.DECIMAL, "Buying Price", FormField.Required.OPTIONAL, 5, 1));
        serviceModuleFormFields.add(new FormField("sellingPrice", FacilioField.FieldDisplayType.DECIMAL, "Selling Price", FormField.Required.REQUIRED, 6, 1));

        FormSection section = new FormSection("Default", 1, serviceModuleFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        serviceModuleForm.setSections(Collections.singletonList(section));
        serviceModuleForm.setIsSystemForm(true);
        serviceModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(serviceModuleForm);
    }
    private static void addSystemButtons() throws Exception {
        SystemButtonRuleContext editService = new SystemButtonRuleContext();
        editService.setName("Edit");
        editService.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editService.setIdentifier("edit");
        editService.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SERVICE,editService);

        SystemButtonRuleContext deleteService = new SystemButtonRuleContext();
        deleteService.setName("Delete");
        deleteService.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        deleteService.setIdentifier("delete");
        deleteService.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SERVICE,deleteService);
    }

}
