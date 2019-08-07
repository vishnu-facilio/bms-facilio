package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.DemoRollUpUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.transaction.FacilioTransactionManager;

public class DemoRollUpCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(DemoRollUpCommand.class.getName());

	@SuppressWarnings("resource")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

			long executionTime=(long) context.get(FacilioConstants.ContextNames.DEMO_ROLLUP_EXECUTION_TIME);
			long daysToMillisec=(executionTime * 24 * 60 * 60 * 1000);
			long startTime = System.currentTimeMillis();
			Map<String,List<String>> tableName=DemoRollUpUtil.TABLES_WITH_COLUMN;
			long orgId=(long) context.get(ContextNames.DEMO_ROLLUP_JOB_ORG);
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection();) {
				for (Map.Entry<String, List<String>> tableList : tableName.entrySet()) {
					String key = tableList.getKey();
					List<String> valueList = tableList.getValue();
					StringBuilder sql=new StringBuilder();
					sql.append("UPDATE").append(" ").append(key).append(" ").append("SET").append("  ");
					for (String columnName : valueList) {
						sql.append(columnName).append("=");
						sql.append(columnName).append("+").append(daysToMillisec);
						sql.append(",");
					}		 
					sql.replace(sql.length()-1, sql.length(), " ");
					sql.append("WHERE ORGID=").append(orgId);
					try(PreparedStatement pstmt = conn.prepareStatement(sql.toString());) {
						int count=  pstmt.executeUpdate();
						System.out.println("###DemoRoleUp"+" "+count+" "+"of rows updated in"+key+"successfully");
						LOGGER.info("###DemoRollsUp"+" "+count+" "+"of rows updated in"+key+"successfully");
					}
					catch(Exception e) {
						LOGGER.info("Exception occurred### in  DemoRollUpJob... TableName is.. "+ key+ e);
						throw e;
					}
				}
			}
			catch(Exception e) {
				LOGGER.info("Exception occurred### in  DemoRollUpJob  ", e);
				throw e;
			}
			LOGGER.info("####DemoRollUp Job completed time  is####" + (System.currentTimeMillis()-startTime));
		return false;
	}

}