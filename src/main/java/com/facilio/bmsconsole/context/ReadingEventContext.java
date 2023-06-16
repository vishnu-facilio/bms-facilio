package com.facilio.bmsconsole.context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.enums.FaultType;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Getter
@Setter
public class ReadingEventContext extends BaseEventContext {
    private static final long serialVersionUID = 1L;

    @Override
    public String constructMessageKey() {
        if (getResource() != null && getRule() != null) {
            return rule.getId() + "_" + getResource().getId();
        }
        return null;
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new ReadingAlarm();
        }
        super.updateAlarmContext(baseAlarm, add);
        ReadingAlarm readingAlarm = (ReadingAlarm) baseAlarm;

        if (readingAlarmCategory == null) {
            if (getResource() != null) {
                readingAlarmCategory = NewAlarmAPI.getReadingAlarmCategory(getResource().getId());
            }
        }
        readingAlarm.setReadingAlarmCategory(readingAlarmCategory);
        readingAlarm.setReadingAlarmAssetCategory(getAssetCategory());

        if (this.getFaultTypeEnum() != null) {
            readingAlarm.setFaultType(this.getFaultTypeEnum());
        }
        readingAlarm.setRule(rule);
        readingAlarm.setSubRule(subRule);
        if (readingFieldId != -1) {
            readingAlarm.setReadingFieldId(readingFieldId);
        }
        readingAlarm.setNewReadingRule(isNewReadingRule);

        return baseAlarm;
    }

    @Override
    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (add && alarmOccurrence == null) {
            alarmOccurrence = new ReadingAlarmOccurrenceContext();
        }

        ReadingAlarmOccurrenceContext readingOccurrence = (ReadingAlarmOccurrenceContext) alarmOccurrence;
        if (readingAlarmCategory == null) {
            if (getResource() != null) {
                readingAlarmCategory = NewAlarmAPI.getReadingAlarmCategory(getResource().getId());
            }
        }
        readingOccurrence.setReadingAlarmCategory(readingAlarmCategory);
        readingOccurrence.setReadingAlarmAssetCategory(getAssetCategory());

        if (this.getFaultTypeEnum() != null) {
            readingOccurrence.setFaultType(this.getFaultTypeEnum());
        }
        readingOccurrence.setRule(rule);
        readingOccurrence.setSubRule(subRule);
        if (readingFieldId != -1) {
            readingOccurrence.setReadingFieldId(readingFieldId);
        }
        readingOccurrence.setNewReadingRule(isNewReadingRule);
        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

    private ReadingAlarmCategoryContext readingAlarmCategory;
    private AssetCategoryContext readingAlarmAssetCategory;

    @JsonDeserialize(as = ReadingRuleContext.class)
    private ReadingRuleInterface rule;

    public void setRule(ReadingRuleInterface rule) throws Exception {
        if(rule == null) {
            return;
        }
        if (isNewReadingRule) {
            NewReadingRuleContext ruleContext = new NewReadingRuleContext();
            ruleContext.setId(rule.getId());
            this.rule = ruleContext;
        } else {
            this.rule = rule;
        }
    }

    @JsonDeserialize(as = ReadingRuleContext.class)
    private ReadingRuleInterface subRule;

    public void setSubRule(ReadingRuleInterface subRule) throws Exception {
        if(subRule == null) {
            return;
        }

        if (isNewReadingRule) {
            NewReadingRuleContext ruleContext = new NewReadingRuleContext();
            ruleContext.setId(subRule.getId());
            this.subRule = ruleContext;
        } else {
            this.subRule = subRule;
        }
    }

    private long readingFieldId = -1;

    private FaultType faultType;

    public int getFaultType() {
        if (faultType != null) {
            return faultType.getIndex();
        }
        return -1;
    }

    public FaultType getFaultTypeEnum() {
        return faultType;
    }

    public void setFaultType(FaultType faultType) {
        this.faultType = faultType;
    }

    public void setFaultType(int faultType) {
        this.faultType = FaultType.valueOf(faultType);
    }

    @Override
    @JsonSerialize
    public Type getEventTypeEnum() {
        return Type.READING_ALARM;
    }

    @JsonIgnore
    @JSON(deserialize = false)
    public void setRuleId(long ruleId) throws Exception {
        if (ruleId <= 0) {
            return;
        }

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE)) {
            NewReadingRuleContext ruleContext = new NewReadingRuleContext();
            ruleContext.setId(ruleId);
            setRule(ruleContext);
        } else {
            ReadingRuleContext ruleContext = new ReadingRuleContext();
            ruleContext.setId(ruleId);
            setRule(ruleContext);
        }
    }

    @JsonIgnore
    @JSON(deserialize = false)
    public void setSubRuleId(long subRuleId) throws Exception {
        if (subRuleId <= 0) {
            return;
        }

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE)) {
            NewReadingRuleContext ruleContext = new NewReadingRuleContext();
            ruleContext.setId(subRuleId);
            setRule(ruleContext);
        } else {
            ReadingRuleContext ruleContext = new ReadingRuleContext();
            ruleContext.setId(subRuleId);
            setRule(ruleContext);
        }
    }

    private boolean isNewReadingRule;

    public void setNewReadingRule(boolean isNewReadingRule) {
        this.isNewReadingRule = isNewReadingRule;
    }

    public boolean getIsNewReadingRule() {
        return isNewReadingRule;
    }

    private Double energyImpact;

    private Double costImpact;

    private AssetCategoryContext getAssetCategory() throws Exception {
        if (readingAlarmAssetCategory == null) {
            if (getResource() != null && getResource().getId() > 0) {
                ResourceContext resource = ResourceAPI.getExtendedResource(getResource().getId());
                if (resource != null && resource.getResourceTypeEnum() == ResourceContext.ResourceType.ASSET) {
                    AssetContext asset = (AssetContext) resource;
                    if (asset.getCategory() != null && asset.getCategory().getId() != -1) {
                        readingAlarmAssetCategory=AssetsAPI.getCategoryForAsset(asset.getCategory().getId());
                    }

                }
            }
        }
        return readingAlarmAssetCategory;
    }
}
