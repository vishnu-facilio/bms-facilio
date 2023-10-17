package com.facilio.fsm.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.*;
import com.facilio.fsm.integrations.GoogleMapsAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.stream.Collectors;

public class TripUtil {

    public static TripStatusContext getTripStatus(String status) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<TripStatusContext> builder = new SelectRecordsBuilder<TripStatusContext>()
                .moduleName(FacilioConstants.Trip.TRIP_STATUS)
                .beanClass(TripStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.Trip.TRIP_STATUS))
                .andCondition(CriteriaAPI.getCondition("STATUS","status",status, StringOperators.IS));

        TripStatusContext tripStatus = builder.fetchFirst();
        return tripStatus;
    }
    public static void ConstructLocationHistoryImage(TripContext trip) throws Exception {
        if(trip != null) {
            FacilioModule tripLocationModule = Constants.getModBean().getModule(FacilioConstants.Trip.TRIP_LOCATION_HISTORY);
            List<FacilioField> tripLocationFields = Constants.getModBean().getAllFields(FacilioConstants.Trip.TRIP_LOCATION_HISTORY);
            Map<String,FacilioField> tripLocationFieldMap = FieldFactory.getAsMap(tripLocationFields);
            V3PeopleContext fieldAgent = trip.getPeople();
            SelectRecordsBuilder<TripLocationHistoryContext> locationsBuilder = new SelectRecordsBuilder<TripLocationHistoryContext>()
                    .select(tripLocationFields)
                    .module(tripLocationModule)
                    .beanClass(TripLocationHistoryContext.class)
                    .andCondition(CriteriaAPI.getCondition(tripLocationFieldMap.get("trip"),String.valueOf(trip.getId()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(tripLocationFieldMap.get("people"), String.valueOf(fieldAgent.getId()),NumberOperators.EQUALS));
            List<TripLocationHistoryContext> locations = locationsBuilder.get();
            if(CollectionUtils.isNotEmpty(locations)){
                List<String> coordinates = locations.stream().map((history -> {
                    if(history != null) {
                        try {
                            JSONObject latLng = (JSONObject) new JSONParser().parse(history.getLocation());
                            return latLng.get("lat")+","+latLng.get("lng");
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return null;
                })).filter(Objects::nonNull).collect(Collectors.toList());

                GoogleMapsAPI.TripDistance tripDistance = GoogleMapsAPI.calculateTripDistance(coordinates,-1, false);
                if(tripDistance != null) {
                    trip.setTripDistance((double) tripDistance.getDistance());
                    trip.setEstimatedDuration(tripDistance.getDuration());
                    long fileId = GoogleMapsAPI.generateTripMapPreview(tripDistance.getOrigin(), tripDistance.getDestination(), tripDistance.getEncodedPolyline(), null);
                    if (fileId > 0) {
                        trip.setJourneyId(fileId);
                    }
                }
            }
        }
    }
    public static List<TripContext> endTrip (Long tripId, LocationContext location) throws Exception {
        List<TripContext> OngoingTrips = new ArrayList<>();

        ModuleBean moduleBean = Constants.getModBean();

        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        FacilioModule serviceAppointment = moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);
        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

        FacilioModule people = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> peopleFields = moduleBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String, FacilioField> peopleFieldMap = FieldFactory.getAsMap(peopleFields);

        FacilioModule tripModule = moduleBean.getModule(FacilioConstants.Trip.TRIP);

        FacilioContext context = V3Util.getSummary(FacilioConstants.Trip.TRIP, Collections.singletonList(tripId));

        if(Constants.getRecordList(context) != null) {
            TripContext ongoingTrip = (TripContext) Constants.getRecordList(context).get(0);
            if (ongoingTrip != null) {
                if(ongoingTrip.getPeople() != null) {
                    long agentId = ongoingTrip.getPeople().getId();
                    V3PeopleContext agent = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,agentId,V3PeopleContext.class);
                    ServiceAppointmentTicketStatusContext InProgressStatus = ServiceAppointmentUtil.getStatus("inProgress");

                    Criteria openAppointmentCriteria = new Criteria();
                    openAppointmentCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get("fieldAgent"),String.valueOf(agentId),NumberOperators.EQUALS));
                    openAppointmentCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get("status"),String.valueOf(InProgressStatus.getId()),NumberOperators.EQUALS));

                    List<ServiceAppointmentContext> OpenAppointmentsForAgent = ServiceAppointmentUtil.getServiceAppointmentsList(serviceAppointment,saFields,Collections.singletonList((LookupField) saFieldMap.get("status")),openAppointmentCriteria,null,null,null,-1,-1);
                    if(org.apache.commons.collections.CollectionUtils.isNotEmpty(OpenAppointmentsForAgent)){
                        agent.setStatus(V3PeopleContext.Status.ON_SITE.getIndex());
                    } else if(agent.isCheckedIn() || ShiftAPI.checkIfPeopleAvailable(agentId,DateTimeUtil.getCurrenTime())){
                        agent.setStatus(V3PeopleContext.Status.AVAILABLE.getIndex());
                    } else {
                        agent.setStatus(V3PeopleContext.Status.NOT_AVAILABLE.getIndex());
                    }
                    V3RecordAPI.updateRecord(agent, people, Collections.singletonList(peopleFieldMap.get(FacilioConstants.ContextNames.STATUS)));

                    ServiceAppointmentContext existingAppointment = ongoingTrip.getServiceAppointment();
                    if(existingAppointment.getActualEndTime() != null){
                        ServiceAppointmentTicketStatusContext completedStatus = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.COMPLETED);
                        if(completedStatus == null){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing completed state");
                        }
                        existingAppointment.setStatus(completedStatus);
                    } else {
                        if(existingAppointment.getActualStartTime() != null){
                            ServiceAppointmentTicketStatusContext inProgressStatus = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.IN_PROGRESS);
                            if(inProgressStatus == null){
                                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing in-progress state");
                            }
                            existingAppointment.setStatus(inProgressStatus);
                        } else {
                            ServiceAppointmentTicketStatusContext dispatchStatus = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.DISPATCHED);
                            if(dispatchStatus == null){
                                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing dispatch state");
                            }
                            existingAppointment.setStatus(dispatchStatus);
                        }
                    }
                    V3RecordAPI.updateRecord(existingAppointment, serviceAppointment, Collections.singletonList(saFieldMap.get(FacilioConstants.ContextNames.STATUS)));

                    ongoingTrip.setEndTime(DateTimeUtil.getCurrenTime());
                    Long duration = ongoingTrip.getEndTime() - ongoingTrip.getStartTime();
                    ongoingTrip.setTripDuration(duration/1000);
                    ongoingTrip.setStatus(getTripStatus(FacilioConstants.Trip.COMPLETED));
                    if(location != null){
                        if (location != null && location.getLat() != -1 && location.getLng() != -1) {
                            if(location.getName() == null) {
                                location.setName(existingAppointment.getName() + "Trip_Location_" + DateTimeUtil.getCurrenTime());
                            }
                            Context locationContext = new FacilioContext();
                            Constants.setRecord(locationContext, location);
                            if (location.getId() > 0) {
                                locationContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, java.util.Collections.singletonList(location.getId()));
                                FacilioChain updateLocation = FacilioChainFactory.updateLocationChain();
                                updateLocation.execute(locationContext);
                            } else {
                                FacilioChain addLocation = FacilioChainFactory.addLocationChain();
                                addLocation.execute(locationContext);

                                long recordId = Constants.getRecordId(locationContext);
                                location.setId(recordId);
                                ongoingTrip.setEndLocation(location);
                            }
                        } else {
                            ongoingTrip.setEndLocation(null);
                        }
                    }
                    TripUtil.ConstructLocationHistoryImage(ongoingTrip);
                    V3RecordAPI.updateRecord(ongoingTrip,tripModule,Constants.getModBean().getAllFields(FacilioConstants.Trip.TRIP));
                    OngoingTrips.add(ongoingTrip);
                }
            }
        }
        return OngoingTrips;
    }
}
