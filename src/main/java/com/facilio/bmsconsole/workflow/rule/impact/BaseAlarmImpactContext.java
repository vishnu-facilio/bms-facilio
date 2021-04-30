package com.facilio.bmsconsole.workflow.rule.impact;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.MethodNotSupportedException;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, visible = true, property = "impactType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConstantImpactContext.class, name = "1"),
//        @JsonSubTypes.Type(value = NodeScoringContext.class, name = "2")
})
public class BaseAlarmImpactContext implements Serializable {

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private long categoryId = -1;
    public long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        this.category = null;
    }

    private AssetCategoryContext category;
    public AssetCategoryContext getCategory() throws Exception {
        if (category == null) {
            if (categoryId > 0) {
                category = (AssetCategoryContext) RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET_CATEGORY, categoryId);
            }
        }
        return category;
    }
    public void setCategory(AssetCategoryContext category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }

    private Boolean enabled;
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public Boolean isEnabled() {
        if (enabled == null) {
            return false;
        }
        return enabled;
    }

    private Type type;
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public void setType(int type) {
        this.type = Type.valueOf(type);
    }
    public Type getTypeEnum() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    private ImpactType impactType;
    public int getImpactType() {
        if (impactType != null) {
            return impactType.getIndex();
        }
        return -1;
    }
    public void setImpactType(int impactType) {
        this.impactType = ImpactType.valueOf(impactType);
    }
    public ImpactType getImpactTypeEnum() {
        return impactType;
    }
    public void setImpactType(ImpactType impactType) {
        this.impactType = impactType;
    }

    public void validate() throws Exception {
        // do all the validations here
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (category == null || category.getId() <= 0) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        if (getTypeEnum() == null) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        if (getImpactTypeEnum() == null) {
            throw new IllegalArgumentException("Impact type cannot be empty");
        }

        validateImpact();
    }

    protected void validateImpact() throws Exception {
        throw new MethodNotSupportedException("Sub-class didn't implemented this method");
    }

    public float calculateImpact() throws Exception {
        throw new MethodNotSupportedException("Sub-class didn't implemented this method");
    }

    public enum ImpactType implements FacilioIntEnum {
        CONSTANT("Constant"),
        ;

        private String name;

        ImpactType(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static ImpactType valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }

    public enum Type implements FacilioIntEnum {
        ELECTRICAL_WASTAGE("Electrical Wastage"),
        ;

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static Type valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }
}
