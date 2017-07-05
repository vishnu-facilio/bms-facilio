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
import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class AddCFAction extends ActionSupport {
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		HttpServletRequest request = ServletActionContext.getRequest();
		String cfDataStr = request.getParameter("cfData");
		String moduleName = request.getParameter("moduleName");
		JSONParser parser = new JSONParser();
		JSONArray cfData = (JSONArray) parser.parse(cfDataStr);
		
		if(cfData != null && cfData.size() > 0) {
			List<FacilioField> fields = new ArrayList<>();
			for(int i = 0; i<cfData.size(); i++) {
				JSONObject cf = (JSONObject) cfData.get(i);
				FacilioField field = new FacilioField();
				field.setName((String) cf.get("fieldName"));
				field.setDataTypeCode(Integer.parseInt((String) cf.get("dataType")));
				fields.add(field);
			}
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
			
			Command addCF = FacilioChainFactory.getAddFieldsChain();
			addCF.execute(context);
			
			setFieldsIds((List<Long>) context.get("FieldIds"));
		}
		
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
