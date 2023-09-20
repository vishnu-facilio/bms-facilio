package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.fsm.util.ServicePlannedMaintenanceAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.getServiceOrderStatus;

public class UpdateNextRunCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServicePlannedMaintenanceContext> servicePlannedMaintenanceList = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(servicePlannedMaintenanceList)){
            for(ServicePlannedMaintenanceContext servicePlannedMaintenance : servicePlannedMaintenanceList) {
                if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("publishServicePM") && (boolean) bodyParams.get("publishServicePM")) {
                     Long nextRun = ServicePlannedMaintenanceAPI.getNextRun(servicePlannedMaintenance.getId());
                     servicePlannedMaintenance.setNextRun(nextRun);
                }
            }
        }
        return false;
    }
}
