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
		
		long orgId = (long) context.get("orgId");
		
		Map<String, String> paramValues = new HashMap<>(); 
		paramValues.put("orgId", String.valueOf(orgId));
		
		SQLScriptRunner scriptRunner = new SQLScriptRunner(INSERT_MODULES_SQL, true, paramValues);
		scriptRunner.runScript(((FacilioContext) context).getConnectionWithTransaction());
		
		return false;
	}

}
