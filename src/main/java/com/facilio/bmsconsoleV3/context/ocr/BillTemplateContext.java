package com.facilio.bmsconsoleV3.context.ocr;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BillTemplateContext extends OCRTemplateContext {
	
	public BillTemplateContext(Long id) {
		this.setId(id);
	}

    private List<Integer> utilityType;
    private BillFrequencyEnum billFrequency;
    private UtilityProviderEnum utilityProvider;
    private Integer startMonth;
    
    public Integer getBillFrequency() {
        if (billFrequency == null) {
            return null;
        }
        return billFrequency.getIndex();
    }
    public void setBillFrequency(Integer billFrequency) {
        if (billFrequency != null) {
            this.billFrequency = BillFrequencyEnum.valueOf(billFrequency);
        } else {
            this.billFrequency = null;
        }
    }
    @AllArgsConstructor
    public static enum BillFrequencyEnum implements FacilioIntEnum {
        MONTHLY ("Monthly"),
        BIMONTHLY("Bi-Monthly"),
        QUARTERLY("Quarterly"),
        HALFYEARLY("Half Yearly"),
        ANNUALLY("Yearly");
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final BillFrequencyEnum[] BILL_FREQUENCY = BillFrequencyEnum.values();
        public static BillFrequencyEnum valueOf(int type) {
            if (type > 0 && type <= BILL_FREQUENCY.length) {
                return BILL_FREQUENCY[type - 1];
            }
            return null;
        }
    }

    public Integer getUtilityProvider() {
        if (utilityProvider == null) {
            return null;
        }
        return utilityProvider.getIndex();
    }
    public void setUtilityProvider(Integer utilityProvider) {
        if (utilityProvider != null) {
            this.utilityProvider = UtilityProviderEnum.valueOf(utilityProvider);
        } else {
            this.utilityProvider = null;
        }
    }
    @AllArgsConstructor
    public static enum UtilityProviderEnum implements FacilioIntEnum {
        DEWA ("DEWA"),
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final UtilityProviderEnum[] UTILITY_PROVIDER = UtilityProviderEnum.values();
        public static UtilityProviderEnum valueOf(int type) {
            if (type > 0 && type <= UTILITY_PROVIDER.length) {
                return UTILITY_PROVIDER[type - 1];
            }
            return null;
        }
    }

}
