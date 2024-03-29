package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.dto.AppDomain;
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
import java.util.Arrays;
import java.util.List;

public class CustomModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return "custom";
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception{

        JSONObject classificationWidgetParam = new JSONObject();
        classificationWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SITE_ACTIVITY);


        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);


        JSONObject failureReportWidgetParam = new JSONObject();
        failureReportWidgetParam.put("card", "failurereports");


        JSONObject timeLogWidgetParam = new JSONObject();
        timeLogWidgetParam.put("card", "timeLog");

        if(app.getDomainType() == AppDomain.AppDomainType.FACILIO.getIndex()) {
            return  new PagesContext(null, null,"", null, true, false, false)
                    .addWebLayout()
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null, null)
                    .addWidget("summaryFieldsWidget", "Summary", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("specification", "Specification", PageTabContext.TabType.SIMPLE,true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", null, null)
                    .addWidget("classificationwidget", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_6",  0, 0,  classificationWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("failurereport", "Failure Report", PageTabContext.TabType.SIMPLE,true, AccountUtil.FeatureLicense.FAILURE_CODES)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("failurereport", null, null)
                    .addWidget("failurereport", "Failure Report", PageWidget.WidgetType.FAILURE_REPORT, "flexiblewebfailurereport_6", 0, 0,  timeLogWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("timelog", "Time Log", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("timelog", null, null)
                    .addWidget("timelogandmetrics", "Time Log", PageWidget.WidgetType.STATE_TRANSITION_TIME_LOG, "flexiblewebstatetransitiontimelog_6", 0, 0, timeLogWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("history", "History", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("history", null, null)
                    .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0,  historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone()
                    .addMobileLayout()
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null,  null)
                    .addWidget("summaryFieldsWidget", null, PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblemobilesummaryfieldswidget_14", 0, 0,  null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("commentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_8", 0, 0, null, getWidgetGroup(true))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("specification", "Specification", PageTabContext.TabType.SIMPLE,true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", "Classification", null)
                    .addWidget("classificationwidget", null, PageWidget.WidgetType.CLASSIFICATION,"flexiblemobileclassification_14", 0, 0,  classificationWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relatedwidgetgroup", null, null)
                    .addWidget("relatedwidgetGroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_8", 0, 0,  null, getMobileRelatedWidgetGroup(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone();
        }
        else {
            return  new PagesContext(null, null,"", null, true, false, false)
                    .addWebLayout()
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null, null)
                    .addWidget("summaryFieldsWidget", "Summary", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0,  null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("specification", "Specification", PageTabContext.TabType.SIMPLE,true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", null, null)
                    .addWidget("classificationwidget", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_6",  0, 0, classificationWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("failurereport", "Failure Report", PageTabContext.TabType.SIMPLE,true, AccountUtil.FeatureLicense.FAILURE_CODES)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("failurereport", null, null)
                    .addWidget("failurereport", "Failure Report", PageWidget.WidgetType.FAILURE_REPORT, "flexiblewebfailurereport_6", 0, 0,  timeLogWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone()
                    .addMobileLayout()
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null,  null)
                    .addWidget("summaryFieldsWidget", null, PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblemobilesummaryfieldswidget_14", 0, 0,  null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null,  null)
                    .addWidget("commentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_8", 0, 0, null, getWidgetGroup(true))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("specification", "Specification", PageTabContext.TabType.SIMPLE,true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", "Classification", null)
                    .addWidget("classificationwidget", null, PageWidget.WidgetType.CLASSIFICATION,"flexiblemobileclassification_14", 0, 0,  classificationWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relatedwidgetgroup", null, null)
                    .addWidget("relatedwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_8", 0, 0,  null, getMobileRelatedWidgetGroup(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone();
        }

    }




    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField siteIdField = moduleBean.getField("siteId", moduleName);
        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, siteIdField,1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedByField,2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 2, 2, 1);


        widgetGroup.setName("generalinformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if(field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if(widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            }
            else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }
    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "cmdnotes");

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", "cmdattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                    .addSection("notes", "Notes", "")
                        .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                        .widgetGroupWidgetDone()
                    .widgetGroupSectionDone()
                    .addSection("documents", "Documents", "")
                        .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                        .widgetGroupWidgetDone()
                    .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getMobileRelatedWidgetGroup(FacilioModule module) throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                        .addWidget("relationships", null, PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblemobilebulkrelationshipwidget_14", 0,0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                        .widgetGroupWidgetDone()
                    .widgetGroupSectionDone()
                    .addSection("relatedlistsection", "Related List", "List of related records across modules")
                        .addWidget("relatedlist", null, PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblemobilebulkrelatedlist_14", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                        .widgetGroupWidgetDone()
                    .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }



}
