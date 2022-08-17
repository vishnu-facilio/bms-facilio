package com.facilio.classifcation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.classifcation.context.ClassificationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResolveClassificationPathCommand extends FacilioCommand {

    private Map<Long, ClassificationContext> classificationCache = new HashMap<>();

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean dontResolvePath = (Boolean) context.get(FacilioConstants.ContextNames.CLASSIFICATION_DONT_RESOLVE_PATH);
        if (BooleanUtils.isTrue(dontResolvePath)) {
            return false;
        }

        List<ClassificationContext> classificationList = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.CLASSIFICATION);
        if (CollectionUtils.isEmpty(classificationList)) {
            return false;
        }

        for (ClassificationContext classification : classificationList) {
            classification.setClassificationPath(constructPath(classification.getParentClassification(), 1));
        }

        Set<Long> classificationIds = classificationList.stream().map(ClassificationContext::getId).collect(Collectors.toSet());
        classificationCache.putAll(getClassificationMap(classificationIds));



        return false;
    }

    private String constructPath(ClassificationContext classification, int level) throws Exception {
        String path = "";
        if (classification == null) {
            return path;
        }
        classification = getFromCache(classification);
        path = classification.getName();
        if (classification.getParentClassification() != null && level < 2) {
//            ClassificationContext parentClassification = getFromCache(classification.getParentClassification());
            path = constructPath(classification.getParentClassification(), level + 1) + " / " + path;
        }

        return path;
    }

    private ClassificationContext getFromCache(ClassificationContext classification) throws Exception {
        if (StringUtils.isNotEmpty(classification.getName())) {
            return classification;
        }

        ClassificationContext cache = classificationCache.get(classification.getId());
        if (cache != null) {
            return cache;
        }

        Map<Long, ClassificationContext> classificationMap = getClassificationMap(Collections.singleton(classification.getId()));
        ClassificationContext classificationFromDB = classificationMap.get(classification.getId());
        classificationCache.put(classification.getId(), classificationFromDB);

        return classificationFromDB;
    }

    private Map<Long, ClassificationContext> getClassificationMap(Collection<Long> ids) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CLASSIFICATION);
        SelectRecordsBuilder<ClassificationContext> builder = new SelectRecordsBuilder()
                .module(module)
                .beanClass(ClassificationContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.CLASSIFICATION))
                .andCondition(CriteriaAPI.getIdCondition(ids, module));
        List<ClassificationContext> list = builder.get();

        return list.stream().collect(Collectors.toMap(ClassificationContext::getId, Function.identity()));
    }
}
