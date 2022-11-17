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
        V3WorkorderItemContext workorderItem = new V3WorkorderItemContext();
        if(itemId != null && workOrderId != null) {
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);
            V3ItemContext item = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ITEM, itemId, V3ItemContext.class);
            if(item != null){
                workorderItem.setItem(item);
                workorderItem.setWorkorder(workOrder);
                workorderItem.setStoreRoom(item.getStoreRoom());
            }
        }
        context.put(FacilioConstants.ContextNames.WORKORDER_ITEMS,workorderItem);
        return false;
    }
}
