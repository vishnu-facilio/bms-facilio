package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3BinContext;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedToolContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class V3InventoryAPI {
    public static V3VendorContext getVendor(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDORS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VENDORS);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);


        SelectRecordsBuilder<V3VendorContext> selectBuilder = new SelectRecordsBuilder<V3VendorContext>()
                .select(fields)
                .table(module.getTableName())
                .moduleName(module.getName())
                .beanClass(V3VendorContext.class)
                .andCustomWhere(module.getTableName()+".ID = ?", id);

        LookupField registeredBy = (LookupField) fieldsAsMap.get("registeredBy");
        selectBuilder.fetchSupplement(registeredBy);

        V3VendorContext vendor = selectBuilder.fetchFirst();

        return vendor;
    }

    public static List<V3PurchasedToolContext> addPurchasedTool(List<V3PurchasedToolContext> tool) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
        InsertRecordBuilder<V3PurchasedToolContext> readingBuilder = new InsertRecordBuilder<V3PurchasedToolContext>()
                .module(module).fields(fields).addRecords(tool);
        readingBuilder.save();
        return readingBuilder.getRecords();
    }

    public static Map<Long, V3BinContext> getBinFromItemTransaction(List<V3ItemTransactionsContext> itemTransactions) throws Exception {
        Set<Long> binIds = itemTransactions.stream().map(i -> i.getBin().getId()).collect(Collectors.toSet());
        return V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.BIN,binIds,V3BinContext.class);
    }
    public static Map<Long, V3BinContext> getBinFromToolTransactions(List<V3ToolTransactionContext> toolTransactions) throws Exception {
        Set<Long> binIds = toolTransactions.stream().map(i -> i.getBin().getId()).collect(Collectors.toSet());
        return V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.BIN,binIds,V3BinContext.class);
    }
}
