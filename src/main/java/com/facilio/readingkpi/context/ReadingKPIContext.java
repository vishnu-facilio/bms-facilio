package com.facilio.readingkpi.context;

import java.util.List;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsoleV3.signup.AddKPIModules;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.V3Context;
import lombok.Data;


@Data
public class ReadingKPIContext extends V3Context {

    private String name;
    private String description;
    private Long kpiCategory;
    private Long frequency;
    private KPIType kpiType;
    private String customUnit;
    private Integer metricId;
    private Integer unitId;
    private FacilioField readingField;
    private FacilioModule readingModule;

    private Long readingFieldId = -1l;
    private Long readingModuleId = -1l;

    private PreventiveMaintenance.PMAssignmentType resourceType;
    private AssetCategoryContext assetCategory;
    private Long assetCategoryId = -1l;
    private SpaceCategoryContext spaceCategory;
    private Long spaceCategoryId = -1l;
    private Boolean status;
    private NameSpaceContext ns;
    private String firstAssetName;
    private List<Long> assets;

    public PreventiveMaintenance.PMAssignmentType getResourceTypeEnum() {
        return resourceType;
    }

    public int getResourceType() {
        if (resourceType != null) {
            return resourceType.getVal();
        }
        return -1;
    }

    public void setResourceType(Integer type) {
        this.resourceType = PreventiveMaintenance.PMAssignmentType.valueOf(type);
    }

    public KPIType getKpiTypeEnum() {
        return this.kpiType;
    }

    public int getKpiType() {
        if (kpiType != null) {
            return kpiType.ordinal() + 1;
        }
        return -1;
    }

    public void setKpiType(Integer type) {
        this.kpiType = KPIType.valueOf(type);
    }

}
