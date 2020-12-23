package con.facilio.control;

import java.util.List;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.v3.context.V3Context;

public class ControlGroupContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	SiteContext site;
	BaseSpaceContext space;
	ControlScheduleContext controlSchedule;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SiteContext getSite() {
		return site;
	}
	public void setSite(SiteContext site) {
		this.site = site;
	}
	public BaseSpaceContext getSpace() {
		return space;
	}
	public void setSpace(BaseSpaceContext space) {
		this.space = space;
	}
	public ControlScheduleContext getControlSchedule() {
		return controlSchedule;
	}
	public void setControlSchedule(ControlScheduleContext controlSchedule) {
		this.controlSchedule = controlSchedule;
	}
	
	List<ControlGroupAssetCategory> categories;

	public List<ControlGroupAssetCategory> getCategories() {
		return categories;
	}
	public void setCategories(List<ControlGroupAssetCategory> categories) {
		this.categories = categories;
	}

	List<ControlGroupRoutineContext> routines;

	public List<ControlGroupRoutineContext> getRoutines() {
		return routines;
	}
	public void setRoutines(List<ControlGroupRoutineContext> routines) {
		this.routines = routines;
	}
	
	
}
