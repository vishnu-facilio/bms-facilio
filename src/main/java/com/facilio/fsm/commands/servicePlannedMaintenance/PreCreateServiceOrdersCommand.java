package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Log4j
public class PreCreateServiceOrdersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ServiceOrderContext> serviceOrders = (List<ServiceOrderContext>) context.get(FacilioConstants.ServicePlannedMaintenance.GENERATED_SERVICE_ORDERS);
        if(CollectionUtils.isNotEmpty(serviceOrders)){
            V3Util.preCreateRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, FieldUtil.getAsMapList(serviceOrders,ServiceOrderContext.class),null,null);
        }
        return false;
    }
}
