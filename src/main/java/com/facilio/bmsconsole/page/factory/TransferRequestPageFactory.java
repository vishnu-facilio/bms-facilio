package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TransferRequestPageFactory extends PageFactory{
    private static final Logger LOGGER = LogManager.getLogger(TransferRequestPageFactory.class.getName());
    public static Page getTransferRequestPage(V3TransferRequestContext transferrequest, FacilioModule module) throws Exception {
        Page page = new Page();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

        if (transferrequest.getDescription() != null && !transferrequest.getDescription().isEmpty()) {
            PageWidget descWidget = new PageWidget(PageWidget.WidgetType.DESCRIPTION_CARD);
            descWidget.addToLayoutParams(tab1Sec1,24, 3);
            tab1Sec1.addWidget(descWidget);
        }
        Page.Section tab1Sec2 = page.new Section();
        tab1.addSection(tab1Sec2);
        PageWidget card1= new PageWidget(PageWidget.WidgetType.TRANSFER_REQUEST_CARD1);
        card1.addToLayoutParams(tab1Sec2, 8, 6);
        card1.addToWidgetParams("card","transferrequestcard1");
        tab1Sec2.addWidget(card1);

        PageWidget card2= new PageWidget(PageWidget.WidgetType.TRANSFER_REQUEST_CARD2);
        card2.addToLayoutParams(tab1Sec2, 8, 6);
        card2.addToWidgetParams("card","transferrequestcard2");
        tab1Sec2.addWidget(card2);

        PageWidget card3= new PageWidget(PageWidget.WidgetType.TRANSFER_REQUEST_CARD3);
        card3.addToLayoutParams(tab1Sec2, 8, 6);
        card3.addToWidgetParams("card","transferrequestcard3");
        tab1Sec2.addWidget(card3);

        Page.Section tab1Sec3 = page.new Section();
        tab1.addSection(tab1Sec3);
        addPurchasedItemsWidget(tab1Sec3);

        Page.Tab tab2 = page.new Tab("Notes & Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addSecondaryDetailsWidget(tab2Sec1);
        addNotesAttachmentsModule(tab2Sec1);

        return page;
    }

    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 7);
        section.addWidget(detailsWidget);
    }

    private static PageWidget addNotesAttachmentsModule(Page.Section section) {

        PageWidget subModuleGroup = new PageWidget(PageWidget.WidgetType.GROUP);
        subModuleGroup.addToLayoutParams(section, 24, 8);
        subModuleGroup.addToWidgetParams("type", WidgetGroup.WidgetGroupType.TAB);
        section.addWidget(subModuleGroup);

        PageWidget notesWidget = new PageWidget();
        notesWidget.setWidgetType(PageWidget.WidgetType.COMMENT);
        subModuleGroup.addToWidget(notesWidget);

        PageWidget attachmentWidget = new PageWidget();
        attachmentWidget.setWidgetType(PageWidget.WidgetType.ATTACHMENT);
        subModuleGroup.addToWidget(attachmentWidget);

        return subModuleGroup;
    }
    private static PageWidget addPurchasedItemsWidget(Page.Section section) {

        PageWidget purchasedItemsWidget = new PageWidget();
        purchasedItemsWidget.addToLayoutParams(section, 24, 7);
        purchasedItemsWidget.setWidgetType(PageWidget.WidgetType.TRANSFER_REQUEST_LINE_ITEMS);
        section.addWidget(purchasedItemsWidget);

        return purchasedItemsWidget;
    }
}
