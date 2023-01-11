package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.kafka.common.protocol.types.Field;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class DeleteAndCreateResourcePlanner extends FacilioCommand {
    public static int iterator = 0;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        Integer setting = importProcessContext.getImportSetting();
        List<Map<String, Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());
        if(setting == 1 || setting == 2){
            return false;
        }
        List<PMPlanner> pmPlannerList = deletePmResourcePlanner(allRows);
        for (Map<String, Object> row : allRows) {
            String rowContextString = String.valueOf(row.get("rowContextString"));
            JSONArray jsonArr = new JSONArray(rowContextString);

            for (int i = 0; i < jsonArr.length(); i++) {
                PMResourcePlanner pmResourcePlannerObject = new PMResourcePlanner();
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                JSONObject colval = new JSONObject();
                if (jsonObject.length() < 1) {
                    return false;
                } else if (jsonObject.isNull("colVal")) {
                    return false;
                } else {
                    colval = jsonObject.getJSONObject("colVal");
                    String plannerName = colval.getString("Planner");
                    Long pmId = colval.getLong("PmId");
                    if(plannerName != null && pmId != null ){
                        if(!colval.isNull("Staff")){
                            pmResourcePlannerObject.setAssignedTo(AccountUtil.getUserBean().getUserFromEmail(colval.getString("Staff"),null,AccountUtil.getCurrentOrg().getOrgId()));
                        }
                        if(!colval.isNull("Job Plan")){
                            pmResourcePlannerObject.setJobPlan(getJobPlanFromName(colval.getString("Job Plan")));
                        }
                        if(!colval.isNull("Resource")){
                            pmResourcePlannerObject.setResource(getResourceFromName(colval.getString("Resource")));
                        }
                        if(!colval.isNull("FormId")){
                            pmResourcePlannerObject.setFormId(colval.getLong("FormId"));
                        }
                        pmResourcePlannerObject.setPlanner(pmPlannerList.get(iterator));
                        iterator++;
                        pmResourcePlannerObject.setPmId(pmId);

                        List<ModuleBaseWithCustomFields> createTriggerRecords = new ArrayList<>();
                        createTriggerRecords.add(pmResourcePlannerObject);

                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        FacilioModule resourcePlannerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);

                        FacilioContext facilioContext = V3Util.createRecord(resourcePlannerModule, createTriggerRecords);

                    }

                }
            }
        }
        return false;
    }
    public static List<PMPlanner> deletePmResourcePlanner(List<Map<String, Object>> allRows) throws Exception{
        List<Long> pmResourcePlannerIdList = new ArrayList<>();
        List<PMPlanner> pmPlannerList = new ArrayList<>();
        for (Map<String, Object> row : allRows) {
            String rowContextString = String.valueOf(row.get("rowContextString"));
            JSONArray jsonArr = new JSONArray(rowContextString);
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                JSONObject colval = jsonObject.getJSONObject("colVal");
                if (colval != null) {
                    String plannerName = colval.getString("Planner");
                    Long pmId = colval.getLong("PmId");
                    if (plannerName != null && pmId != null) {
                        pmPlannerList.add(PlannedMaintenanceAPI.getPlanner(pmId, plannerName));
                        Long plannerId = pmPlannerList.get(pmPlannerList.size() - 1).getId();
                        List<PMResourcePlanner> pmResourcePlannerList = PlannedMaintenanceAPI.getResourcePlanners(plannerId);
                        for (PMResourcePlanner pmResourcePlanner : pmResourcePlannerList) {
                            pmResourcePlannerIdList.add(pmResourcePlanner.getId());
                        }
                    }
                }
            }
        }
        V3RecordAPI.deleteRecordsById(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER, pmResourcePlannerIdList);
        return pmPlannerList;
    }

    public static JobPlanContext getJobPlanFromName(String jobPlanName) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule jobPlanModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        List<FacilioField> jobPlanFields = modBean.getAllFields(FacilioConstants.ContextNames.JOB_PLAN);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(jobPlanFields);

        SelectRecordsBuilder<JobPlanContext> selectRecordsBuilder = new SelectRecordsBuilder<JobPlanContext>()
                .module(jobPlanModule)
                .select(jobPlanFields)
                .beanClass(JobPlanContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"),jobPlanName, StringOperators.IS));

        return selectRecordsBuilder.fetchFirst();
    }
    public static V3ResourceContext getResourceFromName(String resourceName) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        List<FacilioField> resourceFields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
        Map<String, FacilioField>  fieldMap = FieldFactory.getAsMap(resourceFields);

        SelectRecordsBuilder<V3ResourceContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ResourceContext>()
                .module(resourceModule)
                .select(resourceFields)
                .beanClass(V3ResourceContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"),resourceName,StringOperators.IS));

        return selectRecordsBuilder.fetchFirst();
    }
}

