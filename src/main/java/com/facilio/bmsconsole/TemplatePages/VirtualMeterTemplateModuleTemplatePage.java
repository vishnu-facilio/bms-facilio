package com.facilio.bmsconsole.TemplatePages;

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
import java.util.List;
public class VirtualMeterTemplateModuleTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_ACTIVITY);

        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("virtualmetertemplatesummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vmTemplateSection", null, null)
                .addWidget("vmTemplateWidget", "General Information", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName()))
                .widgetDone()
                .sectionDone()
                .addSection("vmTemplateReadingsSection", null, null)
                .addWidget("vmTemplateReadingsWidget", "Readings", PageWidget.WidgetType.VIRTUAL_METER_TEMPLATE_READINGS, "flexiblewebvmtemplatereadings_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("virtualmetertemplatehistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("historySection", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        FacilioField name = moduleBean.getField("name", moduleName);
        FacilioField utilityType = moduleBean.getField("utilityType", moduleName);
        FacilioField scope = moduleBean.getField("scope",moduleName);
        FacilioField relationShipId = moduleBean.getField("relationShipId", moduleName);
        FacilioField description = moduleBean.getField("description", moduleName);
        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, name, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, utilityType, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, scope, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, relationShipId, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, description, 2, 1, 4);
        widgetGroup.setName("moduleDetails");
        //widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

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
        widgetGroupList.add(systemInformationGroup);
        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP).getId());
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
}