package com.facilio.classification.command;

import com.facilio.attribute.context.ClassificationAttributeContext;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeforeUpdateClassificationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<Long, ClassificationContext> oldRecordMap = Constants.getOldRecordMap(context);

        List<ClassificationContext> recordList = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.CLASSIFICATION);

        if (CollectionUtils.isEmpty(recordList) || CollectionUtils.isEmpty(oldRecordMap.values())) {
            return false;
        }
        if (recordList.size() != oldRecordMap.values().size()) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Few records doesn't have data");
        }
        //we need to check Old attributeIds are send properly or not ,throw error if old attributes not contain all of the new attribute id
        for (ClassificationContext classification : recordList) {
            FacilioUtil.throwIllegalArgumentException(StringUtils.isBlank(classification.getName()), "Classification name cannot be empty");
            ClassificationContext oldClassification = oldRecordMap.get(classification.getId());
            List<ClassificationAttributeContext> attributeList = classification.getAttributes();
            if(attributeList!=null){
                Set<Long> attributes = attributeList.stream().map(ClassificationAttributeContext::getId).collect(Collectors.toSet());
                Set<Long> oldAttributes = ClassificationUtil.getClassificationRelatedAttributesIds(oldClassification.getId());
                if (CollectionUtils.isNotEmpty(oldAttributes)) {
                    if(!attributes.containsAll(oldAttributes)){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Old Attributes cannot be removed");
                    }
                    attributes.removeAll(oldAttributes);
                }
                ClassificationUtil.associateAttributesToClassification(classification.getId(), attributes);
            }
        }

        // we need to change name, description for patch request
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(Constants.getModuleName(context));
        List<FacilioField> fields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        fields.add(fieldsMap.get("name"));
        fields.add(fieldsMap.get("description"));
        context.put(Constants.PATCH_FIELDS, fields);


        return false;
    }
}

