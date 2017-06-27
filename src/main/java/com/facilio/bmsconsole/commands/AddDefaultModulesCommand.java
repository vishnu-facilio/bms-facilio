package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.sql.SQLScriptRunner;

public class AddDefaultModulesCommand implements Command {
	
	private static final File INSERT_MODULES_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/defaultModules.sql").getFile());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Map<String, String> signupInfo = (Map<String, String>) context.get(CreateUserCommand.signupinfo);
		
		Map<String, String> paramValues = new HashMap<>(); 
		paramValues.put("orgId", "(SELECT ORGID from Organizations where FACILIODOMAINNAME='"+signupInfo.get("domainname")+"')");
		
		SQLScriptRunner scriptRunner = new SQLScriptRunner(INSERT_MODULES_SQL, false, true, paramValues);
		scriptRunner.runScript();
		
		return false;
	}

}
