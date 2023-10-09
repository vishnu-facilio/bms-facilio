package com.facilio.fsm.context;

import com.facilio.bmsconsole.calendarview.CalendarViewContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.constants.FacilioConstants.ViewConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Getter @Setter
public class DispatcherSettingsContext {
    private long id;
    private String name;
    private String description;


    public Boolean getMapView() {
        return isMapView;
    }

    public void setMapView(Boolean mapView) {
        isMapView = mapView;
    }

    private Boolean isMapView;

    public long getStartTimeFieldId() {
        return startTimeFieldId;
    }

    public void setStartTimeFieldId(long startTimeFieldId) {
        this.startTimeFieldId = startTimeFieldId;
    }

    private long startTimeFieldId;

    public long getEndTimeFieldId() {
        return endTimeFieldId;
    }

    public void setEndTimeFieldId(long endTimeFieldId) {
        this.endTimeFieldId = endTimeFieldId;
    }

    private long endTimeFieldId;


    public FacilioField getStartTimeField() {
        return startTimeField;
    }

    public void setStartTimeField(FacilioField startTimeField) {
        this.startTimeField = startTimeField;
    }

    private FacilioField startTimeField;

    public FacilioField getEndTimeField() {
        return endTimeField;
    }

    public void setEndTimeField(FacilioField endTimeField) {
        this.endTimeField = endTimeField;
    }

    private FacilioField endTimeField;



    private CalendarViewContext calendarViewContext;
    public CalendarViewContext getCalendarViewContext() {
        return calendarViewContext;
    }

    public void setCalendarViewContext(CalendarViewContext calendarViewContext) {
        this.calendarViewContext = calendarViewContext;
    }




    private ViewConstants.CalendarViewType defaultCalendarView;
    public int getDefaultCalendarView() {
        if(defaultCalendarView != null) {
            return defaultCalendarView.getIntVal();
        }
        return ViewConstants.CalendarViewType.DAY.getIntVal();
    }
    public void setDefaultCalendarView(int defaultCalendarView) {
        this.defaultCalendarView = ViewConstants.CalendarViewType.TYPE_MAP.get(defaultCalendarView);
    }
    public void setDefaultCalendarView(ViewConstants.CalendarViewType defaultCalendarView) {
        this.defaultCalendarView = defaultCalendarView;
    }


    public long getResourceCriteriaId() {
        return resourceCriteriaId;
    }

    public void setResourceCriteriaId(long resourceCriteriaId) {
        this.resourceCriteriaId = resourceCriteriaId;
    }

    private long resourceCriteriaId;
    public Criteria getResourceCriteria() {
        return resourceCriteria;
    }

    public void setResourceCriteria(Criteria resourceCriteria) {
        this.resourceCriteria = resourceCriteria;
    }

    private Criteria resourceCriteria;

//    public long getWorkorderCriteriaId() {
//        return workorderCriteriaId;
//    }
//
//    public void setWorkorderCriteriaId(long workorderCriteriaId) {
//        this.workorderCriteriaId = workorderCriteriaId;
//    }
//
//    private long workorderCriteriaId;

//    public Criteria getWorkorderCriteria() {
//        return workorderCriteria;
//    }
//
//    public void setWorkorderCriteria(Criteria workorderCriteria) {
//        this.workorderCriteria = workorderCriteria;
//    }
//
//    private Criteria workorderCriteria;

    public String getWorkorderConfigJson() {
        return (workorderConfigJson != null) ? workorderConfigJson.toJSONString() : null;
    }

    public void setWorkorderConfigJson(JSONObject workorderConfigJson) {
        this.workorderConfigJson = workorderConfigJson;
    }
    public void setWorkorderConfigJson(String data) throws ParseException {
        if (StringUtils.isNotEmpty(data)) {
            this.workorderConfigJson = (JSONObject) new JSONParser().parse(data);
        }
    }

    private JSONObject workorderConfigJson;

    public String getResourceConfigJson() {
        return (resourceConfigJson != null) ? resourceConfigJson.toJSONString() : null;
    }

    public void setResourceConfigJson(JSONObject resourceConfigJson) {
        this.resourceConfigJson = resourceConfigJson;
    }
    public void setResourceConfigJson(String data) throws ParseException {
        if (StringUtils.isNotEmpty(data)) {
            this.resourceConfigJson = (JSONObject) new JSONParser().parse(data);
        }
    }
    private JSONObject resourceConfigJson;

    public String getEventConfigJson() {
        return (eventConfigJson != null) ? eventConfigJson.toJSONString() : null;
    }

    public void setEventConfigJson(JSONObject eventConfigJson) {
        this.eventConfigJson = eventConfigJson;
    }
    public void setEventConfigJson(String data) throws ParseException {
        if (StringUtils.isNotEmpty(data)) {
            this.eventConfigJson = (JSONObject) new JSONParser().parse(data);
        }
    }

    private JSONObject eventConfigJson;

    public SharingContext<SingleSharingContext> getDispatcherSharing() {
        return dispatcherSharing;
    }

    public void setDispatcherSharing(SharingContext<SingleSharingContext> dispatcherSharing) {
        this.dispatcherSharing = dispatcherSharing;
    }

    private SharingContext<SingleSharingContext> dispatcherSharing;

    public  int getDefaultStartDay() {
        return this.defaultStartDay != null ? this.defaultStartDay.getIndex() : -1;
    }
    public void setDefaultStartDay(int defaultStartDay) {
        this.defaultStartDay = StartDay.valueOf(defaultStartDay);
    }
    public void setDefaultStartDay(StartDay defaultStartDay) {
        this.defaultStartDay = defaultStartDay;
    }

    private StartDay defaultStartDay;

    public StartDay getDefaultStartDayEnum() {
        return this.defaultStartDay;
    }

    public static enum StartDay implements FacilioIntEnum {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY;

        private StartDay() {
        }

        public static StartDay valueOf(int value) {
            return value > 0 && value <= values().length ? values()[value - 1] : null;
        }

        public Integer getIndex() {
            return this.ordinal() + 1;
        }

        public String getValue() {
            return this.name();
        }
    }
}

