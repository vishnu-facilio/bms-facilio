package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class InductionResponseModuleTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.Induction.INDUCTION_RESPONSE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Induction.INDUCTION_RESPONSE_ACTIVITY);

        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)

                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summary", "", null)
                .addWidget("inductionSummaryWidget", "Induction", PageWidget.WidgetType.INDUCTION_RESPONSE_SUMMARY, "flexibleinductionresponsesummarywidget_5", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("notesandinformation", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("inductionNotesAndInformationWidget", "Field Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("commentsandattachments", "", null)
                .addWidget("inductionCommentsAndAttachmentsWidget", "", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("related", "Related", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inductionresponserelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("inductionresponsebulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("inductionresponserelatedlist", "Related List", "List of related records across modules")
                .addWidget("inductionresponsebulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inductionresponsehistory", null, null)
                .addWidget("inductionResponseHistoryWidget", "Induction", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField workEndField = moduleBean.getField("actualWorkEnd", moduleName);
        FacilioField workStartField = moduleBean.getField("actualWorkStart", moduleName);
        FacilioField assignedTOField = moduleBean.getField("people", moduleName);
        FacilioField scheduleWorkEndField = moduleBean.getField("scheduledWorkEnd", moduleName);
        FacilioField scheduleWorkStartField = moduleBean.getField("scheduledWorkStart", moduleName);
        FacilioField vendorField = moduleBean.getField("vendor", moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, workEndField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, workStartField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, assignedTOField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, vendorField, 1, 4, 1);

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, scheduleWorkEndField, 2, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, scheduleWorkStartField, 2, 2, 1);


        widgetGroup.setName("generalinformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);
        widgetGroup.setSequenceNumber(1);

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysCreatedByField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysCreatedTimeField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysModifiedByField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, sysModifiedTimeField, 1, 4, 1);

        widgetGroup2.setName("systeminformation");
        widgetGroup2.setDisplayName("System Information");
        widgetGroup2.setColumns(4);
        widgetGroup2.setSequenceNumber(2);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup2);

        pageWidget.setDisplayName("Induction Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("activityModuleName", "inductionResponsenotes");

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("activityModuleName","inductionResponseattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);

    }
}
