package com.facilio.classification.command;

import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.classification.util.ClassificationCache;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResolveClassificationPathCommand extends FacilioCommand {

    private ClassificationCache cache = new ClassificationCache();

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean resolvePath = FacilioUtil.parseBoolean(Constants.getQueryParam(context, FacilioConstants.ContextNames.CLASSIFICATION_RESOLVE_PATH));
        if (!resolvePath) {
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
        cache.addToCache(classificationIds);

        return false;
    }

    private String constructPath(ClassificationContext classification, int level) throws Exception {
        String path = "";
        if (classification == null) {
            return path;
        }
        classification = cache.getFromCache(classification.getId());
        path = classification.getName();
        if (classification.getParentClassification() != null && level < 3) {
//            ClassificationContext parentClassification = getFromCache(classification.getParentClassification());
            if(level==2){
                path="... / ".concat(path);
            }else{
                path = constructPath(classification.getParentClassification(), level + 1) + " / " + path;
            }

        }

        return path;
    }
}
