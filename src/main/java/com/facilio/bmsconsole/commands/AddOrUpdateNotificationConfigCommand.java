package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NotificationConfigContext;
import com.facilio.bmsconsole.context.NotificationConfigContext.Mode;
import com.facilio.bmsconsole.context.NotificationUserContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.NotificationConfigAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;

public class AddOrUpdateNotificationConfigCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		//add action(template and workflow)
		FacilioModule module = ModuleFactory.getNotificationConfigModule();
		List<FacilioField> fields = FieldFactory.getNotificationConfigFields();
		List<NotificationConfigContext> configs = (List<NotificationConfigContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(configs)) {
			for(NotificationConfigContext config : configs) {
				if(config.getParentId() <= 0) {
					throw new IllegalArgumentException("Please provide a valid parent record Id");
				}
				if(StringUtils.isEmpty(config.getConfigModuleName())) {
					throw new IllegalArgumentException("Please provide a valid parent module name");
				}
				if(config.getActionId() <= 0) {
					addAction(config);
				}
				if(config.getId() > 0) {
				   updateRecord(FieldUtil.getAsProperties(config), module.getTableName(), fields, config.getId());
				   NotificationConfigAPI.deleteUsersConfigured(config.getId());
				   NotificationConfigAPI.deleteJobsConfigured(config.getId());
				   CriteriaAPI.deleteCriteria(config.getCriteria().getCriteriaId());
				   
				}
				else {
					List<Map<String, Object>> mapAdded = addRecord(Collections.singletonList(FieldUtil.getAsProperties(config)), ModuleFactory.getNotificationConfigModule().getTableName(), FieldFactory.getNotificationConfigFields());
					config.setId((Long)mapAdded.get(0).get("id")); 
				}
				updateNotificationUsers(config.getNotificationUsers(), config.getId());
				addJob(config);
			}
		}
		
		return false;
	}
	
	public List<Map<String, Object>>  addRecord(List<Map<String, Object>> mapList, String tableName, List<FacilioField> fields) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(tableName)
				.fields(fields)
				.addRecords(mapList);
		insertBuilder.save();
		return mapList;

	}
	
	public void updateRecord(Map<String, Object> map, String tableName, List<FacilioField> fields, long id) throws Exception {
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
				.table(tableName)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getNotificationUserModule()));
		 updateRecordBuilder.update(map);
	}

	private void updateNotificationUsers(List<NotificationUserContext> notificationUsers, long configId) throws Exception {
		if(CollectionUtils.isNotEmpty(notificationUsers)) {
			List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
			for(NotificationUserContext notificationUser : notificationUsers) {
				notificationUser.setConfigId(configId);
				listMap.add(FieldUtil.getAsProperties(notificationUser));
			}
			addRecord(listMap,ModuleFactory.getNotificationUserModule().getTableName(), FieldFactory.getNotificationUserFields());
			
		}
	}
	
	private void addJob(NotificationConfigContext config) throws Exception {
		ScheduleInfo info = config.getSchedule();
		if(config.getScheduleMode() == Mode.PERIODIC.getValue()) {
			FacilioTimer.scheduleCalendarJob(config.getId(), FacilioConstants.Job.NOTIFICATION_TRIGGER_JOB_NAME, System.currentTimeMillis(), info, FacilioConstants.Job.EXECUTER_NAME_FACILIO);
		}
		else {
			long nextExecutionTime = info.nextExecutionTime(getStartTimeInSecond(System.currentTimeMillis()));
			FacilioTimer.scheduleOneTimeJob(config.getId(), FacilioConstants.Job.NOTIFICATION_TRIGGER_JOB_NAME, nextExecutionTime, FacilioConstants.Job.EXECUTER_NAME_FACILIO);
		}
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
	
	private long getStartTimeInSecond(long startTime) {
		long startTimeInSecond = startTime/1000;
		startTimeInSecond = startTimeInSecond - 300; //for calculating next execution time
		
		return startTimeInSecond;
	}
	
	private void addCriteria(NotificationConfigContext config) throws Exception {
		if(config.getCriteria() != null) {
			config.getCriteria().validatePattern();
			long criteriaId = CriteriaAPI.addCriteria(config.getCriteria(),AccountUtil.getCurrentOrg().getId());
			config.getCriteria().setCriteriaId(criteriaId);
		}
	
	}
	
	
}
