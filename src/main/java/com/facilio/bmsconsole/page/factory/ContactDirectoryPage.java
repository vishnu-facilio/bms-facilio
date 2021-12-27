package com.facilio.bmsconsole.page.factory;

import org.apache.commons.lang.StringUtils;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class ContactDirectoryPage extends PageFactory {
	public static Page getContactDirecoryPage(ContactDirectoryContext record, FacilioModule module) throws Exception {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        if (StringUtils.isNotEmpty(record.getContactName())) {
            PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.RICH_TEXT_PREVIEW);
            previewWidget.addToLayoutParams(tab1Sec1, 24, 6);
            previewWidget.addToWidgetParams("fieldKey", "description");
            tab1Sec1.addWidget(previewWidget);
        }

        PageWidget secDetailsWidget = new PageWidget(PageWidget.WidgetType.ANNOUNCEMENT_SECONDARY_DETAILS_WIDGET);
        secDetailsWidget.addToLayoutParams(tab1Sec1, 24, 6);
        secDetailsWidget.setTitle("Contact Details");
        tab1Sec1.addWidget(secDetailsWidget);


        PageWidget publishToWidget = new PageWidget(PageWidget.WidgetType.PUBLISH_TO_INFO);
        publishToWidget.addToLayoutParams(tab1Sec1, 24, 6);
        publishToWidget.setTitle("Published To");
        publishToWidget.addToWidgetParams("sharingInfoModuleName", FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY_SHARING);
        tab1Sec1.addWidget(publishToWidget);

        return page;
    }
}
