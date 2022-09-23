package com.facilio.classification.command;

import com.facilio.attribute.context.ClassificationAttributeContext;
import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.classification.context.ClassificationDataContext;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SummaryClassificationDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean fetchClassificationData=FacilioUtil.parseBoolean(context.get(Constants.FETCH_CLASSIFICATION));
        if (!fetchClassificationData) {
            return false;
        }

        List<V3Context> records = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isEmpty(records)) {
            return false;
        }
        records = records.stream().filter(m -> m.getClassification() != null).collect(Collectors.toList()); //filter clasification holding record only
        if (CollectionUtils.isEmpty(records)) {
            return false;
        }

        List<ClassificationContext> classificationList = records.stream().map(V3Context::getClassification).collect(Collectors.toList());
        Set<Long> classificationIds = classificationList.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toSet());
        Map<Long, List<ClassificationAttributeContext>> attributeMap = ClassificationUtil.getAttributeMap(classificationIds);
        Set<Long> recordIds = records.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toSet());
        Map<Long, List<ClassificationDataContext>> classificationDataMap = ClassificationUtil.getClassificationDataMap(recordIds, Constants.getModuleName(context));


        for (V3Context record : records) {
            ClassificationContext classificationContext = record.getClassification();

            long recordId = record.getId();
            long classificationId = classificationContext.getId();
            List<ClassificationDataContext> classificationData = classificationDataMap.getOrDefault(recordId, new ArrayList<>());
            List<ClassificationAttributeContext> attributeList = getNewAttributeObject(attributeMap.get(classificationId));
            //when many records have same classificationId means we shoud fill data to new attributeList objects otherwise same refernce objects used to other records --Data inconsistency problem
            if (CollectionUtils.isNotEmpty(attributeList) && CollectionUtils.isNotEmpty(classificationData)) {
                Map<Long, ClassificationAttributeContext> attributeMapByAttributeId = attributeList.stream().collect(Collectors.toMap(ClassificationAttributeContext::getId, Function.identity()));
                for (ClassificationDataContext classificationDataContext : classificationData) {
                    Long attributeId = classificationDataContext.getAttribute().getId();
                    ClassificationAttributeContext attribute = attributeMapByAttributeId.get(attributeId);
                    attribute.setDataFromClassificationDataContext(classificationDataContext);
                }
            }

            classificationContext.setAttributes(attributeList);

        }

        return false;
    }

    private List<ClassificationAttributeContext> getNewAttributeObject(List<ClassificationAttributeContext> dbAttributeList) {
        List<ClassificationAttributeContext> attributeList = new ArrayList<>();
        for(ClassificationAttributeContext dbAttribute:dbAttributeList){
            ClassificationAttributeContext attribute=FieldUtil.cloneBean(dbAttribute,ClassificationAttributeContext.class);
            attributeList.add(attribute);
        }

        return attributeList;
    }
}
