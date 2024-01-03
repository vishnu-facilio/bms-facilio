package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.CalendarViewTemplatePage;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.*;

public class CalendarModule extends BaseModuleConfig{
    public CalendarModule() throws Exception{
        setModuleName(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
    }
    public void addData() throws Exception {
        addSystemButtons();
    }
    public List<FacilioForm> getModuleForms() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule calendarModule = modBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);

        FacilioForm defaultCalendarForm = new FacilioForm();
        defaultCalendarForm.setDisplayName("Standard");
        defaultCalendarForm.setModule(calendarModule);
        defaultCalendarForm.setName("default_calendar_web");
        defaultCalendarForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultCalendarForm.setShowInWeb(true);
        defaultCalendarForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP, FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING));

        List<FormField> defaultCalendarFormFields = new ArrayList<>();
        defaultCalendarFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX,"Name", FormField.Required.REQUIRED,1,1));
        defaultCalendarFormFields.add(new FormField("description",FacilioField.FieldDisplayType.TEXTAREA,"Description", FormField.Required.OPTIONAL,2,1));
        defaultCalendarFormFields.add(new FormField("calendarType", FacilioField.FieldDisplayType.SELECTBOX, "Type", FormField.Required.REQUIRED,3, 3));
        defaultCalendarFormFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.REQUIRED,"client",3, 3,false));
        defaultCalendarForm.setFields(defaultCalendarFormFields);
        List<FormSection> sectionList = new ArrayList<>();
        sectionList.add(new FormSection("General Information", 1, defaultCalendarFormFields, false));
        List<FormField> defaultCalendarSec2FormFields = new ArrayList<>();
        defaultCalendarSec2FormFields.add(new FormField("calendarEventList", FacilioField.FieldDisplayType.CALENDAR_CONFIGURATION,"Event List", FormField.Required.OPTIONAL,1,1));
        defaultCalendarForm.setFields(defaultCalendarSec2FormFields);
        sectionList.add(new FormSection("Calendar Events", 2, defaultCalendarSec2FormFields, false));
        defaultCalendarForm.setSections(sectionList);
        defaultCalendarForm.setIsSystemForm(true);
        defaultCalendarForm.setType(FacilioForm.Type.FORM);
        return Collections.singletonList(defaultCalendarForm);
    }
    public static void addSystemButtons() throws Exception {
        SystemButtonApi.addCreateButtonWithModuleDisplayName(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        SystemButtonApi.addExportAsCSV(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        SystemButtonApi.addExportAsExcel(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        SystemButtonApi.addListEditButton(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        SystemButtonApi.addListDeleteButton(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        SystemButtonApi.addBulkDeleteButton(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
    }
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> calenderViews = new ArrayList<FacilioView>();
        calenderViews.add(getAllCalendarView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP, FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING));
        groupDetails.put("views", calenderViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    public static FacilioView getAllCalendarView() throws Exception{
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Calendar");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP, FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING));
        allView.setFields(getAllViewColumns());

        return allView;
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());

        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getCalendarViewPage(app, module));
        }
        return appNameVsPage;
    }

    private List<PagesContext> getCalendarViewPage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject calendarActivityWidgetParam = new JSONObject();
        calendarActivityWidgetParam.put("activityModuleName", FacilioConstants.Calendar.CALENDAR_ACTIVITY_MODULE);
        List<PagesContext> calendarPages = new ArrayList<>();
        PagesContext calendarOperationalPage = new PagesContext("operationalCalendarViewPage", "Operational Calendar View Page", "", CalendarViewTemplatePage.getOperationalSystemTypeCriteria(), false, false, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("calendarEventView", "Calendar View", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("calendarEventViewSection", null, null)
                .addWidget("calendarEventViewSectionWidget", "Calendar Event View", PageWidget.WidgetType.CALENDAR_EVENT_VIEW, "webCalendarEventView_9_12", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("calendarAssociation", "Calendar Association", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("calendarsummaryfields", null, null)
                .addWidget("calendarsummaryfieldswidget", "General Information", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, CalendarViewTemplatePage.getSummaryWidgetDetails(FacilioConstants.Calendar.CALENDAR_MODULE_NAME, app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_8", 0, 0, null, CalendarViewTemplatePage.getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("relatedEventsList", "Related Events", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("calendareventList", null, null)
                .addWidget("calendareventlistwidget", "Events List", PageWidget.WidgetType.CALENDAR_EVENT_LIST, "webCalendarEventList_6_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("calendarhistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("calendarHistoryFields", null, null)
                .addWidget("calendarhistorywidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, calendarActivityWidgetParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
        PagesContext controlActionCalendarPage = new PagesContext("defaultCalendarViewPage", "Default Calendar View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("calendarEventView", "Calendar View", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("calendarEventViewSection", null, null)
                .addWidget("calendarEventViewSectionWidget", "Calendar Event View", PageWidget.WidgetType.CALENDAR_EVENT_VIEW, "webCalendarEventView_9_12", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("calendarAssociation", "Calendar Details", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("calendarsummaryfields", null, null)
                .addWidget("calendarsummaryfieldswidget", "General Information", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, CalendarViewTemplatePage.getSummaryWidgetDetails(FacilioConstants.Calendar.CALENDAR_MODULE_NAME, app))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("relatedEventsList", "Related Events", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("calendareventList", null, null)
                .addWidget("calendareventlistwidget", "Events List", PageWidget.WidgetType.CALENDAR_EVENT_LIST, "webCalendarEventList_6_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("calendarhistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("calendarHistoryFields", null, null)
                .addWidget("calendarhistorywidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, calendarActivityWidgetParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
        calendarPages.add(calendarOperationalPage);
        calendarPages.add(controlActionCalendarPage);
        return calendarPages;
    }
    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("name", "Name"));
        columns.add(new ViewField("calendarType", "Type"));
        columns.add(new ViewField("client", "Client"));
        return  columns;
    }

}
