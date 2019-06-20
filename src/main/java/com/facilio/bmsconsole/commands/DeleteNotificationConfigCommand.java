package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.NotificationConfigAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class DeleteNotificationConfigCommand implements Command{

	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		FacilioModule usersConfiguredModule = modBean.getModule(FacilioConstants.ContextNames.NOTIFICATION_USER);
		
		
		if (CollectionUtils.isNotEmpty(recordIds)) {
			for(Long id : recordIds) {
				NotificationConfigAPI.deleteUsersConfigured(usersConfiguredModule, id);
				NotificationConfigAPI.deleteJobsConfigured(id);	
			}
		}
		return false;
	}

}
