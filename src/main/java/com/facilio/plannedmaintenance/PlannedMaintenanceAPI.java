package com.facilio.plannedmaintenance;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.questions.NumberQuestionContext;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facilio.plannedmaintenance.PlannedMaintenanceAPI.ScheduleOperation.EXTEND;
import static com.facilio.plannedmaintenance.PlannedMaintenanceAPI.ScheduleOperation.REINIT;

public class PlannedMaintenanceAPI {
    public enum ScheduleOperation implements FacilioStringEnum {
        EXTEND(ScheduleExecutor.class),
        REINIT (ScheduleExecutor.class),
        NIGHTLY (NightlyExecutor.class)
        ;
        
        private Class<? extends ExecutorBase> executorClass;
        
        private <E extends ExecutorBase> ScheduleOperation (Class<E> executorClass) {
            this.executorClass = executorClass;
        }
        
        public ExecutorBase getExecutorClass() throws Exception {
        	
        	return executorClass.getConstructor().newInstance();
        }
        
    }

    public static void extendPlanner(long plannerId, Duration duration) {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put("plannerId", plannerId);
        message.put("operation", EXTEND.getValue());
        message.put("duration", duration.toString());

        SessionManager.getInstance().sendMessage(new Message()
                .setTopic("pm_planner/" + plannerId + "/execute")
                .setOrgId(orgId)
                .setContent(message)
        );
    }
    
    public static void runNightlyPlanner(long plannerId) {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put("plannerId", plannerId);
        message.put("operation", ScheduleOperation.NIGHTLY.getValue());

        SessionManager.getInstance().sendMessage(new Message()
                .setTopic("pm_planner/" + plannerId + "/execute")
                .setOrgId(orgId)
                .setContent(message)
        );
    }

    public static void schedulePlanner(long plannerId) throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put("plannerId", plannerId);
        message.put("operation", REINIT.getValue());

        SessionManager.getInstance().sendMessage(new Message()
                .setTopic("pm_planner/" + plannerId + "/execute")
                .setOrgId(orgId)
                .setContent(message)
        );
    }

    public static List<Long> getPlannerIds(List<Long> pmIds) throws Exception {
        List<PMPlanner> pmPlanners = getPmPlanners(pmIds);
        return pmPlanners.stream().map(PMPlanner::getId).collect(Collectors.toList());
    }
    
    public static List<PMResourcePlanner> getResourcePlanners(Long plannerId) throws Exception {
        
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
        FacilioModule pmPlannerModule = modBean.getModule("pmResourcePlanner");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("pmResourcePlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);
        
        FacilioField plannerField = fieldMap.get("planner");

        SelectRecordsBuilder<PMResourcePlanner> records = new SelectRecordsBuilder<PMResourcePlanner>();
        records.select(pmPlannerFields)
                .module(pmPlannerModule)
                .beanClass(PMResourcePlanner.class)
                .andCondition(CriteriaAPI.getCondition(plannerField, plannerId+"", NumberOperators.EQUALS));
        return records.get();
    	
    }

    public static List<PMPlanner> getPmPlanners(List<Long> pmIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule("pmPlanner");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("pmPlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);
        FacilioField pmIdField = fieldMap.get("pmId");

        SelectRecordsBuilder<PMPlanner> records = new SelectRecordsBuilder<>();
        records.select(pmPlannerFields)
                .module(pmPlannerModule)
                .beanClass(PMPlanner.class)
                .andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));
        return records.get();
    }
}
