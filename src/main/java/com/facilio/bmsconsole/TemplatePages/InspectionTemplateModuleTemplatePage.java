package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleWidget.APIModuleWidgets;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InspectionTemplateModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.Inspection.INSPECTION_TEMPLATE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
//        WidgetAPI.addWidgets(getModuleName(), APIModuleWidgets.getInspectionTemplateWidgets().get().getWidgets());

        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)

                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("inspectionSummaryFieldWidget", "Inspection Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("questionspagestriggers", "", null)
                .addWidget("questionsCountWidget", "", PageWidget.WidgetType.INSPECTION_TEMPLATE_SUMMARY_QUESTION_COUNT, "fixedquestioncountwidget_2_4", 0, 0, null, null)
                .widgetDone()
                .addWidget("pagesCountWidget", "", PageWidget.WidgetType.INSPECTION_TEMPLATE_SUMMARY_PAGE_COUNT, "fixedpagecountwidget_2_4", 4, 0, null, null)
                .widgetDone()
                .addWidget("lastTriggeredWidget", "", PageWidget.WidgetType.INSPECTION_TEMPLATE_SUMMARY_LAST_TRIGGERED, "fixedlasttriggeredwidget_2_4", 8, 0, null, null)
                .widgetDone()
                .addWidget("questionWidget", "Questions", PageWidget.WidgetType.INSPECTION_TEMPLATE_SUMMARY_QUESTIONS, "flexibleinspectiontemplatequestionswidget_6", 0, 1, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("trigger", "Trigger", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inspectiontriggers", null, null)
                .addWidget("inspectionTriggers", "Inspection Triggers", PageWidget.WidgetType.INSPECTION_TEMPLATE_TRIGGERS, "flexibleinspectiontemplatetriggerwidget_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("insight", "Insight", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inspectiontemplateinsight", null, null)
                .addWidget("inspectionTemplateInsightGraph", "Inspection By Status", PageWidget.WidgetType.INSPECTION_TEMPLATE_INSIGHT_GRAPH, "flexibleinspectiontemplateinsightgraphwidget_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("inspectionTemplateInsightSummary", "Inspection Summary", PageWidget.WidgetType.INSPECTION_TEMPLATE_INSIGHT_SUMMARY, "flexibleinspectiontemplateinsightsummarywidget_6", 0, 1, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inspectiontemplatehistory", null, null)
                .addWidget("inspectionTemplateHistoryWidget", "Inspection", PageWidget.WidgetType.RELATED_LIST, "flexiblewebrelatedlist_6", 0, 0, null , RelatedListWidgetUtil.getSingleRelatedListForModule(Constants.getModBean().getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE),FacilioConstants.Inspection.INSPECTION_RESPONSE,"parent"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField scopeField = moduleBean.getField("creationType", moduleName);
        FacilioField siteIdField = moduleBean.getField("siteId", moduleName);
        FacilioField spaceAssetField = moduleBean.getField("resource", moduleName);
        FacilioField categoryField = moduleBean.getField("category", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, scopeField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, siteIdField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, spaceAssetField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, categoryField, 1, 4, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, priorityField, 2, 1, 1);


        widgetGroup.setName("configurationinformation");
//        widgetGroup.setDisplayName("Configuration Information");
        widgetGroup.setColumns(4);
        widgetGroup.setSequenceNumber(1);

        SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysCreatedByField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysCreatedTimeField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysModifiedByField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysModifiedTimeField, 1, 4, 1);

        widgetGroup2.setName("fieldinformation");
        widgetGroup2.setDisplayName("System Information");
        widgetGroup2.setColumns(4);
        widgetGroup2.setSequenceNumber(2);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup2);

        pageWidget.setDisplayName("Inspection Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }
}
