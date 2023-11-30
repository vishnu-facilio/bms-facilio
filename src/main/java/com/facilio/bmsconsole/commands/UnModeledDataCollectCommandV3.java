package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.point.Point;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnModeledDataCollectCommandV3 extends AgentV2Command {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, String> data = (Map<String, String>) context.get(FacilioConstants.ContextNames.DataProcessor.UNMODELED);
        long timeStamp = Long.parseLong(context.get(FacilioConstants.ContextNames.TIMESTAMP).toString());
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        Map<String, Point> pointRecords = (Map<String, Point>) context.get(FacilioConstants.ContextNames.DataProcessor.POINT_RECORDS);
        for (Map.Entry<String, String> map : data.entrySet()) {
            String pointName = map.getKey();
            Object instanceVal = map.getValue();
            Map<String, Object> record = new HashMap<String, Object>();
            record.put(AgentConstants.VALUE, instanceVal);
            record.put(AgentConstants.TTIME, timeStamp);
            record.put(AgentConstants.INSTANCE_ID, pointRecords.get(pointName).getId());
            records.add(record);
        }
        context.put(FacilioConstants.ContextNames.DataProcessor.UNMODELED_RECORDS,records);
        return false;
    }
}
