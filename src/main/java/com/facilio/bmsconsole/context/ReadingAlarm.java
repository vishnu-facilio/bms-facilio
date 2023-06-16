package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.enums.FaultType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingAlarm extends BaseAlarmContext {
    private static final long serialVersionUID = 1L;

    private ReadingAlarmCategoryContext readingAlarmCategory;

    @JsonDeserialize(as = ReadingRuleContext.class)
    private ReadingRuleInterface rule;

    public ReadingRuleInterface getRule() {
        return rule;
    }

    public void setRule(ReadingRuleInterface rule) throws Exception {
        if(rule == null) {
            return;
        }
        if(getIsNewReadingRule()) {
            NewReadingRuleContext ruleContext = new NewReadingRuleContext();
            ruleContext.setId(rule.getId());
            this.rule = ruleContext;
        } else {
            this.rule = rule;
        }
    }

    public void setNewRule(ReadingRuleInterface rule) {
        if (rule != null) {
            this.rule = rule;
        }
    }

    @JsonDeserialize(as = ReadingRuleContext.class)
    private ReadingRuleInterface subRule;

    public ReadingRuleInterface getSubRule() {
        return subRule;
    }
    public void setSubRule(ReadingRuleInterface subRule) throws Exception {
        if(subRule == null) {
            return;
        }
        if (getIsNewReadingRule()) {
            NewReadingRuleContext ruleContext = new NewReadingRuleContext();
            ruleContext.setId(subRule.getId());
            this.subRule = ruleContext;
        } else {
            this.subRule = subRule;
        }
    }

    private long readingFieldId;

    private String readingFieldName;

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

    private Double energyImpact;

    private Double costImpact;

    private AssetCategoryContext readingAlarmAssetCategory;

}
