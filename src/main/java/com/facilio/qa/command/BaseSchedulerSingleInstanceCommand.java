package com.facilio.qa.command;

import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseSchedulerSingleInstanceCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(BaseSchedulerSingleInstanceCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        long jobId= (long) context.get(FacilioConstants.ContextNames.JOB_ID);
        String jobName= (String) context.get(FacilioConstants.ContextNames.JOB_NAME);

        BaseScheduleContext baseScheduleContext = getBaseSchedules(jobId);

        JSONObject jobProps = BmsJobUtil.getJobProps(jobId,jobName);
        Boolean isUpdate = (jobProps != null) ? (Boolean) jobProps.getOrDefault("isUpdate", false) : false;
        Boolean saveAsV3 = (jobProps != null) ? (Boolean) jobProps.getOrDefault("saveAsV3", false) : false;
        Boolean saveAsV3PreCreate = (jobProps != null) ? (Boolean) jobProps.getOrDefault("saveAsV3PreCreate", false) : false;

        try {
            List<Map<String, Object>> parentRecordProps = baseScheduleContext.fetchParent();
            List<? extends ModuleBaseWithCustomFields> childRecords = baseScheduleContext.getScheduleTypeEnum().getSchedulerTypeHandler().createRecords(baseScheduleContext, isUpdate, parentRecordProps, false);
            if(childRecords != null && !childRecords.isEmpty()) {
                if(saveAsV3) {
                    baseScheduleContext.saveAsV3Records(childRecords);
                }else if(saveAsV3PreCreate) {
                    baseScheduleContext.saveAsV3PreCreate(childRecords);
                }
                else {
                    baseScheduleContext.saveRecords(childRecords);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
        return false;
    }

    private BaseScheduleContext getBaseSchedules(long baseSchedulerId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getBaseSchedulerFields())
                .table(ModuleFactory.getBaseSchedulerModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(baseSchedulerId, ModuleFactory.getBaseSchedulerModule()));

        Criteria subCriteria = new Criteria();
        subCriteria.addOrCondition(CriteriaAPI.getCondition("END_TIME", "endTime", "" + System.currentTimeMillis(), NumberOperators.GREATER_THAN));
        subCriteria.addOrCondition(CriteriaAPI.getCondition("END_TIME", "endTime", "-1", CommonOperators.IS_EMPTY));
        selectBuilder.andCriteria(subCriteria);

        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            BaseScheduleContext baseSchedules = FieldUtil.getAsBeanFromMap(props.get(0), BaseScheduleContext.class);
            return baseSchedules;
        }
        return null;
    }
}
