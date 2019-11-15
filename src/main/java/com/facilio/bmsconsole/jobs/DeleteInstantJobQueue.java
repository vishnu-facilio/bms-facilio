package com.facilio.bmsconsole.jobs;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeleteInstantJobQueue extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(DeleteInstantJobQueue.class.getName());
    private static final String TABLE_NAME = "FacilioInstantJobQueue";
    private static final List<FacilioField> FIELDS = getFileds();
    public static final Map<String,FacilioField> FIELD_MAP = getAsMap(FIELDS);
    public static final String deleteCondition = "< NOW() - INTERVAL 1 DAY";
    @Override
    public void execute(JobContext jc) throws Exception {

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(TABLE_NAME).andCondition(CriteriaAPI.getCondition(FIELD_MAP.get("deletedTime"),deleteCondition,CommonOperators.IS_NOT_EMPTY));
        LOGGER.info("###############FacilioInstantJobQueue ");
        try{
            int count = builder.delete();
            LOGGER.info("FacilioInstantJobQueue deleted queue count is  : "+count);
        }catch(Exception e){
            LOGGER.info("Exception occurred in FacilioInstantJob deletedJob  :  ",e);
        }

    }
    private static Map<String, FacilioField> getAsMap(Collection<FacilioField> fields) {
        return fields.stream()
                .collect(Collectors.toMap(FacilioField::getName, Function.identity(), (prevValue, curValue) -> {
                    return prevValue;
                }));
    }
    private static List<FacilioField> getFileds(){
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("deletedTime", "DELETED_TIME", FieldType.DATE_TIME));
        return fields;
    }
}
