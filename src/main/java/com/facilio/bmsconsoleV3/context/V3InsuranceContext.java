package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;


import java.io.File;

public class V3InsuranceContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private String companyName;
    private Long validFrom;
    private Long validTill;
    private VendorContext vendor;
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String insuranceCompany) {
        this.companyName = insuranceCompany;
    }
    public Long getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(Long validFrom) {
        this.validFrom = validFrom;
    }
    public Long getValidTill() {
        return validTill;
    }
    public void setValidTill(Long validTill) {
        this.validTill = validTill;
    }
    public VendorContext getVendor() {
        return vendor;
    }
    public void setVendor(VendorContext vendor) {
        this.vendor = vendor;
    }

    private V3InsuranceContext.InsuranceType insuranceType;
    public int getInsuranceType() {
        if (insuranceType != null) {
            return insuranceType.getIndex();
        }
        return -1;
    }
    public void setInsuranceType(int insuranceType) {
        this.insuranceType = V3InsuranceContext.InsuranceType.valueOf(insuranceType);
    }
    public V3InsuranceContext.InsuranceType getInsuranceTypeEnum() {
        return insuranceType;
    }
    public void setInsuranceType(V3InsuranceContext.InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
    }

    public static enum InsuranceType implements FacilioEnum {
        TENANT, VENDOR;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static V3InsuranceContext.InsuranceType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private Long insuranceId;
    public Long getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(Long insuranceId) {
        this.insuranceId = insuranceId;
    }

    private String insuranceUrl;

    public String getInsuranceUrl() {
        return insuranceUrl;
    }

    public void setInsuranceUrl(String insuranceUrl) {
        this.insuranceUrl = insuranceUrl;
    }

    private File insurance;
    public File getInsurance() {
        return insurance;
    }

    public void setInsurance(File insurance) {
        this.insurance = insurance;
    }

    private String insuranceFileName;
    public String getInsuranceFileName() {
        return insuranceFileName;
    }

    public void setInsuranceFileName(String insuranceFileName) {
        this.insuranceFileName = insuranceFileName;
    }

    private  String insuranceContentType;

    public String getInsuranceContentType() {
        return insuranceContentType;
    }

    public void setInsuranceContentType(String insuranceContentType) {
        this.insuranceContentType = insuranceContentType;
    }

    private User addedBy;
    public User getAddedBy() {
        return addedBy;
    }
    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }


}
