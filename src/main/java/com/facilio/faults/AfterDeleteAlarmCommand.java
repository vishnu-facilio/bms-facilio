package com.facilio.faults;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.handler.AlarmDeleteHandler;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class AfterDeleteAlarmCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<BaseAlarmContext> baseAlarms = recordMap.get(moduleName);
        baseAlarms.forEach(baseAlarm -> {
            JSONObject content = new JSONObject();
            content.put("alarmId",baseAlarm.getId());
            WmsBroadcaster.getBroadcaster().sendMessage(new Message()
                    .setTopic(AlarmDeleteHandler.TOPIC)
                    .setContent(content)
            );
        });
        return false;
    }
}