package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class AudiencePageFactory extends PageFactory{
    public static Page getAudiencePage(AudienceContext record, FacilioModule module) {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

        PageWidget secDetailsWidget = new PageWidget(PageWidget.WidgetType.ANNOUNCEMENT_SECONDARY_DETAILS_WIDGET);
        secDetailsWidget.addToLayoutParams(tab1Sec1, 24, 6);
        secDetailsWidget.setTitle("Audience Details");
        tab1Sec1.addWidget(secDetailsWidget);


        PageWidget publishToWidget = new PageWidget(PageWidget.WidgetType.PUBLISH_TO_INFO);
        publishToWidget.addToLayoutParams(tab1Sec1, 24, 6);
        publishToWidget.setTitle("Publish To");
        publishToWidget.addToWidgetParams("sharingInfoModuleName", FacilioConstants.ContextNames.Tenant.ANNOUNCEMENTS_SHARING_INFO);
        tab1Sec1.addWidget(publishToWidget);

        return page;
    }
}
