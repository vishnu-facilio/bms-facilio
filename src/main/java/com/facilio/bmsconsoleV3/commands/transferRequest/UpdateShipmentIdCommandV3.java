package com.facilio.bmsconsoleV3.commands.transferRequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestShipmentContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import java.util.List;

public class UpdateShipmentIdCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
                    Long trId = Constants.getRecordIds(context).get(0);
                    V3TransferRequestContext transferRequestContext = (V3TransferRequestContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.TRANSFER_REQUEST, trId);
                    if(transferRequestContext.getIsShipped().equals(true)){
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        String shipmentModuleName = FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT;
                        List<FacilioField> fields = modBean.getAllFields(shipmentModuleName);

                        SelectRecordsBuilder<V3TransferRequestShipmentContext> builder = new SelectRecordsBuilder<V3TransferRequestShipmentContext>()
                                .moduleName(shipmentModuleName)
                                .select(fields)
                                .beanClass(V3TransferRequestShipmentContext.class)
                                .andCondition(CriteriaAPI.getCondition("TRANSFER_REQUEST_ID", "transferRequest", String.valueOf(trId), NumberOperators.EQUALS));
                        List<V3TransferRequestShipmentContext> list = builder.get();
                        Long shipmentId = list.get(0).getId();
                        transferRequestContext.setShipmentId(shipmentId);
                    }


        return false;
    }
}
