package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.facilio.modules.FieldFactory.getRuleAlarmDetailsFields;

public class ReadingRuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ReadingRules.NEW_READING_RULE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("ruleDetails", null, null)
                .addWidget("ruleDetails", "Rule Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ReadingRules.NEW_READING_RULE))
                .widgetDone()
                .sectionDone()
                .addSection("faultDetails", null, null)
                .addWidget("assetsAndAlarm", null, PageWidget.WidgetType.RULE_ASSETS_ALARM, "webRuleAssetsAndAlarm-30_4", 0, 0, null, null)
                .widgetDone()
                .addWidget("rootCauseAndImpact", null, PageWidget.WidgetType.ROOT_CAUSE_AND_IMPACT, "rootCauseAndImpact-30_3", 4, 0, null, null)
                .widgetDone()
                .addWidget("ruleAlarmInsights", null, PageWidget.WidgetType.RULE_ALARM_INSIGHT, "webRuleAlarmInsights-30_5", 7, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("workOrderDetails", null, null)
                .addWidget("associatedWorkOrders", "Associated Work Orders", PageWidget.WidgetType.RULE_ASSOCIATED_WO, "webAssociatedWorkOrders-15_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("ruleWorkOrderDuration", "Associated Work Orders", PageWidget.WidgetType.RULE_WO_DURATION, "webRuleWorkOrderDuration-15_6", 6, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("ruleInsight", "Rule Insight", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("ruleInsight", null, null)
                .addWidget("ruleInsight", "Rule Insight", PageWidget.WidgetType.RULE_INSIGHT, "ruleInsight-32_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("rootCauses", "Root Causes", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("rootCauses", null, null)
                .addWidget("rootCauses", "Root Causes", PageWidget.WidgetType.ROOT_CAUSES, "rootCauses-32_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("logs", "Logs", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("ruleLogs", null, null)
                .addWidget("ruleLogs", "Activity Log", PageWidget.WidgetType.RULE_LOGS, "ruleLogs-32_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                ;
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField assetCategory = moduleBean.getField("assetCategory", moduleName);

        Map<String, FacilioField> ruleAlarmDetailsFields = FieldFactory.getAsMap(getRuleAlarmDetailsFields());
        FacilioField messageField = ruleAlarmDetailsFields.get("message");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup primaryDetailsWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, messageField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, assetCategory, 2, 2, 1);

        primaryDetailsWidgetGroup.setName("primaryDetails");
        primaryDetailsWidgetGroup.setDisplayName("Primary Details");
        primaryDetailsWidgetGroup.setColumns(4);

        SummaryWidgetGroup rootCauseAndImpactGroup = new SummaryWidgetGroup();

        FacilioField resource = moduleBean.getField("resource", moduleName);

        addSummaryFieldInWidgetGroup(rootCauseAndImpactGroup, resource, 1, 1, 1);
        addSummaryFieldInWidgetGroup(rootCauseAndImpactGroup, resource, 1, 1, 1);
        addSummaryFieldInWidgetGroup(rootCauseAndImpactGroup, resource, 1, 1, 1);

        rootCauseAndImpactGroup.setName("rootCauseAndImpact");
        rootCauseAndImpactGroup.setDisplayName("Root cause & impact");
        rootCauseAndImpactGroup.setColumns(4);
//
//        SummaryWidgetGroup allFieldsWidgetGroup = new SummaryWidgetGroup();
//
//        List<FacilioField> fields = moduleBean.getAllFields(moduleName);
//        int columnNo = 1, rowNo = 1;
//        for (FacilioField field : fields) {
//            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1);
//            columnNo++;
//            if(columnNo > 3) {
//                columnNo = 1;
//                rowNo ++;
//            }
//        }
//
//        allFieldsWidgetGroup.setName("fields");
//        allFieldsWidgetGroup.setDisplayName("Fields");
//        allFieldsWidgetGroup.setColumns(3);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(primaryDetailsWidgetGroup);
//        widgetGroupList.add(otherDetailsWidgetGroup);
//        widgetGroupList.add(allFieldsWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) throws Exception {
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
