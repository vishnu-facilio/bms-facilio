package com.facilio.modules.fields;

import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.fw.BeanFactory;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.util.V3Util;
import com.facilio.beans.ModuleBean;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.criteria.operators.PickListOperators;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CurrencyFieldCRUDHandler extends BaseSingleRelRecordCRUDHandler<Map<String, Object>>{

    public CurrencyFieldCRUDHandler(CurrencyField field) {
        // TODO Auto-generated constructor stub
        super(field);
    }

    private List<Long> ids = new ArrayList<>();
    private Map<Long, Object> recordMap = null;
    public static final String FIELD_ID_FIELD_NAME = "fieldId";
    public static final String PARENT_FIELD_NAME = "parentId";
    private static Logger logger = Logger.getLogger(CurrencyFieldCRUDHandler.class.getName());

    @Override
    public void processRecord(Map<String, Object> record) {
        ids.add((Long) record.get("id"));
    }

    private Map<Long, Object> recordMap() {
        if (recordMap == null) {
            recordMap = new HashMap<>();
        }
        return recordMap;
    }

    @Override
    protected FacilioModule getRelModule() {
        FacilioModule recordModule = null;
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            recordModule = modBean.getModule(FacilioConstants.SystemLookup.CURRENCY_RECORD);
        } catch (Exception e) {
            logger.info("Exception occurred " + e);
        }
        return recordModule;
    }

    @Override
    protected boolean isNull(Map<String, Object> stringObjectMap) throws Exception {
        return MapUtils.isEmpty(stringObjectMap);
    }

    @Override
    public void fetchSupplements(boolean isMap) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> selectFields = modBean.getAllFields(getRelModule().getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
        FacilioField parentField = fieldMap.get(getParentFieldName());

        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> relBuilder = new SelectRecordsBuilder<>()
                .select(selectFields)
                .module(getRelModule())
                .andCondition(CriteriaAPI.getCondition(parentField, ids, PickListOperators.IS))
                ;

        fetchSupplements(isMap, relBuilder, selectFields);
    }

    @Override
    protected void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, List<FacilioField> relFields) throws Exception {
        relBuilder.andCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", String.valueOf(getField().getId()), StringOperators.IS));

        List<Map<String, Object>> props = relBuilder.getAsProps();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> record : props) {
                Long recordId = (Long) record.get(getParentFieldName());
                String currencyCode;
                if (record.get("currencyCode") != null) {
                    currencyCode = (String) record.get("currencyCode");
                } else {
                    currencyCode = (String) CurrencyUtil.getCurrencyInfo().get("currencyCode");
                    record.put("currencyCode", currencyCode);
                }
                Unit metricUnit = Unit.getUnitsForMetric(Metric.CURRENCY).stream()
                        .filter(unit -> unit.getDisplayName().equals(currencyCode))
                        .findFirst()
                        .orElse(null);
                record.put("displaySymbol", (metricUnit != null) ? metricUnit.getSymbol() : currencyCode);
                addToRecordMap(recordId, record);
            }
        }
    }

    @Override
    public void insertSupplements(List<Map<String, Object>> records) throws Exception {
        if (CollectionUtils.isNotEmpty(records)) {
            List<Map<String, Object>> rels = new ArrayList<>();
            for (Map<String, Object> record : records) {
                Map<String, Object> props = (Map<String, Object>) record.get(getFieldName());
                if (!isNull(props)) {
                    long parentId = (long) record.get("id");
                    Map<String, Object> relRecord = createRelRecord(parentId, props);
                    rels.add(relRecord);
                }
            }
            addData(rels);
        }
    }

    @Override
    public void updateSupplements(Map<String, Object> record, Collection<Long> ids, boolean ignoreSplNullHandling) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            Map<String, Object> relRecord = null;
            Map<String, Object> value = (Map<String, Object>) record.get(getFieldName());
            // Not updating anything if value is null and ignore null is false
            if (value != null) {
                List<Map<String, Object>> oldProps = getOldRelRecord(ids);
                for (Map<String, Object> props : oldProps) {
                    relRecord = updateRelRecord(props, value);
                    editData(relRecord);
                }
            }
        }
        // Deleting older data if value is null and ignore null is true
        else if (ignoreSplNullHandling) {
            deleteSupplements(ids);
        }
    }

    @Override
    protected Map<String, Object> createRelRecord(long parentId, Map<String, Object> props) throws Exception {
        String currencyCode = String.valueOf(props.get("currencyCode"));
        CurrencyContext baseCurrency = CurrencyUtil.getBaseCurrency();
        CurrencyContext currency  = CurrencyUtil.getCurrencyFromCode(currencyCode);

        props.put("currencyObject", currency);
        props.put("baseCurrency", baseCurrency);
        props.put(PARENT_FIELD_NAME, parentId);
        props.put(FIELD_ID_FIELD_NAME, getField().getFieldId());

        ((CurrencyField) getField()).validateRecord(props);

        if (baseCurrency == null) {
            props.put("currencyCode", null);
            props.put("exchangeRate", 1);
            return props;
        }

        int decimalPlaces = currency.getDecimalPlaces();
        int baseCurrencyDecimalPlaces = baseCurrency.getDecimalPlaces();

        double exchangeRate = currency.getExchangeRate();
        if (currencyCode.equals(baseCurrency.getCurrencyCode())) {
            exchangeRate = 1;
        }
        double currencyValue = Double.parseDouble((String) props.get("currencyValue"));
        double baseCurrencyValue = CurrencyUtil.getConvertedBaseCurrencyValue(currencyValue, exchangeRate);

        props.put("exchangeRate", roundOff(exchangeRate, 10));
        props.put("currencyValue", roundOff(currencyValue, decimalPlaces));
        props.put("baseCurrencyValue", roundOff(baseCurrencyValue, baseCurrencyDecimalPlaces));

        return props;
    }

    protected Map<String, Object> updateRelRecord(Map<String, Object> oldProps, Map<String, Object> newProps) throws Exception {
        String currencyCode = String.valueOf(newProps.get("currencyCode"));
        CurrencyContext baseCurrency = CurrencyUtil.getBaseCurrency();
        CurrencyContext currency  = CurrencyUtil.getCurrencyFromCode(currencyCode);

        newProps.put("currencyObject", currency);
        newProps.put("baseCurrency", baseCurrency);

        ((CurrencyField) getField()).validateRecord(newProps);

        if (baseCurrency == null) {
            oldProps.put("exchangeRate", 1);
            oldProps.put("currencyValue", newProps.get("currencyValue"));
            return oldProps;
        }

        boolean isSameCurrency = oldProps.get("currencyCode").equals(newProps.get("currencyCode"));
        V3Util.throwRestException(!isSameCurrency, ErrorCode.VALIDATION_ERROR, "Currency code cannot be changed");

        int decimalPlaces = currency.getDecimalPlaces();
        int baseCurrencyDecimalPlaces = baseCurrency.getDecimalPlaces();

        double currencyValue = Double.parseDouble((String) newProps.get("currencyValue"));
        double oldExchangeRate = (double) oldProps.get("exchangeRate");
        double newBaseCurrencyVale = currencyValue * oldExchangeRate;

        oldProps.put("currencyValue", roundOff(currencyValue, decimalPlaces));
        oldProps.put("baseCurrencyValue", roundOff(newBaseCurrencyVale, baseCurrencyDecimalPlaces));

        return oldProps;
    }

    @Override
    protected Criteria additionalCriteriaOtherThanParentId(List<FacilioField> fields) throws Exception {
        Criteria criteria = new Criteria();
        FacilioField fieldIdField = FieldFactory.getAsMap(fields).get(FIELD_ID_FIELD_NAME);
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(getField().getFieldId()), NumberOperators.EQUALS));
        return criteria;
    }

    private void addData(List<Map<String, Object>> rels) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());
        insertData(rels, fields);
    }

    private void editData(Map<String, Object> rels) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());
        updateData(rels, fields);
    }

    protected void updateData(Map<String, Object> rels, List<FacilioField> fields) throws Exception {
        if (MapUtils.isNotEmpty(rels)) {
            new UpdateRecordBuilder()
                    .fields(fields)
                    .module(getRelModule())
                    .andCondition(CriteriaAPI.getIdCondition((Long) rels.get("id"), getRelModule()))
                    .updateViaMap(rels);
        }
    }

    protected void addToRecordMap (long recordId, Object value) {
        Map<Long, Object> recordMap = recordMap();
        recordMap.put(recordId, value);
    }

    @Override
    public void updateRecordWithSupplement(Map<String, Object> record) {
        if (MapUtils.isNotEmpty(recordMap)) {
            Long recordId = (Long) record.get("id");
            Object value = recordMap.get(recordId);
            record.put(getFieldName(), value);
        }
    }

    protected List<Map<String, Object>> getOldRelRecord(Collection<Long> ids) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> selectFields = modBean.getAllFields(getRelModule().getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
        FacilioField parentField = fieldMap.get(getParentFieldName());

        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> relBuilder = new SelectRecordsBuilder<>()
                .select(selectFields)
                .module(getRelModule())
                .andCondition(CriteriaAPI.getCondition(parentField, ids, PickListOperators.IS))
                .andCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", String.valueOf(getField().getId()), StringOperators.IS))
                ;

        List<Map<String, Object>> props = relBuilder.getAsProps();
        if (CollectionUtils.isNotEmpty(props)) {
            return props;
        } else {
            throw new IllegalArgumentException("Currency Record not found");
        }
    }

    public static double roundOff(double value, int decimalDigits)
        {
            double multiplier = Math.pow(10, decimalDigits);
            return Math.round(value * multiplier) / multiplier ;
        }
}
