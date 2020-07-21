package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.BaseCriteriaAPI;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.util.DBConf;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.service.FacilioService;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class IntervalBasedInstantJobQueueDeletion extends FacilioJob {
	
    private static final Logger LOGGER = LogManager.getLogger(IntervalBasedInstantJobQueueDeletion.class.getName());
    
	private static final String RULE_INSTANT_JOB_QUEUE_TABLE_NAME = "RuleInstantJobQueue";	
	private static final String INSTANT_JOB_QUEUE_TABLE_NAME = "InstantJobQueue";
	private static final String FILE_NAMESPACE = "queue_data";

	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			long jobStartTime = System.currentTimeMillis();
			JSONObject timeProps = getInstantJobDeletionProps(jc.getJobId(), jc.getExecutorName());
			if(timeProps != null) 
			{
				Long startTime = (Long) timeProps.get("startTime");
				Long endTime = (Long) timeProps.get("endTime");
				LOGGER.info("IntervalBasedInstantJobQueueDeletion Started for jobId: " +jc.getJobId()+ " between " +startTime+ " and " +endTime+ " at "+jobStartTime);				
				if(startTime != null && startTime != -1 && endTime != null && endTime != -1) 
				{
					deleteInstantJobQueue(startTime, endTime, RULE_INSTANT_JOB_QUEUE_TABLE_NAME);
					deleteInstantJobQueue(startTime, endTime, INSTANT_JOB_QUEUE_TABLE_NAME);	
				}
				LOGGER.info("Time taken for IntervalBasedInstantJobQueueDeletion for jobId: "+jc.getJobId()+" between "+startTime+" and "+endTime+" is  -- "+(System.currentTimeMillis() - jobStartTime));				
			}	
		}
		catch (Exception e) {
			LOGGER.info("Exception occurred in IntervalBasedInstantJobQueueDeletion:  "+e);
		}
	}
	
	private static void deleteInstantJobQueue(long startTime, long endTime, String tableName) throws Exception{	
		try {
			long processStartTime = System.currentTimeMillis();
			int batchCount = 0;
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().select(getFacilioQueueFields()).table(tableName)
					.andCondition(BaseCriteriaAPI.getCondition("DELETED_TIME", "deletedTime", null,
							CommonOperators.IS_NOT_EMPTY))
					.andCondition(BaseCriteriaAPI.getCondition("DELETED_TIME", "deletedTime",
							startTime+","+endTime, DateOperators.BETWEEN))
					.orderBy("ID")
					.limit(30000);

			while (true) {
				List<Map<String, Object>> props = select.get();

				if (CollectionUtils.isEmpty(props)) {
					break;
				}
				List<Long> ids = props.stream().map(p -> (Long) p.get("id")).collect(Collectors.toList());
				List<Long> fileIds = props.stream().map(p ->(Long)p.get("fileId")).collect(Collectors.toList());
				DBConf.getInstance().markFilesAsDeleted(FILE_NAMESPACE, fileIds);
				GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder().table(tableName);
				int deletionCount = builder.batchDeleteById(ids);
				LOGGER.info(tableName + " IntervalBasedInstantJobQueueDeletion deleted queue count is : " +deletionCount+ ". DeletedBatchCount: "+ ++batchCount);
			}
			LOGGER.info("Time taken for InstantJobDeletionTable: "+tableName+" between "+startTime+" and "+endTime+" is  -- "+(System.currentTimeMillis() - processStartTime));				
		} catch (Exception e) {
			LOGGER.info("IntervalBasedInstantJobQueueDeletion Exception occurred in FacilioInstantJobDeletion:  "+e);
		}
	}
	
	private static List<FacilioField> getFacilioQueueFields() {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getField("id", "ID", FieldType.ID));
		fields.add(getField("orgId", "ORGID",FieldType.NUMBER));
		fields.add(getField("fileId", "FILE_ID", FieldType.NUMBER));
		fields.add(getField("addedTime", "ADDED_TIME", FieldType.DATE_TIME));
		fields.add(getField("visibilityTimeout", "VISIBILITY_TIMEOUT", FieldType.NUMBER));
		fields.add(getField("lastClientReceivedTime", "LAST_CLIENT_RECEIVED_TIME", FieldType.DATE_TIME));
		fields.add(getField("maxClientReceiptCount", "MAX_CLIENT_RECEIPT_COUNT", FieldType.NUMBER));
		fields.add(getField("clientReceiptCount", "CLIENT_RECEIPT_COUNT", FieldType.NUMBER));
		fields.add(getField("deletedTime", "DELETED_TIME", FieldType.DATE_TIME));
		return fields;
	}
	
	private static FacilioField getField(String name, String colName, FieldType type) {
		FacilioField columnFld = null;
		switch (type) {
		case NUMBER:
		case DECIMAL:
			columnFld = new NumberField();
			break;
		case BOOLEAN:
			columnFld = new BooleanField();
			break;
		case ENUM:
			columnFld = new EnumField();
			break;
		case SYSTEM_ENUM:
			columnFld = new SystemEnumField();
			break;
		default:
			columnFld = new FacilioField();
			break;
		}
		columnFld.setName(name);
		columnFld.setDisplayName(colName);
		columnFld.setColumnName(colName);
		columnFld.setDataType(type);
		return columnFld;
	}
	
	public static JSONObject getInstantJobDeletionProps (long jobId, String executorName) throws Exception {
		FacilioModule module = ModuleFactory.getInstantJobDeletionPropsModule();
		List<FacilioField> fields = FieldFactory.getInstantJobDeletionPropsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField jobIdField = fieldMap.get("jobId");
		FacilioField executorNameField = fieldMap.get("executorName");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(jobIdField, String.valueOf(jobId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(executorNameField, executorName, StringOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Long startTime = (Long) props.get(0).get("startTime");
			Long endTime = (Long) props.get(0).get("endTime");
			JSONObject timeProps = new JSONObject();
			timeProps.put("startTime", startTime);
			timeProps.put("endTime", endTime);
			return timeProps;
		}
		return null;
	}
}
