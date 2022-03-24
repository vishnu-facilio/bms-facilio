package com.facilio.readingrule.context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class NewReadingRuleContext implements ReadingRuleInterface, Cloneable {

    private static final long serialVersionUID = 1L;

    long id;

    Long orgId;

    Long moduleId;

    FacilioModule module;

    Long fieldId;

    Long assetCategoryId;

    String name;

    String description;

    Long createdTime;

    Long createdBy;

    String appliedTo;

    List<Long> assets;

    private Map<Long, ResourceContext> matchedResources;

    RuleAlarmDetails alarmDetails;

    RuleBuilderConfiguration condition;

    boolean status;

    List<Long> rcaRules;

    public List<Long> getRcaRules() {
        return (rcaRules == null) ? new ArrayList<>() : rcaRules;
    }

    public boolean isActive() {
        return status;
    }

    FacilioField readingField;

    public ResourceType getResourceTypeEnum() {
        return ResourceType.ASSET_CATEGORY;
    }

    List<ActionContext> actions;

    public String toString() {
        return "id: " + id + ", name : " + name;
    }

    public void setNullForResponse() {
        setMatchedResources(null);
        setModule(null);
        setReadingField(null);
        condition.setNullForResponse();
        alarmDetails.setNullForResponse();
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

