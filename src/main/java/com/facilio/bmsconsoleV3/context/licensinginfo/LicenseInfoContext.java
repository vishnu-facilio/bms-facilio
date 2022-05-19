package com.facilio.bmsconsoleV3.context.licensinginfo;


import com.facilio.modules.FacilioIntEnum;


import java.io.Serializable;

import java.util.HashMap;

import java.util.List;

import java.util.Map;


public class LicenseInfoContext implements Serializable {
    private Long orgId;
    private String name;
    private Long allowedLicense;
    private Long usedLicense;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    private Long id;
    private LicensingType licensingType;
    public int getLicensingType() {
        return this.licensingType != null ? this.licensingType.getIndex() : -1;
    }
    public void setLicensingType(int licensingType) {
        this.licensingType = LicensingType.valueOf(licensingType);
    }
    public LicensingType getLicensingTypeEnum() {
        return this.licensingType;
    }
    public void setLicensingType(LicensingType licensingType) {
        this.licensingType = licensingType;
    }
    public static enum LicensingType implements FacilioIntEnum {
        FEATURE,MODULE,API;
        private LicensingType() {
        }
        public Integer getIndex() {
            return this.ordinal() + 1;
        }
        public String getValue() {
            return this.name();
        }
        public static LicensingType valueOf(int value) {
            return value > 0 && value <= values().length ? values()[value - 1] : null;
        }
    }
    public Long getOrgId() {
        return orgId;
    }
    public void setOrgId(Long ouId) {
        this.orgId = orgId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getAllowedLicense() {
        return allowedLicense;
    }
    public void setAllowedLicense(Long allowedLicense) {
        this.allowedLicense = allowedLicense;
    }
    public Long getUsedLicense() {
        return usedLicense;
    }
    public void setUsedLicense(Long usedLicense) {
        this.usedLicense = usedLicense;
    }
    private AggregatingPeriod aggregatingPeriod;
    public int getAggregatingPeriod() {
        if (aggregatingPeriod != null) {
            return aggregatingPeriod.getIndex();
        }
        return -1;
    }
    public void setAggregatingPeriod(int aggregatingPeriod) {
        this.aggregatingPeriod = AggregatingPeriod.valueOf(aggregatingPeriod);
    }
    public AggregatingPeriod getAggregatingPeriodEnum() {
        return aggregatingPeriod;
    }
    public void setLicensingType(AggregatingPeriod aggregatingPeriod) {
        this.aggregatingPeriod = aggregatingPeriod;
    }

    public static enum AggregatingPeriod implements FacilioIntEnum {
        ANNUALLY,MONTHLY,LIFETIME,WEEKLY,DAILY;
        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }
        @Override
        public String getValue() {
            return name();
        }
        public static AggregatingPeriod valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public List<Long> getLicenseInfoIds() {
        return licenseInfoIds;
    }
    public void setLicenseInfoIds(List<Long> licenseInfoIds) {
        this.licenseInfoIds = licenseInfoIds;
    }
    private List<Long> licenseInfoIds;
    public Long getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(Long validFrom) {
        this.validFrom = validFrom;
    }

    private Long validFrom;
    public Long getValidTill() {
        return validTill;
    }

    public void setValidTill(Long validTill) {
        this.validTill = validTill;
    }
    private Long validTill;
}