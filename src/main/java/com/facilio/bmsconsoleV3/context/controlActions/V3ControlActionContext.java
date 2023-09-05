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
        CONTROL_ACTION_TEMPLATE("Control Action Template"),
        FDD("Fault Detection And Diagnostic");

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
    public static enum ControlActionStatus implements FacilioIntEnum{
        UNPUBLISHED("Un Published"),
        PENDING_FIRST_LEVEL_APPROVAL("Pending First Level Approval"),
        PENDING_SECOND_LEVEL_APPROVED("Pending Second Level Approval"),
        REJECTED("Rejected"),
        SCHEDULED("Scheduled"),
        IN_PROGRESS("In Progress"),
        PENDING("Pending"),
        COMPLETED_WITH_ERROR("Completed With Error"),
        COMPLETED("Completed"),
        CANCELED("Canceled"),
        EXPIRED("Expired");

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
}
