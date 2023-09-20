package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.fsm.util.ServicePlannedMaintenanceAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.scheduleServicePM;

@Log4j
public class PublishServicePMCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServicePlannedMaintenanceContext> servicePlannedMaintenanceList = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(servicePlannedMaintenanceList)){
            for(ServicePlannedMaintenanceContext servicePlannedMaintenance : servicePlannedMaintenanceList){
                if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("publishServicePM") && (boolean) bodyParams.get("publishServicePM")){
                    if(!servicePlannedMaintenance.getIsPublished()){
                        ServicePMTriggerContext servicePMTrigger = servicePlannedMaintenance.getServicePMTrigger();
                        if(servicePMTrigger.getStartTime()==null){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Execution start date is required");
                        }
                        servicePlannedMaintenance.setIsPublished(true);
                        scheduleServicePM(servicePlannedMaintenance.getId());
                        LOGGER.info("Service Orders pre created for pm - " + servicePlannedMaintenance.getId());
                    }else{
                        throw new RESTException(ErrorCode.VALIDATION_ERROR,"This PM is already published");
                    }
                }else{
                    ServicePlannedMaintenanceAPI.deleteServiceOrdersInUpcomingState(servicePlannedMaintenance.getId());
                    servicePlannedMaintenance.setIsPublished(false);
                    servicePlannedMaintenance.setNextRun(null);
                }
            }
        }
        return false;
    }
}
