package com.facilio.fsm.commands.dispatchBoard;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.fsm.context.DispatcherResourceContext;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.context.shift.ShiftSlot;
import com.facilio.bmsconsoleV3.util.DispatcherUtil;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.twilio.rest.chat.v1.service.channel.Message;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.bmsconsoleV3.util.ShiftAPI.DAY_IN_MILLIS;
import static com.facilio.bmsconsoleV3.util.ShiftAPI.UNLIMITED_PERIOD;

public class FetchDispatcherResourcesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
        Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
        Long boardId = (Long) context.getOrDefault(FacilioConstants.Dispatcher.BOARD_ID,-1L);

        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioField> allPeopleFields = moduleBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allPeopleFields);
        List<FacilioField> selectFields = new ArrayList<>();
        List<String> defaultFieldNames = new ArrayList<>(Arrays.asList("name","status","currentLocation","avatar","checkedIn","lastCheckedOutTime","lastCheckedInTime"));
        for (String fieldName : defaultFieldNames){
            selectFields.add(fieldMap.get(fieldName));
        }
        List<SupplementRecord> supplementFields = new ArrayList<>();
        MultiLookupMeta territories = new MultiLookupMeta((MultiLookupField) fieldMap.get("territories"));
        supplementFields.add(territories);

        String sortBy = "name";
        String sortOrder = "desc";

        int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);
        int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
        int offset = ((page-1) * perPage);
        if (offset < 0) {
            offset = 0;
        }

        String orderBy = (String) context.get(FacilioConstants.ContextNames.ORDER_BY);
        String orderType = (String) context.get(FacilioConstants.ContextNames.ORDER_TYPE);

        FacilioModule module = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        FacilioField dispatchable = fieldMap.get("dispatchable");

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
                                        if(selectField.getDataTypeEnum().equals(FieldType.LOOKUP)){
                                            supplementFields.add((SupplementRecord)selectField);
                                        }
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
            selectFields = allPeopleFields;
        }
        if(orderBy != null && orderType != null){
            sortBy = moduleBean.getField(orderBy,FacilioConstants.ContextNames.PEOPLE).getCompleteColumnName()+ "," + FieldFactory.getIdField(module).getCompleteColumnName() + " " + orderType;
        } else if (sortBy != null && !sortBy.isEmpty()) {
            sortBy = moduleBean.getField(sortBy,FacilioConstants.ContextNames.PEOPLE).getCompleteColumnName()+ "," + FieldFactory.getIdField(module).getCompleteColumnName() + " " + sortOrder;
        }
        else {
            sortBy = FieldFactory.getIdField(module).getCompleteColumnName()+" " + sortOrder;
        }

        Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);

        Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);

        List<V3PeopleContext> people = V3PeopleAPI.getResourcesList(module,selectFields,supplementFields,serverCriteria,sortBy,filterCriteria,searchCriteria,perPage,offset);

        if(CollectionUtils.isNotEmpty(people)){
            for(V3PeopleContext resource : people){
                DispatcherResourceContext dispatcherResource = new DispatcherResourceContext();
                List<Map<String, Object>> shiftSchedule = ShiftAPI.getShiftListDecoratedWithWeeklyOff(resource.getId(), startTime, endTime);
//                List<ShiftSlot> slots = ShiftAPI.fetchShiftSlotsForDispatcher(Collections.singletonList(resource.getId()), startTime, endTime);
//
//                ShiftSlot defaultShiftSlot = slots.get(0);
//                List<Map<String, Object>> shiftSchedule = new ArrayList<>();
//                Map<Long, Map<String, Object>> shiftScheduleMap = new HashMap<>();
//                for (long time = startTime; time <= endTime; time += DAY_IN_MILLIS) {
//                    if( defaultShiftSlot != null && defaultShiftSlot.getShift() != null ) {
//                        Shift defaultShift = defaultShiftSlot.getShift();
//                        Map<String, Object> shift = new HashMap<>();
//                        shift.put("name", defaultShift.getName());
//                        shift.put("shiftId", defaultShift.getId());
//                        shift.put("colorCode", defaultShift.getColorCode());
//                        boolean isWeeklyOff = defaultShift.isWeeklyOff(time);
//                        if (isWeeklyOff) {
//                            shift.put("startTime", DateTimeUtil.getDayStartTimeOf(time));
//                            shift.put("endTime", DateTimeUtil.getDayEndTimeOf(time));
//                        } else {
//                            Long shiftStartTime = defaultShift.getStartTime();
//                            Long shiftEndTime = defaultShift.getEndTime();
//
//                            if(shiftEndTime != null && shiftStartTime != null) {
//                                if(shiftStartTime > shiftEndTime){
//                                    shiftEndTime += 86400000;
//                                }
//                                shift.put("startTime", shiftStartTime + time);
//                                shift.put("endTime", shiftEndTime + time);
//                            }
//                        }
//                        shift.put("isWeeklyOff", isWeeklyOff);
//                        shiftSchedule.add(shift);
//                        shiftScheduleMap.put(time, shift);
//                    }
//                }
//                for (ShiftSlot slot : slots) {
//                    if(slot != null){
//                        long modificationStart = slot.getFrom();
//                        long modificationEnd = slot.getTo();
//                        if (modificationStart == UNLIMITED_PERIOD || modificationEnd == UNLIMITED_PERIOD) {
//                            continue;
//                        }
//                        Shift shift = slot.getShift();
//
//                        for (long time = modificationStart; time <= modificationEnd; time += DAY_IN_MILLIS) {
//                            Map<String, Object> shiftObj = shiftScheduleMap.get(time);
//                            if (shiftObj == null) {
//                                continue;
//                            }
//                            shiftObj.put("name", shift.getName());
//                            shiftObj.put("shiftId", shift.getId());
//                            shiftObj.put("colorCode", shift.getColorCode());
//                            boolean isWeeklyOff = shift.isWeeklyOff(time);
//                            if (isWeeklyOff) {
//                                shiftObj.put("startTime", DateTimeUtil.getDayStartTimeOf(time));
//                                shiftObj.put("endTime", DateTimeUtil.getDayEndTimeOf(time));
//                            } else {
//                                Long shiftStartTime = shift.getStartTime();
//                                Long shiftEndTime = shift.getEndTime();
//
//                                if(shiftEndTime != null && shiftStartTime != null) {
//                                    if(shiftStartTime > shiftEndTime){
//                                        shiftEndTime += 86400000;
//                                    }
//                                    shiftObj.put("startTime", shiftStartTime + time);
//                                    shiftObj.put("endTime", shiftEndTime + time);
//                                }
//                            }
//                            shiftObj.put("isWeeklyOff", isWeeklyOff);
//                        }
//                    }
//                }
                dispatcherResource.setPeople(resource);
                dispatcherResource.setShifts(shiftSchedule);
                resources.add(dispatcherResource);
            }
        }
        JSONObject result = new JSONObject();
        result.put(FacilioConstants.Dispatcher.RESOURCES,resources);
        result.put(FacilioConstants.ContextNames.COUNT, V3PeopleAPI.getResourcesCount(module,serverCriteria,sortBy,filterCriteria,searchCriteria));
        context.put(FacilioConstants.Dispatcher.RESOURCES,result);
        return false;
    }
}
