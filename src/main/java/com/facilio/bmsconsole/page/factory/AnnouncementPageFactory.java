package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.commons.lang.StringUtils;

public class AnnouncementPageFactory extends PageFactory {

    public static Page getAnnouncementPage(AnnouncementContext record, FacilioModule module) throws Exception {
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


        PageWidget publishToWidget = new PageWidget(PageWidget.WidgetType.PUBLISH_TO_INFO);
        publishToWidget.addToLayoutParams(tab1Sec1, 24, 6);
        publishToWidget.setTitle("Published To");
        publishToWidget.addToWidgetParams("sharingInfoModuleName", FacilioConstants.ContextNames.Tenant.ANNOUNCEMENTS_SHARING_INFO);
        tab1Sec1.addWidget(publishToWidget);

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENTS_PREVIEW);
        attachmentWidget.addToLayoutParams(tab1Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab1Sec1.addWidget(attachmentWidget);

        return page;
    }


}
