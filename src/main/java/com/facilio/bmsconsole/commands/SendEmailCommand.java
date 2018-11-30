package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.logging.*;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Priority;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class SendEmailCommand implements Command,Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(SendEmailCommand.class.getName());
	
	@Override
	public boolean execute(Context c) throws Exception{
		try {
			ImportProcessContext importProcessContext = (ImportProcessContext) c.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
			ImportProcessContext updatedContext = ImportAPI.getImportProcessContext(importProcessContext.getId());
			StringBuilder emailMessage = (StringBuilder) c.get(ImportAPI.ImportProcessConstants.EMAIL_MESSAGE);
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			Integer mailSetting = updatedContext.getMailSetting();
			
			if (mailSetting != null) {
				User user = AccountUtil.getCurrentUser();
				FileInfo fileInfo = fs.getFileInfo(importProcessContext.getFileId());
				EMailTemplate template = new EMailTemplate();
				template.setFrom("alert@facilio.com");
				template.setTo(user.getEmail());
				template.setMessage(emailMessage.toString());
				template.setSubject("Import of" + fileInfo.getFileName());
				JSONObject emailJSON = template.getOriginalTemplate();
				emailJSON.put("mailType", "html");
				AwsUtil.sendEmail(emailJSON);
			}
			else {
				emailMessage.delete(0, emailMessage.length());
			}
			LOGGER.info("Import email sent for importJob:" + importProcessContext.getId());
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			// LOGGER.log(Priority.ERROR, e);
		}
		
		return false;
		
	}

}