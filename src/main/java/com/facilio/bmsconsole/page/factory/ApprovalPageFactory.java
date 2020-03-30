package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ApprovalPageFactory extends PageFactory {
	
	public static Page getApprovalPage(FacilioModule module, ModuleBaseWithCustomFields approval) throws Exception {
		Page page = new Page();

        Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        
        addApprovalFieldsWidget(tab1Sec1);
        addApproversWidget(tab1Sec1);
        addSecondaryDetailWidget(tab1Sec1);
        addCommonSubModuleGroup(tab1Sec1);

        return page;
	}
	
	private static void addApprovalFieldsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.APPROVAL_FIELDS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 4);
		section.addWidget(pageWidget);
	}

	private static void addApproversWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.APPROVERS);
		pageWidget.addToLayoutParams(section, 24, 6);
		section.addWidget(pageWidget);
	}

}
