package com.facilio.bmsconsole.TemplatePages;

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
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilityDisputeTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.UTILITY_DISPUTE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.UTILITY_DISPUTE_ACTIVITY);


        return new PagesContext("disputetemplatepage", " Dispute Template Page", "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("utilitydisputesummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("utilitydisputesummaryfields", null, null)
                .addWidget("utilitydisputesummarywidget", "Dispute  Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.UTILITY_DISPUTE,app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("utilitydisputehistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("history", "History ", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()

                ;
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName,ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField dateField = moduleBean.getField("billDate", moduleName);
        FacilioField accNoField = moduleBean.getField("accountNumber", moduleName);
        FacilioField supplierField = moduleBean.getField("supplier", moduleName);
        FacilioField typeField = moduleBean.getField("type", moduleName);


        FacilioField createdField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy",moduleName);
        FacilioField modifiedField = moduleBean.getField("sysModifiedBy",moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime",moduleName);
        FacilioField resolvedBy = moduleBean.getField("resolvedBy",moduleName);

        FacilioField actualMeterConsumption = moduleBean.getField("actualMeterConsumption",moduleName);
        FacilioField billMeterConsumption = moduleBean.getField("billMeterConsumption",moduleName);
        FacilioField disputedConsumption = moduleBean.getField("disputedConsumption",moduleName);
        FacilioField expectedCost = moduleBean.getField("expectedCost",moduleName);
        FacilioField actualCost = moduleBean.getField("actualCost",moduleName);
        FacilioField differenceInCost = moduleBean.getField("differenceInCost",moduleName);
        FacilioField tariffToBeApplied = moduleBean.getField("tariffToBeApplied",moduleName);
        FacilioField tariffApplied = moduleBean.getField("tariffApplied",moduleName);


        SummaryWidget pageWidget = new SummaryWidget();

        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();


        addSummaryFieldInWidgetGroup(widgetGroup, accNoField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, supplierField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, typeField, 1, 3, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("Primary Information");
        widgetGroup.setColumns(4);


        SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup1, createdField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, createdByField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, modifiedField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, sysModifiedTimeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, resolvedBy, 2, 1, 1);


        widgetGroup1.setName("moduleSystemDetails");
        widgetGroup1.setDisplayName("System Information");
        widgetGroup1.setColumns(4);

        SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup2, actualMeterConsumption, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, billMeterConsumption, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, disputedConsumption, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, expectedCost, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, actualCost, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, differenceInCost, 2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, tariffToBeApplied, 2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, tariffApplied, 2, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, dateField, 3, 1, 1);


        widgetGroup2.setName("otherDetails");
        widgetGroup2.setDisplayName("Dispute Information");
        widgetGroup2.setColumns(4);


        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup2);
        widgetGroupList.add(widgetGroup1);

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

    private static JSONObject getSummaryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getHistoryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ACTIVITY);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("fieldUpdate", "Field Update", "")
                .addWidget("fieldUpdatewidget", "Field Update", PageWidget.WidgetType.ACTIVITY, isMobile ? "flexiblemobilecomment_8" : "flexiblewebactivity_60", 0, 0, historyWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}


