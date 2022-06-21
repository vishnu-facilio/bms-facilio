package com.facilio.bmsconsole.templates;

import com.facilio.bmsconsole.context.SingleSharingContext;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Setter
@Getter
public class SatisfactionSurveyTemplate extends Template{

	private long qandaTemplateId;
	private Integer sharingType;
	private Long userId;
	private Long fieldId;
	private Integer expiryDay;
	private Boolean isRetakeAllowed;
	private Integer retakeExpiryDay;

	@Override
	public JSONObject getOriginalTemplate () throws Exception {
		JSONObject jsonObject = new JSONObject ();
		jsonObject.put ("qandaTemplateId",qandaTemplateId);
		jsonObject.put ("userId",getUserId());
		jsonObject.put("fieldId",getFieldId());
		jsonObject.put("type",getSharingType());
		jsonObject.put ("expiryDay",getExpiryDay());
		jsonObject.put("isRetakeAllowed",getIsRetakeAllowed());
		jsonObject.put("retakeExpiryDay",getRetakeExpiryDay());

		return jsonObject;
	}
}
