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
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InductionTemplateModuleTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.Induction.INDUCTION_TEMPLATE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {

//        WidgetAPI.addWidgets(getModuleName(), APIModuleWidgets.getInspectionTemplateWidgets().get().getWidgets());

        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)

                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("inductionSummaryFieldWidget", "Induction Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("inductionquestionspagestriggers", "", null)
                .addWidget("inductionQuestionsCountWidget", "", PageWidget.WidgetType.INDUCTION_TEMPLATE_SUMMARY_QUESTION_COUNT, "fixedquestioncountwidget_2_4", 0, 0, null, null)
                .widgetDone()
                .addWidget("inductionPagesCountWidget", "", PageWidget.WidgetType.INDUCTION_TEMPLATE_SUMMARY_PAGE_COUNT, "fixedpagecountwidget_2_4", 4, 0, null, null)
                .widgetDone()
                .addWidget("inductionLastTriggeredWidget", "", PageWidget.WidgetType.INDUCTION_TEMPLATE_SUMMARY_LAST_TRIGGERED, "fixedlasttriggeredwidget_2_4", 8, 0, null, null)
                .widgetDone()
                .addWidget("inductionQuestionWidget", "Questions", PageWidget.WidgetType.INDUCTION_TEMPLATE_SUMMARY_QUESTIONS, "flexibleinductiontemplatequestionswidget_6", 0, 1, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("trigger", "Trigger", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inductiontriggers", null, null)
                .addWidget("inductionTriggers", "Induction Triggers", PageWidget.WidgetType.INDUCTION_TEMPLATE_TRIGGERS, "flexibleinductiontemplatetriggerwidget_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("insight", "Insight", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inductiontemplateinsight", null, null)
                .addWidget("inductionTemplateInsightGraph", "Induction By Status", PageWidget.WidgetType.INDUCTION_TEMPLATE_INSIGHT_GRAPH, "flexibleinductiontemplateinsightgraphwidget_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("inductionTemplateInsightSummary", "Induction Summary", PageWidget.WidgetType.INDUCTION_TEMPLATE_INSIGHT_SUMMARY, "flexibleinductiontemplateinsightsummarywidget_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inductiontemplatehistory", null, null)
                .addWidget("inductionTemplateHistoryWidget", "Induction", PageWidget.WidgetType.RELATED_LIST, "flexiblewebrelatedlist_6", 0, 0, null , RelatedListWidgetUtil.getSingleRelatedListForModule(Constants.getModBean().getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE),FacilioConstants.Induction.INDUCTION_RESPONSE,"parent"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField applyToField = moduleBean.getField("siteApplyTo", moduleName);
        FacilioField scopeField = moduleBean.getField("creationType", moduleName);
        FacilioField siteIdField = moduleBean.getField("siteId", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, applyToField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, scopeField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, siteIdField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField, 1, 4, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 2, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedByField, 2, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 2, 3, 1);


        widgetGroup.setName("configurationinformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);
        widgetGroup.setSequenceNumber(1);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        pageWidget.setDisplayName("Induction Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }
}
