package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetMonthlyAmountContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JobPlanAPI {

    public static void deleteJobPlanTasks(Long sectionId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK);
        DeleteRecordBuilder<JobPlanTasksContext> deleteBuilder = new DeleteRecordBuilder<JobPlanTasksContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition("TASK_SECTION_ID", "taskSection", String.valueOf(sectionId), NumberOperators.EQUALS));

        deleteBuilder.delete();

    }

    public static List<JobPlanTaskSectionContext> setJobPlanDetails(long jobPlanId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_SECTION);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<JobPlanTaskSectionContext> builder = new SelectRecordsBuilder<JobPlanTaskSectionContext>()
                .module(module)
                .beanClass(JobPlanTaskSectionContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("jobPlan"), String.valueOf(jobPlanId), NumberOperators.EQUALS))
                .orderBy("ID asc")
                ;
        List<JobPlanTaskSectionContext> sections = builder.get();
        if(CollectionUtils.isNotEmpty(sections)){
            for (JobPlanTaskSectionContext section : sections) {
                List<JobPlanTasksContext> splitList =  getTasks(section.getId());
                if(CollectionUtils.isNotEmpty(splitList)) {
                    List<Map<String, Object>> mapList = FieldUtil.getAsMapList(splitList, BudgetMonthlyAmountContext.class);
                    for(Map<String, Object> map : mapList){
                        map.values().removeAll(Collections.singleton(null));
                    }
                    section.setTasks(mapList);
                }
            }
            return sections;
        }
         return null;
    }

    private static List<JobPlanTasksContext> getTasks(Long parentId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<JobPlanTasksContext> builder = new SelectRecordsBuilder<JobPlanTasksContext>()
                .module(module)
                .beanClass(JobPlanTasksContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("taskSection"), String.valueOf(parentId), NumberOperators.EQUALS))
                ;

        List<JobPlanTasksContext> list = builder.get();
        if(CollectionUtils.isNotEmpty(list))
        {
            return list;
        }
        return null;
    }
}
