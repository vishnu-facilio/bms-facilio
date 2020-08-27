package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.scoringrule.BaseScoringContext;
import com.facilio.bmsconsole.scoringrule.ConditionScoringContext;
import com.facilio.bmsconsole.scoringrule.ScoringRuleContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EvaluateScoringRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        ScoringRuleContext ruleContext = new ScoringRuleContext();
        List<BaseScoringContext> baseScoringList = new ArrayList<>();
        ConditionScoringContext scoringContext = new ConditionScoringContext();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("PRIORITY_ID", "priority", String.valueOf(1), PickListOperators.IS));
        scoringContext.setCriteria(criteria);
        scoringContext.setWeightage(40f);
        baseScoringList.add(scoringContext);

        scoringContext = new ConditionScoringContext();
        criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("CATEGORY_ID", "category", String.valueOf(1), PickListOperators.IS));
        scoringContext.setCriteria(criteria);
        scoringContext.setWeightage(60f);
        baseScoringList.add(scoringContext);

        ruleContext.setBaseScoringContexts(baseScoringList);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("workorder");
        SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
                .module(module)
                .select(modBean.getAllFields(module.getName()))
                .beanClass(WorkOrderContext.class)
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        WorkOrderContext record = builder.fetchFirst();

        Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
        Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders("workorder", record, placeHolders);
        WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(ruleContext, "workorder", record, null, recordPlaceHolders, (FacilioContext) context);

        return false;
    }
}
