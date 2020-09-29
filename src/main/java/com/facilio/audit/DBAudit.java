package com.facilio.audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.server.ServerInfo;


public class DBAudit implements FacilioAudit {

    private static final Logger LOGGER = Logger.getLogger(DBAudit.class.getName());

    private static final String TABLE_NAME = "FacilioAuditData";
    private static final String MODULE_TABLE_NAME = "FacilioAuditModuleData";
    private static final String METHOD_TABLE_NAME = "FacilioAuditMethodData";
    private static final String ACTION_TABLE_NAME = "FacilioAuditActionData";
    private static final String ID = "id";
    private static final String MODULE_ID = "moduleId";
    private static final String METHOD_ID = "methodId";
    private static final String ACTION_ID = "actionId";
    private static final String STATUS = "status";
    private static final String USERID = "userId";
    private static final String ORGID = "orgId";
    private static final String ORG_USERID = "orgUserId";
    private static final String MODULE_COLUMNS = "module";
    private static final String ACTION = "action";
    private static final String METHOD = "method";
    private static final String THREAD = "thread";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String QUERY_COUNT = "queryCount";
    private static final String SESSION_ID = "sessionId";
    private static final String SERVER_ID = "server_id";
    private static final String REMOTE_IP = "remoteAddr";

    private static final FacilioModule MODULE = getFacilioAuditModule();
    private static final FacilioField ID_FIELD = FieldFactory.getIdField(ID, "ID", MODULE);
    private static final List<FacilioField> FIELDS = getFacilioAuditFields();

    private static final FacilioModule FACILIO_MODULE = getFacilioAuditModuleTable();
    private static final FacilioField MODULE_ID_FIELD = FieldFactory.getIdField(ID, "ID", FACILIO_MODULE);
    private static final List<FacilioField> MODULE_FIELDS = getFacilioAuditModuleFields();

    private static final FacilioModule FACILIO_ACTION = getFacilioAuditActionTable();
    private static final FacilioField ACTION_ID_FIELD = FieldFactory.getNumberField(ID, "ID", FACILIO_ACTION);
    private static final List<FacilioField> ACTION_FIELDS = getFacilioAuditActionFields();

    private static final FacilioModule FACILIO_METHOD = getFacilioAuditMethodTable();
    private static final FacilioField METHOD_ID_FIELD = FieldFactory.getNumberField(ID, "ID", FACILIO_METHOD);
    private static final List<FacilioField> METHOD_FIELDS = getFacilioAuditMethodFields();

	private Map<String, Object> MODULE_INFO_LIST = new HashMap<String, Object>();
    private Map<String,Map<String,Map<String,Object>>> AUDIT_INFO = new HashMap<>();

    private long SERVERID = ServerInfo.getServerId();

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
        fields.add(FieldFactory.getNumberField(METHOD_ID, "METHOD_ID", MODULE));
        fields.add(FieldFactory.getStringField(REMOTE_IP, "REMOTE_IP", MODULE));
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
        fields.add(FieldFactory.getNumberField(MODULE_ID, "MODULE_ID", FACILIO_ACTION));
        fields.add(FieldFactory.getStringField(ACTION, "ACTION", FACILIO_ACTION));
        return fields;
    }

    private static List<FacilioField> getFacilioAuditMethodFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(METHOD_ID_FIELD);
        fields.add(FieldFactory.getNumberField(ACTION_ID,"ACTION_ID", FACILIO_METHOD));
        fields.add(FieldFactory.getStringField(METHOD, "METHOD", FACILIO_METHOD));
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
        valueMap.put(METHOD_ID, data.getMethodId());
        valueMap.put(REMOTE_IP, data.getRemoteIPAddress());

        return valueMap;
    }

	public long add(AuditData data) {
		long id = 0L;
		long addStart = System.currentTimeMillis();
		try {
			String module = data.getModule();
			long moduleId = 0L;
			String action = data.getAction();
			long actionId = 0L;
			String method = data.getMethod();
			long methodId = 0L;
			if(MapUtils.isEmpty(AUDIT_INFO) && MapUtils.isEmpty(MODULE_INFO_LIST)) {
				getAuditInfoData(null,null);
				getModuleInfoList();
			}
			if(StringUtils.isNotEmpty(module) && StringUtils.isNotEmpty(action) && StringUtils.isNotEmpty(method) ) {
				if(!AUDIT_INFO.containsKey(module)) {
					getAuditInfoData(module, "module");
					if(!AUDIT_INFO.containsKey(module)) {
						moduleId = addModule(module);
					}
				 }
				 if(!AUDIT_INFO.get(module).containsKey(action)) {
					getAuditInfoData(action, "action");
					if(!AUDIT_INFO.get(module).containsKey(action)) {
						getModuleInfoList();
						moduleId = (long) MODULE_INFO_LIST.get(module);
						actionId = addAction(moduleId, action, module);
						methodId = addMethod(actionId, method, module, action);
					}
				}
				if(!AUDIT_INFO.get(module).get(action).containsKey(method)) {
					getAuditInfoData(method, "method");
					methodId = (long) AUDIT_INFO.get(module).get(action).get(method);
				}
				else {
					methodId = (long) AUDIT_INFO.get(module).get(action).get(method);
				}
			}
			LOGGER.debug("Size of the Map ---> "+AUDIT_INFO.size());
			if(SERVERID != -1) {
				data.setServerId(SERVERID);
			}
			data.setMethodId(methodId);
			id = insertBuilder(getValueMap(data), TABLE_NAME, FIELDS);
			data.setId(id);
		} catch (Exception e) {
			LOGGER.info("Exception while adding : ", e);
		}
//		LOGGER.info("Audit entry add full time timetaken:::: "+(System.currentTimeMillis()-addStart));
		return id;
	}

	private long addModule(String module) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(MODULE_COLUMNS, module);
		long id = insertBuilder(prop, MODULE_TABLE_NAME, MODULE_FIELDS);
		MODULE_INFO_LIST.put(module, id);
		AUDIT_INFO.put(module, new HashMap<>());
		return id;
	}
	
	private long addAction(long id, String action2,String module) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(MODULE_ID, id);
		prop.put(ACTION, action2);
		long actionId = insertBuilder(prop, ACTION_TABLE_NAME, ACTION_FIELDS);
		AUDIT_INFO.get(module).put(action2, new HashMap<>());
		return actionId;
	}

	private long addMethod(long actionId, String method2,String module,String action) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(ACTION_ID, actionId);
		prop.put(METHOD, method2);
		long methodId= insertBuilder(prop, METHOD_TABLE_NAME, METHOD_FIELDS);
		AUDIT_INFO.get(module).get(action).put(method2, methodId);
		return methodId;
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

    private long insertBuilder(Map<String, Object> prop, String tableName, List<FacilioField> fields) throws Exception {
    	 GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(tableName).fields(fields);
    	return insertBuilder.insert(prop);

    }
    
    private Map<String, Object> getModuleInfoList() {

    	List<FacilioField> fields = new ArrayList<FacilioField>();
    	fields.addAll(MODULE_FIELDS);

    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(MODULE_TABLE_NAME);
    	List<Map<String, Object>> prop = new ArrayList<>();
		try {
			prop = builder.get();
			if(CollectionUtils.isNotEmpty(prop)) {
	    		for(Map<String ,Object> itr : prop) {
	    			String module = (String) itr.get("module");
	    			long moduleId = (long) itr.get("id");
	    			if(StringUtils.isNotEmpty(module) && moduleId != 0) {
	    				MODULE_INFO_LIST.put(module,moduleId);
	    			}
	    			
	    		}
	    		
			}
		} catch (Exception e) {
			LOGGER.info("Exception Occurred In Getting ModuleInfoList",e);
		}
		return MODULE_INFO_LIST;
		
	}

    private long getData(String data, String tableName, List<FacilioField> fields, String key) throws Exception {
    	List<FacilioField> field = new ArrayList<FacilioField>();
    	field.addAll(fields);
    	Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
    	long id = 0L;
    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(field)
				.table(tableName)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(key), data, StringOperators.IS));
    	
    	List<Map<String, Object>> prop = builder.get();
    	if(CollectionUtils.isNotEmpty(prop)) {
    		for (Map<String,Object> itr:prop) {
    			id=(long) itr.get("id");
    		}
    	}
    	return id;
    }
    
    private void getAuditInfoData(String moduleVal, String key) throws Exception {
    	List<FacilioField> fields = new ArrayList<>();
    	fields.add(FieldFactory.getStringField(MODULE_COLUMNS, "MODULE", FACILIO_MODULE));
    	fields.add(FieldFactory.getStringField(ACTION, "ACTION", FACILIO_ACTION));
    	fields.addAll(METHOD_FIELDS);
    	Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
    	
    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(MODULE_TABLE_NAME)
				.innerJoin(ACTION_TABLE_NAME)
				.on("FacilioAuditModuleData.ID = FacilioAuditActionData.MODULE_ID")
				.innerJoin(METHOD_TABLE_NAME)
				.on("FacilioAuditActionData.ID = FacilioAuditMethodData.ACTION_ID");
    	if(StringUtils.isNotEmpty(moduleVal)) {
    		builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(key), moduleVal, StringOperators.IS));
    	}
    	
    	List<Map<String, Object>> prop = builder.get();
    	for(Map<String,Object> itr:prop) {
    		String module = (String) itr.get("module");
    		String action = (String) itr.get("action");
    		String method = (String) itr.get("method");
    		long methodId = (long) itr.get("id");
    		Map<String,Object> methodInfo = new HashMap<>();
    		Map<String,Map<String,Object>> actionInfo = new HashMap<>();
    		methodInfo.put(method,methodId);
    		actionInfo.put(action, methodInfo);
    		AUDIT_INFO.put(module, actionInfo);
    	
    	}
    
    }
}
