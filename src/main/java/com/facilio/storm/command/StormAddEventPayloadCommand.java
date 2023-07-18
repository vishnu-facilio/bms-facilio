package com.facilio.storm.command;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.util.DBConf;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.procon.message.FacilioRecord;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class StormAddEventPayloadCommand extends FacilioCommand {
    private static final String CAN_EMIT_STORM = "bms.alarm.canEmitToStorm";
    private static final String BMS_SITE_IDS = "bms.alarm.siteIds";

    boolean isHistorical;

    public StormAddEventPayloadCommand(boolean isHistorical) {
        this.isHistorical = isHistorical;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<BaseEventContext> events = (List) context.getOrDefault(EventConstants.EventContextNames.EVENT_LIST, new ArrayList<>());

        if (CollectionUtils.isNotEmpty(events)) {
            setDefaultProps(events);
            Map<String, Object> orgInfoForAlarm = getOrgInfoForAlarm();

            boolean canEmit = false;

            if ((boolean) orgInfoForAlarm.get(CAN_EMIT_STORM)) {
                List<Long> siteIdsList = (List<Long>) orgInfoForAlarm.get(BMS_SITE_IDS);
                canEmit = CollectionUtils.isEmpty(siteIdsList) || isMatchedBMSIds(siteIdsList, events);
            }

            if (canEmit) {
                emitToStorm(events);
            } else {
                FacilioChain v2AddEventChain = TransactionChainFactory.getV2AddEventChain(isHistorical);
                v2AddEventChain.setContext((FacilioContext) context);
                v2AddEventChain.execute();
            }
        }

        return false;
    }

    private void emitToStorm(List<BaseEventContext> events) throws Exception {
        MessageQueue mq = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());

        long orgId = events.get(0).getOrgId();
        long resourceId = events.get(0).getResource().getId();
        BaseAlarmContext.Type alarmType = events.get(0).getType();

        JSONArray eventArr = FieldUtil.getAsJSONArray(events, NewEventAPI.getEventClass(alarmType));

        JSONObject json = new JSONObject();
        json.put("orgId", events.get(0).getOrgId());
        json.put("data", eventArr);

        String partitionKey = "event-exec/" + orgId + "/" + resourceId;

        mq.put(getTopicName(), new FacilioRecord(partitionKey, json));

    }

    private String getTopicName() {
        return FacilioProperties.getEnvironment() + "-storm-e2a-payload";
    }

    private boolean isMatchedBMSIds(List<Long> bmsidsList, List<BaseEventContext> events) {
        return events.stream().anyMatch(ev -> bmsidsList.contains(ev.getSiteId()));
    }

    private Map<String, Object> getOrgInfoForAlarm() throws Exception {

        Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(new String[]{CAN_EMIT_STORM, BMS_SITE_IDS});
        Map<String, Object> newOrgInfo = new HashMap<>();
        if (MapUtils.isNotEmpty(orgInfo)) {
            String siteIdsStr = orgInfo.getOrDefault(BMS_SITE_IDS, "").trim();
            if (!siteIdsStr.equals("")) {
                String[] siteIdArr = siteIdsStr.split(",");
                List<Long> siteIdList = Arrays.stream(siteIdArr).map(Long::parseLong).collect(Collectors.toList());
                newOrgInfo.put(BMS_SITE_IDS, siteIdList);
            }
        }
        newOrgInfo.put(CAN_EMIT_STORM, orgInfo.getOrDefault(CAN_EMIT_STORM, "false").equals("true"));
        return newOrgInfo;
    }

    private void setDefaultProps(List<BaseEventContext> events) {

        for (BaseEventContext event : events) {
            if (event instanceof ReadingEventContext) {
                event.setType(BaseAlarmContext.Type.READING_ALARM);
            } else {
                //As default BMS Alarm
                event.setType(BaseAlarmContext.Type.BMS_ALARM);
            }

            if(event.getOrgId() == -1) {
                event.setOrgId(DBConf.getInstance().getCurrentOrgId());
            }
        }

    }
}