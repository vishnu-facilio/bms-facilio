package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.facilio.services.filestore.FileStore;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
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
			long jobStart = System.currentTimeMillis();
			long deletedTime;
			long orgId = jc.getOrgId();
			Set<String> namespaces = FileStore.getAllNamespaces();
			for (String namespace : namespaces) {
				FileStore.NamespaceConfig namespaceConfig = FileStore.getNamespace(namespace);
				deletedTime = DateTimeUtil.addDays(System.currentTimeMillis(), -namespaceConfig.getDataRetention());
				if (deletedTime <= 0) {
					throw new IllegalArgumentException("Deleted Time must not empty..." + deletedTime);
				}
				deleteFiles(deletedTime, orgId, namespaceConfig);
			}
			LOGGER.info("FacilioFile deletion Job -- time taken to complete is  :"+(System.currentTimeMillis()-jobStart));
		} catch (Exception e) {
			LOGGER.error("Exception occurred in DeleteFileRecordsJob  :  ", e);
			CommonCommandUtil.emailException("DeleteFileRecordsJob","DeleteFileRecordsJob Failed - jobid -- " + jc.getJobId(), e);
			throw e;
		}
	}

	private void deleteFiles(long deletedTime, long orgId, FileStore.NamespaceConfig namespaceConfig) throws Exception {
		List<FacilioField> fields = FieldFactory.getFileFields(namespaceConfig.getTableName());
		FacilioField deleteColumn = FieldFactory.getAsMap(fields).get("deletedTime"); 
		FacilioField orgIdColumn = FieldFactory.getAsMap(fields).get("orgId");
		FacilioField idColumn = FieldFactory.getAsMap(fields).get("fileId");
		List<FacilioField> idField = new ArrayList<>();
		idField.add(idColumn);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(idField)
				.table(namespaceConfig.getTableName())
				.andCondition(CriteriaAPI.getCondition(orgIdColumn, String.valueOf(orgId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(deleteColumn, String.valueOf(deletedTime),DateOperators.IS_BEFORE))
				.orderBy("ORGID,DELETED_TIME").limit(10000);
		while (true) {
			List<Map<String, Object>> props = builder.get();
			if (CollectionUtils.isEmpty(props)) {
				break;
			}
			List<Long> fileIds = props.stream().map(p -> (Long) p.get("fileId")).collect(Collectors.toList());
			FacilioFactory.getFileStore().deleteFilesPermanently(namespaceConfig.getName(), fileIds);
		}
	}
}
