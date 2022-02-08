package com.facilio.weekends;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.ZonedDateTime;
import java.util.Map;

public class WeekendUtil {

    public static WeekendContext getWeekend(long id) throws Exception {
        FacilioModule weekendModule = ModuleFactory.getWeekendsModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(weekendModule.getTableName())
                .select(FieldFactory.getWeekendsFields(weekendModule))
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(id), NumberOperators.EQUALS));
        Map<String, Object> weekendProps = builder.fetchFirst();
        return (weekendProps != null) ? FieldUtil.getAsBeanFromMap(weekendProps, WeekendContext.class) : null;
    }

    public static boolean isWeekendDate(ZonedDateTime dateTime, WeekendContext weekend) {
        int dayOfWeek = dateTime.getDayOfWeek().getValue();
        int weekOfMonth = ((dateTime.getDayOfMonth() / 7) + 1);
        JSONObject weekendJson = weekend.getValueJSON();
        JSONArray weekends = (weekendJson.containsKey("All")) ? (JSONArray) weekendJson.get("All") :
                                ((weekendJson.containsKey(String.valueOf(weekOfMonth))) ? (JSONArray) weekendJson.get(String.valueOf(weekOfMonth)) : null);
        if(weekends != null && weekends.contains(Long.valueOf(dayOfWeek)))
        {
            return true;
        }
        return false;
    }
}
