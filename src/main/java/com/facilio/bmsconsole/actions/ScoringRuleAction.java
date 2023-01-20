package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.scoringrule.ScoringRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringSystemEnumOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.actions.QAndARuleAction;
import com.facilio.qa.rules.commands.QAndARuleTransactionChainFactory;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ScoringRuleAction extends FacilioAction {

    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private ScoringRuleContext scoringRule;
    public ScoringRuleContext getScoringRule() {
        return scoringRule;
    }
    public void setScoringRule(ScoringRuleContext scoringRule) {
        this.scoringRule = scoringRule;
    }

    private List<Map<String, Object>> scoringContexts;
    public List<Map<String, Object>> getScoringContexts() {
        return scoringContexts;
    }
    public void setScoringContexts(List<Map<String, Object>> scoringContexts) {
        this.scoringContexts = scoringContexts;
    }

    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getScoringRuleListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();
        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));
        return SUCCESS;
    }

    public String viewScoringRule() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getScoringRuleDetailsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RULE_ID, ruleId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE));
        return SUCCESS;
    }

    public String addOrUpdateScoringRule() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateScoringRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, scoringRule);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SCORING_CONTEXT_LIST, scoringContexts);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE));

        return SUCCESS;
    }

    private long ruleId = -1;
    public long getRuleId() {
        return ruleId;
    }
    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    public String deleteScoringRule() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDelWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RULE_ID, ruleId);
        chain.execute();

        return SUCCESS;
    }

    private String tableName;
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String addScoreSubModule() throws Exception {
        FacilioChain chain = TransactionChainFactory.addScoreSubModuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.TABLE_NAME, tableName);
        chain.execute();
        return SUCCESS;
    }
}
