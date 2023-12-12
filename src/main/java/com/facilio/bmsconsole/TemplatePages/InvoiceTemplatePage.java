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

public class InvoiceTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.INVOICE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.INVOICE_ACTIVITY);
        return  new PagesContext(null, null,"", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("invoicePdfViewer", null, null)
                .addWidget("invoicePdfViewerWidget", "Summary", PageWidget.WidgetType.PDF_VIEWER, "flexiblewebpdfviewer_19", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("noteandinformation","Notes & Information",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("invoiceSummaryFields", null, null)
                .addWidget("invoiceSummaryFieldsWidget", "Invoice Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_7", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("invoicewidgetGroup", null,  null)
                .addWidget("invoicecommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("invoicerelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("invoicebulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("invoicerelatedlist", "Related List", "List of related records across modules")
                .addWidget("invoicebulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history","History",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("invoiceHistory",null,null)
                .addWidget("invoiceHistoryWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0,  historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
    public static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedByPeople", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);

        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedByPeople", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField client = moduleBean.getField("client", moduleName);
        FacilioField vendor = moduleBean.getField("vendor", moduleName);

        FacilioField workorder = moduleBean.getField("workorder", moduleName);
        FacilioField siteId = moduleBean.getField("siteId", moduleName);
        FacilioField invoiceType = moduleBean.getField("invoiceType", moduleName);
        FacilioField tenant = moduleBean.getField("tenant", moduleName);
        FacilioField invoiceNumber = moduleBean.getField("invoiceNumber", moduleName);
        FacilioField quote = moduleBean.getField("quote", moduleName);
        FacilioField purchaseOrder = moduleBean.getField("purchaseOrder", moduleName);
        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, invoiceNumber,1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, quote,1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, workorder,1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, purchaseOrder, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, invoiceType, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, vendor, 2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, client, 2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, tenant, 2, 4, 1);
        SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup2, sysCreatedByField,1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, sysCreatedTimeField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, sysModifiedByField,1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, sysModifiedTimeField, 1, 4, 1);
        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);
        widgetGroup2.setName("systemInformation");
        widgetGroup2.setDisplayName("System Information");
        widgetGroup2.setColumns(4);
        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup2);
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
    public static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.INVOICE_NOTES);
        JSONObject attachmentsWidgetParam = new JSONObject();
        notesWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.INVOICE_ATTACHMENTS);
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
}