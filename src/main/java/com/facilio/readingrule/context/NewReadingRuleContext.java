package com.facilio.readingrule.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.rule.AbstractRuleInterface;
import com.facilio.v3.context.V3Context;
import com.facilio.workflows.context.WorkflowContext;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class  NewReadingRuleContext extends V3Context implements ReadingRuleInterface, AbstractRuleInterface, Cloneable {

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

    List<Long> alarmRCARules;

    public List<Long> getAlarmRCARules() {
        return (alarmRCARules == null) ? new ArrayList<>() : alarmRCARules;
    }

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

    public void setNullForResponse() {
        setMatchedResources(null);
        setReadingField(null);
        if (alarmDetails != null) {
            alarmDetails.setNullForResponse();
        }
    }

    public FacilioField fetchRuleReadingResultField() throws Exception {
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = bean.getModule(getReadingModuleId());
        FacilioField field = bean.getField(NewReadingRuleAPI.RuleReadingsConstant.RULE_READING_RESULT, module.getName());
        return field;
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

