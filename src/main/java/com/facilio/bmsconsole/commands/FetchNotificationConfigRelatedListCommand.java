package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NotificationConfigContext;
import com.facilio.bmsconsole.context.NotificationUserContext;
import com.facilio.bmsconsole.util.NotificationConfigAPI;
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
import com.facilio.tasker.job.JobStore;

public class FetchNotificationConfigRelatedListCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<NotificationConfigContext> notificationConfigList = (List<NotificationConfigContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(notificationConfigList)) {
			for(NotificationConfigContext notiConfig : notificationConfigList) {
				notiConfig.setNotificationUsers(FieldUtil.getAsBeanListFromMapList(NotificationConfigAPI.getUsersConfigured(notiConfig.getId()), NotificationUserContext.class));
				notiConfig.setSchedule(JobStore.getJob(notiConfig.getId(), FacilioConstants.Job.NOTIFICATION_TRIGGER_JOB_NAME).getSchedule());
			}
		}
		return false;
	}

}
