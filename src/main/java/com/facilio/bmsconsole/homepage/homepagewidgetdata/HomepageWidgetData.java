package com.facilio.bmsconsole.homepage.homepagewidgetdata;

import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import lombok.Getter;
import lombok.Setter;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.services.procon.message.FacilioRecord;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class HomepageWidgetData {

    String linkName;
    Integer icon;
    String title;
    String primaryText;
    String secondaryText;
    String moduleName;
    String parentModuleName;
    Long RecordId;
    String secondaryText2;
    String date;
    String time;
    V3SpaceContext Space;
    Integer day;
    //List<JSONObject> params;
    JSONObject params;
    Long startTime;
    Long endTime;
    String buildingName;
    String floorName;


}
