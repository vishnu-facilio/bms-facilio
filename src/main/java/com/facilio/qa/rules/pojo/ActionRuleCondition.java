package com.facilio.qa.rules.pojo;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.workflowlog.context.WorkflowLogContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ActionRuleCondition extends RuleCondition {
    private List<ActionContext> actions;

    @Override
    public boolean hasAction() {
        return CollectionUtils.isNotEmpty(actions);
    }

    @Override
    public void executeTrueAction(QuestionContext question, AnswerContext answer) throws Exception {

        if (hasAction()) {
            Map<String, Object> placeHolders = new HashMap<>();
            CommonCommandUtil.appendModuleNameInKey(null, "question", FieldUtil.getAsProperties(question), placeHolders);
            CommonCommandUtil.appendModuleNameInKey(null, "answer", FieldUtil.getAsProperties(answer), placeHolders);
            placeHolders.put("question", question.getQuestion());
            placeHolders.put("answer", answer.getAnswerContext());

			WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext(); // need to change actions doesn't depend on workflowrule.
			workflowRuleContext.setId(getRuleId());
			workflowRuleContext.setParentId(getRuleId());

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.Workflow.WORKFLOW_LOG_PARENT_TYPE, WorkflowLogContext.WorkflowLogType.Q_AND_A_RULE);

            for (ActionContext action : actions) {
                action.executeAction(placeHolders, context, workflowRuleContext, null);
            }
        }
    }
}
