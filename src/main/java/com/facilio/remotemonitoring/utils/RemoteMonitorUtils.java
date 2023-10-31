package com.facilio.remotemonitoring.utils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.controller.Controller;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringSystemEnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RemoteMonitorUtils {
    //    public static FlaggedEventRuleClosureConfigContext getFlaggedEventRuleClosureConfig(long flaggedEventRuleId) throws Exception{
//        if(flaggedEventRuleId >0){
//            List<SupplementRecord> supplementRecords = new ArrayList<>();
//            supplementRecords.add();
//            Criteria criteria = new Criteria();
//            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE_ID","flaggedEventRule",String.valueOf(flaggedEventRuleId),NumberOperators.EQUALS));
//            List<FlaggedEventRuleClosureConfigContext> list = V3RecordAPI.getRecordsListWithSupplements(AddFlaggedEventClosureConfigModule.MODULE_NAME,null,FlaggedEventRuleClosureConfigContext.class,criteria,null);
//            if(CollectionUtils.isNotEmpty(list)) {
//                return list.get(0);
//            }
//        }
//        return null;
//    }
    public static List<FilterRuleCriteriaContext> fetchFilterRuleCriteriaForFilterRule(long filterRuleId) throws Exception {
        if (filterRuleId > 0) {
            ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule alarmFilterRuleCriteriaModule = modbean.getModule(AlarmFilterRuleCriteriaModule.MODULE_NAME);
            List<FacilioField> alarmFilterRuleCriteriaModuleFields = modbean.getAllFields(AlarmFilterRuleCriteriaModule.MODULE_NAME);
            SelectRecordsBuilder<FilterRuleCriteriaContext> selectBaseline = new SelectRecordsBuilder<FilterRuleCriteriaContext>()
                    .module(alarmFilterRuleCriteriaModule)
                    .select(alarmFilterRuleCriteriaModuleFields)
                    .beanClass(FilterRuleCriteriaContext.class)
                    .andCondition(CriteriaAPI.getCondition("ALARM_FILTER_RULE_ID", "alarmFilterRule", String.valueOf(filterRuleId), NumberOperators.EQUALS));
            List<FilterRuleCriteriaContext> alarmFilterRuleCriteriaList = selectBaseline.get();
            return alarmFilterRuleCriteriaList;
        }
        return new ArrayList<>();
    }

    public static int deleteExistingFilterRuleCriteriaForFilterRule(long filterRuleId) throws Exception {
        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule alarmFilterRuleCriteriaModule = modbean.getModule(AlarmFilterRuleCriteriaModule.MODULE_NAME);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(alarmFilterRuleCriteriaModule.getTableName())
                .select(Collections.singletonList(FieldFactory.getIdField(alarmFilterRuleCriteriaModule)))
                .andCondition(CriteriaAPI.getCondition("ALARM_FILTER_RULE_ID", "alarmFilterRule", String.valueOf(filterRuleId), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();
        List<Long> recordIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> map : props) {
                Object id = map.get("id");
                if (id instanceof Number) {
                    recordIds.add(((Number) id).longValue());
                }
            }
        }
        if(!recordIds.isEmpty()){
            Map<String, Object> deleteObj = new HashMap<>();
            deleteObj.put(alarmFilterRuleCriteriaModule.getName(), recordIds);
            FacilioContext context = V3Util.deleteRecords(alarmFilterRuleCriteriaModule.getName(), deleteObj, null, null, false);
            Map<String, Integer> countMap = Constants.getCountMap(context);
            if (countMap.containsKey(alarmFilterRuleCriteriaModule.getName())) {
                return countMap.get(alarmFilterRuleCriteriaModule.getName());
            }
        }
        return 0;
    }
    public static List<FlaggedEventRuleBureauEvaluationContext> getFlaggedEventBureauEval(Long parentId) throws Exception {
        return getFlaggedEventBureauEval(parentId,false);
    }
    public static List<FlaggedEventRuleBureauEvaluationContext> getFlaggedEventBureauEval(Long parentId,boolean skipMeta) throws Exception {
        if(parentId != null && parentId > -1) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE","flaggedEventRule",String.valueOf(parentId),NumberOperators.EQUALS));
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<SupplementRecord> supplementRecord = new ArrayList<>();
            supplementRecord.add((SupplementRecord) modBean.getField("troubleShootingTips", FlaggedEventBureauEvaluationModule.MODULE_NAME));
            List<FlaggedEventRuleBureauEvaluationContext> flaggedEventRuleBureauEvaluationList = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventBureauEvaluationModule.MODULE_NAME, null, FlaggedEventRuleBureauEvaluationContext.class, criteria,supplementRecord);
            if(CollectionUtils.isNotEmpty(flaggedEventRuleBureauEvaluationList)){
               for(FlaggedEventRuleBureauEvaluationContext flaggedEventRuleBureauEvaluation : flaggedEventRuleBureauEvaluationList) {
                   Long bureauEvaluationId = flaggedEventRuleBureauEvaluation.getId();
                   Long emailRuleId = flaggedEventRuleBureauEvaluation.getEmailRuleId();
                   if (emailRuleId != null && emailRuleId > 0 && !skipMeta) {
                       WorkflowRuleContext emailRule = new WorkflowRuleContext();
                       List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(emailRuleId);
                       emailRule.setActions(actions);
                       flaggedEventRuleBureauEvaluation.setEmailRule(emailRule);
                   }
                   flaggedEventRuleBureauEvaluation.setInhibitReasonList(getInhibitReasonList(bureauEvaluationId));
                   flaggedEventRuleBureauEvaluation.setCloseIssueReasonOptionContexts(getCloseIssueReasonOptionContexts(bureauEvaluationId));
                   flaggedEventRuleBureauEvaluation.setCauseList(getBureauCauseList(bureauEvaluationId));
                   flaggedEventRuleBureauEvaluation.setResolutionList(getBureauResolutionList(bureauEvaluationId));
               }
           }
           return flaggedEventRuleBureauEvaluationList;
        }
        return null;
    }

    public static List<FlaggedEventWorkorderFieldMappingContext> getFlaggedEventRuleWOFieldMapping(Long parentId) throws Exception {
        if(parentId != null && parentId > -1) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getFlaggedEventWorkorderTemplateFieldMappingField())
                    .table(ModuleFactory.getflaggedEventWorkorderTemplateFieldMappingModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId",String.valueOf(parentId), NumberOperators.EQUALS));
            List<Map<String,Object>> propsList = selectRecordBuilder.get();
            if(CollectionUtils.isNotEmpty(propsList)) {
                List<FlaggedEventWorkorderFieldMappingContext> fieldMappings = FieldUtil.getAsBeanListFromMapList(propsList,FlaggedEventWorkorderFieldMappingContext.class);
                for(FlaggedEventWorkorderFieldMappingContext fieldMapping : fieldMappings){
                    FacilioField facilioField = modBean.getField(fieldMapping.getLeftFieldId(), ModuleFactory.getWorkOrdersModule().getName());
                    fieldMapping.setLeftFieldName(facilioField.getName());
                }
                return fieldMappings;
            }
        }
        return null;
    }

    public static List<FlaggedEventRuleAlarmTypeRel> getFlaggedEventRuleAlarmTypeRel(Long parentId) throws Exception{
        if(parentId != null && parentId > -1) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria flaggedEventAlarmTypeRelCriteria = new Criteria();
            flaggedEventAlarmTypeRelCriteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE","flaggedEventRule",String.valueOf(parentId), NumberOperators.EQUALS));
            List<SupplementRecord> supplementRecord = new ArrayList<>();
            supplementRecord.add((SupplementRecord) modBean.getField("alarmType", FlaggedEventAlarmTypeRelModule.MODULE_NAME));
            List<FlaggedEventRuleAlarmTypeRel> flaggedEventRuleAlarmTypeRels = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventAlarmTypeRelModule.MODULE_NAME, null, FlaggedEventRuleAlarmTypeRel.class, flaggedEventAlarmTypeRelCriteria, supplementRecord);
            return flaggedEventRuleAlarmTypeRels;
        }
        return null;
    }

    public static List<BureauInhibitReasonListContext> getInhibitReasonList(Long parentId) throws Exception {
        if(parentId != null && parentId > -1) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria bureauEvaluationFilterCriteria = new Criteria();
            bureauEvaluationFilterCriteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_BUREAU_EVALUATION","flaggedEventBureauEvaluation",String.valueOf(parentId), NumberOperators.EQUALS));
            List<BureauInhibitReasonListContext> inhibitReasonList = V3RecordAPI.getRecordsListWithSupplements(BureauInhibitReasonListModule.MODULE_NAME,null, BureauInhibitReasonListContext.class,bureauEvaluationFilterCriteria,null);
            return inhibitReasonList;
        }
        return null;
    }

    public static List<BureauCloseIssueReasonOptionContext> getCloseIssueReasonOptionContexts(Long parentId) throws Exception{
        if(parentId != null && parentId > -1) {
            Criteria bureauEvaluationFilterCriteria = new Criteria();
            bureauEvaluationFilterCriteria.addAndCondition(CriteriaAPI.getCondition("BUREAU_EVALUATION_ID","bureauEvaluationId",String.valueOf(parentId),NumberOperators.EQUALS));
            List<BureauCloseIssueReasonOptionContext> closeIssueReasonOptionContexts = V3RecordAPI.getRecordsListWithSupplements(BureauCloseIssueReasonOptionListModule.MODULE_NAME,null, BureauCloseIssueReasonOptionContext.class,bureauEvaluationFilterCriteria,null);
            return closeIssueReasonOptionContexts;
        }
        return null;
    }

    public static List<BureauCauseListContext> getBureauCauseList(Long parentId) throws Exception{
        if(parentId != null && parentId > -1) {
            Criteria bureauEvaluationFilterCriteria = new Criteria();
            bureauEvaluationFilterCriteria.addAndCondition(CriteriaAPI.getCondition("BUREAU_EVALUATION_ID","bureauEvaluationId",String.valueOf(parentId),NumberOperators.EQUALS));
            List<BureauCauseListContext> causeList =  V3RecordAPI.getRecordsListWithSupplements(BureauCauseListModule.MODULE_NAME,null, BureauCauseListContext.class,bureauEvaluationFilterCriteria,null);
            return causeList;
        }
        return null;
    }

    public static List<BureauResolutionListContext> getBureauResolutionList(Long parentId) throws Exception{
        if(parentId != null && parentId > -1) {
            Criteria bureauEvaluationFilterCriteria = new Criteria();
            bureauEvaluationFilterCriteria.addAndCondition(CriteriaAPI.getCondition("BUREAU_EVALUATION_ID","bureauEvaluationId",String.valueOf(parentId),NumberOperators.EQUALS));
            List<BureauResolutionListContext> resolutionList = V3RecordAPI.getRecordsListWithSupplements(BureauResolutionListModule.MODULE_NAME,null, BureauResolutionListContext.class,bureauEvaluationFilterCriteria,null);
            return resolutionList;
        }
        return null;
    }

    public static WorkflowRuleContext getEmailRule(Long ruleId) throws Exception{
        if(ruleId != null && ruleId > -1) {
            if(FacilioProperties.getService() != null && FacilioProperties.getService().equals("facilio")) {
                WorkflowRuleContext delayedEmailRule = new WorkflowRuleContext();
                List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(ruleId);
                delayedEmailRule.setActions(actions);
                return delayedEmailRule;
            }
        }
        return null;
    }

    public static FlaggedEventRuleClosureConfigContext getFlaggedEventRuleClosureConfig(Long parentId) throws Exception{
        if(parentId != null && parentId > -1) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria flaggedEvenRuleCriteria = new Criteria();
            flaggedEvenRuleCriteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE_ID","flaggedEventRule",String.valueOf(parentId), NumberOperators.EQUALS));
            List<SupplementRecord> supplementRecord = new ArrayList<>();
            supplementRecord.add((SupplementRecord) modBean.getField("workorderStatuses", AddFlaggedEventClosureConfigModule.MODULE_NAME));
            supplementRecord.add((SupplementRecord) modBean.getField("workorderCloseCommandCriteria", AddFlaggedEventClosureConfigModule.MODULE_NAME));
            supplementRecord.add((SupplementRecord) modBean.getField("warningMessage", AddFlaggedEventClosureConfigModule.MODULE_NAME));
            List<FlaggedEventRuleClosureConfigContext> flaggedEventRuleClosureConfigList = V3RecordAPI.getRecordsListWithSupplements(AddFlaggedEventClosureConfigModule.MODULE_NAME,null,FlaggedEventRuleClosureConfigContext.class,flaggedEvenRuleCriteria,supplementRecord);
            if(CollectionUtils.isNotEmpty(flaggedEventRuleClosureConfigList)){
                FlaggedEventRuleClosureConfigContext flaggedEventRuleClosureConfig = flaggedEventRuleClosureConfigList.get(0);
                flaggedEventRuleClosureConfig.setFlaggedEventStatuses(FlaggedEventUtil.getClosureFlaggedEventStatus(flaggedEventRuleClosureConfig.getId()));
                flaggedEventRuleClosureConfig.setCloseEmailRule(getEmailRule(flaggedEventRuleClosureConfig.getCloseEmailNotificationRuleId()));
                return  flaggedEventRuleClosureConfig;
            }
        }
        return null;
    }

    public static List<RelationContext> getAssetRelationForAssetCategory(Long assetCategoryModuleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, FacilioField> relationFields = FieldFactory.getAsMap(FieldFactory.getRelationFields());
        Map<String, FacilioField> relationMappingFields = FieldFactory.getAsMap(FieldFactory.getRelationMappingFields());
        FacilioModule assetCategoryModule = modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY);
        Map<String, FacilioField> assetCategoryFields = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY));
        String allowedRelationTypes = String.join(",", String.valueOf(RelationRequestContext.RelationType.MANY_TO_ONE.getIndex()), String.valueOf(RelationRequestContext.RelationType.ONE_TO_ONE.getIndex()));
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);

        SelectRecordsBuilder<V3AssetCategoryContext> assetCategories = new SelectRecordsBuilder<V3AssetCategoryContext>()
                .select(Arrays.asList(FieldFactory.getIdField(assetCategoryModule),modBean.getField("assetModuleId",assetCategoryModule.getName())))
                .module(assetCategoryModule)
                .beanClass(V3AssetCategoryContext.class);
        List<V3AssetCategoryContext> assetCategoryList = assetCategories.get();
        if(CollectionUtils.isNotEmpty(assetCategoryList)) {
            List<Long> assetModuleIds = assetCategoryList.stream().map(V3AssetCategoryContext::getAssetModuleID).collect(Collectors.toList());
            if(assetModuleIds == null) {
                assetModuleIds = new ArrayList<>();
            }
            assetModuleIds.add(assetModule.getModuleId());
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getRelationFields())
                    .table(ModuleFactory.getRelationModule().getTableName())
                    .innerJoin(ModuleFactory.getRelationMappingModule().getTableName())
                    .on(relationFields.get("id").getCompleteColumnName() + " = " + relationMappingFields.get("relationId").getCompleteColumnName())
                    .andCondition(CriteriaAPI.getCondition(relationMappingFields.get("fromModuleId"), String.valueOf(assetCategoryModuleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(relationMappingFields.get("toModuleId"), StringUtils.join(assetModuleIds,","), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(relationMappingFields.get("relationType"), allowedRelationTypes, NumberOperators.EQUALS));

            return FieldUtil.getAsBeanListFromMapList(selectRecordBuilder.get(), RelationContext.class);

        }


        return null;
    }

    public static Controller getControllerForAlarm(RawAlarmContext alarm) throws Exception {
        if(alarm != null) {
            if(alarm.getController() != null &&  alarm.getController().getControllerType() == ControllerType.LOGICAL_CONTROLLER.getKey()) {
                return getLogicalController();
            }
            else if(alarm.getSourceType() != null && alarm.getSourceType() == RawAlarmContext.RawAlarmSourceType.ROLLUP) {
                return getLogicalController();
            } else if(alarm.getController() != null && alarm.getController().getId() > -1) {
                return V3RecordAPI.getRecord(FacilioConstants.ContextNames.CONTROLLER, alarm.getController().getId(), Controller.class);
            }
        }
        return null;
    }
    public static Controller getLogicalController() {
        Controller logicalController = new Controller();
        logicalController.setName("System Controller");
        logicalController.setControllerType(ControllerType.LOGICAL_CONTROLLER.getKey());
        return logicalController;
    }

    public static String getExecutorName(String executorName) {
        if(FacilioProperties.getService() != null && FacilioProperties.getService().equalsIgnoreCase("storm")) {
            return FacilioProperties.isDevelopment() ? executorName : "pre-" + executorName;
        }
        return executorName;
    }

    public static void computeAndAddAlarmNotReceivedJob() throws Exception {
        Long orgId = AccountUtil.getCurrentOrg().getId();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_FILTER_CRITERIA_TYPE","alarmFilterCriteriaType",AlarmFilterCriteriaType.NO_ALARM_RECEIVED_FOR_SPECIFIC_PERIOD.getIndex(), StringSystemEnumOperators.IS));
        List<FilterRuleCriteriaContext> filterRuleCriteriaList = V3RecordAPI.getRecordsListWithSupplements(AlarmFilterRuleCriteriaModule.MODULE_NAME, null, FilterRuleCriteriaContext.class, criteria, null);
        if(CollectionUtils.isNotEmpty(filterRuleCriteriaList)) {
            JobContext job = FacilioTimer.getJob(orgId, RemoteMonitorConstants.ALARM_NOT_RECEIVED_JOB);
            if(job == null) {
                FacilioTimer.schedulePeriodicJob(orgId, RemoteMonitorConstants.ALARM_NOT_RECEIVED_JOB, 60,60, "priority");
            }
        } else {
            FacilioTimer.deleteJob(orgId, RemoteMonitorConstants.ALARM_NOT_RECEIVED_JOB);
        }
    }
}