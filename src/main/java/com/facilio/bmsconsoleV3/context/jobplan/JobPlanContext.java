package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class JobPlanContext extends V3Context {

    private String name;
    private JPScopeAssignmentType jobPlanCategory;

    public void setJobPlanCategory(Integer type) {
        if (type != null) {
            this.jobPlanCategory = JPScopeAssignmentType.valueOf(type);
        }
    }

    public JPScopeAssignmentType getJobPlanCategoryEnum() {
        return jobPlanCategory;
    }
    public Integer getJobPlanCategory() {
        if(jobPlanCategory != null) {
            return jobPlanCategory.getVal();
        }
        return -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<JobPlanTaskSectionContext> jobplansection;

    public List<JobPlanTaskSectionContext> getJobplansection() {
        return jobplansection;
    }

    public void setJobplansection(List<JobPlanTaskSectionContext> jobplansection) {
        this.jobplansection = jobplansection;
    }

    private V3AssetCategoryContext assetCategory;

    public V3SpaceCategoryContext spaceCategory;

    public V3AssetCategoryContext getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(V3AssetCategoryContext assetCategory) {
        this.assetCategory = assetCategory;
    }

    public V3SpaceCategoryContext getSpaceCategory() {
        return spaceCategory;
    }

    public void setSpaceCategory(V3SpaceCategoryContext spaceCategory) {
        this.spaceCategory = spaceCategory;
    }
    private JobPlanContext group;
    public JobPlanContext getGroup() {
        return group;
    }
    public void setGroup(JobPlanContext group) {
        this.group = group;
    }
    private Double jobPlanVersion;
    public void setJobPlanVersion(Double jobPlanVersion){
        this.jobPlanVersion = jobPlanVersion;
    }
    public Double getJobPlanVersion(){
        return jobPlanVersion;
    }
    private JPStatus jpStatus;

    public Integer getJpStatus() {
        if (jpStatus == null) {
            return null;
        }
        return jpStatus.getIndex();
    }

    public void setJpStatus(Integer jpStatus) {
        if (jpStatus != null) {
            this.jpStatus = JPStatus.valueOf(jpStatus);
        } else {
            this.jpStatus = null;
        }
    }
    public void setJpStatusEnum(JPStatus jpStatus) {
        this.jpStatus = jpStatus;
    }

    public JPStatus getJpStatusEnum() {
        return jpStatus;
    }

    @AllArgsConstructor
    @Getter
    public static enum JPStatus implements FacilioIntEnum {

        IN_ACTIVE("In Active"),
        ACTIVE("Active"),
        DISABLED("Disabled"),
        PENDING_REVISION("Pending Revision"),
        REVISED("Revised")
        ;

        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final JPStatus[] CREATION_TYPES = JPStatus.values();
        public static JPStatus valueOf(int type) {
            if (type > 0 && type <= CREATION_TYPES.length) {
                return CREATION_TYPES[type - 1];
            }
            return null;
        }
    }
    public static enum JPScopeAssignmentType implements FacilioIntEnum {
        ASSETS("Assets"),
        SPACES("Spaces"),
        ASSETCATEGORY("Asset Category"),
        SPACECATEGORY("Space Category"),
        BUILDINGS("Buildings"),
        SITES("Sites"),
        FLOORS("Floors"),
        GENERAL("General"),
        METERTYPE("Meter Type");

        @Override
        public String getValue() {
            return this.name;
        }

        private String name;

        JPScopeAssignmentType(String name) {
            this.name = name;
        }

        public static JPScopeAssignmentType valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }
    }


    public static enum SFGType implements FacilioIntEnum {
        CORE("Core"),
        CUSTOM("Custom");

        @Override
        public String getValue() {
            return this.name;
        }

        private String name;

        SFGType(String name) {
            this.name = name;
        }

        public static SFGType valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }
    }

    public Integer getSfgType() {
        if(sfgType == null)
        {
            return null;
        }
        return sfgType.getVal();
    }

    public void setSfgType(Integer sfgType) {
        if (sfgType != null) {
            this.sfgType = SFGType.valueOf(sfgType);
        } else {
            this.sfgType = null;
        }
    }

    public void setSfgTypeEnum(SFGType sfgType) {
        this.sfgType = sfgType;
    }

    public SFGType getSfgTypeEnum() {
        return sfgType;
    }

    private SFGType sfgType;

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    private Integer scheduleId;

    public String getSfgCode() {
        return sfgCode;
    }

    public void setSfgCode(String sfgCode) {
        this.sfgCode = sfgCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    private String sfgCode;
    private String content;
    private String notes;

    public String getSfgVersion() {
        return sfgVersion;
    }

    public void setSfgVersion(String sfgVersion) {
        this.sfgVersion = sfgVersion;
    }

    private String sfgVersion="";

    public Boolean getSfg() {
        return isSfg;
    }

    public void setSfg(Boolean sfg) {
        isSfg = sfg;
    }

    private Boolean isSfg;

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    private String unitOfMeasure;

    public String getSfgLegislations() {
        return sfgLegislations;
    }

    public void setSfgLegislations(String sfgLegislations) {
        this.sfgLegislations = sfgLegislations;
    }

    private String sfgLegislations;

}
