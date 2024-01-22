package com.facilio.readingkpi.context;

import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.stream.Collectors;

//wrapper class for analytics
@Getter
@Setter
public class KpiContextWrapper {
    private Long id;
    private String name;
    private FacilioFieldKpiWrapper readingField;
    private KPIType kpiType;
    private Long categoryId;
    private Unit unit;

    public KpiContextWrapper(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public KpiContextWrapper(ReadingKPIContext kpi) throws Exception {
        this.id = kpi.getId();
        this.name = kpi.getName();
        this.kpiType = kpi.getKpiTypeEnum();
        this.categoryId = kpi.getCategoryId();
        if (kpiType != KPIType.DYNAMIC) {
            this.readingField = new FacilioFieldKpiWrapper(Constants.getModBean().getField(kpi.getReadingFieldId()));
        }
    }
    public KpiContextWrapper(ReadingKPIContext kpi, boolean isFromAnalytics) throws Exception {
        this.id = kpi.getId();
        this.name = kpi.getName();
        this.kpiType = kpi.getKpiTypeEnum();
        this.categoryId = kpi.getCategoryId();
        if (kpiType != KPIType.DYNAMIC) {
            this.readingField = new FacilioFieldKpiWrapper(Constants.getModBean().getField(kpi.getReadingFieldId()));
            if(isFromAnalytics && kpi.getUnitLabel() != null && !"".equals(kpi.getUnitLabel())){
                this.readingField.setUnit(kpi.getUnitLabel());
            }
        }
        if(isFromAnalytics && kpiType == KPIType.DYNAMIC && kpi.getUnitId() != null && kpi.getUnitId() > 0)
        {
            this.unit = Unit.valueOf(kpi.getUnitId());
        }
    }

}
