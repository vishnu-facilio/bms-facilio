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
import java.util.stream.Collectors;

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
            for (Map.Entry<String, List<ReadingContext>> e : readingMap.entrySet()) {
                String moduleName = e.getKey();
                List<ReadingContext> readings = e.getValue();
                long parentId = readings.get(0).getParentId(); // Assuming all readings will have same parentId
                List<Map<String, Object>> readingArray =  readings.stream().map(r -> createReadingObj(r)).collect(Collectors.toList());
                JSONObject data = new JSONObject();
                data.put("orgId", orgId);
                data.put("moduleName", moduleName);
                data.put("data", readingArray);
                queue.put(getTopicName(), new FacilioRecord(orgId + "/" + parentId + "/" + moduleName, data));
            }
        }

        return true; // As data is already pushed to storm, no need to continue further
    }

    private String getTopicName() {
        return FacilioProperties.getStableEnvironment() + "-timeseries-crud-queue";
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