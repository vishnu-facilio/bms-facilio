package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddOrUpdateRelationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        RelationRequestContext relationRequest = (RelationRequestContext) context.get(FacilioConstants.ContextNames.RELATION);
        validateRelationRequest(relationRequest);

        FacilioModule relationModule = ModuleFactory.getRelationModule();

        RelationContext relationContext;
        if (relationRequest.getId() > 0) {
            // update
            RelationContext oldRelation = RelationUtil.getRelation(relationRequest.getId(), true);
            if (oldRelation == null) {
                throw new IllegalArgumentException("Invalid relation");
            }

            RelationRequestContext oldRequest = RelationUtil.convertToRelationRequest(oldRelation, relationRequest.getFromModuleId());
            if (relationRequest.getRelationTypeEnum() != oldRequest.getRelationTypeEnum()) {
                throw new IllegalArgumentException("Cannot change relation type");
            }

            relationContext = RelationUtil.convertToRelationContext(relationRequest);
            relationContext.setLinkName(null);

            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(relationModule.getTableName())
                    .fields(FieldFactory.getRelationFields())
                    .andCondition(CriteriaAPI.getIdCondition(relationContext.getId(), relationModule));
            builder.update(FieldUtil.getAsProperties(relationContext));

            // delete mapping and add again
            updateMappings(relationContext, oldRelation);
        } else {
            // add
            computeLinkName(relationRequest);

            relationContext = RelationUtil.convertToRelationContext(relationRequest);
            RelationContext.RelationCategory relationCategory = relationRequest.getRelationCategoryEnum() == null ? RelationContext.RelationCategory.NORMAL : relationRequest.getRelationCategoryEnum();
            relationContext.setRelationCategory(relationCategory);

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(relationModule.getTableName())
                    .fields(FieldFactory.getRelationFields());
            long relationId = builder.insert(FieldUtil.getAsProperties(relationContext));
            relationContext.setId(relationId);

            addRelations(relationContext);

            long relationModuleId = addRelationModule(relationContext);
            relationContext.setRelationModuleId(relationModuleId);

            GenericUpdateRecordBuilder updateModuleBuilder = new GenericUpdateRecordBuilder()
                    .table(relationModule.getTableName())
                    .fields(Collections.singletonList(FieldFactory.getNumberField("relationModuleId", "RELATION_MODULE_ID", relationModule)))
                    .andCondition(CriteriaAPI.getIdCondition(relationId, relationModule));
            Map<String, Object> map = new HashMap<>();
            map.put("relationModuleId", relationModuleId);
            updateModuleBuilder.update(map);
        }

        context.put(FacilioConstants.ContextNames.RELATION, RelationUtil.convertToRelationRequest(relationContext, relationRequest.getFromModuleId()));

        return false;
    }

    private long addRelationModule(RelationContext relation) throws Exception {
        FacilioModule module = new FacilioModule();
        module.setName("__relation_module_" + relation.getId());
        module.setDisplayName("Relation Module for " + relation.getId());
        module.setType(FacilioModule.ModuleType.RELATION_DATA);
        module.setTableName("Custom_Relation");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long relationModuleId = modBean.addModule(module);
        module.setModuleId(relationModuleId);

        for (RelationMappingContext mapping : relation.getMappings()) {
            LookupField field = new LookupField();
            field.setDataType(FieldType.LOOKUP);
            field.setName(mapping.getPositionEnum().getFieldName());
            field.setDisplayName(mapping.getFromModule().getDisplayName());
            field.setColumnName(mapping.getPositionEnum().getColumnName());
            field.setModule(module);
            field.setLookupModule(mapping.getFromModule());

            if (mapping.getPositionEnum() == RelationMappingContext.Position.LEFT) {
                field.setMainField(true);
            }

            modBean.addField(field);
        }

        return relationModuleId;
    }

    private void updateMappings(RelationContext relationContext, RelationContext oldRelation) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getRelationMappingModule().getTableName())
                .fields(FieldFactory.getRelationMappingFields());

        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> data = new ArrayList<>();
        Map<Long, RelationMappingContext> oldRelationMap = oldRelation.getMappings().stream().collect(Collectors.toMap(RelationMappingContext::getFromModuleId, Function.identity()));
        for (RelationMappingContext mapping : relationContext.getMappings()) {
            RelationMappingContext oldMapping = oldRelationMap.get(mapping.getFromModuleId());
            if (oldMapping == null) {
                throw new IllegalArgumentException("The modules in relation cannot be changed");
            }

            if (oldMapping.getToModuleId() != mapping.getToModuleId()) {
                throw new IllegalArgumentException("The modules in relation cannot be changed");
            }

            mapping.setId(oldMapping.getId());
            mapping.setRelationId(oldMapping.getRelationId());
            mapping.setPosition(oldMapping.getPositionEnum());
            mapping.setMappingLinkName(oldMapping.getMappingLinkName());

            GenericUpdateRecordBuilder.BatchUpdateByIdContext datum = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            datum.setWhereId(mapping.getId());
            datum.setUpdateValue(FieldUtil.getAsProperties(mapping));
            data.add(datum);
        }
        builder.batchUpdateById(data);
    }

    private void addRelations(RelationContext relationContext) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getRelationMappingModule().getTableName())
                .fields(FieldFactory.getRelationMappingFields());
        for (RelationMappingContext mappingContext : relationContext.getMappings()) {
            mappingContext.setRelationId(relationContext.getId());
            computeMappingLinkName(mappingContext);
            builder.addRecord(FieldUtil.getAsProperties(mappingContext));
        }
        builder.save();
    }

    private void computeLinkName(RelationRequestContext relationRequest) throws Exception {
        relationRequest.setLinkName(relationRequest.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));

        FacilioModule module = ModuleFactory.getRelationModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(module));
        fields.add(FieldFactory.getStringField("linkName", "LINK_NAME", module));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCustomWhere("LINK_NAME = ? OR LINK_NAME LIKE ?",relationRequest.getLinkName(),relationRequest.getLinkName() + "\\_%")
                .orderBy("ID DESC")
                .limit(1);
        Map<String, Object> prop = builder.fetchFirst();
        if (prop != null) {
            String linkName = (String) prop.get("linkName");
            int count = 0;
            if (linkName.contains("_")) {
                try {
                    count = Integer.parseInt(linkName.substring(linkName.lastIndexOf('_') + 1));
                } catch (NumberFormatException ex) {}
            }
            relationRequest.setLinkName(relationRequest.getLinkName() + "_" + (count + 1));
        }
    }

    private void computeMappingLinkName(RelationMappingContext mappingContext) throws Exception {
        mappingContext.setMappingLinkName(mappingContext.getRelationName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));

        FacilioModule module = ModuleFactory.getRelationMappingModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(module));
        fields.add(FieldFactory.getStringField("mappingLinkName", "LINK_NAME", module));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCustomWhere("LINK_NAME = ? OR LINK_NAME LIKE ?",mappingContext.getMappingLinkName(),mappingContext.getMappingLinkName() + "\\_%")
                .orderBy("ID DESC")
                .limit(1);
        Map<String, Object> prop = builder.fetchFirst();
        if (prop != null) {
            String linkName = (String) prop.get("mappingLinkName");
            int count = 0;
            if (linkName.contains("_")) {
                try {
                    count = Integer.parseInt(linkName.substring(linkName.lastIndexOf('_') + 1));
                } catch (NumberFormatException ex) {}
            }
            mappingContext.setMappingLinkName(mappingContext.getMappingLinkName() + "_" + (count + 1));
        }
    }

    private void validateRelationRequest(RelationRequestContext relationRequest) {
        if (relationRequest == null) {
            throw new IllegalArgumentException("Invalid request");
        }

        if (StringUtils.isEmpty(relationRequest.getName())) {
            throw new IllegalArgumentException("Name is required");
        }
        if (relationRequest.getRelationTypeEnum() == null) {
            throw new IllegalArgumentException("Relation type is required");
        }
        if (relationRequest.getFromModuleId() <= 0) {
            throw new IllegalArgumentException("From Module is required");
        }
        if (relationRequest.getToModuleId() <= 0) {
            throw new IllegalArgumentException("To Module is required");
        }
        if (StringUtils.isEmpty(relationRequest.getRelationName())) {
            throw new IllegalArgumentException("Forward Relation Name is required");
        }
        if (StringUtils.isEmpty(relationRequest.getReverseRelationName())) {
            throw new IllegalArgumentException("Reverse Relation Name is required");
        }
        if(relationRequest.getRelationName().equals(relationRequest.getReverseRelationName())) {
            throw new IllegalArgumentException("Forward and Reverse Relation Name should not be same");
        }
        if (relationRequest.getRelationCategoryEnum() == null) {
            relationRequest.setRelationCategory(RelationContext.RelationCategory.NORMAL);
        }
    }
}
