package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsuranceModuleTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return "insurance";
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return  new PagesContext(null, null,"", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("insurancedetails", "Insurance Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("insurancecommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0,  null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "insurancenotes");
        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", "insuranceattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Notes", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField validFromField = moduleBean.getField("validFrom", moduleName);
        FacilioField validTillField = moduleBean.getField("validTill", moduleName);
        FacilioField vendorField = moduleBean.getField("vendor", moduleName);
        FacilioField insuranceField = moduleBean.getField("insurance", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField createdTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField modifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField modifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, vendorField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, validFromField,1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, validTillField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, insuranceField,1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, createdByField,2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, createdTimeField,2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, modifiedByField,2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, modifiedTimeField,2, 4, 1);

        widgetGroup.setName("insuranceDeatils");
        widgetGroup.setDisplayName("");
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
}
