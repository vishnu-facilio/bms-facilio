package com.facilio.bmsconsole.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.GroupMemberContext;
import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.customfields.FacilioCustomField;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class NewTaskAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		
		System.out.println(getTicketId());
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		
		moduleName = CFUtil.getModuleName("Tasks_Objects", orgId);
		List<FacilioCustomField> cfs = CFUtil.getCustomFields("Tasks_Objects", "Tasks_Fields", orgId);
//		assignedToList = UserAPI.getOrgUsers(orgId);
		List<GroupContext> groups = GroupAPI.getGroupsOfOrg(orgId, true);
		assignmentGroupList = new HashMap<>();
		if(groups != null && groups.size() > 0) {
			
			for(GroupContext group : groups) {
				assignmentGroupList.put(group.getGroupId(), group.getName());
			}
		}
		assignedToList = new HashMap<>();
		customFieldNames = new ArrayList<>();
		for(FacilioCustomField field : cfs) {
			customFieldNames.add(field.getFieldName());
		}
		
		return SUCCESS;
	}
	
	private long ticketId;
	public long getTicketId() {
		return ticketId;
	}
	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private List<String> customFieldNames;
	public List<String> getCustomFieldNames() {
		return customFieldNames;
	}
	public void setCustomFieldNames(List<String> customFieldNames) {
		this.customFieldNames = customFieldNames;
	}
	
	private long groupId;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	private Map<Long, String> assignmentGroupList;
	public  Map<Long, String> getAssignmentGroupList() {
		return assignmentGroupList;
	}
	public void setAssignmentGroupList( Map<Long, String> assignmentGroupList) {
		this.assignmentGroupList = assignmentGroupList;
	}
	
	private Map<Long, String> assignedToList;
	public Map<Long, String> getAssignedToList() {
		return assignedToList;
	}
	public void setAssignedToList(Map<Long, String> assignedToList) {
		this.assignedToList = assignedToList;
	}
 }

