package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;

public class UpdateSupportEmailCommand extends FacilioCommand {

	private static Logger log = LogManager.getLogger(UpdateSupportEmailCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		SupportEmailContext supportEmail = (SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL);
			if(supportEmail != null) {
				supportEmail.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				if (StringUtils.isNotEmpty(supportEmail.getActualEmail())) {
					CommonCommandUtil.setFwdMail(supportEmail);
				}
//				SupportEmailAPI.updateSupportEmailSetting(supportEmail);
				FacilioService.runAsService(() -> SupportEmailAPI.updateSupportEmailSetting(supportEmail));
//			supportEmail.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
//			//CommonCommandUtil.setFwdMail(supportEmail);
//			Map<String, Object> emailProps = FieldUtil.getAsProperties(supportEmail);
//			System.out.println("mpa"+emailProps);
//			System.out.println("supportEmail"+supportEmail);
//			if(emailProps.get("autoAssignGroup")!=null)
//			{
//				emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
//			}
//			emailProps.remove("id");
//		//	Connection con = FacilioConnectionPool.getInstance().getConnection();
//			try {
//				List<FacilioField> fields = FieldFactory.getSupportEmailFields();
//				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
//														.table("SupportEmails")
//														.fields(fields)
//														.andCustomWhere("ID = ?", supportEmail.getId());
//				builder.update(emailProps);
//				context.put(FacilioConstants.ContextNames.RESULT, "success");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				log.info("Exception occurred ", e);
//				throw e;
//			}
//			finally
//			{
//			}
		}
		return false;
	}

}
