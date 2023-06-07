package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

public class CreateDefaultAndTemplatePageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        String moduleName = module.getName();

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        PagesUtil.addTemplatePage(moduleName, getCustomModuleTemplatePageForApps(module, appNames));
        PagesUtil.addSystemPages(moduleName, getCustomModuleDefaultPagesForApps(module, appNames));

        return false;
    }

    private static Map<String, List<PagesContext>> getCustomModuleTemplatePageForApps(FacilioModule module, List<String> appNames) throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            PagesContext templatePage = getCustomModulePage(module, module.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "")+"templatepage");
            appNameVsPage.put(appName, new ArrayList<>(Arrays.asList(templatePage)));
        }
        return appNameVsPage;
    }

    private static Map<String, List<PagesContext>> getCustomModuleDefaultPagesForApps(FacilioModule module, List<String> appNames) throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            PagesContext defaultPage = getCustomModulePage(module, module.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "") + "defaultpage");
            appNameVsPage.put(appName, new ArrayList<>(Arrays.asList(defaultPage)));
        }
        return appNameVsPage;
    }

    private static PagesContext getCustomModulePage(FacilioModule module, String pageName) throws Exception{

        String moduleName = module.getName();
        long moduleId = module.getModuleId();
        PagesContext page = new PagesContext();
        page.setName(pageName);
        page.setDisplayName("Default "+module.getDisplayName()+" Page ");
        page.setModuleId(moduleId);
        page.setModuleName(moduleName);
        page.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));


        Map<String, List<PageTabContext>> layouts = new HashMap<>();
        List<PageTabContext> webTabs = new ArrayList<>();

        PageTabContext wt1 = new PageTabContext();
        wt1.setDisplayName("SUMMARY");

        List<PageColumnContext> wt1Columns = new ArrayList<>();
        PageColumnContext wt1c1 = new PageColumnContext();
        wt1c1.setWidth(12);

        List<PageSectionContext> wt1c1Sections = new ArrayList<>();
        PageSectionContext wt1c1s1 = new PageSectionContext();
        wt1c1s1.setDescription("");

        List<PageSectionWidgetContext> wt1c1s1Widgets = new ArrayList<>();
        PageSectionWidgetContext wt1c1s1w1 = new PageSectionWidgetContext();
        wt1c1s1w1.setWidgetType(PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET);
        wt1c1s1w1.setWidth(12);
        wt1c1s1w1.setHeight(4);
        wt1c1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        wt1c1s1w1.setPositionX(0);
        wt1c1s1w1.setPositionY(0);
        wt1c1s1w1.setWidgetDetail(getSummaryWidgetDetails(moduleName));


        wt1c1s1Widgets.add(wt1c1s1w1);
        wt1c1s1.setWidgets(wt1c1s1Widgets);
        wt1c1Sections.add(wt1c1s1);

        PageSectionContext wt1c1s2 = new PageSectionContext();
        wt1c1s2.setDescription("");

        List<PageSectionWidgetContext> wt1c1s2Widgets = new ArrayList<>();
        PageSectionWidgetContext wt1c1s2w1 = new PageSectionWidgetContext();
        wt1c1s2w1.setWidgetType(PageWidget.WidgetType.WIDGET_GROUP);
        wt1c1s2w1.setWidth(12);
        wt1c1s2w1.setHeight(4);
        wt1c1s2w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        wt1c1s2w1.setPositionX(0);
        wt1c1s2w1.setPositionY(4);

        WidgetGroupContext wt1c1s2w1WG1 = new WidgetGroupContext();
        WidgetGroupConfigContext wt1c1s2w1WG1Config = new WidgetGroupConfigContext();
        wt1c1s2w1WG1Config.setConfigType(WidgetGroupConfigContext.ConfigType.TAB);
        wt1c1s2w1WG1.setConfig(wt1c1s2w1WG1Config);

        List<WidgetGroupSectionContext> wt1c1s2w1WG1Sections = new ArrayList<>();
        WidgetGroupSectionContext wt1c1s2w1WG1s1 = new WidgetGroupSectionContext();
        wt1c1s2w1WG1s1.setSequenceNumber(10D);
        wt1c1s2w1WG1s1.setDisplayName("Notes");
        wt1c1s2w1WG1s1.setDescription("");

        WidgetGroupWidgetContext wt1c1s2w1WG1s1w1 = new WidgetGroupWidgetContext();
        wt1c1s2w1WG1s1w1.setWidgetType(PageWidget.WidgetType.COMMENT);
        wt1c1s2w1WG1s1w1.setSequenceNumber(10D);
        wt1c1s2w1WG1s1w1.setWidth(12);
        wt1c1s2w1WG1s1w1.setHeight(4);
        wt1c1s2w1WG1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        wt1c1s2w1WG1s1w1.setPositionX(0L);
        wt1c1s2w1WG1s1w1.setPositionY(4);

        wt1c1s2w1WG1s1.setWidgets(Collections.singletonList(wt1c1s2w1WG1s1w1));
        wt1c1s2w1WG1Sections.add(wt1c1s2w1WG1s1);

        WidgetGroupSectionContext wt1c1s2w1WG1s2 = new WidgetGroupSectionContext();
        wt1c1s2w1WG1s2.setSequenceNumber(20D);
        wt1c1s2w1WG1s2.setDisplayName("Documents");
        wt1c1s2w1WG1s2.setDescription("");

        WidgetGroupWidgetContext wt1c1s2w1WG1s2w1 = new WidgetGroupWidgetContext();
        wt1c1s2w1WG1s2w1.setWidgetType(PageWidget.WidgetType.ATTACHMENT);
        wt1c1s2w1WG1s2w1.setWidth(12);
        wt1c1s2w1WG1s2w1.setSequenceNumber(10D);
        wt1c1s2w1WG1s2w1.setHeight(4);
        wt1c1s2w1WG1s2w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        wt1c1s2w1WG1s2w1.setPositionX(0L);
        wt1c1s2w1WG1s2w1.setPositionY(4);

        wt1c1s2w1WG1s2.setWidgets(Collections.singletonList(wt1c1s2w1WG1s2w1));
        wt1c1s2w1WG1Sections.add(wt1c1s2w1WG1s2);
        wt1c1s2w1WG1.setSections(wt1c1s2w1WG1Sections);
        wt1c1s2w1.setWidgetDetail(FieldUtil.getAsJSON(wt1c1s2w1WG1));

        wt1c1s2Widgets.add(wt1c1s2w1);
        wt1c1s2.setWidgets(wt1c1s2Widgets);
        wt1c1Sections.add(wt1c1s2);
        wt1c1.setSections(wt1c1Sections);
        wt1Columns.add(wt1c1);
        wt1.setColumns(wt1Columns);
        webTabs.add(wt1);

        PageTabContext wt2 = new PageTabContext();
        wt2.setDisplayName("RELATED");

        List<PageColumnContext> wt2Columns = new ArrayList<>();
        PageColumnContext wt2c1 = new PageColumnContext();
        wt2c1.setWidth(12);

        List<PageSectionContext> wt2c1Sections = new ArrayList<>();
        PageSectionContext wt2c1s1 = new PageSectionContext();
        wt2c1s1.setDisplayName("Relationships");
        wt2c1s1.setDescription("List of relationships and types between records across modules");

        List<PageSectionWidgetContext> wt2c1s1Widgets = new ArrayList<>();
        PageSectionWidgetContext wt2c1s1w1 = new PageSectionWidgetContext();
        wt2c1s1w1.setWidgetType(PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET);
        wt2c1s1w1.setWidth(12);
        wt2c1s1w1.setHeight(4);
        wt2c1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        wt2c1s1w1.setPositionX(0);
        wt2c1s1w1.setPositionY(0);

        wt2c1s1Widgets.add(wt2c1s1w1);
        wt2c1s1.setWidgets(wt2c1s1Widgets);
        wt2c1Sections.add(wt2c1s1);

        PageSectionContext wt2c1s2 = new PageSectionContext();
        wt2c1s2.setDisplayName("Related List");
        wt2c1s2.setDescription("List of related records across modules");

        List<PageSectionWidgetContext> wt2c1s2Widgets = new ArrayList<>();
        PageSectionWidgetContext wt2c1s2w1 = new PageSectionWidgetContext();
        wt2c1s2w1.setWidgetType(PageWidget.WidgetType.BULK_RELATED_LIST);
        wt2c1s2w1.setWidth(12);
        wt2c1s2w1.setHeight(4);
        wt2c1s2w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        wt2c1s2w1.setPositionX(0);
        wt2c1s2w1.setPositionY(4);

        wt2c1s2Widgets.add(wt2c1s2w1);
        wt2c1s2.setWidgets(wt2c1s2Widgets);
        wt2c1Sections.add(wt2c1s2);
        wt2c1.setSections(wt2c1Sections);
        wt2Columns.add(wt2c1);
        wt2.setColumns(wt2Columns);
        webTabs.add(wt2);


        PageTabContext wt3 = new PageTabContext();
        wt3.setDisplayName("SPECIFICATION");
        wt3.setFeatureLicense(AccountUtil.FeatureLicense.CLASSIFICATION.getFeatureId());

        List<PageColumnContext> wt3Columns = new ArrayList<>();
        PageColumnContext wt3c1 = new PageColumnContext();
        wt3c1.setWidth(12);

        List<PageSectionContext> wt3c1Sections = new ArrayList<>();
        PageSectionContext wt3c1s1 = new PageSectionContext();
        wt3c1s1.setDisplayName("Classification");
        wt3c1s1.setDescription("");

        List<PageSectionWidgetContext> wt3c1s1Widgets = new ArrayList<>();
        PageSectionWidgetContext wt3c1s1w1 = new PageSectionWidgetContext();
        wt3c1s1w1.setWidgetType(PageWidget.WidgetType.CLASSIFICATION);
        wt3c1s1w1.setWidth(12);
        wt3c1s1w1.setHeight(4);
        wt3c1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        wt3c1s1w1.setPositionX(0);
        wt3c1s1w1.setPositionY(0);
        JSONObject classificationWidgetParam = new JSONObject();
        classificationWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SITE_ACTIVITY);
        wt3c1s1w1.setWidgetParams(classificationWidgetParam);

        wt3c1s1Widgets.add(wt3c1s1w1);
        wt3c1s1.setWidgets(wt3c1s1Widgets);
        wt3c1Sections.add(wt3c1s1);
        wt3c1.setSections(wt3c1Sections);
        wt3Columns.add(wt3c1);
        wt3.setColumns(wt3Columns);
        webTabs.add(wt3);


        PageTabContext wt4 = new PageTabContext();
        wt4.setDisplayName("HISTORY");

        List<PageColumnContext> wt4Columns = new ArrayList<>();
        PageColumnContext wt4c1 = new PageColumnContext();
        wt4c1.setWidth(12);

        List<PageSectionContext> w1t4c1Sections = new ArrayList<>();
        PageSectionContext wt4c1s1 = new PageSectionContext();
        wt4c1s1.setDisplayName("History");
        wt4c1s1.setDescription("");

        List<PageSectionWidgetContext> wt4c1s1Widgets = new ArrayList<>();
        PageSectionWidgetContext wt4c1s1w1 = new PageSectionWidgetContext();
        wt4c1s1w1.setWidgetType(PageWidget.WidgetType.ACTIVITY);
        wt4c1s1w1.setWidth(12);
        wt4c1s1w1.setHeight(2);
        wt4c1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        wt4c1s1w1.setPositionX(0);
        wt4c1s1w1.setPositionY(0);
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
        wt4c1s1w1.setWidgetParams(historyWidgetParam);

        wt4c1s1Widgets.add(wt4c1s1w1);
        wt4c1s1.setWidgets(wt4c1s1Widgets);
        w1t4c1Sections.add(wt4c1s1);
        wt4c1.setSections(w1t4c1Sections);
        wt4Columns.add(wt4c1);
        wt4.setColumns(wt4Columns);
        webTabs.add(wt4);

        PageTabContext wt5 = new PageTabContext();
        wt5.setDisplayName("FAILURE REPORT");
        wt5.setFeatureLicense(AccountUtil.FeatureLicense.FAILURE_CODES.getFeatureId());

        List<PageColumnContext> wt5Columns = new ArrayList<>();
        PageColumnContext wt5c1 = new PageColumnContext();
        wt5c1.setWidth(12);

        List<PageSectionContext> wt5c1Sections = new ArrayList<>();
        PageSectionContext wt5c1s1 = new PageSectionContext();
        List<PageSectionWidgetContext> wt5c1s1Widgets = new ArrayList<>();
        PageSectionWidgetContext wt5c1s1w1 = new PageSectionWidgetContext();
        wt5c1s1w1.setWidgetType(PageWidget.WidgetType.FAILURE_REPORT);
        wt5c1s1w1.setWidth(12);
        wt5c1s1w1.setHeight(4);
        wt5c1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        wt5c1s1w1.setPositionX(0);
        wt5c1s1w1.setPositionY(0);
        JSONObject failureReportWidgetParam = new JSONObject();
        failureReportWidgetParam.put("card", "failurereports");
        wt5c1s1w1.setWidgetParams(failureReportWidgetParam);

        wt5c1s1Widgets.add(wt5c1s1w1);
        wt5c1s1.setWidgets(wt5c1s1Widgets);
        wt5c1Sections.add(wt5c1s1);
        wt5c1.setSections(wt5c1Sections);
        wt5Columns.add(wt5c1);
        wt5.setColumns(wt5Columns);
        webTabs.add(wt5);
        layouts.put(PagesContext.PageLayoutType.WEB.name(), webTabs);



        List<PageTabContext> mobileTabs = new ArrayList<>();

        PageTabContext mt1 = new PageTabContext();
        mt1.setDisplayName("SUMMARY");

        List<PageColumnContext> mt1Columns = new ArrayList<>();
        PageColumnContext mt1c1 = new PageColumnContext();
        mt1c1.setWidth(12);

        List<PageSectionContext> mt1c1Sections = new ArrayList<>();
        PageSectionContext mt1c1s1 = new PageSectionContext();
        mt1c1s1.setDescription("");

        List<PageSectionWidgetContext> mt1c1s1Widgets = new ArrayList<>();
        PageSectionWidgetContext mt1c1s1w1 = new PageSectionWidgetContext();
        mt1c1s1w1.setWidgetType(PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET);
        mt1c1s1w1.setWidth(12);
        mt1c1s1w1.setHeight(4);
        mt1c1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        mt1c1s1w1.setPositionX(0);
        mt1c1s1w1.setPositionY(0);
        mt1c1s1w1.setWidgetDetail(getSummaryWidgetDetails(moduleName));


        mt1c1s1Widgets.add(mt1c1s1w1);
        mt1c1s1.setWidgets(mt1c1s1Widgets);
        mt1c1Sections.add(mt1c1s1);

        PageSectionContext mt1c1s2 = new PageSectionContext();
        mt1c1s2.setDescription("");

        List<PageSectionWidgetContext> mt1c1s2Widgets = new ArrayList<>();
        PageSectionWidgetContext mt1c1s2w1 = new PageSectionWidgetContext();
        mt1c1s2w1.setWidgetType(PageWidget.WidgetType.WIDGET_GROUP);
        mt1c1s2w1.setWidth(12);
        mt1c1s2w1.setHeight(4);
        mt1c1s2w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        mt1c1s2w1.setPositionX(0);
        mt1c1s2w1.setPositionY(4);

        WidgetGroupContext mt1c1s2w1WG1 = new WidgetGroupContext();
        WidgetGroupConfigContext mt1c1s2w1WG1Config = new WidgetGroupConfigContext();
        mt1c1s2w1WG1Config.setConfigType(WidgetGroupConfigContext.ConfigType.TAB);
        mt1c1s2w1WG1.setConfig(mt1c1s2w1WG1Config);

        List<WidgetGroupSectionContext> mt1c1s2w1WG1Sections = new ArrayList<>();
        WidgetGroupSectionContext mt1c1s2w1WG1s1 = new WidgetGroupSectionContext();
        mt1c1s2w1WG1s1.setSequenceNumber(10D);
        mt1c1s2w1WG1s1.setDisplayName("Notes");
        mt1c1s2w1WG1s1.setDescription("");

        WidgetGroupWidgetContext mt1c1s2w1WG1s1w1 = new WidgetGroupWidgetContext();
        mt1c1s2w1WG1s1w1.setWidgetType(PageWidget.WidgetType.COMMENT);
        mt1c1s2w1WG1s1w1.setSequenceNumber(10D);
        mt1c1s2w1WG1s1w1.setWidth(12);
        mt1c1s2w1WG1s1w1.setHeight(4);
        mt1c1s2w1WG1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        mt1c1s2w1WG1s1w1.setPositionX(0L);
        mt1c1s2w1WG1s1w1.setPositionY(4);

        mt1c1s2w1WG1s1.setWidgets(Collections.singletonList(mt1c1s2w1WG1s1w1));
        mt1c1s2w1WG1Sections.add(mt1c1s2w1WG1s1);

        WidgetGroupSectionContext mt1c1s2w1WG1s2 = new WidgetGroupSectionContext();
        mt1c1s2w1WG1s2.setSequenceNumber(10D);
        mt1c1s2w1WG1s2.setDisplayName("Documents");
        mt1c1s2w1WG1s2.setDescription("");

        WidgetGroupWidgetContext mt1c1s2w1WG1s2w1 = new WidgetGroupWidgetContext();
        mt1c1s2w1WG1s2w1.setWidgetType(PageWidget.WidgetType.ATTACHMENT);
        mt1c1s2w1WG1s2w1.setWidth(12);
        mt1c1s2w1WG1s2w1.setSequenceNumber(10D);
        mt1c1s2w1WG1s2w1.setHeight(4);
        mt1c1s2w1WG1s2w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        mt1c1s2w1WG1s2w1.setPositionX(0L);
        mt1c1s2w1WG1s2w1.setPositionY(4);

        mt1c1s2w1WG1s2.setWidgets(Collections.singletonList(mt1c1s2w1WG1s2w1));
        mt1c1s2w1WG1Sections.add(mt1c1s2w1WG1s2);
        mt1c1s2w1WG1.setSections(mt1c1s2w1WG1Sections);
        mt1c1s2w1.setWidgetDetail(FieldUtil.getAsJSON(mt1c1s2w1WG1));

        mt1c1s2Widgets.add(mt1c1s2w1);
        mt1c1s2.setWidgets(mt1c1s2Widgets);
        mt1c1Sections.add(mt1c1s2);
        mt1c1.setSections(mt1c1Sections);
        mt1Columns.add(mt1c1);
        mt1.setColumns(mt1Columns);
        mobileTabs.add(mt1);


        PageTabContext mt2 = new PageTabContext();
        mt2.setDisplayName("RELATED");

        List<PageColumnContext> mt2Columns = new ArrayList<>();
        PageColumnContext mt2c1 = new PageColumnContext();
        mt2c1.setWidth(12);

        List<PageSectionContext> mt2c1Sections = new ArrayList<>();
        PageSectionContext mt2c1s1 = new PageSectionContext();
        mt2c1s1.setDisplayName("Relationships");
        mt2c1s1.setDescription("List of relationships and types between records across modules");

        List<PageSectionWidgetContext> mt2c1s1Widgets = new ArrayList<>();
        PageSectionWidgetContext mt2c1s1w1 = new PageSectionWidgetContext();
        mt2c1s1w1.setWidgetType(PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET);
        mt2c1s1w1.setWidth(12);
        mt2c1s1w1.setHeight(4);
        mt2c1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        mt2c1s1w1.setPositionX(0);
        mt2c1s1w1.setPositionY(0);

        mt2c1s1Widgets.add(mt2c1s1w1);
        mt2c1s1.setWidgets(mt2c1s1Widgets);
        mt2c1Sections.add(mt2c1s1);

        PageSectionContext mt2c1s2 = new PageSectionContext();
        mt2c1s2.setDisplayName("Related List");
        mt2c1s2.setDescription("List of related records across modules");

        List<PageSectionWidgetContext> mt2c1s2Widgets = new ArrayList<>();
        PageSectionWidgetContext mt2c1s2w1 = new PageSectionWidgetContext();
        mt2c1s2w1.setWidgetType(PageWidget.WidgetType.BULK_RELATED_LIST);
        mt2c1s2w1.setWidth(12);
        mt2c1s2w1.setHeight(4);
        mt2c1s2w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        mt2c1s2w1.setPositionX(0);
        mt2c1s2w1.setPositionY(4);

        mt2c1s2Widgets.add(mt2c1s2w1);
        mt2c1s2.setWidgets(mt2c1s2Widgets);
        mt2c1Sections.add(mt2c1s2);
        mt2c1.setSections(mt2c1Sections);
        mt2Columns.add(mt2c1);
        mt2.setColumns(mt2Columns);
        mobileTabs.add(mt2);

        PageTabContext mt3 = new PageTabContext();
        mt3.setDisplayName("SPECIFICATION");
        mt3.setFeatureLicense(AccountUtil.FeatureLicense.CLASSIFICATION.getFeatureId());

        List<PageColumnContext> mt3Columns = new ArrayList<>();
        PageColumnContext mt3c1 = new PageColumnContext();
        mt3c1.setWidth(12);

        List<PageSectionContext> mt3c1Sections = new ArrayList<>();
        PageSectionContext mt3c1s1 = new PageSectionContext();
        mt3c1s1.setDisplayName("Classification");
        mt3c1s1.setDescription("");

        List<PageSectionWidgetContext> mt3c1s1Widgets = new ArrayList<>();
        PageSectionWidgetContext mt3c1s1w1 = new PageSectionWidgetContext();
        mt3c1s1w1.setWidgetType(PageWidget.WidgetType.CLASSIFICATION);
        mt3c1s1w1.setWidth(12);
        mt3c1s1w1.setHeight(4);
        mt3c1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        mt3c1s1w1.setPositionX(0);
        mt3c1s1w1.setPositionY(0);
        mt3c1s1w1.setWidgetParams(classificationWidgetParam);

        mt3c1s1Widgets.add(mt3c1s1w1);
        mt3c1s1.setWidgets(mt3c1s1Widgets);
        mt3c1Sections.add(mt3c1s1);
        mt3c1.setSections(mt3c1Sections);
        mt3Columns.add(mt3c1);
        mt3.setColumns(mt3Columns);
        mobileTabs.add(mt3);


        PageTabContext mt4 = new PageTabContext();
        mt4.setDisplayName("HISTORY");

        List<PageColumnContext> mt4Columns = new ArrayList<>();
        PageColumnContext mt4c1 = new PageColumnContext();
        mt4c1.setWidth(12);

        List<PageSectionContext> m1t4c1Sections = new ArrayList<>();
        PageSectionContext mt4c1s1 = new PageSectionContext();
        mt4c1s1.setDisplayName("History");
        mt4c1s1.setDescription("");

        List<PageSectionWidgetContext> mt4c1s1Widgets = new ArrayList<>();
        PageSectionWidgetContext mt4c1s1w1 = new PageSectionWidgetContext();
        mt4c1s1w1.setWidgetType(PageWidget.WidgetType.ACTIVITY);
        mt4c1s1w1.setWidth(12);
        mt4c1s1w1.setHeight(2);
        mt4c1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        mt4c1s1w1.setPositionX(0);
        mt4c1s1w1.setPositionY(0);
        mt4c1s1w1.setWidgetParams(historyWidgetParam);

        mt4c1s1Widgets.add(mt4c1s1w1);
        mt4c1s1.setWidgets(mt4c1s1Widgets);
        m1t4c1Sections.add(mt4c1s1);
        mt4c1.setSections(m1t4c1Sections);
        mt4Columns.add(mt4c1);
        mt4.setColumns(mt4Columns);
        mobileTabs.add(mt4);

        PageTabContext mt5 = new PageTabContext();
        mt5.setDisplayName("FAILURE REPORT");
        mt5.setFeatureLicense(AccountUtil.FeatureLicense.FAILURE_CODES.getFeatureId());

        List<PageColumnContext> mt5Columns = new ArrayList<>();
        PageColumnContext mt5c1 = new PageColumnContext();
        mt5c1.setWidth(12);

        List<PageSectionContext> mt5c1Sections = new ArrayList<>();
        PageSectionContext mt5c1s1 = new PageSectionContext();
        mt5c1s1.setDescription("");

        List<PageSectionWidgetContext> mt5c1s1Widgets = new ArrayList<>();
        PageSectionWidgetContext mt5c1s1w1 = new PageSectionWidgetContext();
        mt5c1s1w1.setWidgetType(PageWidget.WidgetType.FAILURE_REPORT);
        mt5c1s1w1.setWidth(12);
        mt5c1s1w1.setHeight(4);
        mt5c1s1w1.setConfigType(PageSectionWidgetContext.ConfigType.FLEXIBLE);
        mt5c1s1w1.setPositionX(0);
        mt5c1s1w1.setPositionY(0);
        mt5c1s1w1.setWidgetParams(failureReportWidgetParam);

        mt5c1s1Widgets.add(mt5c1s1w1);
        mt5c1s1.setWidgets(mt5c1s1Widgets);
        mt5c1Sections.add(mt5c1s1);
        mt5c1.setSections(mt5c1Sections);
        mt5Columns.add(mt5c1);
        mt5.setColumns(mt5Columns);
        mobileTabs.add(mt5);

        layouts.put(PagesContext.PageLayoutType.MOBILE.name(), mobileTabs);
        page.setLayouts(layouts);

        return page;
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField siteIdField = moduleBean.getField("siteId", moduleName);
        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget summaryWidget1 = new SummaryWidget();
        SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();

        SummaryWidgetGroupFields groupField11 = new SummaryWidgetGroupFields();
        SummaryWidgetGroupFields groupField12 = new SummaryWidgetGroupFields();
        SummaryWidgetGroupFields groupField13 = new SummaryWidgetGroupFields();
        SummaryWidgetGroupFields groupField14 = new SummaryWidgetGroupFields();
        SummaryWidgetGroupFields groupField15 = new SummaryWidgetGroupFields();
        SummaryWidgetGroupFields groupField16 = new SummaryWidgetGroupFields();

        groupField11.setName(siteIdField.getName());
        groupField11.setDisplayName(siteIdField.getDisplayName());
        groupField11.setFieldId(siteIdField.getId());
        groupField11.setRowIndex(1);
        groupField11.setColIndex(1);
        groupField11.setColSpan(2);

        groupField12.setName(nameField.getName());
        groupField12.setDisplayName(nameField.getDisplayName());
        groupField12.setFieldId(nameField.getId());
        groupField12.setRowIndex(1);
        groupField12.setColIndex(3);
        groupField12.setColSpan(2);

        groupField13.setName(sysCreatedByField.getName());
        groupField13.setDisplayName(sysCreatedByField.getDisplayName());
        groupField13.setFieldId(sysCreatedByField.getId());
        groupField13.setRowIndex(2);
        groupField13.setColIndex(1);
        groupField13.setColSpan(2);

        groupField14.setName(sysCreatedTimeField.getName());
        groupField14.setDisplayName(sysCreatedTimeField.getDisplayName());
        groupField14.setFieldId(sysCreatedTimeField.getId());
        groupField14.setRowIndex(2);
        groupField14.setColIndex(3);
        groupField14.setColSpan(2);

        groupField15.setName(sysModifiedByField.getName());
        groupField15.setDisplayName(sysModifiedByField.getDisplayName());
        groupField15.setFieldId(sysModifiedByField.getId());
        groupField15.setRowIndex(3);
        groupField15.setColIndex(1);
        groupField15.setColSpan(2);

        groupField16.setName(sysModifiedTimeField.getName());
        groupField16.setDisplayName(sysModifiedTimeField.getDisplayName());
        groupField16.setFieldId(sysModifiedTimeField.getId());
        groupField16.setRowIndex(3);
        groupField16.setColIndex(3);
        groupField16.setColSpan(2);

        List<SummaryWidgetGroupFields> groupOneFields = new ArrayList<>();
        groupOneFields.add(groupField11);
        groupOneFields.add(groupField12);
        groupOneFields.add(groupField13);
        groupOneFields.add(groupField14);
        groupOneFields.add(groupField15);
        groupOneFields.add(groupField16);


        widgetGroup1.setName("moduleDetails");
        widgetGroup1.setDisplayName("Module Details");
        widgetGroup1.setColumns(4);
        widgetGroup1.setFields(groupOneFields);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup1);

        summaryWidget1.setDisplayName("Summary Widget");
        summaryWidget1.setModuleId(module.getModuleId());
        summaryWidget1.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
        summaryWidget1.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(summaryWidget1);

    }
}
