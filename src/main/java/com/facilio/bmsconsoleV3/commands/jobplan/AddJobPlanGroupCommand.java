package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

import static com.facilio.bmsconsoleV3.util.V3RecordAPI.updateRecord;

@Log4j
public class AddJobPlanGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<JobPlanContext> jobPlanContextList = new ArrayList<>();
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN)) {
            jobPlanContextList.addAll((List<JobPlanContext>) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN));
        }
        else if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN_LIST)) {
            jobPlanContextList.addAll( (List<JobPlanContext>)recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_LIST));
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);

        if(jobPlanContextList == null || jobPlanContextList.isEmpty()){
            throw new IllegalArgumentException("No Job plans Available");
        }
        for (JobPlanContext jobPlan : jobPlanContextList) {
            if (jobPlan != null && jobPlan.getGroup() == null) {
                    JobPlanContext jp = new JobPlanContext();
                    jp.setId(jobPlan.getId());
                    jobPlan.setGroup(jp);
                    jobPlan.setJobPlanVersion(1.0);
                    UpdateRecordBuilder<JobPlanContext> builder = new UpdateRecordBuilder<JobPlanContext>()
                            .module(jobPlanModule)
                            .fields(modBean.getAllFields(FacilioConstants.ContextNames.JOB_PLAN))
                            .andCondition(CriteriaAPI.getIdCondition(jobPlan.getId(),jobPlanModule));
                    builder.update(jobPlan);
            }

        }
        if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN)){
            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN,jobPlanContextList);
        }
        else if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN_LIST)) {
            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN_LIST,jobPlanContextList);
        }
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);


        return false;
    }
}
