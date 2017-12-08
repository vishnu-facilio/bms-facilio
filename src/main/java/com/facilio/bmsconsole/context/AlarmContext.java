package com.facilio.bmsconsole.context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.dto.User;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class AlarmContext extends TicketContext {
	
	private Boolean isAcknowledged;
	public boolean isAcknowledged() {
		if(isAcknowledged != null) {
			return isAcknowledged.booleanValue();
		}
		return false;
	}
	public void setIsAcknowledged(boolean isAcknowledged) {
		this.isAcknowledged = isAcknowledged;
	}
	public Boolean getIsAcknowledged() {
		return this.isAcknowledged;
	}
	
	private User acknowledgedBy;
	public User getAcknowledgedBy() {
		return acknowledgedBy;
	}
	public void setAcknowledgedBy(User acknowledgedBy) {
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
		this.type = AlarmType.TYPE_MAP.get(type);
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
			catch (java.text.ParseException e) {
				try {
					this.createdTime = FacilioConstants.HTML5_DATE_FORMAT_1.parse(createdTime).getTime();
				} catch (java.text.ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private long modifiedTime = -1;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	private long clearedTime = -1;
	public long getClearedTime() {
		return clearedTime;
	}
	public void setClearedTime(long clearedTime) {
		this.clearedTime = clearedTime;
	}
	
	private User clearedBy;
	public User getClearedBy() {
		return clearedBy;
	}
	public void setClearedBy(User clearedBy) {
		this.clearedBy = clearedBy;
	}

	private String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	private String node;
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	
	private String severityString;
	public String getSeverityString() {
		return severityString;
	}
	public void setSeverityString(String severityString) {
		this.severityString = severityString;
	}

	private AlarmSeverityContext severity;
	public AlarmSeverityContext getSeverity() {
		return severity;
	}
	public void setSeverity(AlarmSeverityContext severity) {
		this.severity = severity;
	}

	private String alarmPriority;
	public String getAlarmPriority() {
		return alarmPriority;
	}
	public void setAlarmPriority(String alarmPriority) {
		this.alarmPriority = alarmPriority;
	}

	private String alarmClass;
	public String getAlarmClass() {
		return alarmClass;
	}
	public void setAlarmClass(String alarmClass) {
		this.alarmClass = alarmClass;
	}
	
	private String state;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public JSONObject additionInfo;
	public JSONObject getAdditionInfo() {
		return additionInfo;
	}
	public void setAdditionInfo(JSONObject additionInfo) {
		this.additionInfo = additionInfo;
	}
	public void addAdditionInfo(String key, Object value) {
		if(this.additionInfo == null) {
			this.additionInfo =  new JSONObject();
		}
		this.additionInfo.put(key,value);
	}
	
	public String getAdditionalInfoJsonStr() {
		if(additionInfo != null) {
			return additionInfo.toJSONString();
		}
		return null;
	}
	public void setAdditionalInfoJsonStr(String jsonStr) throws ParseException {
		JSONParser parser = new JSONParser();
		additionInfo = (JSONObject) parser.parse(jsonStr);
	}
	
	private long noOfEvents = -1;
	public long getNoOfEvents() {
		return noOfEvents;
	}
	public void setNoOfEvents(long noOfEvents) {
		this.noOfEvents = noOfEvents;
	}

	public static enum AlarmType {
		MAINTENANCE(1, "Maintenance"),
		CRITICAL(2, "Critical"),
		LIFE_SAFETY(3, "Life Safety"),
		NORMAL(4, "Normal"),
		ENERGY(5,"Energy");
		
		
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
			return TYPE_MAP.get(val);
		}
		
		private static final Map<Integer, AlarmType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, AlarmType> initTypeMap() {
			Map<Integer, AlarmType> typeMap = new HashMap<>();
			
			for(AlarmType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, AlarmType> getAllTypes() {
			return TYPE_MAP;
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
