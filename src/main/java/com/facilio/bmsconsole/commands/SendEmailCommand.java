package com.facilio.bmsconsole.commands;

import java.io.Serializable;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class SendEmailCommand implements Command,Serializable{
	
	private static final Logger LOGGER = LogManager.getLogger(SendEmailCommand.class.getName());
	
	@Override
	public boolean execute(Context c) throws Exception{
		ImportProcessContext importProcessContext = (ImportProcessContext) c.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		ImportProcessContext updatedContext = ImportAPI.getImportProcessContext(importProcessContext.getId());
		StringBuilder emailMessage = (StringBuilder) c.get(ImportAPI.ImportProcessConstants.EMAIL_MESSAGE);
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		Integer mailSetting = updatedContext.getMailSetting();
		
		if (mailSetting != null) {
			User user = AccountUtil.getCurrentUser();
			FileInfo fileInfo = fs.getFileInfo(importProcessContext.getFileId());
			JSONObject email = new JSONObject();
			email.put("to", user.getEmail());
			email.put("subject", "Import of " + fileInfo.getFileName());
			email.put("message",emailMessage.toString());
			AwsUtil.sendEmail(email);
		}
		else {
			emailMessage.delete(0, emailMessage.length());
		}
		return false;
		
	}

}