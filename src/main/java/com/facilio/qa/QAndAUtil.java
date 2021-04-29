package com.facilio.qa;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.ExtendedModuleUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

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
        record.setSysCreatedTime(0);
        record.setSysModifiedTime(0);
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
        ExtendedModuleUtil.replaceWithExtendedRecords(questions, q -> q.getQuestionType().getSubModuleName());
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

    private static void callFetchHandler (Collection<QuestionContext> questions) throws Exception {
        FacilioChain c = QAndAReadOnlyChainFactory.callQuestionFetchHandlers();
        c.getContext().put(FacilioConstants.QAndA.Command.QUESTION_LIST, questions);
        c.execute();
    }

    public static <T extends ModuleBaseWithCustomFields> void addRecordViaChain(String moduleName, List<T> records) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);
        FacilioChain createRecordChain = ChainUtil.getCreateRecordChain(module.getName());
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
}
