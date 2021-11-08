package com.facilio.qa.rules.pojo;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Log4j
public class ActionRuleCondition extends RuleCondition {
    private List<ActionContext> actions;

    @Override
    public boolean hasAction() {
        return CollectionUtils.isNotEmpty(actions);
    }

    @Override
    public void executeTrueAction(QuestionContext question, AnswerContext answer) throws Exception {

        long startTime = System.currentTimeMillis();
        if (actions == null) {
            actions = getActiveActionsFromWorkflowRule(getRuleId());
            Map<String, Object> placeHolders = new HashMap<>();
            CommonCommandUtil.appendModuleNameInKey(null, "rule", FieldUtil.getAsProperties(this),placeHolders);
            placeHolders.put("question",question.getQuestion());
            placeHolders.put("answer",answer.getResponse());
            LOGGER.debug("Time taken to fetch actions for Q_and_A_rule id : " + getRuleId() + " with actions : " + actions + " is " + (System.currentTimeMillis() - startTime));
            if (actions != null) {
                for (ActionContext action : actions) {
//                    action.executeAction(placeHolders, context, this, record);
                }
            }
        }
    }

    private static List<ActionContext> getActiveActionsFromWorkflowRule ( long ruleId) throws Exception {
        FacilioModule module = ModuleFactory.getActionModule();
        GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getActionFields())
                .table(module.getTableName())
                .innerJoin("Eval_Rule_Action_Rel")
                .on("Action.ID = Eval_Rule_Action_Rel.ACTION_ID")
                .andCustomWhere("Eval_Rule_Action_Rel.RULE_ID = ? AND Action.STATUS = ?", ruleId, true);
        return ActionAPI.getActionsFromPropList(actionBuilder.get());
    }
}
