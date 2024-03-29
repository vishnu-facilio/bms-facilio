package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class VendorQuotesPageFactory extends PageFactory{
    private static final Logger LOGGER = LogManager.getLogger(VendorQuotesPageFactory.class.getName());
    public static Page getVendorQuotesPage(V3VendorQuotesContext vendorQuotesContext, FacilioModule module) throws Exception {
        Page page = new Page();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Page.Section tab1Sec1 = page.new Section();
        PageWidget rfqDetailsWidget = new PageWidget(PageWidget.WidgetType.VENDOR_QUOTES_RFQ_SUMMARY_WIDGET);
        rfqDetailsWidget.addToLayoutParams(tab1Sec1, 24, 6);
        tab1Sec1.addWidget(rfqDetailsWidget);
        tab1.addSection(tab1Sec1);
        addVendorQuotesLineItemsWidget(tab1Sec1);

        Page.Tab tab2 = page.new Tab("Notes & Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addSecondaryDetailsWidget(tab2Sec1);
        addNotesAttachmentsModule(tab2Sec1);

        Page.Tab tab3 = page.new Tab("History");
        page.addTab(tab3);
        Page.Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        activityWidget.addToLayoutParams(tab3Sec1, 24, 3);
        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.VENDOR_QUOTES_ACTIVITY);
        tab3Sec1.addWidget(activityWidget);

        Page.Tab tab4 = page.new Tab("Related");
        boolean isRelationshipNeeded = addRelationshipSection(page, tab4, module.getModuleId());
        Page.Section tab4Sec1 = getRelatedListSectionObj(page);
        tab4.addSection(tab4Sec1);

        addRelatedListWidgets(tab4Sec1, module.getModuleId());

        if ((tab4Sec1.getWidgets() != null && !tab4Sec1.getWidgets().isEmpty()) || isRelationshipNeeded) {
            page.addTab(tab4);
        }

        return page;
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
    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 7);
        section.addWidget(detailsWidget);
    }
    private static PageWidget addVendorQuotesLineItemsWidget(Page.Section section) {

        PageWidget vendorQuotesLineItemsWidget = new PageWidget();
        vendorQuotesLineItemsWidget.addToLayoutParams(section, 24, 9);
        vendorQuotesLineItemsWidget.setWidgetType(PageWidget.WidgetType.VENDOR_QUOTES_LINE_ITEMS);
        section.addWidget(vendorQuotesLineItemsWidget);

        return vendorQuotesLineItemsWidget;
    }
}
