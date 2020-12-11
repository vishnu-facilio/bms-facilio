package com.facilio.bmsconsole.commands;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.util.DBConf;
import com.facilio.db.util.SQLScriptRunner;

public class RunModuleMigrationCommand extends FacilioCommand {
	private static org.apache.log4j.Logger LOGGER = LogManager.getLogger(RunDefaultFieldsMigration.class.getName());
	private static final String DEFAULT_FIELDS_MIGRATION_PATH = "conf/db/" + DBConf.getInstance().getDBName() + "/ModuleMigration.sql";
	@Override
	public boolean executeCommand(Context context) throws Exception {
		try {
			File migrationSql = new File(SQLScriptRunner.class.getClassLoader().getResource(DEFAULT_FIELDS_MIGRATION_PATH).getFile());
			long orgId = (long) AccountUtil.getCurrentOrg().getOrgId();
			if (orgId > 0) {
				Map<String, String> paramValues = new HashMap<>();
				paramValues.put("orgId", String.valueOf(orgId));
				paramValues.put("publicDb", DBConf.getInstance().getDefaultDB());

				SQLScriptRunner scriptRunner = new SQLScriptRunner(migrationSql, true, paramValues, DBUtil.getDBSQLScriptRunnerMode());
				scriptRunner.runScript();

				LOGGER.info(MessageFormat.format("Module migration completed for org => {0}", orgId));
			}
		}
		catch (Exception e) {
			LOGGER.error("Error occurred during module migration", e);
		}
		return false;
	}

}


