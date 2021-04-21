package com.facilio.qa;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.ExtendedModuleUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
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

    public static <P extends ModuleBaseWithCustomFields, C extends ModuleBaseWithCustomFields> void fetchChildrenFromParent (List<P> parentList, String childModuleName, String parentLookupFieldName, Class<C> childClass, ToLongFunction<C> getParentId, BiConsumer<C, P> setParentId, BiConsumer<P, List<C>> setChildrenList) throws Exception {
        fetchChildrenFromParent(parentList, childModuleName, parentLookupFieldName, childClass, getParentId, setParentId, setChildrenList, null);
    }

    public static <P extends ModuleBaseWithCustomFields, C extends ModuleBaseWithCustomFields> void fetchChildrenFromParent (List<P> parentList, String childModuleName, String parentLookupFieldName, Class<C> childClass, ToLongFunction<C> getParentId, BiConsumer<C, P> setParentId, BiConsumer<P, List<C>> setChildrenList, Consumer<C> additionalOperationOnChildren) throws Exception {
        if (CollectionUtils.isNotEmpty(parentList)) {
            FacilioChain childList = ChainUtil.getListChain(childModuleName);
            FacilioContext childListContext = childList.getContext();

            JSONObject sorting = new JSONObject();
            sorting.put("orderBy", new StringJoiner(",").add(parentLookupFieldName).add("position").toString());
            sorting.put("orderType", "asc");
            Constants.setModuleName(childListContext, childModuleName);
            childListContext.put(FacilioConstants.ContextNames.SORTING, sorting);
            childListContext.put(FacilioConstants.ContextNames.OVERRIDE_SORTING, true);
            childListContext.put(Constants.BEAN_CLASS, childClass);

            String parentIds = parentList.stream().map(t -> String.valueOf(t.getId())).collect(Collectors.joining(","));
            ModuleBean modBean = Constants.getModBean();
            FacilioField parentField = modBean.getField(parentLookupFieldName, childModuleName);
            Condition parentCondition = CriteriaAPI.getCondition(parentField, parentIds, PickListOperators.IS);
            childListContext.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, parentCondition);

            childList.execute();
            List<C> childRecords = Constants.getRecordList(childListContext);
            splitAndSetChildrenInParent(parentList, childRecords, getParentId, setParentId, setChildrenList, additionalOperationOnChildren);
        }
    }

    public static <P extends ModuleBaseWithCustomFields, C extends ModuleBaseWithCustomFields> void splitAndSetChildrenInParent (List<P> parentList, List<C> childRecords, ToLongFunction<C> getParentId, BiConsumer<C, P> setParentId, BiConsumer<P, List<C>> setChildrenList) {
        splitAndSetChildrenInParent(parentList, childRecords, getParentId, setParentId, setChildrenList, null);
    }

    public static <P extends ModuleBaseWithCustomFields, C extends ModuleBaseWithCustomFields> void splitAndSetChildrenInParent (List<P> parentList, List<C> childRecords, ToLongFunction<C> getParentId, BiConsumer<C, P> setParentId, BiConsumer<P, List<C>> setChildrenList, Consumer<C> additionalOperationOnChildren) {
        if (CollectionUtils.isNotEmpty(childRecords)) {
            Map<Long, List<C>> parentVsChildren = childRecords.stream().collect(
                    Collectors.groupingBy(
                            c -> getParentId.applyAsLong(c),
                            HashMap::new,
                            Collectors.toList()
                    )
            );
            childRecords.stream().forEach(c -> {
                c.setSysCreatedBy(null);
                c.setSysModifiedBy(null);
                c.setSysCreatedTime(0);
                c.setSysModifiedTime(0);
                setParentId.accept(c, null);
                if (additionalOperationOnChildren != null) {
                    additionalOperationOnChildren.accept(c);
                }
            });

            parentList.stream().forEach(p -> setChildrenList.accept(p, parentVsChildren.get(p.getId())));
        }
    }
}
