package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BulkRelatedListContext;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RelatedListWidgetUtil {

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
    public static BulkRelatedListContext getBulkRelatedListOfWidgetId(long pageWidgetId) throws Exception {
        List<RelatedListWidgetContext> relLists = getRelatedListsOfWidgetId(pageWidgetId, true);
        if(CollectionUtils.isNotEmpty(relLists)) {
            BulkRelatedListContext bulkRelList = new BulkRelatedListContext();
            bulkRelList.setRelatedList(relLists);
            return bulkRelList;
        }
        return null;
    }

    public static List<RelatedListWidgetContext> getRelatedListsOfWidgetId(long pageWidgetId, boolean isBulkWidget) throws Exception {
        List<FacilioField> fields = FieldFactory.getPageRelatedListWidgetsFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getPageRelatedListWidgetsModule().getTableName())
                .select(FieldFactory.getPageRelatedListWidgetsFields())
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("widgetId"), String.valueOf(pageWidgetId)))
                .orderBy(fieldsMap.get("sequenceNumber").getCompleteColumnName()+","+fieldsMap.get("id").getColumnName());
        if(isBulkWidget) {
            builder.andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("status"), String.valueOf(Boolean.TRUE)));
        }
        List<Map<String, Object>> props = builder.get();

        if(CollectionUtils.isNotEmpty(props)) {
            List<RelatedListWidgetContext> relLists =  FieldUtil.getAsBeanListFromMapList(props, RelatedListWidgetContext.class);
            List<Long> relListFieldIds = relLists.stream().map(RelatedListWidgetContext::getFieldId).collect(Collectors.toList());
            List<Long> relListModuleIds = relLists.stream().map(RelatedListWidgetContext::getSubModuleId).collect(Collectors.toList());

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
                        FacilioField field = allModBeanFieldsMap.get(f.getFieldId());
                        f.setFieldName(field.getName());
                        f.setField(field);
                        f.setModule(field.getModule());
                        if(StringUtils.isNotEmpty(((LookupField) field).getRelatedListDisplayName())) {
                            f.setDisplayName(((LookupField) field).getRelatedListDisplayName());
                        }
                        else {
                            f.setDisplayName(relListSubModules.get(f.getSubModuleId()));
                        }
                    }).collect(Collectors.toList());
        }
        return null;
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
