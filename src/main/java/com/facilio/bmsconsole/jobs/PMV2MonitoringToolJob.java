package com.facilio.bmsconsole.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.chargebee.org.json.JSONObject;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.plannedmaintenance.ScheduleExecutor;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class PMV2MonitoringToolJob extends FacilioJob {
	
	final class PMV2SchedulerCheckCommand extends FacilioCommand {
        @Override
        public boolean executeCommand(Context context) throws Exception {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            
            List<Long> activePMIds = getActivePMIds(modBean);
            
            List<PMPlanner> planners = getPlannerList(modBean,activePMIds);
            
            List<Long> plannerIds = planners.stream().map(PMPlanner::getId).collect(Collectors.toList());
            
            List<Long> triggerIds = planners.stream().map(PMPlanner::getTriggerId).collect(Collectors.toList());
            
            Map<Long,Long> triggerVsPlannerId = planners.stream().collect(Collectors.toMap(PMPlanner::getTriggerId, PMPlanner::getId));
            
            List<PMTriggerV2> triggers = getTriggerList(modBean,triggerIds);
            
            Map<Long,Integer> plannerVsResourceCount = getPlannerVsResourceCount(modBean,plannerIds);
            
            StringBuilder resBuilder = (StringBuilder) context.get("resultStringBuilder");
            
            for(PMTriggerV2 trigger : triggers) {
            	
            	Long startTime = DateTimeUtil.getCurrenTime();
            	
            	ScheduleInfo scheduleInfo = trigger.getScheduleInfo();
            	
            	Long endTime = new ScheduleExecutor().computeEndtimeUsingTriggerType(scheduleInfo, startTime);
            	
            	if(trigger.getEndTime() > 0 && endTime > trigger.getEndTime()) {
            		endTime = trigger.getEndTime();
            	}
            	
            	int times = scheduleInfo.nextExecutionTimes((startTime-60000)/1000,endTime/1000).size();
            	
            	int resCount = plannerVsResourceCount.getOrDefault(triggerVsPlannerId.get(trigger.getId()), 0);
            	
            	int reqCountplanner = times * resCount;
            	
            	int woCount = getWoCount(modBean,triggerVsPlannerId.get(trigger.getId()) , startTime, endTime);
            	
            	if(woCount != reqCountplanner) {
            		String res = "planner -- "+triggerVsPlannerId.get(trigger.getId())+", reqCount -- "+reqCountplanner +", actualCount -- "+woCount;
            		resBuilder.append(res);
            		resBuilder.append("\n");
            	}
            }
            	

            return false;
        }

		private int getWoCount(ModuleBean modBean,Long plannerId, Long startTime, Long endTime) throws Exception {

			SelectRecordsBuilder<WorkOrderContext> select3 = new SelectRecordsBuilder<WorkOrderContext>()
            		.aggregate(AggregateOperator.getAggregateOperator(BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue()), FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER)))
            		.module(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER))
            		.beanClass(WorkOrderContext.class)
            		.andCustomWhere("PM_PLANNER_ID = ? AND CREATED_TIME >= ? AND CREATED_TIME <= ?",plannerId,startTime,endTime)
            		.skipModuleCriteria()
            		;
            
            List<Map<String, Object>> resPlannerProps = select3.getAsProps();
            
			return ((Long) resPlannerProps.get(0).get("id")).intValue();
		}

		private Map<Long, Integer> getPlannerVsResourceCount(ModuleBean modBean,List<Long> plannerIds) throws Exception {
			
			Map<String, FacilioField> plannerResourceFieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER));
			
			List<FacilioField> plannerSelectFields = new ArrayList<>();
			
			plannerSelectFields.add(plannerResourceFieldsMap.get("planner"));
			
			Map<Long, Integer> result = new HashMap<>();
			
			SelectRecordsBuilder<PMResourcePlanner> select3 = new SelectRecordsBuilder<PMResourcePlanner>()
					.select(plannerSelectFields)
            		.aggregate(AggregateOperator.getAggregateOperator(BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue()), FieldFactory.getIdField(modBean.getModule(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER)))
            		.module(modBean.getModule(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER))
            		.beanClass(PMResourcePlanner.class)
            		.andCondition(CriteriaAPI.getCondition(plannerResourceFieldsMap.get("planner"), plannerIds, NumberOperators.EQUALS))
            		.groupBy("planner_id");
            
            List<Map<String, Object>> resPlannerProps = select3.getAsProps();
            
            for(Map<String, Object> resPlannerProp : resPlannerProps) {
            	Long plannerID = (Long) ((Map<String, Object>)resPlannerProp.get("planner")).get("id");
            	
            	int count = ((Long) resPlannerProp.get("id")).intValue();
            	
            	result.put(plannerID, count);
            	
            	
            }
			
			return result;
		}

		private List<PMTriggerV2> getTriggerList(ModuleBean modBean, List<Long> triggerIds) throws Exception {
			
			SelectRecordsBuilder<PMTriggerV2> select2 = new SelectRecordsBuilder<PMTriggerV2>()
            		.select(modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_TRIGGER))
            		.module(modBean.getModule(FacilioConstants.PM_V2.PM_V2_TRIGGER))
            		.beanClass(PMTriggerV2.class)
            		.andCondition(CriteriaAPI.getIdCondition(triggerIds, modBean.getModule(FacilioConstants.PM_V2.PM_V2_TRIGGER)))
            		.andCustomWhere("(TRIGGER_END_TIME is null OR TRIGGER_END_TIME = 0 OR TRIGGER_END_TIME > ?)",DateTimeUtil.getCurrenTime());
            
            List<PMTriggerV2> triggers = select2.get();
            
            return triggers;
			
		}

		private List<PMPlanner> getPlannerList(ModuleBean modBean, List<Long> activePMIds) throws Exception {
			
			List<FacilioField> plannerSelectFields = new ArrayList<>();
			
			Map<String, FacilioField> plannerFieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_PLANNER));
			
            plannerSelectFields.add(FieldFactory.getIdField(modBean.getModule(FacilioConstants.PM_V2.PM_V2_PLANNER)));
            plannerSelectFields.add(FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_PLANNER)).get("trigger"));
            
            SelectRecordsBuilder<PMPlanner> select1 = new SelectRecordsBuilder<PMPlanner>()
            		.select(plannerSelectFields)
            		.module(modBean.getModule(FacilioConstants.PM_V2.PM_V2_PLANNER))
            		.beanClass(PMPlanner.class)
            		.andCondition(CriteriaAPI.getCondition(plannerFieldsMap.get("pmId"), activePMIds, NumberOperators.EQUALS))
            		.andCustomWhere("trigger_id is not null");
            
            List<PMPlanner> planners = select1.get();
            
            return planners;
		}

		private List<Long> getActivePMIds(ModuleBean modBean) throws Exception {
			
			List<FacilioField> pmSelectFields = new ArrayList<>();
            pmSelectFields.add(FieldFactory.getIdField(modBean.getModule(FacilioConstants.PM_V2.PM_V2_MODULE_NAME)));
            
            SelectRecordsBuilder<PlannedMaintenance> select = new SelectRecordsBuilder<PlannedMaintenance>()
            		.select(pmSelectFields)
            		.module(modBean.getModule(FacilioConstants.PM_V2.PM_V2_MODULE_NAME))
            		.beanClass(PlannedMaintenance.class)
            		.andCustomWhere("PM_STATUS = 2");
            
            List<PlannedMaintenance> pms = select.get();
            
            List<Long> activePMIds = pms.stream().map(PlannedMaintenance::getId).collect(Collectors.toList());
			
			return activePMIds;
		}

    }
	
	@Override
	public void execute(JobContext jc) throws Exception {
		
		 List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
		 StringBuilder res = new StringBuilder();
        if (CollectionUtils.isNotEmpty(orgs)) {
            for (Organization org : orgs) {
                if (org.getOrgId() > 0 && org.getOrgId() == 28l) {

                    AccountUtil.setCurrentAccount(org.getOrgId());
                    FacilioChain c = FacilioChain.getTransactionChain();
                    c.addCommand(new PMV2SchedulerCheckCommand());
                    
                    c.getContext().put("resultStringBuilder", res);
                    c.execute();
                    
                    AccountUtil.cleanCurrentAccount();

                }
            }
        }
        
        JSONObject result = new JSONObject();
        result.put("result", res);
        
        String query = "insert into Monitoring_Tool_Meta(FEATURE,TTIME,META) values(1,?,?)";
        
        try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement stmt=conn.prepareStatement(query);) {
        	
            stmt.setLong(1,DateTimeUtil.getCurrenTime());  
            stmt.setString(2,result.toString());  
              
            int i=stmt.executeUpdate();  
            System.out.println(i+" records inserted");  
        }
        
        
	}
	
}
