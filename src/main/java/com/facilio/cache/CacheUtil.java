package com.facilio.cache;

import java.io.Serializable;
import java.security.Key;
import java.util.StringJoiner;

import org.apache.log4j.LogManager;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;

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

	public static final String METRIC_KEY(long orgId , int metricId){
		return ORG_KEY(orgId) + KEY_SEPARATOR + METRIC + KEY_SEPARATOR + metricId;
	}

	public static String ORG_KEY(long orgId) {
		return ORG + KEY_SEPARATOR + orgId;
	}

	public static String USER_KEY(long userId) {
		return USER + KEY_SEPARATOR + userId;
	}

	public static String MODULE_KEY(long orgId, String moduleName) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + MODULES + KEY_SEPARATOR + moduleName;
	}

	public static String MODULE_KEY(long orgId, long moduleId) {
		return ORG_KEY(orgId) + KEY_SEPARATOR + MODULES + KEY_SEPARATOR + moduleId;
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