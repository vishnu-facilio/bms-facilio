package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.facilio.tasker.FacilioTimer;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.DigestConfigContext;
import com.facilio.bmsconsole.context.ScheduledActionContext;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.DigestTemplateFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.job.JobContext;

public class DigestConfigAPI {

	public static void sendInstantDigestMail(long id, String... toAddr) throws Exception {
		
		Map<String, Object> config = getDigestConfig(id);
		if (config != null) {
				Long scheduledActionId = (Long)config.get("scheduledActionId");
				ScheduledActionContext schAction = ScheduledActionAPI.getScheduledAction(scheduledActionId);
				ActionContext action = ActionAPI.getAction(schAction.getActionId());
				if (toAddr != null && toAddr.length > 0 && action.getTemplate() instanceof EMailTemplate) {
					((EMailTemplate)action.getTemplate()).setTo(toAddr[0]);
				}
				Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
				action.executeAction(placeHolders, null, null, null);
		}
		
	}
	
	public static void updateConfig(Map<String, Object> updateProps, List<Long> updateIds) throws SQLException {
		FacilioModule module = ModuleFactory.getDigestConfigModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
													.table(module.getTableName())
													.fields(FieldFactory.getDigestConfigFields())
													.andCondition(CriteriaAPI.getIdCondition(updateIds, module));
		updateBuilder.update(updateProps);
	
	}
	
	public static Map<String, Object> getDigestConfig(Long id) throws Exception{
		FacilioModule module = ModuleFactory.getDigestConfigModule();
		List<FacilioField> fields = FieldFactory.getDigestConfigFields();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(fields)
													.andCondition(CriteriaAPI.getIdCondition(id, module))
													;
		List<Map<String, Object>> configs = builder.get();
		if(CollectionUtils.isNotEmpty(configs)) {
			return configs.get(0);
		}
		return null;
	}
	
	public static List<DigestConfigContext> getAllEnabledDigestConfig(Long configId) throws Exception{
		FacilioModule module = ModuleFactory.getDigestConfigModule();
		List<FacilioField> fields = FieldFactory.getDigestConfigFields();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(fields)
													;
		if(configId != null && configId > 0) {
			builder.andCondition(CriteriaAPI.getIdCondition(configId, module));
		}
		List<Map<String, Object>> configs = builder.get();
		if(CollectionUtils.isNotEmpty(configs)) {
			List<DigestConfigContext> configsBeans = FieldUtil.getAsBeanListFromMapList(configs, DigestConfigContext.class);
			for(DigestConfigContext digestConfig : configsBeans) {
				ScheduledActionContext schAction = ScheduledActionAPI.getScheduledAction(digestConfig.getScheduledActionId());
				JobContext job = FacilioTimer.getJob(digestConfig.getScheduledActionId(), FacilioConstants.Job.DIGEST_JOB_NAME);
				ScheduleInfo info = job.getSchedule();
				schAction.setFrequency(info.getFrequencyType());
				digestConfig.setScheduledActionId(schAction.getId());
				digestConfig.setScheduledAction(schAction);
				digestConfig.setSchedule(job.getSchedule());
				digestConfig.setMapContext(DigestTemplateFactory.getDigestTemplate(digestConfig.getDefaultTemplateId()));
			}
			return configsBeans;
		}
		return null;
	}
	
	public static void deleteDigestConfig(long configId) throws Exception {
		FacilioModule module = ModuleFactory.getDigestConfigModule();
		Map<String, Object> config = getDigestConfig(configId);
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(configId, module));
		builder.delete();
		ScheduledActionAPI.deleteScheduledAction((long)config .get("scheduledActionId"),FacilioConstants.Job.DIGEST_JOB_NAME);
		
	}
	
}
