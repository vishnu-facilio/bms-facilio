package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class UpdateSupportEmailCommand implements Command {

	private static Logger log = LogManager.getLogger(UpdateSupportEmailCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		SupportEmailContext supportEmail = (SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL);
			if(supportEmail != null) {
			System.out.println("....UpdateSupport condition");
			supportEmail.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			//CommonCommandUtil.setFwdMail(supportEmail);
			Map<String, Object> emailProps = FieldUtil.getAsProperties(supportEmail);
			System.out.println("mpa"+emailProps);
			System.out.println("supportEmail"+supportEmail);
			if(emailProps.get("autoAssignGroup")!=null)
			{
				emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
			}
			emailProps.remove("id");
		//	Connection con = FacilioConnectionPool.getInstance().getConnection();
			try {
				List<FacilioField> fields = FieldFactory.getSupportEmailFields();
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
														.table("SupportEmails")
														.fields(fields)
														.andCustomWhere("ID = ?", supportEmail.getId());
				builder.update(emailProps);
				context.put(FacilioConstants.ContextNames.RESULT, "success");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
				throw e;
			}
			finally
			{
			}
		}
		return false;
	}

}
