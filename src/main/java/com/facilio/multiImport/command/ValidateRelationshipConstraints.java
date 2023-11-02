
package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.multiImportExceptions.ImportParseException;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class ValidateRelationshipConstraints extends FacilioCommand {
    private boolean isUpdateMode;
    Map<Long, ImportRowContext> logIdVsImportRows;
    Map<ImportFieldMappingContext, RelationMappingContext> relationShipFieldMap = null;
    List<Pair<Long, ModuleBaseWithCustomFields>> records = null;

    Map<RelationMappingContext, RelationContext> relationMappingVsRelationMap = new HashMap<>();
    Map<RelationMappingContext,Map<ImportRowContext,List<Map<String, Object>>>> validatedRelRecords = new HashMap<>();
    private static final int BATCH_SIZE = 500;  //500 * 10  we can select 5000 mapped record ids at a time
    private static final long UN_CREATED_RECORD_ID = -3l;  //-3 to identify related record id already have a relationship with uncreated record
    private String moduleName = null;


    public ValidateRelationshipConstraints(boolean isUpdateMode) {
        this.isUpdateMode = isUpdateMode;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        init(context);

        if (CollectionUtils.isEmpty(records) || MapUtils.isEmpty(relationShipFieldMap)) {
            return false;
        }

        fillRelationMappingVsRelationMap();

        for (Map.Entry<ImportFieldMappingContext, RelationMappingContext> entry : relationShipFieldMap.entrySet()) {
            ImportFieldMappingContext fieldMappingContext = entry.getKey();
            RelationMappingContext relationMappingContext = entry.getValue();
            if(!isUpdateMode && relationMappingContext.getRelationTypeEnum() == RelationRequestContext.RelationType.MANY_TO_MANY){
              continue;
            }
            int fromIndex = 0;
            int toIndex;
            while (fromIndex != records.size()) {
                toIndex = Math.min(fromIndex + BATCH_SIZE, records.size());  //TODO replace min function to find safer and optimized toIndex
                List<Pair<Long, ModuleBaseWithCustomFields>> subList = getRecordBPatch(fromIndex, toIndex);
                setRelDataInCacheAndValidateConstraints(subList, relationMappingContext, fieldMappingContext);
                fromIndex = toIndex;
            }

        }


        ImportConstants.setValidatedRelRecord(context,validatedRelRecords); // final result

        return false;
    }

    private void init(Context context){
        moduleName = Constants.getModuleName(context);
        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> recordMap = isUpdateMode ? ImportConstants.getUpdateRecordMap(context) : ImportConstants.getInsertRecordMap(context);
        records = recordMap.get(moduleName);
        logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);
        relationShipFieldMap = ImportConstants.getRelationshipFieldMapping(context);
        ImportConstants.setRelationMappingVsRelationMap(context,relationMappingVsRelationMap);
    }
    private void fillRelationMappingVsRelationMap() throws Exception{
        for (RelationMappingContext relationMappingContext : relationShipFieldMap.values()) {
            RelationContext relationContext = RelationUtil.getRelation(relationMappingContext.getRelationId(), false);
            relationMappingVsRelationMap.put(relationMappingContext, relationContext);
        }

    }

    private void setRelDataInCacheAndValidateConstraints(List<Pair<Long, ModuleBaseWithCustomFields>> subList, RelationMappingContext relationMappingContext, ImportFieldMappingContext fieldMappingContext) throws Exception {
        Map<Long, Long> firstMap = new HashMap<>();
        Map<Long,Long> secondMap = new HashMap<>();
        Map<Long,List<Long>> manyToManyMap = new HashMap<>();
        RelationMappingContext.Position firstMapPosition = getPositionForFirstMap(relationMappingContext);
        String sheetColumnName = fieldMappingContext.getSheetColumnName();

        for (Pair<Long, ModuleBaseWithCustomFields> pair : subList) {
            Long logId = pair.getKey();

            ImportRowContext rowContext = logIdVsImportRows.get(logId);

            if (rowContext.isErrorOccurredRow()) {
                continue;
            }

            ModuleBaseWithCustomFields record = pair.getValue();
            List<Long> relationshipData = (List<Long>) record.getDatum(relationMappingContext.getMappingLinkName());
            if(CollectionUtils.isEmpty(relationshipData)){
                continue;
            }
            long recordId = record.getId();
            switch (relationMappingContext.getRelationTypeEnum()) {
                case ONE_TO_ONE:
                    firstMap.put(relationshipData.get(0),-1l);
                    if(isUpdateMode) {
                        secondMap.put(recordId, -1l);
                    }
                    break;

                case ONE_TO_MANY:
                    for (Long relatedId : relationshipData) {
                        firstMap.put(relatedId, -1L);
                    }
                    break;

                case MANY_TO_ONE:
                    if(isUpdateMode) { firstMap.put(recordId,-1l); }
                    break;
                case MANY_TO_MANY:
                    if(isUpdateMode) { manyToManyMap.put(recordId,new ArrayList<>()); }
                        break;

            }
        }

        loadRelationshipData(firstMap,relationMappingContext,firstMapPosition);
        loadRelationshipData(secondMap,relationMappingContext,relationMappingContext.getPositionEnum());
        loadManyToManyRelationshipData(manyToManyMap,relationMappingContext,relationMappingContext.getPositionEnum());

        for (Pair<Long, ModuleBaseWithCustomFields> pair : subList) {
            Long logId = pair.getKey();

            ImportRowContext rowContext = logIdVsImportRows.get(logId);

            if (rowContext.isErrorOccurredRow()) {
                continue;
            }

            ModuleBaseWithCustomFields record = pair.getValue();
            List<Long> relationshipData = (List<Long>) record.getDatum(relationMappingContext.getMappingLinkName());
            if(CollectionUtils.isEmpty(relationshipData)){
                continue;
            }
            long recordId = record.getId();
            try{
                switch (relationMappingContext.getRelationTypeEnum()) {
                    case ONE_TO_ONE: {
                        Long relatedId = relationshipData.get(0);
                        validateOneToOne(relatedId,recordId,firstMap,secondMap,relationshipData);

                        if (isUpdateMode) {
                            firstMap.put(relatedId, recordId);
                            secondMap.put(recordId,relatedId);
                        }else{
                            firstMap.put(relatedId,UN_CREATED_RECORD_ID);
                        }
                    }
                    break;

                    case ONE_TO_MANY: {
                        validateOneToMany(relationshipData,firstMap);
                        if (isUpdateMode) {
                            for (Long relatedId : relationshipData) {
                                firstMap.put(relatedId, recordId);
                            }
                        }else{
                            for (Long relatedId : relationshipData) {
                                firstMap.put(relatedId, UN_CREATED_RECORD_ID);
                            }
                        }
                    }
                    break;
                    case MANY_TO_ONE:{
                        Long relatedId = relationshipData.get(0);
                        validateManyToOne(relationshipData,firstMap,recordId);
                        if(isUpdateMode){
                            firstMap.put(recordId,relatedId);
                        }
                    }
                    break;
                    case MANY_TO_MANY:{
                        validateManyToMany(relationshipData,recordId,manyToManyMap);
                        if(isUpdateMode){
                            for (Long relatedId : relationshipData) {
                                firstMap.put(recordId, relatedId);
                            }
                        }
                    }
                }
            }catch (Exception e){
                rowContext.setErrorOccurredRow(true);
                ImportParseException parseException = new ImportParseException(sheetColumnName,e);
                rowContext.setErrorMessage(parseException.getClientMessage());
            }

        }
    }
    private void validateManyToMany(List<Long> relationshipData,Long recordId,Map<Long,List<Long>> manyToManyMap) throws Exception{
        if(!isUpdateMode) return;

        if(manyToManyMap.containsKey(recordId)){
            List<Long> dbChildRecordIds = manyToManyMap.get(recordId);
            List<Long> duplicateRecordIds = relationshipData.stream()
                    .filter(dbChildRecordIds::contains)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(dbChildRecordIds)){
                throw new IllegalArgumentException("Duplicate child record ids are mapped:"+MultiImportApi.joinByComma(duplicateRecordIds));
            }
        }
    }
    private void validateManyToOne(List<Long> relationshipData,Map<Long,Long> firstMap,Long recordId) throws Exception{
         if(relationshipData.size()>1){
             throw new IllegalArgumentException("In Many To One relationship not more than one record should be mapped");
         }
         if(isUpdateMode && firstMap.get(recordId)!=-1l){
             throw new IllegalArgumentException("Parent record id "+recordId+" already have a relationship with child record id "+firstMap.get(recordId)+".");
         }
    }
    private void validateOneToMany(List<Long> relationshipData,Map<Long,Long> firstMap) throws Exception{
        for (Long relatedId : relationshipData) {
            if (firstMap.get(relatedId)!=-1l) {
                throw new IllegalArgumentException(getErrorMessageForRightToLeft(firstMap,relatedId));
            }
        }

    }
    private void validateOneToOne(Long relatedId,Long recordId,Map<Long,Long> firstMap,
                                  Map<Long,Long> secondMap,List<Long> relationshipData){

        if (relationshipData.size() > 1 ) {
            throw new IllegalArgumentException("In One To One relationship not more than one record should be mapped");
        }

        if (firstMap.get(relatedId)!=-1l) {
            throw new IllegalArgumentException(getErrorMessageForRightToLeft(firstMap,relatedId));
        }

        if(isUpdateMode && secondMap.get(recordId)!=-1l){
            throw new IllegalArgumentException("Parent record id "+recordId+" already have a relationship with child record id "+secondMap.get(recordId)+".");
        }
    }
    private String getErrorMessageForRightToLeft(Map<Long,Long> firstMap,Long relatedId){
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Child record id "+relatedId+" already have a relationship with another parent record ");

        if(firstMap.get(relatedId) == UN_CREATED_RECORD_ID){
            errorMessage.append("in the above rows");
        }else{
            errorMessage.append("id "+firstMap.get(relatedId)+".");
        }
       return errorMessage.toString();
    }
    private void loadRelationshipData(Map<Long, Long> map, RelationMappingContext relationMappingContext, RelationMappingContext.Position position) throws Exception{
        if(MapUtils.isEmpty(map)) {
            return;
        }
        List<Map<String, Object>> dbRecord = getRelationShipData(map.keySet(),relationMappingContext,position);

        if (CollectionUtils.isNotEmpty(dbRecord)) {
            for (Map<String, Object> record : dbRecord) {
                Long left = (Long) record.get("left");
                Long right = (Long) record.get("right");

                switch (position) {
                    case LEFT:
                        map.put(left,right);
                        break;
                    case RIGHT:
                        map.put(right,left);
                }
            }
        }

    }
    private void loadManyToManyRelationshipData(Map<Long,List<Long>> map,RelationMappingContext relationMappingContext, RelationMappingContext.Position position) throws Exception{
        if(MapUtils.isEmpty(map)) {
            return;
        }
        List<Map<String, Object>> dbRecord = getRelationShipData(map.keySet(),relationMappingContext,position);

        if (CollectionUtils.isNotEmpty(dbRecord)) {
            for (Map<String, Object> record : dbRecord) {
                Long left = (Long) record.get("left");
                Long right = (Long) record.get("right");

                switch (position) {
                    case LEFT:
                        map.get(left).add(right);
                        break;
                    case RIGHT:
                        map.get(right).add(left);
                }
            }
        }
    }
    private RelationMappingContext.Position getPositionForFirstMap(RelationMappingContext relationMappingContext){
        if(relationMappingContext.getRelationTypeEnum() == RelationRequestContext.RelationType.MANY_TO_ONE){
            return  relationMappingContext.getPositionEnum();
        }
        return relationMappingContext.getReversePosition();
    }

    private List<Pair<Long, ModuleBaseWithCustomFields>> getRecordBPatch(int fromIndex, int toIndex) {
        return records.subList(fromIndex, toIndex);
    }

    private List<Map<String, Object>> getRelationShipData(Set<Long> ids, RelationMappingContext relationMappingContext, RelationMappingContext.Position position) throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        RelationContext relation = relationMappingVsRelationMap.get(relationMappingContext);

        FacilioModule relationDataModule = relation.getRelationModule();
        List<FacilioField> fields = Constants.getModBean().getAllFields(relationDataModule.getName());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleid", String.valueOf(relationDataModule.getModuleId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(position.getColumnName(),position.getFieldName(), StringUtils.join(ids, ","), NumberOperators.EQUALS));

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(relationDataModule.getTableName())
                .select(fields)
                .andCriteria(criteria);

        List<Map<String, Object>> result = selectRecordBuilder.get();

        return CollectionUtils.isNotEmpty(result) ? result : new ArrayList<>();

    }

}
