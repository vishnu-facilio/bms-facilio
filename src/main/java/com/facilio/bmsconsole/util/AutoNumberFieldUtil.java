package com.facilio.bmsconsole.util;

import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.AutoNumberField;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        int updatedRows =  updateRecordBuilder.update(prop);
        FieldUtil.dropFieldFromCache(autoNumberField.getOrgId(),autoNumberField);
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

}
