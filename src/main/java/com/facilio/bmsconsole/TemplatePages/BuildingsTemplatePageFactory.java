package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.facilio.util.SummaryWidgetUtil.addSummaryFieldInWidgetGroup;

public class BuildingsTemplatePageFactory implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.BUILDING;
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
    private static JSONObject getSummaryWidgetDetails(String moduleName,ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioField> buildingFields = moduleBean.getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(buildingFields);

        FacilioField descFields = fieldMap.get("description");
        FacilioField siteField = fieldMap.get("site");
        FacilioField managedByField = fieldMap.get("managedBy");
        FacilioField areaField=fieldMap.get("area");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");

        addSummaryFieldInWidgetGroup(widgetGroup, descFields,1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, managedByField, 2 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,siteField,2,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup,areaField,2,3,1);

        widgetGroup.setColumns(4);

        FacilioField sysCreatedByField = fieldMap.get("sysCreatedBy");
        FacilioField createdTimeField =fieldMap.get("sysCreatedTime");
        FacilioField modifiedByField =fieldMap.get("sysModifiedBy");
        FacilioField modifiedTimeField =fieldMap.get("sysModifiedTime");

        SummaryWidgetGroup systemGroup = new SummaryWidgetGroup();
        systemGroup.setName("systemInformation");
        systemGroup.setDisplayName("System Information");

        addSummaryFieldInWidgetGroup(systemGroup, sysCreatedByField,1, 1, 1);
        addSummaryFieldInWidgetGroup(systemGroup, createdTimeField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(systemGroup,modifiedByField,1,3,1);
        addSummaryFieldInWidgetGroup(systemGroup,modifiedTimeField,1,4,1);

        systemGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(systemGroup);

        pageWidget.setDisplayName("Building details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule buildingModule = modBean.getModule("building");


        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.BUILDING_ACTIVITY);

        JSONObject floorParam = new JSONObject();
        floorParam.put("moduleName","floor");
        floorParam.put("parentName","building");

        JSONObject spaceParam = new JSONObject();
        spaceParam.put("moduleName","space");
        spaceParam.put("parentName","building");

        JSONObject notesModuleParam = new JSONObject();
        notesModuleParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME,"basespacenotes");

        JSONObject attachmentModuleParam = new JSONObject();
        attachmentModuleParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME,"basespaceattachments");

        if(app.getLinkName().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.FSM_APP)){
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Building details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("buildingInsights","",null)
                    .addWidget("buildingLocationWidgets","Location Details", PageWidget.WidgetType.SPACE_LOCATION,"webSpaceLocation_4_3",0,0,null,null)
                    .widgetDone()
                    .addWidget("buildingInsights","Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_6",3,0,spaceParam,null)
                    .widgetDone()
                    .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_4_3",9,0,null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("floorsAndSpaces","Floors and Spaces", PageTabContext.TabType.SIMPLE,true,null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("floors","",null)
                    .addWidget("floors","Floors", PageWidget.WidgetType.FLOORS,"flexibleWebFloors_7",0,0,floorParam,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("spaces","",null)
                    .addWidget("spaces","Spaces", PageWidget.WidgetType.SPACES,"flexibleWebSpaces_7",0,0,spaceParam,null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("buildingRelatedlist", "Related List", "List of related records across modules")
                    .addWidget("buildingRelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
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
                    .addWidget("buildingActivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();
        }
        else if(app.getLinkName().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.ENERGY_APP)){
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Building details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("buildingInsights","",null)
                    .addWidget("buildingLocationWidgets","Location Details", PageWidget.WidgetType.SPACE_LOCATION,"webSpaceLocation_4_3",0,0,null,null)
                    .widgetDone()
                    .addWidget("buildingInsights","Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_6",3,0,spaceParam,null)
                    .widgetDone()
                    .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_4_3",9,0,null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("floorsAndSpaces","Floors and Spaces", PageTabContext.TabType.SIMPLE,true,null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("floors","",null)
                    .addWidget("floors","Floors", PageWidget.WidgetType.FLOORS,"flexibleWebFloors_7",0,0,floorParam,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("spaces","",null)
                    .addWidget("spaces","Spaces", PageWidget.WidgetType.SPACES,"flexibleWebSpaces_7",0,0,spaceParam,null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("buildingRelatedlist", "Related List", "List of related records across modules")
                    .addWidget("buildingRelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("meters", "Meters", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("meterRelations", "", "")
                    .addWidget("meterRelationsWidget", "Relationships", PageWidget.WidgetType.METER_RELATIONSHIPS,"flexiblewebmeterrelationshipwidget_10", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("activity", null, null)
                    .addWidget("buildingActivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();
        }
        else if(app.getLinkName().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)){
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Building details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("buildingInsights","",null)
                    .addWidget("buildingLocationWidgets","Location Details", PageWidget.WidgetType.SPACE_LOCATION,"webSpaceLocation_4_3",0,0,null,null)
                    .widgetDone()
                    .addWidget("buildingInsights","Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_6",3,0,spaceParam,null)
                    .widgetDone()
                    .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_4_3",9,0,null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("floorsAndSpaces","Floors and Spaces", PageTabContext.TabType.SIMPLE,true,null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("floors","",null)
                    .addWidget("floors","Floors", PageWidget.WidgetType.FLOORS,"flexibleWebFloors_7",0,0,floorParam,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("spaces","",null)
                    .addWidget("spaces","Spaces", PageWidget.WidgetType.SPACES,"flexibleWebSpaces_7",0,0,spaceParam,null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", null, null)
                    .addWidget("classification", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("buildingRelatedlist", "Related List", "List of related records across modules")
                    .addWidget("buildingRelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
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
                    .addWidget("buildingActivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();
        }
        else{
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Building details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("buildingInsights","",null)
                    .addWidget("buildingLocationWidgets","Location Details", PageWidget.WidgetType.SPACE_LOCATION,"webSpaceLocation_4_3",0,0,null,null)
                    .widgetDone()
                    .addWidget("buildingInsights","Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_6",3,0,spaceParam,null)
                    .widgetDone()
                    .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_4_3",9,0,null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("floorsAndSpaces","Floors and Spaces", PageTabContext.TabType.SIMPLE,true,null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("floors","",null)
                    .addWidget("floors","Floors", PageWidget.WidgetType.FLOORS,"flexibleWebFloors_7",0,0,floorParam,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("spaces","",null)
                    .addWidget("spaces","Spaces", PageWidget.WidgetType.SPACES,"flexibleWebSpaces_7",0,0,spaceParam,null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("plannedmaintenance", "", null)
                    .addWidget("buildingplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("unplannedmaintenance", "", null)
                    .addWidget("buildingunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("buildingworkorderdetails", null, null)
                    .addWidget("buildingworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("recentlyclosedppm", null, null)
                    .addWidget("buildingrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("safetyPlan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("hazards", "", null)
                    .addWidget("buildinghazards", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("precautions", "", null)
                    .addWidget("buildingPrecautions", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", null, null)
                    .addWidget("classification", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("buildingRelatedlist", "Related List", "List of related records across modules")
                    .addWidget("buildingRelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
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
                    .addWidget("buildingActivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();
        }
    }

}
