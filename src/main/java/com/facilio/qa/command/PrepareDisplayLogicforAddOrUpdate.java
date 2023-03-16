package com.facilio.qa.command;

import static org.junit.jupiter.api.DynamicTest.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.displaylogic.context.DisplayLogicAction;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.context.DisplayLogicTriggerQuestions;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class PrepareDisplayLogicforAddOrUpdate extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		DisplayLogicContext displaylogic = (DisplayLogicContext) context.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT);
		
		V3Util.throwRestException(displaylogic.getDisplayLogicTypeEnum() == null, ErrorCode.VALIDATION_ERROR, "errors.qa.prepareDisplayLogicForAddOrUpdate.displayLogicTypeCheck",true,null);
		V3Util.throwRestException(displaylogic.getTemplateId() == null, ErrorCode.VALIDATION_ERROR, "errors.qa.prepareDisplayLogicForAddOrUpdate.templateCheck",true,null);
		V3Util.throwRestException(displaylogic.getPageId() == null, ErrorCode.VALIDATION_ERROR, "errors.qa.prepareDisplayLogicForAddOrUpdate.pageCheck",true,null);
		//V3Util.throwRestException(displaylogic.getDisplayLogicTypeEnum() == null, ErrorCode.VALIDATION_ERROR, "Display Logic Type cannot be empty during save",true,null);
		//V3Util.throwRestException(displaylogic.getTemplateId() == null, ErrorCode.VALIDATION_ERROR, "Template cannot be empty during save",true,null);
		//V3Util.throwRestException(displaylogic.getPageId() == null, ErrorCode.VALIDATION_ERROR, "Page cannot be empty during save",true,null);
		Predicate<DisplayLogicAction> predicate = (action) -> { return action == null; };
		V3Util.throwRestException((displaylogic.getActions() == null || displaylogic.getActions().isEmpty() || !displaylogic.getActions().stream().filter(predicate).collect(Collectors.toList()).isEmpty()), ErrorCode.VALIDATION_ERROR, "errors.qa.prepareDisplayLogicForAddOrUpdate.actionCheck",true,null);
		//V3Util.throwRestException((displaylogic.getActions() == null || displaylogic.getActions().isEmpty() || !displaylogic.getActions().stream().filter(predicate).collect(Collectors.toList()).isEmpty()), ErrorCode.VALIDATION_ERROR, "Actions cannot be empty during save",true,null);
		
		QAndATemplateContext template = (QAndATemplateContext) Constants.getRecordList(V3Util.getSummary(FacilioConstants.QAndA.Q_AND_A_TEMPLATE, Collections.singletonList(displaylogic.getTemplateId()))).get(0);
		QAndAUtil.populatePagesAndQuestionsInTemplates(Collections.singletonList(template));
		Predicate<PageContext> pagePredicate = (page) -> displaylogic.getPageId().equals(page.getId());
		PageContext page = (PageContext) template.getPages().stream().filter(pagePredicate).findFirst().get();
		
		V3Util.throwRestException(page == null, ErrorCode.VALIDATION_ERROR, "errors.qa.prepareDisplayLogicForAddOrUpdate.pageCheck",true,null);
		//V3Util.throwRestException(page == null, ErrorCode.VALIDATION_ERROR, "Page cannot be empty during save",true,null);

		displaylogic.setPage(page);
		
		List<PageContext> pages = template.getPages();
		List<QuestionContext> questions = new ArrayList<QuestionContext>();
		
		for(PageContext page1 : pages) {
			for(QuestionContext question : page1.getQuestions()) {
				question.setPage(page);
			}
			questions.addAll(page1.getQuestions());
		}
		
		Map<Long, QuestionContext> questionMap = questions.stream().collect(Collectors.toMap(QuestionContext::getId, Function.identity()));
		
		if(displaylogic.getDisplayLogicTypeEnum().isQuestionDependent()) {
			V3Util.throwRestException(displaylogic.getQuestionId() == null, ErrorCode.VALIDATION_ERROR, "errors.qa.prepareDisplayLogicForAddOrUpdate.questionCheck",true,null);
			//V3Util.throwRestException(displaylogic.getQuestionId() == null, ErrorCode.VALIDATION_ERROR, "Question cannot be empty during save",true,null);

			displaylogic.setQuestion(questionMap.get(displaylogic.getQuestionId()));
		}

		if(displaylogic.getCriteria() != null) {
			for(String key : displaylogic.getCriteria().getConditions().keySet()) {
				Condition condition = displaylogic.getCriteria().getConditions().get(key);
				condition.setColumnName("dummy");												// setting this since there will not be any module associated with this condition.
			}
		}
		
		if(displaylogic.getCriteria() != null) {
			long criteriaId = CriteriaAPI.addCriteria(displaylogic.getCriteria());
			displaylogic.setCriteriaId(criteriaId);
			
			for(String key : displaylogic.getCriteria().getConditions().keySet()) {
				Condition condition = displaylogic.getCriteria().getConditions().get(key);
				
				long questionId = -1l;
				if(condition.getFieldName().contains("_")) {
					questionId = Long.parseLong(condition.getFieldName().split("_")[0]);
				}
				else {
					questionId = Long.parseLong(condition.getFieldName());
				}
				
				QuestionContext criteriaQuestion = questionMap.get(questionId);
				
				V3Util.throwRestException(criteriaQuestion == null, ErrorCode.VALIDATION_ERROR, "errors.qa.prepareDisplayLogicForAddOrUpdate.questionTemplateCheck",true,null);
				//V3Util.throwRestException(criteriaQuestion == null, ErrorCode.VALIDATION_ERROR, "Question used should be under current template",true,null);

				if(displaylogic.getDisplayLogicTypeEnum().isQuestionDependent()) {
					V3Util.throwRestException(criteriaQuestion.getPage().getPosition() > displaylogic.getPage().getPosition(), ErrorCode.VALIDATION_ERROR, "errors.qa.prepareDisplayLogicForAddOrUpdate.questionCriteriaCheck",true,null);
					//V3Util.throwRestException(criteriaQuestion.getPage().getPosition() > displaylogic.getPage().getPosition(), ErrorCode.VALIDATION_ERROR, "Question used in criteria should be above the current question",true,null);
					//V3Util.throwRestException(criteriaQuestion.getPage().getPosition() == displaylogic.getPage().getPosition() && criteriaQuestion.getPosition() >= displaylogic.getQuestion().getPosition() , ErrorCode.VALIDATION_ERROR, "Question used in criteria should be above the current question");
				}
				else {
					V3Util.throwRestException(criteriaQuestion.getPage().getPosition() >=  displaylogic.getPage().getPosition(), ErrorCode.VALIDATION_ERROR, "errors.qa.prepareDisplayLogicForAddOrUpdate.questionPageCriteriaCheck",true,null);
					//V3Util.throwRestException(criteriaQuestion.getPage().getPosition() >=  displaylogic.getPage().getPosition(), ErrorCode.VALIDATION_ERROR, "Question used in criteria should be above the current page",true,null);
				}
				
				displaylogic.addDisplayLogicTriggerQuestions(new DisplayLogicTriggerQuestions(criteriaQuestion.getId()));
			}
		}
		
		displaylogic.resetObjects();
		return false;
	}

}
