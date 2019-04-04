package com.facilio.bmsconsole.page;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.page.Page.Column;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup.WidgetGroupType;
import com.facilio.constants.FacilioConstants;

public class PageFactory {

	private static final Map<String, Page> PAGE_MAP = Collections.unmodifiableMap(initPages());

	public static Page getPage(String moduleName) {
		return PAGE_MAP.get(moduleName);
	}

	private static Map<String, Page> initPages() {
		Map<String, Page> allPages = new HashMap<>();
		allPages.put(FacilioConstants.ContextNames.ASSET, getAssetPage());
		// allPages.put(FacilioConstants.ContextNames.CHILLER, getChillerPage());
		return allPages;
	}

	private static Page getAssetPage() {
		Page page = new Page();

		Column column1 = page.new Column();

		Section col1Sec1 = page.new Section();
		PageWidget pageWidget = new PageWidget(WidgetType.PRIMARY_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(24, 10);
		col1Sec1.addWidget(pageWidget);
		column1.addSection(col1Sec1);

		Section col1Sec2 = page.new Section();
		// col1Sec2.setName("overview");
		// col1Sec2.setDisplayName("common.page.overview");
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(24, 5);
		col1Sec2.addWidget(detailsWidget);
		column1.addSection(col1Sec2);

		Section col1Sec3 = page.new Section();
		column1.addSection(col1Sec3);

		WidgetGroup group1 = getCommonSubModuleGroup();
		col1Sec3.addWidgetGroup(group1);

		WidgetGroup group2 = new WidgetGroup(WidgetGroupType.TAB);
		group2.addToLayoutParams(24, 10);
		col1Sec3.addWidgetGroup(group2);

		PageWidget workorderWidget = getCountModuleWidget(FacilioConstants.ContextNames.WORK_ORDER);
		group2.addWidget(workorderWidget);
		PageWidget alarmWidget = getCountModuleWidget(FacilioConstants.ContextNames.ALARM);
		group2.addWidget(alarmWidget);
		PageWidget pmWidget = getListModuleWidget(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		group2.addWidget(pmWidget);
		PageWidget readingWidget = getListModuleWidget(FacilioConstants.ContextNames.READING);
		group2.addWidget(readingWidget);
		// PageWidget activityWidget = new PageWidget(WidgetType.ACTIVITY);
		// group2.addWidget(activityWidget);

		page.addColumns(column1);

		return page;
	}

	private static WidgetGroup getCommonSubModuleGroup() {
		WidgetGroup group = new WidgetGroup(WidgetGroupType.TAB);
		group.addToLayoutParams(24, 10);

		PageWidget notesWidget = new PageWidget();
		notesWidget.setWidgetType(WidgetType.COMMENT);
		// group.addToLayoutParams(24, 5);
		group.addWidget(notesWidget);

		PageWidget attachmentWidget = new PageWidget();
		attachmentWidget.setWidgetType(WidgetType.ATTACHMENT);
		// group.addToLayoutParams(24, 5);
		group.addWidget(attachmentWidget);

		return group;
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

		Column column2 = page.getColumns().get(1);
		column2.addSection(col2Sec2, 1);

		return page;
	}

	private static void addAssetPerformanceWidgets(Section section) {
		PageWidget cardWidget = new PageWidget();
		cardWidget.setWidgetType(WidgetType.CARD);

		cardWidget.addToLayoutParams(8, 2);

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
