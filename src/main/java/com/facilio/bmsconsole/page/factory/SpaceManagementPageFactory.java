package com.facilio.bmsconsole.page.factory;

import static com.facilio.bmsconsole.page.factory.AssetPageFactory.addRelatedListWidget;

import java.util.Arrays;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class SpaceManagementPageFactory extends PageFactory {
	
	public static Page getSitePage(SiteContext site, FacilioModule module) throws Exception {
        Page page = new Page();

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

		Tab tab2 = page.new Tab("Related Records");
		page.addTab(tab2);
		FacilioModule baseSpaceModule = modBean.getModule(ContextNames.BASE_SPACE);
		Section tab1Sec6 = page.new Section();
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



		return page;
	}

	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.FIXED_DETAILS_WIDGET);
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
		section.addWidget(pageWidget);
	}


	private static void addSpacesWidget(Section section, ModuleBean modBean) throws Exception {
		FacilioModule module = modBean.getModule(ContextNames.SPACE);
		JSONObject relatedList = new JSONObject();
		relatedList.put("module", module);
		PageWidget widget = new PageWidget(WidgetType.LIST, "siteSpaces");
		widget.setRelatedList(relatedList);
		widget.addToLayoutParams(section, 24, 10);
		section.addWidget(widget);
	}
	private static void addBuildingRelatedListWidget(Section section) throws Exception {
		PageWidget pageWidget = new PageWidget(WidgetType.LIST, "buildingSpaces");
		JSONObject relatedList = new JSONObject();
		pageWidget.setRelatedList(relatedList);
		pageWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(pageWidget);
	}
	private static void addSpaceRelatedListWidget(Section section,String widgetName , ModuleBean modBean) throws Exception {
		FacilioModule module = modBean.getModule(ContextNames.SPACE);
		JSONObject relatedList = new JSONObject();
		relatedList.put("module", module);
		PageWidget pageWidget = new PageWidget(WidgetType.LIST, widgetName);
		pageWidget.setRelatedList(relatedList);
		pageWidget.addToLayoutParams(section, 24, 10);
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
 }
