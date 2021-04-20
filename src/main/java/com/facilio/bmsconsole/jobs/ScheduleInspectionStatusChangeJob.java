package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ScheduleInspectionStatusChangeJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(ScheduleInspectionStatusChangeJob.class.getName());
	
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            long maxTime = jc.getNextExecutionTime()*1000;

            SelectRecordsBuilder<InspectionResponseContext> selectRecordsBuilder = new SelectRecordsBuilder<InspectionResponseContext>();
            selectRecordsBuilder.select(fields)
                    .module(module)
                    .beanClass(InspectionResponseContext.class)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(InspectionResponseContext.Status.PRE_OPEN.getVal()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(maxTime), NumberOperators.LESS_THAN))
                    .skipModuleCriteria();
            
            List<InspectionResponseContext> inspectionResponses = selectRecordsBuilder.get();

            if (inspectionResponses == null || inspectionResponses.isEmpty()) {
                return;
            }
            

            for (InspectionResponseContext inspectionResponse : inspectionResponses) {
				try {
					FacilioTimer.scheduleOneTimeJobWithTimestampInSec(inspectionResponse.getId(), "OpenScheduledInspection", inspectionResponse.getCreatedTime() / 1000, "priority");
				}
				catch (Exception e) { 
					FacilioTimer.deleteJob(inspectionResponse.getId(), "OpenScheduledInspection");
					FacilioTimer.scheduleOneTimeJobWithTimestampInSec(inspectionResponse.getId(), "OpenScheduledInspection", inspectionResponse.getCreatedTime() / 1000, "priority");
				}
            }

        } catch (Exception e) {
            CommonCommandUtil.emailException("ScheduleInspectionStatusChangeJob", ""+jc.getJobId(), e);
            LOGGER.error("ScheduleInspectionStatusChangeJob failed: ", e);
            throw e;
        }
	}

}
