package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class DeleteFileRecordsJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(DeleteFileRecordsJob.class.getName());
	private static List<FacilioField> fields = FieldFactory.getFileFields();
	private static Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			long orgId = -1L;
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
			bean.deleteOlderFiles(fields, fieldMap);
			List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
			for (Organization org : orgs) {
				ModuleCRUDBean modBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", org.getOrgId());
				modBean.deleteOlderFiles(fields, fieldMap);
			}
			
		} catch (Exception e) {
			LOGGER.error("Exception occurred in DeleteFileRecordsJob  :  ", e);
			CommonCommandUtil.emailException("DeleteFileRecordsJob",
					"DeleteFileRecordsJob Failed - jobid -- " + jc.getJobId(), e);
		}
	}
	
}
