package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GetWorkOrderItemCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long itemId = (Long) context.get(FacilioConstants.ContextNames.ITEM);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkorderItemContext workOrderItem = new V3WorkorderItemContext();
        if(itemId != null) {
            V3ItemContext item = V3ItemsApi.getItems(itemId);
            if(item!=null) {
                V3ItemContext itemRec = new V3ItemContext();
                itemRec.setId(itemId);
                workOrderItem.setItem(itemRec);
                workOrderItem.setStoreRoom(item.getStoreRoom());
                workOrderItem.setBin(item.getDefaultBin());
                if (item.getItemType() != null && item.getItemType().isRotating()) {
                    workOrderItem.setQuantity(1);
                    workOrderItem.setItemType(item.getItemType());
                }
            }
        }
        if(workOrderId!=null){
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);
            workOrderItem.setWorkorder(workOrder);
        }
        context.put(FacilioConstants.ContextNames.WORKORDER_ITEMS,workOrderItem);
        return false;
    }
}
