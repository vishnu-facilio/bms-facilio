package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReportColumnContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.transaction.FacilioConnectionPool;

public class UpdateReportColumnSequence implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReportColumnContext> columns = (List<ReportColumnContext>) context.get(FacilioConstants.ContextNames.REPORT_COLUMN_LIST);
		if (columns != null && !columns.isEmpty()) {
			for (int i = 0; i < columns.size(); i++) {
				String sql = "UPDATE Report_Columns SET WIDTH = "+columns.get(i).getWidth()+", SEQUENCE = "+(i+1)+" where ID="+columns.get(i).getId();
				
				try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString());) {
					int rowsUpdated = pstmt.executeUpdate();
				}
				catch(SQLException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
		else {
			throw new IllegalArgumentException("Report Column ID list cannot be null/ empty during updation of sequence");
		}
		return false;
	}

}
