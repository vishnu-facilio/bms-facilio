package com.facilio.qa.command;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchRelatedRecordsFormQandAQuestionsCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		List<AnswerContext> answers = Constants.getRecordList((FacilioContext) context);
		if (CollectionUtils.isNotEmpty(answers)) {
			Map<Long, QuestionContext> questionMap = fetchQuestions(answers);
			for(AnswerContext answer : answers) {
				QuestionContext question = answer.getQuestion();

				question = questionMap.get(question.getId());

				question.getQuestionType().getAnswerHandler().populateRelatedRecordsForAnswer(answer);

			}
		}
		return false;
	}

	private Map<Long, QuestionContext> fetchQuestions (Collection<AnswerContext> answers) throws Exception {
		List<Long> questionIds = answers.stream().map(a -> a.getQuestion().getId()).collect(Collectors.toList());
		Map<Long, QuestionContext> questions = QAndAUtil.fetchExtendedQuestionMap(questionIds, true);
		return questions;
	}
}
