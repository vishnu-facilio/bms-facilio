package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import com.facilio.services.email.EmailClient;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.fs.FileInfo;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class SendEmailCommand extends FacilioCommand implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(SendEmailCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context c) throws Exception{
		try {
			ImportProcessContext importProcessContext = (ImportProcessContext) c.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
			ImportProcessContext updatedContext = ImportAPI.getImportProcessContext(importProcessContext.getId());
			StringBuilder emailMessage = (StringBuilder) c.get(ImportAPI.ImportProcessConstants.EMAIL_MESSAGE);
			FileStore fs = FacilioFactory.getFileStore();
			Integer mailSetting = updatedContext.getMailSetting();
			
			if (mailSetting != null) {
				User user = AccountUtil.getCurrentUser();
				FileInfo fileInfo = fs.getFileInfo(importProcessContext.getFileId());
				EMailTemplate template = new EMailTemplate();
				template.setFrom(EmailClient.getFromEmail("alert"));
				template.setTo(user.getEmail());
				template.setMessage(emailMessage.toString());
				template.setSubject("Import of" + fileInfo.getFileName());
				JSONObject emailJSON = template.getOriginalTemplate();
				emailJSON.put("mailType", "html");
				FacilioFactory.getEmailClient().sendEmail(emailJSON);
				LOGGER.info("Import email sent for importJob:" + importProcessContext.getId() + "to" + user.getEmail());
			}
			else {
				emailMessage.delete(0, emailMessage.length());
			}
			LOGGER.info("Import email sent for import" + importProcessContext.getId());
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			// LOGGER.log(Priority.ERROR, e);
		}
		
		return false;
		
	}

}