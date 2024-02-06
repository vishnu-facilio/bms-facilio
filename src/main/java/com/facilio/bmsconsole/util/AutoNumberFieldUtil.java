package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.AutoNumberField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoNumberFieldUtil {

    private static final Logger LOGGER = LogManager.getLogger(AutoNumberFieldUtil.class.getName());
    protected static final Object LOCK = new Object();

    public static int getAndUpdateFieldAutoNumberIdProps(AutoNumberField autoNumberField, int currentSize) throws Exception {

        if (currentSize <= 0) {
            throw new IllegalArgumentException("Invalid current id size for fetching auto number last id.");
        }

        int autoNumberIdValue = 0;
        synchronized (LOCK) {

            Connection conn = null;
            boolean olderCommit = false;

            try {

                conn = FacilioConnectionPool.getInstance().getDirectConnection();//Getting connection directly from pool because this should be done outside transaction. Should be used with caution
                olderCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);

                AutoNumberField field = getAutoNumberIdValue(autoNumberField, conn);
                autoNumberIdValue = field.getLastAutoNumberId();

                updateAutoNumberIdValue(autoNumberField, autoNumberIdValue + currentSize, conn);
                conn.commit();

                return autoNumberIdValue;

            } catch (Exception e) {

                if (conn != null) {
                    conn.rollback();
                    LOGGER.debug("Rolled back connection while getting auto number id value for field : " + autoNumberField.getFieldId());
                }

            } finally {
                if (conn != null) {
                    conn.setAutoCommit(olderCommit);
                }
                DBUtil.close(conn);
            }
        }

        return 0;
    }

    public static int updateAutoNumberIdValue(AutoNumberField autoNumberField, int lastAutoNumberId, Connection conn) throws Exception {

        FacilioModule autoNumberFieldsModule = ModuleFactory.getAutoNumberFieldModule();
        FacilioField lastAutoNumberIdField = FieldFactory.getAutoNumberField(autoNumberFieldsModule);

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .useExternalConnection(conn) //This connection will not be closed by builder. Use this with caution
                .table(autoNumberFieldsModule.getTableName())
                .fields(Collections.singletonList(lastAutoNumberIdField))
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(autoNumberField.getFieldId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(autoNumberField.getModuleId()), NumberOperators.EQUALS));

        Map<String, Object> prop = new HashMap<>();
        prop.put("lastAutoNumberId", lastAutoNumberId);
        int updatedRows = updateRecordBuilder.update(prop);
        FieldUtil.dropFieldFromCache(autoNumberField.getOrgId(), autoNumberField);
        return updatedRows;
    }

    private static AutoNumberField getAutoNumberIdValue(AutoNumberField autoNumberField, Connection conn) throws Exception {

        AutoNumberField field = null;

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .useExternalConnection(conn) //This connection will not be closed by builder. Use this with caution
                .table(ModuleFactory.getAutoNumberFieldModule().getTableName())
                .select(FieldFactory.getAutoNumberFieldFields())
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(autoNumberField.getFieldId()), StringOperators.IS))
                .forUpdate();

        List<Map<String, Object>> props = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {
            field = FieldUtil.getAsBeanFromMap(props.get(0), AutoNumberField.class);
        }

        return field;
    }

    public static Map<Long, Map<String, Object>> getAutoNumberFieldExtendedProps(List<Long> fieldIds) throws Exception {

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getAutoNumberFieldFields())
                .table(ModuleFactory.getAutoNumberFieldModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(StringUtils.join(fieldIds, ",")), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {

            Map<Long, Map<String, Object>> fieldProps = new HashMap<>();

            for (Map<String, Object> prop : props) {

                long fieldId = (long) prop.get("fieldId");
                fieldProps.put(fieldId, prop);

            }
            return fieldProps;
        }

        return Collections.EMPTY_MAP;

    }

    public static String constructAutoNumberValue(AutoNumberField autoNumberField, int indexId) {
        return autoNumberField.getPrefix() + indexId + autoNumberField.getSuffix();
    }

    public static void validateAutoNumberRecords(AutoNumberField field,boolean validateOldRecords) throws Exception {

        String prefix = field.getPrefix();
        String suffix = field.getSuffix();
        int idStartsFrom = field.getIdStartsFrom();

        Pattern pattern = Pattern.compile("\\d+");

        FacilioUtil.throwIllegalArgumentException(StringUtils.length(prefix) > 25 || StringUtils.length(suffix) > 25, "Prefix or Suffix character limit of 25 exceeds");
        FacilioUtil.throwIllegalArgumentException(idStartsFrom < 0, "Id starts from cannot be empty");
        FacilioUtil.throwIllegalArgumentException(Objects.equals(prefix,suffix), "Prefix and suffix cannot be same.");

        Matcher prefixMatcher = null;
        Matcher suffixMatcher = null;
        if(StringUtils.isNotEmpty(prefix)){
            prefixMatcher = pattern.matcher(prefix);
        }
        if(StringUtils.isNotEmpty(suffix)){
            suffixMatcher = pattern.matcher(suffix);
        }

        FacilioUtil.throwIllegalArgumentException(prefixMatcher.find() || suffixMatcher.find(), "Prefix and suffix cannot contains numbers.");

        boolean newChangeExistingRecords = field.isChangeExistingIds();

        final String finalPrefix = prefix;
        final String finalSuffix = suffix;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


        if (!newChangeExistingRecords && validateOldRecords) {

            boolean validated = false;

            Criteria extededCriteria = new Criteria();

            do {

                FacilioModule module = field.getModule();
                if (field.getFieldId() > 0) {
                    FacilioField updateField = modBean.getField(field.getFieldId());
                    module = updateField.getModule();
                    field.setColumnName(updateField.getColumnName());
                }

                FacilioField idField = FieldFactory.getIdField(module);

                SelectRecordsBuilder selectBuilder = new SelectRecordsBuilder<>();
                selectBuilder.module(module)
                        .select(Collections.singletonList(field))
                        .skipModuleCriteria()
                        .limit(1);

                if (!extededCriteria.isEmpty()) {
                    selectBuilder.andCriteria(extededCriteria);
                }

                if (StringUtils.isNotEmpty(finalPrefix)) {
                    selectBuilder.andCondition(CriteriaAPI.getCondition(field, finalPrefix, StringOperators.STARTS_WITH));
                }

                if (StringUtils.isNotEmpty(finalSuffix)) {
                    selectBuilder.andCondition(CriteriaAPI.getCondition(field, finalSuffix, StringOperators.ENDS_WITH));
                }

                selectBuilder.orderBy(idField.getCompleteColumnName() + " DESC");

                List<Map<String, Object>> props = selectBuilder.getAsProps();

                if (CollectionUtils.isEmpty(props)) {
                    validated = true;
                } else {

                    Object trimmedValue = null;

                    Map<String, Object> prop = props.get(0);
                    String autoNumberValue = prop.get(field.getName()).toString();

                    if (StringUtils.isNotEmpty(prefix) && StringUtils.isNotEmpty(suffix)) {
                        trimmedValue = autoNumberValue.substring(prefix.length(), autoNumberValue.length() - suffix.length());
                    } else if (StringUtils.isNotEmpty(prefix) && StringUtils.isEmpty(suffix)) {
                        trimmedValue = autoNumberValue.substring(prefix.length());
                    } else if (StringUtils.isNotEmpty(suffix) && StringUtils.isEmpty(prefix)) {
                        trimmedValue = autoNumberValue.substring(0, autoNumberValue.length() - suffix.length());
                    } else {
                        trimmedValue = autoNumberValue;
                    }

                    Matcher matcher = pattern.matcher(trimmedValue.toString());

                    if (matcher.find()) {

                        String trimmedPrefix = trimmedValue.toString().substring(0, matcher.start());
                        String trimmedSuffix = trimmedValue.toString().substring(matcher.end());

                        if (StringUtils.isNotEmpty(trimmedPrefix)) {
                            extededCriteria.addAndCondition(CriteriaAPI.getCondition(field, finalPrefix+trimmedPrefix, StringOperators.DOESNT_CONTAIN));
                        }

                        if (StringUtils.isNotEmpty(trimmedSuffix)) {
                            extededCriteria.addAndCondition(CriteriaAPI.getCondition(field, trimmedSuffix+finalSuffix, StringOperators.DOESNT_CONTAIN));
                        }

                        if(StringUtils.isEmpty(trimmedPrefix) && StringUtils.isEmpty(trimmedSuffix)){
                            int valInteger = NumberUtils.isCreatable(trimmedValue.toString()) ? Integer.parseInt(trimmedValue.toString()) : 0 ;
                            if (valInteger >= field.getIdStartsFrom()) {
                                field.setIdStartsFrom(valInteger + 1);
                            }
                            validated = true;
                        }
                    } else {
                        validated = true;
                    }


                }
            } while (!validated);

        }
        field.setLastAutoNumberId(field.getIdStartsFrom());
    }


}
