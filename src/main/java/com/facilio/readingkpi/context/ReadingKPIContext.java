package com.facilio.readingkpi.context;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.connected.IConnectedRule;
import com.facilio.connected.ResourceCategory;
import com.facilio.connected.ResourceType;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NamespaceFrequency;
import com.facilio.readingkpi.ReadingKpiLoggerAPI;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ReadingKPIContext extends V3Context implements IConnectedRule {

    public ReadingKPIContext(Long id, Long readingFieldId, NameSpaceContext ns) {
        this.setId(id);
        this.setReadingFieldId(readingFieldId);
        this.setNs(ns);
    }

    private String name;
    private String description;
    private Long kpiCategory;
    private NamespaceFrequency frequency;
    private KPIType kpiType;
    private String customUnit;
    private Integer metricId;
    private Integer unitId;
    private FacilioField readingField;
    private FacilioModule readingModule;

    private Long readingFieldId = -1L;
    private Long readingModuleId = -1l;

    private ResourceType resourceType = ResourceType.ASSET_CATEGORY;
    private AssetCategoryContext assetCategory;
    private Long assetCategoryId = -1l;
    private SpaceCategoryContext spaceCategory;
    private Long spaceCategoryId = -1l;
    private Boolean status;
    private NameSpaceContext ns;
    private String firstAssetName;
    private List<Long> assets;

    private List<Long> matchedResourcesIds;

    Long categoryId;

    ResourceCategory<? extends V3Context> category;

    String linkName;

    public ReadingKPIContext(String name, Long readingFieldId, KPIType kpiType) {
        this.name = name;
        this.readingFieldId = readingFieldId;
        this.kpiType = kpiType;
    }

    public Boolean getStatus() {
        return status != null ? status : Boolean.FALSE;
    }
    public ResourceType getResourceTypeEnum() {
        return resourceType;
    }

    public int getResourceType() {
        if (resourceType != null) {
            return resourceType.getIndex();
        }
        return ResourceType.ASSET_CATEGORY.getIndex();
    }

    public void setResourceType(Integer type) {
        this.resourceType = ResourceType.valueOf(type);
        if(this.resourceType == null || type == PreventiveMaintenance.PMAssignmentType.ASSET_CATEGORY.getIndex()) { //TODO: added this check for backward compatability. should be del this block.
            this.resourceType = ResourceType.ASSET_CATEGORY;
        }
    }

    public KPIType getKpiTypeEnum() {
        return this.kpiType;
    }

    public int getKpiType() {
        return (kpiType != null) ? kpiType.getIndex() : -1;
    }

    public void setKpiType(Integer type) {
        this.kpiType = KPIType.valueOf(type);
    }

    public NamespaceFrequency getFrequencyEnum() {
        return this.frequency;
    }

    public void setFrequency(Integer type) {
        this.frequency = NamespaceFrequency.valueOf(type);
    }

    public int getFrequency() {
        return frequency != null ? frequency.getIndex() : -1;
    }

    public Unit getUnit() {
        Unit inputUnit = null;
        if (this.getReadingField() instanceof NumberField) {
            NumberField numberfield = (NumberField) this.getReadingField();
            inputUnit = FormulaFieldAPI.getOrgDisplayUnit(numberfield);
        }
        return inputUnit;
    }

    @Override
    public long insertLog(Long startTime, Long endTime, Integer resourceCount, boolean isSysCreated) throws Exception {
        return ReadingKpiLoggerAPI.insertLog(getId(), getKpiType(), startTime, endTime, isSysCreated, resourceCount == null ? getMatchedResourcesIds().size() : resourceCount);
    }

    public String getUnitLabel(){
        Unit sysUnit = this.unitId != null && this.unitId > 0 ? Unit.valueOf(this.unitId) : null;
        return sysUnit == null ? this.getCustomUnit() : sysUnit.getSymbol();
    }
}
