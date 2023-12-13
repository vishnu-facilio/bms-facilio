package com.facilio.bmsconsoleV3.context.controlActions;

import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
public class V3ControlActionContext extends V3Context {
    private String name;
    private String description;
    private ControlActionSourceTypeEnum controlActionSourceType;
    private ControlActionTypeEnum controlActionType;
    private V3AssetCategoryContext assetCategory;
    private Long scheduledActionDateTime;
    private Long revertActionDateTime;
    private Criteria siteCriteria;
    private Criteria assetCriteria;
    private Criteria controllerCriteria;
    private Long siteCriteriaId;
    private Long assetCriteriaId;
    private Long controllerCriteriaId;
    private V3ControlActionTemplateContext controlActionTemplate;
    private List<V3ActionContext> actionContextList;
    private List<PeopleContext> firstLevelApproval;
    private List<PeopleContext> secondLevelApproval;
    private ControlActionStatus controlActionStatus;
    private ControlActionExecutionType controlActionExecutionType;
    private ControlActionStatus scheduleActionStatus;
    private ControlActionStatus revertActionStatus;
    private Boolean isSandBox;
    public void setControlActionSourceType(Integer type) {
        if (type != null) {
            this.controlActionSourceType = ControlActionSourceTypeEnum.valueOf(type);
        }
    }

    public ControlActionSourceTypeEnum getControlActionSourceTypeEnum() {
        return controlActionSourceType;
    }
    public Integer getControlActionSourceType() {
        if(controlActionSourceType != null) {
            return controlActionSourceType.getVal();
        }
        return -1;
    }

    public static enum ControlActionSourceTypeEnum implements FacilioIntEnum{
        MANUAL("Manual"),
        CONTROL_ACTION_TEMPLATE("Template"),
        FDD("Fault Detection And Diagnostic"),
        FLAGGED_ALARM("Flagged Alarm");

        @Override
        public String getValue() {
            return this.name;
        }

        private String name;

        ControlActionSourceTypeEnum(String name) {
            this.name = name;
        }

        public static ControlActionSourceTypeEnum valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }
    }

    public void setControlActionType(Integer type) {
        if (type != null) {
            this.controlActionType = ControlActionTypeEnum.valueOf(type);
        }
    }

    public ControlActionTypeEnum getControlActionTypeEnum() {
        return controlActionType;
    }
    public Integer getControlActionType() {
        if(controlActionType != null) {
            return controlActionType.getVal();
        }
        return -1;
    }
    public static enum ControlActionTypeEnum implements FacilioIntEnum{
        SET_POINT_CHANGE("Set Point Change"),
        SCHEDULE_CHANGE("Schedule Change");
        @Override
        public String getValue() {
            return this.name;
        }

        private String name;

        ControlActionTypeEnum(String name) {
            this.name = name;
        }

        public static ControlActionTypeEnum valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }
    }
    public void setControlActionStatus(Integer status) {
        if (status != null) {
            this.controlActionStatus = ControlActionStatus.valueOf(status);
        }
    }

    public ControlActionStatus getControlActionStatusEnum() {
        return controlActionStatus;
    }
    public Integer getControlActionStatus() {
        if(controlActionStatus != null) {
            return controlActionStatus.getVal();
        }
        return -1;
    }
    public void setScheduleActionStatus(Integer status) {
        if (status != null) {
            this.scheduleActionStatus = ControlActionStatus.valueOf(status);
        }
    }

    public ControlActionStatus getScheduleActionStatusEnum() {
        return scheduleActionStatus;
    }
    public Integer getScheduleActionStatus() {
        if(scheduleActionStatus != null) {
            return scheduleActionStatus.getVal();
        }
        return -1;
    }
    public void setRevertActionStatus(Integer status) {
        if (status != null) {
            this.revertActionStatus = ControlActionStatus.valueOf(status);
        }
    }

    public ControlActionStatus getRevertActionStatusEnum() {
        return revertActionStatus;
    }
    public Integer getRevertActionStatus() {
        if(revertActionStatus != null) {
            return revertActionStatus.getVal();
        }
        return -1;
    }
    public static enum ControlActionStatus implements FacilioIntEnum{
        UNPUBLISHED("Unpublished"),
        PUBLISHED("Published"),
        WAITING_FOR_FIRST_LEVEL_APPROVAL("Waiting For First Level Approval"),
        FIRST_LEVEL_APPROVED("First Level Approved"),
        WAITING_FOR_SECOND_LEVEL_APPROVAL("Waiting For Second Level Approval"),
        SECOND_LEVEL_APPROVED("Second Level Approved"),
        COMMAND_GENERATED("Command Generated"),
        SCHEDULE_ACTION_SCHEDULED("Schedule Action Scheduled"),
        SCHEDULE_ACTION_IN_PROGRESS("Schedule Action In Progress"),
        SCHEDULE_ACTION_SUCCESS("Schedule Action Success"),
        SCHEDULE_ACTION_FAILED("Schedule Action Failed"),
        SCHEDULE_ACTION_COMPLETED_WITH_ERROR("Schedule Action Completed with Error"),
        REVERT_ACTION_SCHEDULED("Revert Action Scheduled"),
        REVERT_ACTION_IN_PROGRESS("Revert Action In Progress"),
        REVERT_ACTION_SUCCESS("Revert Action Success"),
        REVERT_ACTION_FAILED("Revert Action Failed"),
        REVERT_ACTION_COMPLETED_WITH_ERROR("Revert Action Completed with Error"),
        REJECTED("Cancelled");

        public String getValue() {
            return this.name;
        }

        private String name;

        ControlActionStatus(String name) {
            this.name = name;
        }

        public static ControlActionStatus valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }

    }
    public void setControlActionExecutionType(Integer type) {
        if (type != null) {
            this.controlActionExecutionType = ControlActionExecutionType.valueOf(type);
        }
    }

    public ControlActionExecutionType getControlActionExecutionTypeEnum() {
        return controlActionExecutionType;
    }
    public Integer getControlActionExecutionType() {
        if(controlActionExecutionType != null) {
            return controlActionExecutionType.getVal();
        }
        return -1;
    }
    public static enum ControlActionExecutionType implements FacilioIntEnum{
        ACTUAL("Actual"),
        SANDBOX("Stimulated");

        public String getValue() {
            return this.name;
        }

        private String name;

        ControlActionExecutionType(String name) {
            this.name = name;
        }

        public static ControlActionExecutionType valueOf(int index) {
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
