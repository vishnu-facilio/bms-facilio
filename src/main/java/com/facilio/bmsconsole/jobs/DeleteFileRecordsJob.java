package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class DeleteFileRecordsJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(DeleteFileRecordsJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			long startTime = System.currentTimeMillis();
			long deletedTime = DateTimeUtil.addDays(startTime, -5);
			deleteFiles(deletedTime);
			LOGGER.info("DeleteFile time taken to complete is  :"+(System.currentTimeMillis()-startTime));
		} catch (Exception e) {
			LOGGER.error("Exception occurred in DeleteFileRecordsJob  :  ", e);
			CommonCommandUtil.emailException("DeleteFileRecordsJob",
					"DeleteFileRecordsJob Failed - jobid -- " + jc.getJobId(), e);
		}
	}

	private void deleteFiles(long deletedTime) throws Exception {
		try {
			List<FacilioField> fields = FieldFactory.getFileFields();
			FacilioField deleteColumn = FieldFactory.getAsMap(fields).get("deletedTime"); 
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(fields)
					.table(ModuleFactory.getFilesModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(deleteColumn, CommonOperators.IS_NOT_EMPTY))
					.andCondition(CriteriaAPI.getCondition(deleteColumn,String.valueOf(deletedTime),DateOperators.IS_BEFORE))
					.orderBy("FILE_ID").limit(10000);
			while (true) {
				List<Map<String, Object>> props = builder.get();
				if (CollectionUtils.isEmpty(props)) {
					break;
				}
				List<Long> fileIds = props.stream().map(p -> (Long) p.get("fileId")).collect(Collectors.toList());
				FacilioFactory.getFileStore().deleteFilesPermanently(fileIds);
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
