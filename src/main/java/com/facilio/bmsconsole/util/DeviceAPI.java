package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.device.Device;
import com.facilio.bmsconsole.device.types.DistechControls;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import com.mysql.jdbc.Blob;

public class DeviceAPI 
{
	private static Logger logger = Logger.getLogger(DeviceAPI.class.getName());
	
	public static final Map<String, String> attributeMap = new LinkedHashMap<>();
	static
	{
		attributeMap.put("LINE_VOLTAGE_R", "R Line Voltage");
		attributeMap.put("PHASE_VOLTAGE_R", "R Phase Voltage");
		attributeMap.put("LINE_CURRENT_R", "R Line Current");
		attributeMap.put("POWER_FACTOR_R", "R Power Factor");
		attributeMap.put("FREQUENCY_R", "R Frequency");
		attributeMap.put("ACTIVE_POWER_R", "R Active Power");
		attributeMap.put("REACTIVE_POWER_R", "R Reactive Power");
		attributeMap.put("APPARENT_POWER_R", "R Apparent Power");
		attributeMap.put("PHASE_ENERGY_R", "R Phase Energy");

		attributeMap.put("LINE_VOLTAGE_Y", "Y Line Voltage");
		attributeMap.put("PHASE_VOLTAGE_Y", "Y Phase Voltage");
		attributeMap.put("LINE_CURRENT_Y", "Y Line Current");
		attributeMap.put("POWER_FACTOR_Y", "Y Power Factor");
		attributeMap.put("FREQUENCY_Y", "Y Frequency");
		attributeMap.put("ACTIVE_POWER_Y", "Y Active Power");
		attributeMap.put("REACTIVE_POWER_Y", "Y Reactive Power");
		attributeMap.put("APPARENT_POWER_Y", "Y Apparent Power");
		attributeMap.put("PHASE_ENERGY_Y", "Y Phase Energy");
		
		attributeMap.put("LINE_VOLTAGE_B", "B Line Voltage");
		attributeMap.put("PHASE_VOLTAGE_B", "B Phase Voltage");
		attributeMap.put("LINE_CURRENT_B", "B Line Current");
		attributeMap.put("POWER_FACTOR_B", "B Power Factor");
		attributeMap.put("FREQUENCY_B", "B Frequency");
		attributeMap.put("ACTIVE_POWER_B", "B Active Power");
		attributeMap.put("REACTIVE_POWER_B", "B Reactive Power");
		attributeMap.put("APPARENT_POWER_B", "B Apparent Power");
		attributeMap.put("PHASE_ENERGY_B", "B Phase Energy");
	}
	
	public static Map<String, String> getAttributes()
	{
		return attributeMap;
	}
	
	public static void addController(Long controllerId, int controllerType, String ipAddress, Blob certificate, int polltime, Long jobId) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Controller (CONTROLLER_ID, CONTROLLER_TYPE, IP_ADDRESS, CERTIFICATE, POLL_TIME, JOBID, JOB_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, controllerId);
			pstmt.setInt(2, controllerType);
			pstmt.setString(3, ipAddress);
			pstmt.setBlob(4, certificate);
			pstmt.setInt(5, polltime);
			pstmt.setLong(6, jobId);
			pstmt.setBoolean(7, true);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add controller");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding controller" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static void updateController(Long controllerId, Long jobId, boolean status) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Controller SET JOB_STATUS = ?, JOBID = ? WHERE CONTROLLER_ID = ?", Statement.RETURN_GENERATED_KEYS);
			pstmt.setBoolean(1, status);
			pstmt.setLong(2, jobId);
			pstmt.setLong(3, controllerId);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to update controller");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding controller" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<Map<String, Object>> getAllControllers(Long orgId) throws SQLException
	{
		List<Map<String, Object>> deviceList = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Controller LEFT JOIN Assets ON Controller.CONTROLLER_ID = Assets.ASSETID WHERE ORGID = ?");
			pstmt.setLong(1, orgId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				Map<String, Object> devices = new HashMap<>();
				devices.put("id", rs.getString("ASSETID"));
				devices.put("name", rs.getString("NAME"));
				devices.put("polltime", rs.getString("POLL_TIME"));
				devices.put("type", rs.getInt("CONTROLLER_TYPE"));
				devices.put("status", rs.getBoolean("JOB_STATUS"));
				deviceList.add(devices);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all controllers" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return deviceList;
	}
	
	public static Map<String, Object> getControllerInfo(long controllerId) throws SQLException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Controller LEFT JOIN Assets ON Controller.CONTROLLER_ID = Assets.ASSETID WHERE ASSETID = ?");
			pstmt.setLong(1, controllerId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				Map<String, Object> devices = new HashMap<>();
				devices.put("id", rs.getString("ASSETID"));
				devices.put("name", rs.getString("NAME"));
				devices.put("polltime", rs.getString("POLL_TIME"));
				devices.put("type", rs.getInt("CONTROLLER_TYPE"));
				devices.put("status", rs.getBoolean("JOB_STATUS"));
				return devices;
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all controllers" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static void discoverDevices(Long controllerId, Long orgId) throws Exception
	{
		String userName = "admin";
    	String password = "Admin@1234";
		String ipAddress = "192.168.0.148";
		List<String> deviceNames = new DistechControls(userName, password, ipAddress).getAllDeviceNames();
		System.out.println(deviceNames);
		
		Map<String, Long> deviceMap = new HashMap<>();
		for(String deviceName : deviceNames)
		{
			Long deviceId = AssetsAPI.getAssetId(deviceName, orgId);
			if(deviceId == null)
			{
				deviceId = AssetsAPI.addAsset(deviceName, orgId);
				DeviceAPI.addDevice(deviceId, null, null, null, controllerId, null, 1, 1);
			}
			deviceMap.put(deviceName, deviceId);
		}
		Map<String, Map<String, Object>> deviceInstances = getAllDeviceInstances(controllerId);
		JSONObject dataPoints = new DistechControls(userName, password, ipAddress).getAllDataPoints();
		Iterator<String> keys = dataPoints.keySet().iterator();
		while(keys.hasNext())
		{
			String key = keys.next();
			if(!deviceInstances.containsKey(key))
			{
				JSONObject json = (JSONObject) dataPoints.get(key);
				String deviceName = (String) json.get("description");
				String instanceName = (String) json.get("name");
				Long deviceId = deviceMap.get(deviceName);
				
				addDeviceInstance(deviceId, Integer.parseInt(key), instanceName);
			}
		}
	}
	
	public static void addDevice(Long assetId, Long serviceId, Long zoneid, Long buildingId, Long controllerId, Long parentDeviceId, int deviceType, int status) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			//pstmt = conn.prepareStatement("INSERT INTO Device (DEVICE_ID, SERVICE_ID, ZONE_ID, BUILDING_ID, CONTROLLER_ID, PARENT_DEVICE_ID, DEVICE_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt = conn.prepareStatement("INSERT INTO Device (DEVICE_ID, CONTROLLER_ID, DEVICE_TYPE, STATUS) VALUES (?, ?, ?, ?)");
			pstmt.setLong(1, assetId);
			pstmt.setLong(2, controllerId);
			pstmt.setInt(3, deviceType);
			pstmt.setInt(4, status);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add asset data");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding device" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static void updateDevice(Long deviceId, int status) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Device SET STATUS = ? WHERE DEVICE_ID = ?", Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, status);
			pstmt.setLong(2, deviceId);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to update device");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while update  device" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static void updateDeviceInstance(Long deviceId, int instanceId, String columnName) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Device_Instance SET COLUMN_NAME = ? WHERE DEVICE_ID = ? and INSTANCE_ID = ?", Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, columnName);
			pstmt.setLong(2, deviceId);
			pstmt.setInt(3, instanceId);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to update device instance");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while update  device instance" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static void addDeviceInstance(Long deviceId, int instanceId, String instanceName) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();

			pstmt = conn.prepareStatement("INSERT INTO Device_Instance (DEVICE_ID, INSTANCE_ID, INSTANCE_NAME) VALUES (?, ?, ?)");
			pstmt.setLong(1, deviceId);
			pstmt.setInt(2, instanceId);
			pstmt.setString(3, instanceName);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add asset data");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding device instance" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static Map<String, Map<String, Object>> getAllDeviceInstances(Long controllerId) throws SQLException
	{
		Map<String, Map<String, Object>> deviceInstances = new HashMap<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Device_Instance LEFT JOIN Device ON Device_Instance.DEVICE_ID = Device.DEVICE_ID WHERE CONTROLLER_ID = ?");
			pstmt.setLong(1, controllerId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				String key = String.valueOf(rs.getInt("INSTANCE_ID"));
				Map<String, Object> instanceMap = new HashMap<>();
				instanceMap.put("deviceId", rs.getLong("DEVICE_ID"));
				instanceMap.put("instanceName", rs.getString("INSTANCE_NAME"));
				deviceInstances.put(key, instanceMap);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all controllers" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return deviceInstances;
	}
	
	public static Map<String, Map<String, Object>> getActiveDeviceInstances(Long controllerId) throws SQLException
	{
		Map<String, Map<String, Object>> deviceInstances = new HashMap<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Device_Instance LEFT JOIN Device ON Device_Instance.DEVICE_ID = Device.DEVICE_ID WHERE CONTROLLER_ID = ?");
			pstmt.setLong(1, controllerId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				if(rs.getInt("STATUS") == 1)
				{
					continue;
				}
				String key = String.valueOf(rs.getInt("INSTANCE_ID"));
				Map<String, Object> instanceMap = new HashMap<>();
				instanceMap.put("deviceId", rs.getLong("DEVICE_ID"));
				instanceMap.put("instanceName", rs.getString("INSTANCE_NAME"));
				instanceMap.put("columnName", rs.getString("COLUMN_NAME"));
				deviceInstances.put(key, instanceMap);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all controllers" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return deviceInstances;
	}
	
	@SuppressWarnings("resource")
	public static void addDeviceData(Long timestamp, Map<String, Map<String, Double>> deviceDataMap) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			Iterator<String> iterator = deviceDataMap.keySet().iterator();
			while(iterator.hasNext())
			{
				Long deviceId = Long.parseLong(iterator.next());
				Map<String, Double> dataMap = deviceDataMap.get(deviceId.toString());
				
				String selectColumn =  "DEVICE_ID, ADDED_TIME";
				String values = "?, ?";
				Iterator<String> columns = dataMap.keySet().iterator();
				while(columns.hasNext())
				{
					String column = columns.next();
					selectColumn = selectColumn + ", " + column;
					values = values + ", ?";
				}
				
				pstmt = conn.prepareStatement("INSERT INTO Energy_Data (" + selectColumn + ") VALUES (" + values + ")", Statement.RETURN_GENERATED_KEYS);
				pstmt.setLong(1, deviceId);
				pstmt.setLong(2, timestamp);
				
				int i = 3;
				columns = dataMap.keySet().iterator();
				while(columns.hasNext())
				{
					String column = columns.next();
					pstmt.setDouble(i, dataMap.get(column));
					i++;
				}
				if(pstmt.executeUpdate() < 1) 
				{
					throw new RuntimeException("Unable to add device data");
				}
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding device job" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static Long getControllerId(Long jobId) throws SQLException
	{
		Long controllerId = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Controller where JOBID = ?");
			pstmt.setLong(1, jobId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				controllerId = rs.getLong("CONTROLLER_ID");
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting controller id" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return controllerId;
	}
	
	public static List<Map<String, Object>> getDeviceInstances(Long deviceId) throws SQLException
	{
		List<Map<String, Object>> deviceInstanceList = new ArrayList<Map<String, Object>>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Device_Instance WHERE DEVICE_ID = ?");
			pstmt.setLong(1, deviceId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				Map<String, Object> deviceInstanceMap = new HashMap<>();
				deviceInstanceMap.put("instanceId", rs.getString("INSTANCE_ID"));
				deviceInstanceMap.put("instancename", rs.getString("INSTANCE_NAME"));
				deviceInstanceList.add(deviceInstanceMap);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting devices" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return deviceInstanceList;
	}
	
	public static Map<Long, Device> getDevices(Long controllerId) throws SQLException
	{
		Map<Long, Device> deviceList = new HashMap<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Device LEFT JOIN Assets ON Device.DEVICE_ID = Assets.ASSETID WHERE CONTROLLER_ID = ?");
			pstmt.setLong(1, controllerId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				Device device = new Device()
						.setId(rs.getLong("DEVICE_ID"))
						.setName(rs.getString("NAME"))
						.setParentId(rs.getLong("PARENT_DEVICE_ID"))
						.setStatus(rs.getInt("STATUS"));
				deviceList.put(rs.getLong("DEVICE_ID"), device);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting devices" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return deviceList;
	}
	
	public static Long getDeviceId(Long controllerId) throws SQLException
	{
		Long deviceId = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Device where CONTROLLER_ID = ?");
			pstmt.setLong(1, controllerId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				deviceId = rs.getLong("DEVICE_ID");
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting device id" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return deviceId;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getDeviceData(Long deviceId) throws SQLException
	{
		JSONObject dataList = new JSONObject();
		JSONArray timeArray = new JSONArray();
		JSONArray valueArray = new JSONArray();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Energy_Data where DEVICE_ID = ?");
			pstmt.setLong(1, deviceId);
			rs = pstmt.executeQuery();
			timeArray.add("x");
			valueArray.add("kW");
			while(rs.next()) 
			{
				timeArray.add(rs.getLong("ADDED_TIME"));
				valueArray.add(rs.getLong("TOTAL_ENERGY_CONSUMPTION"));
			}
			dataList.put("x", timeArray);
			dataList.put("y", valueArray);
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting asset details" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return dataList;
	}
}
