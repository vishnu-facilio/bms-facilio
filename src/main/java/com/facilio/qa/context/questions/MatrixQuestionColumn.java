package com.facilio.qa.context.questions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class MatrixQuestionColumn extends V3Context{

	String name;
	Long parentId;  // Not having it as lookup because this is not used in forms or anything. Also this means I can avoid the extra handling done for lookup since this won't be a separate list anyway
	Long fieldId;
	FacilioField field;
	Boolean mandatory;
	String meta;
	private JSONArray displayLogicMeta;
	
	public void setFieldJSON(JSONObject fieldJson) throws Exception {
		field = FieldUtil.parseFieldJson(fieldJson);
	}

	// only for clone
	Long clonedFieldId;
}
