package com.facilio.classification.util;

import com.facilio.attribute.context.ClassificationAttributeContext;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class ClassificationUtil {
    public static List<ClassificationAttributeContext> getClassificationAttributesByIds(Collection<Long> recordIds) throws Exception {
        FacilioContext summaryContext = V3Util.getSummary(FacilioConstants.ContextNames.CLASSIFICATION_ATTRIBUTE, new ArrayList<>(recordIds));
        List<ClassificationAttributeContext> list = Constants.getRecordListFromContext(summaryContext, FacilioConstants.ContextNames.CLASSIFICATION_ATTRIBUTE);
        return list;
    }

    public static Set<Long> getClassificationRelatedAttributesIds(Long classificationId) throws Exception {
        Set<Long> relatedIds = null;

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getClassificationAttributeRelFields());
        FacilioField field = fieldsMap.get("attributeId");
        fields.add(field);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getClassificationAttributeRelModule().getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("CLASSIFICATION_ID", "classificationId", String.valueOf(classificationId), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = builder.get();

        if (CollectionUtils.isNotEmpty(maps)) {
            relatedIds = new HashSet<>();
            for (Map<String, Object> map : maps) {
                Long id = (Long) map.get("attributeId");
                relatedIds.add(id);
            }
        }
        return relatedIds;
    }

    public static Map<Long, List<ClassificationAttributeContext>> getAttributeMap(Set<Long> classificationIds) throws Exception {

        Map<Long, List<ClassificationAttributeContext>> collectionModuleMap = new HashMap<>();
        for (Long classificationId : classificationIds) {
            Set<Long> classificationRelatedAttributeIds = getClassificationRelatedAttributesIds(classificationId);
            List<ClassificationAttributeContext> classificationAttributes = null;
            if (CollectionUtils.isNotEmpty(classificationRelatedAttributeIds)) {
                classificationAttributes = getClassificationAttributesByIds(classificationRelatedAttributeIds);

            }
            collectionModuleMap.put(classificationId, classificationAttributes);
        }
        return collectionModuleMap;
    }

    public static void associateAttributesToClassification(long classificationId, Set<Long> relatedAttributeIds) throws Exception {

        if(CollectionUtils.isEmpty(relatedAttributeIds)) return;

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getClassificationAttributeRelModule().getTableName())
                .fields(FieldFactory.getClassificationAttributeRelFields());
        for (Long attributeId : relatedAttributeIds) {
            Map<String, Object> map = new HashMap<>();
            map.put("attributeId", attributeId);
            map.put("classificationId", classificationId);
            builder.addRecord(map);
        }
        builder.save();
    }
}
