package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesVendorsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrUpdateItemTypeVendorCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(org.apache.commons.chain.Context context) throws Exception {
        List<V3ItemTypesVendorsContext> itemTypeVendorsList = (List<V3ItemTypesVendorsContext>) context.get(FacilioConstants.ContextNames.ITEM_VENDORS_LIST);
        if(itemTypeVendorsList!=null){
            long vendorId = (long) context.get(FacilioConstants.ContextNames.VENDOR_ID);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule itemVendorModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_VENDORS);
            List<FacilioField> itemVendorFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_VENDORS);
            Map<String, FacilioField> itemVendorFieldMap = FieldFactory.getAsMap(itemVendorFields);
            List<Long> itemTypesId = new ArrayList<>();
            Map<Long, Long> itemTypeVsVendor = new HashMap<>();

            SelectRecordsBuilder<V3ItemTypesVendorsContext> itemVendorselectBuilder = new SelectRecordsBuilder<V3ItemTypesVendorsContext>().select(itemVendorFields)
                    .table(itemVendorModule.getTableName()).moduleName(itemVendorModule.getName()).beanClass(V3ItemTypesVendorsContext.class)
                    .andCondition(CriteriaAPI.getCondition(itemVendorFieldMap.get("vendor"), String.valueOf(vendorId),
                            NumberOperators.EQUALS));

            List<V3ItemTypesVendorsContext> itemVendorsList = itemVendorselectBuilder.get();
            if (itemVendorsList != null && !itemVendorsList.isEmpty()) {
                for (V3ItemTypesVendorsContext itemVendor : itemVendorsList) {
                    itemTypesId.add(itemVendor.getItemType().getId());
                    itemTypeVsVendor.put(itemVendor.getItemType().getId(), itemVendor.getId());
                }
            }

            List<V3ItemTypesVendorsContext> itemVendorsToBeAdded = new ArrayList<>();
            for (V3ItemTypesVendorsContext itemVendors : itemTypeVendorsList) {
                if (!itemTypesId.contains(itemVendors.getItemType().getId())) {
                    itemVendorsToBeAdded.add(itemVendors);
                } else {
                    itemVendors.setId(itemTypeVsVendor.get(itemVendors.getItemType().getId()));
                    updateItemVendor(itemVendorModule, itemVendorFields, itemVendors);
                }
            }

            if (itemVendorsToBeAdded != null && !itemVendorsToBeAdded.isEmpty()) {
                addItem(itemVendorModule, itemVendorFields, itemVendorsToBeAdded);
            }
        }
        return false;
    }

    private void addItem(FacilioModule module, List<FacilioField> fields, List<V3ItemTypesVendorsContext> parts) throws Exception {
        InsertRecordBuilder<V3ItemTypesVendorsContext> readingBuilder = new InsertRecordBuilder<V3ItemTypesVendorsContext>().module(module)
                .fields(fields).addRecords(parts);
        readingBuilder.save();
    }

    private void updateItemVendor(FacilioModule module, List<FacilioField> fields, V3ItemTypesVendorsContext parts) throws Exception {
        UpdateRecordBuilder<V3ItemTypesVendorsContext> builder = new UpdateRecordBuilder<V3ItemTypesVendorsContext>().module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(parts.getId(), module));
        builder.update(parts);
    }
}
