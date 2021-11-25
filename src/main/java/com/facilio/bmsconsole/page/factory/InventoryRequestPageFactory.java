package com.facilio.bmsconsole.page.factory;

import java.util.List;
import java.util.stream.Collectors;

import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class InventoryRequestPageFactory extends PageFactory {
	public static Page getInventoryRequestPage(V3InventoryRequestContext inventoryRequest, FacilioModule module)
			throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Page page = new Page();

		Page.Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Page.Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);


		addSecondaryDetailsWidget(tab1Sec1);
		
		
        PageWidget card1= new PageWidget(PageWidget.WidgetType.INVENTORY_REQUEST_CARD1);
        card1.addToLayoutParams(tab1Sec1, 8, 6);
        tab1Sec1.addWidget(card1);
        
        PageWidget card2= new PageWidget(PageWidget.WidgetType.INVENTORY_REQUEST_CARD2);
        card2.addToLayoutParams(tab1Sec1, 8, 6);
        tab1Sec1.addWidget(card2);
        
        PageWidget card3= new PageWidget(PageWidget.WidgetType.INVENTORY_REQUEST_CARD3);
        card3.addToLayoutParams(tab1Sec1, 8, 6);
        tab1Sec1.addWidget(card3);

		Page.Section tab1Sec2 = page.new Section();
		tab1.addSection(tab1Sec2);
		addLineItemsWidget(tab1Sec1);

		Page.Section tab1Sec3 = page.new Section();
		tab1.addSection(tab1Sec3);

		addCommonSubModuleWidget(tab1Sec3, module, inventoryRequest);

		return page;
	}

	private static void addSecondaryDetailsWidget(Page.Section section) {
		PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 7);
		section.addWidget(detailsWidget);
	}
	private static PageWidget addLineItemsWidget(Page.Section section) {

		PageWidget purchasedItemsWidget = new PageWidget();
		purchasedItemsWidget.addToLayoutParams(section, 24, 7);
		purchasedItemsWidget.setWidgetType(PageWidget.WidgetType.INVENTORY_REQUEST_LINE_ITEMS);
		section.addWidget(purchasedItemsWidget);

		return purchasedItemsWidget;
	}
}
