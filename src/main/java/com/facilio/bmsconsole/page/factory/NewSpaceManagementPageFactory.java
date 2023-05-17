package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.weather.util.WeatherAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.json.simple.JSONObject;

import java.util.Arrays;

public class NewSpaceManagementPageFactory extends PageFactory {
    public static Page getSitePage(SiteContext site, FacilioModule module) throws Exception {
        Page page = new Page();
        if(AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)){
            Page.Tab tab1 = page.new Tab("summary");
            page.addTab(tab1);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            Page.Section tab1Sec1 = page.new Section();
            tab1.addSection(tab1Sec1);
            addSecondaryDetailsWidget(tab1Sec1);
            addBuildingsWidget(site.getId(), tab1Sec1,modBean);
            addSpacesWidget(tab1Sec1,modBean);
            addCommonSubModuleWidget(tab1Sec1, module, site);

        }
        else{
        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        addSecondaryDetailsWidget(tab1Sec1);
        addNewRelatedCountWidget(tab1Sec1, Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER, FacilioConstants.ContextNames.NEW_READING_ALARM, FacilioConstants.ContextNames.ASSET));
        addOperatingHoursWidget(tab1Sec1);
        addCurrentWeatherWidget(tab1Sec1);
        addWeatherChartWidget(tab1Sec1);
        if(WeatherAPI.getStationIdForSiteId(site.getSiteId()) > 0L) {
            addWeatherTableWidget(tab1Sec1);
        }
        addCommonSubModuleWidget(tab1Sec1, module, site);
        Page.Tab tab2 = page.new Tab("Building and Spaces");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        addBuildingsWidget(site.getId(), tab2Sec1,modBean);
        addSpacesWidget(tab2Sec1,modBean);
        tab2.addSection(tab2Sec1);
        Page.Tab tab3 = page.new Tab("Readings");
        page.addTab(tab3);
        Page.Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);
        addReadingWidget(tab3Sec1);
        addRelatedReadingWidget(tab3Sec1);
        Page.Tab tab4 = page.new Tab("Related");
        addRelationshipSection(page,tab4,site.getModuleId());
        page.addTab(tab4);

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
            addSafetyPlanTab(page);
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLASSIFICATION)){
            addClassificationTab(page);
        }
        Page.Tab tab5 = page.new Tab("History");
        page.addTab(tab5);
        Page.Section tab5Sec1 = page.new Section();
        tab5.addSection(tab5Sec1);
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        activityWidget.addToLayoutParams(tab5Sec1, 24, 3);
        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.SITE_ACTIVITY);
        tab5Sec1.addWidget(activityWidget);

            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.COMMISSIONING)) {
                Page.Tab tab6 = page.new Tab("Commissioning Log");
                page.addTab(tab6);
                Page.Section tab6Sec1 = page.new Section();
                tab6.addSection(tab6Sec1);
                PageWidget commissionActivityWidget = new PageWidget(PageWidget.WidgetType.COMMISSIONING);
                commissionActivityWidget.addToLayoutParams(tab6Sec1, 24, 3);
                commissionActivityWidget.addToWidgetParams("commissionActivityModuleName", FacilioConstants.ContextNames.COMMISSIONING_ACTIVITY);
                tab6Sec1.addWidget(commissionActivityWidget);
            }
        }
        return page;
    }
    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(detailsWidget);
    }
    private static void addOperatingHoursWidget(Page.Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD);
        cardWidget.addToLayoutParams(section, 10, 5);
        cardWidget.addCardType(PageWidget.CardType.NEW_OPERATING_HOURS);
        section.addWidget(cardWidget);
    }
    private static void addCurrentWeatherWidget(Page.Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD);
        cardWidget.addToLayoutParams(section, 8, 7);
        cardWidget.addCardType(PageWidget.CardType.CURRENT_WEATHER);
        section.addWidget(cardWidget);
    }
    private static void addWeatherChartWidget(Page.Section section){
        PageWidget chartWidget = new PageWidget(PageWidget.WidgetType.WEATHER_CHART);
        chartWidget.addToLayoutParams(section,16,7);
        section.addWidget(chartWidget);
    }

    private static void addWeatherTableWidget(Page.Section section){
        PageWidget tableWidget = new PageWidget(PageWidget.WidgetType.WEATHER_TABLE);
        tableWidget.addToLayoutParams(section,24,8);
        section.addWidget(tableWidget);
    }
    private static void addBuildingsWidget(long siteId, Page.Section section, ModuleBean modBean) throws Exception {
        FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.LIST, "newSiteBuildings");
        JSONObject relatedList = new JSONObject();
        relatedList.put("module", buildingModule);
        pageWidget.setRelatedList(relatedList);
        pageWidget.addToLayoutParams(section, 24, 10);
        pageWidget.addToWidgetParams(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, true);
        section.addWidget(pageWidget);
    }
    private static void addSpacesWidget(Page.Section section, ModuleBean modBean) throws Exception {
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
        JSONObject relatedList = new JSONObject();
        relatedList.put("module", module);
        PageWidget widget = new PageWidget(PageWidget.WidgetType.LIST, "newSiteSpaces");
        widget.setRelatedList(relatedList);
        widget.addToLayoutParams(section, 24, 10);
        widget.addToWidgetParams(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, true);
        section.addWidget(widget);
    }
    private static void addReadingWidget(Page.Section section){
        PageWidget readingsWidget = new PageWidget(PageWidget.WidgetType.LIST, "spaceReadings");
        readingsWidget.addToLayoutParams(section, 24, 8);
        section.addWidget(readingsWidget);
    }

    private static void addRelatedReadingWidget(Page.Section section){
        PageWidget relatedReadingsWidget = new PageWidget(PageWidget.WidgetType.LIST, "relatedReadings");
        relatedReadingsWidget.addToLayoutParams(section, 24, 8);
        section.addWidget(relatedReadingsWidget);
    }
    private static Page.Section addSafetyPlanTab(Page page) throws Exception {
        Page.Tab safetyPlanTab = page.new Tab("safety plan");
        page.addTab(safetyPlanTab);

        Page.Section safetyPlanSection = page.new Section();
        safetyPlanTab.addSection(safetyPlanSection);

        // hazards widget
        PageWidget hazards = new PageWidget(PageWidget.WidgetType.HAZARDS);
        hazards.addToLayoutParams(0, 0, 24, 8);
        safetyPlanSection.addWidget(hazards);

        // precautions widget
        PageWidget widget = new PageWidget(PageWidget.WidgetType.LIST, "safetyPlanPrecautions");
        widget.addToLayoutParams(safetyPlanSection, 24, 10);
        safetyPlanSection.addWidget(widget);

        return safetyPlanSection;
    }
    private static void addClassificationTab(Page page){
        Page.Tab tab = page.new Tab("Specification");
        page.addTab(tab);
        Page.Section tab1Sec1 = page.new Section();
        tab.addSection(tab1Sec1);
        PageWidget classificationWidget = new PageWidget(PageWidget.WidgetType.CLASSIFICATION);
        classificationWidget.setName("Classification");
        classificationWidget.addToLayoutParams(tab1Sec1, 24, 8);
        classificationWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.SITE_ACTIVITY);
        tab1Sec1.addWidget(classificationWidget);
    }
}
