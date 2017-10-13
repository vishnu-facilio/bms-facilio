package com.facilio.bmsconsole.context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class AlarmContext extends TicketContext {
	private AlarmStatus alarmStatus;
	public int getAlarmStatus() {
		if(alarmStatus != null) {
			return alarmStatus.getIntVal();
		}
		return -1;
	}
	public void setAlarmStatus(int alarmStatus) {
		this.alarmStatus = AlarmStatus.statusMap.get(alarmStatus);
	}
	public void setAlarmStatus(AlarmStatus alarmStatus) {
		this.alarmStatus = alarmStatus;
	}
	public String getAlarmStatusVal() {
		if(alarmStatus != null) {
			return alarmStatus.getStringVal();
		}
		return null;
	}
	
	private Boolean isAcknowledged;
	public void setIsAcknowledged(Boolean isAcknowledged) {
		this.isAcknowledged = isAcknowledged;
	}
	public Boolean getIsAcknowledged() {
		return this.isAcknowledged;
	}
	
	private UserContext acknowledgedBy;
	public UserContext getAcknowledgedBy() {
		return acknowledgedBy;
	}
	public void setAcknowledgedBy(UserContext acknowledgedBy) {
		this.acknowledgedBy = acknowledgedBy;
	}

	private long acknowledgedTime;
	public long getAcknowledgedTime() {
		return acknowledgedTime;
	}
	public void setAcknowledgedTime(long acknowledgedTime) {
		this.acknowledgedTime = acknowledgedTime;
	}

	private AlarmType type;
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = AlarmType.typeMap.get(type);
	}
	public void setType(AlarmType type) {
		this.type = type;
	}
	public String getTypeVal() {
		if(type != null) {
			return type.getStringVal();
		}
		return null;
	}

	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setCreatedTime(String createdTime) {
		if(createdTime != null && !createdTime.isEmpty()) {
			try {
				this.createdTime = FacilioConstants.HTML5_DATE_FORMAT.parse(createdTime).getTime();
			}
			catch (ParseException e) {
				try {
					this.createdTime = FacilioConstants.HTML5_DATE_FORMAT_1.parse(createdTime).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private long clearedTime;
	public long getClearedTime() {
		return clearedTime;
	}
	public void setClearedTime(long clearedTime) {
		this.clearedTime = clearedTime;
	}

	private long deviceId = -1;
	public long getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	public static enum AlarmStatus {
		ACTIVE(1, "Active"),
		SUPPRESS(2, "Suppressed"),
		CLEAR(3, "Cleared");
		
		private int intVal;
		private String strVal;
		
		private AlarmStatus(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, AlarmStatus> statusMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, AlarmStatus> initTypeMap() {
			Map<Integer, AlarmStatus> typeMap = new HashMap<>();
			
			for(AlarmStatus type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, AlarmStatus> getAllTypes() {
			return statusMap;
		}
	}
	
	public static enum AlarmType {
		MAINTENANCE(1, "Maintenance"),
		CRITICAL(2, "Critical"),
		LIFE_SAFETY(3, "Life Safety"),
		NORMAL(4, "Normal");
		
		private int intVal;
		private String strVal;
		
		private AlarmType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		public static AlarmType getType(int val) {
			return typeMap.get(val);
		}
		
		private static final Map<Integer, AlarmType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, AlarmType> initTypeMap() {
			Map<Integer, AlarmType> typeMap = new HashMap<>();
			
			for(AlarmType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, AlarmType> getAllTypes() {
			return typeMap;
		}
	}
	
	public List<HashMap<String, Object>> getFollowers() throws Exception {
		if(getId() == -1) {
			return null;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM AlarmFollowers WHERE ALARM_ID=?");
			pstmt.setLong(1, this.getId());
			
			List<HashMap<String, Object>> followers = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("id", rs.getLong("ID"));
				hm.put("type", rs.getString("FOLLOWER_TYPE"));
				hm.put("value", rs.getString("FOLLOWER"));
				
				followers.add(hm);
			}
			return followers;
		}
		catch(SQLException | RuntimeException e) 
		{
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public String getUrl() {
		if(super.getId() == -1) {
			return null;
		}
		else {
			return "http://fazilio.com/app/firealarm/alarms";
		}
	}
}
