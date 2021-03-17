package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.constants.FacilioConstants;
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
		if(AccountUtil.getCurrentOrg().getOrgId() != 407l || AccountUtil.getCurrentApp().getAppCategoryEnum() != ApplicationContext.AppCategory.PORTALS) {
		  	addRelatedListWidgets(tab1Sec1, record.getModuleId());
		}
		addCommonSubModuleWidget(tab1Sec1, module, record);
		
		 Page.Tab tab2 = page.new Tab("Activity");
	        page.addTab(tab2);
	        Page.Section tab4Sec1 = page.new Section();
	        tab2.addSection(tab4Sec1);
	        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
	        activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
	        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
	        tab4Sec1.addWidget(activityWidget);
		return page;
	}

	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 7);
		// Temp..needs to move to db
		if (AccountUtil.getCurrentOrg().getOrgId() == 406) {
			detailsWidget.addToWidgetParams("sort", "form");
		}
		section.addWidget(detailsWidget);
	}


}
