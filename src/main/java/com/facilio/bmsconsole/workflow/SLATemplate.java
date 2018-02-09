package com.facilio.bmsconsole.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class SLATemplate extends UserTemplate {

	long duration;
	
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public int getType() {
		return Type.SLA.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.SLA;
	}
	@Override
	public JSONObject getTemplate(Map<String, Object> placeHolders) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("duration", duration);
		return json;
	}
	@Override
	public JSONObject getOriginalTemplate() {
		// TODO Auto-generated method stub
		return getTemplate(null);
	}
}
