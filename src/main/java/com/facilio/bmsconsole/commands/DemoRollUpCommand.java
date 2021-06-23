 package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.util.DemoRollUpUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.transaction.FacilioConnectionPool;

public class DemoRollUpCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(DemoRollUpCommand.class.getName());

	@SuppressWarnings("resource")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

//<<<<<<< HEAD
//		long executionTime=(long) context.get(FacilioConstants.ContextNames.DEMO_ROLLUP_EXECUTION_TIME);
//		long daysToMillisec=(executionTime * 24 * 60 * 60 * 1000);
//		long startTime = System.currentTimeMillis();
//		Map<String,List<String>> tableName=DemoRollUpUtil.TABLES_WITH_COLUMN;
//		Map<String,List<String>> idColumntablesName=DemoRollUpUtil.TABLES_WITH_ID_COLUMN;
//		long orgId=(long) context.get(ContextNames.DEMO_ROLLUP_JOB_ORG);
//
//		for (Map.Entry<String, List<String>> tableList : tableName.entrySet()) {
//			String moduleName = tableList.getKey();
//			LOGGER.info("###DemoRollUp executing table  is"+"  "+moduleName);
//			List<String> valueList = tableList.getValue();
//			List<FacilioField> fields = new ArrayList<>();
//			FacilioField idField = null;
//			if(idColumntablesName.containsKey(moduleName)){
//				for(Map.Entry<String, List<String>> idList : idColumntablesName.entrySet()) {
//					String idKey = idList.getKey();
//					List<String> idValueList = idList.getValue();
//					if(idKey.equalsIgnoreCase(moduleName)) {
//						for(String idcolumn:idValueList) {
//							idField = FieldFactory.getField(idcolumn, idcolumn, FieldType.ID);
//							break;
//						}
//
//=======
			LOGGER.info("DemoRollUp job start to executed");
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
						System.out.println("###DemoRoleUp "+" "+count+" "+"  of rows updated in  "+key+"  successfully");
						LOGGER.info("###DemoRollUpJob "+count+" of rows updated in  "+key+"  successfully");
					}
					catch(Exception e) {
						LOGGER.info("###Exception occurred in  DemoRollUpJob... TableName is:  "+ key+ e);
						throw e;
//>>>>>>> parent of 850453418... Batch Update added in DemoRollUp
					}
				}
//<<<<<<< HEAD
//			}else {
//				idField = FieldFactory.getField("ID", "ID", FieldType.ID);
//			}
//			fields.add(idField);
//			for(String columns:valueList) {
//				fields.add(FieldFactory.getField(columns, columns, FieldType.DATE_TIME));
//			}
//
//			List<Map<String, Object>> columnData = getColumnDataValue(fields,moduleName);
//			if(CollectionUtils.isEmpty(columnData)) {
//				continue;
//			}
//			List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdate = addTtimewithColumns(columnData, fields, daysToMillisec);
//			if(CollectionUtils.isEmpty(batchUpdate)) {
//				continue;
//			}
//			
//			System.out.println("timeUpdatedValue is :"+columnData);
//			try {
//				new GenericUpdateRecordBuilder()
//				.table(moduleName)
//				.fields(fields)
//				.batchUpdate(Collections.singletonList(idField), batchUpdate)
//				;
////				System.out.println("###DemoRoleUp"+" "+" "+"of rows updated in"+moduleName+"successfully");
//				LOGGER.info("###DemoRollUp updated "+moduleName+"successfully");
//=======
//>>>>>>> parent of 850453418... Batch Update added in DemoRollUp
			}
			catch(Exception e) {
				throw e;
			}
			LOGGER.info("####DemoRollUp Job completed time  is####" + (System.currentTimeMillis()-startTime));
		return false;
	}

}