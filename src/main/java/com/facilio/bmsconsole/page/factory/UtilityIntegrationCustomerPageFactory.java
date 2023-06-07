
package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class UtilityIntegrationCustomerPageFactory extends PageFactory{

    private static final Logger LOGGER = LogManager.getLogger(UtilityIntegrationCustomerPageFactory.class.getName());

    public static Page getUtilityCustomerPage(UtilityIntegrationCustomerContext customer, FacilioModule module) throws Exception {
        Page page = new Page();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule utilityIntegrationCustomermodule = modBean.getModule(customer.getModuleId());

        Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

        addSecondaryDetailsWidget(tab1Sec1);

        int yOffset = 0;

        //Meter List
        PageWidget meterWidget = new PageWidget(WidgetType.METER_WIDGET);
        meterWidget.addToLayoutParams(0, 6 + yOffset, 24, 8);

        JSONObject moduleData = new JSONObject();
        moduleData.put("summaryWidgetName", "meterWidget");

        meterWidget.setWidgetParams(moduleData);
        meterWidget.setRelatedList(moduleData);
        tab1Sec1.addWidget(meterWidget);


        Section tab1Sec3 = page.new Section();
        tab1.addSection(tab1Sec3);

        PageWidget subModuleGroup = new PageWidget(WidgetType.GROUP);
        subModuleGroup.addToLayoutParams(tab1Sec3, 24, 8);
        subModuleGroup.addToWidgetParams("type", WidgetGroup.WidgetGroupType.TAB);
        tab1Sec3.addWidget(subModuleGroup);

        PageWidget notesWidget = new PageWidget();
        notesWidget.setWidgetType(WidgetType.COMMENT);
        subModuleGroup.addToWidget(notesWidget);

      // addCommonSubModuleWidget(tab1Sec1, utilityIntegrationCustomermodule, customer);

        Page.Tab tab2 = page.new Tab("BILLS");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);

        addSubModuleRelatedListWidget(tab2Sec1, FacilioConstants.UTILITY_INTEGRATION_BILLS, module.getModuleId());


        //history tab
        page.addTab(composeHistoryTab(page));


        return page;
    }


    private static void addSecondaryDetailsWidget(Section section) {
        PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 5);
        section.addWidget(detailsWidget);
    }

    private static Page.Tab composeHistoryTab(Page page) {
        Page.Tab historyTab = page.new Tab("History");
        Page.Section primarySection = page.new Section();
        historyTab.addSection(primarySection);
        PageWidget historyWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        historyWidget.addToWidgetParams("activityModuleName", FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ACTIVITY);
        historyWidget.addToLayoutParams(primarySection, 24, 3);
        primarySection.addWidget(historyWidget);

        return historyTab;
    }

}



