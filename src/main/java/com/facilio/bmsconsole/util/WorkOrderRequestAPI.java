package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.sql.GenericInsertRecordBuilder;

public class WorkOrderRequestAPI {
	
	private static Logger logger = Logger.getLogger(WorkOrderRequestAPI.class.getName());
	
	public static long addS3MessageId(String s3Id) throws SQLException {
		try {
			
			Map<String, Object> workOrderEmailProps = new HashMap<>();
			workOrderEmailProps.put("s3MessageId", s3Id);
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table("WorkOrderRequest_EMail")
															.fields(FieldFactory.getWorkorderEmailFields())
															.addRecord(workOrderEmailProps);
			insertBuilder.save();
			return (long) workOrderEmailProps.get("id");
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
