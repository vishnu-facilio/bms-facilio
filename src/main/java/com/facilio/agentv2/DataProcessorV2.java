package com.facilio.agentv2;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentType;
import com.facilio.agent.alarms.AgentEvent;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.iotmessage.IotMessage;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.agentv2.metrics.MetricsApi;
import com.facilio.agentv2.misc.MiscControllerContext;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.DataLogContextV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.FacilioException;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.AlarmApproach;
import com.facilio.remotemonitoring.context.IncomingRawAlarmContext;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.trigger.context.TriggerType;
import com.facilio.util.AckUtil;
import com.facilio.util.FacilioUtil;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class DataProcessorV2 {

    private long orgId;
    private AgentUtilV2 agentUtil;
    private final Map<Long, String> controllerIdVsLastTimeSeriesTimeStamp = new HashMap<>();

    private static final Meter OTEL_METER = GlobalOpenTelemetry.getMeter(DataProcessorV2.class.getSimpleName());

    private static final LongHistogram MESSAGE_COUNT_HISTOGRAM = OTEL_METER
            .histogramBuilder("AgentIncomingMessageMetrics")
            .setDescription("Reports the incoming message count")
            .setUnit("count").ofLongs().build();


    private static final Logger LOGGER = LogManager.getLogger(DataProcessorV2.class.getName());

    public DataProcessorV2(long orgId, AgentUtilV2 agentUtil) {
        this.orgId = orgId;
        this.agentUtil = agentUtil;
    }


    public boolean processRecord(JSONObject payload, FacilioAgent agent,long recordId,int partitionId,String messageSource,int payloadIndex, long startTime) {
        boolean processStatus = false;
        try {

            long timeStamp = getPayloadTimeStamp(payload);
            if (agent.getAgentType() != AgentType.CLOUD.getKey()) {
                agent.setLastDataReceivedTime(timeStamp);
                AgentConstants.getAgentBean().updateAgentLastDataReceivedTime(agent);
            }

            if (!payload.containsKey(AgentConstants.PUBLISH_TYPE)) {
                //add datalog entry with exception here
                LOGGER.info("Exception Occurred, " + AgentConstants.PUBLISH_TYPE + " is mandatory in payload " + payload);
                return false;
            }
            PublishType publishType = PublishType.valueOf(JsonUtil.getInt((payload.get(AgentConstants.PUBLISH_TYPE)))); // change it to Type
            if (publishType == null) {
                //add datalog entry with exception here
                throw new Exception(" publish type cant be null " + JsonUtil.getInt((payload.get(AgentConstants.PUBLISH_TYPE))));
            }


            MESSAGE_COUNT_HISTOGRAM.record(1, Attributes.of(AttributeKey.stringKey("agent"), agent.getName()));

            markMetrices(agent, payload);
            switch (publishType) {
                case CUSTOM:
                    JSONObject customPayload = (JSONObject) payload.clone();
                    processStatus = processCustom(agent, customPayload);
                    break;
                case AGENT:
                    processStatus = processAgent(payload, agent);
                    break;
                case CONTROLLERS:
                    processStatus = processControllers(agent, payload);
                    break;
                case DEVICE_POINTS:
                    processStatus = processDevicePoints(agent, payload);
                    break;
                case ACK:
                    processStatus = processAck(agent, payload);
                    break;
                case TIMESERIES:
                case COV:
                    processStatus = processTimeSeries(agent, timeStamp, payload,recordId,partitionId,messageSource,publishType, payloadIndex, startTime);
                    break;
                case AGENT_EVENTS:
                    processStatus = processAgentEvents(agent, payload);
                    break;
                case ALARM_SOURCE:
                    processStatus = processAlarmSourceEvents(agent,payload);
                    break;
                case EVENTS:
                    processStatus = processIncomingAlarms(agent, payload, timeStamp);
                    break;
                default:
                    throw new Exception("No such Publish type " + publishType.name());
            }
        } catch (Exception e) {
            //add datalog entry for exception
            LOGGER.info("Exception occurred ,", e);
        }
        LOGGER.debug("process status " + processStatus);
        return processStatus;
    }

    // TODO.. Needs to change actual_timestamp value to ts_sec and timestamp value to publish_ts_sec
    // ts_sec would contain data poll time and publish_ts_sec(can be used for debugging) would contain data produced time. actual_ts would be deprecated
    private static long getPayloadTimeStamp(JSONObject payload) {
        Long timeStamp = (Long)payload.get(AgentConstants.TIMESTAMP_SEC);
        if (timeStamp  != null) {
            return timeStamp * 1000;
        }
        // Actual ts will be removed
        if (payload.containsKey(AgentConstants.ACTUAL_TIMESTAMP)) {
            timeStamp = (long) payload.get(AgentConstants.ACTUAL_TIMESTAMP);
        }
        if (timeStamp == null) {
            timeStamp = payload.containsKey(AgentConstants.TIMESTAMP) ? (long)payload.get(AgentConstants.TIMESTAMP)
                    : System.currentTimeMillis();
        }
        return timeStamp;
    }

    private static Controller getOrAddController(JSONObject payload, FacilioAgent agent) throws Exception {
        Controller controller = AgentConstants.getControllerBean().getController(payload, agent.getId());
        if(controller == null){
            FacilioControllerType controllerType = FacilioControllerType.valueOf(((Number) payload.get(AgentConstants.CONTROLLER_TYPE)).intValue());
            if (agent.getAgentTypeEnum().allowAutoAddition(controllerType)) {
                LOGGER.info("Adding Controller for agent "+ agent.getDisplayName() + " of agent type " + agent.getAgentTypeEnum().getLabel());
                MiscControllerContext miscControllerContext = new MiscControllerContext(agent.getId(), AccountUtil.getCurrentOrg().getOrgId());
                JSONObject controllerObj = (JSONObject) payload.get(AgentConstants.CONTROLLER);
                miscControllerContext.setName(controllerObj.get(AgentConstants.NAME).toString());
                miscControllerContext.setDataInterval(agent.getInterval() * 60 * 1000);
                long contorllerId = AgentConstants.getControllerBean().addController(miscControllerContext);
                if(contorllerId!=-1){
                    return miscControllerContext;
                }
            }
        }

        return controller;
    }

    private boolean processAlarmSourceEvents ( FacilioAgent agent,JSONObject payload ) {
        try {
            if(payload.containsKey(AgentConstants.DATA)) {
                List<Map<String, Object>> props = (List<Map<String, Object>>)payload.getOrDefault(AgentConstants.DATA,Collections.emptyList());
                return !props.isEmpty() && EventAPI.addBulkSources(props,agent.getId());
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while adding bulk alarm source list");
        }
        return false;
    }

    private boolean processAck(FacilioAgent agent, JSONObject payload) {
        try {
            payload.put(AgentConstants.IS_NEW_AGENT, Boolean.TRUE);
            IotMessage iotMessage = IotMessageApiV2.getIotMessage(AckUtil.getMessageIdFromPayload(payload));
            if (containsCheck(AgentConstants.CONTROLLER, payload)) {
                Controller controller = AgentConstants.getControllerBean().getController(payload, agent.getId());
                //for modbus device points are sent as ACK's,
                //so redirecting to devicePoints from ack
                if (iotMessage.getCommand()== FacilioCommand.CONFIGURE.asInt()
                        && (controller.getControllerType() == FacilioControllerType.MODBUS_RTU.asInt()
                        || controller.getControllerType() == FacilioControllerType.MODBUS_IP.asInt())){
                    if (agent.getAgentType() == AgentType.FACILIO.getKey()) {
                        return processDevicePoints(agent, payload);
                    }
                }
                if (AckUtil.handleConfigurationAndSubscription(iotMessage, controller, payload)) {
                    return true;
                }
            } else if (iotMessage.getCommand() == FacilioCommand.CONFIGURE_ALL_POINTS.asInt() && containsCheck(AgentConstants.DATA, payload)) {
                    return AckUtil.configureAllPoints(payload, agent);
            } else {
                return AckUtil.processAgentAck(payload, agent.getId(), orgId);
            }
        }catch (Exception e){
            LOGGER.info("Exception while processing ACK ",e);
        }
        return false;
    }

    private boolean processAgentEvents(FacilioAgent agent, JSONObject payload) throws Exception {
        try {
            AgentEvent eventType = getEventType(payload);
            if (eventType != null) {
                if (eventType == AgentEvent.CONTROLLERS_MISSING) {
                    return processControllerMissingEvent(payload, agent);
                }
                if (eventType == AgentEvent.TIMESERIES_DATA_COLLECTION_START) {
                    return true;
                }
                if (eventType == AgentEvent.TIMESERIES_DATA_COLLECTION_END) {
                    return executeTriggers(agent) && AgentUtilV2.sendClearPointAlarm(agent);
                }
                if (eventType == AgentEvent.COMMAND_DELAY) {
                    return processCommandDelayAlarm(payload, agent);
                }
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while processing event ",e);
        }
        return false;
    }

    private boolean executeTriggers(FacilioAgent agent) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.INSTANT_JOB_NAME, "PostTimeseriesWorkflowExecutionJob");
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.TIMESERIES_COMPLETE);
        context.put(FacilioConstants.ContextNames.TRIGGER_TYPE, TriggerType.AGENT_TRIGGER);

        Map<String, FacilioField> triggerFields = FieldFactory.getAsMap(FieldFactory.getAgentTriggerFields());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(triggerFields.get("agentId"), String.valueOf(agent.getId()), NumberOperators.EQUALS));
        context.put(FacilioConstants.ContextNames.CRITERIA, criteria);

        FacilioChain facilioChain = ReadOnlyChainFactory.executeNonModuleTriggersChain();
        facilioChain.setContext(context);
        return !facilioChain.execute();
    }

    private boolean processControllerMissingEvent(JSONObject payload, FacilioAgent agent) throws Exception {
        if (payload.containsKey(AgentConstants.DATA)){
            try{
                JSONArray controllerArray = (JSONArray) payload.get(AgentConstants.DATA);
                if (controllerArray.isEmpty()){
                    ControllerUtilV2.clearControllerAlarm(agent);
                    ControllerUtilV2.makeControllersActive(agent, null);
                } else{
                    List<Controller> listOfControllers = new ArrayList<>();
                    for (Object controllerObj: controllerArray){
                        JSONObject controllerObject = (JSONObject) controllerObj;
                        JSONObject controllerPayload = new JSONObject();

                        if (controllerObject.containsKey(AgentConstants.CONTROLLER_TYPE)) {
                            controllerPayload.put(AgentConstants.CONTROLLER_TYPE, controllerObject.remove(AgentConstants.CONTROLLER_TYPE));
                            controllerPayload.put(AgentConstants.CONTROLLER, controllerObject);
                            Controller controller = AgentConstants.getControllerBean().getController(controllerPayload, agent.getId());
                            if (controller!=null) {
                                listOfControllers.add(controller);
                            } else {
                                LOGGER.info("Controller is null while processing event");
                            }
                        }
                    }
                    if (!listOfControllers.isEmpty()){
                        ControllerUtilV2.raiseControllerAlarm(agent, listOfControllers);
                        List<Long> controllerIds = listOfControllers.stream().map(controller -> controller.getId()).collect(Collectors.toList());
                        ControllerUtilV2.makeControllersActiveAndInactive(agent, controllerIds);
                    }
                }
                return true;
            }catch (Exception ex){
                LOGGER.info("Exception while creating controller alarm ", ex);
                return false;
            }
        }else{
            return false;
        }
    }

    private boolean processCommandDelayAlarm(JSONObject payload, FacilioAgent agent) throws Exception {
        JSONArray dataArr = (JSONArray) payload.get(AgentConstants.DATA);
        JSONObject data = (JSONObject) dataArr.get(0);
        agentUtil.processCommandDelayAlarm(agent, data);
        return true;
    }

    private AgentEvent getEventType(JSONObject payload) {
        AgentEvent eventType = null;
        if (payload.containsKey("event")){
            eventType = AgentEvent.initTypeMap().get(Integer.parseInt(payload.get("event").toString()));
        }
        return eventType;
    }

    private void markMetrices(FacilioAgent agent, JSONObject payload) {
        try {
            MetricsApi.logMetrics(agent,payload);
        } catch (Exception e) {
            LOGGER.info("Exception while logging metrics for ",e);
        }
    }
    public static boolean containsCheck(String key, Map map){
        if( (key != null) && ( ! key.isEmpty()) && ( map != null ) && ( ! map.isEmpty() ) && (map.containsKey(key)) && (map.get(key) != null) ){
            return true;
        }
        return false;
    }


    private boolean processControllers(FacilioAgent agent, JSONObject payload) {
        try {
            return ControllerUtilV2.processControllers(agent, payload);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while processing controller", e);
        }
        return false;
    }


    private void processCOV(JSONObject payload, String agentName) {

    }

    private boolean processDevicePoints(FacilioAgent agent, JSONObject payload) {
        try {
            if (!payload.containsKey(AgentConstants.CONTROLLER)) {
                throw new Exception(" controllerJson missing from discoverPoints payload ->" + payload);
            }

            JSONObject controllerJson = (JSONObject) payload.get(AgentConstants.CONTROLLER);
            if (controllerJson == null || controllerJson.isEmpty()) {
                throw new Exception("Exception occurred, Controller identifier can't be null ->" + payload);
            }
            if( ! payload.containsKey(AgentConstants.CONTROLLER_TYPE)){
                throw new Exception("payload missing controllerType ");
            }
            int type = ((Number)payload.get(AgentConstants.CONTROLLER_TYPE)).intValue();
            Controller controller = AgentConstants.getControllerBean().getController(payload, agent.getId());
            //Device device = FieldDeviceApi.getDevice(agent.getId(), DeviceUtil.getControllerIdentifier(agent, type, controllerJson));
            if (controller != null) {
                return PointsUtil.processPoints(payload, controller, agent);
            } else {
                throw new Exception("Exception occurred, Controller obtained in null");
            }

        } catch (Exception e) {
            LOGGER.info("Exception while processing  points->" + payload+" ",e);
        }
        return false;
    }

    private boolean processAgent(JSONObject payload, FacilioAgent agent) {
        try {
            if (agentUtil.processAgent(payload, agent)) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("Exeception while processing agent", e);
        }
        return false;
    }

    private static void addDefaultValuesForTimeseries(JSONObject payload, FacilioAgent agent) {
        if (agent.getAgentTypeEnum().isCustomPayload()) {
            if (!payload.containsKey(AgentConstants.CONTROLLER) && payload.containsKey(AgentConstants.UNIQUE_ID)) {
                String parentUniqueNum = (String) payload.get(AgentConstants.UNIQUE_ID);
                JSONObject controller = new JSONObject();
                controller.put(FacilioConstants.ContextNames.NAME, parentUniqueNum);
                payload.put(FacilioConstants.ContextNames.CONTROLLER, controller);
                // Controller must be misc in this case
                payload.put(AgentConstants.CONTROLLER_TYPE, ControllerType.MISC.getKey());
            }

            if (!payload.containsKey(AgentConstants.CONTROLLER_TYPE)) {
                payload.put(AgentConstants.CONTROLLER_TYPE, ControllerType.MISC.getKey());
            }
        }

    }


    private boolean processTimeSeries(FacilioAgent agent, long timeStamp, JSONObject payload,long recordId,int partitionId,String messageSource,PublishType publishType, int payloadIndex, long startTime) throws Exception {
        Controller controller = null;
        try {
            addDefaultValuesForTimeseries(payload, agent);
            controller = getOrAddController(payload, agent);
            if (controller == null) {
                throw new FacilioException("The controller in payload is not available");
            }

            JSONObject timeSeriesPayload = (JSONObject) payload.clone();

            // 3.1 version supports multiple timestamp payloads
            // Splitting timestamp payloads adding each timestamp payload separately as version 3
            double payloadVersion = FacilioUtil.parseDouble(payload.getOrDefault(AgentConstants.VERSION, 2));
            if (payloadVersion ==  3.1) {
                List<Map<String, Object>> trendArr = (ArrayList<Map<String, Object>>) timeSeriesPayload.remove(AgentConstants.TIMESERIES);
                if(CollectionUtils.isEmpty(trendArr)) {
                    throw new FacilioException("Timeseries array is mandatory for v3.1");
                }
                int pIndex = 1;  // Assuming payloadindex based on transform payload script from processor util wont be there for 3.1
                Map<String, List<ReadingContext>> mergedReadingMap = new HashMap();
                List<Map<String, Object>> mergedUnmodeledRecords = new ArrayList();
                for(Map<String, Object> trend: trendArr) {
                    JSONObject newPayload = (JSONObject) timeSeriesPayload.clone();
                    newPayload.putAll(trend);
                    long payloadTs = getPayloadTimeStamp(newPayload);
                    Map<String, List<ReadingContext>> readingMap = getReadingContexts(agent, payloadTs, newPayload, controller,recordId,partitionId,messageSource,publishType, pIndex++, startTime, (int)payloadVersion,mergedUnmodeledRecords);
                    mergeReadingMap(mergedReadingMap,readingMap);
                }
                FacilioChain chain = TransactionChainFactory.getTimeSeriesAddOrUpdateChain();
                FacilioContext context = chain.getContext();
                context.put(FacilioConstants.ContextNames.DataProcessor.UNMODELED_RECORDS,mergedUnmodeledRecords);
                context.put(FacilioConstants.ContextNames.READINGS_MAP,mergedReadingMap);
                context.put(FacilioConstants.ContextNames.HISTORY_READINGS,false);
                context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, publishType == PublishType.TIMESERIES);
                Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.FORK_READING_POST_PROCESSING);
                boolean forkPostProcessing = orgInfoMap == null ? false : Boolean.parseBoolean(orgInfoMap.get(FacilioConstants.OrgInfoKeys.FORK_READING_POST_PROCESSING));
                context.put(FacilioConstants.ContextNames.FORK_POST_READING_PROCESSING, forkPostProcessing);
                return !chain.execute();
            }

            // For versions other than 3.1 which will have single timestamp payload
            int messagePartition = 0;
            if (payload.containsKey(AgentConstants.MESSAGE_PARTITION)) {
                messagePartition = Integer.parseInt(payload.get(AgentConstants.MESSAGE_PARTITION).toString());
            }
            Span.current().setAllAttributes(Attributes.of(AttributeKey.stringKey("controller-name"), controller.getName()));


            if (!controllerIdVsLastTimeSeriesTimeStamp.containsKey(controller.getId()) ||
                    !controllerIdVsLastTimeSeriesTimeStamp.get(controller.getId()).equals(timeStamp + "#" + messagePartition)) {

                controllerIdVsLastTimeSeriesTimeStamp.put(controller.getId(), timeStamp + "#" + messagePartition);

                timeSeriesPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, controller.getId());
                addTimeseriesData(agent, timeStamp, timeSeriesPayload, controller,recordId,partitionId,messageSource,publishType, payloadIndex, startTime, (int)payloadVersion);
            } else {
                //add datalog table entry with this exception
                LOGGER.info("Duplicate message for controller id : " + controller.getId() +
                        " a_timestamp : " + timeStamp);
            }
            return true;
        } catch (Exception e) {
            long controllerId = controller != null? controller.getId(): -1l;
            addAgentDataLogForError(e,recordId,agent.getId(),controllerId,messageSource,partitionId,payload,publishType, startTime, payloadIndex);
            LOGGER.info("Exception while processing timeseries data ", e);
        }
        return false;
    }

    private void mergeReadingMap(Map<String, List<ReadingContext>> dest, Map<String, List<ReadingContext>> src){
	    for (String key: src.keySet()){
            List<ReadingContext> readingListDest = dest.getOrDefault(key, new ArrayList<>());
            List<ReadingContext> readingListSrc = src.get(key);
            readingListDest.addAll(readingListSrc);
            dest.put(key, readingListDest);
	    }	   
    }

    private Map<String, List<ReadingContext>> getReadingContexts(FacilioAgent agent, long timeStamp,JSONObject payload, Controller controller, long recordId,int partitionId,String messageSource,PublishType publishType, int payloadIndex, long startTime, int version,List<Map<String, Object>> mergedUnmodeledRecords) throws Exception {
        FacilioChain chain = TransactionChainFactory.getTimeSeriesProcessChainV3();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.RECORD_ID, recordId);
        context.put(AgentConstants.PARTITION_ID, partitionId);
        context.put(AgentKeys.PAYLOAD_INDEX,payloadIndex);
        context.put(AgentConstants.AGENT, agent);
        context.put(AgentConstants.IS_NEW_AGENT, true);
        context.put(AgentConstants.MESSAGE_SOURCE,messageSource);
        context.put(AgentConstants.PUBLISH_TYPE,publishType);
        context.put(AgentKeys.START_TIME, startTime);
        context.put(AgentConstants.CONTROLLER, controller);
        context.put(AgentConstants.CONTROLLER_ID, controller.getId());
        context.put(AgentConstants.AGENT_ID, controller.getAgentId());
        context.put(AgentConstants.TIMESTAMP, timeStamp);
        context.put(AgentConstants.VERSION, version);
        context.put(AgentConstants.PAYLOAD, payload);

        chain.execute();
        mergedUnmodeledRecords.addAll((List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.DataProcessor.UNMODELED_RECORDS));
        return (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.READINGS_MAP);
    }

    private void addTimeseriesData(FacilioAgent agent, long timeStamp,JSONObject payload, Controller controller, long recordId,int partitionId,String messageSource,PublishType publishType, int payloadIndex, long startTime, int version) throws Exception{
        FacilioChain chain = TransactionChainFactory.getTimeSeriesProcessChainV2();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.RECORD_ID, recordId);
        context.put(AgentConstants.PARTITION_ID, partitionId);
        context.put(AgentKeys.PAYLOAD_INDEX,payloadIndex);
        context.put(AgentConstants.AGENT, agent);
        context.put(AgentConstants.IS_NEW_AGENT, true);
        context.put(AgentConstants.MESSAGE_SOURCE,messageSource);
        context.put(AgentConstants.PUBLISH_TYPE,publishType);
        context.put(AgentKeys.START_TIME, startTime);
        context.put(AgentConstants.CONTROLLER, controller);
        context.put(AgentConstants.CONTROLLER_ID, controller.getId());
        context.put(AgentConstants.AGENT_ID, controller.getAgentId());
        context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, publishType == PublishType.TIMESERIES);
        context.put(AgentConstants.TIMESTAMP, timeStamp);
        context.put(AgentConstants.VERSION, version);
        context.put(AgentConstants.PAYLOAD, payload);

        Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.FORK_READING_POST_PROCESSING);
        boolean forkPostProcessing = orgInfoMap == null ? false : Boolean.parseBoolean(orgInfoMap.get(FacilioConstants.OrgInfoKeys.FORK_READING_POST_PROCESSING));
        context.put(FacilioConstants.ContextNames.FORK_POST_READING_PROCESSING, forkPostProcessing);

        chain.execute();
        LOGGER.debug(" done processes data command ");

    }

    private void addAgentDataLogForError(Exception e,long recordId,long agentId,long controllerId,String messageSource,int partitionId,JSONObject payload,PublishType publishType, long startTime, int payloadIndex) throws Exception {

        DataLogContextV3 datalog = new DataLogContextV3();
        datalog.setRecordId(recordId);
        datalog.setPartitionId(partitionId);
        datalog.setAgentId(agentId);
        datalog.setControllerId(controllerId);
        datalog.setMessageSource(messageSource);
        datalog.setPayload(payload.toJSONString());
        datalog.setStartTime(startTime);
        datalog.setMessageStatus(DataLogContextV3.Agent_Message_Status.FAILURE.getKey());
        String errorMessage = e.getMessage() != null ? e.getMessage() : "Internal Server Error";
        datalog.setErrorStackTrace(errorMessage);
        datalog.setPublishType(publishType.asInt());
        datalog.setStartTime(startTime);
        datalog.setEndTime(System.currentTimeMillis());
        datalog.setPayloadIndex(payloadIndex);

        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule agentDataModule = modbean.getModule(FacilioConstants.ContextNames.AGENT_DATA_LOGGER);
        List<FacilioField> agentDataFields = modbean.getAllFields(FacilioConstants.ContextNames.AGENT_DATA_LOGGER);
        InsertRecordBuilder<DataLogContextV3> builder1 = new InsertRecordBuilder<DataLogContextV3>()
                .module(agentDataModule)
                .fields(agentDataFields)
                .addRecord(datalog);
        builder1.save();

    }

    private boolean processCustom(FacilioAgent agent,JSONObject payload) {

    	try {
            Controller customController = AgentConstants.getControllerBean().getController(payload,agent.getId());
            JSONObject customPayload = (JSONObject) payload.clone();
            if (customController != null) {
                customPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, customController.getId());
                customPayload.put(FacilioConstants.ContextNames.CONTROLLER, FieldUtil.getAsJSON(customController));
            }


            FacilioChain chain = TransactionChainFactory.getAddCustomDataChain();
            FacilioContext context = chain.getContext();
            //TODO
            context.put(AgentConstants.CONTROLLER, customController);
            if (customController != null) {
                context.put(AgentConstants.CONTROLLER_ID, customController.getId());
                context.put(AgentConstants.AGENT_ID, customController.getAgentId());
            }

            context.put(AgentConstants.DATA, payload);

            if (payload.containsKey(AgentConstants.TIMESTAMP) && (payload.get(AgentConstants.TIMESTAMP) != null)) {
                context.put(AgentConstants.TIMESTAMP, payload.get(AgentConstants.TIMESTAMP));
            } else {
                context.put(AgentConstants.TIMESTAMP, System.currentTimeMillis());
            }
            chain.execute();
            return true;
        } catch (Exception e) {
            LOGGER.info("Exception while processing custom ", e);
        }
        return false;
    }

    private long getAgentId(String agent) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewAgentModule().getTableName())
                .select(FieldFactory.getNewAgentFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(ModuleFactory.getNewAgentModule()),agent, StringOperators.IS));
        List<Map<String, Object>> rows =selectRecordBuilder.get();
        if (rows.size()>0){
            return Long.parseLong(rows.get(0).get(AgentConstants.ID).toString());
        }
        return -1;
    }

    private boolean processIncomingAlarms(FacilioAgent agent, JSONObject payload, long timestamp) throws Exception {
        JSONArray events;
        if(payload.containsKey(AgentConstants.EVENT_VERSION) && FacilioUtil.parseInt(payload.get(AgentConstants.EVENT_VERSION)) == 2){
            events = (JSONArray) payload.get(EventConstants.EventContextNames.EVENT_LIST);
        }
        else {
            events = new JSONArray();
            events.add(payload);
        }

        FacilioAgent.AgentBMSAlarmProcessorType alarmProcessorType = agent.getAlarmProcessorTypeEnum();
        // If raw alarm or both
        try{
            if (alarmProcessorType != null &&  alarmProcessorType != FacilioAgent.AgentBMSAlarmProcessorType.BMS_ALARM) {
                Controller controller = AgentConstants.getControllerBean().getController(payload, agent.getId());
                processRawAlarm(agent,controller,events, timestamp);
                if (alarmProcessorType == FacilioAgent.AgentBMSAlarmProcessorType.RAW_ALARM) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception while processing raw alarm", e);
        }

        // removing controller object from payload for bmsevent
        payload.remove("controller");

        processBmsEvents(events, timestamp);

        return true;
    }

    private void processRawAlarm(FacilioAgent agent, Controller controller, JSONArray alarms, long timestamp) throws Exception {
        for (int i = 0; i < alarms.size(); i++) {
            JSONObject rawAlarm = (JSONObject) alarms.get(i);
            IncomingRawAlarmContext alarmContext = new IncomingRawAlarmContext();
            alarmContext.setMessage((String) rawAlarm.get("message"));
            alarmContext.setController(controller);
            alarmContext.setSourceType(IncomingRawAlarmContext.RawAlarmSourceType.CONTROLLER);
            String state = (String) rawAlarm.get("state");
            if(state.equals("Alarm")) {
                alarmContext.setOccurredTime(timestamp);
            } else {
                alarmContext.setClearedTime(timestamp);
            }
            if (controller.getControllerType() == FacilioControllerType.E2.asInt()) {
                alarmContext.setAlarmApproach(AlarmApproach.RETURN_TO_NORMAL.getIndex());
            }
            RawAlarmUtil.pushToStormRawAlarmQueue(alarmContext);
            ControllerUtilV2.processUpdateLastDataReceivedTimeAndClearControllerAlarm(agent, controller);
        }
    }

    private void processBmsEvents(JSONArray events, long timestamp) throws Exception {
        List<EventRuleContext> eventRules = new ArrayList<>();
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        List<EventRuleContext> ruleList = bean.getActiveEventRules();
        if (ruleList != null) {
            eventRules = ruleList;
        }
        bean.processEvents(timestamp, events, eventRules);
    }

}
