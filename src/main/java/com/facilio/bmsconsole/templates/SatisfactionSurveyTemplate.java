package com.facilio.bmsconsole.templates;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.RoleFactory;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
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
		jsonObject.put("isRetakeAllowed",getIsRetakeAllowed());
		jsonObject.put("retakeExpiryDay",getRetakeExpiryDay());

		return jsonObject;
	}
}
