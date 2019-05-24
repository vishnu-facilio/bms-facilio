package com.facilio.bmsconsole.commands;

import com.facilio.db.builder.DBUtil;
import com.facilio.db.util.SQLScriptRunner;
import com.facilio.db.util.DBConf;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddEventModuleCommand implements Command {
	
	private static final File EVENT_MODULE_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/db/" + DBConf.getInstance().getDBName() + "/eventModule.sql").getFile());
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		long orgId = (long) context.get("orgId");
		
		Map<String, String> paramValues = new HashMap<>(); 
		paramValues.put("orgId", String.valueOf(orgId));
		
		SQLScriptRunner scriptRunner2 = new SQLScriptRunner(EVENT_MODULE_SQL, true, paramValues, DBUtil.getDBSQLScriptRunnerMode());
		scriptRunner2.runScript();
		
		return false;
	}

}