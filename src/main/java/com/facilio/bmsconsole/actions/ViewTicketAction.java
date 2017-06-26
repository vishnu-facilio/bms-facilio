package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.TaskAPI;
import com.facilio.bmsconsole.util.TicketApi;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class ViewTicketAction extends ActionSupport {
	
	
	@Override
	public String execute() throws Exception {
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		TicketContext tc = TicketApi.getTicketDetails(getTicketId(), orgId);
		
		if( tc != null) {
			ticket = new HashMap<>();
			for(Object key : tc.keySet()) {
				if(!key.equals("connection")) {
					ticket.put((String) key, tc.get(key));
				}
			}
			
			List<TaskContext> taskList = TaskAPI.getTasksOfTicket(orgId, ticketId);
			if(taskList != null && taskList.size() > 0) {
				this.tasks = taskList;
			}
		}
		
		return SUCCESS;
	}
	
	private Map<String, Object> ticket;
	public Map<String, Object> getTicket() {
		return ticket;
	}
	public void setTicket(Map<String, Object> ticket) {
		this.ticket = ticket;
	}
	
	private List<TaskContext> tasks;
	public List<TaskContext> getTasks() {
		return tasks;
	}
	public void setTasks(List<TaskContext> tasks) {
		this.tasks = tasks;
	}
	
	
	private long ticketId;
	public long getTicketId() {
		return ticketId;
	}
	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
	}
}
