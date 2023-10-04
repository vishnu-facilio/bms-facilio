package com.facilio.bmsconsoleV3.context.calendar;


import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.protocol.types.Field;

@Getter
@Setter
public class V3CalendarSlotsContext extends V3Context{
    private V3CalendarContext calendar;
    private V3EventContext event;
    private Integer calendarYear;
    private Integer calendarMonth;
    private Integer calendarDate;
    private Integer calendarWeekNumber;
    private Integer calendarWeekDay;
    private Integer slotStartTime;
    private Integer slotEndTime;
    private String start;
    private String end;
    private String title;
    private String bgColor;
    private String borderColor;
    private String displayStartTime;
    private String displayEndTime;
    public String getStart(){
        return getDate(getCalendarYear(),getCalendarMonth(),getCalendarDate(),getSlotStartTime(),false);
    }
    public String getEnd() {
        return getDate(getCalendarYear(),getCalendarMonth(),getCalendarDate(),getSlotEndTime(),false);
    }
    public String getDate(Integer calendarYear, Integer calendarMonth, Integer calendarDate, Integer slotTime, boolean needTimeSuffix){
        String month = String.valueOf(calendarMonth);
        String monthDate = String.valueOf(calendarDate);
        if(month.length() == 1){
            month = "0"+month;
        }
        if(monthDate.length() == 1){
            monthDate = "0"+monthDate;
        }
        String date =calendarYear+"-"+month+"-"+monthDate;
        String time = getTime(slotTime,needTimeSuffix);
        return date+" "+time.substring(0,5);
     }
    public String getTime(Integer slotTime, boolean needSuffix){
        String hr = String.valueOf(slotTime/60);
        String min = String.valueOf(slotTime%60);
        if(hr.length() == 1){
            hr = "0"+hr;
        }
        if(min.length() == 1){
            min = "0"+min;
        }
        if(!needSuffix){
            return hr+":"+min;
        }
        String suffix = "am";
        if(Integer.parseInt(hr) == 12){
            suffix = "pm";
        }
        if(Integer.parseInt(hr) > 12){
            hr = String.valueOf(Integer.parseInt(hr) - 12);
            if(hr.length() == 1){
                hr = "0"+hr;
            }

            suffix = "pm";
        }
        if(Integer.parseInt(hr) == 0){
            hr = "12";
        }
        String time = hr+":"+min+""+suffix;
        return time;
    }
}
