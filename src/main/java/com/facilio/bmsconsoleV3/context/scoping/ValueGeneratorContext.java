package com.facilio.bmsconsoleV3.context.scoping;

import com.facilio.annotations.ImmutableChildClass;
import com.facilio.bmsconsole.context.IconType;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.modules.ValueGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@ImmutableChildClass(className = "ValueGeneratorCacheContext")
@NoArgsConstructor
public class ValueGeneratorContext implements Serializable {
    private long id = -1L;
    private String specialModuleName;
    private Long moduleId;
    private String linkName;
    private String displayName;
    private Boolean isConstant;
    private Boolean isHidden;
    private Boolean isSystem;
    private Integer operatorId;

    public ValueGeneratorContext(ValueGeneratorContext object) {
        this.id = object.id;
        this.specialModuleName = object.specialModuleName;
        this.moduleId = object.moduleId;
        this.linkName = object.linkName;
        this.displayName = object.displayName;
        this.isConstant = object.isConstant;
        this.isHidden = object.isHidden;
        this.isSystem = object.isSystem;
        this.operatorId = object.operatorId;
        this.valueGeneratorType = object.valueGeneratorType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getSpecialModuleName() {
        return specialModuleName;
    }

    public void setSpecialModuleName(String specialModuleName) {
        this.specialModuleName = specialModuleName;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getIsConstant() {
        return isConstant;
    }

    public void setIsConstant(Boolean constant) {
        isConstant = constant;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean system) {
        isSystem = system;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    private ValueGeneratorType valueGeneratorType;

    @Getter
    public enum ValueGeneratorType implements FacilioStringEnum {
        IDENTIFIER,
        SUB_QUERY;
    }
}