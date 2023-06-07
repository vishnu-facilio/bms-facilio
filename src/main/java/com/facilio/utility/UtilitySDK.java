package com.facilio.utility;

import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.utility.context.*;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.text.DateFormat;
import java.time.ZonedDateTime;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class UtilitySDK {

    private static final Logger LOGGER = Logger.getLogger(UtilitySDK.class.getName());

    public static String UTILITY_ENDPOINT = "https://utilityapi.com/api/v2/";
    public static String UTILITY_REFERRALS = "374013";
    public static String UTILITY_AUTHORIZATION ="Bearer 03093b53146340c9865fbec09f052e5e";

    public static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";

    public static String BASE_URL = "https://utilityapi.com/authorize/keerthana_facilio";

    //"yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";



    public static void getUtilityAccountsAndMeters(String referrals,Long id) throws Exception {


        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, UTILITY_AUTHORIZATION);

        Map<String, String> params = new HashMap<>();
        params.put("referrals", referrals);
        params.put("include", "meters");

        String response = FacilioHttpUtils.doHttpGet(UTILITY_ENDPOINT + "authorizations", headers, params);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<UtilityIntegrationCustomerContext> authLists = new ArrayList<>();
        List<UtilityIntegrationMeterContext> metersList = new ArrayList<>();

        // get authorization details for the given referral
        JSONObject jsonObject = FacilioUtil.parseJson(response);
        JSONArray authObject = (JSONArray) jsonObject.get("authorizations");

        for (Object obj : authObject) {
            JSONObject authObj1 = (JSONObject) obj;
            String created_Time = (String) authObj1.get("created");
            String customerEmail = (String) authObj1.get("customer_email");
            String customerId = (String) authObj1.get("uid");
            String formUid = (String) authObj1.get("form_uid");
            String templateUid = (String) authObj1.get("template_uid");
            boolean isArchived = (boolean) authObj1.get("is_archived");
            String revoked = (String) authObj1.get("revoked");
            boolean isRevoked = (boolean) authObj1.get("is_revoked");
            String auth_status = (String) authObj1.get("status");
            String user_Email = (String) authObj1.get("user_email");
            String user_Uid = (String) authObj1.get("user_uid");
            String userStatus = (String) authObj1.get("user_status");
            String expires = (String) authObj1.get("expires");
            boolean isExpired = (boolean) authObj1.get("is_expired");
            String utility_ID = (String) authObj1.get("utility");

            JSONObject customerSignature = (JSONObject) authObj1.get("customer_signature");
            String name = (String) customerSignature.get("full_name");

            //No.Of.Meters
            JSONObject meterObj = (JSONObject) authObj1.get("meters");
            JSONArray metersArray = (JSONArray) meterObj.get("meters");
            Integer noOfConnections = metersArray.size();

            JSONObject json1 = new JSONObject();
            json1.put("customerSignature", authObj1.get("customer_signature"));
            json1.put("notes", authObj1.get("notes"));
            json1.put("scope", authObj1.get("scope"));
            json1.put("referrals", authObj1.get("referrals"));

            //UtilityIntegrationCustomerContext utilityIntegrationCustomerContext = new UtilityIntegrationCustomerContext();

            UtilityIntegrationCustomerContext utilityIntegrationCustomerContext = (UtilityIntegrationCustomerContext) V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER, id, UtilityIntegrationCustomerContext.class);

            if (utilityIntegrationCustomerContext != null) {


                long createdTime = DateTimeUtil.getTime(created_Time, DATE_FORMAT);
                long expiresTime = DateTimeUtil.getTime(expires, DATE_FORMAT);
                long revokedTime = 0;
                if (revoked != null && !revoked.isEmpty()) {
                    revokedTime = DateTimeUtil.getTime(revoked, DATE_FORMAT);
                }

                utilityIntegrationCustomerContext.setAuthorizationSubmittedTime(createdTime);
                utilityIntegrationCustomerContext.setCustomerEmail(customerEmail);
                utilityIntegrationCustomerContext.setName(name);
                utilityIntegrationCustomerContext.setCustomerId(customerId);
                utilityIntegrationCustomerContext.setFormUid(formUid);
                utilityIntegrationCustomerContext.setTemplateUid(templateUid);
                utilityIntegrationCustomerContext.setIsArchived(isArchived);
                utilityIntegrationCustomerContext.setIsRevoked(isRevoked);
                utilityIntegrationCustomerContext.setRevoked(revokedTime);
                utilityIntegrationCustomerContext.setStatus(auth_status);
                utilityIntegrationCustomerContext.setUserEmail(user_Email);
                utilityIntegrationCustomerContext.setUserUid(user_Uid);
                utilityIntegrationCustomerContext.setUserStatus(userStatus);
                utilityIntegrationCustomerContext.setExpires(expiresTime);
                utilityIntegrationCustomerContext.setIsExpired(isExpired);
                utilityIntegrationCustomerContext.setUtilityID(utility_ID);
                utilityIntegrationCustomerContext.setMeta(json1.toJSONString());
                utilityIntegrationCustomerContext.setNoOfConnections(Long.valueOf(noOfConnections));
                utilityIntegrationCustomerContext.setCustomerType(2);

                // authLists.add(utilityIntegrationCustomerContext);

                FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
                List<FacilioField> fields = modBean.getAllFields(module.getName());
                V3RecordAPI.updateRecord(utilityIntegrationCustomerContext, module, fields);
                //V3Util.updateSingleRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER, id, FieldUtil.getAsJSON(utilityIntegrationCustomerContext),null, null, null, null, null,null,null, null);
                //V3Util.createRecordList(module, FieldUtil.getAsMapList(authLists, UtilityIntegrationCustomerContext.class), null, null);
            }
    }
        for (Object obj1 : authObject) {
            JSONObject authObj2 = (JSONObject) obj1;
            JSONObject meterObj = (JSONObject) authObj2.get("meters");
            JSONArray metersArray = (JSONArray) meterObj.get("meters");
            Integer noOfConnections = metersArray.size();
            for(Object meter: metersArray){
                JSONObject meters = (JSONObject) meter;
                String customerUid = (String) meters.get("authorization_uid");
                String meterUid = (String) meters.get("uid");
                String userEmail = (String) meters.get("user_email");
                String createdTime = (String) meters.get("created");
                long userUid = -1;
                if(meters.get("user_uid") != null ) {
                     userUid = FacilioUtil.parseLong(meters.get("user_uid"));
                }
                Boolean isArchieved = (Boolean) meters.get("is_archived");
                String status = (String) meters.get("status");
                String utilityID = (String) meters.get("utility");
                Boolean is_activated = (Boolean) meters.get("is_activated");
                long bill_count = -1 ; long interval_count = -1;
                if(meters.get("bill_count") != null) {
                    bill_count = FacilioUtil.parseLong(meters.get("bill_count"));
                }
                if(meters.get("interval_count") != null) {
                    interval_count = FacilioUtil.parseLong(meters.get("interval_count"));
                }

                //ongoing monitoring data
                JSONObject monitor = (JSONObject) meters.get("ongoing_monitoring");
                String frequency = (String) monitor.get("frequency");
                String nextPrepay = (String) monitor.get("next_prepay");
                String nextRefresh = (String) monitor.get("next_refresh");
                String prepay = (String) monitor.get("prepay");

                JSONObject json = new JSONObject();
                json.put("blocks",meters.get("blocks"));
                json.put("base",meters.get("base"));
                json.put("statusMessage",meters.get("status_message"));
                json.put("billCoverage",meters.get("bill_coverage"));
                json.put("intervalCoverage",meters.get("interval_coverage"));

                JSONObject baseObj = (JSONObject) meters.get("base");
                String serviceTariff = (String) baseObj.get("service_tariff");
                String serviceClass = (String) baseObj.get("service_class");

                UtilityIntegrationMeterContext.MeterState state = UtilityIntegrationMeterContext.MeterState.UNMAPPED;
                UtilityIntegrationMeterContext meterContext = new UtilityIntegrationMeterContext();

                long crtTime = DateTimeUtil.getTime(createdTime, DATE_FORMAT);

                    meterContext.setCreatedTime(crtTime);
                    meterContext.setMeterUid(meterUid);
                    meterContext.setCustomerUid(customerUid);
                    meterContext.setUserEmail(userEmail);
                    meterContext.setIsArchived(isArchieved);
                    meterContext.setStatus(status);
                    meterContext.setBillCount(bill_count);
                    meterContext.setIntervalCount(interval_count);
                    meterContext.setUserUid(userUid);
                    meterContext.setUtilityID(utilityID);
                    meterContext.setIsActivated(is_activated);
                    meterContext.setMeterState(UtilityIntegrationMeterContext.MeterState.UNMAPPED.getIntVal());
                    meterContext.setFrequency(frequency);
                    meterContext.setServiceClass(serviceClass);
                    meterContext.setServiceTariff(serviceTariff);

                    if(nextPrepay != null && nextRefresh != null && prepay != null) {

                        long nxtPrepay = DateTimeUtil.getTime(nextPrepay, DATE_FORMAT);
                        long nxtRefresh = DateTimeUtil.getTime(nextRefresh, DATE_FORMAT);

                        meterContext.setNextPrepay(nxtPrepay);
                        meterContext.setNextRefresh(nxtRefresh);
                        meterContext.setPrepay(prepay);

                    }

                    // getting utility integartion customer id
                    FacilioModule customerModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
                    String moduleName = FacilioConstants.UTILITY_INTEGRATION_CUSTOMER;
                    List<FacilioField> fields = modBean.getAllFields(moduleName);

                    SelectRecordsBuilder<UtilityIntegrationCustomerContext> builder = new SelectRecordsBuilder<UtilityIntegrationCustomerContext>()
                            .moduleName(moduleName)
                            .select(fields)
                            .beanClass(UtilityIntegrationCustomerContext.class)
                            .andCondition(CriteriaAPI.getCondition("CUSTOMER_UID", "customerId", customerUid, NumberOperators.EQUALS));
                    UtilityIntegrationCustomerContext lists = builder.fetchFirst();
                    meterContext.setUtilityIntegrationCustomer(lists);
                    meterContext.setMeta(json.toJSONString());

                    metersList.add(meterContext);
            }

        }

        FacilioModule meterModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
        V3Util.createRecordList(meterModule,FieldUtil.getAsMapList(metersList,UtilityIntegrationMeterContext.class),null,null);

    }
    public static void activateMeters(List meterUids) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule meterModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_METER;
        List<FacilioField>fields = modBean.getAllFields(moduleName);

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, UTILITY_AUTHORIZATION);

        JSONObject obj = new JSONObject();
        obj.put("meters",meterUids);

        String payload = obj.toJSONString();

        String response = FacilioHttpUtils.doHttpPost(UTILITY_ENDPOINT+"meters/historical-collection", headers, null, payload);

        JSONObject jsonObject =  FacilioUtil.parseJson(response);
        JSONArray meterObjects = (JSONArray) jsonObject.get("meters");
        boolean isSuccess = (boolean) jsonObject.get("success");

        if(isSuccess == true) {
            for (Object meterObjects1 : meterObjects) {
                String meter_uid = (String) meterObjects1;
                SelectRecordsBuilder<UtilityIntegrationMeterContext> builder = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                        .moduleName(moduleName)
                        .select(fields)
                        .beanClass(UtilityIntegrationMeterContext.class)
                        .andCondition(CriteriaAPI.getCondition("METER_UID", "meterUid", meter_uid, NumberOperators.EQUALS))
                        ;
                UtilityIntegrationMeterContext lists = builder.fetchFirst();

                        UtilityIntegrationMeterContext.MeterState state = UtilityIntegrationMeterContext.MeterState.PENDING;
                        lists.setMeterState(state.getIntVal());
                        //for dispute testing--remove
                        //lists.setServiceTariff("Residential");
                        //V3Util.processAndUpdateSingleRecord(FacilioConstants.UTILITY_INTEGRATION_METER, lists.getId(), FieldUtil.getAsJSON(lists), null, null, null, null, null,null,null, null,null);
                        V3RecordAPI.updateRecord(lists, meterModule, fields);
            }
        }
    }

    public static JSONObject getMetersAfterActivation(String meterUid) throws Exception {

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, UTILITY_AUTHORIZATION);

        Map<String, String> params = new HashMap<>();
        params.put("uids", meterUid);

        String response = FacilioHttpUtils.doHttpGet(UTILITY_ENDPOINT + "meters", headers, params);

        JSONObject jsonObject =  FacilioUtil.parseJson(response);

        return jsonObject;
    }

    public static long convertStartDateToMilliseconds(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    public static long convertEndDateToMilliseconds(Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        System.out.println("Calculated milliseconds: " + calendar.getTimeInMillis());
        return calendar.getTimeInMillis();
    }
    public static void checkAndDeleteDuplicateBill(Long meterId,Long startTime,Long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_BILLS;
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SimpleDateFormat simple = new SimpleDateFormat(
                "dd MMM yyyy HH:mm:ss:SSS Z");

        Date strt = new Date(startTime);
        Date end = new Date(endTime);


        long startMillis = convertStartDateToMilliseconds(strt);
        long endMillis = convertEndDateToMilliseconds(end);

        SelectRecordsBuilder<UtilityIntegrationBillContext> builder = new SelectRecordsBuilder<UtilityIntegrationBillContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(UtilityIntegrationBillContext.class)
                .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_METER_ID", "utilityIntegrationMeter", String.valueOf(meterId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("BILL_START_DATE", "billStartDate", String.valueOf(startMillis), NumberOperators.GREATER_THAN_EQUAL))
                .andCondition(CriteriaAPI.getCondition("BILL_END_DATE", "billEndDate", String.valueOf(endMillis), NumberOperators.LESS_THAN_EQUAL))
                ;

        List<UtilityIntegrationBillContext> billLists = builder.get();

        if(CollectionUtils.isNotEmpty(billLists)){

            for(UtilityIntegrationBillContext bill : billLists){
                FacilioModule disputeModule = modBean.getModule(FacilioConstants.UTILITY_DISPUTE);
                String disputeModuleName= FacilioConstants.UTILITY_DISPUTE;
                List<FacilioField> disputeFields = modBean.getAllFields(disputeModuleName);


                SelectRecordsBuilder<UtilityDisputeContext> list = new SelectRecordsBuilder<UtilityDisputeContext>()
                        .moduleName(disputeModuleName)
                        .select(disputeFields)
                        .beanClass(UtilityDisputeContext.class)
                        .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_BILL", "utilityBill", String.valueOf(bill.getId()), NumberOperators.EQUALS));

                List<UtilityDisputeContext> disputeContexts = list.get();
                if(CollectionUtils.isNotEmpty(disputeContexts)){
                    Criteria criteria = new Criteria();
                    Condition condition = CriteriaAPI.getCondition("UTILITY_INTEGRATION_BILL", "utilityBill", String.valueOf(bill.getId()), NumberOperators.EQUALS);
                    criteria.addAndCondition(condition);
                    V3RecordAPI.deleteRecords(disputeModuleName,criteria,true);
                }
            }
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_METER_ID", "utilityIntegrationMeter", String.valueOf(meterId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("BILL_START_DATE", "billStartDate", String.valueOf(startMillis), NumberOperators.GREATER_THAN_EQUAL));
            criteria.addAndCondition(CriteriaAPI.getCondition("BILL_END_DATE", "billEndDate", String.valueOf(endMillis), NumberOperators.LESS_THAN_EQUAL))
            ;
        //delete bill- already exists
            V3RecordAPI.deleteRecords(moduleName,criteria,true);

        }
    }

//    public static String convertToCustomISO8601(long milliseconds, String timezoneOffset) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//        Date date = new Date(milliseconds);
//        String iso8601String = sdf.format(date);
//
//        // Append the custom timezone offset
//        return iso8601String.substring(0, iso8601String.length() - 1) + timezoneOffset;
//    }

    public static String convertMillisToUTCPlusOneDay(long milliseconds) {
        // Convert milliseconds to UTC
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = new Date(milliseconds);

        // Add one day to UTC
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(utcDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date utcPlusOneDay = calendar.getTime();

        // Format the UTC date as a string
        String utcPlusOneDayString = sdf.format(utcPlusOneDay);

        return utcPlusOneDayString;
    }
    public static void getBillsForMeter(String meterUid,Long startTime,Long endTime) throws Exception{
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, UTILITY_AUTHORIZATION);


        String startUTC = convertMillisToUTCPlusOneDay(startTime);
        String endUTC = convertMillisToUTCPlusOneDay(endTime);
       // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //formatter.setTimeZone(TimeZone.getTimeZone("Europe/London"));

//        SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
//        utcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//        Date startDate = new Date(startTime);
//        Date endDate = new Date(endTime);
//
//        String startUTC = utcDateFormat.format(startDate);
//        String endUTC = utcDateFormat.format(endDate);

//        String startstr = convertToCustomISO8601(startTime, "+00:00");
//                //formatter.format(startTime);
//        String endStr = convertToCustomISO8601(endTime, "+00:00");
//                //formatter.format(endTime);



       // String startstr = DateTimeUtil.getFormattedTime(startTime,DATE_FORMAT);
        // String endStr = DateTimeUtil.getFormattedTime(endTime,DATE_FORMAT);

        Map<String, String> params = new HashMap<>();
        params.put("meters", meterUid);
        params.put("start",startUTC);
        params.put("end",endUTC);
        String response = FacilioHttpUtils.doHttpGet(UTILITY_ENDPOINT + "bills", headers, params);

        List<UtilityIntegrationBillContext> billLists = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        JSONObject jsonObject =  FacilioUtil.parseJson(response);
        JSONArray billList = (JSONArray) jsonObject.get("bills");

        //getting bills
        for (Object obj : billList) {
            JSONObject billObj = (JSONObject) obj;
            String authorization_uid = (String) billObj.get("authorization_uid");
            String billUid = (String) billObj.get("uid");
            String meter_uid = (String) billObj.get("meter_uid");
            String created = (String) billObj.get("created");
            String updated = (String) billObj.get("updated");
            String utility = (String) billObj.get("utility");

            JSONObject meta = new JSONObject();
            meta.put("notes", billObj.get("notes"));
            meta.put("blocks", billObj.get("blocks"));

           //getting source blocks
            JSONArray sourceArray = (JSONArray)  billObj.get("sources");
            JSONObject sourceObj = (JSONObject) sourceArray.get(0);
            String rawUrl = (String) sourceObj.get("raw_url");
            String  type = (String) sourceObj.get("type");

            //getting base blocks
            JSONObject baseObj = (JSONObject) billObj.get("base");
            String serviceIdentifier = (String) baseObj.get("service_identifier");
            String serviceTariff = (String) baseObj.get("service_tariff");
            String serviceAddress = (String) baseObj.get("service_address");
            String serviceClass = (String) baseObj.get("service_class");

            JSONObject json = new JSONObject();
            json.put("meterNumbers",baseObj.get("meter_numbers"));

            String billingContact = (String) baseObj.get("billing_contact");
            String billingAddress = (String) baseObj.get("billing_address");
            String billingAccount = (String) baseObj.get("billing_account");
            String billStatementDate = (String) baseObj.get("bill_statement_date");
            String billStartDate = (String) baseObj.get("bill_start_date");
            String billEndDate = (String) baseObj.get("bill_end_date");
            String billTotalUnit = (String) baseObj.get("bill_total_unit");
            Double billTotalCost = null; Double billTotalVolume = null;
            if(baseObj.get("bill_total_cost") != null) {
                  billTotalCost = FacilioUtil.parseDouble(baseObj.get("bill_total_cost"));
                BigDecimal roundedValue = BigDecimal.valueOf(billTotalCost).setScale(2, BigDecimal.ROUND_HALF_UP);
                billTotalCost = roundedValue.doubleValue();
            }
            if( baseObj.get("bill_total_volume") != null) {
                billTotalVolume = FacilioUtil.parseDouble(baseObj.get("bill_total_volume"));
                BigDecimal roundedValue = BigDecimal.valueOf(billTotalVolume).setScale(2, BigDecimal.ROUND_HALF_UP);
                billTotalVolume = roundedValue.doubleValue();
            }


            UtilityIntegrationBillContext utilityIntegrationBillContext = new UtilityIntegrationBillContext();
            utilityIntegrationBillContext.setBillUid(billUid);
            utilityIntegrationBillContext.setMeterUid(meter_uid);
            utilityIntegrationBillContext.setCustomerUid(authorization_uid);

            long crtTime = DateTimeUtil.getTime(created, DATE_FORMAT);
            long updTime = DateTimeUtil.getTime(updated, DATE_FORMAT);

            utilityIntegrationBillContext.setCreatedTime(crtTime);
            utilityIntegrationBillContext.setUpdatedTime(updTime);
            utilityIntegrationBillContext.setUtilityID(utility);
            utilityIntegrationBillContext.setMeta(meta.toJSONString());
            utilityIntegrationBillContext.setServiceIdentifier(serviceIdentifier);
            utilityIntegrationBillContext.setServiceTariff(serviceTariff);
            utilityIntegrationBillContext.setServiceAddress(serviceAddress);
            utilityIntegrationBillContext.setServiceClass(serviceClass);
            utilityIntegrationBillContext.setBillingContact(billingContact);
            utilityIntegrationBillContext.setBillingAddress(billingAddress);
            utilityIntegrationBillContext.setBillingAccount(billingAccount);

            long stdate = DateTimeUtil.getTime(billStatementDate, DATE_FORMAT);
            long start = DateTimeUtil.getTime(billStartDate, DATE_FORMAT);
            long end = DateTimeUtil.getTime(billEndDate, DATE_FORMAT);

            utilityIntegrationBillContext.setBillStatementDate(stdate);
            utilityIntegrationBillContext.setBillStartDate(start);
            utilityIntegrationBillContext.setBillEndDate(end);

            utilityIntegrationBillContext.setBillTotalUnit(billTotalUnit);
            utilityIntegrationBillContext.setBillTotalCost(billTotalCost);
            utilityIntegrationBillContext.setBillTotalVolume(billTotalVolume);
            utilityIntegrationBillContext.setSourceUrl(rawUrl);
            utilityIntegrationBillContext.setSourceType(type);
            utilityIntegrationBillContext.setMeta(json.toJSONString());

            String moduleName= FacilioConstants.UTILITY_INTEGRATION_CUSTOMER;
            List<FacilioField>fields = modBean.getAllFields(moduleName);

            SelectRecordsBuilder<UtilityIntegrationCustomerContext> builder = new SelectRecordsBuilder<UtilityIntegrationCustomerContext>()
                    .moduleName(moduleName)
                    .select(fields)
                    .beanClass(UtilityIntegrationCustomerContext.class)
                    .andCondition(CriteriaAPI.getCondition("CUSTOMER_UID", "customerId", authorization_uid, NumberOperators.EQUALS))
                    ;
            UtilityIntegrationCustomerContext lists = builder.fetchFirst();
            utilityIntegrationBillContext.setUtilityIntegrationCustomer(lists);


            String meterModuleName= FacilioConstants.UTILITY_INTEGRATION_METER;
            List<FacilioField>meterFields = modBean.getAllFields(meterModuleName);

            SelectRecordsBuilder<UtilityIntegrationMeterContext> meterBuilder = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                    .moduleName(meterModuleName)
                    .select(meterFields)
                    .beanClass(UtilityIntegrationMeterContext.class)
                    .andCondition(CriteriaAPI.getCondition("METER_UID", "meterUid", meter_uid, NumberOperators.EQUALS))
                    ;
            UtilityIntegrationMeterContext meterList = meterBuilder.fetchFirst();
            utilityIntegrationBillContext.setUtilityIntegrationMeter(meterList);



            //getting supplier block
            JSONArray supplierArray = (JSONArray) billObj.get("suppliers");
            if (supplierArray != null && !supplierArray.isEmpty()) {
                for (Object supplier : supplierArray) {
                    JSONObject supplierObj = (JSONObject) supplier;
                    String supplierType = (String) supplierObj.get("supplier_type");
                    String supplierName = (String) supplierObj.get("supplier_name");
                    String supplierServiceId = (String) supplierObj.get("supplier_service_id");
                    String supplierTariff = (String) supplierObj.get("supplier_tariff");
                    Double supplierTotalCost = null; Double  supplierTotalVolume= null;
                    if(supplierObj.get("supplier_total_cost") != null) {
                        supplierTotalCost = FacilioUtil.parseDouble(supplierObj.get("supplier_total_cost"));
                        BigDecimal roundedValue = BigDecimal.valueOf(supplierTotalCost).setScale(2, BigDecimal.ROUND_HALF_UP);
                        supplierTotalCost = roundedValue.doubleValue();
                    }
                    if( supplierObj.get("supplier_total_volume") != null) {
                        supplierTotalVolume = FacilioUtil.parseDouble(supplierObj.get("supplier_total_volume"));
                        BigDecimal roundedValue = BigDecimal.valueOf(supplierTotalVolume).setScale(2, BigDecimal.ROUND_HALF_UP);
                        supplierTotalVolume = roundedValue.doubleValue();
                    }
                    String supplierTotalUnit = (String) supplierObj.get("supplier_total_unit");

                    utilityIntegrationBillContext.setSupplierType(supplierType);
                    utilityIntegrationBillContext.setSupplierName(supplierName);
                    utilityIntegrationBillContext.setSupplierServiceId(supplierServiceId);
                    utilityIntegrationBillContext.setSupplierTariff(supplierTariff);
                    utilityIntegrationBillContext.setSupplierTotalCost(supplierTotalCost);
                    utilityIntegrationBillContext.setSupplierTotalVolume(supplierTotalVolume);
                    utilityIntegrationBillContext.setSupplierTotalUnit(supplierTotalUnit);
                }
            }
            
            Long billFileId = UtilitySDK.downloadUtilityRawBill(rawUrl);
            utilityIntegrationBillContext.setBillFileId(billFileId);

            String rawBillDownloadUrl =  FacilioFactory.getFileStore().getDownloadUrl(billFileId);
            utilityIntegrationBillContext.setSourceDownloadUrl(rawBillDownloadUrl);
            utilityIntegrationBillContext.setUtilityBillStatus(UtilityIntegrationBillContext.UtilityBillStatus.CLEAR.getIntVal());

            billLists.add(utilityIntegrationBillContext);
        }
        FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS);
        V3Util.createRecordList(module, FieldUtil.getAsMapList(billLists, UtilityIntegrationBillContext.class), null, null);

        //creating line items
        fetchAndCreateLineItemsForBill(billList);
        //creating tier items
        fetchAndCreateTierItemsForBill(billList);
        // creating supplier line items
        fetchAndCreateSupplierItemsForBill(billList);
       //creating time-of-use items
        fetchAndCreateTOUForBill(billList);
       //creating demand items
        fetchAndCreateDemandItemsForBill(billList);
        //creating power blocks
        fetchAndCreatePowerItemsForBill(billList);



    }

    public static void fetchAndCreateLineItemsForBill(JSONArray billList) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<UtilityIntegrationLineItemContext> lineItemLists = new ArrayList<>();

        for (Object obj : billList) {
            JSONObject billObj = (JSONObject) obj;
            String billUid = (String) billObj.get("uid");

            JSONArray lineItemArray = (JSONArray) billObj.get("line_items");

            if (lineItemArray != null && !lineItemArray.isEmpty()) {
                for (Object lineItems : lineItemArray) {
                    JSONObject lineItem = (JSONObject) lineItems;
                    Double cost = null;
                    if(lineItem.get("cost") != null) {
                        cost = FacilioUtil.parseDouble(lineItem.get("cost"));
                        BigDecimal roundedValue = BigDecimal.valueOf(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
                        cost = roundedValue.doubleValue();
                    }
                    String end = (String) lineItem.get("end");
                    String kind = (String) lineItem.get("kind");
                    String name = (String) lineItem.get("name");
                    Double rate = null;
                    if(lineItem.get("rate") != null) {
                         rate = FacilioUtil.parseDouble(lineItem.get("rate"));
                        BigDecimal roundedValue = BigDecimal.valueOf(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
                        rate = roundedValue.doubleValue();
                    }
                    String start = (String) lineItem.get("start");
                    String unit = (String) lineItem.get("unit");
                    Double volume = null;
                    if(lineItem.get("volume") != null) {
                        volume = FacilioUtil.parseDouble(lineItem.get("volume"));
                        BigDecimal roundedValue = BigDecimal.valueOf(volume).setScale(2, BigDecimal.ROUND_HALF_UP);
                        volume = roundedValue.doubleValue();

                    }

                    UtilityIntegrationLineItemContext utilityIntegrationLineItemContext = new UtilityIntegrationLineItemContext();

                    utilityIntegrationLineItemContext.setName(name);
                    utilityIntegrationLineItemContext.setCost(cost);

                    long startDate = DateTimeUtil.getTime(start, DATE_FORMAT);
                    long endDate = DateTimeUtil.getTime(end, DATE_FORMAT);

                    utilityIntegrationLineItemContext.setStart(startDate);
                    utilityIntegrationLineItemContext.setEnd(endDate);
                    utilityIntegrationLineItemContext.setRate(rate);
                    utilityIntegrationLineItemContext.setUnit(unit);
                    utilityIntegrationLineItemContext.setVolume(volume);
                    utilityIntegrationLineItemContext.setChargeKind(kind);

                    String moduleName = FacilioConstants.UTILITY_INTEGRATION_BILLS;
                    List<FacilioField> fields = modBean.getAllFields(moduleName);

                    SelectRecordsBuilder<UtilityIntegrationBillContext> builder = new SelectRecordsBuilder<UtilityIntegrationBillContext>()
                            .moduleName(moduleName)
                            .select(fields)
                            .beanClass(UtilityIntegrationBillContext.class)
                            .andCondition(CriteriaAPI.getCondition("BILL_UID", "billUid", billUid, NumberOperators.EQUALS));
                    List<UtilityIntegrationBillContext> lists = builder.get();

                    if (CollectionUtils.isNotEmpty(lists)) {
                        for (UtilityIntegrationBillContext list : lists) {
                            utilityIntegrationLineItemContext.setUtilityIntegrationBill(list);
                        }
                    }

                    lineItemLists.add(utilityIntegrationLineItemContext);
                }
            }
        }
        FacilioModule lineItemModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_LINE_ITEMS);
        V3Util.createRecordList(lineItemModule, FieldUtil.getAsMapList(lineItemLists, UtilityIntegrationLineItemContext.class), null, null);
    }

    public static void fetchAndCreateTierItemsForBill(JSONArray billList) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<UtilityIntegrationTierItemContext> lineItemLists = new ArrayList<>();

        for (Object obj : billList) {
            JSONObject billObj = (JSONObject) obj;
            String billUid = (String) billObj.get("uid");

            JSONArray tierItemArray = (JSONArray) billObj.get("tiers");
            if (tierItemArray != null && !tierItemArray.isEmpty()) {
                for (Object tierItems : tierItemArray) {
                    JSONObject tierItem = (JSONObject) tierItems;
                    Double cost = null;
                    if( tierItem.get("cost") != null) {
                         cost = FacilioUtil.parseDouble(tierItem.get("cost"));
                        BigDecimal roundedValue = BigDecimal.valueOf(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
                        cost = roundedValue.doubleValue();

                    }
                    Long level = null;
                    if( tierItem.get("level") != null){
                        level = FacilioUtil.parseLong(tierItem.get("level"));
                    }
                    String unit = (String) tierItem.get("unit");
                    String name = (String) tierItem.get("name");
                    Double volume = null;
                    if(tierItem.get("volume") != null) {
                        volume = FacilioUtil.parseDouble(tierItem.get("volume"));
                        BigDecimal roundedValue = BigDecimal.valueOf(volume).setScale(2, BigDecimal.ROUND_HALF_UP);
                        volume = roundedValue.doubleValue();

                    }

                    UtilityIntegrationTierItemContext utilityIntegrationTierItemContext = new UtilityIntegrationTierItemContext();

                    utilityIntegrationTierItemContext.setName(name);
                    utilityIntegrationTierItemContext.setCost(cost);
                    utilityIntegrationTierItemContext.setLevel(level);
                    utilityIntegrationTierItemContext.setUnit(unit);
                    utilityIntegrationTierItemContext.setVolume(volume);

                    String moduleName = FacilioConstants.UTILITY_INTEGRATION_BILLS;
                    List<FacilioField> fields = modBean.getAllFields(moduleName);
                    SelectRecordsBuilder<UtilityIntegrationBillContext> builder = new SelectRecordsBuilder<UtilityIntegrationBillContext>()
                            .moduleName(moduleName)
                            .select(fields)
                            .beanClass(UtilityIntegrationBillContext.class)
                            .andCondition(CriteriaAPI.getCondition("BILL_UID", "billUid", billUid, NumberOperators.EQUALS));
                    List<UtilityIntegrationBillContext> lists = builder.get();

                    if (CollectionUtils.isNotEmpty(lists)) {
                        for (UtilityIntegrationBillContext list : lists) {
                            utilityIntegrationTierItemContext.setUtilityIntegrationBill(list);
                        }
                    }
                    lineItemLists.add(utilityIntegrationTierItemContext);
                }
            }
        }
        FacilioModule tierItemModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_TIER_ITEMS);
        V3Util.createRecordList(tierItemModule, FieldUtil.getAsMapList(lineItemLists, UtilityIntegrationTierItemContext.class), null, null);

    }

    public static void fetchAndCreateSupplierItemsForBill(JSONArray billList) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
       List<UtilityIntegrationSupplierLineItemContext> lineItemLists = new ArrayList<>();

        for (Object obj : billList) {
            JSONObject billObj = (JSONObject) obj;
            String billUid = (String) billObj.get("uid");

            JSONArray supplierArray = (JSONArray) billObj.get("suppliers");
            if (supplierArray != null && !supplierArray.isEmpty()) {
                for (Object supplier : supplierArray) {
                    JSONObject supplierObj = (JSONObject) supplier;
                    JSONArray supplierLineItems = (JSONArray) supplierObj.get("supplier_line_items");

                    if (!supplierLineItems.isEmpty()) {
                        for (Object lineItem : supplierLineItems) {
                            JSONObject lineItemObj = (JSONObject) lineItem;
                            Double cost = null;
                            if(lineItemObj.get("cost") != null) {
                                 cost = FacilioUtil.parseDouble(lineItemObj.get("cost"));
                                BigDecimal roundedValue = BigDecimal.valueOf(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
                                cost = roundedValue.doubleValue();
                            }
                            String end = (String) lineItemObj.get("end");
                            String kind = (String) lineItemObj.get("kind");
                            String name = (String) lineItemObj.get("name");
                            Double rate = null;
                            if(lineItemObj.get("rate") != null) {
                                 rate = FacilioUtil.parseDouble(lineItemObj.get("rate"));
                                BigDecimal roundedValue = BigDecimal.valueOf(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
                                rate = roundedValue.doubleValue();

                            }
                            String start = (String) lineItemObj.get("start");
                            String unit = (String) lineItemObj.get("unit");
                            Double volume = null;
                            if( lineItemObj.get("volume") != null) {
                                volume = FacilioUtil.parseDouble(lineItemObj.get("volume"));
                                BigDecimal roundedValue = BigDecimal.valueOf(volume).setScale(2, BigDecimal.ROUND_HALF_UP);
                                volume = roundedValue.doubleValue();
                            }

                            UtilityIntegrationSupplierLineItemContext utilityIntegrationSupplierLineItemContext = new UtilityIntegrationSupplierLineItemContext();

                            long startDate = DateTimeUtil.getTime(start, DATE_FORMAT);
                            long endDate = DateTimeUtil.getTime(end, DATE_FORMAT);

                            utilityIntegrationSupplierLineItemContext.setName(name);
                            utilityIntegrationSupplierLineItemContext.setCost(cost);
                            utilityIntegrationSupplierLineItemContext.setStart(startDate);
                            utilityIntegrationSupplierLineItemContext.setEnd(endDate);
                            utilityIntegrationSupplierLineItemContext.setUnit(unit);
                            utilityIntegrationSupplierLineItemContext.setVolume(volume);
                            utilityIntegrationSupplierLineItemContext.setRate(rate);
                            utilityIntegrationSupplierLineItemContext.setChargeKind(kind);

                            String moduleName = FacilioConstants.UTILITY_INTEGRATION_BILLS;
                            List<FacilioField> fields = modBean.getAllFields(moduleName);

                            SelectRecordsBuilder<UtilityIntegrationBillContext> builder = new SelectRecordsBuilder<UtilityIntegrationBillContext>()
                                    .moduleName(moduleName)
                                    .select(fields)
                                    .beanClass(UtilityIntegrationBillContext.class)
                                    .andCondition(CriteriaAPI.getCondition("BILL_UID", "billUid", billUid, NumberOperators.EQUALS));
                            List<UtilityIntegrationBillContext> lists = builder.get();

                            if (CollectionUtils.isNotEmpty(lists)) {
                                for (UtilityIntegrationBillContext list : lists) {
                                    utilityIntegrationSupplierLineItemContext.setUtilityIntegrationBill(list);
                                }
                            }
                            lineItemLists.add(utilityIntegrationSupplierLineItemContext);
                        }
                    }
                }
            }
        }
        FacilioModule tierItemModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_TIER_ITEMS);
        V3Util.createRecordList(tierItemModule, FieldUtil.getAsMapList(lineItemLists, UtilityIntegrationTierItemContext.class), null, null);

    }

    public static void fetchAndCreateTOUForBill(JSONArray billList) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<UtilityIntegrationTOUContext> lineItemLists = new ArrayList<>();

        for (Object obj : billList) {
            JSONObject billObj = (JSONObject) obj;
            String billUid = (String) billObj.get("uid");
            JSONArray touArray = (JSONArray) billObj.get("tou");
            if (touArray != null && !touArray.isEmpty()) {
                for (Object touItems : touArray) {
                    JSONObject touItem = (JSONObject) touItems;
                    Double cost = null;
                    if(touItem.get("cost") != null) {
                        cost = FacilioUtil.parseDouble(touItem.get("cost"));
                        BigDecimal roundedValue = BigDecimal.valueOf(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
                        cost = roundedValue.doubleValue();
                    }
                    String bucket = (String) touItem.get("bucket");
                    String unit = (String) touItem.get("unit");
                    String name = (String) touItem.get("name");
                    Double volume = null;
                    if(touItem.get("volume") != null) {
                        volume = FacilioUtil.parseDouble(touItem.get("volume"));
                        BigDecimal roundedValue = BigDecimal.valueOf(volume).setScale(2, BigDecimal.ROUND_HALF_UP);
                        volume = roundedValue.doubleValue();
                    }

                    UtilityIntegrationTOUContext utilityIntegrationTOUContext = new UtilityIntegrationTOUContext();

                    utilityIntegrationTOUContext.setName(name);
                    utilityIntegrationTOUContext.setCost(cost);
                    utilityIntegrationTOUContext.setBucket(bucket);
                    utilityIntegrationTOUContext.setUnit(unit);
                    utilityIntegrationTOUContext.setVolume(volume);

                    String moduleName = FacilioConstants.UTILITY_INTEGRATION_BILLS;
                    List<FacilioField> fields = modBean.getAllFields(moduleName);

                    SelectRecordsBuilder<UtilityIntegrationBillContext> builder = new SelectRecordsBuilder<UtilityIntegrationBillContext>()
                            .moduleName(moduleName)
                            .select(fields)
                            .beanClass(UtilityIntegrationBillContext.class)
                            .andCondition(CriteriaAPI.getCondition("BILL_UID", "billUid", billUid, NumberOperators.EQUALS));
                    List<UtilityIntegrationBillContext> lists = builder.get();

                    if (CollectionUtils.isNotEmpty(lists)) {
                        for (UtilityIntegrationBillContext list : lists) {
                            utilityIntegrationTOUContext.setUtilityIntegrationBill(list);
                        }
                    }
                    lineItemLists.add(utilityIntegrationTOUContext);
                }
            }
        }
        FacilioModule touModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_TOU);
        V3Util.createRecordList(touModule, FieldUtil.getAsMapList(lineItemLists, UtilityIntegrationTOUContext.class), null, null);
    }


    public static void fetchAndCreateDemandItemsForBill(JSONArray billList) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<UtilityIntegrationDemandContext> lineItemLists = new ArrayList<>();

        for (Object obj : billList) {
            JSONObject billObj = (JSONObject) obj;
            String billUid = (String) billObj.get("uid");
            JSONArray demandArray = (JSONArray) billObj.get("demand");
            if (demandArray != null && !demandArray.isEmpty()) {
                for (Object demandItems : demandArray) {
                    JSONObject demandItem = (JSONObject) demandItems;
                    Double cost = null;
                    if(demandItem.get("cost") != null) {
                        cost = FacilioUtil.parseDouble(demandItem.get("cost"));
                        BigDecimal roundedValue = BigDecimal.valueOf(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
                        cost = roundedValue.doubleValue();
                    }
                    String name = (String) demandItem.get("name");
                    Double demand = null;
                    if(demandItem.get("demand") != null) {
                         demand = FacilioUtil.parseDouble(demandItem.get("demand"));
                        BigDecimal roundedValue = BigDecimal.valueOf(demand).setScale(2, BigDecimal.ROUND_HALF_UP);
                        demand = roundedValue.doubleValue();
                    }

                    UtilityIntegrationDemandContext utilityIntegrationDemandContext = new UtilityIntegrationDemandContext();

                    utilityIntegrationDemandContext.setName(name);
                    utilityIntegrationDemandContext.setCost(cost);
                    utilityIntegrationDemandContext.setDemand(demand);

                    String moduleName = FacilioConstants.UTILITY_INTEGRATION_BILLS;
                    List<FacilioField> fields = modBean.getAllFields(moduleName);
                    SelectRecordsBuilder<UtilityIntegrationBillContext> builder = new SelectRecordsBuilder<UtilityIntegrationBillContext>()
                            .moduleName(moduleName)
                            .select(fields)
                            .beanClass(UtilityIntegrationBillContext.class)
                            .andCondition(CriteriaAPI.getCondition("BILL_UID", "billUid", billUid, NumberOperators.EQUALS));
                    List<UtilityIntegrationBillContext> lists = builder.get();

                    if (CollectionUtils.isNotEmpty(lists)) {
                        for (UtilityIntegrationBillContext list : lists) {
                            utilityIntegrationDemandContext.setUtilityIntegrationBill(list);
                        }
                    }
                    lineItemLists.add(utilityIntegrationDemandContext);
                }
            }
        }
        FacilioModule demandModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_DEMAND);
        V3Util.createRecordList(demandModule, FieldUtil.getAsMapList(lineItemLists, UtilityIntegrationDemandContext.class), null, null);

    }

    public static void fetchAndCreatePowerItemsForBill(JSONArray billList) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<UtilityIntegrationPowerContext> lineItemLists = new ArrayList<>();

        for (Object obj : billList) {
            JSONObject billObj = (JSONObject) obj;
            String billUid = (String) billObj.get("uid");
            JSONArray powerArray = (JSONArray) billObj.get("power");
            if (powerArray != null && !powerArray.isEmpty()) {
                for (Object powerItems : powerArray) {
                    JSONObject powerItem = (JSONObject) powerItems;
                    String name = (String) powerItem.get("name");
                    String type = (String) powerItem.get("type");
                    String unit = (String) powerItem.get("unit");
                    Double volume = null;
                    if(powerItem.get("volume") != null) {
                        volume = FacilioUtil.parseDouble(powerItem.get("volume"));
                        BigDecimal roundedValue = BigDecimal.valueOf(volume).setScale(2, BigDecimal.ROUND_HALF_UP);
                        volume = roundedValue.doubleValue();
                    }

                    UtilityIntegrationPowerContext utilityIntegrationPowerContext = new UtilityIntegrationPowerContext();

                    utilityIntegrationPowerContext.setName(name);
                    utilityIntegrationPowerContext.setPowerType(type);
                    utilityIntegrationPowerContext.setUnit(unit);
                    utilityIntegrationPowerContext.setVolume(volume);

                    String moduleName = FacilioConstants.UTILITY_INTEGRATION_BILLS;
                    List<FacilioField> fields = modBean.getAllFields(moduleName);

                    SelectRecordsBuilder<UtilityIntegrationBillContext> builder = new SelectRecordsBuilder<UtilityIntegrationBillContext>()
                            .moduleName(moduleName)
                            .select(fields)
                            .beanClass(UtilityIntegrationBillContext.class)
                            .andCondition(CriteriaAPI.getCondition("BILL_UID", "billUid", billUid, NumberOperators.EQUALS));
                    List<UtilityIntegrationBillContext> lists = builder.get();

                    if (CollectionUtils.isNotEmpty(lists)) {
                        for (UtilityIntegrationBillContext list : lists) {
                            utilityIntegrationPowerContext.setUtilityIntegrationBill(list);
                        }
                    }
                    lineItemLists.add(utilityIntegrationPowerContext);
                }
            }
        }
        FacilioModule powerModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_POWER);
        V3Util.createRecordList(powerModule, FieldUtil.getAsMapList(lineItemLists, UtilityIntegrationPowerContext.class), null, null);

    }

    public static void enableOnGoingMonitoring(String meterUid, String frequency, String prepay) throws Exception {

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, UTILITY_AUTHORIZATION);

        List<String> meterUids = new ArrayList<>();
        meterUids.add(meterUid);

        JSONObject obj = new JSONObject();
        obj.put("meters",meterUids);
        obj.put("frequency",frequency);
        //prepay is optional, defaults to "month_to_month"
        obj.put("prepay",prepay);

        String payload = obj.toJSONString();
        String response = FacilioHttpUtils.doHttpPost(UTILITY_ENDPOINT+"meters/ongoing-monitoring", headers, null, payload);

        JSONObject jsonObject =  FacilioUtil.parseJson(response);

        boolean isSuccess = (boolean) jsonObject.get("success");
        if(isSuccess)
        {
            JSONArray metersList = (JSONArray) jsonObject.get("meters");
            for (Object meterObj : metersList)
            {
                String meter_Uid = (String) meterObj;

                //fetching meter data after scheduling ongoing-monitoring

                JSONObject meterObject =  getMeters(meter_Uid);

                updateMeterAfterOngoingMonitoring(meterObject,meter_Uid);
            }
        }
    }

    public static JSONObject getMeters(String meterUid) throws Exception {

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, UTILITY_AUTHORIZATION);

        String response = FacilioHttpUtils.doHttpGet(UTILITY_ENDPOINT + "meters/" + meterUid, headers, null);

        JSONObject jsonObject =  FacilioUtil.parseJson(response);

        return jsonObject;
    }

    public static void updateMeterAfterOngoingMonitoring(JSONObject meterObject ,String meter_Uid) throws Exception {

        JSONObject onGoing = (JSONObject) meterObject.get("ongoing_monitoring");
        String freq = (String) onGoing.get("frequency");
        String nextPrepay = (String) onGoing.get("next_prepay");
        String nextRefresh = (String) onGoing.get("next_refresh");
        String prepay1 = (String) onGoing.get("prepay");


        long nxtPrepay = DateTimeUtil.getTime(nextPrepay, DATE_FORMAT);
        long nxtRefresh = DateTimeUtil.getTime(nextRefresh, DATE_FORMAT);


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule meterModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_METER;
        List<FacilioField>fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<UtilityIntegrationMeterContext> builder = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(UtilityIntegrationMeterContext.class)
                .andCondition(CriteriaAPI.getCondition("METER_UID", "meterUid", String.valueOf(meter_Uid), NumberOperators.EQUALS));

        //updating UTILITY_INTEGRATION_METER with new ongoing monitoring, scheduling ongoing for new data
        UtilityIntegrationMeterContext list = builder.fetchFirst();
        if(list != null ){
            list.setFrequency(freq);
            list.setNextPrepay(nxtPrepay);
            list.setNextRefresh(nxtRefresh);
            list.setPrepay(prepay1);
            V3RecordAPI.updateRecord(list, meterModule, fields);
        }

        long meterId = list.getId();
        long nextExecutionTime = DateTimeUtil.getTime(nextRefresh, DATE_FORMAT);
        long nextExecutionTimeInSec = TimeUnit.MILLISECONDS.toSeconds(nextExecutionTime);
        //scheduling ongoing for new data
        FacilioTimer.scheduleOneTimeJobWithTimestampInSec(meterId,"UtilityOngoingMonitoring",nextExecutionTimeInSec,"facilio");
    }

    public static  Long downloadUtilityRawBill(String urlString) throws Exception{
        //String urlString = "https://utilityapi.com/api/v2/files/pdf_bill/1344035/demo_bill_E1_13_2023-04-01.pdf";
        URL url = new URL(urlString);
        String fileName = FilenameUtils.getName(url.getPath());
        File file = File.createTempFile(fileName, "");
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Authorization", UTILITY_AUTHORIZATION);
        FileUtils.copyInputStreamToFile(connection.getInputStream(), file);
        //FileUtils.copyURLToFile(url, file);
        String mimeType = connection.getContentType();
        Long fileId = FacilioFactory.getFileStore().addFile(fileName, file, mimeType);

        return fileId;
    }
    public static Double calculateBill(Double consumption, List<UtilityIntegrationTariffSlabContext> tariffSlabs) throws Exception {
        BigDecimal consumptionBigDecimal = BigDecimal.valueOf(consumption);
        BigDecimal bill = BigDecimal.ZERO;

        for (UtilityIntegrationTariffSlabContext slab : tariffSlabs) {
            BigDecimal slabAmount = consumptionBigDecimal.min(BigDecimal.valueOf(slab.getTo())).subtract(BigDecimal.valueOf(slab.getFrom()));

            if (slabAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            bill = bill.add(slabAmount.multiply(BigDecimal.valueOf(slab.getPrice())));
            consumptionBigDecimal = consumptionBigDecimal.subtract(slabAmount);
        }

        bill = bill.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bill.doubleValue();
    }

//    public static Double calculateBill(Double consumption,List<UtilityIntegrationTariffSlabContext> tariffSlabs) throws Exception{
//        Double bill = 0.0;
//        for (UtilityIntegrationTariffSlabContext slab : tariffSlabs) {
//            double slabAmount = Math.min(consumption, slab.getTo()) - slab.getFrom();
//            if (slabAmount <= 0) {
//                break;
//            }
//            bill += slabAmount * slab.getPrice();
//            consumption -= slabAmount;
//        }
//        return bill;
//    }
    public static void checkAndRaiseDisputes(Long meterId,Long startTime,Long endTime) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule meterModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
        String meterModuleName = FacilioConstants.UTILITY_INTEGRATION_METER;
        List<FacilioField> meterFields = modBean.getAllFields(meterModuleName);
        //time calculateion
        SimpleDateFormat simple = new SimpleDateFormat(
                "dd MMM yyyy HH:mm:ss:SSS Z");

        Date strt = new Date(startTime);
        Date end = new Date(endTime);


        long startMillis = convertStartDateToMilliseconds(strt);
        long endMillis = convertEndDateToMilliseconds(end);

        if (meterModule != null) {

            UtilityIntegrationMeterContext.MeterState state = UtilityIntegrationMeterContext.MeterState.ACTIVATED;
            SelectRecordsBuilder<UtilityIntegrationMeterContext> builder1 = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                    .moduleName(meterModuleName)
                    .select(meterFields)
                    .beanClass(UtilityIntegrationMeterContext.class)
                    .andCondition(CriteriaAPI.getIdCondition(meterId,meterModule))
                    .andCondition(CriteriaAPI.getCondition("METER_STATE", "meterState", String.valueOf(state.getIntVal()), NumberOperators.EQUALS));
            List<UtilityIntegrationMeterContext> lists = builder1.get();

            if (CollectionUtils.isNotEmpty(lists)) {
                for (UtilityIntegrationMeterContext list : lists) {
                    FacilioModule billModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS);
                    String billModulename = FacilioConstants.UTILITY_INTEGRATION_BILLS;
                    List<FacilioField> fields1 = modBean.getAllFields(billModulename);

                    if (billModule != null) {


                        SelectRecordsBuilder<UtilityIntegrationBillContext> bills = new SelectRecordsBuilder<UtilityIntegrationBillContext>()
                                .moduleName(billModulename)
                                .select(fields1)
                                .beanClass(UtilityIntegrationBillContext.class)
                                .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_METER_ID", "utilityIntegrationMeter", String.valueOf(meterId), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition("BILL_START_DATE", "billStartDate", String.valueOf(startMillis), NumberOperators.GREATER_THAN_EQUAL))
                                .andCondition(CriteriaAPI.getCondition("BILL_END_DATE", "billEndDate", String.valueOf(endMillis), NumberOperators.LESS_THAN_EQUAL));
                        List<UtilityIntegrationBillContext> billList = bills.get();
                        if (CollectionUtils.isNotEmpty(billList)) {
                            for (UtilityIntegrationBillContext bill : billList) {

                                //Tariff mismatch
                                if (!list.getServiceTariff().equals(bill.getServiceTariff())) {
                                    UtilityDisputeContext tariffDispute = UtilityDisputeType.TARIFF_MAPPING_MISMATCH.execute(bill, list.getServiceTariff(), bill.getServiceTariff());
                                    if (tariffDispute != null) {
                                        FacilioModule disputeModule = modBean.getModule(FacilioConstants.UTILITY_DISPUTE);
                                        V3Util.createRecord(disputeModule, FieldUtil.getAsJSON(tariffDispute));

                                        bill.setUtilityBillStatus(UtilityIntegrationBillContext.UtilityBillStatus.DISPUTED.getIntVal());
                                        V3Util.processAndUpdateSingleRecord(FacilioConstants.UTILITY_INTEGRATION_BILLS, bill.getId(), FieldUtil.getAsJSON(bill), null, null, null, null, null, null, null, null, null);

                                        FacilioStatus disputedStatus = TicketAPI.getStatus(billModule,"underDispute");
                                        StateFlowRulesAPI.updateState(bill, modBean.getModule(billModulename), disputedStatus, false, new FacilioContext());

                                        //V3RecordAPI.updateRecord(bill, billModule, fields1);

                                    }
                                }

                                //consumption mismatch

                                /*method calling in meter to give meters consumption using below data
                                list.getId()--id in utility_integration_meter

                                bill.getBillStartDate()
                                bill.getBillEndDate() */
                                if(list.getMeter()!=null) {
                                Double actualConsumption = MetersAPI.calculateMeterConsumption(list.getMeter().getId(), startMillis, endMillis);

//                                Double actualConsumption = 100.00;
                                Double difference = null;
                                if (bill.getBillTotalVolume() != actualConsumption) {
                                    if (bill.getBillTotalVolume() > actualConsumption) {
                                        difference = bill.getBillTotalVolume() - actualConsumption;
                                    } else {
                                        difference = actualConsumption - bill.getBillTotalVolume();
                                    }
                                    UtilityDisputeContext consumptionDispute = UtilityDisputeType.CONSUMPTION_READING_MISMATCH.validateConumptionMismatch(bill, actualConsumption, bill.getBillTotalVolume(), difference);
                                    FacilioModule disputeModule = modBean.getModule(FacilioConstants.UTILITY_DISPUTE);
                                    V3Util.createRecord(disputeModule, FieldUtil.getAsJSON(consumptionDispute));
                                    bill.setUtilityBillStatus(UtilityIntegrationBillContext.UtilityBillStatus.DISPUTED.getIntVal());
                                    V3Util.processAndUpdateSingleRecord(FacilioConstants.UTILITY_INTEGRATION_BILLS, bill.getId(), FieldUtil.getAsJSON(bill), null, null, null, null, null, null, null, null, null);
                                    // V3RecordAPI.updateRecord(bill, billModule, fields1);
                                    FacilioStatus disputedStatus = TicketAPI.getStatus(billModule,"underDispute");
                                    StateFlowRulesAPI.updateState(bill, modBean.getModule(billModulename), disputedStatus, false, new FacilioContext());

                                }
                                }
                                //cost mismatch
                                if(list.getServiceTariff().equals(bill.getServiceTariff()) && list.getUtilityID().equals(bill.getUtilityID())){

                                    //get matching tariff
                                    String tariffModule = FacilioConstants.UTILITY_INTEGRATION_TARIFF;
                                    List<FacilioField> fields = modBean.getAllFields(tariffModule);
                                    SelectRecordsBuilder<UtilityIntegrationTariffContext> builder = new SelectRecordsBuilder<UtilityIntegrationTariffContext>()
                                            .moduleName(tariffModule)
                                            .select(fields)
                                            .beanClass(UtilityIntegrationTariffContext.class)
                                            .andCondition(CriteriaAPI.getCondition("NAME","name", bill.getServiceTariff(), StringOperators.IS));
                                            //.andCondition(CriteriaAPI.getCondition("UTILITY_PROVIDER","utilityProviders",UtilityIntegrationTariffContext.UtilityProviders.get, EnumOperators.IS));

                                    UtilityIntegrationTariffContext tariff = builder.fetchFirst();
                                    if(tariff != null) {
                                        //get corresponding tariff slabs
                                        String slabModule = FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB;
                                        List<FacilioField> slabFields = modBean.getAllFields(slabModule);
                                        SelectRecordsBuilder<UtilityIntegrationTariffSlabContext> slabs = new SelectRecordsBuilder<UtilityIntegrationTariffSlabContext>()
                                                .moduleName(slabModule)
                                                .select(slabFields)
                                                .beanClass(UtilityIntegrationTariffSlabContext.class)
                                                .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_TARIFF", "tariff", String.valueOf(tariff.getId()), NumberOperators.EQUALS));

                                        List<UtilityIntegrationTariffSlabContext> tariffSlabs = slabs.get();
                                        //based on consumption calculating amount  with our tariff and slabs
                                        Double calculatedBill = calculateBill(bill.getBillTotalVolume(), tariffSlabs);
                                        if (bill.getBillTotalCost() != calculatedBill) {
                                            Double diff = 0.0;
                                            if (bill.getBillTotalCost() > calculatedBill) {
                                                diff = bill.getBillTotalCost() - calculatedBill;
                                            } else {
                                                diff = calculatedBill - bill.getBillTotalCost();
                                            }
                                            UtilityDisputeContext costDispute = UtilityDisputeType.COST_MISMATCH.validateCostMismatch(bill, calculatedBill, bill.getBillTotalCost(), diff);
                                            FacilioModule disputeModule = modBean.getModule(FacilioConstants.UTILITY_DISPUTE);
                                            V3Util.createRecord(disputeModule, FieldUtil.getAsJSON(costDispute));
                                            bill.setUtilityBillStatus(UtilityIntegrationBillContext.UtilityBillStatus.DISPUTED.getIntVal());
                                            V3Util.processAndUpdateSingleRecord(FacilioConstants.UTILITY_INTEGRATION_BILLS, bill.getId(), FieldUtil.getAsJSON(bill), null, null, null, null, null, null, null, null, null);
                                            //V3RecordAPI.updateRecord(bill, billModule, fields1);

                                            FacilioStatus disputedStatus = TicketAPI.getStatus(billModule,"underDispute");
                                            StateFlowRulesAPI.updateState(bill, modBean.getModule(billModulename), disputedStatus, false, new FacilioContext());


                                        }
                                    }

                                }
                            }
                        }
                    }
                    //account revoked
                    if(list.getUtilityIntegrationCustomer() != null ){

                        FacilioModule customerModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
                        String moduleName = FacilioConstants.UTILITY_INTEGRATION_CUSTOMER;
                        List<FacilioField> fields = modBean.getAllFields(moduleName);

                        if (customerModule != null) {

                            SelectRecordsBuilder<UtilityIntegrationCustomerContext> builder = new SelectRecordsBuilder<UtilityIntegrationCustomerContext>()
                                    .moduleName(moduleName)
                                    .select(fields)
                                    .beanClass(UtilityIntegrationCustomerContext.class)
                                    .andCondition(CriteriaAPI.getIdCondition(list.getUtilityIntegrationCustomer().getId(),customerModule));
                            UtilityIntegrationCustomerContext utilityIntegrationCustomerContext = builder.fetchFirst();

                            if (utilityIntegrationCustomerContext != null) {
                                //account revoked
                                if (utilityIntegrationCustomerContext.getIsRevoked() && utilityIntegrationCustomerContext.getRevoked() != null) {
                                    if (billModule != null) {

                                        SelectRecordsBuilder<UtilityIntegrationBillContext> bill = new SelectRecordsBuilder<UtilityIntegrationBillContext>()
                                                .moduleName(billModulename)
                                                .select(fields1)
                                                .beanClass(UtilityIntegrationBillContext.class)
                                                .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_CUSTOMER_ID", "utilityIntegrationCustomer", String.valueOf(utilityIntegrationCustomerContext.getId()), NumberOperators.EQUALS));
                                        List<UtilityIntegrationBillContext> billLists = bill.get();
                                        if (CollectionUtils.isNotEmpty(billLists)) {
                                            for (UtilityIntegrationBillContext billList : billLists) {
                                                UtilityDisputeContext dispute = UtilityDisputeType.BILL_FOR_TERMINATED_ACCOUNT.validateTerminatedAccount(billList);
                                                FacilioModule disputeModule = modBean.getModule(FacilioConstants.UTILITY_DISPUTE);
                                                V3Util.createRecord(disputeModule, FieldUtil.getAsJSON(dispute));
                                                //update bill sTATUS
                                                billList.setUtilityBillStatus(UtilityIntegrationBillContext.UtilityBillStatus.DISPUTED.getIntVal());
                                                V3Util.processAndUpdateSingleRecord(FacilioConstants.UTILITY_INTEGRATION_BILLS, billList.getId(), FieldUtil.getAsJSON(billList), null, null, null, null, null,null,null, null,null);
                                               // V3RecordAPI.updateRecord(billList, billModule, fields1);
                                                FacilioStatus disputedStatus = TicketAPI.getStatus(billModule,"underDispute");
                                                StateFlowRulesAPI.updateState(billList, modBean.getModule(billModulename), disputedStatus, false, new FacilioContext());

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



