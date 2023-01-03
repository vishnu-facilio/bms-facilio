package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteJobPlanSubModuleRecord extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<JobPlanContext> jobPlanContextList = (List<JobPlanContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.ContextNames.JOB_PLAN));
        for (JobPlanContext jobPlanContext : jobPlanContextList) {
            List<JobPlanTaskSectionContext> jobPlanSection = jobPlanContext.getJobplansection();
            List<Long> jobPlanSectionIds = new ArrayList<>();
            for (JobPlanTaskSectionContext jobPlanTaskSectionContext : jobPlanSection) {
               jobPlanSectionIds.add(jobPlanTaskSectionContext.getId());
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_SECTION);
            String moduleName = module.getName();
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
            SelectRecordsBuilder<JobPlanTaskSectionContext> builder = new SelectRecordsBuilder<JobPlanTaskSectionContext>()
                    .module(module)
                    .beanClass(JobPlanTaskSectionContext.class)
                    .select(modBean.getAllFields(module.getName()))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("jobPlan"), String.valueOf(jobPlanContext.getId()), NumberOperators.EQUALS));
            List<JobPlanTaskSectionContext> props = builder.get();
            if (CollectionUtils.isNotEmpty(props)) {
                List<Long> allJobPlanTaskSectionIdList = new ArrayList<>();
                for (JobPlanTaskSectionContext jobPlanTaskSectionContext : props) {
                    allJobPlanTaskSectionIdList.add(jobPlanTaskSectionContext.getId());
                }
                if (allJobPlanTaskSectionIdList.size() > jobPlanSectionIds.size()) {
                    allJobPlanTaskSectionIdList.removeAll(jobPlanSectionIds);
                    for (Long id : allJobPlanTaskSectionIdList) {
                        JobPlanAPI.deleteJobPlanTasks(id);
                    }
                    int deletedRecord = V3RecordAPI.deleteRecordsById(moduleName, allJobPlanTaskSectionIdList);
                }

            }
        }
        return false;
    }
}

