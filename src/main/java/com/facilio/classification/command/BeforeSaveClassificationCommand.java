package com.facilio.classification.command;


import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.classification.util.ClassificationCache;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.util.DisplayNameToLinkNameUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class BeforeSaveClassificationCommand extends FacilioCommand {

    private ClassificationCache cache = new ClassificationCache();

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ClassificationContext> classificationList = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.CLASSIFICATION);

        if (CollectionUtils.isEmpty(classificationList)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR);
        }

        for (ClassificationContext classificationContext : classificationList) {
            validateClassification(classificationContext);

            String linkname= DisplayNameToLinkNameUtil.getLinkName(classificationContext.getName(),FacilioConstants.ContextNames.CLASSIFICATION,"linkName");
            classificationContext.setLinkName(linkname);
            classificationContext.setStatus(true);

        }
        return false;
    }

    private void validateClassification(ClassificationContext classificationContext) throws Exception {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(classificationContext.getName()), "Classification name cannot be empty");
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(classificationContext.getAppliedModuleIds()), "Classification should be applied to at least one module");

        if (classificationContext.getParentClassification() != null) {
            ClassificationContext parentClassification = cache.getFromCache(classificationContext.getParentClassification().getId());
            if (!(parentClassification.getAppliedModuleIds().containsAll(classificationContext.getAppliedModuleIds()))) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Child classification has modules that doesn't contain in parent");
            }
        }
    }


}
