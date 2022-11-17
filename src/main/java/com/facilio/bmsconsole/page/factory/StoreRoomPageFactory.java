package com.facilio.bmsconsole.page.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class StoreRoomPageFactory extends PageFactory{
    private static final Logger LOGGER = LogManager.getLogger(StoreRoomPageFactory.class.getName());
    public static Page getStoreRoomPage(StoreRoomContext storeroom, FacilioModule module) throws Exception {
    	
    	
    	Page page = new Page();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

        if (storeroom.getDescription() != null && !storeroom.getDescription().isEmpty()) {
            PageWidget descriptionWidget = new PageWidget(PageWidget.WidgetType.DESCRIPTION_CARD);
            descriptionWidget.addToLayoutParams(tab1Sec1,24, 3);
            tab1Sec1.addWidget(descriptionWidget);
        }
        Page.Section tab1Sec2 = page.new Section();
        tab1.addSection(tab1Sec2);
        PageWidget card1= new PageWidget(PageWidget.WidgetType.STORE_ROOM_CARD1);
        card1.addToLayoutParams(tab1Sec2, 8, 6);
        card1.addToWidgetParams("card","storeroomcard1");
        tab1Sec2.addWidget(card1);

        PageWidget card2= new PageWidget(PageWidget.WidgetType.STORE_ROOM_CARD2);
        card2.addToLayoutParams(tab1Sec2, 8, 6);
        card2.addToWidgetParams("card","storeroomcard2");
        tab1Sec2.addWidget(card2);

        PageWidget card3= new PageWidget(PageWidget.WidgetType.STORE_ROOM_CARD3);
        card3.addToLayoutParams(tab1Sec2, 8, 6);
        card3.addToWidgetParams("card","storeroomcard3");
        tab1Sec2.addWidget(card3);

        Page.Section tab1Sec3 = page.new Section("Photos");
        tab1.addSection(tab1Sec3);
        PageWidget photosWidget = new PageWidget(PageWidget.WidgetType.STOREROOM_PHOTOS);
        photosWidget.addToLayoutParams(tab1Sec3, 24, 3);
        tab1Sec3.addWidget(photosWidget);
        
        Page.Section tab1Sec4 = page.new Section();
        tab1.addSection(tab1Sec4);
        addServingSitesWidget(tab1Sec4);
        
        Page.Section tab1Sec5 = page.new Section();
        tab1.addSection(tab1Sec5);
        addItemToolsWidget(tab1Sec5);
        
        
        Page.Tab tab2 = page.new Tab("Notes and Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addSecondaryDetailsWidget(tab2Sec1);
        addNotesAttachmentsModule(tab2Sec1);

//        Page.Tab tab3 = page.new Tab("Related Records");
//        Page.Section tab3Sec1 = page.new Section();
//        tab3.addSection(tab3Sec1);
//		addRelatedListWidgets(tab3Sec1, storeroom.getModuleId());
//        page.addTab(tab3);
//        
        Page.Tab tab3 = page.new Tab("Related");
        page.addTab(tab3);
        addRelationshipSection(page, tab3, module.getModuleId());
        Page.Section tab3Sec1 = getRelatedListSectionObj(page);
        tab3.addSection(tab3Sec1);
        
        addSubModuleRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.PURCHASE_ORDER, module.getModuleId());
        addSubModuleRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.INVENTORY_REQUEST, module.getModuleId());
        addRelatedListWidgets(tab3Sec1, module.getModuleId());
        if (PageFactory.hasStoreRoomPermission(module)) {
            Page.Tab tab4 = page.new Tab("Issuance");
            page.addTab(tab4);
            Page.Section tab4Sec1 = page.new Section();
            tab4.addSection(tab4Sec1);
            addIssuesAndReturnsWidget(tab4Sec1);
        }

        return page;
    }

    private static PageWidget addIssuesAndReturnsWidget(Page.Section section) {

        PageWidget purchasedItemsWidget = new PageWidget();
        purchasedItemsWidget.addToLayoutParams(section, 24, 12);
        purchasedItemsWidget.setWidgetType(PageWidget.WidgetType.ISSUES_RETURNS);
        section.addWidget(purchasedItemsWidget);

        return purchasedItemsWidget;
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
    private static PageWidget addServingSitesWidget(Page.Section section) {

        PageWidget servingSitesWidget = new PageWidget();
        servingSitesWidget.addToLayoutParams(section, 24, 7);
        servingSitesWidget.setWidgetType(PageWidget.WidgetType.SERVING_SITES_WIDGET);
        section.addWidget(servingSitesWidget);

        return servingSitesWidget;
    }
    private static PageWidget addItemToolsWidget(Page.Section section) {

        PageWidget itemsToolsWidget = new PageWidget();
        itemsToolsWidget.addToLayoutParams(section, 24, 13);
        itemsToolsWidget.setWidgetType(PageWidget.WidgetType.ITEMS_TOOLS_WIDGET);
        section.addWidget(itemsToolsWidget);

        return itemsToolsWidget;
    }
}
