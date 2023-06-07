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
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import com.facilio.utility.UtilitySDK;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;


public class UtilityAPIPollMetersJob extends FacilioJob {

    private static final Logger LOGGER = Logger.getLogger(UtilityAPIPollMetersJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        LOGGER.log(Level.ERROR, "UtilityAPIPollMetersJob -> execute(JobContext): ");
        long orgId = jc.getJobId();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule meterModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_METER;
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        UtilityIntegrationMeterContext.MeterState state = UtilityIntegrationMeterContext.MeterState.PENDING;
        if(meterModule != null){

            SelectRecordsBuilder<UtilityIntegrationMeterContext> builder = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                    .moduleName(moduleName)
                    .select(fields)
                    .beanClass(UtilityIntegrationMeterContext.class)
                    .andCondition(CriteriaAPI.getCondition("METER_STATE", "meterState", String.valueOf(state.getIntVal()), NumberOperators.EQUALS))
                    ;
            List<UtilityIntegrationMeterContext> utilityIntegrationMeterContextList = builder.get();

            if(CollectionUtils.isNotEmpty(utilityIntegrationMeterContextList)) {
                for(UtilityIntegrationMeterContext utilityIntegrationMeterContext : utilityIntegrationMeterContextList) {

                    JSONObject jsonObject = UtilitySDK.getMetersAfterActivation(utilityIntegrationMeterContext.getMeterUid());

                    JSONArray metersArray = (JSONArray) jsonObject.get("meters");

                    for (Object meter : metersArray) {
                        JSONObject meters = (JSONObject) meter;

                        long bill_count = (long) meters.get("bill_count");
                        String status = (String) meters.get("status");

                        if (bill_count > 0 && status.equals("updated") ) {

                            Boolean is_activated = (Boolean) meters.get("is_activated");
                            long interval_count = (long) meters.get("interval_count");
                            JSONObject ongoingMonitoring = (JSONObject) meters.get("ongoing_monitoring");
                            String frequency = (String) ongoingMonitoring.get("frequency");
                            String nextPrepay = (String) ongoingMonitoring.get("next_prepay");
                            String nextRefresh = (String) ongoingMonitoring.get("next_refresh");
                            String prepay = (String) ongoingMonitoring.get("prepay");




                            JSONObject json = new JSONObject();
                            json.put("blocks", meters.get("blocks"));
                            json.put("base", meters.get("base"));
                            json.put("billCoverage", meters.get("bill_coverage"));
                            json.put("intervalCoverage", meters.get("interval_coverage"));
                            json.put("statusMessage", meters.get("status_message"));

                            UtilityIntegrationMeterContext.MeterState meterstate = UtilityIntegrationMeterContext.MeterState.ACTIVATED;
                            utilityIntegrationMeterContext.setIsActivated(is_activated);
                            utilityIntegrationMeterContext.setIntervalCount(interval_count);
                            utilityIntegrationMeterContext.setBillCount(bill_count);
                            utilityIntegrationMeterContext.setStatus(status);
                            utilityIntegrationMeterContext.setMeterState(meterstate.getIntVal());
                            utilityIntegrationMeterContext.setMeta(json.toJSONString());
                            utilityIntegrationMeterContext.setFrequency(frequency);

                            if(nextPrepay != null && nextRefresh != null && prepay != null) {
                                long nxtPrepay = DateTimeUtil.getTime(nextPrepay, "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
                                long nxtRefresh = DateTimeUtil.getTime(nextRefresh, "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");

                                utilityIntegrationMeterContext.setNextPrepay(nxtPrepay);
                                utilityIntegrationMeterContext.setNextRefresh(nxtRefresh);
                                utilityIntegrationMeterContext.setPrepay(prepay);
                            }


                            V3RecordAPI.updateRecord(utilityIntegrationMeterContext,meterModule,fields);

                            //fetch bills for a meter
                            Long startTime = DateTimeUtil.addYears(System.currentTimeMillis(),-1);
                            Long endTime = System.currentTimeMillis();

                            UtilitySDK.getBillsForMeter(utilityIntegrationMeterContext.getMeterUid(),startTime,endTime);

                            //default ongoing monitoring after historical collection
                            UtilitySDK.enableOnGoingMonitoring(utilityIntegrationMeterContext.getMeterUid(),"monthly","month_to_month");

                            //dispute job
                            //UtilitySDK.checkAndRaiseDisputes(utilityIntegrationMeterContext.getId(),startTime,endTime);
                        }
                    }
                }
            }
        }
    }
}
