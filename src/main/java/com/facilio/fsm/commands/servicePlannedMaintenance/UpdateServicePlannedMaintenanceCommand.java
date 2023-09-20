package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class UpdateServicePlannedMaintenanceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServicePMTriggerContext> servicePMTriggers = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(servicePMTriggers)){
            for(ServicePMTriggerContext servicePMTrigger : servicePMTriggers){
                if(servicePMTrigger.getServicePlannedMaintenance()!=null){
                    updateServicePM(servicePMTrigger);
                }
            }
        }
        return false;
    }
    private void updateServicePM(ServicePMTriggerContext servicePMTrigger)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        FacilioField servicePMTriggerField = modBean.getField("servicePMTrigger",FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        ServicePlannedMaintenanceContext servicePlannedMaintenance=  new ServicePlannedMaintenanceContext();
        servicePlannedMaintenance.setId(servicePMTrigger.getServicePlannedMaintenance().getId());
        servicePlannedMaintenance.setServicePMTrigger(servicePMTrigger);
        V3RecordAPI.updateRecord(servicePlannedMaintenance,module, Collections.singletonList(servicePMTriggerField));
    }
}
