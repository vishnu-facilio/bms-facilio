package com.facilio.relation.context;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class RelationContext implements Serializable {

    private long id = -1;
    private long orgId = -1;
    private String name;
    private String linkName;
    private String description;
    private Boolean isCustom;

    private long relationModuleId = -1;
    public FacilioModule getRelationModule() throws Exception {
        if (relationModuleId > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            return modBean.getModule(relationModuleId);
        }
        return null;
    }

    @Setter(value = AccessLevel.NONE)
    private RelationMappingContext mapping1;
    @Setter(value = AccessLevel.NONE)
    private RelationMappingContext mapping2;
    public void addMapping(RelationMappingContext mapping) {
        if (mapping1 == null) {
            mapping1 = mapping;
        } else if (mapping2 == null) {
            mapping2 = mapping;
        } else {
            throw new IllegalArgumentException("You cannot add more than 2 mappings");
        }
    }

    @JsonIgnore
    public RelationMappingContext getMapping1() {
        return mapping1;
    }
    @JsonIgnore
    public RelationMappingContext getMapping2() {
        return mapping2;
    }
    @JsonIgnore
    public List<RelationMappingContext> getMappings() {
        if (mapping1 == null || mapping2 == null) {
            throw new IllegalArgumentException("Two mappings to be added");
        }
        return Arrays.asList(mapping1, mapping2);
    }
    public enum RelationCategory implements FacilioIntEnum {
        NORMAL("normal"),
        HIDDEN("hidden"),
        METER("meter"),
        VIRTUAL("virtual"),
        ;

        RelationCategory(String name) {
        }
        public static RelationCategory valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }
    private RelationCategory relationCategory;
    public void setRelationCategory(RelationCategory relationCategory) {
        this.relationCategory = relationCategory;
    }
    public RelationCategory getRelationCategoryEnum() {
        return relationCategory;
    }
    public void setRelationCategory(int val) {
        this.relationCategory = RelationCategory.valueOf(val);
    }
    public int getRelationCategory() {
        if (relationCategory != null) {
            return relationCategory.getIndex();
        }
        return -1;
    }

    public Boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }
    public boolean isCustom() {
        return this.isCustom != null ? this.isCustom : true;
    }

}
