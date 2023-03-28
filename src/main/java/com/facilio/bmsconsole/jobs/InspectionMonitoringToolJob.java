package com.facilio.bmsconsole.jobs;

import com.chargebee.org.json.JSONObject;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.jobs.monitoring.utils.MonitoringFeature;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionScheduler;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.plannedmaintenance.ScheduleExecutor;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InspectionMonitoringToolJob extends FacilioJob {
    final class InspectionSchedulerCheckCommand extends FacilioCommand {

        @Override
        public boolean executeCommand(Context context) throws Exception {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            StringBuilder resBuilder = (StringBuilder) context.get("resultStringBuilder");
            long orgId = AccountUtil.getCurrentOrg().getId();
            List<InspectionTemplateContext> inspectionTemplates = getActiveInspectionTemplateIds(modBean);
            if(inspectionTemplates.size()<1){
                return false;
            }
            Map<Long,InspectionTemplateContext> templateIdvsTemplateContext = inspectionTemplates.stream().collect(Collectors.toMap(InspectionTemplateContext::getId, Function.identity()));
            Map<Long,Integer> activeTemplateVSCreationType = inspectionTemplates.stream().collect(Collectors.toMap(InspectionTemplateContext::getId,InspectionTemplateContext::getCreationType));
            List<Long> templateIds = new ArrayList<Long>(activeTemplateVSCreationType.keySet());
            List<InspectionTriggerContext> triggerData = getActiveTriggers(modBean,templateIds);
            if(triggerData.size()<1){
                return false;
            }
            Map<Long, Integer> templateVsResponseCount =  getInspectionResponseCountForTemplates(modBean,templateIds);
            if(triggerData.size()>0 && templateVsResponseCount.isEmpty()){
                String res = "Org ID -- "+orgId+" Error - Zero Inspection Responses for Active Inspection Templates which also has triggers.";
                resBuilder.append(res);
                resBuilder.append("\n");
                return false;
            }
            List<Long> scheduleIds = triggerData.stream().map(InspectionTriggerContext::getScheduleId).collect(Collectors.toList());
            List<BaseScheduleContext> baseScheduleData = getAllBaseScheduler(scheduleIds,modBean);
            for(BaseScheduleContext baseSchedule : baseScheduleData){
                Integer creationType = activeTemplateVSCreationType.get(baseSchedule.getRecordId());
                Long startTime = DateTimeUtil.getCurrenTime();
                Long endDate = DateTimeUtil.getDayEndTimeOf(DateTimeUtil.addDays(DateTimeUtil.getCurrenTime(), InspectionScheduler.INSPECTION_PRE_GENERATE_INTERVAL_IN_DAYS));
                List<DateRange> times = baseSchedule.getScheduleInfo().getTimeIntervals(startTime, endDate);
                int timesCount = times.size();
                Integer generatedResponseCount = (Integer) templateVsResponseCount.get(baseSchedule.getRecordId());
                if(creationType!=null && creationType==1) {
                    if(timesCount!=generatedResponseCount){
                        String res = "Org ID -- "+orgId+ ", Schedule ID -- "+baseSchedule.getId()+", Template ID -- "+baseSchedule.getRecordId() +", actualCount -- "+timesCount+", generated records count -- "+generatedResponseCount;
                        resBuilder.append(res);
                        resBuilder.append("\n");
                    }
                } else{
                    List<ResourceContext> resources = new ArrayList<ResourceContext>();
                    InspectionTemplateContext inspectionTemplateRecord = templateIdvsTemplateContext.get(baseSchedule.getRecordId());
                    InspectionScheduler scheduler = new InspectionScheduler();
                    resources = scheduler.getMultipleResource(inspectionTemplateRecord,baseSchedule);
                    int resourcesCount = resources.size();
                    int totalCount = timesCount * resourcesCount;
                    if(totalCount!=generatedResponseCount){
                        String res = "Org ID -- "+orgId+"Schedule ID -- "+baseSchedule.getId()+", Template ID -- "+baseSchedule.getRecordId() +", actualCount -- "+resourcesCount+", generated records count -- "+generatedResponseCount;
                        resBuilder.append(res);
                        resBuilder.append("\n");
                    }
                }
            }
            return false;
        }

        private List<BaseScheduleContext> getAllBaseScheduler(List<Long> scheduleIds,ModuleBean modBean) throws Exception{
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getBaseSchedulerFields())
                    .table(ModuleFactory.getBaseSchedulerModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition (scheduleIds,ModuleFactory.getBaseSchedulerModule()));
            List<Map<String, Object>> props = selectBuilder.get();
            List<BaseScheduleContext> baseScheduleData=FieldUtil.getAsBeanListFromMapList(props, BaseScheduleContext.class);
            return baseScheduleData;
        }

        private List<InspectionTriggerContext> getActiveTriggers(ModuleBean modBean,List<Long> activeInspectionTemplateIds) throws Exception{
            FacilioModule inspectionTriggerModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_TRIGGER);
            List<FacilioField> inspectionTriggerSelectFields = new ArrayList<>();
            List<FacilioField> inspectionTriggerAllFields = modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER);
            Map<String, FacilioField> inspectionTriggerAllFieldsMap = FieldFactory.getAsMap(inspectionTriggerAllFields);
            inspectionTriggerSelectFields.add(FieldFactory.getIdField(inspectionTriggerModule));
            inspectionTriggerSelectFields.add(inspectionTriggerAllFieldsMap.get("scheduleId"));
            inspectionTriggerSelectFields.add(inspectionTriggerAllFieldsMap.get("parent"));

            SelectRecordsBuilder<InspectionTriggerContext> select  = new SelectRecordsBuilder<InspectionTriggerContext>()
                    .select(inspectionTriggerSelectFields)
                    .module(inspectionTriggerModule)
                    .beanClass(InspectionTriggerContext.class)
                    .andCondition(CriteriaAPI.getCondition(inspectionTriggerAllFieldsMap.get("parent"), activeInspectionTemplateIds , NumberOperators.EQUALS));
            List<InspectionTriggerContext> inspectionTriggers = select.get();
            return inspectionTriggers;
        }

        private List<InspectionTemplateContext> getActiveInspectionTemplateIds(ModuleBean modBean) throws Exception{
            FacilioModule inspectionTemplateModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
            Map<String, FacilioField> inspectionTemplateAllFields = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TEMPLATE));
            SelectRecordsBuilder<InspectionTemplateContext> select  = new SelectRecordsBuilder<InspectionTemplateContext>()
                        .select(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TEMPLATE))
                        .module(inspectionTemplateModule)
                        .beanClass(InspectionTemplateContext.class).fetchSupplement((SupplementRecord)inspectionTemplateAllFields.get("sites"));
            List<InspectionTemplateContext> inspectionTemplates = select.get();
            return inspectionTemplates;
        }

        private Map<Long, Integer> getInspectionResponseCountForTemplates(ModuleBean modBean, List<Long> activeInspectionTemplateIds) throws Exception {
            FacilioModule inspectionResponseModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);
            List<FacilioField> inspectionResponseSelectFields = new ArrayList<>();
            List<FacilioField> inspectionResponseAllFields = modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE);
            Map<String, FacilioField> inspectionResponseAllFieldsMap = FieldFactory.getAsMap(inspectionResponseAllFields);
            inspectionResponseSelectFields.add(inspectionResponseAllFieldsMap.get("parent"));
            SelectRecordsBuilder<InspectionResponseContext> select1 = new SelectRecordsBuilder<InspectionResponseContext>()
                    .aggregate(AggregateOperator.getAggregateOperator(BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue()), FieldFactory.getIdField(inspectionResponseModule))
                    .select(inspectionResponseSelectFields)
                    .module(inspectionResponseModule)
                    .beanClass(InspectionResponseContext.class)
                    .andCondition(CriteriaAPI.getCondition(inspectionResponseAllFieldsMap.get("status"),InspectionResponseContext.Status.PRE_OPEN.getIndex()+"" , NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(inspectionResponseAllFieldsMap.get("sourceType"), InspectionResponseContext.SourceType.PLANNED.getIndex()+"", NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(inspectionResponseAllFieldsMap.get("parent"),activeInspectionTemplateIds,NumberOperators.EQUALS))
                    .groupBy("parent")
                    .skipModuleCriteria();
            List<Map<String, Object>> inspectionResponseProps = select1.getAsProps();
            Map<Long, Integer> result = new HashMap<>();
            for(Map<String, Object> inspectionResponse : inspectionResponseProps) {
                Long templateID = (Long) ((Map<String, Object>)inspectionResponse.get("parent")).get("id");
                int count = ((Long) inspectionResponse.get("id")).intValue();
                result.put(templateID, count);
            }
            return result;
        }
    }

    @Override
    public void execute(JobContext jc) throws Exception {
        List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
        StringBuilder res = new StringBuilder();
        if (CollectionUtils.isNotEmpty(orgs)) {
            for (Organization org : orgs) {
                if (org.getOrgId() > 0) {
                    AccountUtil.setCurrentAccount(org.getOrgId());
                    FacilioChain c = FacilioChain.getTransactionChain();
                    c.addCommand(new InspectionMonitoringToolJob.InspectionSchedulerCheckCommand());
                    c.getContext().put("resultStringBuilder", res);
                    c.execute();
                    AccountUtil.cleanCurrentAccount();
                }
            }
        }
        JSONObject result = new JSONObject();
        result.put("result", res);
        String query = "insert into Monitoring_Tool_Meta(FEATURE,TTIME,META) values(?,?,?)";
        try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement stmt=conn.prepareStatement(query);) {
            stmt.setInt(1, MonitoringFeature.INSPECTION.getVal());
            stmt.setLong(1, DateTimeUtil.getCurrenTime());
            stmt.setString(2,result.toString());
            int i=stmt.executeUpdate();
            System.out.println(i+" records inserted");
        }
    }
}
