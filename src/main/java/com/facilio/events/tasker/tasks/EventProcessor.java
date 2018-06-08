package com.facilio.events.tasker.tasks;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventAPI;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.BeanFactory;

public class EventProcessor implements IRecordProcessor {

    public static final String DATA_TYPE = "PUBLISH_TYPE";
//    private List<EventRule> eventRules = new ArrayList<>();
    private List<EventRuleContext> eventRules = new ArrayList<>();
    private Map<String, Integer> eventCountMap = new HashMap<>();
    private long orgId;
    private long lastEventTime = System.currentTimeMillis();
    private String shardId;
    private String orgDomainName;

    private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    public EventProcessor(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        this.shardId = initializationInput.getShardId();
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<EventRuleContext> ruleList = bean.getActiveEventRules();
            if(ruleList != null){
                eventRules = ruleList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Record record : processRecordsInput.getRecords()) {
            String data = "";
            try {
                data = decoder.decode(record.getData()).toString();
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(data);
                if(object.containsKey(DATA_TYPE)){
                    String dataType = (String)object.get(DATA_TYPE);
                    if("event".equalsIgnoreCase(dataType)){
                        boolean alarmCreated = processEvents(record.getApproximateArrivalTimestamp().getTime(), object);
                        if (alarmCreated) {
                            processRecordsInput.getCheckpointer().checkpoint(record);
                        }
                    }
                } else {
                    boolean alarmCreated = processEvents(record.getApproximateArrivalTimestamp().getTime(), object);
                    if (alarmCreated) {
                        processRecordsInput.getCheckpointer().checkpoint(record);
                    }
                }
            } catch (Exception e) {
            		CommonCommandUtil.emailException("Error in processing records : "
            			+record.getSequenceNumber()+ " in EventProcessor ", e, data);
            		e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput) {
        System.out.println("Shutting down record processor for stream: "+ orgDomainName +" and shard: " + shardId);
    }


    private boolean processEvents(long timestamp, JSONObject object) throws Exception {
    	
    	ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        long currentExecutionTime = bean.processEvents(timestamp, object, eventRules, eventCountMap, lastEventTime);
        if(currentExecutionTime != -1) {
        	lastEventTime = currentExecutionTime;
        	return true;
        }
        return false;
    }
}
