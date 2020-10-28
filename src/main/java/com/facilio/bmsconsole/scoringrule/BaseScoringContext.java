package com.facilio.bmsconsole.scoringrule;

import com.facilio.modules.FacilioEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.chain.Context;
import org.apache.http.MethodNotSupportedException;
import org.apache.struts2.json.annotations.JSON;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, visible = true, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConditionScoringContext.class, name = "1"),
        @JsonSubTypes.Type(value = NodeScoringContext.class, name = "2")
})
public class BaseScoringContext implements Serializable {
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

    private long scoringCommitmentId = -1;
    public long getScoringCommitmentId() {
        return scoringCommitmentId;
    }
    public void setScoringCommitmentId(long scoringCommitmentId) {
        this.scoringCommitmentId = scoringCommitmentId;
    }

    public String getScoreType() {
        return null;
    }

    @JsonIgnore
    private boolean dirty = true;
    @JSON(serialize = false)
    public boolean isDirty() {
        return dirty;
    }
    @JSON(deserialize = false)
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @JsonIgnore
    private float score = 0f;
    @JSON(serialize = false)
    public float getScore() {
        return score;
    }
    @JSON(deserialize = false)
    public void setScore(float score) {
        this.score = score;
    }

    public float getScore(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        if (isDirty()) {
            float v = evaluatedScore(record, context, placeHolders);
            if (v < 0 || v > 1) {
                throw new IllegalArgumentException("Evaluated score cannot be other than the range of [0 - 1]");
            }
            score = v * (weightage / 100);
            setDirty(false);
        }
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseScoringContext that = (BaseScoringContext) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    public boolean shouldUpdateParent() {
        return false;
    }

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