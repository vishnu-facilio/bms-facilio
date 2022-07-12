package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddPurchasedItemsForBulkItemAddCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<V3PurchasedItemContext> purchasedItem = (List<V3PurchasedItemContext>) context
                .get(FacilioConstants.ContextNames.PURCHASED_ITEM);

        if (purchasedItem != null && !purchasedItem.isEmpty()) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
            List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
            long itemTypesId = -1;
            List<Long> itemTypesIds = new ArrayList<>();
            List<Long> itemIds = new ArrayList<>();
            Set<Long> uniqueItemIds = new HashSet<Long>();
            Set<Long> uniqueItemTypesIds = new HashSet<Long>();

            FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
            List<FacilioField> itemTypesFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);

            List<V3PurchasedItemContext> pcToBeAdded = new ArrayList<>();
            List<V3PurchasedItemContext> purchaseItemsList = new ArrayList<>();
            if (purchasedItem != null && !purchasedItem.isEmpty()) {
                for (V3PurchasedItemContext pi : purchasedItem) {
                    SelectRecordsBuilder<V3ItemTypesContext> itemTypesselectBuilder = new SelectRecordsBuilder<V3ItemTypesContext>()
                            .select(itemTypesFields).table(itemTypesModule.getTableName())
                            .moduleName(itemTypesModule.getName()).beanClass(V3ItemTypesContext.class)
                            .andCondition(CriteriaAPI.getIdCondition(pi.getItemType().getId(), itemTypesModule));

                    List<V3ItemTypesContext> itemTypes = itemTypesselectBuilder.get();
                    V3ItemTypesContext itemType = null;
                    if (itemTypes != null && !itemTypes.isEmpty()) {
                        itemType = itemTypes.get(0);
                    }
                    uniqueItemIds.add(pi.getItem().getId());
                    uniqueItemTypesIds.add(pi.getItemType().getId());
                    if (itemType.isRotating()) {
                        if (pi.getQuantity() > 0) {
                            throw new IllegalArgumentException(
                                    "Quantity cannot be set when individual Tracking is enabled");
                        }
                        pi.setQuantity(1.0);
                        pi.setCurrentQuantity(1.0);
                    } else {
                        if (pi.getQuantity() <= 0) {
                            throw new IllegalArgumentException("Quantity cannot be null");
                        }
                        double quantity = pi.getQuantity();
                        pi.setCurrentQuantity(quantity);
                    }
                    if (pi.getCostDate() == null) {
                        pi.setCostDate(System.currentTimeMillis());
                    }
                    if (pi.getId() <= 0) {
                        // Insert
                        purchaseItemsList.add(pi);
                        pcToBeAdded.add(pi);
                    } else {
                        purchaseItemsList.add(pi);
                        updateInventorycost(purchasedItemModule, purchasedItemFields, pi);
                    }

                }
                int size = 0;
                if (pcToBeAdded != null && !pcToBeAdded.isEmpty()) {
                    size = addInventorycost(purchasedItemModule, purchasedItemFields, pcToBeAdded);
                }
                setImportProcessContext(context, size);
            }
            itemIds.addAll(uniqueItemIds);
            itemTypesIds.addAll(uniqueItemTypesIds);
            context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchaseItemsList);
            context.put(FacilioConstants.ContextNames.RECORD_LIST, purchaseItemsList);
            context.put(FacilioConstants.ContextNames.ITEM_IDS, itemIds);
            context.put(FacilioConstants.ContextNames.TRANSACTION_TYPE, TransactionType.STOCK);
            context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypesId);
            context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, itemTypesIds);
        }
        return false;
    }

    private int addInventorycost(FacilioModule module, List<FacilioField> fields, List<V3PurchasedItemContext> parts)
            throws Exception {
        InsertRecordBuilder<V3PurchasedItemContext> readingBuilder = new InsertRecordBuilder<V3PurchasedItemContext>()
                .module(module).fields(fields).addRecords(parts);
        readingBuilder.save();
        return readingBuilder.getRecords().size();
    }

    private void updateInventorycost(FacilioModule module, List<FacilioField> fields, V3PurchasedItemContext part)
            throws Exception {

        UpdateRecordBuilder<V3PurchasedItemContext> updateBuilder = new UpdateRecordBuilder<V3PurchasedItemContext>()
                .module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(part.getId(), module));
        updateBuilder.update(part);

        System.err.println(Thread.currentThread().getName() + "Exiting updateCosts in  AddorUpdateCommand#######  ");

    }

    private void setImportProcessContext(Context c, int size) throws ParseException {
        ImportProcessContext importProcessContext = (ImportProcessContext) c
                .get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        if (importProcessContext != null) {
            JSONObject importMeta = importProcessContext.getImportJobMetaJson();
            if (importMeta!= null && !importMeta.isEmpty()) {
                importMeta.put("FieldMapping", importProcessContext.getFieldMappingJSON());
                importMeta.put("Inserted", size + "");
            } else {
                JSONObject meta = new JSONObject();
                meta.put("Inserted", size + "");
                importMeta = meta;
            }
            importProcessContext.setImportJobMeta(importMeta.toJSONString());
        }

    }
}
