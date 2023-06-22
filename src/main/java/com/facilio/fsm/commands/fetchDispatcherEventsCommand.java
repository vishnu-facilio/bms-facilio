package com.facilio.fsm.commands;

import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.fsm.context.DispatcherEventContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.TimeOffContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class fetchDispatcherEventsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<Long,List<DispatcherEventContext>> events = new HashMap<>();
        List<Long> peopleIds = (List<Long>) context.get(FacilioConstants.ContextNames.PEOPLE_IDS);
        if(CollectionUtils.isNotEmpty(peopleIds)) {
            Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
            Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
            String timeOffModuleName = FacilioConstants.TimeOff.TIME_OFF;
            String workOrderModuleName = FacilioConstants.ContextNames.WORK_ORDER;
            ModuleBean moduleBean = Constants.getModBean();

            FacilioModule timeOff = moduleBean.getModule(timeOffModuleName);
            List<FacilioField> timeOffFields = moduleBean.getAllFields(timeOffModuleName);
            Map<String, FacilioField> timeOffFieldMap = FieldFactory.getAsMap(timeOffFields);

            EnumField typeEnumField = (EnumField) timeOffFieldMap.get(FacilioConstants.ContextNames.TYPE);
            Map<Integer, Object> timeOffEnum = typeEnumField.getEnumMap();

            List<TimeOffContext> timeOffData = new ArrayList<>();
            Map<Long,List<TimeOffContext>> timeOffMap= new HashMap<>();

            FacilioModule wo = moduleBean.getModule(workOrderModuleName);
            List<FacilioField> woFields = moduleBean.getAllFields(workOrderModuleName);
            Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);

            List<V3WorkOrderContext> woData = new ArrayList<>();
            Map<Long,List<V3WorkOrderContext>> woMap= new HashMap<>();

            List<FacilioField> woSelectFields = new ArrayList<>();
            woSelectFields.add(woFieldMap.get("estimatedStart"));
            woSelectFields.add(woFieldMap.get("estimatedEnd"));
            woSelectFields.add(woFieldMap.get("subject"));
            woSelectFields.add(woFieldMap.get("priority"));
            woSelectFields.add(FieldFactory.getIdField(wo));

            SelectRecordsBuilder<TimeOffContext> timeOffBuilder = new SelectRecordsBuilder<TimeOffContext>()
                    .select(timeOffFields)
                    .module(timeOff)
                    .andCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.PEOPLE), StringUtils.join(peopleIds, ","), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.START_TIME), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
                    .andCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.END_TIME), String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL));
            timeOffData = timeOffBuilder.get();
            if(CollectionUtils.isNotEmpty(timeOffData)){
                timeOffMap = timeOffData.stream().collect(Collectors.groupingBy(data -> data.getPeople().getId()));
            }

            List<User> users = PeopleAPI.getUsersForPeopleIds(peopleIds);

            if (CollectionUtils.isNotEmpty(users)) {

                List<Long> ouids = users.stream().map(User::getOuid).collect(Collectors.toList());
                Map<Long, List<Long>> ppluserIdmap = users.stream().collect(Collectors.groupingBy(User::getPeopleId, Collectors.mapping(User::getOuid, Collectors.toList())));

                SelectRecordsBuilder<V3WorkOrderContext> woBuilder = new SelectRecordsBuilder<V3WorkOrderContext>()
                        .select(woSelectFields)
                        .module(wo)
                        .andCondition(CriteriaAPI.getCondition(woFieldMap.get("estimatedStart"), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
                        .andCondition(CriteriaAPI.getCondition(woFieldMap.get("estimatedEnd"), String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL))
                        .andCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.ASSIGNED_TO_ID), StringUtils.join(ouids, ","), NumberOperators.EQUALS));

                woData = woBuilder.get();
                if(CollectionUtils.isNotEmpty(woData)){
                    woMap = woData.stream().collect(Collectors.groupingBy(data -> data.getAssignedTo().getPeopleId()));
                }
            }

            for(Long peopleId : peopleIds){
                List<DispatcherEventContext> pplEvents = new ArrayList<>();
                List<TimeOffContext> pplTimeOff = timeOffMap.get(peopleId);
                if(CollectionUtils.isNotEmpty(pplTimeOff)) {
                    for(TimeOffContext timeOffEvent : pplTimeOff) {
                        DispatcherEventContext dispatcherTOEvent = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(timeOffEvent), DispatcherEventContext.class);
                        String enumVal = (String) timeOffEnum.get(dispatcherTOEvent.getType());
                        dispatcherTOEvent.setTypeEnum(enumVal);
                        dispatcherTOEvent.setEventType(DispatcherEventContext.EventType.PASSIVE.getIndex());
                        dispatcherTOEvent.setEventObj(timeOffEvent);
                        pplEvents.add(dispatcherTOEvent);
                    }
                }
                List<V3WorkOrderContext> pplWo = woMap.get(peopleId);
                if ((CollectionUtils.isNotEmpty(pplWo))){
                    for(V3WorkOrderContext woEvent : pplWo) {
                        DispatcherEventContext dispatcherWOEvent = new DispatcherEventContext();
                        dispatcherWOEvent.setEventObj(woEvent);
                        dispatcherWOEvent.setEventType(DispatcherEventContext.EventType.ACTIVE.getIndex());
                        dispatcherWOEvent.setAllowResize(true);
                        dispatcherWOEvent.setAllowReschedule(true);
                        dispatcherWOEvent.setStartTime(woEvent.getEstimatedStart());
                        dispatcherWOEvent.setEndTime(woEvent.getEstimatedEnd());
                        pplEvents.add(dispatcherWOEvent);
                    }
                }
                events.put(peopleId,pplEvents);
            }
        }
        context.put(FacilioConstants.Dispatcher.EVENTS, events);
        return false;
    }
}
