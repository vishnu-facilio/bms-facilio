package com.facilio.bmsconsole.TemplatePages;

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
public class MeterTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.Meter.METER;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Meter.METER_ACTIVITY);



        if(app.getLinkName().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.ENERGY_APP)) {
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("metersummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("meterDetailsSection", null, null)
                    .addWidget("meterDetailsWidget", "Meter Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getMeterSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("commentsAndAttachmentsSection", null, null)
                    .addWidget("commentsAndAttachmentsWidget", "Comments And Attachments", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getCommentsAttachmentsWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("meterreadings", "Readings", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("meterReadingsSection", null, null)
                    .addWidget("meterReadingsWidget", "Meter Readings", PageWidget.WidgetType.METER_READINGS, "flexiblewebmeterreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("meterrelated", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("meterrelationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("meterrelatedlist", "Related List", "List of related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 4, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("meters", "Meters", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("meterRelations", "", "")
                    .addWidget("meterRelationsWidget", "Relationships", PageWidget.WidgetType.METER_RELATIONSHIPS,"flexiblewebmeterrelationshipwidget_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("meterhistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("historySection", null, null)
                    .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone();
        }
        else {
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("metersummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("meterDetailsSection", null, null)
                    .addWidget("meterDetailsWidget", "Meter Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getMeterSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("commentsAndAttachmentsSection", null, null)
                    .addWidget("commentsAndAttachmentsWidget", "Comments And Attachments", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getCommentsAttachmentsWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("meterreadings", "Readings", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("meterReadingsSection", null, null)
                    .addWidget("meterReadingsWidget", "Meter Readings", PageWidget.WidgetType.METER_READINGS, "flexiblewebmeterreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("meterrelated", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("meterrelationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("meterrelatedlist", "Related List", "List of related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 4, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("meterhistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("historySection", null, null)
                    .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone();
        }

    }
    private static JSONObject getMeterSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField utilityType = moduleBean.getField("utilityType", moduleName);
        FacilioField meterType = moduleBean.getField("meterType", moduleName);
        FacilioField isCheckMeter = moduleBean.getField("isCheckMeter", moduleName);
        FacilioField description = moduleBean.getField("description", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, utilityType, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, meterType, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, isCheckMeter, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, description, 2, 1, 4);

        widgetGroup.setName("meterModuleDetails");
        //widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);


        FacilioField meterLocation = moduleBean.getField("meterLocation",moduleName);
        FacilioField site = moduleBean.getField("siteId",moduleName);

        SummaryWidgetGroup locationWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(locationWidgetGroup, meterLocation, 1, 1, 1);
        addSummaryFieldInWidgetGroup(locationWidgetGroup, site, 1, 2, 1);

        locationWidgetGroup.setName("locationDetails");
        locationWidgetGroup.setDisplayName("Location");
        locationWidgetGroup.setColumns(4);


        FacilioField manufacturer = moduleBean.getField("manufacturer", moduleName);
        FacilioField model = moduleBean.getField("model", moduleName);
        FacilioField serialNumber = moduleBean.getField("serialNumber", moduleName);
        FacilioField purchasedDate = moduleBean.getField("purchasedDate", moduleName);
        FacilioField retireDate = moduleBean.getField("retireDate", moduleName);

        SummaryWidgetGroup manufactureWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(manufactureWidgetGroup, manufacturer, 1, 1, 1);
        addSummaryFieldInWidgetGroup(manufactureWidgetGroup, model, 1, 2, 1);
        addSummaryFieldInWidgetGroup(manufactureWidgetGroup, serialNumber, 1, 3, 1);
        addSummaryFieldInWidgetGroup(manufactureWidgetGroup, purchasedDate, 1, 4, 1);
        addSummaryFieldInWidgetGroup(manufactureWidgetGroup, retireDate, 2, 1, 1);

        manufactureWidgetGroup.setName("manufactureDetails");
        manufactureWidgetGroup.setDisplayName("Manufacture");
        manufactureWidgetGroup.setColumns(4);


        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedByPeople", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedBy = moduleBean.getField("sysModifiedByPeople", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidgetGroup systemInformationGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(systemInformationGroup, sysCreatedBy, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysCreatedTime, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysModifiedBy, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysModifiedTime, 1, 4, 1);

        systemInformationGroup.setName("systemDetails");
        systemInformationGroup.setDisplayName("System Information");
        systemInformationGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();

        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(locationWidgetGroup);
        widgetGroupList.add(manufactureWidgetGroup);
        widgetGroupList.add(systemInformationGroup);

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
            if (field.getName().equals("meterLocation")) {
                summaryField.setDisplayName("Meter Location");
            }
            else {
                summaryField.setDisplayName(field.getDisplayName());
            }
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

    private static JSONObject getCommentsAttachmentsWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME, FacilioConstants.Meter.METER_NOTES);

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME, FacilioConstants.Meter.METER_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comments", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}