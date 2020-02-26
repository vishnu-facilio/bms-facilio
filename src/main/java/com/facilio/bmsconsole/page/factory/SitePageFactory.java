package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.constants.FacilioConstants.ContextNames;

import java.util.Arrays;

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
		PageWidget detailsWidget = new PageWidget(WidgetType.FIXED_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 3);
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
		PageWidget pageWidget = new PageWidget(WidgetType.LIST, "siteBuildings");
		pageWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(pageWidget);
	}
	
	private static void addSpacesWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.LIST, "siteSpaces");
		cardWidget.addToLayoutParams(section, 24, 8);
		section.addWidget(cardWidget);
	}

}
