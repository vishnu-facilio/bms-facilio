package com.facilio.agentv2.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
public class ProcessMultiTimeDataCommand  extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        //Temp check
        Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.PUSH_TIMESERIES_TO_STORM);
        boolean pushToStorm = orgInfoMap != null && Boolean.parseBoolean(orgInfoMap.get(FacilioConstants.OrgInfoKeys.PUSH_TIMESERIES_TO_STORM));
        if (!pushToStorm){
            return false;
        }

        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
        if (MapUtils.isNotEmpty(readingMap)) {
            MessageQueue queue = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());
            long orgId = AccountUtil.getCurrentOrg().getId();
            for(Iterator<Map.Entry<String, List<ReadingContext>>> mapItr = readingMap.entrySet().iterator(); mapItr.hasNext(); ) {
                Map.Entry<String, List<ReadingContext>> entry = mapItr.next();
                String moduleName = entry.getKey();
                List<ReadingContext> readings = entry.getValue();
                long parentId = -1l;
                JSONArray estimatedReadings = new JSONArray();
                for(Iterator<ReadingContext> i = readings.iterator(); i.hasNext();) {
                    ReadingContext reading = i.next();
                    if (containsEstimated(reading)) {
                        Map<String, Object> readingObj = createReadingObj(reading);
                        estimatedReadings.add(readingObj);
                        parentId = reading.getParentId();   // Assuming all readings will have same parentId
                        i.remove();
                    }
                }

                if (!estimatedReadings.isEmpty()) {
                    JSONObject data = new JSONObject();
                    data.put("orgId", orgId);
                    data.put("moduleName", moduleName);
                    data.put("data", estimatedReadings);
                    queue.put(getTopicName(), new FacilioRecord(orgId + "/" + parentId, data));

                    if (readings.isEmpty()) {
                        mapItr.remove();
                    }
                }
            }
        }

        return MapUtils.isEmpty(readingMap);
    }

    private String getTopicName() {
        return FacilioProperties.getStableEnvironment() + "-timeseries-crud-queue";
    }

    private boolean containsEstimated(ReadingContext reading) {
        Set<String> readingNames = reading.getReadings().keySet();
        return readingNames.contains("energyconsumptionisestimated") || readingNames.contains("generatedenergyisestimated");
    }

    private Map<String, Object> createReadingObj(ReadingContext readingContext) {
        Map<String, Object> reading = new HashMap<>();
        reading.put("ttime", readingContext.getTtime());
        reading.put("parentId", readingContext.getParentId());
        reading.put("dataInterval", readingContext.getDataInterval());
        reading.put("readings", readingContext.getReadings());
        return reading;
    }
}
