package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BulkRelatedListContext;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.factory.PageFactory;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RelatedListWidgetUtil {

    public static void insertRelatedListToDB(RelatedListWidgetContext relatedList) throws Exception {
        if(relatedList != null) {
            insertRelatedListsToDB(new ArrayList<>(Arrays.asList(relatedList)));
        }
    }
    public static void insertRelatedListsToDB(List<RelatedListWidgetContext> relatedLists) throws Exception {
        if (CollectionUtils.isNotEmpty(relatedLists)) {
            List<Map<String, Object>> props = FieldUtil.getAsMapList(relatedLists, RelatedListWidgetContext.class);

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getPageRelatedListWidgetsModule().getTableName())
                    .fields(FieldFactory.getPageRelatedListWidgetsFields())
                    .addRecords(props);
            insertBuilder.save();
        }
    }

    @SneakyThrows
    public static void setRelatedListEnum(RelatedListWidgetContext relList){
        if(relList != null) {
            long subModuleId = relList.getSubModuleId();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule subModule = modBean.getModule(subModuleId);
            if (subModule.getTypeEnum() == FacilioModule.ModuleType.CUSTOM_LINE_ITEM) {
                relList.setRelatedListEnum(RelatedListWidgetContext.RelatedListEnum.CUSTOM_LINE_ITEM_REL_LIST);
            } else {
                relList.setRelatedListEnum(RelatedListWidgetContext.RelatedListEnum.COMMON_REL_LIST);
            }
        }
    }

    public static void updateRelatedList(RelatedListWidgetContext relatedList) throws Exception {
        if(relatedList != null) {
            updateRelatedList(new ArrayList<>(Arrays.asList(relatedList)));
        }
    }

    public static void updateRelatedList(List<RelatedListWidgetContext> relatedLists) throws Exception {
        if(CollectionUtils.isNotEmpty(relatedLists)) {
            FacilioModule module = ModuleFactory.getPageRelatedListWidgetsModule();
            List<FacilioField> fields = FieldFactory.getPageRelatedListWidgetsFields();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

            List<GenericUpdateRecordBuilder.BatchUpdateContext> updateBatch = new ArrayList<>();
            for (RelatedListWidgetContext relList : relatedLists) {

                GenericUpdateRecordBuilder.BatchUpdateContext updateContext = new GenericUpdateRecordBuilder.BatchUpdateContext();
                updateContext.addUpdateValue("sequenceNumber", relList.getSequenceNumber());
                updateContext.addUpdateValue("status", relList.getStatus());

                updateContext.addWhereValue(fieldsMap.get("id").getName(), relList.getId() );
                updateBatch.add(updateContext);
            }

            List<FacilioField> whereFields = new ArrayList<>();
            whereFields.add(fieldsMap.get("id"));

            List<FacilioField> updateField = new ArrayList<>();
            updateField.add(fieldsMap.get("sequenceNumber"));
            updateField.add(fieldsMap.get("status"));
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .fields(updateField)
                    .table(module.getTableName());
            builder.batchUpdate(whereFields, updateBatch);

        }
    }

    /**This is used to add RelatedList in the pageCreation**/
    public static JSONObject fetchAllRelatedListForModule(FacilioModule module) throws Exception {
        return fetchAllRelatedListForModule(module,false);
    }

    public static JSONObject fetchAllRelatedListForModule(FacilioModule module, boolean checkPermission) throws Exception {
        return fetchAllRelatedListForModule(module, checkPermission, null, null);
    }

    public static JSONObject fetchAllRelatedListForModule(FacilioModule module, boolean checkPermission, List<String> modulesToAdd, List<String> modulesToRemove) throws Exception {
        List<RelatedListWidgetContext> relatedLists = fetchAllRelatedList(module, checkPermission, modulesToAdd, modulesToRemove);
        if (CollectionUtils.isNotEmpty(relatedLists)) {
            BulkRelatedListContext bulkRelatedListWidget = new BulkRelatedListContext();
            bulkRelatedListWidget.setRelatedList(relatedLists);
            return FieldUtil.getAsJSON(bulkRelatedListWidget);
        }

        return null;
    }

    public static JSONObject getSingleRelatedListForModule(FacilioModule module, String subModuleName, String fieldName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        RelatedListWidgetContext relList = new RelatedListWidgetContext();
        FacilioModule subModule = modBean.getModule(subModuleName);
        FacilioField field = modBean.getField(fieldName, subModuleName);

        if((field instanceof LookupField && ((LookupField) field).getLookupModuleId() == module.getModuleId())) {
            if (StringUtils.isNotEmpty(((LookupField) field).getRelatedListDisplayName())) {
                relList.setDisplayName(((LookupField) field).getRelatedListDisplayName());
            } else {
                relList.setDisplayName(field.getDisplayName());
            }
            relList.setSubModuleName(subModule.getName());
            relList.setSubModuleId(subModule.getModuleId());
            relList.setFieldName(field.getName());
            relList.setFieldId(field.getFieldId());
            return FieldUtil.getAsJSON(relList);
        }
        return null;
    }
    public static List<RelatedListWidgetContext> fetchAllRelatedList(FacilioModule module, boolean checkPermission, List<String> modulesToAdd, List<String> modulesToRemove) throws Exception {
        List<RelatedListWidgetContext> relLists = new ArrayList<>();
        if(module != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioModule.ModuleType> subModuleTypesToFetch = getSubModuleTypesToFetchForRelatedList(module);

            modulesToAdd = CollectionUtils.isNotEmpty(modulesToAdd) ? modulesToAdd : new ArrayList<>();
            modulesToRemove = CollectionUtils.isNotEmpty(modulesToRemove) ? modulesToRemove : new ArrayList<>();
            if(CollectionUtils.isEmpty(modulesToRemove) && CollectionUtils.isEmpty(modulesToAdd)) {
                addOrRemoveModulesFromRelatedLists(module, modulesToAdd, modulesToRemove);
            }

            List<FacilioModule> subModules = modBean.getSubModules(module.getName(), subModuleTypesToFetch.toArray(new FacilioModule.ModuleType[]{}));
            subModules = subModules != null ? subModules : new ArrayList<>();
            if (CollectionUtils.isNotEmpty(modulesToAdd)) {
                List<String> existingSubModuleNames = subModules
                        .stream()
                        .map(FacilioModule::getName)
                        .collect(Collectors.toList());
                modulesToAdd.removeAll(existingSubModuleNames);
                if (CollectionUtils.isNotEmpty(modulesToAdd)) {
                    subModules.addAll(modBean.getModuleList(modulesToAdd));
                }
            }

            if (CollectionUtils.isNotEmpty(modulesToRemove)) {
                List<String> finalModulesToRemove = modulesToRemove;
                subModules.removeIf(mod -> mod == null || finalModulesToRemove.contains(mod.getName()));
            }
            if (CollectionUtils.isNotEmpty(subModules)) {
                for (FacilioModule subModule : subModules) {
                    if (subModule.isModuleHidden()) {
                        continue;
                    }

                    List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
                    List<FacilioField> fields = allFields.stream()
                            .filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == module.getModuleId()))
                            .collect(Collectors.toList());

                    long moduleId = module.getModuleId();
                    if (CollectionUtils.isNotEmpty(fields)) {

                        for (FacilioField field : fields) {
                            if (!checkPermission || PageFactory.relatedListHasPermission(moduleId, subModule, field)) {
                                RelatedListWidgetContext relList = new RelatedListWidgetContext();
                                if (StringUtils.isNotEmpty(((LookupField) field).getRelatedListDisplayName())) {
                                    relList.setDisplayName(((LookupField) field).getRelatedListDisplayName());
                                } else {
                                    relList.setDisplayName(field.getDisplayName());
                                }
                                relList.setSubModuleName(subModule.getName());
                                relList.setSubModuleId(subModule.getModuleId());
                                relList.setFieldName(field.getName());
                                relList.setFieldId(field.getFieldId());
                                relLists.add(relList);
                            }
                        }
                    }
                }
            }
        }
        return relLists;
    };

    /**
     This moduleTypes are fetched in RelatedList for both Builder and for pageCreation,
     to support new moduleType for specific modules add the moduleType in the switch case
     **/
    public static List<FacilioModule.ModuleType> getSubModuleTypesToFetchForRelatedList(FacilioModule module) throws Exception{
        List<FacilioModule.ModuleType> moduleTypes = new ArrayList<>(Arrays.asList(FacilioModule.ModuleType.BASE_ENTITY,
                FacilioModule.ModuleType.Q_AND_A_RESPONSE,
                FacilioModule.ModuleType.Q_AND_A));
        if(module != null) {
            if(module.isCustom()) {
                moduleTypes.add(FacilioModule.ModuleType.CUSTOM_LINE_ITEM);
            }
            else {
                switch (module.getName()) {
                    default:
                }
            }
        }
        return moduleTypes;
    }

    /**
     modulesToAdd -- except those fetched using moduleTypes these modules are added to relatedList
     modulesToRemove -- these are removed if fetched subModules contains It.
     **/
    public static void addOrRemoveModulesFromRelatedLists(FacilioModule module, List<String> modulesToAdd, List<String> modulesToRemove) {
        switch (module.getName()) {
            case FacilioConstants.ContextNames.VENDORS:
                modulesToRemove.add(FacilioConstants.ContextNames.VENDOR_CONTACT);
                break;
            case "utilityIntegrationCustomer":
                modulesToRemove.add("utilityIntegrationMeter");
                modulesToRemove.add("utilityDispute");

                break;
            default:
        }
    }
    public static void addAllRelatedModuleToWidget(FacilioModule module, PageSectionWidgetContext widget) throws Exception{
        if(module != null && widget != null ) {
            FacilioUtil.throwIllegalArgumentException(widget.getWidgetType() != PageWidget.WidgetType.BULK_RELATED_LIST, "Invalid widget type to add related list");
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            List<FacilioModule> subModules = modBean.getAllSubModules(module.getName());
            BulkRelatedListContext bulkRelList = new BulkRelatedListContext();
            List<RelatedListWidgetContext> relLists = new ArrayList<>();

            for(FacilioModule subModule : subModules ) {
                if (subModule.isModuleHidden()) {
                    continue;
                }
                RelatedListWidgetContext relList = new RelatedListWidgetContext();

                List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
                List<FacilioField> fields = allFields.stream()
                        .filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == module.getModuleId()))
                        .collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(fields)) {

                    for(FacilioField field : fields ) {

                        if(StringUtils.isNotEmpty(((LookupField) field).getRelatedListDisplayName())) {
                            relList.setDisplayName(((LookupField) field).getRelatedListDisplayName());
                        }
                        else {
                            relList.setDisplayName(((LookupField) field).getDisplayName());
                        }
                        relList.setSubModuleName(subModule.getName());
                        relList.setSubModuleId(subModule.getModuleId());
                        relList.setFieldName(field.getName());
                        relList.setFieldId(field.getFieldId());
                        relLists.add(relList);
                    }
                }
            }
            bulkRelList.setRelatedList(relLists);
            widget.setWidgetDetail(FieldUtil.getAsJSON(bulkRelList));
        }
    }
    public static BulkRelatedListContext getBulkRelatedListOfWidgetId(Long widgetId, WidgetWrapperType widgetWrapperType) throws Exception {
        List<RelatedListWidgetContext> relLists = getRelatedListsOfWidgetId(widgetId, widgetWrapperType, true);
        if(CollectionUtils.isNotEmpty(relLists)) {
            BulkRelatedListContext bulkRelList = new BulkRelatedListContext();
            bulkRelList.setRelatedList(relLists);
            return bulkRelList;
        }
        return null;
    }
    public static RelatedListWidgetContext getRelatedListOfWidgetId(Long widgetId, WidgetWrapperType widgetWrapperType) throws Exception {
        List<RelatedListWidgetContext> relList = getRelatedListsOfWidgetId(widgetId, widgetWrapperType, false);
        return CollectionUtils.isNotEmpty(relList)?relList.get(0):null;
    }

    public static void setWidgetIdForRelList(long widgetId, RelatedListWidgetContext relList, WidgetWrapperType type) {
        switch (type) {
            case DEFAULT:
                relList.setWidgetId(widgetId);
                break;
            case WIDGET_GROUP:
                relList.setWidgetGroupWidgetId(widgetId);
                break;
        }
    }

    public static List<RelatedListWidgetContext> getRelatedListsOfWidgetId(Long widgetId, WidgetWrapperType widgetWrapperType, boolean isBulkWidget) throws Exception {
        List<FacilioField> fields = FieldFactory.getPageRelatedListWidgetsFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        FacilioModule pageRelListModule = ModuleFactory.getPageRelatedListWidgetsModule();
        FacilioField widgetIdField =  FieldFactory.getWidgetIdField(pageRelListModule, widgetWrapperType);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(pageRelListModule.getTableName())
                .select(FieldFactory.getPageRelatedListWidgetsFields())
                .orderBy(fieldsMap.get("sequenceNumber").getCompleteColumnName()+","+fieldsMap.get("id").getColumnName())
                .andCondition(CriteriaAPI.getEqualsCondition(widgetIdField, String.valueOf(widgetId)));

        if(isBulkWidget) {
            builder.andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("status"), String.valueOf(Boolean.TRUE)));
        }
        List<Map<String, Object>> props = builder.get();

        if(CollectionUtils.isNotEmpty(props)) {
            List<RelatedListWidgetContext> relLists =  FieldUtil.getAsBeanListFromMapList(props, RelatedListWidgetContext.class);
            List<Long> relListFieldIds = relLists.stream().filter(f->f.getFieldId() != null && f.getFieldId() > 0)
                    .map(RelatedListWidgetContext::getFieldId)
                    .collect(Collectors.toList());
            List<Long> relListModuleIds = relLists.stream().filter(f->f.getSubModuleId() != null && f.getSubModuleId() > 0)
                    .map(RelatedListWidgetContext::getSubModuleId)
                    .collect(Collectors.toList());

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> allModBeanFields = modBean.getFields(relListFieldIds);
            Map<Long, FacilioField> allModBeanFieldsMap = allModBeanFields.stream().collect(Collectors.toMap(FacilioField::getFieldId, Function.identity(), (oldValue, newValue) -> newValue));

            Map<Long, String> relListSubModules = relListModuleIds.stream().map(f-> {
                try {
                    return modBean.getModule(f);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toMap(FacilioModule::getModuleId,FacilioModule::getDisplayName, (oldValue, newValue) -> newValue));

            return relLists.stream()
                    .peek(f-> {
                        if(f.getFieldId() != null && f.getFieldId() > 0) {
                            FacilioField field = allModBeanFieldsMap.get(f.getFieldId());
                            f.setFieldName(field.getName());
                            f.setField(field);
                            setRelatedListEnum(f);
                            f.setModule(field.getModule());
                            if (StringUtils.isNotEmpty(((LookupField) field).getRelatedListDisplayName())) {
                                f.setDisplayName(((LookupField) field).getRelatedListDisplayName());
                            } else {
                                f.setDisplayName(relListSubModules.get(f.getSubModuleId()));
                            }
                        }
                    }).collect(Collectors.toList());
        }
        return null;
    }

    public static void deleteRelatedLists(Long existingRelListId) throws Exception {
        if(existingRelListId != null && existingRelListId > 0) {
            deleteRelatedLists(new ArrayList<>(Arrays.asList(existingRelListId)));
        }
    }
    public static void deleteRelatedLists(List<Long> existingRelListIds) throws Exception {
        if(CollectionUtils.isNotEmpty(existingRelListIds)) {
            FacilioModule module = ModuleFactory.getPageRelatedListWidgetsModule();

            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(existingRelListIds, module));
            builder.delete();
        }
    }
}
