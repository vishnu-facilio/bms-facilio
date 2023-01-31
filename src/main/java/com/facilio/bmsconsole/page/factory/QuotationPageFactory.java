package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class QuotationPageFactory extends PageFactory {

    public static Page getQuotationPage(QuotationContext record, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule quotationModule = modBean.getModule(FacilioConstants.ContextNames.QUOTE);

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.QUOTATION_PREVIEW);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 24);
        tab1Sec1.addWidget(previewWidget);


        Page.Tab tab2 = page.new Tab("Notes and Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addSecondaryDetailsWidget(tab2Sec1);
        addNotesAttachmentsModule(tab2Sec1);

        Page.Tab tab3 = page.new Tab("Related");
        addRelationshipSection(page, tab3, module.getModuleId());
        Page.Section tab3Sec1 = getRelatedListSectionObj(page);
        tab3.addSection(tab3Sec1);
        addAssociatedTermsWidget(tab3Sec1);
        addRelatedListWidgets(tab3Sec1, module.getModuleId());
        page.addTab(tab3);

        Page.Tab tab4 = page.new Tab("History");
        page.addTab(tab4);
        Page.Section tab4Sec1 = page.new Section();
        tab4.addSection(tab4Sec1);
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.QUOTE_ACTIVITY);
        tab4Sec1.addWidget(activityWidget);


        return page;
    }
    private static PageWidget addAssociatedTermsWidget(Page.Section section) {

        PageWidget associatedTermsWidget = new PageWidget();
        associatedTermsWidget.addToLayoutParams(section, 24, 7);
        associatedTermsWidget.setWidgetType(PageWidget.WidgetType.ASSOCIATED_TERMS);
        associatedTermsWidget.addToWidgetParams("moduleName",FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS);
        section.addWidget(associatedTermsWidget);

        return associatedTermsWidget;
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
}