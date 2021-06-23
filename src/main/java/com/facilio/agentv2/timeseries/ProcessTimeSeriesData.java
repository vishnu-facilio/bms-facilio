package com.facilio.agentv2.timeseries;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessTimeSeriesData extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(ProcessTimeSeriesData.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {

        JSONObject payload = (JSONObject) context.get(AgentConstants.DATA);
        Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
        List<Point> points = PointsAPI.getAllPoints(null, controller.getId(),null);
        Map<String,Point> pointsMap = new HashMap<>();
        points.forEach(point -> pointsMap.put(point.getName(),point));
        if(payload.containsKey(AgentConstants.DATA)){
            JSONArray pointsValue = (JSONArray) payload.get(AgentConstants.DATA);
            for (Object p : pointsValue) {
                JSONObject pointJson = (JSONObject) p;
                if( (pointJson.containsKey(AgentConstants.NAME) && (pointJson.get(AgentConstants.NAME) != null) ) && (pointJson.containsKey(AgentConstants.VALUE) )){
                    String pointName = (String) pointJson.get(AgentConstants.NAME);
                    Object value = pointJson.get(AgentConstants.VALUE);
                    //Point point = PointsAPI.getPoint(pointName , controller.getAgentId(), controller.getId(), FacilioControllerType.valueOf(controller.getControllerType()) );
                    if(pointsMap.containsKey(pointName)){
                        Point point = pointsMap.get(pointName);
                         if((point.getFieldId() != null) && (point.getCategoryId() != null) ){
                             // rocessUnaddedadd pointJson to module data
                         }else {
                             // unmoduled data
                             addToUnmodeledData(pointJson, point);
                         }
                    }else {
                        // make new point entry
                        MiscPoint newMiscPoint = new MiscPoint(controller.getAgentId(),controller.getId());
                        newMiscPoint.setName(pointName);
                        if (PointsAPI.addPoint(newMiscPoint)) {
                            pointsMap.put(newMiscPoint.getName(),newMiscPoint);
                            addToUnmodeledData(pointJson,newMiscPoint);
                        }
                    }

            }else {
                    LOGGER.info("Exception occurred missing name and value key in pointJSON ->"+pointJson);
                }

            }
        }else {
            throw new Exception(" Data missing from payload -> "+payload);
        }

        return false;
    }

    private void addToUnmodeledData(JSONObject pointJson, Point point) throws Exception {
        Map<String, FacilioField> unmoduledDataFieldMap = FieldFactory.getAsMap(FieldFactory.getUnmodeledDataFields());
        Map<String,Object> toInsert = new HashMap<>();
        toInsert.put(unmoduledDataFieldMap.get("instanceId").getName(),point.getId());
        toInsert.put(unmoduledDataFieldMap.get("ttime").getName(),System.currentTimeMillis());
        toInsert.put(unmoduledDataFieldMap.get("value").getName(),pointJson.get(AgentConstants.VALUE));
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(ModuleFactory.getUnmodeledDataModule().getTableName())
                .fields(FieldFactory.getUnmodeledDataFields());
        long unMdId = insertBuilder.insert(toInsert);
        LOGGER.info(" Unmodeled data id "+unMdId);
    }
}
