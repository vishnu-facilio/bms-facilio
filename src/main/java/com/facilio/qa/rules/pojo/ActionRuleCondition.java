package com.facilio.qa.rules.pojo;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.modules.FieldUtil;
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
            for (ActionContext action : actions) {
                action.executeAction(placeHolders, null, null, null);
            }
        }
    }
}
