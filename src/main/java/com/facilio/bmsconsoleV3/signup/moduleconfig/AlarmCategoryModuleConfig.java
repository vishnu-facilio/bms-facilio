package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.RemoteMonitorCommonPageConfigUtil;
import com.facilio.remotemonitoring.signup.AddSubModuleRelations;
import com.facilio.remotemonitoring.signup.AlarmCategoryModule;
import org.json.simple.JSONObject;

import java.util.*;

public class AlarmCategoryModuleConfig extends BaseModuleConfig {
    public AlarmCategoryModuleConfig(){
        setModuleName(AlarmCategoryModule.MODULE_NAME);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> views = new ArrayList<FacilioView>();
        views.add(getAllView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", AlarmCategoryModule.MODULE_NAME);
        groupDetails.put("views", views);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }


    private static FacilioView getAllView() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField sysCreatedTime = new FacilioField();
        sysCreatedTime.setName("sysCreatedTime");
        sysCreatedTime.setColumnName("SYS_CREATED_TIME");
        sysCreatedTime.setDataType(FieldType.NUMBER);
        sysCreatedTime.setModule(modBean.getModule(AlarmCategoryModule.MODULE_NAME));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Alarm Categories");
        allView.setSortFields(Arrays.asList(new SortField(sysCreatedTime, false)));
        allView.setModuleName(AlarmCategoryModule.MODULE_NAME);
        List<ViewField> viewFields = new ArrayList<>();

        viewFields.add(new ViewField("name","Name"));
        viewFields.add(new ViewField("description","Description"));
        viewFields.add(new ViewField("client","Client"));

        allView.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }


    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(AlarmCategoryModule.MODULE_NAME);

        FacilioForm form = new FacilioForm();
        form.setDisplayName("Alarm Category");
        form.setName("default_"+ module.getName() +"_web");
        form.setModule(module);
        form.setLabelPosition(FacilioForm.LabelPosition.TOP);
        form.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING));

        List<FormField> formFields = new ArrayList<>();
        formFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        formFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL,2, 1));
        formFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.REQUIRED,3, 1));

        FormSection section = new FormSection("Default", 1,formFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        form.setSections(Collections.singletonList(section));
        form.setIsSystemForm(true);
        form.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(form);
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields remoteMonitorApp = new ScopeVariableModulesFields();
        remoteMonitorApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_remotemonitor_client"));
        remoteMonitorApp.setModuleId(module.getModuleId());
        remoteMonitorApp.setFieldName("client");

        scopeConfigList = Arrays.asList(remoteMonitorApp);
        return scopeConfigList;
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        return getSystemPagesMap(getModuleName());
    }


    public static Map<String, List<PagesContext>> getSystemPagesMap(String moduleName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", AddSubModuleRelations.ALARM_CATEGORY_ACTIVITY);

        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        FacilioModule module = modBean.getModule(moduleName);

        Map<String, List<PagesContext>> pageMap = new HashMap<>();

        pageMap.put(app.getLinkName(), Arrays.asList(new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "SUMMARY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Summary Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 4, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "RELATED", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("relatedlist", "Related List", "List of related records across modules")
                .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "HISTORY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()));
        return pageMap;
    }


    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField clientField = moduleBean.getField("client",moduleName);
        FacilioField nameField = moduleBean.getField("name",moduleName);
        FacilioField descriptionField = moduleBean.getField("description",moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, clientField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, descriptionField, 2, 1, 4);


        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);



        SummaryWidgetGroup systemInfoWidgetGroup = new SummaryWidgetGroup();

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedByPeople", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedByPeople", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        addSummaryFieldInWidgetGroup(systemInfoWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInfoWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInfoWidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInfoWidgetGroup, sysModifiedTimeField, 1, 4, 1);


        systemInfoWidgetGroup.setName("systemInformation");
        systemInfoWidgetGroup.setDisplayName("System Information");
        systemInfoWidgetGroup.setColumns(4);
        widgetGroupList.add(systemInfoWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
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


    private static JSONObject getWidgetGroup() throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_27", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_27", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}