package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;
import com.facilio.modules.FacilioModule;

public class AnnouncementPageFactory extends PageFactory {

    public static Page getAnnouncementPage(AnnouncementContext record, FacilioModule module) throws Exception {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.ANNOUNCEMENT_PRIMARY_DETAILS_WIDGET);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 6);
        tab1Sec1.addWidget(previewWidget);

        PageWidget secDetailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        secDetailsWidget.addToLayoutParams(tab1Sec1, 24, 6);
        secDetailsWidget.setTitle("Announcement Details");
        tab1Sec1.addWidget(secDetailsWidget);


        PageWidget publishToWidget = new PageWidget(PageWidget.WidgetType.ANNOUNCEMENT_PUBLISH_TO);
        publishToWidget.addToLayoutParams(tab1Sec1, 24, 6);
        publishToWidget.setTitle("Published To");
        tab1Sec1.addWidget(publishToWidget);

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        attachmentWidget.addToLayoutParams(tab1Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab1Sec1.addWidget(attachmentWidget);

        return page;
    }


}
