package com.facilio.cache;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.webtab.ModuleTypeHandler;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import org.apache.log4j.LogManager;

import java.io.Serializable;
import java.util.StringJoiner;

public class CacheUtil {

	private static org.apache.log4j.Logger log = LogManager.getLogger(CacheUtil.class.getName());

	public static final String KEY_SEPARATOR = "#";

	public static final String ORG = "org";

	public static final String MODULES = "modules";

	public static final String SUB_MODULES = "subModules";

	public static final String FIELDS = "fields";

	public static final String FIELD = "field";

	public static final String PRIMARY = "primary";

	public static final String RESPONSE = "response";

	public static final String USER = "user";

	public static final String METRIC = "metric";

	public static final String ROLE_ID = "roleId";

	public static final String ROLE_NAME = "roleName";

	public static final String AGENT_NAME = "agentName";

	public static final String APP_ID = "appId";

	public static final String SCOPE_ID = "scopeId";
	public static final String TAB_ID = "tabId";

	public static final String TAB_GROUP_ID = "tabGroupId";

	public static final String APP_LAYOUT_ID = "appLayoutId";

	public static final String NS_FIELD_ID = "nsReadingFieldId";

	public static final String VALUE_GENERATOR_ID = "valueGeneratorId";

	public static final String PEOPLE_ID = "peopleId";

	public static final String ORG_USER_ID = "orgUserId";

	public static final String PERMISSION_SET_PROP_KEY = "permissionSetPropKey";


	public static final String AGENT_ID = "agentId";

	public static final String CONTROLLER_TYPE = "controllerType";

	public static final String CONTROLLER_IDENTIFIER = "controllerIdentifier";

	public static final String ROLE_ID_KEY ( long orgId,long roleId ) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + ROLE_ID + KEY_SEPARATOR + roleId;
	}

	public static final String ROLE_NAME_KEY ( long orgId,String roleName ) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + ROLE_NAME + KEY_SEPARATOR + roleName;
	}

	public static final String METRIC_KEY(long orgId , int metricId){
		return ORG_KEY(orgId) + KEY_SEPARATOR + METRIC + KEY_SEPARATOR + metricId;
	}

	public static String ORG_KEY(long orgId) {
		return ORG + KEY_SEPARATOR + orgId;
	}

	public static String USER_KEY(long userId) {
		return USER + KEY_SEPARATOR + userId;
	}

	public static String ORG_USER_KEY(long orgUserId) {
		return ORG_USER_ID + KEY_SEPARATOR + orgUserId;
	}
	public static String MODULE_KEY(long orgId, String moduleName) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + MODULES + KEY_SEPARATOR + moduleName;
	}

	public static String MODULE_KEY(long orgId, long moduleId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + MODULES + KEY_SEPARATOR + moduleId;
	}

	public static String TABGROUPID_KEY(long groupId) {
		return TAB_GROUP_ID + KEY_SEPARATOR + groupId;
	}

	public static String APP_LAYOUT_KEY(long layoutId) {
		return APP_LAYOUT_ID + layoutId;
	}

	public static String ORG_APP_KEY(long orgId, long appId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + APPID_KEY(appId);
	}

	public static String TABID_KEY(long tabId) {
		return TAB_ID + KEY_SEPARATOR + tabId;
	}

	public static String VALUE_GENERATOR_KEY(long valueGenId) {
		return VALUE_GENERATOR_ID + KEY_SEPARATOR + valueGenId;
	}

	public static String ORG_TAB_KEY(long orgId, long tabId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + TABID_KEY(tabId);
	}

	public static String ORG_APP_LAYOUT_KEY(long orgId, long layoutId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + APP_LAYOUT_KEY(layoutId);
	}

	public static String ORG_TAB_GROUP_KEY(long orgId, long groupId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + TABGROUPID_KEY(groupId);
	}

	public static String ORG_VAL_GEN_KEY(long orgId, long valGenId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + VALUE_GENERATOR_KEY(valGenId);
	}

	public static String SUB_MODULE_KEY(long orgId, String moduleName) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + SUB_MODULES + KEY_SEPARATOR + moduleName;
	}

	public static String SUB_MODULE_KEY(long orgId, long moduleId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + SUB_MODULES + KEY_SEPARATOR + moduleId;
	}

	public static String SUB_MODULE_KEY(long orgId, String moduleName, ModuleType... types) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + SUB_MODULES + KEY_SEPARATOR + getModuleTypes(types) + KEY_SEPARATOR  + moduleName;
	}

	public static String SUB_MODULE_KEY(long orgId, long moduleId, ModuleType... types) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + SUB_MODULES + KEY_SEPARATOR + getModuleTypes(types) + KEY_SEPARATOR  + moduleId;
	}

	public static String FIELDS_KEY(long orgId, String moduleName) {
		return MODULE_KEY(orgId, moduleName) + KEY_SEPARATOR + FIELDS;
	}

	public static String PERMISSIBLE_FIELDS_KEY(long orgId, String moduleName, int permissionType, long roleId) {
		return MODULE_KEY(orgId, moduleName) + KEY_SEPARATOR + permissionType + KEY_SEPARATOR + roleId + KEY_SEPARATOR + FIELDS;
	}

	public static String PERMISSIBLE_SUB_MODULES_KEY(long orgId, long moduleId, int permissionType, long roleId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + moduleId + KEY_SEPARATOR + permissionType + KEY_SEPARATOR + roleId + KEY_SEPARATOR + SUB_MODULES;
	}

	public static String FIELD_KEY(long orgId, long fieldId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + fieldId + KEY_SEPARATOR + FIELD;
	}

	public static String FIELD_NAME_KEY(long orgId, String fieldName, String moduleName) {
		return FIELD_NAME_KEY_FOR_REMOVAL(orgId, fieldName) + moduleName + KEY_SEPARATOR + FIELD;
	}
	public static String FIELD_NAME_KEY_FOR_REMOVAL (long orgId, String fieldName) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + fieldName+ KEY_SEPARATOR;
	}

	public static String PRIMARY_FIELD_KEY(long orgId, String moduleName) {
		return FIELDS_KEY(orgId, moduleName) + KEY_SEPARATOR + PRIMARY;
	}

	public static String RESPONSE_KEY(long orgId, long userId, String uri, String hashedParam) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + USER_KEY(userId) + KEY_SEPARATOR + uri + KEY_SEPARATOR + hashedParam;
	}

	public static String PERMISSION_SET_KEY (long orgId,long orgUserId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + ORG_USER_KEY(orgUserId);
	}
	public static String AGENT_KEY(long orgId, String actualAgentName) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + AGENT_NAME + KEY_SEPARATOR + actualAgentName;
	};

	public static String APPID_KEY(long appId) {
		return APP_ID + KEY_SEPARATOR + appId;
	}

	public static String ALARM_TYPE_KEY(long orgId, String linkName) {
		return ORG_KEY(orgId) + ALARM_TYPE + KEY_SEPARATOR + linkName;
	}
	public static String ALARM_TYPE_KEY(long alarmTypeId) {
		return ALARM_TYPE + KEY_SEPARATOR + alarmTypeId;
	}
	public static String SCOPEID_KEY(long scopeId) {
		return SCOPE_ID + KEY_SEPARATOR + scopeId;
	}

	public static String PEOPLE_ID_KEY(long orgId, long peopleId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + PEOPLE_ID + KEY_SEPARATOR + peopleId;
	}
	public static String GLOBAL_SCOPE_VARIABLE_KEY(long orgId, long appId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + APPID_KEY(appId);
	};

	public static String USER_SCOPE_CONFIG_VARIABLE_KEY(long orgId, long scopeId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + SCOPEID_KEY(scopeId);
	};

	public static String NAMESPACE_KEY(long orgId, long nsId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + nsId ;
	}
	
	public static String NAMESPACE_PARENT_KEY(long orgId, long parentId, int type) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + parentId + KEY_SEPARATOR + type;
	}

	public static String NAMESPACE_IDS_KEY(long orgId, long fieldId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + NS_FIELD_ID + KEY_SEPARATOR + fieldId;
	}


	public static String PERMISSION_SET_PERMISSION_KEY(long orgId, String propKey, long permissionSetId, String fieldEnum) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + PERMISSION_SET_PROP_KEY + propKey + KEY_SEPARATOR + permissionSetId + KEY_SEPARATOR + fieldEnum;
	}

	public static String CONTROLLER_KEY(long orgId, long agentId, int controllerType , String controllerIdentifier){
		return ORG_KEY(orgId) + KEY_SEPARATOR + AGENT_ID + KEY_SEPARATOR + agentId + KEY_SEPARATOR + CONTROLLER_TYPE + KEY_SEPARATOR + controllerType + KEY_SEPARATOR + CONTROLLER_IDENTIFIER + KEY_SEPARATOR + controllerIdentifier;
	}


	public static final String CLIENT_ID = "clientId";
	public static final String ALARM_DEFINITION_TAGGING = "alarmDefinitionTagging";

	public static final String ALARM_TYPE = "alarmType";

	public static String CLIENTID_KEY(long clientId) {
		return CLIENT_ID + KEY_SEPARATOR + clientId;
	}

	public static String CLIENT_ID_KEY(long orgId, long clientId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + CLIENTID_KEY(clientId);
	}

	public static String ALARM_DEFINITION_TAGGING_KEY(long alarmDefinitionId) {
		return ALARM_DEFINITION_TAGGING + KEY_SEPARATOR + alarmDefinitionId;
	}

	public static String ALARM_DEFINITION_TAGGING_ID_KEY(long orgId, long alarmDefinitionId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + ALARM_DEFINITION_TAGGING_KEY(alarmDefinitionId);
	}

	private static final String FLAGGED_EVENT = "flaggedEvent";
	public static String FLAGGED_EVENT_KEY(long flaggedEventId) {
		return FLAGGED_EVENT + KEY_SEPARATOR + flaggedEventId;
	}

	public static String FLAGGED_EVENT_ID_KEY(long orgId, long flaggedEventId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + FLAGGED_EVENT_KEY(flaggedEventId);
	}


	private static final String ALARM_DEFINITION = "alarmDefinition";
	private static final String CONTROLLER = "controller";

	public static String ALARM_ASSET_TAGGING_KEY(long orgId, long clientId,long controllerId,long alarmDefinitionId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR  + CLIENTID_KEY(clientId) + KEY_SEPARATOR + CONTROLLER + KEY_SEPARATOR + controllerId + KEY_SEPARATOR + ALARM_DEFINITION + KEY_SEPARATOR + alarmDefinitionId;
	}
	public static boolean isCacheEnabled() {
		// return RedisManager.getInstance().isRedisEnabled();
        return false;
	}

	public static boolean set(String key, Serializable obj) {
		
		/*if (!isCacheEnabled()) {
			return false;
		}
		
		Jedis conn = null;
		try {
			byte[] bytes = SerializationUtils.serialize(obj);
			
			conn = RedisManager.getInstance().getJedis();
			conn.set(key.getBytes(), bytes);
			return true;
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}*/
		return false;
	}

	private static String getModuleTypes(FacilioModule.ModuleType... types) {
		StringJoiner joiner = new StringJoiner(",");
		for (ModuleType type : types) {
			joiner.add(String.valueOf(type.getValue()));
		}
		return "("+joiner.toString()+")";
	}

	public static Serializable get(String key) {
		
		/*if (!isCacheEnabled()) {
			return null;
		}
		
		Jedis conn = null;
		try {
			conn = RedisManager.getInstance().getJedis();
			byte[] bytes = conn.get(key.getBytes());
			if (bytes != null) {
				return SerializationUtils.deserialize(bytes);
			}
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}*/
		return null;
	}

	public static boolean delete(String... key) {
		
		/*if (!isCacheEnabled()) {
			return false;
		}
		
		Jedis conn = null;
		try {
			conn = RedisManager.getInstance().getJedis();
			conn.del(key);
			return true;
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}*/
		return false;
	}
}