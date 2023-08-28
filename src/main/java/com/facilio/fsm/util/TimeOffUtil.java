package com.facilio.fsm.util;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.TimeOffContext;
import com.facilio.fsm.context.TimeOffTypeContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TimeOffUtil {
    public static Map<Long,String> getTimeOffColorMap() throws Exception {
        Map<Long,String> typeMap = new HashMap<>();
        SelectRecordsBuilder<TimeOffTypeContext> builder = new SelectRecordsBuilder<TimeOffTypeContext>()
                .select(Constants.getModBean().getAllFields(FacilioConstants.TimeOff.TIME_OFF_TYPE))
                .module(Constants.getModBean().getModule(FacilioConstants.TimeOff.TIME_OFF_TYPE))
                .beanClass(TimeOffTypeContext.class);
        List<TimeOffTypeContext> timeOffType = builder.get();
        if(CollectionUtils.isNotEmpty(timeOffType)){
            for(TimeOffTypeContext type : timeOffType){
                typeMap.put(type.getId(),type.getColor());
            }
        }
       return typeMap;
    }

    public static List<TimeOffContext> getTimeOffForPeople(long startTime, long endTime, long peopleId) throws Exception {
        String timeOffModuleName = FacilioConstants.TimeOff.TIME_OFF;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule timeOff = moduleBean.getModule(timeOffModuleName);
        List<FacilioField> timeOffFields = moduleBean.getAllFields(timeOffModuleName);
        Map<String, FacilioField> timeOffFieldMap = FieldFactory.getAsMap(timeOffFields);

        SelectRecordsBuilder<TimeOffContext> timeOffBuilder = new SelectRecordsBuilder<TimeOffContext>()
                .select(timeOffFields)
                .module(timeOff)
                .beanClass(TimeOffContext.class)
                .andCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.PEOPLE), String.valueOf(peopleId), NumberOperators.EQUALS))
                .fetchSupplement((LookupField) timeOffFieldMap.get("type"));

        Criteria timeCrit = new Criteria();
        timeCrit.addAndCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.START_TIME), startTime+","+endTime, DateOperators.BETWEEN));
        timeCrit.addOrCondition(CriteriaAPI.getCondition(timeOffFieldMap.get(FacilioConstants.ContextNames.END_TIME), startTime+","+endTime, DateOperators.BETWEEN));

        timeOffBuilder.andCriteria(timeCrit);

        return timeOffBuilder.get();
    }
}
