package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateServicePMCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServicePlannedMaintenanceContext> servicePlannedMaintenanceList = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(servicePlannedMaintenanceList)){
            for(ServicePlannedMaintenanceContext servicePlannedMaintenance : servicePlannedMaintenanceList){
                if(servicePlannedMaintenance.getSite()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Site is required");
                }
                if(servicePlannedMaintenance.getPmType()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"PM Type is required");
                }
                if(servicePlannedMaintenance.getPreviewPeriod()!=null && servicePlannedMaintenance.getPreviewPeriod()>90){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Forecast period can't be more than 90 days");
                }
                if(servicePlannedMaintenance.getEstimatedDuration()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Estimated duration is required");
                }
                if(servicePlannedMaintenance.getRelations() == null || servicePlannedMaintenance.getRelations().get("servicePMTrigger") == null ){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Trigger is required");
                }
                servicePlannedMaintenance.setTriggerType(ServicePlannedMaintenanceContext.TriggerType.RECURRING_SCHEDULE.getIndex());
                if(servicePlannedMaintenance.getPmType().equals(ServicePlannedMaintenanceContext.PMType.SPACE.getIndex()) && servicePlannedMaintenance.getSpace()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Space is required");
                }
                if(servicePlannedMaintenance.getPmType().equals(ServicePlannedMaintenanceContext.PMType.ASSET.getIndex()) && servicePlannedMaintenance.getAsset()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Asset is required");
                }
            }
        }
        return false;
    }
}
