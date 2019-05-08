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

		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			Map<String,List<String>> tableName=DemoRoleUtil.initDateFieldModified();
			long orgId=AccountUtil.getCurrentOrg().getId();

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
					  sql.append(columnName).append("+").append(( 24 * 60 * 60 *1000));
					  sql.append(",");
				  }		 
				  sql.replace(sql.length()-1, sql.length(), " ");
				  sql.append("WHERE ORGID=").append(orgId);
				  pstmt = conn.prepareStatement(sql.toString());
				  System.out.println("####demo Update Query"+pstmt);
				  if(pstmt==null) {
					  throw new RuntimeException("###DemoRoleup###update String Data is null");
				  }
				  try {
					int count=  pstmt.executeUpdate();
					System.out.println("###DemoRoleUp"+count+"of rows updated in"+key+"successfully");
					LOGGER.info("###DemoRoleUp"+count+"of rows updated in"+key+"successfully");
				  }
				  catch(Exception e) {
					  CommonCommandUtil.emailException("DemoRoleUp", "DemoRoleUp Failed - orgid -- "+AccountUtil.getCurrentOrg().getId(), e);
				  }
			}
		}
		catch(Exception e) {
			System.err.printf("Exception occurred ", e);
			LOGGER.info("Exception occurred ", e);
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
	}

	
}
