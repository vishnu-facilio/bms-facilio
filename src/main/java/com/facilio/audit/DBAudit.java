package com.facilio.audit;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBAudit implements FacilioAudit {

    private static final Logger LOGGER = Logger.getLogger(DBAudit.class.getName());

    private static final String TABLE_NAME = "FacilioAudit";

    private static final String ID = "id";
    private static final String STATUS = "status";
    private static final String USERID = "userId";
    private static final String ORGID = "orgId";
    private static final String ORG_USERID = "orgUserId";
    private static final String SERVER = "server";
    private static final String MODULE_COLUMNS = "module";
    private static final String ACTION = "action";
    private static final String METHOD = "method";
    private static final String THREAD = "thread";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String QUERY_COUNT = "queryCount";
    private static final String SESSION_ID = "sessionId";

    private static final FacilioModule MODULE = getFacilioAuditModule();
    private static final FacilioField ID_FIELD = FieldFactory.getIdField(ID, "ID", MODULE);
    private static final List<FacilioField> FIELDS = getFacilioAuditFields();

    private static FacilioModule getFacilioAuditModule() {
        FacilioModule module = new FacilioModule();
        module.setTableName(TABLE_NAME);
        module.setDisplayName(TABLE_NAME);
        module.setName(TABLE_NAME);
        return module;
    }

    private static List<FacilioField> getFacilioAuditFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(ID_FIELD);
        fields.add(FieldFactory.getStringField(MODULE_COLUMNS, "MODULE", MODULE));
        fields.add(FieldFactory.getStringField(ACTION, "ACTION", MODULE));
        fields.add(FieldFactory.getStringField(METHOD, "METHOD", MODULE));
        fields.add(FieldFactory.getStringField(SERVER, "SERVER", MODULE));
        fields.add(FieldFactory.getDateField(START_TIME, "START_TIME", MODULE));
        fields.add(FieldFactory.getDateField(END_TIME, "END_TIME", MODULE));
        fields.add(FieldFactory.getNumberField(QUERY_COUNT, "QUERY_COUNT", MODULE));
        fields.add(FieldFactory.getNumberField(ORGID, "ORG_ID", MODULE));
        fields.add(FieldFactory.getNumberField(USERID, "USER_ID", MODULE));
        fields.add(FieldFactory.getNumberField(ORG_USERID, "ORG_USER_ID", MODULE));
        fields.add(FieldFactory.getNumberField(SESSION_ID, "SESSION_ID", MODULE));
        fields.add(FieldFactory.getNumberField(STATUS, "STATUS", MODULE));
        fields.add(FieldFactory.getNumberField(THREAD, "THREAD", MODULE));
        return fields;
    }


    private Map<String, Object> getValueMap(AuditData data) {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(ID, data.getId());
        valueMap.put(ORGID, data.getOrgId());
        valueMap.put(USERID, data.getUserId());
        valueMap.put(ORG_USERID, data.getOrgUserId());
        valueMap.put(SERVER, data.getServer());
        valueMap.put(START_TIME, System.currentTimeMillis());
        valueMap.put(SESSION_ID, data.getSessionId());
        valueMap.put(END_TIME, data.getEndTime());
        valueMap.put(MODULE_COLUMNS, data.getModule());
        valueMap.put(METHOD, data.getMethod());
        valueMap.put(ACTION, data.getAction());
        valueMap.put(STATUS, data.getStatus());
        valueMap.put(QUERY_COUNT, data.getQueryCount());
        valueMap.put(THREAD, data.getThread());
        return valueMap;
    }

    public long add(AuditData data) {
        long id = 0L;
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(TABLE_NAME).fields(FIELDS);
        try {
            id = insertBuilder.insert(getValueMap(data));
        } catch (Exception e) {
            LOGGER.info("Exception while adding : ", e);
        }
        return id;
    }

    public long update(AuditData data) {
        long rowsUpdated = 0L;
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder().table(TABLE_NAME).fields(FIELDS)
						.andCondition(CriteriaAPI.getEqualsCondition(ID_FIELD, String.valueOf(data.getId())));
        try {
            rowsUpdated = updateRecordBuilder.update(getValueMap(data));
        } catch (Exception e) {
            LOGGER.info("Exception while updating audit data, ", e);
        }
        return rowsUpdated;
    }

    public long delete(AuditData data) {
        return 0;
    }

    public List<AuditData> get() {
        return new ArrayList<>();
    }
}
