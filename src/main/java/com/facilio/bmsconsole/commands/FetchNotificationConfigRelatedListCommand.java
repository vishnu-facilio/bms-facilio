package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NotificationConfigContext;
import com.facilio.bmsconsole.context.NotificationUserContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.JobContext;

public class FetchNotificationConfigRelatedListCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<NotificationConfigContext> notificationConfigList = (List<NotificationConfigContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(notificationConfigList)) {
			for(NotificationConfigContext notiConfig : notificationConfigList) {
				notiConfig.setNotificationUsers(getUsersConfigured(notiConfig.getId()));
				notiConfig.setJobContext(getJobContext(notiConfig.getId()));
			}
		}
		return false;
	}
	
	private List<NotificationUserContext> getUsersConfigured(long configId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NOTIFICATION_USER);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		SelectRecordsBuilder<NotificationUserContext> builder = new SelectRecordsBuilder<NotificationUserContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition("CONFIGURATION_ID", "configurationId", String.valueOf(configId),NumberOperators.EQUALS));
				;

		List<NotificationUserContext> list = builder.get();
		return list;

	}
	
	private JobContext getJobContext(long configId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table("Jobs")
				.select(FieldFactory.getJobFields())
				.andCondition(CriteriaAPI.getCondition("JOBID", "jobId", String.valueOf(configId), NumberOperators.EQUALS));
		List<Map<String, Object>> list = selectBuilder.get();
		List<JobContext> jobList = FieldUtil.getAsBeanListFromMapList(list, JobContext.class);
		if(CollectionUtils.isNotEmpty(jobList)) {
			return jobList.get(0);
		}
		return null;

	}

}
