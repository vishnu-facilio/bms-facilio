package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.Calendar;
import java.util.List;

public class AssetDepreciationContext extends ModuleBaseWithCustomFields {

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private DepreciationType depreciationType;
    public int getDepreciationType() {
        if (depreciationType != null) {
            return depreciationType.getIndex();
        }
        return -1;
    }
    public void setDepreciationType(int depreciationTypeInt) {
        this.depreciationType = DepreciationType.valueOf(depreciationTypeInt);
    }
    public DepreciationType getDepreciationTypeEnum() {
        return depreciationType;
    }
    public void setDepreciationType(DepreciationType depreciationType) {
        this.depreciationType = depreciationType;
    }

    private List<AssetDepreciationRelContext> assetDepreciationRelList;
    public List<AssetDepreciationRelContext> getAssetDepreciationRelList() {
        return assetDepreciationRelList;
    }
    public void setAssetDepreciationRelList(List<AssetDepreciationRelContext> assetDepreciationRelList) {
        this.assetDepreciationRelList = assetDepreciationRelList;
    }

    private int frequency = -1;
    public int getFrequency() {
        return frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    private FrequencyType frequencyType;
    public int getFrequencyType() {
        if (frequencyType != null) {
            return frequencyType.getIndex();
        }
        return -1;
    }
    public FrequencyType getFrequencyTypeEnum() {
        return frequencyType;
    }
    public void setFrequencyType(int frequencyTypeInt) {
        this.frequencyType = FrequencyType.valueOf(frequencyTypeInt);
    }
    public void setFrequencyType(FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }

    private long totalPriceFieldId = -1;
    public long getTotalPriceFieldId() {
        return totalPriceFieldId;
    }
    public void setTotalPriceFieldId(long totalPriceFieldId) {
        this.totalPriceFieldId = totalPriceFieldId;
    }

    private long salvagePriceFieldId = -1;
    public long getSalvagePriceFieldId() {
        return salvagePriceFieldId;
    }
    public void setSalvagePriceFieldId(long salvagePriceFieldId) {
        this.salvagePriceFieldId = salvagePriceFieldId;
    }

    private long currentPriceFieldId = -1;
    public long getCurrentPriceFieldId() {
        return currentPriceFieldId;
    }
    public void setCurrentPriceFieldId(long currentPriceFieldId) {
        this.currentPriceFieldId = currentPriceFieldId;
    }

    private long startDateFieldId = -1;
    public long getStartDateFieldId() {
        return startDateFieldId;
    }
    public void setStartDateFieldId(long startDateFieldId) {
        this.startDateFieldId = startDateFieldId;
    }

    public Boolean active;
    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public Boolean isActive() {
        if (active != null) {
            return active;
        }
        return false;
    }

    public long nextDate(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        switch (frequencyType) {
            case MONTHLY:
                calendar.add(Calendar.MONTH, 1);
                return calendar.getTimeInMillis();

            case QUARTERLY:
                calendar.add(Calendar.MONTH, 3);
                return calendar.getTimeInMillis();

            case HALF_YEARLY:
                calendar.add(Calendar.MONTH, 6);
                return calendar.getTimeInMillis();

            case YEARLY:
                calendar.add(Calendar.YEAR, 1);
                return calendar.getTimeInMillis();

            default:
                throw new IllegalArgumentException("Invalid frequency type");
        }
    }

    public interface Frequency {

    }

    public enum DepreciationType implements FacilioEnum {
        SINGLE {
            @Override
            public float nextDepreciatedUnitPrice(float totalDepreciationAmount, int frequency, float currentPrice) {
                float depreciatedAmount = totalDepreciationAmount / frequency;
                float newPrice = currentPrice - depreciatedAmount;
                if (newPrice < depreciatedAmount) {
                    return 0;
                }
                return newPrice;
            }
        },
        DOUBLE {
            @Override
            public float nextDepreciatedUnitPrice(float totalDepreciationAmount, int frequency, float currentPrice) {
                float newPrice = currentPrice - ((currentPrice / frequency) * 2);
                if (newPrice < 0) {
                    newPrice = 0;
                }
                return newPrice;
            }
        }
        ;

        public static DepreciationType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        public abstract float nextDepreciatedUnitPrice(float totalDepreciationAmount, int frequency, float currentPrice);

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return null;
        }
    }

    public enum FrequencyType implements FacilioEnum {
        MONTHLY,
        QUARTERLY,
        HALF_YEARLY,
        YEARLY
        ;

        public static FrequencyType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return null;
        }
    }
}
