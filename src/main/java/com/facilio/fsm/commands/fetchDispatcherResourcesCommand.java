package com.facilio.fsm.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.fsm.context.DispatcherResourceContext;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.context.shift.ShiftSlot;
import com.facilio.bmsconsoleV3.util.DispatcherUtil;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.bmsconsoleV3.util.ShiftAPI.DAY_IN_MILLIS;
import static com.facilio.bmsconsoleV3.util.ShiftAPI.UNLIMITED_PERIOD;

public class fetchDispatcherResourcesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
        Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
        Long boardId = (Long) context.getOrDefault(FacilioConstants.Dispatcher.BOARD_ID,-1L);

        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioField> selectFields = new ArrayList<>();
        List<String> defaultFieldNames = new ArrayList<>(Arrays.asList("name","territory","status","currentLocation"));
        for (String fieldName : defaultFieldNames){
            FacilioField selectField = moduleBean.getField(fieldName,FacilioConstants.ContextNames.PEOPLE);
            selectFields.add(selectField);
        }

        String sortBy = "name";
        String sortOrder = "desc";

        int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);
        int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
        int offset = ((page-1) * perPage);
        if (offset < 0) {
            offset = 0;
        }

        FacilioModule module = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        FacilioField dispatchable = moduleBean.getField("dispatchable",FacilioConstants.ContextNames.PEOPLE);

        Criteria serverCriteria = new Criteria();
        serverCriteria.addAndCondition(CriteriaAPI.getCondition(dispatchable,String.valueOf(true) ,BooleanOperators.IS));

        if(boardId != null && boardId > 0){
            DispatcherSettingsContext board = DispatcherUtil.getDispatcher(boardId);
            if(board != null) {
                Criteria boardCriteria = board.getResourceCriteria();
                if(boardCriteria != null){
                    serverCriteria.andCriteria(boardCriteria);
                }
                String resourceConfig = board.getResourceConfigJson();
                JSONObject resourceConfigJson = FacilioUtil.parseJson(resourceConfig);
                if(resourceConfigJson != null){
                    if(resourceConfigJson.get("sortConfig") != null){
                        JSONObject sortConfig = (JSONObject) resourceConfigJson.get("sortConfig");
                        if(sortConfig.get("sortBy") != null){
                            sortBy = (String) sortConfig.get("sortBy");
                        }
                        if(sortConfig.get("sortOrder") != null){
                            sortOrder = (String) sortConfig.get("sortOrder");
                        }
                    }
                    if(resourceConfigJson.get("fields") != null){
                        List<JSONObject> fields = (List<JSONObject>) resourceConfigJson.get("fields");
                        if(CollectionUtils.isNotEmpty(fields)){
                            for(JSONObject field : fields){
                                Long fieldId = (Long) field.get("fieldId");
                                if(fieldId != null && fieldId > 0){
                                    FacilioField selectField = moduleBean.getField(fieldId);
                                    if(!defaultFieldNames.contains(selectField.getName())) {
                                        selectFields.add(selectField);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        List<DispatcherResourceContext> resources = new ArrayList<>();
        if(CollectionUtils.isEmpty(selectFields)){
            selectFields = moduleBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            sortBy = moduleBean.getField(sortBy,FacilioConstants.ContextNames.PEOPLE).getCompleteColumnName()+ "," + FieldFactory.getIdField(module).getCompleteColumnName() + " " + sortOrder;
        }
        else {
            sortBy = FieldFactory.getIdField(module).getCompleteColumnName()+" " + sortOrder;
        }

        SelectRecordsBuilder<V3PeopleContext> selectRecordsBuilder = new SelectRecordsBuilder<V3PeopleContext>()
                .select(selectFields)
                .beanClass(V3PeopleContext.class)
                .andCriteria(serverCriteria)
                .orderBy(sortBy)
                .limit(perPage)
                .offset(page);

        Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
        if (filterCriteria != null) {
            selectRecordsBuilder.andCriteria(filterCriteria);
        }

        Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
        if (searchCriteria != null) {
            selectRecordsBuilder.andCriteria(searchCriteria);
        }

        List<V3PeopleContext> people = selectRecordsBuilder.get();

        if(CollectionUtils.isNotEmpty(people)){
            for(V3PeopleContext resource : people){
                DispatcherResourceContext dispatcherResource = new DispatcherResourceContext();
                List<ShiftSlot> slots = ShiftAPI.fetchShiftSlots(Collections.singletonList(resource.getId()), startTime, endTime);

                ShiftSlot defaultShiftSlot = slots.get(0);
                List<Map<String, Object>> shiftSchedule = new ArrayList<>();
                Map<Long, Map<String, Object>> shiftScheduleMap = new HashMap<>();
                for (long time = startTime; time <= endTime; time += DAY_IN_MILLIS) {
                    Map<String, Object> shift = new HashMap<>();
                    shift.put("name", defaultShiftSlot.getShift().getName());
                    shift.put("shiftId", defaultShiftSlot.getShift().getId());
                    shift.put("colorCode", defaultShiftSlot.getShift().getColorCode());
                    boolean isWeeklyOff = defaultShiftSlot.getShift().isWeeklyOff(time);
                    if(isWeeklyOff){
                        shift.put("startTime", DateTimeUtil.getDayStartTimeOf(time));
                        shift.put("endTime",DateTimeUtil.getDayEndTimeOf(time));
                    } else {
                        shift.put("startTime", time + (defaultShiftSlot.getShift().getStartTime()*1000));
                        shift.put("endTime", time + (defaultShiftSlot.getShift().getEndTime())*1000);
                    }
                    shift.put("isWeeklyOff", isWeeklyOff);
                    shiftSchedule.add(shift);
                    shiftScheduleMap.put(time, shift);
                }
                for (ShiftSlot slot : slots) {

                    long modificationStart = slot.getFrom();
                    long modificationEnd = slot.getTo();
                    if (modificationStart == UNLIMITED_PERIOD || modificationEnd == UNLIMITED_PERIOD) {
                        continue;
                    }
                    Shift shift = slot.getShift();

                    for (long time = modificationStart; time <= modificationEnd; time += DAY_IN_MILLIS) {
                        Map<String, Object> shiftObj = shiftScheduleMap.get(time);
                        if (shiftObj == null) {
                            continue;
                        }
                        shiftObj.put("name", shift.getName());
                        shiftObj.put("shiftId", shift.getId());
                        shiftObj.put("colorCode", shift.getColorCode());
                        boolean isWeeklyOff = shift.isWeeklyOff(time);
                        if(isWeeklyOff){
                            shiftObj.put("startTime", DateTimeUtil.getDayStartTimeOf(time));
                            shiftObj.put("endTime",DateTimeUtil.getDayEndTimeOf(time));
                        } else {
                            shiftObj.put("startTime", (shift.getStartTime()*1000) + time);
                            shiftObj.put("endTime", (shift.getEndTime()*1000) + time);
                        }
                        shiftObj.put("isWeeklyOff", isWeeklyOff);
                    }
                }
                dispatcherResource.setPeople(resource);
                dispatcherResource.setShifts(shiftSchedule);
                resources.add(dispatcherResource);
            }
        }
        context.put(FacilioConstants.Dispatcher.RESOURCES,resources);
        return false;
    }
}
