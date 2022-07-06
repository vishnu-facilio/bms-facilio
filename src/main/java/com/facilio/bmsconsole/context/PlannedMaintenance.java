package com.facilio.bmsconsole.context;

import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.Data;

import java.util.List;

@Data
public class PlannedMaintenance extends V3WorkOrderContext {
    private V3AssetCategoryContext assetCategory;
    private V3SpaceCategoryContext spaceCategory;
    private V3BaseSpaceContext baseSpace;
    private PMScopeAssigmentType assignmentType;

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

    private Long dueDuration;
    private Long estimatedDuration;
    private List<V3SiteContext> sites;

    public enum PMScopeAssigmentType implements FacilioIntEnum {
        ASSETS,
        SPACES,
        ASSETCATEGORY,
        SPACECATEGORY,
        BUILDINGS,
        SITES;

        public static PMScopeAssigmentType valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }
    }
}
