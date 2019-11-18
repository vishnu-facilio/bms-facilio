package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.jobs.DeleteInstantJobQueue;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeleteMessageQueueJobsCommand extends FacilioCommand{

    private static final Logger LOGGER = LogManager.getLogger(DeleteMessageQueueJobsCommand.class.getName());
    private static final List<FacilioField> FIELDS = getFileds();
    private static final Map<String,FacilioField> FIELD_MAP = getAsMap(FIELDS);
    @Override
    public boolean executeCommand(Context context) throws Exception {

        int customDaysToDelete = (int) context.get("NO_OF_DAYS");
        String tableName = (String) context.get("TABLE_NAME");
        String deleteCondition = "< NOW() - INTERVAL "+ customDaysToDelete+" DAY";
        try{
             deleteQueue(tableName,deleteCondition);
        }catch(Exception e){
            LOGGER.info("Exception occurred in DeleteMessage Through Admin Console : ",e);
        }

        return false;
    }

    //Delete Message Queue for Both ExceptionQueue  and InstantJobQueue if do any modification please careful...
    public static int deleteQueue(String tableName, String deleteCondition) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(tableName).andCondition(CriteriaAPI.getCondition(FIELD_MAP.get("deletedTime"),deleteCondition, CommonOperators.IS_NOT_EMPTY));
        return builder.delete();
    }
    private static List<FacilioField> getFileds(){
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("deletedTime", "DELETED_TIME", FieldType.DATE_TIME));
        return fields;
    }

    private static Map<String, FacilioField> getAsMap(Collection<FacilioField> fields) {
        return fields.stream()
                .collect(Collectors.toMap(FacilioField::getName, Function.identity(), (prevValue, curValue) -> {
                    return prevValue;
                }));
    }
}
