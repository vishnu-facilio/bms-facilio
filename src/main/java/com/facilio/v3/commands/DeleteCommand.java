package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class DeleteCommand extends FacilioCommand {
    private static int RESTRICT = 0;
    private static int SET_NULL = 1;
    private static int CASCADE = 2;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        List<Long> recordIds = Constants.getRecordIds(context);

        getDeletedRecords(moduleName, recordIds, context);

        Map<String, Integer> deleteCount = new HashMap<>();
        deleteModuleRecords(recordIds, moduleName, deleteCount, context);
        Constants.setCountMap(context, deleteCount);
        return false;
    }

    private void getDeletedRecords(String moduleName, List<Long> recordIds, Context context) throws Exception {
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = bean.getModule(moduleName);

        V3Config v3Config = Constants.getV3Config(context);
        Class beanClass = ChainUtil.getBeanClass(v3Config, module);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .module(module)
                .beanClass(beanClass)
                .select(bean.getAllFields(moduleName))
                .andCondition(CriteriaAPI.getIdCondition(recordIds, module));
        List<ModuleBaseWithCustomFields> deletedRecords = builder.get();

        Constants.setDeletedRecords(context, deletedRecords);

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        recordMap.put(moduleName,deletedRecords);
        Constants.setRecordMap(context, recordMap);
    }

    private void deleteModuleRecords(List<Long> recordIds,  String moduleName, Map<String, Integer> deleteCount, Context context) throws Exception {
        int count = 0;
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = bean.getModule(moduleName);
        Map<FacilioModule, List<FacilioField>> relatedLookupFields = bean.getRelatedLookupFields(module.getModuleId());
        List<Pair<FacilioModule, Integer>> subModules = bean.getSubModulesWithDeleteType(module.getModuleId(),
                FacilioModule.ModuleType.CUSTOM, FacilioModule.ModuleType.BASE_ENTITY);

        count += deleteRows(module, recordIds, context);

        List<FacilioModule> cascadeModules = new ArrayList<>();
        List<FacilioModule> throwErrorModules = new ArrayList<>();
        for (Pair<FacilioModule, Integer> subModule: subModules) {
            if (subModule.getRight() == RESTRICT) {
              throwErrorModules.add(subModule.getLeft());
            } else if (subModule.getRight() == CASCADE) {
                cascadeModules.add(subModule.getLeft());
            }
        }

        if (!throwErrorModules.isEmpty()) {
            for (FacilioModule restrictModule : throwErrorModules) {
                List<FacilioField> fields = relatedLookupFields.get(restrictModule);
                if (CollectionUtils.isEmpty(fields)) {
                    continue;
                }
                List<Long> childRecordIds = checkLookupData(module, restrictModule, fields, recordIds,RESTRICT);
                if (CollectionUtils.isNotEmpty(childRecordIds)) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "The sub-module " + restrictModule.getDisplayName() + " has data associated. Please delete them manually before deleting");
                }
            }
        }

        for (FacilioModule cascadeModule: cascadeModules) {
            List<FacilioField> fields = relatedLookupFields.get(cascadeModule);
            if (CollectionUtils.isEmpty(fields)) {
                continue;
            }
            List<Long> rowIds = checkLookupData(module, cascadeModule, fields, recordIds,CASCADE);
            if (CollectionUtils.isEmpty(rowIds)) {
                continue;
            }
//            Map<String, Object> deleteObj = new HashMap<>();
//            deleteObj.put(cascadeModule.getName(), rowIds);
//            V3Util.deleteRecords(cascadeModule.getName(), deleteObj, null, null, false);
            deleteModuleRecords(rowIds, cascadeModule.getName(), deleteCount, context);
        }
        deleteCount.put(moduleName, count);
     }

    private int deleteRows(FacilioModule module, List<Long> rowIds, Context context) throws Exception {
        DeleteRecordBuilder builder = new DeleteRecordBuilder()
                .module(module)
                ;

        if (module.isTrashEnabled()) {
            markAsDeletedForRelationshipData(module,rowIds);
            builder.andCondition(CriteriaAPI.getIdCondition(rowIds, module));
            boolean markAsDeleteByPeople = Constants.getMarkAsDeleteByPeople(context);
            return builder.markAsDelete(markAsDeleteByPeople);
        }
        else {
            int deletedCount = builder.batchDeleteById(rowIds);

            if(module != null && !module.getTypeEnum().equals(FacilioModule.ModuleType.RELATION_DATA)) {
                //Deleting current module relationships
                deleteRelationshipData(module, rowIds);

                //Deleting Extended modules relationships
                FacilioModule extendedModule = new FacilioModule(module);
                while (extendedModule.getExtendModule() != null) {
                    deleteRelationshipData(extendedModule.getExtendModule(), rowIds);
                    extendedModule = extendedModule.getExtendModule();
                }

                //Deleting child module relationships
                Set<String> childModules = Constants.getExtendedModules(context);
                if (CollectionUtils.isNotEmpty(childModules)) {
                    ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    for (String extendedModuleName : childModules) {
                        FacilioModule childModule = bean.getModule(extendedModuleName);
                        deleteRelationshipData(childModule, rowIds);
                    }
                }
            }

            return deletedCount;
        }
    }

    private List<Long> checkLookupData(FacilioModule parentModule, FacilioModule childModule, List<FacilioField> relatedLookupFields, List<Long> recordIds,int type) throws Exception {
        List<Long> rowIds = new ArrayList<>();
        if (!LookupSpecialTypeUtil.isSpecialType(parentModule.getName()) && CollectionUtils.isNotEmpty(recordIds)) {
            String idString = StringUtils.join(recordIds, ",");
            if (CollectionUtils.isNotEmpty(relatedLookupFields)) {
                List<FacilioField> fields = new ArrayList<>();
                fields.add(FieldFactory.getIdField(childModule));
                for (FacilioField f : relatedLookupFields) {
                    SelectRecordsBuilder builder = new SelectRecordsBuilder()
                            .module(childModule)
                            .select(fields)
                            .andCondition(CriteriaAPI.getCondition(f, idString, NumberOperators.EQUALS));

                    if(type == RESTRICT){ //For checking purpose we can search only one child record in DB
                        builder.limit(1);
                    }

                    List<Map<String, Object>> data = builder.getAsProps();
                    if (CollectionUtils.isNotEmpty(data)) {
                        data.forEach(i -> rowIds.add((long) i.get("id")));
                    }
                }
            }
        }
        return rowIds;
    }

    private static void deleteRelationshipData(FacilioModule module, List<Long> recordIds) throws Exception{
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(recordIds)) {
            List<RelationRequestContext> relations = RelationUtil.getAllRelations(module);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(relations)) {
                for (RelationRequestContext relation : relations) {
                    RelationMappingContext.Position position = RelationMappingContext.Position.valueOf(relation.getPosition());
                    if (relation.getToModuleId() == module.getModuleId()) {
                        position = RelationUtil.getReversePosition(position);
                    }
                    DeleteRecordBuilder deleteBuilder = new DeleteRecordBuilder()
                            .module(relation.getRelationModule());
                    deleteBuilder.andCondition(CriteriaAPI.getCondition(position.getColumnName(), position.getFieldName(), StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
                    deleteBuilder.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(relation.getRelationModule().getModuleId()), NumberOperators.EQUALS));
                    deleteBuilder.delete();
                }
            }
        }
    }
    private static void markAsDeletedForRelationshipData(FacilioModule module,List<Long> recordIds) throws  Exception{
        if(module != null && !module.getTypeEnum().equals(FacilioModule.ModuleType.RELATION_DATA)) {
            Map<String,FacilioField> fieldsMap=FieldFactory.getAsMap(FieldFactory.getCustomRelationFields());
            if(CollectionUtils.isNotEmpty(recordIds)) {

                List<RelationRequestContext> relationMappingList = RelationUtil.getAllRelations(module);
                Map<Long,List<RelationRequestContext>> moduleIdVsRelationShip=relationMappingList.stream().collect(Collectors.groupingBy(i->i.getRelationModule().getModuleId(), HashMap::new, Collectors.toCollection(ArrayList::new)));
                if(MapUtils.isNotEmpty(moduleIdVsRelationShip)){
                    Criteria criteria=constructCriteria(moduleIdVsRelationShip,recordIds,fieldsMap);
                    if(criteria.getConditions()!=null) {
                            Map<String,Object> prop=new HashMap<>();
                            prop.put("isDeleted",true);
                            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                                    .table(ModuleFactory.getCustomRelationModule().getTableName())
                                    .fields(Collections.singletonList(fieldsMap.get("isDeleted")))
                                    .andCriteria(criteria);
                            updateBuilder.update(prop);
                    }
                }
            }

        }
    }

    public static Criteria constructCriteria(Map<Long,List<RelationRequestContext>> moduleIdVsRelationship, List<Long> recordId, Map<String,FacilioField> fieldsMap) {
        Criteria dataCriteria = new Criteria();
        for (Long moduleId : moduleIdVsRelationship.keySet()) {
            List<RelationRequestContext> relationMappingList = moduleIdVsRelationship.get(moduleId);
            Criteria criteria=new Criteria();
            if (CollectionUtils.isNotEmpty(relationMappingList)) {
                if (relationMappingList.get(0).getFromModuleId() == relationMappingList.get(0).getToModuleId()) {
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("left"), recordId, NumberOperators.EQUALS));
                    criteria.addOrCondition(CriteriaAPI.getCondition(fieldsMap.get("right"), recordId, NumberOperators.EQUALS));
                }else{
                    RelationMappingContext.Position position= RelationMappingContext.Position.valueOf(relationMappingList.get(0).getPosition());
                    if(position== RelationMappingContext.Position.LEFT) {
                        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("left"), recordId, NumberOperators.EQUALS));
                    }else{
                        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("right"), recordId, NumberOperators.EQUALS));
                    }
                }
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"),String.valueOf(moduleId), NumberOperators.EQUALS));
                dataCriteria.orCriteria(criteria);
            }
        }
        return  dataCriteria;
    }
}
