package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssociateRelationshipCommand extends FacilioCommand {
    private boolean isUpdateMode;
    Map<Long, ImportRowContext> logIdVsImportRows;
    List<Pair<Long, ModuleBaseWithCustomFields>> records = null;
    Map<ImportFieldMappingContext, RelationMappingContext> relationShipFieldMap = null;
    List<ImportFieldMappingContext> relMappingFields = null;
    Map<RelationMappingContext, RelationContext> relationMappingVsRelationMap = new HashMap<>();
    private String moduleName = null;

    public AssociateRelationshipCommand(boolean isUpdateMode){
        this.isUpdateMode = isUpdateMode;
    }
    @Override
    public boolean executeCommand(Context context) throws Exception {
        init(context);
        if (CollectionUtils.isEmpty(records) || CollectionUtils.isEmpty(relMappingFields)) {
            return false;
        }
        for (Map.Entry<ImportFieldMappingContext, RelationMappingContext> entry : relationShipFieldMap.entrySet()) {
            RelationMappingContext relationMappingContext = entry.getValue();
            List<Map<String, Object>> validatedData = new ArrayList<>();
            buildRelationsData(records,relationMappingContext,validatedData);
            associate(validatedData,relationMappingContext);
        }
        return false;
    }
    private void init(Context context){
        moduleName = Constants.getModuleName(context);
        ImportFileSheetsContext importSheet = ImportConstants.getImportSheet(context);
        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> recordMap = isUpdateMode ? ImportConstants.getUpdateRecordMap(context) : ImportConstants.getInsertRecordMap(context);
        records = recordMap.get(moduleName);
        logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);
        relationShipFieldMap = ImportConstants.getRelationshipFieldMapping(context);
        relMappingFields = importSheet.getRelationFieldMapping();
        relationMappingVsRelationMap = ImportConstants.getRelationMappingVsFieldMapping(context);
    }

    private void buildRelationsData(List<Pair<Long, ModuleBaseWithCustomFields>> subList, RelationMappingContext relationMappingContext,List<Map<String, Object>> validatedData) {
        for (Pair<Long, ModuleBaseWithCustomFields> pair : subList) {
            Long logId = pair.getKey();
            ModuleBaseWithCustomFields record = pair.getValue();
            long parentId = record.getId();

            ImportRowContext rowContext = logIdVsImportRows.get(logId);

            if (rowContext.isErrorOccurredRow()) {
                continue;
            }
            RelationMappingContext.Position position = relationMappingContext.getPositionEnum();
            List<Long> relationshipData = (List<Long>) record.getDatum(relationMappingContext.getMappingLinkName());
            if (CollectionUtils.isEmpty(relationshipData)) {
                continue;
            }
            for (long sheetChildId : relationshipData) {
                long left = -1l, right = -1l;
                switch (position) {
                    case LEFT:
                        left = parentId;
                        right = sheetChildId;
                        break;

                    case RIGHT:
                        right = parentId;
                        left = sheetChildId;
                }
                Map<String, Object> relationData = buildRelationData(left, right);
                validatedData.add(relationData);
            }
        }
    }
    private Map<String, Object> buildRelationData(long left, long right) {
        Map<String, Object> relationData = new HashMap<>();
        relationData.put("left", left);
        relationData.put("right", right);
        return relationData;
    }
    private void associate(List<Map<String, Object>> validatedData, RelationMappingContext relationMappingContext) throws Exception {
        if(CollectionUtils.isEmpty(validatedData)){
            return;
        }
        RelationContext relation = relationMappingVsRelationMap.get(relationMappingContext);

        FacilioModule relationDataModule = relation.getRelationModule();
        List<FacilioField> fields = Constants.getModBean().getAllFields(relationDataModule.getName());
        InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder()
                .module(relationDataModule)
                .fields(fields)
                .addRecordProps(validatedData);
        insertRecordBuilder.save();
    }
    private List<Pair<Long, ModuleBaseWithCustomFields>> getRecordBPatch(int fromIndex, int toIndex) {
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.subList(fromIndex, toIndex);

    }
}
