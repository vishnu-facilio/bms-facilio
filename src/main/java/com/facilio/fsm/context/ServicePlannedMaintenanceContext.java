package com.facilio.fsm.context;

import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServicePlannedMaintenanceContext extends ServiceOrderContext{
    private static final long serialVersionUID = 1L;

    private String pmName;
    private String pmDescription;
    private PMType pmType;
    private TriggerType triggerType;
    private ServicePMTriggerContext servicePMTrigger;
    private ServicePlanContext servicePlan;
    private Boolean isPublished;
    private Integer leadTime;
    private Integer previewPeriod;
    private Integer counter;
    private Long estimatedDuration;
    private Long nextRun;
    private Long lastRun;
    private ServicePMTemplateContext servicePMTemplate;

    public Integer getPmType() {
        if (pmType != null) {
            return pmType.getIndex();
        }
        return null;
    }

    public void setPmType(Integer pmType) {
        if (pmType != null) {
            this.pmType = PMType.valueOf(pmType);
        }
    }
    public PMType getPmTypeEnum() {
        return pmType;
    }


    public enum PMType implements FacilioIntEnum {
        SITE("Site"),
        ASSET("Asset"),
        SPACE("Space");

        private final String value;
        PMType(String value) {
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return value;
        }

        public static PMType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public Integer getTriggerType() {
        if (triggerType != null) {
            return triggerType.getIndex();
        }
        return null;
    }

    public void setTriggerType(Integer triggerType) {
        if (triggerType != null) {
            this.triggerType = TriggerType.valueOf(triggerType);
        }
    }
    public TriggerType getTriggerTypeEnum() {
        return triggerType;
    }

    public enum TriggerType implements FacilioIntEnum {
        RECURRING_SCHEDULE("Recurring Schedule"),
        CALENDAR("Calendar"),
        METER_READING("Meter Reading");

        private final String value;
        TriggerType(String value) {
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return value;
        }

        public static TriggerType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public Boolean getIsPublished() {
        if(isPublished!=null){
            return isPublished.booleanValue();
        }
        return false;
    }

    public void setIsPublished(Boolean published) {
        isPublished = published;
    }
}
