package com.facilio.bmsconsole.TemplatePages;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.util.SummaryWidgetUtil;
import org.json.simple.JSONObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class WorkorderTemplatePageFactory implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.WORK_ORDER;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderModule = modBean.getModule("workorder");
        JSONObject multiresourceWidgetParam = new JSONObject();
        multiresourceWidgetParam.put("summaryWidgetName", "multiResourceWidget");
        multiresourceWidgetParam.put("module", "\""+ workOrderModule+"\"");

        JSONObject timeLogWidgetParam = new JSONObject();
        timeLogWidgetParam.put("card", "timeLog");

        org.json.simple.JSONObject historyWidgetParam = new org.json.simple.JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

        if(app.getDomainType() == AppDomain.AppDomainType.FACILIO.getIndex()) {

            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Work Order Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_7", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("multiresource", null, null)
                    .addWidget("workordermultiresource", "Space & Asset", PageWidget.WidgetType.MULTIRESOURCE, "flexiblewebmultiresource_4", 0, 0, multiresourceWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("responsibility", null, null)
                    .addWidget("workorderresponsibility", "Responsibility", PageWidget.WidgetType.RESPONSIBILITY, "flexiblewebresponsibility_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("locationdetails", null, null)
                    .addWidget("workorderlocationdetails", "Location Details", PageWidget.WidgetType.RESOURCE, "flexiblewebresource_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("timeDetails", null, null)
                    .addWidget("workordertimedetails", "Time Details", PageWidget.WidgetType.TIME_DETAILS, "flexiblewebtimedetails_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("costdetails", null, null)
                    .addWidget("workordercostdetails", "Cost", PageWidget.WidgetType.COST_DETAILS, "flexiblewebcostdetails_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("safetyplan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.SAFETY_PLAN)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("safetyplanhazard", null, null)
                    .addWidget("safetyplanhazard", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("safetyplanprecaution", null, null)
                    .addWidget("safetyplanprecaution", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("tasks", "Tasks", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("tasksSection", null, null)
                    .addWidget("tasksWidget", "Tasks", PageWidget.WidgetType.TASKS, "flexiblewebtasks_5", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("plan", "Plans", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, AccountUtil.FeatureLicense.INVENTORY)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("planSection", null, null)
                    .addWidget("plansWidget", "Plans", PageWidget.WidgetType.PLANS, "flexiblewebplans_5", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("actuals", "Actuals", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, AccountUtil.FeatureLicense.INVENTORY)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getActualWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("timelog", "Timelog And Metrics", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("timelog", null, null)
                    .addWidget("timelogandmetrics", "Time Log", PageWidget.WidgetType.STATE_TRANSITION_TIME_LOG, "flexiblewebstatetransitiontimelog_6", 0, 0, timeLogWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("failurereport", "Failure Report ", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.FAILURE_CODES)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("failurereport", null, null)
                    .addWidget("failurereport", "Failure Report", PageWidget.WidgetType.FAILURE_REPORT, "flexiblewebfailurereport_6", 0, 0, null, null)
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
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("dependentworkorders", "", null)
                    .addWidget("dependentworkorders", "Dependent Work Orders", PageWidget.WidgetType.RELATED_RECORDS, "flexiblewebrelatedrecords_5", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("history", null, null)
                    .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();
        }
        else if(app.getDomainType() == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {

            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Work Order Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_7", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("multiresource", null, null)
                    .addWidget("workordermultiresource", "Space & Asset", PageWidget.WidgetType.MULTIRESOURCE, "flexiblewebmultiresource_4", 0, 0, multiresourceWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("responsibility", null, null)
                    .addWidget("workorderresponsibility", "Responsibility", PageWidget.WidgetType.RESPONSIBILITY, "flexiblewebresponsibility_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("locationdetails", null, null)
                    .addWidget("workorderlocationdetails", "Location Details", PageWidget.WidgetType.RESOURCE, "flexiblewebresource_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("timeDetails", null, null)
                    .addWidget("workordertimedetails", "Time Details", PageWidget.WidgetType.TIME_DETAILS, "flexiblewebtimedetails_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("costdetails", null, null)
                    .addWidget("workordercostdetails", "Cost", PageWidget.WidgetType.COST_DETAILS, "flexiblewebcostdetails_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("safetyplan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.SAFETY_PLAN)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("safetyplanhazard", null, null)
                    .addWidget("safetyplanhazard", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("safetyplanprecaution", null, null)
                    .addWidget("safetyplanprecaution", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("tasks", "Tasks", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("tasksSection", null, null)
                    .addWidget("tasksWidget", "Tasks", PageWidget.WidgetType.TASKS, "flexiblewebtasks_5", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("plan", "Plans", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, AccountUtil.FeatureLicense.INVENTORY)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("planSection", null, null)
                    .addWidget("plans", "Plans", PageWidget.WidgetType.PLANS, "flexiblewebplans_5", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("actuals", "Actuals", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, AccountUtil.FeatureLicense.INVENTORY)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getActualWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("dependentworkorders", "", null)
                    .addWidget("dependentworkorders", "Dependent Work Orders", PageWidget.WidgetType.RELATED_RECORDS, "flexiblewebrelatedrecords_5", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("history", null, null)
                    .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone();

        }

        else if(app.getDomainType() == AppDomain.AppDomainType.CLIENT_PORTAL.getIndex()) {
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Work Order Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_7", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("multiresource", null, null)
                    .addWidget("workordermultiresource", "Space & Asset", PageWidget.WidgetType.MULTIRESOURCE, "flexiblewebmultiresource_4", 0, 0, multiresourceWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("responsibility", null, null)
                    .addWidget("workorderresponsibility", "Responsibility", PageWidget.WidgetType.RESPONSIBILITY, "flexiblewebresponsibility_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("locationdetails", null, null)
                    .addWidget("workorderlocationdetails", "Location Details", PageWidget.WidgetType.RESOURCE, "flexiblewebresource_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("timeDetails", null, null)
                    .addWidget("workordertimedetails", "Time Details", PageWidget.WidgetType.TIME_DETAILS, "flexiblewebtimedetails_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("costdetails", null, null)
                    .addWidget("workordercostdetails", "Cost", PageWidget.WidgetType.COST_DETAILS, "flexiblewebcostdetails_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("tasks", "Tasks", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("tasksSection", null, null)
                    .addWidget("tasksWidget", "Tasks", PageWidget.WidgetType.TASKS, "flexiblewebtasks_5", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("dependentworkorders", "", null)
                    .addWidget("dependentworkorders", "Dependent Work Orders", PageWidget.WidgetType.RELATED_RECORDS, "flexiblewebrelatedrecords_5", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("history", null, null)
                    .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();

        }
        else{
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Work Order Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_7", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("multiresource", null, null)
                    .addWidget("workordermultiresource", "Space & Asset", PageWidget.WidgetType.MULTIRESOURCE, "flexiblewebmultiresource_4", 0, 0, multiresourceWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("responsibility", null, null)
                    .addWidget("workorderresponsibility", "Responsibility", PageWidget.WidgetType.RESPONSIBILITY, "flexiblewebresponsibility_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("locationdetails", null, null)
                    .addWidget("workorderlocationdetails", "Location Details", PageWidget.WidgetType.RESOURCE, "flexiblewebresource_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("timeDetails", null, null)
                    .addWidget("workordertimedetails", "Time Details", PageWidget.WidgetType.TIME_DETAILS, "flexiblewebtimedetails_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("costdetails", null, null)
                    .addWidget("workordercostdetails", "Cost", PageWidget.WidgetType.COST_DETAILS, "flexiblewebcostdetails_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("dependentworkorders", "", null)
                    .addWidget("dependentworkorders", "Dependent Work Orders", PageWidget.WidgetType.RELATED_RECORDS, "flexiblewebrelatedrecords_5", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("history", null, null)
                    .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();

        }

        }
    private static JSONObject getSummaryWidgetDetails(String moduleName,ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField descFields = moduleBean.getField("description", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);
        FacilioField sourceTypeField=moduleBean.getField("sourceType",moduleName);
        FacilioField categoryField=moduleBean.getField("category",moduleName);
        FacilioField jobplanField=moduleBean.getField("jobPlan",moduleName);
        FacilioField typeField = moduleBean.getField("type", moduleName);
        FacilioField pmField = moduleBean.getField("pmV2", moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("primaryDetails");
        widgetGroup.setDisplayName("Primary Details");

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, descFields,1, 1, 4);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, priorityField, 2 , 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,"Maintenance",typeField,2,2,1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,sourceTypeField,2,3,1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,categoryField,2,4,1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,"PM",pmField,3,1,1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,jobplanField,3,2,1);



        widgetGroup.setColumns(4);





        FacilioField tenantFields = moduleBean.getField("tenant", moduleName);
        FacilioField clientField = moduleBean.getField("client", moduleName);
        FacilioField requesterField=moduleBean.getField("requester",moduleName);

        SummaryWidgetGroup requestingWidgetGroup = new SummaryWidgetGroup();
        requestingWidgetGroup.setName("requestingUserDetails");
        requestingWidgetGroup.setDisplayName("Requesting User Details");

        SummaryWidgetUtil. addSummaryFieldInWidgetGroup(requestingWidgetGroup,tenantFields ,1, 1, 1);
        SummaryWidgetUtil. addSummaryFieldInWidgetGroup(requestingWidgetGroup, clientField, 1 , 2, 1);
        SummaryWidgetUtil. addSummaryFieldInWidgetGroup(requestingWidgetGroup,requesterField,1,3,1);
        requestingWidgetGroup.setColumns(4);


        FacilioField serviceRequestFields = moduleBean.getField("serviceRequest", moduleName);
        FacilioField parentWOField = moduleBean.getField("parentWO", moduleName);
        FacilioField slaPolicyField=moduleBean.getField("slaPolicyId",moduleName);
        FacilioField stateFlowField=moduleBean.getField("stateFlowId",moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("createdBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField modifiedTimeField=moduleBean.getField("modifiedTime",moduleName);

        SummaryWidgetGroup otherWidgetGroup = new SummaryWidgetGroup();
        otherWidgetGroup.setName("otherDetails");
        otherWidgetGroup.setDisplayName("Other Details");

        SummaryWidgetUtil. addSummaryFieldInWidgetGroup(otherWidgetGroup,serviceRequestFields ,1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(otherWidgetGroup, parentWOField, 1 , 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(otherWidgetGroup,slaPolicyField,1,3,1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(otherWidgetGroup,stateFlowField,1,4,1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(otherWidgetGroup,sysCreatedByField,2,1,1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(otherWidgetGroup,sysCreatedTimeField,2,2,1);
        SummaryWidgetUtil. addSummaryFieldInWidgetGroup(otherWidgetGroup,modifiedTimeField,2,3,1);
        otherWidgetGroup.setColumns(4);



        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(requestingWidgetGroup);
        widgetGroupList.add(otherWidgetGroup);

        pageWidget.setDisplayName("Work Order Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }
    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {

        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "ticketnotes");
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "ticketattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
    private static JSONObject getActualWidgetGroup(boolean isMobile) throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("workorderitems", "Items", "")
                .addWidget("commentwidget", "Items", PageWidget.WidgetType.WORK_ORDER_ITEMS,"flexiblewebworkorderitems_5" , 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("workordertools", "Tools", "")
                .addWidget("attachmentwidget", "Tools", PageWidget.WidgetType.WORK_ORDER_TOOLS, "flexiblewebworkordertools_5", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("workorderservice", "Services", "")
                .addWidget("attachmentwidget", "Services", PageWidget.WidgetType.WORK_ORDER_SERVICE, "flexiblewebworkorderservice_5", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("workorderlabour", "Labors", "")
                .addWidget("attachmentwidget", "Labors", PageWidget.WidgetType.WORK_ORDER_LABOUR, "flexiblewebworkorderlabour_5", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
}
