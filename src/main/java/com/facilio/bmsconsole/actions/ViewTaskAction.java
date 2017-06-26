package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.util.ScheduleObjectAPI;
import com.facilio.bmsconsole.util.TaskAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class ViewTaskAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		TaskContext tc = TaskAPI.getTaskDetails(getTaskId(), orgId);
		
		if( tc != null) {
			taskProps = new HashMap<>();
			for(Object key : tc.keySet()) {
				if(!key.equals("connection") && !key.equals("schedule")) {
					taskProps.put((String) key, tc.get(key));
				}
			}
		}
		
		if(tc.getScheduleId() != 0) {
			ScheduleContext sc = ScheduleObjectAPI.getScheduleObjectDetails(orgId, tc.getScheduleId());
			if(sc != null) {
				scheduleProps = new HashMap<>();
				for(Object key : sc.keySet())  {
					if(!key.equals("connection") ) {
						scheduleProps.put((String) key, sc.get(key));
					}
				}
			}
		}
		
		List<NoteContext> taskNotes = TaskAPI.getNotesOfTask(getTaskId(), orgId);
		if(taskNotes != null && taskNotes.size() > 0) {
			setNotes(taskNotes);
		}
		
		return SUCCESS;
	}
	
	private Map<String, Object> taskProps;
	public Map<String, Object> getTaskProps() {
		return taskProps;
	}
	public void setTaskProps(Map<String, Object> taskProps) {
		this.taskProps = taskProps;
	}
	
	private Map<String, Object> scheduleProps;
	public Map<String, Object> getScheduleProps() {
		return scheduleProps;
	}
	public void setSchdeuleProps(Map<String, Object> scheduleProps) {
		this.scheduleProps = scheduleProps;
	}
	
	private List<NoteContext> notes;
	public List<NoteContext> getNotes() {
		return notes;
	}
	public void setNotes(List<NoteContext> notes) {
		this.notes = notes;
	}
	
	private long taskId;
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
}
