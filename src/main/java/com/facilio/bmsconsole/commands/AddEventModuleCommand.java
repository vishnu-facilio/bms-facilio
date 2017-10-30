package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.sql.SQLScriptRunner;

public class AddEventModuleCommand implements Command {
	
	private static final File EVENT_MODULE_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/eventModule.sql").getFile());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
//		Map<String, String> signupInfo = (Map<String, String>) context.get(CreateUserCommand.signupinfo);
		long orgId = (long) context.get(CreateUserCommand.ORG_ID);
		
		
		Map<String, String> paramValues = new HashMap<>(); 
		//paramValues.put("orgId", "(SELECT ORGID from Organizations where FACILIODOMAINNAME='"+signupInfo.get("domainname")+"')");
		paramValues.put("orgId", String.valueOf(orgId));
		SQLScriptRunner scriptRunner = new SQLScriptRunner(EVENT_MODULE_SQL, true, paramValues);
		scriptRunner.runScript(((FacilioContext) context).getConnectionWithTransaction());
		
		return false;
	}

}
