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
	private static final String DEFAULT_DB_ALARM_TABLE_CONF_PATH = "conf/demoRollUpAlarmTables.yml";
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
        Map<String ,List<Map<String,Object>>> readingTableList = null;
        Map<String ,List<Map<String,Object>>> alarmsTableList = null;
        try(InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_DB_CONF_PATH)) {
        	readingTableList = yaml.load(inputStream);
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while reading demoRollUptables conf file. "+e.getMessage(), e);
            throw new RuntimeException("Error occurred while reading demoRollUptables conf file. "+e.getMessage());
        }
        try(InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_DB_ALARM_TABLE_CONF_PATH)) {
        	alarmsTableList = yaml.load(inputStream);
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while reading demoRollUptables conf file. "+e.getMessage(), e);
            throw new RuntimeException("Error occurred while reading demoRollUptables conf file. "+e.getMessage());
        }
        try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			
        	updateTtimeColumns(readingTableList,weekDiff,lastYearWeekStart,lastYearWeekEnd,orgId,conn);
        	System.out.println("laststart :"+lastYearWeekStart + " latend : "+lastYearWeekEnd);
        	lastYearWeekStart = DateTimeUtil.addWeeks(lastYearWeekStart, -1);
        	lastYearWeekEnd = DateTimeUtil.addWeeks(lastYearWeekEnd, -1);
        	System.out.println("laststart :"+lastYearWeekStart + " latend : "+lastYearWeekEnd);
        	weekDiff = (thisYearWeekStart - lastYearWeekStart);
        	updateTtimeColumns(alarmsTableList,weekDiff,lastYearWeekStart,lastYearWeekEnd,orgId,conn);
        }
		catch(Exception e) {
			throw e;
		}
		LOGGER.info("####DemoRollUpYearlyJob time taken to complete : " + (System.currentTimeMillis()-jobStartTime));
		return false;
	}

	private void updateTtimeColumns(Map<String, List<Map<String, Object>>> tableListToUpdate, long weekDiff, long lastYearWeekStart, long lastYearWeekEnd, long orgId, Connection conn) throws Exception {
		for (Entry<String, List<Map<String, Object>>> tableList : tableListToUpdate.entrySet()) {
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
					LOGGER.info("###DemoRollUpYearlyJob " + count + " of rows updated in  " + tableNamekey + "  successfully");
				} catch (Exception e) {
					LOGGER.error("###Exception occurred in  DemoRollUpYearlyJob. TableName is:  " + tableNamekey);
					throw e;
				}
			}
		}
	}
}
