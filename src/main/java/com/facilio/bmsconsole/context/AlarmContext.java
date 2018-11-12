package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.dto.User;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.context.EventContext;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class AlarmContext extends TicketContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = LogManager.getLogger(AlarmContext.class.getName());
	private Boolean isAcknowledged;
	public boolean isAcknowledged() {
		if(isAcknowledged != null) {
			return isAcknowledged.booleanValue();
		}
		return false;
	}
	public void setIsAcknowledged(Boolean isAcknowledged) {
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

	private AlarmType alarmType;
	public int getAlarmType() {
		if(alarmType != null) {
			return alarmType.getIntVal();
		}
		return -1;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = AlarmType.TYPE_MAP.get(alarmType);
	}
	public void setAlarmType(AlarmType alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmTypeVal() {
		if(alarmType != null) {
			return alarmType.getStringVal();
		}
		return null;
	}
	public AlarmType getAlarmTypeEnum() {
		return alarmType;
	}
	
	private long controllerId = -1;
	public long getControllerId() {
		return controllerId;
	}
	
	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
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
					LOGGER.info("Exception occurred ", e1);
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
	public String getModifiedTimeString() {
		if(modifiedTime != -1) {
			return DateTimeUtil.getFormattedTime(modifiedTime);
		}
		return null;
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

	private String condition;
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}

	private String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	private String severityString;
	public String getSeverityString() {
		return severityString;
	}
	public void setSeverityString(String severityString) {
		this.severityString = severityString;
	}
	
	private AlarmSeverityContext previousSeverity;
	public AlarmSeverityContext getPreviousSeverity() {
		return previousSeverity;
	}
	public void setPreviousSeverity(AlarmSeverityContext previousSeverity) {
		this.previousSeverity = previousSeverity;
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
	
	private Boolean isWoCreated;
	public Boolean getIsWoCreated() {
		return isWoCreated;
	}
	public void setIsWoCreated(Boolean isWoCreated) {
		this.isWoCreated = isWoCreated;
	}
	public boolean isWoCreated() {
		if(isWoCreated != null) {
			return isWoCreated.booleanValue();
		}
		return false;
	}
	
	private String problem;
	public String getProblem() {
		return problem;
	}
	public void setProblem(String problem) {
		this.problem = problem;
	}
	
	private String possibleCauses;
	public String getPossibleCauses() {
		return possibleCauses;
	}
	public void setPossibleCauses(String possibleCauses) {
		this.possibleCauses = possibleCauses;
	}
	
	private String recommendation;
	public String getRecommendation() {
		return recommendation;
	}
	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}
	
	private Boolean autoClear;
	public Boolean getAutoClear() {
		return autoClear;
	}
	public void setAutoClear(Boolean autoClear) {
		this.autoClear = autoClear;
	}
	public boolean isAutoClear() {
		if (autoClear != null) {
			return autoClear.booleanValue();
		}
		return false;
	}
	
	private long woId = -1;
	public long getWoId() {
		return woId;
	}
	public void setWoId(long woId) {
		this.woId = woId;
	}
	
	public static enum AlarmType {
		MAINTENANCE(1, "Maintenance"),
		CRITICAL(2, "Critical"),
		LIFE_SAFETY(3, "Life Safety"),
		NORMAL(4, "Normal"),
		ENERGY(5,"Energy"),
		FIRE(6,"Fire"),
		HVAC(7,"Hvac");
		
		
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
	
//	public List<HashMap<String, Object>> getFollowers() throws Exception {
//		if(getId() == -1) {
//			return null;
//		}
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try
//		{
//			conn = FacilioConnectionPool.INSTANCE.getConnection();
//			pstmt = conn.prepareStatement("SELECT * FROM AlarmFollowers WHERE ALARM_ID=?");
//			pstmt.setLong(1, this.getId());
//			
//			List<HashMap<String, Object>> followers = new ArrayList<>();
//			
//			rs = pstmt.executeQuery();
//			while (rs.next()) {
//				HashMap<String, Object> hm = new HashMap<String, Object>();
//				hm.put("id", rs.getLong("ID"));
//				hm.put("type", rs.getString("FOLLOWER_TYPE"));
//				hm.put("value", rs.getString("FOLLOWER"));
//				
//				followers.add(hm);
//			}
//			return followers;
//		}
//		catch(SQLException | RuntimeException e) 
//		{
//			throw e;
//		}
//		finally 
//		{
//			DBUtil.closeAll(conn, pstmt, rs);
//		}
//	}
	
	public String getUrl() {
		if(super.getId() == -1) {
			return null;
		}
		else {
			return AwsUtil.getConfig("clientapp.url")+"/app/fa/alarms/summary/"+super.getId();
		}
	}
	
	public String getMobileUrl() {
		if(super.getId() != -1) {
			return AwsUtil.getConfig("clientapp.url")+"/mobile/alarms/summary/"+getId();
		}
		else {
			return null;
		}
	}
	
	List<EventContext> relatedEvents;
	public List<EventContext> getRelatedEvents() {
		return relatedEvents;
	}
	public void setRelatedEvents(List<EventContext> relatedEvents) {
		this.relatedEvents = relatedEvents;
	}
	public void addrelatedEvent(EventContext relatedEvent) {
		if(this.relatedEvents == null) {
			this.relatedEvents = new ArrayList<>();
		}
		relatedEvents.add(relatedEvent);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Alarm : ["+getId()+", "+getSubject()+"]";
	}
}
