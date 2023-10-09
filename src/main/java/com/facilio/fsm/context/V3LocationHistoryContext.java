package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Getter @Setter
public class V3LocationHistoryContext extends V3Context {

    private long time;
    private V3PeopleContext people;
    public String getLocation(){
        return (location != null) ? location.toJSONString() : null;
    }

    public void setLocation(JSONObject location) {
        this.location = location;
    }

    public void setLocation(String data) throws ParseException{
        if(StringUtils.isNotEmpty(data)){
            this.location = (JSONObject) new JSONParser().parse(data);
        }
    }
    private JSONObject location;
    private Double batteryInfo;
    private Double signalInfo;

}
