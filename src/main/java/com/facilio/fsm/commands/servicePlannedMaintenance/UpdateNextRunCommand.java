package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.fsm.util.ServicePlannedMaintenanceAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class UpdateNextRunCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePMMod = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        FacilioField nextRunField = modBean.getField("nextRun",FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        ServicePlannedMaintenanceContext servicePlannedMaintenance = (ServicePlannedMaintenanceContext) context.get(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        if(servicePlannedMaintenance!=null && servicePlannedMaintenance.getId()>0){
            Long nextRun = ServicePlannedMaintenanceAPI.getNextRun(servicePlannedMaintenance.getId());
            servicePlannedMaintenance.setNextRun(nextRun);
            V3RecordAPI.updateRecord(servicePlannedMaintenance,servicePMMod, Collections.singletonList(nextRunField));
        }
        return false;
    }
}
