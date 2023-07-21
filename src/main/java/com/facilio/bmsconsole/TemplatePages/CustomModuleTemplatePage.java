package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                    .addWebTab("summary", "SUMMARY", true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null, null)
                    .addWidget("summaryFieldsWidget", "Summary Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 4, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("related", "RELATED", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_29", 0, 4,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("specification", "SPECIFICATION",true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", null, null)
                    .addWidget("classificationwidget", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_28",  0, 0,  classificationWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("history", "HISTORY",true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("history", null, null)
                    .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0,  historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("failurereport", "FAILURE REPORT",true, AccountUtil.FeatureLicense.FAILURE_CODES)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("failurereport", null, null)
                    .addWidget("failurereport", "Failure Report", PageWidget.WidgetType.FAILURE_REPORT, "flexiblewebfailurereport_29", 0, 0,  timeLogWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("timelog", "TIME LOG",true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("timelog", null, null)
                    .addWidget("timelogandmetrics", "Time Log", PageWidget.WidgetType.STATE_TRANSITION_TIME_LOG, "flexiblewebstatetransitiontimelog_30", 0, 0, timeLogWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addMobileTab("summary", "SUMMARY",true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null,  null)
                    .addWidget("summaryFieldsWidget", null, PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblemobilesummaryfieldswidget_8", 0, 0,  null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null,  null)
                    .addWidget("commentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_8", 0, 4,  null, getWidgetGroup(true))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addMobileTab("related", "RELATED",true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relatedwidgetgroup", null, null)
                    .addWidget("relatedwidgetGroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_8", 0, 0,  null, getMobileRelatedWidgetGroup())
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addMobileTab("specification", "SPECIFICATION",true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", "Classification", null)
                    .addWidget("classificationwidget", null, PageWidget.WidgetType.CLASSIFICATION,"flexiblemobileclassification_8", 0, 0,  classificationWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone();
        }
        else {
            return  new PagesContext(null, null,"", null, true, false, false)
                    .addWebTab("summary", "SUMMARY", true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null, null)
                    .addWidget("summaryFieldsWidget", "Summary Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0,  null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 4, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("related", "RELATED", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_29", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_29", 0, 4,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("specification", "SPECIFICATION",true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", null, null)
                    .addWidget("classificationwidget", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_28",  0, 0, classificationWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("failurereport", "FAILURE REPORT",true, AccountUtil.FeatureLicense.FAILURE_CODES)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("failurereport", null, null)
                    .addWidget("failurereport", "Failure Report", PageWidget.WidgetType.FAILURE_REPORT, "flexiblewebfailurereport_29", 0, 0,  timeLogWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addMobileTab("summary", "SUMMARY",true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null,  null)
                    .addWidget("summaryFieldsWidget", null, PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblemobilesummaryfieldswidget_8", 0, 0,  null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null,  null)
                    .addWidget("commentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP,"flexiblemobilewidgetgroup_8", 0, 4,  null, getWidgetGroup(true))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addMobileTab("related", "RELATED",true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relatedwidgetgroup", null, null)
                    .addWidget("relatedwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_8", 0, 0,  null, getMobileRelatedWidgetGroup())
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addMobileTab("specification", "SPECIFICATION",true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", "Classification", null)
                    .addWidget("classificationwidget", null, PageWidget.WidgetType.CLASSIFICATION,"flexiblemobileclassification_8", 0, 0,  classificationWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone();
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

        addSummaryFieldInWidgetGroup(widgetGroup, siteIdField,1, 1, 2);
        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1 , 3, 2);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField, 2, 1, 2);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 2, 3, 2);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedByField,3, 1, 2);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 3, 3, 2);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("Module Details");
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
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                    .addSection("notes", "Notes", "")
                        .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 4, null, null)
                        .widgetGroupWidgetDone()
                    .widgetGroupSectionDone()
                    .addSection("documents", "Documents", "")
                        .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 4, null, null)
                        .widgetGroupWidgetDone()
                    .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getMobileRelatedWidgetGroup() throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                        .addWidget("relationships", null, PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblemobilebulkrelationshipwidget_8", 0,0, null, null)
                        .widgetGroupWidgetDone()
                    .widgetGroupSectionDone()
                    .addSection("relatedlistsection", "Related List", "List of related records across modules")
                        .addWidget("relatedlist", null, PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblemobilebulkrelatedlist_8", 0, 0, null, null)
                        .widgetGroupWidgetDone()
                    .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }



}
