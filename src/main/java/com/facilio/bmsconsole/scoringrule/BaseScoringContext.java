package com.facilio.bmsconsole.scoringrule;

import com.facilio.modules.FacilioEnum;
import org.apache.commons.chain.Context;
import org.apache.http.MethodNotSupportedException;

import java.util.Map;

public class BaseScoringContext {
    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private float weightage;
    public float getWeightage() {
        return weightage;
    }
    public void setWeightage(float weightage) {
        this.weightage = weightage;
    }

    private Type type;
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public Type getTypeEnum() {
        return type;
    }
    public void setType(int type) {
        this.type = Type.valueOf(type);
    }
    public void setType(Type type) {
        this.type = type;
    }

    private long scoringRuleId = -1;
    public long getScoringRuleId() {
        return scoringRuleId;
    }
    public void setScoringRuleId(long scoringRuleId) {
        this.scoringRuleId = scoringRuleId;
    }

    public float getScore(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        float v = evaluatedScore(record, context, placeHolders);
        if (v < 0 || v > 1) {
            throw new IllegalArgumentException("Evaluated score cannot be other than the range of [0 - 1]");
        }
        return (v * (weightage / 100));
    }

    // should return always 0 - 1
    public float evaluatedScore(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        throw new MethodNotSupportedException("Sub-class didn't implemented this method");
    }

    public boolean isLeafNode() {
        return false;
    }

    public void validate() {
        if (weightage == -1) {
            throw new IllegalArgumentException("weightage cannot be empty");
        }
        if (weightage < 0 || weightage > 100) {
            throw new IllegalArgumentException("Weightage should be between 0 and 100");
        }
    }

    public void saveChildren() throws Exception {}

    public enum Type implements FacilioEnum {
        CONDITIONED("Conditioned"),
        NODE("Node");

        private String name;
        Type(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        public static Type valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return name;
        }
    }
}