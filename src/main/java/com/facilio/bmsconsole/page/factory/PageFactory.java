package com.facilio.bmsconsole.page.factory;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup.WidgetGroupType;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class PageFactory {

//	private static final Map<String, Page> PAGE_MAP = Collections.unmodifiableMap(initPages());

	public static Page getPage(String moduleName, ModuleBaseWithCustomFields record) {
//		return PAGE_MAP.get(moduleName);
		switch(moduleName) {
			case ContextNames.ASSET:
				return AssetPageFactory.getAssetPage((AssetContext) record);
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
	

	protected static PageWidget addCommonSubModuleGroup(Section section) {

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
	
	protected static void addHistoryWidget(Section section) {
		PageWidget historyWidget = new PageWidget(WidgetType.HISTORY);
		historyWidget.addToLayoutParams(section, 24, 24);
		section.addWidget(historyWidget);
	}

	protected static PageWidget getCountModuleWidget(String module) {
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
