package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddSupportEmailCommand implements Command {
	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		SupportEmailContext supportEmail = (SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL);
		
		if(supportEmail != null) {
			supportEmail.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			CommonCommandUtil.setFwdMail(supportEmail);
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_DEFAULT);
			Map<String, Object> emailProps = mapper.convertValue(supportEmail, Map.class);
//			emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
			if(emailProps.get("autoAssignGroup")!=null)
			{
						emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
			}
			System.out.println(emailProps);
			
			List<FacilioField> fields = FieldFactory.getSupportEmailFields();
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
													.table("SupportEmails")
													.fields(fields)
													.addRecord(emailProps);
			builder.save();
			supportEmail.setId((long) emailProps.get("id"));
		}
		return false;
	}
}
