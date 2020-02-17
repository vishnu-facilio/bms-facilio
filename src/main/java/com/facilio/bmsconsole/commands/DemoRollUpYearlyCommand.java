package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.time.DateTimeUtil;

public class DemoRollUpYearlyCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(DemoRollUpYearlyCommand.class.getName());
	private static final String DEFAULT_DB_CONF_PATH = "conf/demorolluptables.yml"; 
	@Override
	public boolean executeCommand(Context context) throws Exception {

		long jobStartTime = System.currentTimeMillis();
		long orgId=(long) context.get(ContextNames.DEMO_ROLLUP_JOB_ORG);
		ZonedDateTime currentZdt = (ZonedDateTime) context.get(ContextNames.START_TIME);
		ZonedDateTime thisYearWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(currentZdt);
		int currentWeek = thisYearWeekStartZdt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
		ZonedDateTime lastYearWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(thisYearWeekStartZdt.minusYears(1).with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, currentWeek));
		ZonedDateTime lastYearWeekEndZdt = DateTimeUtil.getWeekEndTimeOf(lastYearWeekStartZdt);
		long lastYearWeekStart = lastYearWeekStartZdt.toInstant().toEpochMilli();
		long lastYearWeekEnd = lastYearWeekEndZdt.toInstant().toEpochMilli();
		long thisYearWeekStart = thisYearWeekStartZdt.toInstant().toEpochMilli();
		long weekDiff = (thisYearWeekStart - lastYearWeekStart);

		ClassLoader classLoader = DemoRollUpYearlyCommand.class.getClassLoader();
        Yaml yaml = new Yaml();
        Map<String ,List<Map<String,Object>>> json = null;
        try(InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_DB_CONF_PATH)) {
            json = yaml.load(inputStream);
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while reading demoRollUptables conf file. "+e.getMessage(), e);
            throw new RuntimeException("Error occurred while reading demoRollUptables conf file. "+e.getMessage());
        }
		try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			for (Entry<String, List<Map<String, Object>>> tableList : json.entrySet()) {
				for (Map<String, Object> itr : tableList.getValue()) {
					String tableNamekey = (String) itr.get("tableName");
					String primaryColumn = (String) itr.get("primaryColumn");
					List<String> valueList = (List<String>) itr.get("columns");
					StringBuilder sql = new StringBuilder();
					sql.append("UPDATE  ").append(tableNamekey).append("  SET  ");
					for (String columnName : valueList) {
						sql.append(columnName).append(" = ");
						sql.append("(");
						sql.append(columnName).append(" + ").append(weekDiff).append(")");
						sql.append(",");
					}
					sql.replace(sql.length() - 1, sql.length(), " ");
					sql.append(" WHERE ORGID = ").append(orgId).append(" AND ").append(primaryColumn)
					.append("  BETWEEN ").append(lastYearWeekStart).append(" AND ").append(lastYearWeekEnd);
					try (PreparedStatement pstmt = conn.prepareStatement(sql.toString());) {
						int count = pstmt.executeUpdate();
						System.out.println("###DemoRollUpYearlyJob " + count + " of rows updated in  " + tableNamekey + "  successfully");
						LOGGER.info("###DemoRollUpYearlyJob " + count + " of rows updated in  " + tableNamekey + "  successfully");
					} catch (Exception e) {
						LOGGER.error("###Exception occurred in  DemoRollUpYearlyJob. TableName is:  " + tableNamekey);
						throw e;
					}
				}
			}

		}
		catch(Exception e) {
			throw e;
		}
		LOGGER.info("####DemoRollUpYearlyJob time taken to complete : " + (System.currentTimeMillis()-jobStartTime));
		return false;
	}

}
