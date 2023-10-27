package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
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
import com.facilio.util.SummaryWidgetUtil;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BudgetTemplatePageFactory implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.BUDGET;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
            return new PagesContext(null, null, "", null, true, false, false)
                    .addWebLayout()
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null, null)
                    .addWidget("totalbudget", "Total Budget", PageWidget.WidgetType.TOTAL_BUDGET, "totalbudget_3_4", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("actualAmount","Actual Amount", PageWidget.WidgetType.ACTUAL_BUDGET_AMOUNT,"actualbudgetamount_3_4",4,0,null,null)
                    .widgetDone()
                    .addWidget("remainingAmount","Remaining Amount", PageWidget.WidgetType.REMAINING_BUDGET,"remainingbudget_3_4",8,0,null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("budget", null, null)
                    .addWidget("budget","Budget", PageWidget.WidgetType.BUDGET_INCOME_EXPENSE,"flexiblebudgetincomeexpense_21",0,0,null,null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("notesAndInformation", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null, null)
                    .addWidget("summaryFieldsWidget", "Budget Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("transaction", "Transactions", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("transaction", null, null)
                    .addWidget("transaction", "All Transactions", PageWidget.WidgetType.BUDGET_TRANSACTIONS, "flexiblewebbudgettransactions_9", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone();


    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField fiscalYearStartField = moduleBean.getField("fiscalYearStart", moduleName);
        FacilioField fiscalYearField = moduleBean.getField("fiscalYear", moduleName);
        FacilioField focalPointTypeField = moduleBean.getField("focalPointType", moduleName);
        FacilioField focalPointResourceField = moduleBean.getField("focalPointResource", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, fiscalYearStartField,1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, fiscalYearField, 1 , 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, focalPointTypeField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, focalPointResourceField, 1, 4, 1);


        widgetGroup.setName("generalinformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "cmdnotes");

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", "cmdattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("noteswidget", "Notes", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("attachment", "Attachments", "")
                .addWidget("attachmentwidget", "Attachments", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
}
