package com.facilio.classification.command;

import com.facilio.attribute.context.ClassificationAttributeContext;
import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.*;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;


import java.util.*;
import java.util.stream.Collectors;

public class GetClassificationAssociatedAttributesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ClassificationContext> classificationList = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.CLASSIFICATION);
        if (CollectionUtils.isEmpty(classificationList)) {
            return false;
        }

        Set<Long> classificationIds = classificationList.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toSet());
        Map<Long, List<ClassificationAttributeContext>> attributeMap = ClassificationUtil.getAttributeMap(classificationIds);
        if (MapUtils.isNotEmpty(attributeMap)) {
            for (ClassificationContext classification : classificationList) {
                classification.setAttributes(attributeMap.get(classification.getId()));
            }
        }
        return false;
    }
}
