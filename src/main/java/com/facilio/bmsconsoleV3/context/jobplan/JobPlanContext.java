package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PlannedMaintenance.PMStatus;
import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class JobPlanContext extends V3Context {

    private String name;
    private PlannedMaintenance.PMScopeAssigmentType jobPlanCategory;

    public void setJobPlanCategory(Integer type) {
        if (type != null) {
            this.jobPlanCategory = PlannedMaintenance.PMScopeAssigmentType.valueOf(type);
        }
    }

    public PlannedMaintenance.PMScopeAssigmentType getJobPlanCategoryEnum() {
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
    
}
