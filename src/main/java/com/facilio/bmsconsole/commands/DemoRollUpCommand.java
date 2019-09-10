package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.DemoRollUpUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

public class DemoRollUpCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(DemoRollUpCommand.class.getName());

	@SuppressWarnings("resource")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

			long executionTime=(long) context.get(FacilioConstants.ContextNames.DEMO_ROLLUP_EXECUTION_TIME);
			long daysToMillisec=(executionTime * 24 * 60 * 60 * 1000);
			long startTime = System.currentTimeMillis();
			Map<String, Object> timeUpdatedValue = new HashMap<>();
			Map<String,List<String>> tableName=DemoRollUpUtil.TABLES_WITH_COLUMN;
			long orgId=(long) context.get(ContextNames.DEMO_ROLLUP_JOB_ORG);
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection();) {
				for (Map.Entry<String, List<String>> tableList : tableName.entrySet()) {
					String moduleName = tableList.getKey();
					List<String> valueList = tableList.getValue();
					List<FacilioField> fields = new ArrayList<>();
					for(String columns:valueList) {
						fields.add(FieldFactory.getField(columns, columns, FieldType.DATE_TIME));
						fields.add(FieldFactory.getField("ID", "ID", FieldType.ID));
					}
					List<Map<String, Object>> columnData = getColumnDataValue(fields,moduleName);
					if(columnData.isEmpty()) {
						continue;
					}
				timeUpdatedValue=addTtimewithColumns(columnData,daysToMillisec);
				System.out.println("timeUpdatedValue is :"+timeUpdatedValue);
				Map<String, Object> whereValue = getIdWhereValue(columnData);	
				List<FacilioField> whereFields = getFields(fields);				
				List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdate = new ArrayList<GenericUpdateRecordBuilder.BatchUpdateContext>();
				GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
				updateVal.setUpdateValue(timeUpdatedValue);

				updateVal.setWhereValue(whereValue);

				batchUpdate.add(updateVal);

				try {
					new GenericUpdateRecordBuilder()
					.table(moduleName)
					.fields(fields)
					.batchUpdate(whereFields, batchUpdate)
					;
					System.out.println("###DemoRoleUp"+" "+" "+"of rows updated in"+moduleName+"successfully");
					LOGGER.info("###DemoRollsUp"+" "+" "+"of rows updated in"+moduleName+"successfully");
				}
				catch(Exception e) {
					System.out.println("Exception occurred### in  DemoRollUpJob... TableName is.. "+ moduleName+ e);
					LOGGER.info("Exception occurred### in  DemoRollUpJob... TableName is.. "+ moduleName+ e);
					throw e;
				}
				}
			}
			
		return false;
	}

	private List<FacilioField> getFields(List<FacilioField> fields) {
		// TODO Auto-generated method stub
		List<FacilioField> idFields = new ArrayList<>();
		for (FacilioField id : fields) {
	        if (id.getName().equals("ID")) {
	            idFields.add(id);
	        }
	    }
      if(!idFields.isEmpty()) {
    	  return idFields;
      }
      	
      return Collections.EMPTY_LIST;
	}

	private Map<String, Object> getIdWhereValue(List<Map<String, Object>> columnData) {
		// TODO Auto-generated method stub
		Map<String,Object> fieldValue = new HashMap<>();
		for(Map<String,Object> map:columnData) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
		        String key = entry.getKey();
		        if(key!=null && key.equalsIgnoreCase("ID")) {
		        	 long value = (long) entry.getValue();
		        	 if(value!=0) {
		        		 fieldValue.put(key,value);
		        	 }
		        	 
		        }
		        
		    }
		}
		if(fieldValue!=null) {
			return fieldValue;
		}
		return Collections.EMPTY_MAP;
	}

	private Map<String, Object> addTtimewithColumns(List<Map<String, Object>> columnData, long daysToMillisec) {
		// TODO Auto-generated method stub
		Map<String,Object> fieldValue = new HashMap<>();
		for(Map<String,Object> map:columnData) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
		        String key = entry.getKey();
		        if(key!=null && !key.equalsIgnoreCase("ID") && !key.equalsIgnoreCase("ORGID")) {
		        	 long value = (long) entry.getValue();
		        	 value=value + daysToMillisec;
		        	 fieldValue.put(key,value);
		        }
		        
		    }
		}
		if(fieldValue!=null) {
			return fieldValue;
		}
		return Collections.EMPTY_MAP;
	}

	private List<Map<String, Object>> getColumnDataValue(List<FacilioField> fields, String tableName) throws Exception {
		// TODO Auto-generated method stub
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(tableName);
		List<Map<String, Object>> props = builder.get();
		if(props!=null || !props.isEmpty()) {
			return props;
		}
		return Collections.EMPTY_LIST;
	}

}