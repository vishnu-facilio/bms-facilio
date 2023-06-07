package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.utility.context.UtilityIntegrationBillContext;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UtilityIntegrationBillPageFactory extends PageFactory{

    private static final Logger LOGGER = LogManager.getLogger(UtilityIntegrationBillPageFactory.class.getName());

    public static Page getUtilityBillPage(UtilityIntegrationBillContext bill, FacilioModule module) throws Exception {
        Page page = new Page();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule utilityIntegrationBillmodule = modBean.getModule(bill.getModuleId());


        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.UTILITY_INTEGRATION_BILL_PREVIEW);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 24);
        tab1Sec1.addWidget(previewWidget);

        Page.Tab tab2 = page.new Tab("Notes & Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addSecondaryDetailsWidget(tab2Sec1);
        addNotesAttachmentsModule(tab2Sec1);

        addRelatedRecordsTab(page, utilityIntegrationBillmodule.getModuleId());

//        page.addTab(composeHistoryTab(page));


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

    private static void addRelatedRecordsTab(Page page, long moduleID) throws Exception {
        Page.Tab relatedRecordsTab = page.new Tab("Related");
        addRelationshipSection(page, relatedRecordsTab, moduleID);
        page.addTab(relatedRecordsTab);

        Page.Section relatedRecordsSection = getRelatedListSectionObj(page);
        relatedRecordsTab.addSection(relatedRecordsSection);

        // related records widget
        PageWidget relatedRecords = new PageWidget(PageWidget.WidgetType.RELATED_RECORDS);
        relatedRecords.addToLayoutParams(relatedRecordsSection, 24, 8);
        relatedRecordsSection.addWidget(relatedRecords);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleID);

        addRelatedListWidgets(relatedRecordsSection, module.getModuleId());
    }

//    private static Page.Tab composeHistoryTab(Page page) {
//        Page.Tab historyTab = page.new Tab("History");
//        Page.Section primarySection = page.new Section();
//        historyTab.addSection(primarySection);
//        PageWidget historyWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
//        historyWidget.addToWidgetParams("activityModuleName", FacilioConstants.UTILITY_INTEGRATION_BILL_ACTIVITY);
//        historyWidget.addToLayoutParams(primarySection, 24, 3);
//        primarySection.addWidget(historyWidget);
//
//        return historyTab;
//    }

}
