package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import com.facilio.db.builder.DBUtil;
import com.facilio.db.util.DBConf;
import com.facilio.db.util.SQLScriptRunner;

public class AddDefaultModulesCommand extends FacilioCommand {
	private static org.apache.log4j.Logger log = LogManager.getLogger(AddDefaultModulesCommand.class.getName());

	private static Logger logger = Logger.getLogger("AddDefaultModulesCommand");

	private static final File INSERT_MODULES_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/db/" + DBConf.getInstance().getDBName() + "/defaultModules.sql").getFile());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long orgId = (long) context.get("orgId");
		if(orgId > 0) {
			Map<String, String> paramValues = new HashMap<>(); 
			paramValues.put("orgId", String.valueOf(orgId));
			paramValues.put("publicDb", DBConf.getInstance().getDefaultDB());
		
			SQLScriptRunner scriptRunner = new SQLScriptRunner(INSERT_MODULES_SQL, true, paramValues, DBUtil.getDBSQLScriptRunnerMode());
			scriptRunner.runScript();
		}
		return false;
	}

}
