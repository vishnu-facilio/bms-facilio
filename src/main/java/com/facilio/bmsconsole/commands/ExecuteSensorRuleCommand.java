package com.facilio.bmsconsole.commands;

import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpEventContext;
import com.facilio.alarms.sensor.util.SensorRollUpUtil;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.events.constants.EventConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecuteSensorRuleCommand extends FacilioCommand implements PostTransactionCommand {

    private static final Logger LOGGER = Logger.getLogger(ExecuteSensorRuleCommand.class.getName());
    List<SensorRollUpEventContext> sensorMeterRollUpEvents = new ArrayList<SensorRollUpEventContext>();

    @Override
    public boolean executeCommand(Context context) throws Exception {

        try {
            Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
            if (readingMap != null && !readingMap.isEmpty()) {
                for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
                    String moduleName = entry.getKey();
                    List<ReadingContext> readings = new LinkedList<>(entry.getValue());
                    if (moduleName == null || moduleName.isEmpty() || readings == null || readings.isEmpty()) {
                        LOGGER.log(Level.WARNING, "Module Name / Records is null/ empty while executing readings in ExecuteSensorRuleCommand ==> " + moduleName + "==>" + entry.getValue());
                        continue;
                    }
                    List<SensorRuleContext> sensorRules = SensorRuleUtil.fetchSensorRulesByModule(moduleName, true, false);
                    //	SensorRuleUtil.executeSensorRules(sensorRules, readings, false, sensorMeterRollUpEvents);

                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ExecuteSensorRuleCommand -- " +
                    " Exception: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {

        if (sensorMeterRollUpEvents != null && !sensorMeterRollUpEvents.isEmpty()) {
            List<SensorRollUpEventContext> finalSensorAssetRollUpEvents = new ArrayList<SensorRollUpEventContext>();
            List<String> addedEventsMeterAndTime = SensorRollUpUtil.getSensorRollUpAlarmsByAssetAndTime(sensorMeterRollUpEvents);

            for (SensorRollUpEventContext sensorMeterRollUpEvent : sensorMeterRollUpEvents) {
                String key = sensorMeterRollUpEvent.getResource().getId() + "_" + sensorMeterRollUpEvent.getCreatedTime();
                if (!addedEventsMeterAndTime.contains(key)) {
                    finalSensorAssetRollUpEvents.add(sensorMeterRollUpEvent);
                }
            }
            FacilioChain addEventChain = TransactionChainFactory.getV2AddEventChain(true);
            addEventChain.getContext().put(EventConstants.EventContextNames.EVENT_LIST, finalSensorAssetRollUpEvents);
            addEventChain.execute();
        }
        return false;
    }

}
