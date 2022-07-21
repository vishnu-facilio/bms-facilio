package com.facilio.bmsconsoleV3.commands.workorder;


import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.FacilioStatus;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class ClearAlarmOnWOClosureCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(ClearAlarmOnWOClosureCommand.class.getName());

    private boolean isClosed(V3WorkOrderContext workOrder) {
        FacilioStatus closedStatus = null;
        try {
            closedStatus = TicketAPI.getStatus("Closed");
        } catch (Exception e) {
            LOGGER.error("unable to fetch closed state");
        }
        return workOrder.getStatus() != null &&
                closedStatus != null &&
                workOrder.getStatus().getId() == closedStatus.getId();
    }

    private boolean sourcedFromAlarm(V3WorkOrderContext workOrder) {
        V3TicketContext.SourceType source = workOrder.getSourceTypeEnum();
        return source.equals(V3TicketContext.SourceType.ALARM) ||
                source.equals(V3TicketContext.SourceType.THRESHOLD_ALARM) ||
                source.equals(V3TicketContext.SourceType.ANOMALY_ALARM) ||
                source.equals(V3TicketContext.SourceType.ML_ALARM);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<V3WorkOrderContext> workOrders = (List<V3WorkOrderContext>)
                context.get(FacilioConstants.ContextNames.RECORD_LIST);

        if (CollectionUtils.isEmpty(workOrders)) {
            return false;
        }

        List<Long> woIds = workOrders.stream()
                .filter(this::isClosed)
                .filter(this::sourcedFromAlarm)
                .map(V3WorkOrderContext::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(woIds)) {
            return false;
        }
        List<AlarmContext> alarms = AlarmAPI.getActiveAlarmsFromWoId(woIds);
        for (AlarmContext alarm : alarms) {
            LOGGER.info("clearing alarm " + alarm.getId() + " since WO is closed");
            FacilioContext addEventContext = new FacilioContext();

            JSONObject event = AlarmAPI.constructClearEvent(alarm, "System auto cleared Alarm " +
                    "because associated Workorder was closed");
            addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, event);

            FacilioChain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
            getAddEventChain.execute(addEventContext);
        }

        return false;
    }
}
