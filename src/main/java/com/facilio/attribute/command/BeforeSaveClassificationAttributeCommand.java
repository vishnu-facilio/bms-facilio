package com.facilio.attribute.command;

import com.facilio.attribute.context.ClassificationAttributeContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class BeforeSaveClassificationAttributeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
       List<ClassificationAttributeContext> classificationAttributeContextList=Constants.getRecordListFromContext((FacilioContext) context,FacilioConstants.ContextNames.CLASSIFICATION_ATTRIBUTE);
        if (CollectionUtils.isEmpty(classificationAttributeContextList)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR);
        }
        for(ClassificationAttributeContext classificationAttributeContext:classificationAttributeContextList){
            classificationAttributeContext.checkFieldType();
        }
       return false;
    }

}
