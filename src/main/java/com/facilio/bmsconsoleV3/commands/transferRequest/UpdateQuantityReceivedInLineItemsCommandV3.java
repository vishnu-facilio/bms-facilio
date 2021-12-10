package com.facilio.bmsconsoleV3.commands.transferRequest;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestShipmentReceivablesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpdateQuantityReceivedInLineItemsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String shipmentReceivablesModuleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestShipmentReceivablesContext> shipmentReceivablesContext = recordMap.get(shipmentReceivablesModuleName);
        long lineItemId = shipmentReceivablesContext.get(0).getLineItem().getId();
        double quantityReceived = shipmentReceivablesContext.get(0).getQuantityReceived();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String lineItemModuleName = FacilioConstants.ContextNames.TRANSFER_REQUEST_LINE_ITEM;
        FacilioModule module = modBean.getModule(lineItemModuleName);
        List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);

        SelectRecordsBuilder<V3TransferRequestLineItemContext> builder = new SelectRecordsBuilder<V3TransferRequestLineItemContext>()
                .module(module)
                .beanClass(V3TransferRequestLineItemContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(lineItemId, module));
        List<V3TransferRequestLineItemContext> records = builder.get();

        double oldQuantity= records.get(0).getQuantityReceived();
        double newQuantityReceived =oldQuantity+quantityReceived;
        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("quantityReceived"));

        Map<String, Object> map = new HashMap<>();
        map.put("quantityReceived", newQuantityReceived);

        UpdateRecordBuilder<V3TransferRequestLineItemContext> updateBuilder = new UpdateRecordBuilder<V3TransferRequestLineItemContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(lineItemId, module));
        updateBuilder.updateViaMap(map);
        return false;
    }
}
