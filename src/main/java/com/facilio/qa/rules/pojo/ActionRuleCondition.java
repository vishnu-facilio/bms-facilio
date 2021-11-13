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
import com.facilio.qa.rules.commands.ExecuteQAndARulesCommand;
import com.facilio.qa.rules.commands.ExecuteQAndAScoringRules;
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

        if (hasAction()) {
            long startTime = System.currentTimeMillis();
            Map<String, Object> placeHolders = new HashMap<>();
            CommonCommandUtil.appendModuleNameInKey(null, "question", FieldUtil.getAsProperties("question"), placeHolders);
            CommonCommandUtil.appendModuleNameInKey(null, "answer", FieldUtil.getAsProperties("answer"), placeHolders);
            placeHolders.put("question", question.getQuestion());
            placeHolders.put("answer", answer.getClientAnswerContext());
            LOGGER.debug("Time taken to fetch actions for Q_and_A_rule id : " + getRuleId() + " with actions : " + actions + " is " + (System.currentTimeMillis() - startTime));
            for (ActionContext action : actions) {
                action.executeAction(placeHolders, null, null, null);
            }
        }
    }
}
