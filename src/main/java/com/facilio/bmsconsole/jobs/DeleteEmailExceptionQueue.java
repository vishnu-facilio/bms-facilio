package com.facilio.bmsconsole.jobs;

import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class DeleteEmailExceptionQueue extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(DeleteEmailExceptionQueue.class.getName());
    private static final String TABLE_NAME = "FacilioExceptionQueue";
    private static final Map<String, FacilioField> FIELD_MAP = DeleteInstantJobQueue.FIELD_MAP;
    private static final String deleteCondition = DeleteInstantJobQueue.deleteCondition;
    @Override
    public void execute(JobContext jc) throws Exception {

        try{
            int count = FacilioService.runAsServiceWihReturn(() ->deleteQueue());
            LOGGER.info("FacilioExceptionQueue deleted queue count is  : "+count);
        }catch(Exception e){
            LOGGER.info("Exception occurred in FacilioExceptionQueue deletedJob  :  ",e);
        }

    }
    private static  int deleteQueue() throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(TABLE_NAME).andCondition(CriteriaAPI.getCondition(FIELD_MAP.get("deletedTime"),deleteCondition, CommonOperators.IS_NOT_EMPTY));
        return builder.delete();
    }
}
