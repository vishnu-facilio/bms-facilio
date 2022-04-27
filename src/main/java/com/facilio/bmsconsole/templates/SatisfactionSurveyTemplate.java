package com.facilio.bmsconsole.templates;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Setter
@Getter
public class SatisfactionSurveyTemplate extends Template{

	private long qandaTemplateId;
	private long assignedTo;
	private int expiryDays;
	private boolean isRetake;
	private int retakeExpiryDays;

	@Override
	public JSONObject getOriginalTemplate () throws Exception {
		JSONObject jsonObject = new JSONObject ();
		jsonObject.put ("qandaTemplateId",qandaTemplateId);
		jsonObject.put ("assignedTo",assignedTo);
		jsonObject.put ("expiryDays",getExpiryDays());
		jsonObject.put("isRetake",isRetake());
		jsonObject.put("retakeExpiryDays",getRetakeExpiryDays());

		return jsonObject;
	}
}
