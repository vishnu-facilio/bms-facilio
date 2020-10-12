package com.facilio.modules.fields.relations;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class RunDependencyHistoryJob extends FacilioJob {

    @Override
    public void execute(JobContext jc) throws Exception {
        Map<String, Object> runDependencyJob = RelationFieldUtil.getDependencyJobDetails(jc.getJobId());

        Long assetId = (Long) runDependencyJob.get("assetId");
        Long startTime = (Long) runDependencyJob.get("startTime");
        Long endTime = (Long) runDependencyJob.get("endTime");

        Long fieldDependencyId = (Long) runDependencyJob.get("fieldRelationId");
        if (assetId != null) {
            if (startTime == null || endTime == null) {
                throw new IllegalArgumentException("Start time or end time cannot be empty");
            }

            BaseRelationContext fieldRelation = RelationFieldUtil.getFieldRelation(fieldDependencyId);
            if (fieldRelation == null) {
                throw new IllegalArgumentException("Invalid field relation");
            }

            fieldRelation.setHistory(true); // history mode is on
            FacilioField baseField = fieldRelation.getBaseField();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule baseFieldModule = baseField.getModule();
            List<FacilioField> fields = modBean.getAllFields(baseFieldModule.getName());
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            FacilioField parentField = fieldMap.get("parentId");
            FacilioField ttimeField = fieldMap.get("ttime");

            SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
                    .select(fields).module(baseFieldModule).beanClass(ReadingContext.class)
                    .andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(assetId), PickListOperators.IS))
                    .andCondition(CriteriaAPI.getCondition(baseField, CommonOperators.IS_NOT_EMPTY))
                    .andCondition(CriteriaAPI.getCondition(ttimeField, startTime + "," + endTime, DateOperators.BETWEEN))
                    .orderBy("TTIME");
            List<ReadingContext> readingContexts = builder.get();

            if (CollectionUtils.isNotEmpty(readingContexts)) {
                for (int i = 1; i < readingContexts.size(); i++) {
                    ReadingContext prev = readingContexts.get(i - 1);
                    ReadingContext current = readingContexts.get(i);

                    Object o = fieldRelation.calculateValue(current, getReadingDataMeta(prev, baseField));
                    if (o != null) {
                        ReadingContext clonedReading = getClonedReadingContext(current);
                        clonedReading.addReading(fieldRelation.getDerivedField().getName(), o);

                        UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>().module(baseFieldModule)
                                .fields(fields).andCondition(CriteriaAPI.getIdCondition(current.getId(), baseFieldModule));
                        updateBuilder.update(clonedReading);
                    }
                }

                // update the rdm if needed
                ReadingDataMeta readingDataMeta = ReadingsAPI.getReadingDataMeta(assetId, fieldRelation.getDerivedField());
                if (readingDataMeta != null) {
                    ReadingContext lastReading = readingContexts.get(readingContexts.size() - 1);
                    if (FacilioUtil.isEmptyOrNull(readingDataMeta.getValue()) || readingDataMeta.getReadingDataId() == lastReading.getId()) {
                        ReadingsAPI.updateReadingDataMeta(getReadingDataMeta(lastReading, fieldRelation.getDerivedField()));
                    }
                }
            }
        }
    }

    private ReadingContext getClonedReadingContext(ReadingContext rowSecond) {
        ReadingContext rowSecondCloned = new ReadingContext();

        rowSecondCloned.setId(rowSecond.getId());
        rowSecondCloned.setOrgId(rowSecond.getOrgId());
        rowSecondCloned.setModuleId(rowSecond.getModuleId());
        rowSecondCloned.setParentId(rowSecond.getParentId());
        rowSecondCloned.setTtime(rowSecond.getTtime());

        return rowSecondCloned;
    }

    public ReadingDataMeta getReadingDataMeta(ReadingContext reading, FacilioField field) {
        ReadingDataMeta rdm = new ReadingDataMeta();
        rdm.setResourceId(reading.getParentId());
        rdm.setValue(reading.getReading(field.getName()));
        rdm.setField(field);
        rdm.setFieldId(field.getFieldId());
        rdm.setTtime(reading.getTtime());
        return rdm;
    }
}
