/**
 * 
 */
package com.facilio.bmsconsole.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.DemoRoleUtil;
import com.facilio.sql.DBUtil;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.transaction.FacilioConnectionPool;

/**
 * @author facilio
 *
 */
public class DemoRoleJob extends FacilioJob{

	private static final Logger LOGGER = LogManager.getLogger(DemoRoleJob.class.getName());

	@SuppressWarnings("resource")
	public void execute(JobContext jc)throws Exception  {
		// TODO Auto-generated method stub

		Map<String,List<String>> tableName=DemoRoleUtil.initDateFieldModified();
		long orgId=AccountUtil.getCurrentOrg().getId();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {

			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			for (Map.Entry<String, List<String>> tableList : tableName.entrySet()) {

				  String key = tableList.getKey();
				  if(!DBUtil.isTableNameContains(key)) {
					  continue;
				  }
				  List<String> valueList = tableList.getValue();
				  StringBuilder sql=new StringBuilder();
				  sql.append("UPDATE").append(" ").append(key).append(" ").append("SET").append("  ");
				  for (String columnName : valueList) {
					  sql.append(columnName).append("=");
					  sql.append(columnName).append("+").append(( 24 * 60 * 60));
					  sql.append(",");
				  }		 
				  sql.replace(sql.length()-1, sql.length(), " ");
				  sql.append("WHERE ORGID=").append(orgId);
				  pstmt = conn.prepareStatement(sql.toString());
				  if(pstmt==null) {
					  throw new RuntimeException("###update String Data is null");
				  }
				  try {
				  pstmt.executeUpdate();
				  LOGGER.info("####DemoRoleUp### executed Successfully");
				  }
				  catch(Exception e) {
					  CommonCommandUtil.emailException("DemoRoleUp", "DemoRoleUp Failed - orgid -- "+AccountUtil.getCurrentOrg().getId(), e);
				  }
			}
		}
		catch(Exception e) {
			
			LOGGER.info("Exception occurred ", e);
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
	}

	
}
