package com.facilio.relation.util;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.BulkRelationshipWidget;
import com.facilio.bmsconsole.context.RelationshipWidget;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RelationshipWidgetUtil {
    public static Map<Long, RelationRequestContext> getRelationsAsMapOfMappingId(FacilioModule module) throws Exception {
        List<RelationRequestContext> relationRequests = RelationUtil.getAllRelations(module);
        if (CollectionUtils.isNotEmpty(relationRequests)) {
            return relationRequests.stream().collect(Collectors.toMap(RelationRequestContext::getRelMappingId, Function.identity(), (oldValue, newValue) -> newValue));
        }
        return null;
    }

    public static RelationshipWidget getRelationshipOfWidget(Long widgetId, WidgetWrapperType widgetWrapperType) throws Exception {
        List<RelationshipWidget> relships = getRelationshipsOfWidget(widgetId, widgetWrapperType);
        return  CollectionUtils.isNotEmpty(relships)?relships.get(0):null;
    }
    public static List<RelationshipWidget> getRelationshipsOfWidget(Long widgetId, WidgetWrapperType widgetWrapperType) throws Exception {
        FacilioModule module = ModuleFactory.getPageRelationShipWidgetsModule();
        List<FacilioField> fields = FieldFactory.getPageRelationShipWidgetsFields();
        FacilioField widgetIdField = FieldFactory.getWidgetIdField(module, widgetWrapperType);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getEqualsCondition(widgetIdField, String.valueOf(widgetId)));
        List<Map<String, Object>> props = builder.get();

        if(CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, RelationshipWidget.class);
        }
        return null;
    }

    public static List<Long> getRelationMappingIdInWidget(Long widgetId, WidgetWrapperType widgetWrapperType) throws Exception {
        List<RelationshipWidget> relationships = getRelationshipsOfWidget(widgetId,widgetWrapperType);

        if(CollectionUtils.isNotEmpty(relationships)) {
            return relationships.stream()
                    .map(RelationshipWidget::getRelationMappingId)
                    .collect(Collectors.toList());
        }
        return null;
    }

    public static void insertRelationshipWidgetToDB(RelationshipWidget relship) throws Exception {
        if(relship != null) {
            insertRelationshipWidgetsToDB(new ArrayList<>(Arrays.asList(relship)));
        }
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

    public static void setWidgetIdForRelList(long widgetId, RelationshipWidget relList, WidgetWrapperType type) {
        switch (type) {
            case DEFAULT:
                relList.setWidgetId(widgetId);
                break;
            case WIDGET_GROUP:
                relList.setWidgetGroupWidgetId(widgetId);
                break;
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

    public static BulkRelationshipWidget getBulkRelationShipWidgetForWidgetId(Long widgetId, WidgetWrapperType widgetWrapperType) throws Exception {
        List<RelationshipWidget> relShips = getRelationshipsOfWidget(widgetId, widgetWrapperType);
        if(CollectionUtils.isNotEmpty(relShips)) {
            BulkRelationshipWidget bulkRelShip = new BulkRelationshipWidget();
            bulkRelShip.setRelationships(relShips);
            return bulkRelShip;
        }
        return null;
    }

    public static BulkRelationshipWidget getBulkRelationShipsWithDetails(String moduleName, Long widgetId, WidgetWrapperType widgetWrapperType) throws Exception {

        FacilioChain chain = ReadOnlyChainFactory.getBulkRelationShipWidgetChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.WIDGETID, widgetId);
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        chain.execute();

        return (BulkRelationshipWidget) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
    }
}
