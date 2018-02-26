package com.facilio.bmsconsole.templates;

import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AssignmentTemplate extends UserTemplate {

	
	long assignedGroupId = -1;
	long assignedUserId = -1;
	
	public long getAssignedGroupId() {
		return assignedGroupId;
	}
	public void setAssignedGroupId(long assignedGroupId) {
		this.assignedGroupId = assignedGroupId;
	}
	public long getAssignedUserId() {
		return assignedUserId;
	}
	public void setAssignedUserId(long assignedUserId) {
		this.assignedUserId = assignedUserId;
	}
	Group assignedGroup;
	User assignedUser;
	
	public Group getAssignedGroup() {
		return assignedGroup;
	}
	public void setAssignedGroup(Group assignedGroup) {
		this.assignedGroup = assignedGroup;
	}
	public User getAssignedUser() {
		return assignedUser;
	}
	public void setAssignedUser(User assignedUser) {
		this.assignedUser = assignedUser;
	}
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public JSONObject getTemplate(Map<String, Object> placeHolders) {
//		// TODO Auto-generated method stub
//		JSONObject obj = new JSONObject();
//		obj.put("to", getTo(StrSubstitutor.replace(to, placeHolders)));
//		obj.put("message", StrSubstitutor.replace(message, placeHolders));
//		return obj;
//	}
//	
//	@Override
//	public JSONObject getOriginalTemplate() {
//		JSONObject obj = new JSONObject();
//		obj.put("to", to);
//		obj.put("message", message);
//		
//		return obj;
//	}
//	
//	
//	private Object getTo(String to) {
//		if(to != null && !to.isEmpty()) {
//			if(to.contains(",")) {
//				String[] tos = to.trim().split("\\s*,\\s*");
//				JSONArray toList = new JSONArray();
//				for(String toAddr : tos) {
//					toList.add(toAddr);
//				}
//				return toList;
//			}
//			else {
//				return to;
//			}
//		}
//		return null;
//	}

	@Override
	@JsonInclude(Include.ALWAYS)
	public int getType() {
		return Type.ASSIGNMENT.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.ASSIGNMENT;
	}
	@Override
	public JSONObject getTemplate(Map<String, Object> placeHolders) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("assignedUserId", assignedUserId);
		json.put("assignedGroupId", assignedGroupId);
		return json;
	}
	@Override
	public JSONObject getOriginalTemplate() {
		// TODO Auto-generated method stub
		return getTemplate(null);
	}
}
