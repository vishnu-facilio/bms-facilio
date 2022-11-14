package com.facilio.bmsconsoleV3.context.spacebooking;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.GetBusinessHourCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class V3ValidateSpaceBookingAvailability extends FacilioCommand {

    private BusinessHoursContext businessHour;

    @Override
    public boolean executeCommand(Context context) throws Exception {


    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    Long startTime = (Long) context.get(FacilioConstants.ContextNames.SpaceBooking.BOOKING_START_TIME);
    Long endTime = (Long) context.get(FacilioConstants.ContextNames.SpaceBooking.BOOKING_END_TIME);
    Long buildingId = (Long) context.get(FacilioConstants.ContextNames.SpaceBooking.BUILDING_ID);
    Long businessHourId = null;

    FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
    List<FacilioField> fields = modBean.getAllFields(resourceModule.getName());
    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> selectFields = Arrays.asList(fieldMap.get(FacilioConstants.ContextNames.SpaceBooking.OPERATING_HOUR));


    GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
            .table(resourceModule.getTableName())
            .select(selectFields)
            .andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.SpaceBooking.SPACE_ID), String.valueOf(buildingId), NumberOperators.EQUALS));


    List<Map<String, Object>> props = selectBuilder.get();
    if (props != null && props.size() > 0) {
        for (Map<String, Object> prop : props) {
            businessHourId = (Long) prop.get(FacilioConstants.ContextNames.SpaceBooking.OPERATING_HOUR);
        }

        context.put(FacilioConstants.ContextNames.ID,businessHourId);

        FacilioChain getBusinessHoursChain = ReadOnlyChainFactory.getBusinessHoursChain();
        getBusinessHoursChain.execute(context);
        businessHour = (BusinessHoursContext) context.get(FacilioConstants.ContextNames.BUSINESS_HOUR);


        HashMap<String, Object> startTimeData = DateTimeUtil.getTimeData(startTime);
        int startDay = (int) startTimeData.get("day");
        int startHour = (int) startTimeData.get("hour");

        HashMap<String, Object> endTimeData = DateTimeUtil.getTimeData(endTime);
        int endDay = (int) endTimeData.get("day");
        int endHour = (int) endTimeData.get("hour");

        int flag = 0;
        for (BusinessHourContext eachDayVisit : businessHour.getSingleDaybusinessHoursList()) {
            if (startDay == eachDayVisit.getDayOfWeek()) {

                int stTime = eachDayVisit.getStartTimeAsLocalTime().getHour();
                int enTime = eachDayVisit.getEndTimeAsLocalTime().getHour();
                if (startHour >= stTime && endHour <= enTime) {
                    flag = 1;
                    //Checked only business hour range excluding same day and minutes check
                }

                break;
            }
        }
        if (flag <= 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please book in the business hour range");

        }

    }

        return false;
    }
}



