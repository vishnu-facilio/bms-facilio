package com.facilio.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;


public class DBAudit implements FacilioAudit {

    private static final Logger LOGGER = Logger.getLogger(DBAudit.class.getName());

    private static final String TABLE_NAME = "FacilioAudit";
    private static final String MODULE_TABLE_NAME = "FacilioAuditModule";
    private static final String METHOD_TABLE_NAME = "FacilioAuditMethod";
    private static final String ACTION_TABLE_NAME = "FacilioAuditAction";
    private static final String SEREVR_TABLE_NAME = "server_info";

    private static final String ID = "id";
    private static final String MODULE_ID = "moduleId";
    private static final String ACTION_ID = "actionId";
    private static final String METHOD_ID = "methodId";
    private static final String DISPLAY_NAME = "display_name";
    private static final String SERVER_INFO_ID = "server_info_id";
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
    private static final String PRIVATE_IP = "private_ip";
    private static final String ENVIRONMENT = "environment";
    private static final String PINGTIME ="pingtime";
    private static final String IN_USE ="in_use";
    private static final String LEADER = "leader";
    private static final String SERVER_ID = "server_id";

    private static final FacilioModule MODULE = getFacilioAuditModule();
    private static final FacilioField ID_FIELD = FieldFactory.getIdField(ID, "ID", MODULE);
    private static final List<FacilioField> FIELDS = getFacilioAuditFields();

    private static final FacilioModule FACILIO_MODULE = getFacilioAuditModuleTable();
    private static final FacilioField MODULE_ID_FIELD = FieldFactory.getIdField(ID, "ID", FACILIO_MODULE);
    private static final List<FacilioField> MODULE_FIELDS = getFacilioAuditModuleFields();

    private static final FacilioModule FACILIO_ACTION = getFacilioAuditActionTable();
    private static final FacilioField ACTION_ID_FIELD = FieldFactory.getNumberField(ID, "MODULE_ID", FACILIO_ACTION);
    private static final List<FacilioField> ACTION_FIELDS = getFacilioAuditActionFields();

    private static final FacilioModule FACILIO_METHOD = getFacilioAuditMethodTable();
    private static final FacilioField METHOD_ID_FIELD = FieldFactory.getNumberField(ID, "ACTION_ID", FACILIO_METHOD);
    private static final List<FacilioField> METHOD_FIELDS = getFacilioAuditMethodFields();

    private static final FacilioModule SEREVER_INFO = getFacilioAuditServerInfoTable();
    private static final FacilioField SEREVR_INFO_ID_FIELD = FieldFactory.getIdField(SERVER_INFO_ID, "id", SEREVER_INFO);
    private static final List<FacilioField> SERVER_FIELDS = getFacilioAuditServerInfoFields();

    private static FacilioModule getFacilioAuditModule() {
        FacilioModule module = new FacilioModule();
        module.setTableName(TABLE_NAME);
        module.setDisplayName(TABLE_NAME);
        module.setName(TABLE_NAME);
        return module;
    }

    private static FacilioModule getFacilioAuditModuleTable() {
        FacilioModule module = new FacilioModule();
        module.setTableName(MODULE_TABLE_NAME);
        module.setDisplayName(METHOD_TABLE_NAME);
        module.setName(MODULE_TABLE_NAME);
        return module;
    }

    private static FacilioModule getFacilioAuditMethodTable() {
        FacilioModule module = new FacilioModule();
        module.setTableName(METHOD_TABLE_NAME);
        module.setDisplayName(METHOD_TABLE_NAME);
        module.setName(METHOD_TABLE_NAME);
        return module;
    }

    private static FacilioModule getFacilioAuditActionTable() {
        FacilioModule module = new FacilioModule();
        module.setTableName(ACTION_TABLE_NAME);
        module.setDisplayName(ACTION_TABLE_NAME);
        module.setName(ACTION_TABLE_NAME);
        return module;
    }

    private static FacilioModule getFacilioAuditServerInfoTable() {
        FacilioModule module = new FacilioModule();
        module.setTableName(SEREVR_TABLE_NAME);
        module.setDisplayName(SEREVR_TABLE_NAME);
        module.setName(SEREVR_TABLE_NAME);
        return module;
    }

    private static List<FacilioField> getFacilioAuditFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(ID_FIELD);
        fields.add(FieldFactory.getDateField(START_TIME, "START_TIME", MODULE));
        fields.add(FieldFactory.getDateField(END_TIME, "END_TIME", MODULE));
        fields.add(FieldFactory.getNumberField(QUERY_COUNT, "QUERY_COUNT", MODULE));
        fields.add(FieldFactory.getNumberField(ORGID, "ORG_ID", MODULE));
        fields.add(FieldFactory.getNumberField(USERID, "USER_ID", MODULE));
        fields.add(FieldFactory.getNumberField(ORG_USERID, "ORG_USER_ID", MODULE));
        fields.add(FieldFactory.getNumberField(SESSION_ID, "SESSION_ID", MODULE));
        fields.add(FieldFactory.getNumberField(STATUS, "STATUS", MODULE));
        fields.add(FieldFactory.getNumberField(THREAD, "THREAD", MODULE));
        fields.add(FieldFactory.getNumberField(SERVER_ID, "SERVER_ID", MODULE));
        return fields;
    }

    private static List<FacilioField> getFacilioAuditModuleFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(MODULE_ID_FIELD);
//        fields.add(FieldFactory.getStringField(DISPLAY_NAME, "DISPLAY_NAME", FACILIO_MODULE));
        fields.add(FieldFactory.getStringField(MODULE_COLUMNS, "MODULE", FACILIO_MODULE));
        return fields;
    }

    private static List<FacilioField> getFacilioAuditActionFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(ACTION_ID_FIELD);
//        fields.add(FieldFactory.getStringField(DISPLAY_NAME, "DISPLAY_NAME", FACILIO_ACTION));
        fields.add(FieldFactory.getStringField(ACTION, "ACTION", FACILIO_ACTION));
        return fields;
    }

    private static List<FacilioField> getFacilioAuditMethodFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(METHOD_ID_FIELD);
//        fields.add(FieldFactory.getStringField(DISPLAY_NAME, "DISPLAY_NAME", FACILIO_METHOD));
        fields.add(FieldFactory.getStringField(METHOD, "METHOD", FACILIO_METHOD));
        return fields;
    }

    private static List<FacilioField> getFacilioAuditServerInfoFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(SEREVR_INFO_ID_FIELD);
        fields.add(FieldFactory.getStringField(PRIVATE_IP, PRIVATE_IP, SEREVER_INFO));
        fields.add(FieldFactory.getStringField(ENVIRONMENT, ENVIRONMENT, SEREVER_INFO));
        fields.add(FieldFactory.getNumberField(STATUS,STATUS, SEREVER_INFO));
        fields.add(FieldFactory.getNumberField(PINGTIME, PINGTIME, SEREVER_INFO));
        fields.add(FieldFactory.getNumberField(IN_USE, IN_USE, SEREVER_INFO));
        fields.add(FieldFactory.getNumberField(LEADER, LEADER, SEREVER_INFO));

        return fields;
    }
    private Map<String, Object> getValueMap(AuditData data) {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(ID, data.getId());
        valueMap.put(ORGID, data.getOrgId());
        valueMap.put(USERID, data.getUserId());
        valueMap.put(ORG_USERID, data.getOrgUserId());
        valueMap.put(START_TIME, System.currentTimeMillis());
        valueMap.put(SESSION_ID, data.getSessionId());
        valueMap.put(END_TIME, data.getEndTime());
        valueMap.put(STATUS, data.getStatus());
        valueMap.put(QUERY_COUNT, data.getQueryCount());
        valueMap.put(THREAD, data.getThread());
        valueMap.put(SERVER_ID,data.getServerId());
        return valueMap;
    }

	public long add(AuditData data) {
		long id = 0L;
		try {
				Map<String, Object> server_info = checkServerIPExists(data);
				long server_info_id = (long)server_info.get(SERVER_INFO_ID);
				 if(server_info_id != 0){
					data.setServerId(server_info_id);
					id = insertBuilder(getValueMap(data), TABLE_NAME, FIELDS);
					data.setId(id);
				 }
				Map<String, Object> module_info = checkModuleExists(data);
				if (StringUtils.isEmpty((String)module_info.get(MODULE_COLUMNS))) {
					return addModule(data);
				} else  {
					Map<String, Object> action_info = checkActionExists((long)module_info.get("id"));
					if (StringUtils.isEmpty((String) action_info.get(ACTION)))
					return addAction((long) module_info.get("id"), data);
				}
		} catch (Exception e) {
			LOGGER.info("Exception while adding : ", e);
		}
		return id;
	}

	private long addModule(AuditData data) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(MODULE_COLUMNS, data.getModule());
		return addAction(insertBuilder(prop, MODULE_TABLE_NAME, MODULE_FIELDS), data);
	}

	private long addAction(long id, AuditData data) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(ID, id);
		prop.put(ACTION, data.getAction());
		return addMethod(insertBuilder(prop, ACTION_TABLE_NAME, ACTION_FIELDS), data);
	}

	private long addMethod(long actionId, AuditData data) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(ID, actionId);
		prop.put(METHOD, data.getMethod());
		return insertBuilder(prop, METHOD_TABLE_NAME, METHOD_FIELDS);
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

    private Map<String, Object> checkModuleExists(AuditData data) throws Exception {
    	
    	List<FacilioField> fields = new ArrayList<FacilioField>();
    	fields.addAll(MODULE_FIELDS);
    	Map<String, FacilioField> fieldMaps = FieldFactory.getAsMap(fields);

    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(MODULE_TABLE_NAME)
    			.andCondition(CriteriaAPI.getCondition(fieldMaps.get("module"), data.getModule(), StringOperators.IS));
    	List<Map<String,Object>> prop = builder.get();
    	if(CollectionUtils.isNotEmpty(prop)) {
    		return prop.get(0);
    	}
    	return Collections.emptyMap();
    }

    private Map<String, Object> checkActionExists(long id) throws Exception {
    	
    	List<FacilioField> fields = new ArrayList<FacilioField>();
    	fields.addAll(ACTION_FIELDS);
    	Map<String, FacilioField> fieldMaps = FieldFactory.getAsMap(fields);

    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ACTION_TABLE_NAME)
    			.andCondition(CriteriaAPI.getCondition(fieldMaps.get("id"), String.valueOf(id), StringOperators.IS));
    	List<Map<String,Object>> prop = builder.get();
    	if(CollectionUtils.isNotEmpty(prop)) {
    		return prop.get(0);
    	}
    	return Collections.emptyMap();
    }
    private Map<String, Object> checkServerIPExists(AuditData data) throws Exception {
    	
    	List<FacilioField> fields = new ArrayList<FacilioField>();
    	fields.addAll(SERVER_FIELDS);
    	Map<String, FacilioField> fieldMaps = FieldFactory.getAsMap(fields);

    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(SEREVR_TABLE_NAME)
				.andCondition(CriteriaAPI.getCondition(fieldMaps.get(PRIVATE_IP), data.getServer(), StringOperators.IS));
    	List<Map<String,Object>> prop = builder.get();
    	if(CollectionUtils.isNotEmpty(prop)) {
    		return prop.get(0);
    	}
    	return Collections.emptyMap();
    }

    private long insertBuilder(Map<String, Object> prop, String tableName, List<FacilioField> fields) throws Exception {
    	 GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(tableName).fields(fields);
    	return insertBuilder.insert(prop);

    }
}
