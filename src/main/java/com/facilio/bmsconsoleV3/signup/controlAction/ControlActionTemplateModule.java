package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.ControlActionTemplatePage;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import org.json.simple.JSONObject;

import java.util.*;

public class ControlActionTemplateModule extends BaseModuleConfig {
    public ControlActionTemplateModule() throws Exception{
        setModuleName(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
    }
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlAction = new ArrayList<FacilioView>();
        controlAction.add(getAllControlActionTemplatesViews().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        groupDetails.put("views", controlAction);
        groupVsViews.add(groupDetails);
        return groupVsViews;
    }
    private static FacilioView getAllControlActionTemplatesViews() throws Exception {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Control Action Template");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));


        List<ViewField> fields = new ArrayList<>();
        fields.add(new ViewField("subject","Control Action Template Name"));
        fields.add(new ViewField("description","Description"));
        fields.add(new ViewField("controlActionType","Action Type"));
        fields.add(new ViewField("assetCategory","Asset Category"));
        fields.add(new ViewField("calendar","Calendar"));
        allView.setFields(fields);


        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Control_Action_Templates.ID", FieldType.NUMBER), true));
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
        JSONObject controlActionTemplateActivityWidgetParam = new JSONObject();
        controlActionTemplateActivityWidgetParam.put("activityModuleName", FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_ACTIVITY_MODULE_NAME);
        return new ModulePages()
                .addPage("controlActionTemplateViewPage", "Default Control Action Template View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("controlactiontemplatesummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("controlactiontemplatesummaryfields", null, null)
                .addWidget("controlactiontemplatesummaryfieldswidget", "Control Action Template", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, ControlActionTemplatePage.getSummaryWidgetDetails(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME , app))
                .widgetDone()
                .sectionDone()
                .addSection("controlactiontemplateControlfields", null, null)
                .addWidget("controlactiontemplateControlfieldswidget", "Controls", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 0, null,ControlActionTemplatePage.fetchRelatedListForModule(module,FacilioConstants.Control_Action.ACTION_MODULE_NAME))
                .widgetDone()
                .sectionDone()
                .addSection("controlactiontemplateCriteriafields", null, null)
                .addWidget("controlactiontemplateassetcriteriafieldswidget", "Asset attribute criteria", PageWidget.WidgetType.CRITERIA_LIST_VIEW, "webCriteriaListView_28_6", 0, 0, ControlActionTemplatePage.getSummaryCriteriaListWidgetDetails(FacilioConstants.ContextNames.ASSET,"assetCriteriaId"),null)
                .widgetDone()
                .addWidget("controlactiontemplatesitecriteriafieldswidget", "Site attribute criteria", PageWidget.WidgetType.CRITERIA_LIST_VIEW, "webCriteriaListView_28_6", 6, 0, ControlActionTemplatePage.getSummaryCriteriaListWidgetDetails(FacilioConstants.ContextNames.SITE,"siteCriteriaId"),null)
                .widgetDone()
                .addWidget("controlactiontemplatecontrollercriteriafieldswidget", "Controller attribute criteria", PageWidget.WidgetType.CRITERIA_LIST_VIEW, "webCriteriaListView_28_6", 0, 28, ControlActionTemplatePage.getSummaryCriteriaListWidgetDetails("controllers","controllerCriteriaId"),null)
                .widgetDone()
                .addWidget("widgetGroup", "Comments", PageWidget.WidgetType.WIDGET_GROUP, "webwidgetgroup_28_6", 6, 28, null, ControlActionTemplatePage.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("controlActionTemplateControlactions", "Control Actions", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("controlActionTemplateControlactionfields", null, null)
                .addWidget("controlActionTemplateControlactionRelatedList", "List of Control Actions", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 0, null, ControlActionTemplatePage.fetchRelatedListForModule(module,FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("controlActionTemplateHistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("controlActionTemplateHistoryFields", null, null)
                .addWidget("controlActionTemplateHistory", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, controlActionTemplateActivityWidgetParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }
}
