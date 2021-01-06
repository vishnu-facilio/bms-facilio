package con.facilio.control;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.modules.FieldUtil;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ControlGroupRoutineContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	ControlGroupContext controlGroup;
	JSONObject scheduleJson;
	Integer sequence;
	
	public ScheduleInfo scheduleAsObj() throws Exception {
		if(getScheduleJson() != null) {
			return FieldUtil.getAsBeanFromJson(getScheduleJson(), ScheduleInfo.class);
		}
		return null;
	}
	
	List<ControlGroupRoutineSectionContext> sections;
	
	public void addSection(ControlGroupRoutineSectionContext section) {
		this.sections = this.sections == null ? new ArrayList<ControlGroupRoutineSectionContext>() : this.sections; 
		
		this.sections.add(section);
	}
}
