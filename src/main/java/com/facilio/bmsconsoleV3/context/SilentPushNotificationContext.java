package com.facilio.bmsconsoleV3.context;

import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class SilentPushNotificationContext extends V3Context {
    private static final Logger LOGGER = LogManager.getLogger(SilentPushNotificationContext.class.getName());

    private ActionType actionType;
    public ActionType getActionType() {
        return actionType;
    }
    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    private Map<String, Object> data;
    public Map<String, Object> getData() {
        return data;
    }
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    public JSONObject toJSON() throws Exception {
        try {
            return FacilioUtil.getAsJSON(this);
        }catch (Exception e){
            LOGGER.error("Error converting notification data to Json", e);
        }
        return null;
    }

    public enum ActionType {
        OFFLINE_RECORD_UPDATE(1, "OFFLINE_RECORD_UPDATE"),
        OFFLINE_ATTACHMENT_UPDATE(2, "OFFLINE_ATTACHMENT_UPDATE"),
        OFFLINE_TASK_UPDATE(3, "OFFLINE_TASK_UPDATE"),
        SHIFT_START(4,"SHIFT_START"),
        SHIFT_END(5,"SHIFT_END"),
        CHECK_IN(6,"CHECK_IN"),
        CHECK_OUT(7,"CHECK_OUT"),
        START_TRIP(8,"START_TRIP"),
        END_TRIP(9,"END_TRIP");

        int intVal;
        String name;

        ActionType(int intVal, String name) {
            this.intVal = intVal;
            this.name = name;
        }

        public int getIntVal() {
            return intVal;
        }
        public String getName() {
            return name;
        }
    }
}
