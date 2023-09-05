package com.facilio.wmsv2.handler;

import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.ims.handler.ImsHandler;
import com.facilio.fms.message.Message;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Log4j
public class CalendarHandler extends ImsHandler {

    @Override
    public void processMessage(Message message) {
        try {
            Long calendarId = getCalendarId(message);
            ZonedDateTime startDate = getStartDate(message);
            ZonedDateTime endDate = getEndDate(message);
            CalendarApi.eventSelectorMethod(calendarId,startDate,endDate);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    private Long getCalendarId(Message message) {
       JSONObject object = message.getContent();
       return Long.parseLong(String.valueOf(object.get(FacilioConstants.Calendar.CALENDAR_ID)));
    }
    private ZonedDateTime getStartDate(Message message){
        JSONObject object = message.getContent();
        Long startDate = Long.parseLong(String.valueOf(object.get("startDate")));
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDate), ZoneId.systemDefault());

    }
    private ZonedDateTime getEndDate(Message message){
        JSONObject object = message.getContent();
        Long endDate = Long.parseLong(String.valueOf(object.get("endDate")));
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDate), ZoneId.systemDefault());

    }

}
