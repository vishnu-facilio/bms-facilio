package com.facilio.classification.util;

import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * This cache is used per request level, where we would need to get same classification again and
 * again. This helps in minimal sql queries.
 */
public class ClassificationCache {

    private Map<Long, ClassificationContext> classificationCache = new HashMap<>();

    public ClassificationContext getFromCache(Long classificationId) throws Exception {
//        if (StringUtils.isNotEmpty(classification.getName())) {
//            return classification;
//        }

        ClassificationContext cache = classificationCache.get(classificationId);
        if (cache != null) {
            return cache;
        }

        Map<Long, ClassificationContext> classificationMap = getClassificationMap(Collections.singleton(classificationId));
        ClassificationContext classificationFromDB = classificationMap.get(classificationId);
        classificationCache.put(classificationId, classificationFromDB);

        return classificationFromDB;
    }

    private Map<Long, ClassificationContext> getClassificationMap(Collection<Long> ids) throws Exception {
//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CLASSIFICATION);

        FacilioContext summaryContext = V3Util.getSummary(FacilioConstants.ContextNames.CLASSIFICATION, new ArrayList<>(ids));
        List<ClassificationContext> list = Constants.getRecordListFromContext(summaryContext, FacilioConstants.ContextNames.CLASSIFICATION);

//        SelectRecordsBuilder<ClassificationContext> builder = new SelectRecordsBuilder()
//                .module(module)
//                .beanClass(ClassificationContext.class)
//                .select(modBean.getAllFields(FacilioConstants.ContextNames.CLASSIFICATION))
//                .andCondition(CriteriaAPI.getIdCondition(ids, module));
//        List<ClassificationContext> list = builder.get();

        return list.stream().collect(Collectors.toMap(ClassificationContext::getId, Function.identity()));
    }

    public void addToCache(Set<Long> classificationIds) throws Exception {
        Map<Long, ClassificationContext> classificationMap = getClassificationMap(classificationIds);
        classificationCache.putAll(classificationMap);
    }
}
