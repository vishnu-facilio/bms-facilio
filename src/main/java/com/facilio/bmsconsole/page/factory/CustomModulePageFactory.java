package com.facilio.bmsconsole.page.factory;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class CustomModulePageFactory extends PageFactory {
	
	public static Page getCustomModulePage(ModuleBaseWithCustomFields record) {
		Page page = new Page();
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1); 
		
		addSecondaryDetailsWidget(tab1Sec1);
		addCommonSubModuleGroup(tab1Sec1);
		addRelatedListWidget(tab1Sec1);
		addRelatedListWidget1(tab1Sec1);
		
		
		return page;
		
	}
		
	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(detailsWidget);
	}
	
	private static void addRelatedListWidget(Section section) {
		PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
		JSONObject relatedList = new JSONObject();
		relatedList.put("moduleName", "students");
		relatedList.put("fieldName", "Lookup");
		relatedListWidget.setRelatedList(relatedList);
		relatedListWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(relatedListWidget);
	}
	
	private static void addRelatedListWidget1(Section section) {
		PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
		JSONObject relatedList = new JSONObject();
		relatedList.put("moduleName", "customers");
		relatedList.put("fieldName", "Lookup");
		relatedListWidget.setRelatedList(relatedList);
		relatedListWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(relatedListWidget);
	}
	
}
