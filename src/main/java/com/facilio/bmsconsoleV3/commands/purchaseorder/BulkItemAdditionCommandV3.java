package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsoleV3.context.V3BinContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.enums.CostType;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BulkItemAdditionCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<V3ItemContext> itemsList = (List<V3ItemContext>) context.get(FacilioConstants.ContextNames.ITEMS);
        if (itemsList != null && !itemsList.isEmpty()) {
            long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
            List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
            Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);
            List<Long> itemTypesId = new ArrayList<>();
            Map<Long, Long> itemTypeVsItem = new HashMap<>();
            SelectRecordsBuilder<V3ItemContext> itemselectBuilder = new SelectRecordsBuilder<V3ItemContext>()
                    .select(itemFields).table(itemModule.getTableName()).moduleName(itemModule.getName())
                    .beanClass(V3ItemContext.class).andCondition(CriteriaAPI.getCondition(itemFieldMap.get("storeRoom"),
                            String.valueOf(storeRoomId), NumberOperators.EQUALS));

            List<V3ItemContext> items = itemselectBuilder.get();
            if (items != null && !items.isEmpty()) {
                for (V3ItemContext item : items) {
                    itemTypesId.add(item.getItemType().getId());
                    itemTypeVsItem.put(item.getItemType().getId(), item.getId());
                }
            }

            List<V3ItemContext> itemToBeAdded = new ArrayList<>();
            List<V3PurchasedItemContext> purchasedItems = new ArrayList<>();
            for (V3ItemContext item : itemsList) {
                if (!itemTypesId.contains(item.getItemType().getId())) {
                    if(item.getCostType()<=0) {
                        item.setCostType(CostType.FIFO.getIndex());
                    }
                    itemToBeAdded.add(item);
                } else {
                    item.setId(itemTypeVsItem.get(item.getItemType().getId()));
                    updateItem(itemModule, itemFields, item);
                }
            }

            if (itemToBeAdded != null && !itemToBeAdded.isEmpty()) {
                addItem(itemModule, itemFields, itemToBeAdded);
            }
            List<V3ItemContext> itemCloneList = new ArrayList<>();
            Map<Long, V3BinContext> binMap = new HashMap<>();
            for (V3ItemContext item : itemsList) {
                V3ItemContext itemClone = FieldUtil.cloneBean(item,V3ItemContext.class);
                itemCloneList.add(itemClone);
                if (item.getPurchasedItems() != null && !item.getPurchasedItems().isEmpty()) {
                    for (V3PurchasedItemContext pItem : item.getPurchasedItems()) {
                        pItem.setItem(item);
                        pItem.setItemType(item.getItemType());
                        if(pItem.getBin() == null){
                            if(item.getDefaultBin() != null){
                                pItem.setBin(item.getDefaultBin());
                            } else {
                                V3BinContext bin = V3ItemsApi.addVirtualBin(item);
                                pItem.setBin(bin);
                                V3ItemsApi.makeBinDefault(item,bin);
                                item.setDefaultBin(bin);
                            }
                        }
                        binMap.put(pItem.getBin().getId(),pItem.getBin());

                        purchasedItems.add(pItem);
                    }
                    item.setPurchasedItems(null);
                }
            }
            context.put(FacilioConstants.ContextNames.BIN,binMap.values().stream().collect(Collectors.toList()));
            context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
            context.put(FacilioConstants.ContextNames.RECORD_LIST, itemsList);
            context.put(FacilioConstants.ContextNames.ITEMS, itemCloneList);
        }
        return false;
    }

    private void addItem(FacilioModule module, List<FacilioField> fields, List<V3ItemContext> parts) throws Exception {
        InsertRecordBuilder<V3ItemContext> readingBuilder = new InsertRecordBuilder<V3ItemContext>().module(module)
                .fields(fields).addRecords(parts);
        readingBuilder.save();
    }

    private void updateItem(FacilioModule module, List<FacilioField> fields, V3ItemContext item) throws Exception {
        UpdateRecordBuilder<V3ItemContext> readingBuilder = new UpdateRecordBuilder<V3ItemContext>().module(module)
                .fields(fields).andCondition(CriteriaAPI.getIdCondition(item.getId(), module));
        readingBuilder.update(item);
    }
}
