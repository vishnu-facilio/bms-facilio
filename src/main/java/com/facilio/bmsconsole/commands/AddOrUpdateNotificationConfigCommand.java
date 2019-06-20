package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NotificationConfigContext;
import com.facilio.bmsconsole.context.NotificationUserContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.JobsAPI;
import com.facilio.bmsconsole.util.NotificationConfigAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.JobContext;

public class AddOrUpdateNotificationConfigCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		//add action(template and workflow)
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NOTIFICATION_CONFIG);
		FacilioModule usersConfiguredModule = modBean.getModule(FacilioConstants.ContextNames.NOTIFICATION_USER);
		
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		List<FacilioField> usersConfiguredModuleFields = modBean.getAllFields(usersConfiguredModule.getName());
		
		List<NotificationConfigContext> configs = (List<NotificationConfigContext>)context.get(FacilioConstants.ContextNames.RECORD);
		if(CollectionUtils.isNotEmpty(configs)) {
			for(NotificationConfigContext config : configs) {
				if(config.getActionId() <= 0) {
					addAction(config);
				}
				if(config.getId() > 0) {
				   updateRecord(config, module, fields);
				   NotificationConfigAPI.deleteUsersConfigured(usersConfiguredModule, config.getId());
				   NotificationConfigAPI.deleteJobsConfigured(config.getId());	
				}
				else {
					addRecord(false, Collections.singletonList(config), module, fields);
				}
				updateNotificationUsers(usersConfiguredModule, usersConfiguredModuleFields, config.getNotificationUsers(), config.getId());
				addJob(config);
			}
		}
		
		return false;
	}
	
	public void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
	}
	
	private void updateNotificationUsers(FacilioModule module, List<FacilioField> fields, List<NotificationUserContext> notificationUsers, long configId) throws Exception {
		if(CollectionUtils.isNotEmpty(notificationUsers)) {
			for(NotificationUserContext notificationUser : notificationUsers) {
				notificationUser.setConfigId(configId);
			}
			addRecord(false, notificationUsers, module, fields);
		}
	}
	
	private void addJob(NotificationConfigContext config) throws Exception {
		JobContext job = config.getJobContext();
		job.setJobId(config.getId());
		job.setJobName("NotificationConfigurationTrigger");
		job.setExecutorName("priority");
		JobsAPI.addJobs(Collections.singletonList(job));
		//add job record
	}

	private void addAction(NotificationConfigContext config) throws Exception {
		if(config.getActionContext() == null) {
			throw new IllegalArgumentException("Action must be associated for the notification trigger");
		}
		if(config.getActionContext().getDefaultTemplateId() > 0) {
			ActionAPI.addActions(Collections.singletonList(config.getActionContext()));
		}
		else {
			ActionAPI.addActions(Collections.singletonList(config.getActionContext()), null);
		}
		config.setActionId(config.getActionContext().getId());
	}
	
	public void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		updateRecordBuilder.update(data);
	}
	
	
}
