package com.facilio.relation.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RelationRequestContext implements Serializable {

    private long id = -1;
    private String name;
    private String description;
    private String linkName;
    private long fromModuleId = -1;
    private long toModuleId = -1;
    private String fromModuleName;
    private FacilioModule fromModule;
    private String toModuleName;
    private FacilioModule toModule;
    private String relationName;
    private long relMappingId;
    private String forwardRelationLinkName;
    private String reverseRelationName;
    private String reverseRelationLinkName;
    private FacilioModule relationModule;
    private RelationMappingContext.Position position;
    private String reversePositionFieldName;
    private RelationContext.RelationCategory relationCategory;
    private Boolean isCustom;
    public Boolean getIsCustom() {
        return this.isCustom;
    }
    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }
    public boolean isCustom() {
        return this.isCustom != null ? this.isCustom : true;
    }
    public void setRelationCategory(RelationContext.RelationCategory relationCategory) {
        this.relationCategory = relationCategory;
    }
    public RelationContext.RelationCategory getRelationCategoryEnum() {
        return relationCategory;
    }
    public void setRelationCategory(int val) {
        this.relationCategory = RelationContext.RelationCategory.valueOf(val);
    }
    public int getRelationCategory() {
        if (relationCategory != null) {
            return relationCategory.getIndex();
        }
        return -1;
    }
    public int getPosition() {
        if (position != null) {
            return position.getIndex();
        }
        return -1;
    }
    public void setPosition(int position) {
        this.position = RelationMappingContext.Position.valueOf(position);
    }
    public void setPosition(RelationMappingContext.Position position) {
        this.position = position;
    }

    private RelationType relationType;
    public int getRelationType() {
        if (relationType != null) {
            return relationType.getIndex();
        }
        return -1;
    }
    public RelationType getRelationTypeEnum() {
        return relationType;
    }
    public void setRelationType(int relationType) {
        this.relationType = RelationType.valueOf(relationType);
    }
    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public enum RelationType implements FacilioIntEnum {
        ONE_TO_ONE (1),
        ONE_TO_MANY (3),
        MANY_TO_ONE (2),
        MANY_TO_MANY (4);

        private int reverseRelationType;
        RelationType(int reverseRelationType) {
            this.reverseRelationType = reverseRelationType;
        }

        public RelationType getReverseRelationType() {
            return valueOf(reverseRelationType);
        }

        public static RelationType valueOf(int relationType) {
            if (relationType > 0 && relationType <= values().length) {
                return RelationType.values()[relationType - 1];
            }
            return null;
        }
    }
}
