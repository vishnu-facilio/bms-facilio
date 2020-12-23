package con.facilio.control;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.modules.FieldUtil;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.v3.context.V3Context;

public class ControlGroupRoutineContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	ControlGroupContext controlGroup;
	JSONObject scheduleJson;
	Integer sequence;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ControlGroupContext getControlGroup() {
		return controlGroup;
	}
	public void setControlGroup(ControlGroupContext controlGroup) {
		this.controlGroup = controlGroup;
	}
	public JSONObject getScheduleJson() {
		return scheduleJson;
	}
	public void setScheduleJson(JSONObject scheduleJson) {
		this.scheduleJson = scheduleJson;
	}
	public Integer getSequence() {
		return sequence;
	}
	
	public ScheduleInfo scheduleAsObj() throws Exception {
		if(getScheduleJson() != null) {
			return FieldUtil.getAsBeanFromJson(getScheduleJson(), ScheduleInfo.class);
		}
		return null;
	}
	
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	List<ControlGroupFieldContext> fields;
	
	public List<ControlGroupFieldContext> getFields() {
		return fields;
	}
	public void setFields(List<ControlGroupFieldContext> fields) {
		this.fields = fields;
	}
	public void addField(ControlGroupFieldContext field) {
		this.fields = this.fields == null ? new ArrayList<ControlGroupFieldContext>() : this.fields; 
		
		this.fields.add(field);
	}
}
