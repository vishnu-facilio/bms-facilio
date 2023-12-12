package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceLineItemsContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConvertPOtoInvoiceCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Integer poId = (Integer) context.get(FacilioConstants.ContextNames.RECORD_ID);

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


            if(poId !=null) {
                FacilioModule poItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
                SelectRecordsBuilder<V3PurchaseOrderLineItemContext> b = new SelectRecordsBuilder<V3PurchaseOrderLineItemContext>()
                        .module(poItemModule)
                        .select(modBean.getAllFields(poItemModule.getName()))
                        .beanClass(V3PurchaseOrderLineItemContext.class)
                        .andCondition(CriteriaAPI.getCondition("PO_ID", "purchaseOrder", String.valueOf(poId), NumberOperators.EQUALS));
                List<V3PurchaseOrderLineItemContext> lineItems = b.get();
                List<InvoiceLineItemsContext> invoiceLineItemsContexts = new ArrayList<>();

                for (V3PurchaseOrderLineItemContext lineItem : lineItems) {
                    Map<String, Object> map = FieldUtil.getAsProperties(lineItem);
                    map.put("type",map.get("inventoryType"));
                    InvoiceLineItemsContext lineItemsContext = FieldUtil.getAsBeanFromMap(map, InvoiceLineItemsContext.class);
                    invoiceLineItemsContexts.add(lineItemsContext);
                }
                InvoiceContextV3 invoiceContextV3 = new InvoiceContextV3();
                invoiceContextV3.setLineItems(invoiceLineItemsContexts);

                V3PurchaseOrderContext purchaseOrderContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PURCHASE_ORDER,poId,V3PurchaseOrderContext.class);
                invoiceContextV3.setPurchaseOrder(purchaseOrderContext);
                invoiceContextV3.setInvoiceType(InvoiceContextV3.InvoiceType.VENDOR.getIndex());
                invoiceContextV3.setVendor(purchaseOrderContext.getVendor());


                context.put(FacilioConstants.ContextNames.RECORD, invoiceContextV3);
            }

        return false;
    }

}
