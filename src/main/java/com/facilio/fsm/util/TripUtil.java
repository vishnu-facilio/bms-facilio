package com.facilio.fsm.util;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.context.TripLocationHistoryContext;
import com.facilio.fsm.integrations.GoogleMapsAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TripUtil {
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
}
