package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValidateInventoryRequestCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InventoryRequestContext> inventoryRequestContext = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        boolean isUpdateIRReservationStatus = MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("updateIRReservationStatus");
        if (CollectionUtils.isNotEmpty(inventoryRequestContext)) {
            for (V3InventoryRequestContext inventoryRequestContexts : inventoryRequestContext) {
                if (inventoryRequestContexts.getInventoryrequestlineitems() == null) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Line items cannot be empty");
                }
                if (inventoryRequestContexts.getRequestedTime() == null) {
                    inventoryRequestContexts.setRequestedTime(System.currentTimeMillis());
                }
                if(!isUpdateIRReservationStatus && !inventoryRequestContexts.getInventoryRequestReservationStatus().equals(V3InventoryRequestContext.InventoryRequestReservationStatus.PENDING.getIndex())){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot update Inventory Request after Reservation");
                }
            }
        }
        return false;
    }
}
