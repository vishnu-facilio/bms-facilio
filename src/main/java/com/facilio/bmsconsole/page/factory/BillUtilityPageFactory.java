package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup.WidgetGroupType;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class BillUtilityPageFactory extends PageFactory {
	public static Page getBillUtilityPage(ModuleBaseWithCustomFields record, FacilioModule module) throws Exception {
		Page page = new Page();
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
	

		
		PageWidget cards= new PageWidget(PageWidget.WidgetType.UTILITY_DETAILS);
		cards.addToLayoutParams(tab1Sec1, 24, 4);
		tab1Sec1.addWidget(cards);
		
		PageWidget electriycity= new PageWidget(PageWidget.WidgetType.UTILITY_CONNECTIONS);
		electriycity.addToLayoutParams(tab1Sec1, 24, 10);
		tab1Sec1.addWidget(electriycity);
		
		
		Page.Tab tab2 = page.new Tab("ACTIVE PAYMENTS");
		page.addTab(tab2);
		Page.Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		
		PageWidget payments= new PageWidget(PageWidget.WidgetType.UTILITY_ACTIVITY_PAYMENT);
		payments.addToLayoutParams(tab2Sec1, 24, 24);
		tab2Sec1.addWidget(payments);

		
		
		Page.Tab tab3 = page.new Tab("TRANSACTIONS");
		page.addTab(tab3);
		Page.Section tab3Sec1 = page.new Section();
		tab3.addSection(tab3Sec1);
		
		PageWidget transaction= new PageWidget(PageWidget.WidgetType.UTILITY_TRANSACTIONS);
		transaction.addToLayoutParams(tab3Sec1, 24, 24);
		tab3Sec1.addWidget(transaction);
		
		
		
		Page.Tab tab4 = page.new Tab("CANCELLED PAYMENTS");
		page.addTab(tab4);
		Page.Section tab4Sec1 = page.new Section();
		tab4.addSection(tab4Sec1);
		
		
		PageWidget cancelPayments= new PageWidget(PageWidget.WidgetType.UTILITY_CANCEL_PAYMENT);
		cancelPayments.addToLayoutParams(tab4Sec1, 24, 24);
		tab4Sec1.addWidget(cancelPayments);
		
		

		
		
		Page.Tab tab5 = page.new Tab("ALL ACTIVITIES");
		page.addTab(tab5);
		Page.Section tab5Sec1 = page.new Section();
		tab5.addSection(tab5Sec1);
		
		PageWidget allactivities = new PageWidget(PageWidget.WidgetType.UTILITY_ALL_ACTIVITIES);
		allactivities.addToLayoutParams(tab5Sec1, 24, 24);
		tab5Sec1.addWidget(allactivities);
		
		

		return page;
	}

}
