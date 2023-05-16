package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class ItemPageFactory extends PageFactory{
    private static final Logger LOGGER = LogManager.getLogger(ItemPageFactory.class.getName());
    public static Page getItemPage(ItemContext item, FacilioModule module) throws Exception {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

        PageWidget descWidget = new PageWidget(PageWidget.WidgetType.DESCRIPTION_CARD);
        descWidget.addToLayoutParams(tab1Sec1,24, 3);
        tab1Sec1.addWidget(descWidget);

        PageWidget card1= new PageWidget(PageWidget.WidgetType.INVENTORY_CARD);
        card1.addToLayoutParams(tab1Sec1, 8, 5);
        card1.addToWidgetParams("card","itemcard1");
        tab1Sec1.addWidget(card1);

        PageWidget card2= new PageWidget(PageWidget.WidgetType.INVENTORY_CARD);
        card2.addToLayoutParams(tab1Sec1, 8, 5);
        card2.addToWidgetParams("card","itemcard2");
        tab1Sec1.addWidget(card2);

        PageWidget card3= new PageWidget(PageWidget.WidgetType.INVENTORY_CARD);
        card3.addToLayoutParams(tab1Sec1, 8, 5);
        card3.addToWidgetParams("card","itemcard3");
        tab1Sec1.addWidget(card3);

        if(isRotating(item)){
            addAssetRelatedListWidget(tab1Sec1, FacilioConstants.ContextNames.ASSET, module.getModuleId());
        }
        else{
            addPurchasedItemsWidget(tab1Sec1);
        }

        addTransactionsWidget(tab1Sec1);
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.ASSET_SPARE_PARTS)) {
            addWhereUsedWidget(tab1Sec1);
        }

        Page.Tab tab2 = page.new Tab("Notes & Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addSecondaryDetailsWidget(tab2Sec1);
        addNotesAttachmentsModule(tab2Sec1);

        Page.Tab tab3 = page.new Tab("Related");
        boolean isRelationshipNeeded = addRelationshipSection(page, tab3, module.getModuleId());
        Page.Section tab3Sec1 = getRelatedListSectionObj(page);
        tab3.addSection(tab3Sec1);

        addRelatedListWidgets(tab3Sec1, module.getModuleId());

        if ((tab3Sec1.getWidgets() != null && !tab3Sec1.getWidgets().isEmpty()) || isRelationshipNeeded) {
            page.addTab(tab3);
        }

        return page;
    }
    private static PageWidget addPurchasedItemsWidget(Page.Section section) {

        PageWidget purchasedItemsWidget = new PageWidget();
        purchasedItemsWidget.addToLayoutParams(section, 24, 8);
        purchasedItemsWidget.setWidgetType(PageWidget.WidgetType.PURCHASED_ITEMS);
        section.addWidget(purchasedItemsWidget);

        return purchasedItemsWidget;
    }
    private static void addWhereUsedWidget(Page.Section section) {
        PageWidget whereUsedWidget = new PageWidget(PageWidget.WidgetType.WHERE_USED);
        whereUsedWidget.addToLayoutParams(section, 24, 11);
        section.addWidget(whereUsedWidget);
    }
    private static PageWidget addTransactionsWidget(Page.Section section) {

        PageWidget transactionsWidget = new PageWidget();
        transactionsWidget.addToLayoutParams(section, 24, 8);
        transactionsWidget.setWidgetType(PageWidget.WidgetType.ITEM_TRANSACTIONS);
        section.addWidget(transactionsWidget);

        return transactionsWidget;
    }
    static void addAssetRelatedListWidget(Page.Section section, String moduleName, long parenModuleId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == parenModuleId)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fields)) {
            for (FacilioField field : fields) {
                PageWidget relatedListWidget = new PageWidget(PageWidget.WidgetType.NEW_RELATED_LIST);
                JSONObject relatedList = new JSONObject();
                relatedList.put("module", module);
                relatedList.put("field", field);
                relatedList.put("isEditable", false);
                relatedList.put("isDeletable", false);
                relatedListWidget.setRelatedList(relatedList);
                relatedListWidget.addToLayoutParams(section, 24, 8);
                section.addWidget(relatedListWidget);
            }
        }
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
    private static boolean isRotating(ItemContext item) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.ITEM_TYPES;
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<V3ItemTypesContext> builder = new SelectRecordsBuilder<V3ItemTypesContext>()
                .module(module)
                .beanClass(V3ItemTypesContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(item.getItemType().getId(), module));
        List<V3ItemTypesContext> records = builder.get();
        return records.get(0).isRotating();
    }
}
