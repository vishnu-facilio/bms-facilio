package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class CustomModulePageFactory extends PageFactory {
	
	public static Page getCustomModulePage(ModuleBaseWithCustomFields record, FacilioModule module) throws Exception {
		Page page = new Page();
		
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		addSecondaryDetailsWidget(tab1Sec1);
		if (record == null) {
			return page;
		}
		addRelatedListWidgets(tab1Sec1, record.getModuleId());
		addCommonSubModuleWidget(tab1Sec1, module, record);

		return page;
	}

	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 7);
		section.addWidget(detailsWidget);
	}


}
