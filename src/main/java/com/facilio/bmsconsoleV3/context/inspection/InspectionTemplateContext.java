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
import com.facilio.qa.context.QAndATemplateContext;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
@NoArgsConstructor
public class InspectionTemplateContext extends QAndATemplateContext {
    private SiteContext site;

    List<InspectionTriggerContext> triggers;
    public InspectionTemplateContext(long id) {
        super(id);
    }
    
    PreventiveMaintenance.PMCreationType creationType;
    PreventiveMaintenance.PMAssignmentType assignmentType;
    BaseSpaceContext baseSpace;
    AssetCategoryContext assetCategory;
    SpaceCategoryContext spaceCategory;
    
    ResourceContext resource;
    VendorContext vendor;
    TenantContext tenant;
    InspectionCategoryContext category;
    InspectionPriorityContext priority;
    Group assignmentGroup;
    User assignedTo;
}
