package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ClientContactModuleUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.VisitorManagementModulePageUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.*;

public class GroupInviteModule extends BaseModuleConfig{
    public GroupInviteModule(){
        setModuleName(FacilioConstants.ContextNames.GROUP_VISITOR_INVITE);
    }
    @Override
    public void addData() throws Exception {
        addSystemButton(getModuleName());
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> groupInvite = new ArrayList<FacilioView>();
        groupInvite.add(getAllGroupInvitesView().setOrder(order));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.GROUP_VISITOR_INVITE);
        groupDetails.put("views", groupInvite);
        groupVsViews.add(groupDetails);
        return groupVsViews;
    }
    private static FacilioView getAllGroupInvitesView() throws Exception {

        FacilioView allView = new FacilioView();
        allView.setName("group_invite_all");
        allView.setDisplayName("All Group Invites");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule groupInviteModule = modBean.getModule(FacilioConstants.ContextNames.GROUP_VISITOR_INVITE);
        FacilioField name = FieldFactory.getField("name", "NAME", groupInviteModule, FieldType.STRING);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        allView.setSortFields(Arrays.asList(new SortField(name, true)));

        return allView;
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String>  appNameList=new ArrayList<>();
        appNameList.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.GROUP_VISITOR_INVITE);
        for(String appName:appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
                appNameVsPage.put(appName, buildGroupInvitePage(app, module, false, true));
        }
        return appNameVsPage;
    }

    private List<PagesContext> buildGroupInvitePage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName() + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";
        JSONObject viewParam = new JSONObject();
        viewParam.put("viewName", "InviteVisitorListView");
        viewParam.put("viewModuleName","invitevisitor");
        return new ModulePages()
                .addPage(pageName, pageDisplayName, "", null, isTemplate, isDefault, false)
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
                .layoutDone()
                .pageDone().getCustomPages();
    }
    public static void addSystemButton(String moduleName) throws Exception{

        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName("Create");
        createButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButton.setIdentifier("create");
        createButton.setPermissionRequired(true);
        createButton.setPermission("CREATE");
        SystemButtonApi.addSystemButton(moduleName,createButton);

        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(moduleName,listEditButton);

        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        SystemButtonApi.addSystemButton(moduleName,listDeleteButton);

        SystemButtonRuleContext exportAsCSVButton = new SystemButtonRuleContext();
        exportAsCSVButton.setName("Export As CSV");
        exportAsCSVButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsCSVButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCSVButton.setIdentifier("export_as_csv");
        exportAsCSVButton.setPermissionRequired(true);
        exportAsCSVButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(moduleName,exportAsCSVButton);

        SystemButtonRuleContext exportAsExcelButton = new SystemButtonRuleContext();
        exportAsExcelButton.setName("Export As Excel");
        exportAsExcelButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsExcelButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButton.setIdentifier("export_as_excel");
        exportAsExcelButton.setPermissionRequired(true);
        exportAsExcelButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(moduleName,exportAsExcelButton);

        SystemButtonRuleContext summaryEditButton = new SystemButtonRuleContext();
        summaryEditButton.setName("Edit");
        summaryEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        summaryEditButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        summaryEditButton.setIdentifier("edit_summary");
        summaryEditButton.setPermissionRequired(true);
        summaryEditButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(moduleName,summaryEditButton);
    }

}
