package com.facilio.relation.util;

import com.facilio.bmsconsole.context.BulkRelationshipWidget;
import com.facilio.bmsconsole.context.RelationshipWidget;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationRequestContext;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RelationshipWidgetUtil {
    public static Map<Long, RelationRequestContext> getRelationsAsMapOfMappingId(FacilioModule module) throws Exception {
        List<RelationRequestContext> relationRequests = RelationUtil.getAllRelations(module);
        if (CollectionUtils.isNotEmpty(relationRequests)) {
            return relationRequests.stream().collect(Collectors.toMap(RelationRequestContext::getRelMappingId, Function.identity()));
        }
        return null;
    }

    public static List<RelationshipWidget> getRelationshipsOfWidget(long pageWidgetId) throws Exception {
        FacilioModule module = ModuleFactory.getPageRelationShipWidgetsModule();
        List<FacilioField> fields = FieldFactory.getPageRelationShipWidgetsFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getEqualsCondition(fieldMap.get("widgetId"), String.valueOf(pageWidgetId)));
        List<Map<String, Object>> props = builder.get();

        if(CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, RelationshipWidget.class);
        }
        return null;
    }

    public static List<Long> getRelationMappingIdInWidget(long pageWidgetId) throws Exception {
        List<RelationshipWidget> relationships = getRelationshipsOfWidget(pageWidgetId);

        if(CollectionUtils.isNotEmpty(relationships)) {
            return relationships.stream()
                    .map(RelationshipWidget::getRelationMappingId)
                    .collect(Collectors.toList());
        }
        return null;
    }

    public static void insertRelationshipWidgetsToDB(List<RelationshipWidget> relShips) throws Exception {
        if (CollectionUtils.isNotEmpty(relShips)) {
            List<Map<String, Object>> props = FieldUtil.getAsMapList(relShips, RelationshipWidget.class);

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getPageRelationShipWidgetsModule().getTableName())
                    .fields(FieldFactory.getPageRelationShipWidgetsFields())
                    .addRecords(props);
            insertBuilder.save();
        }
    }

    public static void updateRelationshipsInWidget(List<RelationshipWidget> relShips) throws Exception {

        if (CollectionUtils.isNotEmpty(relShips)) {
            FacilioModule module = ModuleFactory.getPageRelationShipWidgetsModule();
            List<FacilioField> fields = FieldFactory.getPageRelationShipWidgetsFields();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

            List<GenericUpdateRecordBuilder.BatchUpdateContext> updateBatch = new ArrayList<>();
            for (RelationshipWidget relShip : relShips) {

                GenericUpdateRecordBuilder.BatchUpdateContext updateContext = new GenericUpdateRecordBuilder.BatchUpdateContext();
                updateContext.addUpdateValue("sequenceNumber", relShip.getSequenceNumber());

                updateContext.addWhereValue(fieldsMap.get("id").getName(), relShip.getId());
                updateBatch.add(updateContext);
            }

            List<FacilioField> whereFields = new ArrayList<>();
            whereFields.add(fieldsMap.get("id"));

            List<FacilioField> updateField = new ArrayList<>();
            updateField.add(fieldsMap.get("sequenceNumber"));
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .fields(updateField)
                    .table(module.getTableName());
            builder.batchUpdate(whereFields, updateBatch);

        }
    }

    public static void deleteRelationshipsOfWidget(long pageWidgetId, List<Long> existingRelShipMappingIds) throws Exception {
        if(CollectionUtils.isNotEmpty(existingRelShipMappingIds)) {
            FacilioModule module = ModuleFactory.getPageRelationShipWidgetsModule();
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPageRelationShipWidgetsFields());

            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getConditionFromList(fieldMap.get("relationMappingId").getColumnName(), fieldMap.get("relationMappingId").getName(),existingRelShipMappingIds, NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("widgetId").getColumnName(), fieldMap.get("widgetId").getName(), String.valueOf(pageWidgetId), NumberOperators.EQUALS));
            builder.delete();
        }
    }

    public static BulkRelationshipWidget getBulkRelationShipWidgetForWidgetId(long pageWidgetId) throws Exception {
        List<RelationshipWidget> relShips = getRelationshipsOfWidget(pageWidgetId);
        if(CollectionUtils.isNotEmpty(relShips)) {
            BulkRelationshipWidget bulkRelShip = new BulkRelationshipWidget();
            bulkRelShip.setRelationships(relShips);
            return bulkRelShip;
        }
        return null;
    }
}
