package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsoleV3.util.V3InventoryUtil.updateReservedQuantity;

public class UpdateReservedQuantityCommandV3 extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3WorkorderItemContext> workOrderItems = (List<V3WorkorderItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

        if(CollectionUtils.isNotEmpty(workOrderItems)){
            for(V3WorkorderItemContext workOrderItem : workOrderItems){
                if(workOrderItem.getInventoryReservation()!=null && workOrderItem.getInventoryReservation().getId()>0){
                    updateReservedQuantity(workOrderItem.getItem().getId(),workOrderItem.getQuantity());
                }
            }
        }
        return false;
    }
}
