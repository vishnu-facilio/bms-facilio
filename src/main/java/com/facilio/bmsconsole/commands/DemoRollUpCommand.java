package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.util.DemoRollUpUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder.BatchUpdateContext;
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
		Map<String,List<String>> tableName=DemoRollUpUtil.TABLES_WITH_COLUMN;
		Map<String,List<String>> idColumntablesName=DemoRollUpUtil.TABLES_WITH_ID_COLUMN;
		long orgId=(long) context.get(ContextNames.DEMO_ROLLUP_JOB_ORG);

		for (Map.Entry<String, List<String>> tableList : tableName.entrySet()) {
			String moduleName = tableList.getKey();
			List<String> valueList = tableList.getValue();
			List<FacilioField> fields = new ArrayList<>();
			FacilioField idField = null;
			if(idColumntablesName.containsKey(moduleName)){
				for(Map.Entry<String, List<String>> idList : idColumntablesName.entrySet()) {
					String idKey = idList.getKey();
					List<String> idValueList = idList.getValue();
					if(idKey.equalsIgnoreCase(moduleName)) {
						for(String idcolumn:idValueList) {
							idField = FieldFactory.getField(idcolumn, idcolumn, FieldType.ID);
							break;
						}

					}

				}
			}else {
				idField = FieldFactory.getField("ID", "ID", FieldType.ID);
			}
			fields.add(idField);
			for(String columns:valueList) {
				fields.add(FieldFactory.getField(columns, columns, FieldType.DATE_TIME));
			}

			List<Map<String, Object>> columnData = getColumnDataValue(fields,moduleName);
			if(CollectionUtils.isEmpty(columnData)) {
				continue;
			}
			List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdate = addTtimewithColumns(columnData, fields, daysToMillisec);
			if(CollectionUtils.isEmpty(batchUpdate)) {
				continue;
			}
			
			System.out.println("timeUpdatedValue is :"+columnData);
			try {
				new GenericUpdateRecordBuilder()
				.table(moduleName)
				.fields(fields)
				.batchUpdate(Collections.singletonList(idField), batchUpdate)
				;
//				System.out.println("###DemoRoleUp"+" "+" "+"of rows updated in"+moduleName+"successfully");
//				LOGGER.info("###DemoRollUp"+" "+" "+"of rows updated in"+moduleName+"successfully");
			}
			catch(Exception e) {
				System.out.println("Exception occurred### in  DemoRollUpJob... TableName is.. "+ moduleName+ e);
				LOGGER.info("#####Exception occurred in  DemoRollUpJob Where the  TableName is:"+"  "+ moduleName+"  "+ e);
				throw e;
			}
		}
		LOGGER.info("####DemoRollUp Job completed time is :"+"  " + (System.currentTimeMillis()-startTime));
		return false;
	}

	private List<BatchUpdateContext> addTtimewithColumns(List<Map<String, Object>> columnData, List<FacilioField> fields, long daysToMillisec) {
		// TODO Auto-generated method stub
		List<BatchUpdateContext> updateBatches = new ArrayList<>();
		for(Map<String,Object> map:columnData) {
			BatchUpdateContext updateBatch = new BatchUpdateContext();
			for (FacilioField field : fields) {
				if (field.getDataTypeEnum() != FieldType.ID) {
					Long value = (Long) map.get(field.getName());
					if (value != null) {
						value = value + daysToMillisec;
						map.put(field.getName(), value);
					}
				}
				else {
					updateBatch.addWhereValue(field.getName(), map.get(field.getName()));
				}
				updateBatch.setUpdateValue(map);
				
			}
			updateBatches.add(updateBatch);
		}
		return updateBatches;
	}

	private List<Map<String, Object>> getColumnDataValue(List<FacilioField> fields, String tableName) throws Exception {
		// TODO Auto-generated method stub
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(tableName);
		List<Map<String, Object>> props = builder.get();
		return props;
	}

}