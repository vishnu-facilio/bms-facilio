package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.facilio.util.SummaryWidgetUtil.addSummaryFieldInWidgetGroup;

public class SitesTemplatePageFactory implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.SITE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule siteModule = modBean.getModule("site");

        JSONObject locationWidgetParam = new JSONObject();
        locationWidgetParam.put("fieldName","location");

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SITE_ACTIVITY);

        JSONObject buildingsParam = new JSONObject();
        buildingsParam.put("moduleName","building");
        buildingsParam.put("parentName","site");


        JSONObject spaceParam = new JSONObject();
        spaceParam.put("moduleName","space");
        spaceParam.put("parentName","site");

        JSONObject notesModuleParam = new JSONObject();
        notesModuleParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME,"basespacenotes");

        JSONObject attachmentModuleParam = new JSONObject();
        attachmentModuleParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME,"basespaceattachments");

        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Site details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("siteInsights","",null)
                .addWidget("siteLocationWidgets","Location Details", PageWidget.WidgetType.SPACE_LOCATION,"webSpaceLocation_4_3",0,0,locationWidgetParam,null)
                .widgetDone()
                .addWidget("siteInsights","Site Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_6",3,0,spaceParam,null)
                .widgetDone()
                .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_4_3",9,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("weatherCast","",null)
                .addWidget("weatherCard","Weather Card", PageWidget.WidgetType.WEATHER_CARD,"webWeatherCard_6_4",0,0,null,null)
                .widgetDone()
                .addWidget("depreciationAnalysis","Depreciation Analysis", PageWidget.WidgetType.DEPRECIATION_ANALYSIS,"webDepreciationAnalysis_6_8",4,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("HourlyForecast","",null)
                .addWidget("hourlyForecastWidget","Hourly forecast", PageWidget.WidgetType.HOURLY_FORECAST,"flexibleWebHourlyForecast_6",0,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("buildingsAndSpaces","Buildings and Spaces", PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("buildings","",null)
                .addWidget("buildings","Buildings", PageWidget.WidgetType.BUILDINGS,"flexibleWebBuildings_6",0,0,buildingsParam,null)
                .widgetDone()
                .sectionDone()
                .addSection("spaces","",null)
                .addWidget("spaces","Spaces", PageWidget.WidgetType.SPACES,"flexibleWebSpaces_7",0,0,spaceParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("sitereadings", null, null)
                .addWidget("sitereadings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("sitecommand", null, null)
                .addWidget("sitecommand", "Commands", PageWidget.WidgetType.COMMANDS_WIDGET, "flexiblewebcommandswidget_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("siteRelatedReadings", null, null)
                .addWidget("relatedReadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS, "flexiblewebrelatedreadings_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("plannedmaintenance", "", null)
                .addWidget("siteplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("unplannedmaintenance", "", null)
                .addWidget("siteunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                .addSection("siteworkorderdetails", null, null)
                .addWidget("siteworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_3", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("recentlyclosedppm", null, null)
                .addWidget("siterecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_4", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("safetyPlan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("hazards", "", null)
                .addWidget("sitehazards", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .addSection("precautions", "", null)
                .addWidget("sitePrecautions", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.CLASSIFICATION)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("classification", null, null)
                .addWidget("classification", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("siteRelatedlist", "Related List", "List of related records across modules")
                .addWidget("siterelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("siteactivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

    }

    private JSONObject getWidgetGroup(boolean isMobile, JSONObject notesModuleParam, JSONObject attachmentModuleParam) throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesModuleParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentModuleParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField descFields = moduleBean.getField("description", moduleName);
        FacilioField managedByField = moduleBean.getField("managedBy", moduleName);
        FacilioField siteTypeField =moduleBean.getField("siteType",moduleName);
        FacilioField grossFloorAreaField =moduleBean.getField("grossFloorArea",moduleName);
        FacilioField areaField=moduleBean.getField("area",moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("primaryDetails");

        addSummaryFieldInWidgetGroup(widgetGroup, descFields,1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, managedByField, 2 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,siteTypeField,2,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup,grossFloorAreaField,2,3,1);
        addSummaryFieldInWidgetGroup(widgetGroup,areaField,2,4,1);

        widgetGroup.setColumns(4);

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField createdTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField modifiedByField =moduleBean.getField("sysModifiedBy",moduleName);
        FacilioField modifiedTimeField =moduleBean.getField("sysModifiedTime",moduleName);

        SummaryWidgetGroup systemGroup = new SummaryWidgetGroup();
        systemGroup.setName("systemDetails");
        systemGroup.setDisplayName("System Information");

        addSummaryFieldInWidgetGroup(systemGroup, sysCreatedByField,1, 1, 1);
        addSummaryFieldInWidgetGroup(systemGroup, createdTimeField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(systemGroup,modifiedByField,1,3,1);
        addSummaryFieldInWidgetGroup(systemGroup,modifiedTimeField,1,4,1);

        systemGroup.setColumns(4);
        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(systemGroup);

        pageWidget.setDisplayName("Site details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

}
