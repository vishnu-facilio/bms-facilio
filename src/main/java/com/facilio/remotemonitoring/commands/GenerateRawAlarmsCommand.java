package com.facilio.remotemonitoring.commands;

import com.facilio.agentv2.controller.Controller;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.AlarmStrategy;
import com.facilio.remotemonitoring.context.RawAlarmContext;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.naming.ldap.Control;
import java.util.Collections;
import java.util.HashMap;
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
                RawAlarmContext rawAlarmContext = new RawAlarmContext();
                rawAlarmContext.setMessage((String) alarm.get("message"));
                if (alarm.containsKey("occurredTime")){
                    rawAlarmContext.setOccurredTime((Long) alarm.get("occurredTime"));
                } else {
                    rawAlarmContext.setOccurredTime(System.currentTimeMillis() + i);
                }
                if (alarm.containsKey("clearedTime")){
                    rawAlarmContext.setClearedTime((Long) alarm.get("clearedTime"));
                }
                rawAlarmContext.setStrategy(Integer.valueOf(alarm.get("strategy").toString()));
                Controller controller = new Controller();
                controller.setId((Long) alarm.get("controllerId"));
                rawAlarmContext.setController(controller);
                rawAlarmContext.setSourceType(RawAlarmContext.RawAlarmSourceType.SIMULATOR);
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