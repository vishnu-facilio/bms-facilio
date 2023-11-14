package com.facilio.bmsconsoleV3.context.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DashboardCustomActionContext {
    public long id=-1l;
    public String title;
    public long widgetId = -1l;
    public String url;
    public Long connectedAppId;
    public String customScriptId;
    public int actionType;
    public V2ActionType actionTypeEnum;
    public void setActionTypeEnum(int actionType) {
        this.actionTypeEnum = DashboardCustomActionContext.V2ActionType.valueOf(actionType);
    }
    public void setReportType(int actionType) {
        this.actionType = actionType;
        this.setActionTypeEnum(actionType);
    }
    public enum V2ActionType
    {
        URL, CONNECTED_APP, FUNCTION;

        public int getValue() {
            return ordinal() + 1;
        }

        public static DashboardCustomActionContext.V2ActionType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
