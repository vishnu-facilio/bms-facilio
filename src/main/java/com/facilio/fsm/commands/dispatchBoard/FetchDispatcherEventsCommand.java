package com.facilio.fsm.commands.dispatchBoard;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fsm.context.*;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class FetchDispatcherEventsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<Long,List<DispatcherEventContext>> events = new HashMap<>();
        List<Long> peopleIds = (List<Long>) context.get(FacilioConstants.ContextNames.PEOPLE_IDS);
        if(CollectionUtils.isNotEmpty(peopleIds)) {
            Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
            Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
            String timeOffModuleName = FacilioConstants.TimeOff.TIME_OFF;
            String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
            ModuleBean moduleBean = Constants.getModBean();

            FacilioModule timeOff = moduleBean.getModule(timeOffModuleName);
            List<FacilioField> timeOffFields = moduleBean.getAllFields(timeOffModuleName);
            Map<String, FacilioField> timeOffFieldMap = FieldFactory.getAsMap(timeOffFields);

            List<TimeOffContext> timeOffData = new ArrayList<>();
            Map<Long,List<TimeOffContext>> timeOffMap= new HashMap<>();

            FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
            List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);
            Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

            List<ServiceAppointmentContext> saData = new ArrayList<>();
            Map<Long,List<ServiceAppointmentContext>> saMap= new HashMap<>();

            List<FacilioField> saSelectFields = new ArrayList<>();
            saSelectFields.add(saFieldMap.get("scheduledStartTime"));
            saSelectFields.add(saFieldMap.get("scheduledEndTime"));
            saSelectFields.add(saFieldMap.get("name"));
            saSelectFields.add(saFieldMap.get("fieldAgent"));
            saSelectFields.add(saFieldMap.get("priority"));
            saSelectFields.add(saFieldMap.get("resolutionDueStatus"));
            saSelectFields.add(saFieldMap.get("site"));
            saSelectFields.add(saFieldMap.get("status"));
            saSelectFields.add(saFieldMap.get("mismatch"));
            saSelectFields.add(FieldFactory.getIdField(serviceAppointment));



            SelectRecordsBuilder<TimeOffContext> timeOffBuilder = new SelectRecordsBuilder<TimeOffContext>()
                    .select(timeOffFields)
                    .module(timeOff)
                    .beanClass(TimeOffContext.class)
                    .andCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.PEOPLE), StringUtils.join(peopleIds, ","), NumberOperators.EQUALS))
                    .fetchSupplement((LookupField) timeOffFieldMap.get("type"));
            Criteria timeCriteria = new Criteria();

            Criteria eventsStartEndWithinRange = new Criteria();
            eventsStartEndWithinRange.addAndCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.START_TIME), startTime+","+endTime, DateOperators.BETWEEN));
            eventsStartEndWithinRange.addOrCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.END_TIME), startTime+","+endTime, DateOperators.BETWEEN));
            timeCriteria.andCriteria(eventsStartEndWithinRange);

            Criteria eventsStartEndBeyondTimeRange = new Criteria();
            eventsStartEndBeyondTimeRange.addAndCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.START_TIME), Collections.singleton(startTime), DateOperators.IS_BEFORE));
            eventsStartEndBeyondTimeRange.addAndCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.END_TIME), Collections.singleton(endTime), DateOperators.IS_AFTER));
            timeCriteria.orCriteria(eventsStartEndBeyondTimeRange);

            timeOffBuilder.andCriteria(timeCriteria);
            timeOffData = timeOffBuilder.get();
            if(CollectionUtils.isNotEmpty(timeOffData)){
                timeOffMap = timeOffData.stream().collect(Collectors.groupingBy(data -> data.getPeople().getId()));
            }

            List<SupplementRecord> saSupplements = new ArrayList<>();
            saSupplements.add((LookupField) saFieldMap.get("site"));
            saSupplements.add((LookupField) saFieldMap.get("status"));

            SelectRecordsBuilder<ServiceAppointmentContext> serviceAppointmentBuilder = new SelectRecordsBuilder<ServiceAppointmentContext>()
                    .select(saSelectFields)
                    .module(serviceAppointment)
                    .beanClass(ServiceAppointmentContext.class)
                    .andCondition(CriteriaAPI.getCondition(saFieldMap.get("fieldAgent"), StringUtils.join(peopleIds, ","), NumberOperators.EQUALS))
                    .fetchSupplements(saSupplements);
            Criteria saTimeCriteria = new Criteria();

            Criteria saEventsStartEndWithinRange = new Criteria();
            saEventsStartEndWithinRange.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), startTime+","+endTime, DateOperators.BETWEEN));
            saEventsStartEndWithinRange.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), startTime+","+endTime, DateOperators.BETWEEN));
            saTimeCriteria.andCriteria(saEventsStartEndWithinRange);

            Criteria saEventsStartEndBeyondTimeRange = new Criteria();
            saEventsStartEndBeyondTimeRange.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), Collections.singleton(startTime), DateOperators.IS_BEFORE));
            saEventsStartEndBeyondTimeRange.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), Collections.singleton(endTime), DateOperators.IS_AFTER));
            saTimeCriteria.orCriteria(saEventsStartEndBeyondTimeRange);

            serviceAppointmentBuilder.andCriteria(saTimeCriteria);
            saData = serviceAppointmentBuilder.get();
            if(CollectionUtils.isNotEmpty(saData)){
                saMap = saData.stream().collect(Collectors.groupingBy(data -> data.getFieldAgent().getId()));
            }

            for(Long peopleId : peopleIds){
                List<DispatcherEventContext> pplEvents = new ArrayList<>();
                List<TimeOffContext> pplTimeOff = timeOffMap.get(peopleId);
                if(CollectionUtils.isNotEmpty(pplTimeOff)) {
                    for(TimeOffContext timeOffEvent : pplTimeOff) {
                        DispatcherEventContext dispatcherTOEvent = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(timeOffEvent), DispatcherEventContext.class);
                        dispatcherTOEvent.setEventType(DispatcherEventContext.EventType.TIME_OFF.getIndex());
                        dispatcherTOEvent.setTimeOff(timeOffEvent);
                        TimeOffTypeContext timeOffType = timeOffEvent.getType();
                        dispatcherTOEvent.setBackgroundColor(timeOffType.getColor());
                        pplEvents.add(dispatcherTOEvent);
                    }
                }
                List<ServiceAppointmentContext> pplSA = saMap.get(peopleId);
                if ((CollectionUtils.isNotEmpty(pplSA))){
                    for(ServiceAppointmentContext saEvent : pplSA) {
                        DispatcherEventContext dispatcherSAEvent = new DispatcherEventContext();
                        dispatcherSAEvent.setServiceAppointmentContext(saEvent);
                        dispatcherSAEvent.setEventType(DispatcherEventContext.EventType.SERVICE_APPOINTMENT.getIndex());
                        dispatcherSAEvent.setAllowResize(true);
                        dispatcherSAEvent.setAllowReschedule(true);
                        dispatcherSAEvent.setStartTime(saEvent.getScheduledStartTime());
                        dispatcherSAEvent.setEndTime(saEvent.getScheduledEndTime());
                        ServiceAppointmentTicketStatusContext saStatus = saEvent.getStatus();
                        dispatcherSAEvent.setBackgroundColor(saStatus.getBackgroundColor());
                        dispatcherSAEvent.setTextColor(saStatus.getTextColor());
                        pplEvents.add(dispatcherSAEvent);
                    }
                }
                events.put(peopleId,pplEvents);
            }
        }
        JSONObject result = new JSONObject();
        result.put(FacilioConstants.Dispatcher.EVENTS,events);
        context.put(FacilioConstants.Dispatcher.EVENTS, result);
        return false;
    }
}
