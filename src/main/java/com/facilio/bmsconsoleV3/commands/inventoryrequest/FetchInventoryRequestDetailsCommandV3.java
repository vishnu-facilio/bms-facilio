package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class FetchInventoryRequestDetailsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3InventoryRequestContext inventoryRequestContext = (V3InventoryRequestContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.INVENTORY_REQUEST, id);
            if (inventoryRequestContext != null) {
                if(inventoryRequestContext.getParentId()!= null && inventoryRequestContext.getParentId() > 0 ) {
                    WorkOrderContext workOrder = WorkOrderAPI.getWorkOrder(inventoryRequestContext.getParentId());
                    if (workOrder != null) {
                        inventoryRequestContext.setWorkOrderLocalId(workOrder.getLocalId());
                    }
                }
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    String lineItemModuleName = FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS;
                    List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
                    List<V3InventoryRequestLineItemContext> list = InventoryRequestAPI.getLineItemsForInventoryRequestV3(String.valueOf(inventoryRequestContext.getId()), null, null);
                    inventoryRequestContext.setInventoryrequestlineitems(list);
                }

        return false;
    }
}
