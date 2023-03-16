package com.facilio.qa.context.client.answers.handler;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.MCQMultiAnswerContext;
import com.facilio.qa.context.questions.MCQMultiContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.qa.rules.pojo.ScoringRule;
import com.facilio.qa.rules.pojo.ScoringRuleCondition;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MCQMultiAnswerHandler extends AnswerHandler<MCQMultiAnswerContext> {
    public MCQMultiAnswerHandler(Class<MCQMultiAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public MCQMultiAnswerContext serialize(AnswerContext answer) {
        MCQMultiAnswerContext mcqMultiAnswer = new MCQMultiAnswerContext();
        MCQMultiAnswerContext.MCQMultiAnswer multiAnswer = new MCQMultiAnswerContext.MCQMultiAnswer();
        multiAnswer.setSelected(answer.getMultiEnumAnswer().stream()
                                    .map(MCQOptionContext::getId)
                                    .collect(Collectors.toSet()));
        if ( StringUtils.isNotEmpty(answer.getEnumOtherAnswer()) )  { // Assumption is we'll validate option before adding and so not doing any check here
            multiAnswer.setOther(answer.getEnumOtherAnswer());
        }
        mcqMultiAnswer.setAnswer(multiAnswer);

        return mcqMultiAnswer;
    }

    @Override
    public AnswerContext deSerialize(MCQMultiAnswerContext answer, QuestionContext question) throws Exception {
        MCQMultiContext mcqQuestion = (MCQMultiContext) question;
        Set<Long> selected = answer.getAnswer().getSelected();
        V3Util.throwRestException(CollectionUtils.isEmpty(selected), ErrorCode.VALIDATION_ERROR, "errors.qa.mcqMultiAnswerHandler.optionSelectionCheck",true,null);
        //V3Util.throwRestException(CollectionUtils.isEmpty(selected), ErrorCode.VALIDATION_ERROR, "At least one option need to be selected for MCQ",true,null);
        AnswerContext answerContext = new AnswerContext();
        Map<Long, MCQOptionContext> optionMap = mcqQuestion.getOptions().stream().collect(Collectors.toMap(MCQOptionContext::getId, Function.identity()));
        if (CollectionUtils.isNotEmpty(selected)) { // Check is for handling answers only with 'other' option
            answerContext.setMultiEnumAnswer(selected.stream()
                    .map(id -> validateAndReturnMCQOption(id, optionMap, answer, answerContext))
                    .collect(Collectors.toList()));
        }
        else {
            answerContext.setMultiEnumAnswer(Collections.emptyList()); // To handle deletion
        }
        answer.getAnswer().setOther(answerContext.getEnumOtherAnswer()); // To have proper client response
        return answerContext;
    }

    @SneakyThrows
    private MCQOptionContext validateAndReturnMCQOption(Long selected, Map<Long, MCQOptionContext> optionMap, MCQMultiAnswerContext answer, AnswerContext answerContext) {
        Map<String, Number> errorParams = new HashMap<>();
        errorParams.put("selectedOption",selected);
        V3Util.throwRestException(selected == null, ErrorCode.VALIDATION_ERROR, "errors.qa.mcqMultiAnswerHandler.optionSelectionValidationCheck",true,errorParams);
        //V3Util.throwRestException(selected == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid select option ({0}) is specified while adding MCQ Answer", selected),true,errorParams);
        MCQOptionContext option = optionMap.get(selected);
        V3Util.throwRestException(option == null, ErrorCode.VALIDATION_ERROR, "errors.qa.mcqMultiAnswerHandler.optionSelectionValidationCheck",true,errorParams);
        //V3Util.throwRestException(option == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid select option ({0}) is specified while adding MCQ Answer", selected),true,errorParams);
        if (option.otherEnabled() && StringUtils.isNotEmpty(answer.getAnswer().getOther())) {
            answerContext.setEnumOtherAnswer(answer.getAnswer().getOther());
        }
        return new MCQOptionContext(selected);
    }

    @Override
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return CollectionUtils.isEmpty(answer.getMultiEnumAnswer());
    }

    @Override
    public boolean checkIfAnswerIsNull (MCQMultiAnswerContext answer, QuestionContext question) throws Exception {
        return CollectionUtils.isEmpty(answer.getAnswer().getSelected());
    }

    @Override
    public void setSummaryOfResponses(Long parentId, List<QuestionContext> questions, DateRange range) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.QAndA.ANSWER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        FacilioModule relModule = modBean.getModule(FacilioConstants.QAndA.Answers.MCQ_MULTI_ANSWER_REL);
        Map<String, FacilioField> relFieldMap = FieldFactory.getAsMap(modBean.getAllFields(relModule.getName()));

        FacilioField questionField = fieldMap.get("question");
        FacilioField enumField = relFieldMap.get("right");
        Map<Long, QuestionContext> questionMap = questions.stream().collect(Collectors.toMap(QuestionContext::getId, Function.identity()));

        FacilioField idField = FieldFactory.getIdField(module);
        List<Map<String, Object>> props = QAndAUtil.constructAnswerSelectWithQuestionAndResponseTimeRange(modBean, questionMap.keySet(), parentId, range)
                                            .innerJoin(relModule.getTableName())
                                            .on(new StringBuilder().append(idField.getCompleteColumnName()).append("=").append(relFieldMap.get("left").getCompleteColumnName()).toString())
                                            .select(Stream.of(questionField, enumField).collect(Collectors.toList()))
                                            .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, idField)
                                            .groupBy(new StringJoiner(",").add(questionField.getCompleteColumnName()).add(enumField.getCompleteColumnName()).toString())
                                            .getAsProps();

        QAndAUtil.populateMCQSummary(props, questionMap, questionField, enumField, idField);
    }

    @Override
    public double computeFullScore(List<ScoringRuleCondition> conditions) {
        return ScoringRule.computeSumScore(conditions);
    }

	@Override
	public String getAnswerStringValue(AnswerContext answer, QuestionContext question) throws Exception {
		// TODO Auto-generated method stub
		List<MCQOptionContext> multiEnumAnswers = answer.getMultiEnumAnswer();
		
		MCQMultiContext mcqQuestion = (MCQMultiContext) question;
		
		Map<Long, MCQOptionContext> optionMap = mcqQuestion.getOptions().stream().collect(Collectors.toMap(MCQOptionContext::getId, Function.identity()));
		
		StringBuilder result = new StringBuilder();
		
		for(MCQOptionContext multiEnumAnswer : multiEnumAnswers) {
			
			if(result.length() > 0) {
				result.append(", ");
			}
			result.append(optionMap.get(multiEnumAnswer.getId()).getLabel());
		}
		
		return result.toString();
	}
}
