package com.facilio.classification.command;

import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationAttributeContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetClassificationAttributesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ClassificationContext> classificationList = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.CLASSIFICATION);
        if (CollectionUtils.isEmpty(classificationList)) {
            return false;
        }

        Set<Long> ids = classificationList.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toSet());
        Map<Long, List<ClassificationAttributeContext>> attributeMap = getAttributeMap(ids);
        if (MapUtils.isNotEmpty(attributeMap)) {
            for (ClassificationContext classification : classificationList) {
                classification.setAttributes(attributeMap.get(classification.getId()));
            }
        }
        return false;
    }

    private Map<Long, List<ClassificationAttributeContext>> getAttributeMap(Set<Long> ids) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getClassificationAttributeModule().getTableName())
                .select(FieldFactory.getClassificationAttributeFields())
                .andCondition(CriteriaAPI.getCondition("CLASSIFICATION_ID", "classificationId", StringUtils.join(ids, ","), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = builder.get();
        Map<Long, List<ClassificationAttributeContext>> collectionModuleMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map<String, Object> map : maps) {
                String attributeName = (String) map.get("attributeName");
                Integer fieldType = (Integer) map.get("fieldType");
                Long classificationId = (Long) map.get("classificationId");

                List<ClassificationAttributeContext> attributes = collectionModuleMap.get(classificationId);
                if (attributes == null) {
                    attributes = new ArrayList<>();
                    collectionModuleMap.put(classificationId, attributes);
                }
                ClassificationAttributeContext attribute = new ClassificationAttributeContext();
                attribute.setAttributeName(attributeName);
                attribute.setFieldType(fieldType);
                attribute.setClassificationId(classificationId);

                attributes.add(attribute);
            }
        }
        return collectionModuleMap;
    }
}
