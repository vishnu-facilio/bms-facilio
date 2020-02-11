package com.facilio.audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;


public class DBAudit implements FacilioAudit {

    private static final Logger LOGGER = Logger.getLogger(DBAudit.class.getName());

    private static final String TABLE_NAME = "FacilioDBAudit";
    private static final String MODULE_TABLE_NAME = "FacilioAuditModule";
    private static final String METHOD_TABLE_NAME = "FacilioAuditMethod";
    private static final String ACTION_TABLE_NAME = "FacilioAuditAction";
    private static final String SEREVR_TABLE_NAME = "server_info";
    private static final String REFERER_TABLE_NAME = "FacilioAuditReferer";
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

    private static final FacilioModule SEREVER_INFO = getFacilioAuditServerInfoTable();
    private static final FacilioField SEREVR_INFO_ID_FIELD = FieldFactory.getIdField(SERVER_INFO_ID, "id", SEREVER_INFO);
    private static final List<FacilioField> SERVER_FIELDS = getFacilioAuditServerInfoFields();

    private static final FacilioModule REFERER_MODULE = getFacilioAuditRefererTable();
    private static final FacilioField REFERER_ID_FIELD = FieldFactory.getIdField(ID, "REFERER_ID", REFERER_MODULE);
    private static final List<FacilioField> REFERER_FIELDS = getFacilioAuditRefererFields();
    
	private List<Map<String, Object>> MODULE_INFO_LIST = new ArrayList<Map<String,Object>>();
    private List<Map<String, Object>> ACTION_INFO_LIST = new ArrayList<Map<String,Object>>();
    private List<Map<String, Object>> SERVER_INFO_LIST = new ArrayList<Map<String,Object>>();
    private List<Map<String, Object>> METHOD_INFO_LIST = new ArrayList<Map<String,Object>>();
    private List<Map<String, Object>> REFERER_INFO_LIST = new ArrayList<Map<String,Object>>();

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
			
			long moduleId= checkModule(data);
			if (moduleId == 0) {
				moduleId = addModule(data);
			} 
			long actionId = checkAction(data.getAction());
			if (actionId == 0) {
				actionId = addAction(moduleId, data);
			}
			long methodId = checkMethod(data.getMethod());
			if(methodId == 0) {
				methodId =  addMethod(actionId, data);
			}
			String referer = data.getReferer();
			long refereId = 0L;
			if(StringUtils.isNotEmpty(referer)) {
				 refereId = checkReferer(referer);
				if(refereId == 0) {
					refereId = addReferer(data.getReferer());
				}
				if(refereId != 0) {
					data.setRefererId(refereId);
				}
			}
			 
			long serverId = checkServer(data);
			if(serverId == 0) {
				serverId = addServer(data.getServer());
			}
			data.setServerId(serverId);
			data.setModuleId(moduleId);
			id = insertBuilder(getValueMap(data), TABLE_NAME, FIELDS);
			data.setId(id);
		} catch (Exception e) {
			LOGGER.info("Exception while adding : ", e);
		}
		return id;
	}

	private long addModule(AuditData data) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(MODULE_COLUMNS, data.getModule());
		long id = insertBuilder(prop, MODULE_TABLE_NAME, MODULE_FIELDS);
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(MODULE_COLUMNS, data.getModule());
		props.put(ID, id);
		MODULE_INFO_LIST.add(props);
		return id;
	}
	
	private long addAction(long id, AuditData data) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(ID, id);
		prop.put(ACTION, data.getAction());
		long actionId = insertBuilder(prop, ACTION_TABLE_NAME, ACTION_FIELDS);
		ACTION_INFO_LIST.add(prop);
		return actionId;
	}

	private long addMethod(long actionId, AuditData data) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(ID, actionId);
		prop.put(METHOD, data.getMethod());
		long methodId= insertBuilder(prop, METHOD_TABLE_NAME, METHOD_FIELDS);
		METHOD_INFO_LIST.add(prop);
		return methodId;
	}

	private long addReferer(String data) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("referer", data);
		long id = insertBuilder(prop, REFERER_TABLE_NAME, REFERER_FIELDS);
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("referer", data);
		props.put("id",id);
		REFERER_INFO_LIST.add(props);
		return id;
	}

	private long addServer(String data) throws Exception {
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(PRIVATE_IP, data);
		long id = insertBuilder(prop, SEREVR_TABLE_NAME, SERVER_FIELDS);
		Map<String ,Object> addserver = new HashMap<>();
		addserver.put(SERVER_INFO_ID, id);
		addserver.put(PRIVATE_IP, data);
		SERVER_INFO_LIST.add(addserver);
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

    private long checkModule(AuditData data) throws Exception {
    	long id = 0L;
    	if(CollectionUtils.isEmpty(MODULE_INFO_LIST)) {
    		MODULE_INFO_LIST = getModuleInfoList();
    	}
    	if(CollectionUtils.isNotEmpty(MODULE_INFO_LIST)){
    		for(Map<String,Object> itr:MODULE_INFO_LIST) {
        		if(itr.get("module").equals(data.getModule())) {
        			id = (long) itr.get("id");
        			return id;
        		}
        	}
    	}
    	return id;
    }

    private long checkReferer(String data) {
    	long id = 0L;
    	if(CollectionUtils.isEmpty(REFERER_INFO_LIST)) {
    		REFERER_INFO_LIST = getRefererInfoList();
    	}
    	if(CollectionUtils.isNotEmpty(REFERER_INFO_LIST)) {
    		for(Map<String,Object> itr:REFERER_INFO_LIST) {
        		if(itr.get("referer").equals(data)) {
        			id = (long) itr.get("id");
        			return id;
        		}
        	}
    	}
    	return id;
    }
    
    private long checkAction(String action) throws Exception {
    	long existingId = 0L;
    	if(CollectionUtils.isEmpty(ACTION_INFO_LIST)) {
    		ACTION_INFO_LIST = getActionInfoList();
    	}if(CollectionUtils.isNotEmpty(ACTION_INFO_LIST)) {
    		for(Map<String,Object> itr:ACTION_INFO_LIST) {
        		if(itr.get("action").equals(action)) {
        			 existingId = (long) itr.get("id");
        	        		return existingId;
        		}
        	}
    	}
    	
    	return 0L;
    }
    private long checkServer(AuditData data) throws Exception {
    	long serverId = 0L;
    	if(CollectionUtils.isEmpty(SERVER_INFO_LIST)) {
    		SERVER_INFO_LIST = getServerInfoList();
    	}if(CollectionUtils.isNotEmpty(SERVER_INFO_LIST)) {
    		for(Map<String,Object> itr : SERVER_INFO_LIST) {
        		if(itr.get("private_ip").equals(data.getServer())) {
        			serverId = (long) itr.get("server_info_id");
        		}
        	}
    	}
    	return serverId;
    }

    private long checkMethod(String data) {
    	long methodId=0L;
    	if(CollectionUtils.isEmpty(METHOD_INFO_LIST)) {
    		METHOD_INFO_LIST = getMethodInfoList();
    	}if(CollectionUtils.isNotEmpty(METHOD_INFO_LIST)) {
    		for(Map<String,Object> itr : METHOD_INFO_LIST) {
        		if(itr.get("method").equals(data)) {
        			methodId = (long) itr.get("id");
            			return methodId;
        		}
        	}
    		
    	}
    	return 0L;
	}
    private long insertBuilder(Map<String, Object> prop, String tableName, List<FacilioField> fields) throws Exception {
    	 GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(tableName).fields(fields);
    	return insertBuilder.insert(prop);

    }
    
    private  List<Map<String, Object>> getServerInfoList() {
    	List<FacilioField> fields = new ArrayList<FacilioField>();
    	fields.addAll(SERVER_FIELDS);

    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(SEREVR_TABLE_NAME);
    	List<Map<String, Object>> prop = new ArrayList<>();
		try {
			prop = builder.get();
			if(CollectionUtils.isNotEmpty(prop)) {
				for(Map<String ,Object> itr : prop) {
	    			SERVER_INFO_LIST.add(itr);
	    		}
				
			}
		} catch (Exception e) {
			LOGGER.info("Exception Occurred In Getting ServerInfoList",e);
		}
		return prop;
		
    }
    private List<Map<String, Object>> getActionInfoList() {
    	
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
	    			ACTION_INFO_LIST.add(itr);
	    		}
				return prop;
				
			}
		} catch (Exception e) {
			LOGGER.info("Exception Occurred In Getting ActionInfoList",e);
		}
		return prop;
	}
    
    private List<Map<String, Object>> getModuleInfoList() {

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
	    			MODULE_INFO_LIST.add(itr);
	    		}
	    		
			}
		} catch (Exception e) {
			LOGGER.info("Exception Occurred In Getting ModuleInfoList",e);
		}
		return prop;
		
	}

    private List<Map<String, Object>> getMethodInfoList() {

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
	    			METHOD_INFO_LIST.add(itr);
	    		}
	    		return prop;
			}
		} catch (Exception e) {
			LOGGER.info("Exception Occurred In Getting MethodInfoList",e);
		}
		return prop;
	}
    
    private List<Map<String, Object>> getRefererInfoList() {

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
	    			REFERER_INFO_LIST.add(itr);
	    		}
	    		return prop;
			}
		} catch (Exception e) {
			LOGGER.info("Exception Occurred In Getting ModuleInfoList",e);
		}
		return prop;
	}
}
