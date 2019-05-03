package com.facilio.sql;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.transaction.FacilioConnectionPool;

public class DBUtil {
	private static final Logger LOGGER = LogManager.getLogger(DBUtil.class.getName());
	private static final String DB_PROPERTY_FILE = "conf/db/";
	private static final Properties PROPERTIES = new Properties();
	private static Boolean executeSingleStatement;
	private static final Object LOCK = new Object();
	private static final List<String> TABLES_WITHOUT_ORGID= Collections.unmodifiableList(initOrgIdNotRequired());
	private static final HashSet<String> CACHE_ENABLED_TABLES = new HashSet<>();
	private static final HashSet<Long> CACHE_ENABLED_ORG = new HashSet<>();

	public static void close (Statement stmt) {
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing statement ", e);
			}
		}
	}
	
	public static void close (Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing connection ", e);
			}
		}
	}
	
	
	private static List<String> initOrgIdNotRequired() {
		
		List<String> tablesWithoutOrgId = new ArrayList<>();
		
		tablesWithoutOrgId.add("Users");
		tablesWithoutOrgId.add("User_Mobile_Setting");
		tablesWithoutOrgId.add("UserSessions");
		tablesWithoutOrgId.add("TVPasscodes");
		tablesWithoutOrgId.add("Remote_Screens"); // temp
		tablesWithoutOrgId.add("server_info");
		tablesWithoutOrgId.add("faciliousers");
		tablesWithoutOrgId.add("faciliorequestors");
		tablesWithoutOrgId.add("SupportEmails");
		tablesWithoutOrgId.add("Service");
		tablesWithoutOrgId.add("ClientApp");
		tablesWithoutOrgId.add("Weather_Stations");
		tablesWithoutOrgId.add("Organizations");
		tablesWithoutOrgId.add("WorkOrderRequest_EMail");
		tablesWithoutOrgId.add("MobileDetails");
		

		return tablesWithoutOrgId;
	}
	
	static boolean isTableWithoutOrgId(String tableName) {
		return TABLES_WITHOUT_ORGID.contains(tableName);
	}
	
	static FacilioField getOrgIdField(String tableName) {
		FacilioField field = new FacilioField();
		field.setName("orgId");
		field.setDisplayName("Org Id");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName(tableName+".ORGID");
		
		return field;
	}
	
	public static void close (ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing resultset ", e);
			}
		}
	}
	
	public static void closeAll(Connection conn, Statement stmt, ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing resultset ", e);
			}
		}
		closeAll(conn, stmt);
	}
	
	public static void closeAll(Connection conn, Statement stmt) {
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing statement ", e);
			}
		}
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing connection ", e);
			}
		}
	}
	
	public static void closeAll(Statement stmt, ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing resultset ", e);
			}
		}
		
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing statement ", e);
			}
		}
	}
	
	public static HashMap getRecord(String query) throws Exception
	{
		Connection c = FacilioConnectionPool.getInstance().getConnection();
		Statement stmt =null;
		ResultSet rs = null;
		
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next())
			{
				ResultSetMetaData md = rs.getMetaData();
				  int columns = md.getColumnCount();
				HashMap row = new HashMap(columns);
			     for(int i=1; i<=columns; ++i){           
			      row.put(md.getColumnName(i),rs.getObject(i));
			     }
			     return row;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.ERROR, "Exception while getting record ", e);
			throw e;
		}
		finally
		{
			closeAll(c, stmt, rs);
		}
		return null;
	}

	public static String getQuery(String key) {
		String value = PROPERTIES.getProperty(key);
        if (value != null ) {
        	value = value.trim();
        	if(value.length() > 0) {
        		return value;
        	}
        }
		return null;
	}
	
	public static boolean getDBSQLScriptRunnerMode() {
		if (executeSingleStatement == null) {
			synchronized (LOCK) {
				if (executeSingleStatement == null) {
					executeSingleStatement = Boolean.parseBoolean(getQuery("db.executeSingleStatement"));
				}
			}
		}
		return executeSingleStatement;
	}
	
	static {
		loadProperties();
	}

	private static void loadProperties() {
		String propertyFile = DB_PROPERTY_FILE + AwsUtil.getDB() + ".properties";
		URL resource = AwsUtil.class.getClassLoader().getResource(propertyFile);
		if (resource != null) {
			try (InputStream stream = resource.openStream()) {
				PROPERTIES.load(stream);
				addCacheEnabledOrgs();
				addCacheEnabledTables();


			} catch (IOException e) {
				LOGGER.info("Exception while trying to load property file " + propertyFile);
			}
		}
	}

	private static void addCacheEnabledTables() {
		try {
			String tables = PROPERTIES.getProperty("query.cache.tables");
			LOGGER.info("Loaded cache enabled tables" + tables);
			if (tables != null) {
				CACHE_ENABLED_TABLES.addAll(Arrays.asList(tables.split(",")));
			}
		} catch (Exception e) {
			LOGGER.info("Exception while trying to load query cache tables");
		}
	}

	private static void addCacheEnabledOrgs() {
		try {
			String orgIds = PROPERTIES.getProperty("query.cache.orgId");
			LOGGER.info("Loaded cache enabled orgs" + orgIds);
			if (orgIds != null) {
				Arrays.stream(orgIds.split(",")).forEach(orgId -> CACHE_ENABLED_ORG.add(Long.parseLong(orgId.trim())));
			}
		} catch (Exception e) {
			LOGGER.info("Exception while trying to load query cache tables");
		}
	}

	static boolean isQueryCacheEnabled(long orgId, String tableName) {
		if(AwsUtil.isProduction()) {
			return false;
		}
		return CACHE_ENABLED_ORG.contains(orgId) && CACHE_ENABLED_TABLES.contains(tableName);
	}

}
