package com.facilio.bmsconsole.page.factory;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsoleV3.context.V3ParkingStallContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class ParkingStallPageFactory extends PageFactory {
    public static Page getParkingStallPage(V3ParkingStallContext parkingstallContext, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        addSecondaryDetailsWidget(tab1Sec1);
        addCommonSubModuleWidget(tab1Sec1, module, parkingstallContext);

        Tab tab2 = page.new Tab("Related");
        boolean isRelationshipNeeded = addRelationshipSection(page, tab2, module.getModuleId());
		Section tab2Sec1 = getRelatedListSectionObj(page);
		tab2.addSection(tab2Sec1);
		addRelatedListWidgets(tab2Sec1, module.getModuleId());
		if(CollectionUtils.isNotEmpty(tab2Sec1.getWidgets()) || isRelationshipNeeded) {
			page.addTab(tab2);
		}
        
        Page.Tab tab3 = page.new Tab("History");;
		page.addTab(tab3);
		Page.Section tab4Sec1 = page.new Section();
		tab3.addSection(tab4Sec1);
		PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
		activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
		activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		tab4Sec1.addWidget(activityWidget);
        
        return page;
    }

    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 7);
        section.addWidget(detailsWidget);
    }

}
