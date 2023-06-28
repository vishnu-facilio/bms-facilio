package com.facilio.bmsconsole.commands;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.sql.SQLException;
import java.util.*;

public class SetReadingInputValuesAndLabelsCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(SetReadingInputValuesAndLabelsCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        try {
            Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
            List<Map<String,Object>> points = (List<Map<String, Object>>) context.get(AgentConstants.POINTS);
            List<String> multiStatePointNames = getMultiStatePointNames(points);
            List<Map<String, Object>> multiStatePoints = getMultiStatePoints(points);
            List<Map<String, Object>> multiStatePointsFromDb = PointsAPI.getPointsFromDb(multiStatePointNames, controller);
            List<Map<String, Object>> values = getValues(multiStatePointsFromDb, multiStatePoints);
            addInputValueAndInputLabel(values);
        } catch (Exception e){
            LOGGER.error("Exception in SetReadingInputValuesAndLabelsCommand", e);
        }

        return false;
    }

    private List<Map<String, Object>> getValues(List<Map<String, Object>> pointsFromDb, List<Map<String, Object>> points) {
        List<Map<String, Object>> values = new ArrayList<>();
        Map<String,Long> nameVsId = createNameVsPointIdMap(pointsFromDb);
        for (Map<String, Object> point : points) {
            HashMap<Object, Object> enumValue = (HashMap<Object, Object>) point.get(AgentConstants.STATES);
            String name = point.get(AgentConstants.NAME).toString();
            Long id = nameVsId.get(name);
            for (Object key : enumValue.keySet()){
                Map<String, Object> value = new HashMap<>();
                value.put(AgentConstants.POINT_ID, id);
                String inputValue = key.toString();
                String inputLabel = enumValue.get(key).toString();
                value.put(AgentConstants.INPUT_VALUE, inputValue);
                value.put(AgentConstants.INPUT_LABEL, inputLabel);
                values.add(value);
            }
        }
        return values;
    }

    private Map<String, Long> createNameVsPointIdMap(List<Map<String, Object>> pointsFromDb) {
        Map<String, Long> nameVsIdMap = new HashMap<>();
        for (Map<String, Object> point : pointsFromDb) {
            if(point.containsKey(AgentConstants.NAME) && point.containsKey(AgentConstants.ID)){
                String name = String.valueOf(point.get(AgentConstants.NAME));
                Long id = (Long) point.get(AgentConstants.ID);
                nameVsIdMap.put(name, id);
            }
        }
        return nameVsIdMap;
    }

    private void addInputValueAndInputLabel(List<Map<String, Object>> values) throws SQLException {
        if(!values.isEmpty()){
            List<FacilioField> fields = FieldFactory.getReadingInputValuesFields();
            FacilioModule module = ModuleFactory.getReadingInputValuesModule();
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .fields(fields)
                    .table(module.getTableName())
                    .addRecords(values);
            insertBuilder.save();
        }
    }

    private List<String> getMultiStatePointNames(List<Map<String, Object>> points) {
        List<String> pointNames = new ArrayList<>();
        for (Map<String, Object> point : points) {
            if(point.containsKey(AgentConstants.STATES) && point.get(AgentConstants.STATES) != null){
                pointNames.add(point.get(AgentConstants.NAME).toString());
            }
        }
        return pointNames;
    }

    private List<Map<String, Object>> getMultiStatePoints(List<Map<String, Object>> points) {
        List<Map<String, Object>> multiStatePoints = new ArrayList<>();
        for (Map<String, Object> point : points) {
            if(point.containsKey(AgentConstants.STATES) && point.get(AgentConstants.STATES) != null){
                multiStatePoints.add(point);
            }
        }
        return multiStatePoints;
    }

}