package com.facilio.bmsconsole.page.factory;

import static com.facilio.bmsconsole.page.factory.AssetPageFactory.addRelatedListWidget;

import java.util.Arrays;

import com.facilio.bmsconsole.context.*;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class SpaceManagementPageFactory extends PageFactory {
	
	public static Page getSitePage(SiteContext site, FacilioModule module) throws Exception {
        Page page = new Page();

		if (AccountUtil.isFeatureEnabled(FeatureLicense.ETISALAT)) {
		      Tab tab1 = page.new Tab("summary");
		        page.addTab(tab1);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		        Section tab1Sec1 = page.new Section();
		        tab1.addSection(tab1Sec1);


				PageWidget cards= new PageWidget(PageWidget.WidgetType.BILL_SITE_DETAILS);
				cards.addToLayoutParams(tab1Sec1, 24, 4);
				tab1Sec1.addWidget(cards);

				PageWidget electriycity= new PageWidget(PageWidget.WidgetType.UTILITY_CONNECTIONS, "utilityConnections");
				electriycity.addToLayoutParams(tab1Sec1, 24, 10);
				tab1Sec1.addWidget(electriycity);

		        int energyCardHeight = 4;
		        int yPos = tab1Sec1.getLatestY() + energyCardHeight;
		        addRelatedCountWidget(tab1Sec1, yPos, Arrays.asList(ContextNames.WORK_ORDER, ContextNames.NEW_READING_ALARM, ContextNames.ASSET));

		        Section tab1Sec2 = page.new Section();
		        addBuildingsWidget(site.getId(), tab1Sec2,modBean);
		        tab1.addSection(tab1Sec2);
				Section tab1Sec3 = page.new Section("siteSpaces");
				addSpacesWidget(tab1Sec3,modBean);
				tab1.addSection(tab1Sec3);
				Section tab1Sec4 = page.new Section("spaceReadings");
		        tab1.addSection(tab1Sec4);
				addReadingWidget(tab1Sec4);
				addCommonSubModuleWidget(tab1Sec4, module, site);

				if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLASSIFICATION)){
					addClassificationTab(page,ContextNames.SITE_ACTIVITY);
				}
				Page.Tab tab2 = page.new Tab("History");
			    page.addTab(tab2);
			    Page.Section tab2Sec1 = page.new Section();
			    tab2.addSection(tab2Sec1);
			    PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
			    activityWidget.addToLayoutParams(tab2Sec1, 24, 3);
			    activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.SITE_ACTIVITY);
			    tab2Sec1.addWidget(activityWidget);
		}
		else {
		      Tab tab1 = page.new Tab("summary");
		        page.addTab(tab1);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		        Section tab1Sec1 = page.new Section();
		        tab1.addSection(tab1Sec1);
		        addSecondaryDetailsWidget(tab1Sec1);
				addWeatherWidget(tab1Sec1);
		        int energyCardHeight = 4;
		        addEnergyWidget(tab1Sec1, energyCardHeight);
		        int yPos = tab1Sec1.getLatestY() + energyCardHeight;
		        addOperatingHoursWidget(tab1Sec1);
		        addRelatedCountWidget(tab1Sec1, yPos, Arrays.asList(ContextNames.WORK_ORDER, ContextNames.NEW_READING_ALARM, ContextNames.ASSET));
		        
		        Section tab1Sec2 = page.new Section();
		        addBuildingsWidget(site.getId(), tab1Sec2,modBean);
		        tab1.addSection(tab1Sec2);
				Section tab1Sec3 = page.new Section("siteSpaces");
				addSpacesWidget(tab1Sec3,modBean);
				tab1.addSection(tab1Sec3);
				Section tab1Sec4 = page.new Section("spaceReadings");
		        tab1.addSection(tab1Sec4);
				addReadingWidget(tab1Sec4);
				addCommonSubModuleWidget(tab1Sec4, module, site);

			if (AccountUtil.isFeatureEnabled(FeatureLicense.SAFETY_PLAN)) {
				addSafetyPlanTab(page);
			}
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLASSIFICATION)){
				addClassificationTab(page,ContextNames.SITE_ACTIVITY);
			}


			Page.Tab tab2 = page.new Tab("History");
			    page.addTab(tab2);
			    Page.Section tab2Sec1 = page.new Section();
			    tab2.addSection(tab2Sec1);
			    PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
			    activityWidget.addToLayoutParams(tab2Sec1, 24, 3);
			    activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.SITE_ACTIVITY);
			    tab2Sec1.addWidget(activityWidget);

		}

        return page;
    }

	public static Page getBuildingPage(BuildingContext building, FacilioModule module) throws Exception {
		Page page = new Page();

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		addSecondaryDetailsWidget(tab1Sec1);
		addWeatherWidget(tab1Sec1);
		int energyCardHeight = 4;
		addEnergyWidget(tab1Sec1, energyCardHeight);
		int yPos = tab1Sec1.getLatestY() + energyCardHeight;
		addOperatingHoursWidget(tab1Sec1);
		addRelatedCountWidget(tab1Sec1, yPos, Arrays.asList(ContextNames.WORK_ORDER, ContextNames.NEW_READING_ALARM, ContextNames.ASSET));

		Section tab1Sec2 = page.new Section("buildingRelatedList");
		tab1.addSection(tab1Sec2);
		addBuildingRelatedListWidget(tab1Sec2);
		Section tab1Sec3 = page.new Section("spaceReadings");
		addReadingWidget(tab1Sec3);
		tab1.addSection(tab1Sec3);
		addCommonSubModuleWidget(tab1Sec3,module, building);

		if (AccountUtil.isFeatureEnabled(FeatureLicense.SAFETY_PLAN)) {
			addSafetyPlanTab(page);
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLASSIFICATION)){
			addClassificationTab(page,ContextNames.BUILDING_ACTIVITY);
		}

		Page.Tab tab2 = page.new Tab("History");
	    page.addTab(tab2);
	    Page.Section tab2Sec1 = page.new Section();
	    tab2.addSection(tab2Sec1);
	    PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
	    activityWidget.addToLayoutParams(tab2Sec1, 24, 3);
	    activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.BUILDING_ACTIVITY);
	    tab2Sec1.addWidget(activityWidget);
	    
		return page;
	}

	public static Page getFloorPage(FloorContext floor, FacilioModule module) throws Exception {
		Page page = new Page();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		if (floor.getDefaultFloorPlanId() > 0) {
			addFloorMapWidget(tab1Sec1);
		}
		addSecondaryDetailsWidget(tab1Sec1);
		PageWidget cardWidget = new PageWidget(WidgetType.RELATED_COUNT);
		cardWidget.addToLayoutParams(tab1Sec1, 16, 4);
		tab1Sec1.addWidget(cardWidget);
		addEnergyWidget(tab1Sec1, 4);
		Section tab1Sec2 = page.new Section();
		addSpaceRelatedListWidget(tab1Sec2, "floorSpaces",modBean);
		tab1.addSection(tab1Sec2);
		Section tab1Sec3 = page.new Section("spaceReadings");
		addReadingWidget(tab1Sec3);
		tab1.addSection(tab1Sec3);
		addCommonSubModuleWidget(tab1Sec3, module, floor);

		if (AccountUtil.isFeatureEnabled(FeatureLicense.SAFETY_PLAN)) {
			addSafetyPlanTab(page);
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLASSIFICATION)){
			addClassificationTab(page,ContextNames.FLOOR_ACTIVITY);
		}

		Page.Tab tab3 = page.new Tab("History");
	    page.addTab(tab3);
	    Page.Section tab3Sec1 = page.new Section();
	    tab3.addSection(tab3Sec1);
	    PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
	    activityWidget.addToLayoutParams(tab3Sec1, 24, 3);
	    activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.FLOOR_ACTIVITY);
	    tab3Sec1.addWidget(activityWidget);
		return page;
	}

	public static Page getSpacePage(SpaceContext space, FacilioModule module) throws Exception {
		Page page = new Page();

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		addSpacePrimaryReadingsWidget(tab1Sec1);
		addSecondaryDetailsWidget(tab1Sec1);
		PageWidget cardWidget = new PageWidget(WidgetType.RELATED_COUNT);
		cardWidget.addToLayoutParams(tab1Sec1, 16, 4);
		tab1Sec1.addWidget(cardWidget);
		addSpaceOccupancyWidget(tab1Sec1, 4);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (space.getSpace3() == null && space.getNoOfSubSpaces() > 0) {
			Section tab1Sec2 = page.new Section();
			addSpaceRelatedListWidget(tab1Sec2, "relatedSubSpaces",modBean);
			tab1.addSection(tab1Sec2);
		}
		Section tab1Sec3 = page.new Section("spaceReadings");
		addReadingWidget(tab1Sec3);
		addCommonSubModuleWidget(tab1Sec3, module, space);
		tab1.addSection(tab1Sec3);

		Tab tab2 = page.new Tab("Related");
		page.addTab(tab2);
		addRelationshipSection(page, tab2, module.getModuleId());
		FacilioModule baseSpaceModule = modBean.getModule(ContextNames.BASE_SPACE);
		Section tab1Sec6 = getRelatedListSectionObj(page);
		addRelatedListWidget(tab1Sec6, ContextNames.ASSET, baseSpaceModule.getModuleId(), "Assets");
		tab2.addSection(tab1Sec6);

		FacilioModule resourceModule = modBean.getModule(ContextNames.RESOURCE);
		Section tab1Sec7 = page.new Section();
		addRelatedListWidget(tab1Sec7, ContextNames.NEW_READING_ALARM, resourceModule.getModuleId(), "Alarms");
		tab2.addSection(tab1Sec7);

		Section tab1Sec4 = page.new Section("plannedWorkorder");
		PageWidget plannedWidget = new PageWidget(WidgetType.LIST, "plannedWorkorder");
		plannedWidget.addToLayoutParams(tab1Sec4, 24, 10);
		tab1Sec4.addWidget(plannedWidget);
		tab2.addSection(tab1Sec4);

		Section tab1Sec5 = page.new Section("unplannedWorkorder");
		PageWidget unplannedWidget = new PageWidget(WidgetType.LIST, "unPlannedWorkorder");
		unplannedWidget.addToLayoutParams(tab1Sec5, 24, 10);
		tab1Sec5.addWidget(unplannedWidget);
		tab2.addSection(tab1Sec5);

		if (AccountUtil.isFeatureEnabled(FeatureLicense.SAFETY_PLAN)) {
			addSafetyPlanTab(page);
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLASSIFICATION)){
			addClassificationTab(page,ContextNames.SPACE_ACTIVITY);
		}

		Page.Tab tab3 = page.new Tab("History");;
	    page.addTab(tab3);
	    Page.Section tab3Sec1 = page.new Section();
	    tab3.addSection(tab3Sec1);
	    PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
	    activityWidget.addToLayoutParams(tab3Sec1, 24, 3);
	    activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.SPACE_ACTIVITY);
	    tab3Sec1.addWidget(activityWidget);

		return page;
	}

	private static Page.Section addSafetyPlanTab(Page page) throws Exception {
		Page.Tab safetyPlanTab = page.new Tab("safety plan");
		page.addTab(safetyPlanTab);

		Page.Section safetyPlanSection = page.new Section();
		safetyPlanTab.addSection(safetyPlanSection);

		// hazards widget
		PageWidget hazards = new PageWidget(WidgetType.SAFETYPLAY_HAZARD);
		hazards.addToLayoutParams(0, 0, 24, 9);
		safetyPlanSection.addWidget(hazards);

		// precautions widget
		PageWidget widget = new PageWidget(WidgetType.LIST, "safetyPlanPrecautions");
		widget.addToLayoutParams(safetyPlanSection, 24, 9);
		safetyPlanSection.addWidget(widget);

		return safetyPlanSection;
	}

	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 4);
		section.addWidget(detailsWidget);
	}
	
	private static void addWeatherWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 8, 4);
		cardWidget.addCardType(CardType.WEATHER);
		section.addWidget(cardWidget);
	}

	private static void addEnergyWidget(Section section, int height) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 8, height);
		cardWidget.addCardType(CardType.ENERGY);
		section.addWidget(cardWidget);
	}
	
	private static void addOperatingHoursWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 8, 8);
		cardWidget.addCardType(CardType.OPERATING_HOURS);
		section.addWidget(cardWidget);
	}

	private static void addBuildingsWidget(long siteId, Section section, ModuleBean modBean) throws Exception {
		FacilioModule buildingModule = modBean.getModule(ContextNames.BUILDING);
		PageWidget pageWidget = new PageWidget(WidgetType.LIST, "siteBuildings");
		JSONObject relatedList = new JSONObject();
		relatedList.put("module", buildingModule);
		pageWidget.setRelatedList(relatedList);
		pageWidget.addToLayoutParams(section, 24, 10);
		pageWidget.addToWidgetParams(ContextNames.SKIP_MODULE_CRITERIA, true);
		section.addWidget(pageWidget);
	}


	private static void addSpacesWidget(Section section, ModuleBean modBean) throws Exception {
		FacilioModule module = modBean.getModule(ContextNames.SPACE);
		JSONObject relatedList = new JSONObject();
		relatedList.put("module", module);
		PageWidget widget = new PageWidget(WidgetType.LIST, "siteSpaces");
		widget.setRelatedList(relatedList);
		widget.addToLayoutParams(section, 24, 10);
		widget.addToWidgetParams(ContextNames.SKIP_MODULE_CRITERIA, true);
		section.addWidget(widget);
	}
	private static void addBuildingRelatedListWidget(Section section) throws Exception {
		PageWidget pageWidget = new PageWidget(WidgetType.LIST, "buildingSpaces");
		JSONObject relatedList = new JSONObject();
		pageWidget.setRelatedList(relatedList);
		pageWidget.addToLayoutParams(section, 24, 10);
		pageWidget.addToWidgetParams(ContextNames.SKIP_MODULE_CRITERIA, true);
		section.addWidget(pageWidget);
	}
	private static void addSpaceRelatedListWidget(Section section,String widgetName , ModuleBean modBean) throws Exception {
		FacilioModule module = modBean.getModule(ContextNames.SPACE);
		JSONObject relatedList = new JSONObject();
		relatedList.put("module", module);
		PageWidget pageWidget = new PageWidget(WidgetType.LIST, widgetName);
		pageWidget.setRelatedList(relatedList);
		pageWidget.addToLayoutParams(section, 24, 10);
		pageWidget.addToWidgetParams(ContextNames.SKIP_MODULE_CRITERIA, true);
		section.addWidget(pageWidget);
	}

	private static void addFloorMapWidget(Section section) {

		PageWidget pageWidget = new PageWidget(WidgetType.FLOOR_MAP);
		pageWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(pageWidget);
	}

	private static void addSpacePrimaryReadingsWidget(Section section){
		PageWidget pageWidget = new PageWidget(WidgetType.SPACE_PRIMARY_READINGS);
		pageWidget.addToLayoutParams(section,24,3);
		section.addWidget(pageWidget);
		if (AccountUtil.getCurrentOrg().getOrgId() == 343) {
			pageWidget.addToWidgetParams("workflowId", 56556);
		}

	}
	private static void addSpaceOccupancyWidget(Section section, int height) {
		PageWidget widget = new PageWidget(WidgetType.CARD);
		widget.addToLayoutParams(section, 8, height);
		widget.addToWidgetParams("type", CardType.OCCUPANCY.getName());
		section.addWidget(widget);
	}
	private static void addReadingWidget(Section section) {
		PageWidget readingsWidget = new PageWidget(WidgetType.LIST, "spaceReadings");
		readingsWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(readingsWidget);
	}
	private static void addClassificationTab(Page page,String activityModuleName){
		Page.Tab tab = page.new Tab("Specification");
		page.addTab(tab);
		Page.Section tab1Sec1 = page.new Section();
		tab.addSection(tab1Sec1);
		PageWidget classificationWidget = new PageWidget(PageWidget.WidgetType.CLASSIFICATION);
		classificationWidget.setName("Classification");
		classificationWidget.addToLayoutParams(tab1Sec1, 24, 8);
		classificationWidget.addToWidgetParams("activityModuleName", activityModuleName);
		tab1Sec1.addWidget(classificationWidget);
	}
 }
