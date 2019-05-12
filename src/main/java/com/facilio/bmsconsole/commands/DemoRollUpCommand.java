package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.DemoRollUpUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class DemoRollUpCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(DemoRollUpCommand.class.getName());

	@SuppressWarnings("resource")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		try {
			long executionTime=(long) context.get(FacilioConstants.ContextNames.DEMO_ROLLUP_EXECUTION_TIME);
			long daysToMillisec=(executionTime * 24 * 60 * 60 * 1000);
			long startTime = System.currentTimeMillis();
			Map<String,List<String>> tableName=DemoRollUpUtil.TABLES_WITH_COLUMN;
			long orgId=(long) context.get(ContextNames.DEMO_ROLLUP_JOB_ORG);
			Connection conn = null;
			PreparedStatement pstmt = null;
			boolean olderCommit = false;

			try {

				conn = FacilioConnectionPool.INSTANCE.getConnectionFromPool();
				olderCommit = conn.getAutoCommit();
				conn.setAutoCommit(true);

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
					pstmt = conn.prepareStatement(sql.toString());
					if(pstmt==null) {
						throw new RuntimeException("###update String Data is null");
					}
					try {
						int count=  pstmt.executeUpdate();
						System.out.println("###DemoRoleUp"+" "+count+" "+"of rows updated in"+key+"successfully");
						LOGGER.info("###DemoRoleUp"+" "+count+" "+"of rows updated in"+key+"successfully");
					}
					catch(Exception e) {
						LOGGER.info("Exception occurred### in  DemoRollUpJob ", e);
						throw e;
					}
				}
			}
			catch(Exception e) {
				LOGGER.info("Exception occurred### in  DemoRollUpJob  ", e);
				throw e;
			}
			finally {
				conn.setAutoCommit(olderCommit);
				DBUtil.closeAll(conn, pstmt);
			}
			LOGGER.info("####DemoRoleUp Job completed time  is####" + (System.currentTimeMillis()-startTime));
		}
		catch(Exception e) {
			CommonCommandUtil.emailException("DemoRoleUp", "DemoRoleUp Failed - orgid -- "+AccountUtil.getCurrentOrg().getId(), e);
		}
		return false;
	}

}