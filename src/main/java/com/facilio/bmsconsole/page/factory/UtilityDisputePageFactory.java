package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.RoutesContext;
import com.facilio.modules.FacilioModule;
import com.facilio.utility.context.UtilityDisputeContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UtilityDisputePageFactory extends PageFactory {
    private static final Logger LOGGER = LogManager.getLogger(UtilityDisputePageFactory.class.getName());


    public static Page getUtilityDisputePage(UtilityDisputeContext Context, FacilioModule module) throws Exception {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        addSecondaryDetailsWidget(tab1Sec1);

        return page;
    }
    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 5);
        section.addWidget(detailsWidget);
    }
}
