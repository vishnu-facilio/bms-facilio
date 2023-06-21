package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.enums.FaultType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.BooleanUtils;

@Getter
@Setter
public class ReadingAlarmOccurrenceContext extends AlarmOccurrenceContext {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ReadingAlarmCategoryContext readingAlarmCategory;

    public boolean isNewReadingRule;

    public void setIsNewReadingRule(boolean isNewReadingRule) throws Exception {
        this.isNewReadingRule = isNewReadingRule;
        if (this.rule != null) {
            this.setRule(this.rule);
        }
        if (this.subRule != null) {
            this.setSubRule(this.subRule);
        }
    }

    public boolean getIsNewReadingRule() {
        return isNewReadingRule;
    }

    @JsonDeserialize(as = ReadingRuleContext.class)
    private ReadingRuleInterface rule;

    public ReadingRuleInterface getRule() {
        return rule;
    }

    public void setRule(ReadingRuleInterface rule) throws Exception {
        if (rule == null) {
            return;
        }
        if (BooleanUtils.isTrue(isNewReadingRule)) {
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
        if(BooleanUtils.isTrue(isNewReadingRule)) {
                NewReadingRuleContext ruleContext = new NewReadingRuleContext();
                ruleContext.setId(subRule.getId());
                this.subRule = ruleContext;
            } else {
                this.subRule = subRule;
            }
    }

    private long readingFieldId;

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
    public Type getTypeEnum() {
        return Type.READING;
    }

    private Double energyImpact;

    private Double costImpact;

    private AssetCategoryContext readingAlarmAssetCategory;

}
