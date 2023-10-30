package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.calendar.V3CalendarContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVProjectContext;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarViewTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.Calendar.CALENDAR_MODULE_NAME;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject calendarActivityWidgetParam = new JSONObject();
        calendarActivityWidgetParam.put("activityModuleName", FacilioConstants.Calendar.CALENDAR_ACTIVITY_MODULE);
        return new PagesContext(null, null, "", null, true, false, false)
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
                .addWidget("calendarsummaryfieldswidget", "General Information", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.Calendar.CALENDAR_MODULE_NAME, app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_8", 0, 0, null, getWidgetGroup())
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
    }
    public static JSONObject getSummaryWidgetDetails(String moduleName ,ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField descriptionField = moduleBean.getField("description", moduleName);
        FacilioField calendarTypeField = moduleBean.getField("calendarType", moduleName);
        FacilioField clientField = moduleBean.getField("client", moduleName);
        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, calendarTypeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, clientField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, descriptionField, 2, 1, 4);
        widgetGroup.setName("moduleDetails");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);
        return FieldUtil.getAsJSON(pageWidget);
    }
    public static Criteria getOperationalSystemTypeCriteria() {
        Criteria criteria = new Criteria();
        Condition calendarTypeCondition = new Condition();
        calendarTypeCondition.setFieldName("calendarType");
        calendarTypeCondition.setColumnName("Calendar.CALENDAR_TYPE");
        calendarTypeCondition.setOperator(EnumOperators.IS);
        calendarTypeCondition.setValue(String.valueOf(V3CalendarContext.CalendarTypeEnum.OPERATIONAL.getVal()));
        calendarTypeCondition.setModuleName(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        criteria.addAndCondition(calendarTypeCondition);
        return criteria;
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
    public static JSONObject getWidgetGroup() throws Exception {
        JSONObject assetTabWidgetParam = new JSONObject();
        assetTabWidgetParam.put("moduleName", "asset");

        JSONObject siteTabWidgetParam = new JSONObject();
        siteTabWidgetParam.put("moduleName", "site");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("asset", "Asset", "")
                .addWidget("assetwidget", "Asset", PageWidget.WidgetType.CALENDAR_ASSOCIATION_LIST, "flexiblewebcalendarassociationlist_8", 0, 0, assetTabWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("sites", "Sites", "")
                .addWidget("sitewidget", "Sites", PageWidget.WidgetType.CALENDAR_ASSOCIATION_LIST, "flexiblewebcalendarassociationlist_8", 0, 0, siteTabWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}