package com.facilio.bmsconsole.context;

import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext.CreationType;
import com.facilio.modules.FacilioIntEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class PlannedMaintenance extends V3WorkOrderContext {
    private V3AssetCategoryContext assetCategory;
    private V3SpaceCategoryContext spaceCategory;
    private V3BaseSpaceContext baseSpace;
    private PMScopeAssigmentType assignmentType;
    private PMStatus pmStatus; 
    private Long leadTime = 0l;

    private String name;
    public Integer getAssignmentType() {
        if (assignmentType == null) {
            return null;
        }
        return assignmentType.getIndex();
    }

    public void setAssignmentType(Integer assignmentType) {
        if (assignmentType != null) {
            this.assignmentType = PMScopeAssigmentType.valueOf(assignmentType);
        } else {
            this.assignmentType = null;
        }
    }
    public void setAssignmentTypeEnum(PMScopeAssigmentType assignmentType) {
        this.assignmentType = assignmentType;
    }

    public PMScopeAssigmentType getAssignmentTypeEnum() {
        return assignmentType;
    }
    
    
    public Integer getPmStatus() {
        if (pmStatus == null) {
            return null;
        }
        return pmStatus.getIndex();
    }

    public void setPmStatus(Integer pmStatus) {
        if (pmStatus != null) {
            this.pmStatus = PMStatus.valueOf(pmStatus);
        } else {
            this.pmStatus = null;
        }
    }
    public void setPmStatusEnum(PMStatus pmStatus) {
        this.pmStatus = pmStatus;
    }

    public PMStatus getPmStatusEnum() {
        return pmStatus;
    }

    private Long dueDuration;
    private Long estimatedDuration;
    private List<V3SiteContext> sites;
    
    
    @AllArgsConstructor
    @Getter
    public static enum PMStatus implements FacilioIntEnum {
		
		IN_ACTIVE("In Active"), 
		ACTIVE("Active"),
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
		private static final PMStatus[] CREATION_TYPES = PMStatus.values();
		public static PMStatus valueOf(int type) {
			if (type > 0 && type <= CREATION_TYPES.length) {
				return CREATION_TYPES[type - 1];
			}
			return null;
		}
	}
    
    
    public enum PMScopeAssigmentType implements FacilioIntEnum {
        ASSETS("Assets"),
        SPACES("Spaces"),
        ASSETCATEGORY("Asset Category"),
        SPACECATEGORY("Space Category"),
        BUILDINGS("Buildings"),
        SITES("Sites"),
        FLOORS("Floors"),
        SFG20("SFG 20");

        @Override
        public String getValue() {
            return this.name;
        }

        private String name;

        PMScopeAssigmentType(String name) {
            this.name = name;
        }

        public static PMScopeAssigmentType valueOf(int index) {
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
