package com.facilio.v3.util;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.SupplementRecord;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExtendedModuleUtil<T> {
    public static <T, R extends ModuleBaseWithCustomFields> Map<T, List<R>> splitRecordsByType(Collection<R> list, Function<R, T> getTypeFromRecord) {
        return list.stream().collect(
                Collectors.groupingBy(
                        r -> getTypeFromRecord.apply(r),
                        HashMap::new,
                        Collectors.toList()
                )
        );
    }

    public static <R extends ModuleBaseWithCustomFields> Map<String, List<R>> splitRecordsBySubModule(Collection<R> list, Function<R, String> getSubModuleFromRecord) {
        return ExtendedModuleUtil.<String, R>splitRecordsByType(list, getSubModuleFromRecord);
    }

    public static <T, R extends ModuleBaseWithCustomFields> Map<T, List<Long>> splitRecordIdsByType(Collection<R> list, Function<R, T> getTypeFromRecord) {
        return list.stream().collect(
                Collectors.groupingBy(
                        r -> getTypeFromRecord.apply(r),
                        HashMap::new,
                        Collectors.mapping(r -> r.getId(), Collectors.toList())
                )
        );
    }

    public static <R extends ModuleBaseWithCustomFields> Map<String, List<Long>> splitRecordIdsBySubModule(Collection<R> list, Function<R, String> getSubModuleFromRecord) {
        return ExtendedModuleUtil.<String, R>splitRecordIdsByType(list, getSubModuleFromRecord);
    }

    public static <T extends ModuleBaseWithCustomFields> void replaceWithExtendedRecords(List<T> list, Function<T, String> getSubModuleFromRecord) throws Exception {
        replaceWithExtendedRecords(list, getSubModuleFromRecord, null);
    }

    public static <T extends ModuleBaseWithCustomFields> void replaceWithExtendedRecords(List<T> list, Function<T, String> getSubModuleFromRecord, Function<String, Collection<SupplementRecord>> getSupplementFromSubModule) throws Exception {
        Map<String, List<Long>> moduleVsRecords = splitRecordIdsBySubModule(list, getSubModuleFromRecord);
        Map<Long, T> extendedRecords = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : moduleVsRecords.entrySet()) {
            Map<Long, T> extendedRecordMap = V3RecordAPI.getRecordsMap(entry.getKey(), entry.getValue(), getSupplementFromSubModule == null ? null : getSupplementFromSubModule.apply(entry.getKey()));
            extendedRecords.putAll(extendedRecordMap);
        }
        list.replaceAll(r -> extendedRecords.get(r.getId()));
    }

    public static <T extends ModuleBaseWithCustomFields> void replaceWithExtendedRecords (Map<Long, T> map, Function<T, String> getSubModuleFromRecord) throws Exception {
        Map<String, List<Long>> moduleVsRecords = splitRecordIdsBySubModule(map.values(), getSubModuleFromRecord);
        for (Map.Entry<String, List<Long>> entry : moduleVsRecords.entrySet()) {
            Map<Long, T> extendedRecordMap = V3RecordAPI.getRecordsMap(entry.getKey(), entry.getValue());
            map.putAll(extendedRecordMap);
        }
    }

    public static <T extends ModuleBaseWithCustomFields> T fetchExtendedRecord (T record, Function<T, String> getSubModuleFromRecord) throws Exception {
        T extendedRecord = V3RecordAPI.getRecord(getSubModuleFromRecord.apply(record), record.getId());
        return extendedRecord;
    }
}
