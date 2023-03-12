package com.facilio.qa.command;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.context.DisplayLogicTriggerQuestions;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SetIsDisplayLogicPresentForQuestion extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PageContext> pages = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(pages)) {
            Map<Long, QuestionContext> questions = pages.stream().flatMap(QAndAUtil::getQuestionStream).collect(Collectors.toMap(QuestionContext::getId, Function.identity()));
            if(MapUtils.isNotEmpty(questions)) {
                List<Long> questionIds = new ArrayList<>(questions.keySet());
                List<DisplayLogicTriggerQuestions> triggerQuestions = DisplayLogicUtil.fetchDisplayLogicTriggerQuestion(questionIds);
                Set<Long> questionIdsWithDisplayLogics = triggerQuestions.stream().map(DisplayLogicTriggerQuestions::getTriggerQuestionId).collect(Collectors.toSet());

                questions.forEach((questionId,questionContext)->{
                    if(questionIdsWithDisplayLogics.contains(questionId)){
                        questionContext.setIsDisplayLogic(true);
                    } else {
                        questionContext.setIsDisplayLogic(false);
                    }
                });
            }
        }
        return false;
    }
}
