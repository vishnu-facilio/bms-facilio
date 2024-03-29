package com.facilio.control;

import java.util.List;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.v3.context.V3Context;

public class ControlGroupContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private SiteContext site;
	private BaseSpaceContext space;
	private ControlScheduleContext controlSchedule;
	
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
	
	List<ControlGroupSection> sections;

	List<ControlGroupRoutineContext> routines;

	public List<ControlGroupRoutineContext> getRoutines() {
		return routines;
	}
	public void setRoutines(List<ControlGroupRoutineContext> routines) {
		this.routines = routines;
	}
	
	public List<ControlGroupSection> getSections() {
		return sections;
	}
	public void setSections(List<ControlGroupSection> sections) {
		this.sections = sections;
	}
	
	
}
