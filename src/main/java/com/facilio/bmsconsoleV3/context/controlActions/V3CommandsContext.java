package com.facilio.bmsconsoleV3.context.controlActions;

import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V3CommandsContext extends V3Context {
    private String name;
    private V3ControlActionContext controlAction;
    private V3ActionContext action;
    private V3SiteContext site;
    private V3AssetContext asset;
    private ControllerContext controller;
    private Long fieldId;
    private Long actionTime;
    private Object previousValue;
    private Object setValue;
    private Object afterValue;
    private String errorMsg;
    private Long previousValueCapturedTime;
    private ControlActionCommandStatus controlActionCommandStatus;
    private CommandActionType commandActionType;
    private FacilioField readingField;

    public void setControlActionCommandStatus(Integer status) {
        if (status != null) {
            this.controlActionCommandStatus = ControlActionCommandStatus.valueOf(status);
        }
    }

    public ControlActionCommandStatus getControlActionStatusEnum() {
        return controlActionCommandStatus;
    }
    public Integer getControlActionCommandStatus() {
        if(controlActionCommandStatus != null) {
            return controlActionCommandStatus.getVal();
        }
        return -1;
    }
    public static enum ControlActionCommandStatus implements FacilioIntEnum{
        NOT_SCHEDULED("Not Scheduled"),
        CANCELED("Canceled"),
        IN_PROGRESS("In Progess"),
        PENDING("Pending"),
        TIMED_OUT("Timed Out"),
        FAILED("Failed"),
        SUCCESS("Success");

        public String getValue() {
            return this.name;
        }

        private String name;

        ControlActionCommandStatus(String name) {
            this.name = name;
        }

        public static ControlActionCommandStatus valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }

    }
    public void setCommandActionType(Integer actionType) {
        if (actionType != null) {
            this.commandActionType = CommandActionType.valueOf(actionType);
        }
    }

    public CommandActionType getCommandActionTypeEnum() {
        return commandActionType;
    }
    public Integer getCommandActionType() {
        if(commandActionType != null) {
            return commandActionType.getVal();
        }
        return -1;
    }
    public static enum CommandActionType implements FacilioIntEnum{
        SCHEDULED_ACTION("Schedule Action"),
        REVERT_ACTION("Revert Action");

        public String getValue() {
            return this.name;
        }

        private String name;

        CommandActionType(String name) {
            this.name = name;
        }

        public static CommandActionType valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }

    }

}
