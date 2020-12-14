package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.communityfeatures.DealsAndOffersContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NeighbourhoodContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NewsAndInformationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.commons.lang.StringUtils;

public class CommunityFeaturesPageFactory extends PageFactory {

    public static Page getNeighbourhoodPageFactory(NeighbourhoodContext record, FacilioModule module) {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        if (StringUtils.isNotEmpty(record.getDescription())) {
            PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.RICH_TEXT_PREVIEW);
            previewWidget.addToLayoutParams(tab1Sec1, 24, 6);
            previewWidget.addToWidgetParams("fieldKey", "description");
            tab1Sec1.addWidget(previewWidget);
        }

        PageWidget secDetailsWidget = new PageWidget(PageWidget.WidgetType.NEIGHBOURHOOD_DETAILS_WIDGET);
        secDetailsWidget.addToLayoutParams(tab1Sec1, 24, 6);
        secDetailsWidget.setTitle("Neighbourhood Details");
        tab1Sec1.addWidget(secDetailsWidget);

        if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomain.AppDomainType.FACILIO) {
            PageWidget publishToWidget = new PageWidget(PageWidget.WidgetType.PUBLISH_TO_INFO);
            publishToWidget.addToLayoutParams(tab1Sec1, 24, 6);
            publishToWidget.setTitle("Published To");
            publishToWidget.addToWidgetParams("sharingInfoModuleName", FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD_SHARING);
            tab1Sec1.addWidget(publishToWidget);
        }

        PageWidget dealsWidget = new PageWidget(PageWidget.WidgetType.NEIGHBOURHOOD_DEALS);
        dealsWidget.addToLayoutParams(tab1Sec1, 24, 6);
        dealsWidget.setTitle("Deals and Offers");
        tab1Sec1.addWidget(dealsWidget);

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENTS_PREVIEW);
        attachmentWidget.addToLayoutParams(tab1Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab1Sec1.addWidget(attachmentWidget);

        return page;
    }

    public static Page getDealsAndOffersPageFactory(DealsAndOffersContext record, FacilioModule module) {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        if (StringUtils.isNotEmpty(record.getDescription())) {
            PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.RICH_TEXT_PREVIEW);
            previewWidget.addToLayoutParams(tab1Sec1, 24, 6);
            previewWidget.addToWidgetParams("fieldKey", "description");
            tab1Sec1.addWidget(previewWidget);
        }
        PageWidget secDetailsWidget = new PageWidget(PageWidget.WidgetType.DEALS_DETAILS_WIDGET);
        secDetailsWidget.addToLayoutParams(tab1Sec1, 24, 6);
        secDetailsWidget.setTitle("Deals and Offers Details");
        tab1Sec1.addWidget(secDetailsWidget);

        if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomain.AppDomainType.FACILIO) {
            PageWidget publishToWidget = new PageWidget(PageWidget.WidgetType.PUBLISH_TO_INFO);
            publishToWidget.addToLayoutParams(tab1Sec1, 24, 6);
            publishToWidget.setTitle("Published To");
            publishToWidget.addToWidgetParams("sharingInfoModuleName", FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS_SHARING);
            tab1Sec1.addWidget(publishToWidget);
        }

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENTS_PREVIEW);
        attachmentWidget.addToLayoutParams(tab1Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab1Sec1.addWidget(attachmentWidget);

        return page;
    }

    public static Page getNewsAndInformationPageFactory(NewsAndInformationContext record, FacilioModule module) {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

        PageWidget secDetailsWidget = new PageWidget(PageWidget.WidgetType.NEWS_AND_INFORMATION);
        secDetailsWidget.addToLayoutParams(tab1Sec1, 24, 6);
        secDetailsWidget.setTitle("News & Information Details");
        secDetailsWidget.addToWidgetParams("fieldKey", "description");
        tab1Sec1.addWidget(secDetailsWidget);

        if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomain.AppDomainType.FACILIO) {
            PageWidget publishToWidget = new PageWidget(PageWidget.WidgetType.PUBLISH_TO_INFO);
            publishToWidget.addToLayoutParams(tab1Sec1, 24, 6);
            publishToWidget.setTitle("Published To");
            publishToWidget.addToWidgetParams("sharingInfoModuleName", FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION_SHARING);
            tab1Sec1.addWidget(publishToWidget);
        }

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENTS_PREVIEW);
        attachmentWidget.addToLayoutParams(tab1Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab1Sec1.addWidget(attachmentWidget);

        PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
        notesWidget.setTitle("Comments");
        notesWidget.addToWidgetParams("canShowNotifyRequestor", true);
        tab1Sec1.addWidget(notesWidget);

        return page;
    }

}
