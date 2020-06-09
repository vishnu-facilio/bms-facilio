package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;

public class WorkOrderRequestAPI {
	
	private static final Logger LOGGER = LogManager.getLogger(WorkOrderRequestAPI.class.getName());

	public static long addS3MessageId(JSONObject mailObj) throws SQLException {
		try {
			String s3Id = (String) mailObj.get("messageId");
			String destination =  mailObj.get("destination").toString();
			Map<String, Object> workOrderEmailProps = new HashMap<>();
			workOrderEmailProps.put("s3MessageId", s3Id);
			workOrderEmailProps.put("to", StringUtils.truncate(destination, 450)); //To prevent data truncation error. Anyway we are not using this to for processing. THis is just for logging
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
