package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.modules.FacilioModule;

public class InviteVisitorPageFactory extends PageFactory {

    public static Page getInvitesPage(InviteVisitorContextV3 inviteVisitor, FacilioModule module) throws Exception {
    	Page page = new Page();

		Tab tab1 = page.new Tab("summary");

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		PageWidget detailsWidget = new PageWidget(WidgetType.INVITES_PRIMARY_FIELDS);
		detailsWidget.addToLayoutParams(tab1Sec1, 24, 4);
		tab1Sec1.addWidget(detailsWidget);

 		page.addTab(tab1);
 	    
 	    Section tab1Sec2 = page.new Section();
 		tab1.addSection(tab1Sec2);
 		addSecondaryDetailsWidget(tab1Sec2);
 	
 		return page;
     }

     private static void addSecondaryDetailsWidget(Section section) {
 		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
 		detailsWidget.addToLayoutParams(section, 24, 4);
 		section.addWidget(detailsWidget);
 	}

 }
