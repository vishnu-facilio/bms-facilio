package com.facilio.v3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PositionUtil {
    private static <T extends ModuleBaseWithCustomFields> Map<Long, List<T>> fetchExisting(FacilioModule module, FacilioField parentField, FacilioField positionField, FacilioField idField, Class<T> beanClass, ToLongFunction<T> getParentId, Collection<Long> parentIds) throws Exception {
        List<FacilioField> selectFields = new ArrayList<>(2);
        selectFields.add(parentField);
        selectFields.add(positionField);

        SelectRecordsBuilder<T> recordsFetcher = new SelectRecordsBuilder<T>()
                .select(selectFields)
                .module(module)
                .beanClass(beanClass)
                .andCondition(CriteriaAPI.getCondition(parentField, parentIds, PickListOperators.IS))
                .orderBy(MessageFormat.format("{0}, {1}", parentField.getCompleteColumnName(), positionField.getCompleteColumnName()));

        List<T> records = recordsFetcher.get();

        return records.stream().collect(
                Collectors.groupingBy(
                        p -> getParentId.applyAsLong(p),
                        HashMap::new,
                        Collectors.toList()
                )
        );
    }

    private static <T extends ModuleBaseWithCustomFields> boolean rotateList(String moduleName, List<T> existingRecords, T record, T oldRecord, int newArrayPos, BiConsumer<T, Integer> setPosition, Function<T, Integer> getPosition) throws Exception {
        if (getPosition.apply(oldRecord) == getPosition.apply(record)) {
            return false;
        }
        int oldArrayPos = getPosition.apply(oldRecord) - 1;
        setPosition.accept(oldRecord, newArrayPos + 1);
        if (oldArrayPos < newArrayPos) {
            Collections.rotate(existingRecords.subList(oldArrayPos, newArrayPos + 1), -1);
        } else {
            Collections.rotate(existingRecords.subList(newArrayPos, oldArrayPos + 1), 1);
        }
        return true;
    }

    private static <T extends ModuleBaseWithCustomFields> void construcBatchUpdateContext(String positionFieldName, FacilioField moduleIdField, FacilioField idField, List<T> existingRecords, T record, Map<Long, GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateMap, BiConsumer<T, Integer> setPosition, Function<T, Integer> getPosition) {
        IntStream.range(0, existingRecords.size())
                .forEach(i -> {
                    T rec = existingRecords.get(i);
                    if (rec.getId() != record.getId()) {
                        int newPos = i + 1;
                        if (newPos != getPosition.apply(rec)) {
                            GenericUpdateRecordBuilder.BatchUpdateContext batchUpdate = batchUpdateMap.computeIfAbsent(rec.getId(), k -> new GenericUpdateRecordBuilder.BatchUpdateContext());
                            batchUpdate.addUpdateValue(positionFieldName, newPos);
                            batchUpdate.addWhereValue(moduleIdField.getName(), rec.getModuleId());
                            batchUpdate.addWhereValue(idField.getName(), rec.getId());

                            setPosition.accept(rec, newPos);
                        }
                    }
                });
    }

    public static <T extends ModuleBaseWithCustomFields> void computeAndUpdatePosition(String moduleName, String parentFieldName, String positionFieldName, Class<T> beanClass, Map<Long, List<T>> newRecords, Function<T, Integer> getPosition, BiConsumer<T, Integer> setPosition, ToLongFunction<T> getParentId) throws Exception {
        computeAndUpdatePosition(moduleName, parentFieldName, positionFieldName, beanClass, newRecords, getPosition, setPosition, getParentId, false, null, null);
    }

    public static <T extends ModuleBaseWithCustomFields> void computeAndUpdatePosition(String moduleName, String parentFieldName, String positionFieldName, Class<T> beanClass, Map<Long, List<T>> newRecords, Function<T, Integer> getPosition, BiConsumer<T, Integer> setPosition, ToLongFunction<T> getParentId, boolean allowParentToBeChanged, BiConsumer<T, Long> setParentId, Map<Long, T> oldRecords) throws Exception {
        FacilioUtil.throwIllegalArgumentException(allowParentToBeChanged && setParentId == null, "Set parent id function cannot be null when parent id can be changed");
        FacilioUtil.throwIllegalArgumentException(allowParentToBeChanged && oldRecords == null, "Old record map cannot be null when parent id can be changed");
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField parentField = fieldMap.get(parentFieldName);
        FacilioField positionField = fieldMap.get(positionFieldName);
        FacilioModule module = modBean.getModule(moduleName);
        FacilioField idField = FieldFactory.getIdField(module);
        List<FacilioField> selectFields = new ArrayList<>(2);
        selectFields.add(parentField);
        selectFields.add(positionField);
        Set<Long> parentIds = new HashSet<>(newRecords.keySet());
        if (MapUtils.isNotEmpty(oldRecords)) {
            parentIds.addAll(oldRecords.values().stream().map(r -> getParentId.applyAsLong(r)).collect(Collectors.toList()));
        }
        Map<Long, List<T>> existingRecordsMap = fetchExisting(module, parentField, positionField, idField, beanClass, getParentId, parentIds);
        Map<Long, GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateMap = new HashMap<>();
        FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
        for (Map.Entry<Long, List<T>> entry : newRecords.entrySet()) {
            Long parentId = entry.getKey();
            List<T> records = entry.getValue();
            List<T> existingRecords = existingRecordsMap.computeIfAbsent(parentId, k -> new ArrayList<>());
            int lastAddedPosition = 0;
            for (T record : records) {
                Integer newArrayPos = getPosition.apply(record) == null ? null : getPosition.apply(record) - 1;
                if (newArrayPos == null) { //Handle null position value for multiple records
                    newArrayPos = lastAddedPosition++;
                    setPosition.accept(record, newArrayPos + 1);
                }
                Optional<T> oldRecordOptional = existingRecords.stream().filter(r -> r.getId() == record.getId()).findFirst(); // Not using oldrecord map because it's optional
                if (oldRecordOptional.isPresent()) {
                    T oldRecord = oldRecordOptional.get();
                    V3Util.throwRestException(newArrayPos >= existingRecords.size(), ErrorCode.VALIDATION_ERROR, MessageFormat.format("Out of index value ({0}) for position in record : {1}", newArrayPos + 1, record.getId()));
                    if (!rotateList(moduleName, existingRecords, record, oldRecord, newArrayPos, setPosition, getPosition)) {
                        continue;
                    }
                } else {
                    if (record.getId() > 0 && !allowParentToBeChanged) { // Throw error if parent id cannot be changed and record id is not found in existing list
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid id ({0}) for module : {1}", record.getId(), moduleName));
                    }
                    V3Util.throwRestException(newArrayPos > existingRecords.size(), ErrorCode.VALIDATION_ERROR, MessageFormat.format("Out of index value ({0}) for position in record", newArrayPos + 1));
                    existingRecords.add(newArrayPos, record);
                    if (record.getId() > 0) { //Removing record from old parent list
                        T oldRecord = oldRecords.get(record.getId());
                        List<T> oldParentRecords = existingRecordsMap.get(getParentId.applyAsLong(oldRecord));
                        oldParentRecords.remove(getPosition.apply(oldRecord) - 1);
                        construcBatchUpdateContext(positionFieldName, moduleIdField, idField, oldParentRecords, record, batchUpdateMap, setPosition, getPosition);
                    }
                }
                construcBatchUpdateContext(positionFieldName, moduleIdField, idField, existingRecords, record, batchUpdateMap, setPosition, getPosition);
            }
        }
        if (MapUtils.isNotEmpty(batchUpdateMap)) {
            updatePositionForRecords(module, idField, moduleIdField, positionField, batchUpdateMap.values());
        }
    }

    private static void updatePositionForRecords(FacilioModule module, FacilioField idField, FacilioField moduleIdField, FacilioField positionField, Collection<GenericUpdateRecordBuilder.BatchUpdateContext> updateBatch) throws SQLException {
        List<FacilioField> whereFields = new ArrayList<>(2);
        whereFields.add(moduleIdField);
        whereFields.add(idField);
        new GenericUpdateRecordBuilder()
                .fields(Collections.singletonList(positionField))
                .table(module.getTableName())
                .batchUpdate(whereFields, updateBatch);
    }

    public static <T extends ModuleBaseWithCustomFields> void updatePositionOnRemove(String moduleName, String parentFieldName, String positionFieldName, Class<T> beanClass, List<Long> recordIds, Function<T, Integer> getPosition, BiConsumer<T, Integer> setPosition, ToLongFunction<T> getParentId) throws Exception {
        if (CollectionUtils.isNotEmpty(recordIds)) {
            List<T> oldRecords = V3RecordAPI.getRecordsList(moduleName, recordIds);
            ModuleBean modBean = Constants.getModBean();
            List<FacilioField> fields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            FacilioField parentField = fieldMap.get(parentFieldName);
            FacilioField positionField = fieldMap.get(positionFieldName);
            FacilioModule module = modBean.getModule(moduleName);
            FacilioField idField = FieldFactory.getIdField(module);
            List<FacilioField> selectFields = new ArrayList<>(2);
            selectFields.add(parentField);
            selectFields.add(positionField);

            List<Long> parentIds = oldRecords.stream().map(r -> getParentId.applyAsLong(r)).collect(Collectors.toList());
            Map<Long, List<T>> existingRecordsMap = fetchExisting(module, parentField, positionField, idField, beanClass, getParentId, parentIds);

            Map<Long, GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateMap = new HashMap<>();
            FacilioField moduleIdField = FieldFactory.getModuleIdField(module);

            for (T record : oldRecords) {
                long parentId = getParentId.applyAsLong(record);
                int arrayPos = getPosition.apply(record) - 1;
                List<T> existingRecords = existingRecordsMap.get(parentId);
                existingRecords.remove(arrayPos);
                construcBatchUpdateContext(positionFieldName, moduleIdField, idField, existingRecords, record, batchUpdateMap, setPosition, getPosition);
            }
            if (MapUtils.isNotEmpty(batchUpdateMap)) {
                updatePositionForRecords(module, idField, moduleIdField, positionField, batchUpdateMap.values());
            }
        }
    }
}
