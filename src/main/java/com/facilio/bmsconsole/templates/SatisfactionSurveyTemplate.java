package com.facilio.bmsconsole.templates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Setter
@Getter
public class SatisfactionSurveyTemplate extends Template{

	private long qandaTemplateId;
	private long assignedTo;
	private Integer expiryDay;
	private Boolean isRetakeAllowed;
	private Integer retakeExpiryDay;

	@Override
	public JSONObject getOriginalTemplate () throws Exception {
		JSONObject jsonObject = new JSONObject ();
		jsonObject.put ("qandaTemplateId",qandaTemplateId);
		jsonObject.put ("assignedTo",assignedTo);
		jsonObject.put ("expiryDay",getExpiryDay());
		jsonObject.put("isRetake",getIsRetakeAllowed());
		jsonObject.put("retakeExpiryDay",getRetakeExpiryDay());

		return jsonObject;
	}
}
