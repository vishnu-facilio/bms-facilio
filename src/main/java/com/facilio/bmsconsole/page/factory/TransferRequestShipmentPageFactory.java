package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestShipmentContext;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TransferRequestShipmentPageFactory extends PageFactory{
    private static final Logger LOGGER = LogManager.getLogger(TransferRequestShipmentPageFactory.class.getName());
    public static Page getTransferRequestShipmentPage(V3TransferRequestShipmentContext transferrequestshipment, FacilioModule module) throws Exception {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        addSecondaryDetailsWidget(tab1Sec1);

        Page.Section tab1Sec2 = page.new Section();
        tab1.addSection(tab1Sec2);
        addShipmentReceivablesWidget(tab1Sec2);

        Page.Section tab1Sec3 = page.new Section();
        tab1.addSection(tab1Sec3);
        addNotesAttachmentsModule(tab1Sec3);

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
    private static PageWidget addShipmentReceivablesWidget(Page.Section section) {

        PageWidget shipmentReceivablesWidget = new PageWidget();
        shipmentReceivablesWidget.addToLayoutParams(section, 24, 7);
        shipmentReceivablesWidget.setWidgetType(PageWidget.WidgetType.TRANSFER_REQUEST_SHIPMENT_RECEIVABLES);
        section.addWidget(shipmentReceivablesWidget);

        return shipmentReceivablesWidget;
    }
}
