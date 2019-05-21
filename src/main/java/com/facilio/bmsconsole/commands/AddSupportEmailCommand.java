package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddSupportEmailCommand implements Command {
	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		SupportEmailContext supportEmail = (SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL);
		
		if(supportEmail != null) {
			supportEmail.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
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
