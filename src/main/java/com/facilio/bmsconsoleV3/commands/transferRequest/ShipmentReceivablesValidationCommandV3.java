package com.facilio.bmsconsoleV3.commands.transferRequest;

import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestShipmentReceivablesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import nl.basjes.shaded.org.springframework.util.ObjectUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShipmentReceivablesValidationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestShipmentReceivablesContext> receivablesContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(receivablesContexts)) {
            for (V3TransferRequestShipmentReceivablesContext receivablesContext : receivablesContexts) {
                if (receivablesContext != null) {
                    // Date Validation
                   if(receivablesContext.getReceiptDate()==0){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Receipt Date cannot be empty");
                    }
                }
            }
        }
        return false;
    }
}
