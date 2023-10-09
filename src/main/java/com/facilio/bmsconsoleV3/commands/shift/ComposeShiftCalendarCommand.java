package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComposeShiftCalendarCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long rangeFrom = (Long) context.get(FacilioConstants.Shift.RANGE_FROM);
        Long rangeTo = (Long) context.get(FacilioConstants.Shift.RANGE_TO);

        List<V3PeopleContext> peopleList = (List<V3PeopleContext>)
                context.get(FacilioConstants.ContextNames.PEOPLE);
        List<Map<String, Object>> peopleWithWhiteListedFields = stripPeopleFields(peopleList);

        Map<Long, List<Map<String, Object>>> peopleShiftMapping = new HashMap<>();

        for (V3PeopleContext people: peopleList) {
            List<Map<String, Object>> shifts = ShiftAPI.getCompactedShiftList(people.getId(), rangeFrom, rangeTo);
            peopleShiftMapping.put(people.getId(), shifts);
        }

        context.put(FacilioConstants.ContextNames.SHIFTS, peopleShiftMapping);
        context.put(FacilioConstants.ContextNames.PEOPLE, peopleWithWhiteListedFields);

        return false;
    }

    private List<Map<String, Object>> stripPeopleFields(List<V3PeopleContext> peopleList) {
        List<Map<String, Object>> strippedPeople = new ArrayList<>();
        for (V3PeopleContext people: peopleList) {
            HashMap<String, Object> strippedE = new HashMap<>();
            strippedE.put("id", people.getId());
            strippedE.put("name", people.getName());
            strippedPeople.add(strippedE);
        }
        return strippedPeople;
    }
}