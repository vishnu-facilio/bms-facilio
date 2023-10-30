package com.facilio.multiImport.util;

import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.enums.ImportFieldMappingType;
import com.facilio.v3.context.Constants;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class OneLevelImportHelper {
    private Map<Long, List<ImportFieldMappingContext>> oneLevelMap;
    private Map<ImportFieldMappingType,List<ImportFieldMappingContext>> typeVsFieldMappings;
    private Map<String, ImportFieldMappingContext> sheetColumnNameVsFieldMapping;
    private Map<Long, String> fieldIdVsSheetColumnNameMap;
    private Map<String, String> fieldNameVsSheetColumnNameMap;
    private Map<String,List<Map>> moduleNameFacilioFieldMapCache;

    public OneLevelImportHelper(Map<ImportFieldMappingType,
                                List<ImportFieldMappingContext>> typeVsFieldMappings,
                                Map<String, ImportFieldMappingContext> sheetColumnNameVsFieldMapping,
                                Map<Long, String> fieldIdVsSheetColumnNameMap,
                                Map<String, String> fieldNameVsSheetColumnNameMap) throws Exception{
        this.typeVsFieldMappings = typeVsFieldMappings;
        this.sheetColumnNameVsFieldMapping = sheetColumnNameVsFieldMapping;
        this.fieldIdVsSheetColumnNameMap = fieldIdVsSheetColumnNameMap;
        this.fieldNameVsSheetColumnNameMap = fieldNameVsSheetColumnNameMap;
        loadOneLevelMap();
        loadModuleNameFacilioFieldMapCache();
    }
    private void loadOneLevelMap() {
        List<ImportFieldMappingContext> oneLevelFieldMappings = typeVsFieldMappings.get(ImportFieldMappingType.ONE_LEVEL);
        if(CollectionUtils.isEmpty(oneLevelFieldMappings)){
            return;
        }
        oneLevelMap = oneLevelFieldMappings.stream().collect(Collectors.groupingBy(ImportFieldMappingContext::getParentLookupFieldId));
    }
    private void loadModuleNameFacilioFieldMapCache() throws Exception{
        if(MapUtils.isEmpty(oneLevelMap)){
            return;
        }
        moduleNameFacilioFieldMapCache = new HashMap<>();
        Set<Long> parentLookupFields = oneLevelMap.keySet();

        for(Long parentLookupFieldId : parentLookupFields){
            BaseLookupField parentLookupField =(BaseLookupField) Constants.getModBean().getField(parentLookupFieldId);
            String lookupModuleName = parentLookupField.getLookupModule().getName();

            List<Map> fieldMapList = new ArrayList<>();

            List<FacilioField> fields = MultiImportApi.getImportFields(lookupModuleName);

            Map<Long, FacilioField> fieldIdVsFacilioFieldMap = FieldFactory.getAsIdMap(fields);
            Map<String, FacilioField> fieldNameVsFacilioFieldMap = FieldFactory.getAsMap(fields);

            fieldMapList.add(fieldIdVsFacilioFieldMap);
            fieldMapList.add(fieldNameVsFacilioFieldMap);

            moduleNameFacilioFieldMapCache.put(lookupModuleName,fieldMapList);
        }
    }
    public void setOneLevelDataInProp(HashMap<String, Object> props, Map<String, Object> rowVal) throws Exception{

      if(MapUtils.isEmpty(oneLevelMap)){
            return;
      }

      for(Map.Entry<Long,List<ImportFieldMappingContext>> entry:oneLevelMap.entrySet()){
          Long parentLookupFieldId = entry.getKey();
          List<ImportFieldMappingContext> childFieldMappings = entry.getValue();

          BaseLookupField parentLookupField =(BaseLookupField) Constants.getModBean().getField(parentLookupFieldId);
          String lookupModuleName = parentLookupField.getLookupModule().getName();

          Map<String,Object> lookupDataMap = new HashMap<>();

          List<Map> fieldCacheMap = moduleNameFacilioFieldMapCache.get(lookupModuleName);
          Map<String, FacilioField> fieldNameVsFacilioFieldMap = fieldCacheMap.get(0);
          Map<Long, FacilioField> fieldIdVsFacilioFieldMap = fieldCacheMap.get(1);

          for(ImportFieldMappingContext childFieldMapping : childFieldMappings) {
              String sheetColumnName = childFieldMapping.getSheetColumnName();
              FacilioField facilioField = MultiImportApi.getFacilioField(childFieldMapping,
                      fieldIdVsFacilioFieldMap,fieldNameVsFacilioFieldMap);

              Object cellValue = rowVal.get(sheetColumnName);
              if (MultiImportApi.isEmpty(cellValue)) {
                  // The value of row is empty. Set it as null.
                  lookupDataMap.put(facilioField.getName(), null);
                  continue;
              }
              lookupDataMap.put(facilioField.getName(),cellValue);
          }

          props.put(parentLookupField.getName(), lookupDataMap);
      }
    }
}
