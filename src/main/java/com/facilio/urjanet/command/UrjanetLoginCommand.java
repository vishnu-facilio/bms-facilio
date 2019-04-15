package com.facilio.urjanet.command;


import com.facilio.urjanet.UrjanetConnection;
import com.facilio.urjanet.contants.UrjanetConstants;
import com.facilio.urjanet.context.UtilityProviderCredentials;
import com.facilio.urjanet.entity.Credential;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;


public class UrjanetLoginCommand  implements Command 
{
		@Override
		public boolean execute(Context context) throws Exception {
			UtilityProviderCredentials  credentials = (UtilityProviderCredentials)context.get(UrjanetConstants.ContextNames.CREDENTIAL);
			UrjanetConnection connection = new UrjanetConnection();
			JSONObject createJSON = new JSONObject();
			String userName = credentials.getUserName();
			String password = credentials.getPassword();
			String customerId = "facilio";
			String utilityProviderId = credentials.getUpName();
			String url = credentials.getUpUrl();
			String templateId = credentials.getTemplateId();
			createJSON.put("username", userName);
			createJSON.put("password", password);
			createJSON.put("customerId", customerId);
			createJSON.put("utilityProviderId", utilityProviderId);
			if(url != null)
			{
				createJSON.put("url", url);
			}
			if(templateId != null)
			{
				createJSON.put("templateId", templateId);
			}
			
			Credential record = new Credential();
			record.setCreateJSON(createJSON);
			connection.create(record);
			return false;
	}

}
