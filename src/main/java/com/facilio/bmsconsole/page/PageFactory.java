package com.facilio.bmsconsole.page;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup.WidgetGroupType;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class PageFactory {

//	private static final Map<String, Page> PAGE_MAP = Collections.unmodifiableMap(initPages());

	public static Page getPage(String moduleName) {
//		return PAGE_MAP.get(moduleName);
		switch(moduleName) {
			case ContextNames.ASSET:
				return getAssetPage();
		}
		return null;
	}

	/*private static Map<String, Page> initPages() {
		Map<String, Page> allPages = new HashMap<>();
		allPages.put(FacilioConstants.ContextNames.ASSET, getAssetPage());
		// allPages.put(FacilioConstants.ContextNames.CHILLER, getChillerPage());
		return allPages;
	}
*/
	private static Page getAssetPage() {
		Page page = new Page();

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		PageWidget pageWidget = new PageWidget(WidgetType.PRIMARY_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(tab1Sec1, 24, 9);
		pageWidget.addToWidgetParams("showOperatingHours", true);
		tab1Sec1.addWidget(pageWidget);
		
		PageWidget workorderWidget = getCountModuleWidget(FacilioConstants.ContextNames.WORK_ORDER);
		workorderWidget.addToLayoutParams(tab1Sec1, 7, 11);
		tab1Sec1.addWidget(workorderWidget);
		
		PageWidget alarmWidget = getCountModuleWidget(FacilioConstants.ContextNames.ALARM);
		alarmWidget.addToLayoutParams(tab1Sec1, 7, 11);
		tab1Sec1.addWidget(alarmWidget);
		
		PageWidget fddWidget = new PageWidget(WidgetType.CARD);
		fddWidget.addToLayoutParams(tab1Sec1, 10, 11);
		fddWidget.addToWidgetParams("type", CardType.FAILURE_METRICS.getName());
		tab1Sec1.addWidget(fddWidget);
		

		// col1Sec2.setName("overview");
		// col1Sec2.setDisplayName("common.page.overview");
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(tab1Sec1, 24, 5);
		tab1Sec1.addWidget(detailsWidget);
		
		addCommonSubModuleGroup(tab1Sec1);

//		PageWidget readingWidget = getListModuleWidget(FacilioConstants.ContextNames.READING);
//		readingWidget.addToLayoutParams(col1Sec3, 24, 10);
//		col1Sec3.addWidget(readingWidget);
		
		Tab tab2 = page.new Tab("maintenance");
		page.addTab(tab2);
		
		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		
		PageWidget nextPmWidget = new PageWidget(WidgetType.CARD);
		nextPmWidget.addToLayoutParams(tab2Sec1, 8, 9);
		nextPmWidget.addToWidgetParams("type", CardType.NEXT_PM.getName());
		tab2Sec1.addWidget(nextPmWidget);
		
		PageWidget woDetailsWidget = new PageWidget(WidgetType.CARD);
		woDetailsWidget.addToLayoutParams(tab2Sec1, 8, 9);
		woDetailsWidget.addToWidgetParams("type", CardType.WO_DETAILS.getName());
		tab2Sec1.addWidget(woDetailsWidget);
		
		PageWidget recentlyClosedWidget = new PageWidget(WidgetType.CARD);
		recentlyClosedWidget.addToLayoutParams(tab2Sec1, 8, 9);
		recentlyClosedWidget.addToWidgetParams("type", CardType.RECENTLY_CLOSED_PM.getName());
		tab2Sec1.addWidget(recentlyClosedWidget);
		
		Section tab2Sec2 = page.new Section();
		tab2.addSection(tab2Sec2);
		
		PageWidget plannedWidget = new PageWidget(WidgetType.LIST, "plannedWorkorder");
		plannedWidget.addToLayoutParams(tab2Sec2, 24, 10);
		tab2Sec2.addWidget(plannedWidget);
		
		PageWidget unPlannedWidget = new PageWidget(WidgetType.LIST, "unPlannedWorkorder");
		unPlannedWidget.addToLayoutParams(tab2Sec2, 24, 10);
		tab2Sec2.addWidget(unPlannedWidget);
		
		Tab tab3 = page.new Tab("readings");
		page.addTab(tab3);
		
		Section tab3Sec1 = page.new Section();
		tab3.addSection(tab3Sec1);
		
		PageWidget readingsWidget = new PageWidget(WidgetType.LIST, "readings");
		readingsWidget.addToLayoutParams(tab3Sec1, 24, 10);
		tab3Sec1.addWidget(readingsWidget);
		
		Section tab3Sec2 = page.new Section("commands");
		tab3.addSection(tab3Sec2);
		
		
		
		Tab tab4 = page.new Tab("performace");
		page.addTab(tab4);
		
		Section tab4Sec1 = page.new Section();
		tab4.addSection(tab4Sec1);
		
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(tab4Sec1, 24, 3);
		cardWidget.addToWidgetParams("type", CardType.WO_DETAILS.getName());
		tab4Sec1.addWidget(cardWidget);
		
		cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(tab4Sec1, 24, 8);
		cardWidget.addToWidgetParams("type", CardType.ASSET_LIFE.getName());
		tab4Sec1.addWidget(cardWidget);
		
		cardWidget = new PageWidget(WidgetType.CARD, "lastReportedDownTime");
		cardWidget.addToLayoutParams(tab4Sec1, 12, 3);
		cardWidget.addToWidgetParams("type", CardType.LAST_DOWNTIME.getName());
		tab4Sec1.addWidget(cardWidget);
		
		cardWidget = new PageWidget(WidgetType.CARD, "overallDownTime");
		cardWidget.addToLayoutParams(tab4Sec1, 12, 3);
		cardWidget.addToWidgetParams("type", CardType.OVERALL_DOWNTIME.getName());
		tab4Sec1.addWidget(cardWidget);
		
		cardWidget = new PageWidget(WidgetType.CHART);
		cardWidget.addToLayoutParams(tab4Sec1, 12, 6);
		cardWidget.addToWidgetParams("type", CardType.FAILURE_RATE.getName());
		tab4Sec1.addWidget(cardWidget);
		
		cardWidget = new PageWidget(WidgetType.CHART);
		cardWidget.addToLayoutParams(tab4Sec1, 12, 6);
		cardWidget.addToWidgetParams("type", CardType.AVG_TTR.getName());
		tab4Sec1.addWidget(cardWidget);
		

		
		
		Tab tab5 = page.new Tab("history");
		page.addTab(tab5);
		
		Section tab5Sec1 = page.new Section();
		tab5.addSection(tab5Sec1);
		
		PageWidget historyWidget = new PageWidget(WidgetType.HISTORY);
		historyWidget.addToLayoutParams(tab5Sec1, 24, 24);
		tab5Sec1.addWidget(historyWidget);

		return page;
	}

	private static PageWidget addCommonSubModuleGroup(Section section) {

		PageWidget subModuleGroup = new PageWidget(WidgetType.GROUP);
		subModuleGroup.addToLayoutParams(section, 24, 10);
		subModuleGroup.addToWidgetParams("type", WidgetGroupType.TAB);
		section.addWidget(subModuleGroup);
		
		PageWidget notesWidget = new PageWidget();
		notesWidget.setWidgetType(WidgetType.COMMENT);
		subModuleGroup.addToWidget(notesWidget);
		
		PageWidget attachmentWidget = new PageWidget();
		attachmentWidget.setWidgetType(WidgetType.ATTACHMENT);
		subModuleGroup.addToWidget(attachmentWidget);

		return subModuleGroup;
	}

	private static PageWidget getCountModuleWidget(String module) {
		PageWidget widget = new PageWidget();
		widget.setWidgetType(WidgetType.COUNT);
		widget.addToWidgetParams("module", module);
		return widget;
	}

	private static PageWidget getListModuleWidget(String module) {
		PageWidget widget = new PageWidget();
		widget.setWidgetType(WidgetType.LIST);
		widget.addToWidgetParams("module", module);
		return widget;
	}

	private static Page getChillerPage() {
		Page page = getAssetPage();

		Section col2Sec2 = page.new Section();
		col2Sec2.setDisplayName("Asset Performance");
		addAssetPerformanceWidgets(col2Sec2);

//		Column column2 = page.getColumns().get(1);
//		column2.addSection(col2Sec2, 1);

		return page;
	}

	private static void addAssetPerformanceWidgets(Section section) {
		PageWidget cardWidget = new PageWidget();
		cardWidget.setWidgetType(WidgetType.CARD);

		cardWidget.addToLayoutParams(section, 8, 2);

		addReadingCard(cardWidget, "runHours", "chillerreading");

		section.addWidget(cardWidget);
	}

	private static void addReadingCard(PageWidget cardWidget, String fieldName, String moduleName) {
		addReadingCard(cardWidget, fieldName, moduleName, 3, 22, 20);
	}
	
	private static void addReadingCard(PageWidget cardWidget, String fieldName, String moduleName, long aggregateFunc, long dateOperator, long xAggr) {
		cardWidget.addToWidgetParams("staticKey", "readingWithGraphCard");
		JSONObject paramsJson = new JSONObject();
		paramsJson.put("aggregateFunc", aggregateFunc);
		paramsJson.put("dateOperator", dateOperator);
		paramsJson.put("fieldName", fieldName);
		paramsJson.put("moduleName", moduleName);
		paramsJson.put("xAggr", xAggr);
		cardWidget.addToWidgetParams("paramsJson", paramsJson);
	}

}
