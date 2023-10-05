package com.facilio.readingrule.context;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.facilio.connected.ResourceCategory;
import com.facilio.connected.ResourceType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NameSpaceCacheContext;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.connected.IConnectedRule;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class NewReadingRuleContext extends V3Context implements ReadingRuleInterface, IConnectedRule {

    private static final long serialVersionUID = 1L;

    long siteId;

    String name;

    String description;

    String moduleName;

    List<Long> assets;

    Map<Long, ResourceContext> matchedResources;

    RuleAlarmDetails alarmDetails;

    FaultImpactContext impact;

    NameSpaceContext ns;

    Long impactId;

    Boolean autoClear;

    Boolean status;

    int resourceType = ResourceType.ASSET_CATEGORY.getIndex();

    Long categoryId;

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
        this.resourceTypeEnum = ResourceType.valueOf(resourceType);
    }

    ResourceType resourceTypeEnum = ResourceType.ASSET_CATEGORY; //TODO: need to be change

    ResourceCategory<? extends V3Context> category;

    private AssetCategoryContext assetCategory;

    String linkName;

    public void setStatus(Boolean status) {
        this.status = status;
    }

    private ReadingRuleRCAContext rca;

    public boolean isActive() {
        return (status != null) ? status : false;
    }


    List<ActionContext> actions;

    Long readingModuleId;

    Long readingFieldId;

    FacilioField readingField;

    String readingFieldName;

    String readingModuleName;

    public NewReadingRuleContext(Long id, Long readingFieldId, NameSpaceCacheContext ns) {
        this.setId(id);
        this.setReadingFieldId(readingFieldId);
        this.setNs(ns);
    }

    public void setNullForResponse() {
        setMatchedResources(null);
        setReadingField(null);
        if (alarmDetails != null) {
            alarmDetails.setNullForResponse();
        }
    }

    @Override
    public long insertLog(Long startTime, Long endTime, Integer resourceCount, boolean isSysCreated) throws Exception {
        return NewReadingRuleAPI.insertLog(getId(), startTime, endTime, resourceCount == null ? assets.size() : resourceCount);
    }
}

