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
	private Long expiryDate;
	private Boolean isRetakeAllowed;
	private Integer retakeExpiryDuration; // in minutes

	@Override
	public JSONObject getOriginalTemplate () throws Exception {
		JSONObject jsonObject = new JSONObject ();
		jsonObject.put ("qandaTemplateId",qandaTemplateId);
		jsonObject.put ("assignedTo",assignedTo);
		jsonObject.put ("expiryDate",getExpiryDate());
		jsonObject.put("isRetake",getIsRetakeAllowed());
		jsonObject.put("retakeExpiryDuration",getRetakeExpiryDuration());

		return jsonObject;
	}
}
