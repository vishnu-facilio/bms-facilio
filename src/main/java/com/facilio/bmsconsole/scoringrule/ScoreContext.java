package com.facilio.bmsconsole.scoringrule;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ScoreContext extends ModuleBaseWithCustomFields {

    private ModuleBaseWithCustomFields parent;
    public ModuleBaseWithCustomFields getParent() {
        return parent;
    }
    public void setParent(ModuleBaseWithCustomFields parent) {
        this.parent = parent;
    }

    private float score = -1;
    public float getScore() {
        return score;
    }
    public void setScore(float score) {
        this.score = score;
    }

    private long scoreRuleId = -1;
    public long getScoreRuleId() {
        return scoreRuleId;
    }
    public void setScoreRuleId(long scoreRuleId) {
        this.scoreRuleId = scoreRuleId;
    }

    private long createdTime = -1;
    public long getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
