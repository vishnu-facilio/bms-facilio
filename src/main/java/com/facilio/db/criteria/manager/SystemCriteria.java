package com.facilio.db.criteria.manager;

import com.facilio.chain.FacilioContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum SystemCriteria {
    TEST_CRITERIA(1) {
        @Override
        public boolean evaluate(Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
            return true;
        }
    };

    private int val;

    private SystemCriteria(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
    abstract public boolean evaluate(Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception;

    public static SystemCriteria getSystemCriteria(int systemCriteriId) {
        return TYPE_MAP.get(systemCriteriId);
    }

    private static final Map<Integer, SystemCriteria> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

    private static Map<Integer, SystemCriteria> initTypeMap() {
        Map<Integer, SystemCriteria> typeMap = new HashMap<>();
        for (SystemCriteria systemCriteria : values()) {
            typeMap.put(systemCriteria.getVal(), systemCriteria);
        }
        return typeMap;
    }
}
