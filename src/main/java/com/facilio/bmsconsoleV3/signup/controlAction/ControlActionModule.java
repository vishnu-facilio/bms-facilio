package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.ControlActionPage;
import com.facilio.bmsconsole.TemplatePages.ControlActionTemplatePage;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.factory.PageFactory;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class ControlActionModule extends BaseModuleConfig {
    public ControlActionModule() throws Exception{
        setModuleName(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
    }
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlAction = new ArrayList<FacilioView>();
        controlAction.add(getAllControlActionViews().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        groupDetails.put("views", controlAction);
        groupVsViews.add(groupDetails);
        return groupVsViews;
    }
    private static FacilioView getAllControlActionViews() throws Exception{

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Control Action");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<ViewField> fields = new ArrayList<>();
        fields.add(new ViewField("name","Name"));
        fields.add(new ViewField("description","Description"));
        fields.add(new ViewField("controlActionSourceType","Source"));
        fields.add(new ViewField("controlActionType","Action Type"));
        fields.add(new ViewField("assetCategory","Asset Category"));
        fields.add(new ViewField("scheduledActionDateTime","Schedule Action Time"));
        fields.add(new ViewField("revertActionDateTime","Revert Action Time"));
        fields.add(new ViewField("controlActionStatus","Status"));
        allView.setFields(fields);

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Control_Actions.ID", FieldType.NUMBER), true));
        allView.setSortFields(sortFields);

        return allView;
    }
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());

        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getControlActionViewPage(app, module));
        }
        return appNameVsPage;
    }

    private List<PagesContext> getControlActionViewPage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject controlActionActivityWidgetParam = new JSONObject();
        controlActionActivityWidgetParam.put("activityModuleName", FacilioConstants.Control_Action.CONTROL_ACTION_ACTIVITY_MODULE_NAME);
        return new ModulePages()
                .addPage("controlActionViewPage", "Default Control Action View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("controlactionsummary", "SUMMARY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("controlactionsummaryfields", null, null)
                .addWidget("controlactionsummaryfieldswidget", "Control Action", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, ControlActionPage.getSummaryWidgetDetails(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME , app))
                .widgetDone()
                .sectionDone()
                .addSection("controlactionControlfields", null, null)
                .addWidget("controlactionControlfieldswidget", "Controls", PageWidget.WidgetType.BULK_RELATED_LIST, "fixedrelatedListwidgetViewWidget-28*12", 0, 0, null,ControlActionPage.fetchRelatedListForModule(module,FacilioConstants.Control_Action.ACTION_MODULE_NAME))
                .widgetDone()
                .sectionDone()
                .addSection("controlactionCriteriafields", null, null)
                .addWidget("controlactionassetcriteriafieldswidget", "Asset attribute criteria", PageWidget.WidgetType.CONRTOL_ACTION_CRITERIA, "webControlActionCriteriaViewWidget-28*6", 0, 0, ControlActionPage.getSummaryCriteriaListWidgetDetails(FacilioConstants.ContextNames.ASSET, "assetCriteriaId"),null)
                .widgetDone()
                .addWidget("controlactionsitecriteriafieldswidget", "Site attribute criteria", PageWidget.WidgetType.CONRTOL_ACTION_CRITERIA, "webControlActionCriteriaViewWidget-28*6", 6, 0, ControlActionPage.getSummaryCriteriaListWidgetDetails(FacilioConstants.ContextNames.SITE,"siteCriteriaId"),null)
                .widgetDone()
                .addWidget("controlactioncontrollercriteriafieldswidget", "Controller attribute criteria", PageWidget.WidgetType.CONRTOL_ACTION_CRITERIA, "webControlActionCriteriaViewWidget-28*6", 0, 28, ControlActionPage.getSummaryCriteriaListWidgetDetails("controllers","controllerCriteriaId"),null)
                .widgetDone()
                .addWidget("controlactioncommentsfieldswidget", "Comments", PageWidget.WidgetType.COMMENT, "fixedwebwidget_28*6", 6, 28, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("controlActionCommand", "Command results", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("controlActionCommandFields", null, null)
                .addWidget("controlActionRelatedList", "List of Commands", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 0, null, ControlActionPage.fetchRelatedListForModule(module,FacilioConstants.Control_Action.COMMAND_MODULE_NAME))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("controlActionHistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("controlActionHistoryFields", null, null)
                .addWidget("controlActionHistory", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, controlActionActivityWidgetParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }
}
