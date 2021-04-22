package com.facilio.bmsconsoleV3.context.inspection;

import java.util.List;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.qa.context.QAndATemplateContext;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
@NoArgsConstructor
@ToString(callSuper=true, includeFieldNames=true)
public class InspectionTemplateContext extends QAndATemplateContext {
    private SiteContext site;

    List<InspectionTriggerContext> triggers;
    public InspectionTemplateContext(long id) {
        super(id);
    }
    
    private CreationType creationType;
    private PreventiveMaintenance.PMAssignmentType assignmentType;
    private BaseSpaceContext baseSpace;
    private AssetCategoryContext assetCategory;
    private SpaceCategoryContext spaceCategory;
    
    private ResourceContext resource;
    private VendorContext vendor;
    private TenantContext tenant;
    private InspectionCategoryContext category;
    private InspectionPriorityContext priority;
    private Group assignmentGroup;
    private User assignedTo;
    
    public int getCreationType() {
    	if(creationType != null) {
    		return creationType.getIndex();
    	}
    	return -1;
    }
    
    public void setCreationType(int index) {
    	creationType = CreationType.valueOf(index);
    }
    
    public int getAssignmentType() {
    	if(assignmentType != null) {
    		return assignmentType.getIndex();
    	}
    	return -1;
    }
    
    public PreventiveMaintenance.PMAssignmentType getAssignmentTypeEnum() {
    	return assignmentType;
    }
    
    public void setAssignmentType(int index) {
    	assignmentType = PreventiveMaintenance.PMAssignmentType.valueOf(index);
    }
    
    public static enum CreationType implements FacilioEnum<CreationType> {
		
		SINGLE, 
		MULTIPLE,
		;
		public int getVal() {
			return ordinal() + 1;
		}
		private static final CreationType[] CREATION_TYPES = CreationType.values();
		public static CreationType valueOf(int type) {
			if (type > 0 && type <= CREATION_TYPES.length) {
				return CREATION_TYPES[type - 1];
			}
			return null;
		}
	}
    
}
