package com.facilio.bmsconsole.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PreventiveMaintenanceAPI {
	
	private static final Logger LOGGER = Logger.getLogger(PreventiveMaintenanceAPI.class.getName());
	public static final int PM_CALCULATION_DAYS = 62;
	
	public static List<PMJobsContext> createPMJobs (PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long endTime) throws Exception { //Both in seconds
		return createPMJobs(pm, pmTrigger, startTime, endTime, true);
	}
	
	public static List<PMJobsContext> createPMJobs (PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long endTime, boolean addToDb) throws Exception { //Both in seconds
		long nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		List<PMJobsContext> pmJobs = new ArrayList<>();
		while (nextExecutionTime <= endTime && (pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			PMJobsContext pmJob = new PMJobsContext();
			pmJob.setPmId(pm.getId());
			pmJob.setPmTriggerId(pmTrigger.getId());
			pmJob.setNextExecutionTime(nextExecutionTime);
			pmJob.setProjected(!addToDb);
			pmJob.setStatus(PMJobsStatus.ACTIVE);
			pmJobs.add(pmJob);
			nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
			currentCount++;
			if(pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
				break;
			}
		}
		if(addToDb) {
			addPMJobs(pmJobs);
		}
		return pmJobs;
	}
	
	public static List<Map<String, Object>> createProjectedPMJobs (PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long endTime) throws Exception { //Both in seconds
		startTime = pmTrigger.getSchedule().getFrequency() > 1 ? pmTrigger.getStartTime() / 1000 :  startTime;
		long nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		List<Map<String, Object>> pmJobs = new ArrayList<>();
		while (nextExecutionTime <= endTime && (pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			if (nextExecutionTime >= startTime) {
				Map<String, Object> pmJob = new HashMap<>();
				pmJob.put("pmId", pm.getId());
				pmJob.put("pmTriggerId", pmTrigger.getId());
				pmJob.put("nextExecutionTime", nextExecutionTime);
				pmJob.put("projected", true);
				pmJob.put("status", PMJobsStatus.ACTIVE);
				pmJobs.add(pmJob);
			}
			nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
			currentCount++;
			if(pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
				break;
			}
		}
		return pmJobs;
	}
	
	public static PMJobsContext createPMJobOnce(PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime) throws Exception {
		return createPMJobOnce(pm, pmTrigger, startTime, true);
	}
	
	public static PMJobsContext createPMJobOnce(PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, boolean addToDb) throws Exception {
		long nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		if((pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			PMJobsContext pmJob = new PMJobsContext();
			pmJob.setPmId(pm.getId());
			pmJob.setPmTriggerId(pmTrigger.getId());
			pmJob.setNextExecutionTime(nextExecutionTime);
			pmJob.setStatus(PMJobsStatus.ACTIVE);
			pmJob.setProjected(!addToDb);
			
			if (addToDb) {
				addPMJob(pmJob);
			}
			return pmJob;
		}
		return null;
	}
	
	public static List<PMJobsContext> createNNumberOfPMJobs(PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long count) throws Exception {
		long nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		int jobCount = 0;
		List<PMJobsContext> pmJobs = new ArrayList<>();
		while (jobCount < count && (pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			PMJobsContext pmJob = new PMJobsContext();
			pmJob.setPmId(pm.getId());
			pmJob.setPmTriggerId(pmTrigger.getId());
			pmJob.setNextExecutionTime(nextExecutionTime);
			pmJob.setStatus(PMJobsStatus.ACTIVE);
			pmJobs.add(pmJob);
			nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
			currentCount++;
			jobCount++;
			if(pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
				break;
			}
		}
		addPMJobs(pmJobs);
		return pmJobs;
	}
	
	public static PMJobsContext updateAndGetPMJob(PMJobsContext pmJob) throws Exception {
		updatePMJob(pmJob);
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(pmJob.getId(), pmJobsModule));
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(jobProps.get(0), PMJobsContext.class);
		}
		return null;
		
														
	}
	
	public static int updatePMJob(PMJobsContext pmJob) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.fields(fields)
															.table(pmJobsModule.getTableName())
															.andCondition(CriteriaAPI.getIdCondition(pmJob.getId(), pmJobsModule));
		return updateBuilder.update(FieldUtil.getAsProperties(pmJob));
	}
	
	public static void updatePMJobStatus(long id, PMJobsStatus status) throws Exception {
		PMJobsContext updatedJob = new PMJobsContext();
		updatedJob.setId(id);
		updatedJob.setStatus(status);
		updatePMJob(updatedJob);
	}
	
	public static PMJobsContext getNextPMJob(long pmTriggerId, long currentTime, boolean onlyActive) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField pmTriggerField = fieldsMap.get("pmTriggerId");
		FacilioField nextExecutionField = fieldsMap.get("nextExecutionTime");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField, String.valueOf(pmTriggerId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(currentTime), NumberOperators.GREATER_THAN))
														.limit(1)
														;
		
		if (onlyActive) {
			FacilioField isActive = fieldsMap.get("status");
			selectBuilder.andCondition(CriteriaAPI.getCondition(isActive, String.valueOf(PMJobsStatus.IN_ACTIVE.getValue()), NumberOperators.NOT_EQUALS));
		}
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(jobProps.get(0), PMJobsContext.class);
			return pmJob;
		}
		return null;
	}
	
	public static PMJobsContext getLastPMJob(PMTriggerContext pmTrigger) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField pmTriggerField = fieldsMap.get("pmTriggerId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField, String.valueOf(pmTrigger.getId()), NumberOperators.EQUALS))
														.orderBy("NEXT_EXECUTION_TIME DESC")
														.limit(1)
														;
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(jobProps.get(0), PMJobsContext.class);
			return pmJob;
		}
		return null;
	}
	
	public static List<PMJobsContext> getNextPMJobs(PMTriggerContext pmTrigger, long startTime, long endTime) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField pmTriggerField = fieldsMap.get("pmTriggerId");
		FacilioField nextExecutionField = fieldsMap.get("nextExecutionTime");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField, String.valueOf(pmTrigger.getId()), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(endTime), NumberOperators.LESS_THAN))
														.orderBy("nextExecutionTime")
														;
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			List<PMJobsContext> pmJobs = new ArrayList<>();
			for(Map<String, Object> jobProp : jobProps) {
				pmJobs.add(FieldUtil.getAsBeanFromMap(jobProp, PMJobsContext.class));
			}
			return pmJobs;
		}
		return null;
	}
	
	public static Map<Long, List<Map<String, Object>>> getPMJobsFromPMIds(List<Long> pmIds, long startTime, long endTime) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField pmIdField = fieldsMap.get("pmId");
		FacilioField nextExecutionField = fieldsMap.get("nextExecutionTime");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmIdField,pmIds, NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(endTime), NumberOperators.LESS_THAN))
														.orderBy("nextExecutionTime")
														;
		
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		Map<Long, List<Map<String, Object>>> pmJobs = new HashMap<>();
		if(jobProps != null && !jobProps.isEmpty()) {
			for (Map<String, Object> prop : jobProps) {
				// List<PMJobsContext> pmJobList = new ArrayList<>();
				// PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(prop, PMJobsContext.class);
				
				List<Map<String, Object>> pmJobList = pmJobs.get(prop.get("pmTriggerId"));
				if (pmJobList == null) {
					pmJobList = new ArrayList<>();
					pmJobs.put((Long) prop.get("pmTriggerId"), pmJobList);
				}
				pmJobList.add(prop);
			}
		}
		return pmJobs;
	}
	
	public static List<PMJobsContext> getPMJobs(List<Long> triggerIds, boolean hasOnlyNonEmptyTemlplates) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField pmTriggerField = fieldsMap.get("pmTriggerId");
		FacilioField templateField = fieldsMap.get("templateId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField,triggerIds, NumberOperators.EQUALS))
														;
		
		if (hasOnlyNonEmptyTemlplates) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(templateField,(String)null,CommonOperators.IS_NOT_EMPTY));
		}
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		List<PMJobsContext> pmJobs = new ArrayList<>();
		if(jobProps != null && !jobProps.isEmpty()) {
			for (Map<String, Object> prop : jobProps) {
				PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(prop, PMJobsContext.class);
				pmJobs.add(pmJob);
			}
		}
		return pmJobs;
	}
	
	public static List<PMJobsContext> getPMJobs(List<Long> ids) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(ids, pmJobsModule))
														;
		List<Map<String, Object>> jobProps = selectBuilder.get();
		List<PMJobsContext> pmJobs = new ArrayList<>();
		if(jobProps != null && !jobProps.isEmpty()) {
			for (Map<String, Object> prop : jobProps) {
				PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(prop, PMJobsContext.class);
				pmJobs.add(pmJob);
			}
		}
		return pmJobs;
	}
	
	private static void addPMJob(PMJobsContext pmJob) throws SQLException, RuntimeException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		FacilioModule module = ModuleFactory.getPMJobsModule();
		Map<String, Object> props = FieldUtil.getAsProperties(pmJob);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.fields(FieldFactory.getPMJobFields())
														.table(module.getTableName())
														.addRecord(props);
		
		insertBuilder.save();
		pmJob.setId((long) props.get("id"));
	}
	
	private static void addPMJobs(List<PMJobsContext> pmJobs) throws SQLException, RuntimeException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		FacilioModule module = ModuleFactory.getPMJobsModule();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(module.getTableName())
														.fields(FieldFactory.getPMJobFields())
														;
		List<Map<String, Object>> pmJobProps = new ArrayList<>();
		for (PMJobsContext pmJob : pmJobs) {
			pmJobProps.add(FieldUtil.getAsProperties(pmJob));
		}
		insertBuilder.addRecords(pmJobProps);
		insertBuilder.save();
		
		for(int itr = 0; itr < pmJobProps.size(); itr++) {
			Map<String, Object> pmJobProp = pmJobProps.get(itr);
			pmJobs.get(itr).setId((long) pmJobProp.get("id"));
		}
	}
	
	public static void schedulePMJob(PMJobsContext pmJob) throws Exception {
		FacilioTimer.scheduleOneTimeJob(pmJob.getId(), "PreventiveMaintenance", pmJob.getNextExecutionTime(), "priority");
		updatePMJobStatus(pmJob.getId(), PMJobsStatus.SCHEDULED);
	}
	
	public static void reSchedulePMJob(PMJobsContext pmJob) throws Exception {
		FacilioTimer.deleteJob(pmJob.getId(), "PreventiveMaintenance");
		FacilioTimer.scheduleOneTimeJob(pmJob.getId(), "PreventiveMaintenance", pmJob.getNextExecutionTime(), "priority");
		updatePMJobStatus(pmJob.getId(), PMJobsStatus.SCHEDULED);
	}
	
	private static PMJobsContext deletePMJob(long pmTriggerId) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		FacilioField pmTriggerField = FieldFactory.getAsMap(fields).get("pmTriggerId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField, String.valueOf(pmTriggerId), NumberOperators.EQUALS))
														;
		
		PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(selectBuilder.get().get(0), PMJobsContext.class);
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(pmJob.getId(), pmJobsModule));
		deleteBuilder.delete();
		return pmJob;
	}
	
	public static PreventiveMaintenance getActivePM(long id) throws Exception {
		return getPM(id, true);
	}
	
	public static PreventiveMaintenance getPM(long id) throws Exception {
		return getPM(id, false);
	}
	
	private static PreventiveMaintenance getPM(long id, boolean onlyActive) throws Exception {
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		
		if (onlyActive) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), PreventiveMaintenance.class);
		}
		return null;
	}
	
	public static List<PreventiveMaintenance> getAllActivePMs(Criteria filterCriteria) throws Exception {
		return getActivePMs(null,filterCriteria);
	}
	
	public static List<PreventiveMaintenance> getActivePMs(List<Long> ids, Criteria filterCriteria) throws Exception {
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		
		if(filterCriteria == null) {
			filterCriteria = new Criteria();
		}
		
		filterCriteria.addAndCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
		
		return getPMs(ids, filterCriteria,null, null);

	}
	
	public static List<PreventiveMaintenance> getPMsDetails(List<Long> ids) throws Exception {
		return getPMs(ids, null, null, null, true, true);
	}
	public static List<PreventiveMaintenance> getAllPMs(Long orgid,boolean onlyActive) throws Exception {
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														;
		if (onlyActive) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<Long> res = null;
		if(props != null && !props.isEmpty()) {
			
			res = new ArrayList<>();
			for(Map<String, Object> prop :props) {
				
				Long id = (Long) prop.get("id");
				res.add(id);
			}
			
			List<PreventiveMaintenance> pms = getPMs(res,null,null,null,true);
			
			return pms;
		}
		return null;
	}
	public static List<PreventiveMaintenance> getPMs(List<Long> ids, Criteria criteria, String searchQuery, JSONObject pagination, Boolean...fetchDependencies) throws Exception {
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		FacilioField pmSubjectField = FieldFactory.getAsMap(fields).get("title");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.orderBy("Preventive_Maintenance.CREATION_TIME DESC")
														;
		
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			
			selectBuilder.offset(offset);
			selectBuilder.limit(perPage);
		}
		
		if(ids != null && !ids.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		}
		
		if(criteria != null && !criteria.isEmpty()) {
			selectBuilder.andCriteria(criteria);
		}
		LOGGER.log(Level.SEVERE,"searchQuery ---- "+searchQuery);
		if (searchQuery!= null) {
			 selectBuilder.andCondition(CriteriaAPI.getCondition(pmSubjectField, searchQuery, StringOperators.CONTAINS));
		}
		
		boolean fetchDependency = false;
		boolean setTriggers = false;
		if (fetchDependencies != null && fetchDependencies.length > 0) {
			setTriggers = fetchDependencies[0];
			fetchDependency = fetchDependencies.length > 1 && fetchDependencies[1];
		}
		
		if (!fetchDependency) {
			fields.addAll(FieldFactory.getWorkOrderTemplateFields());
			FacilioModule woTemplateModule = ModuleFactory.getWorkOrderTemplateModule();
			selectBuilder.innerJoin(woTemplateModule.getTableName()).on(module.getTableName()+".TEMPLATE_ID = "+woTemplateModule.getTableName()+".ID");
		}
		
		List<Map<String, Object>> pmProps = selectBuilder.get();
		
		LOGGER.log(Level.SEVERE,"selectBuilder ---- "+selectBuilder);
		
		List<Long> resourceIds = new ArrayList<>();
		if(pmProps != null && !pmProps.isEmpty()) {
			List<PreventiveMaintenance> pms = new ArrayList<>();
			
			for(Map<String, Object> prop : pmProps) {
				PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(prop, PreventiveMaintenance.class);
				WorkorderTemplate template;
				if (fetchDependency) {
					template = (WorkorderTemplate) TemplateAPI.getTemplate(pm.getTemplateId());
				}
				else {
					template = FieldUtil.getAsBeanFromMap(prop, WorkorderTemplate.class);
					template.setId(pm.getTemplateId());
				}
				pm.setWoTemplate(template);
				pms.add(pm);
			}
			
			Map<Long, List<PMTriggerContext>> pmTriggers = null;
			if (setTriggers) {
				pmTriggers = PreventiveMaintenanceAPI.getPMTriggers(pms);
			}
			
			for(PreventiveMaintenance pm : pms) {
				resourceIds.add(pm.getWoTemplate().getResourceId());
				if (pmTriggers != null) {
					pm.setTriggers(pmTriggers.get(pm.getId()));
					if (pm.getTriggers() == null) {
						continue;
					}
					for (PMTriggerContext trigger : pm.getTriggers()) {
						if (trigger.getReadingRuleId() != -1) {
							ReadingRuleContext rule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(trigger.getReadingRuleId());
							trigger.setReadingFieldId(rule.getReadingFieldId());
							trigger.setReadingInterval(rule.getInterval());
							trigger.setStartReading(rule.getStartValue());
							trigger.setReadingRule(rule);
						}
					}
				}
			}
			Map<Long, ResourceContext> resourceMap = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds, false);
			for(PreventiveMaintenance pm : pms) {
				pm.getWoTemplate().setResource(resourceMap.get(pm.getWoTemplate().getResourceId()));
			}
			if (fetchDependency) {
				Map<Long, List<PMReminder>> reminders = PreventiveMaintenanceAPI.getPMRemindersAsMap(ids);
				if (reminders != null) {
					for (PreventiveMaintenance pm : pms) {
						long pmId = pm.getId();
						if (reminders.containsKey(pmId)) {
							pm.setReminders(reminders.get(pmId));
						}
					}
				}
			}
			return pms;
		}
		return null;
	}
	
	public static void setPMInActive(long pmId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		PreventiveMaintenance updatePm = new PreventiveMaintenance();
		updatePm.setStatus(false);
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(FieldFactory.getPreventiveMaintenanceFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(pmId, module))
														;
		
		updateBuilder.update(FieldUtil.getAsProperties(updatePm));
	}
	
	public static Map<Long, List<PMTriggerContext>> getPMTriggers(List<PreventiveMaintenance> pms) throws Exception {
		String pmIds = pms.stream()
				.map(pm -> String.valueOf(pm.getId()))
				.collect(Collectors.joining(", "));
		
		FacilioModule module = ModuleFactory.getPMTriggersModule();
		List<FacilioField> fields = FieldFactory.getPMTriggerFields();
		FacilioField pmIdField = FieldFactory.getAsMap(fields).get("pmId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS))
														;
		
		List<Map<String, Object>> triggerProps = selectBuilder.get();
		Map<Long, List<PMTriggerContext>> pmTriggers = new HashMap<>();
		for(Map<String, Object> triggerProp : triggerProps) {
			PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(triggerProp, PMTriggerContext.class);
			List<PMTriggerContext> triggerList = pmTriggers.get(trigger.getPmId());
			if(triggerList == null) {
				triggerList = new ArrayList<>();
				pmTriggers.put(trigger.getPmId(), triggerList);
			}
			triggerList.add(trigger);
		}
		
		return pmTriggers;
	}
	
	public static List<PMTriggerContext> getPMTriggers(PreventiveMaintenance pm) throws Exception {
		Map<Long, List<PMTriggerContext>> pmTriggerMap = getPMTriggers(Collections.singletonList(pm));
		return pmTriggerMap.get(pm.getId());
	}
	
	public static Map<String, List<TaskContext>> getTaskMapFromJson(JSONObject json) throws JsonParseException, JsonMappingException, IOException {
		Map<String, List> tasksMap = FieldUtil.getAsBeanFromJson(json, Map.class);
		Map<String, List<TaskContext>> tasks = new HashMap<>();
		ObjectMapper mapper = FieldUtil.getMapper(TaskContext.class);
		for (Map.Entry<String, List> entry : tasksMap.entrySet()) {
			tasks.put(entry.getKey(), mapper.readValue(JSONArray.toJSONString(entry.getValue()), mapper.getTypeFactory().constructCollectionType(List.class, TaskContext.class)));
		}
		return tasks;
	}
	
	public static void updateResourceDetails(WorkOrderContext wo, Map<String, List<TaskContext>> taskMap) throws Exception {
		List<Long> oldAssetIds = new ArrayList<>();
		checkAndUpdateSpaceResource(wo, oldAssetIds);
		if(taskMap != null && !taskMap.isEmpty()) {
			for (List<TaskContext> tasks : taskMap.values()) {
				for (TaskContext task : tasks) {
					checkAndUpdateSpaceResource(task, oldAssetIds);
				}
			}
		}
		
		if(!oldAssetIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
			
			List<FacilioField> fields = new ArrayList<>();
			fields.add(FieldFactory.getIdField(module));
			FacilioField oldId = FieldFactory.getField("oldId", "OLD_ID_TEMP", module, FieldType.NUMBER);
			fields.add(oldId);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getCondition(oldId, oldAssetIds, NumberOperators.EQUALS))
															;
			List<Map<String, Object>> props = selectBuilder.get();
			Map<Long, Long> oldIdToNewId = new HashMap<>();
			for(Map<String, Object> prop : props) {
				oldIdToNewId.put((Long)prop.get("oldId"), (Long)prop.get("id"));
			}
			
			updateAssetResource(wo, oldIdToNewId);
			if(taskMap != null && !taskMap.isEmpty()) {
				for (List<TaskContext> tasks : taskMap.values()) {
					for (TaskContext task : tasks) {
						updateAssetResource(task, oldIdToNewId);
					}
				}
			}
		}
	}
	
	private static void updateAssetResource(TicketContext ticket, Map<Long, Long> oldIdToNewId) {
		if(ticket.getAsset() != null && ticket.getAsset().getId() != -1) {
			AssetContext asset = ticket.getAsset();
			asset.setId(oldIdToNewId.get(asset.getId()));
			ticket.setResource(asset);
		}
	}
	
	private static void checkAndUpdateSpaceResource(TicketContext ticket, List<Long> oldAssetIds) {
		if(ticket.getAsset() != null && ticket.getAsset().getId() != -1) {
			oldAssetIds.add(ticket.getAsset().getId());
		}
		else if (ticket.getSpace() != null && ticket.getSpace().getId() != -1){
			ticket.setResource(ticket.getSpace());
		}
	}
	
	public static List<PMReminder> getPMReminders(Long pmId) throws Exception {
		List<Long> pmids = new ArrayList<>();
		pmids.add(pmId);
		return getPMReminders(pmids);
	}
	
	public static List<PMReminder> getPMReminders(List<Long> pmIds) throws Exception {
		List<Map<String, Object>> reminderProps = fetchPMReminders(pmIds);
		if(reminderProps != null && !reminderProps.isEmpty()) {
			List<PMReminder> reminders = new ArrayList<>();
			for(Map<String, Object> prop : reminderProps) {
				PMReminder reminder = FieldUtil.getAsBeanFromMap(prop, PMReminder.class);
				reminders.add(reminder);
			}
			return reminders;
		}
		return null;
	}
	
	public static Map<Long,List<PMReminder>> getPMRemindersAsMap(List<Long> pmIds) throws Exception {
		List<Map<String, Object>> reminderProps = fetchPMReminders(pmIds);
		if(reminderProps != null && !reminderProps.isEmpty()) {
			Map<Long,List<PMReminder>> reminderMap = new HashMap<>();
			for(Map<String, Object> prop : reminderProps) {
				PMReminder reminder = FieldUtil.getAsBeanFromMap(prop, PMReminder.class);
				Long pmId = reminder.getPmId();
				if (!reminderMap.containsKey(pmId)) {
					reminderMap.put(pmId, new ArrayList<>());
				}
				List<PMReminder> reminders = reminderMap.get(pmId);
				reminders.add(reminder);
			}
			return reminderMap;
		}
		return null;
	}
	
	private static List<Map<String, Object>> fetchPMReminders (List<Long> pmIds) throws Exception {
		FacilioModule module = ModuleFactory.getPMReminderModule();
		List<FacilioField> fields = FieldFactory.getPMReminderFields();
		Map<String, FacilioField> fieldProps = FieldFactory.getAsMap(fields);
		FacilioField pmIdField = fieldProps.get("pmId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));
		
		return selectBuilder.get();
	}
	
	public static void schedulePrePMReminder(PMReminder reminder, long nextExecutionTime, long triggerId) throws Exception {
		if(reminder.getTypeEnum() == ReminderType.BEFORE_EXECUTION && nextExecutionTime != -1 && triggerId != -1) {
			long id = deleteOrAddPreviousBeforeRemindersRel(reminder.getId(), triggerId);
			FacilioTimer.scheduleOneTimeJob(id, "PrePMReminder", nextExecutionTime-reminder.getDuration(), "facilio");
		}
		else {
			throw new IllegalArgumentException("Invalid parameters for scheduling Before PMReminder job"+reminder.getId());
		}
	}
	
	private static long deleteOrAddPreviousBeforeRemindersRel(long pmReminderId, long triggerId) throws Exception {
		List<FacilioField> fields = FieldFactory.getBeforePMRemindersTriggerRelFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioModule module = ModuleFactory.getBeforePMRemindersTriggerRelModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmReminderId"), String.valueOf(pmReminderId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmTriggerId"), String.valueOf(triggerId), NumberOperators.EQUALS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			Map<String, Object> relProp = props.get(0);
			long id = (long) relProp.get("id");
			FacilioTimer.deleteJob(id, "PrePMReminder");
			return id;
		}
		else {
			Map<String, Object> relProp = new HashMap<>();
			relProp.put("pmReminderId", pmReminderId);
			relProp.put("pmTriggerId", triggerId);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(module.getTableName())
															.fields(fields)
															.addRecord(relProp);
			
			insertBuilder.save();
			return (long) relProp.get("id");
		}
	}
	
	public static void schedulePostPMReminder(PMReminder reminder, long remindTime, long woId) throws SQLException, RuntimeException, Exception {
		if((reminder.getTypeEnum() == ReminderType.AFTER_EXECUTION || reminder.getTypeEnum() == ReminderType.BEFORE_DUE || reminder.getTypeEnum() == ReminderType.AFTER_DUE) && remindTime != -1 && woId != -1) {
			FacilioTimer.scheduleOneTimeJob(addPMReminderToWORel(reminder.getId(), woId), "PostPMReminder", remindTime, "facilio");
		}
		else {
			throw new IllegalArgumentException("Invalid parameters for scheduling After PMReminder job"+reminder.getId());
		}
	}
	
	private static long addPMReminderToWORel(long pmReminderId, long woId) throws SQLException, RuntimeException {
		Map<String, Object> props = new HashMap<>();
		props.put("pmReminderId", pmReminderId);
		props.put("woId", woId);
		
		GenericInsertRecordBuilder recordBuilder = new GenericInsertRecordBuilder()
														.fields(FieldFactory.getAfterPMReminderWORelFields())
														.table(ModuleFactory.getAfterPMRemindersWORelModule().getTableName())
														.addRecord(props);
		
		recordBuilder.save();
		return (long) props.get("id");
	}
	
	public static long getPMCount(List<FacilioField> conditionFields, List<Condition> conditions) throws Exception {
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		FacilioModule woTemplateModule = ModuleFactory.getWorkOrderTemplateModule();
		FacilioField countField = FieldFactory.getField("count", "COUNT(*)", FieldType.NUMBER);
		List<FacilioField> fields = new ArrayList<>();
		fields.add(countField);
		fields.addAll(FieldFactory.getWorkOrderTemplateFields());
		if (conditionFields != null) {
			fields.addAll(conditionFields);
		}
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
												.select(fields)
												.table(module.getTableName())
												.innerJoin(woTemplateModule.getTableName())
												.on(module.getTableName()+".TEMPLATE_ID = "+woTemplateModule.getTableName()+".ID");
		
		if (conditions != null && !conditions.isEmpty()) {
			conditions.forEach(condition -> builder.andCondition(condition));
		}
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return (Long) rs.get(0).get("count");
		}
	}
}
