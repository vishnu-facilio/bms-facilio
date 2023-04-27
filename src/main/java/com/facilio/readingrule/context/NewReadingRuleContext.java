package com.facilio.readingrule.context;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class  NewReadingRuleContext extends V3Context implements ReadingRuleInterface, Cloneable {

    private static final long serialVersionUID = 1L;

    long siteId;

    String name;

    String description;

    String appliedTo;

    String moduleName;

    List<Long> assets;

    Map<Long, ResourceContext> matchedResources;

    RuleAlarmDetails alarmDetails;

    FaultImpactContext impact;

    NameSpaceContext ns;

    Long WorkflowId;

    Long impactId;

    Boolean autoClear;

    Boolean status;

    private SpaceCategoryContext spaceCategory;

    private AssetCategoryContext assetCategory;

    public void setStatus(Boolean status) {
        this.status = status;
    }


    private ReadingRuleRCAContext rca;

    public boolean isActive() {
        if (status != null) {
            return status.booleanValue();
        }
        return false;
    }

    FacilioField readingField;

    public ResourceType getResourceTypeEnum() {
        return ResourceType.ASSET_CATEGORY;
    }

    List<ActionContext> actions;

    Long readingModuleId;

    Long readingFieldId;

    String readingFieldName;
    String readingModuleName;

    public void setNullForResponse() {
        setMatchedResources(null);
        setReadingField(null);
        if (alarmDetails != null) {
            alarmDetails.setNullForResponse();
        }
    }

    public enum ResourceType {
        ONE_RESOURCE,
        ALL_BUILDINGS,
        ALL_FLOORS,
        SPACE_CATEGORY,
        ASSET_CATEGORY;

        public int getValue() {
            return ordinal() + 1;
        }

        public static ResourceType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}

