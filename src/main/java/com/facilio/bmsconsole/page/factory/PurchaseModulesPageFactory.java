package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class PurchaseModulesPageFactory extends PageFactory {

    public static Page getPrPage(V3PurchaseRequestContext record, FacilioModule module) throws Exception {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.PR_PREVIEW);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 24);
        tab1Sec1.addWidget(previewWidget);


        Page.Tab tab2 = page.new Tab("Notes & Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);

        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.FIXED_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(tab2Sec1, 24, 4);
        tab2Sec1.addWidget(detailsWidget);

        PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
        notesWidget.addToLayoutParams(tab2Sec1, 24, 8);
        notesWidget.setTitle("Notes");
        tab2Sec1.addWidget(notesWidget);

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        attachmentWidget.addToLayoutParams(tab2Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab2Sec1.addWidget(attachmentWidget);

        Page.Tab tab3 = page.new Tab("Related Records");
        page.addTab(tab3);
        Page.Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);
        addSubModuleRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS, module.getModuleId());


        return page;
    }


    public static Page getPoPage(V3PurchaseOrderContext record, FacilioModule module) throws Exception {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.PO_PREVIEW);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 24);
        tab1Sec1.addWidget(previewWidget);


        Page.Tab tab2 = page.new Tab("Notes & Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);

        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.FIXED_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(tab2Sec1, 24, 4);
        tab2Sec1.addWidget(detailsWidget);

        PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
        notesWidget.addToLayoutParams(tab2Sec1, 24, 8);
        notesWidget.setTitle("Notes");
        tab2Sec1.addWidget(notesWidget);

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        attachmentWidget.addToLayoutParams(tab2Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab2Sec1.addWidget(attachmentWidget);

        Page.Tab tab3 = page.new Tab("Related Records");
        page.addTab(tab3);
        Page.Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);
        addSubModuleRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS, module.getModuleId());
        addSubModuleRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.PURCHASE_REQUEST, module.getModuleId());


        return page;
    }


}
