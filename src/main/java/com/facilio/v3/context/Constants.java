package com.facilio.v3.context;

import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.List;

public class Constants {
    public static final String FILTERS = "filters";
    public static final String RECORD_MAP = "recordMap";
    public static final String RAW_INPUT = "rawInput";
    public static final String EXCLUDE_PARENT_CRITERIA = "excludeParentCriteria";
    public static final String FILTER_CRITERIA = "filterCriteria";
    public static final String BEFORE_FETCH_CRITERIA = "beforeFetchCriteria";
    public static final String MODULE_BEAN = "moduleBean";
    public static final String RECORD_ID = "recordId";
    public static final String WITH_COUNT = "withCount";
    public static final String COUNT = "count";
    public static final String BEAN_CLASS = "beanClass";
    public static final String ROWS_UPDATED = "rowsUpdated";
    public static final String PATCH_FIELDS = "patchFields";
    public static final String QUERY_PARAMS = "queryParams";
    public static final String STATE_FLOWS = "stateFlows";
    public static final String SUPPLEMENT_MAP = "supplementMap";

    public static final String MODULE_NAME = "moduleName";
    public static String getModuleName(Context context) {
        return (String) context.get(MODULE_NAME);
    }

    public static void setModuleName(Context context, String moduleName) {
        context.put(MODULE_NAME, moduleName);
    }

    private static final String FETCH_SUPPLEMENTS = "fetchSupplement";
    public static List<LookupField> getFetchSupplements(Context context) {
       return (List<LookupField>) context.get(FETCH_SUPPLEMENTS);
    }

    public static void setFetchSupplements(Context context, List<LookupField> supplements) {
        context.put(FETCH_SUPPLEMENTS, supplements);
    }

}
