package com.facilio.fsm.commands.serviceOrders;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class UpdateCounterInServicePMCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrders)){
            Map<Long,Integer> pmVsServiceOrdersCount = new HashMap<>();
            for(ServiceOrderContext serviceOrder : serviceOrders){
                if(serviceOrder.getServicePlannedMaintenance()!=null && serviceOrder.getServicePlannedMaintenance().getId()>0){
                    if(!pmVsServiceOrdersCount.containsKey(serviceOrder.getServicePlannedMaintenance().getId())){
                        pmVsServiceOrdersCount.put(serviceOrder.getServicePlannedMaintenance().getId(),1);
                    }else{
                      Integer count = pmVsServiceOrdersCount.get(serviceOrder.getServicePlannedMaintenance().getId());
                      pmVsServiceOrdersCount.put(serviceOrder.getServicePlannedMaintenance().getId(),count++);
                    }
                }
            }
            Set<Long> pmIdKeys = pmVsServiceOrdersCount.keySet();
            List<Long> pmIds = new ArrayList<>(pmIdKeys);
            if(CollectionUtils.isNotEmpty(pmIds)){
                List<ServicePlannedMaintenanceContext> servicePMList = V3RecordAPI.getRecordsList(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,pmIds);
                for(ServicePlannedMaintenanceContext servicePM : servicePMList){
                    Integer existingCount = servicePM.getCounter();
                    Integer newCount = existingCount + pmVsServiceOrdersCount.get(servicePM.getId());
                    servicePM.setCounter(newCount);
                    V3Util.processAndUpdateSingleRecord(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE, servicePM.getId(), FieldUtil.getAsJSON(servicePM), null, null, null, null, null,null,null, null,null);
                }
            }
        }
        return false;
    }
}
