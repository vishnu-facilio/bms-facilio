package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.Command;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.customfields.FacilioCustomField;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class AddCFAction extends ActionSupport {
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		HttpServletRequest request = ServletActionContext.getRequest();
		String cfDataStr = request.getParameter("cfData");
		
		JSONParser parser = new JSONParser();
		JSONArray cfData = (JSONArray) parser.parse(cfDataStr);
		
		List<FacilioCustomField> fields = new ArrayList<>();
		for(int i = 0; i<cfData.size(); i++) {
			JSONObject cf = (JSONObject) cfData.get(i);
			FacilioCustomField field = new FacilioCustomField();
			field.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			field.setModuleName((String) cf.get("moduleName"));
			field.setFieldName((String) cf.get("fieldName"));
			field.setDataTypeCode(Integer.parseInt((String) cf.get("dataType")));
			fields.add(field);
		}
		
		FacilioContext context = new FacilioContext();
		context.put("CustomFields", fields);
		
		Command addCF = FacilioChainFactory.getAddCustomFieldChain();
		addCF.execute(context);
		
		setFieldsIds((List<Long>) context.get("FieldIds"));
		
		return SUCCESS;
	}
	
	private List<Long> fieldIds;
	public List<Long> getFieldsIds() {
		return fieldIds;
	}
	public void setFieldsIds(List<Long> fieldIds) {
		this.fieldIds = fieldIds;
	}
	
}
