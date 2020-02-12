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

    private static final String TABLE_NAME = "FacilioDBAudit";
    private static final String MODULE_TABLE_NAME = "FacilioAuditModule";
    private static final String METHOD_TABLE_NAME = "FacilioAuditMethod";
    private static final String ACTION_TABLE_NAME = "FacilioAuditAction";
    private static final String REFERER_TABLE_NAME = "FacilioAuditReferer";
    private static final String ID = "id";
    private static final String MODULE_ID = "moduleId";
    private static final String DISPLAY_NAME = "display_name";
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
    private static final String REFERER = "referer"; 
    private static final String REFERER_ID = "refererId";
    
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

    private static final FacilioModule REFERER_MODULE = getFacilioAuditRefererTable();
    private static final FacilioField REFERER_ID_FIELD = FieldFactory.getIdField(ID, "REFERER_ID", REFERER_MODULE);
    private static final List<FacilioField> REFERER_FIELDS = getFacilioAuditRefererFields();
    
	private Map<String, Object> MODULE_INFO_LIST = new HashMap<String, Object>();
    private Map<String, Object> ACTION_INFO_LIST = new HashMap<String, Object>();
    private Map<String, Object> METHOD_INFO_LIST = new HashMap<String, Object>();
    private Map<String, Object> REFERER_INFO_LIST = new HashMap<String, Object>();
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

    private static FacilioModule getFacilioAuditRefererTable() {
        FacilioModule module = new FacilioModule();
        module.setTableName(REFERER_TABLE_NAME);
        module.setDisplayName(REFERER_TABLE_NAME);
        module.setName(REFERER_TABLE_NAME);
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
        fields.add(FieldFactory.getNumberField(MODULE_ID, "MODULE_ID", MODULE));
        fields.add(FieldFactory.getNumberField(REFERER_ID, "REFERER_ID", MODULE));

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

    private static List<FacilioField> getFacilioAuditRefererFields(){
    	List<FacilioField> fields = new ArrayList<>();
        fields.add(REFERER_ID_FIELD);
        fields.add(FieldFactory.getStringField(REFERER, "REFERER", REFERER_MODULE));
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
        valueMap.put(MODULE_ID, data.getModuleId());
        valueMap.put(REFERER_ID,data.getRefererId());

        return valueMap;
    }

	public long add(AuditData data) {
		long id = 0L;
		try {
			String module = data.getModule();
			long moduleId = 0L;
			String action = data.getAction();
			long actionId = 0L;
			String method = data.getMethod();
			long methodId = 0L;
			String referer = data.getReferer();
			long refereId = 0L;
			if(StringUtils.isNotEmpty(module)) {
				 moduleId= checkModule(module);
				if (moduleId == 0) {
					moduleId = addModule(module);
				}
			}
			if(StringUtils.isNotEmpty(action)) {
				 actionId = checkAction(action);
				if (actionId == 0) {
					actionId = addAction(moduleId, action);
				}
			}
			
			if(StringUtils.isNotEmpty(method)) {
				 methodId = checkMethod(method);
				if(methodId == 0) {
					methodId =  addMethod(actionId, method);
				}
			}
			
			if(StringUtils.isNotEmpty(referer)) {
				 refereId = checkReferer(referer);
				if(refereId == 0) {
					refereId = addReferer(referer);
				}
				if(refereId != 0) {
					data.setRefererId(refereId);
				}
			}
			if(SERVERID != -1) {
				data.setServerId(SERVERID);
			}
			data.setModuleId(moduleId);
			id = insertBuilder(getValueMap(data), TABLE_NAME, FIELDS);
			data.setId(id);
		} catch (Exception e) {
			LOGGER.info("Exception while adding : ", e);
		}
		return id;
	}

	private long addModule(String module2) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(MODULE_COLUMNS, module2);
		long id = insertBuilder(prop, MODULE_TABLE_NAME, MODULE_FIELDS);
		MODULE_INFO_LIST.put(module2, id);
		return id;
	}
	
	private long addAction(long id, String action2) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(ID, id);
		prop.put(ACTION, action2);
		long actionId = insertBuilder(prop, ACTION_TABLE_NAME, ACTION_FIELDS);
		ACTION_INFO_LIST.put(action2, actionId);
		return actionId;
	}

	private long addMethod(long actionId, String method2) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(ID, actionId);
		prop.put(METHOD, method2);
		long methodId= insertBuilder(prop, METHOD_TABLE_NAME, METHOD_FIELDS);
		METHOD_INFO_LIST.put(method2,methodId);
		return methodId;
	}

	private long addReferer(String data) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("referer", data);
		long id = insertBuilder(prop, REFERER_TABLE_NAME, REFERER_FIELDS);
		REFERER_INFO_LIST.put(data, id);
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

    private long checkModule(String module) throws Exception {
    	long id = 0L;
    	if(MapUtils.isEmpty(MODULE_INFO_LIST)) {
    		MODULE_INFO_LIST = getModuleInfoList();
    	}
    		if(MapUtils.isNotEmpty(MODULE_INFO_LIST)) {
    		if(StringUtils.isNotEmpty(module)) {
    			if(MODULE_INFO_LIST.containsKey(module)) {
    				id = (long) MODULE_INFO_LIST.get(module);
    			}else{
    				id = getData(module,MODULE_TABLE_NAME,MODULE_FIELDS,"module");
    				if(id != 0) {
    					MODULE_INFO_LIST.put(module, id);
    				}
    			}
    		}
    	}
    	return id;
    }

    private long checkReferer(String data) throws Exception {
    	long id = 0L;
    	if(MapUtils.isEmpty(REFERER_INFO_LIST)) {
    		REFERER_INFO_LIST = getRefererInfoList();
    	}if(MapUtils.isNotEmpty(REFERER_INFO_LIST)){
    		if(StringUtils.isNotEmpty(data)) {
    			if(REFERER_INFO_LIST.containsKey(data)) {
    				id = (long) REFERER_INFO_LIST.get(data);
    			}else {
    				id = getData(data , REFERER_TABLE_NAME, REFERER_FIELDS,"referer");
    				if(id != 0) {
    					REFERER_INFO_LIST.put(data, id);
    				}
    			}
    		}
    	}
    	return id;
    }
    
    private long checkAction(String action) throws Exception {
    	long id = 0L;
    	if(MapUtils.isEmpty(ACTION_INFO_LIST)) {
    		ACTION_INFO_LIST =getActionInfoList();
    	}if(MapUtils.isNotEmpty(ACTION_INFO_LIST)){
    		if(StringUtils.isNotEmpty(action)) {
    			if(ACTION_INFO_LIST.containsKey(action)) {
    				id = (long) ACTION_INFO_LIST.get(action);
    			}else {
    				id = getData(action,ACTION_TABLE_NAME,ACTION_FIELDS,"action");
    				if(id != 0) {
    					ACTION_INFO_LIST.put(action, id);
    				}
    			}
    		}
    	}
    	return id;
    }

    private long checkMethod(String data) throws Exception {
    	long id=0L;
    	if(MapUtils.isEmpty(METHOD_INFO_LIST)) {
    		METHOD_INFO_LIST =getMethodInfoList();
    	}if(MapUtils.isNotEmpty(METHOD_INFO_LIST)){
    		if(StringUtils.isNotEmpty(data)) {
    			if(METHOD_INFO_LIST.containsKey(data)) {
    				id = getData(data, METHOD_TABLE_NAME, METHOD_FIELDS, "method");
    			}
    			if(id != 0) {
    				METHOD_INFO_LIST.put(data, id);
    			}
    		}
    	}
    	return id;
	}
    private long insertBuilder(Map<String, Object> prop, String tableName, List<FacilioField> fields) throws Exception {
    	 GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(tableName).fields(fields);
    	return insertBuilder.insert(prop);

    }
    
    private Map<String, Object> getActionInfoList() {
    	
    	List<FacilioField> fields = new ArrayList<FacilioField>();
    	fields.addAll(ACTION_FIELDS);

    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ACTION_TABLE_NAME);
    	List<Map<String, Object>> prop = new ArrayList<>();
		try {
			prop = builder.get();
			if(CollectionUtils.isNotEmpty(prop)) {
				for(Map<String ,Object> itr : prop) {
					String action = (String) itr.get("action");
	    			long actionId = (long) itr.get("id");
	    			if(StringUtils.isNotEmpty(action) && actionId != 0) {
	    				ACTION_INFO_LIST.put(action,actionId);
	    			}
	    			
	    		}
				return ACTION_INFO_LIST;
				
			}
		} catch (Exception e) {
			LOGGER.info("Exception Occurred In Getting ActionInfoList",e);
		}
		return ACTION_INFO_LIST;
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

    private Map<String, Object> getMethodInfoList() {

    	List<FacilioField> fields = new ArrayList<FacilioField>();
    	fields.addAll(METHOD_FIELDS);

    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(METHOD_TABLE_NAME);
    	List<Map<String, Object>> prop = new ArrayList<>();
		try {
			prop = builder.get();
			if(CollectionUtils.isNotEmpty(prop)) {
	    		for(Map<String ,Object> itr : prop) {
	    			String method = (String) itr.get("method");
	    			long methodId = (long) itr.get("id");
	    			if(StringUtils.isNotEmpty(method) && methodId != 0) {
	    				METHOD_INFO_LIST.put(method , methodId);
	    			}
	    			
	    		}
			}
		} catch (Exception e) {
			LOGGER.info("Exception Occurred In Getting MethodInfoList",e);
		}
		return METHOD_INFO_LIST;
	}
    
    private Map<String, Object> getRefererInfoList() {

    	List<FacilioField> fields = new ArrayList<FacilioField>();
    	fields.addAll(REFERER_FIELDS);

    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(REFERER_TABLE_NAME);
    	List<Map<String, Object>> prop = new ArrayList<>();
		try {
			prop = builder.get();
			if(CollectionUtils.isNotEmpty(prop)) {
	    		for(Map<String ,Object> itr : prop) {
	    			String referer = (String) itr.get("referer");
	    			long refererId = (long) itr.get("id");
	    			if(StringUtils.isNotEmpty(referer) && refererId != 0) {
	    				REFERER_INFO_LIST.put(referer,refererId);
	    			}
	    		}
			}
		} catch (Exception e) {
			LOGGER.info("Exception Occurred In Getting RefererInfoList",e);
		}
		return REFERER_INFO_LIST;
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
    			id=(long) itr.get(key);
    		}
    	}
    	return id;
    }
}
