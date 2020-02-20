package com.facilio.bmsconsole.page.factory;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants.ContextNames;

public class SitePageFactory extends PageFactory {
	
	public static Page getSitePage(SiteContext site) throws Exception {
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
        
        Section tab1Sec2 = page.new Section();
        addBuildingsWidget(site.getId(), tab1Sec2);
        tab1.addSection(tab1Sec2);
        
        Section tab1Sec3 = page.new Section();
        addSpacesWidget(tab1Sec3);
        tab1.addSection(tab1Sec3);
        return page;
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
	
	private static void addBuildingsWidget(long siteId, Section section) throws Exception {
		List<BuildingContext> buildings = SpaceAPI.getAllBuildings(siteId);
		if (CollectionUtils.isEmpty(buildings)) {
			return;
		}
		for(BuildingContext building : buildings) {
			
			PageWidget buildingWidget = new PageWidget(WidgetType.CARD);
			buildingWidget.addToLayoutParams(section, 5, 5);
			buildingWidget.addCardType(CardType.BUILDING);
			
			JSONObject obj = new JSONObject();
			obj.put("id", building.getId());
			obj.put("name", building.getName());
			obj.put("noOfFloors", building.getNoOfFloors());
			obj.put("area", building.getArea());
			obj.put("avatarUrl", building.getAvatarUrl());

			buildingWidget.addToWidgetParams("data", obj);
			section.addWidget(buildingWidget);
		}
	}
	
	private static void addSpacesWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 8, 8);
		cardWidget.addCardType(CardType.OPERATING_HOURS);
		section.addWidget(cardWidget);
	}

}
