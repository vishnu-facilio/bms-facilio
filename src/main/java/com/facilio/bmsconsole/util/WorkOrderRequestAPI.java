package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.sql.GenericInsertRecordBuilder;

public class WorkOrderRequestAPI {
	
	private static final Logger LOGGER = LogManager.getLogger(WorkOrderRequestAPI.class.getName());

	public static long addS3MessageId(String s3Id) throws SQLException {
		try {
			
			Map<String, Object> workOrderEmailProps = new HashMap<>();
			workOrderEmailProps.put("s3MessageId", s3Id);
			workOrderEmailProps.put("createdTime", System.currentTimeMillis());
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table("WorkOrderRequest_EMail")
															.fields(FieldFactory.getWorkorderEmailFields())
															.addRecord(workOrderEmailProps);
			insertBuilder.save();
			return (long) workOrderEmailProps.get("id");
		}
		catch(SQLException e) {
			LOGGER.error("Exception occurred during adding Workorder request email S3 id to DB ", e);
			throw e;
		}
	}
}
