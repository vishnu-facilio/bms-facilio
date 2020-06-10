package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FetchSourceTypeDetailsForWorkorderCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(wos)){
            V3WorkOrderContext workOrder = wos.get(0);
            if (workOrder != null) {
                switch (workOrder.getSourceTypeEnum()) {
                    case THRESHOLD_ALARM:
                    case ALARM:
                    case ANOMALY_ALARM:
                    case ML_ALARM:
                        List<AlarmContext> alarms = AlarmAPI.getActiveAlarmsFromWoId(Collections.singletonList(workOrder.getId()));
                        if (alarms != null && !alarms.isEmpty()) {
                            workOrder.setAlarm(alarms.get(0));
                        }
                        break;
                }
            }
        }
        return false;
    }
}
