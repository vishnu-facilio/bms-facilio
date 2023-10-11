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

public class ServiceModuleTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.SERVICE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicesummary", null, null)
                .addWidget("servicesummary", "Service Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
//                .addSection("widgetGroup", null, null)
//                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup())
//                .widgetDone()
//                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("vendors", "Vendors", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicevendors", null, null)
                .addWidget("servicevendors", "Vendors", PageWidget.WidgetType.SERVICE_VENDORS, "flexiblewebservicevendors_11", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        //general information

        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField duration = moduleBean.getField("duration", moduleName);
        FacilioField paymentType = moduleBean.getField("paymentType", moduleName);
        FacilioField buyingPrice = moduleBean.getField("buyingPrice", moduleName);
        FacilioField sellingPrice = moduleBean.getField("sellingPrice", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField sysModifiedBy = moduleBean.getField("sysModifiedBy", moduleName);

        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, duration, 2, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, paymentType, 2, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, buyingPrice, 2, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sellingPrice, 2, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sysCreatedTime, 3, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sysCreatedBy, 3, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sysModifiedTime, 3, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sysModifiedBy, 3, 4, 1);


        generalInformationwidgetGroup.setName("moduleDetails");
        generalInformationwidgetGroup.setDisplayName("Primary Details");
        generalInformationwidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationwidgetGroup);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("primaryDetails");
        widgetGroup.setDisplayName("Primary Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }
    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.SERVICE_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.SERVICE_ATTACHMENTS);
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT,  "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT,  "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, colSpan, null);
    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan, FacilioField lookupField) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (lookupField != null) {
                summaryField.setParentLookupFieldId(lookupField.getFieldId());
            }

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }
}
