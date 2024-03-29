package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.util.DBConf;
import com.facilio.db.util.SQLScriptRunner;
import com.facilio.i18n.util.TranslationUtil;

public class AddDefaultDataCommand extends FacilioCommand {
	
	private static final File INSERT_MODULES_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource(FacilioUtil.normalizePath("conf/db/" + DBConf.getInstance().getDBName() + "/defaultData.sql")).getFile());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long orgId = (long) context.get("orgId");
		if(orgId > 0) {
			Map<String, String> paramValues = new HashMap<>(); 
			paramValues.put("orgId", String.valueOf(orgId));
			paramValues.put("publicDb", DBConf.getInstance().getDefaultDB());
			
			addAllProperties(paramValues);
			
			SQLScriptRunner scriptRunner = new SQLScriptRunner(INSERT_MODULES_SQL, true, paramValues, DBUtil.getDBSQLScriptRunnerMode());
			scriptRunner.runScript();
		}
		return false;
	}

	private void addAllProperties(Map<String, String> paramValues) {
		ResourceBundle bundle = TranslationUtil.getBundle();
		Collections.list(bundle.getKeys()).forEach(key -> paramValues.put(key, bundle.getString(key)));
	}
}
