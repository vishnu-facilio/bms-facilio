package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.constants.FacilioConstants;
import com.facilio.transaction.FacilioConnectionPool;

public class UpdateReportColumnSequence implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> ids = (List<Long>) context.get(FacilioConstants.ContextNames.REPORT_COLUMN_LIST);
		if (ids != null && !ids.isEmpty()) {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE Report_Columns SET SEQUENCE = CASE ");
			for (int i = 0; i < ids.size(); i++) {
				sql.append("WHEN ID = ")
					.append(ids.get(i))
					.append(" THEN ")
					.append(i+1)
					.append(" ");
			}
			sql.append("ELSE SEQUENCE END WHERE ID IN (")
				.append(StringUtils.join(ids,","))
				.append(")");
			
			try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString());) {
				int rowsUpdated = pstmt.executeUpdate();
				context.put(FacilioConstants.ContextNames.RESULT, "success");
			}
			catch(SQLException e) {
				e.printStackTrace();
				throw e;
			}
		}
		else {
			throw new IllegalArgumentException("Report Column ID list cannot be null/ empty during updation of sequence");
		}
		return false;
	}

}
