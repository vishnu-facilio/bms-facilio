package com.facilio.bmsconsole.commands;

import com.facilio.aws.util.AwsUtil;
import com.facilio.sql.DBUtil;
import com.facilio.sql.SQLScriptRunner;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AddDefaultModulesCommand implements Command {
	private static org.apache.log4j.Logger log = LogManager.getLogger(AddDefaultModulesCommand.class.getName());

	private static Logger logger = Logger.getLogger("AddDefaultModulesCommand");

	private static final File INSERT_MODULES_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/db/" + AwsUtil.getDB() + "/defaultModules.sql").getFile());
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		long orgId = (long) context.get("orgId");
		
		Map<String, String> paramValues = new HashMap<>(); 
		paramValues.put("orgId", String.valueOf(orgId));
	
		SQLScriptRunner scriptRunner = new SQLScriptRunner(INSERT_MODULES_SQL, true, paramValues, DBUtil.getDBSQLScriptRunnerMode());
		scriptRunner.runScript();
		
		return false;
	}

}
