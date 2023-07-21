package com.facilio.agent.commands;

import com.facilio.agent.AgentKeys;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.bmsconsoleV3.context.DataLogContextV3;
import com.facilio.bmsconsoleV3.context.DataLogSummaryContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class AddAgentDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        try{
            ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule agentDataModule = modbean.getModule(FacilioConstants.ContextNames.AGENT_DATA_LOGGER);
            if(agentDataModule != null && agentDataModule.getModuleId() > 0 ){
                PublishType publishType = (PublishType) context.get(AgentConstants.PUBLISH_TYPE);
                if(publishType == PublishType.TIMESERIES || publishType == PublishType.COV){
                    long startTime = (long) context.get(AgentKeys.START_TIME);
                    Map<String,String>errorPoints =  (Map<String,String>) context.get("errorPoints");
                    long recordId = (long) context.get(AgentConstants.RECORD_ID);
                    int partitionId = (int) context.get(AgentConstants.PARTITION_ID);
                    String messageSource = (String) context.get(AgentConstants.MESSAGE_SOURCE);
                    JSONObject payload = (JSONObject) context.get(AgentConstants.DATA);
                    JSONArray data = (JSONArray) payload.get("data");
                    JSONObject jsonObject = (JSONObject) data.get(0);
                    Map<String, Object> pointMap = new HashMap<String, Object>();

                    for (String key : (Iterable<String>) jsonObject.keySet()) {
                        Object value = jsonObject.get(key);

                        pointMap.put(key, value);
                    }
                    List<String>pointNames = new ArrayList<String>(pointMap.keySet());

                    //parent module insert
                    FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
                    Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
                    DataLogContextV3 datalog = new DataLogContextV3();
                    datalog.setPartitionId(partitionId);
                    datalog.setMessageSource(messageSource);
                    datalog.setRecordId(recordId);
                    datalog.setPayload(payload.toJSONString());
                    datalog.setPublishType(publishType.asInt());
                    datalog.setStartTime(startTime);
                    datalog.setEndTime(System.currentTimeMillis());
                    datalog.setAgent(agent);
                    datalog.setController(controller);
                    String errorMessage = null;
                    if(errorPoints.size() > 3){
                        errorMessage = "Error occured for Points : "+ String.join(", ",(errorPoints.keySet().stream().limit(3).collect(Collectors.toSet()))) + " + " + (errorPoints.size() - 3) +" more";
                    }
                    else{
                        errorMessage = "Error occured for Points : "+ String.join(", ",errorPoints.keySet());
                    }

                    setMessageStatus(errorMessage,errorPoints,datalog,pointNames);

                    //parent module insert

                    List<FacilioField> agentDataFields = modbean.getAllFields(FacilioConstants.ContextNames.AGENT_DATA_LOGGER);
                    InsertRecordBuilder<DataLogContextV3> builder1 = new InsertRecordBuilder<DataLogContextV3>()
                            .module(agentDataModule)
                            .fields(agentDataFields)
                            .addRecord(datalog);
                    builder1.save();
                    long parentId = datalog.getId();

                    List<DataLogSummaryContextV3> props = getSummaryModuleProps( pointNames,controller,recordId,pointMap,parentId,errorPoints);

                        //child module insert
                        FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.AGENT_DATA_PROCESSING_LOGGER);
                        List<FacilioField> fields = modbean.getModuleFields(FacilioConstants.ContextNames.AGENT_DATA_PROCESSING_LOGGER);

                        InsertRecordBuilder<DataLogSummaryContextV3> builder = new InsertRecordBuilder<DataLogSummaryContextV3>()
                                .module(module)
                                .fields(fields)
                                .addRecords(props);
                        builder.save();

                        return false;
                }
            }
            return false;

        }catch(Exception e){
            LOGGER.info(e);
            return false;
        }
    }
private List<DataLogSummaryContextV3> getSummaryModuleProps(List<String>pointNames,Controller controller,long recordId,Map<String, Object> pointMap,long parentId,Map<String,String>errorPoints) throws Exception {
    List<DataLogSummaryContextV3> props = new ArrayList<>();
    List<Map<String, Object>> pointsFromDb = PointsAPI.getPointsFromDb(pointNames,controller);
    for(Map<String, Object> point : pointsFromDb){
        DataLogSummaryContextV3 summaryContext = new DataLogSummaryContextV3();
        summaryContext.setRecordId(recordId);
        summaryContext.setPoint((String) point.get("name"));
        Long fieldId = (Long) point.get("fieldId");
        Long resourceId = (Long) point.get("resourceId");
        if(resourceId !=null){
            summaryContext.setAssetId(resourceId);
        }
        if(fieldId != null ){
            summaryContext.setReadingId(fieldId);
        }
        summaryContext.setValue(String.valueOf(pointMap.get(point.get("name"))));
        summaryContext.setParentId(parentId);
        summaryContext.setControllerId(controller.getId());
        if (errorPoints!=null && !errorPoints.isEmpty()){
            if(errorPoints.keySet().contains((String) point.get("name"))){
                //fail case
                summaryContext.setStatus(DataLogSummaryContextV3.TimeSeries_Status.FAILURE.getKey());
                summaryContext.setErrorStackTrace(errorPoints.get((String) point.get("name")));
            }
            else{summaryContext.setStatus(DataLogSummaryContextV3.TimeSeries_Status.SUCCESS.getKey());}
        }
        else{summaryContext.setStatus(DataLogSummaryContextV3.TimeSeries_Status.SUCCESS.getKey());}
        //set status from the list in context
        props.add(summaryContext);
    }
    return props;
}
  private void  setMessageStatus(String errorMessage,Map<String,String>errorPoints,DataLogContextV3 datalog,List<String>pointNames){
      if(errorPoints == null || errorPoints.isEmpty()){
          datalog.setMessageStatus(DataLogContextV3.Agent_Message_Status.SUCCESS.getKey());
          //complete success
      }
      else if(errorPoints.size() == pointNames.size()){

          datalog.setMessageStatus(DataLogContextV3.Agent_Message_Status.FAILURE.getKey());
          datalog.setErrorStackTrace(errorMessage);
          // complete failure
      }
      else if (errorPoints.size() < pointNames.size()){
          datalog.setMessageStatus(DataLogContextV3.Agent_Message_Status.PARTIAL_SUCCESS.getKey());
          datalog.setErrorStackTrace(errorMessage);
          //partial success
      }
  }

}
