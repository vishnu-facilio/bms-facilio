package com.facilio.attribute.command;

import com.facilio.attribute.context.ClassificationAttributeContext;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class BeforeUpdateClassificationAttributeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<Long, ClassificationAttributeContext> oldRecordMap = Constants.getOldRecordMap(context);

        List<ClassificationAttributeContext> recordList = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.CLASSIFICATION_ATTRIBUTE);
        if (CollectionUtils.isEmpty(recordList) || CollectionUtils.isEmpty(oldRecordMap.values())) {
            return false;
        }
        if (recordList.size() != oldRecordMap.values().size()) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Few records doesn't have data");
        }

        for (ClassificationAttributeContext updateClassificationAttributeContext : recordList) {
            ClassificationAttributeContext oldClassificationAttributeContext = oldRecordMap.get(updateClassificationAttributeContext.getId());
            FacilioUtil.throwIllegalArgumentException(StringUtils.isBlank(updateClassificationAttributeContext.getName()), "Attribute name cannot be empty");
            FacilioUtil.throwIllegalArgumentException(updateClassificationAttributeContext.getFieldType() != oldClassificationAttributeContext.getFieldType(), "FieldType cannot be changed");
        }


        ModuleBean modBean = Constants.getModBean();
        List<FacilioField> fields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(Constants.getModuleName(context)));
        fields.add(fieldsMap.get("name"));
        fields.add(fieldsMap.get("description"));
        fields.add(fieldsMap.get("metric"));
        fields.add(fieldsMap.get("unitId"));
        context.put(Constants.PATCH_FIELDS, fields);

        return false;
    }
}
