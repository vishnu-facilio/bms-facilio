package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessDataCommandV2 extends AgentV2Command {

    private static final Logger LOGGER = LogManager.getLogger(ProcessDataCommandV2.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        if( containsCheck(AgentConstants.DATA,context) ){
            Object payloadObject = context.get(AgentConstants.DATA);
            if( payloadObject instanceof JSONObject){
                JSONObject payload = (JSONObject) payloadObject;

                if( containsCheck(AgentConstants.CONTROLLER,context) ){
                    Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
                    if( containsCheck(AgentConstants.DATA,payload)){
                        JSONArray pointData = (JSONArray) payload.get(AgentConstants.DATA);
                        List<String> pointNames = new ArrayList<>();
                        JSONObject pointJSON;
                        Map<String, Map<String,String>> deviceData= new HashMap<>();
                        deviceData.put(controller.getName(),new HashMap<>());
                        for (Object point : pointData) {
                            pointJSON = (JSONObject) point;
                            if (!pointJSON.isEmpty()) {
                                for (Object key : pointJSON.keySet()) {
                                    String pointName = (String) key;
                                    if(containsCheck(pointName,pointJSON)){
                                        pointNames.add(pointName);
                                        deviceData.get(controller.getName()).put(pointName, String.valueOf(pointJSON.get(key)));
                                    }
                                }
                                context.put(FacilioConstants.ContextNames.DEVICE_DATA,deviceData);
                            } else {
                                LOGGER.info(" points can't be empty ");
                            }
                        }
                        if( ! pointNames.isEmpty()){
                            Criteria criteria = new Criteria();
                            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(ModuleFactory.getPointModule()), String.valueOf(controller.getId()), NumberOperators.EQUALS));
                            criteria.addAndCondition(CriteriaAPI.getNameCondition(String.join(",",pointNames),ModuleFactory.getPointModule()));

                            List<Map<String, Object>> pointsFromDb = getPointData(criteria);
                            if( ! pointsFromDb.isEmpty() ){
                                for (Map<String, Object> pointRow : pointsFromDb) {
                                    if( ! pointRow.isEmpty()){
                                        pointRow.put(AgentConstants.CONTROLLER_NAME,controller.getName());
                                        reformatDataPoint(pointRow);
                                    }
                                }
                                context.put("DATA_POINTS",pointsFromDb);
                            }else {
                                throw new Exception("points from db can't be empty");
                            }

                        }else {
                            throw new Exception(" points name can't be empty");
                        }
                    }
                }else {
                    throw new Exception("Controller missing from payload");
                }
            }else {
                throw new Exception(" payload must be a jsonObject");
            }

        }else {
            throw new Exception("payload missing from context->"+context);
        }
        LOGGER.info("--------process data command v2---------");
        for (Object key : context.keySet()) {
            LOGGER.info(key+"->"+context.get(key));
        }
        LOGGER.info("----------------------------------------");

        return false;
    }

    private void reformatDataPoint(Map<String, Object> pointRow) {

        if(containsCheck(AgentConstants.NAME,pointRow)){
            pointRow.put("instance",pointRow.get(AgentConstants.NAME));
        }
        if(containsCheck(AgentConstants.CONTROLLER_NAME,pointRow)){
            pointRow.put("device",pointRow.get(AgentConstants.CONTROLLER_NAME));
        }
        if(containsCheck(AgentConstants.NAME,pointRow)){
            pointRow.put("instanceName",pointRow.get(AgentConstants.NAME));
        }
    }

    private List<Map<String,Object>> getPointData(Criteria criteriaList) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getPointModule().getTableName())
                .select(FieldFactory.getPointFields())
                .andCriteria(criteriaList);
        return builder.get();
    }


}
