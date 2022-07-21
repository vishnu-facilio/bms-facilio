package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ItemTypesPageFactory extends PageFactory{
    private static final Logger LOGGER = LogManager.getLogger(ItemTypesPageFactory.class.getName());
    public static Page getItemTypesPage(ItemTypesContext itemTypes, FacilioModule module) throws Exception {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

        if ((itemTypes.getDescription() != null && !itemTypes.getDescription().isEmpty())||(itemTypes.getCategory() != null) ) {
            PageWidget descWidget = new PageWidget(PageWidget.WidgetType.DESCRIPTION_CARD);
            descWidget.addToLayoutParams(tab1Sec1,24, 4);
            tab1Sec1.addWidget(descWidget);
        }

        PageWidget card1= new PageWidget(PageWidget.WidgetType.INVENTORY_CARD);
        card1.addToLayoutParams(tab1Sec1, 8, 5);
        card1.addToWidgetParams("card","itemtypescard1");
        tab1Sec1.addWidget(card1);

        PageWidget card2= new PageWidget(PageWidget.WidgetType.INVENTORY_CARD);
        card2.addToLayoutParams(tab1Sec1, 8, 5);
        card2.addToWidgetParams("card","itemtypescard2");
        tab1Sec1.addWidget(card2);

        PageWidget card3= new PageWidget(PageWidget.WidgetType.INVENTORY_CARD);
        card3.addToLayoutParams(tab1Sec1, 8, 5);
        card3.addToWidgetParams("card","itemtypescard3");
        tab1Sec1.addWidget(card3);

        addStoreroomWidget(tab1Sec1);

        addTransactionsWidget(tab1Sec1);

        Page.Tab tab2 = page.new Tab("Notes & Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addSecondaryDetailsWidget(tab2Sec1);
        addNotesAttachmentsModule(tab2Sec1);

        Page.Tab tab3 = page.new Tab("Related");
        boolean isRelationshipAdded = addRelationshipSection(page, tab3, module.getModuleId());
        Page.Section tab3Sec1 = getRelatedListSectionObj(page);
        tab3.addSection(tab3Sec1);

        addRelatedListWidgets(tab3Sec1, module.getModuleId());

        if ((tab3Sec1.getWidgets() != null && !tab3Sec1.getWidgets().isEmpty()) || isRelationshipAdded) {
            page.addTab(tab3);
        }

        return page;
    }
    private static PageWidget addStoreroomWidget(Page.Section section) {

        PageWidget storeRoomWidget = new PageWidget();
        storeRoomWidget.addToLayoutParams(section, 24, 8);
        storeRoomWidget.setWidgetType(PageWidget.WidgetType.STORE_ROOM);
        storeRoomWidget.addToWidgetParams("storeroom","itemStoreroom");
        section.addWidget(storeRoomWidget);

        return storeRoomWidget;
    }
    private static PageWidget addTransactionsWidget(Page.Section section) {

        PageWidget transactionsWidget = new PageWidget();
        transactionsWidget.addToLayoutParams(section, 24, 8);
        transactionsWidget.setWidgetType(PageWidget.WidgetType.TRANSACTIONS);
        transactionsWidget.addToWidgetParams("transactions","itemTransactions");
        section.addWidget(transactionsWidget);

        return transactionsWidget;
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
}
