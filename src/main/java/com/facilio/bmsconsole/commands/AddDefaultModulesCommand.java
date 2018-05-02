package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.sql.SQLScriptRunner;

public class AddDefaultModulesCommand implements Command {
	private static Logger logger = Logger.getLogger("AddDefaultModulesCommand");

	private static final File INSERT_MODULES_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/defaultModules.sql").getFile());
	
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
			logger.log(Level.SEVERE,"Error",e);
		}
		
		return false;
	}

}
