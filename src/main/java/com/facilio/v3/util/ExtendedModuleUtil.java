package com.facilio.v3.util;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExtendedModuleUtil {
    public static <T, R extends ModuleBaseWithCustomFields> Map<T, List<R>> splitRecordsByType(List<R> list, Function<R, T> getTypeFromRecord) {
        return list.stream().collect(
                Collectors.groupingBy(
                        r -> getTypeFromRecord.apply(r),
                        HashMap::new,
                        Collectors.toList()
                )
        );
    }

    public static <R extends ModuleBaseWithCustomFields> Map<String, List<R>> splitRecordsBySubModule(List<R> list, Function<R, String> getSubModuleFromRecord) {
        return ExtendedModuleUtil.<String, R>splitRecordsByType(list, getSubModuleFromRecord);
    }

    public static <T, R extends ModuleBaseWithCustomFields> Map<T, List<Long>> splitRecordIdsByType(List<R> list, Function<R, T> getTypeFromRecord) {
        return list.stream().collect(
                Collectors.groupingBy(
                        r -> getTypeFromRecord.apply(r),
                        HashMap::new,
                        Collectors.mapping(r -> r.getId(), Collectors.toList())
                )
        );
    }

    public static <R extends ModuleBaseWithCustomFields> Map<String, List<Long>> splitRecordIdsBySubModule(List<R> list, Function<R, String> getSubModuleFromRecord) {
        return ExtendedModuleUtil.<String, R>splitRecordIdsByType(list, getSubModuleFromRecord);
    }

    public static <T extends ModuleBaseWithCustomFields> void replaceWithExtendedRecords(List<T> list, Function<T, String> getSubModuleFromRecord) throws Exception {
        Map<String, List<Long>> moduleVsRecords = splitRecordIdsBySubModule(list, getSubModuleFromRecord);
        Map<Long, T> extendedRecords = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : moduleVsRecords.entrySet()) {
            Map<Long, T> extendedRecordMap = V3RecordAPI.getRecordsMap(entry.getKey(), entry.getValue());
            extendedRecords.putAll(extendedRecordMap);
        }
        list.replaceAll(r -> extendedRecords.get(r.getId()));
    }

    public static <T extends ModuleBaseWithCustomFields> Map<String, List<? extends T>> fetchExtendedRecords(List<T> list, Function<T, String> getSubModuleFromRecord) throws Exception {
        Map<String, List<Long>> moduleVsRecords = splitRecordIdsBySubModule(list, getSubModuleFromRecord);
        Map<String, List<? extends T>> extendedRecords = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : moduleVsRecords.entrySet()) {
            List<? extends T> extendedRecordList = V3RecordAPI.getRecordsList(entry.getKey(), entry.getValue());
            extendedRecords.put(entry.getKey(), extendedRecordList);
        }
        return extendedRecords;
    }


}
