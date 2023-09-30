package com.facilio.fsm.commands.dispatchBoard;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.DispatcherUtil;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.DispatcherEventContext;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.FilterUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class FetchDispatcherMapDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
        Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
        Long boardId = (Long) context.getOrDefault(FacilioConstants.Dispatcher.BOARD_ID,-1L);
        JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        // fetching people list
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule peopleModule = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> allPeopleFields = moduleBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allPeopleFields);

        List<FacilioField> peopleSelectFields = new ArrayList<>();
        List<String> defaultFieldNames = new ArrayList<>(Arrays.asList("name","status","lastSyncTime","currentLocation","avatar"));
        for (String fieldName : defaultFieldNames){
            peopleSelectFields.add(fieldMap.get(fieldName));
        }

        List<SupplementRecord> peopleSupplementFields = new ArrayList<>();
        MultiLookupMeta territories = new MultiLookupMeta((MultiLookupField) fieldMap.get("territories"));
        peopleSupplementFields.add(territories);

        Criteria peopledefaultCriteria = new Criteria();
        peopledefaultCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("dispatchable"),String.valueOf(true) , BooleanOperators.IS));

        if(boardId != null && boardId > 0){
            DispatcherSettingsContext board = DispatcherUtil.getDispatcher(boardId);
            if(board != null) {
                Criteria boardCriteria = board.getResourceCriteria();
                if(boardCriteria != null){
                    peopledefaultCriteria.andCriteria(boardCriteria);
                }
            }
        }

        Criteria peopleFilterCriteria = null;
        if(filters != null && filters.get(FacilioConstants.ContextNames.PEOPLE) != null){
            peopleFilterCriteria = FilterUtil.getCriteriaFromFilters((JSONObject) filters.get(FacilioConstants.ContextNames.PEOPLE),FacilioConstants.ContextNames.PEOPLE, null);
        }

        Criteria peopleSearchCriteria = null;
        if(search != null){
            peopleSearchCriteria = new Criteria();
            peopleSearchCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("name"), search, StringOperators.CONTAINS));
        }

        SelectRecordsBuilder<V3PeopleContext> peopleBuilder = V3PeopleAPI.getPeopleSelectBuilder(peopleModule,peopleSelectFields,peopleSupplementFields,peopledefaultCriteria,null,peopleFilterCriteria,peopleSearchCriteria);
        List<V3PeopleContext> people = peopleBuilder.get();
        context.put(FacilioConstants.Dispatcher.RESOURCES,people);

        // fetching events list
        List<DispatcherEventContext> events = new ArrayList<>();

        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);
        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        List<FacilioField> saSelectFields = new ArrayList<>();
        saSelectFields.add(saFieldMap.get("name"));
        saSelectFields.add(saFieldMap.get("code"));

        List<LookupField> saSupplements = new ArrayList<>();
        saSupplements.add((LookupField) saFieldMap.get("site"));
        saSupplements.add((LookupField) saFieldMap.get("location"));
        saSupplements.add((LookupField) saFieldMap.get("status"));

        Criteria saTimeCriteria = null;

        if(Optional.ofNullable(startTime).orElse(0L) > 0 &&
                Optional.ofNullable(endTime).orElse(0L) > 0) {

            saTimeCriteria = new Criteria();

            Criteria saEventsStartEndWithinRange = new Criteria();
            saEventsStartEndWithinRange.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), startTime + "," + endTime, DateOperators.BETWEEN));
            saEventsStartEndWithinRange.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), startTime + "," + endTime, DateOperators.BETWEEN));
            saTimeCriteria.andCriteria(saEventsStartEndWithinRange);

            Criteria saEventsStartEndBeyondTimeRange = new Criteria();
            saEventsStartEndBeyondTimeRange.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), Collections.singleton(startTime), DateOperators.IS_BEFORE));
            saEventsStartEndBeyondTimeRange.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), Collections.singleton(endTime), DateOperators.IS_AFTER));
            saTimeCriteria.orCriteria(saEventsStartEndBeyondTimeRange);

        }

        Criteria saFilterCriteria = null;
        if(filters != null && filters.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT) != null){
            saFilterCriteria = FilterUtil.getCriteriaFromFilters((JSONObject) filters.get(serviceAppointmentModuleName),serviceAppointmentModuleName, null);
        }

        Criteria saSearchCriteria = null;
        if(search != null){
            saSearchCriteria = new Criteria();
            saSearchCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get("name"), search, StringOperators.CONTAINS));
            saSearchCriteria.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get("code"), search, StringOperators.CONTAINS));
        }

        SelectRecordsBuilder<ServiceAppointmentContext> appointmentBuilder = ServiceAppointmentUtil.getServiceAppointments(serviceAppointment,saSelectFields,saSupplements,saTimeCriteria,null,saFilterCriteria,saSearchCriteria);
        List<ServiceAppointmentContext> appointments = appointmentBuilder.get();
        if(CollectionUtils.isNotEmpty(appointments)){
            for(ServiceAppointmentContext saEvent : appointments) {
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
                dispatcherSAEvent.setBackgroundColorHover(saStatus.getBackgroundColorHover());
                dispatcherSAEvent.setTextColorHover(saStatus.getTextColorHover());
                events.add(dispatcherSAEvent);
            }
        }
        context.put(FacilioConstants.Dispatcher.EVENTS,events);
        return false;
    }
}
