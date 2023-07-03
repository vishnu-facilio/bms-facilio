package com.facilio.qa;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.qa.context.*;
import com.facilio.qa.context.questions.BaseMCQContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.qa.context.questions.RatingQuestionContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioStreamUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.util.MathUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.ExtendedModuleUtil;
import com.facilio.v3.util.V3Util;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QAndAUtil {
    public static void splitAndAddQuestionModules (FacilioContext context, List<QuestionContext> list) {
        Map<String, List<QuestionContext>> questionsModuleMap = ExtendedModuleUtil.splitRecordsBySubModule(list, q -> q.getQuestionType().getSubModuleName());
        addExtendedQuestionsToRecordMap(context,  questionsModuleMap);
    }

    public static void addExtendedQuestionsToRecordMap (FacilioContext context, Map<String, List<QuestionContext>> questionsModuleMap) {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
//        recordMap.remove(FacilioConstants.QAndA.QUESTION);
        questionsModuleMap.forEach((k, v) -> Constants.addRecordList(recordMap, k, v));
        Constants.setExtendedModules(context, questionsModuleMap.keySet());
    }

    public static <E extends V3Context> void setDefaultPropsAsNullToReduceRespSize(E record) {
        record.setSysCreatedBy(null);
        record.setSysModifiedBy(null);
//        record.setSysCreatedTime(0);
//        record.setSysModifiedTime(0);
        record._setOrgId(null);
        record._setModuleId(null);
    }

    public static <P extends ModuleBaseWithCustomFields, C> void fetchChildrenFromParent (Collection<P> parentList, String childModuleName, String parentLookupFieldName, String additionalOrderBy, ToLongFunction<C> getParentId, BiConsumer<C, P> setParentId, BiConsumer<P, List<C>> setChildrenList) throws Exception {
        fetchChildrenFromParent(parentList, childModuleName, parentLookupFieldName, additionalOrderBy, getParentId, setParentId, setChildrenList, null, null, null);
    }

    public static <P extends ModuleBaseWithCustomFields, C> void fetchChildrenFromParent (Collection<P> parentList, String childModuleName, String parentLookupFieldName, String additionalOrderBy, ToLongFunction<C> getParentId, BiConsumer<C, P> setParentId, BiConsumer<P, List<C>> setChildrenList, Consumer<C> additionalOperationOnChildren) throws Exception {
        fetchChildrenFromParent(parentList, childModuleName, parentLookupFieldName, additionalOrderBy, getParentId, setParentId, setChildrenList, additionalOperationOnChildren, null, null);
    }

    public static <P extends ModuleBaseWithCustomFields, C> void fetchChildrenFromParent (Collection<P> parentList, String childModuleName, String parentLookupFieldName, String additionalOrderBy, ToLongFunction<C> getParentId, BiConsumer<C, P> setParentId, BiConsumer<P, List<C>> setChildrenList, Consumer<C> additionalOperationOnChildren, Criteria additionalChildCriteria, Consumer<FacilioContext> additionalPropsForFetchChain) throws Exception {
        if (CollectionUtils.isNotEmpty(parentList)) {
            FacilioChain childList = ChainUtil.getListChain(childModuleName);
            FacilioContext childListContext = childList.getContext();
            ModuleBean modBean = Constants.getModBean();
            FacilioModule childModule = modBean.getModule(childModuleName);

            JSONObject sorting = new JSONObject();
            StringJoiner orderBy = new StringJoiner(",").add(parentLookupFieldName);
            if (StringUtils.isNotEmpty(additionalOrderBy)) {
                orderBy.add(additionalOrderBy);
            }

            sorting.put("orderBy", orderBy.toString());
            sorting.put("orderType", "asc");
            Constants.setModuleName(childListContext, childModuleName);
            childListContext.put(FacilioConstants.ContextNames.SORTING, sorting);
            childListContext.put(FacilioConstants.ContextNames.OVERRIDE_SORTING, true);
            childListContext.put(Constants.BEAN_CLASS, FacilioConstants.ContextNames.getClassFromModule(childModule));

            String parentIds = parentList.stream().map(t -> String.valueOf(t.getId())).collect(Collectors.joining(","));
            FacilioField parentField = modBean.getField(parentLookupFieldName, childModuleName);

            Condition parentCondition = CriteriaAPI.getCondition(parentField, parentIds, PickListOperators.IS);
            if (additionalChildCriteria == null) {
                childListContext.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, parentCondition);
            }
            else {
                additionalChildCriteria.addAndCondition(parentCondition);
                childListContext.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, additionalChildCriteria);
            }

            if (additionalPropsForFetchChain != null) {
                additionalPropsForFetchChain.accept(childListContext);
            }

            childList.execute();
            Map<String, List> recordMap = (Map<String, List>) childListContext.get(Constants.RECORD_MAP);
            List<C> childRecords = recordMap.get(childModuleName);
            splitAndSetChildrenInParent(parentList, childRecords, getParentId, setParentId, setChildrenList, additionalOperationOnChildren);
        }
    }

    public static <P extends ModuleBaseWithCustomFields, C> void splitAndSetChildrenInParent (Collection<P> parentList, List<C> childRecords, ToLongFunction<C> getParentId, BiConsumer<C, P> setParentId, BiConsumer<P, List<C>> setChildrenList) {
        splitAndSetChildrenInParent(parentList, childRecords, getParentId, setParentId, setChildrenList, null);
    }

    public static <P extends ModuleBaseWithCustomFields, C> void splitAndSetChildrenInParent (Collection<P> parentList, List<C> childRecords, ToLongFunction<C> getParentId, BiConsumer<C, P> setParentId, BiConsumer<P, List<C>> setChildrenList, Consumer<C> additionalOperationOnChildren) {
        if (CollectionUtils.isNotEmpty(childRecords)) {
            Map<Long, List<C>> parentVsChildren = childRecords.stream().collect(
                    Collectors.groupingBy(
                            c -> getParentId.applyAsLong(c),
                            HashMap::new,
                            Collectors.toList()
                    )
            );
            childRecords.stream().forEach(c -> {
                setParentId.accept(c, null);
                if (additionalOperationOnChildren != null) {
                    additionalOperationOnChildren.accept(c);
                }
            });

            parentList.stream().forEach(p -> setChildrenList.accept(p, parentVsChildren.get(p.getId())));
        }
    }

    public static List<QuestionContext> fetchExtendedQuestions (Collection<Long> recordIds) throws Exception {
        return fetchExtendedQuestions(recordIds, false);
    }

    public static List<QuestionContext> fetchExtendedQuestions (Collection<Long> recordIds, boolean callFetchHandler) throws Exception {
        List<QuestionContext> questions = V3RecordAPI.getRecordsList(FacilioConstants.QAndA.QUESTION, recordIds);
        replaceWithExtendedQuestions(questions);
        if (callFetchHandler) {
            callFetchHandler(questions);
        }
        return questions;
    }

    public static Map<Long, QuestionContext> fetchExtendedQuestionMap (Collection<Long> recordIds) throws Exception {
        return fetchExtendedQuestionMap(recordIds, false);
    }

    public static Map<Long, QuestionContext> fetchExtendedQuestionMap (Collection<Long> recordIds, boolean callFetchHandler) throws Exception {
        Map<Long, QuestionContext> questions = V3RecordAPI.getRecordsMap(FacilioConstants.QAndA.QUESTION, recordIds);
        ExtendedModuleUtil.replaceWithExtendedRecords(questions, q -> q.getQuestionType().getSubModuleName());
        if (callFetchHandler) {
            callFetchHandler(questions.values());
        }
        return questions;
    }

	public static void updateResponseRetakeExpiry(ResponseContext response) throws Exception{
		FacilioChain chain = QAndATransactionChainFactory.updateResponseRetakeExpiryChain();
		chain.getContext().put(FacilioConstants.QAndA.RESPONSE,response);
		chain.execute();
	}

	private static void callFetchHandler (Collection<QuestionContext> questions) throws Exception {
        FacilioChain c = QAndAReadOnlyChainFactory.callQuestionFetchHandlers();
        c.getContext().put(FacilioConstants.QAndA.Command.QUESTION_LIST, questions);
        c.execute();
    }

    public static Stream<QuestionContext> getQuestionStream (PageContext page) {
        return FacilioStreamUtil.emptyIfNull(page.getQuestions());
    }

    public static <T extends V3Context> void addRecordViaV3Chain(String moduleName, List<T> records) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);
        FacilioChain createRecordChain = ChainUtil.getCreateChain(module.getName());
        FacilioContext addAnswerContext = createRecordChain.getContext();
        Class beanClass = ChainUtil.getBeanClass(v3Config, module);

        Constants.setV3config(addAnswerContext, v3Config);
        Constants.setModuleName(addAnswerContext, module.getName());
        addAnswerContext.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
        addAnswerContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
        addAnswerContext.put(Constants.BEAN_CLASS, beanClass);
        Map<String, List> recordMap = new HashMap<>();
        recordMap.put(module.getName(), records);
        addAnswerContext.put(Constants.RECORD_MAP, recordMap);

        createRecordChain.execute();
    }

    public static <T extends V3Context> int updateRecordViaV3Chain(String moduleName, T record, T oldRecord) throws Exception { //Expecting record with full props. That's how fw works
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);
        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        FacilioChain patchChain = ChainUtil.getPatchChain(moduleName,null);
        FacilioContext context = patchChain.getContext();

        Constants.setV3config(context, v3Config);
        Constants.setModuleName(context, moduleName);
        Constants.addToOldRecordMap(context, moduleName, oldRecord);
        context.put(Constants.RECORD_ID, record.getId());
        context.put(Constants.BEAN_CLASS, beanClass);
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);

        Map<String, List> recordMap = new HashMap<>();
        recordMap.put(module.getName(), Collections.singletonList(record));
        context.put(Constants.RECORD_MAP, recordMap);
        patchChain.execute();

        Integer count = (Integer) context.get(Constants.ROWS_UPDATED);
        return count == null ? 0 : count;
    }

    public static List<QuestionContext> getQuestionsFromTemplate (long templateId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.QAndA.QUESTION);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        FacilioField parentField = FieldFactory.filterField(fields, "parent");
        SelectRecordsBuilder<QuestionContext> builder = new SelectRecordsBuilder<QuestionContext>()
                .select(fields)
                .module(module)
                .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                .andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(templateId), PickListOperators.IS))
                ;
        List<QuestionContext> questions = builder.get();
        replaceWithExtendedQuestions(questions);
        callFetchHandler(questions);
        return questions;
    }

    public static List<AnswerContext> getAnswersFromTemplateAndResponse (long templateId, long responseId) throws Exception {

		FacilioChain chain = QAndAReadOnlyChainFactory.fetchAnswersFromTemplateAndResponseChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.QAndA.Command.TEMPLATE_ID,templateId);
		context.put(FacilioConstants.QAndA.Command.RESPONSE_ID,responseId);

		chain.execute();

		return (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWER_LIST);
    }

    public static void populateMCQSummary (List<Map<String, Object>> props, Map<Long, QuestionContext> questionMap, FacilioField questionField, FacilioField enumField, FacilioField idField) throws Exception {
        if (CollectionUtils.isNotEmpty(props)) {
            Map<Long, Map<Long, BaseMCQContext.OptionSummary>> summaryMap = new HashMap<>();
            for (Map<String, Object> prop : props) {
                Long questionId = (Long) ((Map<String, Object>) prop.get(questionField.getName())).get("id");
                Long enumAnswer = enumField.getDataTypeEnum() == FieldType.NUMBER ? (Long) prop.get(enumField.getName()) : (Long) ((Map<String, Object>) prop.get(enumField.getName())).get("id");
                BaseMCQContext mcq = (BaseMCQContext) questionMap.get(questionId);
                double count = ((Number) prop.get(idField.getName())).doubleValue();
                double total = mcq.getAnswered() == null ? 0 : mcq.getAnswered().doubleValue();
                V3Util.throwRestException(total < count, ErrorCode.UNHANDLED_EXCEPTION, "Total answered cannot be less than individual mcq answer count. This is not supposed to happen");

                BaseMCQContext.OptionSummary summary = new BaseMCQContext.OptionSummary(enumAnswer, MathUtil.calculatePercentage(count, total), (int) count);
                summaryMap.computeIfAbsent(questionId, k -> new HashMap<>()).put(enumAnswer, summary);
            }

            questionMap.values().forEach(q -> populateIndividualSummary((BaseMCQContext) q, summaryMap));
        }
    }

    private static void populateIndividualSummary (BaseMCQContext question, Map<Long, Map<Long, BaseMCQContext.OptionSummary>> summaryMap) {
        Map<Long, BaseMCQContext.OptionSummary> questionSummary = summaryMap.get(question.getId());
        if (questionSummary != null) {
            List<BaseMCQContext.OptionSummary> summaryList = new ArrayList<>();
            for (MCQOptionContext option : question.getOptions()) {
                BaseMCQContext.OptionSummary summary = questionSummary.get(option.getId());
                summaryList.add(summary == null ? new BaseMCQContext.OptionSummary(option.getId(), 0f, 0) : summary);
            }
            question.setSummary(summaryList);
        }
    }

    public static void populateAnswersForQuestions (Map<Long, QuestionContext> questions, Criteria criteria, boolean isSingleResponse) throws Exception {
        QAndAUtil.fetchChildrenFromParent(questions.values(),
                FacilioConstants.QAndA.ANSWER,
                "question",
                null,
                ClientAnswerContext::getQuestion,
                ClientAnswerContext::addQuestionId,
                isSingleResponse ? QuestionContext::setAnswer : QuestionContext::setAnswers,
                null,
                criteria,
                c -> addQuestionToFetchChain(c, questions, isSingleResponse));
    }

    private static void addQuestionToFetchChain(FacilioContext context, Map<Long, QuestionContext> questions, boolean isSingleResponse) {
        context.put(FacilioConstants.QAndA.Command.QUESTION_MAP, questions);
        context.put(FacilioConstants.QAndA.Command.IS_SINGLE_RESPONSE, isSingleResponse);
    }

    public static void replaceWithExtendedQuestions (List<QuestionContext> questions) throws Exception {
        ExtendedModuleUtil.replaceWithExtendedRecords(questions, QAndAUtil::getQuestionSubModuleName, QAndAUtil::getSupplementsFromQuestionSubModule);
    }

    private static String getQuestionSubModuleName (QuestionContext q) {
        return Objects.requireNonNull(q.getQuestionType(), "Question Type cannot be null to get sub module from Question").getSubModuleName();
    }

    @SneakyThrows
    private static Collection<SupplementRecord> getSupplementsFromQuestionSubModule (String subModule) {
        QuestionType type = QuestionType.getTypeFromSubModule(subModule);
        QuestionHandler handler = type.getQuestionHandler();
        return handler == null ? null :
                handler.defaultSaveOption() == null ? null :
                    handler.defaultSaveOption().getSupplements();
    }

    public static QuestionContext fetchQuestionWithProps (long id) throws Exception {
        QuestionContext question = V3RecordAPI.getRecord(FacilioConstants.QAndA.QUESTION, id);
        if (question == null) {
            return null;
        }
        List<QuestionContext> questions = Stream.of(question).collect(Collectors.toList());
        replaceWithExtendedQuestions(questions);
        question = questions.get(0);
        QuestionHandler handler = question.getQuestionType().getQuestionHandler();
        if (handler != null) {
            handler.afterFetch(questions);
        }
        return question;
    }

    public static SelectRecordsBuilder<AnswerContext> constructAnswerSelectWithQuestionAndResponseTimeRange(ModuleBean modBean, Collection<Long> questionIds, long parentId, DateRange range) throws Exception {
        FacilioModule answerModule = modBean.getModule(FacilioConstants.QAndA.ANSWER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(answerModule.getName()));

        FacilioModule responseModule = modBean.getModule(FacilioConstants.QAndA.RESPONSE);
        FacilioField responseSysModifiedTime = modBean.getField("sysModifiedTime", responseModule.getName());
        FacilioField responseIdField = FieldFactory.getIdField(responseModule);

        return new SelectRecordsBuilder<AnswerContext>()
                .module(answerModule)
                .innerJoin(responseModule.getTableName())
                .on(new StringBuilder().append(fieldMap.get("response").getCompleteColumnName()).append("=").append(responseIdField.getCompleteColumnName()).toString())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), String.valueOf(parentId), PickListOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("question"), questionIds, PickListOperators.IS))
                .andCondition(CriteriaAPI.getCondition(responseSysModifiedTime, range.toString(), DateOperators.BETWEEN));
    }
    
    public static void populatePagesAndQuestionsInTemplates(Collection<QAndATemplateContext> templates) throws Exception {
    	populatePagesInTemplates(templates);
    	for(QAndATemplateContext template : templates) {
    		populateQuestionsInPages(template.getPages());
    	}
    }

    public static void populatePagesInTemplates(Collection<QAndATemplateContext> templates) throws Exception {
            fetchChildrenFromParent(templates,
                FacilioConstants.QAndA.PAGE,
                "parent",
                "position",
                p -> p.getParent().getId(),
                PageContext::setParent,
                QAndATemplateContext::setPages,
                QAndAUtil::setDefaultPropsAsNullToReduceRespSize);
    }

    public static void populateQuestionsInPages(Collection<PageContext> pages) throws Exception {
        fetchChildrenFromParent(pages,
                FacilioConstants.QAndA.QUESTION,
                "page",
                "position",
                q -> q.getPage().getId(),
                QuestionContext::setPage,
                PageContext::setQuestions,
                QAndAUtil::removeDefaultQuestionProps);
    }

    public static void populateQuestionsInPages(Collection<PageContext> pages, Criteria additionalCriteria) throws Exception {
        fetchChildrenFromParent(pages,
                FacilioConstants.QAndA.QUESTION,
                "page",
                "position",
                q -> q.getPage().getId(),
                QuestionContext::setPage,
                PageContext::setQuestions,
                QAndAUtil::removeDefaultQuestionProps,
                additionalCriteria,
                null);
    }

    private static void removeDefaultQuestionProps (QuestionContext question) {
        setDefaultPropsAsNullToReduceRespSize(question);
        question.setParent(null);
    }

    public static ClientAnswerContext serializedAnswer(QuestionContext question,AnswerContext answer) throws Exception {
        ClientAnswerContext clientAnswer = question.getQuestionType().getAnswerHandler().serialize(answer);
        clientAnswer.addQuestionId(question);
        clientAnswer.setId(answer.getId());
        clientAnswer.setFullScore(answer.getFullScore());
        clientAnswer.setScore(answer.getScore());
        clientAnswer.setComments(answer.getComments());
        clientAnswer.setResponse(answer.getResponse());
        clientAnswer.setResponseId(answer.getResponseId());
        clientAnswer.setParent(answer.getParent());
        clientAnswer.setScorePercent(answer.getScorePercent());
        clientAnswer.setAttachmentList(answer.getAttachmentList());
        return clientAnswer;
    }

    public static void populateRatingSummary(List<Map<String, Object>> props, Map<Long, QuestionContext> questionMap, FacilioField questionField, FacilioField ratingAnswerField, FacilioField idField) throws Exception {
        if (CollectionUtils.isNotEmpty(props)) {
            Map<Long, Map<Long, RatingQuestionContext.OptionSummary>> summaryMap = new HashMap<>();
            for (Map<String, Object> prop : props) {
                Long questionId = (Long) ((Map<String, Object>) prop.get(questionField.getName())).get("id");
                Number ratingAnswer = Math.toIntExact(ratingAnswerField.getDataTypeEnum() == FieldType.NUMBER ? (Integer) prop.get(ratingAnswerField.getName()) : (Integer) ((Map<String, Object>) prop.get(ratingAnswerField.getName())).get("id"));
                RatingQuestionContext rq = (RatingQuestionContext) questionMap.get(questionId);
                double count = ((Number) prop.get(idField.getName())).doubleValue();
                double total = rq.getAnswered() == null ? 0 : rq.getAnswered().doubleValue();
                V3Util.throwRestException(total < count, ErrorCode.UNHANDLED_EXCEPTION, "Total answered cannot be less than individual mcq answer count. This is not supposed to happen");

                RatingQuestionContext.OptionSummary summary = new RatingQuestionContext.OptionSummary(ratingAnswer.longValue(), MathUtil.calculatePercentage(count, total), (int) count);
                summaryMap.computeIfAbsent(questionId, k -> new HashMap<>()).put(ratingAnswer.longValue(), summary);
            }
            questionMap.values().forEach(q -> populateIndividualRatingSummary((RatingQuestionContext) q, summaryMap));
        }
    }

    public static void computeRatingScaleRange(RatingQuestionContext rq) {
        int ratingScale = rq.getRatingScale();
        int startIdx = Math.round(5 - (ratingScale/2));
        int endInx = startIdx + ratingScale -1;
        List<Integer> ratingScaleRange = new ArrayList<>();
        ratingScaleRange.add(startIdx);
        ratingScaleRange.add(endInx);
        rq.setRatingScaleRange(ratingScaleRange);
    }

    private static void populateIndividualRatingSummary (RatingQuestionContext question, Map<Long, Map<Long, RatingQuestionContext.OptionSummary>> summaryMap) {
        Map<Long, RatingQuestionContext.OptionSummary> questionSummary = summaryMap.get(question.getId());
        if (questionSummary != null) {
            List<RatingQuestionContext.OptionSummary> summaryList = new ArrayList<>();
            int ratingScale = question.getRatingScale()+1;
            for (long i=1;i<ratingScale;i++) {
                RatingQuestionContext.OptionSummary summary = questionSummary.get(i);
                summaryList.add(summary == null ? new RatingQuestionContext.OptionSummary((long) i, 0f, 0) : summary);
            }
            question.setSummary(summaryList);
        }
    }

    public static List<Map<String, Object>> fetchEvalRuleActionRel(long conditionId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(com.facilio.qa.rules.Constants.FieldFactory.evalRuleActionRelFields())
                .table(com.facilio.qa.rules.Constants.ModuleFactory.evalRuleActionRelModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("Q_AND_A_RULE_CONDITION_ID","conditionId", String.valueOf(conditionId), NumberOperators.EQUALS));
        return builder.get();
    }

	public static void executeTemplate (String moduleName, JSONObject template, List< ResourceContext> resources,Long ruleId) throws Exception {
		FacilioChain executeTemplateChain = QAndATransactionChainFactory.executeSurveyTemplateChain();
		FacilioContext context = executeTemplateChain.getContext();
		Constants.setModuleName(context,moduleName);
		Constants.setRecordId(context,(Long)template.get("qandaTemplateId"));
		context.put("ruleId",ruleId);
		context.put("expiryDay",template.get("expiryDay"));
		context.put("isRetakeAllowed",template.get("isRetakeAllowed"));
		context.put("retakeExpiryDay",template.get("retakeExpiryDay"));
		context.put("retakeExpiryDuration",template.get("retakeExpiryDuration"));
		context.put("parentId",template.get("parentId"));
		context.put("assignedTo",template.get("assignedTo"));
		context.put(FacilioConstants.ContextNames.RESOURCE_LIST,resources);
		executeTemplateChain.execute();
	}

	public static void validateResponseExpiry(ResponseContext response){

		long currentTime = DateTimeUtil.getCurrenTime();
		if (response.isRetake()) {
			if (response.getExpiryDate() == null) {
				FacilioUtil.throwRunTimeException(response.getRetakeExpiry() != null && response.getRetakeExpiry() < currentTime, "Retake survey expired.");
			}
			else {
				FacilioUtil.throwRunTimeException(response.getExpiryDate() < currentTime, "Survey Expired.");
			}
		}
		else {
			FacilioUtil.throwRunTimeException(response.getExpiryDate() != null && response.getExpiryDate() < currentTime, "Survey Expired.");
		}
	}
}
