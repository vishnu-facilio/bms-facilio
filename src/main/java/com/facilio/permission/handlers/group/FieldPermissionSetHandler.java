package com.facilio.permission.handlers.group;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.module.GetSortableFieldsCommand;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.context.module.FieldPermissionSet;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class FieldPermissionSetHandler implements PermissionSetGroupHandler<FieldPermissionSet> {
    @Override
    public List<FieldPermissionSet> getPermissions(Long groupId) throws Exception {
        if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FIELD_LIST_PERMISSION)){
            return null;
        }
        List<FieldPermissionSet> fieldPermissionSets = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(groupId);
        if(module == null){
            return fieldPermissionSets;
        }

        List<FacilioField> searchableAndSortableFieldsFromChains = new ArrayList<>();
        FacilioChain chain = ReadOnlyChainFactoryV3.getSortableFieldsCommand();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        chain.getContext().put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.SORT.getVal());
        chain.execute();


        List<FacilioField> sortableFields = (List<FacilioField>) chain.getContext().get(FacilioConstants.ContextNames.SORT_FIELDS);
        if(CollectionUtils.isNotEmpty(sortableFields)){
            searchableAndSortableFieldsFromChains.addAll(sortableFields);
        }

        FacilioChain filterableFields = ReadOnlyChainFactory.getFilterableFields();
        filterableFields.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        filterableFields.getContext().put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.CRITERIA.getVal());
        filterableFields.execute();

        List<FacilioField> searchableFields = (List<FacilioField>) filterableFields.getContext().get(FacilioConstants.Filters.FILTER_FIELDS);
        if(CollectionUtils.isNotEmpty(searchableFields)){
            searchableAndSortableFieldsFromChains.addAll(searchableFields);
        }

        if(CollectionUtils.isNotEmpty(searchableAndSortableFieldsFromChains)) {
            List<FacilioField> searchableAndSortableFacilioFields = new ArrayList<>(convertToFacilioFields(searchableAndSortableFieldsFromChains));
            Map<Long, FieldPermissionSet> uniqueFieldsMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(searchableAndSortableFacilioFields)) {
                for (FacilioField field : searchableAndSortableFacilioFields) {
                    FieldPermissionSet item = new FieldPermissionSet();
                    Map<String, Long> queryProp = new HashMap<>();
                    item.setFieldId(field.getFieldId());
                    item.setDisplayName(field.getDisplayName());
                    queryProp.put("moduleId", module.getModuleId());
                    queryProp.put("fieldId", field.getId());
                    item.setDefaultValue(getDefaultValue(queryProp));
                    item.setDisabled(getIsDisabled(queryProp));
                    item.setModuleId(module.getModuleId());
                    uniqueFieldsMap.put(field.getFieldId(), item);
                }
                if(CollectionUtils.isNotEmpty(uniqueFieldsMap.values())) {
                    fieldPermissionSets.addAll(uniqueFieldsMap.values());
                    fieldPermissionSets.sort(Comparator.comparing(FieldPermissionSet::getDisplayName));
                }
            }
        }
        return fieldPermissionSets;
    }

    @Override
    public Map<String, Long> paramsResolver(Map<String, String> httpParametersMap) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String,Long> prop = new HashMap<>();
        if(MapUtils.isNotEmpty(httpParametersMap)) {
            String currentModuleName = httpParametersMap.get("moduleName");
            if(currentModuleName != null) {
                FacilioModule currentModule = modBean.getModule(currentModuleName);
                if(currentModule != null) {
                    prop.put("moduleId",currentModule.getModuleId());
                }
            }
            String fieldName = httpParametersMap.get("fieldName");
            if(fieldName != null) {
                FacilioField currentField = modBean.getField(fieldName,currentModuleName);
                if(currentField != null) {
                    prop.put("fieldId",currentField.getFieldId());
                }
            }
        }
        return prop;
    }

    @Override
    public boolean getDefaultValue(Map<String,Long> queryProp) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        if(!queryProp.isEmpty()){
            Long fieldId = queryProp.get("fieldId");
            if(fieldId != null && fieldId > 0) {
                FacilioField field = modBean.getField(fieldId);
                if (field != null) {
                    if (field.isMainField()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean getIsDisabled(Map<String,Long> queryProp) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        if(!queryProp.isEmpty()){
            Long fieldId = queryProp.get("fieldId");
            if(fieldId != null && fieldId > 0) {
                FacilioField field = modBean.getField(fieldId);
                if (field != null) {
                    if (field.isMainField()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean showParent(Long groupId) throws Exception {
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FIELD_LIST_PERMISSION)){
            return true;
        }
        return false;
    }

    private List<FacilioField> convertToFacilioFields(List<FacilioField> combinedList) throws Exception {
        List<FacilioField> searchableAndSortableFieldsFromChains = new ArrayList<>();
        List<Long> sortableFieldIds = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        for (Object field : combinedList) {
            if (field instanceof GetSortableFieldsCommand.SortableField) {
                var sortableField = (GetSortableFieldsCommand.SortableField) field;
                if(sortableField != null && sortableField.getId() > 0){
                    sortableFieldIds.add(sortableField.getId());}
            } else if (field instanceof FilterFieldContext) {
                var searchableField = (FilterFieldContext) field;
                if(searchableField != null && searchableField.getField() != null && searchableField.getField().getFieldId() > 0){
                searchableAndSortableFieldsFromChains.add(searchableField.getField());}
            }
        }
        searchableAndSortableFieldsFromChains.addAll(modBean.getFields(sortableFieldIds));
        return searchableAndSortableFieldsFromChains;
    }
}
