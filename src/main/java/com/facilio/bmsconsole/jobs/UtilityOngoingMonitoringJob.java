package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.utility.UtilitySDK;
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;

public class UtilityOngoingMonitoringJob extends FacilioJob {

    private static final Logger LOGGER = Logger.getLogger(UtilityOngoingMonitoringJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        LOGGER.log(Level.ERROR, "UtilityOngoingMonitoringJob -> execute(JobContext): ");
        long meterId = jc.getJobId();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule meterModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_METER;
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<UtilityIntegrationMeterContext> builder = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(UtilityIntegrationMeterContext.class)
                .andCondition(CriteriaAPI.getIdCondition(meterId,meterModule));

        UtilityIntegrationMeterContext list = builder.fetchFirst();

        //long endTime = DateTimeUtil.getTime(list.getNextRefresh(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        long endTime = list.getNextRefresh();
        long startTime = DateTimeUtil.addMonths(endTime,-1);

        if(! list.getFrequency().isEmpty() && !list.getFrequency().equals("off")){

            UtilitySDK.getBillsForMeter(list.getMeterUid(),startTime,endTime);
        }

        //getting meter updated ongoing monitoring data from utility api
        JSONObject jsonObject = UtilitySDK.getMeters(list.getMeterUid());

        //updating UTILITY_INTEGRATION_METER with new ongoing monitoring, scheduling ongoing for new data
        UtilitySDK.updateMeterAfterOngoingMonitoring(jsonObject,list.getMeterUid());


    }
}
