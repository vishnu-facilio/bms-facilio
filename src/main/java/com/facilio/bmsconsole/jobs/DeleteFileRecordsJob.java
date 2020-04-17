package com.facilio.bmsconsole.jobs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
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
			List<FacilioField> fields = FieldFactory.getFileFields();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(fields)
					.table(ModuleFactory.getFilesModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("deletedTime"), CommonOperators.IS_NOT_EMPTY))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("deletedTime"), String.valueOf(DateTimeUtil.addMonths(System.currentTimeMillis(), -1)),DateOperators.IS_BEFORE));

			GenericSelectRecordBuilder.GenericBatchResult batchSelect = builder.getInBatches("FILE_ID", 30000);
			while (batchSelect.hasNext()) {
				List<Map<String, Object>> props = batchSelect.get();
				List<Long> fileIds = props.stream().map(p -> (Long) p.get("fileId")).collect(Collectors.toList());
				FacilioFactory.getFileStore().deleteFilesPermanently(fileIds);
			}
		} catch (Exception e) {
			LOGGER.info("Exception occurred in DeleteFileRecordsJob  :  ", e);
			CommonCommandUtil.emailException("DeleteFileRecordsJob","DeleteFileRecordsJob Failed - jobid -- " + jc.getJobId(), e);
		}
	}
}
