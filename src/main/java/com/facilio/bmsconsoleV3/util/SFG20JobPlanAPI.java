package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.*;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioHttpUtilsFW;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j
public class SFG20JobPlanAPI {
    public static SFG20SettingsContext getSFG20Setting() throws Exception {
        FacilioModule module= ModuleFactory.getSFG20SettingModule();
        List<FacilioField> fieldList = FieldFactory.getSFG20SettingsFields();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(fieldList)
                .table(module.getTableName());
        List<Map<String, Object>> props= builder.get();
        if(props.isEmpty())
        {
            return null;
        }
        return FieldUtil.getAsBeanFromMap(props.get(0),SFG20SettingsContext.class);
    }
    public static Map<String, Object> getSFG20ClientAccessToken(SFG20SettingsContext sfg20SettingsContext) throws Exception {
        String facilioBridgeKey = FacilioProperties.getSgfBridgeKey();
        String facilioSecretKey = FacilioProperties.getSfgSecretKey();
        String url = "https://az-sfg20-api-ote.azurewebsites.net/token";
        Map<String, String>  headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        if(sfg20SettingsContext.getCustomerKey().isEmpty() || sfg20SettingsContext.getCustomerSecret().isEmpty() )
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "SFG20 Setting not configured properly");
        }
        String requestBody = "grant_type=password&client_id="+facilioBridgeKey+"&" +
                             "client_secret="+facilioSecretKey+"&" +
                             "customer_id="+sfg20SettingsContext.getCustomerKey()+"&" +
                             "customer_secret="+sfg20SettingsContext.getCustomerSecret();
        String response = FacilioHttpUtilsFW.doHttpPost(url, headers, null, requestBody,null,-1);
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Access the values from the JsonObject
        return result;
    }

    public static List<Map<String, Object>> getSFG20SchedulesAPI(String accessToken) throws IOException {
        String url = "https://az-sfg20-api-ote.azurewebsites.net/api2/schedules/v2";
        Map<String, String>  headers = new HashMap<>();
        headers.put("Accept","application/json");
        headers.put("Authorization","bearer "+accessToken);
        String response = FacilioHttpUtilsFW.doHttpGet(url, headers,null,-1);

        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> computedResult = new ArrayList<>();
        SFG20Facilio keySet = new SFG20Facilio();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            result = objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(Map<String, Object> map : result)
        {
            computedResult.add(getSFG20Mapping(keySet.getSfgData(),map));
        }
        return computedResult;
    }
    public static Map<String, Object> getSFG20DetailsForScheduleIdAPI(String accessToken,Integer scheduleId) throws IOException {
        String url = "https://az-sfg20-api-ote.azurewebsites.net/api2/schedule/"+scheduleId+"/v2";
        Map<String, String>  headers = new HashMap<>();
        headers.put("Accept","application/json");
        headers.put("Authorization","bearer "+accessToken);
        String response = FacilioHttpUtilsFW.doHttpGet(url, headers,null,-1);
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static List<Map<String,Object>> getSFG20DetailsForScheduleIdsAPI(String accessToken,List<Integer> scheduleIds) throws IOException {
        List<Map<String,Object>> schedulesList = new ArrayList<>();
        for(Integer id: scheduleIds)
        {
            schedulesList.add(getSFG20DetailsForScheduleIdAPI(accessToken,id));
        }
      return schedulesList;
    }

    public static List<Map<String,Object>> getSFG20DetailsForSchedulesAPI(String accessToken,List<Map<String,Object>> schedules,long syncHistoryId) throws Exception {
        for(Map<String,Object> schedule: schedules)
        {
            Map<String,Object> scheduleDetail = getSFG20DetailsForScheduleIdAPI(accessToken, (Integer) schedule.get("scheduleId"));
            FileStore fileStore = FacilioFactory.getFileStoreFromOrg(AccountUtil.getCurrentOrg().getOrgId());
            Long fileId= fileStore.addFile((String) schedule.get("title"),scheduleDetail.toString(),"text/plain");
            schedule.put("fileId",fileId);
            schedule.put("syncId",syncHistoryId);
            schedule.put("scheduleDetail",scheduleDetail);
        }
        return schedules;
    }

    public static <K1, K2, V> Map<K2, V> getSFG20Mapping(Map<K1, K2> keySetMapping, Map<K1, V> sfgDataMap) {
        return sfgDataMap.entrySet().stream()
                .filter(entry -> keySetMapping.containsKey(entry.getKey()))
                .collect(Collectors.toMap(entry -> keySetMapping.get(entry.getKey()), Map.Entry::getValue, (v1, v2) -> v1, HashMap::new));
    }

    public static void updateSyncHistoryStatus(SFG20SyncHistoryContext syncHistoryContext,Integer status) throws Exception {
        syncHistoryContext.setStatus(status);
        if(status.equals(SFG20SyncHistoryContext.Status.COMPLETED.getIndex()))
        {
            syncHistoryContext.setSyncEndTime(System.currentTimeMillis());
        }
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getSFG20SyncHistoryModule().getTableName())
                .fields(FieldFactory.getSFG20SyncHistoryFields())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(syncHistoryContext.getId()), NumberOperators.EQUALS));
        updateRecordBuilder.update(FieldUtil.getAsProperties(syncHistoryContext));
    }

    public static SFG20SyncHistoryContext getSFGSyncHistoryForId(long syncId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSFG20SyncHistoryModule().getTableName())
                .select(FieldFactory.getSFG20SyncHistoryFields())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(syncId), NumberOperators.EQUALS));
        List<Map<String, Object>> records = selectRecordBuilder.get();
        if (records != null && !records.isEmpty()) {
            return FieldUtil.getAsBeanFromMap(records.get(0), SFG20SyncHistoryContext.class);
        }
        return null;
    }

    public static void updateSFGSchWithJobPlanId(long syncId,long scheduleId,long jobPlanId) throws Exception {
        Map<String, Object> record = new HashMap<>();
        record.put("jobPlanId",jobPlanId);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getSFG20JobPlanModule().getTableName())
                .fields(FieldFactory.getSFG20JobPlansFields())
                .andCondition(CriteriaAPI.getCondition("SYNC_ID", "syncId", String.valueOf(syncId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("SCHEDULE_ID", "scheduleId", String.valueOf(scheduleId), NumberOperators.EQUALS));
        updateRecordBuilder.update(record);

    }

    public static JobPlanContext getFacilioJobPlanForSFGData(SFGScheduleContext sfgData) throws Exception {
        Map<String, Object> props = new HashMap<>();
        SFG20Facilio mapping = new SFG20Facilio();
        ObjectMapper objectMapper = new ObjectMapper();
        props.put("jobPlanCategory", JobPlanContext.JPScopeAssignmentType.GENERAL.getVal());
        Map<String,Object> sfgDataMap = FieldUtil.getAsProperties(sfgData);
        props.putAll(getSFG20Mapping(mapping.getSfgJobPlan(),sfgDataMap));
        props.put("content",removeHTMLTagsAndNewlines(sfgData.getScheduleIntro().getContent()));
        props.put("name",sfgData.getCode()+" "+sfgData.getTitle());
        props.put("notes", removeHTMLTagsAndNewlines(sfgData.getScheduleIntro().getNotes()));
        props.put("formId", -1);
        props.put("isSfg", true);
        props.put("sfgType", JobPlanContext.SFGType.CORE.getVal());
        props.put("unitOfMeasure", sfgData.getUnitOfMeasure());
        String jsonLegislations = null;
        try {
            jsonLegislations = objectMapper.writeValueAsString(sfgData.getScheduleLegislations());
        } catch (Exception e) {
            jsonLegislations ="";
        }
        props.put("sfgLegislations", jsonLegislations);
        List<SFGScheduleContext.ScheduleTask> taskListDetails = sfgData.getScheduleTasks();
        List<JobPlanTaskSectionContext> taskSections =getFacilioTaskAndSectionForSFGData(taskListDetails,sfgData.getScheduleId());
        JobPlanContext jobPlanContext = FieldUtil.getAsBeanFromMap(props,JobPlanContext.class);
        jobPlanContext.setJobplansection(taskSections);
        return jobPlanContext;

    }


    public static List<JobPlanTaskSectionContext> getFacilioTaskAndSectionForSFGData( List<SFGScheduleContext.ScheduleTask> taskListDetails,int scheduleId) throws Exception {
        Map<String, Object> props = new HashMap<>();
        SFG20Facilio mapping = new SFG20Facilio();
        long sectionCount = 0;
        long taskCount = 0;
        List<JobPlanTaskSectionContext> sections = new ArrayList<>();
        JobPlanTaskSectionContext currentSection = null;
        for(SFGScheduleContext.ScheduleTask taskDetail : taskListDetails)
        {

            if(taskDetail.isIsHeader())
            {
                if(currentSection !=null)
                {
                    sections.add(currentSection);
                    taskCount=0;
                }
                Map<String, Object> section = new HashMap<>(getSFG20Mapping(mapping.getSfgJobPlanSection(),FieldUtil.getAsProperties(taskDetail)));
                section.put("sequenceNumber",++sectionCount);
                section.put("statusNew",V3TaskContext.TaskStatus.OPEN.getValue());
                section.put("jobPlanSectionCategory", PreventiveMaintenance.PMAssignmentType.CURRENT_ASSET.getVal());
                section.put("inputType", V3TaskContext.InputType.NONE.getVal());
                currentSection = FieldUtil.getAsBeanFromMap(section,JobPlanTaskSectionContext.class);
            }
            else{
                Map<String, Object> task = new HashMap<>(getSFG20Mapping(mapping.getSfgJobPlanTask(), FieldUtil.getAsProperties(taskDetail)));
                task.put("sequence",++taskCount);
                task.put("taskId",taskCount);
                task.put("inputType", V3TaskContext.InputType.NONE.getVal());
                task.put("jobPlanSectionCategory", PreventiveMaintenance.PMAssignmentType.CURRENT_ASSET.getVal());
                task.put("jobPlanTaskCategory", PreventiveMaintenance.PMAssignmentType.CURRENT_ASSET.getVal());
                task.put("statusNew",V3TaskContext.TaskStatus.OPEN.getValue());
                task.put("name","Section "+sectionCount);
                task.put("taskFrequency",getTaskFrequencyForSFG(taskDetail));
                task.put("taskCriticality",getTaskPriorityForSFG(taskDetail));
                task.put("sfgScheduleId",scheduleId);
                task.put("skillSet", V3TaskContext.SkillSet.fromString(taskDetail.getSkillSet()));
                task.put("description", removeHTMLTagsAndNewlines(taskDetail.getNotes()));
                task.put("actionContent", removeHTMLTagsAndNewlines(taskDetail.getAction()));
                JobPlanTasksContext tasksContext = FieldUtil.getAsBeanFromMap(task,JobPlanTasksContext.class);
                currentSection.addTasks(tasksContext);
           }
        }
        return sections;
    }

    public static List<JobPlanContext> getAllSFGTypeFacilioJobPlans() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField isSfg = fieldMap.get("isSfg");
        FacilioField jpStatus = fieldMap.get("jpStatus");
        List<Long> jobPlanActiveStatusList = new ArrayList<>();
        jobPlanActiveStatusList.add((long) JobPlanContext.JPStatus.ACTIVE.getVal());
        jobPlanActiveStatusList.add((long) JobPlanContext.JPStatus.IN_ACTIVE.getVal());
        SelectRecordsBuilder<JobPlanContext> selectBuilder = new SelectRecordsBuilder<JobPlanContext>()
                .select(fields)
                .table(module.getTableName())
                .moduleName(module.getName())
                .beanClass(JobPlanContext.class)
                .andCondition(CriteriaAPI.getCondition(isSfg, String.valueOf(true), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(jpStatus, jobPlanActiveStatusList, NumberOperators.EQUALS));

        return selectBuilder.get();
    }

    public static Map<Integer, JobPlanContext> getSFGTypeJPMap() throws Exception {
        List<JobPlanContext> jobPlanContextList = new ArrayList<>();
        jobPlanContextList= getAllSFGTypeFacilioJobPlans();
        Map<Integer,JobPlanContext> sfgJobPlanMap = new HashMap<>();
        for(JobPlanContext jobPlanContext : jobPlanContextList)
        {
            sfgJobPlanMap.put(jobPlanContext.getScheduleId(),jobPlanContext);
        }
        return sfgJobPlanMap;
    }

    public static int getTaskFrequencyForSFG(SFGScheduleContext.ScheduleTask task)
    {
        if(task.getFrequencyPeriod().equals("H"))
        {
            return V3TaskContext.TaskFrequency.HOURLY.getVal();
        }
        else if(task.getFrequencyPeriod().equals("D"))
        {
            return V3TaskContext.TaskFrequency.DAILY.getVal();
        }
        else if(task.getFrequencyPeriod().equals("W"))
        {
            return V3TaskContext.TaskFrequency.WEEKLY.getVal();
        }
        else if(task.getFrequencyPeriod().equals("M") && task.getFrequencyInterval()<3)
        {
            return V3TaskContext.TaskFrequency.MONTHLY.getVal();
        }
        else if(task.getFrequencyPeriod().equals("M") && task.getFrequencyInterval()==3)
        {
            return V3TaskContext.TaskFrequency.QUARTERLY.getVal();
        }
        else if(task.getFrequencyPeriod().equals("M") && task.getFrequencyInterval()==6)
        {
            return V3TaskContext.TaskFrequency.HALF_YEARLY.getVal();
        }
        else if((task.getFrequencyPeriod().equals("M") && task.getFrequencyInterval()==12) ||(task.getFrequencyPeriod().equals("Y")))
        {
            return V3TaskContext.TaskFrequency.ANNUALLY.getVal();
        }
         return -1;
    }

    public static int getTaskPriorityForSFG(SFGScheduleContext.ScheduleTask task)
    {
        if(task.getCriticality().equals("A"))
        {
            return V3TaskContext.TaskCriticality.OPTIMAL.getVal();
        }
        else if(task.getCriticality().equals("G"))
        {
            return V3TaskContext.TaskCriticality.DISCRETIONARY.getVal();
        }
        else if(task.getCriticality().equals("P"))
        {
            return V3TaskContext.TaskCriticality.MANDATORY.getVal();
        }
        else if(task.getCriticality().equals("R"))
        {
            return V3TaskContext.TaskCriticality.STATUTORY.getVal();
        }

        return -1;
    }
    public static String removeHTMLTagsAndNewlines(String input) {

        Pattern pattern = Pattern.compile("<[^>]*>|\\n");


        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("");
    }
}
