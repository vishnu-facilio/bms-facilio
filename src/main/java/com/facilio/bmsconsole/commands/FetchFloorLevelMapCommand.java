package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchFloorLevelMapCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
            long buildingId = (long) context.get("buildingId");
            Map<Long,Integer> floorLevelMap = new HashMap<>();
            FacilioModule module = Constants.getModBean().getModule(FacilioConstants.ContextNames.FLOOR);
            List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.FLOOR);
            Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            List<FacilioField> selectableFields = new ArrayList<>();
            selectableFields.add(fieldMap.get("building"));
            selectableFields.add(fieldMap.get("floorlevel"));
            selectableFields.add(FieldFactory.getIdField(module));
            SelectRecordsBuilder<FloorContext> selectBuilder = new SelectRecordsBuilder<FloorContext>()
                    .module(module)
                    .select(selectableFields)
                    .beanClass(FloorContext.class)
                    .andCondition(CriteriaAPI.getCondition("BUILDING_ID", "building", String.valueOf(buildingId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("SPACE_TYPE", "spaceType", String.valueOf(BaseSpaceContext.SpaceType.FLOOR.getIntVal()), NumberOperators.EQUALS))
                    .orderBy("Floor.FLOOR_LEVEL is null,Floor.FLOOR_LEVEL ASC,Resources.NAME ASC");
            List<FloorContext> floors = selectBuilder.get();
            for(int i=0;i<floors.size();i++){
                FloorContext floor = floors.get(i);
                floorLevelMap.put(floor.getId(),i);
            }
            context.put(FacilioConstants.ContextNames.FLOOR_LIST,floorLevelMap);
        return false;
    }
}
