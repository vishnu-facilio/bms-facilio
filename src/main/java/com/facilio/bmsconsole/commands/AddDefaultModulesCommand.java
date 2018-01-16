package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.sql.SQLScriptRunner;

public class AddDefaultModulesCommand implements Command {
	
	private static final File INSERT_MODULES_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/defaultModules.sql").getFile());
	
	private static final File INSERT_REPORTS_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/defaultReports.sql").getFile());
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		long orgId = (long) context.get("orgId");
		
		Map<String, String> paramValues = new HashMap<>(); 
		paramValues.put("orgId", String.valueOf(orgId));
	
		try {
			SQLScriptRunner scriptRunner = new SQLScriptRunner(INSERT_MODULES_SQL, true, paramValues);
			scriptRunner.runScript();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			SQLScriptRunner scriptRunner = new SQLScriptRunner(INSERT_REPORTS_SQL, true, paramValues);
			scriptRunner.runScript();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

}
