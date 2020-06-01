package com.facilio.bmsconsole.context;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.time.DateTimeUtil;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class AggregationMetaContext implements Serializable {

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private FacilioModule storageModule;
    public FacilioModule getStorageModule() {
        return storageModule;
    }
    public void setStorageModule(FacilioModule storageModule) {
        this.storageModule = storageModule;
    }

    private Long storageModuleId;
    public Long getStorageModuleId() {
        return storageModuleId;
    }
    public void setStorageModuleId(Long storageModuleId) {
        this.storageModuleId = storageModuleId;
    }

    private Long criteriaId;
    public Long getCriteriaId() {
        return criteriaId;
    }
    public void setCriteriaId(Long criteriaId) {
        this.criteriaId = criteriaId;
    }

    private Criteria criteria;
    public Criteria getCriteria() {
        return criteria;
    }
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    private List<AggregationColumnMetaContext> columnList;
    public List<AggregationColumnMetaContext> getColumnList() {
        return columnList;
    }
    public void setColumnList(List<AggregationColumnMetaContext> columnList) {
        this.columnList = columnList;
    }

    private FrequencyType frequencyType;
    public Integer getFrequencyType() {
        if (frequencyType == null) {
            return null;
        }
        return frequencyType.getIndex();
    }
    public void setFrequencyType(Integer frequencyTypeInt) {
        if (frequencyTypeInt != null) {
            frequencyType = FrequencyType.valueOf(frequencyTypeInt);
        }
    }
    public FrequencyType getFrequencyTypeEnum() {
        return frequencyType;
    }
    public void setFrequencyTypeEnum(FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }

    private Long interval;
    public Long getInterval() {
        return interval;
    }
    public void setInterval(Long interval) {
        this.interval = interval;
    }

    private Long lastSync;
    public Long getLastSync() {
        return lastSync;
    }
    public void setLastSync(Long lastSync) {
        this.lastSync = lastSync;
    }

    public enum FrequencyType implements FacilioEnum {
        HOURLY("Hourly", BmsAggregateOperators.DateAggregateOperator.HOURSOFDAYONLY.getValue()) {
            @Override
            public long getAggregatedTime(Long ttime) {
                ZonedDateTime zonedDateTime = DateTimeUtil.getZonedDateTime(ttime);
                zonedDateTime = zonedDateTime.withMinute(0)
                        .withSecond(0);
                return DateTimeUtil.getMillis(zonedDateTime, true);
            }

            @Override
            public long getNextSyncTime(Long lastSync) {
                return lastSync + (60 * 60 * 1000);
            }
        };

        private String name;
        private Integer aggregateOperatorInt;
        FrequencyType (String name, Integer aggregateOperator) {
            this.name = name;
            this.aggregateOperatorInt = aggregateOperator;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public BmsAggregateOperators.DateAggregateOperator getAggregateOperator() {
            return (BmsAggregateOperators.DateAggregateOperator) AggregateOperator.getAggregateOperator(aggregateOperatorInt);
        }

        public static FrequencyType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        public abstract long getAggregatedTime(Long ttime);

        public abstract long getNextSyncTime(Long lastSync);
    }
}
