package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestShipmentContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestShipmentReceivablesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.db.criteria.operators.MultiFieldOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SetPendingLineItemsAndReceivablesCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3TransferRequestShipmentContext transferRequestShipmentContext = (V3TransferRequestShipmentContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT, id);
        long trId= transferRequestShipmentContext.getTransferRequest().getId();
        //set pending line items
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String lineItemModuleName = FacilioConstants.ContextNames.TRANSFER_REQUEST_LINE_ITEM;
        List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3TransferRequestLineItemContext> builder = new SelectRecordsBuilder<V3TransferRequestLineItemContext>()
                .moduleName(lineItemModuleName)
                .select(fields)
                .beanClass(V3TransferRequestLineItemContext.class)
                .andCondition(CriteriaAPI.getCondition("TRANSFER_REQUEST_ID", "transferRequest", String.valueOf(trId), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"), (LookupField) fieldsAsMap.get("toolType")));
        List<V3TransferRequestLineItemContext> list = builder.get();
        transferRequestShipmentContext.getTransferRequest().setLineItems(list);

        //set receivables
        String receivablesModuleName = FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT_RECEIVABLES;
        List<FacilioField> receivablesfields = modBean.getAllFields(receivablesModuleName);
        Map<String, FacilioField> receivablesfieldsAsMap = FieldFactory.getAsMap(receivablesfields);

        SelectRecordsBuilder<V3TransferRequestShipmentReceivablesContext> build = new SelectRecordsBuilder<V3TransferRequestShipmentReceivablesContext>()
                .moduleName(receivablesModuleName)
                .select(receivablesfields)
                .beanClass(V3TransferRequestShipmentReceivablesContext.class)
                .andCondition(CriteriaAPI.getCondition("TRANSFER_REQUEST_SHIPMENT_ID", "shipment", String.valueOf(id), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) receivablesfieldsAsMap.get("itemType"), (LookupField) receivablesfieldsAsMap.get("toolType")));

        List<V3TransferRequestShipmentReceivablesContext> receivablesList = build.get();
        transferRequestShipmentContext.setShipmentReceivables(receivablesList);
        return false;
    }
}
