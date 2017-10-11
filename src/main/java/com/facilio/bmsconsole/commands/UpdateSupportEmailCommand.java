package com.facilio.bmsconsole.commands;

import java.sql.Connection;
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
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateSupportEmailCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		SupportEmailContext supportEmail = (SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL);
		if(supportEmail != null) {
			supportEmail.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			//CommonCommandUtil.setFwdMail(supportEmail);
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_DEFAULT);
			Map<String, Object> emailProps = mapper.convertValue(supportEmail, Map.class);
			System.out.println("mpa"+emailProps);
			System.out.println("supportEmail"+supportEmail);
if(emailProps.get("autoAssignGroup")!=null)
{
			emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
}
			emailProps.remove("id");
			Connection con = FacilioConnectionPool.getInstance().getConnection();
			try {
				List<FacilioField> fields = FieldFactory.getSupportEmailFields();
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
														.connection(con)
														.table("SupportEmails")
														.fields(fields)
														.andCustomWhere("ID = ?", supportEmail.getId());
				builder.update(emailProps);
				context.put(FacilioConstants.ContextNames.RESULT, "success");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw e;
			}
			finally
			{
				con.close();
			}
		}
		return false;
	}

}
