package com.facilio.bmsconsole.ModuleSettingConfig.impl;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PreCommitWorkflowRuleUtil {
    public static Map<Long, List<WorkflowRuleContext>>  getModulesForPreCommitRule(long orgId,String[] isPrecommit) throws Exception {

        AccountUtil.setCurrentAccount(orgId);
        if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == orgId) {
            if(isPrecommit==null  || isPrecommit.length<=0)
                return null;
            FacilioChain chain = ReadOnlyChainFactory.getAutomationModules();
            FacilioContext context = chain.getContext();
            chain.execute();

            List<FacilioModule> moduleList=(List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
            List<Long> moduleIdsList=  moduleList.stream().map(i->i.getModuleId()).collect(Collectors.toList());
            List<WorkflowRuleContext>ruleList= getPrecommitWorkflowRulesForModules(orgId,moduleIdsList,isPrecommit[0]);
            if(CollectionUtils.isEmpty(ruleList)) return null;
            Map<Long, List<WorkflowRuleContext>> moduleIdVsRule = ruleList.stream().collect(Collectors.groupingBy((WorkflowRuleContext workflowRuleContext) -> {
                try {
                    return workflowRuleContext.getModuleId();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            },
            HashMap::new,
            Collectors.toCollection(ArrayList::new)));
            return  moduleIdVsRule;
        }
        return null;
    }

    public static List<WorkflowRuleContext> getPrecommitWorkflowRulesForModules(long orgId,List<Long> moduleIdsList,String isPrecommit) throws Exception {
        AccountUtil.setCurrentAccount(orgId);
        if(AccountUtil.getCurrentOrg() == null && AccountUtil.getCurrentOrg().getOrgId() != orgId) {
            return null;
        }

        FacilioModule module = ModuleFactory.getWorkflowRuleModule();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
        GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getWorkflowRuleFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("preCommit"),isPrecommit, BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), moduleIdsList, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleType"), String.valueOf(WorkflowRuleContext.RuleType.MODULE_RULE.getIntVal()), NumberOperators.EQUALS));
        return WorkflowRuleAPI.getWorkFlowsFromMapList(ruleBuilder.get(), true, true);
    }
    public static List<WorkflowRuleContext> getRuleList(String[] selectedModules,List<WorkflowRuleContext> ruleList,Map<Long, List<WorkflowRuleContext>>moduleIdVsRule){
        if( selectedModules==null || moduleIdVsRule==null ) {
            return null;
        }
            List<Long> selectedModuleIdList = Stream.of(selectedModules).map(Long::valueOf).collect(Collectors.toList());
            if(selectedModuleIdList != null) {
                for(Long ids:selectedModuleIdList){
                    if(moduleIdVsRule.containsKey(ids)) {
                        if (ruleList == null || ruleList.isEmpty()) ruleList = moduleIdVsRule.get(ids);
                        else ruleList.addAll(moduleIdVsRule.get(ids));
                    }
                }
                    return ruleList;
            }

        return null;
    }
    public static void updateWorkflowRulesToPreCommit(List<Long> ruleIdsList,boolean isPrecommit) throws Exception {
        FacilioModule module = ModuleFactory.getWorkflowRuleModule();
        Map<String,Object> updateMap =new HashMap<>();
        updateMap.put("preCommit",isPrecommit);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
        GenericUpdateRecordBuilder updataeBuilder=new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(Collections.singletonList(fieldMap.get("preCommit")))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), ruleIdsList, NumberOperators.EQUALS));
        updataeBuilder.update(updateMap);
    }
}
