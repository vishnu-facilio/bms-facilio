package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.announcement.PeopleAnnouncementContext;
import com.facilio.modules.FacilioModule;
import org.apache.commons.lang.StringUtils;

public class PeopleAnnouncementPageFactory extends PageFactory{

    public static Page getPeopleAnnouncementPage(PeopleAnnouncementContext record, FacilioModule module) throws Exception {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        if (StringUtils.isNotEmpty(record.getLongDescription())) {
            PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.RICH_TEXT_PREVIEW);
            previewWidget.addToLayoutParams(tab1Sec1, 24, 6);
            previewWidget.addToWidgetParams("fieldKey", "longDescription");
            tab1Sec1.addWidget(previewWidget);
        }

        PageWidget secDetailsWidget = new PageWidget(PageWidget.WidgetType.ANNOUNCEMENT_SECONDARY_DETAILS_WIDGET);
        secDetailsWidget.addToLayoutParams(tab1Sec1, 24, 6);
        secDetailsWidget.setTitle("Announcement Details");
        tab1Sec1.addWidget(secDetailsWidget);

//        PageWidget publishToWidget = new PageWidget(PageWidget.WidgetType.ANNOUNCEMENT_PUBLISH_TO);
//        publishToWidget.addToLayoutParams(tab1Sec1, 24, 6);
//        publishToWidget.setTitle("Published To");
//        tab1Sec1.addWidget(publishToWidget);
        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENTS_PREVIEW);
        attachmentWidget.addToLayoutParams(tab1Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab1Sec1.addWidget(attachmentWidget);

        return page;
    }

}
