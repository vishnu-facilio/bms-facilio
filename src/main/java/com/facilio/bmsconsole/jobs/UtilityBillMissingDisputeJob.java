package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.utility.UtilityDisputeType;
import com.facilio.utility.UtilitySDK;
import com.facilio.utility.context.UtilityDisputeContext;
import com.facilio.utility.context.UtilityIntegrationBillContext;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UtilityBillMissingDisputeJob extends FacilioJob {

    private static final Logger LOGGER = Logger.getLogger(UtilityBillMissingDisputeJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        LOGGER.log(Level.ERROR, "UtilityBillMissingDisputeJob -> execute(JobContext): ");
        long orgId = jc.getJobId();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule customerModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
        String moduleName = FacilioConstants.UTILITY_INTEGRATION_CUSTOMER;
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        if (customerModule != null) {

            SelectRecordsBuilder<UtilityIntegrationCustomerContext> builder = new SelectRecordsBuilder<UtilityIntegrationCustomerContext>()
                    .moduleName(moduleName)
                    .select(fields)
                    .beanClass(UtilityIntegrationCustomerContext.class);
            List<UtilityIntegrationCustomerContext> utilityIntegrationCustomerContextList = builder.get();

            if (CollectionUtils.isNotEmpty(utilityIntegrationCustomerContextList)) {
                for (UtilityIntegrationCustomerContext utilityIntegrationCustomerContext : utilityIntegrationCustomerContextList) {

                    FacilioModule meterModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
                    String meterModuleName = FacilioConstants.UTILITY_INTEGRATION_METER;
                    List<FacilioField> meterFields = modBean.getAllFields(meterModuleName);

                    if (meterModule != null) {

                        UtilityIntegrationMeterContext.MeterState state = UtilityIntegrationMeterContext.MeterState.ACTIVATED;
                        SelectRecordsBuilder<UtilityIntegrationMeterContext> builder1 = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                                .moduleName(meterModuleName)
                                .select(meterFields)
                                .beanClass(UtilityIntegrationMeterContext.class)
                                .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_CUSTOMER_ID", "utilityIntegrationCustomer", String.valueOf(utilityIntegrationCustomerContext.getId()), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition("METER_STATE", "meterState", String.valueOf(state.getIntVal()), NumberOperators.EQUALS));
                        List<UtilityIntegrationMeterContext> lists = builder1.get();

                        if (CollectionUtils.isNotEmpty(lists)) {
                            for (UtilityIntegrationMeterContext list : lists) {
                                FacilioModule billModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS);
                                String billModulename = FacilioConstants.UTILITY_INTEGRATION_BILLS;
                                List<FacilioField> fields1 = modBean.getAllFields(billModulename);

                                if (billModule != null) {

                                    //start and date date
                                    long billDate = DateTimeUtil.addMonths(DateTimeUtil.getMonthStartTime(), -1);
                                    Calendar cal = Calendar.getInstance();
                                    cal.add(Calendar.MONTH, -1);
                                    cal.set(Calendar.DATE, 1);
                                    Date firstDateOfPreviousMonth = cal.getTime();
                                    long startTime = firstDateOfPreviousMonth.getTime();
                                    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

                                    Date lastDateOfPreviousMonth = cal.getTime();
                                    long endTime = lastDateOfPreviousMonth.getTime();

                                    long startMillis = UtilitySDK.convertStartDateToMilliseconds(firstDateOfPreviousMonth);
                                    long endMillis = UtilitySDK.convertEndDateToMilliseconds(lastDateOfPreviousMonth);

                                    SelectRecordsBuilder<UtilityIntegrationBillContext> billContext = new SelectRecordsBuilder<UtilityIntegrationBillContext>()
                                            .moduleName(billModulename)
                                            .select(fields1)
                                            .beanClass(UtilityIntegrationBillContext.class)
                                            .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_METER_ID", "utilityIntegrationMeter", String.valueOf(list.getId()), NumberOperators.EQUALS))
                                            .andCondition(CriteriaAPI.getCondition("BILL_START_DATE", "billStartDate", String.valueOf(startMillis), NumberOperators.GREATER_THAN_EQUAL))
                                            .orCondition(CriteriaAPI.getCondition("BILL_END_DATE", "billEndDate", String.valueOf(endMillis), NumberOperators.LESS_THAN_EQUAL));

                                    List<UtilityIntegrationBillContext> billContextList = billContext.get();

                                    if (CollectionUtils.isEmpty(billContextList)) {
                                        FacilioContext context = new FacilioContext();
                                        context.put("billDate",billDate);
                                        context.put("list",list);

                                        UtilityDisputeContext dispute = UtilityDisputeType.BILL_MISSING.execute(context);
                                                //billDate, list);
                                        if (dispute != null) {
                                            FacilioModule disputeModule = modBean.getModule(FacilioConstants.UTILITY_DISPUTE);
                                            V3Util.createRecord(disputeModule, FieldUtil.getAsJSON(dispute));
                                        }
                                    }
                                    UtilitySDK.checkAndRaiseDisputes(list.getId(),startTime,endTime);

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

