package com.facilio.remotemonitoring.commands;

import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.IncomingRawAlarmContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class GenerateRawAlarmsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Map<String,Object>> alarmList = (List<Map<String, Object>>) context.get(RemoteMonitorConstants.RAW_ALARMS);
        if(CollectionUtils.isNotEmpty(alarmList)) {
            if(alarmList.size() >= 5000) {
                FacilioUtil.throwIllegalArgumentException(true,"Raw alarm list size is greater than 5000");
            }
            int i = 0;
            for(Map<String,Object> alarm : alarmList) {
                ++i;
                IncomingRawAlarmContext rawAlarmContext = new IncomingRawAlarmContext();
                rawAlarmContext.setMessage((String) alarm.get("message"));
                if (alarm.containsKey("occurredTime")){
                    rawAlarmContext.setOccurredTime((Long) alarm.get("occurredTime"));
                } else {
                    rawAlarmContext.setOccurredTime(System.currentTimeMillis() + i);
                }
                if (alarm.containsKey("clearedTime")){
                    rawAlarmContext.setClearedTime((Long) alarm.get("clearedTime"));
                }
                rawAlarmContext.setAlarmApproach(Integer.valueOf(alarm.get("strategy").toString()));
                Controller controller = new Controller();
                controller.setId((Long) alarm.get("controllerId"));
                rawAlarmContext.setController(controller);
                rawAlarmContext.setSourceType(IncomingRawAlarmContext.RawAlarmSourceType.SIMULATOR);
                if (alarm.containsKey("assetId")){
                    V3AssetContext asset = new V3AssetContext();
                    if(alarm.get("assetId") != null) {
                        String assetId = alarm.get("assetId").toString();
                        if (StringUtils.isNotEmpty(assetId)) {
                            asset.setId(Long.parseLong(assetId));
                            rawAlarmContext.setAsset(asset);
                        }
                    }
                }
                RawAlarmUtil.pushToStormRawAlarmQueue(rawAlarmContext);
            }
        }
        return false;
    }
}