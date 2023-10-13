package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PageColumnContext;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.VisitorManagementModulePageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

public class GroupInvitesModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.GROUP_VISITOR_INVITE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {

        JSONObject viewParam = new JSONObject();
        viewParam.put("viewName", "InviteVisitorListView");
        viewParam.put("viewModuleName","invitevisitor");
        return  new PagesContext(null, null,"", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("groupinviteSummaryDetails", "Group Invite Details", PageWidget.WidgetType.FIXED_SUMMARY_FIELDS_WIDGET, "webfixedsummaryfieldswidget_6_9", 0, 0, null, VisitorManagementModulePageUtil.getSummaryWidgetDetailsForGroupInvitesModule(module.getName(), app))
                .widgetDone()
                .addWidget("totalInviteDetails", "Total Invites", PageWidget.WidgetType.TOTAL_INVITE_WIDGET, "webtotalinvitewidget_3_3", 9, 0, null, null)
                .widgetDone()
                .addWidget("totalCheckedInInviteDetails", "Total CheckedIn Invites", PageWidget.WidgetType.CHECKIN_COUNT_WIDGET, "webtotalcheckedininvitewidget_3_3", 9, 3, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("inviteDeatils", null, null)
                .addWidget("inviteDetails", "All Invites", PageWidget.WidgetType.GROUP_INVITE_LIST_WIDGET, "fexiblewebgroupinvitewidget_6", 0, 0, viewParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
}
