package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class BillTariffPageFactory extends PageFactory {
	public static Page getBillTariffPage(ModuleBaseWithCustomFields record, FacilioModule module) throws Exception {
		Page page = new Page();
		
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		if (record == null) {
			return page;
        }
		
	    PageWidget tariffDetails= new PageWidget(PageWidget.WidgetType.TARIFF_DETAILS);
        tariffDetails.addToLayoutParams(tab1Sec1, 24, 4);
        tab1Sec1.addWidget(tariffDetails);
         
		Section tab1Sec2 = page.new Section("TARIFF INFORMATION");
		tab1.addSection(tab1Sec2);
		
	     PageWidget tariffInfo= new PageWidget(PageWidget.WidgetType.TARIFF_INFORMATION);
	     tariffInfo.addToLayoutParams(tab1Sec2, 24, 10);
	      tab1Sec2.addWidget(tariffInfo);
		return page;
	}
}
